package mx.com.telcel.config;

import mx.com.telcel.notificacionfacturatelmextelcel.NotificacionFacturaTelmexRequest;
import mx.com.telcel.notificacionfacturatelmextelcel.NotificacionFacturaTelmexResponse;


public interface NotificacionFacturaTelmexRequestProcessor
{

	NotificacionFacturaTelmexResponse process(NotificacionFacturaTelmexRequest request);

}
