package mx.com.telcel.models.fedex;

import java.util.List;


public class Label
{

	private String LabelFormatType;
	private String ImageType;
	private String LabelStockType;
	private String LabelPrintingOrientation;
	private String ShippingDocumentDisposition;
	private String Resolution;
	private String CopiesToPrint;
	private List<Characteristic> characteristics;

	/**
	 * @return the labelFormatType
	 */
	public String getLabelFormatType()
	{
		return LabelFormatType;
	}

	/**
	 * @param labelFormatType
	 *           the labelFormatType to set
	 */
	public void setLabelFormatType(final String labelFormatType)
	{
		LabelFormatType = labelFormatType;
	}

	/**
	 * @return the imageType
	 */
	public String getImageType()
	{
		return ImageType;
	}

	/**
	 * @param imageType
	 *           the imageType to set
	 */
	public void setImageType(final String imageType)
	{
		ImageType = imageType;
	}

	/**
	 * @return the labelStockType
	 */
	public String getLabelStockType()
	{
		return LabelStockType;
	}

	/**
	 * @param labelStockType
	 *           the labelStockType to set
	 */
	public void setLabelStockType(final String labelStockType)
	{
		LabelStockType = labelStockType;
	}

	/**
	 * @return the labelPrintingOrientation
	 */
	public String getLabelPrintingOrientation()
	{
		return LabelPrintingOrientation;
	}

	/**
	 * @param labelPrintingOrientation
	 *           the labelPrintingOrientation to set
	 */
	public void setLabelPrintingOrientation(final String labelPrintingOrientation)
	{
		LabelPrintingOrientation = labelPrintingOrientation;
	}

	/**
	 * @return the shippingDocumentDisposition
	 */
	public String getShippingDocumentDisposition()
	{
		return ShippingDocumentDisposition;
	}

	/**
	 * @param shippingDocumentDisposition
	 *           the shippingDocumentDisposition to set
	 */
	public void setShippingDocumentDisposition(final String shippingDocumentDisposition)
	{
		ShippingDocumentDisposition = shippingDocumentDisposition;
	}

	/**
	 * @return the resolution
	 */
	public String getResolution()
	{
		return Resolution;
	}

	/**
	 * @param resolution
	 *           the resolution to set
	 */
	public void setResolution(final String resolution)
	{
		Resolution = resolution;
	}

	/**
	 * @return the copiesToPrint
	 */
	public String getCopiesToPrint()
	{
		return CopiesToPrint;
	}

	/**
	 * @param copiesToPrint
	 *           the copiesToPrint to set
	 */
	public void setCopiesToPrint(final String copiesToPrint)
	{
		CopiesToPrint = copiesToPrint;
	}

	/**
	 * @return the characteristics
	 */
	public List<Characteristic> getCharacteristics()
	{
		return characteristics;
	}

	/**
	 * @param characteristics
	 *           the characteristics to set
	 */
	public void setCharacteristics(final List<Characteristic> characteristics)
	{
		this.characteristics = characteristics;
	}

}
