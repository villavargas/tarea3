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
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import com.google.common.io.CharStreams;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import mx.com.telcel.core.daos.cvtdat.UserInfoCVTDao;
import mx.com.telcel.core.daos.cvtdat.UserInfoDATDao;
import mx.com.telcel.facades.cvtdat.data.UserInfoCVTData;
import mx.com.telcel.facades.cvtdat.data.UserInfoDATData;
import mx.com.telcel.models.cvtdat.Characteristic;
import mx.com.telcel.models.cvtdat.GenericFault;
import mx.com.telcel.models.cvtdat.Individual;
import mx.com.telcel.models.cvtdat.RelatedParty;
import mx.com.telcel.models.cvtdat.UserAsset;
import mx.com.telcel.models.cvtdat.UserInfo;
import mx.com.telcel.models.cvtdat.UserInfoCVTDATResponse;
import mx.com.telcel.services.CVTDATService;


public class CVTDATServiceImpl implements CVTDATService
{

	@Resource(name = "defaultConfigurationService")
	private ConfigurationService configurationService;

	@Resource(name = "userInfoCVTDao")
	private UserInfoCVTDao userInfoCVTDao;

	@Resource(name = "userInfoDATDao")
	private UserInfoDATDao userInfoDATDao;

	private static final String USER_INFO_CVTDAT = "telcel.cvtdat.userinfo";

	private static final Logger LOG = LoggerFactory.getLogger(CVTDATServiceImpl.class);

	@Override
	public UserInfoCVTDATResponse userInfoCVT(final String accesoSeguro, final String password)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException, URISyntaxException
	{
		LOG.info("Acceso Seguro : " + accesoSeguro);
		LOG.info("password : " + password);
		final HttpClient client = getHttpClientWithoutCertificate(true);
		final String api = configurationService.getConfiguration().getString(USER_INFO_CVTDAT);
		LOG.info("API : " + api);
		final HttpGet serviceRequest = new HttpGet(api);
		final URI uri = new URIBuilder(serviceRequest.getURI()).addParameter("password", password)
				.addParameter("session", accesoSeguro).build();
		((HttpRequestBase) serviceRequest).setURI(uri);
		serviceRequest.addHeader("version", "1");
		serviceRequest.addHeader("messageUUID", System.currentTimeMillis() + "fi");
		serviceRequest.addHeader("senderBy", "tienda");
		final HttpResponse serviceResponse = client.execute(serviceRequest);
		final InputStream input = serviceResponse.getEntity().getContent();
		final Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
		final String responseStr = CharStreams.toString(reader);
		final UserInfoCVTDATResponse response = new UserInfoCVTDATResponse();
		LOG.info("RESPONSE CVT CODE : " + serviceResponse.getStatusLine().getStatusCode());
		LOG.info("RESPONSE CVT MODEL : " + responseStr);
		if (serviceResponse.getStatusLine().getStatusCode() == HttpStatus.OK.value())
		{
			final Type addType = new TypeToken<List<UserInfo>>()
			{
			}.getType();
			final List<UserInfo> userInfo = new Gson().fromJson(responseStr, addType);
			response.setCode(serviceResponse.getStatusLine().getStatusCode());
			if (!Objects.isNull(userInfo))
			{
				if (!userInfo.isEmpty())
				{
					response.setUserInfo(userInfo.get(0));
				}
			}
		}
		else
		{
			final Type addType = new TypeToken<GenericFault>()
			{
			}.getType();
			final GenericFault genericFault = new Gson().fromJson(responseStr, addType);
			response.setCode(serviceResponse.getStatusLine().getStatusCode());
			response.setError(genericFault);
		}
		return response;

	}

	@Override
	public UserInfoDATData saveUserInfoDAT(final String base64DAT)
	{
		LOG.info("B64 : " + base64DAT);
		final byte[] decodedBytes = Base64.getDecoder().decode(base64DAT);
		final String decodedString = new String(decodedBytes);
		LOG.info("DECODE : " + decodedString);
		final Type addType = new TypeToken<UserInfoDATData>()
		{
		}.getType();
		final UserInfoDATData data = new Gson().fromJson(decodedString, addType);
		final boolean save = userInfoDATDao.saveUserInfoDAT(data);
		LOG.info("SAVE : " + save);
		if (!save)
		{
			return null;
		}
		return data;
	}

	@Override
	public UserInfoCVTData saveUserInfoCVT(final UserInfo userInfo)
	{
		final UserInfoCVTData data = decodeUserCVT(userInfo);
		final boolean save = userInfoCVTDao.saveUserInfoCVT(data);
		if (!save)
		{
			return null;
		}
		return data;
	}

	@Override
	public UserInfoDATData existUserDAT(final String username)
	{
		return userInfoDATDao.getUserInfoDAT(username);
	}

	@Override
	public UserInfoCVTData existUserCVT(final String username)
	{
		return userInfoCVTDao.getUserInfoCVT(username);
	}

	@Override
	public UserInfoDATData decodeUserDAT(final String base64DAT)
	{
		try
		{
			LOG.info("B64 : " + base64DAT);
			final byte[] decodedBytes = Base64.getDecoder().decode(base64DAT);
			final String decodedString = new String(decodedBytes);
			LOG.info("DECODE : " + decodedString);
			final Type addType = new TypeToken<UserInfoDATData>()
			{
			}.getType();
			return new Gson().fromJson(decodedString, addType);
		}
		catch (final Exception e)
		{
			LOG.error("ERROR DECODIFICACION : " + e.getMessage());
			return null;
		}
	}

