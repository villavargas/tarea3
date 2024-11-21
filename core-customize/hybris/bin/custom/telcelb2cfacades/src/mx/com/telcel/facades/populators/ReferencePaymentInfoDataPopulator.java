/*
 *
 * [y] hybris Platform
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package mx.com.telcel.facades.populators;

import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.ReceiptInfoData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.payment.ReferencePaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import mx.com.telcel.core.model.PaymentReceiptModel;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Reference payment info data populator.
 */
public class ReferencePaymentInfoDataPopulator implements Populator<ReferencePaymentInfoModel, CCPaymentInfoData> {
    private Converter<AddressModel, AddressData> addressConverter;

    /**
     * Gets address converter.
     *
     * @return the address converter
     */
    protected Converter<AddressModel, AddressData> getAddressConverter() {
        return addressConverter;
    }

    /**
     * Sets address converter.
     *
     * @param addressConverter the address converter
     */
    @Required
    public void setAddressConverter(final Converter<AddressModel, AddressData> addressConverter) {
        this.addressConverter = addressConverter;
    }

    @Override
    public void populate(final ReferencePaymentInfoModel source, final CCPaymentInfoData target) {
        target.setId(source.getPk().toString());
        target.setPaymentType("REFERENCE");
        target.setSubscriptionId(source.getSubscriptionId());
        target.setSaved(source.isSaved());
        target.setPaymentReferenceName(source.getPaymentReferenceName());

        if (source.getBillingAddress() != null) {
            target.setBillingAddress(getAddressConverter().convert(source.getBillingAddress()));
        }

        List<ReceiptInfoData> receipt = new ArrayList<>();
        if(source.getReceipt() != null){
            for(PaymentReceiptModel paymentReceiptModel:source.getReceipt()){
                ReceiptInfoData receiptInfoData = new ReceiptInfoData();
                receiptInfoData.setTransaction(paymentReceiptModel.getTransaction());
                receiptInfoData.setReference(paymentReceiptModel.getReference());
                receiptInfoData.setExpiration(paymentReceiptModel.getExpiration());
                receiptInfoData.setCode(paymentReceiptModel.getCode());
                receiptInfoData.setBarcode(paymentReceiptModel.getBarcode());
                receiptInfoData.setStatus(paymentReceiptModel.getStatus());
                receipt.add(receiptInfoData);
            }
            target.setReceipt(receipt);

        }

    }
}
