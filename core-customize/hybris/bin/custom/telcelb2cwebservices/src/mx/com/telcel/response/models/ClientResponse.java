package mx.com.telcel.response.models;

import mx.com.telcel.models.Client;
import mx.com.telcel.models.Error;
import mx.com.telcel.models.Parameter;


public class ClientResponse
{

	private Error error;
	private Client client;
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
	 * @return the client
	 */
	public Client getClient()
	{
		return client;
	}

	/**
	 * @param client
	 *           the client to set
	 */
	public void setClient(final Client client)
	{
		this.client = client;
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
