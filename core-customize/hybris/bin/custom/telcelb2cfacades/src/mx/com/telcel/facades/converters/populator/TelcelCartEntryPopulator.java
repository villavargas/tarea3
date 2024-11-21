/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.facades.converters.populator;

import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import mx.com.telcel.core.model.AdditionalServiceEntryModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


/**
 * The type Telcel cart entry populator.
 */
public class TelcelCartEntryPopulator implements Populator<AbstractOrderEntryModel, OrderEntryData> {

    private static final String AMIGO_CHIPCLASSIFICATION = "amigo_chipclassification";
    private PriceDataFactory priceDataFactory;

    @Override
    public void populate(AbstractOrderEntryModel source, OrderEntryData target) throws ConversionException {
        Optional<ClassificationClassModel> classificationClass = source.getProduct().getClassificationClasses().stream().findFirst();
        List<AdditionalServiceEntryModel> additionalServiceEntryModels = source.getAdditionalServiceEntries();
        if (classificationClass.isPresent() && AMIGO_CHIPCLASSIFICATION.equals(classificationClass.get().getCode()) && CollectionUtils.isNotEmpty(additionalServiceEntryModels)) {
            if (Objects.nonNull(target.getBasePrice()) && StringUtils.isNotEmpty(target.getBasePrice().getCurrencyIso())) {
                if (Objects.nonNull(source.getBasePricevalue())) {
                    target.setBasePricevalue(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(Double.sum(source.getBasePricevalue(), additionalServiceEntryModels.iterator().next().getBasePrice())), target.getBasePrice().getCurrencyIso()));
                }
                if (Objects.nonNull(source.getMaxPrice())) {
                    target.setMaxPrice(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(Double.sum(source.getMaxPrice(), additionalServiceEntryModels.iterator().next().getBasePrice())), target.getBasePrice().getCurrencyIso()));
                }
                target.setBasePrice(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(Double.sum(source.getBasePrice(), additionalServiceEntryModels.iterator().next().getBasePrice())), target.getBasePrice().getCurrencyIso()));
            }
        } else {
            if (Objects.nonNull(target.getBasePrice()) && StringUtils.isNotEmpty(target.getBasePrice().getCurrencyIso())) {
                if (Objects.nonNull(source.getBasePricevalue())) {
                    target.setBasePricevalue(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(source.getBasePricevalue()), target.getBasePrice().getCurrencyIso()));
                }
                if (Objects.nonNull(source.getMaxPrice())) {
                    target.setMaxPrice(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(source.getMaxPrice()), target.getBasePrice().getCurrencyIso()));
                }
            }
        }


    }

    /**
     * Gets price data factory.
     *
     * @return the price data factory
     */
    public PriceDataFactory getPriceDataFactory() {
        return priceDataFactory;
    }

    /**
     * Sets price data factory.
     *
     * @param priceDataFactory the price data factory
     */
    public void setPriceDataFactory(PriceDataFactory priceDataFactory) {
        this.priceDataFactory = priceDataFactory;
    }
}
