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
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.TelcelReplacementOrderModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.returns.ReturnService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.commons.collections4.CollectionUtils;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.Set;

public class CreateTelcelCancellationsReturnsRequestAction extends AbstractComponentWidgetAdapterAware implements CockpitAction<OrderModel, OrderModel> {
    protected static final String SOCKET_OUT_CONTEXT = "createTelcelCancellationsReturnsRequestContext";
    protected static final String CREATE_RETURN_REQUEST_MODIFIED_FAILURE = "customersupport.create.returnrequest.modified.failure";
    public static final String GROUPS_DISABLED = "telcel.backoffice.return.disabled";

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


    public CreateTelcelCancellationsReturnsRequestAction() {
    }

    public boolean canPerform(ActionContext<OrderModel> actionContext) {
        Object data = actionContext.getData();
        if(Objects.nonNull(data) && data instanceof OrderModel){
            OrderModel order = (OrderModel) data;

            //obtener rol
            UserModel userModel = getUserService().getCurrentUser();
            Set<PrincipalGroupModel> groups = userModel.getGroups();
            final String grupos = configurationService.getConfiguration().getString(GROUPS_DISABLED);
            final String[] arrGrupos = grupos.split(",");
            for(PrincipalGroupModel group : groups){
                for(String grupo : arrGrupos){
                    if(grupo.equalsIgnoreCase(group.getUid())){
                        return false;
                    }
                }
            }



            if (this.containsConsignments(order) && !this.getReturnService().getAllReturnableEntries(order).isEmpty()) {
                final OrderStatus orderStatus = order.getStatus();
                if (orderStatus != null && (orderStatus.equals(OrderStatus.FRAUD_CHECKED) || orderStatus.equals(OrderStatus.FRAUD_RISK) ||
                        orderStatus.equals(OrderStatus.DELIVERY_FAILED) ||
                        orderStatus.equals(OrderStatus.MANUFACTURING_DEFECTS) ||
                        orderStatus.equals(OrderStatus.CANCELLATION_PRODUCTIVE_TESTS) ||
                        orderStatus.equals(OrderStatus.DAMAGED) ||
                        orderStatus.equals(OrderStatus.CANCELLATION_REQUEST_CLIENT) ||
                        orderStatus.equals(OrderStatus.RETURNED_CLIENT) ||
                        orderStatus.equals(OrderStatus.CANCELLATION_INTERNAL_ISSUE) ||
                        orderStatus.equals(OrderStatus.REPLACEMENT))) {
                    return true;
                }

                return !order.getConsignments().stream().noneMatch((consignment) -> {
                    return ConsignmentStatus.PICKUP_COMPLETE.equals(consignment.getStatus()) ||
                            ConsignmentStatus.ORDER_DELIVERED.equals(consignment.getStatus()) ;
                });
            }
        }

        return false;
    }

    private boolean containsConsignments(OrderModel order) {
        return order != null && !CollectionUtils.isEmpty(order.getEntries()) && !CollectionUtils.isEmpty(order.getConsignments());
    }

    public String getConfirmationMessage(ActionContext<OrderModel> actionContext) {
        return null;
    }

    public boolean needsConfirmation(ActionContext<OrderModel> actionContext) {
        return false;
    }

    public ActionResult<OrderModel> perform(ActionContext<OrderModel> actionContext) {
        OrderModel order = (OrderModel) actionContext.getData();
        this.getModelService().refresh(order);
        ActionResult actionResult;
        if (this.canPerform(actionContext)) {
            this.sendOutput(SOCKET_OUT_CONTEXT, actionContext.getData());
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
