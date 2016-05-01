package br.nom.leonardo.tudotopdf.pdf;

import java.util.Arrays;
import java.util.List;

import br.nom.leonardo.tudotopdf.config.Config;

/**
 * Office To PDF (Windows) Converter. Source / Dest folder must be mounted in the server. Windows
 * server must be using a converter service.
 * 
 * @author leonardo
 *
 */
public class OfficeToPDFConverter extends FilePollingConverter {

	public static final String CODE = "OfficeToPDF";

	@Override
	public String getCode() {
		return CODE;
	}


	private static final List<String> SUPPORTED_MIMES = Arrays.asList(new String[] { Config.getString("mime.DOC"),
			Config.getString("mime.XLS"), Config.getString("mime.PPT"), Config.getString("mime.RTF"),
			Config.getString("mime.TXT"), Config.getString("mime.DOCX"), Config.getString("mime.PPTX"),
			Config.getString("mime.XLSX") });

	static boolean isContentSupported(String contentType) {
		return SUPPORTED_MIMES.contains(contentType);
	}

	/**
	 * @return the supported extensions from this converter
	 */
	public static List<String> supportedExtensions() {
		return PDFConverterFactory.supportedExtensions(SUPPORTED_MIMES);
	}

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
