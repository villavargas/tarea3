/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.fulfilmentprocess.actions.consignment.replacement;

import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.action.AbstractAction;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

/**
 * The type Telcel guide express delivered action.
 */
public class TelcelGuideExpressDeliveredAction extends AbstractAction<ConsignmentProcessModel> {
    private static final Logger LOG = Logger.getLogger(TelcelGuideExpressDeliveredAction.class);

    /**
     * The enum Transition.
     */
    public enum Transition {
        /**
         * Ok transition.
         */
        OK;

        /**
         * Gets string values.
         *
         * @return the string values
         */
        public static Set<String> getStringValues() {
            final Set<String> res = new HashSet<String>();

            for (final mx.com.telcel.fulfilmentprocess.actions.consignment.TelcelGuideExpressDeliveredAction.Transition transition : mx.com.telcel.fulfilmentprocess.actions.consignment.TelcelGuideExpressDeliveredAction.Transition.values()) {
                res.add(transition.toString());
            }
            return res;
        }
    }

    @Override
    public String execute(ConsignmentProcessModel consignmentProcessModel) {
        LOG.info("Process: " + consignmentProcessModel.getCode() + " in step OK - " + getClass());
        consignmentProcessModel.setWaitingForGuideExpressDelivered(Boolean.FALSE);
        getModelService().save(consignmentProcessModel);
        return mx.com.telcel.fulfilmentprocess.actions.consignment.TelcelGuideExpressDeliveredAction.Transition.OK.toString();
    }

    @Override
    public Set<String> getTransitions() {
        return mx.com.telcel.fulfilmentprocess.actions.consignment.TelcelGuideExpressDeliveredAction.Transition.getStringValues();
    }
}
