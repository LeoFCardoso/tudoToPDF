package br.nom.leonardo.tudotopdf.pdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Makes no conversion at all. Use with PDFs
 * @author leonardo
 *
 */
public class NoConverter implements PDFConverter {

	@Override
	public InputStream convertPDF(File theFile) throws PDFConverterException {
		try {
			return new FileInputStream(theFile);
		} catch (Exception e) {
			throw new PDFConverterException("Error in NoConverter", e);
		}
	}

}
