/*
 *
 * [y] hybris Platform
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package mx.com.telcel.controllers;

import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commercewebservicescommons.dto.order.CartAddressRespWsDTO;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import mx.com.telcel.dto.CartAddressWsDTO;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Objects;

@Controller
@RequestMapping(value = "/cart")
@CacheControl(directive = CacheControlDirective.NO_STORE)
@Api(tags = "Cart Controller")
public class CartController {
    private static final Logger LOG = Logger.getLogger(CartController.class);

    @Resource(name = "baseStoreService")
    private BaseStoreService baseStoreService;

    @Resource
    private BaseSiteService baseSiteService;

    @Resource
    private CommerceCartService commerceCartService;

    @Resource(name = "modelService")
    private ModelService modelService;


    @RequestMapping(value = "/addip", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> addip(@ApiParam(value = "Cart Address object.", required = true) @RequestBody final CartAddressWsDTO cartAddressWsDTO){

        LOG.info("CartController.addip guid["+cartAddressWsDTO.getGuid()+"] / ip ["+cartAddressWsDTO.getIp()+"]");

        BaseStoreModel baseStoreModel = getBaseStoreModel();

        if (Objects.nonNull(baseStoreModel)) {
            final CartModel cartModel = getCommerceCartService().getCartForGuidAndSite(cartAddressWsDTO.getGuid(), getBaseSiteService().getBaseSiteForUID("telcel"));
            if (Objects.isNull(cartModel)) {
                LOG.info("Order Dont Exist");
                return null;
            }

            cartModel.setIp(cartAddressWsDTO.getIp());
            cartModel.setSessionIdCibersource(cartAddressWsDTO.getSessionId());
            modelService.save(cartModel);
        }

        CartAddressRespWsDTO cartAddressRespWsDTO = new CartAddressRespWsDTO();
        cartAddressRespWsDTO.setStatus("OK");
        return new ResponseEntity<>(cartAddressRespWsDTO, HttpStatus.ACCEPTED);

    }

    private BaseStoreModel getBaseStoreModel() {
        BaseStoreModel baseStoreModel = baseStoreService.getCurrentBaseStore();
        if (Objects.isNull(baseStoreModel)) {
            baseStoreModel = baseStoreService.getBaseStoreForUid("telcel");
        }
        return baseStoreModel;
    }

    public BaseStoreService getBaseStoreService() {
        return baseStoreService;
    }

    public void setBaseStoreService(BaseStoreService baseStoreService) {
        this.baseStoreService = baseStoreService;
    }

    public BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    public void setBaseSiteService(BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }

    public CommerceCartService getCommerceCartService() {
        return commerceCartService;
    }

    public void setCommerceCartService(CommerceCartService commerceCartService) {
        this.commerceCartService = commerceCartService;
    }
}
