/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.fulfilmentprocess.actions.order;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.enums.PaymentStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;

import java.util.HashSet;
import java.util.Set;

import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;


/**
 * The type Processing payment action.
 */
public class ProcessingPaymentAction extends AbstractOrderAction<OrderProcessModel>
{

	private static final Logger LOG = Logger.getLogger(ProcessingPaymentAction.class);
	private static final String PROCESS_MSG = "Process: ";

	/**
	 * The enum Transition.
	 */
	public enum Transition
	{
		/**
		 * Ok transition.
		 */
		OK,
		/**
		 * Nok transition.
		 */
		NOK,
		/**
		 * Payment cancel transition.
		 */
		PAYMENT_CANCEL;

		/**
		 * Gets string values.
		 *
		 * @return the string values
		 */
		public static Set<String> getStringValues()
		{
			final Set<String> res = new HashSet<String>();
			for (final Transition transitions : Transition.values())
			{
				res.add(transitions.toString());
			}
			return res;
		}
	}

	@Override
	public String execute(final OrderProcessModel process) throws Exception
	{
		LOG.info(PROCESS_MSG + process.getCode() + " in step " + getClass());
		final OrderModel orderModel = process.getOrder();
		setIdsToEntriesToConsignments(orderModel);

		if (orderModel.getPaymentStatus().equals(PaymentStatus.PAYMENT_NOT_MADE)
				|| orderModel.getPaymentStatus().equals(PaymentStatus.PAYMENT_ERROR)
				|| orderModel.getPaymentStatus().equals(PaymentStatus.UNAUTHORIZED_PAYMENT))
		{
			LOG.info(PROCESS_MSG + process.getCode() + " Payment cancel in step " + getClass());
			return Transition.PAYMENT_CANCEL.toString();
		}

		//pago esta correcto no importa el tipo de pago
		if (orderModel.getPaymentStatus().equals(PaymentStatus.PAID))
		{
			setOrderStatus(orderModel, OrderStatus.VALID_VOUCHERS);
			LOG.info(PROCESS_MSG + process.getCode() + " Payment ok in step " + getClass());
			return Transition.OK.toString();
		}
		else
		{
			//Se esta esperando el pago en efectivo
			if(!OrderStatus.PAYMENT_ON_VALIDATION.equals(orderModel.getStatus())){
				setOrderStatus(orderModel, OrderStatus.WAITING_FOR_PAYMENT);
			}
			LOG.info(PROCESS_MSG + process.getCode() + " Waiting for payment in step " + getClass());
			return Transition.NOK.toString();
		}
	}

	private void setIdsToEntriesToConsignments(OrderModel orderModel) {
		for (ConsignmentModel consignmentModel : orderModel.getConsignments()) {
			consignmentModel.setJmsid(StringUtils.leftPad(consignmentModel.getCode().toUpperCase(), 24, '0'));
			getModelService().save(consignmentModel);
		}
	}

	@Override
	public Set<String> getTransitions()
	{
		return Transition.getStringValues();
	}

}
