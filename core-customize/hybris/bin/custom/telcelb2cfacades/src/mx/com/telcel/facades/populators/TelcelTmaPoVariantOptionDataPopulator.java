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

import de.hybris.platform.b2ctelcofacades.converters.populator.variants.TmaPoVariantOptionDataPopulator;
import de.hybris.platform.commercefacades.product.data.VariantMatrixElementData;
import org.apache.commons.collections.CollectionUtils;


public class TelcelTmaPoVariantOptionDataPopulator extends TmaPoVariantOptionDataPopulator {


    @Override
    protected void populateParent(VariantMatrixElementData parent) {
        if (CollectionUtils.isNotEmpty(parent.getElements()) && parent.getVariantOption() != null)
        {
            final VariantMatrixElementData child = getChildForParentCode(parent);
            parent.getVariantOption().setUrl(child.getVariantOption().getUrl());
            parent.getVariantOption().setCode(child.getVariantOption().getCode());
            parent.getVariantOption().setColor(child.getVariantOption().getColor());
            parent.getVariantOption().setCapacidad(child.getVariantOption().getCapacidad());
            parent.getVariantOption().setVariantOptionQualifiers(child.getVariantOption().getVariantOptionQualifiers());
        }
    }
}
