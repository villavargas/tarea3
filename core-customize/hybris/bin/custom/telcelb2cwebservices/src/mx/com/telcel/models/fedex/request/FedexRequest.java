package mx.com.telcel.models.fedex.request;

import java.util.List;

import mx.com.telcel.models.fedex.Party;
import mx.com.telcel.models.fedex.Service;


public class FedexRequest
{

	private Service service;
	private List<Party> party;

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

	/**
	 * @return the party
	 */
	public List<Party> getParty()
	{
		return party;
	}

	/**
	 * @param party
	 *           the party to set
	 */
	public void setParty(final List<Party> party)
	{
		this.party = party;
	}

}
