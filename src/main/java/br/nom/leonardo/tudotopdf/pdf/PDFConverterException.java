package br.nom.leonardo.tudotopdf.pdf;

/**
 * Generic exception for PDF Converters
 * @author leonardo
 *
 */
public class PDFConverterException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7910577632194739186L;

	public PDFConverterException() {
		super();
	}

	public PDFConverterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public PDFConverterException(String message, Throwable cause) {
		super(message, cause);
	}

	public PDFConverterException(String message) {
		super(message);
	}

	public PDFConverterException(Throwable cause) {
		super(cause);
	}

	
	
}
