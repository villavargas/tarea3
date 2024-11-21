package mx.com.telcel.response.models;

public class TokenResponse
{

	private String access_token;
	private String error;
	private String error_description;
	private String expires_in;
	private String id_token;
	private String refresh_token;
	private String token_type;

	/**
	 * @return the access_token
	 */
	public String getAccess_token()
	{
		return access_token;
	}

	/**
	 * @param access_token
	 *           the access_token to set
	 */
	public void setAccess_token(final String access_token)
	{
		this.access_token = access_token;
	}

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

	/**
	 * @return the expires_in
	 */
	public String getExpires_in()
	{
		return expires_in;
	}

	/**
	 * @param expires_in
	 *           the expires_in to set
	 */
	public void setExpires_in(final String expires_in)
	{
		this.expires_in = expires_in;
	}

	/**
	 * @return the id_token
	 */
	public String getId_token()
	{
		return id_token;
	}

	/**
	 * @param id_token
	 *           the id_token to set
	 */
	public void setId_token(final String id_token)
	{
		this.id_token = id_token;
	}

	/**
	 * @return the refresh_token
	 */
	public String getRefresh_token()
	{
		return refresh_token;
	}

	/**
	 * @param refresh_token
	 *           the refresh_token to set
	 */
	public void setRefresh_token(final String refresh_token)
	{
		this.refresh_token = refresh_token;
	}

	/**
	 * @return the token_type
	 */
	public String getToken_type()
	{
		return token_type;
	}

	/**
	 * @param token_type
	 *           the token_type to set
	 */
	public void setToken_type(final String token_type)
	{
		this.token_type = token_type;
	}

}
