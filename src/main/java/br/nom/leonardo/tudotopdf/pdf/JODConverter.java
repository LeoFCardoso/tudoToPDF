package br.nom.leonardo.tudotopdf.pdf;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.nom.leonardo.tudotopdf.config.Config;
import br.nom.leonardo.tudotopdf.servlet.AppContext;

public class JODConverter extends AbstractPDFConverter {

	private Logger log = LoggerFactory.getLogger(JODConverter.class);

	public static final String CODE = "JOD";

	@Override
	public String getCode() {
		return CODE;
	}

	private static final List<String> SUPPORTED_MIMES = Arrays
			.asList(new String[] { Config.getString("mime.DOC"), Config.getString("mime.XLS"),
					Config.getString("mime.PPT"), Config.getString("mime.RTF"), Config.getString("mime.TXT"),
					Config.getString("mime.DOCX"), Config.getString("mime.PPTX"), Config.getString("mime.XLSX"),
					Config.getString("mime.ODT"), Config.getString("mime.ODS"), Config.getString("mime.ODP") });

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
			OfficeDocumentConverter converter = new OfficeDocumentConverter(AppContext.getOfficeManager());
			String outFileName = this.getOutputFileName(md5UploadedFile);
			File pdfOutput = new File(Config.getString("application.staticFiles"), outFileName);
			converter.convert(theFile, pdfOutput);
			return pdfOutput;
		} catch (Exception e) {
			log.error("Fail to create PDF in JOD Converter", e);
			log.info("Trying to restart office manager for next request...");
			AppContext.restartOfficeManager();
			throw new PDFConverterException("Fail to create PDF in JOD Converter", e);
		}
	}

}
