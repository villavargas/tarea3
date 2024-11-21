package mx.com.telcel.services.impl;

import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Objects;

import javax.annotation.Resource;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.assertj.core.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import com.google.common.io.CharStreams;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import mx.com.telcel.constants.AuthorizationType;
import mx.com.telcel.models.Authorize;
import mx.com.telcel.models.Token;
import mx.com.telcel.request.models.ClientRequest;
import mx.com.telcel.request.models.UserRequest;
import mx.com.telcel.response.models.AuthorizationResponse;
import mx.com.telcel.response.models.AuthorizeErrorResponse;
import mx.com.telcel.response.models.ClientResponse;
import mx.com.telcel.response.models.ErrorResponse;
import mx.com.telcel.response.models.MessageResponse;
import mx.com.telcel.response.models.RevokeResponse;
import mx.com.telcel.response.models.TokenNipResponse;
import mx.com.telcel.response.models.TokenResponse;
import mx.com.telcel.response.models.UserInfoResponse;
import mx.com.telcel.response.models.UserResponse;
import mx.com.telcel.services.TelcelSSOService;


public class TelcelSSOServiceImpl implements TelcelSSOService
{

	@Resource(name = "defaultConfigurationService")
	private ConfigurationService configurationService;

	private static final String CERTIFICATE_PATH = "telcel.sso.certificate.path";
	private static final String CERTIFICATE_PATH_LOCAL = "telcel.sso.certificate.path.local";
	private static final String HTTP_HOST = "telcel.sso.host.http";
	private static final String HTTPS_HOST = "telcel.sso.host.https";

	private static final Logger LOG = LoggerFactory.getLogger(TelcelSSOServiceImpl.class);

	@Override
	public UserResponse addUser(final UserRequest request)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException
	{
		final CloseableHttpClient client = HttpClients.custom()
				.setSSLSocketFactory(new SSLConnectionSocketFactory(
						SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build(),
						NoopHostnameVerifier.INSTANCE))
				.build();
		final String urlApi = configurationService.getConfiguration().getString("telcel.sso.api.userAdd");
		final HttpPost serviceRequest = new HttpPost(urlApi);
		serviceRequest.addHeader("Accept", "application/json");
		serviceRequest.addHeader("Content-type", "application/json");
		final Gson gson = new Gson();
		LOG.info("REQUEST ADDUSER: " + gson.toJson(request));
		final StringEntity postingString = new StringEntity(gson.toJson(request));
		serviceRequest.setEntity(postingString);
		final CloseableHttpResponse serviceResponse = client.execute(serviceRequest);
		final InputStream input = serviceResponse.getEntity().getContent();
		final Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
		final String responseContent = CharStreams.toString(reader);
		LOG.info("RESPONSE ADDUSER: " + responseContent);
		final Type addType = new TypeToken<UserResponse>()
		{
		}.getType();
		final UserResponse response = new Gson().fromJson(responseContent, addType);
		return response;
	}

	@Override
	public UserResponse lookupUser(final String userId) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException,
			IOException, URISyntaxException, CertificateException
	{
		final CloseableHttpClient client = HttpClients.custom()
				.setSSLSocketFactory(new SSLConnectionSocketFactory(
						SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build(),
						NoopHostnameVerifier.INSTANCE))
				.build();

		final String urlApi = configurationService.getConfiguration().getString("telcel.sso.api.lookup");
		final HttpGet serviceRequest = new HttpGet(urlApi);
		final URI uri = new URIBuilder(serviceRequest.getURI()).addParameter("userId", userId).build();
		((HttpRequestBase) serviceRequest).setURI(uri);
		LOG.info("URI LOOKUP:" + serviceRequest.getURI());
		final HttpResponse serviceResponse = client.execute(serviceRequest);
		final InputStream input = serviceResponse.getEntity().getContent();
		final Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
		final String responseContent = CharStreams.toString(reader);
		LOG.info("RESPONSE LOOKUP: " + responseContent);
		final Type addType = new TypeToken<UserResponse>()
		{
		}.getType();
		final UserResponse response = new Gson().fromJson(responseContent, addType);
		return response;
	}

