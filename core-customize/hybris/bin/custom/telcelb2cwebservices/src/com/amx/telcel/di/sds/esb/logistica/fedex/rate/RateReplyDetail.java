
package com.amx.telcel.di.sds.esb.logistica.fedex.rate;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>
 * Java class for RateReplyDetail complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="RateReplyDetail"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ServiceType" type="{http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate}ServiceType" minOccurs="0"/&gt;
 *         &lt;element name="PackagingType" type="{http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate}PackagingType" minOccurs="0"/&gt;
 *         &lt;element name="DeliveryStation" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="DeliveryDayOfWeek" type="{http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate}DayOfWeekType" minOccurs="0"/&gt;
 *         &lt;element name="DeliveryTimestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="CommitDetails" type="{http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate}CommitDetail" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="DestinationAirportId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="IneligibleForMoneyBackGuarantee" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="OriginServiceArea" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="DestinationServiceArea" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="SignatureOption" type="{http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate}SignatureOptionType" minOccurs="0"/&gt;
 *         &lt;element name="ActualRateType" type="{http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate}ReturnedRateType" minOccurs="0"/&gt;
 *         &lt;element name="RatedShipmentDetails" type="{http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate}RatedShipmentDetail" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RateReplyDetail", propOrder =
{ "serviceType", "packagingType", "deliveryStation", "deliveryDayOfWeek", "deliveryTimestamp", "commitDetails",
		"destinationAirportId", "ineligibleForMoneyBackGuarantee", "originServiceArea", "destinationServiceArea", "signatureOption",
		"actualRateType", "ratedShipmentDetails" })
public class RateReplyDetail
{

	@XmlElement(name = "ServiceType")
	@XmlSchemaType(name = "string")
	protected ServiceType serviceType;
	@XmlElement(name = "PackagingType")
	@XmlSchemaType(name = "string")
	protected PackagingType packagingType;
	@XmlElement(name = "DeliveryStation")
	protected String deliveryStation;
	@XmlElement(name = "DeliveryDayOfWeek")
	@XmlSchemaType(name = "string")
	protected DayOfWeekType deliveryDayOfWeek;
	@XmlElement(name = "DeliveryTimestamp")
	@XmlSchemaType(name = "dateTime")
	protected String deliveryTimestamp;
	@XmlElement(name = "CommitDetails")
	protected List<CommitDetail> commitDetails;
	@XmlElement(name = "DestinationAirportId")
	protected String destinationAirportId;
	@XmlElement(name = "IneligibleForMoneyBackGuarantee")
	protected Boolean ineligibleForMoneyBackGuarantee;
	@XmlElement(name = "OriginServiceArea")
	protected String originServiceArea;
	@XmlElement(name = "DestinationServiceArea")
	protected String destinationServiceArea;
	@XmlElement(name = "SignatureOption")
	@XmlSchemaType(name = "string")
	protected SignatureOptionType signatureOption;
	@XmlElement(name = "ActualRateType")
	@XmlSchemaType(name = "string")
	protected ReturnedRateType actualRateType;
	@XmlElement(name = "RatedShipmentDetails")
	protected List<RatedShipmentDetail> ratedShipmentDetails;

	/**
	 * Gets the value of the serviceType property.
	 * 
	 * @return possible object is {@link ServiceType }
	 * 
	 */
	public ServiceType getServiceType()
	{
		return serviceType;
	}

	/**
	 * Sets the value of the serviceType property.
	 * 
	 * @param value
	 *           allowed object is {@link ServiceType }
	 * 
	 */
	public void setServiceType(final ServiceType value)
	{
		this.serviceType = value;
	}

	/**
	 * Gets the value of the packagingType property.
	 * 
	 * @return possible object is {@link PackagingType }
	 * 
	 */
	public PackagingType getPackagingType()
	{
		return packagingType;
	}

	/**
	 * Sets the value of the packagingType property.
	 * 
	 * @param value
	 *           allowed object is {@link PackagingType }
	 * 
	 */
	public void setPackagingType(final PackagingType value)
	{
		this.packagingType = value;
	}

	/**
	 * Gets the value of the deliveryStation property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getDeliveryStation()
	{
		return deliveryStation;
	}

	/**
	 * Sets the value of the deliveryStation property.
	 * 
	 * @param value
	 *           allowed object is {@link String }
	 * 
	 */
	public void setDeliveryStation(final String value)
	{
		this.deliveryStation = value;
	}

