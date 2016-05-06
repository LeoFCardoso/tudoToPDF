package br.nom.leonardo.tudotopdf.pdf;

import java.io.File;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.util.Matrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.nom.leonardo.tudotopdf.config.Config;
import br.nom.leonardo.tudotopdf.model.ConversionConfiguration;

public class PDFPostProcess {

	private static Logger log = LoggerFactory.getLogger(PDFPostProcess.class);

	/**
	 * Post process a PDF to add watermarks and protection
	 * 
	 * @param pdfFile
	 *            file with PDF content
	 * @param md5PdfFile
	 *            md5 sum of first parameter
	 * @param config
	 *            configuration
	 * @return file with PDF content
	 */
	public static File process(File pdfFile, String md5PdfFile, ConversionConfiguration config)
			throws PDFConverterException {

		PDDocument doc = null;
		try {

			// Allways try to decrypt with blank password
			doc = PDDocument.load(pdfFile, "");

			if (config.isWatermark()) {

				PDExtendedGraphicsState extendedGraphicsState = new PDExtendedGraphicsState();
				extendedGraphicsState.setNonStrokingAlphaConstant(Float.valueOf(config.getTransparency()) / 100f);

				PDFont font = PDType1Font.HELVETICA;
				// InputStream realPath = PDFPostProcess.class.getClassLoader()
				// .getResourceAsStream("STREETWISEBUDDY.TTF");
				// PDFont font = PDType0Font.load(doc, realPath);

				// Iterate on every page to add watermark
				for (PDPage page : doc.getPages()) {
					PDRectangle pageSize = page.getMediaBox();

					PDResources resources = page.getResources();
					PDExtendedGraphicsState graphicsState = null;
					if (resources != null) {
						graphicsState = resources.getExtGState(COSName.TRANSPARENCY);
						if (graphicsState == null) {
							graphicsState = extendedGraphicsState;
						}
						resources.add(graphicsState);
					}

					// calculate the width / height of the string according to the font
					float textWidthMiddle = font.getStringWidth(config.getTextMiddle()) * config.getSizeMiddle()
							/ 1000f;
					float textHeightMiddle = font.getFontDescriptor().getFontBoundingBox().getHeight()
							* config.getSizeMiddle() / 1000f;

					float textWidthTop = font.getStringWidth(config.getTextTop()) * config.getSizeTop() / 1000f;
					float textHeightTop = font.getFontDescriptor().getFontBoundingBox().getHeight()
							* config.getSizeTop() / 1000f;

					float textWidthBottom = font.getStringWidth(config.getTextBottom()) * config.getSizeBottom()
							/ 1000f;
					float textHeightBottom = font.getFontDescriptor().getFontBoundingBox().getHeight()
							* config.getSizeBottom() / 1000f;

					// float textWidthHeader = font.getStringWidth(config.getTextHeader()) *
					// config.getSizeHeader() / 1000f;
					float textHeightHeader = font.getFontDescriptor().getFontBoundingBox().getHeight()
							* config.getSizeHeader() / 1000f;

					// float textWidthFooter = font.getStringWidth(config.getTextFooter()) *
					// config.getSizeFooter() / 1000f;
					// float textHeightFooter =
					// font.getFontDescriptor().getFontBoundingBox().getHeight()
					// * config.getSizeFooter() / 1000f;

					// Page dimensions
					float pageWidth = pageSize.getWidth();
					float pageHeight = pageSize.getHeight();

					PDPageContentStream contentStream = new PDPageContentStream(doc, page, AppendMode.APPEND, true,
							true);

					// Transparent...
					if (graphicsState != null) {
						contentStream.setGraphicsStateParameters(graphicsState);
					}

					// Color
					contentStream.setNonStrokingColor(config.getWaterMarkColor());

					float yMargin = 50;

					if ("horizontal".equals(config.getWaterMarkType())) {
						// Middle
						contentStream.beginText();
						contentStream.setFont(font, config.getSizeMiddle());
						contentStream.newLineAtOffset((pageWidth - textWidthMiddle) / 2,
								yMargin + (pageHeight - textHeightMiddle) / 2);
						contentStream.showText(config.getTextMiddle());
						contentStream.endText();

						// Top
						contentStream.beginText();
						contentStream.setFont(font, config.getSizeTop());
						contentStream.newLineAtOffset((pageWidth - textWidthTop) / 2,
								yMargin + pageHeight / 2 + (pageHeight / 2 - textHeightTop) / 2);
						contentStream.showText(config.getTextTop());
						contentStream.endText();

						// Bottom
						contentStream.beginText();
						contentStream.setFont(font, config.getSizeBottom());
						contentStream.newLineAtOffset((pageWidth - textWidthBottom) / 2,
								yMargin + (pageHeight / 2 - textHeightBottom) / 2);
						contentStream.showText(config.getTextBottom());
						contentStream.endText();
					}

					if ("diagonal".equals(config.getWaterMarkType())) {
						// Middle (diagonal)
						contentStream.beginText();
						contentStream.setFont(font, config.getSizeMiddle());
						double angle = Math.atan(pageHeight / pageWidth);
						contentStream.setTextMatrix(Matrix.getRotateInstance(angle, 0, 0));
						float pageDiagonal = (float) Math.sqrt(Math.pow(pageHeight, 2) + Math.pow(pageWidth, 2));
						contentStream.newLineAtOffset((pageDiagonal - textWidthMiddle) / 2,
								yMargin - textHeightMiddle / 2);
						contentStream.showText(config.getTextMiddle());
						contentStream.endText();

						// Top (diagonal)
						contentStream.beginText();
						contentStream.setTextMatrix(Matrix.getRotateInstance(angle, 0, 0));
						contentStream.setFont(font, config.getSizeTop());
						// float upDiagonal = (float) Math.sqrt(Math.pow(pageHeight / 2, 2) +
						// Math.pow(pageWidth / 2, 2));
						contentStream.newLineAtOffset((pageDiagonal - textWidthTop) / 2,
								yMargin - textHeightTop / 2 + pageHeight / 4);
						contentStream.showText(config.getTextTop());
						contentStream.endText();

						// Bottom (diagonal)
						contentStream.beginText();
						contentStream.setTextMatrix(Matrix.getRotateInstance(angle, 0, 0));
						contentStream.setFont(font, config.getSizeBottom());
						contentStream.newLineAtOffset((pageDiagonal - textWidthBottom) / 2,
								yMargin - textHeightBottom / 2 - pageHeight / 4);
						contentStream.showText(config.getTextBottom());
						contentStream.endText();
					}

					// Header
					contentStream.beginText();
					contentStream.setFont(font, config.getSizeHeader());
					contentStream.newLineAtOffset(10, pageHeight - textHeightHeader);
					contentStream.showText(config.getTextHeader());
					contentStream.endText();

					// Footer
					contentStream.beginText();
					contentStream.setFont(font, config.getSizeFooter());
					contentStream.newLineAtOffset(10, 10);
					contentStream.showText(config.getTextFooter());
					contentStream.endText();

					// close and clean up
					contentStream.close();
				}

			}

			if (config.isProtect()) {
				String ownerPassword = RandomStringUtils.randomAlphanumeric(30);
				String userPassword = "";
				AccessPermission ap = new AccessPermission(0);
				ap.setCanPrint(true);
				ap.setCanPrintDegraded(true);
				int keyLength = 128;
				// TODO AES Encryption
				StandardProtectionPolicy spp = new StandardProtectionPolicy(ownerPassword, userPassword, ap);
				spp.setEncryptionKeyLength(keyLength);
				doc.protect(spp);
			} else {
				// No protection. In fact, this can remove protection from documents.
				// Use with caution
				doc.setAllSecurityToBeRemoved(true);
			}

			// Add tudoToPDF "signature"
			// String creator = doc.getDocumentInformation().getCreator() == null ? "" :
			// doc.getDocumentInformation().getCreator();
			String producer = doc.getDocumentInformation().getProducer() == null ? ""
					: doc.getDocumentInformation().getProducer();
			// doc.getDocumentInformation().setCreator(creator + " - tudoToPDF");
			doc.getDocumentInformation().setProducer(producer + " - tudoToPDF");

			String finalOutFileName = md5PdfFile + "-" + config.hashCode() + ".pdf";
			File finalOutputFile = new File(Config.getString("application.staticFiles"), finalOutFileName);

			doc.save(finalOutputFile);
			doc.close();

			return finalOutputFile;

		} catch (Exception e) {
			log.error("Fail to post process PDF. Document protected?", e);
			throw new PDFConverterException("Fail to post process PDF. Document protected?", e);
		}

	}
}
