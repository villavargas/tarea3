package mx.com.telcel.response.models;

import com.google.gson.annotations.SerializedName;


public class TokenNipResponse
{

	@SerializedName("request_id")
	private String requestId;
	@SerializedName("mfa_required")
	private String mfaRequired;
	@SerializedName("access_token")
	private String accessToken;
	@SerializedName("token_type")
	private String tokenType;
	@SerializedName("expires_in")
	private Integer expiresIn;
	@SerializedName("refresh_token")
	private String refreshToken;
	private String error;
	@SerializedName("error_description")
	private String errorDescription;
	private int code;
	private String description;
	private String uid;

	/**
	 * @return the requestId
	 */
	public String getRequestId()
	{
		return requestId;
	}

	/**
	 * @param requestId
	 *           the requestId to set
	 */
	public void setRequestId(final String requestId)
	{
		this.requestId = requestId;
	}

	/**
	 * @return the mfaRequired
	 */
	public String getMfaRequired()
	{
		return mfaRequired;
	}

	/**
	 * @param mfaRequired
	 *           the mfaRequired to set
	 */
	public void setMfaRequired(final String mfaRequired)
	{
		this.mfaRequired = mfaRequired;
	}

	/**
	 * @return the accessToken
	 */
	public String getAccessToken()
	{
		return accessToken;
	}

	/**
	 * @param accessToken
	 *           the accessToken to set
	 */
	public void setAccessToken(final String accessToken)
	{
		this.accessToken = accessToken;
	}

	/**
	 * @return the tokenType
	 */
	public String getTokenType()
	{
		return tokenType;
	}

	/**
	 * @param tokenType
	 *           the tokenType to set
	 */
	public void setTokenType(final String tokenType)
	{
		this.tokenType = tokenType;
	}

	/**
	 * @return the expiresIn
	 */
	public Integer getExpiresIn()
	{
		return expiresIn;
	}

	/**
	 * @param expiresIn
	 *           the expiresIn to set
	 */
	public void setExpiresIn(final Integer expiresIn)
	{
		this.expiresIn = expiresIn;
	}

	/**
	 * @return the refreshToken
	 */
	public String getRefreshToken()
	{
		return refreshToken;
	}

	/**
	 * @param refreshToken
	 *           the refreshToken to set
	 */
	public void setRefreshToken(final String refreshToken)
	{
		this.refreshToken = refreshToken;
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
	 * @return the errorDescription
	 */
	public String getErrorDescription()
	{
		return errorDescription;
	}

	/**
	 * @param errorDescription
	 *           the errorDescription to set
	 */
	public void setErrorDescription(final String errorDescription)
	{
		this.errorDescription = errorDescription;
	}


	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
}
