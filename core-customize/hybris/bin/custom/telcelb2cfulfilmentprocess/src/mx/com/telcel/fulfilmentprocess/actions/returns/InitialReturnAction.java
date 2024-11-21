/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.fulfilmentprocess.actions.returns;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.basecommerce.enums.RefundReason;
import de.hybris.platform.basecommerce.enums.ReturnAction;
import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.processengine.action.AbstractAction;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Set;


/**
 * Check whether the return request is an instore or an online request and redirects it to the appropriate step.
 */
public class InitialReturnAction extends AbstractAction<ReturnProcessModel>
{
	private static final Logger LOG = Logger.getLogger(InitialReturnAction.class);

	protected enum Transition
	{
		ONLINE, INSTORE;

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

	@Override
	public String execute(final ReturnProcessModel process)
	{
		LOG.info("Process: " + process.getCode() + " in step " + getClass().getSimpleName());

		final ReturnRequestModel returnRequest = process.getReturnRequest();

		final String transition = returnRequest.getReturnEntries().stream().allMatch(entry -> entry.getAction().equals(
				ReturnAction.IMMEDIATE)) ? Transition.INSTORE.toString() : Transition.ONLINE.toString();

		LOG.debug("Process: " + process.getCode() + " transitions to " + transition);

		returnRequest.getReturnEntries().stream().forEach(entry -> {
			//Para cuando termine el rma, el id de consignment tenga su propio estatus
			final RefundEntryModel refundEntryModel = (RefundEntryModel) entry;
			ConsignmentModel consignmentModel = refundEntryModel.getConsignment();
			if (consignmentModel != null) {
				final RefundReason refundReason = refundEntryModel.getReason();
				if (RefundReason.BANKSTATEMENT.equals(refundReason)) {
					consignmentModel.setStatus(ConsignmentStatus.BANK_STATEMENT);
				} else if (RefundReason.FRAUDRISK.equals(refundReason)) {
					consignmentModel.setStatus(ConsignmentStatus.FRAUD_RISK);
				} else if (RefundReason.DELIVERYFAILED.equals(refundReason)) {
					consignmentModel.setStatus(ConsignmentStatus.DELIVERY_FAILED);
				} else if (RefundReason.CANCELPRODTEST.equals(refundReason)) {
					consignmentModel.setStatus(ConsignmentStatus.CANCEL_PROD_TEST);
				} else if (RefundReason.CANCELINCOMPLETEORDER.equals(refundReason)) {
					consignmentModel.setStatus(ConsignmentStatus.CANCEL_INCOMPLETE_ORDER);
				} else if (RefundReason.CANCELLOSS.equals(refundReason)) {
					consignmentModel.setStatus(ConsignmentStatus.CANCEL_LOSS);
				} else if (RefundReason.CANCELREQUESTCLIENT.equals(refundReason)) {
					consignmentModel.setStatus(ConsignmentStatus.CANCEL_REQUEST_CLIENT);
				} else if (RefundReason.RETURNREQUESTCLIENT.equals(refundReason)) {
					consignmentModel.setStatus(ConsignmentStatus.RETURN_REQUEST_CLIENT);
				} else if (RefundReason.CANCELINTERNALISSUE.equals(refundReason)) {
					consignmentModel.setStatus(ConsignmentStatus.CANCEL_INTERNAL_ISSUE);
				} else if (RefundReason.MANUFACTURING_DEFECTS.equals(refundReason)) {
					consignmentModel.setStatus(ConsignmentStatus.MANUFACTURING_DEFECTS);
				}
				getModelService().save(consignmentModel);
			}
		});

		return transition;
	}

	@Override
	public Set<String> getTransitions()
	{
		return Transition.getStringValues();
	}

}
