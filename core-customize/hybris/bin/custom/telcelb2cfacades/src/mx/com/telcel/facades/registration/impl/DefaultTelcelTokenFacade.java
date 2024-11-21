/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.facades.registration.impl;

import mx.com.telcel.core.model.RegistrationTokenModel;
import mx.com.telcel.core.services.TelcelTokenService;
import mx.com.telcel.facades.registration.TelcelTokenFacade;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;

/**
 * The type Default telcel token facade.
 */
public class DefaultTelcelTokenFacade implements TelcelTokenFacade {
    private static final Logger LOG = Logger.getLogger(DefaultTelcelTokenFacade.class);
    private TelcelTokenService telcelTokenService;

    @Override
    public RegistrationTokenModel saveRegistrationToken(String userID) {
        LOG.debug(String.format("DefaultTelcelTokenFacade - saveRegistrationToken for ID: %s ", userID));
        Assert.assertTrue(StringUtils.isNoneEmpty(userID));
        return telcelTokenService.saveRegistrationToken(userID);
    }

    @Override
    public RegistrationTokenModel findRegistrationToken(String userID, String token) {
        LOG.debug(String.format("DefaultTelcelTokenFacade - findRegistrationToken for ID: %s, and token: %s", userID, token));
        Assert.assertTrue(StringUtils.isNoneEmpty(userID));
        Assert.assertTrue(StringUtils.isNoneEmpty(token));
        return telcelTokenService.findRegistrationToken(userID, token);
    }

    @Override
    public String getToken() {
        return telcelTokenService.generateRandomToken();
    }

    /**
     * Gets telcel token service.
     *
     * @return the telcel token service
     */
    public TelcelTokenService getTelcelTokenService() {
        return telcelTokenService;
    }

    /**
     * Sets telcel token service.
     *
     * @param telcelTokenService the telcel token service
     */
    public void setTelcelTokenService(TelcelTokenService telcelTokenService) {
        this.telcelTokenService = telcelTokenService;
    }
}
