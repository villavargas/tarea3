/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.fulfilmentprocess.actions.consignment.telmex;

import de.hybris.platform.orderprocessing.events.SendReadyForPickupMessageEvent;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.servicelayer.event.EventService;
import mx.com.telcel.fulfilmentprocess.actions.consignment.SendReadyForPickupMessageAction;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * The type Send telmex ready for pickup message action.
 */
public class SendTelmexReadyForPickupMessageAction extends AbstractProceduralAction<ConsignmentProcessModel> {
    private static final Logger LOG = Logger.getLogger(SendTelmexReadyForPickupMessageAction.class);

    private EventService eventService;

    @Override
    public void executeAction(final ConsignmentProcessModel process) {
        getEventService().publishEvent(getEvent(process));
        if (LOG.isInfoEnabled()) {
            LOG.info("Process: " + process.getCode() + " in step " + getClass());
        }
    }

    /**
     * Gets event service.
     *
     * @return the event service
     */
    protected EventService getEventService() {
        return eventService;
    }

    /**
     * Sets event service.
     *
     * @param eventService the event service
     */
    @Required
    public void setEventService(final EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Gets event.
     *
     * @param process the process
     * @return the event
     */
    protected SendReadyForPickupMessageEvent getEvent(final ConsignmentProcessModel process) {
        return new SendReadyForPickupMessageEvent(process);
    }
}
