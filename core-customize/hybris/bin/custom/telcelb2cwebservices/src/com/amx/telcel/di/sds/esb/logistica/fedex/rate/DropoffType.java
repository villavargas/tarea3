
package com.amx.telcel.di.sds.esb.logistica.fedex.rate;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DropoffType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="DropoffType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="BUSINESS_SERVICE_CENTER"/&gt;
 *     &lt;enumeration value="DROP_BOX"/&gt;
 *     &lt;enumeration value="REGULAR_PICKUP"/&gt;
 *     &lt;enumeration value="REQUEST_COURIER"/&gt;
 *     &lt;enumeration value="STATION"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "DropoffType")
@XmlEnum
public enum DropoffType {

    BUSINESS_SERVICE_CENTER,
    DROP_BOX,
    REGULAR_PICKUP,
    REQUEST_COURIER,
    STATION;

    public String value() {
        return name();
    }

    public static DropoffType fromValue(String v) {
        return valueOf(v);
    }

}