	@Override
	public UserResponse modifyUser(final UserRequest request)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException
	{
		final CloseableHttpClient client = HttpClients.custom()
				.setSSLSocketFactory(new SSLConnectionSocketFactory(
						SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build(),
						NoopHostnameVerifier.INSTANCE))
				.build();
		final String host = configurationService.getConfiguration().getString(HTTP_HOST);
		final HttpPost serviceRequest = new HttpPost(host + "/provisioning/service/user/modify");
		serviceRequest.addHeader("Accept", "application/json");
		serviceRequest.addHeader("Content-type", "application/json");
		final Gson gson = new Gson();
		LOG.info("REQUEST MODIFY: " + gson.toJson(request));
		//final StringEntity postingString = new StringEntity(gson.toJson(request));
		HttpEntity postingString = new StringEntity(gson.toJson(request), "UTF-8");
		serviceRequest.setEntity(postingString);
		serviceRequest.setEntity(postingString);
		final CloseableHttpResponse serviceResponse = client.execute(serviceRequest);
		final InputStream input = serviceResponse.getEntity().getContent();
		final Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
		final String responseContent = CharStreams.toString(reader);
		LOG.info("RESPONSE MODIFY: " + responseContent);
		final Type addType = new TypeToken<UserResponse>()
		{
		}.getType();
		final UserResponse response = new Gson().fromJson(responseContent, addType);
		return response;
	}

	@Override
	public UserResponse modifyUserSendMail(final UserRequest request)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException
	{
		final CloseableHttpClient client = HttpClients.custom()
				.setSSLSocketFactory(new SSLConnectionSocketFactory(
						SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build(),
						NoopHostnameVerifier.INSTANCE))
				.build();
		final String clientId = configurationService.getConfiguration().getString("telcel.sso.email.resend.client.id");
		final String host = configurationService.getConfiguration().getString(HTTP_HOST);
		final HttpPost serviceRequest = new HttpPost(
				host + "/provisioning/service/user/modify?clientId="+clientId);
		serviceRequest.addHeader("Accept", "application/json");
		serviceRequest.addHeader("Content-type", "application/json");
		final Gson gson = new Gson();
		LOG.info("URI: " + serviceRequest.getURI());
		LOG.info("REQUEST MODIFY: " + gson.toJson(request));
		//final StringEntity postingString = new StringEntity(gson.toJson(request));
		HttpEntity postingString = new StringEntity(gson.toJson(request), "UTF-8");
		serviceRequest.setEntity(postingString);
		final CloseableHttpResponse serviceResponse = client.execute(serviceRequest);
		final InputStream input = serviceResponse.getEntity().getContent();
		final Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
		final String responseContent = CharStreams.toString(reader);
		LOG.info("RESPONSE MODIFY: " + responseContent);
		final Type addType = new TypeToken<UserResponse>()
		{
		}.getType();
		final UserResponse response = new Gson().fromJson(responseContent, addType);
		return response;
	}

	@Override
	public UserResponse deleteUser(final String userId, final UserRequest request)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException
	{
		final CloseableHttpClient client = HttpClients.custom()
				.setSSLSocketFactory(new SSLConnectionSocketFactory(
						SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build(),
						NoopHostnameVerifier.INSTANCE))
				.build();
		final String host = configurationService.getConfiguration().getString(HTTP_HOST);
		final HttpPost serviceRequest = new HttpPost(host + "/provisioning/service/user/delete?userId=" + userId);
		serviceRequest.addHeader("Accept", "application/json");
		serviceRequest.addHeader("Content-type", "application/json");
		final Gson gson = new Gson();
		final StringEntity postingString = new StringEntity(gson.toJson(request));
		serviceRequest.setEntity(postingString);
		final CloseableHttpResponse serviceResponse = client.execute(serviceRequest);
		final InputStream input = serviceResponse.getEntity().getContent();
		final Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
		final Type addType = new TypeToken<UserResponse>()
		{
		}.getType();
		final UserResponse response = new Gson().fromJson(reader, addType);
		return response;
	}

