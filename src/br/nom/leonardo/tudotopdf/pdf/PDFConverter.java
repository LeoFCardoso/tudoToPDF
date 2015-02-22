package br.nom.leonardo.tudotopdf.pdf;

import java.io.File;
import java.io.InputStream;

/**
 * Generic PDFConverter interface
 * 
 * @author leonardo
 *
 */
public interface PDFConverter {

	/**
	 * Convert file to PDF
	 * @param theFile
	 * @return PDF input stream
	 * @throws PDFConverterException
	 */
	public InputStream convertPDF(File theFile) throws PDFConverterException;

}
