/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.fulfilmentprocess.actions.consignment.replacement;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.action.AbstractAction;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

/**
 * The type Confirm consignment pickup action.
 */
public class ConfirmConsignmentPickupAction extends AbstractAction<ConsignmentProcessModel> {
    private static final Logger LOG = Logger.getLogger(ConfirmConsignmentPickupAction.class);

    /**
     * The enum Transition.
     */
    public enum Transition {
        /**
         * Ok transition.
         */
        OK,
        /**
         * Cancel transition.
         */
        CANCEL,
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

            for (final mx.com.telcel.fulfilmentprocess.actions.consignment.ConfirmConsignmentPickupAction.Transition transition : mx.com.telcel.fulfilmentprocess.actions.consignment.ConfirmConsignmentPickupAction.Transition.values()) {
                res.add(transition.toString());
            }
            return res;
        }
    }

    @Override
    public String execute(final ConsignmentProcessModel process) {
        final ConsignmentModel consignment = process.getConsignment();
        if (consignment != null) {
            consignment.setStatus(ConsignmentStatus.PICKUP_COMPLETE);
            getModelService().save(consignment);
            return mx.com.telcel.fulfilmentprocess.actions.consignment.ConfirmConsignmentPickupAction.Transition.OK.toString();
        }
        LOG.error("Process has no consignment");
        return mx.com.telcel.fulfilmentprocess.actions.consignment.ConfirmConsignmentPickupAction.Transition.ERROR.toString();
    }

    @Override
    public Set<String> getTransitions() {
        return mx.com.telcel.fulfilmentprocess.actions.consignment.ConfirmConsignmentPickupAction.Transition.getStringValues();
    }
}
