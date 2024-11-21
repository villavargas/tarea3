/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.fulfilmentprocess.actions.consignment.telmex;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;

/**
 * The type Cancel telmex consignment action.
 */
public class CancelTelmexConsignmentAction extends AbstractProceduralAction<ConsignmentProcessModel> {
    @Override
    public void executeAction(final ConsignmentProcessModel process) {
        final ConsignmentModel consignment = process.getConsignment();
        if (consignment != null) {
            consignment.setStatus(ConsignmentStatus.CANCELLED);
            getModelService().save(consignment);
        }
    }
}
