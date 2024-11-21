/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.web.payment.forms;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;



public class SopPaymentDetailsForm
{
	private String amount;
	private String billTo_city; // NOSONAR
	private String billTo_country; // NOSONAR
	private String billTo_customerID; // NOSONAR
	private String billTo_email; // NOSONAR
	private String billTo_firstName; // NOSONAR
	private String billTo_lastName; // NOSONAR
	private String billTo_phoneNumber; // NOSONAR
	private String billTo_postalCode; // NOSONAR
	private String billTo_titleCode; // NOSONAR
	private String billTo_state; // NOSONAR
	private String billTo_street1; // NOSONAR
	private String billTo_street2; // NOSONAR
	private String card_accountNumber; // NOSONAR
	private String card_cardType; // NOSONAR
	private String card_startMonth; // NOSONAR
	private String card_startYear; // NOSONAR
	private String card_issueNumber; // NOSONAR
	private String card_cvNumber; // NOSONAR
	private String card_expirationMonth; // NOSONAR
	private String card_expirationYear; // NOSONAR
	private String card_tokenCard; // NOSONAR
	private String card_holderName; // NOSONAR
	private String card_holderLastName; // NOSONAR
	private String card_url3ds; // NOSONAR
	private String card_bank; // NOSONAR
	private String card_company; // NOSONAR
	private String card_zipCode; // NOSONAR
	private String card_paymentType; // NOSONAR
	private String card_msi; // NOSONAR
	private String card_cvn; // NOSONAR
	private Boolean card_saved; // NOSONAR
	private Boolean card_creditCard; // NOSONAR
	private String card_paymentReferenceName; // NOSONAR
	private String comments;
	private String currency;
	private String shipTo_city; // NOSONAR
	private String shipTo_country; // NOSONAR
	private String shipTo_firstName; // NOSONAR
	private String shipTo_lastName; // NOSONAR
	private String shipTo_phoneNumber; // NOSONAR
	private String shipTo_postalCode; // NOSONAR
	private String shipTo_shippingMethod; // NOSONAR
	private String shipTo_state; // NOSONAR
	private String shipTo_street1; // NOSONAR
	private String shipTo_street2; // NOSONAR
	private String taxAmount;
	private boolean savePaymentInfo;

	private Map parameters;

	public String getCard_url3ds() {
		return card_url3ds;
	}

	public void setCard_url3ds(String card_url3ds) {
		this.card_url3ds = card_url3ds;
	}

	public String getCard_bank() {
		return card_bank;
	}

	public void setCard_bank(String card_bank) {
		this.card_bank = card_bank;
	}

	public String getCard_company() {
		return card_company;
	}

	public void setCard_company(String card_company) {
		this.card_company = card_company;
	}

	public Boolean getCard_creditCard() {
		return card_creditCard;
	}

	public void setCard_creditCard(Boolean card_creditCard) {
		this.card_creditCard = card_creditCard;
	}

	public String getCard_holderLastName() {
		return card_holderLastName;
	}

	public void setCard_holderLastName(String card_holderLastName) {
		this.card_holderLastName = card_holderLastName;
	}

	public Boolean getCard_saved() {
		return card_saved;
	}

	public void setCard_saved(Boolean card_saved) {
		this.card_saved = card_saved;
	}

	public String getCard_cvn() {
		return card_cvn;
	}

	public void setCard_cvn(String card_cvn) {
		this.card_cvn = card_cvn;
	}

	public String getAmount()
	{
		return amount;
	}

	public void setAmount(final String amount)
	{
		this.amount = amount;
	}

	public String getBillTo_city() // NOSONAR
	{
		return billTo_city;
	}

	public void setBillTo_city(final String billTo_city) // NOSONAR
	{
		this.billTo_city = billTo_city;
	}

	public String getBillTo_country() // NOSONAR
	{
		return billTo_country;
	}

	public void setBillTo_country(final String billTo_country) // NOSONAR
	{
		this.billTo_country = billTo_country;
	}

	public String getCard_msi() {
		return card_msi;
	}

	public void setCard_msi(String card_msi) {
		this.card_msi = card_msi;
	}

	public String getBillTo_customerID() // NOSONAR
	{
		return billTo_customerID;
	}

	public void setBillTo_customerID(final String billTo_customerID) // NOSONAR
	{
		this.billTo_customerID = billTo_customerID;
	}

	public String getBillTo_email() // NOSONAR
	{
		return billTo_email;
	}

	public void setBillTo_email(final String billTo_email) // NOSONAR
	{
		this.billTo_email = billTo_email;
	}

