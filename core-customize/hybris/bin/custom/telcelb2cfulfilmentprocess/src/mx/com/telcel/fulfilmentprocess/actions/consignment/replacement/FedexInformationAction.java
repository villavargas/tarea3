/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.fulfilmentprocess.actions.consignment.replacement;

import com.google.gson.Gson;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.action.AbstractAction;
import de.hybris.platform.util.Config;
import mx.com.telcel.core.daos.fedex.TelcelFedexDao;
import mx.com.telcel.core.model.FedexGuidesModel;
import mx.com.telcel.core.model.LetterGiftModel;
import mx.com.telcel.core.util.TelcelUtil;
import mx.com.telcel.models.fedex.*;
import mx.com.telcel.models.fedex.request.FedexRequest;
import mx.com.telcel.models.fedex.request.HeaderRequest;
import mx.com.telcel.models.fedex.response.ErrorResponse;
import mx.com.telcel.models.fedex.response.FedexResponse;
import mx.com.telcel.services.FedexService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.annotation.Resource;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.*;


/**
 * The type Fedex information action.
 */
public class FedexInformationAction extends AbstractAction<ConsignmentProcessModel>
{

	private static final Logger LOG = Logger.getLogger(FedexInformationAction.class);
	private static final String IMAGE = "image";
	private static final String FEDEX = "Fedex-";
	private static final String EXTENSION = ".png";
	private static final String TELCEL_CONTENT_CATALOG = "telcelContentCatalog";
	private static final String TELCEL_PRODUCT_CATALOG = "telcelProductCatalog";
	private static final String VERSION = "Staged";
	private static final String MIME_TYPE = "image/png";
	private static final String ORDER_FEDEX_SEND_BY = "order.fedex.SendBy";
	private static final String ORDER_FEDEX_PACKAGES = "order.fedex.packages";
	private static final String ORDER_FEDEX_LANGUAGE_CODE = "order.fedex.language.code";
	private static final String ORDER_FEDEX_LOCALE_CODE = "order.fedex.locale.code";
	private static final String ORDER_FEDEX_ITEM_KEY = "order.fedex.item.key";
	private static final String ORDER_FEDEX_DESCRIPTION = "order.fedex.description";
	private static final String ORDER_FEDEX_CREATESHIPMENT_SERVICE_ORDEN_NAME = "order.fedex.createShipment.service.order.name";
	private static final String ORDER_FEDEX_CREATESHIPMENT_SERVICE_TYPE = "order.fedex.createShipment.service.type";
	private static final String ORDER_FEDEX_CREATESHIPMENT_SERVICE_ORDER_CHARACTERISTICS1_NAME = "order.fedex.createShipment.service.order.characteristics1.name";
	private static final String ORDER_FEDEX_CREATESHIPMENT_SERVICE_ORDER_CHARACTERISTICS2_NAME = "order.fedex.createShipment.service.order.characteristics2.name";
	private static final String ORDER_FEDEX_CREATESHIPMENT_SERVICE_ORDER_CHARACTERISTICS3_NAME = "order.fedex.createShipment.service.order.characteristics3.name";
	private static final String ORDER_FEDEX_CREATESHIPMENT_SERVICE_ORDER_ORDERITEM_CHARACTERISTICS_NAME1 = "order.fedex.createShipment.service.order.orderItem.characteristics.name1";
	private static final String ORDER_FEDEX_CREATESHIPMENT_SERVICE_ORDER_ORDERITEM_CHARACTERISTICS_NAME2 = "order.fedex.createShipment.service.order.orderItem.characteristics.name2";
	private static final String ORDER_FEDEX_CREATESHIPMENT_SERVICE_ORDER_ORDERITEM_CHARACTERISTICS_NAME3 = "order.fedex.createShipment.service.order.orderItem.characteristics.name3";
	private static final String ORDER_FEDEX_CREATESHIPMENT_SERVICE_ORDER_ORDERITEM_CHARACTERISTICS_NAME4 = "order.fedex.createShipment.service.order.orderItem.characteristics.name4";
	private static final String ORDER_FEDEX_CREATESHIPMENT_SERVICE_ORDER_ORDERITEM_CHARACTERISTICS_NAME5 = "order.fedex.createShipment.service.order.orderItem.characteristics.name5";
	private static final String ORDER_FEDEX_CREATESHIPMENT_SERVICE_ORDER_ORDERITEM_CHARACTERISTICS_NAME6 = "order.fedex.createShipment.service.order.orderItem.characteristics.name6";
	private static final String ORDER_FEDEX_CREATESHIPMENT_SERVICE_ACTWGT_VALUE = "order.fedex.createShipment.service.ACTWGT.value";
	private static final String ORDER_FEDEX_CREATESHIPMENT_SERVICE_ACTWGT_UNIT = "order.fedex.createShipment.service.ACTWGT.unit";
	private static final String ORDER_FEDEX_CREATESHIPMENT_SERVICE_CAD = "order.fedex.createShipment.service.CAD.value";
	private static final String ORDER_FEDEX_CREATESHIPMENT_SERVICE_LENGTH = "order.fedex.createShipment.service.length.value";
	private static final String ORDER_FEDEX_CREATESHIPMENT_SERVICE_WIDTH = "order.fedex.createShipment.service.width.value";
	private static final String ORDER_FEDEX_CREATESHIPMENT_SERVICE_HEIGHT = "order.fedex.createShipment.service.height.value";
	private static final String ORDER_FEDEX_CREATESHIPMENT_SERVICE_UNIT = "order.fedex.createShipment.service.unit.value";
	private static final String FEDEX_SERVICES_MESSAGEUUID_VALUE = "fedex.services.messageuuid.value";
	private static final String FEDEX_SERVICES_VERSION_VALUE = "fedex.services.version.value";
	private static final String FEDEX_SERVICES_ACCOUNT_NUMBER = "fedex.services.account.number";
	private static final String ORDER_FEDEX_CREATESHIPMENT_SERVICE_ORDER_ORDERITEM_CHARACTERISTICS_LABELFORMATTYPE = "order.fedex.createShipment.service.order.orderItem.characteristics.labelFormatType";
	private static final String ORDER_FEDEX_CREATESHIPMENT_SERVICE_ORDER_ORDERITEM_CHARACTERISTICS_IMAGETYPE = "order.fedex.createShipment.service.order.orderItem.characteristics.imageType";
	private static final String ORDER_FEDEX_CREATESHIPMENT_SERVICE_ORDER_ORDERITEM_CHARACTERISTICS_LABELSTOCKTYPE = "order.fedex.createShipment.service.order.orderItem.characteristics.labelStockType";
	private static final String FEDEX_SERVICES_SHIPMENT_SERVICETYPE_STANDARDOVERNIGHT = "fedex.services.shipment.servicetype.standardovernight";
	private static final String FEDEX_SERVICES_SHIPMENT_SERVICETYPE_EXPRESSSAVER="fedex.services.shipment.servicetype.expresssaver";
	private static final String FEDEX_SERVICES_SHIPTMENT_PAYMENTYPE = "fedex.services.shiptment.paymentype";
	private static final String ERROR_OVERNIGHT = "ESB_6548";
	private static final String ERROR_OVERNIGHT_DESCRIPTION = "STANDARD_OVERNIGHT is not supported for the destination.";
	private static final double FEDEX_WEIGHT_ONE = 1.0;
	private static final double FEDEX_WEIGHT_DOT_ONE = 0.01;

