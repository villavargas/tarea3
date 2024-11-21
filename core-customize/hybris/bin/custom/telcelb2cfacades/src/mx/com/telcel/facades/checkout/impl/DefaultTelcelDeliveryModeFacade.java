/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.facades.checkout.impl;

import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeModel;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeValueModel;
import de.hybris.platform.order.ZoneDeliveryModeService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.util.Config;
import mx.com.telcel.facades.checkout.TelcelDeliveryModeFacade;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The type Default telcel delivery mode facade.
 */
public class DefaultTelcelDeliveryModeFacade implements TelcelDeliveryModeFacade {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultTelcelDeliveryModeFacade.class);

    private static final String AMIGO_CHIPCLASSIFICATION = "chip.amigo.chipclassification";
    private static final String CHIP_ZONEDELIVERY = "chip.amigo.zone.delivery";
    private static final String INITIAL_DELIVERY_COSTS_ZONES = "initial.delivery.costs.zones";

    private ModelService modelService;
    private CommerceCartService commerceCartService;
    private BaseSiteService baseSiteService;
    private Converter<CartModel, CartData> cartConverter;
    private UserService userService;
    private ZoneDeliveryModeService zoneDeliveryModeService;


    @Override
    public Optional<CartData> getCartAndSetDeliveryModeByCartID(String guid) {
        final CartModel cartModel = getCommerceCartService().getCartForGuidAndSite(guid, getBaseSiteService().getCurrentBaseSite());
        if (cartModel == null) {
            return Optional.empty();
        }
        setDeliveryMode(cartModel);
        return cartModel == null ? Optional.empty() : Optional.of(getCartConverter().convert(cartModel));
    }

    @Override
    public Optional<CartData> getCartAndSetDeliveryModeByCartIdAndCustomer(String code, String customerId) {
        final UserModel user = getUserService().getUserForUID(customerId);
        final CartModel cartModel = getCommerceCartService().getCartForCodeAndUser(code, user);
        if (cartModel == null) {
            return Optional.empty();
        }
        setDeliveryMode(cartModel);
        return Optional.of(getCartConverter().convert(cartModel));
    }


    private void setDeliveryMode(CartModel cartModel) {
        final CurrencyModel currency = cartModel.getCurrency();
        double totalChipExpress = 0D;
        boolean allChips = true;
        boolean havechips = false;
        boolean isDeliveryZero = true;
        List<AbstractOrderEntryModel> abstractOrderEntryModels = cartModel.getEntries();
        Map<ZoneDeliveryModeModel, Double> zoneDeliveryModeModels = new HashMap<>();
        for (AbstractOrderEntryModel entryModel : abstractOrderEntryModels) {
            ZoneDeliveryModeModel zoneDeliveryModeModel = entryModel.getProduct().getZoneDelivery();
            if (Objects.nonNull(zoneDeliveryModeModel)) {
                Set<ZoneDeliveryModeValueModel> zoneDeliveryModeValueModels = zoneDeliveryModeModel.getValues();
                if (CollectionUtils.isNotEmpty(zoneDeliveryModeValueModels)) {
                    for (ZoneDeliveryModeValueModel zoneValue : zoneDeliveryModeValueModels) {
                        ProductModel product = entryModel.getProduct();
                        if (product.getClassificationClasses().iterator().next().getCode().equals(Config.getParameter(AMIGO_CHIPCLASSIFICATION))) {
                            totalChipExpress += entryModel.getTotalPrice();
                            LOG.info(String.valueOf(totalChipExpress));
                            zoneDeliveryModeModels.put(zoneDeliveryModeModel, zoneValue.getValue());
                            havechips = true;
                        } else {
                            allChips = false;
                            if (zoneValue.getCurrency().equals(currency)) {
                                if (zoneValue.getValue() > 0D) {
                                    isDeliveryZero = false;
                                }
                                zoneDeliveryModeModels.put(zoneDeliveryModeModel, zoneValue.getValue());
                                break;
                            }
                        }
                    }
                } else {
                    zoneDeliveryModeModels.put(zoneDeliveryModeModel, 0D);
                }
            }
        }

        if ((allChips && totalChipExpress >= 300D) || (havechips && !allChips && isDeliveryZero)) {
            final DeliveryModeModel chipCost = zoneDeliveryModeService.getDeliveryModeForCode(Config.getParameter(CHIP_ZONEDELIVERY));
            ZoneDeliveryModeModel zoneDeliveryModeModelChip = (ZoneDeliveryModeModel) chipCost;
            zoneDeliveryModeModels = new HashMap<>();
            Set<ZoneDeliveryModeValueModel> zoneDeliveryModeValueModelsChip = zoneDeliveryModeModelChip.getValues();
            for (ZoneDeliveryModeValueModel zoneValueChip : zoneDeliveryModeValueModelsChip) {
                zoneDeliveryModeModels.put(zoneDeliveryModeModelChip, zoneValueChip.getValue());
            }
        }

         if (zoneDeliveryModeModels.size() > 0) {
            List<Map.Entry<ZoneDeliveryModeModel, Double>> sorted =
                    zoneDeliveryModeModels.entrySet().stream()
                            .sorted(Map.Entry.comparingByValue()).collect(Collectors.toList());
            cartModel.setDeliveryMode(sorted.get(zoneDeliveryModeModels.size() - 1).getKey());
            cartModel.setDeliveryCost(sorted.get(zoneDeliveryModeModels.size() - 1).getValue());
            getModelService().save(cartModel);
            recalculateCart(cartModel);
        } else {
            LOG.debug(String.format("Alert - Cart with code %s doesnt have delivery modes in products", cartModel.getCode()));
        }
    }

    private void recalculateCart(CartModel cartModel) {
        try {
            final CommerceCartParameter recalcParam = new CommerceCartParameter();
            recalcParam.setEnableHooks(true);
            recalcParam.setCart(cartModel);
            getCommerceCartService().recalculateCart(recalcParam);
            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("Cart %s has been recalculated", cartModel.toString()));
            }
        } catch (final CalculationException e) {
            LOG.error(String.format("Error recalculating cart: %s", e.getMessage()));
        }
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

    /**
     * Gets commerce cart service.
     *
     * @return the commerce cart service
     */
    public CommerceCartService getCommerceCartService() {
        return commerceCartService;
    }

    /**
     * Sets commerce cart service.
     *
     * @param commerceCartService the commerce cart service
     */
    public void setCommerceCartService(CommerceCartService commerceCartService) {
        this.commerceCartService = commerceCartService;
    }

    /**
     * Gets cart converter.
     *
     * @return the cart converter
     */
    public Converter<CartModel, CartData> getCartConverter() {
        return cartConverter;
    }

    /**
     * Sets cart converter.
     *
     * @param cartConverter the cart converter
     */
    public void setCartConverter(Converter<CartModel, CartData> cartConverter) {
        this.cartConverter = cartConverter;
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
     * Gets base site service.
     *
     * @return the base site service
     */
    public BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    /**
     * Sets base site service.
     *
     * @param baseSiteService the base site service
     */
    public void setBaseSiteService(BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }

    /**
     * Gets zone delivery mode service.
     *
     * @return the zone delivery mode service
     */
    public ZoneDeliveryModeService getZoneDeliveryModeService() {
        return zoneDeliveryModeService;
    }

    /**
     * Sets zone delivery mode service.
     *
     * @param zoneDeliveryModeService the zone delivery mode service
     */
    public void setZoneDeliveryModeService(ZoneDeliveryModeService zoneDeliveryModeService) {
        this.zoneDeliveryModeService = zoneDeliveryModeService;

    }
}
