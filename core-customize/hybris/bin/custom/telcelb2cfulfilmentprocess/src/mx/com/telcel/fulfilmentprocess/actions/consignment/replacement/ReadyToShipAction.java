/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.fulfilmentprocess.actions.consignment.replacement;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.action.AbstractAction;
import de.hybris.platform.task.RetryLaterException;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 * The type Ready to ship action.
 */
public class ReadyToShipAction extends AbstractAction<ConsignmentProcessModel>
{

	private static final Logger LOG = Logger.getLogger(ReadyToShipAction.class);

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
		 * Wait transition.
		 */
		WAIT;

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

	@Override
	public String execute(final ConsignmentProcessModel consignmentProcessModel) throws RetryLaterException, Exception
	{
		LOG.info("Process: " + consignmentProcessModel.getCode() + " in step was Ready To Ship Action - " + getClass());
		final ConsignmentModel consignmentModel = consignmentProcessModel.getConsignment();
		LOG.info("*Consignment : " + consignmentModel.getCode());
		LOG.info("isWaitingForReadyToShip : " + consignmentProcessModel.isWaitingForReadyToShip());
		LOG.info("getRunPickupAfter : " + consignmentModel.getRunPickupAfter());
		if ((consignmentProcessModel.isWaitingForReadyToShip() && StringUtils.isNotBlank(consignmentModel.getIdCreatePickup()))
				|| (consignmentModel.getRunPickupAfter() != null && consignmentModel.getRunPickupAfter()))
		{
			consignmentProcessModel.setWaitingForReadyToShip(false);
			modelService.save(consignmentProcessModel);
			consignmentModel.setStatus(ConsignmentStatus.ORDER_SENT);
			modelService.save(consignmentModel);
			return Transition.OK.toString();

		}
		LOG.info("Process: " + consignmentProcessModel.getCode() + " in step was Ready To Ship Action WAIT - " + getClass());
		LOG.info("Process: " + consignmentProcessModel.getCode() + " WAIT FOR READY TO SHIP- " + getClass());
		return Transition.WAIT.toString();
	}

	@Override
	public Set<String> getTransitions()
	{
		return Transition.getStringValues();
	}

}