	/**
	 * Gets the value of the deliveryDayOfWeek property.
	 * 
	 * @return possible object is {@link DayOfWeekType }
	 * 
	 */
	public DayOfWeekType getDeliveryDayOfWeek()
	{
		return deliveryDayOfWeek;
	}

	/**
	 * Sets the value of the deliveryDayOfWeek property.
	 * 
	 * @param value
	 *           allowed object is {@link DayOfWeekType }
	 * 
	 */
	public void setDeliveryDayOfWeek(final DayOfWeekType value)
	{
		this.deliveryDayOfWeek = value;
	}

	/**
	 * Gets the value of the deliveryTimestamp property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getDeliveryTimestamp()
	{
		return deliveryTimestamp;
	}

	/**
	 * Sets the value of the deliveryTimestamp property.
	 * 
	 * @param value
	 *           allowed object is {@link String }
	 * 
	 */
	public void setDeliveryTimestamp(final String value)
	{
		this.deliveryTimestamp = value;
	}

	/**
	 * Gets the value of the commitDetails property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
	 * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
	 * the commitDetails property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 *
	 * <pre>
	 * getCommitDetails().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link CommitDetail }
	 * 
	 * 
	 */
	public List<CommitDetail> getCommitDetails()
	{
		if (commitDetails == null)
		{
			commitDetails = new ArrayList<CommitDetail>();
		}
		return this.commitDetails;
	}

	/**
	 * Gets the value of the destinationAirportId property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getDestinationAirportId()
	{
		return destinationAirportId;
	}

	/**
	 * Sets the value of the destinationAirportId property.
	 * 
	 * @param value
	 *           allowed object is {@link String }
	 * 
	 */
	public void setDestinationAirportId(final String value)
	{
		this.destinationAirportId = value;
	}

	/**
	 * Gets the value of the ineligibleForMoneyBackGuarantee property.
	 * 
	 * @return possible object is {@link Boolean }
	 * 
	 */
	public Boolean isIneligibleForMoneyBackGuarantee()
	{
		return ineligibleForMoneyBackGuarantee;
	}

	/**
	 * Sets the value of the ineligibleForMoneyBackGuarantee property.
	 * 
	 * @param value
	 *           allowed object is {@link Boolean }
	 * 
	 */
	public void setIneligibleForMoneyBackGuarantee(final Boolean value)
	{
		this.ineligibleForMoneyBackGuarantee = value;
	}

	/**
	 * Gets the value of the originServiceArea property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getOriginServiceArea()
	{
		return originServiceArea;
	}

	/**
	 * Sets the value of the originServiceArea property.
	 * 
	 * @param value
	 *           allowed object is {@link String }
	 * 
	 */
	public void setOriginServiceArea(final String value)
	{
		this.originServiceArea = value;
	}

	/**
	 * Gets the value of the destinationServiceArea property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getDestinationServiceArea()
	{
		return destinationServiceArea;
	}

	/**
	 * Sets the value of the destinationServiceArea property.
	 * 
	 * @param value
	 *           allowed object is {@link String }
	 * 
	 */
	public void setDestinationServiceArea(final String value)
	{
		this.destinationServiceArea = value;
	}

	/**
	 * Gets the value of the signatureOption property.
	 * 
	 * @return possible object is {@link SignatureOptionType }
	 * 
	 */
	public SignatureOptionType getSignatureOption()
	{
		return signatureOption;
	}

	/**
	 * Sets the value of the signatureOption property.
	 * 
	 * @param value
	 *           allowed object is {@link SignatureOptionType }
	 * 
	 */
	public void setSignatureOption(final SignatureOptionType value)
	{
		this.signatureOption = value;
	}

	/**
	 * Gets the value of the actualRateType property.
	 * 
	 * @return possible object is {@link ReturnedRateType }
	 * 
	 */
	public ReturnedRateType getActualRateType()
	{
		return actualRateType;
	}

	/**
	 * Sets the value of the actualRateType property.
	 * 
	 * @param value
	 *           allowed object is {@link ReturnedRateType }
	 * 
	 */
	public void setActualRateType(final ReturnedRateType value)
	{
		this.actualRateType = value;
	}

	/**
	 * Gets the value of the ratedShipmentDetails property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
	 * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
	 * the ratedShipmentDetails property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 *
	 * <pre>
	 * getRatedShipmentDetails().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link RatedShipmentDetail }
	 * 
	 * 
	 */
	public List<RatedShipmentDetail> getRatedShipmentDetails()
	{
		if (ratedShipmentDetails == null)
		{
			ratedShipmentDetails = new ArrayList<RatedShipmentDetail>();
		}
		return this.ratedShipmentDetails;
	}

}
