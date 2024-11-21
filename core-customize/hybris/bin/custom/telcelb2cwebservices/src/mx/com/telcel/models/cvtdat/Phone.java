package mx.com.telcel.models.cvtdat;

public class Phone
{

	private String id;
	private String name;
	private String description;
	private String type;
	private String number;
	private Boolean isPrimary;
	private Boolean verified;

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
	 * @return the number
	 */
	public String getNumber()
	{
		return number;
	}

	/**
	 * @param number
	 *           the number to set
	 */
	public void setNumber(final String number)
	{
		this.number = number;
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

}
