package mx.com.telcel.models.individual;

import com.fasterxml.jackson.annotation.JsonProperty;


public class GeographicSubAddress
{

	private String id;
	private String type;
	private String name;
	private String description;
	private String subUnitType;
	private String subUnitNumber;
	private String levelType;
	private String levelNumber;
	private String buildingName;
	private String privateStreetNumber;
	private String privateStreetName;
	@JsonProperty("@type")
	private String type2;
	@JsonProperty("@schemaLocation")
	private String schemaLocation;

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
	 * @return the subUnitType
	 */
	public String getSubUnitType()
	{
		return subUnitType;
	}

	/**
	 * @param subUnitType
	 *           the subUnitType to set
	 */
	public void setSubUnitType(final String subUnitType)
	{
		this.subUnitType = subUnitType;
	}

	/**
	 * @return the subUnitNumber
	 */
	public String getSubUnitNumber()
	{
		return subUnitNumber;
	}

	/**
	 * @param subUnitNumber
	 *           the subUnitNumber to set
	 */
	public void setSubUnitNumber(final String subUnitNumber)
	{
		this.subUnitNumber = subUnitNumber;
	}

	/**
	 * @return the levelType
	 */
	public String getLevelType()
	{
		return levelType;
	}

	/**
	 * @param levelType
	 *           the levelType to set
	 */
	public void setLevelType(final String levelType)
	{
		this.levelType = levelType;
	}

	/**
	 * @return the levelNumber
	 */
	public String getLevelNumber()
	{
		return levelNumber;
	}

	/**
	 * @param levelNumber
	 *           the levelNumber to set
	 */
	public void setLevelNumber(final String levelNumber)
	{
		this.levelNumber = levelNumber;
	}

	/**
	 * @return the buildingName
	 */
	public String getBuildingName()
	{
		return buildingName;
	}

	/**
	 * @param buildingName
	 *           the buildingName to set
	 */
	public void setBuildingName(final String buildingName)
	{
		this.buildingName = buildingName;
	}

	/**
	 * @return the privateStreetNumber
	 */
	public String getPrivateStreetNumber()
	{
		return privateStreetNumber;
	}

	/**
	 * @param privateStreetNumber
	 *           the privateStreetNumber to set
	 */
	public void setPrivateStreetNumber(final String privateStreetNumber)
	{
		this.privateStreetNumber = privateStreetNumber;
	}

	/**
	 * @return the privateStreetName
	 */
	public String getPrivateStreetName()
	{
		return privateStreetName;
	}

	/**
	 * @param privateStreetName
	 *           the privateStreetName to set
	 */
	public void setPrivateStreetName(final String privateStreetName)
	{
		this.privateStreetName = privateStreetName;
	}

	/**
	 * @return the type2
	 */
	public String getType2()
	{
		return type2;
	}

	/**
	 * @param type2
	 *           the type2 to set
	 */
	public void setType2(final String type2)
	{
		this.type2 = type2;
	}

	/**
	 * @return the schemaLocation
	 */
	public String getSchemaLocation()
	{
		return schemaLocation;
	}

	/**
	 * @param schemaLocation
	 *           the schemaLocation to set
	 */
	public void setSchemaLocation(final String schemaLocation)
	{
		this.schemaLocation = schemaLocation;
	}

}
