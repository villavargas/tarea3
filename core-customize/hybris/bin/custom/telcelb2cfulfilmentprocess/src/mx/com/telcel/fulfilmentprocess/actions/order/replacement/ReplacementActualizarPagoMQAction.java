package mx.com.telcel.fulfilmentprocess.actions.order.replacement;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import mx.com.telcel.core.services.mq.facturacion.FacturacionMQService;


public class ReplacementActualizarPagoMQAction extends AbstractProceduralAction<OrderProcessModel>
{

	private static final Logger LOG = Logger.getLogger(ReplacementActualizarPagoMQAction.class);
	private static final String PROCESS_MSG = "Process: ";

	@Resource(name = "facturacionMQService")
	private FacturacionMQService facturacionMQService;

	@Override
	public void executeAction(final OrderProcessModel process) throws Exception
	{
		LOG.info(PROCESS_MSG + process.getCode() + " in step " + getClass());
		final OrderModel orderModel = process.getOrder();
		orderModel.setStatus(OrderStatus.VALID_VOUCHERS);
		for (final ConsignmentModel consignmentModel : orderModel.getConsignments())
		{
			facturacionMQService.updatePaymentRequest(consignmentModel.getPoNoSalesDocument(), consignmentModel.getPoNoDelivery(),
					consignmentModel.getJmsid());
		}

		orderModel.setPendingForPaymentMQ(true);
		getModelService().save(orderModel);
	}

}
