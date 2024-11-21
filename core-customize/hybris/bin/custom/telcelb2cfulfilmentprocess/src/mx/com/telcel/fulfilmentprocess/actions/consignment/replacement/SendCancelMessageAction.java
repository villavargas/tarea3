/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.fulfilmentprocess.actions.consignment.replacement;

import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;

/**
 * The type Send cancel message action.
 */
public class SendCancelMessageAction extends AbstractProceduralAction<ConsignmentProcessModel> {
    /*
     * (non-Javadoc)
     *
     * @see de.hybris.processengine.action.AbstractProceduralAction#executeAction(de.hybris.platform.processengine.model.
     * BusinessProcessModel)
     */
    @Override
    public void executeAction(final ConsignmentProcessModel process) {
        // empty
    }
}
