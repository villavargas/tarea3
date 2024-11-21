/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.fulfilmentprocess.actions.consignment.replacement;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;

/**
 * The type Cancel consignment action.
 */
public class CancelConsignmentAction extends AbstractProceduralAction<ConsignmentProcessModel> {
    @Override
    public void executeAction(final ConsignmentProcessModel process) {
        final ConsignmentModel consignment = process.getConsignment();
        if (consignment != null) {
            consignment.setStatus(ConsignmentStatus.CANCELLED);
            getModelService().save(consignment);
        }
    }
}
