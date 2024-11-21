/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.controllers;

import com.google.gson.Gson;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;
import de.hybris.platform.util.localization.Localization;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import mx.com.telcel.core.daos.tokennip.TokenNipDao;
import mx.com.telcel.core.helper.TelcelEmailsHelper;
import mx.com.telcel.core.model.RegistrationTokenModel;
import mx.com.telcel.core.model.RestorePasswordTokenModel;
import mx.com.telcel.facades.customer.TelcelCustomerFacade;
import mx.com.telcel.facades.registration.TelcelTokenFacade;
import mx.com.telcel.models.Parameter;
import mx.com.telcel.models.User;
import mx.com.telcel.request.models.UserCodeRequest;
import mx.com.telcel.request.models.UserPreregisterRequest;
import mx.com.telcel.request.models.UserRequest;
import mx.com.telcel.response.models.TokenNipResponse;
import mx.com.telcel.response.models.UserResponse;
import mx.com.telcel.services.CustomValidationService;
import mx.com.telcel.services.TelcelSSOService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.assertj.core.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static mx.com.telcel.constants.Telcelb2cwebservicesConstants.CLIENT_CREDENTIAL_AUTHORIZATION_NAME;
import static mx.com.telcel.constants.Telcelb2cwebservicesConstants.PASSWORD_AUTHORIZATION_NAME;


/**
 * The type Registration controller.
 */
@Controller
@RequestMapping(value = "/registration")
@CacheControl(directive = CacheControlDirective.NO_STORE)
@Api(tags = "User register")
public class RegistrationController
{

	private static final Logger LOG = Logger.getLogger(RegistrationController.class.getName());


	/**
	 * The constant NOT_FOUND_CUSTOMER_CODE.
	 */
	private static final int SUCCESS = 0;
	public static final int NOT_FOUND_CUSTOMER_CODE = 114;
	public static final int ITS_NOT_AN_OPERATORS_USER_CODE = 118;
	private static final String NUMBER_USERNAME_PREFIX = "52";
	private static final String USER_ID = "userID";
	private static final String STATUS = "status";

	private static final String SOURCE_PROPERTY = "telcelb2cwebservices.registration.user.source";
	private static final String USER_PREFERRED_LANGUAGE_PROPERTY = "telcelb2cwebservices.registration.user.userpreferredlanguage";
	private static final String LOCALE_PROPERTY = "telcelb2cwebservices.registration.user.locale";
	private static final String TIMEZONE_PROPERTY = "telcelb2cwebservices.registration.user.timezone";
	private static final String CREATION_SOURCE_PROPERTY = "telcelb2cwebservices.registration.user.creationsource.nvatiendatl";
	private static final String CREATION_SOURCE_LITE_PROPERTY = "telcelb2cwebservices.registration.user.creationsource.lite";
	private static final String CREATION_SOURCE_NVATIENDATL_PROPERTY = "telcelb2cwebservices.registration.user.creationsource.nvatiendatl";

	private static final String KEY_PROPERTY = "telcelb2cwebservices.registration.parameter.key";
	private static final String VALUE_PROPERTY = "telcelb2cwebservices.registration.parameter.value";
	private static final String VALUE_LEGACY_PROPERTY = "telcelb2cwebservices.registration.parameter.legacy.value";
	private static final String TYPE_PROPERTY = "telcelb2cwebservices.registration.parameter.type";
	private static final String SECURITY_CODE_TEST = "telcel.performance.test.enabled";
	private static final String ACCESS_TOKEN = "telcel.access.token";
	private static final String REFRESH_TOKEN = "telcel.refresh.token";


