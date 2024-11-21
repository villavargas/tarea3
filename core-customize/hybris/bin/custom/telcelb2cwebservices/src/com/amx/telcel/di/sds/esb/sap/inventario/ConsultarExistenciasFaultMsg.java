
package com.amx.telcel.di.sds.esb.sap.inventario;

import javax.xml.ws.WebFault;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.3.2
 * Generated source version: 2.2
 * 
 */
@WebFault(name = "consultarExistenciasException", targetNamespace = "http://www.amx.com/telcel/di/sds/esb/sap/inventario")
public class ConsultarExistenciasFaultMsg
    extends Exception
{

    /**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private InventarioProductosException faultInfo;

    /**
     * 
     * @param faultInfo
     * @param message
     */
    public ConsultarExistenciasFaultMsg(String message, InventarioProductosException faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param faultInfo
     * @param cause
     * @param message
     */
    public ConsultarExistenciasFaultMsg(String message, InventarioProductosException faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: com.amx.telcel.di.sds.esb.sap.inventario.InventarioProductosException
     */
    public InventarioProductosException getFaultInfo() {
        return faultInfo;
    }

}