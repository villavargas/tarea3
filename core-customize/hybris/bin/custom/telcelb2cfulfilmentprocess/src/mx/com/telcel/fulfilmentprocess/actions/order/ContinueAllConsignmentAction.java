/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.fulfilmentprocess.actions.order;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.task.RetryLaterException;
import org.apache.commons.lang.BooleanUtils;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

/**
 * The type Continue all consignment action.
 */
public class ContinueAllConsignmentAction extends AbstractOrderAction<OrderProcessModel> {
    private static final Logger LOG = Logger.getLogger(ContinueAllConsignmentAction.class);
    private static final String PROCESS_MSG = "Process: ";
    public static final String WAIT_FOR_WAREHOUSE_SUB_PROCESS_END = "_waitForWarehouseSubprocessEnd";
    private BusinessProcessService businessProcessService;

   /* @Override
    public void executeAction(OrderProcessModel orderProcessModel) throws Exception {
        LOG.info(PROCESS_MSG + orderProcessModel.getCode() + " in step " + getClass());
        OrderModel orderModel = orderProcessModel.getOrder();
        orderModel.setStatus(OrderStatus.BILLED_ORDER);
        getModelService().save(orderModel);
        for (ConsignmentModel consignmentModel : orderModel.getConsignments()) {
            ConsignmentProcessModel consignmentProcessModel = consignmentModel.getConsignmentProcesses().iterator().next();
            getBusinessProcessService().triggerEvent(consignmentProcessModel.getCode() + TELCEL_WAIT_FOR_BILLED_ORDER);
        }
    }*/

    /**
     * The enum TransitionAllConsignment.
     */
    public enum TransitionAllConsignment
    {
        /**
         * Ok transition.
         */
        OK,
        /**
         * Nok transition.
         */
        NOK;

        public static Set<String> getStringValues()
        {
            final Set<String> res = new HashSet<String>();
            for (final TransitionAllConsignment transitions : TransitionAllConsignment.values())
            {
                res.add(transitions.toString());
            }
            return res;
        }
    }

    @Override
    public String execute(OrderProcessModel orderProcessModel) throws RetryLaterException, Exception {
        LOG.info(PROCESS_MSG + orderProcessModel.getCode() + " in step " + getClass());
        OrderModel orderModel = orderProcessModel.getOrder();

       //Recorremos todos los consigments
        boolean someTelcelFacturaISNOTNULL= orderModel.getConsignments().stream().allMatch(data -> data.getTelcelFactura()!=null);

        if(BooleanUtils.isTrue(someTelcelFacturaISNOTNULL)){
            orderModel.setStatus(OrderStatus.BILLED_ORDER);
            getModelService().save(orderModel);
          return TransitionAllConsignment.OK.toString();
        }

        return TransitionAllConsignment.NOK.toString();
    }

    @Override
    public Set<String> getTransitions() {
        return TransitionAllConsignment.getStringValues();
    }

    /**
     * Gets business process service.
     *
     * @return the business process service
     */
    public BusinessProcessService getBusinessProcessService() {
        return businessProcessService;
    }

    /**
     * Sets business process service.
     *
     * @param businessProcessService the business process service
     */
    public void setBusinessProcessService(BusinessProcessService businessProcessService) {
        this.businessProcessService = businessProcessService;
    }
}