	@Override
	public ErrorResponse password1stUser(final String userId, final String clientId)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException, URISyntaxException
	{
		final CloseableHttpClient client = HttpClients.custom()
				.setSSLSocketFactory(new SSLConnectionSocketFactory(
						SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build(),
						NoopHostnameVerifier.INSTANCE))
				.build();
		final String urlApi = configurationService.getConfiguration().getString("telcel.sso.api.requestpassword1st");
		final HttpGet serviceRequest = new HttpGet(urlApi);
		final URIBuilder uriBuilder = new URIBuilder(serviceRequest.getURI());
		uriBuilder.addParameter("userId", userId);
		uriBuilder.addParameter("clientId", clientId);
		final URI uri = uriBuilder.build();
		((HttpRequestBase) serviceRequest).setURI(uri);
		final CloseableHttpResponse serviceResponse = client.execute(serviceRequest);
		final InputStream input = serviceResponse.getEntity().getContent();
		final Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
		final Type addType = new TypeToken<ErrorResponse>()
		{
		}.getType();
		final ErrorResponse response = new Gson().fromJson(reader, addType);

		return response;
	}

	@Override
	public ClientResponse otpCreate(final String userId, final String clientId, final ClientRequest request)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException
	{
		final CloseableHttpClient client = HttpClients.custom()
				.setSSLSocketFactory(new SSLConnectionSocketFactory(
						SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build(),
						NoopHostnameVerifier.INSTANCE))
				.build();
		final String host = configurationService.getConfiguration().getString(HTTP_HOST);
		final HttpPost serviceRequest = new HttpPost(
				host + "/provisioning/service/otp/create?userId=" + userId + "&clientId=" + clientId);
		serviceRequest.addHeader("Accept", "application/json");
		serviceRequest.addHeader("Content-type", "application/json");
		final Gson gson = new Gson();
		final StringEntity postingString = new StringEntity(gson.toJson(request));
		serviceRequest.setEntity(postingString);
		final CloseableHttpResponse serviceResponse = client.execute(serviceRequest);
		final InputStream input = serviceResponse.getEntity().getContent();
		final Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
		final Type addType = new TypeToken<ClientResponse>()
		{
		}.getType();
		final ClientResponse response = new Gson().fromJson(reader, addType);
		return response;
	}

	@Override
	public AuthorizationResponse authorize(final String username, final String password, final String otp, final String msisdn,
			final Authorize authorize, final AuthorizationType authType) throws KeyManagementException, NoSuchAlgorithmException,
			KeyStoreException, IOException, URISyntaxException, CertificateException
	{
		//final HttpClient client = getHttpClientCertificate(true, CERTIFICATE_PATH);
		final HttpClient client = getHttpClientWithoutCertificateAndRedirect();

		final String urlApi = configurationService.getConfiguration().getString("telcel.sso.api.authorize");
		final HttpGet serviceRequest = new HttpGet(urlApi);
		switch (authType)
		{
			case USER_PASSWORD:
				serviceRequest.addHeader("USERNAME", username);
				serviceRequest.addHeader("PASSWORD", password);
				break;
			case OTP:
				serviceRequest.addHeader("USERNAME", username);
				serviceRequest.addHeader("OTP", otp);
				break;
			case MSISDN:
				serviceRequest.addHeader("MSISDN", msisdn);
				break;
		}
		final URIBuilder uriBuilder = new URIBuilder(serviceRequest.getURI());
		uriBuilder.addParameter("client_id", authorize.getClienteId());
		uriBuilder.addParameter("response_type", authorize.getResponseType());
		uriBuilder.addParameter("redirect_uri", authorize.getRedirectUri());
		uriBuilder.addParameter("scope", authorize.getScope());
		uriBuilder.addParameter("state", authorize.getState());
		uriBuilder.addParameter("nonce", authorize.getNonce());
		uriBuilder.addParameter("acr_values", authorize.getArcValues());
		if (Objects.nonNull(authorize.getDisplay()))
		{
			uriBuilder.addParameter("display", authorize.getDisplay());
		}
		if (Objects.nonNull(authorize.getPrompt()))
		{
			uriBuilder.addParameter("prompt", authorize.getPrompt());
		}
		if (Objects.nonNull(authorize.getMaxAge()))
		{
			uriBuilder.addParameter("max-age", authorize.getMaxAge());
		}
		if (Objects.nonNull(authorize.getUiLocales()))
		{
			uriBuilder.addParameter("ui-locales", authorize.getUiLocales());
		}
		if (Objects.nonNull(authorize.getClaimsLocales()))
		{
			uriBuilder.addParameter("claims-locales", authorize.getClaimsLocales().toString());
		}
		if (Objects.nonNull(authorize.getIdTokenHint()))
		{
			uriBuilder.addParameter("id-token-hint", authorize.getIdTokenHint());
		}
		if (Objects.nonNull(authorize.getLoginHint()))
		{
			uriBuilder.addParameter("login-hint", authorize.getLoginHint());
		}
		if (Objects.nonNull(authorize.getDtbs()))
		{
			uriBuilder.addParameter("dtbs", authorize.getDtbs());
		}
		final URI uri = uriBuilder.build();
		((HttpRequestBase) serviceRequest).setURI(uri);
		final HttpResponse serviceResponse = client.execute(serviceRequest);
		final InputStream input = serviceResponse.getEntity().getContent();
		final Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
		String response = CharStreams.toString(reader);
		final AuthorizationResponse authResponse = new AuthorizationResponse();
		LOG.info("respues del servicio de autorizaci�n..." + response);
		if (response.startsWith("code"))
		{
			final String[] responseValues = response.split("&");
			for (final String r : responseValues)
			{
				if (r.contains("code"))
				{
					final String[] rValues = r.split("=");
					response = rValues[1];
					break;
				}
			}
			authResponse.setCode(response);
			authResponse.setError("");
		}
		if (response.startsWith("error"))
		{
			authResponse.setCode("");
			authResponse.setError("User Not Found or incorrect credentials");
		}
		return authResponse;
	}

