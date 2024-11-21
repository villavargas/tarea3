
package com.amx.telcel.di.sds.esb.sap.inventario;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import mx.com.amx.mexico.telcel.mq.v1_1.OperationExceptionType;
import mx.com.amx.mexico.telcel.mq.v1_1.OperationRequestType;
import mx.com.amx.mexico.telcel.mq.v1_1.OperationResponseType;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.amx.telcel.di.sds.esb.sap.inventario package. 
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

    private final static QName _ConsultarExistencias_QNAME = new QName("http://www.amx.com/telcel/di/sds/esb/sap/inventario", "consultarExistencias");
    private final static QName _ConsultarExistenciasResponse_QNAME = new QName("http://www.amx.com/telcel/di/sds/esb/sap/inventario", "consultarExistenciasResponse");
    private final static QName _ConsultarExistenciasException_QNAME = new QName("http://www.amx.com/telcel/di/sds/esb/sap/inventario", "consultarExistenciasException");
    private final static QName _ConsultarExistenciasNPeticion_QNAME = new QName("http://www.amx.com/telcel/di/sds/esb/sap/inventario", "consultarExistenciasNPeticion");
    private final static QName _ConsultarExistenciasNRespuesta_QNAME = new QName("http://www.amx.com/telcel/di/sds/esb/sap/inventario", "consultarExistenciasNRespuesta");
    private final static QName _ConsultarProductosPeticion_QNAME = new QName("http://www.amx.com/telcel/di/sds/esb/sap/inventario", "consultarProductosPeticion");
    private final static QName _ConsultarProductosRespuesta_QNAME = new QName("http://www.amx.com/telcel/di/sds/esb/sap/inventario", "consultarProductosRespuesta");
    private final static QName _GeneralException_QNAME = new QName("http://www.amx.com/telcel/di/sds/esb/sap/inventario", "generalException");
    private final static QName _GeneralExceptionMQ_QNAME = new QName("http://www.amx.com/telcel/di/sds/esb/sap/inventario", "generalExceptionMQ");
    private final static QName _ConsultarExistenciasPeticion_QNAME = new QName("http://www.amx.com/telcel/di/sds/esb/sap/inventario", "consultarExistenciasPeticion");
    private final static QName _ConsultarProductosException_QNAME = new QName("http://www.amx.com/telcel/di/sds/esb/sap/inventario", "consultarProductosException");
    private final static QName _ConsultarProductos_QNAME = new QName("http://www.amx.com/telcel/di/sds/esb/sap/inventario", "consultarProductos");
    private final static QName _ConsultarProductosResponse_QNAME = new QName("http://www.amx.com/telcel/di/sds/esb/sap/inventario", "consultarProductosResponse");
    private final static QName _ConsultarExistenciasN_QNAME = new QName("http://www.amx.com/telcel/di/sds/esb/sap/inventario", "consultarExistenciasN");
    private final static QName _ConsultarExistenciasNResponse_QNAME = new QName("http://www.amx.com/telcel/di/sds/esb/sap/inventario", "consultarExistenciasNResponse");
    private final static QName _ConsultarExistenciasNException_QNAME = new QName("http://www.amx.com/telcel/di/sds/esb/sap/inventario", "consultarExistenciasNException");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.amx.telcel.di.sds.esb.sap.inventario
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ConsultarExistenciasOpPetType }
     * 
     */
    public ConsultarExistenciasOpPetType createConsultarExistenciasOpPetType() {
        return new ConsultarExistenciasOpPetType();
    }

    /**
     * Create an instance of {@link ConsultarExistenciasOpRespType }
     * 
     */
    public ConsultarExistenciasOpRespType createConsultarExistenciasOpRespType() {
        return new ConsultarExistenciasOpRespType();
    }

    /**
     * Create an instance of {@link InventarioProductosException }
     * 
     */
    public InventarioProductosException createInventarioProductosException() {
        return new InventarioProductosException();
    }

    /**
     * Create an instance of {@link ConsultarExistenciasNPetType }
     * 
     */
    public ConsultarExistenciasNPetType createConsultarExistenciasNPetType() {
        return new ConsultarExistenciasNPetType();
    }

    /**
     * Create an instance of {@link ConsultarExistenciasRespType }
     * 
     */
    public ConsultarExistenciasRespType createConsultarExistenciasRespType() {
        return new ConsultarExistenciasRespType();
    }

    /**
     * Create an instance of {@link ConsultarProductosPetType }
     * 
     */
    public ConsultarProductosPetType createConsultarProductosPetType() {
        return new ConsultarProductosPetType();
    }

    /**
     * Create an instance of {@link ConsultarProductosRespType }
     * 
     */
    public ConsultarProductosRespType createConsultarProductosRespType() {
        return new ConsultarProductosRespType();
    }

    /**
     * Create an instance of {@link ConsultarExistenciasType }
     * 
     */
    public ConsultarExistenciasType createConsultarExistenciasType() {
        return new ConsultarExistenciasType();
    }

    /**
     * Create an instance of {@link MaterialAlmacenType }
     * 
     */
    public MaterialAlmacenType createMaterialAlmacenType() {
        return new MaterialAlmacenType();
    }

    /**
     * Create an instance of {@link ExistenciaType }
     * 
     */
    public ExistenciaType createExistenciaType() {
        return new ExistenciaType();
    }

    /**
     * Create an instance of {@link MaterialType }
     * 
     */
    public MaterialType createMaterialType() {
        return new MaterialType();
    }

    /**
     * Create an instance of {@link ResultadoType }
     * 
     */
    public ResultadoType createResultadoType() {
        return new ResultadoType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConsultarExistenciasOpPetType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ConsultarExistenciasOpPetType }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.amx.com/telcel/di/sds/esb/sap/inventario", name = "consultarExistencias")
    public JAXBElement<ConsultarExistenciasOpPetType> createConsultarExistencias(ConsultarExistenciasOpPetType value) {
        return new JAXBElement<ConsultarExistenciasOpPetType>(_ConsultarExistencias_QNAME, ConsultarExistenciasOpPetType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConsultarExistenciasOpRespType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ConsultarExistenciasOpRespType }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.amx.com/telcel/di/sds/esb/sap/inventario", name = "consultarExistenciasResponse")
    public JAXBElement<ConsultarExistenciasOpRespType> createConsultarExistenciasResponse(ConsultarExistenciasOpRespType value) {
        return new JAXBElement<ConsultarExistenciasOpRespType>(_ConsultarExistenciasResponse_QNAME, ConsultarExistenciasOpRespType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InventarioProductosException }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link InventarioProductosException }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.amx.com/telcel/di/sds/esb/sap/inventario", name = "consultarExistenciasException")
    public JAXBElement<InventarioProductosException> createConsultarExistenciasException(InventarioProductosException value) {
        return new JAXBElement<InventarioProductosException>(_ConsultarExistenciasException_QNAME, InventarioProductosException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConsultarExistenciasNPetType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ConsultarExistenciasNPetType }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.amx.com/telcel/di/sds/esb/sap/inventario", name = "consultarExistenciasNPeticion")
    public JAXBElement<ConsultarExistenciasNPetType> createConsultarExistenciasNPeticion(ConsultarExistenciasNPetType value) {
        return new JAXBElement<ConsultarExistenciasNPetType>(_ConsultarExistenciasNPeticion_QNAME, ConsultarExistenciasNPetType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConsultarExistenciasRespType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ConsultarExistenciasRespType }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.amx.com/telcel/di/sds/esb/sap/inventario", name = "consultarExistenciasNRespuesta")
    public JAXBElement<ConsultarExistenciasRespType> createConsultarExistenciasNRespuesta(ConsultarExistenciasRespType value) {
        return new JAXBElement<ConsultarExistenciasRespType>(_ConsultarExistenciasNRespuesta_QNAME, ConsultarExistenciasRespType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConsultarProductosPetType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ConsultarProductosPetType }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.amx.com/telcel/di/sds/esb/sap/inventario", name = "consultarProductosPeticion")
    public JAXBElement<ConsultarProductosPetType> createConsultarProductosPeticion(ConsultarProductosPetType value) {
        return new JAXBElement<ConsultarProductosPetType>(_ConsultarProductosPeticion_QNAME, ConsultarProductosPetType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConsultarProductosRespType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ConsultarProductosRespType }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.amx.com/telcel/di/sds/esb/sap/inventario", name = "consultarProductosRespuesta")
    public JAXBElement<ConsultarProductosRespType> createConsultarProductosRespuesta(ConsultarProductosRespType value) {
        return new JAXBElement<ConsultarProductosRespType>(_ConsultarProductosRespuesta_QNAME, ConsultarProductosRespType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InventarioProductosException }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link InventarioProductosException }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.amx.com/telcel/di/sds/esb/sap/inventario", name = "generalException")
    public JAXBElement<InventarioProductosException> createGeneralException(InventarioProductosException value) {
        return new JAXBElement<InventarioProductosException>(_GeneralException_QNAME, InventarioProductosException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OperationExceptionType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link OperationExceptionType }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.amx.com/telcel/di/sds/esb/sap/inventario", name = "generalExceptionMQ")
    public JAXBElement<OperationExceptionType> createGeneralExceptionMQ(OperationExceptionType value) {
        return new JAXBElement<OperationExceptionType>(_GeneralExceptionMQ_QNAME, OperationExceptionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConsultarExistenciasType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ConsultarExistenciasType }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.amx.com/telcel/di/sds/esb/sap/inventario", name = "consultarExistenciasPeticion")
    public JAXBElement<ConsultarExistenciasType> createConsultarExistenciasPeticion(ConsultarExistenciasType value) {
        return new JAXBElement<ConsultarExistenciasType>(_ConsultarExistenciasPeticion_QNAME, ConsultarExistenciasType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OperationExceptionType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link OperationExceptionType }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.amx.com/telcel/di/sds/esb/sap/inventario", name = "consultarProductosException")
    public JAXBElement<OperationExceptionType> createConsultarProductosException(OperationExceptionType value) {
        return new JAXBElement<OperationExceptionType>(_ConsultarProductosException_QNAME, OperationExceptionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OperationRequestType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link OperationRequestType }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.amx.com/telcel/di/sds/esb/sap/inventario", name = "consultarProductos")
    public JAXBElement<OperationRequestType> createConsultarProductos(OperationRequestType value) {
        return new JAXBElement<OperationRequestType>(_ConsultarProductos_QNAME, OperationRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OperationResponseType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link OperationResponseType }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.amx.com/telcel/di/sds/esb/sap/inventario", name = "consultarProductosResponse")
    public JAXBElement<OperationResponseType> createConsultarProductosResponse(OperationResponseType value) {
        return new JAXBElement<OperationResponseType>(_ConsultarProductosResponse_QNAME, OperationResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OperationRequestType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link OperationRequestType }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.amx.com/telcel/di/sds/esb/sap/inventario", name = "consultarExistenciasN")
    public JAXBElement<OperationRequestType> createConsultarExistenciasN(OperationRequestType value) {
        return new JAXBElement<OperationRequestType>(_ConsultarExistenciasN_QNAME, OperationRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OperationResponseType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link OperationResponseType }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.amx.com/telcel/di/sds/esb/sap/inventario", name = "consultarExistenciasNResponse")
    public JAXBElement<OperationResponseType> createConsultarExistenciasNResponse(OperationResponseType value) {
        return new JAXBElement<OperationResponseType>(_ConsultarExistenciasNResponse_QNAME, OperationResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OperationExceptionType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link OperationExceptionType }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.amx.com/telcel/di/sds/esb/sap/inventario", name = "consultarExistenciasNException")
    public JAXBElement<OperationExceptionType> createConsultarExistenciasNException(OperationExceptionType value) {
        return new JAXBElement<OperationExceptionType>(_ConsultarExistenciasNException_QNAME, OperationExceptionType.class, null, value);
    }

}