	public String getBillTo_firstName() // NOSONAR
	{
		return billTo_firstName;
	}

	public void setBillTo_firstName(final String billTo_firstName) // NOSONAR
	{
		this.billTo_firstName = billTo_firstName;
	}

	public String getBillTo_lastName() // NOSONAR
	{
		return billTo_lastName;
	}

	public void setBillTo_lastName(final String billTo_lastName) // NOSONAR
	{
		this.billTo_lastName = billTo_lastName;
	}

	public String getBillTo_phoneNumber() // NOSONAR
	{
		return billTo_phoneNumber;
	}

	public void setBillTo_phoneNumber(final String billTo_phoneNumber) // NOSONAR
	{
		this.billTo_phoneNumber = billTo_phoneNumber;
	}

	public String getBillTo_postalCode() // NOSONAR
	{
		return billTo_postalCode;
	}

	public void setBillTo_postalCode(final String billTo_postalCode) // NOSONAR
	{
		this.billTo_postalCode = billTo_postalCode;
	}

	public String getBillTo_titleCode() // NOSONAR
	{
		return billTo_titleCode;
	}

	public void setBillTo_titleCode(final String billTo_titleCode) // NOSONAR
	{
		this.billTo_titleCode = billTo_titleCode;
	}

	public String getBillTo_state() // NOSONAR
	{
		return billTo_state;
	}

	public void setBillTo_state(final String billTo_state) // NOSONAR
	{
		this.billTo_state = billTo_state;
	}

	public String getBillTo_street1() // NOSONAR
	{
		return billTo_street1;
	}

	public void setBillTo_street1(final String billTo_street1) // NOSONAR
	{
		this.billTo_street1 = billTo_street1;
	}

	public String getBillTo_street2() // NOSONAR
	{
		return billTo_street2;
	}

	public void setBillTo_street2(final String billTo_street2) // NOSONAR
	{
		this.billTo_street2 = billTo_street2;
	}

	//	@NotNull(message = "{payment.cardNumber.invalid}")
//	@Size(min = 16, max = 16, message = "{payment.cardNumber.invalid}")
//	@Pattern(regexp = "(^$|^?\\d*$)", message = "{payment.cardNumber.invalid}")
	public String getCard_accountNumber() // NOSONAR
	{
		return card_accountNumber;
	}

	public void setCard_accountNumber(final String card_accountNumber) // NOSONAR
	{
		this.card_accountNumber = card_accountNumber;
	}

	public String getCard_startMonth() // NOSONAR
	{
		return card_startMonth;
	}

	public void setCard_startMonth(final String card_startMonth) // NOSONAR
	{
		this.card_startMonth = card_startMonth;
	}

	public String getCard_startYear() // NOSONAR
	{
		return card_startYear;
	}

	public void setCard_startYear(final String card_startYear) // NOSONAR
	{
		this.card_startYear = card_startYear;
	}

	@Pattern(regexp = "(^$|^?\\d*$)", message = "{payment.issueNumber.invalid}")
	@Size(min = 0, max = 16, message = "{payment.issueNumber.invalid}")
	public String getCard_issueNumber() // NOSONAR
	{
		return card_issueNumber;
	}

	public void setCard_issueNumber(final String card_issueNumber) // NOSONAR
	{
		this.card_issueNumber = card_issueNumber;
	}

//	@NotNull(message = "{payment.cardType.invalid}")
//	@Size(min = 1, max = 255, message = "{payment.cardType.invalid}")
	public String getCard_cardType() // NOSONAR
	{
		return card_cardType;
	}

	public void setCard_cardType(final String card_cardType) // NOSONAR
	{
		this.card_cardType = card_cardType;
	}

	//	@NotNull()
//	@Size(min = 3, max = 4)
	public String getCard_cvNumber() // NOSONAR
	{
		return card_cvNumber;
	}

	public void setCard_cvNumber(final String card_cvNumber) // NOSONAR
	{
		this.card_cvNumber = card_cvNumber;
	}

	//	@NotNull(message = "{payment.expiryMonth.invalid}")
//	@Size(min = 1, max = 2, message = "{payment.expiryMonth.invalid}")
	public String getCard_expirationMonth() // NOSONAR
	{
		return card_expirationMonth;
	}

	public void setCard_expirationMonth(final String card_expirationMonth) // NOSONAR
	{
		this.card_expirationMonth = card_expirationMonth;
	}

	//	@NotNull(message = "{payment.expiryYear.invalid}")
//	@Size(min = 2, max = 4, message = "{payment.expiryYear.invalid}")
	public String getCard_expirationYear() // NOSONAR
	{
		return card_expirationYear;
	}

