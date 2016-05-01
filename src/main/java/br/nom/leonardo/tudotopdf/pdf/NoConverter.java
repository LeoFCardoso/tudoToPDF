package br.nom.leonardo.tudotopdf.pdf;

import java.io.File;

/**
 * Makes no conversion at all. Use with PDFs
 * @author leonardo
 *
 */
public class NoConverter implements PDFConverter {

	@Override
	public String getCode() {
		return "NoConverter";
	}
	
	@Override
	public File convertPDF(File theFile, String md5UploadedFile) throws PDFConverterException {
		try {
			return theFile;
		} catch (Exception e) {
			throw new PDFConverterException("Error in NoConverter", e);
		}
	}

}
