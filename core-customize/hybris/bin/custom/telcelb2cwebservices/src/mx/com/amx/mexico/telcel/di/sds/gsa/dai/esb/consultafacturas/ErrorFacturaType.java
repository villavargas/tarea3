
package mx.com.amx.mexico.telcel.di.sds.gsa.dai.esb.consultafacturas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ErrorFacturaType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ErrorFacturaType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="idError" type="{http://www.amx.com.mx/mexico/telcel/di/sds/gsa/dai/esb/consultafacturas}IdErrorType" minOccurs="0"/&gt;
 *         &lt;element name="msgError" type="{http://www.amx.com.mx/mexico/telcel/di/sds/gsa/dai/esb/consultafacturas}MsgErrorType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ErrorFacturaType", propOrder = {
    "idError",
    "msgError"
})
public class ErrorFacturaType {

    protected String idError;
    protected String msgError;

    /**
     * Gets the value of the idError property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdError() {
        return idError;
    }

    /**
     * Sets the value of the idError property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdError(String value) {
        this.idError = value;
    }

    /**
     * Gets the value of the msgError property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMsgError() {
        return msgError;
    }

    /**
     * Sets the value of the msgError property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMsgError(String value) {
        this.msgError = value;
    }

}
