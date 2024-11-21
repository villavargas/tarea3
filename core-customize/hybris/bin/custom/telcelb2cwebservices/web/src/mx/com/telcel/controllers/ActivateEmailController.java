package mx.com.telcel.controllers;

import com.google.gson.Gson;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Map;

import javax.annotation.Resource;

import de.hybris.platform.util.Config;
import de.hybris.platform.util.localization.Localization;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import mx.com.telcel.models.Parameter;
import mx.com.telcel.models.User;
import mx.com.telcel.request.models.UserRequest;
import mx.com.telcel.response.models.UserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import mx.com.telcel.response.models.MessageResponse;
import mx.com.telcel.services.TelcelSSOService;

import static mx.com.telcel.constants.Telcelb2cwebservicesConstants.CLIENT_CREDENTIAL_AUTHORIZATION_NAME;
import static mx.com.telcel.constants.Telcelb2cwebservicesConstants.PASSWORD_AUTHORIZATION_NAME;


@Controller
@RequestMapping(value = "/activate")
@Api(tags = "Activate Email SSO")
public class ActivateEmailController
{
	private static final int SUCCESS = 0;
	public static final int NOT_FOUND_CUSTOMER_CODE = 114;

	private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);
	private static final String WEBSITE_TELCEL = "website.telcel.external.https";
	private static final String LOGIN = "/login";
	private static final String EMAIL = "email";

	private static final String SOURCE_PROPERTY = "telcelb2cwebservices.registration.user.source";
	private static final String KEY_PROPERTY = "telcelb2cwebservices.registration.parameter.key";
	private static final String VALUE_LEGACY_PROPERTY = "telcelb2cwebservices.registration.parameter.legacy.value";
	private static final String TYPE_PROPERTY = "telcelb2cwebservices.registration.parameter.type";

	@Resource(name = "telcelSSOService")
	private TelcelSSOService telcelSSOService;

	@Resource(name = "defaultConfigurationService")
	private ConfigurationService configurationService;
	/**
	 * URL : http://localhost:9001/telcelb2cwebservices/activate/resend/key</br>
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
	 *            **/
	@PostMapping("/resend/key")
	@ResponseBody
	@ApiOperation(value = "Update User Registration Send Mail Validation", notes = "Method to send email validation SSO", produces = "application/json,application/xml", authorizations =
			{ @Authorization(value = CLIENT_CREDENTIAL_AUTHORIZATION_NAME), @Authorization(value = PASSWORD_AUTHORIZATION_NAME) })
	public ResponseEntity<MessageResponse> resendEmail(@RequestBody final Map<String, Object> request)throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException
	{
		String email;
		try{
			email = request.get(EMAIL).toString();
		}catch (NullPointerException | ClassCastException ex)
		{
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		LOG.info("RESEND KEY");
		LOG.info("email : " + email);
		final MessageResponse messageResponse = telcelSSOService.resendKeyEmail(email);
		return new ResponseEntity<>(messageResponse,HttpStatus.OK);
	}

	@GetMapping(value = "/email")
	public ResponseEntity<Void> email(@RequestParam(name = "ACTIVATION_KEY") final String activationKey,
									  @RequestParam(name = "EMAIL") final String email) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException, URISyntaxException, CertificateException
	{
		LOG.info("ACTIVATE EMAIL");
		LOG.info("KEY : " + activationKey);
		LOG.info("EMAIL : " + email);
		final String urlTiendaLogin = getLoginUrl();
		final MessageResponse response = telcelSSOService.activateEmail(activationKey);
		if (response.getResponseCode() == String.valueOf(HttpStatus.OK.value()))
		{
			final UserResponse lookupUser = telcelSSOService.lookupUser(email);
			if (lookupUser.getError().getCode() == NOT_FOUND_CUSTOMER_CODE)
			{
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
			if (lookupUser.getError().getCode() == SUCCESS)
			{
				if (!lookupUser.getUser().isActive() && lookupUser.getUser().getPasswordfailedattemptscounter() != 0)
				{
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
			userMod.setRegistrationcompleted(true);
			final String userMod_json = new Gson().toJson(userMod);
			paramMod.setKey("modifyUser");
			paramMod.setValue(userMod_json);
			paramMod.setType("com.hp.sso.provisioning.entities.User");
			parameters[1] = paramMod;

			final User user = new User();
			user.setLoginname(new String[]
					{ email });
			user.setId(id);

			final UserRequest userRequest = new UserRequest();
			userRequest.setSource(Config.getParameter(SOURCE_PROPERTY));
			userRequest.setUser(user);
			userRequest.setParameters(parameters);

			final UserResponse userResponse = telcelSSOService.modifyUserSendMail(userRequest);
			if (userResponse.getError().getCode() == SUCCESS){
				return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(urlTiendaLogin)).build();
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}

		}
		else
		{
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping(value = "/email2")
	public ResponseEntity<Void> email2(@RequestParam(name = "ACTIVATION_KEY")
									  final String activationKey) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException
	{
		LOG.info("ACTIVATE EMAIL");
		LOG.info("KEY : " + activationKey);
		final String urlTiendaLogin = getLoginUrl();
		final MessageResponse response = telcelSSOService.activateEmail(activationKey);
		if (response.getResponseCode() == String.valueOf(HttpStatus.OK.value()))
		{
			return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(urlTiendaLogin)).build();
		}
		else
		{
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	private String getLoginUrl()
	{
		return configurationService.getConfiguration().getString(WEBSITE_TELCEL)+LOGIN;
	}
}
