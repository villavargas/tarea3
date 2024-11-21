/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.fulfilmentprocess.actions.order;

import de.hybris.platform.core.enums.PaymentStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.task.RetryLaterException;

import javax.annotation.Resource;

import mx.com.telcel.core.model.SendErpModel;
import mx.com.telcel.core.model.SendErpRespModel;
import mx.com.telcel.core.model.TelcelRollbackModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.amx.telcel.di.sds.esb.sap.cancelacion.LiberarRecursosResponse;

import mx.com.telcel.core.model.ReleaseResourcesModel;
import mx.com.telcel.core.models.liberarrecursos.InfoLiberarRecursos;
import mx.com.telcel.core.services.mq.liberacionrecursos.LiberaRecursosMQService;

import java.util.ArrayList;
import java.util.List;


/**
 * The type Roll back cancel action.
 */
public class RollBackCancelAction extends AbstractProceduralAction<OrderProcessModel> {
    private static final Logger LOG = Logger.getLogger(CancelPaymentAction.class);
    private static final String PROCESS_MSG = "Process: ";

    @Resource(name = "liberaRecursosMQService")
    private LiberaRecursosMQService liberaRecursosMQService;

    @Override
    public void executeAction(final OrderProcessModel process) throws RetryLaterException, Exception {
        LOG.info(PROCESS_MSG + process.getCode() + " in step " + getClass());
        final OrderModel orderModel = process.getOrder();
        final List<String> poNoSalesDocument = new ArrayList<>();
        final List<String> poNoDelivery = new ArrayList<>();
        for (final ConsignmentModel consignmentModel : orderModel.getConsignments()) {
            if (StringUtils.isNotEmpty(consignmentModel.getPoNoDelivery()) && StringUtils.isNotEmpty(consignmentModel.getPoNoSalesDocument())) {
                final InfoLiberarRecursos infoLiberarRecursos = new InfoLiberarRecursos();
                poNoSalesDocument.add(consignmentModel.getPoNoSalesDocument());
                poNoDelivery.add(consignmentModel.getPoNoDelivery());
                infoLiberarRecursos.setIdPedido(consignmentModel.getPoNoSalesDocument());
                infoLiberarRecursos.setIdEntrega(consignmentModel.getPoNoDelivery());
                infoLiberarRecursos.setRegion(orderModel.getRegionCode());
                infoLiberarRecursos.setbConsigment(true);
                infoLiberarRecursos.setbImei(false);
                infoLiberarRecursos.setbIccid(false);
                final LiberarRecursosResponse liberarRecursosResponse = liberaRecursosMQService.rollbackService(infoLiberarRecursos,
                        consignmentModel);
                getModelService().save(consignmentModel);
                if (liberarRecursosResponse != null) {
                    final ReleaseResourcesModel releaseResourcesModel = getModelService().create(ReleaseResourcesModel.class);
                    releaseResourcesModel.setMessageUUID(liberarRecursosResponse.getControlData().getMessageUUID());
                    releaseResourcesModel
                            .setIdTransaccion(liberarRecursosResponse.getLiberarRecursosActivacionResponse().getIdTransaccion());
                    releaseResourcesModel.setIdProceso(liberarRecursosResponse.getLiberarRecursosActivacionResponse().getIdProceso());
                    releaseResourcesModel.setRegion(infoLiberarRecursos.getRegion());
                    releaseResourcesModel.setIdEntrega(infoLiberarRecursos.getIdEntrega());
                    releaseResourcesModel.setIdPedido(infoLiberarRecursos.getIdPedido());
                    releaseResourcesModel.setOrder(orderModel.getCode());
                    releaseResourcesModel.setRMA(StringUtils.EMPTY);
                    releaseResourcesModel.setReturnProcesCode(process.getCode());
                    releaseResourcesModel.setMsisdn(
                            StringUtils.isNotEmpty(infoLiberarRecursos.getMsisdn()) ? infoLiberarRecursos.getMsisdn() : StringUtils.EMPTY);
                    releaseResourcesModel.setIccid(StringUtils.EMPTY);
                    releaseResourcesModel.setImei(StringUtils.EMPTY);
                    releaseResourcesModel.setConsignment(consignmentModel);
                    consignmentModel.setReleaseResources(releaseResourcesModel);
                    getModelService().saveAll(consignmentModel);
                }
            } else {
                LOG.info(String.format("the consignment with code %s does not have the necessary information to execute rollback", consignmentModel.getCode()));
            }
        }

        if (PaymentStatus.UNAUTHORIZED_PAYMENT.equals(orderModel.getPaymentStatus())) {
            for (AbstractOrderEntryModel entryModel : orderModel.getEntries()) {
                for (final SendErpRespModel sendErpRespModel : entryModel.getSendErpRespList()) {
                    for (final SendErpModel sendErpModel : sendErpRespModel.getSendErpList()) {
                        try {
                            final String salesDocument = sendErpModel.getPoNoSalesDocument();
                            final String noDelivery = sendErpModel.getPoNoDelivery();
                            boolean exist = false;
                            for (int x = 0; x < poNoSalesDocument.size(); x++) {
                                if (poNoSalesDocument.get(x).equals(salesDocument) && poNoDelivery.get(x).equals(noDelivery)) {
                                    exist = true;
                                    break;
                                }
                            }
                            if (!exist) {
                                final TelcelRollbackModel telcelRollbackModel = modelService.create(TelcelRollbackModel.class);
                                telcelRollbackModel.setPo_no_salesdocument(sendErpModel.getPoNoSalesDocument());
                                telcelRollbackModel.setPo_no_delivery(sendErpModel.getPoNoDelivery());
                                telcelRollbackModel.setPo_no_kunnr(sendErpModel.getPoNoKunnr());
                                telcelRollbackModel.setOrderID(orderModel.getCode());
                                telcelRollbackModel.setRegionCode(StringUtils.EMPTY);
                                modelService.save(telcelRollbackModel);
                            }
                        } catch (final Exception e) {
                            LOG.info(String.format("Error creating TelcelRollbackModel for order, order code:", orderModel.getCode()));
                            LOG.error(e.getMessage());
                        }
                    }
                }
            }
        }
    }
}
