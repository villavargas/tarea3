/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.fulfilmentprocess.actions.order.telmex;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.task.RetryLaterException;
import mx.com.telcel.core.services.ordersplitting.TelcelOrderSplittingService;
import mx.com.telcel.fulfilmentprocess.constants.Telcelb2cFulfilmentProcessConstants;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Telmex split order action.
 */
public class TelmexSplitOrderAction extends AbstractProceduralAction<OrderProcessModel> {
    private static final Logger LOG = Logger.getLogger(TelmexSplitOrderAction.class);
    private TelcelOrderSplittingService telcelOrderSplittingService;
    private BusinessProcessService businessProcessService;

    @Override
    public void executeAction(OrderProcessModel process) throws RetryLaterException, Exception {
        if (LOG.isInfoEnabled()) {
            LOG.info("Process: " + process.getCode() + " in step " + getClass());
        }

        // find the order's entries that are not already allocated to consignments
        final List<AbstractOrderEntryModel> entriesToSplit = new ArrayList<AbstractOrderEntryModel>();
        for (final AbstractOrderEntryModel entry : process.getOrder().getEntries()) {
            if (entry.getConsignmentEntries() == null || entry.getConsignmentEntries().isEmpty()) {
                entriesToSplit.add(entry);
            }
        }

        final List<ConsignmentModel> consignments = getTelcelOrderSplittingService().splitTelmexOrderForConsignment(process.getOrder(),
                entriesToSplit);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Splitting order into " + consignments.size() + " consignments.");
        }

        int index = 0;
        for (final ConsignmentModel consignment : consignments) {
            final ConsignmentProcessModel subProcess = getBusinessProcessService().<ConsignmentProcessModel>createProcess(
                    process.getCode() + "_" + (++index), Telcelb2cFulfilmentProcessConstants.CONSIGNMENT_SUBPROCESS_TELMEX_NAME);
            subProcess.setParentProcess(process);
            subProcess.setConsignment(consignment);
            save(subProcess);
            getBusinessProcessService().startProcess(subProcess);
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
    public BusinessProcessService getBusinessProcessService() {
        return businessProcessService;
    }

    /**
     * Sets business process service.
     *
     * @param businessProcessService the business process service
     */
    public void setBusinessProcessService(BusinessProcessService businessProcessService) {
        this.businessProcessService = businessProcessService;
    }
}
