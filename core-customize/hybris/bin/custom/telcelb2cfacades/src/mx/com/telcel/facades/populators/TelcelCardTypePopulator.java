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

import de.hybris.platform.commercefacades.order.converters.populator.CardTypePopulator;
import de.hybris.platform.commercefacades.order.data.CardTypeData;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.servicelayer.type.TypeService;
import org.springframework.beans.factory.annotation.Required;

public class TelcelCardTypePopulator extends CardTypePopulator {

    public static final String MASTERCARD = "MASTERCARD";
    public static final String MASTER = "master";
    private TypeService typeService;

    protected TypeService getTypeService()
    {
        return typeService;
    }

    @Required
    public void setTypeService(final TypeService typeService)
    {
        this.typeService = typeService;
    }

    @Override
    public void populate(final CreditCardType source, final CardTypeData target)
    {
        if(MASTER.equalsIgnoreCase(source.getCode())){
            target.setCode(MASTERCARD);
            target.setName(getTypeService().getEnumerationValue(source).getName());
        }else{
            target.setCode(source.getCode());
            target.setName(getTypeService().getEnumerationValue(source).getName());
        }

    }
}
