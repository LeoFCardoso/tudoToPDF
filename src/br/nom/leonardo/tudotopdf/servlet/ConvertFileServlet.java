package br.nom.leonardo.tudotopdf.servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.nom.leonardo.tudotopdf.config.Config;
import br.nom.leonardo.tudotopdf.model.ConversionConfiguration;
import br.nom.leonardo.tudotopdf.pdf.PDFConverter;
import br.nom.leonardo.tudotopdf.pdf.PDFConverterException;
import br.nom.leonardo.tudotopdf.pdf.PDFConverterFactory;
import br.nom.leonardo.tudotopdf.pdf.PDFPostProcess;

/**
 * Servlet implementation class ConvertFileServlet
 */
@WebServlet(description = "A servlet to convert generic content to PDF", urlPatterns = { "/fileSubmit" })
@MultipartConfig
public class ConvertFileServlet extends HttpServlet {

	private Logger log = LoggerFactory.getLogger(ConvertFileServlet.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 9089231803242510987L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ConvertFileServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		log.debug("Starting file conversion. Session id=" + request.getSession().getId());

		Part filePart = request.getPart("theFile");

		String uploadFilePath = Config.getString("application.uploadedFiles");
		log.debug("Upload file path is {}.", uploadFilePath);

		if (filePart.getSize() > Config.getLong("application.maxFileSizeBytes")) {
			showError(response, "Uploaded file is bigger than " + Config.getLong("application.maxFileSizeBytes")
					+ " bytes.");
			return;
		}

		// Use UUID to handle same file name uploaded multiple times
		File uploadedFile = new File(uploadFilePath + File.separatorChar + UUID.randomUUID().toString() + '-'
				+ getFileName(filePart));
		FileUtils.copyInputStreamToFile(filePart.getInputStream(), uploadedFile);
		log.debug("File saved to {}.", uploadedFile.getCanonicalPath());

		String uploadedContentType = filePart.getContentType();
		Tika tika = new Tika();
		String realContentType = tika.detect(uploadedFile);

		log.debug("The file name is \"{}\", with uploaded content type {} and real content type {}.",
				getFileName(filePart), uploadedContentType, realContentType);

		boolean isWatermarked = "on".equals(request.getParameter("watermark"));
		log.debug("PDF will be watermarked: " + isWatermarked);

		boolean isProtected = "on".equals(request.getParameter("protected"));
		log.debug("PDF will be protected: " + isProtected);

		String textHeader = StringUtils.isBlank(request.getParameter("headerWmText")) ? "" : request
				.getParameter("headerWmText");
		log.debug("PDF Header: " + textHeader);
		String textTop = StringUtils.isBlank(request.getParameter("topWmText")) ? "" : request
				.getParameter("topWmText");
		log.debug("PDF Top: " + textTop);
		String textMiddle = StringUtils.isBlank(request.getParameter("middleWmText")) ? "" : request
				.getParameter("middleWmText");
		log.debug("PDF Middle: " + textMiddle);
		String textBottom = StringUtils.isBlank(request.getParameter("bottomWmText")) ? "" : request
				.getParameter("bottomWmText");
		log.debug("PDF Bottom: " + textBottom);
		String textFooter = StringUtils.isBlank(request.getParameter("footerWmText")) ? "" : request
				.getParameter("footerWmText");
		log.debug("PDF Footer: " + textFooter);

		int sizeHeader = StringUtils.isBlank(request.getParameter("headerWmSize"))
				|| !StringUtils.isNumeric(request.getParameter("headerWmSize")) ? 0 : Integer.parseInt(request
				.getParameter("headerWmSize"));
		log.debug("PDF Header size: " + sizeHeader);
		int sizeTop = StringUtils.isBlank(request.getParameter("topWmSize"))
				|| !StringUtils.isNumeric(request.getParameter("topWmSize")) ? 0 : Integer.parseInt(request
				.getParameter("topWmSize"));
		log.debug("PDF Top size: " + sizeTop);
		int sizeMiddle = StringUtils.isBlank(request.getParameter("middleWmSize"))
				|| !StringUtils.isNumeric(request.getParameter("middleWmSize")) ? 0 : Integer.parseInt(request
				.getParameter("middleWmSize"));
		log.debug("PDF Middle size: " + sizeMiddle);
		int sizeBottom = StringUtils.isBlank(request.getParameter("bottomWmSize"))
				|| !StringUtils.isNumeric(request.getParameter("bottomWmSize")) ? 0 : Integer.parseInt(request
				.getParameter("bottomWmSize"));
		log.debug("PDF Bottom size: " + sizeBottom);
		int sizeFooter = StringUtils.isBlank(request.getParameter("footerWmSize"))
				|| !StringUtils.isNumeric(request.getParameter("footerWmSize")) ? 0 : Integer.parseInt(request
				.getParameter("footerWmSize"));
		log.debug("PDF Footer size: " + sizeFooter);

		String waterkMarkType = StringUtils.isBlank(request.getParameter("watermarkType")) ? "diagonal" : request
				.getParameter("watermarkType");
		if (!"horizontal".equals(waterkMarkType)) {
			waterkMarkType = "diagonal"; // Allows horizontal and nothing else
		}
		log.debug("Watermark type: " + waterkMarkType);

		int transparency = StringUtils.isBlank(request.getParameter("transparency"))
				|| !StringUtils.isNumeric(request.getParameter("transparency")) ? 50 : Integer.parseInt(request
				.getParameter("transparency"));
		log.debug("Transparency: " + transparency);

		String strategy = StringUtils.isBlank(request.getParameter("strategy")) ? "" : request.getParameter("strategy");
		log.debug("Conversion strategy: " + strategy);

		PDFConverter converter = PDFConverterFactory.createPDFConverter(realContentType, strategy);
		if (converter == null) {
			showError(response, "Invalid strategy for mime type " + realContentType);
			return;
		}

		InputStream pdfIS = null;
		try {
			pdfIS = converter.convertPDF(uploadedFile);
			log.debug("PDF content generated");
		} catch (PDFConverterException e) {
			log.error(e.getMessage(), e);
			showError(response, e.getMessage());
			return;
		}

		// We already have a PDF. Post process it.
		try {
			ConversionConfiguration config = new ConversionConfiguration(isWatermarked, isProtected, textHeader,
					textTop, textMiddle, textBottom, textFooter, sizeHeader, sizeTop, sizeMiddle, sizeBottom,
					sizeFooter, transparency, waterkMarkType);

			pdfIS = PDFPostProcess.process(pdfIS, config);
			log.debug("PDF post processed");
		} catch (PDFConverterException e) {
			log.error(e.getMessage(), e);
			showError(response, e.getMessage());
			return;
		}

		response.setContentType("application/pdf");
		IOUtils.copy(pdfIS, response.getOutputStream());

	}

	private void showError(HttpServletResponse response, String msg) {
		log.error(msg);
		response.setContentType("text/plain");
		response.setStatus(500);
		try {
			PrintWriter out = response.getWriter();
			out.print(msg);
			out.close();
		} catch (IOException e) {
			log.error("Unexpected exception generating error message.", e);
		}
	}

	/**
	 * Helper method to get uploaded file name
	 * 
	 * @param part
	 * @return
	 */
	private String getFileName(Part part) {
		for (String cd : part.getHeader("content-disposition").split(";")) {
			if (cd.trim().startsWith("filename")) {
				String fileName = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
				return fileName.substring(fileName.lastIndexOf('/') + 1).substring(fileName.lastIndexOf('\\') + 1); // MSIE
																													// fix.
			}
		}
		return null;
	}

}
