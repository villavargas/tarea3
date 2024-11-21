
package com.amx.telcel.di.sds.esb.sap.facturacion;

import java.math.BigDecimal;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SapTiItem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SapTiItem"&gt;
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
 *         &lt;element name="MATERIAL"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="18"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="PLANT"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="4"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="STORE_LOC"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="4"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="TARGET_QTY"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}decimal"&gt;
 *               &lt;totalDigits value="13"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="PRC_GROUP1" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="3"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="PRC_GROUP2" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="3"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="ZFOLIOCA" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="20"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="PMNTTRMS" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="4"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="REQ_QTY" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="13"/&gt;
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
@XmlType(name = "SapTiItem", propOrder = {
    "itmnumber",
    "material",
    "plant",
    "storeloc",
    "targetqty",
    "prcgroup1",
    "prcgroup2",
    "zfolioca",
    "pmnttrms",
    "reqqty"
})
public class SapTiItem {

    @XmlElement(name = "ITM_NUMBER", required = true, nillable = true)
    protected String itmnumber;
    @XmlElement(name = "MATERIAL", required = true, nillable = true)
    protected String material;
    @XmlElement(name = "PLANT", required = true, nillable = true)
    protected String plant;
    @XmlElement(name = "STORE_LOC", required = true, nillable = true)
    protected String storeloc;
    @XmlElement(name = "TARGET_QTY", required = true, nillable = true)
    protected BigDecimal targetqty;
    @XmlElementRef(name = "PRC_GROUP1", type = JAXBElement.class, required = false)
    protected JAXBElement<String> prcgroup1;
    @XmlElementRef(name = "PRC_GROUP2", type = JAXBElement.class, required = false)
    protected JAXBElement<String> prcgroup2;
    @XmlElementRef(name = "ZFOLIOCA", type = JAXBElement.class, required = false)
    protected JAXBElement<String> zfolioca;
    @XmlElementRef(name = "PMNTTRMS", type = JAXBElement.class, required = false)
    protected JAXBElement<String> pmnttrms;
    @XmlElementRef(name = "REQ_QTY", type = JAXBElement.class, required = false)
    protected JAXBElement<String> reqqty;

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
     * Gets the value of the material property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMATERIAL() {
        return material;
    }

    /**
     * Sets the value of the material property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMATERIAL(String value) {
        this.material = value;
    }

    /**
     * Gets the value of the plant property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPLANT() {
        return plant;
    }

    /**
     * Sets the value of the plant property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPLANT(String value) {
        this.plant = value;
    }

    /**
     * Gets the value of the storeloc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSTORELOC() {
        return storeloc;
    }

    /**
     * Sets the value of the storeloc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSTORELOC(String value) {
        this.storeloc = value;
    }

    /**
     * Gets the value of the targetqty property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getTARGETQTY() {
        return targetqty;
    }

    /**
     * Sets the value of the targetqty property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setTARGETQTY(BigDecimal value) {
        this.targetqty = value;
    }

    /**
     * Gets the value of the prcgroup1 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPRCGROUP1() {
        return prcgroup1;
    }

    /**
     * Sets the value of the prcgroup1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPRCGROUP1(JAXBElement<String> value) {
        this.prcgroup1 = value;
    }

    /**
     * Gets the value of the prcgroup2 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPRCGROUP2() {
        return prcgroup2;
    }

    /**
     * Sets the value of the prcgroup2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPRCGROUP2(JAXBElement<String> value) {
        this.prcgroup2 = value;
    }

    /**
     * Gets the value of the zfolioca property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getZFOLIOCA() {
        return zfolioca;
    }

    /**
     * Sets the value of the zfolioca property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setZFOLIOCA(JAXBElement<String> value) {
        this.zfolioca = value;
    }

    /**
     * Gets the value of the pmnttrms property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPMNTTRMS() {
        return pmnttrms;
    }

    /**
     * Sets the value of the pmnttrms property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPMNTTRMS(JAXBElement<String> value) {
        this.pmnttrms = value;
    }

    /**
     * Gets the value of the reqqty property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getREQQTY() {
        return reqqty;
    }

    /**
     * Sets the value of the reqqty property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setREQQTY(JAXBElement<String> value) {
        this.reqqty = value;
    }

}
