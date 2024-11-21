
package mx.com.amx.mexico.telcel.di.sds.gsa.dai.esb.consultafacturas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ConsultaFacturaPDFXMLRespType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ConsultaFacturaPDFXMLRespType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="facturaPDFXML" type="{http://www.amx.com.mx/mexico/telcel/di/sds/gsa/dai/esb/consultafacturas}FacturaPDFXMLType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConsultaFacturaPDFXMLRespType", propOrder = {
    "facturaPDFXML"
})
public class ConsultaFacturaPDFXMLRespType {

    protected FacturaPDFXMLType facturaPDFXML;

    /**
     * Gets the value of the facturaPDFXML property.
     * 
     * @return
     *     possible object is
     *     {@link FacturaPDFXMLType }
     *     
     */
    public FacturaPDFXMLType getFacturaPDFXML() {
        return facturaPDFXML;
    }

    /**
     * Sets the value of the facturaPDFXML property.
     * 
     * @param value
     *     allowed object is
     *     {@link FacturaPDFXMLType }
     *     
     */
    public void setFacturaPDFXML(FacturaPDFXMLType value) {
        this.facturaPDFXML = value;
    }

}
