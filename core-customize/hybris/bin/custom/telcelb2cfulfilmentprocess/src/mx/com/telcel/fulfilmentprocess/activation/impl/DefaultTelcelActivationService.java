package mx.com.telcel.fulfilmentprocess.activation.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.CronJobService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Async;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import mx.com.telcel.core.util.JsonConverter;
import mx.com.telcel.facades.order.data.ActivationResponseError;
import mx.com.telcel.facades.order.data.ActivationResponseErrorObject;
import mx.com.telcel.facades.order.data.Area;
import mx.com.telcel.facades.order.data.Channel;
import mx.com.telcel.facades.order.data.Characteristic;
import mx.com.telcel.facades.order.data.ContactMedium;
import mx.com.telcel.facades.order.data.ItemProductOfferingPrice;
import mx.com.telcel.facades.order.data.ItemProductOfferingPriceCategory;
import mx.com.telcel.facades.order.data.ItemProductOfferingPriceData;
import mx.com.telcel.facades.order.data.ItemProductOfferingPriceTaxIncludedAmount;
import mx.com.telcel.facades.order.data.ProductOrder;
import mx.com.telcel.facades.order.data.ProductOrderItem;
import mx.com.telcel.facades.order.data.ProductOrderItemCharacteristic;
import mx.com.telcel.facades.order.data.ProductOrderItemProduct;
import mx.com.telcel.facades.order.data.ProductOrderItemProductOffering;
import mx.com.telcel.facades.order.data.ProductOrderingResponse;
import mx.com.telcel.facades.order.data.RelatedParty;
import mx.com.telcel.facades.order.data.ResourceCharacteristic;
import mx.com.telcel.facades.order.data.ResourceProductOrder;
import mx.com.telcel.facades.order.data.RespuestaActivacion;
import mx.com.telcel.facades.zipcodes.data.CodigosPostalesTelcelData;
import mx.com.telcel.fulfilmentprocess.actions.consignment.ActivationAction;
import mx.com.telcel.fulfilmentprocess.activation.TelcelActivationService;


public class DefaultTelcelActivationService implements TelcelActivationService
{

	private static final String TELCEL_SALES_FORCE_R = "telcel.sales.force.r";
	private static final Logger LOG = Logger.getLogger(DefaultTelcelActivationService.class);
	private static final String TELCEL_API_ACTIVACION_URL = "telcel.api.activacion.URL";
	private static final String TELCEL_API_ACTIVACION_USER_NAME = "telcel.api.activacion.userName";
	private static final String TELCEL_API_ACTIVACION_VERSION = "telcel.api.activacion.version";
	private static final String TELCEL_API_ACTIVACION_SENDER_BY = "telcel.api.activacion.senderBy";
	private static final String APPLICATION_JSON = "application/json";
	private static final String TLINEA = "TLINEA";
	private static final String CHIP = "CHIP";
	private static final String KIT = "KIT";
	private static final String SIM = "SIM";
	private static final String LOGICAL_RESOURCE = "logicalResource";
	private static final String ESIM = "esim";
	private static final String PHYSICAL_RESOURCE = "physicalResource";
	private static final String EQUIPMENT = "equipment";
	private static final String IMEI = "imei";
	private static final String TIENDA_LINEA = "TiendaLinea";
	private static final String MOBILEBASE = "mobilebase";
	private static final String INITIAL_BALANCE = "initialBalance";
	private static final String ACTIVATION_TYPE = "activationType";
	private static final String PAYMENT_MODEL_TYPE = "paymentModelType";
	private static final String ADD = "add";
	private static final String STRING_EMPTY = "";
	private static final String EQUIPMENT_COST = "equipmentCost";
	private static final String USER_ACTIVATION = "userActivation";
	private static final String SALES_FORCE_ALPHANUMERIC = "salesForceAlphanumeric";
	private static final String CERTIFICATE_PATH = "telcel.api.activacion.certificate.path";
	private static final String SALES_FORCE = "salesForce";
	public static final String CITY = "city";
	public static final String POPULATION = "population";
	public static final String CONTENT_TYPE = "Content-type";
	public static final String USER_NAME = "userName";
	public static final String VERSION = "version";
	public static final String MESSAGE_UUID = "messageUUID";
	public static final String REQUEST_DATE = "requestDate";
	public static final String SENDER_BY = "senderBy";
	public static final String YYYY_MM_DD_T_HH_MM_SS_SSS = "yyyy-MM-dd'T'HH:mm:ss.SSS";
	private static final String SKU = "sku";
	public static final String ACTIVACION_CRON_JOB = "activacionCronJob";
	private static final String TELCEL = "telcel";
	private static final String TELMEX = "telmex";



