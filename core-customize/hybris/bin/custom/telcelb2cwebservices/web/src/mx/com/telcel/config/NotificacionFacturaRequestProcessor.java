package mx.com.telcel.config;

import mx.com.telcel.notificacionfacturatelmex.NotificacionFacturaRequest;
import mx.com.telcel.notificacionfacturatelmex.NotificacionFacturaResponse;

public interface NotificacionFacturaRequestProcessor {

    NotificacionFacturaResponse process(NotificacionFacturaRequest request);

}
