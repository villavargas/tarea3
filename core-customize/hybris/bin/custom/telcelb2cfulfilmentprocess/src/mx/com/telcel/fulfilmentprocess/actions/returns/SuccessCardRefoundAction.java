/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.fulfilmentprocess.actions.returns;

import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.task.RetryLaterException;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.List;

/**
 * The type Success card refound action.
 */
public class SuccessCardRefoundAction extends AbstractProceduralAction<ReturnProcessModel> {
    private static final Logger LOG = Logger.getLogger(FailedRefoundAction.class);
    private static final List<String> reasons = Arrays.asList("MANUFACTURING_DEFECTS", "CANCELREQUESTCLIENT", "RETURNREQUESTCLIENT");

    @Override
    public void executeAction(ReturnProcessModel process) throws RetryLaterException, Exception {
        LOG.info("Process: " + process.getCode() + " in step " + getClass().getSimpleName());
        final ReturnRequestModel returnRequestModel = process.getReturnRequest();
        final RefundEntryModel entryModel = (RefundEntryModel) returnRequestModel.getReturnEntries().get(0);
        final String reason = entryModel.getReason().getCode().toUpperCase();
        if (reasons.contains(reason)) {
            returnRequestModel.setStatus(ReturnStatus.RETURN_COMPLETED);
        } else {
            returnRequestModel.setStatus(ReturnStatus.CANCELED);
        }
        getModelService().save(returnRequestModel);
    }
}
