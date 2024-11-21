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

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import mx.com.telcel.core.model.TelcelAdditionalServiceProductOfferingModel;
import mx.com.telcel.facades.product.data.AdditionalServiceData;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

public class AdditionalServicePopulator implements Populator<TelcelAdditionalServiceProductOfferingModel, AdditionalServiceData>
{
    private Populator<ProductModel, ProductData> productDescriptionPopulator;
    private Populator<ProductModel, ProductData> tmaProductOfferingPricesPopulator;
    private Populator<ProductModel, ProductData> productPrimaryImagePopulator;

    @Override
    public void populate(final TelcelAdditionalServiceProductOfferingModel source, final AdditionalServiceData target)
    {
        Assert.notNull(source, "Parameter source cannot be null.");
        Assert.notNull(target, "Parameter target cannot be null.");

        target.setCode(source.getCode());
        target.setName(source.getName());
        getProductDescriptionPopulator().populate(source, target);
        getTmaProductOfferingPricesPopulator().populate(source, target);
        getProductPrimaryImagePopulator().populate(source, target);
        addAditionalServiceAtributtes(source,target);
    }

    private void addAditionalServiceAtributtes(final TelcelAdditionalServiceProductOfferingModel source, final AdditionalServiceData target)
    {
        target.setType(source.getType().getCode());
    }

    protected Populator<ProductModel, ProductData> getProductDescriptionPopulator()
    {
        return productDescriptionPopulator;
    }

    @Required
    public void setProductDescriptionPopulator(final Populator<ProductModel, ProductData> productDescriptionPopulator)
    {
        this.productDescriptionPopulator = productDescriptionPopulator;
    }

    protected Populator<ProductModel, ProductData> getTmaProductOfferingPricesPopulator()
    {
        return tmaProductOfferingPricesPopulator;
    }

    @Required
    public void setTmaProductOfferingPricesPopulator(final Populator<ProductModel, ProductData> tmaProductOfferingPricesPopulator)
    {
        this.tmaProductOfferingPricesPopulator = tmaProductOfferingPricesPopulator;
    }

    protected Populator<ProductModel, ProductData> getProductPrimaryImagePopulator()
    {
        return productPrimaryImagePopulator;
    }

    @Required
    public void setProductPrimaryImagePopulator(final Populator<ProductModel, ProductData> productPrimaryImagePopulator)
    {
        this.productPrimaryImagePopulator = productPrimaryImagePopulator;
    }
}
