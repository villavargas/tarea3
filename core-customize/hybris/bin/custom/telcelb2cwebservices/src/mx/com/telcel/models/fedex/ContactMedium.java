package mx.com.telcel.models.fedex;

public class ContactMedium
{

	private String preferred;
	private String type;
	private TimePeriod validFor;
	private MediumCharacteristic contactMediumCharacteristic;

	/**
	 * @return the preferred
	 */
	public String getPreferred()
	{
		return preferred;
	}

	/**
	 * @param preferred
	 *           the preferred to set
	 */
	public void setPreferred(final String preferred)
	{
		this.preferred = preferred;
	}

	/**
	 * @return the type
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * @param type
	 *           the type to set
	 */
	public void setType(final String type)
	{
		this.type = type;
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

	/**
	 * @return the contactMediumCharacteristic
	 */
	public MediumCharacteristic getContactMediumCharacteristic()
	{
		return contactMediumCharacteristic;
	}

	/**
	 * @param contactMediumCharacteristic
	 *           the contactMediumCharacteristic to set
	 */
	public void setContactMediumCharacteristic(final MediumCharacteristic contactMediumCharacteristic)
	{
		this.contactMediumCharacteristic = contactMediumCharacteristic;
	}

}
