/**
 *
 */
package mx.com.telcel.controllers;


import de.hybris.platform.oauth2.services.impl.DefaultHybrisOpenIDTokenServices;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.security.auth.AuthenticationService;
import de.hybris.platform.webservicescommons.oauth2.token.provider.HybrisOAuthTokenStore;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.ApiParam;
import mx.com.telcel.constants.AuthorizationType;
import mx.com.telcel.dto.LoginResponseTokenDTO;
import mx.com.telcel.models.Authorize;
import mx.com.telcel.response.models.AuthorizationResponse;
import mx.com.telcel.response.models.UserInfoResponse;
import mx.com.telcel.services.CustomValidationService;
import mx.com.telcel.services.TelcelSSOService;

/**
 * @author luis.villa
 *
 */
@Controller
@RequestMapping(value = "/api")
public class LoginAuthController
{
	private static final Logger LOG = Logger.getLogger(LoginAuthController.class);

	@Resource(name = "oauthTokenServices")
	private DefaultHybrisOpenIDTokenServices oauthTokenServices;

	@Resource(name = "authenticationService")
	private AuthenticationService authenticationService;

	@Resource(name = "authenticationManager")
	private AuthenticationManager authenticationManager;

	@Resource(name = "oAuth2RequestFactory")
	private DefaultOAuth2RequestFactory auth2RequestFactory;

	@Resource(name = "oauthTokenStore")
	private HybrisOAuthTokenStore oAuthTokenStore;

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	@Resource(name = "telcelSSOService")
	private TelcelSSOService telcelSSOService;

	@Resource(name = "customValidationService")
	private CustomValidationService validationService;




	private static final String CLIENT_ID = "telcel.sso.authorize.client.id";