	private FedexService fedexService;

	@Resource
	private TelcelUtil telcelUtil; // NOSONAR

	@Resource
	private TelcelFedexDao telcelFedexDao;


	/**
	 * The enum Transition.
	 */
	public enum Transition
	{
		/**
		 * Ok transition.
		 */
		OK,
		WAIT;

		/**
		 * Gets string values.
		 *
		 * @return the string values
		 */
		public static Set<String> getStringValues()
		{
			final Set<String> res = new HashSet<String>();

			for (final Transition transition : Transition
					.values())
			{
				res.add(transition.toString());
			}
			return res;
		}
	}

	@Override
	public String execute(final ConsignmentProcessModel consignmentProcessModel) throws Exception
	{
		final FedexRequest request = new FedexRequest();
		final HeaderRequest headers = new HeaderRequest();
		final ConsignmentModel consignmentModel = consignmentProcessModel.getConsignment();
		if(consignmentModel.getFedexGuide() != null){
			final FedexGuidesModel fedexGuidesModelRemove = telcelFedexDao.findFedexGuideByConsignmentIdAndGuideCode(consignmentModel.getCode(), consignmentModel.getFedexGuide());
			modelService.remove(fedexGuidesModelRemove);
		}
		createRequestAndResponse(headers, request, consignmentModel, false);
		Object response = fedexService.createShipment(request, headers);
		if(response instanceof FedexResponse){
			return setResponseValues(consignmentProcessModel, request, headers, consignmentModel, response);
		} else {
			if(response instanceof ErrorResponse){
				final ErrorResponse errorResponse = (ErrorResponse) response;
				if(errorResponse.getErrorList().get(0).getError().getCode().equals(ERROR_OVERNIGHT)
						&& errorResponse.getErrorList().get(0).getError().getDescription().equals(ERROR_OVERNIGHT_DESCRIPTION)){
					createRequestAndResponse(headers, request, consignmentModel, true);
					Object response2 = fedexService.createShipment(request, headers);
					if (response2 instanceof FedexResponse)
					{
						return setResponseValues(consignmentProcessModel, request, headers, consignmentModel, response2);
					}
				}//condicion de los errores de overnight
			}//condición si lleva ErrorResponse
		}//else cuando no regresa FedexResponse
		return Transition.WAIT.toString();
	}

