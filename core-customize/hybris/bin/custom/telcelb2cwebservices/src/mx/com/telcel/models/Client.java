package mx.com.telcel.models;

public class Client
{

	private String clientid;
	private String Package;//package
	private Integer sessiontime;
	private Integer passwordmaxattempts;
	private Integer lockedtime;
	private String applicationtype;
	private String clientname;
	private String logouri;
	private String clienturi;
	private String policyuri;
	private String tosuri;
	private String jwksuri;
	private String jwks;
	private String sectoridentifieruri;
	private String subjecttype;
	private String idtokensignedresponsealg;
	private String idtokenencryptedresponsealg;
	private String idtokenencryptedresponseenc;
	private String userinfosignedresponsealg;
	private String userinfoencryptedresponsealg;
	private String userinfoencryptedresponseenc;
	private String requestobjectsigningalg;
	private String requestobjectencryptionalg;
	private String requestobjectencryptionenc;
	private String tokenendpoIntegerauthmethod;
	private String tokenendpoIntegerauthSigningalg;
	private Integer defaultmaxage;
	private Boolean requireauthtime;
	private String initiateloginuri;
	private Boolean clientactive;
	private String[] redirecturi;
	private String[] responsetype;
	private String[] granttype;
	private String contact;
	private String defaultacrvalue;
	private String requesturi;
	private String[] device;
	private String clientsecret;

	/**
	 * @return the clientid
	 */
	public String getClientid()
	{
		return clientid;
	}

	/**
	 * @param clientid
	 *           the clientid to set
	 */
	public void setClientid(final String clientid)
	{
		this.clientid = clientid;
	}

	/**
	 * @return the package
	 */
	public String getPackage()
	{
		return Package;
	}

	/**
	 * @param package1
	 *           the package to set
	 */
	public void setPackage(final String package1)
	{
		Package = package1;
	}

	/**
	 * @return the sessiontime
	 */
	public Integer getSessiontime()
	{
		return sessiontime;
	}

	/**
	 * @param sessiontime
	 *           the sessiontime to set
	 */
	public void setSessiontime(final Integer sessiontime)
	{
		this.sessiontime = sessiontime;
	}

	/**
	 * @return the passwordmaxattempts
	 */
	public Integer getPasswordmaxattempts()
	{
		return passwordmaxattempts;
	}

	/**
	 * @param passwordmaxattempts
	 *           the passwordmaxattempts to set
	 */
	public void setPasswordmaxattempts(final Integer passwordmaxattempts)
	{
		this.passwordmaxattempts = passwordmaxattempts;
	}

	/**
	 * @return the lockedtime
	 */
	public Integer getLockedtime()
	{
		return lockedtime;
	}

	/**
	 * @param lockedtime
	 *           the lockedtime to set
	 */
	public void setLockedtime(final Integer lockedtime)
	{
		this.lockedtime = lockedtime;
	}

	/**
	 * @return the applicationtype
	 */
	public String getApplicationtype()
	{
		return applicationtype;
	}

	/**
	 * @param applicationtype
	 *           the applicationtype to set
	 */
	public void setApplicationtype(final String applicationtype)
	{
		this.applicationtype = applicationtype;
	}

	/**
	 * @return the clientname
	 */
	public String getClientname()
	{
		return clientname;
	}

	/**
	 * @param clientname
	 *           the clientname to set
	 */
	public void setClientname(final String clientname)
	{
		this.clientname = clientname;
	}

	/**
	 * @return the logouri
	 */
	public String getLogouri()
	{
		return logouri;
	}

	/**
	 * @param logouri
	 *           the logouri to set
	 */
	public void setLogouri(final String logouri)
	{
		this.logouri = logouri;
	}

	/**
	 * @return the clienturi
	 */
	public String getClienturi()
	{
		return clienturi;
	}

	/**
	 * @param clienturi
	 *           the clienturi to set
	 */
	public void setClienturi(final String clienturi)
	{
		this.clienturi = clienturi;
	}

	/**
	 * @return the policyuri
	 */
	public String getPolicyuri()
	{
		return policyuri;
	}

	/**
	 * @param policyuri
	 *           the policyuri to set
	 */
	public void setPolicyuri(final String policyuri)
	{
		this.policyuri = policyuri;
	}

	/**
	 * @return the tosuri
	 */
	public String getTosuri()
	{
		return tosuri;
	}

	/**
	 * @param tosuri
	 *           the tosuri to set
	 */
	public void setTosuri(final String tosuri)
	{
		this.tosuri = tosuri;
	}

	/**
	 * @return the jwksuri
	 */
	public String getJwksuri()
	{
		return jwksuri;
	}

	/**
	 * @param jwksuri
	 *           the jwksuri to set
	 */
	public void setJwksuri(final String jwksuri)
	{
		this.jwksuri = jwksuri;
	}

