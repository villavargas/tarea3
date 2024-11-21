
package mx.com.amx.mexico.telcel.mq.v1_1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import com.amx.mexico.telcel.esb.v1_1.ControlDataResponseType;
import com.amx.mexico.telcel.esb.v1_1.DetailResponseType;


/**
 * <p>Java class for OperationResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OperationResponseType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="controlData" type="{http://amx.com/mexico/telcel/esb/v1_1}ControlDataResponseType"/&gt;
 *         &lt;element name="detailResponse" type="{http://amx.com/mexico/telcel/esb/v1_1}DetailResponseType"/&gt;
 *         &lt;element name="payload" type="{http://www.w3.org/2001/XMLSchema}anyType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OperationResponseType", propOrder = {
    "controlData",
    "detailResponse",
    "payload"
})
public class OperationResponseType {

    @XmlElement(required = true)
    protected ControlDataResponseType controlData;
    @XmlElement(required = true)
    protected DetailResponseType detailResponse;
    @XmlElement(required = true)
    protected Object payload;

    /**
     * Gets the value of the controlData property.
     * 
     * @return
     *     possible object is
     *     {@link ControlDataResponseType }
     *     
     */
    public ControlDataResponseType getControlData() {
        return controlData;
    }

    /**
     * Sets the value of the controlData property.
     * 
     * @param value
     *     allowed object is
     *     {@link ControlDataResponseType }
     *     
     */
    public void setControlData(ControlDataResponseType value) {
        this.controlData = value;
    }

    /**
     * Gets the value of the detailResponse property.
     * 
     * @return
     *     possible object is
     *     {@link DetailResponseType }
     *     
     */
    public DetailResponseType getDetailResponse() {
        return detailResponse;
    }

    /**
     * Sets the value of the detailResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link DetailResponseType }
     *     
     */
    public void setDetailResponse(DetailResponseType value) {
        this.detailResponse = value;
    }

    /**
     * Gets the value of the payload property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getPayload() {
        return payload;
    }

    /**
     * Sets the value of the payload property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setPayload(Object value) {
        this.payload = value;
    }

}
