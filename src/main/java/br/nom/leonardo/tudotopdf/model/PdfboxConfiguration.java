package br.nom.leonardo.tudotopdf.model;

import java.io.Serializable;

/**
 * This class encapsulates pdfbox conversion configuration
 * @author leonardo
 */
public class PdfboxConfiguration implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3943894248099975617L;
	
	private String pageSize, widthCm, heightCm;

	public String getPageSize() {
		return pageSize;
	}

	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}

	public String getWidthCm() {
		return widthCm;
	}

	public void setWidthCm(String widthCm) {
		this.widthCm = widthCm;
	}

	public String getHeightCm() {
		return heightCm;
	}

	public void setHeightCm(String heightCm) {
		this.heightCm = heightCm;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((heightCm == null) ? 0 : heightCm.hashCode());
		result = prime * result + ((pageSize == null) ? 0 : pageSize.hashCode());
		result = prime * result + ((widthCm == null) ? 0 : widthCm.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PdfboxConfiguration other = (PdfboxConfiguration) obj;
		if (heightCm == null) {
			if (other.heightCm != null)
				return false;
		} else if (!heightCm.equals(other.heightCm))
			return false;
		if (pageSize == null) {
			if (other.pageSize != null)
				return false;
		} else if (!pageSize.equals(other.pageSize))
			return false;
		if (widthCm == null) {
			if (other.widthCm != null)
				return false;
		} else if (!widthCm.equals(other.widthCm))
			return false;
		return true;
	}

}
