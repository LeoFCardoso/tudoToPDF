package br.nom.leonardo.tudotopdf.pdf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import br.nom.leonardo.tudotopdf.config.Config;

/**
 * Factory class to create PDFConverter instances
 * 
 * @author leonardo
 *
 */
public class PDFConverterFactory {

	/**
	 * @param contentType
	 *            real content type
	 * @param strategy
	 *            selected strategy
	 * @return
	 */
	public static PDFConverter createPDFConverter(String contentType, String strategy) {

		if (Docx4JConverter.getCode().equals(strategy) && Docx4JConverter.isContentSupported(contentType)) {
			return new Docx4JConverter();
		}

		if (JODConverter.getCode().equals(strategy) && JODConverter.isContentSupported(contentType)) {
			return new JODConverter();
		}

		if (XDocReportConverter.getCode().equals(strategy) && XDocReportConverter.isContentSupported(contentType)) {
			return new XDocReportConverter();
		}

		if (OfficeToPDFConverter.getCode().equals(strategy) && OfficeToPDFConverter.isContentSupported(contentType)) {
			return new OfficeToPDFConverter();
		}

		if (AsposeWordsConverter.getCode().equals(strategy) && AsposeWordsConverter.isContentSupported(contentType)) {
			return new AsposeWordsConverter();
		}

		if (PDFBoxConverter.getCode().equals(strategy) && PDFBoxConverter.isContentSupported(contentType)) {
			return new PDFBoxConverter();
		}

		if (PDF2PDFOCRConverter.getCode().equals(strategy) && PDF2PDFOCRConverter.isContentSupported(contentType)) {
			return new PDF2PDFOCRConverter();
		}
		
		// At this point, PDF will be passed "as is"
		if ("application/pdf".equals(contentType)) {
			return new NoConverter();
		}
		
		if ("Dummy".equals(strategy)) {
			return new DummyPDFConverter();
		}

		return null;
	}

	/**
	 * @return the supported extensions from a list of supported mimes (read configuration file)
	 */
	static List<String> supportedExtensions(List<String> supportedMimes) {
		final String prefixMime = "mime.";
		Iterator<String> allExt = Config.getKeys(prefixMime);
		List<String> out = new ArrayList<String>();
		while (allExt.hasNext()) {
			String extension = allExt.next();
			if (supportedMimes.contains(Config.getString(extension))) {
				out.add(extension.substring(prefixMime.length()));
			}
		}
		Collections.sort(out);
		return out;
	}

}
