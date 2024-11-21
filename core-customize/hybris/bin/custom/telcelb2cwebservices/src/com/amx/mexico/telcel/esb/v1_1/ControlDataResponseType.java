
package com.amx.mexico.telcel.esb.v1_1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>
 * Java class for ControlDataResponseType complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ControlDataResponseType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="messageUUID" type="{http://amx.com/mexico/telcel/esb/v1_1}MessageUUIDType"/&gt;
 *         &lt;element name="responseDate" type="{http://amx.com/mexico/telcel/esb/v1_1}DateTimeType"/&gt;
 *         &lt;element name="sendBy" type="{http://amx.com/mexico/telcel/esb/v1_1}SenderType"/&gt;
 *         &lt;element name="latency" type="{http://amx.com/mexico/telcel/esb/v1_1}LatencyType"/&gt;
 *         &lt;element name="version" type="{http://amx.com/mexico/telcel/esb/v1_1}VersionType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ControlDataResponseType", propOrder =
{ "messageUUID", "responseDate", "sendBy", "latency", "version" })
public class ControlDataResponseType
{

	@XmlElement(required = true)
	protected String messageUUID;
	@XmlElement(required = true)
	protected String responseDate;
	@XmlElement(required = true)
	protected String sendBy;
	protected int latency;
	@XmlElement(required = true)
	protected String version;

	/**
	 * Gets the value of the messageUUID property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getMessageUUID()
	{
		return messageUUID;
	}

	/**
	 * Sets the value of the messageUUID property.
	 * 
	 * @param value
	 *           allowed object is {@link String }
	 * 
	 */
	public void setMessageUUID(final String value)
	{
		this.messageUUID = value;
	}

	/**
	 * Gets the value of the responseDate property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getResponseDate()
	{
		return responseDate;
	}

	/**
	 * Sets the value of the responseDate property.
	 * 
	 * @param value
	 *           allowed object is {@link String }
	 * 
	 */
	public void setResponseDate(final String value)
	{
		this.responseDate = value;
	}

	/**
	 * Gets the value of the sendBy property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getSendBy()
	{
		return sendBy;
	}

	/**
	 * Sets the value of the sendBy property.
	 * 
	 * @param value
	 *           allowed object is {@link String }
	 * 
	 */
	public void setSendBy(final String value)
	{
		this.sendBy = value;
	}

	/**
	 * Gets the value of the latency property.
	 * 
	 */
	public int getLatency()
	{
		return latency;
	}

	/**
	 * Sets the value of the latency property.
	 * 
	 */
	public void setLatency(final int value)
	{
		this.latency = value;
	}

	/**
	 * Gets the value of the version property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getVersion()
	{
		return version;
	}

	/**
	 * Sets the value of the version property.
	 * 
	 * @param value
	 *           allowed object is {@link String }
	 * 
	 */
	public void setVersion(final String value)
	{
		this.version = value;
	}

}