	public void setCard_expirationYear(final String card_expirationYear) // NOSONAR
	{
		this.card_expirationYear = card_expirationYear;
	}

	public String getComments()
	{
		return comments;
	}

	public void setComments(final String comments)
	{
		this.comments = comments;
	}

	public String getCurrency()
	{
		return currency;
	}

	public void setCurrency(final String currency)
	{
		this.currency = currency;
	}

	public String getShipTo_city() // NOSONAR
	{
		return shipTo_city;
	}

	public void setShipTo_city(final String shipTo_city) // NOSONAR
	{
		this.shipTo_city = shipTo_city;
	}

	public String getShipTo_country() // NOSONAR
	{
		return shipTo_country;
	}

	public void setShipTo_country(final String shipTo_country) // NOSONAR
	{
		this.shipTo_country = shipTo_country;
	}

	public String getShipTo_firstName() // NOSONAR
	{
		return shipTo_firstName;
	}

	public void setShipTo_firstName(final String shipTo_firstName) // NOSONAR
	{
		this.shipTo_firstName = shipTo_firstName;
	}

	public String getShipTo_lastName() // NOSONAR
	{
		return shipTo_lastName;
	}

	public void setShipTo_lastName(final String shipTo_lastName) // NOSONAR
	{
		this.shipTo_lastName = shipTo_lastName;
	}

	public String getShipTo_phoneNumber() // NOSONAR
	{
		return shipTo_phoneNumber;
	}

	public void setShipTo_phoneNumber(final String shipTo_phoneNumber) // NOSONAR
	{
		this.shipTo_phoneNumber = shipTo_phoneNumber;
	}

	public String getShipTo_postalCode() // NOSONAR
	{
		return shipTo_postalCode;
	}

	public void setShipTo_postalCode(final String shipTo_postalCode) // NOSONAR
	{
		this.shipTo_postalCode = shipTo_postalCode;
	}

	public String getShipTo_shippingMethod() // NOSONAR
	{
		return shipTo_shippingMethod;
	}

	public void setShipTo_shippingMethod(final String shipTo_shippingMethod) // NOSONAR
	{
		this.shipTo_shippingMethod = shipTo_shippingMethod;
	}

	public String getShipTo_state() // NOSONAR
	{
		return shipTo_state;
	}

	public void setShipTo_state(final String shipTo_state) // NOSONAR
	{
		this.shipTo_state = shipTo_state;
	}

	public String getShipTo_street1() // NOSONAR
	{
		return shipTo_street1;
	}

	public void setShipTo_street1(final String shipTo_street1) // NOSONAR
	{
		this.shipTo_street1 = shipTo_street1;
	}

	public String getShipTo_street2() // NOSONAR
	{
		return shipTo_street2;
	}

	public void setShipTo_street2(final String shipTo_street2) // NOSONAR
	{
		this.shipTo_street2 = shipTo_street2;
	}

	public String getCard_tokenCard() {
		return card_tokenCard;
	}

	public void setCard_tokenCard(String card_tokenCard) {
		this.card_tokenCard = card_tokenCard;
	}

	public String getCard_holderName() {
		return card_holderName;
	}

	public void setCard_holderName(String card_holderName) {
		this.card_holderName = card_holderName;
	}

	public String getCard_zipCode() {
		return card_zipCode;
	}

	public void setCard_zipCode(String card_zipCode) {
		this.card_zipCode = card_zipCode;
	}

	public String getCard_paymentType() {
		return card_paymentType;
	}

	public void setCard_paymentType(String card_paymentType) {
		this.card_paymentType = card_paymentType;
	}

	public String getTaxAmount()
	{
		return taxAmount;
	}

	public void setTaxAmount(final String taxAmount)
	{
		this.taxAmount = taxAmount;
	}

	public boolean isSavePaymentInfo()
	{
		return savePaymentInfo;
	}

	public void setSavePaymentInfo(final boolean savePaymentInfo)
	{
		this.savePaymentInfo = savePaymentInfo;
	}

	public Map getParameters()
	{
		return parameters;
	}

	public void setParameters(final Map parameters)
	{
		this.parameters = parameters;
	}

	public String getCard_paymentReferenceName() {
		return card_paymentReferenceName;
	}

	public void setCard_paymentReferenceName(String card_paymentReferenceName) {
		this.card_paymentReferenceName = card_paymentReferenceName;
	}

