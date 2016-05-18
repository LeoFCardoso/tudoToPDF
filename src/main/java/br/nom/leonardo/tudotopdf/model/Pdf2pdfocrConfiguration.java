package br.nom.leonardo.tudotopdf.model;

import java.io.Serializable;

/**
 * This class encapsulates pdf2pdfocr conversion configuration
 * 
 * @author leonardo
 *
 */
public class Pdf2pdfocrConfiguration implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8745233584493736163L;

	private boolean flagT, flagA, flagF, flagD, flagP;
	private String flagGValue, flagDValue;
	public boolean isFlagT() {
		return flagT;
	}
	public void setFlagT(boolean flagT) {
		this.flagT = flagT;
	}
	public boolean isFlagA() {
		return flagA;
	}
	public void setFlagA(boolean flagA) {
		this.flagA = flagA;
	}
	public boolean isFlagF() {
		return flagF;
	}
	public void setFlagF(boolean flagF) {
		this.flagF = flagF;
	}
	public boolean isFlagD() {
		return flagD;
	}
	public void setFlagD(boolean flagD) {
		this.flagD = flagD;
	}
	public boolean isFlagP() {
		return flagP;
	}
	public void setFlagP(boolean flagP) {
		this.flagP = flagP;
	}
	public String getFlagGValue() {
		return flagGValue;
	}
	public void setFlagGValue(String flagGValue) {
		this.flagGValue = flagGValue;
	}
	public String getFlagDValue() {
		return flagDValue;
	}
	public void setFlagDValue(String flagDValue) {
		this.flagDValue = flagDValue;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (flagA ? 1231 : 1237);
		result = prime * result + (flagD ? 1231 : 1237);
		result = prime * result + ((flagDValue == null) ? 0 : flagDValue.hashCode());
		result = prime * result + (flagF ? 1231 : 1237);
		result = prime * result + ((flagGValue == null) ? 0 : flagGValue.hashCode());
		result = prime * result + (flagP ? 1231 : 1237);
		result = prime * result + (flagT ? 1231 : 1237);
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
		Pdf2pdfocrConfiguration other = (Pdf2pdfocrConfiguration) obj;
		if (flagA != other.flagA)
			return false;
		if (flagD != other.flagD)
			return false;
		if (flagDValue == null) {
			if (other.flagDValue != null)
				return false;
		} else if (!flagDValue.equals(other.flagDValue))
			return false;
		if (flagF != other.flagF)
			return false;
		if (flagGValue == null) {
			if (other.flagGValue != null)
				return false;
		} else if (!flagGValue.equals(other.flagGValue))
			return false;
		if (flagP != other.flagP)
			return false;
		if (flagT != other.flagT)
			return false;
		return true;
	}

}
