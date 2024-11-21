package mx.com.telcel.models.individual;

import java.util.List;

import org.joda.time.DateTime;


public class Individual
{

	private String id;
	private String href;
	private DateTime birthDate;
	private String familyName;
	private String gender;
	private String givenName;
	private String maritalStatus;
	private String secondLastName;
	private String nationality;
	private String title;
	private List<ContactMedium> contactMedium;
	private List<ExternalReference> externalReference;
	private List<IndividualIdentification> individualIdentification;
	private List<LanguageAbility> languageAbility;
	private List<Characteristic> partyCharacteristic;
	private List<PartyRef> partyRef;
	private String status;
	private PartyProfileType partyProfileType;
	private String owner;
	private DateTime createDateTime;
	private DateTime lastUpdateDateTime;
	private String createdBy;
	private String lastUpdateBy;

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
	 * @return the birthDate
	 */
	public DateTime getBirthDate()
	{
		return birthDate;
	}

	/**
	 * @param birthDate
	 *           the birthDate to set
	 */
	public void setBirthDate(final DateTime birthDate)
	{
		this.birthDate = birthDate;
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
	 * @return the gender
	 */
	public String getGender()
	{
		return gender;
	}

	/**
	 * @param gender
	 *           the gender to set
	 */
	public void setGender(final String gender)
	{
		this.gender = gender;
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
	 * @return the maritalStatus
	 */
	public String getMaritalStatus()
	{
		return maritalStatus;
	}

	/**
	 * @param maritalStatus
	 *           the maritalStatus to set
	 */
	public void setMaritalStatus(final String maritalStatus)
	{
		this.maritalStatus = maritalStatus;
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
	 * @return the nationality
	 */
	public String getNationality()
	{
		return nationality;
	}

	/**
	 * @param nationality
	 *           the nationality to set
	 */
	public void setNationality(final String nationality)
	{
		this.nationality = nationality;
	}

	/**
	 * @return the title
	 */
	public String getTitle()
	{
		return title;
	}

	/**
	 * @param title
	 *           the title to set
	 */
	public void setTitle(final String title)
	{
		this.title = title;
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
	 * @return the externalReference
	 */
	public List<ExternalReference> getExternalReference()
	{
		return externalReference;
	}

	/**
	 * @param externalReference
	 *           the externalReference to set
	 */
	public void setExternalReference(final List<ExternalReference> externalReference)
	{
		this.externalReference = externalReference;
	}

	/**
	 * @return the individualIdentification
	 */
	public List<IndividualIdentification> getIndividualIdentification()
	{
		return individualIdentification;
	}

	/**
	 * @param individualIdentification
	 *           the individualIdentification to set
	 */
	public void setIndividualIdentification(final List<IndividualIdentification> individualIdentification)
	{
		this.individualIdentification = individualIdentification;
	}

	/**
	 * @return the languageAbility
	 */
	public List<LanguageAbility> getLanguageAbility()
	{
		return languageAbility;
	}

	/**
	 * @param languageAbility
	 *           the languageAbility to set
	 */
	public void setLanguageAbility(final List<LanguageAbility> languageAbility)
	{
		this.languageAbility = languageAbility;
	}

	/**
	 * @return the partyCharacteristic
	 */
	public List<Characteristic> getPartyCharacteristic()
	{
		return partyCharacteristic;
	}

	/**
	 * @param partyCharacteristic
	 *           the partyCharacteristic to set
	 */
	public void setPartyCharacteristic(final List<Characteristic> partyCharacteristic)
	{
		this.partyCharacteristic = partyCharacteristic;
	}

	/**
	 * @return the partyRef
	 */
	public List<PartyRef> getPartyRef()
	{
		return partyRef;
	}

	/**
	 * @param partyRef
	 *           the partyRef to set
	 */
	public void setPartyRef(final List<PartyRef> partyRef)
	{
		this.partyRef = partyRef;
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
	 * @return the partyProfileType
	 */
	public PartyProfileType getPartyProfileType()
	{
		return partyProfileType;
	}

	/**
	 * @param partyProfileType
	 *           the partyProfileType to set
	 */
	public void setPartyProfileType(final PartyProfileType partyProfileType)
	{
		this.partyProfileType = partyProfileType;
	}

	/**
	 * @return the owner
	 */
	public String getOwner()
	{
		return owner;
	}

	/**
	 * @param owner
	 *           the owner to set
	 */
	public void setOwner(final String owner)
	{
		this.owner = owner;
	}

	/**
	 * @return the createDateTime
	 */
	public DateTime getCreateDateTime()
	{
		return createDateTime;
	}

	/**
	 * @param createDateTime
	 *           the createDateTime to set
	 */
	public void setCreateDateTime(final DateTime createDateTime)
	{
		this.createDateTime = createDateTime;
	}

	/**
	 * @return the lastUpdateDateTime
	 */
	public DateTime getLastUpdateDateTime()
	{
		return lastUpdateDateTime;
	}

	/**
	 * @param lastUpdateDateTime
	 *           the lastUpdateDateTime to set
	 */
	public void setLastUpdateDateTime(final DateTime lastUpdateDateTime)
	{
		this.lastUpdateDateTime = lastUpdateDateTime;
	}

	/**
	 * @return the createdBy
	 */
	public String getCreatedBy()
	{
		return createdBy;
	}

	/**
	 * @param createdBy
	 *           the createdBy to set
	 */
	public void setCreatedBy(final String createdBy)
	{
		this.createdBy = createdBy;
	}

	/**
	 * @return the lastUpdateBy
	 */
	public String getLastUpdateBy()
	{
		return lastUpdateBy;
	}

	/**
	 * @param lastUpdateBy
	 *           the lastUpdateBy to set
	 */
	public void setLastUpdateBy(final String lastUpdateBy)
	{
		this.lastUpdateBy = lastUpdateBy;
	}

}
