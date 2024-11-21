
package com.amx.telcel.di.sds.esb.sap.facturacion;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import com.amx.mexico.telcel.esb.v1_0_2.RequestHeaderType;


/**
 * <p>Java class for RealizaPedidoOpPetType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RealizaPedidoOpPetType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="encabezado" type="{http://amx.com/mexico/telcel/esb/v1_0_2}RequestHeaderType"/&gt;
 *         &lt;element name="peticion" type="{http://www.amx.com/telcel/di/sds/esb/sap/facturacion}RealizaPedidoPetType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RealizaPedidoOpPetType", propOrder = {
    "encabezado",
    "peticion"
})
public class RealizaPedidoOpPetType {

    @XmlElement(required = true)
    protected RequestHeaderType encabezado;
    @XmlElement(required = true)
    protected RealizaPedidoPetType peticion;

    /**
     * Gets the value of the encabezado property.
     * 
     * @return
     *     possible object is
     *     {@link RequestHeaderType }
     *     
     */
    public RequestHeaderType getEncabezado() {
        return encabezado;
    }

    /**
     * Sets the value of the encabezado property.
     * 
     * @param value
     *     allowed object is
     *     {@link RequestHeaderType }
     *     
     */
    public void setEncabezado(RequestHeaderType value) {
        this.encabezado = value;
    }

    /**
     * Gets the value of the peticion property.
     * 
     * @return
     *     possible object is
     *     {@link RealizaPedidoPetType }
     *     
     */
    public RealizaPedidoPetType getPeticion() {
        return peticion;
    }

    /**
     * Sets the value of the peticion property.
     * 
     * @param value
     *     allowed object is
     *     {@link RealizaPedidoPetType }
     *     
     */
    public void setPeticion(RealizaPedidoPetType value) {
        this.peticion = value;
    }

}
