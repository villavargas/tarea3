package mx.com.telcel.response.models;

public class AuthorizeErrorResponse
{

	private String error;
	private String error_description;

	/**
	 * @return the error
	 */
	public String getError()
	{
		return error;
	}

	/**
	 * @param error
	 *           the error to set
	 */
	public void setError(final String error)
	{
		this.error = error;
	}

	/**
	 * @return the error_description
	 */
	public String getError_description()
	{
		return error_description;
	}

	/**
	 * @param error_description
	 *           the error_description to set
	 */
	public void setError_description(final String error_description)
	{
		this.error_description = error_description;
	}

}
