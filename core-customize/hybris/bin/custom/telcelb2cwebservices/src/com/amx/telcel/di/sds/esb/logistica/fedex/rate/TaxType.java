
package com.amx.telcel.di.sds.esb.logistica.fedex.rate;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TaxType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="TaxType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="EXPORT"/&gt;
 *     &lt;enumeration value="GST"/&gt;
 *     &lt;enumeration value="HST"/&gt;
 *     &lt;enumeration value="INTRACOUNTRY"/&gt;
 *     &lt;enumeration value="OTHER"/&gt;
 *     &lt;enumeration value="PST"/&gt;
 *     &lt;enumeration value="VAT"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "TaxType")
@XmlEnum
public enum TaxType {

    EXPORT,
    GST,
    HST,
    INTRACOUNTRY,
    OTHER,
    PST,
    VAT;

    public String value() {
        return name();
    }

    public static TaxType fromValue(String v) {
        return valueOf(v);
    }

}