	@PostMapping(value = "/token", consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE }, produces =
	{ MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	//@ApiOperation(nickname = "creacionToken", value = "Crear token usuario.", notes = "Crear token usuario request.", consumes = "application/x-www-form-urlencoded;charset=UTF-8", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LoginResponseTokenDTO> loginAuthentication(final HttpServletRequest request,
			@ApiParam(value = "Values Token", required = true)
			@RequestBody
			final MultiValueMap<String, String> body)
	{
		final LoginResponseTokenDTO response = new LoginResponseTokenDTO();
		try
		{
			final String testToken = configurationService.getConfiguration().getString("telcel.token.test");

			if (body != null)
			{

				if (!body.getFirst("password").isEmpty() && !body.getFirst("username").isEmpty())
				{
					String userName = body.getFirst("username");
					final String password = body.getFirst("password");


					boolean isnumber = false;
					if (validationService.validateIsANumber(userName))
					{
						isnumber = true;
						if (!validationService.is10DigitPhoneNumber(userName))
						{
							response.setError("invalid_grant");
							response.setError_description("Bad credentials");
							return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
						}
						else if (validationService.is10DigitPhoneNumber(userName))
						{
							userName = "52" + userName;
						}
					}
					if (!validationService.validateIsAnEmail(userName) && !isnumber)
					{
						response.setError("invalid_grant");
						response.setError_description("Bad credentials");
						return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
					}

					final Authorize auth = new Authorize();
					final String clientId = configurationService.getConfiguration().getString(CLIENT_ID);
					auth.setClienteId(clientId);
					auth.setResponseType("code");
					auth.setRedirectUri("tienda.telcel.com");
					auth.setScope("openid");
					auth.setState("0");
					auth.setNonce("0");
					auth.setArcValues("1");


					final AuthorizationResponse authResponse = telcelSSOService.authorize(userName, password, "", "", auth,
							AuthorizationType.USER_PASSWORD);

					if (!authResponse.getCode().trim().equals(""))
					{
						UsernamePasswordAuthenticationToken authenticationToken;
						if (testToken.isEmpty())
						{
							authenticationToken = new UsernamePasswordAuthenticationToken(body.getFirst("username"), "");
						}
						else
						{
							authenticationToken = new UsernamePasswordAuthenticationToken(testToken, "");
						}

						if ("SUCCESS".equalsIgnoreCase("SUCCESS"))
						{
							final Authentication authentication = authenticationManager.authenticate(authenticationToken);
							final SecurityContext securityContext = SecurityContextHolder.getContext();
							securityContext.setAuthentication(authentication);
							final HttpSession session = request.getSession();
							session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);


							final OAuth2AccessToken auth2AccessToken = getAccessToken(authentication, body);

							response.setAccess_token(auth2AccessToken.getValue());
							response.setToken_type(auth2AccessToken.getTokenType());
							response.setRefresh_token(String.valueOf(auth2AccessToken.getRefreshToken()));
							response.setExpiredIn(String.valueOf(auth2AccessToken.getExpiresIn()));
							response.setScope(String.valueOf(auth2AccessToken.getScope()));
						}
					}
					else
					{
						response.setError("invalid_grant");
						response.setError_description("Bad credentials");
						return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
					}
				}
				else
				{
					if (!body.getFirst("token").isEmpty())
					{
						final String accessToken = body.getFirst("token");
						final UserInfoResponse responseUserI = telcelSSOService.userInfo(accessToken);
						if (responseUserI != null && !responseUserI.getLoginname().isEmpty())
						{

							final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
									body.getFirst("username"), "");
							final Authentication authentication = authenticationManager.authenticate(authenticationToken);
							final SecurityContext securityContext = SecurityContextHolder.getContext();
							securityContext.setAuthentication(authentication);
							final HttpSession session = request.getSession();
							session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);


							final OAuth2AccessToken auth2AccessToken = getAccessToken(authentication, body);

							response.setAccess_token(auth2AccessToken.getValue());
							response.setToken_type(auth2AccessToken.getTokenType());
							response.setRefresh_token(String.valueOf(auth2AccessToken.getRefreshToken()));
							response.setExpiredIn(String.valueOf(auth2AccessToken.getExpiresIn()));
							response.setScope(String.valueOf(auth2AccessToken.getScope()));

						}
						else
						{

							response.setError("invalid_grant");
							response.setError_description("Bad credentials");
							return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

						}
					}
				}

			}
			else
			{
				response.setError("invalid_grant");
				response.setError_description("Bad credentials");
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}


			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		catch (final Exception e)
		{
			LOG.info("LoginAuthentication", e);
			response.setError("invalid_grant");
			response.setError_description("Bad credentials");
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
	}

	private OAuth2AccessToken getAccessToken(final Authentication authentication, final MultiValueMap<String, String> body)
	{
		final Configuration configuration = configurationService.getConfiguration();
		final Map<String, String> authorizationParameters = new HashMap<>();
		authorizationParameters.put(OAuth2Utils.SCOPE, "basic");
		authorizationParameters.put(OAuth2Utils.CLIENT_ID, body.getFirst("client_id"));
		authorizationParameters.put(OAuth2Utils.GRANT_TYPE, body.getFirst("grant_type"));
		authorizationParameters.put("client_secret", body.getFirst("client_secret"));

		//authorizationParameters.put(OAuth2Utils.GRANT_TYPE, "authorization_code");


		final AuthorizationRequest authorizationRequest = auth2RequestFactory.createAuthorizationRequest(authorizationParameters);
		authorizationRequest.setApproved(Boolean.TRUE);

		final Set<GrantedAuthority> authorities = new HashSet<>();
		authorities.add(new SimpleGrantedAuthority("ROLE_CLIENT"));
		authorizationRequest.setAuthorities(authorities);

		final Set<String> resourceIds = new HashSet<>();
		resourceIds.add("hybris");
		authorizationRequest.setResourceIds(resourceIds);


		final OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(authorizationRequest.createOAuth2Request(),
				authentication);



		final OAuth2AccessToken tokenHybris = oAuthTokenStore.getAccessToken(oAuth2Authentication);

		return oauthTokenServices.createAccessToken(oAuth2Authentication);
	}

}
