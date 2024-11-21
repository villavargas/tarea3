
package com.amx.mexico.telcel.esb.v1_0_2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ResponseOperationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ResponseOperationType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="XMLMessage" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="DetailResponse" type="{http://amx.com/mexico/telcel/esb/v1_0_2}DetailResponseType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResponseOperationType", propOrder = {
    "xmlMessage",
    "detailResponse"
})
public class ResponseOperationType {

    @XmlElement(name = "XMLMessage", required = true)
    protected String xmlMessage;
    @XmlElement(name = "DetailResponse", required = true)
    protected DetailResponseType detailResponse;

    /**
     * Gets the value of the xmlMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXMLMessage() {
        return xmlMessage;
    }

    /**
     * Sets the value of the xmlMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXMLMessage(String value) {
        this.xmlMessage = value;
    }

    /**
     * Gets the value of the detailResponse property.
     * 
     * @return
     *     possible object is
     *     {@link DetailResponseType }
     *     
     */
    public DetailResponseType getDetailResponse() {
        return detailResponse;
    }

    /**
     * Sets the value of the detailResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link DetailResponseType }
     *     
     */
    public void setDetailResponse(DetailResponseType value) {
        this.detailResponse = value;
    }

}
