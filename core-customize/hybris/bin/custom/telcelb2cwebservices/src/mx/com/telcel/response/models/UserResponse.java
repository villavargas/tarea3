package mx.com.telcel.response.models;

import mx.com.telcel.models.Error;
import mx.com.telcel.models.Parameter;
import mx.com.telcel.models.User;


public class UserResponse
{

	private Error error;
	private User user;
	private Parameter[] parameters;

	/**
	 * @return the error
	 */
	public Error getError()
	{
		return error;
	}

	/**
	 * @param error
	 *           the error to set
	 */
	public void setError(final Error error)
	{
		this.error = error;
	}

	/**
	 * @return the user
	 */
	public User getUser()
	{
		return user;
	}

	/**
	 * @param user
	 *           the user to set
	 */
	public void setUser(final User user)
	{
		this.user = user;
	}

	/**
	 * @return the parameters
	 */
	public Parameter[] getParameters()
	{
		return parameters;
	}

	/**
	 * @param parameters
	 *           the parameters to set
	 */
	public void setParameters(final Parameter[] parameters)
	{
		this.parameters = parameters;
	}

}
