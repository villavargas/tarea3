package mx.com.telcel.services.impl;

import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import mx.com.telcel.core.daos.fedex.TelcelFedexDao;
import mx.com.telcel.core.model.FedexGuidesModel;
import mx.com.telcel.models.Error;
import mx.com.telcel.models.fedex.request.FedexRequest;
import mx.com.telcel.models.fedex.request.HeaderRequest;
import mx.com.telcel.models.fedex.response.ErrorResponse;
import mx.com.telcel.models.fedex.response.FedexRateResponse;
import mx.com.telcel.models.fedex.response.FedexResponse;
import mx.com.telcel.services.FedexService;
import mx.com.telcel.utilities.AbstractProxyControllerServlet;
import mx.com.telcel.utilities.HttpGenericResponse;
import mx.com.telcel.utilities.JsonConverter;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.assertj.core.util.Strings;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;


public class FedexServiceImpl extends AbstractProxyControllerServlet implements FedexService
{

	private static final Logger LOG = Logger.getLogger(FedexServiceImpl.class);

	@Resource(name = "jsonConverter")
	private JsonConverter jsonConverter;

	@Resource(name = "defaultConfigurationService")
	private ConfigurationService configurationService;

	private ModelService modelService;
	private TelcelFedexDao telcelFedexDao;


	private static final String RATE = "fedex.services.rate";
	private static final String SHIPMENT = "fedex.services.shipment";
	private static final String PICKUP = "fedex.services.pickup";

	public FedexServiceImpl() {
	}

	@Override
	public Object retrieveRate(final FedexRequest request, final HeaderRequest headerRequest)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException
	{
		final FedexRateResponse fedexRate = new FedexRateResponse();
		LocalDateTime localDate =  null;
		LocalDateTime localDateEntrega = null;
		int days = 3;
		final String requestJson = jsonConverter.convertObjectToString(request);
		final Map<String, String> headers = createHeaderMap(headerRequest);
		final String urlApi = configurationService.getConfiguration().getString(RATE);
		final HttpGenericResponse response = this.doPostJson(urlApi, requestJson, HTTP_UTF8_ENCODING, headers,
				"RETRIEVE_RATE_FEDEX", false, "", "", false);
		if (response.getCode() == 200)
		{
			final FedexResponse fedexResponse = jsonConverter.convertJsonToObject(response.getResponse(), FedexResponse.class);
			if(fedexResponse.getService().getOrder().getServiceRates().get(0).getDeliveryDate() != null){
				localDate = LocalDateTime.parse(fedexResponse.getService().getOrder().getServiceRates().get(0).getDeliveryDate());
				localDateEntrega = localDate.plusDays(SumarTresDias(localDate, days));
			} else {
				localDate = LocalDateTime.now();
				localDateEntrega = localDate.plusDays(SumarCuatroDias(localDate, days));
			}
			String month = jsonConverter.converterStringMonth(localDateEntrega.getMonthValue());
			fedexRate.setDescription(String.valueOf(localDateEntrega.getDayOfMonth())+" de "+month+ " de "+String.valueOf(localDateEntrega.getYear()));
			fedexRate.setCode(0);
			fedexRate.setDeliveryDate(fedexResponse.getService().getOrder().getServiceRates().get(0).getDeliveryDate());
			return fedexRate;
		}
		else
		{
			localDate = LocalDateTime.now();
			localDateEntrega = localDate.plusDays(SumarCuatroDias(localDate, days));
			String month = jsonConverter.converterStringMonth(localDateEntrega.getMonthValue());
			fedexRate.setDescription(String.valueOf(localDateEntrega.getDayOfMonth())+" de "+month+ " de "+String.valueOf(localDateEntrega.getYear()));
			fedexRate.setCode(1);
			return fedexRate;
			//return jsonConverter.convertJsonToObject(response.getResponse(), ErrorResponse.class);
		}
	}

