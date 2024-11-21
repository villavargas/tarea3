package mx.com.telcel.utilities;

public class HttpGenericResponse
{

	private String response;
	private int code;

	/**
	 * @return the response
	 */
	public String getResponse()
	{
		return response;
	}

	/**
	 * @param response
	 *           the response to set
	 */
	public void setResponse(final String response)
	{
		this.response = response;
	}

	/**
	 * @return the code
	 */
	public int getCode()
	{
		return code;
	}

	/**
	 * @param code
	 *           the code to set
	 */
	public void setCode(final int code)
	{
		this.code = code;
	}

}
