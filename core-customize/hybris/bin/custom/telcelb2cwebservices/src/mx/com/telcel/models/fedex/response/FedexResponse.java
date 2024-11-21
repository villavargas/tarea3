package mx.com.telcel.models.fedex.response;

import mx.com.telcel.models.fedex.Service;


public class FedexResponse
{

	private Service service;

	/**
	 * @return the service
	 */
	public Service getService()
	{
		return service;
	}

	/**
	 * @param service
	 *           the service to set
	 */
	public void setService(final Service service)
	{
		this.service = service;
	}

}
