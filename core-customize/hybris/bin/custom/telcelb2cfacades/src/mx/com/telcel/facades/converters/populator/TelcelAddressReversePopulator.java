/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.facades.converters.populator;

import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.springframework.util.Assert;


/**
 * The type Telcel address reverse populator.
 */
public class TelcelAddressReversePopulator implements Populator<AddressData, AddressModel>
{

	@Override
	public void populate(final AddressData source, final AddressModel target) throws ConversionException
	{
		Assert.notNull(source, "Parameter addressData cannot be null.");
		Assert.notNull(target, "Parameter addressModel cannot be null.");
		target.setExternalNumber(source.getExternalNumber());
		target.setInteriorNumber(source.getInteriorNumber());
		target.setReferences(source.getReferences());
		target.setRazonSocial(source.getRazonSocial());
		target.setRfc(source.getRfc());
		target.setConcepto(source.getConcepto());
		target.setRegimen(source.getRegimen());
		target.setRegimenCapital(source.getRegimenCapital());
		target.setConceptoDescription(source.getConceptoDescription());
		target.setRegimenDescription(source.getRegimenDescription());
		target.setRegimenCapitalDescription(source.getRegimenCapitalDescription());
		target.setLastname(source.getLastName());
	}
}
