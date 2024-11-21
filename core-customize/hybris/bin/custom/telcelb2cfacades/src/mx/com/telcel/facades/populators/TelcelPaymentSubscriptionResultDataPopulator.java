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

import de.hybris.platform.acceleratorfacades.payment.data.PaymentSubscriptionResultData;
import de.hybris.platform.acceleratorfacades.payment.populators.PaymentSubscriptionResultDataPopulator;
import de.hybris.platform.acceleratorservices.payment.data.PaymentSubscriptionResultItem;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CardTypeData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.ReferencePaymentInfoModel;
import de.hybris.platform.core.model.order.payment.SpeiPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import javax.annotation.Resource;

public class TelcelPaymentSubscriptionResultDataPopulator extends PaymentSubscriptionResultDataPopulator {

    @Resource
    private Converter<AddressModel, AddressData> addressConverter;


    @Override
    public void populate(final PaymentSubscriptionResultItem source, final PaymentSubscriptionResultData target) throws ConversionException
    {
        super.populate(source,target);
        if (source.getStoredReference()!= null)
        {
            target.setStoredReference(populateStoredReference(source.getStoredReference()));
        }

        if (source.getStoredSpei()!= null)
        {
            target.setStoredSpei(populateStoredSpei(source.getStoredSpei()));
        }
    }


    public CCPaymentInfoData populateStoredReference(final ReferencePaymentInfoModel source)
    {
        CCPaymentInfoData target = new CCPaymentInfoData();
        target.setId(source.getPk().toString());

        target.setSubscriptionId(source.getSubscriptionId());
        target.setSaved(source.isSaved());

        if (source.getBillingAddress() != null)
        {
            target.setBillingAddress(getAddressConverter().convert(source.getBillingAddress()));
        }

        return target;

    }

    public CCPaymentInfoData populateStoredSpei(final SpeiPaymentInfoModel source)
    {
        CCPaymentInfoData target = new CCPaymentInfoData();
        target.setId(source.getPk().toString());

        target.setSubscriptionId(source.getSubscriptionId());
        target.setSaved(source.isSaved());

        if (source.getBillingAddress() != null)
        {
            target.setBillingAddress(getAddressConverter().convert(source.getBillingAddress()));
        }

        return target;

    }

    public Converter<AddressModel, AddressData> getAddressConverter() {
        return addressConverter;
    }

    public void setAddressConverter(Converter<AddressModel, AddressData> addressConverter) {
        this.addressConverter = addressConverter;
    }
}
