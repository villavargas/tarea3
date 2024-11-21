package mx.com.telcel.controllers;

import com.google.gson.Gson;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.CustomerModel;
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
import mx.com.telcel.core.model.RestorePasswordTokenModel;
import mx.com.telcel.models.ErrorRestorePassword;
import mx.com.telcel.models.Parameter;
import mx.com.telcel.models.ResponseRestorePassword;
import mx.com.telcel.models.User;
import mx.com.telcel.request.models.UserPreregisterRequest;
import mx.com.telcel.request.models.UserRequest;
import mx.com.telcel.response.models.ErrorResponse;
import mx.com.telcel.response.models.UserResponse;
import mx.com.telcel.services.CustomValidationService;
import mx.com.telcel.services.TelcelSSOService;
import org.apache.log4j.Logger;
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
import java.util.Arrays;
import java.util.Collections;

import static mx.com.telcel.constants.Telcelb2cwebservicesConstants.CLIENT_CREDENTIAL_AUTHORIZATION_NAME;
import static mx.com.telcel.constants.Telcelb2cwebservicesConstants.PASSWORD_AUTHORIZATION_NAME;


@Controller
@RequestMapping(value = "/sso/restore")
@Api(tags = "RestorePassword")
public class RestorePasswordController
{

	private static final Logger LOG = Logger.getLogger(RestorePasswordController.class);
	private static final int SUCCESS = 0;
	public static final int NOT_FOUND_CUSTOMER_CODE = 114;

	private static final String SOURCE_PROPERTY = "telcelb2cwebservices.registration.user.source";

	private static final String KEY_PROPERTY = "telcelb2cwebservices.registration.parameter.key";
	private static final String VALUE_LEGACY_PROPERTY = "telcelb2cwebservices.registration.parameter.legacy.value";
	private static final String TYPE_PROPERTY = "telcelb2cwebservices.registration.parameter.type";

	@Resource(name = "telcelSSOService")
	private TelcelSSOService telcelSSOService;

