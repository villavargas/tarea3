package mx.com.telcel.models.cvtdat;

public class OrganizationRef
{

	private String href;
	private String id;
	private String name;
	private String organizationType;

	/**
	 * @return the href
	 */
	public String getHref()
	{
		return href;
	}

	/**
	 * @param href
	 *           the href to set
	 */
	public void setHref(final String href)
	{
		this.href = href;
	}

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
	 * @return the organizationType
	 */
	public String getOrganizationType()
	{
		return organizationType;
	}

	/**
	 * @param organizationType
	 *           the organizationType to set
	 */
	public void setOrganizationType(final String organizationType)
	{
		this.organizationType = organizationType;
	}

}
