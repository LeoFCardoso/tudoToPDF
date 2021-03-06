package br.nom.leonardo.tudotopdf.pdf;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.nom.leonardo.tudotopdf.config.Config;
import br.nom.leonardo.tudotopdf.model.PdfboxConfiguration;

/**
 * Convert using PDFBox directly. Mainly used to image formats using Java Image API (Java advanced imaging)
 * 
 * @author leonardo
 *
 */
public class PDFBoxConverter extends AbstractPDFConverter {

	private Logger log = LoggerFactory.getLogger(PDFBoxConverter.class);

	private PdfboxConfiguration pdfboxConfig;

	public PDFBoxConverter(PdfboxConfiguration pdfboxConfig) {
		this.pdfboxConfig = pdfboxConfig;
	}

	public static final String CODE = "PDFBox";

	@Override
	public String getCode() {
		return CODE;
	}

	private static final List<String> SUPPORTED_MIMES = Arrays
			.asList(new String[] { Config.getString("mime.BMP"), Config.getString("mime.GIF"),
					Config.getString("mime.JPG"), Config.getString("mime.PNG"), Config.getString("mime.TIF") });

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
	String getOutFileNameSuffix() {
		return "" + pdfboxConfig.hashCode();
	}

	@Override
	public File convertPDF(File theFile, String md5UploadedFile) throws PDFConverterException {
		try {
			Tika tika = new Tika();
			String realContentType = tika.detect(theFile);

			ImageInputStream isFile = ImageIO.createImageInputStream(theFile);
			if (isFile == null || isFile.length() == 0) {
				log.error("Fail to read file {}", theFile.getCanonicalPath());
			}
			Iterator<ImageReader> iterImgReaders = ImageIO.getImageReaders(isFile);
			if (iterImgReaders == null || !iterImgReaders.hasNext()) {
				throw new PDFConverterException(
						"Image file format not supported by java ImageIO API (is Java Advanced Imaging installed?)");
			}
			// First reader compatible will be used
			ImageReader imgReader = (ImageReader) iterImgReaders.next();
			imgReader.setInput(isFile);
			int numberOfPages = imgReader.getNumImages(true);
			log.debug("This image has {} pages.", numberOfPages);

			PDDocument doc = new PDDocument();

			for (int i = 0; i < numberOfPages; i++) {

				BufferedImage buffImage = imgReader.read(i);

				PDPage page = null;
				switch (pdfboxConfig.getPageSize()) {
				case "LETTER":
					page = new PDPage(PDRectangle.LETTER);
					break;
				case "LEGAL":
					page = new PDPage(PDRectangle.LEGAL);
					break;
				case "A2":
					page = new PDPage(PDRectangle.A2);
					break;
				case "A3":
					page = new PDPage(PDRectangle.A3);
					break;
				case "A4":
					page = new PDPage(PDRectangle.A4);
					break;
				case "A5":
					page = new PDPage(PDRectangle.A5);
					break;
				case "A6":
					page = new PDPage(PDRectangle.A6);
					break;
				case "IMAGE":
					page = new PDPage(new PDRectangle(buffImage.getWidth(), buffImage.getHeight()));
					break;

				default:
					page = new PDPage(PDRectangle.A4);
					break;
				}

				doc.addPage(page);

				PDImageXObject pdfBoxImage = null;
				if (Config.getString("mime.JPG").equals(realContentType)) {
					// JPG compression
					pdfBoxImage = JPEGFactory.createFromImage(doc, buffImage);
				} else {
					// All other - PNG compression
					pdfBoxImage = LosslessFactory.createFromImage(doc, buffImage);
				}
				// TIFF compression (uses temporary file - TODO fix, because it's not working with
				// all tiffs
				/*
				 * File tmpTiffFile = File.createTempFile("Tiff-Temp", ".tiff"); ImageIO.write(buffImage, "tiff",
				 * tmpTiffFile); PDCcitt pdfBoxImage = new PDCcitt(doc, new RandomAccessFile(tmpTiffFile, "r"));
				 */

				float inch = 72.0f;
				float cm = inch / 2.54f;

				PDPageContentStream contentStream = new PDPageContentStream(doc, page);

				Float imageWidth = "".equals(pdfboxConfig.getWidthCm()) ? null
						: Float.parseFloat(pdfboxConfig.getWidthCm()) * cm;
				Float imageHeight = "".equals(pdfboxConfig.getHeightCm()) ? null
						: Float.parseFloat(pdfboxConfig.getHeightCm()) * cm;

				float imageWidthHeighRatio = (float) pdfBoxImage.getWidth() / pdfBoxImage.getHeight();
				if (imageWidth == null && imageHeight != null) {
					imageWidth = imageHeight * imageWidthHeighRatio;
				}
				if (imageHeight == null && imageWidth != null) {
					imageHeight = imageWidth / imageWidthHeighRatio;
				}
				if (imageWidth == null && imageHeight == null) {
					// full page
					imageWidth = page.getCropBox().getWidth();
					imageHeight = page.getCropBox().getHeight();
				}

				int x = 0;
				x = (int) ((page.getCropBox().getWidth() - imageWidth) / 2);
				int y = 0;
				y = (int) ((page.getCropBox().getHeight() - imageHeight) / 2);

				contentStream.drawImage(pdfBoxImage, x, y, imageWidth, imageHeight);

				contentStream.close();

				/*
				 * TIFF compression: tmpTiffFile.delete();
				 */

				log.debug("Page {} from image generated in PDF", i + 1);
			}

			String outFileName = this.getOutputFileName(md5UploadedFile);

			File outputFile = new File(Config.getString("application.staticFiles"), outFileName);
			doc.save(outputFile);
			doc.close();
			return outputFile;

		} catch (Exception e) {
			final String errorMsg = "Fail to create PDF in PDFBox";
			log.error(errorMsg, e);
			throw new PDFConverterException(errorMsg, e);
		}

	}

	public PdfboxConfiguration getPdfboxConfig() {
		return pdfboxConfig;
	}

	public void setPdfboxConfig(PdfboxConfiguration pdfboxConfig) {
		this.pdfboxConfig = pdfboxConfig;
	}

}
