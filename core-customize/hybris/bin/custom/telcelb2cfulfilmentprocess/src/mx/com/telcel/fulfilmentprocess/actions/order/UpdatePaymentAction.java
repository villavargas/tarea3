/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.fulfilmentprocess.actions.order;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.task.RetryLaterException;
import mx.com.telcel.core.model.HolderLineModel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import mx.com.telcel.core.helper.TelcelEmailsHelper;

import javax.annotation.Resource;

/**
 * The type Update payment action.
 */
public class UpdatePaymentAction extends AbstractProceduralAction<OrderProcessModel> {
    private static final Logger LOG = Logger.getLogger(UpdatePaymentAction.class);
    private static final String PROCESS_MSG = "Process: ";

    @Resource(name = "telcelEmailsHelper")
    private TelcelEmailsHelper telcelEmailsHelper;


    @Override
    public void executeAction(OrderProcessModel process) throws RetryLaterException, Exception {
        LOG.info(PROCESS_MSG + process.getCode() + " in step " + getClass());
        OrderModel orderModel = process.getOrder();
        HolderLineModel addressHolderLine = process.getOrder().getAddressHolderLine();
        String paymentType = process.getOrder().getPaymentInfo().getPaymentType();
        try {
            telcelEmailsHelper.sendSuccessfulPurchaseEmail(addressHolderLine.getEmail(), process.getOrder(), paymentType);
        } catch (Exception e) {
            LOG.info("Error to send sendSuccessfulPurchaseEmail");
            LOG.info(e.getStackTrace());
        }
        LOG.info("Preparing to save order status WAITING_BILLING");
        orderModel.setStatus(OrderStatus.WAITING_BILLING);
        getModelService().save(orderModel);
        LOG.info("the order status was saved to: WAITING_BILLING");
        LOG.info(PROCESS_MSG + process.getCode() + " Waiting for billing in step " + getClass());
    }

}
