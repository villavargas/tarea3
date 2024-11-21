/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.fulfilmentprocess.actions.order.replacement;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.task.RetryLaterException;
import org.apache.log4j.Logger;

/**
 * The type Update payment action.
 */
public class UpdatePaymentAction extends AbstractProceduralAction<OrderProcessModel> {
    private static final Logger LOG = Logger.getLogger(UpdatePaymentAction.class);
    private static final String PROCESS_MSG = "Process: ";

    @Override
    public void executeAction(OrderProcessModel process) throws RetryLaterException, Exception {
        LOG.info(PROCESS_MSG + process.getCode() + " in step " + getClass());
        OrderModel orderModel = process.getOrder();
        orderModel.setStatus(OrderStatus.WAITING_BILLING);
        getModelService().save(orderModel);
        LOG.info(PROCESS_MSG + process.getCode() + " Waiting for billing in step " + getClass());
    }

}
