package br.nom.leonardo.tudotopdf.pdf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import br.nom.leonardo.tudotopdf.config.Config;
import br.nom.leonardo.tudotopdf.model.ConversionConfiguration;

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
	 * @param config
	 *            configuration for conversion
	 * @return
	 */
	public static PDFConverter createPDFConverter(String contentType, String strategy, ConversionConfiguration config) {

		if (Docx4JConverter.CODE.equals(strategy) && Docx4JConverter.isContentSupported(contentType)) {
			return new Docx4JConverter();
		}

		if (JODConverter.CODE.equals(strategy) && JODConverter.isContentSupported(contentType)) {
			return new JODConverter();
		}

		if (XDocReportConverter.CODE.equals(strategy) && XDocReportConverter.isContentSupported(contentType)) {
			return new XDocReportConverter();
		}

		if (OfficeToPDFConverter.CODE.equals(strategy) && OfficeToPDFConverter.isContentSupported(contentType)) {
			return new OfficeToPDFConverter();
		}

		if (AsposeWordsConverter.CODE.equals(strategy) && AsposeWordsConverter.isContentSupported(contentType)) {
			return new AsposeWordsConverter();
		}

		if (PDFBoxConverter.CODE.equals(strategy) && PDFBoxConverter.isContentSupported(contentType)) {
			return new PDFBoxConverter();
		}

		if (PDF2PDFOCRConverter.CODE.equals(strategy) && PDF2PDFOCRConverter.isContentSupported(contentType)) {
			return new PDF2PDFOCRConverter(config.getPdf2pdfocrConfig());
		}

		// At this point, PDF will be passed "as is"
		if ("application/pdf".equals(contentType)) {
			return new NoConverter();
		}

		if (DummyPDFConverter.CODE.equals(strategy)) {
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
