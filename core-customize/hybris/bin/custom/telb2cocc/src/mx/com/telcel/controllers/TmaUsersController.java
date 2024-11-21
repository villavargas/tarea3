/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.controllers;

import com.google.gson.Gson;
import de.hybris.platform.b2ctelcocommercewebservicescommons.dto.TmaIdentificationWsDTO;
import de.hybris.platform.b2ctelcofacades.data.TmaIdentificationData;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.RegisterData;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commerceservices.request.mapping.annotation.RequestMappingOverride;
import de.hybris.platform.commercewebservicescommons.dto.user.UserSignUpWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.UserWsDTO;
import de.hybris.platform.util.Config;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import mx.com.telcel.core.models.sso.User;
import mx.com.telcel.core.services.TelcelSSOCoreService;
import mx.com.telcel.dto.sso.SSOParameterDTO;
import mx.com.telcel.dto.sso.SSOUserDTO;
import mx.com.telcel.dto.sso.request.SSOUserRequestDTO;
import mx.com.telcel.dto.sso.response.SSOMessageResponseDTO;
import mx.com.telcel.dto.sso.response.SSOUserResponseDTO;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * Controller for user related requests such as create/update a user.
 *
 * @since 1911
 */

@Controller
@RequestMapping(value = "/{baseSiteId}/users")
@CacheControl(directive = CacheControlDirective.PRIVATE)
@ApiVersion("v2")
@Api(tags = "Users")
public class TmaUsersController extends BaseController
{
	private static final Logger LOG = LoggerFactory.getLogger(TmaUsersController.class);

	private static final String IDENTIFICATION = "identification";
	private static final String ONLY_NUMBERS_PATTERN = "\\d+";
	private static final String KEY_PROPERTY = "telcelb2cwebservices.registration.parameter.key";
	private static final String VALUE_LEGACY_PROPERTY = "telcelb2cwebservices.registration.parameter.legacy.value";
	private static final String TYPE_PROPERTY = "telcelb2cwebservices.registration.parameter.type";
	private static final String SOURCE_PROPERTY = "telcelb2cwebservices.registration.user.creationsource.nvatiendatl";

	@Resource(name = "tmaCustomerFacade")
	private CustomerFacade customerFacade;

	@Resource(name = "telcelSSOCoreService")
	private TelcelSSOCoreService telcelSSOCoreService;

	/*
	 * @Resource(name = "userSignUpDTOValidator") private Validator userSignUpDTOValidator;
	 *
	 * @Resource(name = "putUserDTOValidator") private Validator putUserDTOValidator;
	 *
	 * @Resource(name = "tmaIdentificationValidator") private Validator identificationValidator;
	 */

	private final String[] DISALLOWED_FIELDS = new String[] {};

