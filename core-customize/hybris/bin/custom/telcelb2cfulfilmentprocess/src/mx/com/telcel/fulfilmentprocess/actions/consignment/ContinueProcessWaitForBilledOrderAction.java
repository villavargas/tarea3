package mx.com.telcel.fulfilmentprocess.actions.consignment;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.task.RetryLaterException;
import org.apache.log4j.Logger;

public class ContinueProcessWaitForBilledOrderAction extends AbstractProceduralAction<ConsignmentProcessModel> {
    private static final Logger LOG = Logger.getLogger(ContinueProcessWaitForBilledOrderAction.class);
    private static final String PROCESS_MSG = "Process: ";
    private static final String WAIT_FOR_BILLED_ORDER = "_WaitForBilledOrder";

    private BusinessProcessService businessProcessService;

    @Override
    public void executeAction(ConsignmentProcessModel consignmentProcessModel) throws RetryLaterException, Exception {
        LOG.info(PROCESS_MSG + consignmentProcessModel.getCode() + " in step " + getClass());


        getBusinessProcessService().triggerEvent( consignmentProcessModel.getParentProcess().getOrder().getOrderProcess().iterator().next().getCode()+ WAIT_FOR_BILLED_ORDER);
    }

    public BusinessProcessService getBusinessProcessService() {
        return businessProcessService;
    }

    public void setBusinessProcessService(BusinessProcessService businessProcessService) {
        this.businessProcessService = businessProcessService;
    }
}
