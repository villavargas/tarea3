/*
 *
 * [y] hybris Platform
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.commercewebservices.core.form;


public class PaymentDetails {

    PaymenDetails paymenDetails;

    public PaymenDetails getPaymenDetails() {
        return paymenDetails;
    }

    public void setPaymenDetails(PaymenDetails paymenDetails) {
        this.paymenDetails = paymenDetails;
    }
}
