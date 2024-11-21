package mx.com.telcel.models.cvtdat;

import java.util.List;


public class RelatedParty
{

	private String id;
	private String identifierType;
	private String href;
	private String name;
	private String role;
	private String type;
	private List<Characteristic> characteristic;
	private Individual individual;

	/**
	 * @return the id
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * @param id
	 *           the id to set
	 */
	public void setId(final String id)
	{
		this.id = id;
	}

	/**
	 * @return the identifierType
	 */
	public String getIdentifierType()
	{
		return identifierType;
	}

	/**
	 * @param identifierType
	 *           the identifierType to set
	 */
	public void setIdentifierType(final String identifierType)
	{
		this.identifierType = identifierType;
	}

	/**
	 * @return the href
	 */
	public String getHref()
	{
		return href;
	}

	/**
	 * @param href
	 *           the href to set
	 */
	public void setHref(final String href)
	{
		this.href = href;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name
	 *           the name to set
	 */
	public void setName(final String name)
	{
		this.name = name;
	}

	/**
	 * @return the role
	 */
	public String getRole()
	{
		return role;
	}

	/**
	 * @param role
	 *           the role to set
	 */
	public void setRole(final String role)
	{
		this.role = role;
	}

	/**
	 * @return the type
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * @param type
	 *           the type to set
	 */
	public void setType(final String type)
	{
		this.type = type;
	}

	/**
	 * @return the characteristic
	 */
	public List<Characteristic> getCharacteristic()
	{
		return characteristic;
	}

	/**
	 * @param characteristic
	 *           the characteristic to set
	 */
	public void setCharacteristic(final List<Characteristic> characteristic)
	{
		this.characteristic = characteristic;
	}

	/**
	 * @return the individual
	 */
	public Individual getIndividual()
	{
		return individual;
	}

	/**
	 * @param individual
	 *           the individual to set
	 */
	public void setIndividual(final Individual individual)
	{
		this.individual = individual;
	}

}
