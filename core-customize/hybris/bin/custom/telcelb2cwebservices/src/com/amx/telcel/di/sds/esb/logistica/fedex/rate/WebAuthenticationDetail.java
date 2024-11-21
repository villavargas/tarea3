
package com.amx.telcel.di.sds.esb.logistica.fedex.rate;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Used in authentication of the sender's identity.
 * 
 * <p>Java class for WebAuthenticationDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WebAuthenticationDetail"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="UserCredential" type="{http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate}WebAuthenticationCredential"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WebAuthenticationDetail", propOrder = {
    "userCredential"
})
public class WebAuthenticationDetail {

    @XmlElement(name = "UserCredential", required = true)
    protected WebAuthenticationCredential userCredential;

    /**
     * Gets the value of the userCredential property.
     * 
     * @return
     *     possible object is
     *     {@link WebAuthenticationCredential }
     *     
     */
    public WebAuthenticationCredential getUserCredential() {
        return userCredential;
    }

    /**
     * Sets the value of the userCredential property.
     * 
     * @param value
     *     allowed object is
     *     {@link WebAuthenticationCredential }
     *     
     */
    public void setUserCredential(WebAuthenticationCredential value) {
        this.userCredential = value;
    }

}
