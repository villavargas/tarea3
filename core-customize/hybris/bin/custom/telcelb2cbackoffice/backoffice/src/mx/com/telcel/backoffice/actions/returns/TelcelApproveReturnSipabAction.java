/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.backoffice.actions.returns;

import com.hybris.backoffice.widgets.notificationarea.event.NotificationEvent;
import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import com.hybris.cockpitng.actions.ActionResult.StatusFlag;
import com.hybris.cockpitng.actions.CockpitAction;
import com.hybris.cockpitng.engine.impl.AbstractComponentWidgetAdapterAware;
import com.hybris.cockpitng.util.notifications.NotificationService;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.log4j.Logger;

import javax.annotation.Resource;

/**
 * The type Telcel approve return action.
 */
public class TelcelApproveReturnSipabAction extends AbstractComponentWidgetAdapterAware implements CockpitAction<ReturnRequestModel, ReturnRequestModel> {

    private static final Logger LOG = Logger.getLogger(TelcelApproveReturnSipabAction.class);
    @Resource
    private ModelService modelService;
    @Resource
    private NotificationService notificationService;

    @Resource(name = "businessProcessService")
    private BusinessProcessService businessProcessService;

    @Resource
    private UserService userService;

    /**
     * Instantiates a new Telcel approve return action.
     */
    public TelcelApproveReturnSipabAction() {
    }

    public boolean canPerform(ActionContext<ReturnRequestModel> actionContext) {
        boolean result = false;
        if (actionContext.getData() != null && actionContext.getData() instanceof ReturnRequestModel) {
            ReturnRequestModel returnRequest = (ReturnRequestModel) actionContext.getData();
            if (returnRequest.getAmountReview() != null && returnRequest.getAmountReview()) {
                result = true;
            }
            final ReturnProcessModel returnProcessModel = returnRequest.getReturnProcess().iterator().next();
            if (returnProcessModel.isWaitingForRefund()) {
                result = true;
            }
        }
        return result;
    }

    public String getConfirmationMessage(ActionContext<ReturnRequestModel> actionContext) {
        ReturnRequestModel returnRequest = (ReturnRequestModel) actionContext.getData();
        if (returnRequest.getAmountReview() != null && returnRequest.getAmountReview()) {
            return actionContext.getLabel("customersupportbackoffice.returnrequest.approval.amount.review.modified");
        }
        return actionContext.getLabel("customersupportbackoffice.returnrequest.approval.sipab.sipab.finish");
    }

    public boolean needsConfirmation(ActionContext<ReturnRequestModel> actionContext) {
        return true;
    }

    public ActionResult<ReturnRequestModel> perform(ActionContext<ReturnRequestModel> actionContext) {
        ReturnRequestModel returnRequest = (ReturnRequestModel) actionContext.getData();
        final ReturnProcessModel returnProcessModel = returnRequest.getReturnProcess().iterator().next();
        this.getModelService().refresh(returnRequest);
        ActionResult actionResult;
        this.notificationService.clearNotifications("");
        if((returnRequest.getAmountReview() != null && returnRequest.getAmountReview()) || returnProcessModel.isWaitingForRefund()){
            final UserModel currentUser = userService.getCurrentUser();
            returnRequest.setSipabUser(currentUser);
            returnRequest.setAmountReview(false);
            getModelService().save(returnRequest);
            returnProcessModel.setWaitingForRefund(false);
            getModelService().save(returnProcessModel);
            businessProcessService.restartProcess(returnProcessModel, "setSIPABStatusAction");
            this.notificationService.notifyUser("", "JustMessage", NotificationEvent.Level.SUCCESS, new Object[]{actionContext.getLabel("customersupportbackoffice.returnrequestsipab.approval.success")});
            actionResult = new ActionResult("success");
        }else{
            this.notificationService.notifyUser(this.notificationService.getWidgetNotificationSource(actionContext), "JustMessage", NotificationEvent.Level.FAILURE, new Object[]{actionContext.getLabel("customersupport.create.returnrequest..sipab.modified.failure")});
            actionResult = new ActionResult("error");
        }

        actionResult.getStatusFlags().add(StatusFlag.OBJECT_PERSISTED);
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
}
