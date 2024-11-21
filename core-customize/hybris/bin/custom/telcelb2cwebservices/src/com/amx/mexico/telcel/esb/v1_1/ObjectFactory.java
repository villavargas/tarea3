
package com.amx.mexico.telcel.esb.v1_1;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.amx.mexico.telcel.esb.v1_1 package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.amx.mexico.telcel.esb.v1_1
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ControlDataRequestType }
     * 
     */
    public ControlDataRequestType createControlDataRequestType() {
        return new ControlDataRequestType();
    }

    /**
     * Create an instance of {@link ControlDataResponseType }
     * 
     */
    public ControlDataResponseType createControlDataResponseType() {
        return new ControlDataResponseType();
    }

    /**
     * Create an instance of {@link DetailResponseType }
     * 
     */
    public DetailResponseType createDetailResponseType() {
        return new DetailResponseType();
    }

    /**
     * Create an instance of {@link ErrorType }
     * 
     */
    public ErrorType createErrorType() {
        return new ErrorType();
    }

    /**
     * Create an instance of {@link DetailFailType }
     * 
     */
    public DetailFailType createDetailFailType() {
        return new DetailFailType();
    }

}
