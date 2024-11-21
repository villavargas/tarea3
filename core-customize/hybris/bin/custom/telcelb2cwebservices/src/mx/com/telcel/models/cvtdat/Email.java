package mx.com.telcel.models.cvtdat;

public class Email
{

	private String id;
	private String name;
	private String description;
	private String type;
	private String address;
	private Boolean isPrimary;
	private Boolean verified;
	private String deliveryDate;
	private String date;
	private String receives;

	/**
	 * @return the id
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * @param id
	 *           the id to set
	 */
	public void setId(final String id)
	{
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name
	 *           the name to set
	 */
	public void setName(final String name)
	{
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * @param description
	 *           the description to set
	 */
	public void setDescription(final String description)
	{
		this.description = description;
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
	 * @return the address
	 */
	public String getAddress()
	{
		return address;
	}

	/**
	 * @param address
	 *           the address to set
	 */
	public void setAddress(final String address)
	{
		this.address = address;
	}

	/**
	 * @return the isPrimary
	 */
	public Boolean getIsPrimary()
	{
		return isPrimary;
	}

	/**
	 * @param isPrimary
	 *           the isPrimary to set
	 */
	public void setIsPrimary(final Boolean isPrimary)
	{
		this.isPrimary = isPrimary;
	}

	/**
	 * @return the verified
	 */
	public Boolean getVerified()
	{
		return verified;
	}

	/**
	 * @param verified
	 *           the verified to set
	 */
	public void setVerified(final Boolean verified)
	{
		this.verified = verified;
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
	 * @return the date
	 */
	public String getDate()
	{
		return date;
	}

	/**
	 * @param date
	 *           the date to set
	 */
	public void setDate(final String date)
	{
		this.date = date;
	}

	/**
	 * @return the receives
	 */
	public String getReceives()
	{
		return receives;
	}

	/**
	 * @param receives
	 *           the receives to set
	 */
	public void setReceives(final String receives)
	{
		this.receives = receives;
	}

}
