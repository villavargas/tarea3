package mx.com.telcel.fulfilmentprocess.actions.consignment.telmex;

import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.action.AbstractAction;
import org.apache.log4j.Logger;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;
import mx.com.telcel.core.services.ordertelmex.NotifyProductOrderTelmex;

public class TelmexContinueProcessForGuideTypeAction extends AbstractAction<ConsignmentProcessModel> {
    private static final Logger LOG = Logger.getLogger(TelmexContinueProcessForGuideTypeAction.class);

    @Resource(name = "notifyProductOrderTelmex")
    private NotifyProductOrderTelmex notifyProductOrderTelmex;

    /**
     * The enum Transition.
     */
    public enum Transition {
        /**
         * Ok transition.
         */
        FEDEX,
        EXPRESS;

        /**
         * Gets string values.
         *
         * @return the string values
         */
        public static Set<String> getStringValues() {
            final Set<String> res = new HashSet<String>();

            for (final TelmexContinueProcessForGuideTypeAction.Transition transition : TelmexContinueProcessForGuideTypeAction.Transition.values()) {
                res.add(transition.toString());
            }
            return res;
        }
    }

    @Override
    public String execute(ConsignmentProcessModel consignmentProcessModel){
        consignmentProcessModel.setWaitingForGuideType(Boolean.FALSE);
        if (consignmentProcessModel.getConsignment().getEntregaExpress()) {
            LOG.info("Process: " + consignmentProcessModel.getCode() + " in step EXPRESS - " + getClass());
            notifyProductOrderTelmex.notifyProductOrderStateChange(consignmentProcessModel.getConsignment(), "EXPRESS");
            consignmentProcessModel.setWaitingForGuideExpressDelivered(Boolean.TRUE);
            getModelService().save(consignmentProcessModel);
            return Transition.EXPRESS.toString();
        } else {
            LOG.info("Process: " + consignmentProcessModel.getCode() + " in step FEDEX - " + getClass());
            return Transition.FEDEX.toString();
        }
    }

    @Override
    public Set<String> getTransitions() {
        return TelmexContinueProcessForGuideTypeAction.Transition.getStringValues();
    }
}
