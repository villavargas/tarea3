
package com.amx.telcel.di.sds.esb.logistica.fedex.rate;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SignatureOptionType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="SignatureOptionType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="ADULT"/&gt;
 *     &lt;enumeration value="DIRECT"/&gt;
 *     &lt;enumeration value="INDIRECT"/&gt;
 *     &lt;enumeration value="NO_SIGNATURE_REQUIRED"/&gt;
 *     &lt;enumeration value="SERVICE_DEFAULT"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "SignatureOptionType")
@XmlEnum
public enum SignatureOptionType {

    ADULT,
    DIRECT,
    INDIRECT,
    NO_SIGNATURE_REQUIRED,
    SERVICE_DEFAULT;

    public String value() {
        return name();
    }

    public static SignatureOptionType fromValue(String v) {
        return valueOf(v);
    }

}
