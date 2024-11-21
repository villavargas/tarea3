package mx.com.telcel.models.individual;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;


public class GeographicAddressRef
{

	private String id;
	private String externalId;
	private String href;
	private String type;
	private String streetNr;
	private String streetName;
	private Area streetType;
	private String postcode;
	private Area locality;
	private Area city;
	private Area stateOrProvince;
	private Area country;
	@JsonProperty("@type")
	private String type2;
	@JsonProperty("@schemaLocation")
	private String schemaLocation;
	private GeographicLocation geographicLocation;
	private List<GeographicSubAddress> geographicSubAddress;
	private Area area;
	private List<LocationCharacteristic> characteristic;
	private String owner;
	private List<GeographicAddressSpec> geographicAddressSpec;

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
	 * @return the externalId
	 */
	public String getExternalId()
	{
		return externalId;
	}

	/**
	 * @param externalId
	 *           the externalId to set
	 */
	public void setExternalId(final String externalId)
	{
		this.externalId = externalId;
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
	 * @return the streetNr
	 */
	public String getStreetNr()
	{
		return streetNr;
	}

	/**
	 * @param streetNr
	 *           the streetNr to set
	 */
	public void setStreetNr(final String streetNr)
	{
		this.streetNr = streetNr;
	}

	/**
	 * @return the streetName
	 */
	public String getStreetName()
	{
		return streetName;
	}

	/**
	 * @param streetName
	 *           the streetName to set
	 */
	public void setStreetName(final String streetName)
	{
		this.streetName = streetName;
	}

	/**
	 * @return the streetType
	 */
	public Area getStreetType()
	{
		return streetType;
	}

	/**
	 * @param streetType
	 *           the streetType to set
	 */
	public void setStreetType(final Area streetType)
	{
		this.streetType = streetType;
	}

	/**
	 * @return the postcode
	 */
	public String getPostcode()
	{
		return postcode;
	}

	/**
	 * @param postcode
	 *           the postcode to set
	 */
	public void setPostcode(final String postcode)
	{
		this.postcode = postcode;
	}

	/**
	 * @return the locality
	 */
	public Area getLocality()
	{
		return locality;
	}

	/**
	 * @param locality
	 *           the locality to set
	 */
	public void setLocality(final Area locality)
	{
		this.locality = locality;
	}

	/**
	 * @return the city
	 */
	public Area getCity()
	{
		return city;
	}

	/**
	 * @param city
	 *           the city to set
	 */
	public void setCity(final Area city)
	{
		this.city = city;
	}

	/**
	 * @return the stateOrProvince
	 */
	public Area getStateOrProvince()
	{
		return stateOrProvince;
	}

	/**
	 * @param stateOrProvince
	 *           the stateOrProvince to set
	 */
	public void setStateOrProvince(final Area stateOrProvince)
	{
		this.stateOrProvince = stateOrProvince;
	}

	/**
	 * @return the country
	 */
	public Area getCountry()
	{
		return country;
	}

	/**
	 * @param country
	 *           the country to set
	 */
	public void setCountry(final Area country)
	{
		this.country = country;
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

	/**
	 * @return the geographicLocation
	 */
	public GeographicLocation getGeographicLocation()
	{
		return geographicLocation;
	}

	/**
	 * @param geographicLocation
	 *           the geographicLocation to set
	 */
	public void setGeographicLocation(final GeographicLocation geographicLocation)
	{
		this.geographicLocation = geographicLocation;
	}

	/**
	 * @return the geographicSubAddress
	 */
	public List<GeographicSubAddress> getGeographicSubAddress()
	{
		return geographicSubAddress;
	}

	/**
	 * @param geographicSubAddress
	 *           the geographicSubAddress to set
	 */
	public void setGeographicSubAddress(final List<GeographicSubAddress> geographicSubAddress)
	{
		this.geographicSubAddress = geographicSubAddress;
	}

	/**
	 * @return the area
	 */
	public Area getArea()
	{
		return area;
	}

	/**
	 * @param area
	 *           the area to set
	 */
	public void setArea(final Area area)
	{
		this.area = area;
	}

	/**
	 * @return the characteristic
	 */
	public List<LocationCharacteristic> getCharacteristic()
	{
		return characteristic;
	}

	/**
	 * @param characteristic
	 *           the characteristic to set
	 */
	public void setCharacteristic(final List<LocationCharacteristic> characteristic)
	{
		this.characteristic = characteristic;
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
	 * @return the geographicAddressSpec
	 */
	public List<GeographicAddressSpec> getGeographicAddressSpec()
	{
		return geographicAddressSpec;
	}

	/**
	 * @param geographicAddressSpec
	 *           the geographicAddressSpec to set
	 */
	public void setGeographicAddressSpec(final List<GeographicAddressSpec> geographicAddressSpec)
	{
		this.geographicAddressSpec = geographicAddressSpec;
	}

}
