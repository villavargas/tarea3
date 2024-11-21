package mx.com.telcel.utilities;

import com.google.common.io.CharStreams;
import org.apache.http.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
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
import java.util.Map;
import java.util.Objects;


public abstract class AbstractProxyControllerServlet extends HttpServlet
{

	private static final long serialVersionUID = 3726702775536096413L;

	private static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 7500;

	private static final int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 200;

	private static final int DEFAULT_CONN_TIMEOUT_MILLISECONDS = 20 * 1000;

	private static final int DEFAULT_STALE_CONN_TIMEOUT_MILLISECONDS = 20 * 1000;

	private static final int DEFAULT_READ_TIMEOUT_MILLISECONDS = 60 * 1000;

	public static final String HTTP_UTF8_ENCODING = "utf-8";

	public static final String HTTP_APPLICATION_JSON = "application/json";

	private static String LINE_SEPARATOR = "\r\n";

	private static final int RETRY_COUNT = 4;

	private static final int RETRY_SLEEP_TIME = 3000;

	public static final String MSG_FAIL = "fail";
	public static final String MSG_SUCCESS = "success";
	public static final String MSG_VACIO = "Sin datos";
	public static final String MSG_INVALID = "Incorrecto";
	public static final String TIMED_OUT = "timed out";

	protected static final Logger log = LoggerFactory.getLogger(AbstractProxyControllerServlet.class);

