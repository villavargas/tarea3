/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.fulfilmentprocess.actions.consignment;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.action.AbstractAction;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.task.RetryLaterException;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Resource;

import mx.com.telcel.core.daos.serieiccid.TelcelSerieIccidDao;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import mx.com.telcel.core.model.SeriesICCIDModel;
import mx.com.telcel.core.model.SeriesImeiModel;
import mx.com.telcel.core.model.UserInfoCVTModel;
import mx.com.telcel.core.model.UserInfoDATModel;
import mx.com.telcel.facades.order.data.ProductOrderingResponse;
import mx.com.telcel.fulfilmentprocess.activation.TelcelActivationService;


/**
 * The type Activation action.
 */
public class ActivationAction extends AbstractAction<ConsignmentProcessModel>
{

	private static final Logger LOG = Logger.getLogger(ActivationAction.class);
	private static final String STRING_EMPTY = "";
	private static final String TELCEL_SALES_FORCE_R = "telcel.sales.force.r";
	private static final String TELCEL_SALES_FORCE_ALPHANUMERIC_R = "telcel.sales.force.alphanumeric.r";

	@Resource(name = "telcelActivationService")
	private TelcelActivationService activationService;

	@Resource(name = "defaultConfigurationService")
	private ConfigurationService configurationService;
	@Resource(name = "telcelSerieIccidDao")
	private TelcelSerieIccidDao telcelSerieIccidDao;


	/**
	 * The enum Transition.
	 */
	public enum Transition
	{
		/**
		 * Requires activation transition.
		 */
		REQUIRES_ACTIVATION,
		/**
		 * No activation required transition.
		 */
		NO_ACTIVATION_REQUIRED;

		/**
		 * Gets string values.
		 *
		 * @return the string values
		 */
		public static Set<String> getStringValues()
		{
			final Set<String> res = new HashSet<String>();

			for (final Transition transition : Transition.values())
			{
				res.add(transition.toString());
			}
			return res;
		}
	}

