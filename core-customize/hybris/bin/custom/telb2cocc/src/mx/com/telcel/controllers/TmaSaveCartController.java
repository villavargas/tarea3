/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.controllers;

import de.hybris.platform.b2ctelcofacades.data.CartActionInput;
import de.hybris.platform.b2ctelcofacades.order.TmaCartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.CommerceSaveCartResultData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceSaveCartException;
import de.hybris.platform.commerceservices.request.mapping.annotation.RequestMappingOverride;
import de.hybris.platform.commercewebservicescommons.dto.order.SaveCartResultWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.CartException;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;

import java.util.Collections;
import java.util.Optional;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


/**
 * Controller for saved cart related requests such as saving a cart or cloning/restoring a saved cart
 *
 * @since 1911
 */
@Controller
@RequestMapping(value = "/{baseSiteId}/users/{userId}/carts")
@CacheControl(directive = CacheControlDirective.NO_CACHE)
@Api(tags = "Save Cart")
public class TmaSaveCartController extends BaseController
{
	protected static final String ANONYMOUS = "anonymous";

	@Resource(name = "tmaCartFacade")
	private TmaCartFacade tmaCartFacade;

	@RequestMapping(value = "/{cartId}/clonesavedcart", method = RequestMethod.POST)
	@RequestMappingOverride(priorityProperty = "telb2cocc.TmaCartsController.cloneCart.priority")
	@ResponseBody
	@ApiOperation(nickname = "doCartClone", value = "Explicitly clones a cart.", notes = "Explicitly clones a cart.")
	@ApiBaseSiteIdAndUserIdParam
	public SaveCartResultWsDTO cloneCart(
			@ApiParam(value = "Cart identifier: cart code for logged in user, cart guid for anonymous user, 'current' for the last modified cart", required = true) @PathVariable final String cartId,
			@ApiParam(value = "The name that should be applied to the cloned cart.") @RequestParam(value = "name", required = false) final String name,
			@ApiParam(value = "Identifier of the Customer", required = true) @PathVariable("userId") final String userId,
			@ApiParam(value = "The description that should be applied to the cloned cart.") @RequestParam(value = "description", required = false) final String description,
			@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) throws CommerceSaveCartException
	{
		if (StringUtils.isEmpty(cartId))
		{
			throw new CommerceSaveCartException("Cart code cannot be empty");
		}

		final CartActionInput cartActionInput = new CartActionInput();
		cartActionInput.setUserGuid(userId);
		if (userId.equalsIgnoreCase(ANONYMOUS))
		{
			cartActionInput.setToCartGUID(cartId);
		}
		else
		{
			cartActionInput.setCloneCartID(cartId);
		}
		cartActionInput.setCloneCartDescription(description);
		cartActionInput.setCloneCartName(name);
		try
		{
			tmaCartFacade.validateAndUpdateCartActionInputForClone(cartActionInput);
			final CartModificationData cartModificationData = tmaCartFacade
					.processCartAction(Collections.singletonList(cartActionInput)).get(0);

			final CommerceSaveCartResultData result = new CommerceSaveCartResultData();
			final Optional<CartData> cartData = tmaCartFacade.getCartForCodeAndCustomer(cartModificationData.getClonedCartId(), userId);
			if (cartData.isPresent())
			{
				result.setSavedCartData(cartData.get());
			}
			return getDataMapper().map(result, SaveCartResultWsDTO.class, fields);
		}
		catch (final CommerceCartModificationException e)
		{
			throw new CartException("Couldn't clone cart " + cartId, CartException.INVALID, e);
		}
	}

	@RequestMapping(value = "/{cartId}/restoresavedcart", method = RequestMethod.PATCH)
	@RequestMappingOverride(priorityProperty = "telb2cocc.TmaSaveCartController.doUpdateSavedCart.priority")
	@ResponseBody
	@ApiOperation(nickname = "doUpdateSavedCart", value = "Restore a saved cart.", notes = "Restore a saved cart.")
	@ApiBaseSiteIdAndUserIdParam
	public SaveCartResultWsDTO doUpdateSavedCart(
			@ApiParam(value = "Cart identifier: cart code for logged in user, cart guid for anonymous user, 'current' for the last modified cart", required = true) @PathVariable final String cartId,
			@ApiParam(value = "Identifier of the Customer", required = true) @PathVariable("userId") final String userId,
			@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) throws CommerceSaveCartException
	{
		if (StringUtils.isEmpty(cartId))
		{
			throw new CommerceSaveCartException("Cart code cannot be empty");
		}
		final CartActionInput cartActionInput = new CartActionInput();
		cartActionInput.setCartId(cartId);
		cartActionInput.setRestoreCart(true);
		cartActionInput.setUserGuid(userId);
		try
		{
			tmaCartFacade.processCartAction(Collections.singletonList(cartActionInput));
			final CommerceSaveCartResultData result = new CommerceSaveCartResultData();
			result.setSavedCartData(tmaCartFacade.getSessionCart());
			return getDataMapper().map(result, SaveCartResultWsDTO.class, fields);
		}
		catch (final CommerceCartModificationException e)
		{
			throw new CartException("Cart " + cartId + "Cannot be Restore", CartException.INVALID, e);
		}
	}
}
