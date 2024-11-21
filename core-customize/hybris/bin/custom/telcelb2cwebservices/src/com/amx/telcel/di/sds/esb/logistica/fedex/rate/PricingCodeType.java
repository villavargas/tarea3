
package com.amx.telcel.di.sds.esb.logistica.fedex.rate;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PricingCodeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PricingCodeType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="ACTUAL"/&gt;
 *     &lt;enumeration value="ALTERNATE"/&gt;
 *     &lt;enumeration value="BASE"/&gt;
 *     &lt;enumeration value="HUNDREDWEIGHT"/&gt;
 *     &lt;enumeration value="HUNDREDWEIGHT_ALTERNATE"/&gt;
 *     &lt;enumeration value="INTERNATIONAL_DISTRIBUTION"/&gt;
 *     &lt;enumeration value="INTERNATIONAL_ECONOMY_SERVICE"/&gt;
 *     &lt;enumeration value="LTL_FREIGHT"/&gt;
 *     &lt;enumeration value="PACKAGE"/&gt;
 *     &lt;enumeration value="SHIPMENT"/&gt;
 *     &lt;enumeration value="SHIPMENT_FIVE_POUND_OPTIONAL"/&gt;
 *     &lt;enumeration value="SHIPMENT_OPTIONAL"/&gt;
 *     &lt;enumeration value="SPECIAL"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "PricingCodeType")
@XmlEnum
public enum PricingCodeType {

    ACTUAL,
    ALTERNATE,
    BASE,
    HUNDREDWEIGHT,
    HUNDREDWEIGHT_ALTERNATE,
    INTERNATIONAL_DISTRIBUTION,
    INTERNATIONAL_ECONOMY_SERVICE,
    LTL_FREIGHT,
    PACKAGE,
    SHIPMENT,
    SHIPMENT_FIVE_POUND_OPTIONAL,
    SHIPMENT_OPTIONAL,
    SPECIAL;

    public String value() {
        return name();
    }

    public static PricingCodeType fromValue(String v) {
        return valueOf(v);
    }

}
