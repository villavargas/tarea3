/*
 *  Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

package mx.com.telcel.controllers;

import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.request.mapping.annotation.RequestMappingOverride;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.util.List;


/**
 * Web Service Controller for the Address resource
 *
 * @since 2007
 */
@Controller
@RequestMapping(value = "/{baseSiteId}/users/{userId}/addresses")
@CacheControl(directive = CacheControlDirective.PRIVATE)
@Api(tags = "Address")
public class TmaAddressController extends BaseController
{
	public static final String ADDRESS_DOES_NOT_EXIST = "Address with given id: '%s' doesn't exist or belong to another user";
	private static final String ADDRESS_MAPPING = "firstName,lastName,titleCode,phone,cellphone,line1,line2,town,postalCode,region(isocode),district,country(isocode),defaultAddress,installationAddress,building,apartment,externalNumber,interiorNumber,references";
	private static final String OBJECT_NAME_ADDRESS = "address";
	private static final String OBJECT_NAME_ADDRESS_ID = "addressId";

	//	@Resource(name = "addressValidator")
	//	private Validator addressValidator;
	//@Resource(name = "addressDTOValidator")
	//private Validator addressDTOValidator;
	@Resource(name = "userFacade")
	private UserFacade userFacade;

	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(method = RequestMethod.POST, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@RequestMappingOverride(priorityProperty = "telb2cocc.TmaAddressController.createAddress.priority")
	@ResponseBody
	@ResponseStatus(value = HttpStatus.CREATED)
	@ApiOperation(nickname = "createAddress", value = "Creates a new address.", notes = "Creates a new address.")
	@ApiBaseSiteIdAndUserIdParam
	public AddressWsDTO createAddress(@ApiParam(value = "Address object.", required = true)
	@RequestBody
	final AddressWsDTO address, @ApiFieldsParam
	@RequestParam(defaultValue = DEFAULT_FIELD_SET)
	final String fields)
	{
		//	validate(address, OBJECT_NAME_ADDRESS, getAddressDTOValidator());
		final AddressData addressData = getDataMapper().map(address, AddressData.class, ADDRESS_MAPPING);
		List<AddressData> addressDataList = userFacade.getAddressBook();
		if (addressDataList.size() == 0) {
			addressData.setDefaultAddress(Boolean.TRUE);
		}
		addressData.setShippingAddress(true);
		addressData.setVisibleInAddressBook(true);
		userFacade.addAddress(addressData);
		if (addressData.isDefaultAddress())
		{
			userFacade.setDefaultAddress(addressData);
		}

		return getDataMapper().map(addressData, AddressWsDTO.class);
	}

	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{addressId}", method = RequestMethod.PATCH, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@RequestMappingOverride(priorityProperty = "telb2cocc.TmaAddressController.updateAddress.priority")
	@ApiOperation(nickname = "updateAddress", value = "Updates the address", notes = "Updates the address. Only attributes provided in the request body will be changed.")
	@ApiBaseSiteIdAndUserIdParam
	@ResponseStatus(HttpStatus.OK)
	public void updateAddress(@ApiParam(value = "Address identifier.", required = true)
	@PathVariable
	final String addressId, @ApiParam(value = "Address object", required = true)
	@RequestBody
	final AddressWsDTO newAddress)
	{
		final AddressData databaseAddress = getAddressData(addressId);
		final boolean isAlreadyDefaultAddress = databaseAddress.isDefaultAddress();

		prepareRegionForDeletion(newAddress, databaseAddress);

		getDataMapper().map(newAddress, databaseAddress, ADDRESS_MAPPING, false);
		//	validate(databaseAddress, OBJECT_NAME_ADDRESS, getAddressValidator());

		if (databaseAddress.getId().equals(userFacade.getDefaultAddress().getId()))
		{
			databaseAddress.setDefaultAddress(true);
			databaseAddress.setVisibleInAddressBook(true);
		}
		if (!isAlreadyDefaultAddress && databaseAddress.isDefaultAddress())
		{
			userFacade.setDefaultAddress(databaseAddress);
		}
		userFacade.editAddress(databaseAddress);
	}

	private void prepareRegionForDeletion(final AddressWsDTO newAddress, final AddressData databaseAddress)
	{
		if (newAddress.getRegion() == null && databaseAddress.getRegion() != null)
		{
			databaseAddress.setRegion(null);
		}
	}

	private AddressData getAddressData(final String addressId)
	{
		final AddressData addressData = userFacade.getAddressForCode(addressId);
		if (addressData == null)
		{
			throw new RequestParameterException(String.format(ADDRESS_DOES_NOT_EXIST, sanitize(addressId)),
					RequestParameterException.INVALID, OBJECT_NAME_ADDRESS_ID);
		}
		return addressData;
	}

	/*
	 * protected Validator getAddressValidator() { return addressValidator; }
	 *
	 * protected Validator getAddressDTOValidator() { return addressDTOValidator; }
	 */
}