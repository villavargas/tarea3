/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.fulfilmentprocess.actions.consignment.telmex;

import de.hybris.platform.orderprocessing.events.SendPickedUpMessageEvent;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.servicelayer.event.EventService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * The type Send telmex picked up message action.
 */
public class SendTelmexPickedUpMessageAction extends AbstractProceduralAction<ConsignmentProcessModel> {
    private static final Logger LOG = Logger.getLogger(SendTelmexPickedUpMessageAction.class);

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
    protected SendPickedUpMessageEvent getEvent(final ConsignmentProcessModel process) {
        return new SendPickedUpMessageEvent(process);
    }
}
