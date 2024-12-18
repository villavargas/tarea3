/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercewebservices.core.v2.controller;

import de.hybris.platform.commercefacades.address.AddressVerificationFacade;
import de.hybris.platform.commercefacades.address.data.AddressVerificationResult;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.address.AddressVerificationDecision;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commercewebservices.core.swagger.ApiBaseSiteIdAndUserIdAndAddressParams;
import de.hybris.platform.commercewebservices.core.user.data.AddressDataList;
import de.hybris.platform.commercewebservices.core.validation.data.AddressValidationData;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressValidationWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;


@Controller
@RequestMapping(value = "/{baseSiteId}/users/{userId}/addresses")
@CacheControl(directive = CacheControlDirective.PRIVATE)
@Api(tags = "Address")
public class AddressController extends BaseCommerceController
{
	public static final String ADDRESS_DOES_NOT_EXIST = "Address with given id: '%s' doesn't exist or belong to another user";
	private static final Logger LOG = LoggerFactory.getLogger(AddressController.class);

	private static final String ADDRESS_MAPPING = "firstName,lastName,titleCode,email,phone,cellphone,line1,line2,town,postalCode,region(isocode),district,country(isocode),defaultAddress,externalNumber,interiorNumber,references";
	private static final String OBJECT_NAME_ADDRESS = "address";
	private static final String OBJECT_NAME_ADDRESS_ID = "addressId";
	private static final String OBJECT_NAME_ADDRESS_DATA = "addressData";

	@Resource(name = "addressVerificationFacade")
	private AddressVerificationFacade addressVerificationFacade;
	@Resource(name = "addressDataErrorsPopulator")
	private Populator<AddressVerificationResult<AddressVerificationDecision>, Errors> addressDataErrorsPopulator;
	@Resource(name = "validationErrorConverter")
	private Converter<Object, List<ErrorWsDTO>> validationErrorConverter;

	@Resource(name = "telcelAddressWsDTOValidator")
	private Validator telcelAddressWsDTOValidator;

	@Resource(name = "commerceCartService")
	private CommerceCartService commerceCartService;

	@Resource(name = "addressConverter")
	private Converter<AddressModel, AddressData> addressConverter;

