package br.nom.leonardo.tudotopdf.servlet;

import java.io.File;

import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.nom.leonardo.tudotopdf.config.Config;

/**
 * Handles init and destroy tasks within this webapp. It's a monostate.
 * 
 * @author leonardo
 */
public class AppContext {

	private static Logger log = LoggerFactory.getLogger(AppContext.class);

	private static OfficeManager officeManager = null;
	private static DefaultOfficeManagerConfiguration configuration = null;

	static {
		buildJODConfiguration();
	}

	private static void buildJODConfiguration() {
		configuration = new DefaultOfficeManagerConfiguration();
		int[] officePortParam = Config.getIntArray("jod.port");
		if (officePortParam != null) {
			configuration.setPortNumbers(officePortParam);
		}
		String officeHomeParam = Config.getString("jod.home");
		if (officeHomeParam != null) {
			configuration.setOfficeHome(new File(officeHomeParam));
		}
		String officeProfileParam = Config.getString("jod.profile");
		if (officeProfileParam != null) {
			configuration.setTemplateProfileDir(new File(officeProfileParam));
		}
	}

	static void destroy() {
		officeManager.stop();
		log.debug("JOD OfficeManager stoped");
		officeManager = null;
	}

	public static OfficeManager getOfficeManager() {
		/*
		 * In OSX and LibreOffice 5, you must create a symlink for soffice executable. $ pwd
		 * /Applications/LibreOffice.app/Contents/MacOS $ ln -s ./soffice ./soffice.bin
		 */
		if (officeManager == null) {
			officeManager = configuration.buildOfficeManager();
			log.debug("JOD OfficeManager created");
			officeManager.start();
			log.debug("JOD OfficeManager started (lazy)");
		}
		if (!officeManager.isRunning()) {
			officeManager.start();
		}
		return officeManager;
	}

}
