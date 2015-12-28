package br.nom.leonardo.tudotopdf.pdf;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.nom.leonardo.tudotopdf.config.Config;

/**
 * Convert using PDF2PDFOCR script Results in searchable PDF's.
 * 
 * @author leonardo
 *
 */
public class PDF2PDFOCRConverter implements PDFConverter {

	private Logger log = LoggerFactory.getLogger(PDF2PDFOCRConverter.class);

	private static final String CODE = "PDF2PDFOCR";

	public static String getCode() {
		return CODE;
	}

	private static final List<String> SUPPORTED_MIMES = Arrays.asList(
			new String[] { Config.getString("mime.PDF"), Config.getString("mime.JPG"), Config.getString("mime.TIF") });

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

			// Call pdf2pdfocr.sh script
			// https://github.com/LeoFCardoso/pdf2pdfocr

			// This will be the output file from script.
			File tmpPDFOutput = File.createTempFile("OCR-PDF-Temp-Output", ".pdf");

			// Run the script
			List<String> command = new ArrayList<String>();
			command.add(Config.getString("pdf2pdfocr.scriptPath"));
			command.add("-t"); // Using safe text mode. Does not process if original PDF already contains text
			command.add("-o");
			command.add(tmpPDFOutput.getAbsolutePath());
			command.add(theFile.getAbsolutePath());

			ProcessBuilder builder = new ProcessBuilder(command);
			Map<String, String> env = builder.environment();
			// Load all "env" options in file
			Iterator<String> allEnvs = Config.getKeys("pdf2pdfocr.env.");
			while (allEnvs.hasNext()) {
				String key = allEnvs.next();
				env.put(key.substring("pdf2pdfocr.env.".length()), Config.getString(key));
			}
			builder.directory(new File(Config.getString("pdf2pdfocr.workDir")));

			final Process process = builder.start();
			String outputFromScript = IOUtils.toString(process.getInputStream());
			String errorFromScript = IOUtils.toString(process.getErrorStream());
			log.info("pdf2pdfocr.sh ended with stdout {} and stderr {}.", outputFromScript, errorFromScript);

			if (process.exitValue() != 0) {
				tmpPDFOutput.delete();
				log.error("Fail to OCR when post process PDF.");
				throw new PDFConverterException("Fail to OCR when post process PDF. Please see logs.");
			}

			ByteArrayInputStream resultStream = new ByteArrayInputStream(
					IOUtils.toByteArray(new FileInputStream(tmpPDFOutput)));
			tmpPDFOutput.delete();
			return resultStream;

		} catch (Exception e) {
			final String errorMsg = "Fail to create PDF in PDF2PDFOCR";
			log.error(errorMsg, e);
			throw new PDFConverterException(errorMsg, e);
		}

	}
}
