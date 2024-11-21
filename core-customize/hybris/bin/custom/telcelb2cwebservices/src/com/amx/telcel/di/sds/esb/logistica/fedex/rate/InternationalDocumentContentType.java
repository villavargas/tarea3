
package com.amx.telcel.di.sds.esb.logistica.fedex.rate;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for InternationalDocumentContentType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="InternationalDocumentContentType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="DOCUMENTS_ONLY"/&gt;
 *     &lt;enumeration value="NON_DOCUMENTS"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "InternationalDocumentContentType")
@XmlEnum
public enum InternationalDocumentContentType {

    DOCUMENTS_ONLY,
    NON_DOCUMENTS;

    public String value() {
        return name();
    }

    public static InternationalDocumentContentType fromValue(String v) {
        return valueOf(v);
    }

}
