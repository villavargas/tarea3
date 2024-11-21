
package mx.com.amx.mexico.telcel.di.sds.gsa.dai.esb.consultafacturas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ConsultaFacturaPDFXMLPetType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ConsultaFacturaPDFXMLPetType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="usuario" type="{http://www.amx.com.mx/mexico/telcel/di/sds/gsa/dai/esb/consultafacturas}UsuarioType" minOccurs="0"/&gt;
 *         &lt;element name="password" type="{http://www.amx.com.mx/mexico/telcel/di/sds/gsa/dai/esb/consultafacturas}PasswordType" minOccurs="0"/&gt;
 *         &lt;element name="factura" type="{http://www.amx.com.mx/mexico/telcel/di/sds/gsa/dai/esb/consultafacturas}FacturaType"/&gt;
 *         &lt;element name="opcion" type="{http://www.amx.com.mx/mexico/telcel/di/sds/gsa/dai/esb/consultafacturas}OpcionType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConsultaFacturaPDFXMLPetType", propOrder = {
    "usuario",
    "password",
    "factura",
    "opcion"
})
public class ConsultaFacturaPDFXMLPetType {

    protected String usuario;
    protected String password;
    @XmlElement(required = true)
    protected String factura;
    protected int opcion;

    /**
     * Gets the value of the usuario property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsuario() {
        return usuario;
    }

    /**
     * Sets the value of the usuario property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsuario(String value) {
        this.usuario = value;
    }

    /**
     * Gets the value of the password property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the value of the password property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPassword(String value) {
        this.password = value;
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
     * Gets the value of the opcion property.
     * 
     */
    public int getOpcion() {
        return opcion;
    }

    /**
     * Sets the value of the opcion property.
     * 
     */
    public void setOpcion(int value) {
        this.opcion = value;
    }

}
