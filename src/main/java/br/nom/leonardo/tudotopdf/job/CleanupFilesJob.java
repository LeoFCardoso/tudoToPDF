package br.nom.leonardo.tudotopdf.job;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.nom.leonardo.tudotopdf.config.Config;

/**
 * A job to cleanup old files in temp space
 * 
 * @author leonardo
 */
public class CleanupFilesJob implements InterruptableJob {

	private Logger log = LoggerFactory.getLogger(CleanupFilesJob.class);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			File folderUploaded = new File(Config.getString("application.uploadedFiles"));
			Collection<File> filesUploaded = FileUtils.listFiles(folderUploaded, null, false);
			cleanup(filesUploaded);

			File folderStatic = new File(Config.getString("application.staticFiles"));
			Collection<File> filesStatic = FileUtils.listFiles(folderStatic, new String[] { "pdf" }, false);
			cleanup(filesStatic);

		} catch (Exception e) {
			log.error("Fail when cleanig up files", e);
		}
	}

	private void cleanup(Collection<File> files) {
		for (File file : files) {
			if (file.isFile()) {
				if (FileUtils.isFileOlder(file,
						(System.currentTimeMillis() - Config.getInt("application.fileAgeMinutes") * 60 * 1000))) {
					log.info("{} is old. Removing...", file.getName());
					FileUtils.deleteQuietly(file);
				}
			}
		}
	}

	@Override
	public void interrupt() throws UnableToInterruptJobException {
		log.warn("I was interrupted!");
	}

}
