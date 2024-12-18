/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorocc.controllers;

import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceDataList;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commercefacades.order.data.CartModificationDataList;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping(value = "/{baseSiteId}/cart")
@ApiVersion("v1")
public class ExtendedCartController
{
	@Resource(name = "acceleratorCheckoutFacade")
	private AcceleratorCheckoutFacade acceleratorCheckoutFacade;

	/**
	 * Web service handler for getting consolidated pickup options<br>
	 * Sample target URL : http://localhost:9001/rest/v1/cart/consolidatedOptions<br>
	 * Request Method = <code>GET</code>
	 *
	 * @return {@link PointOfServiceDataList} as response body
	 */
	@RequestMapping(value = "/consolidate", method = RequestMethod.GET)
	@ResponseBody
	public PointOfServiceDataList getConsolidatedPickupOptions()
	{
		final PointOfServiceDataList pointOfServices = new PointOfServiceDataList();
		pointOfServices.setPointOfServices(acceleratorCheckoutFacade.getConsolidatedPickupOptions());
		return pointOfServices;
	}

	/**
	 * Web service handler for consolidating pickup locations<br>
	 * Sample target URL : http://localhost:9001/rest/v1/cart/consolidate?storeName=Encoded%20Store%20Name<br>
	 * Request Method = <code>POST</code>
	 *
	 * @param storeName
	 * 		- name of store where items will be picked
	 * @return {@link CartModificationDataList} as response body
	 */
	@RequestMapping(value = "/consolidate", method = RequestMethod.POST)
	@ResponseBody
	public CartModificationDataList consolidatePickupLocations(@RequestParam(required = true) final String storeName)
			throws CommerceCartModificationException
	{
		final CartModificationDataList modifications = new CartModificationDataList();
		modifications.setCartModificationList(acceleratorCheckoutFacade.consolidateCheckoutCart(storeName));
		return modifications;
	}
}
