package br.nom.leonardo.tudotopdf.pdf;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aspose.words.Document;

import br.nom.leonardo.tudotopdf.config.Config;

public class AsposeWordsConverter implements PDFConverter {

	private Logger log = LoggerFactory.getLogger(AsposeWordsConverter.class);

	public static final String CODE = "Aspose";

	@Override
	public String getCode() {
		return CODE;
	}

	private static final List<String> SUPPORTED_MIMES = Arrays
			.asList(new String[] { Config.getString("mime.DOC"), Config.getString("mime.DOCX"),
					Config.getString("mime.ODT"), Config.getString("mime.RTF"), Config.getString("mime.TXT") });

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
	public File convertPDF(File theFile, String md5UploadedFile) throws PDFConverterException {
		try {
			// TEST ONLY!
			// License license = new License();
			// license.setLicense("");
			String outFileName = md5UploadedFile + "-" + CODE + ".pdf";
			File pdfOutput = new File(Config.getString("application.staticFiles"), outFileName);
			Document doc = new Document(new FileInputStream(theFile));
			doc.save(pdfOutput.getCanonicalPath());
			return pdfOutput;
		} catch (Exception e) {
			log.error("Fail to create PDF in AsposeWords Converter", e);
			throw new PDFConverterException("Fail to create PDF in AsposeWords Converter", e);
		}
	}

}
