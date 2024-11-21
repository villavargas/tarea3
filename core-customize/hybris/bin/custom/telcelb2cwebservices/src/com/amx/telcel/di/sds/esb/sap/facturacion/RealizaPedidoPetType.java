
package com.amx.telcel.di.sds.esb.sap.facturacion;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RealizaPedidoPetType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RealizaPedidoPetType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="SapSiHeader" type="{http://www.amx.com/telcel/di/sds/esb/sap/facturacion}SapSiHeader"/&gt;
 *         &lt;element name="SapTiKunnr" type="{http://www.amx.com/telcel/di/sds/esb/sap/facturacion}SapTiKunnr" maxOccurs="unbounded"/&gt;
 *         &lt;element name="SapTiItem" type="{http://www.amx.com/telcel/di/sds/esb/sap/facturacion}SapTiItem" maxOccurs="unbounded"/&gt;
 *         &lt;element name="SapTiCond" type="{http://www.amx.com/telcel/di/sds/esb/sap/facturacion}SapTiCond" maxOccurs="unbounded"/&gt;
 *         &lt;element name="SapTiText" type="{http://www.amx.com/telcel/di/sds/esb/sap/facturacion}SapTiText" maxOccurs="unbounded"/&gt;
 *         &lt;element name="SapTiCirculoAzul" type="{http://www.amx.com/telcel/di/sds/esb/sap/facturacion}SapTiCirculoAzul" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RealizaPedidoPetType", propOrder = {
    "sapSiHeader",
    "sapTiKunnr",
    "sapTiItem",
    "sapTiCond",
    "sapTiText",
    "sapTiCirculoAzul"
})
public class RealizaPedidoPetType {

    @XmlElement(name = "SapSiHeader", required = true, nillable = true)
    protected SapSiHeader sapSiHeader;
    @XmlElement(name = "SapTiKunnr", required = true, nillable = true)
    protected List<SapTiKunnr> sapTiKunnr;
    @XmlElement(name = "SapTiItem", required = true, nillable = true)
    protected List<SapTiItem> sapTiItem;
    @XmlElement(name = "SapTiCond", required = true, nillable = true)
    protected List<SapTiCond> sapTiCond;
    @XmlElement(name = "SapTiText", required = true, nillable = true)
    protected List<SapTiText> sapTiText;
    @XmlElement(name = "SapTiCirculoAzul", nillable = true)
    protected List<SapTiCirculoAzul> sapTiCirculoAzul;

    /**
     * Gets the value of the sapSiHeader property.
     * 
     * @return
     *     possible object is
     *     {@link SapSiHeader }
     *     
     */
    public SapSiHeader getSapSiHeader() {
        return sapSiHeader;
    }

    /**
     * Sets the value of the sapSiHeader property.
     * 
     * @param value
     *     allowed object is
     *     {@link SapSiHeader }
     *     
     */
    public void setSapSiHeader(SapSiHeader value) {
        this.sapSiHeader = value;
    }

    /**
     * Gets the value of the sapTiKunnr property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the sapTiKunnr property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSapTiKunnr().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SapTiKunnr }
     * 
     * 
     */
    public List<SapTiKunnr> getSapTiKunnr() {
        if (sapTiKunnr == null) {
            sapTiKunnr = new ArrayList<SapTiKunnr>();
        }
        return this.sapTiKunnr;
    }

    /**
     * Gets the value of the sapTiItem property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the sapTiItem property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSapTiItem().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SapTiItem }
     * 
     * 
     */
    public List<SapTiItem> getSapTiItem() {
        if (sapTiItem == null) {
            sapTiItem = new ArrayList<SapTiItem>();
        }
        return this.sapTiItem;
    }

    /**
     * Gets the value of the sapTiCond property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the sapTiCond property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSapTiCond().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SapTiCond }
     * 
     * 
     */
    public List<SapTiCond> getSapTiCond() {
        if (sapTiCond == null) {
            sapTiCond = new ArrayList<SapTiCond>();
        }
        return this.sapTiCond;
    }

    /**
     * Gets the value of the sapTiText property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the sapTiText property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSapTiText().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SapTiText }
     * 
     * 
     */
    public List<SapTiText> getSapTiText() {
        if (sapTiText == null) {
            sapTiText = new ArrayList<SapTiText>();
        }
        return this.sapTiText;
    }

    /**
     * Gets the value of the sapTiCirculoAzul property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the sapTiCirculoAzul property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSapTiCirculoAzul().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SapTiCirculoAzul }
     * 
     * 
     */
    public List<SapTiCirculoAzul> getSapTiCirculoAzul() {
        if (sapTiCirculoAzul == null) {
            sapTiCirculoAzul = new ArrayList<SapTiCirculoAzul>();
        }
        return this.sapTiCirculoAzul;
    }

}
