/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.fulfilmentprocess.actions.returns;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.basecommerce.enums.RefundReason;
import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import org.apache.log4j.Logger;


/**
 * Mock implementation for updating he inventory for the ReturnRequest
 */
public class InventoryUpdateAction extends AbstractProceduralAction<ReturnProcessModel> {
    private static final Logger LOG = Logger.getLogger(InventoryUpdateAction.class);

    @Override
    public void executeAction(final ReturnProcessModel process) {
        LOG.info("Process: " + process.getCode() + " in step " + getClass().getSimpleName());

        // Todo: Implement the update inventory behavior that you want to apply after a successful return

        final ReturnRequestModel returnRequest = process.getReturnRequest();
        //returnRequest.setStatus(ReturnStatus.COMPLETED);
        returnRequest.getReturnEntries().stream().forEach(entry -> {
            entry.setStatus(ReturnStatus.COMPLETED);
            //Para cuando termine el rma, el id de consignment tenga su propio estatus
            final RefundEntryModel refundEntryModel = (RefundEntryModel) entry;
            ConsignmentModel consignmentModel = refundEntryModel.getConsignment();
            if (consignmentModel != null) {
                final RefundReason refundReason = refundEntryModel.getReason();
                if (RefundReason.FRAUDRISK.equals(refundReason)) {
                    consignmentModel.setStatus(ConsignmentStatus.FRAUD_RISK_CANCEL);
                } else if (RefundReason.DELIVERYFAILED.equals(refundReason)) {
                    consignmentModel.setStatus(ConsignmentStatus.DELIVERY_FAILED_CANCEL);
                } else if (RefundReason.CANCELPRODTEST.equals(refundReason)) {
                    consignmentModel.setStatus(ConsignmentStatus.CANCEL_PROD_TEST_CANCEL);
                } else if (RefundReason.CANCELINCOMPLETEORDER.equals(refundReason)) {
                    consignmentModel.setStatus(ConsignmentStatus.CANCEL_INCOMPLETE_ORDER_CANCEL);
                } else if (RefundReason.CANCELLOSS.equals(refundReason)) {
                    consignmentModel.setStatus(ConsignmentStatus.CANCEL_LOSS_CANCEL);
                } else if (RefundReason.CANCELREQUESTCLIENT.equals(refundReason)) {
                    consignmentModel.setStatus(ConsignmentStatus.CANCEL_REQUEST_CLIENT_CANCEL);
                } else if (RefundReason.RETURNREQUESTCLIENT.equals(refundReason)) {
                    consignmentModel.setStatus(ConsignmentStatus.RETURN_REQUEST_CLIENT_CANCEL);
                } else if (RefundReason.CANCELINTERNALISSUE.equals(refundReason)) {
                    consignmentModel.setStatus(ConsignmentStatus.CANCEL_INTERNAL_ISSUE_CANCEL);
                } else if (RefundReason.MANUFACTURING_DEFECTS.equals(refundReason)) {
                    consignmentModel.setStatus(ConsignmentStatus.MANUFACTURING_DEFECTS_CANCEL);
                }
                getModelService().save(consignmentModel);
            }
            getModelService().save(entry);
        });
        getModelService().save(returnRequest);
    }
}
