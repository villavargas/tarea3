/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.fulfilmentprocess.actions.returns;

import de.hybris.platform.processengine.action.AbstractAction;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.task.RetryLaterException;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import mx.com.telcel.core.services.refund.RefundCashPaymentCommerceService;


public class RefundPaymentCashAction extends AbstractAction<ReturnProcessModel>
{

	@Resource(name = "refundCashPaymentCommerceService")
	private RefundCashPaymentCommerceService refundCashPaymentCommerceService;

	private static final Logger LOG = Logger.getLogger(RefundPaymentCashAction.class);

	@Override
	public String execute(final ReturnProcessModel process) throws RetryLaterException, Exception
	{
		LOG.info("Process: " + process.getCode() + " in step " + getClass().getSimpleName());
		final ReturnRequestModel returnRequestModel = process.getReturnRequest();
		final boolean sendCashRefund = refundCashPaymentCommerceService.sendRefund(returnRequestModel, getModelService());
		LOG.info("SEND CASH REFUND : " + sendCashRefund);
		return Transition.OK.toString();
	}

	@Override
	public Set<String> getTransitions()
	{
		return Transition.getStringValues();
	}

	/**
	 * The enum Transition.
	 */
	protected enum Transition
	{
		OK;

		/**
		 * Gets string values.
		 *
		 * @return the string values
		 */
		public static Set<String> getStringValues()
		{
			final Set<String> res = new HashSet<String>();
			for (final Transition transition : Transition.values())
			{
				res.add(transition.toString());
			}
			return res;
		}
	}

}
