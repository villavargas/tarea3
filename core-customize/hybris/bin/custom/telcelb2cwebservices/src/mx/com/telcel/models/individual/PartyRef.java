package mx.com.telcel.models.individual;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;


public class PartyRef
{

	private String id;
	private String href;
	private String name;
	private String role;
	private String identifierType;
	private String partyRank;
	private List<PartyProfileType> partyProfileType;
	private List<ExternalReference> externalReference;
	private List<PartyIdentification> partyIdentification;
	@JsonProperty("@type")
	private String type;
	private Boolean isLegalEntity;

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
	 * @return the partyRank
	 */
	public String getPartyRank()
	{
		return partyRank;
	}

	/**
	 * @param partyRank
	 *           the partyRank to set
	 */
	public void setPartyRank(final String partyRank)
	{
		this.partyRank = partyRank;
	}

	/**
	 * @return the partyProfileType
	 */
	public List<PartyProfileType> getPartyProfileType()
	{
		return partyProfileType;
	}

	/**
	 * @param partyProfileType
	 *           the partyProfileType to set
	 */
	public void setPartyProfileType(final List<PartyProfileType> partyProfileType)
	{
		this.partyProfileType = partyProfileType;
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
	 * @return the partyIdentification
	 */
	public List<PartyIdentification> getPartyIdentification()
	{
		return partyIdentification;
	}

	/**
	 * @param partyIdentification
	 *           the partyIdentification to set
	 */
	public void setPartyIdentification(final List<PartyIdentification> partyIdentification)
	{
		this.partyIdentification = partyIdentification;
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
	 * @return the isLegalEntity
	 */
	public Boolean getIsLegalEntity()
	{
		return isLegalEntity;
	}

	/**
	 * @param isLegalEntity
	 *           the isLegalEntity to set
	 */
	public void setIsLegalEntity(final Boolean isLegalEntity)
	{
		this.isLegalEntity = isLegalEntity;
	}

}
