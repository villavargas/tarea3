/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.facades.process.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.media.MediaService;
import mx.com.telcel.core.model.SendBlockedAccountEmailProcessModel;

import javax.annotation.Resource;

/**
 * The type Blocked account email context.
 */
public class BlockedAccountEmailContext extends AbstractEmailContext<SendBlockedAccountEmailProcessModel> {

    /**
     * The constant EMAIL.
     */
    public static final String EMAIL = "email";
    private CommonI18NService commonI18NService;
    @Resource
    private MediaService mediaService;

    @Resource
    private CatalogVersionService catalogVersionService;

    public CatalogVersionService getCatalogVersionService() {
        return catalogVersionService;
    }

    public void setCatalogVersionService(CatalogVersionService catalogVersionService) {
        this.catalogVersionService = catalogVersionService;
    }

    @Override
    public void init(final SendBlockedAccountEmailProcessModel sendBlockedAccountEmailProcessModel,
                     final EmailPageModel emailPageModel) {
        super.init(sendBlockedAccountEmailProcessModel, emailPageModel);

        put("minutes", sendBlockedAccountEmailProcessModel.getMinutes());
        put("channel", sendBlockedAccountEmailProcessModel.getChannel());
        CatalogVersionModel cat = new CatalogVersionModel();
        cat = getCatalogVersionService().getCatalogVersion("telcelContentCatalog", "Online");
        MediaModel bannerModel = getMediaService().getMedia(cat,"/images/theme/img-banner_01.png");
        MediaModel logoModel = getMediaService().getMedia(cat,"/images/theme/logo-telcel.png");
        put("bannerModel", bannerModel.getURL2());
        put("logoModel", logoModel.getURL2());
        put(EMAIL, sendBlockedAccountEmailProcessModel.getEmail());
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
    protected BaseSiteModel getSite(SendBlockedAccountEmailProcessModel businessProcessModel) {
        return businessProcessModel.getSite();
    }

    /**
     * Gets customer.
     *
     * @param businessProcessModel the business process model
     * @return the customer
     */
    @Override
    protected CustomerModel getCustomer(SendBlockedAccountEmailProcessModel businessProcessModel) {
        return businessProcessModel.getCustomer();
    }

    public MediaService getMediaService() {
        return mediaService;
    }

    public void setMediaService(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    /**
     * Gets email language.
     *
     * @param businessProcessModel the business process model
     * @return the email language
     */
    @Override
    protected LanguageModel getEmailLanguage(SendBlockedAccountEmailProcessModel businessProcessModel) {
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
