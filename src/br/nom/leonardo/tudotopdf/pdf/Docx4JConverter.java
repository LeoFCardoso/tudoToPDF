package br.nom.leonardo.tudotopdf.pdf;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.docx4j.Docx4J;
import org.docx4j.convert.out.FOSettings;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Convert to PDF using DOC4J engine
 * 
 * @author leonardo
 *
 */
public class Docx4JConverter implements PDFConverter {

	private Logger log = LoggerFactory.getLogger(Docx4JConverter.class);

	@Override
	public InputStream convertPDF(File theFile) throws PDFConverterException {

		try {
			WordprocessingMLPackage wordMLPckg = Docx4J.load(theFile);
			
	    	FOSettings foSettings = Docx4J.createFOSettings();
			foSettings.setWmlPackage(wordMLPckg);
			
			ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
			Docx4J.toFO(foSettings, pdfStream, Docx4J.FLAG_EXPORT_PREFER_XSL);
			pdfStream.close();
			
			ByteArrayInputStream resultStream = new ByteArrayInputStream(pdfStream.toByteArray());
			return resultStream;

		} catch (Exception e) {
			log.error("Fail to create PDF in DOC4J Converter", e);
			throw new PDFConverterException("Fail to create PDF in DOC4J Converter", e);
		}

	}

}
