package mx.com.telcel.models.fedex;

public class Amount
{

	private String type;
	private String description;
	private Quantity quote;
	private String percentage;

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
	 * @return the quote
	 */
	public Quantity getQuote()
	{
		return quote;
	}

	/**
	 * @param quote
	 *           the quote to set
	 */
	public void setQuote(final Quantity quote)
	{
		this.quote = quote;
	}

	/**
	 * @return the percentage
	 */
	public String getPercentage()
	{
		return percentage;
	}

	/**
	 * @param percentage
	 *           the percentage to set
	 */
	public void setPercentage(final String percentage)
	{
		this.percentage = percentage;
	}

}
