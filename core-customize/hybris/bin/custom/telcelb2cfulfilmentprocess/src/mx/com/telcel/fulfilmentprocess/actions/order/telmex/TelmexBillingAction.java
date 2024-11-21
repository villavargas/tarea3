/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.fulfilmentprocess.actions.order.telmex;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.task.RetryLaterException;
import org.apache.log4j.Logger;

/**
 * The type Telmex billing action.
 */
public class TelmexBillingAction extends AbstractProceduralAction<OrderProcessModel> {
    private static final Logger LOG = Logger.getLogger(TelmexBillingAction.class);
    private static final String PROCESS_MSG = "Process: ";

    @Override
    public void executeAction(OrderProcessModel process) throws RetryLaterException, Exception {
        final OrderModel orderModel = process.getOrder();
        LOG.info(PROCESS_MSG + process.getCode() + " in step " + getClass());
        orderModel.setStatus(OrderStatus.BILLED_ORDER);
        getModelService().save(orderModel);
    }
}
