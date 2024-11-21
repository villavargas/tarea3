/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.fulfilmentprocess.actions.consignment.replacement;

import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import mx.com.telcel.fulfilmentprocess.constants.Telcelb2cFulfilmentProcessConstants;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * The type Subprocess end action.
 */
public class SubprocessEndAction extends AbstractProceduralAction<ConsignmentProcessModel> {
    private static final Logger LOG = Logger.getLogger(SubprocessEndAction.class);

    private static final String PROCESS_MSG = "Process: ";

    private BusinessProcessService businessProcessService;

    /**
     * Gets business process service.
     *
     * @return the business process service
     */
    protected BusinessProcessService getBusinessProcessService() {
        return businessProcessService;
    }

    /**
     * Sets business process service.
     *
     * @param businessProcessService the business process service
     */
    @Required
    public void setBusinessProcessService(final BusinessProcessService businessProcessService) {
        this.businessProcessService = businessProcessService;
    }

    @Override
    public void executeAction(final ConsignmentProcessModel process) {
        LOG.info(PROCESS_MSG + process.getCode() + " in step " + getClass());

        try {
            // simulate different ending times
            Thread.sleep((long) (Math.random() * 2000));
        } catch (final InterruptedException e) {
            LOG.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }

        process.setDone(true);

        save(process);
        LOG.info(PROCESS_MSG + process.getCode() + " wrote DONE marker");

        getBusinessProcessService().triggerEvent(
                process.getParentProcess().getCode() + "_"
                        + Telcelb2cFulfilmentProcessConstants.REPLACEMENT_CONSIGNMENT_SUBPROCESS_END_EVENT_NAME);
        LOG.info(PROCESS_MSG + process.getCode() + " fired event "
                + Telcelb2cFulfilmentProcessConstants.REPLACEMENT_CONSIGNMENT_SUBPROCESS_END_EVENT_NAME);
    }
}