	public MessageResponse passwordRecoveryUser(final String msisdn, final String packageId)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException
	{
		final CloseableHttpClient client = HttpClients.custom()
				.setSSLSocketFactory(new SSLConnectionSocketFactory(
						SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build(),
						NoopHostnameVerifier.INSTANCE))
				.build();
		final String host = configurationService.getConfiguration().getString(HTTP_HOST);
		final HttpPost serviceRequest = new HttpPost(host + "/amxsso/tools/v1/recover" + "/" + msisdn + "/MTAND?format=json");
		serviceRequest.addHeader("Accept", "application/json");
		serviceRequest.addHeader("Content-type", "application/json");
		final StringEntity postingString = new StringEntity("{\"packageId\":" + "\"" + packageId + "\"}");
		serviceRequest.setEntity(postingString);
		final CloseableHttpResponse serviceResponse = client.execute(serviceRequest);
		final InputStream input = serviceResponse.getEntity().getContent();
		final Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
		final Type addType = new TypeToken<MessageResponse>()
		{
		}.getType();
		final MessageResponse response = new Gson().fromJson(reader, addType);
		return response;
	}

	public TokenResponse token(final Token token, final String authorization) throws KeyManagementException,
			NoSuchAlgorithmException, KeyStoreException, IOException, URISyntaxException, CertificateException
	{
		//final HttpClient client = getHttpClientCertificate(false, CERTIFICATE_PATH);
		final HttpClient client = getHttpClientWithoutCertificate();

		final String urlApi = configurationService.getConfiguration().getString("telcel.sso.api.token");
		final HttpGet serviceRequest = new HttpGet(urlApi);
		if (Strings.isNullOrEmpty(authorization))
		{
			serviceRequest.addHeader("Authorization", authorization);
		}
		final URIBuilder uriBuilder = new URIBuilder(serviceRequest.getURI());
		uriBuilder.addParameter("grant_type", token.getGrantType());
		uriBuilder.addParameter("code", token.getCode());
		uriBuilder.addParameter("redirect_uri", token.getRedirectUri());
		if (Objects.nonNull(token.getClientId()))
		{
			uriBuilder.addParameter("client_id", token.getClientId());
		}
		if (Objects.nonNull(token.getClientCredential()))
		{
			uriBuilder.addParameter("client_credential", token.getClientCredential());
		}
		final URI uri = uriBuilder.build();
		((HttpRequestBase) serviceRequest).setURI(uri);
		final HttpResponse serviceResponse = client.execute(serviceRequest);
		final InputStream input = serviceResponse.getEntity().getContent();
		final Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
		final Type addType = new TypeToken<TokenResponse>()
		{
		}.getType();
		final TokenResponse response = new Gson().fromJson(reader, addType);
		return response;
	}

