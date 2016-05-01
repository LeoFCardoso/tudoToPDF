package br.nom.leonardo.tudotopdf.servlet;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import br.nom.leonardo.tudotopdf.config.Config;

public class WebAppContextListener implements ServletContextListener {

	public void contextInitialized(ServletContextEvent event) {
		File outputDir = new File(Config.getString("application.staticFiles"));
		outputDir.mkdirs();
	}

	public void contextDestroyed(ServletContextEvent event) {
		AppContext.destroy();
	}

}
