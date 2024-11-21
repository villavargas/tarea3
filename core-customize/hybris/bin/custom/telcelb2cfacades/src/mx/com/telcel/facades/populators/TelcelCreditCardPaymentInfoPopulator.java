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

import de.hybris.platform.commercefacades.order.converters.populator.CreditCardPaymentInfoPopulator;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CardTypeData;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.payment.dto.FingerPrint;
import org.apache.commons.lang.StringUtils;

public class TelcelCreditCardPaymentInfoPopulator  extends CreditCardPaymentInfoPopulator {

    @Override
    public void populate(final CreditCardPaymentInfoModel source, final CCPaymentInfoData target)
    {
        super.populate(source,target);

        if (source.getType() != null)
        {
            final CardTypeData cardTypeData = getCardTypeConverter().convert(source.getType());
            target.setCardType(cardTypeData.getCode().toUpperCase());
            target.setCardTypeData(cardTypeData);
        }

        target.setTokenCard(source.getTokenCardTelcel());
        target.setPromotions(source.getPromotions());
        target.setHolderName(source.getHolderName());
        target.setHolderLastName(source.getHolderLastName());
        target.setUrl3ds(source.getUrl3ds());
        target.setBank(source.getBank());
        target.setCompany(source.getCompany());
        target.setCredit(source.isCreditCard());
        target.setAccountHolderName(source.getHolderName()+source.getHolderLastName());
        target.setZipCode(source.getZipCode());
        target.setPaymentType(source.getPaymentType());
        target.setMsi(StringUtils.isEmpty(source.getMsi())? 0 :  Integer.valueOf(source.getMsi()) );
        target.setCvv(source.getCvv());
        target.setCvn(source.getCvv());
        target.setSaved(source.isSaved());

        FingerPrint fingerPrint = new FingerPrint();
        if(source.getFingerprint() != null){
            fingerPrint.setSessionId(source.getFingerprint().getSessionId());
            fingerPrint.setOrganizationId(source.getFingerprint().getOrganizationId());
            fingerPrint.setWebSession(source.getFingerprint().getWebSession());
            target.setFingerprint(fingerPrint);
        }


        if(source.getOpenpayKeys() != null){
            target.setMerchantId(source.getOpenpayKeys().getMerchantId());
            target.setApiKey(source.getOpenpayKeys().getApiKey());
        }

        if(source.getCybersourceKeys() != null){
            target.setSessionId(source.getCybersourceKeys().getSessionId());
        }
    }
}
