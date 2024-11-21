package mx.com.telcel.models.fedex;

public class Role
{

	private String id;
	private String name;
	private String partnershipHref;
	private String partnershipId;
	private String partnershipName;

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
	 * @return the partnershipHref
	 */
	public String getPartnershipHref()
	{
		return partnershipHref;
	}

	/**
	 * @param partnershipHref
	 *           the partnershipHref to set
	 */
	public void setPartnershipHref(final String partnershipHref)
	{
		this.partnershipHref = partnershipHref;
	}

	/**
	 * @return the partnershipId
	 */
	public String getPartnershipId()
	{
		return partnershipId;
	}

	/**
	 * @param partnershipId
	 *           the partnershipId to set
	 */
	public void setPartnershipId(final String partnershipId)
	{
		this.partnershipId = partnershipId;
	}

	/**
	 * @return the partnershipName
	 */
	public String getPartnershipName()
	{
		return partnershipName;
	}

	/**
	 * @param partnershipName
	 *           the partnershipName to set
	 */
	public void setPartnershipName(final String partnershipName)
	{
		this.partnershipName = partnershipName;
	}

}
