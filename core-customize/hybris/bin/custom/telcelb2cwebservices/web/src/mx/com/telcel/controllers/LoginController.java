package mx.com.telcel.controllers;


import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.europe1.enums.UserPriceGroup;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import mx.com.telcel.core.services.TelcelCodigosPostalesService;
import mx.com.telcel.core.util.TelcelUtil;
import mx.com.telcel.facades.holderline.data.TelcelCPRegionData;
import mx.com.telcel.facades.zipcodes.data.CodigosPostalesTelcelData;
import mx.com.telcel.response.models.RevokeResponse;
import mx.com.telcel.response.models.UserInfoResponse;
import mx.com.telcel.services.CustomValidationService;
import mx.com.telcel.services.TelcelSSOService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.*;


/**
 * <li>https://localhost:9002/telcelb2cwebservices/LoginWS/login *
 **/
@Controller
@RequestMapping(value = "/loginNotificationWS")
@Api(tags = "Login Notification")
public class LoginController
{
	private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);
	private static final int USER_NOT_FOUND = 114;
	private static final int SUCCESS = 0;
	private static final String DASH = "-";
	private static final String ACCESS_TOKEN = "accessToken";
	private static final String NUMBER_USERNAME_PREFIX = "52";
	private static final String CLIENT_ID = "telcel.sso.authorize.client.id";
	private static final String RESPONSE_TYPE = "telcel.sso.authorize.response.type";
	private static final String REDIRECT_URI = "telcel.sso.authorize.redirect.uri";
	private static final String SCOPE = "telcel.sso.authorize.scope";
	private static final String STATE = "telcel.sso.authorize.state";
	private static final String NONCE = "telcel.sso.authorize.nonce";
	private static final String ARC_VALUES = "telcel.sso.authorize.arcvalues";

	@Resource(name = "telcelSSOService")
	private TelcelSSOService telcelSSOService;

	@Resource(name = "customValidationService")
	private CustomValidationService validationService;

	@Resource
	private CustomerAccountService customerAccountService;

	@Resource(name = "telcelCodigosPostalesService")
	private TelcelCodigosPostalesService telcelCodigosPostalesService;

	@Resource
	private EnumerationService enumerationService;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource
	private TelcelUtil telcelUtil; // NOSONAR

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "dataMapper")
	private DataMapper dataMapper;

	@ResponseBody
	@PostMapping("/login")
	public ResponseEntity<UserInfoResponse> webLogin(@RequestBody final Map<String, Object> request,
													 final HttpServletResponse httpServletResponse, final HttpServletRequest httpServletRequest)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException {
		if(Objects.isNull(request.get(ACCESS_TOKEN)))
		{
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		final String accessToken = request.get(ACCESS_TOKEN).toString();

		final UserInfoResponse responseUserI = telcelSSOService.userInfo(accessToken);
		String userName = "";
		boolean flagLoginName = false;
		if (validationService.validateIsANumber(responseUserI.getLoginname()))
		{
			if (validationService.is12DigitPhoneNumber(responseUserI.getLoginname()))
			{
				userName = removePrefix(responseUserI.getLoginname(), "52");
				flagLoginName = userService.isUserExisting(userName);
				if (!flagLoginName){
					if(responseUserI.getEmail() != null){
						flagLoginName = userService.isUserExisting(responseUserI.getEmail());
						if(flagLoginName){
							responseUserI.setLoginname(responseUserI.getEmail());
							userName = responseUserI.getLoginname();
							final CustomerModel customerModel = (CustomerModel) userService.getUserForUID(userName);
							customerModel.setName(responseUserI.getGiven_name());
							modelService.save(customerModel);
						} else {
							createCustomer(userName, responseUserI);
						}
					} else {
						createCustomer(userName, responseUserI);
					}
				}
			}
		} else if (validationService.validateIsAnEmail(responseUserI.getLoginname()))
		{
			userName = responseUserI.getLoginname();
			flagLoginName = userService.isUserExisting(userName);
			if (!flagLoginName){
				if(responseUserI.getPhone_number() != null){
					flagLoginName = userService.isUserExisting(removePrefix(responseUserI.getPhone_number(), "52"));
					if(flagLoginName){
						userName = removePrefix(responseUserI.getPhone_number(), "52");;
						responseUserI.setLoginname(userName);
						final CustomerModel customerModel = (CustomerModel) userService.getUserForUID(userName);
						customerModel.setName(responseUserI.getGiven_name());
						modelService.save(customerModel);
					} else {
						createCustomer(userName, responseUserI);
					}
				}
				else {
					createCustomer(userName, responseUserI);
				}

			}
		}
		responseUserI.setErrorCode(0);
		setRegionOnCookieByResponse(httpServletResponse, httpServletRequest, userName);
		httpServletResponse.setHeader("Set-Cookie", "access-token=" + accessToken);
		final String region = telcelUtil.getRegionByRequest(httpServletRequest);
		LOG.info("Region: "+region);
		return new ResponseEntity<>(responseUserI, HttpStatus.OK);
	}

	@ResponseBody
	@GetMapping(value = "/logout", produces = "application/json")
	public ResponseEntity<RevokeResponse> logoutWebLogin(final HttpServletRequest httpServletRequest)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException, URISyntaxException {
		String accessToken = telcelUtil.getCookieByRequest(httpServletRequest, "access-token");

		final RevokeResponse revokeResponse = telcelSSOService.revoke(accessToken);
		return new ResponseEntity<>(revokeResponse, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/currentRegion", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@ApiOperation(nickname = "currentRegionRequest", value = "Get current region.", notes = "Get current region request.",
			consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public TelcelCPRegionData currentRegion(final HttpServletRequest httpServletRequest) {
		final String currentRegion = telcelUtil.getCookieByRequest(httpServletRequest, "tlsr");
		TelcelCPRegionData telcelCPRegionData = new TelcelCPRegionData();
		if (StringUtils.isNotEmpty(currentRegion)) {
			telcelCPRegionData.setRegion(currentRegion.split(DASH)[0]);
			telcelCPRegionData.setCp(currentRegion.split(DASH)[1]);
		}
		return dataMapper.map(telcelCPRegionData, TelcelCPRegionData.class);
	}


	private void setRegionOnCookieByResponse(final HttpServletResponse httpServletResponse,
											 final HttpServletRequest httpServletRequest, final String username) {
		final CustomerModel customerModel = (CustomerModel) userService.getUserForUID(username);
		final String currentRegion = telcelUtil.getRegionByRequest(httpServletRequest);
		if (StringUtils.isNotEmpty(currentRegion)) {
			setAnonymousRegion(httpServletResponse, httpServletRequest, customerModel, currentRegion);
		} else {
			final AddressModel addressModel = customerAccountService.getDefaultAddress(customerModel);
			//Usuario con direccion default asignada
			if (Objects.nonNull(addressModel) && StringUtils.isNotEmpty(addressModel.getPostalcode())) {
				final CodigosPostalesTelcelData codigosPostalesTelcelData = telcelCodigosPostalesService
						.getInfForZipcode(addressModel.getPostalcode());
				if (Objects.nonNull(codigosPostalesTelcelData)) {
					final UserPriceGroup userPriceGroup = enumerationService.getEnumerationValue(UserPriceGroup.class,
							codigosPostalesTelcelData.getRegion().getCode());
					if (!userService.isAnonymousUser(customerModel)) {
						customerModel.setEurope1PriceFactory_UPG(userPriceGroup);
						modelService.save(customerModel);
					}
					httpServletResponse.setHeader("Set-Cookie", "tlsr=" + userPriceGroup.getCode() + DASH
							+ codigosPostalesTelcelData.getCodigo() + "; Path=/; Secure");
				} else {
					setAnonymousRegion(httpServletResponse, httpServletRequest, customerModel, currentRegion);
				}
			} else {
				setAnonymousRegion(httpServletResponse, httpServletRequest, customerModel, currentRegion);
			}
		}
	}

	private void setAnonymousRegion(final HttpServletResponse httpServletResponse, final HttpServletRequest httpServletRequest,
									final CustomerModel customerModel, String region) {
		if (StringUtils.isNotEmpty(region)) {
			final UserPriceGroup userPriceGroup = enumerationService.getEnumerationValue(UserPriceGroup.class, region);
			if (!userService.isAnonymousUser(customerModel)) {
				customerModel.setEurope1PriceFactory_UPG(userPriceGroup);
				modelService.save(customerModel);
			}
			httpServletResponse.setHeader("Set-Cookie",
					"tlsr=" + telcelUtil.getCookieByRequest(httpServletRequest, "tlsr") + "; Path=/; Secure");
		}
	}

	public static String removePrefix(String s, String prefix)
	{
		if (s != null && s.startsWith(prefix)) {
			return s.split(prefix, 2)[1];
		}
		return s;
	}

	private CustomerModel createCustomer(final String userID, final UserInfoResponse userInfoResponse)
	{
		final CustomerModel customerModel = modelService.create(CustomerModel.class);
		customerModel.setUid(userID);
		if (validationService.validateIsAnEmail(userID))
		{
			customerModel.setDetailEmail(userID);
			if(userInfoResponse.getPhone_number() != null){
				customerModel.setNumberPhone(userInfoResponse.getPhone_number());
			} else {
				customerModel.setNumberPhone("");
			}
		}
		else
		{
			customerModel.setNumberPhone(userID);
			if(userInfoResponse.getEmail() != null){
				customerModel.setDetailEmail(userInfoResponse.getEmail());
			} else {
				customerModel.setDetailEmail("");
			}
		}
		customerModel.setGroups(Collections.singleton((PrincipalGroupModel) userService.getUserGroupForUID("customergroup")));
		if(userInfoResponse.getGiven_name() != null){
			customerModel.setName(userInfoResponse.getGiven_name());
		} else {
			customerModel.setName("-");
		}
		if(userInfoResponse.getFamily_name() != null){
			customerModel.setLastName(userInfoResponse.getFamily_name());
		}

		customerModel.setCustomerID(userID);
		customerModel.setLoginDisabled(false);
		modelService.save(customerModel);
		return customerModel;
	}
}

