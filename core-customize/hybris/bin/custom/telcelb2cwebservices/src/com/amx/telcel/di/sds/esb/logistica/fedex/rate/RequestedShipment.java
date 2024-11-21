
package com.amx.telcel.di.sds.esb.logistica.fedex.rate;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for RequestedShipment complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RequestedShipment"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ShipTimestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="DropoffType" type="{http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate}DropoffType" minOccurs="0"/&gt;
 *         &lt;element name="PackagingType" type="{http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate}PackagingType" minOccurs="0"/&gt;
 *         &lt;element name="Shipper" type="{http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate}Party" minOccurs="0"/&gt;
 *         &lt;element name="Recipient" type="{http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate}Party" minOccurs="0"/&gt;
 *         &lt;element name="ShippingChargesPayment" type="{http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate}Payment" minOccurs="0"/&gt;
 *         &lt;element name="RateRequestTypes" type="{http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate}RateRequestType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="PackageCount" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/&gt;
 *         &lt;element name="RequestedPackageLineItems" type="{http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate}RequestedPackageLineItem" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequestedShipment", propOrder = {
    "shipTimestamp",
    "dropoffType",
    "packagingType",
    "shipper",
    "recipient",
    "shippingChargesPayment",
    "rateRequestTypes",
    "packageCount",
    "requestedPackageLineItems"
})
public class RequestedShipment {

    @XmlElement(name = "ShipTimestamp")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar shipTimestamp;
    @XmlElement(name = "DropoffType")
    @XmlSchemaType(name = "string")
    protected DropoffType dropoffType;
    @XmlElement(name = "PackagingType")
    @XmlSchemaType(name = "string")
    protected PackagingType packagingType;
    @XmlElement(name = "Shipper")
    protected Party shipper;
    @XmlElement(name = "Recipient")
    protected Party recipient;
    @XmlElement(name = "ShippingChargesPayment")
    protected Payment shippingChargesPayment;
    @XmlElement(name = "RateRequestTypes")
    @XmlSchemaType(name = "string")
    protected List<RateRequestType> rateRequestTypes;
    @XmlElement(name = "PackageCount")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger packageCount;
    @XmlElement(name = "RequestedPackageLineItems")
    protected List<RequestedPackageLineItem> requestedPackageLineItems;

    /**
     * Gets the value of the shipTimestamp property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getShipTimestamp() {
        return shipTimestamp;
    }

    /**
     * Sets the value of the shipTimestamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setShipTimestamp(XMLGregorianCalendar value) {
        this.shipTimestamp = value;
    }

    /**
     * Gets the value of the dropoffType property.
     * 
     * @return
     *     possible object is
     *     {@link DropoffType }
     *     
     */
    public DropoffType getDropoffType() {
        return dropoffType;
    }

    /**
     * Sets the value of the dropoffType property.
     * 
     * @param value
     *     allowed object is
     *     {@link DropoffType }
     *     
     */
    public void setDropoffType(DropoffType value) {
        this.dropoffType = value;
    }

    /**
     * Gets the value of the packagingType property.
     * 
     * @return
     *     possible object is
     *     {@link PackagingType }
     *     
     */
    public PackagingType getPackagingType() {
        return packagingType;
    }

    /**
     * Sets the value of the packagingType property.
     * 
     * @param value
     *     allowed object is
     *     {@link PackagingType }
     *     
     */
    public void setPackagingType(PackagingType value) {
        this.packagingType = value;
    }

    /**
     * Gets the value of the shipper property.
     * 
     * @return
     *     possible object is
     *     {@link Party }
     *     
     */
    public Party getShipper() {
        return shipper;
    }

    /**
     * Sets the value of the shipper property.
     * 
     * @param value
     *     allowed object is
     *     {@link Party }
     *     
     */
    public void setShipper(Party value) {
        this.shipper = value;
    }

    /**
     * Gets the value of the recipient property.
     * 
     * @return
     *     possible object is
     *     {@link Party }
     *     
     */
    public Party getRecipient() {
        return recipient;
    }

    /**
     * Sets the value of the recipient property.
     * 
     * @param value
     *     allowed object is
     *     {@link Party }
     *     
     */
    public void setRecipient(Party value) {
        this.recipient = value;
    }

    /**
     * Gets the value of the shippingChargesPayment property.
     * 
     * @return
     *     possible object is
     *     {@link Payment }
     *     
     */
    public Payment getShippingChargesPayment() {
        return shippingChargesPayment;
    }

    /**
     * Sets the value of the shippingChargesPayment property.
     * 
     * @param value
     *     allowed object is
     *     {@link Payment }
     *     
     */
    public void setShippingChargesPayment(Payment value) {
        this.shippingChargesPayment = value;
    }

    /**
     * Gets the value of the rateRequestTypes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rateRequestTypes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRateRequestTypes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RateRequestType }
     * 
     * 
     */
    public List<RateRequestType> getRateRequestTypes() {
        if (rateRequestTypes == null) {
            rateRequestTypes = new ArrayList<RateRequestType>();
        }
        return this.rateRequestTypes;
    }

    /**
     * Gets the value of the packageCount property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getPackageCount() {
        return packageCount;
    }

    /**
     * Sets the value of the packageCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setPackageCount(BigInteger value) {
        this.packageCount = value;
    }

    /**
     * Gets the value of the requestedPackageLineItems property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the requestedPackageLineItems property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRequestedPackageLineItems().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RequestedPackageLineItem }
     * 
     * 
     */
    public List<RequestedPackageLineItem> getRequestedPackageLineItems() {
        if (requestedPackageLineItems == null) {
            requestedPackageLineItems = new ArrayList<RequestedPackageLineItem>();
        }
        return this.requestedPackageLineItems;
    }

}
