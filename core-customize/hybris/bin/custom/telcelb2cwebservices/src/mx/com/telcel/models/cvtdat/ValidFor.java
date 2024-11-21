package mx.com.telcel.models.cvtdat;

import org.joda.time.DateTime;


public class ValidFor
{

	private DateTime startDateTime;
	private DateTime endDateTime;

	/**
	 * @return the startDateTime
	 */
	public DateTime getStartDateTime()
	{
		return startDateTime;
	}

	/**
	 * @param startDateTime
	 *           the startDateTime to set
	 */
	public void setStartDateTime(final DateTime startDateTime)
	{
		this.startDateTime = startDateTime;
	}

	/**
	 * @return the endDateTime
	 */
	public DateTime getEndDateTime()
	{
		return endDateTime;
	}

	/**
	 * @param endDateTime
	 *           the endDateTime to set
	 */
	public void setEndDateTime(final DateTime endDateTime)
	{
		this.endDateTime = endDateTime;
	}

}
