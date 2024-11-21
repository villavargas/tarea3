package mx.com.telcel.fulfilmentprocess.actions.consignment;

import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.action.AbstractAction;
import mx.com.telcel.fulfilmentprocess.actions.consignment.telmex.WaitingToTelmexBeShippedAction;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

public class TelcelGuideExpressDeliveredAction extends AbstractAction<ConsignmentProcessModel> {
    private static final Logger LOG = Logger.getLogger(TelcelGuideExpressDeliveredAction.class);

    /**
     * The enum Transition.
     */
    public enum Transition {
        /**
         * Ok transition.
         */
        OK;

        /**
         * Gets string values.
         *
         * @return the string values
         */
        public static Set<String> getStringValues() {
            final Set<String> res = new HashSet<String>();

            for (final TelcelGuideExpressDeliveredAction.Transition transition : TelcelGuideExpressDeliveredAction.Transition.values()) {
                res.add(transition.toString());
            }
            return res;
        }
    }

    @Override
    public String execute(ConsignmentProcessModel consignmentProcessModel){
        LOG.info("Process: " + consignmentProcessModel.getCode() + " in step OK - " + getClass());
        consignmentProcessModel.setWaitingForGuideExpressDelivered(Boolean.FALSE);
        getModelService().save(consignmentProcessModel);
        return Transition.OK.toString();
    }

    @Override
    public Set<String> getTransitions() {
        return TelcelGuideExpressDeliveredAction.Transition.getStringValues();
    }
}
