package mx.com.telcel.models.individual;

import java.util.List;


public class CalendarPeriod
{

	private String day;
	private String status;
	private String comment;
	private String timeZone;
	private List<HourPeriod> hourPeriod;

	/**
	 * @return the day
	 */
	public String getDay()
	{
		return day;
	}

	/**
	 * @param day
	 *           the day to set
	 */
	public void setDay(final String day)
	{
		this.day = day;
	}

	/**
	 * @return the status
	 */
	public String getStatus()
	{
		return status;
	}

	/**
	 * @param status
	 *           the status to set
	 */
	public void setStatus(final String status)
	{
		this.status = status;
	}

	/**
	 * @return the comment
	 */
	public String getComment()
	{
		return comment;
	}

	/**
	 * @param comment
	 *           the comment to set
	 */
	public void setComment(final String comment)
	{
		this.comment = comment;
	}

	/**
	 * @return the timeZone
	 */
	public String getTimeZone()
	{
		return timeZone;
	}

	/**
	 * @param timeZone
	 *           the timeZone to set
	 */
	public void setTimeZone(final String timeZone)
	{
		this.timeZone = timeZone;
	}

	/**
	 * @return the hourPeriod
	 */
	public List<HourPeriod> getHourPeriod()
	{
		return hourPeriod;
	}

	/**
	 * @param hourPeriod
	 *           the hourPeriod to set
	 */
	public void setHourPeriod(final List<HourPeriod> hourPeriod)
	{
		this.hourPeriod = hourPeriod;
	}

}
