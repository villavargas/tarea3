
package com.amx.telcel.di.sds.esb.sap.facturacion;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for SapSiHeader complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SapSiHeader"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="DOC_TYPE"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="4"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="SALES_ORG"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="4"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="DISTR_CHAN"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="2"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="DIVISION"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="2"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="SALES_OFF"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="4"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="SALES_GRP" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="3"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="ORD_REASON"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="3"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="PRICE_GRP" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="2"/&gt;
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
 *         &lt;element name="REF_1_S" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="12"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="PO_METH_S" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="4"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="PO_METHOD" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="4"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="PRICE_DATE" type="{http://www.w3.org/2001/XMLSchema}date"/&gt;
 *         &lt;element name="REF_DOC_L" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="16"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="ZLSCH01" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="2"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="ZZACC_TYPE01" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="60"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="ZLSCH02" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="2"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="ZZACC_TYPE02" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="60"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="ZLSCH03" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="2"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="ZZACC_TYPE03" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="60"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="ZLSCH04" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="2"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="ZZACC_TYPE04" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="60"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="IPAPP" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="20"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="PRINTER" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="30"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="PURCH_DATE" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="PRICE_LIST" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="2"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="ZMETPAGO"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="3"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="ZUSOCFDI"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="3"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="ZREGFISCAL"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="3"/&gt;
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
@XmlType(name = "SapSiHeader", propOrder = {
    "doctype",
    "salesorg",
    "distrchan",
    "division",
    "salesoff",
    "salesgrp",
    "ordreason",
    "pricegrp",
    "pmnttrms",
    "ref1S",
    "pomeths",
    "pomethod",
    "pricedate",
    "refdocl",
    "zlsch01",
    "zzacctype01",
    "zlsch02",
    "zzacctype02",
    "zlsch03",
    "zzacctype03",
    "zlsch04",
    "zzacctype04",
    "ipapp",
    "printer",
    "purchdate",
    "pricelist",
    "zmetpago",
    "zusocfdi",
    "zregfiscal"
})
public class SapSiHeader {

