/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.facades.converters.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import mx.com.telcel.core.model.HolderLineModel;
import mx.com.telcel.dto.CustomerCheckoutInfWsDTO;


/**
 * The type Holder line reverse populator.
 */
public class HolderLineReversePopulator implements Populator<CustomerCheckoutInfWsDTO, HolderLineModel>
{

	@Override
	public void populate(final CustomerCheckoutInfWsDTO source, final HolderLineModel target) throws ConversionException
	{
		target.setName(source.getName());
		target.setLastName(source.getLastName());
		target.setDateOfBirth(source.getDateOfBirth());
		target.setEmail(source.getEmail());
		target.setCurp(source.getCurp());
	}
}
