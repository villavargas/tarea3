
package com.amx.telcel.di.sds.esb.logistica.fedex.rate;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * This class groups the shipment and package rating data
 * 				for a specific rate type for use in a rating reply,
 * 				which groups result data by rate type.
 * 
 * <p>Java class for RatedShipmentDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RatedShipmentDetail"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ShipmentRateDetail" type="{http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate}ShipmentRateDetail" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RatedShipmentDetail", propOrder = {
    "shipmentRateDetail"
})
public class RatedShipmentDetail {

    @XmlElement(name = "ShipmentRateDetail")
    protected ShipmentRateDetail shipmentRateDetail;

    /**
     * Gets the value of the shipmentRateDetail property.
     * 
     * @return
     *     possible object is
     *     {@link ShipmentRateDetail }
     *     
     */
    public ShipmentRateDetail getShipmentRateDetail() {
        return shipmentRateDetail;
    }

    /**
     * Sets the value of the shipmentRateDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShipmentRateDetail }
     *     
     */
    public void setShipmentRateDetail(ShipmentRateDetail value) {
        this.shipmentRateDetail = value;
    }

}
