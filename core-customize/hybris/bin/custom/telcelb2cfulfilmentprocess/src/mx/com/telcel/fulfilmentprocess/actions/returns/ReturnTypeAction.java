/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.fulfilmentprocess.actions.returns;

import de.hybris.platform.processengine.action.AbstractAction;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.task.RetryLaterException;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

/**
 * The type Return type action.
 */
public class ReturnTypeAction extends AbstractAction<ReturnProcessModel> {
    private static final Logger LOG = Logger.getLogger(ReturnTypeAction.class);

    @Override
    public String execute(ReturnProcessModel process) throws RetryLaterException, Exception {
        LOG.info("Process: " + process.getCode() + " in step " + getClass().getSimpleName());
        final ReturnRequestModel returnRequestModel = process.getReturnRequest();
        final RefundEntryModel entryModel = (RefundEntryModel) returnRequestModel.getReturnEntries().get(0);
        final String reason = entryModel.getReason().getCode().toUpperCase();
        switch (reason) {
            case "BANKSTATEMENT":
                return Transition.BANKSTATEMENT.toString();
            case "FRAUDRISK":
                return Transition.FRAUDRISK.toString();
            case "DELIVERYFAILED":
                return Transition.DELIVERYFAILED.toString();
            case "MANUFACTURING_DEFECTS":
                return Transition.MANUFACTURING_DEFECTS.toString();
            case "CANCELPRODTEST":
                return Transition.CANCELPRODTEST.toString();
            case "CANCELINCOMPLETEORDER":
            case "CANCELLOSS":
                return Transition.DAMAGED.toString();
            case "CANCELREQUESTCLIENT":
                return Transition.CANCELREQUESTCLIENT.toString();
            case "RETURNREQUESTCLIENT":
                return Transition.RETURNREQUESTCLIENT.toString();
            case "CANCELINTERNALISSUE":
                return Transition.CANCELINTERNALISSUE.toString();
        }
        return Transition.ERROR.toString();
    }

    @Override
    public Set<String> getTransitions() {
        return Transition.getStringValues();
    }

    /**
     * The enum Transition.
     */
    protected enum Transition {
        BANKSTATEMENT,
        FRAUDRISK,
        DELIVERYFAILED,
        MANUFACTURING_DEFECTS,
        CANCELPRODTEST,
        DAMAGED,
        CANCELREQUESTCLIENT,
        RETURNREQUESTCLIENT,
        CANCELINTERNALISSUE,
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
}
