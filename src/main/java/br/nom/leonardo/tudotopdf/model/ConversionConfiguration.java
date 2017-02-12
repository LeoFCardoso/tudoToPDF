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

	private Pdf2pdfocrConfiguration pdf2pdfocrConfig;
	private PdfboxConfiguration pdfboxConfig;
	private boolean protect;
	private int sizeHeader, sizeTop, sizeMiddle, sizeBottom, sizeFooter;
	private String textHeader, textTop, textMiddle, textBottom, textFooter;
	/**
	 * between 0 and 100
	 */
	private int transparency;
	private boolean watermark;
	private Color waterMarkColor;
	private String waterMarkType;

	public ConversionConfiguration(boolean watermark, boolean protect, String textHeader, String textTop,
			String textMiddle, String textBottom, String textFooter, int sizeHeader, int sizeTop, int sizeMiddle,
			int sizeBottom, int sizeFooter, int transparency, String waterkMarkType, Color waterkMarkColor,
			Pdf2pdfocrConfiguration pdf2pdfocrConfig, PdfboxConfiguration pdfboxConfig) {
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
		this.pdf2pdfocrConfig = pdf2pdfocrConfig;
		this.pdfboxConfig = pdfboxConfig;
	}

	public Pdf2pdfocrConfiguration getPdf2pdfocrConfig() {
		return pdf2pdfocrConfig;
	}

	public int getSizeBottom() {
		return sizeBottom;
	}

	public int getSizeFooter() {
		return sizeFooter;
	}

	public int getSizeHeader() {
		return sizeHeader;
	}

	public int getSizeMiddle() {
		return sizeMiddle;
	}

	public int getSizeTop() {
		return sizeTop;
	}

	public String getTextBottom() {
		return textBottom;
	}

	public String getTextFooter() {
		return textFooter;
	}

	public String getTextHeader() {
		return textHeader;
	}

	public String getTextMiddle() {
		return textMiddle;
	}

	public String getTextTop() {
		return textTop;
	}

	public int getTransparency() {
		return transparency;
	}

	public Color getWaterMarkColor() {
		return waterMarkColor;
	}

	public String getWaterMarkType() {
		return waterMarkType;
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
		result = prime * result + ((pdf2pdfocrConfig == null) ? 0 : pdf2pdfocrConfig.hashCode());
		result = prime * result + ((pdfboxConfig == null) ? 0 : pdfboxConfig.hashCode());
		return result;
	}

	public boolean isProtect() {
		return protect;
	}

	public boolean isWatermark() {
		return watermark;
	}

	public void setPdf2pdfocrConfig(Pdf2pdfocrConfiguration pdf2pdfocrConfig) {
		this.pdf2pdfocrConfig = pdf2pdfocrConfig;
	}

	public void setProtect(boolean protect) {
		this.protect = protect;
	}

	public void setSizeBottom(int sizeBottom) {
		this.sizeBottom = sizeBottom;
	}

	public void setSizeFooter(int sizeFooter) {
		this.sizeFooter = sizeFooter;
	}

	public void setSizeHeader(int sizeHeader) {
		this.sizeHeader = sizeHeader;
	}

	public void setSizeMiddle(int sizeMiddle) {
		this.sizeMiddle = sizeMiddle;
	}

	public void setSizeTop(int sizeTop) {
		this.sizeTop = sizeTop;
	}

	public void setTextBottom(String textBottom) {
		this.textBottom = textBottom;
	}

	public void setTextFooter(String textFooter) {
		this.textFooter = textFooter;
	}

	public void setTextHeader(String textHeader) {
		this.textHeader = textHeader;
	}

	public void setTextMiddle(String textMiddle) {
		this.textMiddle = textMiddle;
	}

	public void setTextTop(String textTop) {
		this.textTop = textTop;
	}

	public void setTransparency(int transparency) {
		this.transparency = transparency;
	}

	public void setWatermark(boolean watermark) {
		this.watermark = watermark;
	}

	public void setWaterMarkColor(Color waterMarkColor) {
		this.waterMarkColor = waterMarkColor;
	}

	public void setWaterMarkType(String waterMarkType) {
		this.waterMarkType = waterMarkType;
	}

	public PdfboxConfiguration getPdfboxConfig() {
		return pdfboxConfig;
	}

	public void setPdfboxConfig(PdfboxConfiguration pdfboxConfig) {
		this.pdfboxConfig = pdfboxConfig;
	}
		
}