	@Resource(name = "defaultConfigurationService")
	private ConfigurationService configurationService;

	@Resource(name = "defaultTelcelCodigosPostalesService")
	private mx.com.telcel.core.services.impl.DefaultTelcelCodigosPostalesService codigosPostalesService;

	@Resource(name = "jsonConverterService")
	private JsonConverter jsonConverter;

	@Resource
	private CronJobService cronJobService;

	public ProductOrderingResponse activationPost(final String region, final String postalCode, final String imei,
			final String iccid, final String salesForce, final String salesForceAlphanumeric,
			final AbstractOrderEntryModel entryModel, final String esquemaCobro, final ConsignmentModel consignmentModel,
			final String store)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException
	{

		final String urlApi = configurationService.getConfiguration().getString(TELCEL_API_ACTIVACION_URL);

		final HttpPost serviceRequest = new HttpPost(urlApi);
		final SimpleDateFormat formatter = new SimpleDateFormat(YYYY_MM_DD_T_HH_MM_SS_SSS);
		addHeaders(serviceRequest, TLINEA + entryModel.getOrder().getCode(),
				formatter.format(entryModel.getOrder().getCreationtime()));

		final ProductOrder order = new ProductOrder();

		final RelatedParty relatedParty = new RelatedParty();
		relatedParty.setRole(SALES_FORCE);
		relatedParty.setId(salesForce);

		String idPoblacion = STRING_EMPTY;

		String sku = "";
		if (store.equals(TELCEL))
		{
			if (consignmentModel.getAdditionalServiceEntry() != null)
			{
				sku = consignmentModel.getAdditionalServiceEntry().getAdditionalServiceProduct().getSku();
				LOG.info("Has additional Service Entry:: SKU: " + sku);
			}
			else
			{
				sku = entryModel.getProduct().getSku();
				LOG.info("Not Has additional Service Entry:: SKU: " + sku);
			}
		}
		else
		{
			LOG.info("Send ProductSKU, Telmex for Activation");
			sku = entryModel.getProduct().getSku();
		}


		if (postalCode != null)
		{
			final CodigosPostalesTelcelData codigosPostalesTelcel = codigosPostalesService.getInfForZipcode(postalCode);
			if (codigosPostalesTelcel != null)
			{
				idPoblacion = codigosPostalesTelcel.getCvePoblacion();
			}
		}


		final Characteristic characteristic = new Characteristic();
		characteristic.setLocality(region);
		characteristic.setPostCode(postalCode);
		final Area areaCity = new Area();
		areaCity.setId(STRING_EMPTY);
		areaCity.setType(CITY);
		final Area areaPopulation = new Area();
		areaPopulation.setType(POPULATION);
		areaPopulation.setId(idPoblacion);
		final List<Area> areas = new ArrayList<>();
		areas.add(areaCity);
		areas.add(areaPopulation);
		characteristic.setArea(areas);

		final ContactMedium contactMedium = new ContactMedium();
		contactMedium.setCharacteristic(characteristic);
		final List<ContactMedium> contactMediumList = new ArrayList<>();
		contactMediumList.add(contactMedium);
		relatedParty.setContactMedium(contactMediumList);

		final String userName = configurationService.getConfiguration().getString(TELCEL_API_ACTIVACION_USER_NAME);

		final RelatedParty relatedPartyTwo = new RelatedParty();
		relatedPartyTwo.setRole(USER_ACTIVATION);
		relatedPartyTwo.setId(userName);

		final RelatedParty relatedPartySalesForce = new RelatedParty();
		relatedPartySalesForce.setRole(SALES_FORCE_ALPHANUMERIC);
		relatedPartySalesForce.setId(salesForceAlphanumeric);

		final List<RelatedParty> relatedParties = new ArrayList<>();
		relatedParties.add(relatedParty);
		relatedParties.add(relatedPartyTwo);
		relatedParties.add(relatedPartySalesForce);

		order.setRelatedParty(relatedParties);

		final Double precio = entryModel.getBasePrice();

		final ResourceCharacteristic resourceCharacteristicOrder = new ResourceCharacteristic();
		resourceCharacteristicOrder.setName(PHYSICAL_RESOURCE);
		resourceCharacteristicOrder.setValue(precio.toString());
		resourceCharacteristicOrder.setValueType(EQUIPMENT_COST);

		final List<ResourceCharacteristic> characteristicsResourceOrder = new ArrayList<>();
		characteristicsResourceOrder.add(resourceCharacteristicOrder);

		final ResourceProductOrder resourceOrder = new ResourceProductOrder();
		resourceOrder.setResourceCharacteristic(characteristicsResourceOrder);

		final List<ResourceProductOrder> resourceListOrder = new ArrayList<>();
		resourceListOrder.add(resourceOrder);

		order.setResource(resourceListOrder);

		final Channel channel = new Channel();
		channel.setId(userName);

		final List<Channel> channels = new ArrayList<>();
		channels.add(channel);
		order.setChannel(channels);

		final ProductOrderItem productOrderItem = new ProductOrderItem();
		productOrderItem.setAction(ADD);

		final ProductOrderItemProduct productOrderItemProduct = new ProductOrderItemProduct();
		final String productName = imei == null || imei.equals(STRING_EMPTY) ? CHIP : KIT;
		productOrderItemProduct.setName(productName);
		productOrderItemProduct.setDescription(SIM);

		final ProductOrderItemCharacteristic productOrderItemCharacteristic = new ProductOrderItemCharacteristic();
		productOrderItemCharacteristic.setName(PAYMENT_MODEL_TYPE);
		productOrderItemCharacteristic.setValue(esquemaCobro);
		final ProductOrderItemCharacteristic productOrderItemCharacteristicTwo = new ProductOrderItemCharacteristic();
		productOrderItemCharacteristicTwo.setValue(STRING_EMPTY);
		productOrderItemCharacteristicTwo.setName(ACTIVATION_TYPE);
		final List<ProductOrderItemCharacteristic> orderItemCharacteristics = new ArrayList<>();
		orderItemCharacteristics.add(productOrderItemCharacteristic);
		orderItemCharacteristics.add(productOrderItemCharacteristicTwo);
		productOrderItemProduct.setCharacteristic(orderItemCharacteristics);

		final List<ProductOrderItemProduct> orderItemProducts = new ArrayList<>();
		orderItemProducts.add(productOrderItemProduct);
		productOrderItem.setProduct(orderItemProducts);

		final ItemProductOfferingPriceData itemProductOfferingPriceData = new ItemProductOfferingPriceData();
		final ItemProductOfferingPriceTaxIncludedAmount taxIncludedAmount = new ItemProductOfferingPriceTaxIncludedAmount();
		taxIncludedAmount.setValue(0.0);
		itemProductOfferingPriceData.setTaxIncludedAmount(taxIncludedAmount);

		final ItemProductOfferingPrice itemProductOfferingPrice = new ItemProductOfferingPrice();
		itemProductOfferingPrice.setPrice(itemProductOfferingPriceData);
		itemProductOfferingPrice.setPriceType(INITIAL_BALANCE);
		final List<ItemProductOfferingPrice> offeringPrices = new ArrayList<>();
		offeringPrices.add(itemProductOfferingPrice);

		final ItemProductOfferingPriceCategory itemProductOfferingPriceCategory = new ItemProductOfferingPriceCategory();
		itemProductOfferingPriceCategory.setName(MOBILEBASE);
		final List<ItemProductOfferingPriceCategory> priceCategories = new ArrayList<>();
		priceCategories.add(itemProductOfferingPriceCategory);

		final ProductOrderItemProductOffering productOrderItemProductOffering = new ProductOrderItemProductOffering();
		productOrderItemProductOffering.setId(TIENDA_LINEA);
		productOrderItemProductOffering.setCategory(priceCategories);
		productOrderItemProductOffering.setProductOfferingPrice(offeringPrices);
		final List<ProductOrderItemProductOffering> productOfferingsList = new ArrayList<>();
		productOfferingsList.add(productOrderItemProductOffering);

		productOrderItem.setProductOffering(productOfferingsList);

		final ResourceCharacteristic imeiResource = new ResourceCharacteristic();
		imeiResource.setName(IMEI);
		imeiResource.setValue(imei);

		final ResourceCharacteristic skuResource = new ResourceCharacteristic();
		skuResource.setName(SKU);
		skuResource.setValue(sku);

		final List<ResourceCharacteristic> characteristics = new ArrayList<>();
		characteristics.add(imeiResource);
		characteristics.add(skuResource);

		final mx.com.telcel.facades.order.data.Resource resource = new mx.com.telcel.facades.order.data.Resource();
		resource.setName(EQUIPMENT);
		resource.setType(PHYSICAL_RESOURCE);
		resource.setResourceCharacteristic(characteristics);

		final mx.com.telcel.facades.order.data.Resource resourceTwo = new mx.com.telcel.facades.order.data.Resource();
		resourceTwo.setName(ESIM);
		resourceTwo.setId(iccid);
		resourceTwo.setType(LOGICAL_RESOURCE);

		final List<mx.com.telcel.facades.order.data.Resource> resourceList = new ArrayList<>();
		resourceList.add(resource);
		resourceList.add(resourceTwo);

		productOrderItem.setResource(resourceList);

		final List<ProductOrderItem> productOrderItemsList = new ArrayList<>();
		productOrderItemsList.add(productOrderItem);
		order.setProductOrderItem(productOrderItemsList);

		final ObjectMapper mapper = new ObjectMapper();
		String jsonRequest = STRING_EMPTY;
		try
		{
			jsonRequest = mapper.writeValueAsString(order);
		}
		catch (final JsonProcessingException e)
		{
			LOG.error("Ocurrio un error al convertir a JSON el objeto para la activacion. Error:   " + e.getMessage());
		}
		LOG.info("Request Activacion Iccid :" + iccid + " Imei: " + imei + " Json " + jsonRequest);
		//consignmentModel.setActivacionesRequest(jsonRequest);
		final StringEntity postingString = new StringEntity(jsonRequest);
		serviceRequest.setEntity(postingString);

		final HttpClient client = getHttpClientCertificate();

		final HttpResponse serviceResponse = client.execute(serviceRequest);
		final String jsonResponse = EntityUtils.toString(serviceResponse.getEntity(), StandardCharsets.UTF_8);
		LOG.info("\n Response Activacion : " + jsonResponse);
		//consignmentModel.setActivacionesResponse(jsonResponse);
		final StatusLine statusLine = serviceResponse.getStatusLine();

		if (statusLine.getStatusCode() == 200 || statusLine.getStatusCode() == 202)
		{
			LOG.info("\n respuesta de estatus:: " + statusLine.getStatusCode());
			final RespuestaActivacion respuestaActivacion = jsonConverter.convertJsonToObject(jsonResponse,
					RespuestaActivacion.class);
			if (respuestaActivacion != null && !respuestaActivacion.getProductOrdering().isEmpty())
			{
				LOG.info("!respuestaActivacion.getProductOrdering().isEmpty()");
				final ProductOrderingResponse response = new ProductOrderingResponse();
				response.setId(respuestaActivacion.getProductOrdering().get(0).getId());
				response.setState(respuestaActivacion.getProductOrdering().get(0).getState());
				response.setError(Boolean.FALSE);

				return response;
			}
		}
		else
		{
			final ActivationResponseError responseError = jsonConverter.convertJsonToObject(jsonResponse,
					ActivationResponseError.class);
			if (responseError != null && responseError.getErrorList() != null && !responseError.getErrorList().getError().isEmpty())
			{
				LOG.info("Error en activacion");
				final ProductOrderingResponse response = new ProductOrderingResponse();
				final ActivationResponseErrorObject error = responseError.getErrorList().getError().get(0);
				if (error != null)
				{
					LOG.info("Id:: " + error.getCode());
					response.setId(error.getCode());
					LOG.info("state:: " + error.getBusinessMeaning());
					response.setState(error.getBusinessMeaning());
					response.setError(Boolean.TRUE);

					return response;
				}
			}
		}

		return null;
	}

