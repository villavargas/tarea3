/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.fulfilmentprocess.actions.returns;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.task.RetryLaterException;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.List;

/**
 * The type Set sipab status action.
 */
public class SetSIPABStatusAction extends AbstractProceduralAction<ReturnProcessModel> {

    private static final Logger LOG = Logger.getLogger(SetSIPABStatusAction.class);

    private static final List<String> reasons = Arrays.asList("MANUFACTURING_DEFECTS", "CANCELREQUESTCLIENT");

    @Override
    public void executeAction(ReturnProcessModel process) throws RetryLaterException, Exception {
        LOG.info("Process: " + process.getCode() + " in step " + getClass().getSimpleName());
        final ReturnRequestModel returnRequestModel = process.getReturnRequest();
        final RefundEntryModel entryModel = (RefundEntryModel) returnRequestModel.getReturnEntries().get(0);

        final String reason = entryModel.getReason().getCode().toUpperCase();
        if (reasons.contains(reason)) {
            returnRequestModel.setStatus(ReturnStatus.RETURN_COMPLETED_SIPAB);
        } else {
            returnRequestModel.setStatus(ReturnStatus.CANCELLED_SIPAB);
        }
        getModelService().save(returnRequestModel);
    }
}
