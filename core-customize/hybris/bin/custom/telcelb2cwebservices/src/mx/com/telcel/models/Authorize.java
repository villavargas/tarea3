package mx.com.telcel.models;

public class Authorize
{

	//Mandatory Parameters
	private String clienteId;
	private String responseType;
	private String redirectUri;
	private String scope;
	private String state;
	private String nonce;
	private String arcValues;
	//Optional Parameters
	private String display;
	private String prompt;
	private String maxAge;
	private String uiLocales;
	private Boolean claimsLocales;
	private String idTokenHint;
	private String loginHint;
	private String dtbs;

	/**
	 * @return the clienteId
	 */
	public String getClienteId()
	{
		return clienteId;
	}

	/**
	 * @param clienteId
	 *           the clienteId to set
	 */
	public void setClienteId(final String clienteId)
	{
		this.clienteId = clienteId;
	}

	/**
	 * @return the responseType
	 */
	public String getResponseType()
	{
		return responseType;
	}

	/**
	 * @param responseType
	 *           the responseType to set
	 */
	public void setResponseType(final String responseType)
	{
		this.responseType = responseType;
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
	 * @return the scope
	 */
	public String getScope()
	{
		return scope;
	}

	/**
	 * @param scope
	 *           the scope to set
	 */
	public void setScope(final String scope)
	{
		this.scope = scope;
	}

	/**
	 * @return the state
	 */
	public String getState()
	{
		return state;
	}

	/**
	 * @param state
	 *           the state to set
	 */
	public void setState(final String state)
	{
		this.state = state;
	}

	/**
	 * @return the nonce
	 */
	public String getNonce()
	{
		return nonce;
	}

	/**
	 * @param nonce
	 *           the nonce to set
	 */
	public void setNonce(final String nonce)
	{
		this.nonce = nonce;
	}

	/**
	 * @return the arcValues
	 */
	public String getArcValues()
	{
		return arcValues;
	}

	/**
	 * @param arcValues
	 *           the arcValues to set
	 */
	public void setArcValues(final String arcValues)
	{
		this.arcValues = arcValues;
	}

	/**
	 * @return the display
	 */
	public String getDisplay()
	{
		return display;
	}

	/**
	 * @param display
	 *           the display to set
	 */
	public void setDisplay(final String display)
	{
		this.display = display;
	}

	/**
	 * @return the prompt
	 */
	public String getPrompt()
	{
		return prompt;
	}

	/**
	 * @param prompt
	 *           the prompt to set
	 */
	public void setPrompt(final String prompt)
	{
		this.prompt = prompt;
	}

	/**
	 * @return the maxAge
	 */
	public String getMaxAge()
	{
		return maxAge;
	}

	/**
	 * @param maxAge
	 *           the maxAge to set
	 */
	public void setMaxAge(final String maxAge)
	{
		this.maxAge = maxAge;
	}

	/**
	 * @return the uiLocales
	 */
	public String getUiLocales()
	{
		return uiLocales;
	}

	/**
	 * @param uiLocales
	 *           the uiLocales to set
	 */
	public void setUiLocales(final String uiLocales)
	{
		this.uiLocales = uiLocales;
	}

	/**
	 * @return the claimsLocales
	 */
	public Boolean getClaimsLocales()
	{
		return claimsLocales;
	}

	/**
	 * @param claimsLocales
	 *           the claimsLocales to set
	 */
	public void setClaimsLocales(final Boolean claimsLocales)
	{
		this.claimsLocales = claimsLocales;
	}

	/**
	 * @return the idTokenHint
	 */
	public String getIdTokenHint()
	{
		return idTokenHint;
	}

	/**
	 * @param idTokenHint
	 *           the idTokenHint to set
	 */
	public void setIdTokenHint(final String idTokenHint)
	{
		this.idTokenHint = idTokenHint;
	}

	/**
	 * @return the loginHint
	 */
	public String getLoginHint()
	{
		return loginHint;
	}

	/**
	 * @param loginHint
	 *           the loginHint to set
	 */
	public void setLoginHint(final String loginHint)
	{
		this.loginHint = loginHint;
	}

	/**
	 * @return the dtbs
	 */
	public String getDtbs()
	{
		return dtbs;
	}

	/**
	 * @param dtbs
	 *           the dtbs to set
	 */
	public void setDtbs(final String dtbs)
	{
		this.dtbs = dtbs;
	}

}
