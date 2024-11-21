
package mx.com.amx.mexico.telcel.mq.v1_1;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the mx.com.amx.mexico.telcel.mq.v1_1 package. 
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

    private final static QName _OperationNameRequest_QNAME = new QName("http://www.amx.com.mx/mexico/telcel/mq/v1_1", "operationNameRequest");
    private final static QName _OperationNameResponse_QNAME = new QName("http://www.amx.com.mx/mexico/telcel/mq/v1_1", "operationNameResponse");
    private final static QName _OperationNameException_QNAME = new QName("http://www.amx.com.mx/mexico/telcel/mq/v1_1", "operationNameException");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: mx.com.amx.mexico.telcel.mq.v1_1
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link OperationExceptionType }
     * 
     */
    public OperationExceptionType createOperationExceptionType() {
        return new OperationExceptionType();
    }

    /**
     * Create an instance of {@link OperationRequestType }
     * 
     */
    public OperationRequestType createOperationRequestType() {
        return new OperationRequestType();
    }

    /**
     * Create an instance of {@link OperationResponseType }
     * 
     */
    public OperationResponseType createOperationResponseType() {
        return new OperationResponseType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OperationRequestType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link OperationRequestType }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.amx.com.mx/mexico/telcel/mq/v1_1", name = "operationNameRequest")
    public JAXBElement<OperationRequestType> createOperationNameRequest(OperationRequestType value) {
        return new JAXBElement<OperationRequestType>(_OperationNameRequest_QNAME, OperationRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OperationResponseType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link OperationResponseType }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.amx.com.mx/mexico/telcel/mq/v1_1", name = "operationNameResponse")
    public JAXBElement<OperationResponseType> createOperationNameResponse(OperationResponseType value) {
        return new JAXBElement<OperationResponseType>(_OperationNameResponse_QNAME, OperationResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OperationExceptionType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link OperationExceptionType }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.amx.com.mx/mexico/telcel/mq/v1_1", name = "operationNameException")
    public JAXBElement<OperationExceptionType> createOperationNameException(OperationExceptionType value) {
        return new JAXBElement<OperationExceptionType>(_OperationNameException_QNAME, OperationExceptionType.class, null, value);
    }

}
