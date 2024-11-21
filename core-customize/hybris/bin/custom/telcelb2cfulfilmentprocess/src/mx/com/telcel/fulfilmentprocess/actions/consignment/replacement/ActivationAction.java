/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.fulfilmentprocess.actions.consignment.replacement;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.action.AbstractAction;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import mx.com.telcel.core.model.SeriesICCIDModel;
import mx.com.telcel.core.model.SeriesImeiModel;
import mx.com.telcel.facades.order.data.ProductOrderingResponse;
import mx.com.telcel.fulfilmentprocess.activation.TelcelActivationService;
import org.apache.commons.lang.BooleanUtils;
import org.apache.log4j.Logger;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

/**
 * The type Activation action.
 */
public class ActivationAction extends AbstractAction<ConsignmentProcessModel> {

    private static final Logger LOG = Logger.getLogger(ActivationAction.class);
    private static final String STRING_EMPTY = "";
    private static final String TELCEL_SALES_FORCE_R = "telcel.sales.force.r";
    private static final String TELCEL_SALES_FORCE_ALPHANUMERIC_R = "telcel.sales.force.alphanumeric.r";

    @Resource(name = "telcelActivationService")
    private TelcelActivationService activationService;

    @Resource(name = "defaultConfigurationService")
    private ConfigurationService configurationService;

    /**
     * The enum Transition.
     */
    public enum Transition {
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
        public static Set<String> getStringValues() {
            final Set<String> res = new HashSet<String>();

            for (final Transition transition : Transition.values()) {
                res.add(transition.toString());
            }
            return res;
        }
    }

    @Override
    public String execute(ConsignmentProcessModel process) throws Exception {
        if (LOG.isInfoEnabled()) {
            LOG.info("Process: " + process.getCode() + " in step " + getClass());
        }
        AbstractOrderModel orderModel = process.getConsignment().getOrder();
        ConsignmentModel consignmentModel = process.getConsignment();
        ConsignmentEntryModel consignmentEntryModel = process.getConsignment().getConsignmentEntries().iterator().next();
        AbstractOrderEntryModel orderEntry = consignmentEntryModel.getOrderEntry();
        boolean activable = orderEntry != null
                && orderEntry.getProduct() != null
                && orderEntry.getProduct().getActivable();

        if (activable) {
            consignmentModel.setStatus(ConsignmentStatus.PROCESSING_ACTIVATION);
            getModelService().save(consignmentModel);
            if (consignmentModel.getSeriesICCID() == null) {
                LOG.error(String.format("The consignment doesnt have SerieICCID: %s", consignmentModel.getCode()));
            }

            SeriesImeiModel imeiModel = consignmentModel.getSeriesImei();
            SeriesICCIDModel iccidModel = consignmentModel.getSeriesICCID();
            String imei = imeiModel != null ? imeiModel.getImei() : "";
            String iccid = iccidModel.getIccid();
            String esquemaDeCobro = orderEntry.getEsquemaCobro() != null ?
                    orderEntry.getEsquemaCobro().getCode() : STRING_EMPTY;

            String region = orderModel.getRegionCode();
            String salesForceId = configurationService.getConfiguration().getString(TELCEL_SALES_FORCE_R + region);
            String salesForceAlphanumeric = configurationService.getConfiguration().getString(TELCEL_SALES_FORCE_ALPHANUMERIC_R + region);

            String postalCode = "";
            AddressModel deliveryAddress = orderModel.getDeliveryAddress();
            if (deliveryAddress != null) {
                postalCode = deliveryAddress.getPostalcode();
            }

            ProductOrderingResponse response =
                    this.activationService.activationPost(region, postalCode, imei, iccid,
                            salesForceId, salesForceAlphanumeric, orderEntry, esquemaDeCobro,consignmentModel,orderModel.getStore().getUid());

            if (response != null) {
                consignmentEntryModel.setActivacionId(response.getId());
                consignmentEntryModel.setActivacionState(response.getState());

                if (BooleanUtils.isFalse(response.getError())) {
                    LOG.info("El Estatus del IMEI,es Actualizado a ACTIVADO");
                    if (imeiModel != null) {
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
        } else {
            consignmentModel.setStatus(ConsignmentStatus.READY_TO_PACK);
            getModelService().save(consignmentModel);
            return Transition.NO_ACTIVATION_REQUIRED.toString();
        }
    }

    @Override
    public Set<String> getTransitions() {
        return Transition.getStringValues();
    }

}