	@Secured(
			{ "ROLE_CLIENT", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(method = RequestMethod.POST, consumes =
			{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@RequestMappingOverride(priorityProperty = "telb2cocc.TmaUsersController.createUser.priority")
	@ResponseStatus(value = HttpStatus.CREATED)
	@ResponseBody
	@ApiOperation(nickname = "createUser", value = "Registers a customer", notes = "Registers a customer. Captures customers identification details if provided. Requires the following "
			+ "parameters:login, password, firstName, lastName, titleCode, identifications")
	@ApiBaseSiteIdParam
	public UserWsDTO createUser(@ApiParam(value = "User's object.", required = true) @RequestBody final UserSignUpWsDTO user,
								@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields,
								final HttpServletRequest httpRequest, final HttpServletResponse httpResponse)
	{
		//	validate(user, "user", userSignUpDTOValidator);
		final List<TmaIdentificationWsDTO> identificationList = user.getIdentifications();
		/*
		 * if (CollectionUtils.isNotEmpty(identificationList)) { validate(identificationList, IDENTIFICATION,
		 * identificationValidator); }
		 */
		final RegisterData registerData = getDataMapper().map(user, RegisterData.class);
		boolean userExists = false;
		try
		{
			customerFacade.register(registerData);
		}
		catch (final DuplicateUidException ex)
		{
			userExists = true;
			LOG.debug("Duplicated UID", ex);
		}
		final String userId = user.getUid().toLowerCase(Locale.ENGLISH);
		httpResponse.setHeader(Telb2coccControllerConstants.LOCATION, getAbsoluteLocationURL(httpRequest, userId)); //NOSONAR
		final CustomerData customerData = getCustomerData(registerData, userExists, userId);
		return getDataMapper().map(customerData, UserWsDTO.class, fields);
	}


	@Secured(
			{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}", method = RequestMethod.PUT, consumes =
			{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@RequestMappingOverride(priorityProperty = "telb2cocc.TmaUsersController.createUser.priority")
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(nickname = "replaceUser", value = "Updates customer profile", notes = "Updates customer profile and identification details. Attributes not provided in the request body will be defined again (set to null or default).")
	@ApiBaseSiteIdAndUserIdParam
	public void replaceUser(@ApiParam(value = "User's object", required = true) @RequestBody final UserWsDTO user)
			throws DuplicateUidException
	{
		//	validate(user, "user", putUserDTOValidator);
		final List<TmaIdentificationWsDTO> identificationList = user.getIdentifications();
		/*
		 * if (CollectionUtils.isNotEmpty(identificationList)) { validate(identificationList, IDENTIFICATION,
		 * identificationValidator); }
		 */
		final CustomerData customer = customerFacade.getCurrentCustomer();
		if (LOG.isDebugEnabled())
		{
			LOG.debug("replaceUser: userId={}", customer.getUid());
		}
		getDataMapper().map(user, customer, true);
		customerFacade.updateFullProfile(customer);
	}

	@Secured(
			{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}", method = RequestMethod.PATCH, consumes =
			{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@RequestMappingOverride(priorityProperty = "telb2cocc.TmaUsersController.createUser.priority")
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(nickname = "updateUser", value = "Updates customer profile", notes = "Updates customer profile and identification details. Only attributes provided in the request body will be changed.")
	@ApiBaseSiteIdAndUserIdParam
	public void  updateUser(@ApiParam(value = "User's object.", required = true) @RequestBody final UserWsDTO user)
			throws DuplicateUidException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException, URISyntaxException,
			CertificateException
	{
		final List<TmaIdentificationWsDTO> identificationList = user.getIdentifications();
		final CustomerData customer = customerFacade.getCurrentCustomer();
		String userID = customer.getUid();
		String originalID = customer.getUid();
		boolean uid = false;
		boolean noMail = false;

		if (validateIsANumber(userID)) {
			if (is10DigitPhoneNumber(userID))
			{
				userID = "52" + userID;
				uid = true;
			}
		}
		final SSOUserResponseDTO lookupUser = telcelSSOCoreService.lookupUser(userID);
		final String id = lookupUser.getUser().getId();

		final List<SSOParameterDTO> parameters = new ArrayList<>();
		final SSOParameterDTO param = new SSOParameterDTO();
		param.setKey(Config.getParameter(KEY_PROPERTY));
		param.setValue(Config.getParameter(VALUE_LEGACY_PROPERTY));
		param.setType(Config.getParameter(TYPE_PROPERTY));
		parameters.add(param);
		final SSOParameterDTO paramMod = new SSOParameterDTO();
		final User userMod = new User();

		SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
		if(userID.equals(customer.getDetailEmail())){
			userMod.setLoginname(new String[]{userID});
			if(!user.getPhoneNumber().isEmpty()){
				userMod.setPhonenumberone("52"+user.getPhoneNumber());;
			} else {
				if(lookupUser.getUser().getPhonenumberone() != null){
					userMod.setPhonenumberone("");
				}
			}
		} else if(originalID.equals(customer.getPhoneNumber())){
			if(!user.getDetailEmail().equals(lookupUser.getUser().getEmailone())){
				userMod.setEmailverifiedone(false);
				noMail = true;
			}
			if(!user.getDetailEmail().isEmpty()){
				userMod.setEmailone(user.getDetailEmail());
				userMod.setLoginname(new String[]{userID, user.getDetailEmail()});
			}
		}
		userMod.setUsergivenname(user.getFirstName().trim());
		userMod.setUserfamilyname(user.getLastName().trim());
		userMod.setBirthdate(sdf.format(user.getBirthdate()));
		final String userMod_json = new Gson().toJson(userMod);
		paramMod.setKey("modifyUser");
		paramMod.setValue(userMod_json);
		paramMod.setType("com.hp.sso.provisioning.entities.User");
		parameters.add(paramMod);

		final SSOUserDTO userModify = new SSOUserDTO();
		userModify.setLoginname(new String[]
				{ userID });
		userModify.setId(id);

		final SSOUserRequestDTO request = new SSOUserRequestDTO();
		request.setSource(Config.getParameter(SOURCE_PROPERTY));
		request.setUser(userModify);
		request.setParameters(parameters);

		SSOUserResponseDTO userResponse = telcelSSOCoreService.modifyUser(request);

		if(uid && userResponse.getError().getCode() == 0  && noMail){
			final SSOMessageResponseDTO messageResponse = telcelSSOCoreService.resendKeyEmail(user.getDetailEmail());
		}

		if(userResponse.getError().getCode() == 0){
			LOG.info("SE ACTULIZO EN SSO!!");
		} else {
			if(userResponse.getError().getCode() == 5003){
				final List<SSOParameterDTO> parameters2 = new ArrayList<>();
				final SSOParameterDTO param2 = new SSOParameterDTO();
				param2.setKey(Config.getParameter(KEY_PROPERTY));
				param2.setValue(Config.getParameter(VALUE_LEGACY_PROPERTY));
				param2.setType(Config.getParameter(TYPE_PROPERTY));
				parameters2.add(param2);
				final SSOParameterDTO paramMod2 = new SSOParameterDTO();
				final User userMod2 = new User();

				SimpleDateFormat sdf2 = new SimpleDateFormat("YYYY-MM-dd");
				if(userID.equals(customer.getDetailEmail())){
					userMod2.setLoginname(new String[]{userID});
					if(!user.getPhoneNumber().isEmpty()){
						userMod2.setPhonenumberone("52"+user.getPhoneNumber());;
					}else {
						userMod2.setPhonenumberone("");
					}
				} else if(originalID.equals(customer.getPhoneNumber())){
					if(!user.getDetailEmail().equals(lookupUser.getUser().getEmailone())){
						userMod2.setEmailverifiedone(false);
					}
					userMod2.setEmailone(user.getDetailEmail());
					userMod2.setLoginname(new String[]{userID});
				}
				userMod2.setUsergivenname(user.getFirstName().trim());
				userMod2.setUserfamilyname(user.getLastName().trim());
				userMod2.setBirthdate(sdf2.format(user.getBirthdate()));
				final String userMod_json2 = new Gson().toJson(userMod2);
				paramMod2.setKey("modifyUser");
				paramMod2.setValue(userMod_json2);
				paramMod2.setType("com.hp.sso.provisioning.entities.User");
				parameters2.add(paramMod2);

				final SSOUserDTO userModify2 = new SSOUserDTO();
				userModify2.setLoginname(new String[]
						{ userID });
				userModify2.setId(id);

				final SSOUserRequestDTO request2 = new SSOUserRequestDTO();
				request2.setSource(Config.getParameter(SOURCE_PROPERTY));
				request2.setUser(userModify2);
				request2.setParameters(parameters2);

				SSOUserResponseDTO userResponseTwo = telcelSSOCoreService.modifyUser(request2);

				if(userResponseTwo.getError().getCode() == 0){
					LOG.info("SE ACTULIZO EN SSO SIN LOGIN NAME!!");
				}
			}
		}

		if (CollectionUtils.isNotEmpty(identificationList))
		{
			//	validate(identificationList, IDENTIFICATION, identificationValidator);
			this.patchIdentificationDataForCustomer(user, customer);
		}
		if (LOG.isDebugEnabled())
		{
			LOG.debug("updateUser: userId={}", customer.getUid());
		}
		getDataMapper().map(user, customer, false);
		customerFacade.updateFullProfile(customer);

	}

	protected CustomerData getCustomerData(final RegisterData registerData, final boolean userExists, final String userId)
	{
		final CustomerData customerData;
		if (userExists)
		{
			customerData = customerFacade.nextDummyCustomerData(registerData);
		}
		else
		{
			customerData = customerFacade.getUserForUID(userId);
		}
		return customerData;
	}

	protected String getAbsoluteLocationURL(final HttpServletRequest httpRequest, final String uid)
	{
		final String requestURL = sanitize(httpRequest.getRequestURL().toString());
		final StringBuilder absoluteURLSb = new StringBuilder(requestURL);
		if (!requestURL.endsWith(Telb2coccControllerConstants.SLASH))
		{
			absoluteURLSb.append(Telb2coccControllerConstants.SLASH);
		}
		absoluteURLSb.append(UriUtils.encodePathSegment(uid, StandardCharsets.UTF_8.name()));
		return absoluteURLSb.toString();
	}

	private void patchIdentificationDataForCustomer(final UserWsDTO user, final CustomerData customer)
	{
		final List<TmaIdentificationWsDTO> mergedIdentificationWsDTO = new ArrayList<>();
		mergedIdentificationWsDTO.addAll(user.getIdentifications());
		final List<TmaIdentificationData> existingIdentificationData = customer.getIdentifications();
		existingIdentificationData.forEach(identification ->
		{
			mergedIdentificationWsDTO.add(getDataMapper().map(identification, TmaIdentificationWsDTO.class));
		});
		user.setIdentifications(mergedIdentificationWsDTO);
	}


	@InitBinder
	public void initBinder(final WebDataBinder binder)
	{
		binder.setDisallowedFields(DISALLOWED_FIELDS);
	}

	public boolean validateIsANumber(final String userID)
	{
		try
		{
			final Pattern pattern = Pattern.compile(ONLY_NUMBERS_PATTERN);
			final Matcher matcher = pattern.matcher(userID);
			if (matcher.matches())
			{
				return true;
			}
		}
		catch (final NumberFormatException nfe)
		{
		}
		return false;
	}

	public boolean is10DigitPhoneNumber(final String phoneNumber)
	{
		final String regex = "^[0-9]{10}$";
		return phoneNumber.matches(regex);
	}
}