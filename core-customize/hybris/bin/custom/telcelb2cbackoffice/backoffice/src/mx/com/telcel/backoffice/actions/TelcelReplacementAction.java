//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package mx.com.telcel.backoffice.actions;

import com.hybris.backoffice.widgets.notificationarea.event.NotificationEvent.Level;
import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import com.hybris.cockpitng.actions.ActionResult.StatusFlag;
import com.hybris.cockpitng.actions.CockpitAction;
import com.hybris.cockpitng.engine.impl.AbstractComponentWidgetAdapterAware;
import com.hybris.cockpitng.util.notifications.NotificationService;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.TelcelReplacementOrderModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.returns.ReturnService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;

import javax.annotation.Resource;
import java.util.Set;

public class TelcelReplacementAction extends AbstractComponentWidgetAdapterAware implements CockpitAction<ConsignmentModel, ConsignmentModel> {
    private static final String STORE_UID_TELMEX = "telmex";
    public static final String CONSULTA = "telcel.backoffice.replacement.disabled";
    @Resource
    private ReturnService returnService;
    @Resource
    private ModelService modelService;
    @Resource
    private NotificationService notificationService;
    @Resource(name = "userService")
    private UserService userService;
    @Resource(name = "configurationService")
    private ConfigurationService configurationService;

    public TelcelReplacementAction() {
    }

    public boolean canPerform(ActionContext<ConsignmentModel> actionContext) {
        Object data = actionContext.getData();
        ConsignmentModel consignmentModel = null;
        if (data instanceof ConsignmentModel) {
            consignmentModel = (ConsignmentModel) data;
        }
        if (consignmentModel != null) {
            //obtener rol
            UserModel userModel = getUserService().getCurrentUser();
            Set<PrincipalGroupModel>  groups = userModel.getGroups();

            final String grupos = configurationService.getConfiguration().getString(CONSULTA);
            final String[] arrGrupos = grupos.split(",");
            for(PrincipalGroupModel group : groups){
                for(String grupo : arrGrupos){
                    if(grupo.equalsIgnoreCase(group.getUid())){
                        return false;
                    }
                }
            }

            //regresar falsa si es de consulta
            final AbstractOrderModel orderModel = consignmentModel.getOrder();
            if (orderModel == null || orderModel instanceof TelcelReplacementOrderModel) {
                return false;
            }

            final BaseStoreModel baseStoreModel = orderModel.getStore();
            if (baseStoreModel.getUid().equalsIgnoreCase(STORE_UID_TELMEX)) {
                return false;
            }

            if (consignmentModel.getStatus() != null && consignmentModel.getWasReplacement() != null && !consignmentModel.getWasReplacement()) {
                //final ConsignmentStatus consignmentStatus = consignmentModel.getStatus();
                //if (consignmentStatus.equals(ConsignmentStatus.ORDER_DELIVERED) || consignmentStatus.equals(ConsignmentStatus.DELIVERY_COMPLETED)) {
                    return true;
                //}
            }
        }
        return false;
    }

    public String getConfirmationMessage(ActionContext<ConsignmentModel> actionContext) {
        return null;
    }

    public boolean needsConfirmation(ActionContext<ConsignmentModel> actionContext) {
        return false;
    }

    public ActionResult<ConsignmentModel> perform(ActionContext<ConsignmentModel> actionContext) {
        ConsignmentModel consignmentModel = (ConsignmentModel) actionContext.getData();
        this.getModelService().refresh(consignmentModel);
        ActionResult actionResult;
        if (this.canPerform(actionContext)) {
            this.sendOutput("createTelcelReplacementRequestContext", actionContext.getData());
            actionResult = new ActionResult("success");
        } else {
            this.notificationService.notifyUser(this.notificationService.getWidgetNotificationSource(actionContext), "JustMessage", Level.FAILURE, new Object[]{actionContext.getLabel("customersupport.create.returnrequest.modified.failure")});
            actionResult = new ActionResult("error");
        }

        actionResult.getStatusFlags().add(StatusFlag.OBJECT_PERSISTED);
        return actionResult;
    }

    protected ModelService getModelService() {
        return this.modelService;
    }

    protected ReturnService getReturnService() {
        return this.returnService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
