package br.nom.leonardo.tudotopdf.pdf;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.apache.pdfbox.pdmodel.encryption.StandardSecurityHandler;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.PDExtendedGraphicsState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.nom.leonardo.tudotopdf.model.ConversionConfiguration;

public class PDFPostProcess {

	private static Logger log = LoggerFactory.getLogger(PDFPostProcess.class);

	/**
	 * Post process a PDF to add watermarks and protection
	 * 
	 * @param pdfIS
	 *            stream with PDF content
	 * @param config
	 *            configuration
	 * @return stream with PDF content
	 */
	public static InputStream process(InputStream pdfIS, ConversionConfiguration config) throws PDFConverterException {

		PDDocument doc = null;
		try {

			doc = PDDocument.load(pdfIS);

			// Try to decrypt with blank password
			if (doc.isEncrypted()) {
				doc.decrypt("");
			}

			if (config.isWatermark()) {

				PDExtendedGraphicsState graphicsState = new PDExtendedGraphicsState();
				graphicsState.setNonStrokingAlphaConstant(Float.valueOf(config.getTransparency()) / 100f);

				@SuppressWarnings("rawtypes")
				List allPages = doc.getDocumentCatalog().getAllPages();

				PDFont font = PDType1Font.getStandardFont("Helvetica");

				// Iterate on every page to add watermark
				for (int i = 0; i < allPages.size(); i++) {

					PDPage page = (PDPage) allPages.get(i);
					PDRectangle pageSize = page.findMediaBox();

					PDResources resources = page.findResources();
					Map<String, PDExtendedGraphicsState> graphicsStateDictionary = resources.getGraphicsStates();
					if (graphicsStateDictionary == null) {
						graphicsStateDictionary = new TreeMap<String, PDExtendedGraphicsState>();
					}
					String graphicStateDictName = "TransparentLayerGraphicState";
					graphicsStateDictionary.put(graphicStateDictName, graphicsState);
					resources.setGraphicsStates(graphicsStateDictionary);

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

					PDPageContentStream contentStream = new PDPageContentStream(doc, page, true, true, true);

					// http://jesseontech.blogspot.com.br/2012/07/opacity-in-pdfboxs-pdpagecontentstream.html
					contentStream.appendRawCommands("/" + graphicStateDictName + " gs\n");

					// Color
					// More Info: http://docs.oracle.com/javase/7/docs/api/java/awt/Color.html
					Field field = Color.class.getField("lightGray"); // TODO param the color
					Color color = (Color) field.get(null);
					contentStream.setNonStrokingColor(color);

					float yMargin = 50;

					if ("horizontal".equals(config.getWaterMarkType())) {
						// Middle
						contentStream.beginText();
						contentStream.setFont(font, config.getSizeMiddle());
						contentStream.moveTextPositionByAmount((pageWidth - textWidthMiddle) / 2, yMargin
								+ (pageHeight - textHeightMiddle) / 2);
						contentStream.drawString(config.getTextMiddle());
						contentStream.endText();

						// Top
						contentStream.beginText();
						contentStream.setFont(font, config.getSizeTop());
						contentStream.moveTextPositionByAmount((pageWidth - textWidthTop) / 2, yMargin + pageHeight / 2
								+ (pageHeight / 2 - textHeightTop) / 2);
						contentStream.drawString(config.getTextTop());
						contentStream.endText();

						// Bottom
						contentStream.beginText();
						contentStream.setFont(font, config.getSizeBottom());
						contentStream.moveTextPositionByAmount((pageWidth - textWidthBottom) / 2, yMargin
								+ (pageHeight / 2 - textHeightBottom) / 2);
						contentStream.drawString(config.getTextBottom());
						contentStream.endText();
					}

					if ("diagonal".equals(config.getWaterMarkType())) {
						// Middle (diagonal)
						contentStream.beginText();
						contentStream.setFont(font, config.getSizeMiddle());
						double angle = Math.atan(pageHeight / pageWidth);
						contentStream.setTextRotation(angle, 0, 0);
						float pageDiagonal = (float) Math.sqrt(Math.pow(pageHeight, 2) + Math.pow(pageWidth, 2));
						contentStream.moveTextPositionByAmount((pageDiagonal - textWidthMiddle) / 2, yMargin
								- textHeightMiddle / 2);
						contentStream.drawString(config.getTextMiddle());
						contentStream.endText();

						// Top (diagonal)
						contentStream.beginText();
						contentStream.setTextRotation(angle, 0, 0);
						contentStream.setFont(font, config.getSizeTop());
						// float upDiagonal = (float) Math.sqrt(Math.pow(pageHeight / 2, 2) +
						// Math.pow(pageWidth / 2, 2));
						contentStream.moveTextPositionByAmount((pageDiagonal - textWidthTop) / 2, yMargin
								- textHeightTop / 2 + pageHeight / 4);
						contentStream.drawString(config.getTextTop());
						contentStream.endText();

						// Bottom (diagonal)
						contentStream.beginText();
						contentStream.setTextRotation(angle, 0, 0);
						contentStream.setFont(font, config.getSizeBottom());
						contentStream.moveTextPositionByAmount((pageDiagonal - textWidthBottom) / 2, yMargin
								- textHeightBottom / 2 - pageHeight / 4);
						contentStream.drawString(config.getTextBottom());
						contentStream.endText();
					}

					// Header
					contentStream.beginText();
					contentStream.setFont(font, config.getSizeHeader());
					contentStream.moveTextPositionByAmount(10, pageHeight - textHeightHeader);
					contentStream.drawString(config.getTextHeader());
					contentStream.endText();

					// Footer
					contentStream.beginText();
					contentStream.setFont(font, config.getSizeFooter());
					contentStream.moveTextPositionByAmount(10, 10);
					contentStream.drawString(config.getTextFooter());
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

				StandardProtectionPolicy spp = new StandardProtectionPolicy(ownerPassword, userPassword, ap);
				spp.setEncryptionKeyLength(128);

				StandardSecurityHandler ssh = new StandardSecurityHandler(spp);
				ssh.setKeyLength(128); // TODO AES not working. Maybe in PDFBOX 2.0
				ssh.setAES(true);

				doc.setSecurityHandler(ssh);
				doc.protect(spp);
			}

			// Add tudoToPDF "signature"
			// String creator = doc.getDocumentInformation().getCreator() == null ? "" :
			// doc.getDocumentInformation().getCreator();
			String producer = doc.getDocumentInformation().getProducer() == null ? "" : doc.getDocumentInformation()
					.getProducer();
			// doc.getDocumentInformation().setCreator(creator + " - tudoToPDF");
			doc.getDocumentInformation().setProducer(producer + " - tudoToPDF");

			ByteArrayOutputStream postProcessStream = new ByteArrayOutputStream();
			doc.save(postProcessStream);
			doc.close();
			postProcessStream.close();

			return new ByteArrayInputStream(postProcessStream.toByteArray());

		} catch (Exception e) {
			log.error("Fail to post process PDF. Document protected?", e);
			throw new PDFConverterException("Fail to post process PDF. Document protected?", e);
		}

	}

}
