/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.fulfilmentprocess.actions.returns;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.processengine.action.AbstractAction;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.task.RetryLaterException;
import mx.com.telcel.core.model.SipabbModel;
import mx.com.telcel.core.services.mq.liberacionrecursos.LiberaRecursosMQService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * The type Validate sipab restrictions action.
 */
public class ValidateSIPABRestrictionsAction extends AbstractAction<ReturnProcessModel> {

    private static final Logger LOG = Logger.getLogger(ValidateSIPABRestrictionsAction.class);
    private static final String SEPARATOR = "-";

    @Resource(name = "liberaRecursosMQService")
    private LiberaRecursosMQService liberaRecursosMQService;

    @Override
    public String execute(ReturnProcessModel process) throws RetryLaterException, Exception {
        LOG.info("Process: " + process.getCode() + " in step " + getClass().getSimpleName());
        final ReturnRequestModel returnRequestModel = process.getReturnRequest();
        final OrderModel orderModel = returnRequestModel.getOrder();
        final Set<ConsignmentModel> consignmentModels = orderModel.getConsignments();
        final SipabbModel sipabModel = returnRequestModel.getSipab();


        if (consignmentModels.size() == 1) {
            return validateAmountForOneConsignment(returnRequestModel, orderModel, consignmentModels.iterator().next(), sipabModel);
        } else {
            final String consignments = sipabModel.getConsignments();
            List<String> consignmentsList = new ArrayList<>();
            if (StringUtils.isNotEmpty(consignments)) {
                consignmentsList = Arrays.asList(consignments.toLowerCase().split(SEPARATOR));
            }

            if (consignmentsList.size() == 1) {
                for (ConsignmentModel consignmentModel : consignmentModels) {
                    if (consignmentModel.getCode().equals(consignmentsList.get(0))) {
                        return validateAmountForOneConsignment(returnRequestModel, orderModel, consignmentModel, sipabModel);
                    }
                }
                throw new Exception(String.format("Consignments from SIPAB list doesnt match with order: %s, %s", sipabModel.getNumeroDeOrdenCommerce(), sipabModel.getConsignments()));
            }
            List<ReturnRequestModel> returnRequestModelList = new ArrayList<>();
            BigDecimal totalAmount = new BigDecimal(0);
            for (ConsignmentModel consignmentModel : consignmentModels) {
                if (consignmentsList.contains(consignmentModel.getCode())) {
                    ReturnRequestModel returnRequestModel1 = liberaRecursosMQService.findRMAByConsignment(consignmentModel);
                    returnRequestModelList.add(returnRequestModel1);
                    totalAmount = totalAmount.add(getTotalamountFromReturnRequest(returnRequestModel1, orderModel, consignmentModel));
                }
            }
            return validateAmount(totalAmount, new BigDecimal(sipabModel.getImporte()), returnRequestModelList);
        }

    }

    private String validateAmountForOneConsignment(ReturnRequestModel returnRequestModel, OrderModel orderModel, ConsignmentModel consignmentModel, SipabbModel sipabModel) {
        BigDecimal totalAmount = getTotalamountFromReturnRequest(returnRequestModel, orderModel, consignmentModel);
        return validateAmount(totalAmount, new BigDecimal(sipabModel.getImporte()), Arrays.asList(returnRequestModel));
    }

    private BigDecimal getTotalamountFromReturnRequest(ReturnRequestModel returnRequestModel, OrderModel orderModel, ConsignmentModel consignmentModel) {
        BigDecimal totalAmount = returnRequestModel.getSubtotal();
        if (returnRequestModel.getRefundDeliveryCost() && orderModel.getDeliveryCost() > 0D) {
            totalAmount = totalAmount.add(new BigDecimal(orderModel.getDeliveryCost()));
        }
        if (returnRequestModel.getRefundAdditionalService() && consignmentModel.getAdditionalServiceEntry() != null) {
            totalAmount = totalAmount.add(new BigDecimal(consignmentModel.getAdditionalServiceEntry().getBasePrice()));
        }
        return totalAmount;
    }

    private String validateAmount(BigDecimal consignmentAmount, BigDecimal sipabAmount, List<ReturnRequestModel> returnRequestModelList) {
        int result = consignmentAmount.compareTo(sipabAmount);
        LOG.info(String.format("ValidateAmount - Comparing amounts requests: %s, SIPAB-importe: %s, result: %d", consignmentAmount, sipabAmount, result));
        returnRequestModelList.stream().forEach(returnRequestModel -> {
            if(result != 0){
                returnRequestModel.setAmountReview(Boolean.TRUE);
            }
            returnRequestModel.setConsignmentsAmount(consignmentAmount);
            returnRequestModel.setSipabAmount(sipabAmount);
            modelService.save(returnRequestModel);
        });

        return result == 0 ? Transition.OK.toString() : Transition.WAIT_FOR_AMOUNT_REVIEW.toString();
    }

    @Override
    public Set<String> getTransitions() {
        return Transition.getStringValues();
    }

    /**
     * The enum Transition.
     */
    protected enum Transition {
        /**
         * Ok transition.
         */
        OK,
        /**
         * Wait for amount review transition.
         */
        WAIT_FOR_AMOUNT_REVIEW;

        /**
         * Gets string values.
         *
         * @return the string values
         */
        public static Set<String> getStringValues() {
            final Set<String> res = new HashSet<String>();

            for (final Transition transition : Transition.values()) {
                res.add(transition.toString());
            }
            return res;
        }
    }
}
