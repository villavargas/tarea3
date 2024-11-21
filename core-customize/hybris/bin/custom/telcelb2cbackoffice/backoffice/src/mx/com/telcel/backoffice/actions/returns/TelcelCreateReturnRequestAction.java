//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package mx.com.telcel.backoffice.actions.returns;

import com.hybris.backoffice.widgets.notificationarea.event.NotificationEvent.Level;
import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import com.hybris.cockpitng.actions.CockpitAction;
import com.hybris.cockpitng.actions.ActionResult.StatusFlag;
import com.hybris.cockpitng.engine.impl.AbstractComponentWidgetAdapterAware;
import com.hybris.cockpitng.util.notifications.NotificationService;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.TelcelReplacementOrderModel;
import de.hybris.platform.returns.ReturnService;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;

public class TelcelCreateReturnRequestAction extends AbstractComponentWidgetAdapterAware implements CockpitAction<OrderModel, OrderModel> {
    protected static final String SOCKET_OUT_CONTEXT = "createReturnRequestContext";
    protected static final String CREATE_RETURN_REQUEST_MODIFIED_FAILURE = "customersupport.create.returnrequest.modified.failure";
    @Resource
    private ReturnService returnService;
    @Resource
    private ModelService modelService;
    @Resource
    private NotificationService notificationService;

    public TelcelCreateReturnRequestAction() {
    }

    public boolean canPerform(ActionContext<OrderModel> actionContext) {
        Object data = actionContext.getData();
        OrderModel order = null;
        if (data instanceof OrderModel) {
            order = (OrderModel) data;
        }

        if(order != null){
            if(order instanceof TelcelReplacementOrderModel){
                return false;
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
            this.sendOutput("createReturnRequestContext", actionContext.getData());
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
}
