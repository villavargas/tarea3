package mx.com.telcel.models.msiconfiguration;

public class PaymentDetail
{

	private Integer month;
	private Double amount;
	private String label;
	private String content;

	/**
	 * @return the month
	 */
	public Integer getMonth()
	{
		return month;
	}

	/**
	 * @param month
	 *           the month to set
	 */
	public void setMonth(final Integer month)
	{
		this.month = month;
	}

	/**
	 * @return the amount
	 */
	public Double getAmount()
	{
		return amount;
	}

	/**
	 * @param amount
	 *           the amount to set
	 */
	public void setAmount(final Double amount)
	{
		this.amount = amount;
	}

	/**
	 * @return the label
	 */
	public String getLabel()
	{
		return label;
	}

	/**
	 * @param label
	 *           the label to set
	 */
	public void setLabel(final String label)
	{
		this.label = label;
	}

	/**
	 * @return the content
	 */
	public String getContent()
	{
		return content;
	}

	/**
	 * @param content
	 *           the content to set
	 */
	public void setContent(final String content)
	{
		this.content = content;
	}

}
