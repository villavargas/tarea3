/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.fulfilmentprocess.actions.returns;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.task.RetryLaterException;
import mx.com.telcel.core.helper.TelcelEmailsHelper;
import org.apache.log4j.Logger;

import javax.annotation.Resource;


/**
 * The type Send cancellation telcel emails action.
 */
public class SendCancellationTelcelEmailsAction extends AbstractProceduralAction<ReturnProcessModel> {
    private static final Logger LOG = Logger.getLogger(ReturnTypeAction.class);

    @Resource(name = "telcelEmailsHelper")
    private TelcelEmailsHelper telcelEmailsHelper;

    @Override
    public void executeAction(ReturnProcessModel process) throws RetryLaterException, Exception {
        LOG.info("Process: " + process.getCode() + " in step " + getClass().getSimpleName());
        final ReturnRequestModel returnRequestModel = process.getReturnRequest();
        final RefundEntryModel entryModel = (RefundEntryModel) returnRequestModel.getReturnEntries().get(0);
        final String reason = entryModel.getReason().getCode().toUpperCase();
        final OrderModel orderModel = returnRequestModel.getOrder();
        final PaymentInfoModel infoModel = orderModel.getPaymentInfo();
        switch (reason) {
            case "FRAUDRISK":
            case "CANCELPRODTEST":
            case "CANCELINCOMPLETEORDER":
            case "CANCELLOSS":
            case "CANCELINTERNALISSUE":
                telcelEmailsHelper.sendCancellationTelcelEmailProcessEmail(entryModel.getConsignment(), returnRequestModel);
                break;
            case "DELIVERYFAILED":
                if (infoModel instanceof CreditCardPaymentInfoModel) {
                    telcelEmailsHelper.sendCancellationCardItemProcessEmail(entryModel.getConsignment(), returnRequestModel);
                } else {
                    telcelEmailsHelper.sendCancellationOthersItemProcessEmail(entryModel.getConsignment(), returnRequestModel);
                }
                break;
            case "MANUFACTURING_DEFECTS":
            case "CANCELREQUESTCLIENT":
            case "RETURNREQUESTCLIENT":
                if (infoModel instanceof CreditCardPaymentInfoModel) {
                    telcelEmailsHelper.sendCancellationUserCardProcessEmail(entryModel.getConsignment(), returnRequestModel);
                } else {
                    telcelEmailsHelper.sendCancellationUserOthersProcessEmail(entryModel.getConsignment(), returnRequestModel);
                }
                break;
        }
    }
}