    @XmlElement(name = "DOC_TYPE", required = true, nillable = true)
    protected String doctype;
    @XmlElement(name = "SALES_ORG", required = true, nillable = true)
    protected String salesorg;
    @XmlElement(name = "DISTR_CHAN", required = true, nillable = true)
    protected String distrchan;
    @XmlElement(name = "DIVISION", required = true, nillable = true)
    protected String division;
    @XmlElement(name = "SALES_OFF", required = true, nillable = true)
    protected String salesoff;
    @XmlElementRef(name = "SALES_GRP", type = JAXBElement.class, required = false)
    protected JAXBElement<String> salesgrp;
    @XmlElement(name = "ORD_REASON", required = true, nillable = true)
    protected String ordreason;
    @XmlElementRef(name = "PRICE_GRP", type = JAXBElement.class, required = false)
    protected JAXBElement<String> pricegrp;
    @XmlElementRef(name = "PMNTTRMS", type = JAXBElement.class, required = false)
    protected JAXBElement<String> pmnttrms;
    @XmlElementRef(name = "REF_1_S", type = JAXBElement.class, required = false)
    protected JAXBElement<String> ref1S;
    @XmlElementRef(name = "PO_METH_S", type = JAXBElement.class, required = false)
    protected JAXBElement<String> pomeths;
    @XmlElementRef(name = "PO_METHOD", type = JAXBElement.class, required = false)
    protected JAXBElement<String> pomethod;
    @XmlElement(name = "PRICE_DATE", required = true, nillable = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar pricedate;
    @XmlElementRef(name = "REF_DOC_L", type = JAXBElement.class, required = false)
    protected JAXBElement<String> refdocl;
    @XmlElementRef(name = "ZLSCH01", type = JAXBElement.class, required = false)
    protected JAXBElement<String> zlsch01;
    @XmlElementRef(name = "ZZACC_TYPE01", type = JAXBElement.class, required = false)
    protected JAXBElement<String> zzacctype01;
    @XmlElementRef(name = "ZLSCH02", type = JAXBElement.class, required = false)
    protected JAXBElement<String> zlsch02;
    @XmlElementRef(name = "ZZACC_TYPE02", type = JAXBElement.class, required = false)
    protected JAXBElement<String> zzacctype02;
    @XmlElementRef(name = "ZLSCH03", type = JAXBElement.class, required = false)
    protected JAXBElement<String> zlsch03;
    @XmlElementRef(name = "ZZACC_TYPE03", type = JAXBElement.class, required = false)
    protected JAXBElement<String> zzacctype03;
    @XmlElementRef(name = "ZLSCH04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> zlsch04;
    @XmlElementRef(name = "ZZACC_TYPE04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> zzacctype04;
    @XmlElementRef(name = "IPAPP", type = JAXBElement.class, required = false)
    protected JAXBElement<String> ipapp;
    @XmlElementRef(name = "PRINTER", type = JAXBElement.class, required = false)
    protected JAXBElement<String> printer;
    @XmlElementRef(name = "PURCH_DATE", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> purchdate;
    @XmlElementRef(name = "PRICE_LIST", type = JAXBElement.class, required = false)
    protected JAXBElement<String> pricelist;
    @XmlElement(name = "ZMETPAGO", required = true, nillable = true)
    protected String zmetpago;
    @XmlElement(name = "ZUSOCFDI", required = true, nillable = true)
    protected String zusocfdi;
    @XmlElement(name = "ZREGFISCAL", required = true, nillable = true)
    protected String zregfiscal;

    /**
     * Gets the value of the doctype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDOCTYPE() {
        return doctype;
    }

    /**
     * Sets the value of the doctype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDOCTYPE(String value) {
        this.doctype = value;
    }

    /**
     * Gets the value of the salesorg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSALESORG() {
        return salesorg;
    }

    /**
     * Sets the value of the salesorg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSALESORG(String value) {
        this.salesorg = value;
    }

    /**
     * Gets the value of the distrchan property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDISTRCHAN() {
        return distrchan;
    }

    /**
     * Sets the value of the distrchan property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDISTRCHAN(String value) {
        this.distrchan = value;
    }

    /**
     * Gets the value of the division property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDIVISION() {
        return division;
    }

    /**
     * Sets the value of the division property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDIVISION(String value) {
        this.division = value;
    }

    /**
     * Gets the value of the salesoff property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSALESOFF() {
        return salesoff;
    }

    /**
     * Sets the value of the salesoff property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSALESOFF(String value) {
        this.salesoff = value;
    }

    /**
     * Gets the value of the salesgrp property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getSALESGRP() {
        return salesgrp;
    }

    /**
     * Sets the value of the salesgrp property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setSALESGRP(JAXBElement<String> value) {
        this.salesgrp = value;
    }

    /**
     * Gets the value of the ordreason property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getORDREASON() {
        return ordreason;
    }

    /**
     * Sets the value of the ordreason property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setORDREASON(String value) {
        this.ordreason = value;
    }

    /**
     * Gets the value of the pricegrp property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPRICEGRP() {
        return pricegrp;
    }

    /**
     * Sets the value of the pricegrp property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPRICEGRP(JAXBElement<String> value) {
        this.pricegrp = value;
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
     * Gets the value of the ref1S property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getREF1S() {
        return ref1S;
    }

    /**
     * Sets the value of the ref1S property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setREF1S(JAXBElement<String> value) {
        this.ref1S = value;
    }

    /**
     * Gets the value of the pomeths property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPOMETHS() {
        return pomeths;
    }

    /**
     * Sets the value of the pomeths property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPOMETHS(JAXBElement<String> value) {
        this.pomeths = value;
    }

    /**
     * Gets the value of the pomethod property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPOMETHOD() {
        return pomethod;
    }

    /**
     * Sets the value of the pomethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPOMETHOD(JAXBElement<String> value) {
        this.pomethod = value;
    }

    /**
     * Gets the value of the pricedate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getPRICEDATE() {
        return pricedate;
    }

    /**
     * Sets the value of the pricedate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setPRICEDATE(XMLGregorianCalendar value) {
        this.pricedate = value;
    }

    /**
     * Gets the value of the refdocl property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getREFDOCL() {
        return refdocl;
    }

    /**
     * Sets the value of the refdocl property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setREFDOCL(JAXBElement<String> value) {
        this.refdocl = value;
    }

    /**
     * Gets the value of the zlsch01 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getZLSCH01() {
        return zlsch01;
    }

    /**
     * Sets the value of the zlsch01 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setZLSCH01(JAXBElement<String> value) {
        this.zlsch01 = value;
    }

    /**
     * Gets the value of the zzacctype01 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getZZACCTYPE01() {
        return zzacctype01;
    }

    /**
     * Sets the value of the zzacctype01 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setZZACCTYPE01(JAXBElement<String> value) {
        this.zzacctype01 = value;
    }

    /**
     * Gets the value of the zlsch02 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getZLSCH02() {
        return zlsch02;
    }

    /**
     * Sets the value of the zlsch02 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setZLSCH02(JAXBElement<String> value) {
        this.zlsch02 = value;
    }

    /**
     * Gets the value of the zzacctype02 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getZZACCTYPE02() {
        return zzacctype02;
    }

    /**
     * Sets the value of the zzacctype02 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setZZACCTYPE02(JAXBElement<String> value) {
        this.zzacctype02 = value;
    }

    /**
     * Gets the value of the zlsch03 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getZLSCH03() {
        return zlsch03;
    }

    /**
     * Sets the value of the zlsch03 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setZLSCH03(JAXBElement<String> value) {
        this.zlsch03 = value;
    }

    /**
     * Gets the value of the zzacctype03 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getZZACCTYPE03() {
        return zzacctype03;
    }

    /**
     * Sets the value of the zzacctype03 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setZZACCTYPE03(JAXBElement<String> value) {
        this.zzacctype03 = value;
    }

    /**
     * Gets the value of the zlsch04 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getZLSCH04() {
        return zlsch04;
    }

    /**
     * Sets the value of the zlsch04 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setZLSCH04(JAXBElement<String> value) {
        this.zlsch04 = value;
    }

    /**
     * Gets the value of the zzacctype04 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getZZACCTYPE04() {
        return zzacctype04;
    }

    /**
     * Sets the value of the zzacctype04 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setZZACCTYPE04(JAXBElement<String> value) {
        this.zzacctype04 = value;
    }

    /**
     * Gets the value of the ipapp property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getIPAPP() {
        return ipapp;
    }

    /**
     * Sets the value of the ipapp property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setIPAPP(JAXBElement<String> value) {
        this.ipapp = value;
    }

    /**
     * Gets the value of the printer property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPRINTER() {
        return printer;
    }

    /**
     * Sets the value of the printer property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPRINTER(JAXBElement<String> value) {
        this.printer = value;
    }

    /**
     * Gets the value of the purchdate property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getPURCHDATE() {
        return purchdate;
    }

    /**
     * Sets the value of the purchdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setPURCHDATE(JAXBElement<XMLGregorianCalendar> value) {
        this.purchdate = value;
    }

    /**
     * Gets the value of the pricelist property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPRICELIST() {
        return pricelist;
    }

    /**
     * Sets the value of the pricelist property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPRICELIST(JAXBElement<String> value) {
        this.pricelist = value;
    }

    /**
     * Gets the value of the zmetpago property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZMETPAGO() {
        return zmetpago;
    }

    /**
     * Sets the value of the zmetpago property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZMETPAGO(String value) {
        this.zmetpago = value;
    }

    /**
     * Gets the value of the zusocfdi property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZUSOCFDI() {
        return zusocfdi;
    }

    /**
     * Sets the value of the zusocfdi property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZUSOCFDI(String value) {
        this.zusocfdi = value;
    }

    /**
     * Gets the value of the zregfiscal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZREGFISCAL() {
        return zregfiscal;
    }

    /**
     * Sets the value of the zregfiscal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZREGFISCAL(String value) {
        this.zregfiscal = value;
    }

}
