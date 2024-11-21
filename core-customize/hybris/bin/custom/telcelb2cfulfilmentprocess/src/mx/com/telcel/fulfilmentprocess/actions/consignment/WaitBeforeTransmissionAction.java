/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.fulfilmentprocess.actions.consignment;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import org.apache.log4j.Logger;

import java.util.Date;


/**
 * The type Wait before transmission action.
 */
public class WaitBeforeTransmissionAction extends AbstractSimpleDecisionAction<ConsignmentProcessModel> {
	private static final Logger LOG = Logger.getLogger(WaitBeforeTransmissionAction.class);
    private static final String PROCESS_MSG = "Process: ";
	private static final int PLACES = 2;

	@Override
    public Transition executeAction(final ConsignmentProcessModel process) {
        LOG.info(PROCESS_MSG + process.getCode() + " in step " + getClass());
        ConsignmentModel consignmentModel = process.getConsignment();

        setInsuranceCost(consignmentModel);
        consignmentModel.setLastStatusDate(new Date());
        //consignmentModel.setStatus(ConsignmentStatus.PRE_ACTIVE_LINE);
        consignmentModel.setStatus(ConsignmentStatus.CONSIGNMENT_BILLED);
        getModelService().save(consignmentModel);
        // If you return NOK this action will be called again.
        // You might want to do this when you want to poll a resource to be ready.
        return Transition.OK;
    }

    private void setInsuranceCost(ConsignmentModel consignmentModel) {
        Double insuranceCost = 0D;
        for (ConsignmentEntryModel consignmentEntryModel : consignmentModel.getConsignmentEntries()) {
            Double totalQty = consignmentEntryModel.getQuantity().doubleValue();
            Double basePrice = consignmentEntryModel.getOrderEntry().getBasePrice();
            insuranceCost += totalQty * basePrice;
        }
        consignmentModel.setInsuranceCost(round(insuranceCost, PLACES));
    }

	/**
	 * Round double.
	 *
	 * @param value  the value
	 * @param places the places
	 * @return the double
	 */
	public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
