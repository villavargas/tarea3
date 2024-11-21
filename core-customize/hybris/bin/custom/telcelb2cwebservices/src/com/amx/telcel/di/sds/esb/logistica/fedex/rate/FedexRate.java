
package com.amx.telcel.di.sds.esb.logistica.fedex.rate;

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
@WebService(name = "FedexRate", targetNamespace = "http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate")
@XmlSeeAlso({
    com.amx.mexico.telcel.esb.v1_0_2.ObjectFactory.class,
    com.amx.telcel.di.sds.esb.logistica.fedex.rate.ObjectFactory.class
})
public interface FedexRate {


    /**
     * 
     * @param encabezado0
     * @param encabezado
     * @param respuesta
     * @param peticion
     * @throws GetRatesFaultMsg
     */
    @WebMethod(action = "http://mx/telcel/di/sds/fedex/rate/v0/getRates")
    @RequestWrapper(localName = "getRates", targetNamespace = "http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate", className = "com.amx.telcel.di.sds.esb.logistica.fedex.rate.GetRatesOpPetType")
    @ResponseWrapper(localName = "getRatesResponse", targetNamespace = "http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate", className = "com.amx.telcel.di.sds.esb.logistica.fedex.rate.GetRatesOpRespType")
    public void getRates(
        @WebParam(name = "encabezado", targetNamespace = "")
        RequestHeaderType encabezado,
        @WebParam(name = "peticion", targetNamespace = "")
        GetRatesPetType peticion,
        @WebParam(name = "encabezado", targetNamespace = "", mode = WebParam.Mode.OUT)
        Holder<ResponseHeaderType> encabezado0,
        @WebParam(name = "respuesta", targetNamespace = "", mode = WebParam.Mode.OUT)
        Holder<GetRatesRespType> respuesta)
        throws GetRatesFaultMsg
    ;

}