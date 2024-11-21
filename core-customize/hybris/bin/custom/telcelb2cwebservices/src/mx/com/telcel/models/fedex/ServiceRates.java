package mx.com.telcel.models.fedex;

import java.util.List;


public class ServiceRates
{

	private String serviceType;
	private String packagingType;
	private String deliveryStation;
	private String deliveryDate;
	private List<Characteristic> characteristics;
	private List<Rate> rates;

	/**
	 * @return the serviceType
	 */
	public String getServiceType()
	{
		return serviceType;
	}

	/**
	 * @param serviceType
	 *           the serviceType to set
	 */
	public void setServiceType(final String serviceType)
	{
		this.serviceType = serviceType;
	}

	/**
	 * @return the packagingType
	 */
	public String getPackagingType()
	{
		return packagingType;
	}

	/**
	 * @param packagingType
	 *           the packagingType to set
	 */
	public void setPackagingType(final String packagingType)
	{
		this.packagingType = packagingType;
	}

	/**
	 * @return the deliveryStation
	 */
	public String getDeliveryStation()
	{
		return deliveryStation;
	}

	/**
	 * @param deliveryStation
	 *           the deliveryStation to set
	 */
	public void setDeliveryStation(final String deliveryStation)
	{
		this.deliveryStation = deliveryStation;
	}

	/**
	 * @return the deliveryDate
	 */
	public String getDeliveryDate()
	{
		return deliveryDate;
	}

	/**
	 * @param deliveryDate
	 *           the deliveryDate to set
	 */
	public void setDeliveryDate(final String deliveryDate)
	{
		this.deliveryDate = deliveryDate;
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

	/**
	 * @return the rates
	 */
	public List<Rate> getRates()
	{
		return rates;
	}

	/**
	 * @param rates
	 *           the rates to set
	 */
	public void setRates(final List<Rate> rates)
	{
		this.rates = rates;
	}

}
