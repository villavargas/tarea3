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

import de.hybris.platform.b2ctelcocommercewebservicescommons.dto.RootGroupsWsDTO;
import de.hybris.platform.b2ctelcocommercewebservicescommons.mappers.cart.TmaCartRootGroupsAttributeMapper;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercewebservicescommons.dto.order.CartWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderEntryWsDTO;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

public class TelcelTmaCartRootGroupsAttributeMapper  extends TmaCartRootGroupsAttributeMapper {

    private MapperFacade mapperFacade;

    @Resource(name = "dataMapper")
    private DataMapper dataMapper;


    @Override
    public void populateTargetAttributeFromSource(final CartData source, final CartWsDTO target, final MappingContext context)
    {
        Assert.notNull(source, "Parameter source cannot be null.");
        Assert.notNull(target, "Parameter target cannot be null.");

        super.populateTargetAttributeFromSource(source,target,context);

        List<OrderEntryData>  orderEntryDataList = source.getEntriesAccesories();
        List<OrderEntryWsDTO> orderEntryWsDTOS = new ArrayList<>();

        if(CollectionUtils.isNotEmpty(orderEntryDataList)){
            for(OrderEntryData orderEntryData : orderEntryDataList){
                OrderEntryWsDTO orderEntryWsDTO = getDataMapper().map(orderEntryData, OrderEntryWsDTO.class, "DEFAULT");
                orderEntryWsDTOS.add(orderEntryWsDTO);
            }
        }

        target.setEntriesAccesories(orderEntryWsDTOS);
    }

    protected MapperFacade getMapperFacade()
    {
        return mapperFacade;
    }

    @Required
    public void setMapperFacade(final MapperFacade mapperFacade)
    {
        this.mapperFacade = mapperFacade;
    }

    public DataMapper getDataMapper() {
        return dataMapper;
    }

    public void setDataMapper(DataMapper dataMapper) {
        this.dataMapper = dataMapper;
    }
}
