/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.facades.converters.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import mx.com.telcel.core.model.HolderLineModel;
import mx.com.telcel.facades.holderline.data.HolderLineData;


/**
 * The type Holder line populator.
 */
public class HolderLinePopulator implements Populator<HolderLineModel, HolderLineData>
{
	@Override
	public void populate(final HolderLineModel source, final HolderLineData target) throws ConversionException
	{
		target.setName(source.getName());
		target.setLastName(source.getLastName());
		target.setDateOfBirth(source.getDateOfBirth());
		target.setEmail(source.getEmail());
		target.setCurp(source.getCurp());
	}
}
