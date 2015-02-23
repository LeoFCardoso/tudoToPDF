package br.nom.leonardo.tudotopdf.pdf;

import java.util.Arrays;

/**
 * Factory class to create PDFConverter instances
 * 
 * @author leonardo
 *
 */
public class PDFConverterFactory {

	private static final String DOC = "application/msword";
	private static final String XLS = "application/vnd.ms-excel";
	private static final String PPT = "application/vnd.ms-powerpoint";
	
	private static final String RTF = "application/rtf";
	
	private static final String TXT = "text/plain";

	private static final String DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
	private static final String XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
	private static final String PPTX = "application/vnd.openxmlformats-officedocument.presentationml.presentation";

	private static final String ODT = "application/vnd.oasis.opendocument.text";
	private static final String ODS = "application/vnd.oasis.opendocument.spreadsheet";
	private static final String ODP = "application/vnd.oasis.opendocument.presentation";

	/**
	 * @param contentType
	 *            real content type
	 * @param strategy
	 *            selected strategy
	 * @return
	 */
	public static PDFConverter createPDFConverter(String contentType, String strategy) {

		// PDF - this is fixed
		if ("application/pdf".equals(contentType)) {
			return new NoConverter();
		}

		if ("Docx4J".equals(strategy) && Arrays.asList(new String[] { DOCX, PPTX, XLSX }).contains(contentType)) {
			return new Docx4JConverter();
		}

		if ("JOD".equals(strategy)
				&& Arrays.asList(new String[] { DOC, XLS, PPT, RTF, TXT, DOCX, PPTX, XLSX, ODT, ODS, ODP }).contains(contentType)) {
			return new JODConverter();
		}

		if ("XDocReport".equals(strategy) && Arrays.asList(new String[] { DOCX }).contains(contentType)) {
			return new XDocReportConverter();
		}

		if ("OfficeToPDF".equals(strategy)
				&& Arrays.asList(new String[] { DOC, XLS, PPT, RTF, DOCX, XLSX, PPTX }).contains(contentType)) {
			return new OfficeToPDFConverter();
		}

		if ("AsposeWords".equals(strategy) && Arrays.asList(new String[] { DOC, DOCX, ODT, RTF, TXT }).contains(contentType)) {
			return new AsposeWordsConverter();
		}

		if ("Dummy".equals(strategy)) {
			return new DummyPDFConverter();
		}

		return null;
	}
}
