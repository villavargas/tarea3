
package com.amx.mexico.telcel.esb.v1_0_2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ErrorType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ErrorType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Code" type="{http://amx.com/mexico/telcel/esb/v1_0_2}CodeType"/&gt;
 *         &lt;element name="SeverityLevel" type="{http://amx.com/mexico/telcel/esb/v1_0_2}SeverityType"/&gt;
 *         &lt;element name="Description" type="{http://amx.com/mexico/telcel/esb/v1_0_2}DescriptionType"/&gt;
 *         &lt;element name="Actor" type="{http://amx.com/mexico/telcel/esb/v1_0_2}ActorType" minOccurs="0"/&gt;
 *         &lt;element name="BussinesMeaning" type="{http://amx.com/mexico/telcel/esb/v1_0_2}MeaningType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ErrorType", propOrder = {
    "code",
    "severityLevel",
    "description",
    "actor",
    "bussinesMeaning"
})
public class ErrorType {

    @XmlElement(name = "Code", required = true)
    protected String code;
    @XmlElement(name = "SeverityLevel", required = true)
    protected String severityLevel;
    @XmlElement(name = "Description", required = true)
    protected String description;
    @XmlElement(name = "Actor")
    protected String actor;
    @XmlElement(name = "BussinesMeaning", required = true)
    protected String bussinesMeaning;

    /**
     * Gets the value of the code property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCode(String value) {
        this.code = value;
    }

    /**
     * Gets the value of the severityLevel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSeverityLevel() {
        return severityLevel;
    }

    /**
     * Sets the value of the severityLevel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSeverityLevel(String value) {
        this.severityLevel = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the actor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getActor() {
        return actor;
    }

    /**
     * Sets the value of the actor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setActor(String value) {
        this.actor = value;
    }

    /**
     * Gets the value of the bussinesMeaning property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBussinesMeaning() {
        return bussinesMeaning;
    }

    /**
     * Sets the value of the bussinesMeaning property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBussinesMeaning(String value) {
        this.bussinesMeaning = value;
    }

}
