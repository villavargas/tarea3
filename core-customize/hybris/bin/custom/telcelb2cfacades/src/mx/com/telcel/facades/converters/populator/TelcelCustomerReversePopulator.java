/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.facades.converters.populator;

import de.hybris.platform.commercefacades.user.converters.populator.CustomerReversePopulator;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * The type Telcel customer reverse populator.
 */
public class TelcelCustomerReversePopulator extends CustomerReversePopulator {
    private static final Logger LOG = LoggerFactory.getLogger(TelcelCustomerReversePopulator.class);
    @Override
    public void populate(final CustomerData source, final CustomerModel target) throws ConversionException {
        Assert.notNull(source, "Parameter source cannot be null.");
        Assert.notNull(target, "Parameter target cannot be null.");
        super.populate(source, target);

        target.setNumberPhone(StringUtils.isNotEmpty(source.getPhoneNumber()) ? source.getPhoneNumber() : StringUtils.EMPTY);
        target.setDetailEmail(StringUtils.isNotEmpty(source.getDetailEmail()) ? source.getDetailEmail() : StringUtils.EMPTY);
        if (source.getBirthdate() != null) {
            target.setBirthdate(source.getBirthdate());
        }
        target.setCurp(StringUtils.isNotEmpty(source.getCurp()) ? source.getCurp() : StringUtils.EMPTY);
        target.setLastName(StringUtils.isNotEmpty(source.getLastName()) ? source.getLastName() : StringUtils.EMPTY);

        LOG.info("Fecha cumple: "+ target.getBirthdate());
        LOG.info("Numero telefonico: "+ target.getNumberPhone());
        LOG.info("Correo de detalles: "+ target.getDetailEmail());
        LOG.info("CURP: "+ target.getCurp());
        LOG.info("Apellidos: "+ target.getLastName());
    }
}
