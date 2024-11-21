/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.fulfilmentprocess.actions.returns;

import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.task.RetryLaterException;
import org.apache.log4j.Logger;

/**
 * The type Failed refound action.
 */
public class FailedRefoundAction extends AbstractProceduralAction<ReturnProcessModel> {
    private static final Logger LOG = Logger.getLogger(FailedRefoundAction.class);

    @Override
    public void executeAction(ReturnProcessModel process) throws RetryLaterException, Exception {
        LOG.info("Process: " + process.getCode() + " in step " + getClass().getSimpleName());
        final ReturnRequestModel returnRequestModel = process.getReturnRequest();
        returnRequestModel.setStatus(ReturnStatus.FAILED_REFUND);
        getModelService().save(returnRequestModel);
    }
}