	@Resource(name = "customValidationService")
	private CustomValidationService validationService;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "tokenNipDao")
	private TokenNipDao tokenNipDao;

	/**
	 * Service that restore password and send via phone/email<br/>
	 * Example :</br>
	 * GET
	 * https://localhost:9002/telcelb2cwebservices/sso/restore/password1st?userId=521234567890&clientId=appsso_demo</br>
	 * Method : GET</br>
	 *
	 * @param userId
	 *           - phonenumber[10 digits]/email[nombre@dominio]</br>
	 * @param clientId
	 *           - client id</br>
	 * @return - ErrorResponse object</br>
	 *
	 */
	@ResponseBody
	@GetMapping(value = "/password1st", produces = "application/json")
	@ApiOperation(value = "Restore Password Method", notes = "This Method restore and send password via phone number or email", produces = "application/json", authorizations =
	{ @Authorization(value = CLIENT_CREDENTIAL_AUTHORIZATION_NAME), @Authorization(value = PASSWORD_AUTHORIZATION_NAME) })
	public ErrorResponse password1stUser(@ApiParam(value = "User loginname (phonenumber/email)", required = true)
	@RequestParam(name = "userId", required = true)
	String userId, @ApiParam(value = "Id of the client", required = true)
	@RequestParam(name = "clientId", required = true)
	final String clientId)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException, URISyntaxException
	{
		LOG.info("Restore Password Service ...");
		if (validationService.is10DigitPhoneNumber(userId))
		{
			userId = "52" + userId;
		}
		return telcelSSOService.password1stUser(userId, clientId);
	}

	/**
	 * URL : http://localhost:9001/telcelb2cwebservices/sso/restore/password</br>
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
	@PostMapping("/password")
	@CacheControl(directive = CacheControlDirective.PUBLIC, maxAge = 1800)
	@ResponseBody
	@ApiOperation(value = "Restore Password", notes = "Method to restore password", produces = "application/json,application/xml", authorizations =
			{ @Authorization(value = CLIENT_CREDENTIAL_AUTHORIZATION_NAME), @Authorization(value = PASSWORD_AUTHORIZATION_NAME) })
	public ResponseEntity<ResponseRestorePassword> updateUserPreregister(@Valid @RequestBody UserPreregisterRequest request) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException, URISyntaxException, CertificateException
	{
		//Validate phone or email

		boolean isnumber = false;
		String userID = request.getUserID();
		String tokenNip = request.getToken();

		ErrorRestorePassword errorPass = new ErrorRestorePassword();
		RestorePasswordTokenModel restorePasswordTokenModel = tokenNipDao.findUserAndToken(userID);
		if(restorePasswordTokenModel != null && restorePasswordTokenModel.getToken().equals(tokenNip)){
			modelService.remove(restorePasswordTokenModel);
			boolean flagLoginName = false;
			if (validationService.validateIsANumber(userID))
			{
				isnumber = true;

				if (!validationService.is10DigitPhoneNumber(userID))
				{
					//final mx.com.telcel.models.Error err = new mx.com.telcel.models.Error();
					final ErrorRestorePassword err = new ErrorRestorePassword();
					err.setCode(-5);
					err.setDescription(Localization.getLocalizedString("login.validation.phonenumber.label"));
				/*final UserResponse response = new UserResponse();
				response.setError(err);*/
					final ResponseRestorePassword response = new ResponseRestorePassword();
					response.setError(err);
					return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
				}
				else if (validationService.is10DigitPhoneNumber(userID))
				{
					userID = "52" + userID;
				}
			}
			if (!validationService.validateIsAnEmail(userID) && !isnumber)
			{
				//final mx.com.telcel.models.Error err = new mx.com.telcel.models.Error();
				final ErrorRestorePassword err = new ErrorRestorePassword();
				err.setCode(-6);
				err.setDescription(Localization.getLocalizedString("login.validation.email.label"));
			/*final UserResponse response = new UserResponse();
			response.setError(err);*/
				final ResponseRestorePassword response = new ResponseRestorePassword();
				response.setError(err);
				return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);

			}
			//Validate password
			if (!request.getNewPassword().equals(request.getConfirmNewPassword()))
			{
				//final mx.com.telcel.models.Error err = new mx.com.telcel.models.Error();
				final ErrorRestorePassword err = new ErrorRestorePassword();
				err.setCode(-2);
				err.setDescription(Localization.getLocalizedString("user.password.change"));
			/*final UserResponse response = new UserResponse();
			response.setError(err);*/
				final ResponseRestorePassword response = new ResponseRestorePassword();
				response.setError(err);
				return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
			}
			//Validate new password with sso rules
			if (!validationService.validatePasswordSSO(request.getNewPassword()))
			{
				//final mx.com.telcel.models.Error err = new mx.com.telcel.models.Error();
				final ErrorRestorePassword err = new ErrorRestorePassword();
				err.setCode(-1);
				final StringBuilder strBuilder = new StringBuilder();
				strBuilder.append(Localization.getLocalizedString("user.password.sso.rules.1") + "\n");
				strBuilder.append(Localization.getLocalizedString("user.password.sso.rules.2") + "\n");
				strBuilder.append(Localization.getLocalizedString("user.password.sso.rules.3") + "\n");
				strBuilder.append(Localization.getLocalizedString("user.password.sso.rules.4") + "\n");
				strBuilder.append(Localization.getLocalizedString("user.password.sso.rules.5"));
				err.setDescription(strBuilder.toString());
			/*final UserResponse response = new UserResponse();
			response.setError(err);*/
				final ResponseRestorePassword response = new ResponseRestorePassword();
				response.setError(err);
				return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
			}

			final UserResponse lookupUser = telcelSSOService.lookupUser(userID);
			final String id = lookupUser.getUser().getId();

			final Parameter[] parameters = new Parameter[2];
			final Parameter param = new Parameter();
			param.setKey(Config.getParameter(KEY_PROPERTY));
			param.setValue(Config.getParameter(VALUE_LEGACY_PROPERTY));
			param.setType(Config.getParameter(TYPE_PROPERTY));
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
					{ userID });
			user.setId(id);

			final UserRequest userRequest = new UserRequest();
			userRequest.setSource(SOURCE_PROPERTY);
			userRequest.setUser(user);
			userRequest.setParameters(parameters);


			UserResponse userResponse = telcelSSOService.modifyUserSendMail(userRequest);


			
			flagLoginName = userService.isUserExisting(request.getUserID());
			LOG.info("Existe usuario en Commerce: "+flagLoginName);
			errorPass.setCode(userResponse.getError().getCode());
			errorPass.setDescription(userResponse.getError().getDescription());
			if (flagLoginName){
				errorPass.setLoginName(request.getUserID());
			} else {
				String[] login = lookupUser.getUser().getLoginname();// se recupera el número de login o login´s
				if(login.length == 2){ //se compara
					String item = userID;
					String[] newLogin = null;
					newLogin = (String[]) removeLogin(login, item); // se suprime el atributo ingresa en formaulario
					System.out.println(Arrays.toString(login));
					if(isnumber){
						flagLoginName = userService.isUserExisting(newLogin[0]);//se pregunta si existe el usuario en commerce
					} else {
						flagLoginName = userService.isUserExisting(removePrefix(newLogin[0], "52"));//se pregunta si existe el usuario en commerce
					}
					if(flagLoginName){ // se valida el booleano con el otro login en el usuario
						if(isnumber){
							errorPass.setLoginName(newLogin[0]);
						} else {
							errorPass.setLoginName(removePrefix(newLogin[0], "52"));
						}
					} else {
						createCustomer(request.getUserID(), lookupUser);
						errorPass.setLoginName(request.getUserID());
					}
				} else{ //si solo tiene un login name y no existe en commerce
					createCustomer(request.getUserID(), lookupUser);
					errorPass.setLoginName(request.getUserID());
				}
			} //condición para saber si el usuario existe de inicio
		} else {
			if(restorePasswordTokenModel != null){
				modelService.remove(restorePasswordTokenModel);
			}
			final ErrorRestorePassword err = new ErrorRestorePassword();
			err.setCode(-6);
			err.setDescription(Localization.getLocalizedString("login.validation.email.label"));
			final ResponseRestorePassword response = new ResponseRestorePassword();
			response.setError(err);
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		final ResponseRestorePassword response = new ResponseRestorePassword();
		response.setError(errorPass);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	public static String[] removeLogin(String[] arr, String item) {
		return Arrays.stream(arr)
				.filter(s -> !s.equals(item))
				.toArray(String[]::new);
	}

	public static String removePrefix(String s, String prefix)
	{
		if (s != null && s.startsWith(prefix)) {
			return s.split(prefix, 2)[1];
		}
		return s;
	}

	private CustomerModel createCustomer(final String userID, final UserResponse userResponse)
	{
		final CustomerModel customerModel = modelService.create(CustomerModel.class);

		customerModel.setUid(userID);
		customerModel.setGroups(Collections.singleton((PrincipalGroupModel) userService.getUserGroupForUID("customergroup")));
		if (validationService.validateIsAnEmail(userID))
		{
			customerModel.setDetailEmail(userID);
			if(userResponse.getUser().getPhonenumberone() != null){
				customerModel.setNumberPhone(userResponse.getUser().getPhonenumberone());
			} else {
				customerModel.setNumberPhone("");
			}
		}
		else
		{
			customerModel.setNumberPhone(userID);
			if(userResponse.getUser().getEmailone() != null){
				customerModel.setDetailEmail(userResponse.getUser().getEmailone());
			} else {
				customerModel.setDetailEmail("");
			}
		}
		if(userResponse.getUser().getUsergivenname() != null){
			customerModel.setName(userResponse.getUser().getUsergivenname());
		} else {
			customerModel.setName("-");
		}
		if(userResponse.getUser().getUserfamilyname() != null){
			customerModel.setLastName(userResponse.getUser().getUserfamilyname());
		}

		customerModel.setCustomerID(userID);
		customerModel.setLoginDisabled(false);
		modelService.save(customerModel);
		return customerModel;
	}
}