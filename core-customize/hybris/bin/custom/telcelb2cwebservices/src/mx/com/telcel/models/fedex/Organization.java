package mx.com.telcel.models.fedex;

public class Organization
{

	private String organizationName;
	private String tradingName;
	private String categoryName;

	/**
	 * @return the organizationName
	 */
	public String getOrganizationName()
	{
		return organizationName;
	}

	/**
	 * @param organizationName
	 *           the organizationName to set
	 */
	public void setOrganizationName(final String organizationName)
	{
		this.organizationName = organizationName;
	}

	/**
	 * @return the tradingName
	 */
	public String getTradingName()
	{
		return tradingName;
	}

	/**
	 * @param tradingName
	 *           the tradingName to set
	 */
	public void setTradingName(final String tradingName)
	{
		this.tradingName = tradingName;
	}

	/**
	 * @return the categoryName
	 */
	public String getCategoryName()
	{
		return categoryName;
	}

	/**
	 * @param categoryName
	 *           the categoryName to set
	 */
	public void setCategoryName(final String categoryName)
	{
		this.categoryName = categoryName;
	}

}
