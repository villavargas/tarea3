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

import de.hybris.platform.acceleratorservices.payment.cybersource.converters.populators.response.PaymentInfoResultPopulator;
import de.hybris.platform.acceleratorservices.payment.data.CreateSubscriptionResult;
import de.hybris.platform.acceleratorservices.payment.data.PaymentInfoData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

public class PaymentTelcelInfoResultPopulator extends PaymentInfoResultPopulator {

    @Override
    public void populate(final Map<String, String> source, final CreateSubscriptionResult target) throws ConversionException
    {
        validateParameterNotNull(source, "Parameter [Map<String, String>] source cannot be null");
        validateParameterNotNull(target, "Parameter [CreateSubscriptionResult] target cannot be null");

        final PaymentInfoData data = new PaymentInfoData();
        data.setCardAccountNumber(source.get("card_accountNumber"));
        data.setCardCardType(source.get("card_cardType"));
        data.setCardAccountHolderName(source.get("card_nameOnCard"));
        data.setCardExpirationMonth(getIntegerForString(source.get("card_expirationMonth")));
        data.setCardExpirationYear(getIntegerForString(source.get("card_expirationYear")));
        data.setCardStartMonth(source.get("card_startMonth"));
        data.setCardStartYear(source.get("card_startYear"));
        data.setPaymentOption(source.get("paymentOption"));
        data.setTokenCard(source.get("card_tokenCard"));
        data.setPromotions(source.get("card_promotions"));
//        data.setPromotions("6841597");
        data.setPaymentReferenceName(source.get("card_paymentReferenceName"));
        data.setHolderName(source.get("card_holderName"));
        data.setHolderLastName(source.get("card_holderLastName"));
        data.setUrl3ds(source.get("url3ds"));
        data.setBank(source.get("card_bank"));
        data.setCompany(source.get("card_company"));
        if(source.get("card_credit") != null){
            data.setCredit(Boolean.parseBoolean(source.get("card_credit")));
        }

        if(source.get("card_saved") != null){
            data.setSaved(Boolean.parseBoolean(source.get("card_saved")));
        }
        data.setZipCode(source.get("card_zipCode"));
        data.setMsi(StringUtils.isNotBlank(source.get("card_msi")) && !"undefined".equalsIgnoreCase(source.get("card_msi"))? Integer.valueOf(source.get("card_msi")) : 0);
        data.setCvn(source.get("card_cvn"));
        data.setCvv(source.get("card_cvn"));
        data.setPaymentType(source.get("card_paymentType"));
        target.setPaymentInfoData(data);
    }


}