	public Map getSubscriptionSignatureParams()
	{
		if (parameters != null)
		{
			final Map subscriptionSignatureParams = new HashMap();
			subscriptionSignatureParams.put("recurringSubscriptionInfo_amount", parameters.get("recurringSubscriptionInfo_amount"));
			subscriptionSignatureParams.put("recurringSubscriptionInfo_numberOfPayments",
					parameters.get("recurringSubscriptionInfo_numberOfPayments"));
			subscriptionSignatureParams.put("recurringSubscriptionInfo_frequency",
					parameters.get("recurringSubscriptionInfo_frequency"));
			subscriptionSignatureParams.put("recurringSubscriptionInfo_automaticRenew",
					parameters.get("recurringSubscriptionInfo_automaticRenew"));
			subscriptionSignatureParams.put("recurringSubscriptionInfo_startDate",
					parameters.get("recurringSubscriptionInfo_startDate"));
			subscriptionSignatureParams.put("recurringSubscriptionInfo_signaturePublic",
					parameters.get("recurringSubscriptionInfo_signaturePublic"));
			return subscriptionSignatureParams;
		}
		return null;
	}

	public Map getSignatureParams()
	{
		if (parameters != null)
		{
			final Map signatureParams = new HashMap();
			signatureParams.put("orderPage_signaturePublic", parameters.get("orderPage_signaturePublic"));
			signatureParams.put("merchantID", parameters.get("merchantID"));
			signatureParams.put("amount", parameters.get("amount"));
			signatureParams.put("currency", parameters.get("currency"));
			signatureParams.put("orderPage_timestamp", parameters.get("orderPage_timestamp"));
			signatureParams.put("orderPage_transactionType", parameters.get("orderPage_transactionType"));
			signatureParams.put("orderPage_version", parameters.get("orderPage_version"));
			signatureParams.put("orderPage_serialNumber", parameters.get("orderPage_serialNumber"));
			return signatureParams;
		}
		return null;
	}

	@Override
	public String toString() {
		return "SopPaymentDetailsForm{" +
				"amount='" + amount + '\'' +
				", billTo_city='" + billTo_city + '\'' +
				", billTo_country='" + billTo_country + '\'' +
				", billTo_customerID='" + billTo_customerID + '\'' +
				", billTo_email='" + billTo_email + '\'' +
				", billTo_firstName='" + billTo_firstName + '\'' +
				", billTo_lastName='" + billTo_lastName + '\'' +
				", billTo_phoneNumber='" + billTo_phoneNumber + '\'' +
				", billTo_postalCode='" + billTo_postalCode + '\'' +
				", billTo_titleCode='" + billTo_titleCode + '\'' +
				", billTo_state='" + billTo_state + '\'' +
				", billTo_street1='" + billTo_street1 + '\'' +
				", billTo_street2='" + billTo_street2 + '\'' +
				", card_accountNumber='" + card_accountNumber + '\'' +
				", card_cardType='" + card_cardType + '\'' +
				", card_startMonth='" + card_startMonth + '\'' +
				", card_startYear='" + card_startYear + '\'' +
				", card_issueNumber='" + card_issueNumber + '\'' +
				", card_cvNumber='" + card_cvNumber + '\'' +
				", card_expirationMonth='" + card_expirationMonth + '\'' +
				", card_expirationYear='" + card_expirationYear + '\'' +
				", card_tokenCard='" + card_tokenCard + '\'' +
				", card_holderName='" + card_holderName + '\'' +
				", card_zipCode='" + card_zipCode + '\'' +
				", card_paymentType='" + card_paymentType + '\'' +
				", card_msi='" + card_msi + '\'' +
				", card_cvn='" + card_cvn + '\'' +
				", card_saved=" + card_saved +
				", card_paymentReferenceName='" + card_paymentReferenceName + '\'' +
				", comments='" + comments + '\'' +
				", currency='" + currency + '\'' +
				", shipTo_city='" + shipTo_city + '\'' +
				", shipTo_country='" + shipTo_country + '\'' +
				", shipTo_firstName='" + shipTo_firstName + '\'' +
				", shipTo_lastName='" + shipTo_lastName + '\'' +
				", shipTo_phoneNumber='" + shipTo_phoneNumber + '\'' +
				", shipTo_postalCode='" + shipTo_postalCode + '\'' +
				", shipTo_shippingMethod='" + shipTo_shippingMethod + '\'' +
				", shipTo_state='" + shipTo_state + '\'' +
				", shipTo_street1='" + shipTo_street1 + '\'' +
				", shipTo_street2='" + shipTo_street2 + '\'' +
				", taxAmount='" + taxAmount + '\'' +
				", savePaymentInfo=" + savePaymentInfo +
				", parameters=" + parameters +
				'}';
	}
}
