package mx.com.telcel.response.models;

import mx.com.telcel.models.AddressUser;


public class UserInfoResponse
{

	private String sub;
	private String name;
	private String given_name;
	private String family_name;
	private String middle_name;
	private Integer region;
	private String nickname;
	private String preferred_username;
	private String profile;
	private String picture;
	private String website;
	private String email;
	private Boolean email_verified;
	private String gender;
	private String birthdate;
	private String zoneinfo;
	private String locale;
	private String phone_number;
	private Boolean phone_number_verfied;
	private AddressUser address;
	private Integer updated_at;
	private String rol;
	private String supervisor;
	private String error;
	private Integer errorCode;
	private String loginname;

	/**
	 * @return the sub
	 */
	public String getSub()
	{
		return sub;
	}

	/**
	 * @param sub
	 *           the sub to set
	 */
	public void setSub(final String sub)
	{
		this.sub = sub;
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
	 * @return the given_name
	 */
	public String getGiven_name()
	{
		return given_name;
	}

	/**
	 * @param given_name
	 *           the given_name to set
	 */
	public void setGiven_name(final String given_name)
	{
		this.given_name = given_name;
	}

	/**
	 * @return the family_name
	 */
	public String getFamily_name()
	{
		return family_name;
	}

	/**
	 * @param family_name
	 *           the family_name to set
	 */
	public void setFamily_name(final String family_name)
	{
		this.family_name = family_name;
	}

	/**
	 * @return the middle_name
	 */
	public String getMiddle_name()
	{
		return middle_name;
	}

	/**
	 * @param middle_name
	 *           the middle_name to set
	 */
	public void setMiddle_name(final String middle_name)
	{
		this.middle_name = middle_name;
	}

	/**
	 * @return the region
	 */
	public Integer getRegion()
	{
		return region;
	}

	/**
	 * @param region
	 *           the region to set
	 */
	public void setRegion(final Integer region)
	{
		this.region = region;
	}

	/**
	 * @return the nickname
	 */
	public String getNickname()
	{
		return nickname;
	}

	/**
	 * @param nickname
	 *           the nickname to set
	 */
	public void setNickname(final String nickname)
	{
		this.nickname = nickname;
	}

	/**
	 * @return the preferred_username
	 */
	public String getPreferred_username()
	{
		return preferred_username;
	}

	/**
	 * @param preferred_username
	 *           the preferred_username to set
	 */
	public void setPreferred_username(final String preferred_username)
	{
		this.preferred_username = preferred_username;
	}

	/**
	 * @return the profile
	 */
	public String getProfile()
	{
		return profile;
	}

	/**
	 * @param profile
	 *           the profile to set
	 */
	public void setProfile(final String profile)
	{
		this.profile = profile;
	}

	/**
	 * @return the picture
	 */
	public String getPicture()
	{
		return picture;
	}

	/**
	 * @param picture
	 *           the picture to set
	 */
	public void setPicture(final String picture)
	{
		this.picture = picture;
	}

	/**
	 * @return the website
	 */
	public String getWebsite()
	{
		return website;
	}

	/**
	 * @param website
	 *           the website to set
	 */
	public void setWebsite(final String website)
	{
		this.website = website;
	}

	/**
	 * @return the email
	 */
	public String getEmail()
	{
		return email;
	}

	/**
	 * @param email
	 *           the email to set
	 */
	public void setEmail(final String email)
	{
		this.email = email;
	}

	/**
	 * @return the email_verified
	 */
	public Boolean getEmail_verified()
	{
		return email_verified;
	}

	/**
	 * @param email_verified
	 *           the email_verified to set
	 */
	public void setEmail_verified(final Boolean email_verified)
	{
		this.email_verified = email_verified;
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
	 * @return the birthdate
	 */
	public String getBirthdate()
	{
		return birthdate;
	}

	/**
	 * @param birthdate
	 *           the birthdate to set
	 */
	public void setBirthdate(final String birthdate)
	{
		this.birthdate = birthdate;
	}

	/**
	 * @return the zoneinfo
	 */
	public String getZoneinfo()
	{
		return zoneinfo;
	}

	/**
	 * @param zoneinfo
	 *           the zoneinfo to set
	 */
	public void setZoneinfo(final String zoneinfo)
	{
		this.zoneinfo = zoneinfo;
	}

	/**
	 * @return the locale
	 */
	public String getLocale()
	{
		return locale;
	}

	/**
	 * @param locale
	 *           the locale to set
	 */
	public void setLocale(final String locale)
	{
		this.locale = locale;
	}

	/**
	 * @return the phone_number
	 */
	public String getPhone_number()
	{
		return phone_number;
	}

	/**
	 * @param phone_number
	 *           the phone_number to set
	 */
	public void setPhone_number(final String phone_number)
	{
		this.phone_number = phone_number;
	}

	/**
	 * @return the phone_number_verfied
	 */
	public Boolean getPhone_number_verfied()
	{
		return phone_number_verfied;
	}

	/**
	 * @param phone_number_verfied
	 *           the phone_number_verfied to set
	 */
	public void setPhone_number_verfied(final Boolean phone_number_verfied)
	{
		this.phone_number_verfied = phone_number_verfied;
	}

	/**
	 * @return the address
	 */
	public AddressUser getAddress()
	{
		return address;
	}

	/**
	 * @param address
	 *           the address to set
	 */
	public void setAddress(final AddressUser address)
	{
		this.address = address;
	}

	/**
	 * @return the updated_at
	 */
	public Integer getUpdated_at()
	{
		return updated_at;
	}

	/**
	 * @param updated_at
	 *           the updated_at to set
	 */
	public void setUpdated_at(final Integer updated_at)
	{
		this.updated_at = updated_at;
	}

	/**
	 * @return the rol
	 */
	public String getRol()
	{
		return rol;
	}

	/**
	 * @param rol
	 *           the rol to set
	 */
	public void setRol(final String rol)
	{
		this.rol = rol;
	}

	/**
	 * @return the supervisor
	 */
	public String getSupervisor()
	{
		return supervisor;
	}

	/**
	 * @param supervisor
	 *           the supervisor to set
	 */
	public void setSupervisor(final String supervisor)
	{
		this.supervisor = supervisor;
	}

	/**
	 * @return the error
	 */
	public String getError()
	{
		return error;
	}

	/**
	 * @param error
	 *           the error to set
	 */
	public void setError(final String error)
	{
		this.error = error;
	}

	/**
	 * @return the errorCode
	 */
	public Integer getErrorCode()
	{
		return errorCode;
	}

	/**
	 * @param errorCode
	 *           the errorCode to set
	 */
	public void setErrorCode(final Integer errorCode)
	{
		this.errorCode = errorCode;
	}

	/**
	 * @return the loginname
	 */
	public String getLoginname()
	{
		return loginname;
	}

	/**
	 * @param loginname
	 *           the loginname to set
	 */
	public void setLoginname(final String loginname)
	{
		this.loginname = loginname;
	}

}
