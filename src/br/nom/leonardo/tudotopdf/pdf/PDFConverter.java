package br.nom.leonardo.tudotopdf.pdf;

import java.io.File;

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
	 * @param md5UploadedFile 
	 * @return PDF file on disk
	 * @throws PDFConverterException
	 */
	public File convertPDF(File theFile, String md5UploadedFile) throws PDFConverterException;

	/**
	 * @return Unique CODE of the converter. It's a simple String
	 */
	public String getCode();
	
}
