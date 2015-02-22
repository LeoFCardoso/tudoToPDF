package br.nom.leonardo.tudotopdf.pdf;

import br.nom.leonardo.tudotopdf.config.Config;

/**
 * Office To PDF (Windows) Converter. Source / Dest folder must be mounted in the server. Windows
 * server must be using a converter service.
 * 
 * @author leonardo
 *
 */
public class OfficeToPDFConverter extends FilePollingConverter {

	@Override
	protected String getSourceFilesFolder() {
		return Config.getString("officetopdf.sourceFolder");
	}

	@Override
	protected String getDestinationFilesFolder() {
		return Config.getString("officetopdf.destFolder");
	}

	@Override
	protected int getTimeOutMinutes() {
		return Config.getInt("officetopdf.timeoutMinutes");
	}

}
