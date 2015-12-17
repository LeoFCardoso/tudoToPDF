package br.nom.leonardo.tudotopdf.pdf;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
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

/**
 * Convert using PDFBox directly. Mainly used to image formats using Java Image API (Java advanced
 * imaging)
 * 
 * @author leonardo
 *
 */
public class PDFBoxConverter implements PDFConverter {

	private Logger log = LoggerFactory.getLogger(PDFBoxConverter.class);

	private static final String CODE = "PDFBox";

	public static String getCode() {
		return CODE;
	}

	private static final List<String> SUPPORTED_MIMES = Arrays.asList(new String[] { Config.getString("mime.BMP"),
			Config.getString("mime.GIF"), Config.getString("mime.JPG"), Config.getString("mime.PNG"),
			Config.getString("mime.TIF") });

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

				// This code use fixed page size
				// PDPage page = new PDPage(PDPage.PAGE_SIZE_A4);

				// This code creates a page with image size
				PDPage page = new PDPage(new PDRectangle(buffImage.getWidth(), buffImage.getHeight()));
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
				 * File tmpTiffFile = File.createTempFile("Tiff-Temp", ".tiff");
				 * ImageIO.write(buffImage, "tiff", tmpTiffFile); PDCcitt pdfBoxImage = new
				 * PDCcitt(doc, new RandomAccessFile(tmpTiffFile, "r"));
				 */

				PDPageContentStream contentStream = new PDPageContentStream(doc, page);
				float scaleW = page.getArtBox().getWidth() / pdfBoxImage.getWidth();
				float scaleH = page.getArtBox().getHeight() / pdfBoxImage.getHeight();
				contentStream.drawXObject(pdfBoxImage, 0, 0, pdfBoxImage.getWidth() * scaleW, pdfBoxImage.getHeight()
						* scaleH);
				contentStream.close();

				/*
				 * TIFF compression: tmpTiffFile.delete();
				 */

				log.debug("Page {} from image generated in PDF", i + 1);
			}

			ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
			doc.save(pdfStream);
			doc.close();
			return new ByteArrayInputStream(pdfStream.toByteArray());

		} catch (Exception e) {
			final String errorMsg = "Fail to create PDF in PDFBox";
			log.error(errorMsg, e);
			throw new PDFConverterException(errorMsg, e);
		}

	}
}
