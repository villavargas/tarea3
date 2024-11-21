package mx.com.telcel.models;

public class User
{

	private String userpreferredlanguage;
	private String locale;
	private String timezone;
	private String[] loginname;
	private String creationsource;
	private String phonenumberone;
	private String emailone;
	private String id;
	private Boolean traceflag;
	private Boolean registrationcompleted;
	private Long creationdate;
	private Integer passwordfailedattemptscounter;
	private Long passwordfailedattemptsdate;
	private Integer pinfailedattemptscounter;
	private Long pinfailedattemptsdate;
	private Long requestedpasswordate;
	private Integer requestedpasswordcounter;
	private String externalid;
	private String username;
	private String password;
	private String pin;
	private String userformattedname;
	private String userfamilyname;
	private String usergivenname;
	private String honorificprefix;
	private String honorificsuffix;
	private String userdisplayname;
	private String nickname;
	private String gender;
	private String birthdate;
	private Integer region;
	private String profileurl;
	private String photourl;
	private String usertitle;
	private String usertype;
	private Boolean active;
	private String group;
	private Boolean promotionflag;
	private Boolean advertisementconsentflag;
	private Boolean confidentialityagreement;
	private Long updatedate;
	private String updatesource;
	private Long lastaccessdate;
	private String securityquestion;
	private String securityanswer;
	private String addressformattedone;
	private String addressstreetone;
	private String addresslocalityone;
	private String addressregionone;
	private String addresspostalcodeone;
	private String addresscountryone;
	private String addresstypeone;
	private Boolean addressprimaryone;
	private String addressformattedtwo;
	private String addressstreettwo;
	private String addresslocalitytwo;
	private String addressregiontwo;
	private String addresspostalcodetwo;
	private String addresscountrytwo;
	private String addresstypetwo;
	private Boolean addressprimarytwo;
	private String emailtypeone;
	private Boolean emailprimaryone;
	private Boolean emailverifiedone;
	private String emailtwo;
	private String emailtypetwo;
	private Boolean emailprimarytwo;
	private Boolean emailverifiedtwo;
	private String phonenumbertypeone;
	private Boolean phonenumberprimaryone;
	private Boolean phonenumberverifiedone;
	private String phonenumbertwo;
	private String phonenumbertypetwo;
	private Boolean phonenumberprimarytwo;
	private Boolean phonenumberverifiedtwo;
	private String imone;
	private String imtypeone;
	private String imtwo;
	private String imtypetwo;
	private String serviceprovider;
	private Long lockdate;
	private Boolean suspended;
	private Long suspendeddate;
	private String Serviciossso;
	private Long Fechamail;
	private Long Fechaenvmail;
	private String Emailrecibe;
	private Integer Tipoenviofactura;
	private Long Fechaenviofactura;
	private String Sid;
	private String Sidacceso;
	private Integer Tipoplan;
	private Integer Cuenta;
	private Integer Cuentaconsolidada;
	private String RFC;

	/**
	 * @return the userpreferredlanguage
	 */
	public String getUserpreferredlanguage()
	{
		return userpreferredlanguage;
	}

