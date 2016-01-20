package br.nom.leonardo.tudotopdf.pdf;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.nom.leonardo.tudotopdf.config.Config;
import br.nom.leonardo.tudotopdf.servlet.AppContext;

public class JODConverter implements PDFConverter {

	private Logger log = LoggerFactory.getLogger(JODConverter.class);

	private static final String CODE = "JOD";

	public static String getCode() {
		return CODE;
	}

	private static final List<String> SUPPORTED_MIMES = Arrays.asList(new String[] { Config.getString("mime.DOC"),
			Config.getString("mime.XLS"), Config.getString("mime.PPT"), Config.getString("mime.RTF"),
			Config.getString("mime.TXT"), Config.getString("mime.DOCX"), Config.getString("mime.PPTX"),
			Config.getString("mime.XLSX"), Config.getString("mime.ODT"), Config.getString("mime.ODS"),
			Config.getString("mime.ODP") });

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
			OfficeDocumentConverter converter = new OfficeDocumentConverter(AppContext.getOfficeManager());
			File tmpPDFOutput = File.createTempFile("JOD-PDF-Temp", ".pdf");
			converter.convert(theFile, tmpPDFOutput);
			ByteArrayInputStream resultStream = new ByteArrayInputStream(IOUtils.toByteArray(new FileInputStream(
					tmpPDFOutput)));
			tmpPDFOutput.delete();
			return resultStream;
		} catch (Exception e) {
			log.error("Fail to create PDF in JOD Converter", e);
			log.info("Trying to restart office manager for next request...");
			AppContext.restartOfficeManager();
			throw new PDFConverterException("Fail to create PDF in JOD Converter", e);
		}
	}

}
