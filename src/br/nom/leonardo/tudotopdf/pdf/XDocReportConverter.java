package br.nom.leonardo.tudotopdf.pdf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.nom.leonardo.tudotopdf.config.Config;

/**
 * Converter for XDocReport
 * 
 * @author leonardo
 *
 */
public class XDocReportConverter implements PDFConverter {

	private Logger log = LoggerFactory.getLogger(XDocReportConverter.class);

	private static final String CODE = "XDocRep";

	public static String getCode() {
		return CODE;
	}

	private static final List<String> SUPPORTED_MIMES = Arrays.asList(new String[] { Config.getString("mime.DOCX") });

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
			InputStream is = new FileInputStream(theFile);
			XWPFDocument document = new XWPFDocument(is);
			PdfOptions options = PdfOptions.create();
			ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
			PdfConverter.getInstance().convert(document, pdfStream, options);
			pdfStream.close();
			ByteArrayInputStream resultStream = new ByteArrayInputStream(pdfStream.toByteArray());
			return resultStream;
		} catch (Exception e) {
			log.error("Fail to create PDF in XDocReport Converter", e);
			throw new PDFConverterException("Fail to create PDF in XDocReport Converter", e);
		}
	}

}
