/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.fulfilmentprocess.actions.order;

import de.hybris.platform.core.enums.ExportStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.task.RetryLaterException;
import mx.com.telcel.core.services.TelcelOrderService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * The type Sent to erp action.
 */
public class SentToERPAction extends AbstractSimpleDecisionAction<OrderProcessModel> {
    private static final Logger LOG = Logger.getLogger(SentToERPAction.class);

    private TelcelOrderService telcelOrderService;

    @Override
    public Transition executeAction(OrderProcessModel process) throws RetryLaterException, Exception {
        ServicesUtil.validateParameterNotNull(process, "Order Process Model cannot be null");
        //final OrderModel orderModel = process.getOrder();
        //ServicesUtil.validateParameterNotNull(orderModel, "Order cannot be null");
        if (LOG.isInfoEnabled()) {
            LOG.info("Process: " + process.getCode() + " in step " + getClass());
        }
        /*telcelOrderService.sendOrderToERP(orderModel);
        boolean sendOk = true;
        for (ConsignmentModel consignmentModel : orderModel.getConsignments()) {
            if (!consignmentModel.getWasSent()) {
                sendOk = false;
                break;
            }
        }
        if (sendOk) {
            orderModel.setExportStatus(ExportStatus.EXPORTED);
            getModelService().save(orderModel);
            setIdsToEntriesToConsignments(orderModel);
            return Transition.OK;
        }
        orderModel.setExportStatus(ExportStatus.NOTEXPORTED);
        getModelService().save(orderModel);
        return Transition.NOK;*/

        return Transition.OK;
    }

    private void setIdsToEntriesToConsignments(OrderModel orderModel) {
        for (ConsignmentModel consignmentModel : orderModel.getConsignments()) {
            consignmentModel.setJmsid(StringUtils.leftPad(consignmentModel.getCode().toUpperCase(), 24, '0'));
            getModelService().save(consignmentModel);
        }
    }

    /**
     * Gets telcel order service.
     *
     * @return the telcel order service
     */
    public TelcelOrderService getTelcelOrderService() {
        return telcelOrderService;
    }

    /**
     * Sets telcel order service.
     *
     * @param telcelOrderService the telcel order service
     */
    public void setTelcelOrderService(TelcelOrderService telcelOrderService) {
        this.telcelOrderService = telcelOrderService;
    }
}
