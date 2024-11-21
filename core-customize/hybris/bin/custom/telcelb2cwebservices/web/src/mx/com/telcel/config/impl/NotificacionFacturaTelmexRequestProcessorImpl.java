package mx.com.telcel.config.impl;

import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DuplicateKeyException;

import com.google.gson.Gson;

import mx.com.telcel.config.NotificacionFacturaTelmexRequestProcessor;
import mx.com.telcel.core.model.PrecioTelmexModel;
import mx.com.telcel.core.model.SeriesICCIDModel;
import mx.com.telcel.core.model.SeriesImeiModel;
import mx.com.telcel.core.model.TelcelFacturaModel;
import mx.com.telcel.facades.orders.TelcelOrdersFacade;
import mx.com.telcel.notificacionfacturatelmextelcel.Factura;
import mx.com.telcel.notificacionfacturatelmextelcel.NotificacionFacturaTelmexRequest;
import mx.com.telcel.notificacionfacturatelmextelcel.NotificacionFacturaTelmexResponse;
import mx.com.telcel.notificacionfacturatelmextelcel.Posicion;
import mx.com.telcel.notificacionfacturatelmextelcel.Posiciones;
import mx.com.telcel.notificacionfacturatelmextelcel.PrecioTelmex;
import mx.com.telcel.notificacionfacturatelmextelcel.SeriesICCID;
import mx.com.telcel.notificacionfacturatelmextelcel.SeriesIMEI;


public class NotificacionFacturaTelmexRequestProcessorImpl implements NotificacionFacturaTelmexRequestProcessor
{

	private static final String TELMEX = "telmex";
	private static final Logger LOG = Logger.getLogger(NotificacionFacturaTelmexRequestProcessorImpl.class);
	private static final String NO_SE_PUEDE_PROCESAR_LA_NOTIFICACION = "No se puede procesar la notificación";
	private static final String LA_FACTURA_YA_HABIA_SIDO_NOTIFICADA = "La factura ya había sido notificada";
	private static final String EL_IMEI_YA_HABIA_SIDO_NOTIFICADO = "El IMEI ya había sido notificado";
	private static final String EL_ICCID_YA_HABIA_SIDO_NOTIFICADO = "El ICCID ya había sido notificado";
	private static final String POSICION_ARTICULO = "10";
	private static final String POSICION_SIM = "20";

	private ModelService modelService;
	private CustomerAccountService customerAccountService;
	private BusinessProcessService businessProcessService;
	private TelcelOrdersFacade telcelOrdersFacade;

