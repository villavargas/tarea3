/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.backoffice.security.handler;

import com.hybris.cockpitng.util.web.authorization.BackofficeAuthenticationSuccessHandler;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import org.springframework.security.core.Authentication;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * The type Telcel success handler.
 */
public class TelcelSuccessHandler extends BackofficeAuthenticationSuccessHandler {

    private UserService userService;
    private ModelService modelService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        super.onAuthenticationSuccess(request, response, authentication);
        UserModel userModel = userService.getCurrentUser();
        userModel.setLastLogin(new Date());
        modelService.save(userModel);
    }

    /**
     * Gets user service.
     *
     * @return the user service
     */
    public UserService getUserService() {
        return userService;
    }

    /**
     * Sets user service.
     *
     * @param userService the user service
     */
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Gets model service.
     *
     * @return the model service
     */
    public ModelService getModelService() {
        return modelService;
    }

    /**
     * Sets model service.
     *
     * @param modelService the model service
     */
    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }
}
