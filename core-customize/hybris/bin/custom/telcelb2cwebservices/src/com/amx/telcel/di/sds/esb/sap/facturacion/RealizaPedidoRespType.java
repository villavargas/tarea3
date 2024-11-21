
package com.amx.telcel.di.sds.esb.sap.facturacion;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RealizaPedidoRespType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RealizaPedidoRespType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="PO_NO_SALESDOCUMENT" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="10"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="PO_NO_DELIVERY" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="10"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="PO_NO_KUNNR" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="10"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="SapToReturn" type="{http://www.amx.com/telcel/di/sds/esb/sap/facturacion}SapToReturn" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RealizaPedidoRespType", propOrder = {
    "ponosalesdocument",
    "ponodelivery",
    "ponokunnr",
    "sapToReturn"
})
public class RealizaPedidoRespType {

    @XmlElementRef(name = "PO_NO_SALESDOCUMENT", type = JAXBElement.class, required = false)
    protected JAXBElement<String> ponosalesdocument;
    @XmlElementRef(name = "PO_NO_DELIVERY", type = JAXBElement.class, required = false)
    protected JAXBElement<String> ponodelivery;
    @XmlElementRef(name = "PO_NO_KUNNR", type = JAXBElement.class, required = false)
    protected JAXBElement<String> ponokunnr;
    @XmlElement(name = "SapToReturn", required = true, nillable = true)
    protected List<SapToReturn> sapToReturn;

    /**
     * Gets the value of the ponosalesdocument property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPONOSALESDOCUMENT() {
        return ponosalesdocument;
    }

    /**
     * Sets the value of the ponosalesdocument property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPONOSALESDOCUMENT(JAXBElement<String> value) {
        this.ponosalesdocument = value;
    }

    /**
     * Gets the value of the ponodelivery property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPONODELIVERY() {
        return ponodelivery;
    }

    /**
     * Sets the value of the ponodelivery property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPONODELIVERY(JAXBElement<String> value) {
        this.ponodelivery = value;
    }

    /**
     * Gets the value of the ponokunnr property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPONOKUNNR() {
        return ponokunnr;
    }

    /**
     * Sets the value of the ponokunnr property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPONOKUNNR(JAXBElement<String> value) {
        this.ponokunnr = value;
    }

    /**
     * Gets the value of the sapToReturn property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the sapToReturn property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSapToReturn().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SapToReturn }
     * 
     * 
     */
    public List<SapToReturn> getSapToReturn() {
        if (sapToReturn == null) {
            sapToReturn = new ArrayList<SapToReturn>();
        }
        return this.sapToReturn;
    }

}
