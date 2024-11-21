/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.fulfilmentprocess.actions.order;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.task.RetryLaterException;

import java.util.Set;

import org.apache.log4j.Logger;


/**
 * The type Cancel payment action.
 */
public class CancelPaymentAction extends AbstractProceduralAction<OrderProcessModel>
{
	private static final Logger LOG = Logger.getLogger(CancelPaymentAction.class);
	private static final String PROCESS_MSG = "Process: ";

	@Override
	public void executeAction(final OrderProcessModel process) throws RetryLaterException, Exception
	{
		LOG.info(PROCESS_MSG + process.getCode() + " in step " + getClass());
		final OrderModel orderModel = process.getOrder();
		cancelConsignments(orderModel);
		setOrderStatus(orderModel, OrderStatus.CANCELLED);
		//liberar productos de SAP
		LOG.info(PROCESS_MSG + process.getCode() + "Order cancelled in step " + getClass());
	}

	private void cancelConsignments(final OrderModel order)
	{
		final Set<ConsignmentModel> consignments = order.getConsignments();
		for (final ConsignmentModel con : consignments)
		{
			con.setStatus(ConsignmentStatus.CANCELLED);
			modelService.save(con);
		}
	}

}