	@Override
	public NotificacionFacturaTelmexResponse process(final NotificacionFacturaTelmexRequest request)
	{
		LOG.info("Factura Telmex WSDL" + new Gson().toJson(request));

		final Factura factura = request.getFactura();

		final NotificacionFacturaTelmexResponse response = new NotificacionFacturaTelmexResponse();

		String pedido = "";
		String numeroFactura = "";
		String entregaFactura = "";

		if (factura != null)
		{
			pedido = factura.getPedido();
			numeroFactura = factura.getFactura();
			entregaFactura = factura.getEntrega();

			final TelcelFacturaModel telcelFacturaModel = new TelcelFacturaModel();
			telcelFacturaModel.setNumeroFactura(numeroFactura);
			telcelFacturaModel.setEntrega(entregaFactura);
			telcelFacturaModel.setAlmacen(factura.getAlmacen());
			telcelFacturaModel.setRegion(factura.getRegion());
			OrderModel orderModel = null;

			try
			{
				final List<OrderModel> orderTelmexByReservationId = getTelcelOrdersFacade().getOrderTelmexByReservationId(pedido);
				if (!orderTelmexByReservationId.isEmpty())
				{
					orderModel = orderTelmexByReservationId.get(0);
				}
			}
			catch (final ModelNotFoundException e)
			{
				response.setCodigo("2");
				response.setDescripcion("El pedido no existe en Ecommerce");

				return response;
			}

			if (orderModel == null)
			{
				response.setCodigo("2");
				response.setDescripcion("El pedido no existe en Ecommerce");

				return response;
			}

			int totalItems = 0;
			int totalfacturas = 0;

			for (final AbstractOrderEntryModel entryModel : orderModel.getEntries())
			{
				totalItems += entryModel.getQuantity();
				totalfacturas += entryModel.getFacturas().size();
			}

			final Posiciones posiciones = factura.getPosiciones();
			if (!posiciones.getPosicion().isEmpty())
			{
				final AbstractOrderEntryModel entryModel = findOrderEntryProduct(posiciones, orderModel);
				for (final Posicion posicion : posiciones.getPosicion())
				{
					LOG.info("Posicion : " + posicion.getId() + " - " + posicion.getMaterial().getDescripcionSAT());
					//POSICION ARTICULO
					if (Objects.nonNull(entryModel) && posicion.getId().equals(POSICION_ARTICULO))
					{
						final List<SeriesImeiModel> imeiList = new ArrayList<>();
						for (final SeriesIMEI seriesIMEI : posicion.getSeriesIMEI())
						{
							final SeriesImeiModel imeiModel = new SeriesImeiModel();
							imeiModel.setImei(seriesIMEI.getImei());
							imeiModel.setSku(posicion.getMaterial().getCveMaterial());
							imeiModel.setActivado(Boolean.FALSE);
							imeiModel.setClaveSAT(posicion.getMaterial().getClaveSAT());
							imeiModel.setDescriptionSAT(posicion.getMaterial().getDescripcionSAT());
							imeiList.add(imeiModel);
						}
						if (!entryModel.getSeriesIMEI().isEmpty())
						{
							imeiList.addAll(entryModel.getSeriesIMEI());
						}

						final List<SeriesICCIDModel> iccidList = new ArrayList<>();
						for (final SeriesICCID serieICCID : posicion.getSeriesICCID())
						{
							final SeriesICCIDModel iccidModel = new SeriesICCIDModel();
							iccidModel.setIccid(serieICCID.getIccid());
							iccidModel.setSku(posicion.getMaterial().getCveMaterial());
							iccidModel.setActivado(Boolean.FALSE);
							iccidModel.setClaveSAT(posicion.getMaterial().getClaveSAT());
							iccidModel.setDescriptionSAT(posicion.getMaterial().getDescripcionSAT());
							iccidList.add(iccidModel);
						}
						if (!entryModel.getSeriesICCID().isEmpty())
						{
							iccidList.addAll(entryModel.getSeriesICCID());
						}
						if (!imeiList.isEmpty())
						{
							entryModel.setSeriesIMEI(imeiList);
							try
							{
								getModelService().saveAll(entryModel);
							}
							catch (final Exception e)
							{
								LOG.error("Error al guardar IMEI, error: " + e.getMessage());
								response.setCodigo("2");
								response.setDescripcion(EL_IMEI_YA_HABIA_SIDO_NOTIFICADO);

								return response;
							}
						}
						if (!iccidList.isEmpty())
						{
							entryModel.setSeriesICCID(iccidList);
							try
							{
								getModelService().saveAll(entryModel);
							}
							catch (final Exception e)
							{
								LOG.error("Error al guardar ICCID, error: " + e.getMessage());
								response.setCodigo("2");
								response.setDescripcion(EL_ICCID_YA_HABIA_SIDO_NOTIFICADO);

								return response;
							}
						}

						final List<TelcelFacturaModel> facturalList = new ArrayList<>();
						if (entryModel.getFacturas() != null && !entryModel.getFacturas().isEmpty())
						{
							facturalList.addAll(entryModel.getFacturas());
							facturalList.add(telcelFacturaModel);
						}
						else
						{
							facturalList.add(telcelFacturaModel);
						}

						try
						{
							entryModel.setFacturas(facturalList);
							getModelService().save(entryModel);
							totalfacturas += 1;
						}
						catch (DuplicateKeyException | ModelSavingException e)
						{
							LOG.error("Error al guardar Factura Telmex, error: " + e.getMessage());
							response.setCodigo("2");
							response.setDescripcion(LA_FACTURA_YA_HABIA_SIDO_NOTIFICADA);

							return response;
						}
						catch (final Exception ex)
						{
							LOG.error("Error al guardar Factura Telmex, error: " + ex.getMessage());
							response.setCodigo("2");
							response.setDescripcion(NO_SE_PUEDE_PROCESAR_LA_NOTIFICACION);

							return response;
						}
						if (!posicion.getPrecioTelmex().isEmpty())
						{
							final List<PrecioTelmexModel> precioTelmexList = new ArrayList<>();

							for (final PrecioTelmex precioTelmex : posicion.getPrecioTelmex())
							{
								final PrecioTelmexModel precioTelmexModel = new PrecioTelmexModel();
								precioTelmexModel.setPrecio(precioTelmex.getPrecio());

								precioTelmexList.add(precioTelmexModel);
							}
							entryModel.setPrecioTelmex(precioTelmexList);
						}
					}
					//POSICION SIM
					else if (Objects.nonNull(entryModel) && posicion.getId().equals(POSICION_SIM))
					{
						final List<SeriesICCIDModel> iccidList = new ArrayList<>();
						for (final SeriesICCID serieICCID : posicion.getSeriesICCID())
						{
							final SeriesICCIDModel iccidModel = new SeriesICCIDModel();
							iccidModel.setIccid(serieICCID.getIccid());
							iccidModel.setSku(posicion.getMaterial().getCveMaterial());
							iccidModel.setActivado(Boolean.FALSE);
							iccidModel.setClaveSAT(posicion.getMaterial().getClaveSAT());
							iccidModel.setDescriptionSAT(posicion.getMaterial().getDescripcionSAT());
							iccidList.add(iccidModel);
						}

						if (!entryModel.getSeriesICCID().isEmpty())
						{
							iccidList.addAll(entryModel.getSeriesICCID());
						}

						if (!iccidList.isEmpty())
						{
							entryModel.setSeriesICCID(iccidList);
							try
							{
								getModelService().saveAll(entryModel);
							}
							catch (final Exception e)
							{
								LOG.error("Error al guardar ICCID, error: " + e.getMessage());
								response.setCodigo("2");
								response.setDescripcion(EL_ICCID_YA_HABIA_SIDO_NOTIFICADO);

								return response;
							}
						}
					}
				}
			}
			final boolean notificadoCompleto = totalfacturas == totalItems;

			if (notificadoCompleto)
			{
				getBusinessProcessService().triggerEvent(orderModel.getOrderProcess().iterator().next().getCode() + "_TelmexBilling");
			}

		}

		response.setCodigo("1");
		response.setDescripcion("Datos Recibidos Exitosamente");

		return response;

	}

	private AbstractOrderEntryModel findOrderEntryProduct(final Posiciones posiciones, final OrderModel orderModel)
	{
		for (final Posicion posicion : posiciones.getPosicion())
		{
			final Optional<AbstractOrderEntryModel> entry = orderModel.getEntries().stream()
					.filter(obj -> obj.getProduct().getSku().equals(posicion.getMaterial().getCveMaterial())).findFirst();
			if (entry.isPresent())
			{
				return entry.get();
			}
		}
		return null;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	public CustomerAccountService getCustomerAccountService()
	{
		return customerAccountService;
	}

	@Required
	public void setCustomerAccountService(final CustomerAccountService customerAccountService)
	{
		this.customerAccountService = customerAccountService;
	}


	public BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	@Required
	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}

	public TelcelOrdersFacade getTelcelOrdersFacade()
	{
		return telcelOrdersFacade;
	}

	@Required
	public void setTelcelOrdersFacade(final TelcelOrdersFacade telcelOrdersFacade)
	{
		this.telcelOrdersFacade = telcelOrdersFacade;
	}

}
