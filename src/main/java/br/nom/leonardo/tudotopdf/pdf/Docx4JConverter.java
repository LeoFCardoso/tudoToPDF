package br.nom.leonardo.tudotopdf.pdf;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;

import org.docx4j.Docx4J;
import org.docx4j.convert.out.FOSettings;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.nom.leonardo.tudotopdf.config.Config;

/**
 * Convert to PDF using DOC4J engine
 * 
 * @author leonardo
 */
public class Docx4JConverter extends AbstractPDFConverter {

	private Logger log = LoggerFactory.getLogger(Docx4JConverter.class);

	public static final String CODE = "Docx4j";

	@Override
	public String getCode() {
		return CODE;
	}

	private static final List<String> SUPPORTED_MIMES = Arrays.asList(new String[] { Config.getString("mime.DOCX"),
			Config.getString("mime.PPTX"), Config.getString("mime.XLSX") });

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
			WordprocessingMLPackage wordMLPckg = Docx4J.load(theFile);
			FOSettings foSettings = Docx4J.createFOSettings();
			foSettings.setWmlPackage(wordMLPckg);
			String outFileName = this.getOutputFileName(md5UploadedFile);
			File outputFile = new File(Config.getString("application.staticFiles"), outFileName);
			FileOutputStream pdfStream = new FileOutputStream(outputFile);
			Docx4J.toFO(foSettings, pdfStream, Docx4J.FLAG_EXPORT_PREFER_XSL);
			pdfStream.close();
			return outputFile;
		} catch (Exception e) {
			log.error("Fail to create PDF in DOC4J Converter", e);
			throw new PDFConverterException("Fail to create PDF in DOC4J Converter", e);
		}
	}

}
