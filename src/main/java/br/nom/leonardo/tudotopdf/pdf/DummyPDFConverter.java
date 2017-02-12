package br.nom.leonardo.tudotopdf.pdf;

import java.io.File;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.nom.leonardo.tudotopdf.config.Config;

/**
 * This is a dummy PDF converter only to test purposes. Only a single PDF dummy page is generated.
 * 
 * @author leonardo
 *
 */
public class DummyPDFConverter extends AbstractPDFConverter {

	private Logger log = LoggerFactory.getLogger(DummyPDFConverter.class);

	public static final String CODE = "Dummy";
	
	public String getCode() {
		return CODE;
	}

	@Override
	public File convertPDF(File theFile, String md5UploadedFile) throws PDFConverterException {

		try {
			PDDocument document = new PDDocument();
			PDPage page = new PDPage();
			document.addPage(page);
			PDFont font = PDType1Font.HELVETICA_BOLD;
			PDPageContentStream contentStream = new PDPageContentStream(document, page);
			contentStream.beginText();
			contentStream.setFont(font, 12);
			contentStream.newLineAtOffset(100, 700);
			contentStream.showText("This is a test PDF file.");
			contentStream.endText();
			contentStream.close();
			String outFileName = this.getOutputFileName(md5UploadedFile);
			File outputFile = new File(Config.getString("application.staticFiles"), outFileName);
			document.save(outputFile);
			document.close();
			return outputFile;
		} catch (Exception e) {
			log.error("Fail to create dummy PDF", e);
			throw new PDFConverterException(e);
		}

	}

}
