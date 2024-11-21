/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.fulfilmentprocess.actions.returns;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.task.RetryLaterException;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.amx.telcel.di.sds.esb.sap.cancelacion.LiberarRecursosResponse;

import mx.com.telcel.core.model.ReleaseResourcesModel;
import mx.com.telcel.core.model.SeriesICCIDModel;
import mx.com.telcel.core.model.SeriesImeiModel;
import mx.com.telcel.core.model.TelcelFacturaModel;
import mx.com.telcel.core.models.liberarrecursos.InfoLiberarRecursos;
import mx.com.telcel.core.services.mq.liberacionrecursos.LiberaRecursosMQService;


/**
 * The type Roll back return action.
 */
public class RollBackReturnAction extends AbstractProceduralAction<ReturnProcessModel>
{
	private static final Logger LOG = Logger.getLogger(RollBackReturnAction.class);

	@Resource(name = "liberaRecursosMQService")
	private LiberaRecursosMQService liberaRecursosMQService;

	@Override
	public void executeAction(final ReturnProcessModel process) throws RetryLaterException, Exception
	{
		LOG.info("Process: " + process.getCode() + " in step " + getClass().getSimpleName());
		final OrderModel orderModel = process.getReturnRequest().getOrder();

		final ReturnRequestModel returnRequestModel = process.getReturnRequest();
		final RefundEntryModel entryModel = (RefundEntryModel) returnRequestModel.getReturnEntries().get(0);
		final ConsignmentModel consignmentModel = entryModel.getConsignment();
		final TelcelFacturaModel telcelFacturaModel = consignmentModel.getTelcelFactura();
		final boolean sendSAPSources = telcelFacturaModel == null || !StringUtils.isNotEmpty(telcelFacturaModel.getNumeroFactura());
		final ProductModel productModel = consignmentModel.getConsignmentEntries().iterator().next().getOrderEntry().getProduct();
		final boolean needActivation = productModel.getActivable();
		if (needActivation || sendSAPSources)
		{
			LOG.info("Calling rollback.- returnRequestModel:" + returnRequestModel.getRMA() + ", needActivation:" + needActivation + ", sendSAPSources:" + sendSAPSources);
			callRollback(process, orderModel, returnRequestModel, consignmentModel, sendSAPSources);
		}
		else
		{
			LOG.info(String.format("Rollback was not executed because it does not apply for rma: %s", returnRequestModel.getRMA()));
		}
	}

	private void callRollback(final ReturnProcessModel process, final OrderModel orderModel,
			final ReturnRequestModel returnRequestModel, final ConsignmentModel consignmentModel, final boolean sendSAPSources)
			throws Exception
	{
		final InfoLiberarRecursos infoLiberarRecursos = new InfoLiberarRecursos();

		infoLiberarRecursos.setIdPedido(consignmentModel.getPoNoSalesDocument());
		infoLiberarRecursos.setIdEntrega(consignmentModel.getPoNoDelivery());
		infoLiberarRecursos.setbConsigment(sendSAPSources);
		infoLiberarRecursos.setRegion(orderModel.getRegionCode());

		if ((consignmentModel.getSeriesICCID() == null || StringUtils.isEmpty(consignmentModel.getSeriesICCID().getLinea()))
				&& !sendSAPSources)
		{
			LOG.info("Rollback for RMA is not running.....................");
			return;
		}

		final SeriesICCIDModel serieICCIDModel = consignmentModel.getSeriesICCID();

		if (serieICCIDModel != null && StringUtils.isNotEmpty(serieICCIDModel.getLinea()))
		{
			final SeriesImeiModel serieImeiModel = consignmentModel.getSeriesImei();
			if (serieImeiModel != null)
			{
				infoLiberarRecursos.setImei(serieImeiModel.getImei());
				infoLiberarRecursos.setbImei(true);
			}
			else
			{
				infoLiberarRecursos.setbImei(false);
			}
			infoLiberarRecursos.setIccid(serieICCIDModel.getIccid());
			infoLiberarRecursos.setbIccid(true);
			infoLiberarRecursos.setMsisdn(serieICCIDModel.getLinea());
		}
		else
		{
			infoLiberarRecursos.setbImei(false);
			infoLiberarRecursos.setbIccid(false);
		}

		final LiberarRecursosResponse liberarRecursosResponse = liberaRecursosMQService.rollbackService(infoLiberarRecursos,
				consignmentModel);
		getModelService().save(consignmentModel);
		if (liberarRecursosResponse != null)
		{
			final ReleaseResourcesModel releaseResourcesModel = getModelService().create(ReleaseResourcesModel.class);
			releaseResourcesModel.setMessageUUID(liberarRecursosResponse.getControlData().getMessageUUID());
			releaseResourcesModel
					.setIdTransaccion(liberarRecursosResponse.getLiberarRecursosActivacionResponse().getIdTransaccion());
			releaseResourcesModel.setIdProceso(liberarRecursosResponse.getLiberarRecursosActivacionResponse().getIdProceso());
			releaseResourcesModel.setRegion(infoLiberarRecursos.getRegion());
			releaseResourcesModel.setIdEntrega(infoLiberarRecursos.getIdEntrega());
			releaseResourcesModel.setIdPedido(infoLiberarRecursos.getIdPedido());
			releaseResourcesModel.setOrder(orderModel.getCode());
			releaseResourcesModel.setRMA(returnRequestModel.getRMA());
			releaseResourcesModel.setReturnProcesCode(process.getCode());
			releaseResourcesModel.setMsisdn(
					StringUtils.isNotEmpty(infoLiberarRecursos.getMsisdn()) ? infoLiberarRecursos.getMsisdn() : StringUtils.EMPTY);
			releaseResourcesModel.setIccid(
					StringUtils.isNotEmpty(infoLiberarRecursos.getIccid()) ? infoLiberarRecursos.getIccid() : StringUtils.EMPTY);
			releaseResourcesModel.setImei(
					StringUtils.isNotEmpty(infoLiberarRecursos.getImei()) ? infoLiberarRecursos.getImei() : StringUtils.EMPTY);
			returnRequestModel.setReleaseResources(releaseResourcesModel);
			getModelService().saveAll(returnRequestModel);
			//liberaRecursosMQService.updateLiberarRecursosResponse();
		}
		else
		{
			throw new Exception(String.format("Error calling rollback service for RMA: %s", returnRequestModel.getRMA()));
		}
	}
}
