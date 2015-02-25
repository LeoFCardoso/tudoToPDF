package br.nom.leonardo.tudotopdf.pdf;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.nom.leonardo.tudotopdf.config.Config;

import com.aspose.words.Document;

public class AsposeWordsConverter implements PDFConverter {

	private Logger log = LoggerFactory.getLogger(AsposeWordsConverter.class);

	private static final String CODE = "Aspose";

	public static String getCode() {
		return CODE;
	}

	private static final List<String> SUPPORTED_MIMES = Arrays.asList(new String[] { Config.getString("mime.DOC"),
			Config.getString("mime.DOCX"), Config.getString("mime.ODT"), Config.getString("mime.RTF"),
			Config.getString("mime.TXT") });

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
	public InputStream convertPDF(File theFile) throws PDFConverterException {
		try {
			// TEST ONLY!
			// License license = new License();
			// license.setLicense("");

			File tmpPDFOutput = File.createTempFile("JOD-PDF-Temp", ".pdf");
			Document doc = new Document(new FileInputStream(theFile));
			doc.save(tmpPDFOutput.getCanonicalPath());

			ByteArrayInputStream resultStream = new ByteArrayInputStream(IOUtils.toByteArray(new FileInputStream(
					tmpPDFOutput)));

			tmpPDFOutput.delete();

			return resultStream;

		} catch (Exception e) {
			log.error("Fail to create PDF in AsposeWords Converter", e);
			throw new PDFConverterException("Fail to create PDF in AsposeWords Converter", e);
		}
	}

}
