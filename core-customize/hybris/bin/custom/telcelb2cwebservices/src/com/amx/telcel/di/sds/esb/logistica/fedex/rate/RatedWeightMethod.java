
package com.amx.telcel.di.sds.esb.logistica.fedex.rate;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RatedWeightMethod.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="RatedWeightMethod"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="ACTUAL"/&gt;
 *     &lt;enumeration value="AVERAGE_PACKAGE_WEIGHT_MINIMUM"/&gt;
 *     &lt;enumeration value="BALLOON"/&gt;
 *     &lt;enumeration value="DEFAULT_WEIGHT_APPLIED"/&gt;
 *     &lt;enumeration value="DIM"/&gt;
 *     &lt;enumeration value="FREIGHT_MINIMUM"/&gt;
 *     &lt;enumeration value="MIXED"/&gt;
 *     &lt;enumeration value="OVERSIZE"/&gt;
 *     &lt;enumeration value="OVERSIZE_1"/&gt;
 *     &lt;enumeration value="OVERSIZE_2"/&gt;
 *     &lt;enumeration value="OVERSIZE_3"/&gt;
 *     &lt;enumeration value="PACKAGING_MINIMUM"/&gt;
 *     &lt;enumeration value="WEIGHT_BREAK"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "RatedWeightMethod")
@XmlEnum
public enum RatedWeightMethod {

    ACTUAL,
    AVERAGE_PACKAGE_WEIGHT_MINIMUM,
    BALLOON,
    DEFAULT_WEIGHT_APPLIED,
    DIM,
    FREIGHT_MINIMUM,
    MIXED,
    OVERSIZE,
    OVERSIZE_1,
    OVERSIZE_2,
    OVERSIZE_3,
    PACKAGING_MINIMUM,
    WEIGHT_BREAK;

    public String value() {
        return name();
    }

    public static RatedWeightMethod fromValue(String v) {
        return valueOf(v);
    }

}
