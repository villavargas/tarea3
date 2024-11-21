package mx.com.telcel.models.fedex;

import java.util.List;


public class Party
{

	private String type;
	private String description;
	private String href;
	private String id;
	private String isLegalEntity;
	private Individual individual;
	private Organization organization;
	private Role role;
	private List<Characteristic> characteristics;
	private Address address;
	private List<ContactMedium> contactMedium;
	private List<Account> account;

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
	 * @return the description
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * @param description
	 *           the description to set
	 */
	public void setDescription(final String description)
	{
		this.description = description;
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
	 * @return the isLegalEntity
	 */
	public String getIsLegalEntity()
	{
		return isLegalEntity;
	}

	/**
	 * @param isLegalEntity
	 *           the isLegalEntity to set
	 */
	public void setIsLegalEntity(final String isLegalEntity)
	{
		this.isLegalEntity = isLegalEntity;
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

	/**
	 * @return the organization
	 */
	public Organization getOrganization()
	{
		return organization;
	}

	/**
	 * @param organization
	 *           the organization to set
	 */
	public void setOrganization(final Organization organization)
	{
		this.organization = organization;
	}

	/**
	 * @return the role
	 */
	public Role getRole()
	{
		return role;
	}

	/**
	 * @param role
	 *           the role to set
	 */
	public void setRole(final Role role)
	{
		this.role = role;
	}

	/**
	 * @return the characteristics
	 */
	public List<Characteristic> getCharacteristics()
	{
		return characteristics;
	}

	/**
	 * @param characteristics
	 *           the characteristics to set
	 */
	public void setCharacteristics(final List<Characteristic> characteristics)
	{
		this.characteristics = characteristics;
	}

	/**
	 * @return the address
	 */
	public Address getAddress()
	{
		return address;
	}

	/**
	 * @param address
	 *           the address to set
	 */
	public void setAddress(final Address address)
	{
		this.address = address;
	}

	/**
	 * @return the contactMedium
	 */
	public List<ContactMedium> getContactMedium()
	{
		return contactMedium;
	}

	/**
	 * @param contactMedium
	 *           the contactMedium to set
	 */
	public void setContactMedium(final List<ContactMedium> contactMedium)
	{
		this.contactMedium = contactMedium;
	}

	/**
	 * @return the account
	 */
	public List<Account> getAccount()
	{
		return account;
	}

	/**
	 * @param account
	 *           the account to set
	 */
	public void setAccount(final List<Account> account)
	{
		this.account = account;
	}

}
