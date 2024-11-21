package mx.com.telcel.models.fedex;

public class TimePeriod
{

	private String endDateTime;
	private String startDateTime;
	private String unit;
	private String amount;

	/**
	 * @return the endDateTime
	 */
	public String getEndDateTime()
	{
		return endDateTime;
	}

	/**
	 * @param endDateTime
	 *           the endDateTime to set
	 */
	public void setEndDateTime(final String endDateTime)
	{
		this.endDateTime = endDateTime;
	}

	/**
	 * @return the startDateTime
	 */
	public String getStartDateTime()
	{
		return startDateTime;
	}

	/**
	 * @param startDateTime
	 *           the startDateTime to set
	 */
	public void setStartDateTime(final String startDateTime)
	{
		this.startDateTime = startDateTime;
	}

	/**
	 * @return the unit
	 */
	public String getUnit()
	{
		return unit;
	}

	/**
	 * @param unit
	 *           the unit to set
	 */
	public void setUnit(final String unit)
	{
		this.unit = unit;
	}

	/**
	 * @return the amount
	 */
	public String getAmount()
	{
		return amount;
	}

	/**
	 * @param amount
	 *           the amount to set
	 */
	public void setAmount(final String amount)
	{
		this.amount = amount;
	}

}
