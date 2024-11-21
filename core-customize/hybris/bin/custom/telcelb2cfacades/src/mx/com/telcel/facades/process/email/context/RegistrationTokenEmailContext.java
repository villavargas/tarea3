/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.facades.process.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import mx.com.telcel.core.model.RegistrationTokenModel;
import mx.com.telcel.core.model.SendRegistrationTokenEmailProcessModel;

/**
 * The type Registration token email context.
 */
public class RegistrationTokenEmailContext extends AbstractEmailContext<SendRegistrationTokenEmailProcessModel> {

    /**
     * The constant TOKEN.
     */
    public static final String TOKEN = "token";
    private CommonI18NService commonI18NService;

    @Override
    public void init(final SendRegistrationTokenEmailProcessModel sendRegistrationTokenEmailProcessModel, final EmailPageModel emailPageModel) {
        super.init(sendRegistrationTokenEmailProcessModel, emailPageModel);
        final RegistrationTokenModel registrationTokenModel = sendRegistrationTokenEmailProcessModel.getRegistrationToken();
        put(TOKEN, registrationTokenModel.getToken());

        put(EMAIL, sendRegistrationTokenEmailProcessModel.getEmail());
        put(FROM_EMAIL, emailPageModel.getFromEmail());
        put(DISPLAY_NAME, emailPageModel.getFromEmail());
    }

    @Override
    protected BaseSiteModel getSite(SendRegistrationTokenEmailProcessModel businessProcessModel) {
        return businessProcessModel.getSite();
    }

    @Override
    protected CustomerModel getCustomer(SendRegistrationTokenEmailProcessModel businessProcessModel) {
        return businessProcessModel.getCustomer();
    }

    @Override
    protected LanguageModel getEmailLanguage(SendRegistrationTokenEmailProcessModel businessProcessModel) {
        return businessProcessModel.getLanguage();
    }

    /**
     * Gets common i 18 n service.
     *
     * @return the common i 18 n service
     */
    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    /**
     * Sets common i 18 n service.
     *
     * @param commonI18NService the common i 18 n service
     */
    public void setCommonI18NService(CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }
}
