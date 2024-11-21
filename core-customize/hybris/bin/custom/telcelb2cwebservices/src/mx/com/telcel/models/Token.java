package mx.com.telcel.models;

public class Token
{

	private String grantType;
	private String code;
	private String redirectUri;
	private String clientId;
	private String clientCredential;

	/**
	 * @return the grantType
	 */
	public String getGrantType()
	{
		return grantType;
	}

	/**
	 * @param grantType
	 *           the grantType to set
	 */
	public void setGrantType(final String grantType)
	{
		this.grantType = grantType;
	}

	/**
	 * @return the code
	 */
	public String getCode()
	{
		return code;
	}

	/**
	 * @param code
	 *           the code to set
	 */
	public void setCode(final String code)
	{
		this.code = code;
	}

	/**
	 * @return the redirectUri
	 */
	public String getRedirectUri()
	{
		return redirectUri;
	}

	/**
	 * @param redirectUri
	 *           the redirectUri to set
	 */
	public void setRedirectUri(final String redirectUri)
	{
		this.redirectUri = redirectUri;
	}

	/**
	 * @return the clientId
	 */
	public String getClientId()
	{
		return clientId;
	}

	/**
	 * @param clientId
	 *           the clientId to set
	 */
	public void setClientId(final String clientId)
	{
		this.clientId = clientId;
	}

	/**
	 * @return the clientCredential
	 */
	public String getClientCredential()
	{
		return clientCredential;
	}

	/**
	 * @param clientCredential
	 *           the clientCredential to set
	 */
	public void setClientCredential(final String clientCredential)
	{
		this.clientCredential = clientCredential;
	}

}
