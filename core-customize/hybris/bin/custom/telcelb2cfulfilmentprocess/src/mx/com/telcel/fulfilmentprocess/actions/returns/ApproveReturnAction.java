/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.fulfilmentprocess.actions.returns;

import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import mx.com.telcel.services.FedexService;
import org.apache.log4j.Logger;

import javax.annotation.Resource;
import java.util.Set;


/**
 * Mock implementation for approving the ReturnRequest
 */
public class ApproveReturnAction extends AbstractProceduralAction<ReturnProcessModel> {
    private static final Logger LOG = Logger.getLogger(ApproveReturnAction.class);

    @Resource(name = "fedexService")
    private FedexService fedexService;

    @Override
    public void executeAction(final ReturnProcessModel process) {
        LOG.info("Process: " + process.getCode() + " in step " + getClass().getSimpleName());
        final ReturnRequestModel returnRequest = process.getReturnRequest();
        returnRequest.getReturnEntries().stream().forEach(entry -> {
            final RefundEntryModel refundEntryModel = (RefundEntryModel) entry;
            ConsignmentModel consignmentModel = refundEntryModel.getConsignment();
            if (consignmentModel != null) {
                fedexService.removeGuideFromConsignment(consignmentModel);
            }
        });
    }

    @Override
    public Set<String> getTransitions() {
        return Transition.getStringValues();
    }

}