	public UserInfoResponse userInfo(final String accessToken)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException
	{
		//final HttpClient client = getHttpClientCertificate(false, CERTIFICATE_PATH);
		final HttpClient client = getHttpClientWithoutCertificate();

		final String urlApi = configurationService.getConfiguration().getString("telcel.sso.api.userinfo");
		final HttpGet serviceRequest = new HttpGet(urlApi);
		serviceRequest.addHeader("Authorization", "Bearer " + accessToken);
		final HttpResponse serviceResponse = client.execute(serviceRequest);
		final InputStream input = serviceResponse.getEntity().getContent();
		final Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
		final String responseContent = CharStreams.toString(reader);
		LOG.info("REPONSE USER INFO: " + responseContent);
		final Type addType = new TypeToken<UserInfoResponse>()
		{
		}.getType();
		final UserInfoResponse response = new Gson().fromJson(responseContent, addType);
		return response;
	}

	@Override
	public AuthorizeErrorResponse password2stUser(final String msisdn, final String clientId, final String redirectUri)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException, URISyntaxException
	{
		final CloseableHttpClient client = HttpClients.custom()
				.setSSLSocketFactory(new SSLConnectionSocketFactory(
						SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build(),
						NoopHostnameVerifier.INSTANCE))
				.build();
		final String host = configurationService.getConfiguration().getString(HTTP_HOST);
		final HttpGet serviceRequest = new HttpGet(host + "/requestpassword2st");
		serviceRequest.addHeader("MSISDN", msisdn);
		final URIBuilder uriBuilder = new URIBuilder(serviceRequest.getURI());
		uriBuilder.addParameter("clientId", clientId);
		uriBuilder.addParameter("redirect_uri", redirectUri);
		final URI uri = uriBuilder.build();
		((HttpRequestBase) serviceRequest).setURI(uri);
		final CloseableHttpResponse serviceResponse = client.execute(serviceRequest);
		final InputStream input = serviceResponse.getEntity().getContent();
		final Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
		final Type addType = new TypeToken<AuthorizeErrorResponse>()
		{
		}.getType();
		final AuthorizeErrorResponse response = new Gson().fromJson(reader, addType);
		return response;
	}

	@Override
	public TokenNipResponse sendTokenNip(final String username)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException
	{
		//final HttpClient client = getHttpClientCertificate(false, CERTIFICATE_PATH);
		final HttpClient client = getHttpClientWithoutCertificate();
		final String api = configurationService.getConfiguration().getString("telcel.sso.api.tokennip");
		final String clientId = configurationService.getConfiguration().getString("telcel.sso.email.resend.client.id");
		final String clientSecret = configurationService.getConfiguration().getString("telcel.sso.email.resend.client.secret");
		final String authorization = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
		final HttpPost serviceRequest = new HttpPost(api + "?grant_type=otp" + "&username=" + username);
		serviceRequest.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
		serviceRequest.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
		serviceRequest.addHeader(HttpHeaders.AUTHORIZATION,
				"Basic "+authorization); // NOSONAR
		//serviceRequest.addHeader("client_id", "c7268556-f714-4449-84c7-39e8cd8ad028");
		//serviceRequest.addHeader("client_secret", "0F51197649B472F852E69CFC7A188056");
		LOG.info("URI TOKEN NIP: " + serviceRequest.getURI());
		LOG.info(
				"AUTHORIZATION TOKEN NIP: "+authorization);
		final HttpResponse serviceResponse = client.execute(serviceRequest);
		final InputStream input = serviceResponse.getEntity().getContent();
		final Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
		final String responseContent = CharStreams.toString(reader);
		LOG.info("RESPONSE TOKEN NIP: " + responseContent);
		final Type addType = new TypeToken<TokenNipResponse>()
		{
		}.getType();
		return new Gson().fromJson(responseContent, addType);
	}

	@Override
	public TokenNipResponse sendTokenNipRC(final String username)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException
	{
		//final HttpClient client = getHttpClientCertificate(false, CERTIFICATE_PATH);
		final HttpClient client = getHttpClientWithoutCertificate();
		final String api = configurationService.getConfiguration().getString("telcel.sso.api.tokennip");
		final String clientId = configurationService.getConfiguration().getString("telcel.sso.email.resend.client.idRC");
		final String clientSecret = configurationService.getConfiguration().getString("telcel.sso.email.resend.client.secret");
		final String authorization = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
		final HttpPost serviceRequest = new HttpPost(api + "?grant_type=otp" + "&username=" + username);
		serviceRequest.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
		serviceRequest.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
		serviceRequest.addHeader(HttpHeaders.AUTHORIZATION,
				"Basic "+authorization);
		LOG.info("URI TOKEN NIP: " + serviceRequest.getURI());
		LOG.info(
				"AUTHORIZATION TOKEN NIP: "+authorization);
		final HttpResponse serviceResponse = client.execute(serviceRequest);
		final InputStream input = serviceResponse.getEntity().getContent();
		final Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
		final String responseContent = CharStreams.toString(reader);
		LOG.info("RESPONSE TOKEN NIP: " + responseContent);
		final Type addType = new TypeToken<TokenNipResponse>()
		{
		}.getType();
		return new Gson().fromJson(responseContent, addType);
	}

