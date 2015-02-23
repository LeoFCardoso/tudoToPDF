package br.nom.leonardo.tudotopdf.config;

import java.io.File;
import java.util.Iterator;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.DefaultConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Monostate class. Application configuration based on commons configuration
 * 
 * @author leonardo
 *
 */
public class Config {

	private static Logger log = LoggerFactory.getLogger(Config.class);

	private static Configuration config = null;

	public static String getString(String param) {
		return config.getString(param);
	}

	public static long getLong(String param) {
		return config.getLong(param);
	}

	public static int getInt(String param) {
		return config.getInt(param);
	}

	public static int[] getIntArray(String param) {
		String[] values =  config.getStringArray(param);
		int[] result = new int[values.length];
		for (int i = 0; i < values.length; i++) {
			String value = values[i];
			result[i] = Integer.parseInt(value);
		}
		return result;
	}

	public static Iterator<String> getKeys(String prefix) {
		return config.getKeys(prefix);
	}
	
	static {
		try {
			DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
			builder.setFile(new File("config.xml"));
			config = builder.getConfiguration(true);
		} catch (ConfigurationException e) {
			log.error("Configuration could not be done. This app can't run. Please check 'config.xml' file.", e);
		}
	}


}
