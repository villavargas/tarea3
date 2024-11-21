
package com.amx.telcel.di.sds.esb.logistica.fedex.rate;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SurchargeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="SurchargeType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="ADDITIONAL_HANDLING"/&gt;
 *     &lt;enumeration value="ANCILLARY_FEE"/&gt;
 *     &lt;enumeration value="APPOINTMENT_DELIVERY"/&gt;
 *     &lt;enumeration value="BROKER_SELECT_OPTION"/&gt;
 *     &lt;enumeration value="CANADIAN_DESTINATION"/&gt;
 *     &lt;enumeration value="CLEARANCE_ENTRY_FEE"/&gt;
 *     &lt;enumeration value="COD"/&gt;
 *     &lt;enumeration value="CUT_FLOWERS"/&gt;
 *     &lt;enumeration value="DANGEROUS_GOODS"/&gt;
 *     &lt;enumeration value="DELIVERY_AREA"/&gt;
 *     &lt;enumeration value="DELIVERY_CONFIRMATION"/&gt;
 *     &lt;enumeration value="DOCUMENTATION_FEE"/&gt;
 *     &lt;enumeration value="DRY_ICE"/&gt;
 *     &lt;enumeration value="EMAIL_LABEL"/&gt;
 *     &lt;enumeration value="EUROPE_FIRST"/&gt;
 *     &lt;enumeration value="EXCESS_VALUE"/&gt;
 *     &lt;enumeration value="EXHIBITION"/&gt;
 *     &lt;enumeration value="EXPORT"/&gt;
 *     &lt;enumeration value="EXTRA_SURFACE_HANDLING_CHARGE"/&gt;
 *     &lt;enumeration value="EXTREME_LENGTH"/&gt;
 *     &lt;enumeration value="FEDEX_INTRACOUNTRY_FEES"/&gt;
 *     &lt;enumeration value="FEDEX_TAG"/&gt;
 *     &lt;enumeration value="FICE"/&gt;
 *     &lt;enumeration value="FLATBED"/&gt;
 *     &lt;enumeration value="FREIGHT_GUARANTEE"/&gt;
 *     &lt;enumeration value="FREIGHT_ON_VALUE"/&gt;
 *     &lt;enumeration value="FREIGHT_TO_COLLECT"/&gt;
 *     &lt;enumeration value="FUEL"/&gt;
 *     &lt;enumeration value="HOLD_AT_LOCATION"/&gt;
 *     &lt;enumeration value="HOME_DELIVERY_APPOINTMENT"/&gt;
 *     &lt;enumeration value="HOME_DELIVERY_DATE_CERTAIN"/&gt;
 *     &lt;enumeration value="HOME_DELIVERY_EVENING"/&gt;
 *     &lt;enumeration value="INSIDE_DELIVERY"/&gt;
 *     &lt;enumeration value="INSIDE_PICKUP"/&gt;
 *     &lt;enumeration value="INSURED_VALUE"/&gt;
 *     &lt;enumeration value="INTERHAWAII"/&gt;
 *     &lt;enumeration value="LIFTGATE_DELIVERY"/&gt;
 *     &lt;enumeration value="LIFTGATE_PICKUP"/&gt;
 *     &lt;enumeration value="LIMITED_ACCESS_DELIVERY"/&gt;
 *     &lt;enumeration value="LIMITED_ACCESS_PICKUP"/&gt;
 *     &lt;enumeration value="METRO_DELIVERY"/&gt;
 *     &lt;enumeration value="METRO_PICKUP"/&gt;
 *     &lt;enumeration value="NON_MACHINABLE"/&gt;
 *     &lt;enumeration value="OFFSHORE"/&gt;
 *     &lt;enumeration value="ON_CALL_PICKUP"/&gt;
 *     &lt;enumeration value="OTHER"/&gt;
 *     &lt;enumeration value="OUT_OF_DELIVERY_AREA"/&gt;
 *     &lt;enumeration value="OUT_OF_PICKUP_AREA"/&gt;
 *     &lt;enumeration value="OVERSIZE"/&gt;
 *     &lt;enumeration value="OVER_DIMENSION"/&gt;
 *     &lt;enumeration value="PIECE_COUNT_VERIFICATION"/&gt;
 *     &lt;enumeration value="PRE_DELIVERY_NOTIFICATION"/&gt;
 *     &lt;enumeration value="PRIORITY_ALERT"/&gt;
 *     &lt;enumeration value="PROTECTION_FROM_FREEZING"/&gt;
 *     &lt;enumeration value="REGIONAL_MALL_DELIVERY"/&gt;
 *     &lt;enumeration value="REGIONAL_MALL_PICKUP"/&gt;
 *     &lt;enumeration value="REROUTE"/&gt;
 *     &lt;enumeration value="RESCHEDULE"/&gt;
 *     &lt;enumeration value="RESIDENTIAL_DELIVERY"/&gt;
 *     &lt;enumeration value="RESIDENTIAL_PICKUP"/&gt;
 *     &lt;enumeration value="RETURN_LABEL"/&gt;
 *     &lt;enumeration value="SATURDAY_DELIVERY"/&gt;
 *     &lt;enumeration value="SATURDAY_PICKUP"/&gt;
 *     &lt;enumeration value="SIGNATURE_OPTION"/&gt;
 *     &lt;enumeration value="TARP"/&gt;
 *     &lt;enumeration value="THIRD_PARTY_CONSIGNEE"/&gt;
 *     &lt;enumeration value="TRANSMART_SERVICE_FEE"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "SurchargeType")
