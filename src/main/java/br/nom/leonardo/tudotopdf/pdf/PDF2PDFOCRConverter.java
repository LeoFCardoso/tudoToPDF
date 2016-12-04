package br.nom.leonardo.tudotopdf.pdf;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.nom.leonardo.tudotopdf.config.Config;
import br.nom.leonardo.tudotopdf.model.Pdf2pdfocrConfiguration;

/**
 * Convert using PDF2PDFOCR script Results in searchable PDF's.
 * 
 * @author leonardo
 *
 */
public class PDF2PDFOCRConverter implements PDFConverter {

	private Logger log = LoggerFactory.getLogger(PDF2PDFOCRConverter.class);

	public static final String CODE = "PDF2PDFOCR";

	private Pdf2pdfocrConfiguration pdf2pdfocrConfig;

	public PDF2PDFOCRConverter(Pdf2pdfocrConfiguration pdf2pdfocrConfig) {
		this.pdf2pdfocrConfig = pdf2pdfocrConfig;
	}

	@Override
	public String getCode() {
		return CODE;
	}

	private static final List<String> SUPPORTED_MIMES = Arrays.asList(new String[] { Config.getString("mime.PDF"),
			Config.getString("mime.JPG"), Config.getString("mime.TIF"), Config.getString("mime.PNG") });

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

			// Call pdf2pdfocr.py script
			// https://github.com/LeoFCardoso/pdf2pdfocr

			// This will be the output file from script.
			String outFileName = md5UploadedFile + "-" + CODE + "-" + pdf2pdfocrConfig.hashCode() + ".pdf";

			File pdfOutput = new File(Config.getString("application.staticFiles"), outFileName);

			List<String> command = new ArrayList<String>();
			command.add(Config.getString("pdf2pdfocr.scriptPath"));

			if (pdf2pdfocrConfig.isFlagT()) {
				command.add("-t");
			}

			if (pdf2pdfocrConfig.isFlagA()) {
				command.add("-a");
			}

			if (pdf2pdfocrConfig.isFlagF()) {
				command.add("-f");
			}

			if ((!"".equals(pdf2pdfocrConfig.getFlagGValue())) && (pdf2pdfocrConfig.getFlagGValue() != null)) {
				command.add("-g");
				command.add(pdf2pdfocrConfig.getFlagGValue());
			}

			if (pdf2pdfocrConfig.isFlagD()) {
				command.add("-d");
				command.add(pdf2pdfocrConfig.getFlagDValue());
			}

			if (pdf2pdfocrConfig.isFlagP()) {
				command.add("-p");
			}

			// Fixed commands
			command.add("-o");
			command.add(pdfOutput.getAbsolutePath());
			command.add("-i");			
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
			process.waitFor();
			String outputFromScript = IOUtils.toString(process.getInputStream());
			String errorFromScript = IOUtils.toString(process.getErrorStream());
			log.info("pdf2pdfocr.sh ended with stdout {} and stderr {}.", outputFromScript, errorFromScript);

			if (process.exitValue() != 0) {
				pdfOutput.delete();
				log.error("Fail in PDF2PDFOCR");
				throw new PDFConverterException("Fail in PDF2PDFOCR: " + errorFromScript);
			}

			return pdfOutput;

		} catch (IOException | InterruptedException e) {
			final String errorMsg = "Fail to create PDF in PDF2PDFOCR";
			log.error(errorMsg, e);
			throw new PDFConverterException(errorMsg, e);
		}

	}

	public Pdf2pdfocrConfiguration getPdf2pdfocrConfig() {
		return pdf2pdfocrConfig;
	}

	public void setPdf2pdfocrConfig(Pdf2pdfocrConfiguration pdf2pdfocrConfig) {
		this.pdf2pdfocrConfig = pdf2pdfocrConfig;
	}
}
