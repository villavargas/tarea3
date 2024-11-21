/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.facades.converters.populator;

import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import mx.com.telcel.core.model.ConceptosRfcTelcelModel;
import mx.com.telcel.core.model.RegimenCapitalModel;
import mx.com.telcel.core.model.RegimenFiscalModel;
import mx.com.telcel.core.service.BillingService;


/**
 * The type Telcel address populator.
 */
public class TelcelAddressPopulator implements Populator<AddressModel, AddressData>
{

	@Resource(name = "billingService")
	private BillingService billingService;

	@Override
	public void populate(final AddressModel source, final AddressData target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");
		target.setExternalNumber(source.getExternalNumber());
		target.setInteriorNumber(source.getInteriorNumber());
		target.setReferences(source.getReferences());
		target.setRazonSocial(source.getRazonSocial());
		target.setRfc(source.getRfc());
		target.setConcepto(source.getConcepto());
		target.setRegimen(source.getRegimen());
		target.setCalle(source.getStreetname());
		target.setRegimenCapital(source.getRegimenCapital());
		if (StringUtils.isNotEmpty(source.getConcepto()))
		{
			final ConceptosRfcTelcelModel conceptosRfcTelcelModel = billingService.getConceptoRfcById(source.getConcepto());
			final String conceptoDes = conceptosRfcTelcelModel != null ? conceptosRfcTelcelModel.getConcepto() : StringUtils.EMPTY;
			target.setConceptoDescription(conceptoDes);
		}
		if (StringUtils.isNotEmpty(source.getRegimenCapital()))
		{
			final RegimenCapitalModel regimenCapitalModel = billingService.getRegimenCapitalById(source.getRegimenCapital());
			final String regimenCapitalDescription = regimenCapitalModel != null ? regimenCapitalModel.getDescription()
					: StringUtils.EMPTY;
			target.setRegimenCapitalDescription(regimenCapitalDescription);
		}
		if (StringUtils.isNotEmpty(source.getRegimen()))
		{
			final RegimenFiscalModel regimenFiscalModel = billingService.getRegimenFiscalById(source.getRegimen());
			final String regimenFiscalDescription = regimenFiscalModel != null ? regimenFiscalModel.getDescription()
					: StringUtils.EMPTY;

			target.setRegimenDescription(regimenFiscalDescription);
		}
		target.setLastName(source.getLastname());
	}
}
