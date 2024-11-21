
package com.amx.telcel.di.sds.esb.sap.facturacion;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SapTiCond complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SapTiCond"&gt;
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
 *         &lt;element name="COND_TYPE"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="4"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="COND_VALUE"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}decimal"&gt;
 *               &lt;totalDigits value="29"/&gt;
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
@XmlType(name = "SapTiCond", propOrder = {
    "itmnumber",
    "condtype",
    "condvalue"
})
public class SapTiCond {

    @XmlElement(name = "ITM_NUMBER", required = true, nillable = true)
    protected String itmnumber;
    @XmlElement(name = "COND_TYPE", required = true, nillable = true)
    protected String condtype;
    @XmlElement(name = "COND_VALUE", required = true, nillable = true)
    protected BigDecimal condvalue;

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
     * Gets the value of the condtype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCONDTYPE() {
        return condtype;
    }

    /**
     * Sets the value of the condtype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCONDTYPE(String value) {
        this.condtype = value;
    }

    /**
     * Gets the value of the condvalue property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getCONDVALUE() {
        return condvalue;
    }

    /**
     * Sets the value of the condvalue property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setCONDVALUE(BigDecimal value) {
        this.condvalue = value;
    }

}
