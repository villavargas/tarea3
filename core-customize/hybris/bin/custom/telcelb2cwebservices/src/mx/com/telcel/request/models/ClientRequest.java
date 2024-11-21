/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.request.models;

import mx.com.telcel.models.Parameter;


public class ClientRequest
{

	private String source;
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
