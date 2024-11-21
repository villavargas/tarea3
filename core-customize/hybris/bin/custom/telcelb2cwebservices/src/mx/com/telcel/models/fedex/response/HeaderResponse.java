package mx.com.telcel.models.fedex.response;

import java.util.Date;


public class HeaderResponse
{

	private Integer latency;
	private String messageUUID;
	private Date responseDate;
	private String sendBy;
	private String user;
	private String version;

	/**
	 * @return the latency
	 */
	public Integer getLatency()
	{
		return latency;
	}

	/**
	 * @param latency
	 *           the latency to set
	 */
	public void setLatency(final Integer latency)
	{
		this.latency = latency;
	}

	/**
	 * @return the messageUUID
	 */
	public String getMessageUUID()
	{
		return messageUUID;
	}

	/**
	 * @param messageUUID
	 *           the messageUUID to set
	 */
	public void setMessageUUID(final String messageUUID)
	{
		this.messageUUID = messageUUID;
	}

	/**
	 * @return the responseDate
	 */
	public Date getResponseDate()
	{
		return responseDate;
	}

	/**
	 * @param responseDate
	 *           the responseDate to set
	 */
	public void setResponseDate(final Date responseDate)
	{
		this.responseDate = responseDate;
	}

	/**
	 * @return the sendBy
	 */
	public String getSendBy()
	{
		return sendBy;
	}

	/**
	 * @param sendBy
	 *           the sendBy to set
	 */
	public void setSendBy(final String sendBy)
	{
		this.sendBy = sendBy;
	}

	/**
	 * @return the user
	 */
	public String getUser()
	{
		return user;
	}

	/**
	 * @param user
	 *           the user to set
	 */
	public void setUser(final String user)
	{
		this.user = user;
	}

	/**
	 * @return the version
	 */
	public String getVersion()
	{
		return version;
	}

	/**
	 * @param version
	 *           the version to set
	 */
	public void setVersion(final String version)
	{
		this.version = version;
	}

}
