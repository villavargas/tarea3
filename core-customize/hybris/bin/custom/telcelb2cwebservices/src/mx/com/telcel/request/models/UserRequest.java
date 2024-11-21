/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.request.models;

import mx.com.telcel.models.Parameter;
import mx.com.telcel.models.User;

public class UserRequest
{

	private String source;
	private User user;
	private Parameter[] parameters;

	/**
	 * @return the source
	 */
	public String getSource()
	{
		return source;
	}

	/**
	 * @param source
	 *           the source to set
	 */
	public void setSource(final String source)
	{
		this.source = source;
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
