
package com.amx.telcel.di.sds.esb.logistica.fedex.rate;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DayOfWeekType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="DayOfWeekType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="FRI"/&gt;
 *     &lt;enumeration value="MON"/&gt;
 *     &lt;enumeration value="SAT"/&gt;
 *     &lt;enumeration value="SUN"/&gt;
 *     &lt;enumeration value="THU"/&gt;
 *     &lt;enumeration value="TUE"/&gt;
 *     &lt;enumeration value="WED"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "DayOfWeekType")
@XmlEnum
public enum DayOfWeekType {

    FRI,
    MON,
    SAT,
    SUN,
    THU,
    TUE,
    WED;

    public String value() {
        return name();
    }

    public static DayOfWeekType fromValue(String v) {
        return valueOf(v);
    }

}
