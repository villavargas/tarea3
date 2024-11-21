package mx.com.telcel.models.individual;

import com.fasterxml.jackson.annotation.JsonProperty;


public class GeographicLocation
{

	private String id;
	private String href;
	private String name;
	private String geometryType;
	private String accuracy;
	private String spatialRef;
	@JsonProperty("@type")
	private String type;
	@JsonProperty("@schemaLocation")
	private String schemaLocation;
	private GeographicPoint geometry;

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
	 * @return the geometryType
	 */
	public String getGeometryType()
	{
		return geometryType;
	}

	/**
	 * @param geometryType
	 *           the geometryType to set
	 */
	public void setGeometryType(final String geometryType)
	{
		this.geometryType = geometryType;
	}

	/**
	 * @return the accuracy
	 */
	public String getAccuracy()
	{
		return accuracy;
	}

	/**
	 * @param accuracy
	 *           the accuracy to set
	 */
	public void setAccuracy(final String accuracy)
	{
		this.accuracy = accuracy;
	}

	/**
	 * @return the spatialRef
	 */
	public String getSpatialRef()
	{
		return spatialRef;
	}

	/**
	 * @param spatialRef
	 *           the spatialRef to set
	 */
	public void setSpatialRef(final String spatialRef)
	{
		this.spatialRef = spatialRef;
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

	/**
	 * @return the geometry
	 */
	public GeographicPoint getGeometry()
	{
		return geometry;
	}

	/**
	 * @param geometry
	 *           the geometry to set
	 */
	public void setGeometry(final GeographicPoint geometry)
	{
		this.geometry = geometry;
	}

}
