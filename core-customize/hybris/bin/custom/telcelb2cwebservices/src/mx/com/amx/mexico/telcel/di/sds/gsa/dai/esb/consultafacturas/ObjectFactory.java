
package mx.com.amx.mexico.telcel.di.sds.gsa.dai.esb.consultafacturas;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the mx.com.amx.mexico.telcel.di.sds.gsa.dai.esb.consultafacturas package. 
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

    private final static QName _ConsultaFacturaPDFXML_QNAME = new QName("http://www.amx.com.mx/mexico/telcel/di/sds/gsa/dai/esb/consultafacturas", "consultaFacturaPDFXML");
    private final static QName _ConsultaFacturaPDFXMLResponse_QNAME = new QName("http://www.amx.com.mx/mexico/telcel/di/sds/gsa/dai/esb/consultafacturas", "consultaFacturaPDFXMLResponse");
    private final static QName _ConsultaFacturaPDFXMLException_QNAME = new QName("http://www.amx.com.mx/mexico/telcel/di/sds/gsa/dai/esb/consultafacturas", "consultaFacturaPDFXMLException");
    private final static QName _ConsultaFacturaPDFXMLPeticion_QNAME = new QName("http://www.amx.com.mx/mexico/telcel/di/sds/gsa/dai/esb/consultafacturas", "consultaFacturaPDFXMLPeticion");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: mx.com.amx.mexico.telcel.di.sds.gsa.dai.esb.consultafacturas
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ConsultaFacturaPDFXML }
     * 
     */
    public ConsultaFacturaPDFXML createConsultaFacturaPDFXML() {
        return new ConsultaFacturaPDFXML();
    }

    /**
     * Create an instance of {@link ConsultaFacturaPDFXMLResponse }
     * 
     */
    public ConsultaFacturaPDFXMLResponse createConsultaFacturaPDFXMLResponse() {
        return new ConsultaFacturaPDFXMLResponse();
    }

    /**
     * Create an instance of {@link ConsultaFacturasException }
     * 
     */
    public ConsultaFacturasException createConsultaFacturasException() {
        return new ConsultaFacturasException();
    }

    /**
     * Create an instance of {@link ConsultaFacturaPDFXMLPetType }
     * 
     */
    public ConsultaFacturaPDFXMLPetType createConsultaFacturaPDFXMLPetType() {
        return new ConsultaFacturaPDFXMLPetType();
    }

    /**
     * Create an instance of {@link FacturaPDFXMLType }
     * 
     */
    public FacturaPDFXMLType createFacturaPDFXMLType() {
        return new FacturaPDFXMLType();
    }

    /**
     * Create an instance of {@link ErrorFacturaType }
     * 
     */
    public ErrorFacturaType createErrorFacturaType() {
        return new ErrorFacturaType();
    }

    /**
     * Create an instance of {@link ConsultaFacturaPDFXMLRespType }
     * 
     */
    public ConsultaFacturaPDFXMLRespType createConsultaFacturaPDFXMLRespType() {
        return new ConsultaFacturaPDFXMLRespType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConsultaFacturaPDFXML }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ConsultaFacturaPDFXML }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.amx.com.mx/mexico/telcel/di/sds/gsa/dai/esb/consultafacturas", name = "consultaFacturaPDFXML")
    public JAXBElement<ConsultaFacturaPDFXML> createConsultaFacturaPDFXML(ConsultaFacturaPDFXML value) {
        return new JAXBElement<ConsultaFacturaPDFXML>(_ConsultaFacturaPDFXML_QNAME, ConsultaFacturaPDFXML.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConsultaFacturaPDFXMLResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ConsultaFacturaPDFXMLResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.amx.com.mx/mexico/telcel/di/sds/gsa/dai/esb/consultafacturas", name = "consultaFacturaPDFXMLResponse")
    public JAXBElement<ConsultaFacturaPDFXMLResponse> createConsultaFacturaPDFXMLResponse(ConsultaFacturaPDFXMLResponse value) {
        return new JAXBElement<ConsultaFacturaPDFXMLResponse>(_ConsultaFacturaPDFXMLResponse_QNAME, ConsultaFacturaPDFXMLResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConsultaFacturasException }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ConsultaFacturasException }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.amx.com.mx/mexico/telcel/di/sds/gsa/dai/esb/consultafacturas", name = "consultaFacturaPDFXMLException")
    public JAXBElement<ConsultaFacturasException> createConsultaFacturaPDFXMLException(ConsultaFacturasException value) {
        return new JAXBElement<ConsultaFacturasException>(_ConsultaFacturaPDFXMLException_QNAME, ConsultaFacturasException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConsultaFacturaPDFXMLPetType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ConsultaFacturaPDFXMLPetType }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.amx.com.mx/mexico/telcel/di/sds/gsa/dai/esb/consultafacturas", name = "consultaFacturaPDFXMLPeticion")
    public JAXBElement<ConsultaFacturaPDFXMLPetType> createConsultaFacturaPDFXMLPeticion(ConsultaFacturaPDFXMLPetType value) {
        return new JAXBElement<ConsultaFacturaPDFXMLPetType>(_ConsultaFacturaPDFXMLPeticion_QNAME, ConsultaFacturaPDFXMLPetType.class, null, value);
    }

}
