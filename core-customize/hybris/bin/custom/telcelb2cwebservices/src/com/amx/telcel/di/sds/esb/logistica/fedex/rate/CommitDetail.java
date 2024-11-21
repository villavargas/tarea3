
package com.amx.telcel.di.sds.esb.logistica.fedex.rate;

import java.math.BigInteger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * Information about the transit time and delivery commitment date and time.
 *
 * <p>
 * Java class for CommitDetail complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="CommitDetail"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ServiceType" type="{http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate}ServiceType" minOccurs="0"/&gt;
 *         &lt;element name="CommitTimestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="DayOfWeek" type="{http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate}DayOfWeekType" minOccurs="0"/&gt;
 *         &lt;element name="DestinationServiceArea" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="BrokerToDestinationDays" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/&gt;
 *         &lt;element name="DocumentContent" type="{http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate}InternationalDocumentContentType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CommitDetail", propOrder =
{ "serviceType", "commitTimestamp", "dayOfWeek", "destinationServiceArea", "brokerToDestinationDays", "documentContent" })
public class CommitDetail
{

	@XmlElement(name = "ServiceType")
	@XmlSchemaType(name = "string")
	protected ServiceType serviceType;
	@XmlElement(name = "CommitTimestamp")
	@XmlSchemaType(name = "string")
	protected String commitTimestamp;
	@XmlElement(name = "DayOfWeek")
	@XmlSchemaType(name = "string")
	protected DayOfWeekType dayOfWeek;
	@XmlElement(name = "DestinationServiceArea")
	protected String destinationServiceArea;
	@XmlElement(name = "BrokerToDestinationDays")
	@XmlSchemaType(name = "nonNegativeInteger")
	protected BigInteger brokerToDestinationDays;
	@XmlElement(name = "DocumentContent")
	@XmlSchemaType(name = "string")
	protected InternationalDocumentContentType documentContent;

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
	 * Gets the value of the commitTimestamp property.
	 *
	 * @return possible object is {@link String }
	 *
	 */

	public String getCommitTimestamp()
	{
		return commitTimestamp;
	}

	/**
	 * Sets the value of the commitTimestamp property.
	 *
	 * @param value
	 *           allowed object is {@link String }
	 *
	 */

	public void setCommitTimestamp(final String value)
	{
		this.commitTimestamp = value;
	}

	/**
	 * Gets the value of the dayOfWeek property.
	 *
	 * @return possible object is {@link DayOfWeekType }
	 *
	 */
	public DayOfWeekType getDayOfWeek()
	{
		return dayOfWeek;
	}

	/**
	 * Sets the value of the dayOfWeek property.
	 *
	 * @param value
	 *           allowed object is {@link DayOfWeekType }
	 *
	 */
	public void setDayOfWeek(final DayOfWeekType value)
	{
		this.dayOfWeek = value;
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
	 * Gets the value of the brokerToDestinationDays property.
	 *
	 * @return possible object is {@link BigInteger }
	 *
	 */
	public BigInteger getBrokerToDestinationDays()
	{
		return brokerToDestinationDays;
	}

	/**
	 * Sets the value of the brokerToDestinationDays property.
	 *
	 * @param value
	 *           allowed object is {@link BigInteger }
	 *
	 */
	public void setBrokerToDestinationDays(final BigInteger value)
	{
		this.brokerToDestinationDays = value;
	}

	/**
	 * Gets the value of the documentContent property.
	 *
	 * @return possible object is {@link InternationalDocumentContentType }
	 *
	 */
	public InternationalDocumentContentType getDocumentContent()
	{
		return documentContent;
	}

	/**
	 * Sets the value of the documentContent property.
	 *
	 * @param value
	 *           allowed object is {@link InternationalDocumentContentType }
	 *
	 */
	public void setDocumentContent(final InternationalDocumentContentType value)
	{
		this.documentContent = value;
	}

}
