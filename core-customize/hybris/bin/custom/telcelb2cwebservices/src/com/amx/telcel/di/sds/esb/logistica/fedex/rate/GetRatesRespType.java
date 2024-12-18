
package com.amx.telcel.di.sds.esb.logistica.fedex.rate;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * The response to a RateRequest. The Notifications indicate whether the request was successful or not.
 *
 * <p>
 * Java class for GetRatesRespType complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="GetRatesRespType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="HighestSeverity" type="{http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate}NotificationSeverityType"/&gt;
 *         &lt;element name="Notifications" type="{http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate}Notification" maxOccurs="unbounded"/&gt;
 *         &lt;element name="TransactionDetail" type="{http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate}TransactionDetail" minOccurs="0"/&gt;
 *         &lt;element name="Version" type="{http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate}VersionId"/&gt;
 *         &lt;element name="RateReplyDetails" type="{http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate}RateReplyDetail" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetRatesRespType", propOrder =
{ "highestSeverity", "notifications", "transactionDetail", "version", "rateReplyDetails" })
public class GetRatesRespType
{

	@XmlElement(name = "HighestSeverity", required = true)
	@XmlSchemaType(name = "string")
	protected NotificationSeverityType highestSeverity;
	@XmlElement(name = "Notifications", required = true)
	protected List<Notification> notifications;
	@XmlElement(name = "TransactionDetail")
	protected TransactionDetail transactionDetail;
	@XmlElement(name = "Version", required = true)
	protected VersionId version;
	@XmlElement(name = "RateReplyDetails")
	protected List<RateReplyDetail> rateReplyDetails;

	/**
	 * Gets the value of the highestSeverity property.
	 * 
	 * @return possible object is {@link NotificationSeverityType }
	 * 
	 */
	public NotificationSeverityType getHighestSeverity()
	{
		return highestSeverity;
	}

	/**
	 * Sets the value of the highestSeverity property.
	 * 
	 * @param value
	 *           allowed object is {@link NotificationSeverityType }
	 * 
	 */
	public void setHighestSeverity(final NotificationSeverityType value)
	{
		this.highestSeverity = value;
	}

	/**
	 * Gets the value of the notifications property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
	 * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
	 * the notifications property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 *
	 * <pre>
	 * getNotifications().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link Notification }
	 * 
	 * 
	 */
	public List<Notification> getNotifications()
	{
		if (notifications == null)
		{
			notifications = new ArrayList<Notification>();
		}
		return this.notifications;
	}

	/**
	 * Gets the value of the transactionDetail property.
	 * 
	 * @return possible object is {@link TransactionDetail }
	 * 
	 */
	public TransactionDetail getTransactionDetail()
	{
		return transactionDetail;
	}

	/**
	 * Sets the value of the transactionDetail property.
	 * 
	 * @param value
	 *           allowed object is {@link TransactionDetail }
	 * 
	 */
	public void setTransactionDetail(final TransactionDetail value)
	{
		this.transactionDetail = value;
	}

	/**
	 * Gets the value of the version property.
	 * 
	 * @return possible object is {@link VersionId }
	 * 
	 */
	public VersionId getVersion()
	{
		return version;
	}

	/**
	 * Sets the value of the version property.
	 * 
	 * @param value
	 *           allowed object is {@link VersionId }
	 * 
	 */
	public void setVersion(final VersionId value)
	{
		this.version = value;
	}

	/**
	 * Gets the value of the rateReplyDetails property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
	 * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
	 * the rateReplyDetails property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 *
	 * <pre>
	 * getRateReplyDetails().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link RateReplyDetail }
	 * 
	 * 
	 */
	public List<RateReplyDetail> getRateReplyDetails()
	{
		if (rateReplyDetails == null)
		{
			rateReplyDetails = new ArrayList<RateReplyDetail>();
		}
		return this.rateReplyDetails;
	}

}
