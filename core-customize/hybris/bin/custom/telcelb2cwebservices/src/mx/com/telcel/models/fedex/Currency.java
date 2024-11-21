package mx.com.telcel.models.fedex;

public class Currency
{

	private String currencyFrom;
	private String currencyTo;
	private String currencyAmount;

	/**
	 * @return the currencyFrom
	 */
	public String getCurrencyFrom()
	{
		return currencyFrom;
	}

	/**
	 * @param currencyFrom
	 *           the currencyFrom to set
	 */
	public void setCurrencyFrom(final String currencyFrom)
	{
		this.currencyFrom = currencyFrom;
	}

	/**
	 * @return the currencyTo
	 */
	public String getCurrencyTo()
	{
		return currencyTo;
	}

	/**
	 * @param currencyTo
	 *           the currencyTo to set
	 */
	public void setCurrencyTo(final String currencyTo)
	{
		this.currencyTo = currencyTo;
	}

	/**
	 * @return the currencyAmount
	 */
	public String getCurrencyAmount()
	{
		return currencyAmount;
	}

	/**
	 * @param currencyAmount
	 *           the currencyAmount to set
	 */
	public void setCurrencyAmount(final String currencyAmount)
	{
		this.currencyAmount = currencyAmount;
	}

}
