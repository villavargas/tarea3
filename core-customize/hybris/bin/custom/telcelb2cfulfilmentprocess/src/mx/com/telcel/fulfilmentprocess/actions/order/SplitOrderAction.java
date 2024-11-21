/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.fulfilmentprocess.actions.order;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.enums.PaymentStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import mx.com.telcel.core.model.AdditionalServiceEntryModel;
import mx.com.telcel.core.model.SendErpRespModel;
import mx.com.telcel.core.service.ReplicateStockService;
import mx.com.telcel.core.service.TelcelWarehouseService;
import mx.com.telcel.core.services.ordersplitting.TelcelOrderSplittingService;
import mx.com.telcel.fulfilmentprocess.constants.Telcelb2cFulfilmentProcessConstants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;


/**
 * The type Split order action.
 */
public class SplitOrderAction extends AbstractProceduralAction<OrderProcessModel> {
    private static final Logger LOG = Logger.getLogger(SplitOrderAction.class);

    private TelcelOrderSplittingService telcelOrderSplittingService;
    private BusinessProcessService businessProcessService;
    private ReplicateStockService replicateStockService;
    private TelcelWarehouseService telcelWarehouseService;

    @Override
    public void executeAction(final OrderProcessModel process) throws Exception {

        if (LOG.isInfoEnabled()) {
            LOG.info("Process: " + process.getCode() + " in step " + getClass());
        }

        final OrderModel orderModel = process.getOrder();
        List<ConsignmentModel> consignments = new ArrayList<>();
        for (AbstractOrderEntryModel entry : orderModel.getEntries()) {
            // find the order's entries that are not already allocated to consignments
            final List<AbstractOrderEntryModel> entriesToSplit = new ArrayList<AbstractOrderEntryModel>();
            entriesToSplit.add(entry);
            if(CollectionUtils.isEmpty(entry.getSendErpRespList())){
                throw new Exception(String.format("Entry for order %s doesn't have SendErpResp models", orderModel.getCode()));
            }
            int x = 0;
            for(SendErpRespModel sendErpRespModel : entry.getSendErpRespList()){
                if (CollectionUtils.isNotEmpty(entry.getAdditionalServiceEntries())) {
                    try {
                        AdditionalServiceEntryModel additionalServiceEntryModel = entry.getAdditionalServiceEntries().get(x);
                        if (!additionalServiceEntryModel.getRejected()) {
                            sendErpRespModel.setInlcuyeRecarga(true);
                            sendErpRespModel.setAdditionalServiceEntry(additionalServiceEntryModel);
                        } else {
                            sendErpRespModel.setInlcuyeRecarga(false);
                        }
                    } catch (Exception e) {
                        //the values of the list are not the same as those of the recharges
                    }
                    modelService.save(sendErpRespModel);
                }
                x++;
            }
            consignments.addAll(getTelcelOrderSplittingService().splitTelcelOrderForEntry(process.getOrder(), entry.getSendErpRespList(), entriesToSplit));
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Splitting order into " + consignments.size() + " consignments.");
        }

        int index = 0;
        for (final ConsignmentModel consignment : consignments) {
            final ConsignmentProcessModel subProcess = getBusinessProcessService().<ConsignmentProcessModel>createProcess(
                    process.getCode() + "_" + System.currentTimeMillis() + "_" + (++index), Telcelb2cFulfilmentProcessConstants.CONSIGNMENT_SUBPROCESS_NAME);
            subProcess.setParentProcess(process);
            subProcess.setConsignment(consignment);
            save(subProcess);
            getBusinessProcessService().startProcess(subProcess);
        }

        orderModel.setLastStatusDate(new Date());
        if (!OrderStatus.PAYMENT_ON_VALIDATION.equals(orderModel.getStatus())) {
            setOrderStatus(orderModel, OrderStatus.CREATED);
        }
        getModelService().saveAll();

        if (!PaymentStatus.PAID.equals(orderModel.getPaymentStatus())) {
            changeConsignmentsStatus(orderModel.getConsignments());
        }
    }

    private void changeConsignmentsStatus(Set<ConsignmentModel> consignments) {
        if (CollectionUtils.isNotEmpty(consignments)) {
            consignments.stream().forEach(consignmentModel -> {
                consignmentModel.setStatus(ConsignmentStatus.WAITING_PAYMENT);
                modelService.save(consignmentModel);
            });
        }
    }

    /**
     * Gets telcel order splitting service.
     *
     * @return the telcel order splitting service
     */
    public TelcelOrderSplittingService getTelcelOrderSplittingService() {
        return telcelOrderSplittingService;
    }

    /**
     * Sets telcel order splitting service.
     *
     * @param telcelOrderSplittingService the telcel order splitting service
     */
    public void setTelcelOrderSplittingService(TelcelOrderSplittingService telcelOrderSplittingService) {
        this.telcelOrderSplittingService = telcelOrderSplittingService;
    }

    /**
     * Gets business process service.
     *
     * @return the business process service
     */
    protected BusinessProcessService getBusinessProcessService() {
        return businessProcessService;
    }

    /**
     * Sets business process service.
     *
     * @param businessProcessService the business process service
     */
    @Required
    public void setBusinessProcessService(final BusinessProcessService businessProcessService) {
        this.businessProcessService = businessProcessService;
    }


    /**
     * Gets replicate stock service.
     *
     * @return the replicate stock service
     */
    public ReplicateStockService getReplicateStockService() {
        return replicateStockService;
    }

    /**
     * Sets replicate stock service.
     *
     * @param replicateStockService the replicate stock service
     */
    public void setReplicateStockService(ReplicateStockService replicateStockService) {
        this.replicateStockService = replicateStockService;
    }

    /**
     * Gets telcel warehouse service.
     *
     * @return the telcel warehouse service
     */
    public TelcelWarehouseService getTelcelWarehouseService() {
        return telcelWarehouseService;
    }

    /**
     * Sets telcel warehouse service.
     *
     * @param telcelWarehouseService the telcel warehouse service
     */
    public void setTelcelWarehouseService(TelcelWarehouseService telcelWarehouseService) {
        this.telcelWarehouseService = telcelWarehouseService;
    }
}
