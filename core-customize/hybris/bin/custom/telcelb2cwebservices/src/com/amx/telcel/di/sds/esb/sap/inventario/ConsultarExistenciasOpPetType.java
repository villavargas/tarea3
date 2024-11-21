
package com.amx.telcel.di.sds.esb.sap.inventario;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import com.amx.mexico.telcel.esb.v1_1.ControlDataRequestType;


/**
 * <p>Java class for ConsultarExistenciasOpPetType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ConsultarExistenciasOpPetType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="controlData" type="{http://amx.com/mexico/telcel/esb/v1_1}ControlDataRequestType"/&gt;
 *         &lt;element name="peticion" type="{http://www.amx.com/telcel/di/sds/esb/sap/inventario}ConsultarExistenciasType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConsultarExistenciasOpPetType", propOrder = {
    "controlData",
    "peticion"
})
public class ConsultarExistenciasOpPetType {

    @XmlElement(required = true)
    protected ControlDataRequestType controlData;
    @XmlElement(required = true)
    protected ConsultarExistenciasType peticion;

    /**
     * Gets the value of the controlData property.
     * 
     * @return
     *     possible object is
     *     {@link ControlDataRequestType }
     *     
     */
    public ControlDataRequestType getControlData() {
        return controlData;
    }

    /**
     * Sets the value of the controlData property.
     * 
     * @param value
     *     allowed object is
     *     {@link ControlDataRequestType }
     *     
     */
    public void setControlData(ControlDataRequestType value) {
        this.controlData = value;
    }

    /**
     * Gets the value of the peticion property.
     * 
     * @return
     *     possible object is
     *     {@link ConsultarExistenciasType }
     *     
     */
    public ConsultarExistenciasType getPeticion() {
        return peticion;
    }

    /**
     * Sets the value of the peticion property.
     * 
     * @param value
     *     allowed object is
     *     {@link ConsultarExistenciasType }
     *     
     */
    public void setPeticion(ConsultarExistenciasType value) {
        this.peticion = value;
    }

}
