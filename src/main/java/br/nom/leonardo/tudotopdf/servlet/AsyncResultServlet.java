package br.nom.leonardo.tudotopdf.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.nom.leonardo.tudotopdf.config.Config;
import br.nom.leonardo.tudotopdf.job.JobStatus;

@WebServlet(description = "A servlet to handle background tasks", urlPatterns = { "/asyncResult" })
public class AsyncResultServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2408872275039744994L;

	private Logger log = LoggerFactory.getLogger(AsyncResultServlet.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			String checkId = request.getParameter("id");
			if (checkId == null) {
				checkId = "";
			}
			Scheduler scheduler = new StdSchedulerFactory().getScheduler();
			JobKey jobKey = JobKey.jobKey(checkId);
			JobDetail job = scheduler.getJobDetail(jobKey);
			if (job == null) {
				response.setContentType("application/json");
				PrintWriter out = response.getWriter();
				log.error("unknown job id: " + checkId);
				out.print("{ result: 'error', msg: 'unknown job id' }");
				out.close();
			} else {
				JobStatus status = (JobStatus) job.getJobDataMap().get("p_status");
				boolean jobEnded = (status == JobStatus.ENDED);
				Exception jobException = (Exception) job.getJobDataMap().get("p_exception");
				boolean jobWithException = (jobException != null);
				if (jobEnded) {
					if (jobWithException) {
						response.setContentType("application/json");
						PrintWriter out = response.getWriter();
						out.print("{ result: 'ERROR', msg: '" + jobException.getMessage() + "' }");
						out.close();
					} else {
						// Send redirect to finalPDFFile
						// With Tomcat, please read:
						// http://www.moreofless.co.uk/static-content-web-pages-images-tomcat-outside-war/
						String protocol = request.getScheme();
						String host = request.getServerName() + ":" + request.getServerPort();
						String staticContext = Config.getString("application.staticContext");
						File finalPDFFile = (File) job.getJobDataMap().get("p_finalFile");
						response.sendRedirect(protocol + "://" + host + staticContext + "/" + finalPDFFile.getName());
					}
				} else {
					response.setContentType("application/json");
					PrintWriter out = response.getWriter();
					out.print("{ result: '" + status + "' }");
					out.close();
				}
			}

		} catch (Exception e) {
			log.error("Unexpected exception. :(", e);
		}
	}

}