	/**
	 * @return the jwks
	 */
	public String getJwks()
	{
		return jwks;
	}

	/**
	 * @param jwks
	 *           the jwks to set
	 */
	public void setJwks(final String jwks)
	{
		this.jwks = jwks;
	}

	/**
	 * @return the sectoridentifieruri
	 */
	public String getSectoridentifieruri()
	{
		return sectoridentifieruri;
	}

	/**
	 * @param sectoridentifieruri
	 *           the sectoridentifieruri to set
	 */
	public void setSectoridentifieruri(final String sectoridentifieruri)
	{
		this.sectoridentifieruri = sectoridentifieruri;
	}

	/**
	 * @return the subjecttype
	 */
	public String getSubjecttype()
	{
		return subjecttype;
	}

	/**
	 * @param subjecttype
	 *           the subjecttype to set
	 */
	public void setSubjecttype(final String subjecttype)
	{
		this.subjecttype = subjecttype;
	}

	/**
	 * @return the idtokensignedresponsealg
	 */
	public String getIdtokensignedresponsealg()
	{
		return idtokensignedresponsealg;
	}

	/**
	 * @param idtokensignedresponsealg
	 *           the idtokensignedresponsealg to set
	 */
	public void setIdtokensignedresponsealg(final String idtokensignedresponsealg)
	{
		this.idtokensignedresponsealg = idtokensignedresponsealg;
	}

	/**
	 * @return the idtokenencryptedresponsealg
	 */
	public String getIdtokenencryptedresponsealg()
	{
		return idtokenencryptedresponsealg;
	}

	/**
	 * @param idtokenencryptedresponsealg
	 *           the idtokenencryptedresponsealg to set
	 */
	public void setIdtokenencryptedresponsealg(final String idtokenencryptedresponsealg)
	{
		this.idtokenencryptedresponsealg = idtokenencryptedresponsealg;
	}

	/**
	 * @return the idtokenencryptedresponseenc
	 */
	public String getIdtokenencryptedresponseenc()
	{
		return idtokenencryptedresponseenc;
	}

	/**
	 * @param idtokenencryptedresponseenc
	 *           the idtokenencryptedresponseenc to set
	 */
	public void setIdtokenencryptedresponseenc(final String idtokenencryptedresponseenc)
	{
		this.idtokenencryptedresponseenc = idtokenencryptedresponseenc;
	}

	/**
	 * @return the userinfosignedresponsealg
	 */
	public String getUserinfosignedresponsealg()
	{
		return userinfosignedresponsealg;
	}

	/**
	 * @param userinfosignedresponsealg
	 *           the userinfosignedresponsealg to set
	 */
	public void setUserinfosignedresponsealg(final String userinfosignedresponsealg)
	{
		this.userinfosignedresponsealg = userinfosignedresponsealg;
	}

	/**
	 * @return the userinfoencryptedresponsealg
	 */
	public String getUserinfoencryptedresponsealg()
	{
		return userinfoencryptedresponsealg;
	}

	/**
	 * @param userinfoencryptedresponsealg
	 *           the userinfoencryptedresponsealg to set
	 */
	public void setUserinfoencryptedresponsealg(final String userinfoencryptedresponsealg)
	{
		this.userinfoencryptedresponsealg = userinfoencryptedresponsealg;
	}

	/**
	 * @return the userinfoencryptedresponseenc
	 */
	public String getUserinfoencryptedresponseenc()
	{
		return userinfoencryptedresponseenc;
	}

	/**
	 * @param userinfoencryptedresponseenc
	 *           the userinfoencryptedresponseenc to set
	 */
	public void setUserinfoencryptedresponseenc(final String userinfoencryptedresponseenc)
	{
		this.userinfoencryptedresponseenc = userinfoencryptedresponseenc;
	}

	/**
	 * @return the requestobjectsigningalg
	 */
	public String getRequestobjectsigningalg()
	{
		return requestobjectsigningalg;
	}

	/**
	 * @param requestobjectsigningalg
	 *           the requestobjectsigningalg to set
	 */
	public void setRequestobjectsigningalg(final String requestobjectsigningalg)
	{
		this.requestobjectsigningalg = requestobjectsigningalg;
	}

	/**
	 * @return the requestobjectencryptionalg
	 */
	public String getRequestobjectencryptionalg()
	{
		return requestobjectencryptionalg;
	}

	/**
	 * @param requestobjectencryptionalg
	 *           the requestobjectencryptionalg to set
	 */
	public void setRequestobjectencryptionalg(final String requestobjectencryptionalg)
	{
		this.requestobjectencryptionalg = requestobjectencryptionalg;
	}

	/**
	 * @return the requestobjectencryptionenc
	 */
	public String getRequestobjectencryptionenc()
	{
		return requestobjectencryptionenc;
	}