	/**
	 * @param userpreferredlanguage
	 *           the userpreferredlanguage to set
	 */
	public void setUserpreferredlanguage(final String userpreferredlanguage)
	{
		this.userpreferredlanguage = userpreferredlanguage;
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
	 * @return the timezone
	 */
	public String getTimezone()
	{
		return timezone;
	}

	/**
	 * @param timezone
	 *           the timezone to set
	 */
	public void setTimezone(final String timezone)
	{
		this.timezone = timezone;
	}

	/**
	 * @return the loginname
	 */
	public String[] getLoginname()
	{
		return loginname;
	}

	/**
	 * @param loginname
	 *           the loginname to set
	 */
	public void setLoginname(final String[] loginname)
	{
		this.loginname = loginname;
	}

	/**
	 * @return the creationsource
	 */
	public String getCreationsource()
	{
		return creationsource;
	}

	/**
	 * @param creationsource
	 *           the creationsource to set
	 */
	public void setCreationsource(final String creationsource)
	{
		this.creationsource = creationsource;
	}

	/**
	 * @return the phonenumberone
	 */
	public String getPhonenumberone()
	{
		return phonenumberone;
	}

	/**
	 * @param phonenumberone
	 *           the phonenumberone to set
	 */
	public void setPhonenumberone(final String phonenumberone)
	{
		this.phonenumberone = phonenumberone;
	}

	/**
	 * @return the emailone
	 */
	public String getEmailone()
	{
		return emailone;
	}

	/**
	 * @param emailone
	 *           the emailone to set
	 */
	public void setEmailone(final String emailone)
	{
		this.emailone = emailone;
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
	 * @return the traceflag
	 */
	public Boolean isTraceflag()
	{
		return traceflag;
	}

	/**
	 * @param traceflag
	 *           the traceflag to set
	 */
	public void setTraceflag(final Boolean traceflag)
	{
		this.traceflag = traceflag;
	}

	/**
	 * @return the registrationcompleted
	 */
	public Boolean isRegistrationcompleted()
	{
		return registrationcompleted;
	}

	/**
	 * @param registrationcompleted
	 *           the registrationcompleted to set
	 */
	public void setRegistrationcompleted(final Boolean registrationcompleted)
	{
		this.registrationcompleted = registrationcompleted;
	}

	/**
	 * @return the creationdate
	 */
	public Long getCreationdate()
	{
		return creationdate;
	}

	/**
	 * @param creationdate
	 *           the creationdate to set
	 */
	public void setCreationdate(final Long creationdate)
	{
		this.creationdate = creationdate;
	}

	/**
	 * @return the passwordfailedattemptscounter
	 */
	public Integer getPasswordfailedattemptscounter()
	{
		return passwordfailedattemptscounter;
	}

	/**
	 * @param passwordfailedattemptscounter
	 *           the passwordfailedattemptscounter to set
	 */
	public void setPasswordfailedattemptscounter(final Integer passwordfailedattemptscounter)
	{
		this.passwordfailedattemptscounter = passwordfailedattemptscounter;
	}

	/**
	 * @return the passwordfailedattemptsdate
	 */
	public Long getPasswordfailedattemptsdate()
	{
		return passwordfailedattemptsdate;
	}

	/**
	 * @param passwordfailedattemptsdate
	 *           the passwordfailedattemptsdate to set
	 */
	public void setPasswordfailedattemptsdate(final Long passwordfailedattemptsdate)
	{
		this.passwordfailedattemptsdate = passwordfailedattemptsdate;
	}

	/**
	 * @return the pinfailedattemptscounter
	 */
	public Integer getPinfailedattemptscounter()
	{
		return pinfailedattemptscounter;
	}

	/**
	 * @param pinfailedattemptscounter
	 *           the pinfailedattemptscounter to set
	 */
	public void setPinfailedattemptscounter(final Integer pinfailedattemptscounter)
	{
		this.pinfailedattemptscounter = pinfailedattemptscounter;
	}

	/**
	 * @return the pinfailedattemptsdate
	 */
	public Long getPinfailedattemptsdate()
	{
		return pinfailedattemptsdate;
	}

	/**
	 * @param pinfailedattemptsdate
	 *           the pinfailedattemptsdate to set
	 */
	public void setPinfailedattemptsdate(final Long pinfailedattemptsdate)
	{
		this.pinfailedattemptsdate = pinfailedattemptsdate;
	}

	/**
	 * @return the requestedpasswordate
	 */
	public Long getRequestedpasswordate()
	{
		return requestedpasswordate;
	}

	/**
	 * @param requestedpasswordate
	 *           the requestedpasswordate to set
	 */
	public void setRequestedpasswordate(final Long requestedpasswordate)
	{
		this.requestedpasswordate = requestedpasswordate;
	}

	/**
	 * @return the requestedpasswordcounter
	 */
	public Integer getRequestedpasswordcounter()
	{
		return requestedpasswordcounter;
	}

	/**
	 * @param requestedpasswordcounter
	 *           the requestedpasswordcounter to set
	 */
	public void setRequestedpasswordcounter(final Integer requestedpasswordcounter)
	{
		this.requestedpasswordcounter = requestedpasswordcounter;
	}

	/**
	 * @return the externalid
	 */
	public String getExternalid()
	{
		return externalid;
	}

	/**
	 * @param externalid
	 *           the externalid to set
	 */
	public void setExternalid(final String externalid)
	{
		this.externalid = externalid;
	}

	/**
	 * @return the username
	 */
	public String getUsername()
	{
		return username;
	}

	/**
	 * @param username
	 *           the username to set
	 */
	public void setUsername(final String username)
	{
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword()
	{
		return password;
	}

	/**
	 * @param password
	 *           the password to set
	 */
	public void setPassword(final String password)
	{
		this.password = password;
	}

	/**
	 * @return the pin
	 */
	public String getPin()
	{
		return pin;
	}

	/**
	 * @param pin
	 *           the pin to set
	 */
	public void setPin(final String pin)
	{
		this.pin = pin;
	}

	/**
	 * @return the userformattedname
	 */
	public String getUserformattedname()
	{
		return userformattedname;
	}

	/**
	 * @param userformattedname
	 *           the userformattedname to set
	 */
	public void setUserformattedname(final String userformattedname)
	{
		this.userformattedname = userformattedname;
	}

	/**
	 * @return the userfamilyname
	 */
	public String getUserfamilyname()
	{
		return userfamilyname;
	}

	/**
	 * @param userfamilyname
	 *           the userfamilyname to set
	 */
	public void setUserfamilyname(final String userfamilyname)
	{
		this.userfamilyname = userfamilyname;
	}

	/**
	 * @return the usergivenname
	 */
	public String getUsergivenname()
	{
		return usergivenname;
	}

	/**
	 * @param usergivenname
	 *           the usergivenname to set
	 */
	public void setUsergivenname(final String usergivenname)
	{
		this.usergivenname = usergivenname;
	}

	/**
	 * @return the honorificprefix
	 */
	public String getHonorificprefix()
	{
		return honorificprefix;
	}

	/**
	 * @param honorificprefix
	 *           the honorificprefix to set
	 */
	public void setHonorificprefix(final String honorificprefix)
	{
		this.honorificprefix = honorificprefix;
	}

	/**
	 * @return the honorificsuffix
	 */
	public String getHonorificsuffix()
	{
		return honorificsuffix;
	}

	/**
	 * @param honorificsuffix
	 *           the honorificsuffix to set
	 */
	public void setHonorificsuffix(final String honorificsuffix)
	{
		this.honorificsuffix = honorificsuffix;
	}

	/**
	 * @return the userdisplayname
	 */
	public String getUserdisplayname()
	{
		return userdisplayname;
	}

	/**
	 * @param userdisplayname
	 *           the userdisplayname to set
	 */
	public void setUserdisplayname(final String userdisplayname)
	{
		this.userdisplayname = userdisplayname;
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
	 * @return the profileurl
	 */
	public String getProfileurl()
	{
		return profileurl;
	}

	/**
	 * @param profileurl
	 *           the profileurl to set
	 */
	public void setProfileurl(final String profileurl)
	{
		this.profileurl = profileurl;
	}

	/**
	 * @return the photourl
	 */
	public String getPhotourl()
	{
		return photourl;
	}

	/**
	 * @param photourl
	 *           the photourl to set
	 */
	public void setPhotourl(final String photourl)
	{
		this.photourl = photourl;
	}

	/**
	 * @return the usertitle
	 */
	public String getUsertitle()
	{
		return usertitle;
	}

	/**
	 * @param usertitle
	 *           the usertitle to set
	 */
	public void setUsertitle(final String usertitle)
	{
		this.usertitle = usertitle;
	}

	/**
	 * @return the usertype
	 */
	public String getUsertype()
	{
		return usertype;
	}

	/**
	 * @param usertype
	 *           the usertype to set
	 */
	public void setUsertype(final String usertype)
	{
		this.usertype = usertype;
	}

	/**
	 * @return the active
	 */
	public Boolean isActive()
	{
		return active;
	}

	/**
	 * @param active
	 *           the active to set
	 */
	public void setActive(final Boolean active)
	{
		this.active = active;
	}

	/**
	 * @return the group
	 */
	public String getGroup()
	{
		return group;
	}

	/**
	 * @param group
	 *           the group to set
	 */
	public void setGroup(final String group)
	{
		this.group = group;
	}

	/**
	 * @return the promotionflag
	 */
	public Boolean isPromotionflag()
	{
		return promotionflag;
	}

	/**
	 * @param promotionflag
	 *           the promotionflag to set
	 */
	public void setPromotionflag(final Boolean promotionflag)
	{
		this.promotionflag = promotionflag;
	}

	/**
	 * @return the advertisementconsentflag
	 */
	public Boolean isAdvertisementconsentflag()
	{
		return advertisementconsentflag;
	}

	/**
	 * @param advertisementconsentflag
	 *           the advertisementconsentflag to set
	 */
	public void setAdvertisementconsentflag(final Boolean advertisementconsentflag)
	{
		this.advertisementconsentflag = advertisementconsentflag;
	}

	/**
	 * @return the confidentialityagreement
	 */
	public Boolean isConfidentialityagreement()
	{
		return confidentialityagreement;
	}

	/**
	 * @param confidentialityagreement
	 *           the confidentialityagreement to set
	 */
	public void setConfidentialityagreement(final Boolean confidentialityagreement)
	{
		this.confidentialityagreement = confidentialityagreement;
	}

	/**
	 * @return the updatedate
	 */
	public Long getUpdatedate()
	{
		return updatedate;
	}

	/**
	 * @param updatedate
	 *           the updatedate to set
	 */
	public void setUpdatedate(final Long updatedate)
	{
		this.updatedate = updatedate;
	}

	/**
	 * @return the updatesource
	 */
	public String getUpdatesource()
	{
		return updatesource;
	}

	/**
	 * @param updatesource
	 *           the updatesource to set
	 */
	public void setUpdatesource(final String updatesource)
	{
		this.updatesource = updatesource;
	}

	/**
	 * @return the lastaccessdate
	 */
	public Long getLastaccessdate()
	{
		return lastaccessdate;
	}

	/**
	 * @param lastaccessdate
	 *           the lastaccessdate to set
	 */
	public void setLastaccessdate(final Long lastaccessdate)
	{
		this.lastaccessdate = lastaccessdate;
	}

	/**
	 * @return the securityquestion
	 */
	public String getSecurityquestion()
	{
		return securityquestion;
	}

	/**
	 * @param securityquestion
	 *           the securityquestion to set
	 */
	public void setSecurityquestion(final String securityquestion)
	{
		this.securityquestion = securityquestion;
	}

	/**
	 * @return the securityanswer
	 */
	public String getSecurityanswer()
	{
		return securityanswer;
	}

	/**
	 * @param securityanswer
	 *           the securityanswer to set
	 */
	public void setSecurityanswer(final String securityanswer)
	{
		this.securityanswer = securityanswer;
	}

	/**
	 * @return the addressformattedone
	 */
	public String getAddressformattedone()
	{
		return addressformattedone;
	}

	/**
	 * @param addressformattedone
	 *           the addressformattedone to set
	 */
	public void setAddressformattedone(final String addressformattedone)
	{
		this.addressformattedone = addressformattedone;
	}

	/**
	 * @return the addressstreetone
	 */
	public String getAddressstreetone()
	{
		return addressstreetone;
	}

	/**
	 * @param addressstreetone
	 *           the addressstreetone to set
	 */
	public void setAddressstreetone(final String addressstreetone)
	{
		this.addressstreetone = addressstreetone;
	}

	/**
	 * @return the addresslocalityone
	 */
	public String getAddresslocalityone()
	{
		return addresslocalityone;
	}

	/**
	 * @param addresslocalityone
	 *           the addresslocalityone to set
	 */
	public void setAddresslocalityone(final String addresslocalityone)
	{
		this.addresslocalityone = addresslocalityone;
	}

	/**
	 * @return the addressregionone
	 */
	public String getAddressregionone()
	{
		return addressregionone;
	}

	/**
	 * @param addressregionone
	 *           the addressregionone to set
	 */
	public void setAddressregionone(final String addressregionone)
	{
		this.addressregionone = addressregionone;
	}

	/**
	 * @return the addresspostalcodeone
	 */
	public String getAddresspostalcodeone()
	{
		return addresspostalcodeone;
	}

	/**
	 * @param addresspostalcodeone
	 *           the addresspostalcodeone to set
	 */
	public void setAddresspostalcodeone(final String addresspostalcodeone)
	{
		this.addresspostalcodeone = addresspostalcodeone;
	}

	/**
	 * @return the addresscountryone
	 */
	public String getAddresscountryone()
	{
		return addresscountryone;
	}

	/**
	 * @param addresscountryone
	 *           the addresscountryone to set
	 */
	public void setAddresscountryone(final String addresscountryone)
	{
		this.addresscountryone = addresscountryone;
	}

	/**
	 * @return the addresstypeone
	 */
	public String getAddresstypeone()
	{
		return addresstypeone;
	}

	/**
	 * @param addresstypeone
	 *           the addresstypeone to set
	 */
	public void setAddresstypeone(final String addresstypeone)
	{
		this.addresstypeone = addresstypeone;
	}

	/**
	 * @return the addressprimaryone
	 */
	public Boolean getAddressprimaryone()
	{
		return addressprimaryone;
	}

	/**
	 * @param addressprimaryone
	 *           the addressprimaryone to set
	 */
	public void setAddressprimaryone(final Boolean addressprimaryone)
	{
		this.addressprimaryone = addressprimaryone;
	}

	/**
	 * @return the addressformattedtwo
	 */
	public String getAddressformattedtwo()
	{
		return addressformattedtwo;
	}

	/**
	 * @param addressformattedtwo
	 *           the addressformattedtwo to set
	 */
	public void setAddressformattedtwo(final String addressformattedtwo)
	{
		this.addressformattedtwo = addressformattedtwo;
	}

	/**
	 * @return the addressstreettwo
	 */
	public String getAddressstreettwo()
	{
		return addressstreettwo;
	}

	/**
	 * @param addressstreettwo
	 *           the addressstreettwo to set
	 */
	public void setAddressstreettwo(final String addressstreettwo)
	{
		this.addressstreettwo = addressstreettwo;
	}

	/**
	 * @return the addresslocalitytwo
	 */
	public String getAddresslocalitytwo()
	{
		return addresslocalitytwo;
	}

	/**
	 * @param addresslocalitytwo
	 *           the addresslocalitytwo to set
	 */
	public void setAddresslocalitytwo(final String addresslocalitytwo)
	{
		this.addresslocalitytwo = addresslocalitytwo;
	}

	/**
	 * @return the addressregiontwo
	 */
	public String getAddressregiontwo()
	{
		return addressregiontwo;
	}

	/**
	 * @param addressregiontwo
	 *           the addressregiontwo to set
	 */
	public void setAddressregiontwo(final String addressregiontwo)
	{
		this.addressregiontwo = addressregiontwo;
	}

	/**
	 * @return the addresspostalcodetwo
	 */
	public String getAddresspostalcodetwo()
	{
		return addresspostalcodetwo;
	}

	/**
	 * @param addresspostalcodetwo
	 *           the addresspostalcodetwo to set
	 */
	public void setAddresspostalcodetwo(final String addresspostalcodetwo)
	{
		this.addresspostalcodetwo = addresspostalcodetwo;
	}

	/**
	 * @return the addresscountrytwo
	 */
	public String getAddresscountrytwo()
	{
		return addresscountrytwo;
	}

	/**
	 * @param addresscountrytwo
	 *           the addresscountrytwo to set
	 */
	public void setAddresscountrytwo(final String addresscountrytwo)
	{
		this.addresscountrytwo = addresscountrytwo;
	}

	/**
	 * @return the addresstypetwo
	 */
	public String getAddresstypetwo()
	{
		return addresstypetwo;
	}

	/**
	 * @param addresstypetwo
	 *           the addresstypetwo to set
	 */
	public void setAddresstypetwo(final String addresstypetwo)
	{
		this.addresstypetwo = addresstypetwo;
	}

	/**
	 * @return the addressprimarytwo
	 */
	public Boolean getAddressprimarytwo()
	{
		return addressprimarytwo;
	}

	/**
	 * @param addressprimarytwo
	 *           the addressprimarytwo to set
	 */
	public void setAddressprimarytwo(final Boolean addressprimarytwo)
	{
		this.addressprimarytwo = addressprimarytwo;
	}

	/**
	 * @return the emailtypeone
	 */
	public String getEmailtypeone()
	{
		return emailtypeone;
	}

	/**
	 * @param emailtypeone
	 *           the emailtypeone to set
	 */
	public void setEmailtypeone(final String emailtypeone)
	{
		this.emailtypeone = emailtypeone;
	}

	/**
	 * @return the emailprimaryone
	 */
	public Boolean isEmailprimaryone()
	{
		return emailprimaryone;
	}

	/**
	 * @param emailprimaryone
	 *           the emailprimaryone to set
	 */
	public void setEmailprimaryone(final Boolean emailprimaryone)
	{
		this.emailprimaryone = emailprimaryone;
	}

	/**
	 * @return the emailverifiedone
	 */
	public Boolean getEmailverifiedone()
	{
		return emailverifiedone;
	}

	/**
	 * @param emailverifiedone
	 *           the emailverifiedone to set
	 */
	public void setEmailverifiedone(final Boolean emailverifiedone)
	{
		this.emailverifiedone = emailverifiedone;
	}

	/**
	 * @return the emailtwo
	 */
	public String getEmailtwo()
	{
		return emailtwo;
	}

	/**
	 * @param emailtwo
	 *           the emailtwo to set
	 */
	public void setEmailtwo(final String emailtwo)
	{
		this.emailtwo = emailtwo;
	}

	/**
	 * @return the emailtypetwo
	 */
	public String getEmailtypetwo()
	{
		return emailtypetwo;
	}

	/**
	 * @param emailtypetwo
	 *           the emailtypetwo to set
	 */
	public void setEmailtypetwo(final String emailtypetwo)
	{
		this.emailtypetwo = emailtypetwo;
	}

	/**
	 * @return the emailprimarytwo
	 */
	public Boolean isEmailprimarytwo()
	{
		return emailprimarytwo;
	}

	/**
	 * @param emailprimarytwo
	 *           the emailprimarytwo to set
	 */
	public void setEmailprimarytwo(final Boolean emailprimarytwo)
	{
		this.emailprimarytwo = emailprimarytwo;
	}

	/**
	 * @return the emailverifiedtwo
	 */
	public Boolean isEmailverifiedtwo()
	{
		return emailverifiedtwo;
	}

	/**
	 * @param emailverifiedtwo
	 *           the emailverifiedtwo to set
	 */
	public void setEmailverifiedtwo(final Boolean emailverifiedtwo)
	{
		this.emailverifiedtwo = emailverifiedtwo;
	}

	/**
	 * @return the phonenumbertypeone
	 */
	public String getPhonenumbertypeone()
	{
		return phonenumbertypeone;
	}

	/**
	 * @param phonenumbertypeone
	 *           the phonenumbertypeone to set
	 */
	public void setPhonenumbertypeone(final String phonenumbertypeone)
	{
		this.phonenumbertypeone = phonenumbertypeone;
	}

	/**
	 * @return the phonenumberprimaryone
	 */
	public Boolean isPhonenumberprimaryone()
	{
		return phonenumberprimaryone;
	}

	/**
	 * @param phonenumberprimaryone
	 *           the phonenumberprimaryone to set
	 */
	public void setPhonenumberprimaryone(final Boolean phonenumberprimaryone)
	{
		this.phonenumberprimaryone = phonenumberprimaryone;
	}

	/**
	 * @return the phonenumberverifiedone
	 */
	public Boolean isPhonenumberverifiedone()
	{
		return phonenumberverifiedone;
	}

	/**
	 * @param phonenumberverifiedone
	 *           the phonenumberverifiedone to set
	 */
	public void setPhonenumberverifiedone(final Boolean phonenumberverifiedone)
	{
		this.phonenumberverifiedone = phonenumberverifiedone;
	}

	/**
	 * @return the phonenumbertwo
	 */
	public String getPhonenumbertwo()
	{
		return phonenumbertwo;
	}

	/**
	 * @param phonenumbertwo
	 *           the phonenumbertwo to set
	 */
	public void setPhonenumbertwo(final String phonenumbertwo)
	{
		this.phonenumbertwo = phonenumbertwo;
	}

	/**
	 * @return the phonenumbertypetwo
	 */
	public String getPhonenumbertypetwo()
	{
		return phonenumbertypetwo;
	}

	/**
	 * @param phonenumbertypetwo
	 *           the phonenumbertypetwo to set
	 */
	public void setPhonenumbertypetwo(final String phonenumbertypetwo)
	{
		this.phonenumbertypetwo = phonenumbertypetwo;
	}

	/**
	 * @return the phonenumberprimarytwo
	 */
	public Boolean isPhonenumberprimarytwo()
	{
		return phonenumberprimarytwo;
	}

	/**
	 * @param phonenumberprimarytwo
	 *           the phonenumberprimarytwo to set
	 */
	public void setPhonenumberprimarytwo(final Boolean phonenumberprimarytwo)
	{
		this.phonenumberprimarytwo = phonenumberprimarytwo;
	}

	/**
	 * @return the phonenumberverifiedtwo
	 */
	public Boolean isPhonenumberverifiedtwo()
	{
		return phonenumberverifiedtwo;
	}

	/**
	 * @param phonenumberverifiedtwo
	 *           the phonenumberverifiedtwo to set
	 */
	public void setPhonenumberverifiedtwo(final Boolean phonenumberverifiedtwo)
	{
		this.phonenumberverifiedtwo = phonenumberverifiedtwo;
	}

	/**
	 * @return the imone
	 */
	public String getImone()
	{
		return imone;
	}

	/**
	 * @param imone
	 *           the imone to set
	 */
	public void setImone(final String imone)
	{
		this.imone = imone;
	}

	/**
	 * @return the imtypeone
	 */
	public String getImtypeone()
	{
		return imtypeone;
	}

	/**
	 * @param imtypeone
	 *           the imtypeone to set
	 */
	public void setImtypeone(final String imtypeone)
	{
		this.imtypeone = imtypeone;
	}

	/**
	 * @return the imtwo
	 */
	public String getImtwo()
	{
		return imtwo;
	}

	/**
	 * @param imtwo
	 *           the imtwo to set
	 */
	public void setImtwo(final String imtwo)
	{
		this.imtwo = imtwo;
	}

	/**
	 * @return the imtypetwo
	 */
	public String getImtypetwo()
	{
		return imtypetwo;
	}

	/**
	 * @param imtypetwo
	 *           the imtypetwo to set
	 */
	public void setImtypetwo(final String imtypetwo)
	{
		this.imtypetwo = imtypetwo;
	}

	/**
	 * @return the serviceprovider
	 */
	public String getServiceprovider()
	{
		return serviceprovider;
	}

	/**
	 * @param serviceprovider
	 *           the serviceprovider to set
	 */
	public void setServiceprovider(final String serviceprovider)
	{
		this.serviceprovider = serviceprovider;
	}

	/**
	 * @return the lockdate
	 */
	public Long getLockdate()
	{
		return lockdate;
	}

	/**
	 * @param lockdate
	 *           the lockdate to set
	 */
	public void setLockdate(final Long lockdate)
	{
		this.lockdate = lockdate;
	}

	/**
	 * @return the suspended
	 */
	public Boolean isSuspended()
	{
		return suspended;
	}

	/**
	 * @param suspended
	 *           the suspended to set
	 */
	public void setSuspended(final Boolean suspended)
	{
		this.suspended = suspended;
	}

	/**
	 * @return the suspendeddate
	 */
	public Long getSuspendeddate()
	{
		return suspendeddate;
	}

	/**
	 * @param suspendeddate
	 *           the suspendeddate to set
	 */
	public void setSuspendeddate(final Long suspendeddate)
	{
		this.suspendeddate = suspendeddate;
	}

	/**
	 * @return the serviciossso
	 */
	public String getServiciossso()
	{
		return Serviciossso;
	}

	/**
	 * @param serviciossso
	 *           the serviciossso to set
	 */
	public void setServiciossso(final String serviciossso)
	{
		Serviciossso = serviciossso;
	}

	/**
	 * @return the fechamail
	 */
	public Long getFechamail()
	{
		return Fechamail;
	}

	/**
	 * @param fechamail
	 *           the fechamail to set
	 */
	public void setFechamail(final Long fechamail)
	{
		Fechamail = fechamail;
	}

	/**
	 * @return the fechaenvmail
	 */
	public Long getFechaenvmail()
	{
		return Fechaenvmail;
	}

	/**
	 * @param fechaenvmail
	 *           the fechaenvmail to set
	 */
	public void setFechaenvmail(final Long fechaenvmail)
	{
		Fechaenvmail = fechaenvmail;
	}

	/**
	 * @return the emailrecibe
	 */
	public String getEmailrecibe()
	{
		return Emailrecibe;
	}

	/**
	 * @param emailrecibe
	 *           the emailrecibe to set
	 */
	public void setEmailrecibe(final String emailrecibe)
	{
		Emailrecibe = emailrecibe;
	}

	/**
	 * @return the tipoenviofactura
	 */
	public Integer getTipoenviofactura()
	{
		return Tipoenviofactura;
	}

	/**
	 * @param tipoenviofactura
	 *           the tipoenviofactura to set
	 */
	public void setTipoenviofactura(final Integer tipoenviofactura)
	{
		Tipoenviofactura = tipoenviofactura;
	}

	/**
	 * @return the fechaenviofactura
	 */
	public Long getFechaenviofactura()
	{
		return Fechaenviofactura;
	}

	/**
	 * @param fechaenviofactura
	 *           the fechaenviofactura to set
	 */
	public void setFechaenviofactura(final Long fechaenviofactura)
	{
		Fechaenviofactura = fechaenviofactura;
	}

	/**
	 * @return the sid
	 */
	public String getSid()
	{
		return Sid;
	}

	/**
	 * @param sid
	 *           the sid to set
	 */
	public void setSid(final String sid)
	{
		Sid = sid;
	}

	/**
	 * @return the sidacceso
	 */
	public String getSidacceso()
	{
		return Sidacceso;
	}

	/**
	 * @param sidacceso
	 *           the sidacceso to set
	 */
	public void setSidacceso(final String sidacceso)
	{
		Sidacceso = sidacceso;
	}

	/**
	 * @return the tipoplan
	 */
	public Integer getTipoplan()
	{
		return Tipoplan;
	}

	/**
	 * @param tipoplan
	 *           the tipoplan to set
	 */
	public void setTipoplan(final Integer tipoplan)
	{
		Tipoplan = tipoplan;
	}

	/**
	 * @return the cuenta
	 */
	public Integer getCuenta()
	{
		return Cuenta;
	}

	/**
	 * @param cuenta
	 *           the cuenta to set
	 */
	public void setCuenta(final Integer cuenta)
	{
		Cuenta = cuenta;
	}

	/**
	 * @return the cuentaconsolidada
	 */
	public Integer getCuentaconsolidada()
	{
		return Cuentaconsolidada;
	}

	/**
	 * @param cuentaconsolidada
	 *           the cuentaconsolidada to set
	 */
	public void setCuentaconsolidada(final Integer cuentaconsolidada)
	{
		Cuentaconsolidada = cuentaconsolidada;
	}

	/**
	 * @return the rFC
	 */
	public String getRFC()
	{
		return RFC;
	}

	/**
	 * @param rFC
	 *           the rFC to set
	 */
	public void setRFC(final String rFC)
	{
		RFC = rFC;
	}

}