	@Override
	public String execute(final ConsignmentProcessModel process) throws RetryLaterException, Exception
	{
		if (LOG.isInfoEnabled())
		{
			LOG.info("Process: " + process.getCode() + " in step " + getClass());
		}
		final AbstractOrderModel orderModel = process.getConsignment().getOrder();
		final ConsignmentModel consignmentModel = process.getConsignment();
		final ConsignmentEntryModel consignmentEntryModel = process.getConsignment().getConsignmentEntries().iterator().next();
		final AbstractOrderEntryModel orderEntry = consignmentEntryModel.getOrderEntry();
		final boolean activable = orderEntry != null && orderEntry.getProduct() != null && orderEntry.getProduct().getActivable();

		if (activable)
		{
			consignmentModel.setStatus(ConsignmentStatus.PROCESSING_ACTIVATION);
			getModelService().save(consignmentModel);

			if (consignmentModel.getSeriesICCID() == null)
			{
				LOG.error(String.format("The consignment doesnt have SerieICCID: %s", consignmentModel.getCode()));
			}

			final SeriesImeiModel imeiModel = consignmentModel.getSeriesImei();
			final SeriesICCIDModel iccidModel = consignmentModel.getSeriesICCID();
			String imei = "";
			if(Objects.nonNull(consignmentModel.getSeriesImei())){
				if(StringUtils.isNotEmpty(imeiModel.getImei())){
					modelService.refresh(consignmentModel);
					LOG.info("The consignment:::" + consignmentModel.getCode() + " have IMEI:::" + consignmentModel.getSeriesImei().getImei());
					imei = consignmentModel.getSeriesImei().getImei();
				}else{
					LOG.info("The consignment:::" + consignmentModel.getCode() + " doesnt have IMEI:::" + consignmentModel.getSeriesImei().getImei());
				}
			}else{
				List<SeriesImeiModel> seriesIMEIModels = telcelSerieIccidDao.getIMEIByConsignmentCode(consignmentModel.getCode());
				if(seriesIMEIModels.size()>0){
					if(StringUtils.isNotEmpty(seriesIMEIModels.get(0).getImei())){
						imei = seriesIMEIModels.get(0).getImei();
						LOG.info("the IMEI:" + imei + " was obtained by query");
					}else{
						LOG.info("The seriesIMEI Object, not has value imei.");
					}
				}else{
					LOG.info("Query IMEI Empty");
				}
			}

			String iccid = "";
			if(Objects.nonNull(consignmentModel.getSeriesICCID())){
				if(StringUtils.isNotEmpty(iccidModel.getIccid())){
					modelService.refresh(consignmentModel);
					LOG.info("The consignment:::" + consignmentModel.getCode() + " have ICCID:::" + consignmentModel.getSeriesICCID().getIccid());
					iccid = consignmentModel.getSeriesICCID().getIccid();
				}else{
					LOG.info("The consignment:::" + consignmentModel.getCode() + " doesnt have ICCID:::" + consignmentModel.getSeriesICCID().getIccid());
				}
			}else{
				List<SeriesICCIDModel> serieICCIDModels = telcelSerieIccidDao.getIccidByConsignmentCode(consignmentModel.getCode());
				if(serieICCIDModels.size()>0){
					if(StringUtils.isNotEmpty(serieICCIDModels.get(0).getIccid())){
						iccid = serieICCIDModels.get(0).getIccid();
						LOG.info("the ICCID:" + iccid + " was obtained by query");
					}else{
						LOG.info("The seriesICCID Object, not has value iccid.");
					}
				}else{
					LOG.info("Query ICCID Empty");
				}
			}

			final String esquemaDeCobro = orderEntry.getEsquemaCobro() != null ? orderEntry.getEsquemaCobro().getCode()
					: STRING_EMPTY;

			final String region = orderModel.getRegionCode();
			String salesForceId = configurationService.getConfiguration().getString(TELCEL_SALES_FORCE_R + region);
			String salesForceAlphanumeric = configurationService.getConfiguration()
					.getString(TELCEL_SALES_FORCE_ALPHANUMERIC_R + region);

			//DAT ACTIVACIONES
			if (Objects.nonNull(orderModel.getUserinfodat()))
			{
				final UserInfoDATModel dat = orderModel.getUserinfodat();
				LOG.info("ACTIVACIONES DAT");
				LOG.info("ID FUERZA VENTA ACTIVA : " + dat.getIdFuerzaVentaActiva());
				LOG.info("FUERZA VENTA ACTIVA : " + dat.getFuerzaVentaActiva());
				salesForceId = dat.getIdFuerzaVentaActiva();
				salesForceAlphanumeric = dat.getFuerzaVentaActiva();
			}
			//CVT ACTIVACIONES
			if (Objects.nonNull(orderModel.getUserinfocvt()))
			{
				final UserInfoCVTModel cvt = orderModel.getUserinfocvt();
				LOG.info("ACTIVACIONES CVT");
				LOG.info("REGION : " + region);
				if (region.trim().equals("9"))
				{
					LOG.info("fvPrepagoPadre : " + cvt.getFvPrepagoPadre());
					LOG.info("fvPospagoPadre. : " + cvt.getFvPospagoPadre());
					salesForceId = cvt.getFvPrepagoPadre();
					salesForceAlphanumeric = cvt.getFvPospagoPadre();
				}
				else
				{
					LOG.info("fvPrepagoPersonal : " + cvt.getFvPrepagoPersonal());
					LOG.info("fvPospagoPersonal. : " + cvt.getFvPospagoPersonal());
					salesForceId = cvt.getFvPrepagoPersonal();
					salesForceAlphanumeric = cvt.getFvPospagoPersonal();
				}
			}

			String postalCode = "";
			final AddressModel deliveryAddress = orderModel.getDeliveryAddress();
			if (deliveryAddress != null)
			{
				postalCode = deliveryAddress.getPostalcode();
			}

			final ProductOrderingResponse response = this.activationService.activationPost(region, postalCode, imei, iccid,
					salesForceId, salesForceAlphanumeric, orderEntry, esquemaDeCobro, consignmentModel,
					orderModel.getStore().getUid());
			getModelService().save(consignmentModel);

			if (response != null)
			{
				LOG.info("## Asignacion ##");
				LOG.info("## ActivacionId::" + response.getId());
				LOG.info("## ActivacionState:: " + response.getState());
				consignmentEntryModel.setActivacionId(response.getId());
				consignmentEntryModel.setActivacionState(response.getState());

				if (BooleanUtils.isFalse(response.getError())) {
					LOG.info("El Estatus del IMEI,es Actualizado a ACTIVADO");
					if (imeiModel != null) {
						LOG.info("Save Activado");
						imeiModel.setActivado(Boolean.TRUE);
						getModelService().save(imeiModel);
					}
				}

				iccidModel.setActivado(Boolean.FALSE);
				iccidModel.setActivacionError(response.getError());
				iccidModel.setActivacionId(response.getId());
				iccidModel.setActivacionState(response.getState());
				getModelService().save(iccidModel);

				getModelService().save(consignmentEntryModel);
				getModelService().refresh(consignmentEntryModel);
			}
			process.setWaitingForReadyToPack(true);
			getModelService().save(process);
			return Transition.REQUIRES_ACTIVATION.toString();
		}
		else
		{
			LOG.info("Activation:: consignment Code:" + consignmentModel.getCode() + " Product Code:: " + orderEntry.getProduct().getCode() + " Product sku:: " +orderEntry.getProduct().getSku() + " Not activatable");
			consignmentModel.setStatus(ConsignmentStatus.READY_TO_PACK);
			getModelService().save(consignmentModel);
			return Transition.NO_ACTIVATION_REQUIRED.toString();
		}

	}

	@Override
	public Set<String> getTransitions()
	{
		return Transition.getStringValues();
	}
}
