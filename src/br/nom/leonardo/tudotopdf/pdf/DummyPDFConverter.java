package br.nom.leonardo.tudotopdf.pdf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a dummy PDF converter only to test purposes. Only a single PDF dummy page is generated.
 * 
 * @author leonardo
 *
 */
public class DummyPDFConverter implements PDFConverter {

	private Logger log = LoggerFactory.getLogger(DummyPDFConverter.class);

	@Override
	public InputStream convertPDF(File theFile) throws PDFConverterException {

		try {
			PDDocument document = new PDDocument();
			PDPage page = new PDPage();
			document.addPage(page);
			PDFont font = PDType1Font.HELVETICA_BOLD;
			PDPageContentStream contentStream = new PDPageContentStream(document, page);
			contentStream.beginText();
			contentStream.setFont(font, 12);
			contentStream.moveTextPositionByAmount(100, 700);
			contentStream.drawString("This is a test PDF file.");
			contentStream.endText();
			contentStream.close();
			ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
			document.save(pdfStream);
			document.close();
			return new ByteArrayInputStream(pdfStream.toByteArray());
		} catch (Exception e) {
			log.error("Fail to create dummy PDF", e);
			throw new PDFConverterException(e);
		}

	}

}