	@Override
	public Object createShipment(final FedexRequest request, final HeaderRequest headerRequest)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException
	{
		final String requestJson = jsonConverter.convertObjectToString(request);
		LOG.info("REQUEST CREATE_SHIPMENT: "+requestJson);
		final Map<String, String> headers = createHeaderMap(headerRequest);
		final String urlApi = configurationService.getConfiguration().getString(SHIPMENT);
		final HttpGenericResponse response = this.doPostJson(urlApi, requestJson, HTTP_UTF8_ENCODING, headers,
				"CREATE_SHIPMENT_FEDEX", false, "", "", false);
		if (response.getCode() == 200)
		{
			LOG.info("RESPONSE CREATE_SHIPMENT: "+response.getResponse());
			return jsonConverter.convertJsonToObject(response.getResponse(), FedexResponse.class);
		}
		else
		{
			if(response.getCode() == -2){
				LOG.info("ERROR RESPONSE CREATE_SHIPMENT TIMED OUT: "+response.getResponse());
				Error error = new Error();
				error.setDescription(response.getResponse());
				error.setCode(response.getCode());
				return error;
			} else {
				LOG.info("ERROR RESPONSE CREATE_SHIPMENT: "+response.getResponse());
				return jsonConverter.convertJsonToObject(response.getResponse(), ErrorResponse.class);
			}
		}
	}

	@Override
	public Object retrieveTrack(final Map<String, String> parameters, final HeaderRequest headerRequest)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException, URISyntaxException
	{
		final Map<String, String> headers = createHeaderMap(headerRequest);
		final String urlApi = configurationService.getConfiguration().getString(SHIPMENT);
		final HttpGenericResponse response = this.doGetJson(urlApi, HTTP_UTF8_ENCODING, parameters, headers, "RETRIEVE_TRACK_FEDEX",
				false, "", "", false);
		if (response.getCode() == 200)
		{
			return jsonConverter.convertJsonToObject(response.getResponse(), FedexResponse.class);
		}
		else
		{
			return jsonConverter.convertJsonToObject(response.getResponse(), ErrorResponse.class);
		}
	}

	@Override
	public Object createPickup(final FedexRequest request, final HeaderRequest headerRequest)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException
	{
		final String requestJson = jsonConverter.convertObjectToString(request);
		LOG.info("REQUEST CREATE_PICKUP: "+requestJson);
		final Map<String, String> headers = createHeaderMap(headerRequest);
		final String urlApi = configurationService.getConfiguration().getString(PICKUP);
		final HttpGenericResponse response = this.doPostJson(urlApi, requestJson, HTTP_UTF8_ENCODING, headers,
				"CREATE_PICKUP_FEDEX", false, "", "", false);
		if (response.getCode() == 200)
		{
			LOG.info("RESPONSE CREATE_PICKUP: "+response.getResponse());
			return jsonConverter.convertJsonToObject(response.getResponse(), FedexResponse.class);
		}
		else
		{
			LOG.info("ERROR RESPONSE CREATE_PICKUP: "+response.getResponse());
			return jsonConverter.convertJsonToObject(response.getResponse(), ErrorResponse.class);
		}
	}

	@Override
	public Object retrievePickup(final Map<String, String> parameters, final HeaderRequest headerRequest)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException, URISyntaxException
	{
		final Map<String, String> headers = createHeaderMap(headerRequest);
		final String urlApi = configurationService.getConfiguration().getString(PICKUP);
		final HttpGenericResponse response = this.doGetJson(urlApi, HTTP_UTF8_ENCODING, parameters, headers,
				"RETRIEVE_PICKUP_FEDEX", false, "", "", false);
		if (response.getCode() == 200)
		{
			return jsonConverter.convertJsonToObject(response.getResponse(), FedexResponse.class);
		}
		else
		{
			return jsonConverter.convertJsonToObject(response.getResponse(), ErrorResponse.class);
		}
	}