	@Override
	public UserInfoCVTData decodeUserCVT(final UserInfo userInfo)
	{
		final UserInfoCVTData data = new UserInfoCVTData();
		data.setNumEmpleado(findUserAssetId(userInfo.getUserAssets(), "employeeId"));
		data.setUsrUniversal(findUserAssetId(userInfo.getUserAssets(), "universalUser"));
		data.setNombre(userInfo.getGivenName());
		data.setApPaterno(userInfo.getFamilyName());
		data.setApMaterno(userInfo.getSecondLastName());
		try
		{
			data.setRegion(userInfo.getAddress().get(0).getLocality());
		}
		catch (final Exception e)
		{
			LOG.error("CVT COPY MODEL Region: " + e.getMessage());
		}
		data.setIdPerfil(userInfo.getNickname());
		data.setNombrePerfil(userInfo.getProfile());
		data.setFvPrepagoPadre(findRelatedPartyCharacteristicValue(userInfo.getRelatedParty(), "parentSalesForcePre"));
		data.setFvPospagoPadre(findRelatedPartyCharacteristicValue(userInfo.getRelatedParty(), "parentSalesForcePos"));
		data.setFvPrepagoPersonal(findRelatedPartyCharacteristicValue(userInfo.getRelatedParty(), "personalSaleForcePrepay"));
		data.setFvPospagoPersonal(findRelatedPartyCharacteristicValue(userInfo.getRelatedParty(), "personalSaleForcePospay"));
		data.setFvPrepagoReporte(findRelatedPartyCharacteristicValue(userInfo.getRelatedParty(), "reportSalesForcePre"));
		data.setFvPospagoReporte(findRelatedPartyCharacteristicValue(userInfo.getRelatedParty(), "reportSalesForcePos"));
		data.setEscenario(findRelatedPartyId(userInfo.getRelatedParty(), "salesForceScenario"));
		data.setDireccion(findRelatedPartyId(userInfo.getRelatedParty(), "direction"));
		data.setSubDireccion(findRelatedPartyId(userInfo.getRelatedParty(), "subDirection"));
		data.setGerencia(findRelatedPartyId(userInfo.getRelatedParty(), "managerId"));
		data.setDepartamento(findRelatedPartyId(userInfo.getRelatedParty(), "departmentId"));
		data.setDescDepartamento(findRelatedPartyCharacteristicValue(userInfo.getRelatedParty(), "departmentDescription"));
		data.setPuesto(findRelatedPartyIndividualCharacteristicValue(userInfo.getRelatedParty(), "employeePosition"));
		try
		{
			data.setCorreo(userInfo.getEmail().get(0).getAddress());
		}
		catch (final Exception e)
		{
			LOG.error("CVT COPY MODEL Correo: " + e.getMessage());
		}
		return data;
	}

	private String findUserAssetId(final List<UserAsset> userAssetList, final String identifier)
	{
		try
		{
			if (Objects.isNull(userAssetList))
			{
				return "";
			}
			for (final UserAsset ua : userAssetList)
			{
				if (ua.getIdentifierType().equals(identifier))
				{
					return ua.getId();
				}
			}
		}
		catch (final Exception e)
		{
			LOG.error("CVT COPY MODEL " + identifier + " : " + e.getMessage());
		}
		return "";
	}

	private String findRelatedPartyCharacteristicValue(final List<RelatedParty> relatedPartyList, final String name)
	{
		try
		{
			if (Objects.isNull(relatedPartyList))
			{
				return "";
			}
			for (final RelatedParty rp : relatedPartyList)
			{
				for (final Characteristic c : rp.getCharacteristic())
				{
					if (c.getName().equals(name))
					{
						return c.getValue();
					}
				}
			}
		}
		catch (final Exception e)
		{
			LOG.error("CVT COPY MODEL " + name + " : " + e.getMessage());
		}
		return "";
	}

	private String findRelatedPartyId(final List<RelatedParty> relatedPartyList, final String identifier)
	{
		try
		{
			if (Objects.isNull(relatedPartyList))
			{
				return "";
			}
			for (final RelatedParty rp : relatedPartyList)
			{
				if (rp.getIdentifierType().equals(identifier))
				{
					return rp.getId();
				}
			}
		}
		catch (final Exception e)
		{
			LOG.error("CVT COPY MODEL " + identifier + " : " + e.getMessage());
		}
		return "";
	}

	private String findRelatedPartyIndividualCharacteristicValue(final List<RelatedParty> relatedPartyList, final String name)
	{
		try
		{
			if (Objects.isNull(relatedPartyList))
			{
				return "";
			}
			for (final RelatedParty rp : relatedPartyList)
			{
				final Individual in = rp.getIndividual();
				if (!Objects.isNull(in))
				{
					for (final Characteristic c : in.getCharacteristic())
					{
						if (c.getName().equals(name))
						{
							return c.getValue();
						}
					}
				}
			}
		}
		catch (final Exception e)
		{
			LOG.error("CVT COPY MODEL " + name + " : " + e.getMessage());
		}
		return "";
	}

	public HttpClient getHttpClientWithoutCertificate(final boolean disableRedirect)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException
	{
		final SSLContextBuilder builder = new SSLContextBuilder();
		builder.loadTrustMaterial(null, (chain, authType) -> true);
		final SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build(), NoopHostnameVerifier.INSTANCE);
		if (disableRedirect)
		{
			return HttpClients.custom().setSSLSocketFactory(sslsf).disableRedirectHandling().build();
		}
		else
		{
			return HttpClients.custom().setSSLSocketFactory(sslsf).build();
		}
	}

}
