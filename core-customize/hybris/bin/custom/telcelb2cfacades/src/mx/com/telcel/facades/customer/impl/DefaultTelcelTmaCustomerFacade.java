/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.facades.customer.impl;

import de.hybris.platform.commercefacades.customer.impl.DefaultCustomerFacade;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.europe1.enums.UserPriceGroup;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import mx.com.telcel.core.util.TelcelUtil;
import mx.com.telcel.facades.customer.TelcelCustomerFacade;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import java.util.Objects;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

public class DefaultTelcelTmaCustomerFacade extends DefaultCustomerFacade implements TelcelCustomerFacade {

    @Resource
    private TelcelUtil telcelUtil; // NOSONAR

    private EnumerationService enumerationService;

    @Resource(name = "userService")
    private UserService userService;

    @Resource(name = "modelService")
    private ModelService modelService;

    @Override
    public void createGuestUserForAnonymousCheckout(String email, String name) throws DuplicateUidException {

        validateParameterNotNullStandardMessage("email", email);
        final CustomerModel guestCustomer = getModelService().create(CustomerModel.class);
        final String guid = generateGUID();

        //takes care of localizing the name based on the site language
        guestCustomer.setUid(guid + "|" + email);
        guestCustomer.setName(name);
        guestCustomer.setType(CustomerType.valueOf(CustomerType.GUEST.getCode()));
        guestCustomer.setSessionLanguage(getCommonI18NService().getCurrentLanguage());
        guestCustomer.setSessionCurrency(getCommonI18NService().getCurrentCurrency());
        guestCustomer.setTitle(getUserService().getTitleForCode("mr"));

        final String region = telcelUtil.getRegionByRequest(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest()); // NOSONAR
        if (StringUtils.isNotEmpty(region)) {
            final UserPriceGroup userPriceGroup = getEnumerationService().getEnumerationValue(UserPriceGroup.class, region);
            if (Objects.nonNull(userPriceGroup)) {
                guestCustomer.setEurope1PriceFactory_UPG(userPriceGroup);
            }
        }

        getCustomerAccountService().registerGuestForAnonymousCheckout(guestCustomer, guid);
        updateCartWithGuestForAnonymousCheckout(getCustomerConverter().convert(guestCustomer));
    }

    public EnumerationService getEnumerationService() {
        return enumerationService;
    }

    public void setEnumerationService(EnumerationService enumerationService) {
        this.enumerationService = enumerationService;
    }

    @Override
    public void updateEmail(String uid, String email) {

        final CustomerModel customerModel = (CustomerModel) userService.getUserForUID(uid);
        customerModel.setDetailEmail(email);

        modelService.save(customerModel);
    }
}
