
package com.amx.telcel.di.sds.esb.logistica.fedex.rate;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ServiceType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ServiceType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="EUROPE_FIRST_INTERNATIONAL_PRIORITY"/&gt;
 *     &lt;enumeration value="FEDEX_1_DAY_FREIGHT"/&gt;
 *     &lt;enumeration value="FEDEX_2_DAY"/&gt;
 *     &lt;enumeration value="FEDEX_2_DAY_AM"/&gt;
 *     &lt;enumeration value="FEDEX_2_DAY_FREIGHT"/&gt;
 *     &lt;enumeration value="FEDEX_3_DAY_FREIGHT"/&gt;
 *     &lt;enumeration value="FEDEX_DISTANCE_DEFERRED"/&gt;
 *     &lt;enumeration value="FEDEX_EXPRESS_SAVER"/&gt;
 *     &lt;enumeration value="FEDEX_FIRST_FREIGHT"/&gt;
 *     &lt;enumeration value="FEDEX_FREIGHT_ECONOMY"/&gt;
 *     &lt;enumeration value="FEDEX_FREIGHT_PRIORITY"/&gt;
 *     &lt;enumeration value="FEDEX_GROUND"/&gt;
 *     &lt;enumeration value="FEDEX_NEXT_DAY_AFTERNOON"/&gt;
 *     &lt;enumeration value="FEDEX_NEXT_DAY_EARLY_MORNING"/&gt;
 *     &lt;enumeration value="FEDEX_NEXT_DAY_END_OF_DAY"/&gt;
 *     &lt;enumeration value="FEDEX_NEXT_DAY_FREIGHT"/&gt;
 *     &lt;enumeration value="FEDEX_NEXT_DAY_MID_MORNING"/&gt;
 *     &lt;enumeration value="FIRST_OVERNIGHT"/&gt;
 *     &lt;enumeration value="GROUND_HOME_DELIVERY"/&gt;
 *     &lt;enumeration value="INTERNATIONAL_ECONOMY"/&gt;
 *     &lt;enumeration value="INTERNATIONAL_ECONOMY_FREIGHT"/&gt;
 *     &lt;enumeration value="INTERNATIONAL_FIRST"/&gt;
 *     &lt;enumeration value="INTERNATIONAL_PRIORITY"/&gt;
 *     &lt;enumeration value="INTERNATIONAL_PRIORITY_FREIGHT"/&gt;
 *     &lt;enumeration value="PRIORITY_OVERNIGHT"/&gt;
 *     &lt;enumeration value="SAME_DAY"/&gt;
 *     &lt;enumeration value="SAME_DAY_CITY"/&gt;
 *     &lt;enumeration value="SMART_POST"/&gt;
 *     &lt;enumeration value="STANDARD_OVERNIGHT"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "ServiceType")
@XmlEnum
public enum ServiceType {

    EUROPE_FIRST_INTERNATIONAL_PRIORITY,
    FEDEX_1_DAY_FREIGHT,
    FEDEX_2_DAY,
    FEDEX_2_DAY_AM,
    FEDEX_2_DAY_FREIGHT,
    FEDEX_3_DAY_FREIGHT,
    FEDEX_DISTANCE_DEFERRED,
    FEDEX_EXPRESS_SAVER,
    FEDEX_FIRST_FREIGHT,
    FEDEX_FREIGHT_ECONOMY,
    FEDEX_FREIGHT_PRIORITY,
    FEDEX_GROUND,
    FEDEX_NEXT_DAY_AFTERNOON,
    FEDEX_NEXT_DAY_EARLY_MORNING,
    FEDEX_NEXT_DAY_END_OF_DAY,
    FEDEX_NEXT_DAY_FREIGHT,
    FEDEX_NEXT_DAY_MID_MORNING,
    FIRST_OVERNIGHT,
    GROUND_HOME_DELIVERY,
    INTERNATIONAL_ECONOMY,
    INTERNATIONAL_ECONOMY_FREIGHT,
    INTERNATIONAL_FIRST,
    INTERNATIONAL_PRIORITY,
    INTERNATIONAL_PRIORITY_FREIGHT,
    PRIORITY_OVERNIGHT,
    SAME_DAY,
    SAME_DAY_CITY,
    SMART_POST,
    STANDARD_OVERNIGHT;

    public String value() {
        return name();
    }

    public static ServiceType fromValue(String v) {
        return valueOf(v);
    }

}
