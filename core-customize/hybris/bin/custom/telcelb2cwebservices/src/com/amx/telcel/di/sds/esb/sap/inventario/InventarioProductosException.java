
package com.amx.telcel.di.sds.esb.sap.inventario;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import com.amx.mexico.telcel.esb.v1_1.ControlDataResponseType;
import com.amx.mexico.telcel.esb.v1_1.DetailFailType;


/**
 * <p>Java class for InventarioProductosException complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InventarioProductosException"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="controlData" type="{http://amx.com/mexico/telcel/esb/v1_1}ControlDataResponseType"/&gt;
 *         &lt;element name="detailFail" type="{http://amx.com/mexico/telcel/esb/v1_1}DetailFailType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InventarioProductosException", propOrder = {
    "controlData",
    "detailFail"
})
public class InventarioProductosException {

    @XmlElement(required = true)
    protected ControlDataResponseType controlData;
    @XmlElement(required = true)
    protected DetailFailType detailFail;

    /**
     * Gets the value of the controlData property.
     * 
     * @return
     *     possible object is
     *     {@link ControlDataResponseType }
     *     
     */
    public ControlDataResponseType getControlData() {
        return controlData;
    }

    /**
     * Sets the value of the controlData property.
     * 
     * @param value
     *     allowed object is
     *     {@link ControlDataResponseType }
     *     
     */
    public void setControlData(ControlDataResponseType value) {
        this.controlData = value;
    }

    /**
     * Gets the value of the detailFail property.
     * 
     * @return
     *     possible object is
     *     {@link DetailFailType }
     *     
     */
    public DetailFailType getDetailFail() {
        return detailFail;
    }

    /**
     * Sets the value of the detailFail property.
     * 
     * @param value
     *     allowed object is
     *     {@link DetailFailType }
     *     
     */
    public void setDetailFail(DetailFailType value) {
        this.detailFail = value;
    }

}
