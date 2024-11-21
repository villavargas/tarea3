package com.telcel.payment.commons.dto.webhook.response;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;


public class NotificationResponse implements Serializable
{

	private String orderId;
	private String commerceId;
	private String email;
	private String firstname;
	private String lastname;
	private String provider;
	private String paymentId;
	private String folio;
	private String transaction;
	private String authorization;
	private String membership;
	private BigDecimal amount;
	private String bank;
	private Integer msi;
	private String lastDigits;
	private String type;
	private String risk;
	private String clabe;
	private String reference;
	private String agreement;
	private String commerce;
	private String description;
	@JsonFormat(pattern = "yyyyMMdd HHmmss")
	private Date payDate;
	private String code;
	private String status;
	@JsonFormat(pattern = "yyyyMMdd HHmmss")
	private Date expiration;
	private Integer credit;

	/**
	 * @return the orderId
	 */
	public String getOrderId()
	{
		return orderId;
	}

	/**
	 * @param orderId
	 *           the orderId to set
	 */
	public void setOrderId(final String orderId)
	{
		this.orderId = orderId;
	}

	/**
	 * @return the commerceId
	 */
	public String getCommerceId()
	{
		return commerceId;
	}

	/**
	 * @param commerceId
	 *           the commerceId to set
	 */
	public void setCommerceId(final String commerceId)
	{
		this.commerceId = commerceId;
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
	 * @return the firstname
	 */
	public String getFirstname()
	{
		return firstname;
	}

	/**
	 * @param firstname
	 *           the firstname to set
	 */
	public void setFirstname(final String firstname)
	{
		this.firstname = firstname;
	}

	/**
	 * @return the lastname
	 */
	public String getLastname()
	{
		return lastname;
	}

	/**
	 * @param lastname
	 *           the lastname to set
	 */
	public void setLastname(final String lastname)
	{
		this.lastname = lastname;
	}

	/**
	 * @return the provider
	 */
	public String getProvider()
	{
		return provider;
	}

	/**
	 * @param provider
	 *           the provider to set
	 */
	public void setProvider(final String provider)
	{
		this.provider = provider;
	}

	/**
	 * @return the paymentId
	 */
	public String getPaymentId()
	{
		return paymentId;
	}

	/**
	 * @param paymentId
	 *           the paymentId to set
	 */
	public void setPaymentId(final String paymentId)
	{
		this.paymentId = paymentId;
	}

	/**
	 * @return the folio
	 */
	public String getFolio()
	{
		return folio;
	}

	/**
	 * @param folio
	 *           the folio to set
	 */
	public void setFolio(final String folio)
	{
		this.folio = folio;
	}

	/**
	 * @return the transaction
	 */
	public String getTransaction()
	{
		return transaction;
	}

	/**
	 * @param transaction
	 *           the transaction to set
	 */
	public void setTransaction(final String transaction)
	{
		this.transaction = transaction;
	}

	/**
	 * @return the authorization
	 */
	public String getAuthorization()
	{
		return authorization;
	}

	/**
	 * @param authorization
	 *           the authorization to set
	 */
	public void setAuthorization(final String authorization)
	{
		this.authorization = authorization;
	}

	/**
	 * @return the membership
	 */
	public String getMembership()
	{
		return membership;
	}

	/**
	 * @param membership
	 *           the membership to set
	 */
	public void setMembership(final String membership)
	{
		this.membership = membership;
	}

	/**
	 * @return the amount
	 */
	public BigDecimal getAmount()
	{
		return amount;
	}

	/**
	 * @param amount
	 *           the amount to set
	 */
	public void setAmount(final BigDecimal amount)
	{
		this.amount = amount;
	}

	/**
	 * @return the bank
	 */
	public String getBank()
	{
		return bank;
	}

	/**
	 * @param bank
	 *           the bank to set
	 */
	public void setBank(final String bank)
	{
		this.bank = bank;
	}

	/**
	 * @return the msi
	 */
	public Integer getMsi()
	{
		return msi;
	}

	/**
	 * @param msi
	 *           the msi to set
	 */
	public void setMsi(final Integer msi)
	{
		this.msi = msi;
	}

	/**
	 * @return the lastDigits
	 */
	public String getLastDigits()
	{
		return lastDigits;
	}

	/**
	 * @param lastDigits
	 *           the lastDigits to set
	 */
	public void setLastDigits(final String lastDigits)
	{
		this.lastDigits = lastDigits;
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
	 * @return the risk
	 */
	public String getRisk()
	{
		return risk;
	}

	/**
	 * @param risk
	 *           the risk to set
	 */
	public void setRisk(final String risk)
	{
		this.risk = risk;
	}

	/**
	 * @return the clabe
	 */
	public String getClabe()
	{
		return clabe;
	}

	/**
	 * @param clabe
	 *           the clabe to set
	 */
	public void setClabe(final String clabe)
	{
		this.clabe = clabe;
	}

	/**
	 * @return the reference
	 */
	public String getReference()
	{
		return reference;
	}

	/**
	 * @param reference
	 *           the reference to set
	 */
	public void setReference(final String reference)
	{
		this.reference = reference;
	}

	/**
	 * @return the agreement
	 */
	public String getAgreement()
	{
		return agreement;
	}

	/**
	 * @param agreement
	 *           the agreement to set
	 */
	public void setAgreement(final String agreement)
	{
		this.agreement = agreement;
	}

	/**
	 * @return the commerce
	 */
	public String getCommerce()
	{
		return commerce;
	}

	/**
	 * @param commerce
	 *           the commerce to set
	 */
	public void setCommerce(final String commerce)
	{
		this.commerce = commerce;
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
	 * @return the payDate
	 */
	public Date getPayDate()
	{
		return payDate;
	}

	/**
	 * @param payDate
	 *           the payDate to set
	 */
	public void setPayDate(final Date payDate)
	{
		this.payDate = payDate;
	}

	/**
	 * @return the code
	 */
	public String getCode()
	{
		return code;
	}

	/**
	 * @param code
	 *           the code to set
	 */
	public void setCode(final String code)
	{
		this.code = code;
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
	 * @return the expiration
	 */
	public Date getExpiration()
	{
		return expiration;
	}

	/**
	 * @param expiration
	 *           the expiration to set
	 */
	public void setExpiration(final Date expiration)
	{
		this.expiration = expiration;
	}

	/**
	 * @return the credit
	 */
	public Integer getCredit()
	{
		return credit;
	}

	/**
	 * @param credit
	 *           the credit to set
	 */
	public void setCredit(final Integer credit)
	{
		this.credit = credit;
	}

}
