package mx.com.telcel.models.cvtdat;

public class UserInfoCVTDATResponse
{

	private Integer code;
	private UserInfo userInfo;
	private GenericFault error;

	/**
	 * @return the code
	 */
	public Integer getCode()
	{
		return code;
	}

	/**
	 * @param code
	 *           the code to set
	 */
	public void setCode(final Integer code)
	{
		this.code = code;
	}

	/**
	 * @return the userInfo
	 */
	public UserInfo getUserInfo()
	{
		return userInfo;
	}

	/**
	 * @param userInfo
	 *           the userInfo to set
	 */
	public void setUserInfo(final UserInfo userInfo)
	{
		this.userInfo = userInfo;
	}

	/**
	 * @return the error
	 */
	public GenericFault getError()
	{
		return error;
	}

	/**
	 * @param error
	 *           the error to set
	 */
	public void setError(final GenericFault error)
	{
		this.error = error;
	}

}
