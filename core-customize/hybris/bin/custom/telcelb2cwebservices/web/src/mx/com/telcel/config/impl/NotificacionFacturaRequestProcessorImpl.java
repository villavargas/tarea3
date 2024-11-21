package mx.com.telcel.config.impl;

import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.TelcelReplacementOrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DuplicateKeyException;

import com.google.gson.Gson;

import mx.com.telcel.config.NotificacionFacturaRequestProcessor;
import mx.com.telcel.core.daos.consignment.TelcelConsignmentDao;
import mx.com.telcel.core.daos.order.TelcelOrderDao;
import mx.com.telcel.core.model.SeriesICCIDModel;
import mx.com.telcel.core.model.SeriesImeiModel;
import mx.com.telcel.core.model.TelcelFacturaModel;
import mx.com.telcel.notificacionfacturatelmex.Factura;
import mx.com.telcel.notificacionfacturatelmex.NotificacionFacturaRequest;
import mx.com.telcel.notificacionfacturatelmex.NotificacionFacturaResponse;
import mx.com.telcel.notificacionfacturatelmex.Posicion;
import mx.com.telcel.notificacionfacturatelmex.SeriesICCID;
import mx.com.telcel.notificacionfacturatelmex.SeriesIMEI;


public class NotificacionFacturaRequestProcessorImpl implements NotificacionFacturaRequestProcessor
{

	private static final String NO_SE_PUEDE_PROCESAR_LA_NOTIFICACION = "No se puede procesar la notificación";
	private static final String EL_IMEI_YA_HABIA_SIDO_NOTIFICADO = "El IMEI ya había sido notificado";
	private static final String EL_ICCID_YA_HABIA_SIDO_NOTIFICADO = "El ICCID ya había sido notificado";
	private ModelService modelService;
	public static final String TELCEL = "telcel";
	private static final String LA_FACTURA_YA_HABIA_SIDO_NOTIFICADA = "La factura ya había sido notificada";
	private static final String NOT_FOUND_PRODUCT_SKU = "La SKU del producto a facturar no existe,favor de validar.";
	private static final String TELCEL_WAIT_FOR_BILLED_ORDER = "_TelcelWaitForBilledOrder";


	private BaseStoreService baseStoreService;

	private CustomerAccountService customerAccountService;

	private BusinessProcessService businessProcessService;

	private TelcelConsignmentDao telcelConsignmentDao;

	private TelcelOrderDao telcelOrderDao;

	private static final Logger LOG = Logger.getLogger(NotificacionFacturaRequestProcessorImpl.class);

