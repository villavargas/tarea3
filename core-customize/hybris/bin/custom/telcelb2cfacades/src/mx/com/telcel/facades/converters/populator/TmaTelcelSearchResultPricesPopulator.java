/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.facades.converters.populator;


import de.hybris.platform.b2ctelcofacades.data.TmaOneTimeProdOfferPriceChargeData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;


/**
 * The type Default tma telcel search result prices populator.
 */
public class TmaTelcelSearchResultPricesPopulator implements Populator<SearchResultValueData, PriceData> {

    @Override
    public void populate(SearchResultValueData searchResultValueData, PriceData priceData) throws ConversionException {
        final ArrayList chipPriceList = this.getValue(searchResultValueData, "chipPrice");
        if (CollectionUtils.isNotEmpty(chipPriceList)) {
            if (priceData != null && priceData.getProductOfferingPrice() != null && priceData.getProductOfferingPrice() instanceof TmaOneTimeProdOfferPriceChargeData) {
                TmaOneTimeProdOfferPriceChargeData priceChargeData = (TmaOneTimeProdOfferPriceChargeData) priceData.getProductOfferingPrice();
                double chipPrice = (double) chipPriceList.get(0);
                priceChargeData.setValue(Double.sum(chipPrice, priceChargeData.getValue()));
                if (priceChargeData.getMaxPrice() != null) {
                    priceChargeData.setMaxPrice(Double.sum(chipPrice, priceChargeData.getMaxPrice()));
                }
                if (priceChargeData.getBasePricevalue() != null) {
                    priceChargeData.setBasePricevalue(Double.sum(chipPrice, priceChargeData.getBasePricevalue()));
                }
                priceData.setProductOfferingPrice(priceChargeData);
            }
        }
    }

    /**
     * Gets value.
     *
     * @param <T>          the type parameter
     * @param source       the source
     * @param propertyName the property name
     * @return the value
     */
    protected <T> T getValue(final SearchResultValueData source, final String propertyName) {
        if (source.getValues() == null) {
            return null;
        }

        // DO NOT REMOVE the cast (T) below, while it should be unnecessary it is required by the javac compiler
        return (T) source.getValues().get(propertyName);
    }
}
