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

import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderEntryWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.product.PriceWsDTO;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import mx.com.telcel.facades.esquemacobro.data.TelcelEsquemaCobroDTO;
import org.springframework.beans.factory.annotation.Required;

import java.util.Objects;

public class TelcelOrderEntriesReturnsAttributesMapper extends TelcelAttributeMapper<OrderEntryData, OrderEntryWsDTO> {

    private MapperFacade mapperFacade;

    @Override
    public void populateTargetAttributeFromSource(OrderEntryData source, OrderEntryWsDTO target, MappingContext context) {
        if (Objects.nonNull(source.getReturnableQty()) && source.getReturnableQty() > 0L) {
            target.setReturnedItemsPrice(getMapperFacade().map(source, PriceWsDTO.class, context));
            target.setReturnableQuantity(Long.valueOf(source.getReturnableQty()));
        }
        if (Objects.nonNull(source.getEsquemaCobro())) {
            target.setEsquemaCobro(getMapperFacade().map(source.getEsquemaCobro(), TelcelEsquemaCobroDTO.class));
        }
    }


    /**
     * Gets mapper facade.
     *
     * @return the mapper facade
     */
    public MapperFacade getMapperFacade() {
        return mapperFacade;
    }

    /**
     * Sets mapper facade.
     *
     * @param mapperFacade the mapper facade
     */
    @Required
    public void setMapperFacade(final MapperFacade mapperFacade) {
        this.mapperFacade = mapperFacade;
    }
}
