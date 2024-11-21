package mx.com.telcel.fulfilmentprocess.actions.consignment.telmex;

import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.action.AbstractAction;
import org.apache.log4j.Logger;
import mx.com.telcel.core.services.ordertelmex.NotifyProductOrderTelmex;
import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

public class TelmexGuideExpressDeliveredAction extends AbstractAction<ConsignmentProcessModel> {
    private static final Logger LOG = Logger.getLogger(TelmexGuideExpressDeliveredAction.class);

    @Resource(name = "notifyProductOrderTelmex")
    private NotifyProductOrderTelmex notifyProductOrderTelmex;

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

            for (final TelmexGuideExpressDeliveredAction.Transition transition : TelmexGuideExpressDeliveredAction.Transition.values()) {
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
        notifyProductOrderTelmex.notifyProductOrderStateChange(consignmentProcessModel.getConsignment(), "ENTREGADO");
        return Transition.OK.toString();
    }

    @Override
    public Set<String> getTransitions() {
        return TelmexGuideExpressDeliveredAction.Transition.getStringValues();
    }
}
