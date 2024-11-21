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
import mx.com.telcel.core.model.SendPwdConfirmationEmailProcessModel;


/**
 * The type Pwd confirmation email context.
 */
public class PwdConfirmationEmailContext extends AbstractEmailContext<SendPwdConfirmationEmailProcessModel> {

    /**
     * The constant EMAIL.
     */
    public static final String EMAIL = "email";
    private CommonI18NService commonI18NService;

    @Override
    public void init(final SendPwdConfirmationEmailProcessModel sendPwdConfirmationEmailProcessModel, final EmailPageModel emailPageModel) {
        super.init(sendPwdConfirmationEmailProcessModel, emailPageModel);

        put("channel", sendPwdConfirmationEmailProcessModel.getChannel());
        put("company", sendPwdConfirmationEmailProcessModel.getCompany());
        put("channelOne", sendPwdConfirmationEmailProcessModel.getChannelOne());
        put("channelTwo", sendPwdConfirmationEmailProcessModel.getChannelTwo());

        put(EMAIL, sendPwdConfirmationEmailProcessModel.getEmail());
        put(FROM_EMAIL, emailPageModel.getFromEmail());
        put(DISPLAY_NAME, emailPageModel.getFromEmail());
    }

    /**
     * Gets site.
     *
     * @param businessProcessModel the business process model
     * @return the site
     */
    @Override
    protected BaseSiteModel getSite(SendPwdConfirmationEmailProcessModel businessProcessModel) {
        return businessProcessModel.getSite();
    }

    /**
     * Gets customer.
     *
     * @param businessProcessModel the business process model
     * @return the customer
     */
    @Override
    protected CustomerModel getCustomer(SendPwdConfirmationEmailProcessModel businessProcessModel) {
        return businessProcessModel.getCustomer();
    }

    /**
     * Gets email language.
     *
     * @param businessProcessModel the business process model
     * @return the email language
     */
    @Override
    protected LanguageModel getEmailLanguage(SendPwdConfirmationEmailProcessModel businessProcessModel) {
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
