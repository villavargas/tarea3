
package com.amx.telcel.di.sds.esb.sap.facturacion;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import com.amx.mexico.telcel.esb.v1_0_2.ResponseHeaderType;


/**
 * <p>Java class for BorraDocumentosOpRespType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BorraDocumentosOpRespType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="encabezado" type="{http://amx.com/mexico/telcel/esb/v1_0_2}ResponseHeaderType"/&gt;
 *         &lt;element name="respuesta" type="{http://www.amx.com/telcel/di/sds/esb/sap/facturacion}BorraDocumentosRespType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BorraDocumentosOpRespType", propOrder = {
    "encabezado",
    "respuesta"
})
public class BorraDocumentosOpRespType {

    @XmlElement(required = true)
    protected ResponseHeaderType encabezado;
    @XmlElement(required = true)
    protected BorraDocumentosRespType respuesta;

    /**
     * Gets the value of the encabezado property.
     * 
     * @return
     *     possible object is
     *     {@link ResponseHeaderType }
     *     
     */
    public ResponseHeaderType getEncabezado() {
        return encabezado;
    }

    /**
     * Sets the value of the encabezado property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResponseHeaderType }
     *     
     */
    public void setEncabezado(ResponseHeaderType value) {
        this.encabezado = value;
    }

    /**
     * Gets the value of the respuesta property.
     * 
     * @return
     *     possible object is
     *     {@link BorraDocumentosRespType }
     *     
     */
    public BorraDocumentosRespType getRespuesta() {
        return respuesta;
    }

    /**
     * Sets the value of the respuesta property.
     * 
     * @param value
     *     allowed object is
     *     {@link BorraDocumentosRespType }
     *     
     */
    public void setRespuesta(BorraDocumentosRespType value) {
        this.respuesta = value;
    }

}
