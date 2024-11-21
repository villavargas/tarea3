/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.fulfilmentprocess.actions.returns;

import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.processengine.action.AbstractAction;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Set;


/**
 * Mock implementation for refunding the money to the customer for the ReturnRequest
 */
public class CheckReturnAction extends AbstractAction<ReturnProcessModel>
{
    private static final Logger LOG = Logger.getLogger(CheckReturnAction.class);

    protected enum Transition
    {
        CANCEL, APPROVE;

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
        if(returnRequest.getStatus().equals(ReturnStatus.APPROVING)){

            return Transition.APPROVE.toString();
        }

        return Transition.CANCEL.toString();
    }

    @Override
    public Set<String> getTransitions()
    {
        return Transition.getStringValues();
    }
}
