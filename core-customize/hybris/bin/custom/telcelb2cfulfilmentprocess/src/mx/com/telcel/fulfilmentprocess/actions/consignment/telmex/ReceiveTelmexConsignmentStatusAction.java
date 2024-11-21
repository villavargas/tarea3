/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.fulfilmentprocess.actions.consignment.telmex;

import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.action.AbstractAction;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

/**
 * The type Receive telmex consignment status action.
 */
public class ReceiveTelmexConsignmentStatusAction extends AbstractAction<ConsignmentProcessModel> {
    private static final Logger LOG = Logger.getLogger(ReceiveTelmexConsignmentStatusAction.class);

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

            for (final Transition transition : Transition.values()) {
                res.add(transition.toString());
            }
            return res;
        }
    }

    @Override
    public String execute(final ConsignmentProcessModel process) {
        Transition result = null;
        if (process.getWarehouseConsignmentState() == null) {
            LOG.error("Process has no warehouse consignment state");
            result = Transition.ERROR;
        } else {
            switch (process.getWarehouseConsignmentState()) {
                case CANCEL:
                    result = Transition.CANCEL;
                    break;
                case COMPLETE:
                    result = Transition.OK;
                    break;
                default:
                    LOG.error("Unexpected warehouse consignment state: " + process.getWarehouseConsignmentState());
                    result = Transition.ERROR;
            }
        }
        process.setWaitingForConsignment(false);
        getModelService().save(process);
        return result.toString();
    }

    @Override
    public Set<String> getTransitions() {
        return Transition.getStringValues();
    }
}
