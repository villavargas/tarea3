
package mx.com.amx.mexico.telcel.di.sds.gsa.dai.esb.consultafacturas;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.3.2
 * Generated source version: 2.2
 * 
 */
@WebServiceClient(name = "consultaFacturasHttpService", targetNamespace = "http://www.amx.com.mx/mexico/telcel/di/sds/gsa/dai/esb/consultafacturas", wsdlLocation = "file:/C:/Users/luis.patino/AppData/Local/Temp/tempdir14974413718632795341.tmp/v1_1.wsdl")
public class ConsultaFacturasHttpService
    extends Service
{

    private final static URL CONSULTAFACTURASHTTPSERVICE_WSDL_LOCATION;
    private final static WebServiceException CONSULTAFACTURASHTTPSERVICE_EXCEPTION;
    private final static QName CONSULTAFACTURASHTTPSERVICE_QNAME = new QName("http://www.amx.com.mx/mexico/telcel/di/sds/gsa/dai/esb/consultafacturas", "consultaFacturasHttpService");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("file:/C:/Users/luis.patino/AppData/Local/Temp/tempdir14974413718632795341.tmp/v1_1.wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        CONSULTAFACTURASHTTPSERVICE_WSDL_LOCATION = url;
        CONSULTAFACTURASHTTPSERVICE_EXCEPTION = e;
    }

    public ConsultaFacturasHttpService() {
        super(__getWsdlLocation(), CONSULTAFACTURASHTTPSERVICE_QNAME);
    }

    public ConsultaFacturasHttpService(WebServiceFeature... features) {
        super(__getWsdlLocation(), CONSULTAFACTURASHTTPSERVICE_QNAME, features);
    }

    public ConsultaFacturasHttpService(URL wsdlLocation) {
        super(wsdlLocation, CONSULTAFACTURASHTTPSERVICE_QNAME);
    }

    public ConsultaFacturasHttpService(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, CONSULTAFACTURASHTTPSERVICE_QNAME, features);
    }

    public ConsultaFacturasHttpService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public ConsultaFacturasHttpService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns ConsultaFacturas
     */
    @WebEndpoint(name = "consultaFacturasHttpPort")
    public ConsultaFacturas getConsultaFacturasHttpPort() {
        return super.getPort(new QName("http://www.amx.com.mx/mexico/telcel/di/sds/gsa/dai/esb/consultafacturas", "consultaFacturasHttpPort"), ConsultaFacturas.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns ConsultaFacturas
     */
    @WebEndpoint(name = "consultaFacturasHttpPort")
    public ConsultaFacturas getConsultaFacturasHttpPort(WebServiceFeature... features) {
        return super.getPort(new QName("http://www.amx.com.mx/mexico/telcel/di/sds/gsa/dai/esb/consultafacturas", "consultaFacturasHttpPort"), ConsultaFacturas.class, features);
    }

    private static URL __getWsdlLocation() {
        if (CONSULTAFACTURASHTTPSERVICE_EXCEPTION!= null) {
            throw CONSULTAFACTURASHTTPSERVICE_EXCEPTION;
        }
        return CONSULTAFACTURASHTTPSERVICE_WSDL_LOCATION;
    }

}
