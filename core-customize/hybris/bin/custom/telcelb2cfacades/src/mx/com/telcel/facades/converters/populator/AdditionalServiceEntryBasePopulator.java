/*
 *
 * [y] hybris Platform
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package mx.com.telcel.facades.converters.populator;

import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import mx.com.telcel.core.model.AdditionalServiceEntryModel;
import mx.com.telcel.core.model.TelcelAdditionalServiceProductOfferingModel;
import mx.com.telcel.facades.order.data.AdditionalServiceEntryData;
import mx.com.telcel.facades.product.data.AdditionalServiceData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Objects;

public class AdditionalServiceEntryBasePopulator implements Populator<AdditionalServiceEntryModel, AdditionalServiceEntryData>
{
    private static final Logger LOG = LoggerFactory.getLogger(AdditionalServiceEntryBasePopulator.class);

    private Converter<TelcelAdditionalServiceProductOfferingModel, AdditionalServiceData> additionalServiceConverter;
    private PriceDataFactory priceDataFactory;

    @Override
    public void populate(AdditionalServiceEntryModel source, AdditionalServiceEntryData target) throws ConversionException
    {
        Assert.notNull(source, "Parameter source cannot be null.");
        Assert.notNull(target, "Parameter target cannot be null.");

        try {
            if(source != null && source.getEntryNumber() != null){
                target.setEntryNumber(source.getEntryNumber());
                target.setRejected(source.getRejected());
                target.setDeniedPayment(source.getDeniedPayment() != null ? source.getDeniedPayment() : false);
                if(Objects.nonNull(source.getAdditionalServiceProduct())){
                    target.setAdditionalServiceProduct(getAdditionalServiceConverter().convert(source.getAdditionalServiceProduct()));
                }
                target.setBasePrice(createPrice(source, source.getBasePrice()));
            }
        }catch (Exception e){
            LOG.error("Ocurrio un error al obtener el Additional Service ["+e.getMessage()+"]");
        }


    }

    protected PriceData createPrice(final AdditionalServiceEntryModel source, final Double val)
    {
        return getPriceDataFactory().create(PriceDataType.BUY, BigDecimal.valueOf(val.doubleValue()),
                source.getEntry().getOrder().getCurrency());
    }

    protected Converter<TelcelAdditionalServiceProductOfferingModel, AdditionalServiceData> getAdditionalServiceConverter()
    {
        return additionalServiceConverter;
    }

    public void setAdditionalServiceConverter(final Converter<TelcelAdditionalServiceProductOfferingModel, AdditionalServiceData> additionalServiceConverter)
    {
        this.additionalServiceConverter = additionalServiceConverter;
    }

    protected PriceDataFactory getPriceDataFactory()
    {
        return priceDataFactory;
    }

    public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
    {
        this.priceDataFactory = priceDataFactory;
    }
}
