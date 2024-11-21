/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.fulfilmentprocess.actions.order;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import mx.com.telcel.core.services.mq.facturacion.FacturacionMQService;
import org.apache.log4j.Logger;

import javax.annotation.Resource;


/**
 * The type Actualizar pago mq action.
 */
public class ActualizarPagoMQAction extends AbstractProceduralAction<OrderProcessModel>
{
	private static final Logger LOG = Logger.getLogger(ActualizarPagoMQAction.class);
	private static final String PROCESS_MSG = "Process: ";

	@Resource(name = "facturacionMQService")
	private FacturacionMQService facturacionMQService;

	@Override
	public void executeAction(final OrderProcessModel process) throws Exception
	{
		LOG.info(PROCESS_MSG + process.getCode() + " in step " + getClass());
		final OrderModel orderModel = process.getOrder();
		setOrderStatus(orderModel, OrderStatus.VALID_VOUCHERS);

		for (ConsignmentModel consignmentModel : orderModel.getConsignments()) {
			facturacionMQService.updatePaymentRequest(consignmentModel.getPoNoSalesDocument(),
					consignmentModel.getPoNoDelivery(), consignmentModel.getJmsid());
		}

		orderModel.setPendingForPaymentMQ(true);
		getModelService().save(orderModel);
	}
}
