package mx.com.telcel.models.individual;

import java.util.List;


public class GenericFault
{

	private String messageUUID;
	private String responseDate;
	private String latency;
	private List<ErrorList> errorList;

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
	public String getResponseDate()
	{
		return responseDate;
	}

	/**
	 * @param responseDate
	 *           the responseDate to set
	 */
	public void setResponseDate(final String responseDate)
	{
		this.responseDate = responseDate;
	}

	/**
	 * @return the latency
	 */
	public String getLatency()
	{
		return latency;
	}

	/**
	 * @param latency
	 *           the latency to set
	 */
	public void setLatency(final String latency)
	{
		this.latency = latency;
	}

	/**
	 * @return the errorList
	 */
	public List<ErrorList> getErrorList()
	{
		return errorList;
	}

	/**
	 * @param errorList
	 *           the errorList to set
	 */
	public void setErrorList(final List<ErrorList> errorList)
	{
		this.errorList = errorList;
	}

}
