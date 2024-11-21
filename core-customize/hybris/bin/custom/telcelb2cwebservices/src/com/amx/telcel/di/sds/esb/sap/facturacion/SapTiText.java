
package com.amx.telcel.di.sds.esb.sap.facturacion;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SapTiText complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SapTiText"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ITM_NUMBER"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="6"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="ZOBSERV"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="132"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SapTiText", propOrder = {
    "itmnumber",
    "zobserv"
})
public class SapTiText {

    @XmlElement(name = "ITM_NUMBER", required = true, nillable = true)
    protected String itmnumber;
    @XmlElement(name = "ZOBSERV", required = true, nillable = true)
    protected String zobserv;

    /**
     * Gets the value of the itmnumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getITMNUMBER() {
        return itmnumber;
    }

    /**
     * Sets the value of the itmnumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setITMNUMBER(String value) {
        this.itmnumber = value;
    }

    /**
     * Gets the value of the zobserv property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZOBSERV() {
        return zobserv;
    }

    /**
     * Sets the value of the zobserv property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZOBSERV(String value) {
        this.zobserv = value;
    }

}