	/**
	 * @param requestobjectencryptionenc
	 *           the requestobjectencryptionenc to set
	 */
	public void setRequestobjectencryptionenc(final String requestobjectencryptionenc)
	{
		this.requestobjectencryptionenc = requestobjectencryptionenc;
	}

	/**
	 * @return the tokenendpoIntegerauthmethod
	 */
	public String getTokenendpoIntegerauthmethod()
	{
		return tokenendpoIntegerauthmethod;
	}

	/**
	 * @param tokenendpoIntegerauthmethod
	 *           the tokenendpoIntegerauthmethod to set
	 */
	public void setTokenendpoIntegerauthmethod(final String tokenendpoIntegerauthmethod)
	{
		this.tokenendpoIntegerauthmethod = tokenendpoIntegerauthmethod;
	}

	/**
	 * @return the tokenendpoIntegerauthSigningalg
	 */
	public String getTokenendpoIntegerauthSigningalg()
	{
		return tokenendpoIntegerauthSigningalg;
	}

	/**
	 * @param tokenendpoIntegerauthSigningalg
	 *           the tokenendpoIntegerauthSigningalg to set
	 */
	public void setTokenendpoIntegerauthSigningalg(final String tokenendpoIntegerauthSigningalg)
	{
		this.tokenendpoIntegerauthSigningalg = tokenendpoIntegerauthSigningalg;
	}

	/**
	 * @return the defaultmaxage
	 */
	public Integer getDefaultmaxage()
	{
		return defaultmaxage;
	}

	/**
	 * @param defaultmaxage
	 *           the defaultmaxage to set
	 */
	public void setDefaultmaxage(final Integer defaultmaxage)
	{
		this.defaultmaxage = defaultmaxage;
	}

	/**
	 * @return the requireauthtime
	 */
	public Boolean getRequireauthtime()
	{
		return requireauthtime;
	}

	/**
	 * @param requireauthtime
	 *           the requireauthtime to set
	 */
	public void setRequireauthtime(final Boolean requireauthtime)
	{
		this.requireauthtime = requireauthtime;
	}

	/**
	 * @return the initiateloginuri
	 */
	public String getInitiateloginuri()
	{
		return initiateloginuri;
	}

	/**
	 * @param initiateloginuri
	 *           the initiateloginuri to set
	 */
	public void setInitiateloginuri(final String initiateloginuri)
	{
		this.initiateloginuri = initiateloginuri;
	}

	/**
	 * @return the clientactive
	 */
	public Boolean getClientactive()
	{
		return clientactive;
	}

	/**
	 * @param clientactive
	 *           the clientactive to set
	 */
	public void setClientactive(final Boolean clientactive)
	{
		this.clientactive = clientactive;
	}

	/**
	 * @return the redirecturi
	 */
	public String[] getRedirecturi()
	{
		return redirecturi;
	}

	/**
	 * @param redirecturi
	 *           the redirecturi to set
	 */
	public void setRedirecturi(final String[] redirecturi)
	{
		this.redirecturi = redirecturi;
	}

	/**
	 * @return the responsetype
	 */
	public String[] getResponsetype()
	{
		return responsetype;
	}

	/**
	 * @param responsetype
	 *           the responsetype to set
	 */
	public void setResponsetype(final String[] responsetype)
	{
		this.responsetype = responsetype;
	}

	/**
	 * @return the granttype
	 */
	public String[] getGranttype()
	{
		return granttype;
	}

	/**
	 * @param granttype
	 *           the granttype to set
	 */
	public void setGranttype(final String[] granttype)
	{
		this.granttype = granttype;
	}

	/**
	 * @return the contact
	 */
	public String getContact()
	{
		return contact;
	}

	/**
	 * @param contact
	 *           the contact to set
	 */
	public void setContact(final String contact)
	{
		this.contact = contact;
	}

	/**
	 * @return the defaultacrvalue
	 */
	public String getDefaultacrvalue()
	{
		return defaultacrvalue;
	}

	/**
	 * @param defaultacrvalue
	 *           the defaultacrvalue to set
	 */
	public void setDefaultacrvalue(final String defaultacrvalue)
	{
		this.defaultacrvalue = defaultacrvalue;
	}

	/**
	 * @return the requesturi
	 */
	public String getRequesturi()
	{
		return requesturi;
	}

	/**
	 * @param requesturi
	 *           the requesturi to set
	 */
	public void setRequesturi(final String requesturi)
	{
		this.requesturi = requesturi;
	}

	/**
	 * @return the device
	 */
	public String[] getDevice()
	{
		return device;
	}

	/**
	 * @param device
	 *           the device to set
	 */
	public void setDevice(final String[] device)
	{
		this.device = device;
	}

	/**
	 * @return the clientsecret
	 */
	public String getClientsecret()
	{
		return clientsecret;
	}

	/**
	 * @param clientsecret
	 *           the clientsecret to set
	 */
	public void setClientsecret(final String clientsecret)
	{
		this.clientsecret = clientsecret;
	}

}