	private void addHeaders(final HttpPost serviceRequest, final String messageUUID, final String requestDate)
	{
		final String userName = configurationService.getConfiguration().getString(TELCEL_API_ACTIVACION_USER_NAME);
		final String version = configurationService.getConfiguration().getString(TELCEL_API_ACTIVACION_VERSION);
		final String senderBy = configurationService.getConfiguration().getString(TELCEL_API_ACTIVACION_SENDER_BY);

		serviceRequest.addHeader(CONTENT_TYPE, APPLICATION_JSON);
		serviceRequest.addHeader(USER_NAME, userName);
		serviceRequest.addHeader(VERSION, version);
		serviceRequest.addHeader(MESSAGE_UUID, messageUUID);
		serviceRequest.addHeader(REQUEST_DATE, requestDate);
		serviceRequest.addHeader(SENDER_BY, senderBy);
	}

	private HttpClient getHttpClientCertificate()
			throws CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException
	{

		final String certificatePath = configurationService.getConfiguration().getString(CERTIFICATE_PATH);
		if (certificatePath == null || certificatePath.equals(STRING_EMPTY))
		{
			return HttpClients.custom()
					.setSSLSocketFactory(new SSLConnectionSocketFactory(
							SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build(),
							NoopHostnameVerifier.INSTANCE))
					.build();
		}
		final ClassLoader classLoader = ActivationAction.class.getClassLoader();
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

		return HttpClients.custom().setConnectionManager(connManager).setSSLSocketFactory(sslConnectionFactory).build();

	}

	@Async
	@Override
	public void ejecutaCronMensajesMq()
	{
		final CronJobModel cronJob = cronJobService.getCronJob(ACTIVACION_CRON_JOB);
		cronJobService.performCronJob(cronJob, false);
	}

}
