/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.fulfilmentprocess.actions.order.replacement;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.servicelayer.time.TimeService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * The type Set order expiration time action.
 */
public class SetOrderExpirationTimeAction extends AbstractProceduralAction<OrderProcessModel> {
    private static final Logger LOG = Logger.getLogger(SetOrderExpirationTimeAction.class);

    private TimeService timeService;

    @Override
    public void executeAction(final OrderProcessModel process) {
        if (LOG.isInfoEnabled()) {
            LOG.info("Process: " + process.getCode() + " in step " + getClass());
        }
        final OrderModel order = process.getOrder();
        order.setExpirationTime(getTimeService().getCurrentTime());
        getModelService().save(order);
    }

    /**
     * Gets time service.
     *
     * @return the time service
     */
    protected TimeService getTimeService() {
        return timeService;
    }

    /**
     * Sets time service.
     *
     * @param timeService the time service
     */
    @Required
    public void setTimeService(final TimeService timeService) {
        this.timeService = timeService;
    }
}
