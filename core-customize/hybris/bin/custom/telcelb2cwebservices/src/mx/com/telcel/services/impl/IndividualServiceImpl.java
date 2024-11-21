package mx.com.telcel.services.impl;

import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import mx.com.telcel.models.individual.GenericFault;
import mx.com.telcel.models.individual.Individual;
import mx.com.telcel.models.individual.IndividualResponse;
import mx.com.telcel.services.IndividualService;
import mx.com.telcel.utilities.AbstractProxyControllerServlet;
import mx.com.telcel.utilities.HttpGenericResponse;
import mx.com.telcel.utilities.JsonConverter;


public class IndividualServiceImpl extends AbstractProxyControllerServlet implements IndividualService
{

	@Resource(name = "defaultConfigurationService")
	private ConfigurationService configurationService;

	@Resource(name = "jsonConverter")
	private JsonConverter jsonConverter;

	private static final String INDIVIDUAL_IDENTIFICATION = "telcel.party.management.individual.identification";
	private static final String INDIVIDUAL_IDENTIFICATION_VERSION = "telcel.party.management.individual.identification.version";
	private static final String INDIVIDUAL_IDENTIFICATION_SENDBY = "telcel.party.management.individual.identification.sendBy";
	private static final String INDIVIDUAL_IDENTIFICATION_USER = "telcel.party.management.individual.identification.user";
	public static final String YYYY_MM_DD_T_HH_MM_SS_SSS = "yyyy-MM-dd'T'HH:mm:ss.SSS";

	private static final Logger LOG = LoggerFactory.getLogger(IndividualServiceImpl.class);

	@Override
	public IndividualResponse validateRFC(final String rfc)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException, URISyntaxException
	{
		final SimpleDateFormat formatter = new SimpleDateFormat(YYYY_MM_DD_T_HH_MM_SS_SSS);
		final Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("individualId", rfc);
		parameters.put("identificationType", "RFC");
		final Map<String, String> headers = new HashMap<String, String>();
		headers.put("version", configurationService.getConfiguration().getString(INDIVIDUAL_IDENTIFICATION_VERSION));
		headers.put("messageUUID", System.currentTimeMillis() + "rfcap");
		headers.put("sendBy", configurationService.getConfiguration().getString(INDIVIDUAL_IDENTIFICATION_SENDBY));
		headers.put("requestDate", formatter.format(new Date()));
		headers.put("user", configurationService.getConfiguration().getString(INDIVIDUAL_IDENTIFICATION_USER));
		final String apiUrl = configurationService.getConfiguration().getString(INDIVIDUAL_IDENTIFICATION);
		final HttpGenericResponse serviceResponse = doGetJson(apiUrl, HTTP_UTF8_ENCODING, parameters, headers, "Individual", false,
				null, null, false);

		LOG.info("serviceResponse [" + rfc + "] = " + serviceResponse.getResponse());

		final IndividualResponse response = new IndividualResponse();
		if (serviceResponse.getCode() == HttpStatus.OK.value())
		{
			final Type addType = new TypeToken<List<Individual>>()
			{
			}.getType();
			final List<Individual> individualArray = new Gson().fromJson(serviceResponse.getResponse(), addType);
			response.setCode(serviceResponse.getCode());
			if (Objects.nonNull(individualArray))
			{
				response.setIndividual(individualArray.get(0));
			}
		}
		else
		{
			final GenericFault genericFault = jsonConverter.convertJsonToObject(serviceResponse.getResponse(), GenericFault.class);
			response.setCode(serviceResponse.getCode());
			response.setError(genericFault);
		}
		return response;
	}

}