	@Override
	public NotificacionFacturaResponse process(final NotificacionFacturaRequest request)
	{

		String pedido = "";
		String numeroFactura = "";
		String entregaFactura = "";

		LOG.info("Factura Telcel WSDL" + new Gson().toJson(request));

		final NotificacionFacturaResponse response = new NotificacionFacturaResponse();

		final Factura factura = request.getFactura();
		boolean notificadoCompleto = true;
		if (factura != null)
		{
			pedido = factura.getPedido();
			numeroFactura = factura.getFactura();
			entregaFactura = factura.getEntrega();

			for (final Posicion posicion : factura.getPosiciones().getPosicion())
			{

			}

			final TelcelFacturaModel telcelFacturaModel = new TelcelFacturaModel();
			telcelFacturaModel.setNumeroFactura(numeroFactura);
			telcelFacturaModel.setEntrega(entregaFactura);

			final BaseStoreModel baseStore = baseStoreService.getBaseStoreForUid(TELCEL);

			OrderModel orderModel = null;
			ConsignmentModel consignmentModel = null;

			try
			{
				consignmentModel = telcelConsignmentDao.findTelcelConsignmentByCode(pedido, baseStore);
			}
			catch (final ModelNotFoundException e)
			{
				response.setCodigo("2");
				response.setDescripcion("El pedido no existe en Ecommerce");

				return response;
			}

			if (consignmentModel == null)
			{
				response.setCodigo("2");
				response.setDescripcion("El pedido no existe en Ecommerce");

				return response;
			}
			final int Positions = factura.getPosiciones().getPosicion().size();

			if (Positions == 1)
			{
				LOG.info("################ SUCCESS ######################");
				LOG.info("## Order::" + consignmentModel.getOrder().getCode());
				LOG.info("## Consignment::" + consignmentModel.getCode());
				LOG.info("## Description: Accessories and Services.  ####");
				LOG.info("################################################");
				for (final Posicion posicion : factura.getPosiciones().getPosicion())
				{
					for (final SeriesIMEI seriesIMEI : posicion.getSeriesIMEI())
					{
						final SeriesImeiModel imeiModel = getModelService().create(SeriesImeiModel.class);
						imeiModel.setImei(removeSpaces(seriesIMEI.getImei()));
						imeiModel.setActivado(Boolean.FALSE);
						imeiModel.setSku(removeSpaces(posicion.getMaterial().getCveMaterial()));
						imeiModel.setClaveSAT(posicion.getMaterial().getClaveSAT());
						imeiModel.setDescriptionSAT(posicion.getMaterial().getDescripcionSAT());
						if (consignmentModel.getSeriesImei() == null)
						{
							LOG.info("Servicios/Accesorios:: SeriesIMEI:  " + imeiModel.getImei() + " SKU: " + imeiModel.getSku());
							consignmentModel.setSeriesImei(imeiModel);

						}
					}

					for (final SeriesICCID seriesICCID : posicion.getSeriesICCID())
					{
						final SeriesICCIDModel iccidModel = getModelService().create(SeriesICCIDModel.class);
						iccidModel.setIccid(removeSpaces(seriesICCID.getIccid()));
						iccidModel.setActivado(Boolean.FALSE);
						iccidModel.setSku(removeSpaces(posicion.getMaterial().getCveMaterial()));
						iccidModel.setClaveSAT(posicion.getMaterial().getClaveSAT());
						iccidModel.setDescriptionSAT(posicion.getMaterial().getDescripcionSAT());
						if (consignmentModel.getSeriesICCID() == null)
						{
							LOG.info("Servicios/Accesorios:: SeriesICCID:  " + iccidModel.getIccid() + " SKU: " + iccidModel.getSku());
							consignmentModel.setSeriesICCID(iccidModel);

						}
					}
				}
				try
				{
					consignmentModel.setTelcelFactura(telcelFacturaModel);
					modelService.saveAll(consignmentModel);
					LOG.info("SaveAll..Servicios/Accesorios::");
				}
				catch (DuplicateKeyException | ModelSavingException e)
				{
					final StackTraceElement[] elementsRastreo = e.getStackTrace();
					for (final StackTraceElement element : elementsRastreo)
					{
						LOG.error("\n" + element.getClassName());
						LOG.error("\n" + element.getFileName());
						LOG.error("\n" + element.getLineNumber());
						LOG.error("\n" + element.getMethodName());
					}
					response.setCodigo("2");
					response.setDescripcion(LA_FACTURA_YA_HABIA_SIDO_NOTIFICADA);
					return response;
				}
			}
			else
			{
				for (final Posicion posicion : factura.getPosiciones().getPosicion())
				{
					//Validate productSku from consignmentEntries
					final Optional<ConsignmentEntryModel> optionalConsEntryModel = consignmentModel.getConsignmentEntries().stream()
							.filter(v1 -> v1.getOrderEntry().getProduct().getSku()
									.equalsIgnoreCase(removeSpaces(posicion.getMaterial().getCveMaterial())))
							.findFirst();
					if (optionalConsEntryModel.isPresent())
					{
						LOG.info("################ SUCCESS ######################");
						LOG.info("## Order::" + consignmentModel.getOrder().getCode());
						LOG.info("## Consignment::" + consignmentModel.getCode());
						LOG.info("## CveMaterial::" + removeSpaces(posicion.getMaterial().getCveMaterial()));
						LOG.info("################################################");

						//Get IMEI
						for (final SeriesIMEI seriesIMEI : posicion.getSeriesIMEI())
						{
							final SeriesImeiModel imeiModel = getModelService().create(SeriesImeiModel.class);
							imeiModel.setImei(removeSpaces(seriesIMEI.getImei()));
							imeiModel.setActivado(Boolean.FALSE);
							imeiModel.setSku(removeSpaces(posicion.getMaterial().getCveMaterial()));
							imeiModel.setClaveSAT(posicion.getMaterial().getClaveSAT());
							imeiModel.setDescriptionSAT(posicion.getMaterial().getDescripcionSAT());
							if (consignmentModel.getSeriesImei() == null)
							{
								LOG.info("SeriesIMEI:  " + imeiModel.getImei() + " SKU: " + imeiModel.getSku());
								consignmentModel.setSeriesImei(imeiModel);
							}
							break;
						}

						for (final SeriesICCID serieICCID : posicion.getSeriesICCID())
						{
							final SeriesICCIDModel iccidModel = getModelService().create(SeriesICCIDModel.class);
							iccidModel.setIccid(removeSpaces(serieICCID.getIccid()));
							iccidModel.setActivado(Boolean.FALSE);
							iccidModel.setSku(removeSpaces(posicion.getMaterial().getCveMaterial()));
							iccidModel.setClaveSAT(posicion.getMaterial().getClaveSAT());
							iccidModel.setDescriptionSAT(posicion.getMaterial().getDescripcionSAT());
							if (consignmentModel.getSeriesICCID() == null)
							{
								LOG.info("SeriesICCID:  " + iccidModel.getIccid() + " SKU: " + iccidModel.getSku());
								consignmentModel.setSeriesICCID(iccidModel);
							}
							break;
						}

					}
					else
					{
						LOG.info("###############  WARNING  ##################");
						LOG.info("## VALIDATION SKU NOT FOUND ###");
						LOG.info("## Order::" + consignmentModel.getOrder().getCode());
						LOG.info("## Consignment::" + consignmentModel.getCode());
						LOG.info("## CveMaterial::" + removeSpaces(posicion.getMaterial().getCveMaterial()));
						LOG.info("## PositionId::" + posicion.getId());
						LOG.info("############################################");

						//Get ICCID
						for (final SeriesICCID seriesICCID : posicion.getSeriesICCID())
						{
							final SeriesICCIDModel iccidModel = getModelService().create(SeriesICCIDModel.class);
							iccidModel.setIccid(removeSpaces(seriesICCID.getIccid()));
							iccidModel.setActivado(Boolean.FALSE);
							iccidModel.setSku(removeSpaces(posicion.getMaterial().getCveMaterial()));
							iccidModel.setClaveSAT(posicion.getMaterial().getClaveSAT());
							iccidModel.setDescriptionSAT(posicion.getMaterial().getDescripcionSAT());
							if (consignmentModel.getSeriesICCID() == null)
							{
								LOG.info("SeriesICCID::  " + iccidModel.getIccid() + " SKU: " + iccidModel.getSku());
								consignmentModel.setSeriesICCID(iccidModel);

							}
							else
							{
								if (consignmentModel.getSeriesICCID().getIccid().equals(removeSpaces(seriesICCID.getIccid())))
								{
									LOG.info("Trato de guardar el mismo ICCID, debido a las iteraciones de cada  posicion de la factura.");
								}
								else
								{
									if (!removeSpaces(seriesICCID.getIccid()).equals(""))
									{
										iccidModel.setIccid(removeSpaces(seriesICCID.getIccid()));
										iccidModel.setActivado(Boolean.FALSE);
										iccidModel.setSku(removeSpaces(posicion.getMaterial().getCveMaterial()));
										iccidModel.setClaveSAT(posicion.getMaterial().getClaveSAT());
										iccidModel.setDescriptionSAT(posicion.getMaterial().getDescripcionSAT());
										LOG.info("SeriesICCID:::  " + iccidModel.getIccid() + " SKU: " + iccidModel.getSku());
										consignmentModel.setSeriesICCID(iccidModel);


									}
								}
							}
						}

					}
				}
				try
				{
					consignmentModel.setTelcelFactura(telcelFacturaModel);
					modelService.saveAll(consignmentModel);
					LOG.info("SaveAll::");
				}
				catch (final Exception e)
				{
					LOG.error("Error al guardar Factura : " + e.getMessage());
					/*
					 * final StackTraceElement[] elementsRastreo = e.getStackTrace(); for (final StackTraceElement element :
					 * elementsRastreo) { LOG.error(element.getClassName() + " : " + element.getFileName() + " : " +
					 * element.getLineNumber() + " : " + element.getMethodName()); }
					 */
					response.setCodigo("2");
					response.setDescripcion(LA_FACTURA_YA_HABIA_SIDO_NOTIFICADA);
					return response;
				}
			}

			if (consignmentModel.getTelcelFactura() == null)
			{
				notificadoCompleto = false;
				LOG.info("Factura is False");
			}

			if (notificadoCompleto)
			{
				modelService.refresh(consignmentModel);
				String valorIMEI = "";
				valorIMEI = consignmentModel.getSeriesImei().getImei() == null
						|| consignmentModel.getSeriesImei().getImei().equals("") ? "Empty" : consignmentModel.getSeriesImei().getImei();
				String valorICCID = "";
				valorICCID = consignmentModel.getSeriesICCID().getIccid() == null
						|| consignmentModel.getSeriesICCID().getIccid().equals("") ? "Empty"
								: consignmentModel.getSeriesICCID().getIccid();
				orderModel = telcelOrderDao.getOrderModelByPoNoSalesDocument(pedido);


				if (orderModel instanceof TelcelReplacementOrderModel)
				{
					getBusinessProcessService()
							.triggerEvent(orderModel.getOrderProcess().iterator().next().getCode() + "_ReplacementWaitForBilledOrder");
				}
				else
				{
					final ConsignmentProcessModel consignmentProcessModel = consignmentModel.getConsignmentProcesses().iterator()
							.next();
					LOG.info("#################################################");
					LOG.info("## SUCCESS: Start Process _WaitForBilledOrder ###");
					LOG.info("## ORDER: " + consignmentModel.getOrder().getCode() + " Consigment: " + consignmentModel.getCode()
							+ " consignmentProcessCode: " + consignmentProcessModel.getCode());
					LOG.info("## IMEI:: " + valorIMEI);
					LOG.info("## ICCID:: " + valorICCID);
					LOG.info("## Factura:: " + consignmentModel.getTelcelFactura().getNumeroFactura());
					LOG.info("##################################################");
					//Execute ConsignmentProcess
					getBusinessProcessService().triggerEvent(consignmentProcessModel.getCode() + TELCEL_WAIT_FOR_BILLED_ORDER);
				}
				response.setCodigo("1");
				response.setDescripcion("Datos Recibidos Exitosamente");
				return response;
			}
		}
		else
		{
			response.setCodigo("2");
			response.setDescripcion(NO_SE_PUEDE_PROCESAR_LA_NOTIFICACION);
		}


		return response;
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

	public BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	@Required
	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}

	public BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	@Required
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
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

	public TelcelConsignmentDao getTelcelConsignmentDao()
	{
		return telcelConsignmentDao;
	}

	@Required
	public void setTelcelConsignmentDao(final TelcelConsignmentDao telcelConsignmentDao)
	{
		this.telcelConsignmentDao = telcelConsignmentDao;
	}

	public TelcelOrderDao getTelcelOrderDao()
	{
		return telcelOrderDao;
	}

	@Required
	public void setTelcelOrderDao(final TelcelOrderDao telcelOrderDao)
	{
		this.telcelOrderDao = telcelOrderDao;
	}

	public String removeSpaces(final String value)
	{
		String valueWithoutSpaces = "";
		valueWithoutSpaces = value.replaceAll("\\s", "");
		return valueWithoutSpaces;
	}
}
