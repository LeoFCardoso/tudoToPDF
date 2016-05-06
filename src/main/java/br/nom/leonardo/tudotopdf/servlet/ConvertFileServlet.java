package br.nom.leonardo.tudotopdf.servlet;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.text.Normalizer;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tika.Tika;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.nom.leonardo.tudotopdf.config.Config;
import br.nom.leonardo.tudotopdf.job.JobStatus;
import br.nom.leonardo.tudotopdf.job.PdfConversionJob;
import br.nom.leonardo.tudotopdf.model.ConversionConfiguration;
import br.nom.leonardo.tudotopdf.pdf.PDFConverter;
import br.nom.leonardo.tudotopdf.pdf.PDFConverterFactory;

/**
 * Servlet implementation class ConvertFileServlet
 */
@WebServlet(description = "A servlet to convert generic content to PDF", urlPatterns = { "/fileSubmit.pdf" })
@MultipartConfig
public class ConvertFileServlet extends HttpServlet {

	private Logger log = LoggerFactory.getLogger(ConvertFileServlet.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 9089231803242510987L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ConvertFileServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			request.setCharacterEncoding("UTF-8");

			log.debug("Starting file conversion. Session id=" + request.getSession().getId());

			Part filePart = request.getPart("theFile");

			String uploadFilePath = Config.getString("application.uploadedFiles");
			log.debug("Upload file path is {}.", uploadFilePath);

			if (filePart.getSize() > Config.getLong("application.maxFileSizeBytes")) {
				showError(response,
						"Uploaded file is bigger than " + Config.getLong("application.maxFileSizeBytes") + " bytes.");
				return;
			}

			// Use UUID to handle same file name uploaded multiple times
			File uploadedFile = new File(uploadFilePath + File.separatorChar + UUID.randomUUID().toString() + '-'
					+ flattenToAscii(getFileName(filePart)));
			FileUtils.copyInputStreamToFile(filePart.getInputStream(), uploadedFile);
			log.debug("File saved to {}.", uploadedFile.getCanonicalPath());

			String uploadedContentType = filePart.getContentType();
			Tika tika = new Tika();
			String realContentType = tika.detect(uploadedFile);

			log.debug("The file name is \"{}\", with uploaded content type {} and real content type {}.",
					getFileName(filePart), uploadedContentType, realContentType);

			FileInputStream fis = new FileInputStream(uploadedFile);
			String md5UploadedFile = DigestUtils.md5Hex(fis);
			fis.close();
			log.debug("The file md5 is \"{}\"", md5UploadedFile);

			String strategy = StringUtils.isBlank(request.getParameter("strategy")) ? ""
					: request.getParameter("strategy");
			log.debug("Conversion strategy: " + strategy);

			ConversionConfiguration config = buildConversionConfiguration(request);

			PDFConverter converter = PDFConverterFactory.createPDFConverter(realContentType, strategy);
			if (converter == null) {
				showError(response, "Invalid strategy for mime type " + realContentType);
				return;
			}

			boolean isAsync = "on".equals(request.getParameter("async"));
			log.debug("PDF processing will be async?: " + isAsync);

			if (isAsync) {
				String jobUUID = UUID.randomUUID().toString();
				JobKey jobKey = JobKey.jobKey(jobUUID);
				JobDetail job = JobBuilder.newJob(PdfConversionJob.class).withIdentity(jobKey).storeDurably().build();
				job.getJobDataMap().put("p_name", jobUUID);
				job.getJobDataMap().put("p_status", JobStatus.PROCESSING);
				job.getJobDataMap().put("p_converter", converter);
				job.getJobDataMap().put("p_md5UploadedFile", md5UploadedFile);
				job.getJobDataMap().put("p_uploadedFile", uploadedFile);
				job.getJobDataMap().put("p_config", config);

				Scheduler scheduler = new StdSchedulerFactory().getScheduler();
				Trigger trigger = TriggerBuilder.newTrigger().withIdentity(jobUUID).startNow().build();
				scheduler.scheduleJob(job, trigger);

				response.setContentType("application/json");
				PrintWriter out = response.getWriter();
				String protocol = request.getScheme();
				String host = request.getServerName() + ":" + request.getServerPort();
				String context = request.getServletContext().getContextPath();
				String link = protocol + "://" + host + context + "/asyncResult?id=" + jobUUID;
				out.print("{ result: '" + link + "' }");
				out.close();
			} else {
				PdfConversionJob syncJob = new PdfConversionJob(converter, md5UploadedFile, uploadedFile, config);
				syncJob.runSync();
				// Send redirect to finalPDFFile
				// With Tomcat, please read:
				// http://www.moreofless.co.uk/static-content-web-pages-images-tomcat-outside-war/
				String protocol = request.getScheme();
				String host = request.getServerName() + ":" + request.getServerPort();
				String staticContext = Config.getString("application.staticContext");
				File finalPDFFile = syncJob.getFinalPDFFile();
				response.sendRedirect(protocol + "://" + host + staticContext + "/" + finalPDFFile.getName());
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			showError(response, e.getMessage());
			return;
		}

	}

