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

import de.hybris.platform.b2ctelcofacades.mappers.TmaAttributeMapper;
import de.hybris.platform.commercefacades.product.data.VariantOptionData;
import de.hybris.platform.commercewebservicescommons.dto.product.VariantOptionWsDTO;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import org.springframework.beans.factory.annotation.Required;

/**
 * The type Telcel variant product specification attribute mapper.
 */
public class TelcelVariantProductSpecificationAttributeMapper extends TmaAttributeMapper<VariantOptionData, VariantOptionWsDTO>{

    private MapperFacade mapperFacade;


    @Override
    public void populateTargetAttributeFromSource(VariantOptionData variantOptionData, VariantOptionWsDTO variantOptionWsDTO, MappingContext context) {
        variantOptionWsDTO.setCapacidad(variantOptionData.getCapacidad());
        variantOptionWsDTO.setColor(variantOptionData.getColor());
    }


    /**
     * Gets mapper facade.
     *
     * @return the mapper facade
     */
    public MapperFacade getMapperFacade()
    {
        return mapperFacade;
    }

    /**
     * Sets mapper facade.
     *
     * @param mapperFacade the mapper facade
     */
    @Required
    public void setMapperFacade(final MapperFacade mapperFacade)
    {
        this.mapperFacade = mapperFacade;
    }
}
