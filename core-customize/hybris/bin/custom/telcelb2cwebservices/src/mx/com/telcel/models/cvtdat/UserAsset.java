package mx.com.telcel.models.cvtdat;

public class UserAsset
{

	private String entityType;
	private Entitlement entitlement;
	private String id;
	private String identifierType;
	private String assetType;
	private String role;

	/**
	 * @return the entityType
	 */
	public String getEntityType()
	{
		return entityType;
	}

	/**
	 * @param entityType
	 *           the entityType to set
	 */
	public void setEntityType(final String entityType)
	{
		this.entityType = entityType;
	}

	/**
	 * @return the entitlement
	 */
	public Entitlement getEntitlement()
	{
		return entitlement;
	}

	/**
	 * @param entitlement
	 *           the entitlement to set
	 */
	public void setEntitlement(final Entitlement entitlement)
	{
		this.entitlement = entitlement;
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
	 * @return the assetType
	 */
	public String getAssetType()
	{
		return assetType;
	}

	/**
	 * @param assetType
	 *           the assetType to set
	 */
	public void setAssetType(final String assetType)
	{
		this.assetType = assetType;
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

}