	@Override
	public TokenNipResponse validateTokenNip(final String username, final String requestId, final String otpCode)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException
	{
		//final HttpClient client = getHttpClientCertificate(false, CERTIFICATE_PATH);
		final HttpClient client = getHttpClientWithoutCertificate();
		final String api = configurationService.getConfiguration().getString("telcel.sso.api.tokennip");
		final String clientId = configurationService.getConfiguration().getString("telcel.sso.email.resend.client.id");
		final String clientSecret = configurationService.getConfiguration().getString("telcel.sso.email.resend.client.secret");
		final String authorization = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
		final HttpPost serviceRequest = new HttpPost(
				api + "?grant_type=otp_verify" + "&username=" + username + "&request_id=" + requestId + "&otp_code=" + otpCode);
		serviceRequest.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
		serviceRequest.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
		serviceRequest.addHeader(HttpHeaders.AUTHORIZATION,
				"Basic "+authorization); // NOSONAR
		LOG.info("URI VALIDATE TOKEN NIP: " + serviceRequest.getURI());
		LOG.info("AUTHORIZATION: "+authorization);
		final HttpResponse serviceResponse = client.execute(serviceRequest);
		final InputStream input = serviceResponse.getEntity().getContent();
		final Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
		final String responseContent = CharStreams.toString(reader);
		LOG.info("RESPONSE VALIDATE TOKEN NIP: " + responseContent);
		final Type addType = new TypeToken<TokenNipResponse>()
		{
		}.getType();
		return new Gson().fromJson(responseContent, addType);
	}

	@Override
	public RevokeResponse revoke(final String accessToken)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException
	{
		final HttpClient client = getHttpClientWithoutCertificate();
		final String api = configurationService.getConfiguration().getString("telcel.sso.api.revoke");
		final HttpPost serviceRequest = new HttpPost(api);
		serviceRequest.addHeader("Token_type", "revoke");
		serviceRequest.addHeader("Token", accessToken);
		LOG.info("URI LOGOUT: " + serviceRequest.getURI());
		LOG.info("ACCESS TOKEN LOGOUT: " + accessToken);
		final HttpResponse serviceResponse = client.execute(serviceRequest);
		final InputStream input = serviceResponse.getEntity().getContent();
		final Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
		final String responseContent = CharStreams.toString(reader);
		LOG.info("RESPONSE LOGOUT: " + responseContent);
		final Type addType = new TypeToken<RevokeResponse>()
		{
		}.getType();
		return new Gson().fromJson(responseContent, addType);
	}

	@Override
	public MessageResponse resendKeyEmail(final String email)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException
	{
		final HttpClient client = getHttpClientWithoutCertificate();
		final String api = configurationService.getConfiguration().getString("telcel.sso.api.email.resend");
		final String clientId = configurationService.getConfiguration().getString("telcel.sso.email.resend.client.id");
		final String clientSecret = configurationService.getConfiguration().getString("telcel.sso.email.resend.client.secret");
		final String authorization = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
		LOG.info("Authorization : " + authorization);
		final HttpPost serviceRequest = new HttpPost(api);
		serviceRequest.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
		serviceRequest.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
		serviceRequest.addHeader(HttpHeaders.AUTHORIZATION, "Basic " + authorization);
		final String strRequest = "{\"resendKeyRequest\":{" + "\"email\":" + "\"" + email + "\"" + "}}";
		final StringEntity postingString = new StringEntity(strRequest);
		LOG.info("REQUEST : " + strRequest);
		serviceRequest.setEntity(postingString);
		final HttpResponse serviceResponse = client.execute(serviceRequest);
		LOG.info("Status Code Response : " + serviceResponse.getStatusLine().getStatusCode());
		final MessageResponse response = new MessageResponse();
		response.setResponseCode("" + serviceResponse.getStatusLine().getStatusCode());
		if (serviceResponse.getStatusLine().getStatusCode() == HttpStatus.OK.value())
		{
			response.setResponseMessage("SUCCESS");
		}
		else
		{
			response.setResponseMessage("ERROR");
		}
		return response;
	}

