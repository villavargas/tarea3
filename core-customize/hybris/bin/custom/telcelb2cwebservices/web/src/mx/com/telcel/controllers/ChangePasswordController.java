/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.controllers;

import static mx.com.telcel.constants.Telcelb2cwebservicesConstants.CLIENT_CREDENTIAL_AUTHORIZATION_NAME;
import static mx.com.telcel.constants.Telcelb2cwebservicesConstants.PASSWORD_AUTHORIZATION_NAME;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.util.localization.Localization;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.annotation.Resource;
import javax.validation.Valid;

import mx.com.telcel.request.models.ChangePasswordRequest;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.google.gson.Gson;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import mx.com.telcel.constants.AuthorizationType;
import mx.com.telcel.models.Authorize;
import mx.com.telcel.models.Parameter;
import mx.com.telcel.models.User;
import mx.com.telcel.request.models.UserRequest;
import mx.com.telcel.response.models.AuthorizationResponse;
import mx.com.telcel.response.models.UserResponse;
import mx.com.telcel.services.CustomValidationService;
import mx.com.telcel.services.TelcelSSOService;


@Controller
@RequestMapping(value = "/sso/change")
@Api(tags = "ChangePassword")
public class ChangePasswordController
{

	private static final Logger LOG = Logger.getLogger(ChangePasswordController.class);
	private static final int USER_NOT_FOUND = 114;
	private static final int SUCCESS = 0;
	private static final String CLIENT_ID = "telcel.sso.authorize.client.id";

	@Resource(name = "telcelSSOService")
	private TelcelSSOService telcelSSOService;

	@Resource(name = "customValidationService")
	private CustomValidationService validationService;


	@Resource(name = "defaultConfigurationService")
	private ConfigurationService configurationService;


	/**
	 * Service that change password<br/>
	 * Example :</br>
	 * POST https://localhost:9002/telcelb2cwebservices/sso/change/password?userId=521234567890</br>
	 * Header </br>
	 * currentPass : ABC_123</br>
	 * newPass : 123qweZ</br>
	 *
	 * Method : POST</br>
	 * @return - UserResponse object</br>
	 * @throws IOException
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 * @throws URISyntaxException
	 * @throws CertificateException
	 *
	 */

	@ResponseBody
	@PostMapping("/password")
	@ApiOperation(value = "Change Password Method", notes = "This Method change password", produces = "application/json", authorizations =
	{ @Authorization(value = CLIENT_CREDENTIAL_AUTHORIZATION_NAME), @Authorization(value = PASSWORD_AUTHORIZATION_NAME) })
	public UserResponse changePassword(@Valid @RequestBody ChangePasswordRequest request) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException, URISyntaxException
	{
		try
		{
			String userId = request.getUserID();
			LOG.info("Change Password Service ...");
			boolean isnumber = false;
			if (validationService.validateIsANumber(userId))
			{
				isnumber = true;
				if (!validationService.is10DigitPhoneNumber(userId))
				{
					final mx.com.telcel.models.Error err = new mx.com.telcel.models.Error();
					err.setCode(-5);
					err.setDescription(Localization.getLocalizedString("login.validation.phonenumber.label"));
					final UserResponse response = new UserResponse();
					response.setError(err);
					return response;
				}
				else if (validationService.is10DigitPhoneNumber(userId))
				{
					userId = "52" + userId;
				}
			}
			if (!validationService.validateIsAnEmail(userId) && !isnumber)
			{
				final mx.com.telcel.models.Error err = new mx.com.telcel.models.Error();
				err.setCode(-6);
				err.setDescription(Localization.getLocalizedString("login.validation.email.label"));
				final UserResponse response = new UserResponse();
				response.setError(err);
				return response;
			}
			//Validate is user exist in sso
			final UserResponse lookupUser = telcelSSOService.lookupUser(userId);
			if (lookupUser.getError().getCode() == USER_NOT_FOUND)
			{
				final mx.com.telcel.models.Error err = new mx.com.telcel.models.Error();
				err.setCode(-7);
				err.setDescription(Localization.getLocalizedString("login.validation.usernotfound.label"));
				final UserResponse response = new UserResponse();
				response.setError(err);
				return response;
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
					return response;
				}
			}

			final String id = lookupUser.getUser().getId();
			//Validate current password user vs user password model
			final Authorize auth = new Authorize();
			final String clientId = configurationService.getConfiguration().getString(CLIENT_ID);
			auth.setClienteId(clientId);
			auth.setResponseType("code");
			auth.setRedirectUri("tienda.telcel.com");
			auth.setScope("openid");
			auth.setState("0");
			auth.setNonce("0");
			auth.setArcValues("1");
			final AuthorizationResponse authResponse = telcelSSOService.authorize(userId, request.getCurrentPassword(), "", "", auth,
					AuthorizationType.USER_PASSWORD);
			if (authResponse.getCode().trim().equals(""))
			{
				final mx.com.telcel.models.Error err = new mx.com.telcel.models.Error();
				err.setCode(-3);
				err.setDescription(Localization.getLocalizedString("login.validation.password.label"));
				final UserResponse response = new UserResponse();
				response.setError(err);
				return response;
			}
			//Validate new password vs confirm new password
			if (!request.getNewPassword().equals(request.getConfirmNewPassword()))
			{
				final mx.com.telcel.models.Error err = new mx.com.telcel.models.Error();
				err.setCode(-2);
				err.setDescription(Localization.getLocalizedString("user.password.change"));
				final UserResponse response = new UserResponse();
				response.setError(err);
				return response;
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
				return response;
			}

			final Parameter[] parameters = new Parameter[2];
			final Parameter param = new Parameter();
			param.setKey("Version");
			param.setValue("Legacy");
			param.setType("java.lang.String");
			parameters[0] = param;
			final Parameter paramMod = new Parameter();
			final User userMod = new User();
			userMod.setPassword(request.getNewPassword());
			final String userMod_json = new Gson().toJson(userMod);
			paramMod.setKey("modifyUser");
			paramMod.setValue(userMod_json);
			paramMod.setType("com.hp.sso.provisioning.entities.User");
			parameters[1] = paramMod;

			final User user = new User();
			user.setLoginname(new String[]
			{ userId });
			user.setId(id);

			final UserRequest userRequest = new UserRequest();
			userRequest.setSource(request.getSource());
			userRequest.setUser(user);
			userRequest.setParameters(parameters);

			return telcelSSOService.modifyUserSendMail(userRequest);
		}
		catch (final Exception e)
		{
			LOG.error(e);
			LOG.error(e.getMessage());
			final mx.com.telcel.models.Error err = new mx.com.telcel.models.Error();
			err.setCode(-4);
			err.setDescription(Localization.getLocalizedString("login.validation.error.label"));
			final UserResponse response = new UserResponse();
			response.setError(err);
			return response;
		}
	}

}
