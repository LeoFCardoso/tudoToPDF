package br.nom.leonardo.tudotopdf.model;

import java.awt.Color;
import java.io.Serializable;

/**
 * This class encapsulates conversion configuration
 * 
 * @author leonardo
 *
 */
public class ConversionConfiguration implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -352432106072559106L;

	private boolean watermark;
	private boolean protect;
	private String textHeader, textTop, textMiddle, textBottom, textFooter;
	private int sizeHeader, sizeTop, sizeMiddle, sizeBottom, sizeFooter;
	private String waterMarkType;
	private Color waterMarkColor;
	/**
	 * between 0 and 100
	 */
	private int transparency;

	public ConversionConfiguration(boolean watermark, boolean protect, String textHeader, String textTop,
			String textMiddle, String textBottom, String textFooter, int sizeHeader, int sizeTop, int sizeMiddle,
			int sizeBottom, int sizeFooter, int transparency, String waterkMarkType, Color waterkMarkColor) {
		super();
		this.watermark = watermark;
		this.protect = protect;
		this.textHeader = textHeader;
		this.textTop = textTop;
		this.textMiddle = textMiddle;
		this.textBottom = textBottom;
		this.textFooter = textFooter;
		this.sizeHeader = sizeHeader;
		this.sizeTop = sizeTop;
		this.sizeMiddle = sizeMiddle;
		this.sizeBottom = sizeBottom;
		this.sizeFooter = sizeFooter;
		this.transparency = transparency;
		this.waterMarkType = waterkMarkType;
		this.waterMarkColor = waterkMarkColor;
	}

	public boolean isWatermark() {
		return watermark;
	}

	public void setWatermark(boolean watermark) {
		this.watermark = watermark;
	}

	public boolean isProtect() {
		return protect;
	}

	public void setProtect(boolean protect) {
		this.protect = protect;
	}

	public String getTextHeader() {
		return textHeader;
	}

	public void setTextHeader(String textHeader) {
		this.textHeader = textHeader;
	}

	public String getTextTop() {
		return textTop;
	}

	public void setTextTop(String textTop) {
		this.textTop = textTop;
	}

	public String getTextMiddle() {
		return textMiddle;
	}

	public void setTextMiddle(String textMiddle) {
		this.textMiddle = textMiddle;
	}

	public String getTextBottom() {
		return textBottom;
	}

	public void setTextBottom(String textBottom) {
		this.textBottom = textBottom;
	}

	public String getTextFooter() {
		return textFooter;
	}

	public void setTextFooter(String textFooter) {
		this.textFooter = textFooter;
	}

	public int getSizeHeader() {
		return sizeHeader;
	}

	public void setSizeHeader(int sizeHeader) {
		this.sizeHeader = sizeHeader;
	}

	public int getSizeTop() {
		return sizeTop;
	}

	public void setSizeTop(int sizeTop) {
		this.sizeTop = sizeTop;
	}

	public int getSizeMiddle() {
		return sizeMiddle;
	}

	public void setSizeMiddle(int sizeMiddle) {
		this.sizeMiddle = sizeMiddle;
	}

	public int getSizeBottom() {
		return sizeBottom;
	}

	public void setSizeBottom(int sizeBottom) {
		this.sizeBottom = sizeBottom;
	}

	public int getSizeFooter() {
		return sizeFooter;
	}

	public void setSizeFooter(int sizeFooter) {
		this.sizeFooter = sizeFooter;
	}

	public int getTransparency() {
		return transparency;
	}

	public void setTransparency(int transparency) {
		this.transparency = transparency;
	}

	public String getWaterMarkType() {
		return waterMarkType;
	}

	public void setWaterMarkType(String waterMarkType) {
		this.waterMarkType = waterMarkType;
	}

	public Color getWaterMarkColor() {
		return waterMarkColor;
	}

	public void setWaterMarkColor(Color waterMarkColor) {
		this.waterMarkColor = waterMarkColor;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (protect ? 1231 : 1237);
		result = prime * result + sizeBottom;
		result = prime * result + sizeFooter;
		result = prime * result + sizeHeader;
		result = prime * result + sizeMiddle;
		result = prime * result + sizeTop;
		result = prime * result + ((textBottom == null) ? 0 : textBottom.hashCode());
		result = prime * result + ((textFooter == null) ? 0 : textFooter.hashCode());
		result = prime * result + ((textHeader == null) ? 0 : textHeader.hashCode());
		result = prime * result + ((textMiddle == null) ? 0 : textMiddle.hashCode());
		result = prime * result + ((textTop == null) ? 0 : textTop.hashCode());
		result = prime * result + transparency;
		result = prime * result + ((waterMarkColor == null) ? 0 : waterMarkColor.hashCode());
		result = prime * result + ((waterMarkType == null) ? 0 : waterMarkType.hashCode());
		result = prime * result + (watermark ? 1231 : 1237);
		return result;
	}
	
}
