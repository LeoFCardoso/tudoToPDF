package br.nom.leonardo.tudotopdf.pdf;

/**
 * Simple abstract impl of PDFConverter
 * 
 * @author leonardo
 *
 */
public abstract class AbstractPDFConverter implements PDFConverter {

	@Override
	public String getOutputFileName(String md5UploadedFile) {
		return md5UploadedFile + "-" + this.getCode() + "-" + this.getOutFileNameSuffix() + ".pdf";
	}

	/**
	 * @return suffix of output file name
	 */
	String getOutFileNameSuffix() {
		return "no_suffix"; //Must be overriden in subclasses that uses complement 
	}

}