	private String setResponseValues(ConsignmentProcessModel consignmentProcessModel, FedexRequest request, HeaderRequest headers, ConsignmentModel consignmentModel, Object response) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException {
		final FedexResponse fedexResponse = (FedexResponse) response;
		final List<Characteristic> characteristics = fedexResponse.getService().getOrder().getOrderItem().get(0)
				.getCharacteristics();
		for (final Characteristic characteristic : characteristics)
		{
			if (characteristic.getName().equalsIgnoreCase(IMAGE))
			{
				final MediaModel image = telcelUtil.convertBase64ToImage(characteristic.getValue(),
						FEDEX + consignmentModel.getCode(), EXTENSION, TELCEL_PRODUCT_CATALOG, VERSION, MIME_TYPE);
				if (Objects.nonNull(image))
				{
					consignmentModel.setLabelFile(image);
				}
				break;
			}
		}
		consignmentProcessModel.setWaitingForReadyToShip(true);
		getModelService().save(consignmentProcessModel);
		consignmentModel.setStatus(ConsignmentStatus.LABELLED);
		consignmentModel.setFedexGuide(fedexResponse.getService().getOrder().getTrackingDetails().getTrackId());
		getModelService().save(consignmentModel);
		addFedexGuideToCronjob(consignmentModel.getOrder().getCode(), consignmentModel.getCode(), consignmentProcessModel.getCode(),
				consignmentModel.getFedexGuide());
		LOG.info("Process: " + consignmentProcessModel.getCode() + " in step OK - " + getClass());
		return Transition.OK.toString();
	}

	private void addFedexGuideToCronjob(final String orderModelCode, final String consignmentModelCode,
										final String processModelCode, final String fedexGuide)
	{
		final FedexGuidesModel fedexGuidesModel = getModelService().create(FedexGuidesModel.class);
		fedexGuidesModel.setOrderId(orderModelCode);
		fedexGuidesModel.setConsignmentId(consignmentModelCode);
		fedexGuidesModel.setConsignmentProccessId(processModelCode);
		fedexGuidesModel.setGuide(fedexGuide);
		getModelService().save(fedexGuidesModel);
	}

