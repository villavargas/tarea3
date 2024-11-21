/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.fulfilmentprocess.actions.order;

import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.task.RetryLaterException;
import mx.com.telcel.core.helper.TelcelEmailsHelper;
import org.apache.log4j.Logger;

/**
 * The type Send cancel notification action.
 */
public class SendCancelNotificationAction extends AbstractProceduralAction<OrderProcessModel> {

    private static final Logger LOG = Logger.getLogger(SendCancelNotificationAction.class);

    private EventService eventService;
    private TelcelEmailsHelper telcelEmailsHelper;

    @Override
    public void executeAction(OrderProcessModel process) throws RetryLaterException, Exception {
        if (LOG.isInfoEnabled()) {
            LOG.info("Process: " + process.getCode() + " in step " + getClass());
        }
        telcelEmailsHelper.sendCancellationUnderpaymentEmail(process.getOrder());
    }

    /**
     * Gets event service.
     *
     * @return the event service
     */
    public EventService getEventService() {
        return eventService;
    }

    /**
     * Sets event service.
     *
     * @param eventService the event service
     */
    public void setEventService(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Gets telcel emails helper.
     *
     * @return the telcel emails helper
     */
    public TelcelEmailsHelper getTelcelEmailsHelper() {
        return telcelEmailsHelper;
    }

    /**
     * Sets telcel emails helper.
     *
     * @param telcelEmailsHelper the telcel emails helper
     */
    public void setTelcelEmailsHelper(TelcelEmailsHelper telcelEmailsHelper) {
        this.telcelEmailsHelper = telcelEmailsHelper;
    }
}
