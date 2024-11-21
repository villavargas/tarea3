/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.fulfilmentprocess.actions.order.replacement;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import org.apache.log4j.Logger;

public class ContinueAllConsignmentAction extends AbstractProceduralAction<OrderProcessModel> {

    private static final Logger LOG = Logger.getLogger(ContinueAllConsignmentAction.class);
    private static final String PROCESS_MSG = "Process: ";
    public static final String REPLACEMENT_TELCEL_WAIT_FOR_BILLED_ORDER = "_ReplacementTelcelWaitForBilledOrder";
    private BusinessProcessService businessProcessService;

    @Override
    public void executeAction(OrderProcessModel orderProcessModel) throws Exception {
        LOG.info(PROCESS_MSG + orderProcessModel.getCode() + " in step " + getClass());
        OrderModel orderModel = orderProcessModel.getOrder();
        orderModel.setStatus(OrderStatus.BILLED_ORDER);
        getModelService().save(orderModel);
        for (ConsignmentModel consignmentModel : orderModel.getConsignments()) {
            ConsignmentProcessModel consignmentProcessModel = consignmentModel.getConsignmentProcesses().iterator().next();
            getBusinessProcessService().triggerEvent(consignmentProcessModel.getCode() + REPLACEMENT_TELCEL_WAIT_FOR_BILLED_ORDER);
        }
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
