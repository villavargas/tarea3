package mx.com.telcel.models.cvtdat;

import java.util.List;


public class Individual
{

	private String id;
	private String href;
	private String familyName;
	private String givenName;
	private String secondLastName;
	private String type;
	private String status;
	private Identification individualIdentification;
	private List<Contact> contact;
	private List<Characteristic> characteristic;

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
	 * @return the familyName
	 */
	public String getFamilyName()
	{
		return familyName;
	}

	/**
	 * @param familyName
	 *           the familyName to set
	 */
	public void setFamilyName(final String familyName)
	{
		this.familyName = familyName;
	}

	/**
	 * @return the givenName
	 */
	public String getGivenName()
	{
		return givenName;
	}

	/**
	 * @param givenName
	 *           the givenName to set
	 */
	public void setGivenName(final String givenName)
	{
		this.givenName = givenName;
	}

	/**
	 * @return the secondLastName
	 */
	public String getSecondLastName()
	{
		return secondLastName;
	}

	/**
	 * @param secondLastName
	 *           the secondLastName to set
	 */
	public void setSecondLastName(final String secondLastName)
	{
		this.secondLastName = secondLastName;
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
	 * @return the status
	 */
	public String getStatus()
	{
		return status;
	}

	/**
	 * @param status
	 *           the status to set
	 */
	public void setStatus(final String status)
	{
		this.status = status;
	}

	/**
	 * @return the individualIdentification
	 */
	public Identification getIndividualIdentification()
	{
		return individualIdentification;
	}

	/**
	 * @param individualIdentification
	 *           the individualIdentification to set
	 */
	public void setIndividualIdentification(final Identification individualIdentification)
	{
		this.individualIdentification = individualIdentification;
	}

	/**
	 * @return the contact
	 */
	public List<Contact> getContact()
	{
		return contact;
	}

	/**
	 * @param contact
	 *           the contact to set
	 */
	public void setContact(final List<Contact> contact)
	{
		this.contact = contact;
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

}
