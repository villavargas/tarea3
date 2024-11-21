package mx.com.telcel.models.cvtdat;

public class Contact
{

	private String contactName;
	private String contactType;
	private String partyRoleType;
	private ContactMedium contactMedium;

	/**
	 * @return the contactName
	 */
	public String getContactName()
	{
		return contactName;
	}

	/**
	 * @param contactName
	 *           the contactName to set
	 */
	public void setContactName(final String contactName)
	{
		this.contactName = contactName;
	}

	/**
	 * @return the contactType
	 */
	public String getContactType()
	{
		return contactType;
	}

	/**
	 * @param contactType
	 *           the contactType to set
	 */
	public void setContactType(final String contactType)
	{
		this.contactType = contactType;
	}

	/**
	 * @return the partyRoleType
	 */
	public String getPartyRoleType()
	{
		return partyRoleType;
	}

	/**
	 * @param partyRoleType
	 *           the partyRoleType to set
	 */
	public void setPartyRoleType(final String partyRoleType)
	{
		this.partyRoleType = partyRoleType;
	}

	/**
	 * @return the contactMedium
	 */
	public ContactMedium getContactMedium()
	{
		return contactMedium;
	}

	/**
	 * @param contactMedium
	 *           the contactMedium to set
	 */
	public void setContactMedium(final ContactMedium contactMedium)
	{
		this.contactMedium = contactMedium;
	}

}