	@Override
	public void removeGuideFromConsignment(ConsignmentModel consignmentModel) {
		if (consignmentModel != null && StringUtils.isNotEmpty(consignmentModel.getFedexGuide())) {
			FedexGuidesModel fedexGuidesModel = telcelFedexDao.findFedexGuideByConsignmentIdAndGuideCode(consignmentModel.getCode(), consignmentModel.getFedexGuide());
			if(fedexGuidesModel != null){
				LOG.info(String.format("\n\nRemoving guide: %s, %s, %s, %s\n",fedexGuidesModel.getGuide(), fedexGuidesModel.getConsignmentId(),
						fedexGuidesModel.getConsignmentProccessId(), fedexGuidesModel.getOrderId()));
				modelService.remove(fedexGuidesModel);
			}
		}
	}

	private Map<String, String> createHeaderMap(final HeaderRequest headerRequest)
	{
		final Map<String, String> headers = new HashMap<String, String>();
		headers.put("Accept", HTTP_APPLICATION_JSON);
		headers.put("Content-Type", HTTP_APPLICATION_JSON);
		if (Objects.nonNull(headerRequest))
		{
			if (!Strings.isNullOrEmpty(headerRequest.getMessageUUID()))
			{
				headers.put("messageUUID", headerRequest.getMessageUUID());
			}
			if (!Strings.isNullOrEmpty(headerRequest.getRequestDate()))
			{
				headers.put("requestDate", headerRequest.getRequestDate());
			}
			if (!Strings.isNullOrEmpty(headerRequest.getSendBy()))
			{
				headers.put("sendBy", headerRequest.getSendBy());
			}
			if (!Strings.isNullOrEmpty(headerRequest.getVersion()))
			{
				headers.put("version", headerRequest.getVersion());
			}
			if (!Strings.isNullOrEmpty(headerRequest.getIpClient()))
			{
				headers.put("ipClient", headerRequest.getIpClient());
			}
			if (!Strings.isNullOrEmpty(headerRequest.getIpServer()))
			{
				headers.put("ipServer", headerRequest.getIpServer());
			}
			if (!Strings.isNullOrEmpty(headerRequest.getUser()))
			{
				headers.put("user", headerRequest.getUser());
			}
		}
		return headers;
	}


	public static int generaNumero() {
		int min = 1;
		int max = 2;
		Random random = new Random(); // NOSONAR
		int value = random.nextInt(max + min) + min;
		return value;
	}

	private static int SumarCuatroDias(LocalDateTime localDateEntrega, int days) {
		if (localDateEntrega.getDayOfWeek().equals(DayOfWeek.FRIDAY)
				|| localDateEntrega.getDayOfWeek().equals(DayOfWeek.THURSDAY)
				|| localDateEntrega.getDayOfWeek().equals(DayOfWeek.WEDNESDAY)) {
			days = days + 3;
		} else if (localDateEntrega.getDayOfWeek().equals(DayOfWeek.SATURDAY)
				|| localDateEntrega.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
			days = days + 2;
		}
		return days;
	}

	private static int SumarTresDias(LocalDateTime localDateEntrega, int days) {
		if (localDateEntrega.getDayOfWeek().equals(DayOfWeek.FRIDAY)
				|| localDateEntrega.getDayOfWeek().equals(DayOfWeek.THURSDAY)
				|| localDateEntrega.getDayOfWeek().equals(DayOfWeek.WEDNESDAY)) {
			days = days + 2;
		} else if (localDateEntrega.getDayOfWeek().equals(DayOfWeek.SATURDAY)
				|| localDateEntrega.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
			days = days + 1;
		}
		return days;
	}

	public TelcelFedexDao getTelcelFedexDao() {
		return telcelFedexDao;
	}

	public void setTelcelFedexDao(TelcelFedexDao telcelFedexDao) {
		this.telcelFedexDao = telcelFedexDao;
	}

	public ModelService getModelService() {
		return modelService;
	}

	public void setModelService(ModelService modelService) {
		this.modelService = modelService;
	}
}
