/*
 *
 * [y] hybris Platform
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package mx.com.telcel.facades.mappers;

import de.hybris.platform.b2ctelcocommercewebservicescommons.dto.MoneyWsDTO;
import de.hybris.platform.b2ctelcocommercewebservicescommons.dto.ProductOfferingPriceWsDTO;
import de.hybris.platform.b2ctelcofacades.data.TmaComponentProdOfferPriceData;
import de.hybris.platform.b2ctelcofacades.mappers.TmaAttributeMapper;
import ma.glasnost.orika.MappingContext;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

public class TelcelTmaPopPriceBaseAttributeMapper extends TmaAttributeMapper<TmaComponentProdOfferPriceData, ProductOfferingPriceWsDTO> {

    private static final Logger LOG = Logger.getLogger(TelcelTmaPopPriceBaseAttributeMapper.class);


    @Override
    public void populateTargetAttributeFromSource(TmaComponentProdOfferPriceData source, ProductOfferingPriceWsDTO target, MappingContext context) {
        Assert.notNull(source, "Parameter source cannot be null.");
        Assert.notNull(target, "Parameter target cannot be null.");

        if (source.getMaxPrice() != null)
        {
            final MoneyWsDTO rcBasePrice = new MoneyWsDTO();
            rcBasePrice.setCurrencyIso(source.getCurrency().getIsocode());
            rcBasePrice.setValue(source.getMaxPrice().toString());
            target.setMaxPrice(rcBasePrice);
        }
    }


}