	@Override
	public MessageResponse activateEmail(final String activationKey)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException
	{
		final HttpClient client = getHttpClientWithoutCertificate();
		final String api = configurationService.getConfiguration().getString("telcel.sso.api.email.activate");
		final String clientId = configurationService.getConfiguration().getString("telcel.sso.email.resend.client.id");
		final String clientSecret = configurationService.getConfiguration().getString("telcel.sso.email.resend.client.secret");
		final String authorization = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
		LOG.info("Authorization : " + authorization);
		final HttpPost serviceRequest = new HttpPost(api);
		serviceRequest.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
		serviceRequest.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
		serviceRequest.addHeader(HttpHeaders.AUTHORIZATION, "Basic " + authorization);
		final String strRequest = "{\"emailValidationRequest\":{" + "\"activationKey\":" + "\"" + activationKey + "\"" + "}}";
		final StringEntity postingString = new StringEntity(strRequest);
		LOG.info("REQUEST : " + strRequest);
		serviceRequest.setEntity(postingString);
		final HttpResponse serviceResponse = client.execute(serviceRequest);
		LOG.info("Status Code Response : " + serviceResponse.getStatusLine().getStatusCode());
		final MessageResponse response = new MessageResponse();
		response.setResponseCode("" + serviceResponse.getStatusLine().getStatusCode());
		if (serviceResponse.getStatusLine().getStatusCode() == HttpStatus.OK.value())
		{
			response.setResponseMessage("SUCCESS");
		}
		else
		{
			response.setResponseMessage("ERROR");
		}
		return response;
	}

	private HttpClient getHttpClientCertificate(final boolean disableRedirect, final String path)
			throws CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException
	{
		final String certificatePath = configurationService.getConfiguration().getString(path);
		//final InputStream is = getClass().getClassLoader().getResourceAsStream(certificatePath);
		final ClassLoader classLoader = TelcelSSOServiceImpl.class.getClassLoader();
		InputStream is = null;
		if (classLoader != null)
		{
			LOG.info(certificatePath);
			is = classLoader.getResourceAsStream(certificatePath);
		}
		else
		{
			LOG.info("Class loader is null");
		}
		final CertificateFactory cf = CertificateFactory.getInstance("X.509");
		final X509Certificate caCert = (X509Certificate) cf.generateCertificate(is);
		final TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		final KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		ks.load(null);
		ks.setCertificateEntry("caCert", caCert);
		tmf.init(ks);
		final SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(null, tmf.getTrustManagers(), null);
		final SSLConnectionSocketFactory sslConnectionFactory = new SSLConnectionSocketFactory(sslContext,
				NoopHostnameVerifier.INSTANCE);
		final Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create()
				.register("https", sslConnectionFactory).register("http", new PlainConnectionSocketFactory()).build();
		final BasicHttpClientConnectionManager connManager = new BasicHttpClientConnectionManager(registry);
		if (disableRedirect)
		{
			return HttpClients.custom().setConnectionManager(connManager).setSSLSocketFactory(sslConnectionFactory)
					.disableRedirectHandling().build();
		}
		else
		{
			return HttpClients.custom().setConnectionManager(connManager).setSSLSocketFactory(sslConnectionFactory).build();
		}
	}

	public HttpClient getHttpClientWithoutCertificate() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException
	{
		final SSLContextBuilder builder = new SSLContextBuilder();
		builder.loadTrustMaterial(null, (chain, authType) -> true);
		final SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build(), NoopHostnameVerifier.INSTANCE);
		return HttpClients.custom().setSSLSocketFactory(sslsf).build();
	}

	public HttpClient getHttpClientWithoutCertificateAndRedirect() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException
	{
		final SSLContextBuilder builder = new SSLContextBuilder();
		builder.loadTrustMaterial(null, (chain, authType) -> true);
		final SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build(), NoopHostnameVerifier.INSTANCE);
		return HttpClients.custom().setSSLSocketFactory(sslsf).disableRedirectHandling().build();
	}

}
