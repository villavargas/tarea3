/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.backoffice.omsbackoffice.widgets.returns.dtos;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.returns.model.RefundEntryModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.zkoss.zul.ListModelArray;

/**
 * The type Return entry to create dto.
 */
public class ReturnEntryToCreateDto {
    private RefundEntryModel refundEntry = null;
    private int returnableQuantity;
    private int quantityToReturn;
    private String refundEntryComment;
    private String resourceSAP;
    private String resourceSIAP;
    private boolean discountApplied;
    private BigDecimal tax;
    private ListModelArray reasonsModel = new ListModelArray(new ArrayList());
    private ListModelArray consignments = new ListModelArray(new ArrayList());

    /**
     * Instantiates a new Return entry to create dto.
     *
     * @param orderEntry         the order entry
     * @param returnableQuantity the returnable quantity
     * @param reasons            the reasons
     */
    public ReturnEntryToCreateDto(AbstractOrderEntryModel orderEntry, int returnableQuantity, List<String> reasons) {
        RefundEntryModel defaultRefundEntry = new RefundEntryModel();
        defaultRefundEntry.setOrderEntry(orderEntry);
        defaultRefundEntry.setAmount(BigDecimal.ZERO);
        this.setRefundEntry(defaultRefundEntry);
        this.setReturnableQuantity(returnableQuantity);
        this.setQuantityToReturn(0);
        this.setReasonsModel(new ListModelArray(reasons));
        final AbstractOrderModel orderModel = orderEntry.getOrder();
        List<String> consignments = new ArrayList<>();
        for (ConsignmentModel consignmentModel : orderModel.getConsignments()) {
            if (!consignmentModel.getWasItReturned() && orderEntry.getEntryNumber().equals(consignmentModel.getConsignmentEntries().iterator().next().getOrderEntry().getEntryNumber())) {
                if (StringUtils.isNotEmpty(consignmentModel.getPoNoSalesDocument()) && StringUtils.isNotEmpty(consignmentModel.getPoNoDelivery())) {
                    consignments.add(consignmentModel.getCode());
                }
            }
        }
        this.setConsignments(new ListModelArray(consignments));
        this.setDiscountApplied(orderEntry.getDiscountValues() != null && orderEntry.getDiscountValues().size() > 0);
    }

    /**
     * Gets refund entry.
     *
     * @return the refund entry
     */
    public RefundEntryModel getRefundEntry() {
        return this.refundEntry;
    }

    /**
     * Sets refund entry.
     *
     * @param refundEntry the refund entry
     */
    public void setRefundEntry(RefundEntryModel refundEntry) {
        this.refundEntry = refundEntry;
    }

    /**
     * Gets refund entry comment.
     *
     * @return the refund entry comment
     */
    public String getRefundEntryComment() {
        return this.refundEntryComment;
    }

    /**
     * Sets refund entry comment.
     *
     * @param refundEntryComment the refund entry comment
     */
    public void setRefundEntryComment(String refundEntryComment) {
        this.refundEntryComment = refundEntryComment;
    }

    /**
     * Gets reasons model.
     *
     * @return the reasons model
     */
    public ListModelArray getReasonsModel() {
        return this.reasonsModel;
    }

    /**
     * Sets reasons model.
     *
     * @param reasonsModel the reasons model
     */
    public void setReasonsModel(ListModelArray reasonsModel) {
        this.reasonsModel = reasonsModel;
    }

    /**
     * Gets returnable quantity.
     *
     * @return the returnable quantity
     */
    public int getReturnableQuantity() {
        return this.returnableQuantity;
    }

    /**
     * Sets returnable quantity.
     *
     * @param returnableQuantity the returnable quantity
     */
    public void setReturnableQuantity(int returnableQuantity) {
        this.returnableQuantity = returnableQuantity;
    }

    /**
     * Gets quantity to return.
     *
     * @return the quantity to return
     */
    public int getQuantityToReturn() {
        return this.quantityToReturn;
    }

    /**
     * Sets quantity to return.
     *
     * @param quantityToReturn the quantity to return
     */
    public void setQuantityToReturn(int quantityToReturn) {
        this.quantityToReturn = quantityToReturn;
    }

    /**
     * Is discount applied boolean.
     *
     * @return the boolean
     */
    public boolean isDiscountApplied() {
        return this.discountApplied;
    }

    /**
     * Sets discount applied.
     *
     * @param discountApplied the discount applied
     */
    public void setDiscountApplied(boolean discountApplied) {
        this.discountApplied = discountApplied;
    }

    /**
     * Gets tax.
     *
     * @return the tax
     */
    public BigDecimal getTax() {
        return this.tax;
    }

    /**
     * Sets tax.
     *
     * @param tax the tax
     */
    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }

    /**
     * Gets consignments.
     *
     * @return the consignments
     */
    public ListModelArray getConsignments() {
        return consignments;
    }

    /**
     * Sets consignments.
     *
     * @param consignments the consignments
     */
    public void setConsignments(ListModelArray consignments) {
        this.consignments = consignments;
    }

    /**
     * Gets resource sap.
     *
     * @return the resource sap
     */
    public String getResourceSAP() {
        return resourceSAP;
    }

    /**
     * Sets resource sap.
     *
     * @param resourceSAP the resource sap
     */
    public void setResourceSAP(String resourceSAP) {
        this.resourceSAP = resourceSAP;
    }

    /**
     * Gets resource siap.
     *
     * @return the resource siap
     */
    public String getResourceSIAP() {
        return resourceSIAP;
    }

    /**
     * Sets resource siap.
     *
     * @param resourceSIAP the resource siap
     */
    public void setResourceSIAP(String resourceSIAP) {
        this.resourceSIAP = resourceSIAP;
    }
}
