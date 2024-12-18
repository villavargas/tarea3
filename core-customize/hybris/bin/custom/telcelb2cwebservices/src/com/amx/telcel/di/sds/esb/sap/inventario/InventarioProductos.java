
package com.amx.telcel.di.sds.esb.sap.inventario;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Holder;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import com.amx.mexico.telcel.esb.v1_1.ControlDataRequestType;
import com.amx.mexico.telcel.esb.v1_1.ControlDataResponseType;
import com.amx.mexico.telcel.esb.v1_1.DetailResponseType;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.3.2
 * Generated source version: 2.2
 * 
 */
@WebService(name = "InventarioProductos", targetNamespace = "http://www.amx.com/telcel/di/sds/esb/sap/inventario")
@XmlSeeAlso({
    com.amx.mexico.telcel.esb.v1_1.ObjectFactory.class,
    com.amx.telcel.di.sds.esb.sap.inventario.ObjectFactory.class,
    mx.com.amx.mexico.telcel.mq.v1_1.ObjectFactory.class
})
public interface InventarioProductos {


    /**
     * 
     * @param controlData0
     * @param detailResponse
     * @param respuesta
     * @param controlData
     * @param peticion
     * @throws ConsultarExistenciasFaultMsg
     * @throws ConsultarExistenciasFault
     */
    @WebMethod(action = "http://mx/telcel/df/sap/inventarios/ecomm/InventarioProductos/v1/consultarExistencias")
    @RequestWrapper(localName = "consultarExistencias", targetNamespace = "http://www.amx.com/telcel/di/sds/esb/sap/inventario", className = "com.amx.telcel.di.sds.esb.sap.inventario.ConsultarExistenciasOpPetType")
    @ResponseWrapper(localName = "consultarExistenciasResponse", targetNamespace = "http://www.amx.com/telcel/di/sds/esb/sap/inventario", className = "com.amx.telcel.di.sds.esb.sap.inventario.ConsultarExistenciasOpRespType")
    public void consultarExistencias(
        @WebParam(name = "controlData", targetNamespace = "")
        ControlDataRequestType controlData,
        @WebParam(name = "peticion", targetNamespace = "")
        ConsultarExistenciasType peticion,
        @WebParam(name = "controlData", targetNamespace = "", mode = WebParam.Mode.OUT)
        Holder<ControlDataResponseType> controlData0,
        @WebParam(name = "detailResponse", targetNamespace = "", mode = WebParam.Mode.OUT)
        Holder<DetailResponseType> detailResponse,
        @WebParam(name = "respuesta", targetNamespace = "", mode = WebParam.Mode.OUT)
        Holder<ConsultarExistenciasRespType> respuesta)
        throws ConsultarExistenciasFault, ConsultarExistenciasFaultMsg
    ;

}
