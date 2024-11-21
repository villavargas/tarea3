
package com.amx.telcel.di.sds.esb.logistica.fedex.rate;

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
@WebServiceClient(name = "FedexRateHttpService", targetNamespace = "http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate", wsdlLocation = "file:/C:/Users/luis.patino/AppData/Local/Temp/tempdir9652179995283921305.tmp/v0_1.wsdl")
public class FedexRateHttpService
    extends Service
{

    private final static URL FEDEXRATEHTTPSERVICE_WSDL_LOCATION;
    private final static WebServiceException FEDEXRATEHTTPSERVICE_EXCEPTION;
    private final static QName FEDEXRATEHTTPSERVICE_QNAME = new QName("http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate", "FedexRateHttpService");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("file:/C:/Users/luis.patino/AppData/Local/Temp/tempdir9652179995283921305.tmp/v0_1.wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        FEDEXRATEHTTPSERVICE_WSDL_LOCATION = url;
        FEDEXRATEHTTPSERVICE_EXCEPTION = e;
    }

    public FedexRateHttpService() {
        super(__getWsdlLocation(), FEDEXRATEHTTPSERVICE_QNAME);
    }

    public FedexRateHttpService(WebServiceFeature... features) {
        super(__getWsdlLocation(), FEDEXRATEHTTPSERVICE_QNAME, features);
    }

    public FedexRateHttpService(URL wsdlLocation) {
        super(wsdlLocation, FEDEXRATEHTTPSERVICE_QNAME);
    }

    public FedexRateHttpService(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, FEDEXRATEHTTPSERVICE_QNAME, features);
    }

    public FedexRateHttpService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public FedexRateHttpService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns FedexRate
     */
    @WebEndpoint(name = "FedexRateHttpPort")
    public FedexRate getFedexRateHttpPort() {
        return super.getPort(new QName("http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate", "FedexRateHttpPort"), FedexRate.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns FedexRate
     */
    @WebEndpoint(name = "FedexRateHttpPort")
    public FedexRate getFedexRateHttpPort(WebServiceFeature... features) {
        return super.getPort(new QName("http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate", "FedexRateHttpPort"), FedexRate.class, features);
    }

    private static URL __getWsdlLocation() {
        if (FEDEXRATEHTTPSERVICE_EXCEPTION!= null) {
            throw FEDEXRATEHTTPSERVICE_EXCEPTION;
        }
        return FEDEXRATEHTTPSERVICE_WSDL_LOCATION;
    }

}
