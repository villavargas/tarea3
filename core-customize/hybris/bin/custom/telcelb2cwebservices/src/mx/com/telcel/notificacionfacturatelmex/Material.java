
package mx.com.telcel.notificacionfacturatelmex;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Material complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Material"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="cveMaterial" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="pzas" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="claveSAT" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="descripcionSAT" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Material", propOrder = {
    "cveMaterial",
    "pzas",
    "claveSAT",
    "descripcionSAT"
})
public class Material {

    @XmlElement(required = true)
    protected String cveMaterial;
    @XmlElement(required = true)
    protected String pzas;
    @XmlElement(required = true)
    protected String claveSAT;
    @XmlElement(required = true)
    protected String descripcionSAT;

    /**
     * Gets the value of the cveMaterial property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCveMaterial() {
        return cveMaterial;
    }

    /**
     * Sets the value of the cveMaterial property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCveMaterial(String value) {
        this.cveMaterial = value;
    }

    /**
     * Gets the value of the pzas property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPzas() {
        return pzas;
    }

    /**
     * Sets the value of the pzas property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPzas(String value) {
        this.pzas = value;
    }

    /**
     * Gets the value of the claveSAT property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClaveSAT() {
        return claveSAT;
    }

    /**
     * Sets the value of the claveSAT property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClaveSAT(String value) {
        this.claveSAT = value;
    }

    /**
     * Gets the value of the descripcionSAT property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescripcionSAT() {
        return descripcionSAT;
    }

    /**
     * Sets the value of the descripcionSAT property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescripcionSAT(String value) {
        this.descripcionSAT = value;
    }

}
