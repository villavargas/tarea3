
package com.amx.telcel.di.sds.esb.sap.inventario;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MaterialType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MaterialType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="MATNR" type="{http://www.amx.com/telcel/di/sds/esb/sap/inventario}MATNRTYPE" minOccurs="0"/&gt;
 *         &lt;element name="MAKTX" type="{http://www.amx.com/telcel/di/sds/esb/sap/inventario}MAKTXType" minOccurs="0"/&gt;
 *         &lt;element name="MARCA" type="{http://www.amx.com/telcel/di/sds/esb/sap/inventario}MARCAType" minOccurs="0"/&gt;
 *         &lt;element name="MODELO" type="{http://www.amx.com/telcel/di/sds/esb/sap/inventario}MODELOType" minOccurs="0"/&gt;
 *         &lt;element name="COLOR" type="{http://www.amx.com/telcel/di/sds/esb/sap/inventario}COLORType" minOccurs="0"/&gt;
 *         &lt;element name="MATKL" type="{http://www.amx.com/telcel/di/sds/esb/sap/inventario}MATKLType" minOccurs="0"/&gt;
 *         &lt;element name="DESC_GRUPO" type="{http://www.amx.com/telcel/di/sds/esb/sap/inventario}DESC_GRUPOType" minOccurs="0"/&gt;
 *         &lt;element name="SPART" type="{http://www.amx.com/telcel/di/sds/esb/sap/inventario}SPARTType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MaterialType", propOrder = {
    "matnr",
    "maktx",
    "marca",
    "modelo",
    "color",
    "matkl",
    "descgrupo",
    "spart"
})
public class MaterialType {

    @XmlElement(name = "MATNR")
    protected String matnr;
    @XmlElement(name = "MAKTX")
    protected String maktx;
    @XmlElement(name = "MARCA")
    protected String marca;
    @XmlElement(name = "MODELO")
    protected String modelo;
    @XmlElement(name = "COLOR")
    protected String color;
    @XmlElement(name = "MATKL")
    protected String matkl;
    @XmlElement(name = "DESC_GRUPO")
    protected String descgrupo;
    @XmlElement(name = "SPART")
    protected String spart;

    /**
     * Gets the value of the matnr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMATNR() {
        return matnr;
    }

    /**
     * Sets the value of the matnr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMATNR(String value) {
        this.matnr = value;
    }

    /**
     * Gets the value of the maktx property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMAKTX() {
        return maktx;
    }

    /**
     * Sets the value of the maktx property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMAKTX(String value) {
        this.maktx = value;
    }

    /**
     * Gets the value of the marca property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMARCA() {
        return marca;
    }

    /**
     * Sets the value of the marca property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMARCA(String value) {
        this.marca = value;
    }

    /**
     * Gets the value of the modelo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMODELO() {
        return modelo;
    }

    /**
     * Sets the value of the modelo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMODELO(String value) {
        this.modelo = value;
    }

    /**
     * Gets the value of the color property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCOLOR() {
        return color;
    }

    /**
     * Sets the value of the color property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCOLOR(String value) {
        this.color = value;
    }

    /**
     * Gets the value of the matkl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMATKL() {
        return matkl;
    }

    /**
     * Sets the value of the matkl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMATKL(String value) {
        this.matkl = value;
    }

    /**
     * Gets the value of the descgrupo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDESCGRUPO() {
        return descgrupo;
    }

    /**
     * Sets the value of the descgrupo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDESCGRUPO(String value) {
        this.descgrupo = value;
    }

    /**
     * Gets the value of the spart property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSPART() {
        return spart;
    }

    /**
     * Sets the value of the spart property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSPART(String value) {
        this.spart = value;
    }

}
