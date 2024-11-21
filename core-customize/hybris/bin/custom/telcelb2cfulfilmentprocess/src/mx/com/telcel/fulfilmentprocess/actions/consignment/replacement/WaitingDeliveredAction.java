/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.fulfilmentprocess.actions.consignment.replacement;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.action.AbstractAction;
import de.hybris.platform.task.RetryLaterException;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

/**
 * The type Waiting delivered action.
 */
public class WaitingDeliveredAction extends AbstractAction<ConsignmentProcessModel> {
    private static final Logger LOG = Logger.getLogger(WaitingDeliveredAction.class);

    /**
     * The enum Transition.
     */
    public enum Transition {
        /**
         * Ok transition.
         */
        OK,
        /**
         * Wait transition.
         */
        WAIT,
        /**
         * Error transition.
         */
        ERROR;

        /**
         * Gets string values.
         *
         * @return the string values
         */
        public static Set<String> getStringValues() {
            final Set<String> res = new HashSet<String>();

            for (final mx.com.telcel.fulfilmentprocess.actions.consignment.WaitingDeliveredAction.Transition transition : mx.com.telcel.fulfilmentprocess.actions.consignment.WaitingDeliveredAction.Transition.values()) {
                res.add(transition.toString());
            }
            return res;
        }
    }

    @Override
    public String execute(ConsignmentProcessModel consignmentProcessModel) throws RetryLaterException, Exception {
        LOG.info("Process: " + consignmentProcessModel.getCode() + " in step was Waiting Delivered Action WAIT - " + getClass());
        ConsignmentModel consignmentModel = consignmentProcessModel.getConsignment();
        String status = mx.com.telcel.fulfilmentprocess.actions.consignment.WaitingDeliveredAction.Transition.WAIT.toString();
        if (consignmentModel.getStatus().equals(ConsignmentStatus.DELIVERY_PROBLEM)) {
            status = mx.com.telcel.fulfilmentprocess.actions.consignment.WaitingDeliveredAction.Transition.ERROR.toString();
        } else if (consignmentModel.getStatus().equals(ConsignmentStatus.ORDER_DELIVERED)) {
            status = mx.com.telcel.fulfilmentprocess.actions.consignment.WaitingDeliveredAction.Transition.OK.toString();
        }
        return status;
    }

    @Override
    public Set<String> getTransitions() {
        return mx.com.telcel.fulfilmentprocess.actions.consignment.WaitingDeliveredAction.Transition.getStringValues();
    }
}
