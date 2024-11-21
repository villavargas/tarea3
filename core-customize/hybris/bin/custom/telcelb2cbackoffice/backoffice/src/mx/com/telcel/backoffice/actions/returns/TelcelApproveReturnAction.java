/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.backoffice.actions.returns;

import com.hybris.backoffice.widgets.notificationarea.event.NotificationEvent;
import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import com.hybris.cockpitng.actions.CockpitAction;
import com.hybris.cockpitng.engine.impl.AbstractComponentWidgetAdapterAware;
import com.hybris.cockpitng.util.notifications.NotificationService;
import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.returns.OrderReturnException;
import de.hybris.platform.returns.ReturnActionResponse;
import de.hybris.platform.returns.ReturnCallbackService;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * The type Telcel approve return action.
 */
public class TelcelApproveReturnAction extends AbstractComponentWidgetAdapterAware implements CockpitAction<ReturnRequestModel, ReturnRequestModel> {
    /**
     * The constant APPROVAL_SUCCESS.
     */
    protected static final String APPROVAL_SUCCESS = "customersupportbackoffice.returnrequest.approval.success";
    /**
     * The constant APPROVAL_FAILURE.
     */
    protected static final String APPROVAL_FAILURE = "customersupportbackoffice.returnrequest.approval.failure";
    /**
     * The constant MODIFIED_RETURN_REQUEST.
     */
    protected static final String MODIFIED_RETURN_REQUEST = "customersupportbackoffice.returnrequest.approval.modified";
    /**
     * The constant APPROVAL_CANCELLED_FAILURE.
     */
    protected static final String APPROVAL_CANCELLED_FAILURE = "customersupportbackoffice.returnrequest.approval.cancelled.failure";
    private static final Logger LOG = Logger.getLogger(TelcelApproveReturnAction.class);
    @Resource
    private ReturnCallbackService returnCallbackService;
    @Resource
    private ModelService modelService;
    @Resource
    private NotificationService notificationService;

    /**
     * Instantiates a new Telcel approve return action.
     */
    public TelcelApproveReturnAction() {
    }

    public boolean canPerform(ActionContext<ReturnRequestModel> actionContext) {
        boolean result = false;
        ReturnRequestModel returnRequest = (ReturnRequestModel) actionContext.getData();
        if (returnRequest != null) {
            result = !CollectionUtils.isEmpty(returnRequest.getReturnEntries()) && returnRequest.getStatus().equals(ReturnStatus.APPROVAL_PENDING);

            if (returnRequest.getAmountReview() != null && returnRequest.getAmountReview() && returnRequest.getSipabAmount() != null && returnRequest.getConsignmentsAmount() != null) {
                final BigDecimal sipabAmount = returnRequest.getSipabAmount();
                final BigDecimal consignmentsAmount = returnRequest.getConsignmentsAmount();
                int compareTo = consignmentsAmount.compareTo(sipabAmount);
                LOG.info(String.format("ValidateAmount - Comparing amounts requests: %s, SIPAB-importe: %s, result: %d", consignmentsAmount, sipabAmount, compareTo));
                if (compareTo != 0) {
                    this.notificationService.clearNotifications("");
                    this.notificationService.notifyUser("", "JustMessage", NotificationEvent.Level.WARNING, new Object[]{actionContext.getLabel("customersupportbackoffice.returnrequest.telcelapproval.warn") + ". RMA: " + returnRequest.getRMA()});
                }
            }
        }


        return result;
    }

    public String getConfirmationMessage(ActionContext<ReturnRequestModel> actionContext) {
        return actionContext.getLabel("customersupportbackoffice.returnrequest.approval.modified");
    }

    public boolean needsConfirmation(ActionContext<ReturnRequestModel> actionContext) {
        ReturnRequestModel returnRequest = (ReturnRequestModel) actionContext.getData();
        ReturnRequestModel upToDateReturnRequest = (ReturnRequestModel) this.getModelService().get(returnRequest.getPk());
        boolean result = false;
        if (returnRequest.getStatus().equals(upToDateReturnRequest.getStatus())) {
            result = !this.getModelService().isUpToDate(actionContext.getData()) || ((ReturnRequestModel) actionContext.getData()).getReturnEntries().stream().anyMatch((entry) -> {
                return !this.getModelService().isUpToDate(entry);
            });
        }

        return result;
    }

    public ActionResult<ReturnRequestModel> perform(ActionContext<ReturnRequestModel> actionContext) {
        ReturnRequestModel returnRequest = (ReturnRequestModel) actionContext.getData();
        ReturnRequestModel upToDateReturnRequest = (ReturnRequestModel) this.getModelService().get(returnRequest.getPk());
        ActionResult actionResult;
        if (returnRequest.getStatus().equals(upToDateReturnRequest.getStatus())) {
            returnRequest.getReturnEntries().forEach((entry) -> {
                this.getModelService().save(entry);
            });
            this.getModelService().save(actionContext.getData());
            ReturnActionResponse returnActionResponse = new ReturnActionResponse(returnRequest);

            try {
                this.getReturnCallbackService().onReturnApprovalResponse(returnActionResponse);
                this.notificationService.notifyUser("", "JustMessage", NotificationEvent.Level.SUCCESS, new Object[]{actionContext.getLabel("customersupportbackoffice.returnrequest.approval.success")});
                actionResult = new ActionResult("success");
            } catch (OrderReturnException var7) {
                LOG.error(var7.getMessage());
                this.notificationService.notifyUser("", "JustMessage", NotificationEvent.Level.FAILURE, new Object[]{actionContext.getLabel("customersupportbackoffice.returnrequest.approval.failure")});
                actionResult = new ActionResult("error");
            }
        } else {
            this.notificationService.notifyUser("", "JustMessage", NotificationEvent.Level.FAILURE, new Object[]{actionContext.getLabel("customersupportbackoffice.returnrequest.approval.cancelled.failure")});
            actionResult = new ActionResult("error");
        }

        actionResult.getStatusFlags().add(ActionResult.StatusFlag.OBJECT_PERSISTED);
        return actionResult;
    }

    /**
     * Gets model service.
     *
     * @return the model service
     */
    protected ModelService getModelService() {
        return this.modelService;
    }

    /**
     * Gets return callback service.
     *
     * @return the return callback service
     */
    protected ReturnCallbackService getReturnCallbackService() {
        return this.returnCallbackService;
    }
}
