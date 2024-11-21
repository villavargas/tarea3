/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.fulfilmentprocess.actions.order.replacement;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import mx.com.telcel.core.services.TelcelCodigosPostalesService;
import mx.com.telcel.fulfilmentprocess.CheckOrderService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Resource;

/**
 * The type Check order action.
 */
public class CheckOrderAction extends AbstractSimpleDecisionAction<OrderProcessModel> {
    private static final Logger LOG = Logger.getLogger(CheckOrderAction.class);

    private CheckOrderService checkOrderService;

    @Override
    public Transition executeAction(final OrderProcessModel process) {
        final OrderModel order = process.getOrder();

        if (order == null) {
            LOG.error("Missing the order, exiting the process");
            return Transition.NOK;
        }

        if (getCheckOrderService().check(order)) {
            //setOrderStatus(order, OrderStatus.CHECKED_VALID);
            return Transition.OK;
        } else {
            setOrderStatus(order, OrderStatus.CHECKED_INVALID);
            return Transition.NOK;
        }
    }

    /**
     * Gets check order service.
     *
     * @return the check order service
     */
    protected CheckOrderService getCheckOrderService() {
        return checkOrderService;
    }

    /**
     * Sets check order service.
     *
     * @param checkOrderService the check order service
     */
    @Required
    public void setCheckOrderService(final CheckOrderService checkOrderService) {
        this.checkOrderService = checkOrderService;
    }
}
