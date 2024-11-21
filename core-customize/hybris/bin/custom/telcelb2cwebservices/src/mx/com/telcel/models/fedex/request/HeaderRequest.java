package mx.com.telcel.models.fedex.request;

public class HeaderRequest
{

	private String ipClient;
	private String ipServer;
	private String messageUUID;
	private String requestDate;
	private String sendBy;
	private String user;
	private String version;

	/**
	 * @return the ipClient
	 */
	public String getIpClient()
	{
		return ipClient;
	}

	/**
	 * @param ipClient
	 *           the ipClient to set
	 */
	public void setIpClient(final String ipClient)
	{
		this.ipClient = ipClient;
	}

	/**
	 * @return the ipServer
	 */
	public String getIpServer()
	{
		return ipServer;
	}

	/**
	 * @param ipServer
	 *           the ipServer to set
	 */
	public void setIpServer(final String ipServer)
	{
		this.ipServer = ipServer;
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
	 * @return the requestDate
	 */
	public String getRequestDate()
	{
		return requestDate;
	}

	/**
	 * @param requestDate
	 *           the requestDate to set
	 */
	public void setRequestDate(final String requestDate)
	{
		this.requestDate = requestDate;
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
