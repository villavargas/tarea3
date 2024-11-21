/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercewebservices.core.v2.controller;

import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercewebservicescommons.annotation.SiteChannelRestriction;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.CartException;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.europe1.enums.UserPriceGroup;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdUserIdAndCartIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;

import java.util.Objects;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import mx.com.telcel.core.services.TelcelCodigosPostalesService;
import mx.com.telcel.core.util.TelcelUtil;
import mx.com.telcel.facades.zipcodes.data.CodigosPostalesTelcelData;


@Controller
@RequestMapping(value = "/{baseSiteId}/users/{userId}/carts")
@CacheControl(directive = CacheControlDirective.NO_CACHE)
@Api(tags = "Cart Addresses")
public class CartAddressesController extends BaseCommerceController
{
	private static final Logger LOG = LoggerFactory.getLogger(CartAddressesController.class);

	private static final String ADDRESS_MAPPING = "firstName,lastName,titleCode,phone,line1,line2,town,postalCode,country(isocode),region(isocode),defaultAddress,district,externalNumber,interiorNumber,references";
	private static final String OBJECT_NAME_ADDRESS = "address";
	private static final String DASH = "-";

	@Resource(name = "telcelAddressWsDTOValidator")
	private Validator telcelAddressWsDTOValidator;

	@Resource
	private TelcelUtil telcelUtil; // NOSONAR

	@Resource(name = "telcelCodigosPostalesService")
	private TelcelCodigosPostalesService telcelCodigosPostalesService;

	@Resource
	private UserService userService;

	@Resource
	private EnumerationService enumerationService;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_GUEST", "ROLE_TRUSTED_CLIENT" })
	@PostMapping(value = "/{cartId}/addresses/delivery", consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	@ApiOperation(nickname = "createCartDeliveryAddress", value = "Creates a delivery address for the cart.", notes = "Creates an address and assigns it to the cart as the delivery address.")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public AddressWsDTO createCartDeliveryAddress(
			@ApiParam(value = "Request body parameter that contains details such as the customer's first name (firstName), the customer's last name (lastName), the customer's title (titleCode), the customer's phone (phone), "
					+ "the country (country.isocode), the first part of the address (line1), the second part of the address (line2), the town (town), the postal code (postalCode), and the region (region.isocode).\n\nThe DTO is in XML or .json format.", required = true)
			@RequestBody
			final AddressWsDTO address, @ApiFieldsParam
			@RequestParam(defaultValue = DEFAULT_FIELD_SET)
			final String fields, final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
	{

		if (StringUtils.isEmpty(address.getTitleCode()))
		{
			address.setTitleCode("mr");
		}
		LOG.debug("createCartDeliveryAddress");
		validate(address, "telcelAddressWsDTOValidator", telcelAddressWsDTOValidator);
		AddressData addressData = getDataMapper().map(address, AddressData.class, ADDRESS_MAPPING);
		//ValidateCP
		setRegion(httpServletRequest, httpServletResponse, addressData.getPostalCode());

		addressData = createAddressInternal(addressData);
		setCartDeliveryAddressInternal(addressData.getId());
		return getDataMapper().map(addressData, AddressWsDTO.class);
	}

	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@PutMapping(value = "/{cartId}/addresses/delivery")
	@ResponseStatus(HttpStatus.OK)
	@SiteChannelRestriction(allowedSiteChannelsProperty = API_COMPATIBILITY_B2C_CHANNELS)
	@ApiOperation(nickname = "replaceCartDeliveryAddress", value = "Sets a delivery address for the cart.", notes = "Sets a delivery address for the cart. The address country must be placed among the delivery countries of the current base store.")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public void replaceCartDeliveryAddress(@ApiParam(value = "Address identifier", required = true)
	@RequestParam
	final String addressId, final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
	{
		final CartData cartData = setCartDeliveryAddressInternal(addressId);
		setRegion(httpServletRequest, httpServletResponse, cartData.getDeliveryAddress().getPostalCode());
	}

	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@DeleteMapping(value = "/{cartId}/addresses/delivery")
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(nickname = "removeCartDeliveryAddress", value = "Deletes the delivery address from the cart.", notes = "Deletes the delivery address from the cart.")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public void removeCartDeliveryAddress()
	{
		LOG.debug("removeCartDeliveryAddress");
		if (!getCheckoutFacade().removeDeliveryAddress())
		{
			throw new CartException("Cannot reset address!", CartException.CANNOT_RESET_ADDRESS);
		}
	}

	private void setRegion(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse,
			final String codigoPostal)
	{
		final String cookie = telcelUtil.getCookieByRequest(httpServletRequest, "tlsr");
		if (StringUtils.isNotEmpty(cookie))
		{
			final String region = cookie.split(DASH)[0];
			final String postalCode = cookie.split(DASH)[1];

			if (!postalCode.equalsIgnoreCase(codigoPostal))
			{
				final CodigosPostalesTelcelData codigosPostalesTelcelData = telcelCodigosPostalesService
						.getInfForZipcode(codigoPostal);
				if (!codigosPostalesTelcelData.getRegion().getCode().equals(region))
				{
					//Cambiarle Region al usuario
					final CustomerModel customerModel = (CustomerModel) userService.getCurrentUser();
					final UserPriceGroup userPriceGroup = enumerationService.getEnumerationValue(UserPriceGroup.class,
							codigosPostalesTelcelData.getRegion().getCode());
					LOG.info("Codigo de cliente: " + customerModel.getUid());
					if (Objects.nonNull(customerModel) && !userService.isAnonymousUser(customerModel))
					{
						customerModel.setEurope1PriceFactory_UPG(userPriceGroup);
						modelService.save(customerModel);
					}
					httpServletResponse.setHeader("Set-Cookie",
							"tlsr=" + userPriceGroup.getCode() + DASH + codigosPostalesTelcelData.getCodigo() + "; Path=/; Secure");
				}
			}
		}
	}
}