	private void createRequestAndResponse(final HeaderRequest headers, final FedexRequest request,
										  final ConsignmentModel consignment, final boolean flag)
	{
		LOG.info("create shipment service...");
		//SERVICE
		final TimePeriod validFor = new TimePeriod();
		validFor.setStartDateTime(String.valueOf(Instant.now()));
		final Quantity quantity = new Quantity();
		quantity.setUnit(Config.getParameter(ORDER_FEDEX_PACKAGES));
		quantity.setAmount("1");//numero total de paquetes
		final Characteristic c1 = new Characteristic();
		c1.setName(Config.getParameter(ORDER_FEDEX_CREATESHIPMENT_SERVICE_ORDER_CHARACTERISTICS1_NAME));
		if(flag){
			c1.setValue(Config.getParameter(FEDEX_SERVICES_SHIPMENT_SERVICETYPE_EXPRESSSAVER));//Identifica el servicio FedEx usado en el envío del paquete.
		} else {
			c1.setValue(Config.getParameter(FEDEX_SERVICES_SHIPMENT_SERVICETYPE_STANDARDOVERNIGHT));//Identifica el servicio FedEx usado en el envío del paquete.
		}
		final Characteristic c2 = new Characteristic();
		c2.setName(Config.getParameter(ORDER_FEDEX_CREATESHIPMENT_SERVICE_ORDER_CHARACTERISTICS2_NAME));
		c2.setValue("YOUR_PACKAGING");//Indica el empaquetado usado por el solicitador para el paquete
		final Characteristic c4 = new Characteristic();
		c4.setName(Config.getParameter(ORDER_FEDEX_CREATESHIPMENT_SERVICE_ORDER_CHARACTERISTICS3_NAME));
		c4.setValue(Config.getParameter(FEDEX_SERVICES_SHIPTMENT_PAYMENTYPE));//Indica el método de pago por un servicio.
		final List<Characteristic> characteristics = new ArrayList<Characteristic>();
		characteristics.add(c1);
		characteristics.add(c2);
		characteristics.add(c4);
		final OrderItem orderItem = new OrderItem();
		final Quantity weight = new Quantity();
		weight.setUnit(Config.getParameter(ORDER_FEDEX_CREATESHIPMENT_SERVICE_ACTWGT_UNIT));//Unidad de medida para el peso de los paquetes.
		//weight.setAmount(Config.getParameter(ORDER_FEDEX_CREATESHIPMENT_SERVICE_ACTWGT_VALUE));//Peso del paquete
		final List<Characteristic> characteristicsOI = new ArrayList<Characteristic>();
		final Characteristic c1OI = new Characteristic();
		c1OI.setName(Config.getParameter(ORDER_FEDEX_CREATESHIPMENT_SERVICE_ORDER_ORDERITEM_CHARACTERISTICS_NAME1));
		c1OI.setValue(Config.getParameter(ORDER_FEDEX_CREATESHIPMENT_SERVICE_LENGTH));//Longitud del paquete que será enviado.
		final Characteristic c2OI = new Characteristic();
		c2OI.setName(Config.getParameter(ORDER_FEDEX_CREATESHIPMENT_SERVICE_ORDER_ORDERITEM_CHARACTERISTICS_NAME2));
		c2OI.setValue(Config.getParameter(ORDER_FEDEX_CREATESHIPMENT_SERVICE_WIDTH));//Anchura del paquete que será enviado.
		final Characteristic c3OI = new Characteristic();
		c3OI.setName(Config.getParameter(ORDER_FEDEX_CREATESHIPMENT_SERVICE_ORDER_ORDERITEM_CHARACTERISTICS_NAME3));
		c3OI.setValue(Config.getParameter(ORDER_FEDEX_CREATESHIPMENT_SERVICE_HEIGHT));//Altura del paquete que será enviado.
		final Characteristic c4OI = new Characteristic();
		c4OI.setName(Config.getParameter(ORDER_FEDEX_CREATESHIPMENT_SERVICE_ORDER_ORDERITEM_CHARACTERISTICS_NAME4));
		c4OI.setValue(Config.getParameter(ORDER_FEDEX_CREATESHIPMENT_SERVICE_UNIT));//Unidad de medida de las dimensiones que especifican el paquete.
		final Characteristic c5OI = new Characteristic();
		c5OI.setName(Config.getParameter(ORDER_FEDEX_CREATESHIPMENT_SERVICE_ORDER_ORDERITEM_CHARACTERISTICS_NAME5));
		c5OI.setValue("NMP");
		final Characteristic c6OI = new Characteristic();
		characteristicsOI.add(c1OI);
		characteristicsOI.add(c2OI);
		characteristicsOI.add(c3OI);
		characteristicsOI.add(c4OI);
		characteristicsOI.add(c5OI);
		final Label label = new Label();
		final List<Characteristic> characteristicsLabel = new ArrayList<Characteristic>();
		final Characteristic c1Label = new Characteristic();
		c1Label.setName(Config.getParameter(ORDER_FEDEX_LANGUAGE_CODE));
		c1Label.setValue("es");
		final Characteristic c2Label = new Characteristic();
		c2Label.setName(Config.getParameter(ORDER_FEDEX_LOCALE_CODE));
		c2Label.setValue("MX");
		characteristicsLabel.add(c1Label);
		characteristicsLabel.add(c2Label);
		label.setLabelFormatType(Config.getParameter(ORDER_FEDEX_CREATESHIPMENT_SERVICE_ORDER_ORDERITEM_CHARACTERISTICS_LABELFORMATTYPE));//Especifica el tipo de etiqueta que será regresada
		label.setImageType(Config.getParameter(ORDER_FEDEX_CREATESHIPMENT_SERVICE_ORDER_ORDERITEM_CHARACTERISTICS_IMAGETYPE));
		label.setLabelStockType(Config.getParameter(ORDER_FEDEX_CREATESHIPMENT_SERVICE_ORDER_ORDERITEM_CHARACTERISTICS_LABELSTOCKTYPE));
		label.setLabelPrintingOrientation("BOTTOM_EDGE_OF_TEXT_FIRST");
		label.setCharacteristics(characteristicsLabel);
		orderItem.setItemSequence("1");
		orderItem.setItemGroup("1");
		//orderItem.setWeight(weight);
		orderItem.setCharacteristics(characteristicsOI);
		orderItem.setLabel(label);


		final List<OrderItem> orderItems = new ArrayList<OrderItem>();
		orderItems.add(orderItem);
		double precioCompleto = 0.0;
		int totalNoOfGoods = 0;
		double weightTotal = 0.0;
		for (final ConsignmentEntryModel entryModel : consignment.getConsignmentEntries())
		{
			final AbstractOrderEntryModel abstractOrderEntryModel = entryModel.getOrderEntry();
			final OrderItem orderItemCh = new OrderItem();
			final Quantity weightCh = new Quantity();
			weightCh.setUnit(Config.getParameter(ORDER_FEDEX_PACKAGES));
			weightCh.setAmount(String.valueOf(FEDEX_WEIGHT_ONE));
			weightTotal += FEDEX_WEIGHT_ONE;
			final List<Characteristic> characteristicsOICh = new ArrayList<Characteristic>();
			final Characteristic c1OICh = new Characteristic();
			c1OICh.setName(Config.getParameter(ORDER_FEDEX_ITEM_KEY));
			c1OICh.setValue(consignment.getSeriesImei().getClaveSAT());
			final Characteristic c2OICh = new Characteristic();
			c2OICh.setName(Config.getParameter(ORDER_FEDEX_DESCRIPTION));
			c2OICh.setValue(consignment.getSeriesImei().getDescriptionSAT());
			final Characteristic c3OICh = new Characteristic();
			c3OICh.setName("merchandiseValue");
			c3OICh.setValue(String.valueOf(abstractOrderEntryModel.getBasePrice()));
			precioCompleto += abstractOrderEntryModel.getBasePrice();
			final Characteristic c4OICh = new Characteristic();
			c4OICh.setName("merchandiseTotal");
			c4OICh.setValue(String.valueOf(entryModel.getQuantity()));
			characteristicsOICh.add(c1OICh);
			characteristicsOICh.add(c2OICh);
			characteristicsOICh.add(c3OICh);
			characteristicsOICh.add(c4OICh);
			orderItemCh.setItemGroup("items");
			orderItemCh.setWeight(weightCh);
			orderItemCh.setCharacteristics(characteristicsOICh);
			orderItems.add(orderItemCh);
			totalNoOfGoods ++;
			if(consignment.getAdditionalServiceEntry() != null) {
				final OrderItem orderItemCh2 = new OrderItem();
				final Quantity weightCh2 = new Quantity();
				weightCh2.setUnit(Config.getParameter(ORDER_FEDEX_PACKAGES));
				weightCh2.setAmount(String.valueOf(FEDEX_WEIGHT_DOT_ONE));
				weightTotal += FEDEX_WEIGHT_DOT_ONE;
				final List<Characteristic> characteristicsOICh2 = new ArrayList<Characteristic>();
				final Characteristic c1OICh2 = new Characteristic();
				c1OICh2.setName(Config.getParameter(ORDER_FEDEX_ITEM_KEY));
				c1OICh2.setValue(consignment.getSeriesICCID().getClaveSAT());
				final Characteristic c2OICh2 = new Characteristic();
				c2OICh2.setName(Config.getParameter(ORDER_FEDEX_DESCRIPTION));
				c2OICh2.setValue(consignment.getSeriesICCID().getDescriptionSAT());
				final Characteristic c3OICh2 = new Characteristic();
				c3OICh2.setName("merchandiseValue");
				c3OICh2.setValue(String.valueOf(consignment.getAdditionalServiceEntry().getBasePrice()));
				precioCompleto += consignment.getAdditionalServiceEntry().getBasePrice();
				final Characteristic c4OICh2 = new Characteristic();
				c4OICh2.setName("merchandiseTotal");
				c4OICh2.setValue(String.valueOf(entryModel.getQuantity()));
				characteristicsOICh2.add(c1OICh2);
				characteristicsOICh2.add(c2OICh2);
				characteristicsOICh2.add(c3OICh2);
				characteristicsOICh2.add(c4OICh2);
				orderItemCh2.setItemGroup("items");
				orderItemCh2.setWeight(weightCh2);
				orderItemCh2.setCharacteristics(characteristicsOICh2);
				orderItems.add(orderItemCh2);
				totalNoOfGoods ++;
			}//fin condición de servicios adicionales
		}


		// Carta porte
		for (final LetterGiftModel letterGift : consignment.getLetterGift())
		{
			final OrderItem orderItemCh = new OrderItem();
			final Quantity weightCh = new Quantity();
			weightCh.setUnit(Config.getParameter(ORDER_FEDEX_PACKAGES));
			weightCh.setAmount(String.valueOf(FEDEX_WEIGHT_ONE));
			weightTotal += FEDEX_WEIGHT_ONE;
			final List<Characteristic> characteristicsOICh = new ArrayList<Characteristic>();
			final Characteristic c1OICh = new Characteristic();
			c1OICh.setName(Config.getParameter(ORDER_FEDEX_ITEM_KEY));
			c1OICh.setValue(letterGift.getCve_producto());
			final Characteristic c2OICh = new Characteristic();
			c2OICh.setName(Config.getParameter(ORDER_FEDEX_DESCRIPTION));
			c2OICh.setValue(letterGift.getDescr());
			final Characteristic c3OICh = new Characteristic();
			c3OICh.setName("merchandiseValue");
			c3OICh.setValue(String.valueOf(letterGift.getPrice()));
			precioCompleto += Double.valueOf(letterGift.getPrice());
			final Characteristic c4OICh = new Characteristic();
			c4OICh.setName("merchandiseTotal");
			c4OICh.setValue("1");
			characteristicsOICh.add(c1OICh);
			characteristicsOICh.add(c2OICh);
			characteristicsOICh.add(c3OICh);
			characteristicsOICh.add(c4OICh);
			orderItemCh.setItemGroup("items");
			orderItemCh.setWeight(weightCh);
			orderItemCh.setCharacteristics(characteristicsOICh);
			orderItems.add(orderItemCh);
			totalNoOfGoods ++;
		}

		final Characteristic c3 = new Characteristic();
		c3.setName("totalNoOfGoods");
		c3.setValue(String.valueOf(totalNoOfGoods));
		characteristics.add(c3);

		c6OI.setName(Config.getParameter(ORDER_FEDEX_CREATESHIPMENT_SERVICE_ORDER_ORDERITEM_CHARACTERISTICS_NAME6));
		c6OI.setValue(String.valueOf(precioCompleto));
		characteristicsOI.add(c6OI);

		weight.setAmount(String.valueOf(weightTotal));//Peso del paquete
		orderItem.setWeight(weight);

		final Order order = new Order();
		order.setType(Config.getParameter(ORDER_FEDEX_CREATESHIPMENT_SERVICE_ORDEN_NAME));
		order.setName(Config.getParameter(ORDER_FEDEX_CREATESHIPMENT_SERVICE_ORDEN_NAME));
		order.setValidFor(validFor);
		order.setQuantity(quantity);
		order.setCharacteristics(characteristics);
		order.setOrderItem(orderItems);

		final Service service = new Service();
		service.setType(Config.getParameter(ORDER_FEDEX_CREATESHIPMENT_SERVICE_TYPE));
		service.setOrder(order);

		//PARTY
		//Party 1
		final Organization organization1 = new Organization();
		organization1.setOrganizationName("");
		organization1.setTradingName(consignment.getWarehouse().getCode());
		final Role role1 = new Role();
		role1.setName("Shipper");
		final Address address1 = new Address();
		final String centro = consignment.getWarehouse().getCentro();
		final String region = StringUtils.isNotEmpty(centro) ? centro.substring(centro.length() - 1) : StringUtils.EMPTY;
		address1.setLocality(region);
		address1.setIsResidential("true");
		final MediumCharacteristic mediumCharacteristic1 = new MediumCharacteristic();
		String numberContact = "0";
		if(consignment.getWarehouse().getNumberContact() != null){
			numberContact = consignment.getWarehouse().getNumberContact();
		}
		mediumCharacteristic1.setPhoneNumber(numberContact);
		final ContactMedium contactMedium1 = new ContactMedium();
		contactMedium1.setContactMediumCharacteristic(mediumCharacteristic1);
		final List<ContactMedium> contactsMediums1 = new ArrayList<ContactMedium>();
		contactsMediums1.add(contactMedium1);
		final Account account1 = new Account();
		account1.setAccountNumber(Config.getParameter(FEDEX_SERVICES_ACCOUNT_NUMBER));
		final List<Account> accounts1 = new ArrayList<Account>();
		accounts1.add(account1);
		final Party party1 = new Party();
		party1.setType("legalEntity");
		party1.setId(consignment.getWarehouse().getIsocode());
		party1.setIsLegalEntity("true");
		party1.setOrganization(organization1);
		party1.setRole(role1);
		party1.setAddress(address1);
		party1.setContactMedium(contactsMediums1);
		party1.setAccount(accounts1);
		//Party 2
		final Individual individual2 = new Individual();
		individual2.setName(consignment.getOrder().getDeliveryAddress().getFirstname() + " "
				+ consignment.getOrder().getDeliveryAddress().getLastname());
		individual2.setLastName(consignment.getOrder().getDeliveryAddress().getLastname());
		individual2.setSecondLastName(consignment.getOrder().getDeliveryAddress().getMiddlename());
		//se validan numeros para colocar prefijo # e INT
		String numExt = "";
		String numInt = "";
		if(consignment.getOrder().getDeliveryAddress().getExternalNumber() != null){
			numExt = "# "+consignment.getOrder().getDeliveryAddress().getExternalNumber();
		}
		if(consignment.getOrder().getDeliveryAddress().getInteriorNumber() != null){
			numInt = "INT "+consignment.getOrder().getDeliveryAddress().getInteriorNumber()+" ";
		}
		final Role role2 = new Role();
		role2.setName("Recipient");
		final Characteristic cp2 = new Characteristic();
		cp2.setName("companyName");
		cp2.setValue(consignment.getOrder().getDeliveryAddress().getStreetname());
		final Characteristic cp22 = new Characteristic();
		cp22.setName("RFC");
		if (consignment.getOrder().getBillingAddress() != null
				&& !StringUtils.isEmpty(consignment.getOrder().getBillingAddress().getRfc()))
		{
			cp22.setValue(consignment.getOrder().getBillingAddress().getRfc());
		}
		else
		{
			cp22.setValue("XAXX010101000");//XAXX010101000
		}
		final List<Characteristic> characteristicsp2 = new ArrayList<Characteristic>();
		characteristicsp2.add(cp2);
		characteristicsp2.add(cp22);
		final Address address2 = new Address();
		address2.setZipCode(consignment.getOrder().getDeliveryAddress().getPostalcode());
		address2.setCity(consignment.getOrder().getDeliveryAddress().getTown());
		address2.setStateOrProvince(consignment.getOrder().getDeliveryAddress().getRegion().getIsocode().substring(3));
		address2.setCountry(consignment.getOrder().getDeliveryAddress().getCountry().getIsocode());
		address2.setAddressLine1(numExt+" "+numInt + consignment.getOrder().getDeliveryAddress().getDistrict());
		address2.setIsResidential("true");
		final MediumCharacteristic mediumCharacteristic2 = new MediumCharacteristic();
		mediumCharacteristic2.setPhoneNumber(consignment.getOrder().getDeliveryAddress().getPhone1());
		final ContactMedium contactMedium2 = new ContactMedium();
		contactMedium2.setContactMediumCharacteristic(mediumCharacteristic2);
		final List<ContactMedium> contactsMediums2 = new ArrayList<ContactMedium>();
		contactsMediums2.add(contactMedium2);
		final Account account2 = new Account();
		account2.setAccountNumber(Config.getParameter(FEDEX_SERVICES_ACCOUNT_NUMBER));
		final List<Account> accounts2 = new ArrayList<Account>();
		accounts2.add(account1);
		final Party party2 = new Party();
		party2.setType("individual");
		party2.setIsLegalEntity("false");
		party2.setIndividual(individual2);
		party2.setRole(role2);
		party2.setCharacteristics(characteristicsp2);
		party2.setAddress(address2);
		party2.setContactMedium(contactsMediums2);
		party2.setAccount(accounts2);
		//Party 3
		final Organization organization3 = new Organization();
		organization3.setOrganizationName("");
		organization3.setTradingName(consignment.getWarehouse().getCode());
		final Role role3 = new Role();
		role3.setName("Payor");
		final Address address3 = new Address();
		address3.setLocality(region);
		address3.setIsResidential("true");
		final MediumCharacteristic mediumCharacteristic3 = new MediumCharacteristic();
		mediumCharacteristic3.setPhoneNumber(numberContact);
		final ContactMedium contactMedium3 = new ContactMedium();
		contactMedium3.setContactMediumCharacteristic(mediumCharacteristic3);
		final List<ContactMedium> contactsMediums3 = new ArrayList<ContactMedium>();
		contactsMediums3.add(contactMedium3);
		final Account account3 = new Account();
		account3.setAccountNumber(Config.getParameter(FEDEX_SERVICES_ACCOUNT_NUMBER));
		final List<Account> accounts3 = new ArrayList<Account>();
		accounts3.add(account3);
		final Party party3 = new Party();
		party3.setType("legalEntity");

		party3.setId(consignment.getWarehouse().getIsocode());
		party3.setIsLegalEntity("true");
		party3.setOrganization(organization3);
		party3.setRole(role3);
		party3.setAddress(address3);
		party3.setContactMedium(contactsMediums3);
		party3.setAccount(accounts3);
		//Party 4
		final Role role4 = new Role();
		role4.setName("CustomerReferences");
		final Characteristic cp41 = new Characteristic();
		cp41.setName("CUSTOMER_REFERENCE");
		cp41.setValue(
				consignment.getOrder().getRegionCode() + "|" + consignment.getOrder().getCode() + "|" + consignment.getCode());
		final Characteristic cp42 = new Characteristic();
		cp42.setName("INVOICE_NUMBER");
		cp42.setValue("IV123");
		final Characteristic cp43 = new Characteristic();
		cp43.setName("P_O_NUMBER");
		cp43.setValue("P_O_NUMBER");
		final List<Characteristic> characteristicsp4 = new ArrayList<Characteristic>();
		characteristicsp4.add(cp41);
		characteristicsp4.add(cp42);
		characteristicsp4.add(cp43);
		final Party party4 = new Party();
		party4.setRole(role4);
		party4.setCharacteristics(characteristicsp4);

		final List<Party> parties = new ArrayList<Party>();
		parties.add(party1);
		parties.add(party2);
		parties.add(party3);
		parties.add(party4);

		//Request
		request.setService(service);
		request.setParty(parties);

		headers.setMessageUUID(System.currentTimeMillis() + "fedex");//Obligatorio
		headers.setRequestDate(String.valueOf(Instant.now()));//Obligatorio
		headers.setSendBy(Config.getParameter(ORDER_FEDEX_SEND_BY));//Obligatorio
		headers.setVersion(Config.getParameter(FEDEX_SERVICES_VERSION_VALUE));//Obligatorio
		LOG.info("FEDEX HEADER : " + new Gson().toJson(headers));
	}

	@Override
	public Set<String> getTransitions()
	{
		return Transition.getStringValues();
	}

	/**
	 * Gets fedex service.
	 *
	 * @return the fedex service
	 */
	public FedexService getFedexService()
	{
		return fedexService;
	}

	/**
	 * Sets fedex service.
	 *
	 * @param fedexService
	 *           the fedex service
	 */
	public void setFedexService(final FedexService fedexService)
	{
		this.fedexService = fedexService;
	}
}