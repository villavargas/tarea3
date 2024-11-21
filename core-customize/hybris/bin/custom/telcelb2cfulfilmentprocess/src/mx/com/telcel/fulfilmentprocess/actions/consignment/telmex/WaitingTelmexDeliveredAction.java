/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.fulfilmentprocess.actions.consignment.telmex;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.action.AbstractAction;
import de.hybris.platform.task.RetryLaterException;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

/**
 * The type Waiting telmex delivered action.
 */
public class WaitingTelmexDeliveredAction extends AbstractAction<ConsignmentProcessModel> {
    private static final Logger LOG = Logger.getLogger(WaitingTelmexDeliveredAction.class);

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

            for (final Transition transition : Transition.values()) {
                res.add(transition.toString());
            }
            return res;
        }
    }

    @Override
    public String execute(ConsignmentProcessModel consignmentProcessModel) throws RetryLaterException, Exception {
        LOG.info("Process: " + consignmentProcessModel.getCode() + " in step was Waiting Delivered Action WAIT - " + getClass());
        ConsignmentModel consignmentModel = consignmentProcessModel.getConsignment();
        String status = Transition.WAIT.toString();
        if (consignmentModel.getStatus().equals(ConsignmentStatus.DELIVERY_PROBLEM)) {
            status = Transition.ERROR.toString();
        } else if (consignmentModel.getStatus().equals(ConsignmentStatus.ORDER_DELIVERED)) {
            status = Transition.OK.toString();
        }
        return status;
    }

    @Override
    public Set<String> getTransitions() {
        return Transition.getStringValues();
    }
}
