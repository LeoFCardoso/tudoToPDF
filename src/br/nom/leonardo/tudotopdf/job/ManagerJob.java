package br.nom.leonardo.tudotopdf.job;

import org.quartz.InterruptableJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.UnableToInterruptJobException;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.nom.leonardo.tudotopdf.config.Config;

/**
 * Sample class with a test quartz job
 * 
 * @author leonardo
 *
 */
public class ManagerJob implements InterruptableJob {

	private Logger log = LoggerFactory.getLogger(ManagerJob.class);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			log.info("I'm the manager running!");
			Scheduler scheduler = new StdSchedulerFactory().getScheduler();
			for (String groupName : scheduler.getJobGroupNames()) {
				for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
					JobDetail jobDetail = scheduler.getJobDetail(jobKey);
					if (jobDetail.getJobClass().equals(PdfConversionJob.class)) {
						log.debug("I have a job! " + jobKey.getName() + " is the name.");
						JobDataMap jobDataMap = jobDetail.getJobDataMap();
						JobStatus jobStatus = (JobStatus) jobDataMap.get("p_status");
						if ((jobStatus == JobStatus.ENDED) && (System.currentTimeMillis()
								- jobDataMap.getLong("p_endTime") > (Config.getInt("application.fileAgeMinutes") / 2)
										* 60 * 1000)) {
							log.info("Job " + jobKey.getName() + " will be deleted.");
							scheduler.deleteJob(jobKey);
						}
					}
				}
			}
			// for (String groupName : scheduler.getTriggerGroupNames()) {
			// for (TriggerKey triggerKey : scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(groupName))) {
			// log.debug("I have a trigger! " + triggerKey.getName() + " is the name!");
			// }
			// }
		} catch (Exception e) {
			log.error("Fail", e);
		}
	}

	@Override
	public void interrupt() throws UnableToInterruptJobException {
		log.warn("I was interrupted!");
	}

}
