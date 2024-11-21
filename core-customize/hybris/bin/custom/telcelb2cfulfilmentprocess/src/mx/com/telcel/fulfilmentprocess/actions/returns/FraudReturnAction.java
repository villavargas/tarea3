/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.fulfilmentprocess.actions.returns;

import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import org.apache.log4j.Logger;

/**
 * The type Fraud return action.
 */
public class FraudReturnAction extends AbstractProceduralAction<ReturnProcessModel> {
    private static final Logger LOG = Logger.getLogger(FraudReturnAction.class);

    @Override
    public void executeAction(ReturnProcessModel process) throws Exception {
        LOG.info("Process: " + process.getCode() + " in step " + getClass().getSimpleName());
        final ReturnRequestModel returnRequest = process.getReturnRequest();
        returnRequest.setStatus(ReturnStatus.REJECTED);
        getModelService().save(returnRequest);
    }
}
