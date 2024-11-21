
package mx.com.telcel.notificacionfacturatelmex;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ibcomercio.telcel_com.commerce.services.sap.notificacionfactura package. 
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


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ibcomercio.telcel_com.commerce.services.sap.notificacionfactura
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link NotificacionFacturaRequest }
     * 
     */
    public NotificacionFacturaRequest createNotificacionFacturaRequest() {
        return new NotificacionFacturaRequest();
    }

    /**
     * Create an instance of {@link Factura }
     * 
     */
    public Factura createFactura() {
        return new Factura();
    }

    /**
     * Create an instance of {@link NotificacionFacturaResponse }
     * 
     */
    public NotificacionFacturaResponse createNotificacionFacturaResponse() {
        return new NotificacionFacturaResponse();
    }

    /**
     * Create an instance of {@link Material }
     * 
     */
    public Material createMaterial() {
        return new Material();
    }

    /**
     * Create an instance of {@link Posicion }
     * 
     */
    public Posicion createPosicion() {
        return new Posicion();
    }

    /**
     * Create an instance of {@link Posiciones }
     * 
     */
    public Posiciones createPosiciones() {
        return new Posiciones();
    }

    /**
     * Create an instance of {@link SeriesICCID }
     * 
     */
    public SeriesICCID createSeriesICCID() {
        return new SeriesICCID();
    }

    /**
     * Create an instance of {@link SeriesIMEI }
     * 
     */
    public SeriesIMEI createSeriesIMEI() {
        return new SeriesIMEI();
    }

}
