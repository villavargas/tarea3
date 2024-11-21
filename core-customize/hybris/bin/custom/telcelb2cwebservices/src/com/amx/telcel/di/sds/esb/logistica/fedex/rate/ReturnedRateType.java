
package com.amx.telcel.di.sds.esb.logistica.fedex.rate;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ReturnedRateType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ReturnedRateType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="PAYOR_ACCOUNT_PACKAGE"/&gt;
 *     &lt;enumeration value="PAYOR_ACCOUNT_SHIPMENT"/&gt;
 *     &lt;enumeration value="PAYOR_LIST_PACKAGE"/&gt;
 *     &lt;enumeration value="PAYOR_LIST_SHIPMENT"/&gt;
 *     &lt;enumeration value="PREFERRED_ACCOUNT_PACKAGE"/&gt;
 *     &lt;enumeration value="PREFERRED_ACCOUNT_SHIPMENT"/&gt;
 *     &lt;enumeration value="PREFERRED_LIST_PACKAGE"/&gt;
 *     &lt;enumeration value="PREFERRED_LIST_SHIPMENT"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "ReturnedRateType")
@XmlEnum
public enum ReturnedRateType {

    PAYOR_ACCOUNT_PACKAGE,
    PAYOR_ACCOUNT_SHIPMENT,
    PAYOR_LIST_PACKAGE,
    PAYOR_LIST_SHIPMENT,
    PREFERRED_ACCOUNT_PACKAGE,
    PREFERRED_ACCOUNT_SHIPMENT,
    PREFERRED_LIST_PACKAGE,
    PREFERRED_LIST_SHIPMENT;

    public String value() {
        return name();
    }

    public static ReturnedRateType fromValue(String v) {
        return valueOf(v);
    }

}
