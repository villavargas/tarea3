package mx.com.telcel.models.individual;

public class ContactMedium
{

	private String mediumType;
	private Boolean preferred;
	private MediumCharacteristic characteristic;
	private GeographicAddressRef address;
	private TimePeriod validFor;

	/**
	 * @return the mediumType
	 */
	public String getMediumType()
	{
		return mediumType;
	}

	/**
	 * @param mediumType
	 *           the mediumType to set
	 */
	public void setMediumType(final String mediumType)
	{
		this.mediumType = mediumType;
	}

	/**
	 * @return the preferred
	 */
	public Boolean getPreferred()
	{
		return preferred;
	}

	/**
	 * @param preferred
	 *           the preferred to set
	 */
	public void setPreferred(final Boolean preferred)
	{
		this.preferred = preferred;
	}

	/**
	 * @return the characteristic
	 */
	public MediumCharacteristic getCharacteristic()
	{
		return characteristic;
	}

	/**
	 * @param characteristic
	 *           the characteristic to set
	 */
	public void setCharacteristic(final MediumCharacteristic characteristic)
	{
		this.characteristic = characteristic;
	}

	/**
	 * @return the address
	 */
	public GeographicAddressRef getAddress()
	{
		return address;
	}

	/**
	 * @param address
	 *           the address to set
	 */
	public void setAddress(final GeographicAddressRef address)
	{
		this.address = address;
	}

	/**
	 * @return the validFor
	 */
	public TimePeriod getValidFor()
	{
		return validFor;
	}

	/**
	 * @param validFor
	 *           the validFor to set
	 */
	public void setValidFor(final TimePeriod validFor)
	{
		this.validFor = validFor;
	}

}
