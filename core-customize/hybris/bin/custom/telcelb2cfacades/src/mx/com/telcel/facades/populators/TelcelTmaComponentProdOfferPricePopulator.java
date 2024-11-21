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

import de.hybris.platform.b2ctelcofacades.data.TmaComponentProdOfferPriceData;
import de.hybris.platform.b2ctelcoservices.model.TmaComponentProdOfferPriceModel;
import de.hybris.platform.b2ctelcoservices.model.TmaOneTimeProdOfferPriceChargeModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import mx.com.telcel.core.model.TelcelPoVariantModel;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collection;

public class TelcelTmaComponentProdOfferPricePopulator<SOURCE extends TmaComponentProdOfferPriceModel,
        TARGET extends TmaComponentProdOfferPriceData> implements Populator<SOURCE, TARGET> {


    @Override
    public void populate(SOURCE source, TARGET target) throws ConversionException {
        target.setBasePricevalue(source.getBasePricevalue());
        target.setMaxPrice(source.getMaxPrice());

        ProductModel product = source.getPriceRows() != null ? source.getPriceRows().iterator().next().getProduct() : null;

        if (product != null && product instanceof TelcelPoVariantModel) {
            TelcelPoVariantModel telcelPoVariantModel = (TelcelPoVariantModel) product;

            if (telcelPoVariantModel.getChipAdditionalServiceProduct() != null) {
                final Collection<PriceRowModel> priceRowModels = telcelPoVariantModel.getChipAdditionalServiceProduct().getEurope1Prices();
                if (CollectionUtils.isNotEmpty(priceRowModels)) {
                    final PriceRowModel priceRowModel1 = priceRowModels.iterator().next();
                    final TmaOneTimeProdOfferPriceChargeModel productOfferingPrice =
                            (TmaOneTimeProdOfferPriceChargeModel) priceRowModel1.getProductOfferingPrice();

                    if (source.getBasePricevalue() != null) {
                        target.setBasePricevalue(Double.sum(source.getBasePricevalue(), productOfferingPrice.getValue()));
                    }
                    if (source.getMaxPrice() != null) {
                        target.setMaxPrice(Double.sum(source.getMaxPrice(), productOfferingPrice.getValue()));
                    }
                    target.setValue(Double.sum(target.getValue(), productOfferingPrice.getValue()));
                }
            }
        }
    }
}
