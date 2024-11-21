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
 * The type Telcel continue process for guide type action.
 */
public class TelcelContinueProcessForGuideTypeAction extends AbstractAction<ConsignmentProcessModel> {
    private static final Logger LOG = Logger.getLogger(TelcelContinueProcessForGuideTypeAction.class);

    /**
     * The enum Transition.
     */
    public enum Transition {
        /**
         * Ok transition.
         */
        FEDEX,
        /**
         * Express transition.
         */
        EXPRESS;

        /**
         * Gets string values.
         *
         * @return the string values
         */
        public static Set<String> getStringValues() {
            final Set<String> res = new HashSet<String>();

            for (final mx.com.telcel.fulfilmentprocess.actions.consignment.TelcelContinueProcessForGuideTypeAction.Transition transition : mx.com.telcel.fulfilmentprocess.actions.consignment.TelcelContinueProcessForGuideTypeAction.Transition.values()) {
                res.add(transition.toString());
            }
            return res;
        }
    }

    @Override
    public String execute(ConsignmentProcessModel consignmentProcessModel) {
        consignmentProcessModel.setWaitingForGuideType(Boolean.FALSE);
        if (consignmentProcessModel.getConsignment().getEntregaExpress()) {
            LOG.info("Process: " + consignmentProcessModel.getCode() + " in step EXPRESS - " + getClass());
            consignmentProcessModel.setWaitingForGuideExpressDelivered(Boolean.TRUE);
            getModelService().save(consignmentProcessModel);
            return mx.com.telcel.fulfilmentprocess.actions.consignment.TelcelContinueProcessForGuideTypeAction.Transition.EXPRESS.toString();
        } else {
            LOG.info("Process: " + consignmentProcessModel.getCode() + " in step FEDEX - " + getClass());
            return mx.com.telcel.fulfilmentprocess.actions.consignment.TelcelContinueProcessForGuideTypeAction.Transition.FEDEX.toString();
        }
    }

    @Override
    public Set<String> getTransitions() {
        return mx.com.telcel.fulfilmentprocess.actions.consignment.TelcelContinueProcessForGuideTypeAction.Transition.getStringValues();
    }
}