	protected HttpGenericResponse doPostSoap(final String reqURL, final String entities, final String responseEncoding,
			final String interfaz, final boolean userValidate, final String user, final String password)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException
	{
		final HttpGenericResponse httpGenericResponse = new HttpGenericResponse();
		final HttpClient httpClient = getHttpClient(); // NOSONAR
		HttpContext context = null;
		HttpResponse response = null;
		String responseContent = "";
		String exceptionMessage = "";
		final StringEntity lEntity = new StringEntity(entities, ContentType.TEXT_XML);
		final HttpPost httpPost = new HttpPost(reqURL);
		httpPost.addHeader("SOAPAction", "");
		httpPost.setEntity(lEntity);
		if (userValidate)
		{
			context = new BasicHttpContext();
			final CredentialsProvider credsProvider = new BasicCredentialsProvider();
			credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(user, password));
			context.setAttribute(ClientContext.CREDS_PROVIDER, credsProvider);
			context.setAttribute(ClientPNames.HANDLE_AUTHENTICATION, true);
		}
		int code = -1;
		for (int iTries = 0; iTries < AbstractProxyControllerServlet.RETRY_COUNT; iTries++)
		{
			try
			{
				response = httpClient.execute(httpPost, context);
				code = response.getStatusLine().getStatusCode();
				log.debug("code[{}]", new Object[]
				{ code });
				if (code == HttpStatus.SC_OK)
				{
					break;
				}
				else
				{
					try
					{
						// life is too short for exponential backoff
						Thread.sleep(AbstractProxyControllerServlet.RETRY_SLEEP_TIME);
					}
					catch (final InterruptedException e1) // NOSONAR
					{
						log.debug("Exception: RETRY_SLEEP_TIME {}", new Object[]
						{ e1.getMessage() });
					}
				}
			}
			catch (final Exception e) // NOSONAR
			{
				exceptionMessage = "Exception in .doPost method: " + e.getMessage();
				validateResponse(exceptionMessage);
			}
		}
		validateResponse(response, httpPost, interfaz, exceptionMessage);
		// Get hold of response XML
		try
		{
			if (response != null)
			{
				final HttpEntity entity = response.getEntity();
				responseContent = EntityUtils.toString(entity, responseEncoding);
				EntityUtils.consume(entity);
			}
			else
			{
				responseContent = MSG_FAIL;
			}
		}
		catch (IOException | ParseException e)
		{
			httpPost.abort();
		}
		httpGenericResponse.setCode(code);
		httpGenericResponse.setResponse(responseContent);
		return httpGenericResponse;
	}

	protected HttpGenericResponse doPostJson(final String requestUrl, final String request, final String responseEncoding,
			final Map<String, String> headers, final String interfaz, final boolean userValidate, final String user,
			final String password, final boolean retry)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, ClientProtocolException, IOException
	{
		final HttpGenericResponse httpGenericResponse = new HttpGenericResponse();
		HttpContext context = null;
		HttpResponse response = null;
		String responseContent = "";
		String exceptionMessage = "";
		final HttpClient httpClient = getHttpClient(); // NOSONAR
		final HttpPost httpPost = new HttpPost(requestUrl);
		if (Objects.nonNull(headers))
		{
			for (final Map.Entry<String, String> entry : headers.entrySet())
			{
				httpPost.addHeader(entry.getKey(), entry.getValue());
			}
		}
		//final StringEntity postingString = new StringEntity(request);
		//postingString.setContentType("application/json");
		//postingString.setContentEncoding("UTF-8");
		//httpPost.setEntity(postingString);
		HttpEntity postingString = new StringEntity(request, "UTF-8");
		httpPost.setEntity(postingString);
		if (userValidate)
		{
			context = new BasicHttpContext();
			final CredentialsProvider credsProvider = new BasicCredentialsProvider();
			credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(user, password));
			context.setAttribute(ClientContext.CREDS_PROVIDER, credsProvider);
			context.setAttribute(ClientPNames.HANDLE_AUTHENTICATION, true);
		}

		int code = -1;
		if (!retry)
		{
			try
			{
				response = httpClient.execute(httpPost, context);
				if (response != null)
				{
					code = response.getStatusLine().getStatusCode();
					//final HttpEntity entity = response.getEntity();
					//responseContent = EntityUtils.toString(entity, responseEncoding);
					//EntityUtils.consume(entity);
					final InputStream input = response.getEntity().getContent();
					final Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
					responseContent = CharStreams.toString(reader);
				}
				else
				{
					responseContent = MSG_FAIL;
				}
				httpGenericResponse.setCode(code);
				httpGenericResponse.setResponse(responseContent);
			}
			catch (final Exception e)
			{
				exceptionMessage = "Exception in .doPost method: " + e.getMessage();
				if(exceptionMessage.contains(TIMED_OUT)){
					httpGenericResponse.setCode(-2);
					httpGenericResponse.setResponse(exceptionMessage);
				}
				validateResponse(exceptionMessage);
			}
		}
		else
		{
			for (int iTries = 0; iTries < AbstractProxyControllerServlet.RETRY_COUNT; iTries++)
			{
				try
				{
					response = httpClient.execute(httpPost, context);
					code = response.getStatusLine().getStatusCode();
					log.debug("code[{}]", new Object[]
					{ code });
					if (code == HttpStatus.SC_OK)
					{
						break;
					}
					else
					{
						try
						{
							// life is too short for exponential backoff
							Thread.sleep(AbstractProxyControllerServlet.RETRY_SLEEP_TIME);
						}
						catch (final InterruptedException e1) // NOSONAR
						{
							log.debug("Exception: RETRY_SLEEP_TIME {}", new Object[]
							{ e1.getMessage() });
						}
					}
				}
				catch (final Exception e) // NOSONAR
				{
					exceptionMessage = "Exception in .doPost method: " + e.getMessage();
					validateResponse(exceptionMessage);
				}
			}
			validateResponse(response, httpPost, interfaz, exceptionMessage);
			try
			{
				if (response != null)
				{
					final HttpEntity entity = response.getEntity();
					responseContent = EntityUtils.toString(entity, responseEncoding);
					EntityUtils.consume(entity);
				}
				else
				{
					responseContent = MSG_FAIL;
				}
			}
			catch (IOException | ParseException e)
			{
				httpPost.abort();
			}
			httpGenericResponse.setCode(code);
			httpGenericResponse.setResponse(responseContent);
		}
		return httpGenericResponse;
	}

	protected HttpGenericResponse doGetJson(final String requestUrl, final String responseEncoding,
			final Map<String, String> parameters, final Map<String, String> headers, final String interfaz,
			final boolean userValidate, final String user, final String password, final boolean retry)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException, URISyntaxException
	{
		final HttpGenericResponse httpGenericResponse = new HttpGenericResponse();
		HttpContext context = null;
		HttpResponse response = null;
		String responseContent = "";
		String exceptionMessage = "";
		final HttpClient httpClient = getHttpClient(); // NOSONAR
		final HttpGet httpGet = new HttpGet(requestUrl);
		if (Objects.nonNull(parameters))
		{
			final URIBuilder uriBuilder = new URIBuilder(httpGet.getURI());
			for (final Map.Entry<String, String> entry : parameters.entrySet())
			{
				uriBuilder.addParameter(entry.getKey(), entry.getValue());
			}
			final URI uri = uriBuilder.build(); // NOSONAR
			((HttpRequestBase) httpGet).setURI(uri);
		}
		if (Objects.nonNull(headers))
		{
			for (final Map.Entry<String, String> entry : headers.entrySet())
			{
				httpGet.addHeader(entry.getKey(), entry.getValue());
			}
		}
		if (userValidate)
		{
			context = new BasicHttpContext();
			final CredentialsProvider credsProvider = new BasicCredentialsProvider();
			credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(user, password));
			context.setAttribute(ClientContext.CREDS_PROVIDER, credsProvider);
			context.setAttribute(ClientPNames.HANDLE_AUTHENTICATION, true);
		}
		int code = -1;
		if (!retry)
		{
			try
			{
				response = httpClient.execute(httpGet, context);
				if (response != null)
				{
					code = response.getStatusLine().getStatusCode();
					final HttpEntity entity = response.getEntity();
					responseContent = EntityUtils.toString(entity, responseEncoding);
					EntityUtils.consume(entity);
				}
				else
				{
					responseContent = MSG_FAIL;
				}
				httpGenericResponse.setCode(code);
				httpGenericResponse.setResponse(responseContent);
			}
			catch (final Exception e)
			{
				exceptionMessage = "Exception in .doGet method: " + e.getMessage();
				validateResponse(exceptionMessage);
			}
		}
		else
		{
			for (int iTries = 0; iTries < AbstractProxyControllerServlet.RETRY_COUNT; iTries++)
			{
				try
				{
					response = httpClient.execute(httpGet, context);
					code = response.getStatusLine().getStatusCode();
					log.debug("code[{}]", new Object[]
					{ code });
					if (code == HttpStatus.SC_OK)
					{
						break;
					}
					else
					{
						try
						{
							// life is too short for exponential backoff
							Thread.sleep(AbstractProxyControllerServlet.RETRY_SLEEP_TIME);
						}
						catch (final InterruptedException e1) // NOSONAR
						{
							log.debug("Exception: RETRY_SLEEP_TIME {}", new Object[]
							{ e1.getMessage() });
						}
					}
				}
				catch (final Exception e) // NOSONAR
				{
					exceptionMessage = "Exception in .doGet method: " + e.getMessage();
					validateResponse(exceptionMessage);
				}
			}
			validateResponse(response, httpGet, interfaz, exceptionMessage);
			try
			{
				if (response != null)
				{
					final HttpEntity entity = response.getEntity();
					responseContent = EntityUtils.toString(entity, responseEncoding);
					EntityUtils.consume(entity);
				}
				else
				{
					responseContent = MSG_FAIL;
				}
			}
			catch (IOException | ParseException e)
			{
				httpGet.abort();
			}
			httpGenericResponse.setCode(code);
			httpGenericResponse.setResponse(responseContent);
		}
		return httpGenericResponse;
	}

	private HttpClient getHttpClient() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException
	{
		final RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(DEFAULT_CONN_TIMEOUT_MILLISECONDS).build();
		final SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(DEFAULT_READ_TIMEOUT_MILLISECONDS)
				.setTcpNoDelay(Boolean.TRUE).build();
		final SSLContext sslContext = SSLContexts.createDefault();
		final SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext, new String[]
		{ "TLSv1.1", "TLSv1.2" }, null, SSLConnectionSocketFactory.getDefaultHostnameVerifier());
		final Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
				.register("https", sslConnectionSocketFactory).register("http", new PlainConnectionSocketFactory()).build();
		final PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry); // NOSONAR
		connManager.setValidateAfterInactivity(DEFAULT_STALE_CONN_TIMEOUT_MILLISECONDS);
		connManager.setMaxTotal(DEFAULT_MAX_TOTAL_CONNECTIONS);
		connManager.setDefaultMaxPerRoute(DEFAULT_MAX_CONNECTIONS_PER_ROUTE);
		return HttpClients.custom()
				.setSSLSocketFactory(new SSLConnectionSocketFactory(
						SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build(),
						NoopHostnameVerifier.INSTANCE))
				.setDefaultSocketConfig(socketConfig).setDefaultRequestConfig(requestConfig).setConnectionManager(connManager)
				.build();
	}

	private HttpClient getHttpClientCertificate(final String certificatePath)
			throws CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException
	{
		final InputStream is = getClass().getClassLoader().getResourceAsStream(certificatePath);
		if (Objects.isNull(is))
		{
			log.debug("certificate {} dont load", new Object[]
			{ certificatePath });
			return null;
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
		return HttpClients.custom().setConnectionManager(connManager).setSSLSocketFactory(sslConnectionFactory).build();
	}

	private void validateResponse(final HttpResponse response, final HttpPost post, final String interfaz, final String exception)
	{
		if (response != null)
		{
			final StatusLine status = response.getStatusLine();
			if (status.getStatusCode() >= HttpStatus.SC_MULTIPLE_CHOICES)
			{
				log.debug("Did not receive successful HTTP response: status code = {}, status message = {}", new Object[]
				{ status.getStatusCode(), status.getReasonPhrase() });

				final String subject = "Error: ocurrido un fallo al intentar env�ar la interfaz:  " + interfaz.toUpperCase();
				final String bodyText = "No se ha recibido una respuesta satisfactoria: \n status code =" + status.getStatusCode()
						+ "\n" + " status message = " + status.getReasonPhrase();
				log.debug(subject);
				log.debug(bodyText);
				post.abort();
			}
		}
		else
		{
			log.debug("** EXCEPTION ** {}", new Object[]
			{ exception });
			final String subject = "Error: ocurrido un fallo al intentar env�ar la interfaz:  " + interfaz.toUpperCase();
			final String bodyText = "No se ha recibido una respuesta satisfactoria: \n exception message =" + exception + "\n"
					+ " \n Para mayor detalle, revisar el archivo adjunto";
			log.debug(subject);
			log.debug(bodyText);
			post.abort();
		}
	}

	private void validateResponse(final HttpResponse response, final HttpGet get, final String interfaz, final String exception)
	{
		if (response != null)
		{
			final StatusLine status = response.getStatusLine();
			if (status.getStatusCode() >= HttpStatus.SC_MULTIPLE_CHOICES)
			{
				log.debug("Did not receive successful HTTP response: status code = {}, status message = {}", new Object[]
				{ status.getStatusCode(), status.getReasonPhrase() });

				final String subject = "Error: ocurrido un fallo al intentar env�ar la interfaz:  " + interfaz.toUpperCase();
				final String bodyText = "No se ha recibido una respuesta satisfactoria: \n status code =" + status.getStatusCode()
						+ "\n" + " status message = " + status.getReasonPhrase();
				log.debug(subject);
				log.debug(bodyText);
				get.abort();
			}
		}
		else
		{
			log.debug("** EXCEPTION ** {}", new Object[]
			{ exception });
			final String subject = "Error: ocurrido un fallo al intentar env�ar la interfaz:  " + interfaz.toUpperCase();
			final String bodyText = "No se ha recibido una respuesta satisfactoria: \n exception message =" + exception + "\n"
					+ " \n Para mayor detalle, revisar el archivo adjunto";
			log.debug(subject);
			log.debug(bodyText);
			get.abort();
		}
	}

	public void validateResponse(final String e1)
	{
		log.debug(e1);
	}

}
