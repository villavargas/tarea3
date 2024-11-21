
package com.amx.telcel.di.sds.esb.sap.inventario;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ExistenciaType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ExistenciaType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="MATNR" type="{http://www.amx.com/telcel/di/sds/esb/sap/inventario}MATNRTYPE" minOccurs="0"/&gt;
 *         &lt;element name="WERKS" type="{http://www.amx.com/telcel/di/sds/esb/sap/inventario}WERKSType" minOccurs="0"/&gt;
 *         &lt;element name="LGORT" type="{http://www.amx.com/telcel/di/sds/esb/sap/inventario}LGORTType" minOccurs="0"/&gt;
 *         &lt;element name="LABST" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExistenciaType", propOrder = {
    "matnr",
    "werks",
    "lgort",
    "labst"
})
public class ExistenciaType {

    @XmlElement(name = "MATNR")
    protected String matnr;
    @XmlElement(name = "WERKS")
    protected String werks;
    @XmlElement(name = "LGORT")
    protected String lgort;
    @XmlElement(name = "LABST")
    protected BigDecimal labst;

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
     * Gets the value of the werks property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWERKS() {
        return werks;
    }

    /**
     * Sets the value of the werks property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWERKS(String value) {
        this.werks = value;
    }

    /**
     * Gets the value of the lgort property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLGORT() {
        return lgort;
    }

    /**
     * Sets the value of the lgort property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLGORT(String value) {
        this.lgort = value;
    }

    /**
     * Gets the value of the labst property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getLABST() {
        return labst;
    }

    /**
     * Sets the value of the labst property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setLABST(BigDecimal value) {
        this.labst = value;
    }

}
