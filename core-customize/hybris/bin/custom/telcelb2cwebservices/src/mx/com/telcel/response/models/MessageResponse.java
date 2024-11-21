package mx.com.telcel.response.models;

public class MessageResponse
{

	private String responseCode;
	private String responseMessage;

	/**
	 * @return the responseCode
	 */
	public String getResponseCode()
	{
		return responseCode;
	}

	/**
	 * @param responseCode
	 *           the responseCode to set
	 */
	public void setResponseCode(final String responseCode)
	{
		this.responseCode = responseCode;
	}

	/**
	 * @return the responseMessage
	 */
	public String getResponseMessage()
	{
		return responseMessage;
	}

	/**
	 * @param responseMessage
	 *           the responseMessage to set
	 */
	public void setResponseMessage(final String responseMessage)
	{
		this.responseMessage = responseMessage;
	}

}
