
package com.amx.mexico.telcel.esb.v1_0_2;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.amx.mexico.telcel.esb.v1_0_2 package. 
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

    private final static QName _RequestOperation_QNAME = new QName("http://amx.com/mexico/telcel/esb/v1_0_2", "RequestOperation");
    private final static QName _ResponseOperation_QNAME = new QName("http://amx.com/mexico/telcel/esb/v1_0_2", "ResponseOperation");
    private final static QName _DetailFail_QNAME = new QName("http://amx.com/mexico/telcel/esb/v1_0_2", "DetailFail");
    private final static QName _RequestHeader_QNAME = new QName("http://amx.com/mexico/telcel/esb/v1_0_2", "RequestHeader");
    private final static QName _ResponseHeader_QNAME = new QName("http://amx.com/mexico/telcel/esb/v1_0_2", "ResponseHeader");
    private final static QName _DetailResponse_QNAME = new QName("http://amx.com/mexico/telcel/esb/v1_0_2", "DetailResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.amx.mexico.telcel.esb.v1_0_2
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DetailType }
     * 
     */
    public DetailType createDetailType() {
        return new DetailType();
    }

    /**
     * Create an instance of {@link RequestOperationType }
     * 
     */
    public RequestOperationType createRequestOperationType() {
        return new RequestOperationType();
    }

    /**
     * Create an instance of {@link ResponseOperationType }
     * 
     */
    public ResponseOperationType createResponseOperationType() {
        return new ResponseOperationType();
    }

    /**
     * Create an instance of {@link RequestHeaderType }
     * 
     */
    public RequestHeaderType createRequestHeaderType() {
        return new RequestHeaderType();
    }

    /**
     * Create an instance of {@link ResponseHeaderType }
     * 
     */
    public ResponseHeaderType createResponseHeaderType() {
        return new ResponseHeaderType();
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
     * Create an instance of {@link JAXBElement }{@code <}{@link RequestOperationType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link RequestOperationType }{@code >}
     */
    @XmlElementDecl(namespace = "http://amx.com/mexico/telcel/esb/v1_0_2", name = "RequestOperation")
    public JAXBElement<RequestOperationType> createRequestOperation(RequestOperationType value) {
        return new JAXBElement<RequestOperationType>(_RequestOperation_QNAME, RequestOperationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ResponseOperationType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ResponseOperationType }{@code >}
     */
    @XmlElementDecl(namespace = "http://amx.com/mexico/telcel/esb/v1_0_2", name = "ResponseOperation")
    public JAXBElement<ResponseOperationType> createResponseOperation(ResponseOperationType value) {
        return new JAXBElement<ResponseOperationType>(_ResponseOperation_QNAME, ResponseOperationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DetailType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link DetailType }{@code >}
     */
    @XmlElementDecl(namespace = "http://amx.com/mexico/telcel/esb/v1_0_2", name = "DetailFail")
    public JAXBElement<DetailType> createDetailFail(DetailType value) {
        return new JAXBElement<DetailType>(_DetailFail_QNAME, DetailType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RequestHeaderType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link RequestHeaderType }{@code >}
     */
    @XmlElementDecl(namespace = "http://amx.com/mexico/telcel/esb/v1_0_2", name = "RequestHeader")
    public JAXBElement<RequestHeaderType> createRequestHeader(RequestHeaderType value) {
        return new JAXBElement<RequestHeaderType>(_RequestHeader_QNAME, RequestHeaderType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ResponseHeaderType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ResponseHeaderType }{@code >}
     */
    @XmlElementDecl(namespace = "http://amx.com/mexico/telcel/esb/v1_0_2", name = "ResponseHeader")
    public JAXBElement<ResponseHeaderType> createResponseHeader(ResponseHeaderType value) {
        return new JAXBElement<ResponseHeaderType>(_ResponseHeader_QNAME, ResponseHeaderType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DetailResponseType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link DetailResponseType }{@code >}
     */
    @XmlElementDecl(namespace = "http://amx.com/mexico/telcel/esb/v1_0_2", name = "DetailResponse")
    public JAXBElement<DetailResponseType> createDetailResponse(DetailResponseType value) {
        return new JAXBElement<DetailResponseType>(_DetailResponse_QNAME, DetailResponseType.class, null, value);
    }

}
