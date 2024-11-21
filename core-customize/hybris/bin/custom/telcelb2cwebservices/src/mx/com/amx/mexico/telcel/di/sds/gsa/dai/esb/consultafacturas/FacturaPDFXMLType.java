
package mx.com.amx.mexico.telcel.di.sds.gsa.dai.esb.consultafacturas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FacturaPDFXMLType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FacturaPDFXMLType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="pdf" type="{http://www.amx.com.mx/mexico/telcel/di/sds/gsa/dai/esb/consultafacturas}BytePdfType" minOccurs="0"/&gt;
 *         &lt;element name="xml" type="{http://www.amx.com.mx/mexico/telcel/di/sds/gsa/dai/esb/consultafacturas}ByteXMLType" minOccurs="0"/&gt;
 *         &lt;element name="errorFactura" type="{http://www.amx.com.mx/mexico/telcel/di/sds/gsa/dai/esb/consultafacturas}ErrorFacturaType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FacturaPDFXMLType", propOrder = {
    "pdf",
    "xml",
    "errorFactura"
})
public class FacturaPDFXMLType {

    protected String pdf;
    protected String xml;
    protected ErrorFacturaType errorFactura;

    /**
     * Gets the value of the pdf property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPdf() {
        return pdf;
    }

    /**
     * Sets the value of the pdf property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPdf(String value) {
        this.pdf = value;
    }

    /**
     * Gets the value of the xml property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXml() {
        return xml;
    }

    /**
     * Sets the value of the xml property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXml(String value) {
        this.xml = value;
    }

    /**
     * Gets the value of the errorFactura property.
     * 
     * @return
     *     possible object is
     *     {@link ErrorFacturaType }
     *     
     */
    public ErrorFacturaType getErrorFactura() {
        return errorFactura;
    }

    /**
     * Sets the value of the errorFactura property.
     * 
     * @param value
     *     allowed object is
     *     {@link ErrorFacturaType }
     *     
     */
    public void setErrorFactura(ErrorFacturaType value) {
        this.errorFactura = value;
    }

}
