/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.fulfilmentprocess.actions.order.replacement;

import de.hybris.platform.orderprocessing.events.OrderCompletedEvent;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.servicelayer.event.EventService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * The type Send order completed notification action.
 */
public class SendOrderCompletedNotificationAction extends AbstractProceduralAction<OrderProcessModel>
{
	private static final Logger LOG = Logger.getLogger(SendOrderCompletedNotificationAction.class);

	private EventService eventService;

	@Override
	public void executeAction(final OrderProcessModel process)
	{
		//getEventService().publishEvent(new OrderPlacedEvent(process));
		getEventService().publishEvent(new OrderCompletedEvent(process));
		LOG.info("Process: " + process.getCode() + " in step " + getClass());
	}

	/**
	 * Gets event service.
	 *
	 * @return the event service
	 */
	protected EventService getEventService()
	{
		return eventService;
	}

	/**
	 * Sets event service.
	 *
	 * @param eventService
	 *           the event service
	 */
	@Required
	public void setEventService(final EventService eventService)
	{
		this.eventService = eventService;
	}
}