	@Resource(name = "telcelSSOService")
	private TelcelSSOService telcelSSOService;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "telcelEmailsHelper")
	private TelcelEmailsHelper telcelEmailsHelper;

	@Resource(name = "telcelTokenFacade")
	private TelcelTokenFacade telcelTokenFacade;

	@Resource(name = "customValidationService")
	private CustomValidationService validationService;

	@Resource(name = "telcelCustomerFacade")
	private TelcelCustomerFacade telcelCustomerFacade;
	
	@Resource(name = "defaultConfigurationService")
	private ConfigurationService configurationService;

	@Resource(name = "tokenNipDao")
	private TokenNipDao tokenNipDao;

	/**
	 * Service that checks if the mail/phone exists in SSO<br/>
	 * Example :</br>
	 * GET https://localhost:9002/telcelb2cwebservices/registration/addUser</br>
	 * Method : GET</br>
	 *
	 * @param userID
	 *           the user id
	 * @param clientID
	 *           the client id
	 * @return the response entity
	 * @throws KeyManagementException
	 *            the key management exception
	 * @throws NoSuchAlgorithmException
	 *            the no such algorithm exception
	 * @throws KeyStoreException
	 *            the key store exception
	 * @throws IOException
	 *            the io exception
	 * @throws URISyntaxException
	 *            the uri syntax exception
	 * @throws CertificateException
	 *            the certificate exception
	 */
	@RequestMapping(value = "/addUser", method = RequestMethod.GET)
	@CacheControl(directive = CacheControlDirective.PUBLIC, maxAge = 1800)
	@ResponseBody
	@ApiOperation(value = "Validate user by email or phone", notes = "Method returning if the user exists in SSO", produces = "application/json,application/xml", authorizations =
	{ @Authorization(value = CLIENT_CREDENTIAL_AUTHORIZATION_NAME), @Authorization(value = PASSWORD_AUTHORIZATION_NAME) })
	public ResponseEntity<String> addUser(@ApiParam(value = "User ID")
	@RequestParam(required = true)
	String userID, @ApiParam(value = "Client ID")
	@RequestParam(required = true)
	final String clientID) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException,
			URISyntaxException, CertificateException
	{
		LOG.info("RegistrationController.addUser");

		String message = Localization.getLocalizedString("registration.user.no.information.label");
		HttpStatus httpStatus = HttpStatus.NO_CONTENT;
		userID = userID.toLowerCase();
		//Validate
		if (validationService.validateIsANumber(userID))
		{
			LOG.info("RegistrationController.addUser IsANumber ["+userID+"]");

			if (validationService.is10DigitPhoneNumber(userID))
			{
				userID = NUMBER_USERNAME_PREFIX + userID;
			}
			//Invocacion de SSO -User lookup
			final Boolean exists = userExistsInSSO(userID);
			if (exists != null && !exists)
			{
				LOG.info("RegistrationController.userExistsInSSO Invocacion de SSO -User Add");

				//Invocacion de SSO -User Add
				final Boolean otherOperatorValidator = addUserByPhoneToSSOAndSap(userID, clientID);
				if (otherOperatorValidator)
				{
					LOG.info("RegistrationController.userExistsInSSO Invocacion de SSO -User Add "+Localization.getLocalizedString("registration.user.success.label"));
					message = Localization.getLocalizedString("registration.user.success.label");
					httpStatus = HttpStatus.CREATED;
				}
				else
				{
					LOG.info("RegistrationController.userExistsInSSO Invocacion de SSO -User Add no se creo correctamente");
					message = Localization.getLocalizedString("registration.user.other.operator");
					httpStatus = HttpStatus.NOT_ACCEPTABLE;
				}
			}
			else
			{
				LOG.info("RegistrationController.userExistsInSSO Usuario ya encontrado como registrado");
				message = Localization.getLocalizedString("registration.user.found.label");
				httpStatus = HttpStatus.CONFLICT;
			}
			return new ResponseEntity<>(message, httpStatus);

		}
		else if (validationService.validateIsAnEmail(userID))
		{
			LOG.info("RegistrationController.validateIsAnEmail");

			final Boolean exists = userExistsInSSO(userID);
			if (exists != null && !exists)
			{
				LOG.info("RegistrationController.validateIsAnEmail email no existe en sso");

				//No existe -- Continuar tipo de registro "Email"
				//Genrar correo con token
				final RegistrationTokenModel registrationTokenModel = modelService.create(RegistrationTokenModel.class);
				registrationTokenModel.setEmail(userID);
				registrationTokenModel.setToken(telcelTokenFacade.getToken());
				registrationTokenModel.setCurrentDate(new Date());
				modelService.save(registrationTokenModel);
				telcelEmailsHelper.sendRegistrationTokenEmail(registrationTokenModel);
				message = Localization.getLocalizedString("registration.user.send.token.label");
				LOG.info("RegistrationController.validateIsAnEmail user creado");
				httpStatus = HttpStatus.CREATED;

			}
			else
			{
				LOG.info("RegistrationController.validateIsAnEmail email ya existe en sso");

				message = Localization.getLocalizedString("registration.user.found.label");
				httpStatus = HttpStatus.CONFLICT;
			}
			return new ResponseEntity<>(message, httpStatus);
		}

		return new ResponseEntity<>(message, httpStatus);
	}


	/**
	 * Generate code response entity.
	 * @return the response entity
	 */
	@PostMapping("/sendCodeSecurity")
	@CacheControl(directive = CacheControlDirective.PUBLIC, maxAge = 1800)
	@ResponseBody
	@ApiOperation(value = "Generate code security", notes = "Generate the security code sent by telcel", produces = "application/json,application/xml")
	public TokenNipResponse generateCode(@RequestBody final Map<String, Object> request) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, KeyManagementException, URISyntaxException
	{
		if(Objects.isNull(request.get("uid")))
		{
			return createErrorTokenNipResponse("-1","uid not found");
		}

		String uid = request.get("uid").toString();
		LOG.debug("RegistrationController.generateCode = " + uid);
		uid = uid.toLowerCase();
		//Validate
		if (validationService.validateIsANumber(uid))
		{
			if (validationService.is10DigitPhoneNumber(uid))
			{
				uid = NUMBER_USERNAME_PREFIX + uid;
			}
			//Invocacion de SSO -User lookup
			final UserResponse userResponse = telcelSSOService.lookupUser(uid);
			final Boolean exists = userResponse.getUser().isRegistrationcompleted();

			if (exists != null && exists)
			{
				return createErrorTokenNipResponse("-1", Localization.getLocalizedString("registration.user.numberfound"));
			}
			try
			{
				final TokenNipResponse tokenNipResponse = telcelSSOService.sendTokenNip(uid);
				return tokenNipResponse;
			}
			catch (final Exception e)
			{
				LOG.error("Ocurrio un error = " + e.getLocalizedMessage());

				return createErrorTokenNipResponse("-3", Localization.getLocalizedString("registration.user.error.number"));
			}


		}
		else if (validationService.validateIsAnEmail(uid))
		{
			//Invocacion de SSO -User lookup
			final UserResponse userResponse = telcelSSOService.lookupUser(uid);
			final Boolean exists = userResponse.getUser().isRegistrationcompleted();

			if (exists != null && exists)
			{
				return createErrorTokenNipResponse("-1", Localization.getLocalizedString("registration.user.numberfound"));
			}

			//Llamar a servicio que envia correo con codigo de seguridad
			try
			{
				final TokenNipResponse tokenNipResponse = telcelSSOService.sendTokenNip(uid);
				return tokenNipResponse;
			}
			catch (final Exception e)
			{
				LOG.error("Ocurrio un error = " + e.getLocalizedMessage());
				return createErrorTokenNipResponse("-4", Localization.getLocalizedString("registration.user.error.mail"));
			}
		}
		LOG.error ("generateCode no es ni numero ni email ["+uid+"]");

		return createErrorTokenNipResponse("-2", Localization.getLocalizedString("registration.user.error"));
	}

	private TokenNipResponse createErrorTokenNipResponse(final String error, final String desc)
	{
		final TokenNipResponse tokenNipResponse = new TokenNipResponse();
		tokenNipResponse.setError(error);
		tokenNipResponse.setErrorDescription(desc);

		return tokenNipResponse;
	}

	/**
	 * Validate code response entity.

	 * @return the response entity
	 */
	@PostMapping("/validateCode")
	@CacheControl(directive = CacheControlDirective.PUBLIC, maxAge = 1800)
	@ResponseBody
	@ApiOperation(value = "Validate code security", notes = "validate the security code sent by telcel", produces = "application/json,application/xml", authorizations =
	{ @Authorization(value = CLIENT_CREDENTIAL_AUTHORIZATION_NAME), @Authorization(value = PASSWORD_AUTHORIZATION_NAME) })
	public TokenNipResponse validateCode(@Valid @RequestBody UserCodeRequest request) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, KeyManagementException
	{


		String username = request.getUsername().toLowerCase();


		TokenNipResponse tokenNipResponse = null;
		
		final String security_code_test = configurationService.getConfiguration().getString(SECURITY_CODE_TEST);
		Pattern pat = Pattern.compile(".*@saplogtest.com.*");
		Matcher mat = pat.matcher(username);


		if (security_code_test.equals("true") && mat.matches())
		{
			LOG.info("true = " + security_code_test);
			final String access_token = configurationService.getConfiguration().getString(ACCESS_TOKEN);
			final String refresh_token = configurationService.getConfiguration().getString(REFRESH_TOKEN);

			tokenNipResponse = new TokenNipResponse();
			tokenNipResponse.setAccessToken(access_token);
			tokenNipResponse.setCode(0);
			tokenNipResponse.setExpiresIn(3600);
			tokenNipResponse.setRefreshToken(refresh_token);
			tokenNipResponse.setTokenType("Bearer");
			tokenNipResponse.setRequestId(request.getRequestId());
			tokenNipResponse.setMfaRequired("true");

			return tokenNipResponse;

		}
		else
		{
			LOG.info("else = " + security_code_test);


		//Validate
		if (validationService.validateIsANumber(username))
		{
			if (validationService.is10DigitPhoneNumber(username))
			{
				username = NUMBER_USERNAME_PREFIX + username;
			}

			//validar si ya existe en SSO
			
			tokenNipResponse = telcelSSOService.validateTokenNip(username, request.getRequestId(), request.getCode());
		
			if (Objects.isNull(tokenNipResponse.getAccessToken()))
			{
				if (tokenNipResponse.getError().equals("OTP_TIMEOUT")
						|| tokenNipResponse.getErrorDescription().equals("request invalid or expired.")) {
					LOG.info("El código ha caducado " + tokenNipResponse.getErrorDescription());
					tokenNipResponse.setError("-2");
					tokenNipResponse.setErrorDescription("El código ha caducado");
				} else if (tokenNipResponse.getErrorDescription().equals("user not authenticated.")) {
					LOG.info("El código es incorrecto " + tokenNipResponse.getErrorDescription());
					tokenNipResponse.setError("-3");
					tokenNipResponse.setErrorDescription("El código es incorrecto");
				} else {
					tokenNipResponse.setError("-1");
					tokenNipResponse.setErrorDescription("Ocurrio un error al validar el codigo");
				}
			} else {
				checkTokenNip(request.getCode(), username, true);
			}
			return tokenNipResponse;
		}
		else if (validationService.validateIsAnEmail(username))
		{
			//Llamar a servicio que valida codigho de seguridad que se envio por email
			tokenNipResponse = telcelSSOService.validateTokenNip(username, request.getRequestId(), request.getCode());
			if (tokenNipResponse.getAccessToken() == null)
			{
				if (tokenNipResponse.getError().equals("OTP_TIMEOUT")
						|| tokenNipResponse.getErrorDescription().equals("request invalid or expired.")) {
					LOG.info("El código ha caducado " + tokenNipResponse.getErrorDescription());
					tokenNipResponse.setError("-2");
					tokenNipResponse.setErrorDescription("El código ha caducado");
				} else if (tokenNipResponse.getErrorDescription().equals("user not authenticated.")) {
					LOG.info("El código es incorrecto " + tokenNipResponse.getErrorDescription());
					tokenNipResponse.setError("-3");
					tokenNipResponse.setErrorDescription("El código es incorrecto");
				} else {
					tokenNipResponse.setError("-1");
					tokenNipResponse.setErrorDescription("Ocurrio un error al validar el codigo");
				}
			} else {
				checkTokenNip(request.getCode(), username, false);
			}
			return tokenNipResponse;
		}

		return null;
	  }

	}

	private void checkTokenNip(String code, String username, boolean bandera) {
		if(bandera){
			username = removePrefix(username,"52");
		}
		RestorePasswordTokenModel restorePasswordTokenModel = tokenNipDao.findUserAndToken(username);
		if(restorePasswordTokenModel != null){
			modelService.remove(restorePasswordTokenModel);
		}
		saveTokenNip(code, username);
	}

	private boolean addUserByPhoneToSSOAndSap(final String userID, final String clientID) throws NoSuchAlgorithmException,
			KeyStoreException, IOException, KeyManagementException, URISyntaxException, CertificateException
	{
		LOG.info("RegistrationController.addUserByPhoneToSSOAndSap addUserByPhoneToSSOAndSap");

		final UserResponse userResponse = addUserToSSO(userID, null);

		if (userResponse != null && userResponse.getError().getCode() != ITS_NOT_AN_OPERATORS_USER_CODE)
		{
			//Invocacion del servicio -Reset password 1st
			telcelSSOService.password1stUser(userID, clientID);
			//Respuesta && guardado de usuario en Sap Commerce
			createCustomer(userID);
			LOG.info("RegistrationController.addUserByPhoneToSSOAndSap createCustomer true");

			return true;
		}
		return false;
	}

	private UserResponse addUserToSSO(final String userID, final String password)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException
	{

		LOG.info("RegistrationController.addUserToSSO");

		final UserRequest userRequest = new UserRequest();
		userRequest.setSource(Config.getParameter(SOURCE_PROPERTY));
		// Set User
		final User user = new User();
		user.setUserpreferredlanguage(Config.getParameter(USER_PREFERRED_LANGUAGE_PROPERTY));
		user.setLocale(Config.getParameter(LOCALE_PROPERTY));
		final String[] loginName = new String[]
		{ userID };
		user.setLoginname(loginName);
		user.setTimezone(Config.getParameter(TIMEZONE_PROPERTY));
		user.setCreationsource(Config.getParameter(CREATION_SOURCE_PROPERTY));
		if (StringUtils.isNotEmpty(password))
		{
			user.setPassword(password);
		}
		userRequest.setUser(user);
		// Set Parameters
		final Parameter parameter = new Parameter();
		parameter.setKey(Config.getParameter(KEY_PROPERTY));
		parameter.setValue(Config.getParameter(VALUE_PROPERTY));
		parameter.setType(Config.getParameter(TYPE_PROPERTY));
		final Parameter[] parameters = new Parameter[]
		{ parameter };
		userRequest.setParameters(parameters);
		//Add user SSO
		return telcelSSOService.addUser(userRequest);
	}

	private CustomerModel createCustomer(final String userID)
	{
		final CustomerModel customerModel = modelService.create(CustomerModel.class);
		customerModel.setUid(userID);
		if (validationService.validateIsAnEmail(userID))
		{
			customerModel.setDetailEmail(userID);
		}
		else
		{
			customerModel.setNumberPhone(userID);
		}
		customerModel.setGroups(Collections.singleton((PrincipalGroupModel) userService.getUserGroupForUID("customergroup")));
		customerModel.setName("-");
		customerModel.setCustomerID(userID);
		customerModel.setLoginDisabled(false);
		modelService.save(customerModel);
		return customerModel;
	}

	/**
	 * Validate user token for email response entity. URL :
	 * http://localhost:9001/telcelb2cwebservices/registration/validateUserTokenForEmail</br>
	 * Method : GET</br>
	 * Header : Content-Type=application/json</br>
	 *
	 * @param userID
	 *           the user id
	 * @param clientID
	 *           the client id
	 * @param token
	 *           the token
	 * @return the response entity
	 * @throws KeyManagementException
	 *            the key management exception
	 * @throws NoSuchAlgorithmException
	 *            the no such algorithm exception
	 * @throws KeyStoreException
	 *            the key store exception
	 * @throws IOException
	 *            the io exception
	 * @throws URISyntaxException
	 *            the uri syntax exception
	 * @throws CertificateException
	 *            the certificate exception
	 */
	@RequestMapping(value = "/validateUserTokenForEmail", method = RequestMethod.GET)
	@CacheControl(directive = CacheControlDirective.PUBLIC, maxAge = 1800)
	@ResponseBody
	@ApiOperation(value = "Validate user by email or phone", notes = "Method returning if the user exists in SSO", produces = "application/json,application/xml", authorizations =
	{ @Authorization(value = CLIENT_CREDENTIAL_AUTHORIZATION_NAME), @Authorization(value = PASSWORD_AUTHORIZATION_NAME) })
	public ResponseEntity<String> validateUserTokenForEmail(@ApiParam(value = "User ID")
	@RequestParam(required = true)
	final String userID, @ApiParam(value = "Client ID")
	@RequestParam(required = true)
	final String clientID, @ApiParam(value = "Token")
	@RequestParam(required = true)
	final String token) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException,
			URISyntaxException, CertificateException
	{
		//Validar token
		//No es correcto - Enviar notificacion a front
		String message = Localization.getLocalizedString("registration.token.no.information.label");
		HttpStatus httpStatus = HttpStatus.NOT_FOUND;
		if (telcelTokenFacade.findRegistrationToken(userID, token) != null)
		{
			//Es correcto - Enviar notificacion a front para que proporcione los demas datos.
			message = Localization.getLocalizedString("registration.token.information.label");
			httpStatus = HttpStatus.FOUND;
		}
		return new ResponseEntity<>(message, httpStatus);
	}

	/**
	 * URL : http://localhost:9001/telcelb2cwebservices/registration/createUserByEmail</br>
	 * Method : GET</br>
	 * Header : Content-Type=application/json</br>
	 *
	 * @param userID
	 *           the user id
	 * @param clientID
	 *           the client id
	 * @param password
	 *           the password
	 * @return the response entity
	 * @throws KeyManagementException
	 *            the key management exception
	 * @throws NoSuchAlgorithmException
	 *            the no such algorithm exception
	 * @throws KeyStoreException
	 *            the key store exception
	 * @throws IOException
	 *            the io exception
	 * @throws URISyntaxException
	 *            the uri syntax exception
	 * @throws CertificateException
	 *            the certificate exception
	 */
	@RequestMapping(value = "/createUserByEmail", method = RequestMethod.GET)
	@CacheControl(directive = CacheControlDirective.PUBLIC, maxAge = 1800)
	@ResponseBody
	@ApiOperation(value = "Create user by email ", notes = "Method to create customer", produces = "application/json,application/xml", authorizations =
	{ @Authorization(value = CLIENT_CREDENTIAL_AUTHORIZATION_NAME), @Authorization(value = PASSWORD_AUTHORIZATION_NAME) })
	public ResponseEntity<String> createUserByEmail(@ApiParam(value = "User ID")
	@RequestParam(required = true)
	final String userID, @ApiParam(value = "Client ID")
	@RequestParam(required = true)
	final String clientID, @ApiParam(value = "Password")
	@RequestParam(required = true)
	final String password) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException,
			URISyntaxException, CertificateException
	{
		//Invocacion de SSO para guardar el usuario
		final UserResponse userResponse = addUserToSSO(userID, password);
		if (userResponse != null)
		{

		}
		//Respuesta no exitosa - Notificar al usuario de que algo salio mal
		//Respuesta exitosa
		//Agregar usuario a SAP commerce
		final CustomerModel customerModel = createCustomer(userID);
		//Enviar Email de bienvenida
		telcelEmailsHelper.sendWelcomeEmail(customerModel);
		// Notificar al usuario de que se registro exitosamenete
		return new ResponseEntity<>(Localization.getLocalizedString("registration.user.success.label"), HttpStatus.CREATED);
	}

	/**
	 * URL : http://localhost:9001/telcelb2cwebservices/registration/userRegistrationCompleted</br>
	 * Method : POST</br>
	 * Header : Content-Type=application/json</br>
	 * @return the response entity
	 * @throws KeyManagementException
	 *            the key management exception
	 * @throws NoSuchAlgorithmException
	 *            the no such algorithm exception
	 * @throws KeyStoreException
	 *            the key store exception
	 * @throws IOException
	 *            the io exception
	 * @throws URISyntaxException
	 *            the uri syntax exception
	 * @throws CertificateException
	 *            the certificate exception
	 */
	@PostMapping("/userRegistrationCompleted")
	@CacheControl(directive = CacheControlDirective.PUBLIC, maxAge = 1800)
	@ResponseBody
	@ApiOperation(value = "User Registration Completed status", notes = "Method to get user registration status", produces = "application/json,application/xml", authorizations =
	{ @Authorization(value = CLIENT_CREDENTIAL_AUTHORIZATION_NAME), @Authorization(value = PASSWORD_AUTHORIZATION_NAME) })
	public ResponseEntity<UserResponse> userRegistrationCompleted(@RequestBody final Map<String, Object> request) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException, URISyntaxException, CertificateException
	{
		if(Objects.isNull(request.get(USER_ID)))
		{
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		String userID = request.get(USER_ID).toString();
		//Validate phone or email
		boolean isnumber = false;
		if (validationService.validateIsANumber(userID))
		{
			isnumber = true;
			if (!validationService.is10DigitPhoneNumber(userID))
			{
				final mx.com.telcel.models.Error err = new mx.com.telcel.models.Error();
				err.setCode(-5);
				err.setDescription(Localization.getLocalizedString("login.validation.phonenumber.label"));
				final UserResponse response = new UserResponse();
				response.setError(err);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			else if (validationService.is10DigitPhoneNumber(userID))
			{
				userID = NUMBER_USERNAME_PREFIX + userID;
			}
		}
		if (!validationService.validateIsAnEmail(userID) && !isnumber)
		{
			final mx.com.telcel.models.Error err = new mx.com.telcel.models.Error();
			err.setCode(-6);
			err.setDescription(Localization.getLocalizedString("login.validation.email.label"));
			final UserResponse response = new UserResponse();
			response.setError(err);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		//Validate is user exist in sso
		final UserResponse lookupUser = telcelSSOService.lookupUser(userID);
		if (lookupUser.getError().getCode() == NOT_FOUND_CUSTOMER_CODE)
		{
			final mx.com.telcel.models.Error err = new mx.com.telcel.models.Error();
			err.setCode(-7);
			err.setDescription(Localization.getLocalizedString("login.validation.usernotfound.label"));
			final UserResponse response = new UserResponse();
			response.setError(err);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		if (lookupUser.getError().getCode() == SUCCESS)
		{
			if (!lookupUser.getUser().isActive() && lookupUser.getUser().getPasswordfailedattemptscounter() != 0)
			{
				final mx.com.telcel.models.Error err = new mx.com.telcel.models.Error();
				err.setCode(-8);
				err.setDescription(Localization.getLocalizedString("login.validation.blockaccount.label"));
				final UserResponse response = new UserResponse();
				response.setError(err);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		}
		final UserResponse userResponse = new UserResponse();
		final User u = new User();
		u.setRegistrationcompleted(lookupUser.getUser().isRegistrationcompleted());
		userResponse.setUser(u);

		return new ResponseEntity<>(userResponse, HttpStatus.OK);
	}

	/**
	 * URL : http://localhost:9001/telcelb2cwebservices/registration/updateUserRegistrationCompleted</br>
	 * Method : POST</br>
	 * Header : Content-Type=application/json</br>
	 * @return the response entity
	 * @throws KeyManagementException
	 *            the key management exception
	 * @throws NoSuchAlgorithmException
	 *            the no such algorithm exception
	 * @throws KeyStoreException
	 *            the key store exception
	 * @throws IOException
	 *            the io exception
	 * @throws URISyntaxException
	 *            the uri syntax exception
	 * @throws CertificateException
	 *            the certificate exception
	 */
	@PostMapping("/updateUserRegistrationCompleted")
	@CacheControl(directive = CacheControlDirective.PUBLIC, maxAge = 1800)
	@ResponseBody
	@ApiOperation(value = "Update User Registration Completed status", notes = "Method to update user registration status", produces = "application/json,application/xml", authorizations =
	{ @Authorization(value = CLIENT_CREDENTIAL_AUTHORIZATION_NAME), @Authorization(value = PASSWORD_AUTHORIZATION_NAME) })
	public ResponseEntity<UserResponse> updateUserRegistrationCompleted(@RequestBody final Map<String, Object> request) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException, URISyntaxException, CertificateException
	{
		String userID;
		boolean status;
		try{
			userID = request.get(USER_ID).toString();
			status = (Boolean)request.get(STATUS);
		}catch (NullPointerException | ClassCastException ex)
		{
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		//Validate phone or email
		boolean isnumber = false;
		final String sapUserID = userID;
		if (validationService.validateIsANumber(userID))
		{
			isnumber = true;
			if (!validationService.is10DigitPhoneNumber(userID))
			{
				final mx.com.telcel.models.Error err = new mx.com.telcel.models.Error();
				err.setCode(-5);
				err.setDescription(Localization.getLocalizedString("login.validation.phonenumber.label"));
				final UserResponse response = new UserResponse();
				response.setError(err);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			else if (validationService.is10DigitPhoneNumber(userID))
			{
				userID = NUMBER_USERNAME_PREFIX + userID;
			}
		}
		if (!validationService.validateIsAnEmail(userID) && !isnumber)
		{
			final mx.com.telcel.models.Error err = new mx.com.telcel.models.Error();
			err.setCode(-6);
			err.setDescription(Localization.getLocalizedString("login.validation.email.label"));
			final UserResponse response = new UserResponse();
			response.setError(err);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		//Validate is user exist in sso
		final UserResponse lookupUser = telcelSSOService.lookupUser(userID);
		if (lookupUser.getError().getCode() == NOT_FOUND_CUSTOMER_CODE)
		{
			final mx.com.telcel.models.Error err = new mx.com.telcel.models.Error();
			err.setCode(-7);
			err.setDescription(Localization.getLocalizedString("login.validation.usernotfound.label"));
			final UserResponse response = new UserResponse();
			response.setError(err);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		if (lookupUser.getError().getCode() == SUCCESS)
		{
			if (!lookupUser.getUser().isActive() && lookupUser.getUser().getPasswordfailedattemptscounter() != 0)
			{
				final mx.com.telcel.models.Error err = new mx.com.telcel.models.Error();
				err.setCode(-8);
				err.setDescription(Localization.getLocalizedString("login.validation.blockaccount.label"));
				final UserResponse response = new UserResponse();
				response.setError(err);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		}
		final String id = lookupUser.getUser().getId();
		final Parameter[] parameters = new Parameter[2];
		final Parameter param = new Parameter();
		param.setKey(Config.getParameter(KEY_PROPERTY));
		param.setValue(Config.getParameter(VALUE_LEGACY_PROPERTY));
		param.setType(Config.getParameter(TYPE_PROPERTY));
		parameters[0] = param;
		final Parameter paramMod = new Parameter();
		final User userMod = new User();
		userMod.setRegistrationcompleted(status);
		final String userMod_json = new Gson().toJson(userMod);
		paramMod.setKey("modifyUser");
		paramMod.setValue(userMod_json);
		paramMod.setType("com.hp.sso.provisioning.entities.User");
		parameters[1] = paramMod;

		final User user = new User();
		user.setLoginname(new String[]
		{ userID });
		user.setId(id);

		final UserRequest userRequest = new UserRequest();
		userRequest.setSource(Config.getParameter(SOURCE_PROPERTY));
		userRequest.setUser(user);
		userRequest.setParameters(parameters);

		final UserResponse userResponse = telcelSSOService.modifyUserSendMail(userRequest);

		/*final CustomerModel customerModelS = (CustomerModel) userService.getUserForUID(sapUserID);
		customerModelS.setName(lookupUser.getUser().getUsergivenname());
		modelService.save(customerModelS);*/

		return new ResponseEntity<>(userResponse, HttpStatus.OK);
	}

	/**
	 * URL : http://localhost:9001/telcelb2cwebservices/registration/addUserPreregister</br>
	 * Method : POST</br>
	 * Header : Content-Type=application/json</br>
	 *
	 * @return the response entity
	 * @throws KeyManagementException
	 *            the key management exception
	 * @throws NoSuchAlgorithmException
	 *            the no such algorithm exception
	 * @throws KeyStoreException
	 *            the key store exception
	 * @throws IOException
	 *            the io exception
	 * @throws URISyntaxException
	 *            the uri syntax exception
	 * @throws CertificateException
	 *            the certificate exception
	 */
	@PostMapping("/addUserPreregister")
	@CacheControl(directive = CacheControlDirective.PUBLIC, maxAge = 1800)
	@ResponseBody
	@ApiOperation(value = "Add User Registration Data", notes = "Method to add user registration data", produces = "application/json,application/xml", authorizations =
	{ @Authorization(value = CLIENT_CREDENTIAL_AUTHORIZATION_NAME), @Authorization(value = PASSWORD_AUTHORIZATION_NAME) })
	public ResponseEntity<UserResponse> addUserPreregister(@RequestBody final Map<String, Object> request) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException, URISyntaxException, CertificateException
	{
		if(Objects.isNull(request.get(USER_ID)))
		{
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		String userID = request.get(USER_ID).toString();
		//Validate phone or email
		boolean isnumber = false;
		final String originalUserID = userID;
		if (validationService.validateIsANumber(userID))
		{
			isnumber = true;
			if (!validationService.is10DigitPhoneNumber(userID))
			{
				final mx.com.telcel.models.Error err = new mx.com.telcel.models.Error();
				err.setCode(-5);
				err.setDescription(Localization.getLocalizedString("login.validation.phonenumber.label"));
				final UserResponse response = new UserResponse();
				response.setError(err);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			else if (validationService.is10DigitPhoneNumber(userID))
			{
				userID = NUMBER_USERNAME_PREFIX + userID;
			}
		}
		if (!validationService.validateIsAnEmail(userID) && !isnumber)
		{
			final mx.com.telcel.models.Error err = new mx.com.telcel.models.Error();
			err.setCode(-6);
			err.setDescription(Localization.getLocalizedString("login.validation.email.label"));
			final UserResponse response = new UserResponse();
			response.setError(err);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}

		final UserRequest userRequest = new UserRequest();
		userRequest.setSource(Config.getParameter(SOURCE_PROPERTY));
		// Set User
		final User user = new User();
		user.setUserpreferredlanguage(Config.getParameter(USER_PREFERRED_LANGUAGE_PROPERTY));
		user.setLocale(Config.getParameter(LOCALE_PROPERTY));
		final String[] loginName = new String[]
		{ userID };
		user.setLoginname(loginName);
		user.setTimezone(Config.getParameter(TIMEZONE_PROPERTY));
		user.setCreationsource(Config.getParameter(CREATION_SOURCE_NVATIENDATL_PROPERTY));
		if (isnumber)
		{
			user.setPhonenumberone(userID);
		}
		else
		{
			user.setEmailone(userID);
		}
		user.setActive(true);
		user.setRegistrationcompleted(false);
		userRequest.setUser(user);
		// Set Parameters
		final Parameter parameter = new Parameter();
		parameter.setKey(Config.getParameter(KEY_PROPERTY));
		if (isnumber)
		{
			parameter.setValue(Config.getParameter(VALUE_PROPERTY));
		}
		else
		{
			parameter.setValue(Config.getParameter(VALUE_LEGACY_PROPERTY));
		}
		parameter.setType(Config.getParameter(TYPE_PROPERTY));
		final Parameter[] parameters = new Parameter[]
		{ parameter };
		userRequest.setParameters(parameters);
		//Add user SSO
		final UserResponse userResponse = telcelSSOService.addUser(userRequest);
		if (userResponse != null && userResponse.getError().getCode() != ITS_NOT_AN_OPERATORS_USER_CODE)
		{
			createCustomer(originalUserID);
		}
		return new ResponseEntity<>(userResponse, HttpStatus.OK);
	}

	/**
	 * URL : http://localhost:9001/telcelb2cwebservices/registration/updateUserPreregister</br>
	 * Method : POST</br>
	 * Header : Content-Type=application/json</br>
	 *
	 * @return the response entity
	 * @throws KeyManagementException
	 *            the key management exception
	 * @throws NoSuchAlgorithmException
	 *            the no such algorithm exception
	 * @throws KeyStoreException
	 *            the key store exception
	 * @throws IOException
	 *            the io exception
	 * @throws URISyntaxException
	 *            the uri syntax exception
	 * @throws CertificateException
	 *            the certificate exception
	 */
	@PostMapping("/updateUserPreregister")
	@CacheControl(directive = CacheControlDirective.PUBLIC, maxAge = 1800)
	@ResponseBody
	@ApiOperation(value = "Update User Registration Data", notes = "Method to update user registration data", produces = "application/json,application/xml", authorizations =
	{ @Authorization(value = CLIENT_CREDENTIAL_AUTHORIZATION_NAME), @Authorization(value = PASSWORD_AUTHORIZATION_NAME) })
	public ResponseEntity<UserResponse> updateUserPreregister(@Valid @RequestBody UserPreregisterRequest request) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException, URISyntaxException, CertificateException
	{
		//Validate phone or email
		boolean isnumber = false;
		final String originalUserID = request.getUserID();
		String userID = originalUserID;
		//String name = "";
		if (validationService.validateIsANumber(userID))
		{
			isnumber = true;
			if (!validationService.is10DigitPhoneNumber(userID))
			{
				final mx.com.telcel.models.Error err = new mx.com.telcel.models.Error();
				err.setCode(-5);
				err.setDescription(Localization.getLocalizedString("login.validation.phonenumber.label"));
				final UserResponse response = new UserResponse();
				response.setError(err);

				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			else if (validationService.is10DigitPhoneNumber(userID))
			{
				userID = NUMBER_USERNAME_PREFIX + userID;
			}
		}
		if (!validationService.validateIsAnEmail(userID) && !isnumber)
		{
			final mx.com.telcel.models.Error err = new mx.com.telcel.models.Error();
			err.setCode(-6);
			err.setDescription(Localization.getLocalizedString("login.validation.email.label"));
			final UserResponse response = new UserResponse();
			response.setError(err);

			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		//Validate is user exist in sso
		final UserResponse lookupUser = telcelSSOService.lookupUser(userID);
		if (lookupUser.getError().getCode() == NOT_FOUND_CUSTOMER_CODE)
		{
			final mx.com.telcel.models.Error err = new mx.com.telcel.models.Error();
			err.setCode(-7);
			err.setDescription(Localization.getLocalizedString("login.validation.usernotfound.label"));
			final UserResponse response = new UserResponse();
			response.setError(err);

			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		if (lookupUser.getError().getCode() == SUCCESS)
		{
			if (lookupUser.getUser().getPasswordfailedattemptscounter() != 0)
			{
				final mx.com.telcel.models.Error err = new mx.com.telcel.models.Error();
				err.setCode(-8);
				err.setDescription(Localization.getLocalizedString("login.validation.blockaccount.label"));
				final UserResponse response = new UserResponse();
				response.setError(err);

				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			if (Objects.nonNull(lookupUser.getUser().isActive()))
			{
				if (!lookupUser.getUser().isActive() && lookupUser.getUser().getPasswordfailedattemptscounter() != 0)
				{
					final mx.com.telcel.models.Error err = new mx.com.telcel.models.Error();
					err.setCode(-8);
					err.setDescription(Localization.getLocalizedString("login.validation.blockaccount.label"));
					final UserResponse response = new UserResponse();
					response.setError(err);

					return new ResponseEntity<>(response, HttpStatus.OK);
				}
			}
		}
		//Validate password
		if (!request.getNewPassword().equals(request.getConfirmNewPassword()))
		{
			final mx.com.telcel.models.Error err = new mx.com.telcel.models.Error();
			err.setCode(-2);
			err.setDescription(Localization.getLocalizedString("user.password.change"));
			final UserResponse response = new UserResponse();
			response.setError(err);

			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		//Validate new password with sso rules
		if (!validationService.validatePasswordSSO(request.getNewPassword()))
		{
			final mx.com.telcel.models.Error err = new mx.com.telcel.models.Error();
			err.setCode(-1);
			final StringBuilder strBuilder = new StringBuilder();
			strBuilder.append(Localization.getLocalizedString("user.password.sso.rules.1") + "\n");
			strBuilder.append(Localization.getLocalizedString("user.password.sso.rules.2") + "\n");
			strBuilder.append(Localization.getLocalizedString("user.password.sso.rules.3") + "\n");
			strBuilder.append(Localization.getLocalizedString("user.password.sso.rules.4") + "\n");
			strBuilder.append(Localization.getLocalizedString("user.password.sso.rules.5"));
			err.setDescription(strBuilder.toString());
			final UserResponse response = new UserResponse();
			response.setError(err);

			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		//Validate second phone or email
		boolean existSecondLoginName = false;
		if (!Strings.isNullOrEmpty(request.getPhoneEmail()))
		{
			final UserResponse urValidate = validatePhoneEmail(request.getPhoneEmail());
			if (urValidate.getError().getCode() != 0)
			{

				return new ResponseEntity<>(urValidate, HttpStatus.OK);
			}
			//Validate if second phone or email loginname exists in sso
			final UserResponse lookupUserSecond = telcelSSOService.lookupUser(urValidate.getError().getDescription());
			if (lookupUserSecond.getError().getCode() == SUCCESS)
			{
				existSecondLoginName = true;
			}
		}
		else
		{
			existSecondLoginName = true;
		}

		final String id = lookupUser.getUser().getId();
		final Parameter[] parameters = new Parameter[2];
		final Parameter param = new Parameter();
		param.setKey(Config.getParameter(KEY_PROPERTY));
		param.setValue(Config.getParameter(VALUE_LEGACY_PROPERTY));
		param.setType(Config.getParameter(TYPE_PROPERTY));
		parameters[0] = param;
		final Parameter paramMod = new Parameter();
		final User userMod = new User();
		if (!existSecondLoginName)
		{
			boolean registerEmail = false;
			boolean registerPhone = false;
			if (Objects.nonNull(lookupUser.getUser().getEmailone()))
			{
				if (!Strings.isNullOrEmpty(lookupUser.getUser().getEmailone()))
				{
					registerEmail = true;
				}
			}
			if (Objects.nonNull(lookupUser.getUser().getPhonenumberone()))
			{
				if (!Strings.isNullOrEmpty(lookupUser.getUser().getPhonenumberone()))
				{
					registerPhone = true;
				}
			}
			if (registerEmail && !registerPhone && !Strings.isNullOrEmpty(request.getPhoneEmail()))
			{
				userMod.setPhonenumberone(NUMBER_USERNAME_PREFIX+request.getPhoneEmail());
				final String[] loginName = new String[]
				{ lookupUser.getUser().getLoginname()[0]};
				userMod.setLoginname(loginName);
			}
			if (registerPhone && !registerEmail && !Strings.isNullOrEmpty(request.getPhoneEmail()))
			{
				userMod.setEmailone(request.getPhoneEmail());
				userMod.setEmailverifiedone(false);
				final String[] loginName = new String[]
				{ lookupUser.getUser().getLoginname()[0], request.getPhoneEmail() };
				userMod.setLoginname(loginName);
			}
		}
		userMod.setPassword(request.getNewPassword());

		/*ArrayList<String> surnames = separateSurnames(request.getCompleteName());
		if(surnames.size() == 1){
			userMod.setUsergivenname(surnames.get(0).trim());
			name = surnames.get(0).trim();
		} else {
			if(surnames.size() > 2) {
				if(surnames.size() > 3){
					name = surnames.get(0).trim()+ " " + surnames.get(1).trim();
					userMod.setUsergivenname(surnames.get(0).trim()+ " " + surnames.get(1).trim());
					userMod.setUserfamilyname(surnames.get(2).trim() + " " + surnames.get(3).trim());
				}
				else {
					name = surnames.get(0).trim();
					userMod.setUsergivenname(surnames.get(0).trim());
					userMod.setUserfamilyname(surnames.get(1).trim() + " " + surnames.get(2).trim());
				}
			} else {
				name = surnames.get(0).trim();
				userMod.setUsergivenname(surnames.get(0).trim());
				userMod.setUserfamilyname(surnames.get(1).trim());
			}
		}*/

		userMod.setUsergivenname(request.getFirstName());
		userMod.setUserfamilyname(request.getLastName());
		final String userMod_json = new Gson().toJson(userMod);
		paramMod.setKey("modifyUser");
		paramMod.setValue(userMod_json);
		paramMod.setType("com.hp.sso.provisioning.entities.User");
		parameters[1] = paramMod;

		final User user = new User();
		user.setLoginname(new String[]
		{ userID });
		user.setId(id);

		final UserRequest userRequest = new UserRequest();
		userRequest.setSource(Config.getParameter(SOURCE_PROPERTY));
		userRequest.setUser(user);
		userRequest.setParameters(parameters);

		final UserResponse userResponse = telcelSSOService.modifyUser(userRequest);
		//Enviar Email de bienvenida
		try
		{
			if (!isnumber && userResponse.getError().getCode() == SUCCESS)
			{
				final CustomerModel customerModelS = (CustomerModel) userService.getUserForUID(originalUserID);
				customerModelS.setName(request.getFirstName());
				customerModelS.setLastName(request.getLastName());
				customerModelS.setNumberPhone(request.getPhoneEmail());
				modelService.save(customerModelS);
				//final CustomerModel customerModel = (CustomerModel) userService.getUserForUID(originalUserID);
				//telcelEmailsHelper.sendWelcomeEmail(customerModel);
			} else if (isnumber && userResponse.getError().getCode() == SUCCESS){
				final CustomerModel customerModelS = (CustomerModel) userService.getUserForUID(originalUserID);
				customerModelS.setName(request.getFirstName());
				customerModelS.setLastName(request.getLastName());
				modelService.save(customerModelS);
			}
		}
		catch (final Exception e)
		{
			LOG.warn("Error sending welcome email", e);
		}
		if (validationService.validateIsAnEmail(request.getPhoneEmail())) {
			telcelCustomerFacade.updateEmail(originalUserID,request.getPhoneEmail());
		}

		return new ResponseEntity<>(userResponse, HttpStatus.OK);
	}

	private Boolean userExistsInSSO(final String userID) throws KeyManagementException, NoSuchAlgorithmException,
			KeyStoreException, IOException, URISyntaxException, CertificateException
	{
		LOG.info("RegistrationController.userExistsInSSO userID ["+userID+"]");

		final UserResponse userResponse = telcelSSOService.lookupUser(userID);
		if (userResponse != null)
		{
			if (userResponse.getError().getCode() == NOT_FOUND_CUSTOMER_CODE)
			{
				LOG.info("RegistrationController.userExistsInSSO userID ["+userID+"] NOT_FOUND_CUSTOMER_CODE");
				return false;
			}
			else
			{
				LOG.info("RegistrationController.userExistsInSSO userID ["+userID+"] FOUND");
				return true;
			}
		}
		return null;
	}

	private UserResponse validatePhoneEmail(String userID)
	{
		boolean isnumber = false;
		if (validationService.validateIsANumber(userID))
		{
			isnumber = true;
			if (!validationService.is10DigitPhoneNumber(userID))
			{
				final mx.com.telcel.models.Error err = new mx.com.telcel.models.Error();
				err.setCode(-5);
				err.setDescription(Localization.getLocalizedString("login.validation.phonenumber.label"));
				final UserResponse response = new UserResponse();
				response.setError(err);
				return response;
			}
			else if (validationService.is10DigitPhoneNumber(userID))
			{
				userID = NUMBER_USERNAME_PREFIX + userID;
			}
		}
		if (!validationService.validateIsAnEmail(userID) && !isnumber)
		{
			final mx.com.telcel.models.Error err = new mx.com.telcel.models.Error();
			err.setCode(-6);
			err.setDescription(Localization.getLocalizedString("login.validation.email.label"));
			final UserResponse response = new UserResponse();
			response.setError(err);
			return response;
		}
		final mx.com.telcel.models.Error err = new mx.com.telcel.models.Error();
		err.setCode(0);
		err.setDescription(userID);
		final UserResponse response = new UserResponse();
		response.setError(err);
		return response;
	}

	public static ArrayList<String> separateSurnames(String nombre) {
		String[] articulos = new String[]{"de", "del", "la", "los", "las", "De", "Del", "La", "Los", "Las", "o", "O", "Mac", "mac", "di", "Di", "da", "do", "dos", "san", "d"};
		ArrayList<String> nombreArrayList = new ArrayList<String>();
		String[] nameSurnameTemp = nombre.split(" ");
		String aux = "";
		for (String i : nameSurnameTemp) {
			if (Arrays.stream(articulos).anyMatch(i::equals)) {
				aux += " "+i;
			} else {
				nombreArrayList.add(aux + " "+i);
				aux = "";
			}
		}
		return nombreArrayList;
	}

	public void saveTokenNip(String token, String userId){
		RestorePasswordTokenModel restorePasswordToken = new RestorePasswordTokenModel();
		restorePasswordToken.setToken(token);
		restorePasswordToken.setUserId(userId);
		modelService.save(restorePasswordToken);
	}

	public static String removePrefix(String s, String prefix)
	{
		if (s != null && s.startsWith(prefix)) {
			return s.split(prefix, 2)[1];
		}
		return s;
	}

}
