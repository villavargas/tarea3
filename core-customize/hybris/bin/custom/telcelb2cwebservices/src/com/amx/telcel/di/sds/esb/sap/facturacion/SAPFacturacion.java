
package com.amx.telcel.di.sds.esb.sap.facturacion;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Holder;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import com.amx.mexico.telcel.esb.v1_0_2.RequestHeaderType;
import com.amx.mexico.telcel.esb.v1_0_2.ResponseHeaderType;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.3.2
 * Generated source version: 2.2
 * 
 */
@WebService(name = "SAPFacturacion", targetNamespace = "http://www.amx.com/telcel/di/sds/esb/sap/facturacion")
@XmlSeeAlso({
    com.amx.mexico.telcel.esb.v1_0_2.ObjectFactory.class,
    com.amx.telcel.di.sds.esb.sap.facturacion.ObjectFactory.class
})
public interface SAPFacturacion {


    /**
     * 
     * @param encabezado0
     * @param encabezado
     * @param respuesta
     * @param peticion
     * @throws RealizaPedidoFaultMsg
     */
    @WebMethod(action = "http://mx/telcel/df/sap/facturacion/v1/realizaPedido")
    @RequestWrapper(localName = "realizaPedido", targetNamespace = "http://www.amx.com/telcel/di/sds/esb/sap/facturacion", className = "com.amx.telcel.di.sds.esb.sap.facturacion.RealizaPedidoOpPetType")
    @ResponseWrapper(localName = "realizaPedidoResponse", targetNamespace = "http://www.amx.com/telcel/di/sds/esb/sap/facturacion", className = "com.amx.telcel.di.sds.esb.sap.facturacion.RealizaPedidoOpRespType")
    public void realizaPedido(
        @WebParam(name = "encabezado", targetNamespace = "")
        RequestHeaderType encabezado,
        @WebParam(name = "peticion", targetNamespace = "")
        RealizaPedidoPetType peticion,
        @WebParam(name = "encabezado", targetNamespace = "", mode = WebParam.Mode.OUT)
        Holder<ResponseHeaderType> encabezado0,
        @WebParam(name = "respuesta", targetNamespace = "", mode = WebParam.Mode.OUT)
        Holder<RealizaPedidoRespType> respuesta)
        throws RealizaPedidoFaultMsg
    ;

    /**
     * 
     * @param encabezado0
     * @param encabezado
     * @param respuesta
     * @param peticion
     * @throws BorraDocumentosFaultMsg
     */
    @WebMethod(action = "http://mx/telcel/df/sap/facturacion/v1/borraDocumentos")
    @RequestWrapper(localName = "borraDocumentosRequest", targetNamespace = "http://www.amx.com/telcel/di/sds/esb/sap/facturacion", className = "com.amx.telcel.di.sds.esb.sap.facturacion.BorraDocumentosOpPetType")
    @ResponseWrapper(localName = "borraDocumentosResponse", targetNamespace = "http://www.amx.com/telcel/di/sds/esb/sap/facturacion", className = "com.amx.telcel.di.sds.esb.sap.facturacion.BorraDocumentosOpRespType")
    public void borraDocumentosRequest(
        @WebParam(name = "encabezado", targetNamespace = "")
        RequestHeaderType encabezado,
        @WebParam(name = "peticion", targetNamespace = "")
        PedidoType peticion,
        @WebParam(name = "encabezado", targetNamespace = "", mode = WebParam.Mode.OUT)
        Holder<ResponseHeaderType> encabezado0,
        @WebParam(name = "respuesta", targetNamespace = "", mode = WebParam.Mode.OUT)
        Holder<BorraDocumentosRespType> respuesta)
        throws BorraDocumentosFaultMsg
    ;

}
