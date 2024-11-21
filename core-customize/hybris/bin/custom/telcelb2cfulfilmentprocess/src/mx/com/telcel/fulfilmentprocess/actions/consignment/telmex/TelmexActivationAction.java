/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.fulfilmentprocess.actions.consignment.telmex;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.action.AbstractAction;
import de.hybris.platform.task.RetryLaterException;
import mx.com.telcel.core.model.SeriesICCIDModel;
import mx.com.telcel.core.model.SeriesImeiModel;
import mx.com.telcel.facades.order.data.ProductOrderingResponse;
import mx.com.telcel.fulfilmentprocess.activation.TelcelActivationService;
import org.apache.commons.lang.BooleanUtils;
import org.apache.log4j.Logger;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * The type Telmex activation action.
 */
public class TelmexActivationAction extends AbstractAction<ConsignmentProcessModel> {

    private static final Logger LOG = Logger.getLogger(TelmexActivationAction.class);
    private static final String STRING_EMPTY = "";
    private static final String ASL = "ASL";


    @Resource(name = "telcelActivationService")
    private TelcelActivationService activationService;

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
    public String execute(ConsignmentProcessModel process) throws RetryLaterException, Exception {
        if (LOG.isInfoEnabled()) {
            LOG.info("Process: " + process.getCode() + " in step " + getClass());
        }
        AbstractOrderModel orderModel = process.getConsignment().getOrder();
        ConsignmentModel consignmentModel = process.getConsignment();
        ConsignmentEntryModel consignmentEntryModel = consignmentModel.getConsignmentEntries().iterator().next();
        AbstractOrderEntryModel orderEntry = consignmentEntryModel.getOrderEntry();
        boolean activable = orderEntry != null
                && orderEntry.getProduct() != null
                && orderEntry.getProduct().getActivable();

        if (activable) {
            consignmentModel.setStatus(ConsignmentStatus.PROCESSING_ACTIVATION);
            getModelService().save(consignmentModel);

            for (int i = 1; i <= orderEntry.getQuantity(); ++i) {
                Optional<SeriesImeiModel> imeiModel = orderEntry.getSeriesIMEI()
                        .stream().filter(obj -> !obj.getActivado()).findFirst();
                Optional<SeriesICCIDModel> iccidModel = orderEntry.getSeriesICCID()
                        .stream().filter(obj -> !obj.getActivado()).findFirst();
                String imei = imeiModel.isPresent() ? imeiModel.get().getImei() : "";
                String iccid = iccidModel.isPresent() ? iccidModel.get().getIccid() : "";

                //todo
                consignmentModel.setTelmexActivationId(iccidModel.isPresent() ? iccidModel.get().getIccid() : "");
                getModelService().save(consignmentModel);

                String esquemaDeCobro = ASL;

                String region = consignmentEntryModel.getConsignment().getOrder().getRegionCode();

                final String salesForceTelmex = orderEntry.getOrder().getSalesForceTelmexDistId() != null
                        ? orderEntry.getOrder().getSalesForceTelmexDistId() : STRING_EMPTY;

                final String salesForceAlphanumeric = orderEntry.getOrder().getSalesForceTelmexId() != null
                        ? orderEntry.getOrder().getSalesForceTelmexId() : STRING_EMPTY;

                String postalCode = "";
                AddressModel deliveryAddress = consignmentEntryModel.getOrderEntry().getOrder().getDeliveryAddress();
                if (deliveryAddress != null) {
                    postalCode = deliveryAddress.getPostalcode();
                }

                ProductOrderingResponse response =
                        this.activationService.activationPost(region, postalCode, imei, iccid,
                                salesForceTelmex, salesForceAlphanumeric, orderEntry, esquemaDeCobro,consignmentModel,orderModel.getStore().getUid());

                if (response != null) {
                    consignmentEntryModel.setActivacionId(response.getId());
                    consignmentEntryModel.setActivacionState(response.getState());

                    if (BooleanUtils.isFalse(response.getError())) {
                        LOG.info("El Estatus del IMEI,es Actualizado a ACTIVADO");
                        if (imeiModel.isPresent()) {
                            imeiModel.get().setActivado(Boolean.TRUE);
                            getModelService().save(imeiModel.get());
                        }
                    }

                    if (iccidModel.isPresent()) {
                        iccidModel.get().setActivado(Boolean.FALSE);
                        iccidModel.get().setActivacionError(response.getError());
                        iccidModel.get().setActivacionId(response.getId());
                        iccidModel.get().setActivacionState(response.getState());
                        iccidModel.get().setImeiActivado(imei);
                        getModelService().save(iccidModel.get());
                    }
                    getModelService().save(consignmentEntryModel);
                    getModelService().refresh(consignmentEntryModel);
                }
            }
            this.activationService.ejecutaCronMensajesMq();
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