@XmlEnum
public enum SurchargeType {

    ADDITIONAL_HANDLING,
    ANCILLARY_FEE,
    APPOINTMENT_DELIVERY,
    BROKER_SELECT_OPTION,
    CANADIAN_DESTINATION,
    CLEARANCE_ENTRY_FEE,
    COD,
    CUT_FLOWERS,
    DANGEROUS_GOODS,
    DELIVERY_AREA,
    DELIVERY_CONFIRMATION,
    DOCUMENTATION_FEE,
    DRY_ICE,
    EMAIL_LABEL,
    EUROPE_FIRST,
    EXCESS_VALUE,
    EXHIBITION,
    EXPORT,
    EXTRA_SURFACE_HANDLING_CHARGE,
    EXTREME_LENGTH,
    FEDEX_INTRACOUNTRY_FEES,
    FEDEX_TAG,
    FICE,
    FLATBED,
    FREIGHT_GUARANTEE,
    FREIGHT_ON_VALUE,
    FREIGHT_TO_COLLECT,
    FUEL,
    HOLD_AT_LOCATION,
    HOME_DELIVERY_APPOINTMENT,
    HOME_DELIVERY_DATE_CERTAIN,
    HOME_DELIVERY_EVENING,
    INSIDE_DELIVERY,
    INSIDE_PICKUP,
    INSURED_VALUE,
    INTERHAWAII,
    LIFTGATE_DELIVERY,
    LIFTGATE_PICKUP,
    LIMITED_ACCESS_DELIVERY,
    LIMITED_ACCESS_PICKUP,
    METRO_DELIVERY,
    METRO_PICKUP,
    NON_MACHINABLE,
    OFFSHORE,
    ON_CALL_PICKUP,
    OTHER,
    OUT_OF_DELIVERY_AREA,
    OUT_OF_PICKUP_AREA,
    OVERSIZE,
    OVER_DIMENSION,
    PIECE_COUNT_VERIFICATION,
    PRE_DELIVERY_NOTIFICATION,
    PRIORITY_ALERT,
    PROTECTION_FROM_FREEZING,
    REGIONAL_MALL_DELIVERY,
    REGIONAL_MALL_PICKUP,
    REROUTE,
    RESCHEDULE,
    RESIDENTIAL_DELIVERY,
    RESIDENTIAL_PICKUP,
    RETURN_LABEL,
    SATURDAY_DELIVERY,
    SATURDAY_PICKUP,
    SIGNATURE_OPTION,
    TARP,
    THIRD_PARTY_CONSIGNEE,
    TRANSMART_SERVICE_FEE;

    public String value() {
        return name();
    }

    public static SurchargeType fromValue(String v) {
        return valueOf(v);
    }

}
