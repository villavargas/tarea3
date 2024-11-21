/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.facades.registration;

import mx.com.telcel.core.model.RegistrationTokenModel;

/**
 * The interface Telcel token facade.
 */
public interface TelcelTokenFacade {

    /**
     * Save registration token registration token model.
     *
     * @param userID the user id
     * @return the registration token model
     */
    RegistrationTokenModel saveRegistrationToken(String userID);

    /**
     * Find registration token registration token model.
     *
     * @param userID the user id
     * @param token  the token
     * @return the registration token model
     */
    RegistrationTokenModel findRegistrationToken(String userID, String token);

    String getToken();
}
