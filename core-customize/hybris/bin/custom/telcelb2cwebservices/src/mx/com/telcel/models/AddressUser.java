package mx.com.telcel.models;

public class AddressUser
{

	private String formatted;
	private String street_address;
	private String localy;
	private String region;
	private String postal_code;
	private String country;
	private Integer numberaddress;
	private String phonenumber;
	private Boolean addressprimary;
	private String userreceiving;

	/**
	 * @return the formatted
	 */
	public String getFormatted()
	{
		return formatted;
	}

	/**
	 * @param formatted
	 *           the formatted to set
	 */
	public void setFormatted(final String formatted)
	{
		this.formatted = formatted;
	}

	/**
	 * @return the street_address
	 */
	public String getStreet_address()
	{
		return street_address;
	}

	/**
	 * @param street_address
	 *           the street_address to set
	 */
	public void setStreet_address(final String street_address)
	{
		this.street_address = street_address;
	}

	/**
	 * @return the localy
	 */
	public String getLocaly()
	{
		return localy;
	}

	/**
	 * @param localy
	 *           the localy to set
	 */
	public void setLocaly(final String localy)
	{
		this.localy = localy;
	}

	/**
	 * @return the region
	 */
	public String getRegion()
	{
		return region;
	}

	/**
	 * @param region
	 *           the region to set
	 */
	public void setRegion(final String region)
	{
		this.region = region;
	}

	/**
	 * @return the postal_code
	 */
	public String getPostal_code()
	{
		return postal_code;
	}

	/**
	 * @param postal_code
	 *           the postal_code to set
	 */
	public void setPostal_code(final String postal_code)
	{
		this.postal_code = postal_code;
	}

	/**
	 * @return the country
	 */
	public String getCountry()
	{
		return country;
	}

	/**
	 * @param country
	 *           the country to set
	 */
	public void setCountry(final String country)
	{
		this.country = country;
	}

	/**
	 * @return the numberaddress
	 */
	public Integer getNumberaddress()
	{
		return numberaddress;
	}

	/**
	 * @param numberaddress
	 *           the numberaddress to set
	 */
	public void setNumberaddress(final Integer numberaddress)
	{
		this.numberaddress = numberaddress;
	}

	/**
	 * @return the phonenumber
	 */
	public String getPhonenumber()
	{
		return phonenumber;
	}

	/**
	 * @param phonenumber
	 *           the phonenumber to set
	 */
	public void setPhonenumber(final String phonenumber)
	{
		this.phonenumber = phonenumber;
	}

	/**
	 * @return the addressprimary
	 */
	public Boolean getAddressprimary()
	{
		return addressprimary;
	}

	/**
	 * @param addressprimary
	 *           the addressprimary to set
	 */
	public void setAddressprimary(final Boolean addressprimary)
	{
		this.addressprimary = addressprimary;
	}

	/**
	 * @return the userreceiving
	 */
	public String getUserreceiving()
	{
		return userreceiving;
	}

	/**
	 * @param userreceiving
	 *           the userreceiving to set
	 */
	public void setUserreceiving(final String userreceiving)
	{
		this.userreceiving = userreceiving;
	}

}