	private ConversionConfiguration buildConversionConfiguration(HttpServletRequest request) throws Exception {
		boolean isWatermarked = "on".equals(request.getParameter("watermark"));
		log.debug("PDF will be watermarked: " + isWatermarked);

		boolean isProtected = "on".equals(request.getParameter("protected"));
		log.debug("PDF will be protected: " + isProtected);

		String textHeader = StringUtils.isBlank(request.getParameter("headerWmText")) ? ""
				: request.getParameter("headerWmText");
		log.debug("PDF Header: " + textHeader);
		String textTop = StringUtils.isBlank(request.getParameter("topWmText")) ? ""
				: request.getParameter("topWmText");
		log.debug("PDF Top: " + textTop);
		String textMiddle = StringUtils.isBlank(request.getParameter("middleWmText")) ? ""
				: request.getParameter("middleWmText");
		log.debug("PDF Middle: " + textMiddle);
		String textBottom = StringUtils.isBlank(request.getParameter("bottomWmText")) ? ""
				: request.getParameter("bottomWmText");
		log.debug("PDF Bottom: " + textBottom);
		String textFooter = StringUtils.isBlank(request.getParameter("footerWmText")) ? ""
				: request.getParameter("footerWmText");
		log.debug("PDF Footer: " + textFooter);

		int sizeHeader = StringUtils.isBlank(request.getParameter("headerWmSize"))
				|| !StringUtils.isNumeric(request.getParameter("headerWmSize")) ? 0
						: Integer.parseInt(request.getParameter("headerWmSize"));
		log.debug("PDF Header size: " + sizeHeader);
		int sizeTop = StringUtils.isBlank(request.getParameter("topWmSize"))
				|| !StringUtils.isNumeric(request.getParameter("topWmSize")) ? 0
						: Integer.parseInt(request.getParameter("topWmSize"));
		log.debug("PDF Top size: " + sizeTop);
		int sizeMiddle = StringUtils.isBlank(request.getParameter("middleWmSize"))
				|| !StringUtils.isNumeric(request.getParameter("middleWmSize")) ? 0
						: Integer.parseInt(request.getParameter("middleWmSize"));
		log.debug("PDF Middle size: " + sizeMiddle);
		int sizeBottom = StringUtils.isBlank(request.getParameter("bottomWmSize"))
				|| !StringUtils.isNumeric(request.getParameter("bottomWmSize")) ? 0
						: Integer.parseInt(request.getParameter("bottomWmSize"));
		log.debug("PDF Bottom size: " + sizeBottom);
		int sizeFooter = StringUtils.isBlank(request.getParameter("footerWmSize"))
				|| !StringUtils.isNumeric(request.getParameter("footerWmSize")) ? 0
						: Integer.parseInt(request.getParameter("footerWmSize"));
		log.debug("PDF Footer size: " + sizeFooter);

		String waterkMarkType = StringUtils.isBlank(request.getParameter("watermarkType")) ? "diagonal"
				: request.getParameter("watermarkType");
		if (!"horizontal".equals(waterkMarkType)) {
			waterkMarkType = "diagonal"; // Allows horizontal and nothing else
		}
		log.debug("Watermark type: " + waterkMarkType);

		int transparency = StringUtils.isBlank(request.getParameter("transparency"))
				|| !StringUtils.isNumeric(request.getParameter("transparency")) ? 50
						: Integer.parseInt(request.getParameter("transparency"));
		log.debug("Transparency: " + transparency);
		
		String wmColor = StringUtils.isBlank(request.getParameter("watermarkColor")) ? "lightGray"
				: request.getParameter("watermarkColor");
		log.debug("Watermark color: " + wmColor);
		Field field = Color.class.getField(wmColor); 
		Color waterkMarkColor = (Color) field.get(null);

		ConversionConfiguration config = new ConversionConfiguration(isWatermarked, isProtected, textHeader, textTop,
				textMiddle, textBottom, textFooter, sizeHeader, sizeTop, sizeMiddle, sizeBottom, sizeFooter,
				transparency, waterkMarkType, waterkMarkColor);
		return config;
	}

	private void showError(HttpServletResponse response, String msg) {
		// TODO better error handling with json
		log.error(msg);
		response.setContentType("text/plain");
		response.setStatus(500);
		try {
			PrintWriter out = response.getWriter();
			out.print(msg);
			out.close();
		} catch (IOException e) {
			log.error("Unexpected exception generating error message.", e);
		}
	}

	/**
	 * Helper method to get uploaded file name
	 * 
	 * @param part
	 * @return
	 */
	private String getFileName(Part part) {
		for (String cd : part.getHeader("content-disposition").split(";")) {
			if (cd.trim().startsWith("filename")) {
				String fileName = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
				return fileName.substring(fileName.lastIndexOf('/') + 1).substring(fileName.lastIndexOf('\\') + 1); // MSIE
																													// fix.
			}
		}
		return null;
	}

	private String flattenToAscii(String string) {
		StringBuilder sb = new StringBuilder(string.length());
		string = Normalizer.normalize(string, Normalizer.Form.NFD);
		for (char c : string.toCharArray()) {
			if (c <= '\u007F')
				sb.append(c);
		}
		return sb.toString();
	}

}
