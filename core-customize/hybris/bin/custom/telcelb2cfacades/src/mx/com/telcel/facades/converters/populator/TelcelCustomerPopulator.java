/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.facades.converters.populator;

import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.CustomerModel;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;


/**
 * Extended populator implementation for {@link de.hybris.platform.core.model.user.CustomerModel} as source and
 * {@link de.hybris.platform.commercefacades.user.data.CustomerData} as target type.
 */
public class TelcelCustomerPopulator implements Populator<CustomerModel, CustomerData>
{
	private static final Logger LOG = LoggerFactory.getLogger(TelcelCustomerPopulator.class);
	@Override
	public void populate(final CustomerModel source, final CustomerData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setPhoneNumber(StringUtils.isNotEmpty(source.getNumberPhone()) ? removePrefix(source.getNumberPhone(),"52") : StringUtils.EMPTY);
		target.setDetailEmail(StringUtils.isNotEmpty(source.getDetailEmail()) ? source.getDetailEmail() : StringUtils.EMPTY);
		if(source.getBirthdate() != null){
			target.setBirthdate(source.getBirthdate());
		}
	    target.setCurp(StringUtils.isNotEmpty(source.getCurp()) ? source.getCurp() : StringUtils.EMPTY);
		target.setLastName(StringUtils.isNotEmpty(source.getLastName()) ? source.getLastName() : StringUtils.EMPTY);
		
		LOG.info("Fecha cumple: "+ target.getBirthdate());
        LOG.info("Numero telefonico: "+ target.getPhoneNumber());
        LOG.info("Correo de detalles: "+ target.getDetailEmail());
        LOG.info("CURP: "+ target.getCurp());
		LOG.info("Apellidos: "+ target.getLastName());
	}

	public static String removePrefix(String s, String prefix)
	{
		if (s != null && s.startsWith(prefix)) {
			return s.split(prefix, 2)[1];
		}
		return s;
	}
}
