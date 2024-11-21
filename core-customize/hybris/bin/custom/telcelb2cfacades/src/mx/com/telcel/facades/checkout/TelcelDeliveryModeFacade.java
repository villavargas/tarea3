/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.facades.checkout;

import de.hybris.platform.commercefacades.order.data.CartData;

import java.util.Optional;

/**
 * The interface Telcel delivery mode facade.
 */
public interface TelcelDeliveryModeFacade {

    /**
     * Sets delivery mode by cart id.
     *
     * @param cartCode the cart code
     * @return the delivery mode by cart id
     */
    Optional<CartData> getCartAndSetDeliveryModeByCartID(String cartCode);

    /**
     * Sets delivery mode by cart id and customer.
     *
     * @param cartCode   the cart code
     * @param customerId the customer id
     * @return the delivery mode by cart id and customer
     */
    Optional<CartData> getCartAndSetDeliveryModeByCartIdAndCustomer(String cartCode, String customerId);
}
