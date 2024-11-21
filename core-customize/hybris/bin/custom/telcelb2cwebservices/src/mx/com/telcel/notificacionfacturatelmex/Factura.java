
package mx.com.telcel.notificacionfacturatelmex;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Factura complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Factura"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="pedido" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="entrega" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="factura" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="posiciones" type="{http://ibcomercio/telcel.com/commerce/services/SAP/NotificacionFactura/}Posiciones"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Factura", propOrder = {
    "pedido",
    "entrega",
    "factura",
    "posiciones"
})
public class Factura {

    @XmlElement(required = true)
    protected String pedido;
    @XmlElement(required = true)
    protected String entrega;
    @XmlElement(required = true)
    protected String factura;
    @XmlElement(required = true)
    protected Posiciones posiciones;

    /**
     * Gets the value of the pedido property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPedido() {
        return pedido;
    }

    /**
     * Sets the value of the pedido property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPedido(String value) {
        this.pedido = value;
    }

    /**
     * Gets the value of the entrega property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEntrega() {
        return entrega;
    }

    /**
     * Sets the value of the entrega property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEntrega(String value) {
        this.entrega = value;
    }

    /**
     * Gets the value of the factura property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFactura() {
        return factura;
    }

    /**
     * Sets the value of the factura property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFactura(String value) {
        this.factura = value;
    }

    /**
     * Gets the value of the posiciones property.
     * 
     * @return
     *     possible object is
     *     {@link Posiciones }
     *     
     */
    public Posiciones getPosiciones() {
        return posiciones;
    }

    /**
     * Sets the value of the posiciones property.
     * 
     * @param value
     *     allowed object is
     *     {@link Posiciones }
     *     
     */
    public void setPosiciones(Posiciones value) {
        this.posiciones = value;
    }

}