	@Resource
	private BaseSiteService baseSiteService;

	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(nickname = "getAddresses", value = "Get customer's addresses", notes = "Returns customer's addresses.")
	@ApiBaseSiteIdAndUserIdParam
	@ApiResponse(code = 200, message = "List of customer's addresses")
	public AddressListWsDTO getAddresses(@ApiFieldsParam
	@RequestParam(defaultValue = DEFAULT_FIELD_SET)
	final String fields)
	{
		final List<AddressData> addressList = getUserFacade().getAddressBook();
		final AddressDataList addressDataList = new AddressDataList();
		addressDataList.setAddresses(addressList);
		return getDataMapper().map(addressDataList, AddressListWsDTO.class);
	}

	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(method = RequestMethod.GET, value = "/validateBillingAddress/{guid}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@ApiBaseSiteIdAndUserIdParam
	@ApiOperation(nickname = "validateAddressBilling", value = "Validate Address billing.", notes = "Validate Address billing.", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public AddressWsDTO validateAddressBilling(@ApiParam(value = "Guid ID", required = true)
	@PathVariable
	final String guid, @ApiParam(value = "Base site identifier", required = true)
	@PathVariable
	final String baseSiteId)
	{
		if (StringUtils.isNotEmpty(guid))
		{
			final CartModel cartModel = getCartForUserByGUID(guid, baseSiteId);
			if (cartModel != null)
			{
				final AddressModel addressModel = cartModel.getBillingAddress();
				if (addressModel != null)
				{
					return getDataMapper().map(addressConverter.convert(addressModel), AddressWsDTO.class);
				}
			}
		}
		return null;
	}

	/**
	 * @deprecated since 2005. Please use {@link AddressController#createAddress(AddressWsDTO, String)} instead.
	 */
	@Deprecated(since = "2005", forRemoval = true)
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.CREATED)
	@ApiOperation(hidden = true, value = "Creates a new address.", notes = "Creates a new address.")
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "baseSiteId", value = "Base site identifier", required = true, dataType = "String", paramType = "path"),
			@ApiImplicitParam(name = "userId", value = "User identifier", required = true, dataType = "String", paramType = "path"),
			@ApiImplicitParam(name = "firstName", value = "Customer's first name", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "lastName", value = "Customer's last name", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "email", value = "Customer's email", dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "titleCode", value = "Customer's title code. Customer's title code. For a list of codes, see /{baseSiteId}/titles resource", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "country.isocode", value = "Country isocode. This parameter is required and have influence on how rest of parameters are validated (e.g. if parameters are required : line1,line2,town,postalCode,region.isocode)", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "line1", value = "First part of address. If this parameter is required depends on country (usually it is required).", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "line2", value = "Second part of address. If this parameter is required depends on country (usually it is not required)", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "town", value = "Town name or city isocode. If this parameter is required depends on country (usually it is required)", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "postalCode", value = "Postal code. Isocode for region. If this parameter is required depends on country.", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "region.isocode", value = "Second part of address. If this parameter is required depends on country (usually it is not required)", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "district", value = "District isocode. If this parameter is required depends on country (usually it is required)", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "cellphone", value = "Cellphone number. If this parameter is required depends on country (usually it is required)", required = false, dataType = "String", paramType = "query") })
	public AddressWsDTO createAddress(final HttpServletRequest request, @ApiFieldsParam
	@RequestParam(defaultValue = DEFAULT_FIELD_SET)
	final String fields)
	{
		final AddressData addressData = super.createAddressInternal(request);
		return getDataMapper().map(addressData, AddressWsDTO.class, fields);
	}

	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(method = RequestMethod.POST, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
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
		validate(address, OBJECT_NAME_ADDRESS, getAddressDTOValidator());
		final AddressData addressData = getDataMapper().map(address, AddressData.class, ADDRESS_MAPPING);
		addressData.setShippingAddress(true);
		addressData.setVisibleInAddressBook(true);

		getUserFacade().addAddress(addressData);
		if (addressData.isDefaultAddress())
		{
			getUserFacade().setDefaultAddress(addressData);
		}

		return getDataMapper().map(addressData, AddressWsDTO.class, fields);
	}

	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{addressId}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(nickname = "getAddress", value = "Get info about address", notes = "Returns detailed information about address with a given id.")
	@ApiBaseSiteIdAndUserIdParam
	public AddressWsDTO getAddress(@ApiParam(value = "Address identifier.", required = true)
	@PathVariable
	final String addressId, @ApiFieldsParam
	@RequestParam(defaultValue = DEFAULT_FIELD_SET)
	final String fields)
	{
		LOG.debug("getAddress: id={}", sanitize(addressId));
		final AddressData addressData = getAddressData(addressId);
		return getDataMapper().map(addressData, AddressWsDTO.class, fields);
	}

	/**
	 * @deprecated since 2005. Please use {@link AddressController#replaceAddress(String, AddressWsDTO)} instead.
	 */
	@Deprecated(since = "2005", forRemoval = true)
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{addressId}", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(hidden = true, value = "Updates the address", notes = "Updates the address. Attributes not provided in the request will be defined again (set to null or default).")
	@ApiBaseSiteIdAndUserIdAndAddressParams
	public void replaceAddress(@ApiParam(value = "Address identifier.", required = true)
	@PathVariable
	final String addressId, final HttpServletRequest request)
	{
		LOG.debug("editAddress: id={}", sanitize(addressId));
		final AddressData addressData = getAddressData(addressId);
		final boolean isAlreadyDefaultAddress = addressData.isDefaultAddress();
		addressData.setFirstName(null);
		addressData.setLastName(null);
		addressData.setEmail(null);
		addressData.setCountry(null);
		addressData.setLine1(null);
		addressData.setLine2(null);
		addressData.setPostalCode(null);
		addressData.setRegion(null);
		addressData.setDistrict(null);
		addressData.setTitle(null);
		addressData.setTown(null);
		addressData.setDefaultAddress(false);
		addressData.setFormattedAddress(null);

		getHttpRequestAddressDataPopulator().populate(request, addressData);

		final Errors errors = new BeanPropertyBindingResult(addressData, OBJECT_NAME_ADDRESS_DATA);
		getAddressValidator().validate(addressData, errors);

		if (errors.hasErrors())
		{
			throw new WebserviceValidationException(errors);
		}
		getUserFacade().editAddress(addressData);

		if (!isAlreadyDefaultAddress && addressData.isDefaultAddress())
		{
			getUserFacade().setDefaultAddress(addressData);
		}
	}

	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{addressId}", method = RequestMethod.PUT, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(nickname = "replaceAddress", value = "Updates the address", notes = "Updates the address. Attributes not provided in the request will be defined again (set to null or default).")
	@ApiBaseSiteIdAndUserIdParam
	public void replaceAddress(@ApiParam(value = "Address identifier.", required = true)
	@PathVariable
	final String addressId, @ApiParam(value = "Address object.", required = true)
	@RequestBody
	final AddressWsDTO address)
	{
		validate(address, OBJECT_NAME_ADDRESS, getAddressDTOValidator());
		final AddressData addressData = getAddressData(addressId);
		final boolean isAlreadyDefaultAddress = addressData.isDefaultAddress();
		addressData.setFormattedAddress(null);
		getDataMapper().map(address, addressData, ADDRESS_MAPPING, true);

		getUserFacade().editAddress(addressData);

		if (!isAlreadyDefaultAddress && addressData.isDefaultAddress())
		{
			getUserFacade().setDefaultAddress(addressData);
		}
	}

	/**
	 * @deprecated since 2005. Please use {@link AddressController#updateAddress(String, AddressWsDTO)} instead.
	 */
	@Deprecated(since = "2005", forRemoval = true)
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{addressId}", method = RequestMethod.PATCH)
	@ApiOperation(hidden = true, value = "Updates the address", notes = "Updates the address. Only attributes provided in the request body will be changed.")
	@ApiBaseSiteIdAndUserIdAndAddressParams
	@ResponseStatus(HttpStatus.OK)
	public void updateAddress(@ApiParam(value = "Address identifier.", required = true)
	@PathVariable
	final String addressId, final HttpServletRequest request)
	{
		LOG.debug("editAddress: id={}", sanitize(addressId));
		final AddressData addressData = getAddressData(addressId);
		final boolean isAlreadyDefaultAddress = addressData.isDefaultAddress();
		addressData.setFormattedAddress(null);
		final Errors errors = new BeanPropertyBindingResult(addressData, OBJECT_NAME_ADDRESS_DATA);

		getHttpRequestAddressDataPopulator().populate(request, addressData);
		getAddressValidator().validate(addressData, errors);

		if (errors.hasErrors())
		{
			throw new WebserviceValidationException(errors);
		}

		if (addressData.getId().equals(getUserFacade().getDefaultAddress().getId()))
		{
			addressData.setDefaultAddress(true);
			addressData.setVisibleInAddressBook(true);
		}
		if (!isAlreadyDefaultAddress && addressData.isDefaultAddress())
		{
			getUserFacade().setDefaultAddress(addressData);
		}
		getUserFacade().editAddress(addressData);
	}

	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{addressId}", method = RequestMethod.PATCH, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ApiOperation(nickname = "updateAddress", value = "Updates the address", notes = "Updates the address. Only attributes provided in the request body will be changed.")
	@ApiBaseSiteIdAndUserIdParam
	@ResponseStatus(HttpStatus.OK)
	public void updateAddress(@ApiParam(value = "Address identifier.", required = true)
	@PathVariable
	final String addressId, @ApiParam(value = "Address object", required = true)
	@RequestBody
	final AddressWsDTO address)
	{
		final AddressData addressData = getAddressData(addressId);
		final boolean isAlreadyDefaultAddress = addressData.isDefaultAddress();
		addressData.setFormattedAddress(null);

		getDataMapper().map(address, addressData, ADDRESS_MAPPING, false);
		validate(addressData, OBJECT_NAME_ADDRESS, getAddressValidator());

		if (addressData.getId().equals(getUserFacade().getDefaultAddress().getId()))
		{
			addressData.setDefaultAddress(true);
			addressData.setVisibleInAddressBook(true);
		}
		if (!isAlreadyDefaultAddress && addressData.isDefaultAddress())
		{
			getUserFacade().setDefaultAddress(addressData);
		}
		getUserFacade().editAddress(addressData);
	}

	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{addressId}", method = RequestMethod.DELETE)
	@ApiOperation(nickname = "removeAddress", value = "Delete customer's address.", notes = "Removes customer's address.")
	@ApiBaseSiteIdAndUserIdParam
	@ResponseStatus(HttpStatus.OK)
	public void removeAddress(@ApiParam(value = "Address identifier.", required = true)
	@PathVariable
	final String addressId)
	{
		LOG.debug("removeAddress: id={}", sanitize(addressId));
		final AddressData address = getAddressData(addressId);
		getUserFacade().removeAddress(address);
	}

	private AddressData getAddressData(final String addressId)
	{
		final AddressData addressData = getUserFacade().getAddressForCode(addressId);
		if (addressData == null)
		{
			throw new RequestParameterException(String.format(ADDRESS_DOES_NOT_EXIST, sanitize(addressId)),
					RequestParameterException.INVALID, OBJECT_NAME_ADDRESS_ID);
		}
		return addressData;
	}

	/**
	 * @deprecated since 2005. Please use {@link AddressController#validateAddress(AddressWsDTO, String)} instead.
	 */
	@Deprecated(since = "2005", forRemoval = true)
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/verification", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(hidden = true, value = "Verifies the address", notes = "Verifies the address.")
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "baseSiteId", value = "Base site identifier", required = true, dataType = "String", paramType = "path"),
			@ApiImplicitParam(name = "userId", value = "User identifier", required = true, dataType = "String", paramType = "path"),
			@ApiImplicitParam(name = "country.isocode", value = "Country isocode. This parameter is required and have influence on how rest of parameters are validated (e.g. if parameters are required : line1,line2,town,postalCode,region.isocode)", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "line1", value = "First part of address. If this parameter is required depends on country (usually it is required).", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "line2", value = "Second part of address. If this parameter is required depends on country (usually it is not required)", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "town", value = "Town name. If this parameter is required depends on country (usually it is required)", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "postalCode", value = "Postal code. Isocode for region. If this parameter is required depends on country.", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "region.isocode", value = "Isocode for region. If this parameter is required depends on country.", required = false, dataType = "String", paramType = "query") })
	public AddressValidationWsDTO validateAddress(final HttpServletRequest request, @ApiFieldsParam
	@RequestParam(defaultValue = DEFAULT_FIELD_SET)
	final String fields)
	{
		final AddressData addressData = new AddressData();
		final Errors errors = new BeanPropertyBindingResult(addressData, OBJECT_NAME_ADDRESS_DATA);
		AddressValidationData validationData = new AddressValidationData();

		getHttpRequestAddressDataPopulator().populate(request, addressData);
		if (isAddressValid(addressData, errors, validationData))
		{
			validationData = verifyAddresByService(addressData, errors, validationData);
		}
		return getDataMapper().map(validationData, AddressValidationWsDTO.class, fields);
	}

	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/verification", method = RequestMethod.POST, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ApiOperation(nickname = "validateAddress", value = "Verifies address.", notes = "Verifies address.")
	@ApiBaseSiteIdAndUserIdParam
	@ResponseBody
	public AddressValidationWsDTO validateAddress(@ApiParam(value = "Address object.", required = true)
	@RequestBody
	final AddressWsDTO address, @ApiFieldsParam
	@RequestParam(defaultValue = DEFAULT_FIELD_SET)
	final String fields)
	{
		// validation is a bit different here
		if (StringUtils.isEmpty(address.getTitleCode()))
		{
			address.setTitleCode("mr");
		}
		final AddressData addressData = getDataMapper().map(address, AddressData.class, ADDRESS_MAPPING);
		//final Errors errors = new BeanPropertyBindingResult(addressData, OBJECT_NAME_ADDRESS_DATA);
		final Errors errors = genericValidator(address, "telcelAddressWsDTOValidator", telcelAddressWsDTOValidator);
		AddressValidationData validationData = new AddressValidationData();

		if (isAddressValid(addressData, errors, validationData))
		{
			validationData = verifyAddresByService(addressData, errors, validationData);
		}
		return getDataMapper().map(validationData, AddressValidationWsDTO.class, fields);
	}

	protected Errors genericValidator(final Object object, final String objectName, final Validator validator)
	{
		final Errors errors = new BeanPropertyBindingResult(object, objectName);
		validator.validate(object, errors);
		if (errors.hasErrors())
		{
			throw new WebserviceValidationException(errors);
		}
		return errors;
	}

	/**
	 * Checks if address is valid by a validators
	 *
	 * @return <code>true</code> - address is valid , <code>false</code> - address is invalid
	 * @formparam addressData
	 * @formparam errors
	 * @formparam validationData
	 */
	protected boolean isAddressValid(final AddressData addressData, final Errors errors,
			final AddressValidationData validationData)
	{
		getAddressValidator().validate(addressData, errors);

		if (errors.hasErrors())
		{
			validationData.setDecision(AddressVerificationDecision.REJECT.toString());
			validationData.setErrors(createResponseErrors(errors));
			return false;
		}
		return true;
	}

	/**
	 * Verifies address by commerce service
	 *
	 * @return object with verification errors and suggested addresses list
	 * @formparam addressData
	 * @formparam errors
	 * @formparam validationData
	 */
	protected AddressValidationData verifyAddresByService(final AddressData addressData, final Errors errors,
			final AddressValidationData validationData)
	{
		final AddressVerificationResult<AddressVerificationDecision> verificationDecision = addressVerificationFacade
				.verifyAddressData(addressData);
		if (verificationDecision.getErrors() != null && !verificationDecision.getErrors().isEmpty())
		{
			populateErrors(errors, verificationDecision);
			validationData.setErrors(createResponseErrors(errors));
		}

		validationData.setDecision(verificationDecision.getDecision().toString());

		if (verificationDecision.getSuggestedAddresses() != null && !verificationDecision.getSuggestedAddresses().isEmpty())
		{
			final AddressDataList addressDataList = new AddressDataList();
			addressDataList.setAddresses(verificationDecision.getSuggestedAddresses());
			validationData.setSuggestedAddressesList(addressDataList);
		}

		return validationData;
	}

	protected ErrorListWsDTO createResponseErrors(final Errors errors)
	{
		final List<ErrorWsDTO> webserviceErrorDto = new ArrayList<>();
		validationErrorConverter.convert(errors, webserviceErrorDto);
		final ErrorListWsDTO webserviceErrorList = new ErrorListWsDTO();
		webserviceErrorList.setErrors(webserviceErrorDto);
		return webserviceErrorList;
	}

	/**
	 * Populates Errors object
	 *
	 * @param errors
	 * @param addressVerificationResult
	 */
	protected void populateErrors(final Errors errors,
			final AddressVerificationResult<AddressVerificationDecision> addressVerificationResult)
	{
		addressDataErrorsPopulator.populate(addressVerificationResult, errors);
	}

	private CartModel getCartForUserByGUID(final String guid, final String baseSiteId)
	{
		return commerceCartService.getCartForGuidAndSite(guid, baseSiteService.getBaseSiteForUID(baseSiteId));
	}
}
