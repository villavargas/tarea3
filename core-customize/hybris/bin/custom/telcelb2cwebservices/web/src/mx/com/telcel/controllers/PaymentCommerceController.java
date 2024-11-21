package mx.com.telcel.controllers;

import static mx.com.telcel.constants.Telcelb2cwebservicesConstants.CLIENT_CREDENTIAL_AUTHORIZATION_NAME;
import static mx.com.telcel.constants.Telcelb2cwebservicesConstants.PASSWORD_AUTHORIZATION_NAME;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.core.enums.PaymentStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Resource;

import mx.com.telcel.core.daos.cvtdat.UserInfoCVTDao;
import mx.com.telcel.core.daos.cvtdat.UserInfoDATDao;
import mx.com.telcel.core.model.TelcelPoVariantModel;
import mx.com.telcel.core.model.UserInfoCVTModel;
import mx.com.telcel.core.model.UserInfoDATModel;
import mx.com.telcel.core.services.TelcelCodigosPostalesService;
import mx.com.telcel.core.util.TelcelUtil;
import mx.com.telcel.facades.zipcodes.data.CodigosPostalesTelcelData;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.assertj.core.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import mx.com.telcel.core.models.paymentcommerce.AuthRequest;
import mx.com.telcel.core.models.paymentcommerce.AuthResponse;
import mx.com.telcel.core.models.paymentcommerce.CardConfirmRequest;
import mx.com.telcel.core.models.paymentcommerce.CardConfirmResponse;
import mx.com.telcel.core.models.paymentcommerce.CardValidateRequest;
import mx.com.telcel.core.models.paymentcommerce.CardValidateResponse;
import mx.com.telcel.core.models.paymentcommerce.Item;
import mx.com.telcel.core.models.paymentcommerce.Order;
import mx.com.telcel.core.models.paymentcommerce.Payment;
import mx.com.telcel.core.models.paymentcommerce.PaymentCommerceMessage;
import mx.com.telcel.core.models.paymentcommerce.PaymentConfirmRequest;
import mx.com.telcel.core.models.paymentcommerce.PaymentValidateRequest;
import mx.com.telcel.core.models.paymentcommerce.PromoRequest;
import mx.com.telcel.core.models.paymentcommerce.PromoResponse;
import mx.com.telcel.core.models.paymentcommerce.ReferenceConfirmRequest;
import mx.com.telcel.core.models.paymentcommerce.ReferenceConfirmResponse;
import mx.com.telcel.core.models.paymentcommerce.ReferenceValidateRequest;
import mx.com.telcel.core.models.paymentcommerce.ReferenceValidateResponse;
import mx.com.telcel.core.models.paymentcommerce.SpeiConfirmRequest;
import mx.com.telcel.core.models.paymentcommerce.SpeiConfirmResponse;
import mx.com.telcel.core.models.paymentcommerce.SpeiValidateRequest;
import mx.com.telcel.core.models.paymentcommerce.SpeiValidateResponse;
import mx.com.telcel.core.models.paymentcommerce.ValidateTokenResponse;
import mx.com.telcel.core.services.paymentcommerce.PaymentCommerceService;
import mx.com.telcel.facades.paymentcommerce.data.ItemPCData;
import mx.com.telcel.facades.paymentcommerce.data.PaymentCommerceInfoData;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@Controller
@RequestMapping(value = "/paymentcommerce")
public class PaymentCommerceController
{

	@Resource(name = "paymentCommerceService")
	private PaymentCommerceService paymentCommerceService;

	@Resource(name = "baseStoreService")
	private BaseStoreService baseStoreService;

	@Resource(name = "customerAccountService")
	private CustomerAccountService customerAccountService;

	@Resource(name = "paymentCommerceInfoConverter")
	private Converter<OrderModel, PaymentCommerceInfoData> paymentCommerceInfoConverter;

	@Resource(name = "paymentCommerceInfoCartConverter")
	private Converter<CartModel, PaymentCommerceInfoData> paymentCommerceInfoCartConverter;

	@Resource(name = "defaultConfigurationService")
	private ConfigurationService configurationService;

	@Resource(name = "commerceCartService")
	private CommerceCartService commerceCartService;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "productVariantFacade")
	private ProductFacade productFacade;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource
	private TelcelUtil telcelUtil; // NOSONAR

	@Resource(name = "telcelCodigosPostalesService")
	private TelcelCodigosPostalesService telcelCodigosPostalesService;

	@Resource(name = "userInfoDATDao")
	private UserInfoDATDao userInfoDATDao;

	@Resource(name = "userInfoCVTDao")
	private UserInfoCVTDao userInfoCVTDao;

	private static final Logger LOG = Logger.getLogger(PaymentCommerceController.class);
	private static final String CATALOG_ID = "telcelProductCatalog";
	private static final String VERSION_ONLINE = "Online";
	private static final String TELCEL_SALES_FORCE_R = "telcel.sales.force.r";
	private static final String TELCEL_SALES_FORCE_ALPHANUMERIC_R = "telcel.sales.force.alphanumeric.r";

	private static final String PAYMENT_TYPE_CARD = "CARD";
	private static final String PAYMENT_TYPE_SPEI = "SPEI";
	private static final String PAYMENT_TYPE_REFERENCE = "REFERENCE";
	public static final String TELCEL = "telcel";
	private static final String AUTH_USER = "telcel.payment.commerce.auth.user";
	private static final String AUTH_PASSWORD = "telcel.payment.commerce.auth.password";
	private static final String AUTH_ACCOUNT = "telcel.payment.commerce.auth.account";
	private static final String AUTH_CHANNEL = "telcel.payment.commerce.auth.channel";
	private static final String SMARTPHONE = "telefonos-y-smartphones";

	@Resource(name = "sessionService")
	private SessionService sessionService;

	/**
	 * Service execute payment<br/>
	 * Example :</br>
	 * POST https://localhost:9002/telcelb2cwebservices/paymentcommerce/executepaymentvalidate</br>
	 *
	 * Method : POST</br>
	 *
	 * @param request
	 *           PaymentValidateRequest</br>
	 * @return - PaymentCommerceMessage object</br>
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 * @throws IOException
	 *
	 */
	@ResponseBody
	@PostMapping(value = "/executepaymentvalidate", produces = "application/json")
	@ApiOperation(value = "Execute Payment Validate", notes = "Execute Payment Validate Process", produces = "application/json", authorizations =
	{ @Authorization(value = CLIENT_CREDENTIAL_AUTHORIZATION_NAME), @Authorization(value = PASSWORD_AUTHORIZATION_NAME) })
	public PaymentCommerceMessage executepaymentvalidate(@ApiParam(value = "Payment Validate Request", required = true)
	@RequestBody(required = true)
	final PaymentValidateRequest request)
	{
		LOG.info("Execute Payment Validate");
		final PaymentCommerceMessage message = new PaymentCommerceMessage();
		try
		{
			final CartModel cart = getCartForUserByCartId(request.getCartId(), request.getUserId());
			if (Objects.isNull(cart))
			{
				throw new Exception("No se encontro el carrito con el codigo : " + request.getCartId());
			}
			LOG.info("CART CODE: " + cart.getCode());
			final PaymentCommerceInfoData pci = paymentCommerceInfoCartConverter.convert(cart);
//			final ValidateTokenResponse tokenResponse = getToken();
			if(cart.getUser() instanceof CustomerModel){
				CustomerModel customerModel = (CustomerModel)cart.getUser();
				final String emailUid = CustomerType.GUEST.equals(customerModel.getType()) ? StringUtils.substringAfter(
						customerModel.getUid(), "|") : telcelUtil.getEmailForToken(customerModel.getContactEmail(),cart.getAddressHolderLine().getEmail());
				final ValidateTokenResponse tokenResponse = paymentCommerceService.getTokenByUid(emailUid);

				message.setToken(tokenResponse.getToken());
				switch (pci.getPayment().getPaymentType())
				{
					case PAYMENT_TYPE_CARD:
						LOG.info("CARD PAYMENT");
						final CardValidateResponse cardValidate = executePaymentCard(request.getCartId(), request.getUserId(),
								tokenResponse.getToken());
						if (cardValidate.getStatusCode() != HttpStatus.OK.value())
						{
							message.setStatus(false);
							message.setMessage("No se ejecuto el servicio correctamente");
						}
						else
						{
							message.setStatus(true);
							message.setMessage("Se ejecuto el servicio correctamente");
						}
						message.setCardValidateResponse(cardValidate);
						break;
					case PAYMENT_TYPE_SPEI:
						LOG.info("SPEI PAYMENT");
						final SpeiValidateResponse speiValidate = executePaymentSpei(request.getCartId(), request.getUserId(),
								tokenResponse.getToken());
						if (speiValidate.getStatusCode() != HttpStatus.OK.value())
						{
							message.setStatus(false);
							message.setMessage("No se ejecuto el servicio correctamente");
						}
						else
						{
							message.setStatus(true);
							message.setMessage("Se ejecuto el servicio correctamente");
						}
						message.setSpeiValidateResponse(speiValidate);
						break;
					case PAYMENT_TYPE_REFERENCE:
						LOG.info("REFERENCE PAYMENT");
						final ReferenceValidateResponse referenceValidate = executePaymentReference(request.getCartId(),
								request.getUserId(), tokenResponse.getToken());
						if (referenceValidate.getStatusCode() != HttpStatus.OK.value())
						{
							message.setStatus(false);
							message.setMessage("No se ejecuto el servicio correctamente");
						}
						else
						{
							message.setStatus(true);
							message.setMessage("Se ejecuto el servicio correctamente");
						}
						message.setReferenceValidateResponse(referenceValidate);
						break;
					default:
						message.setStatus(false);
						message.setMessage("Debe proporcionar un metodo de pago");
						break;
				}
			}

		}
		catch (final Exception e)
		{
			LOG.info("Error Payment Commerce : " + e.getMessage());
			message.setStatus(false);
			message.setMessage("Ocurrio un error en los servicios de Payment Commerce");
		}
		return message;
	}

	/**
	 * Service execute payment confirm<br/>
	 * Example :</br>
	 * POST https://localhost:9002/telcelb2cwebservices/paymentcommerce/executepaymentconfirm</br>
	 *
	 * Method : POST</br>
	 *
	 * @param request
	 *           Object PaymentConfirmRequest</br>
	 * @return - PaymentCommerceMessage object</br>
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 * @throws IOException
	 *
	 */

	@ResponseBody
	@PostMapping(value = "/executepaymentconfirm", produces = "application/json")
	@ApiOperation(value = "Execute Payment Confirm", notes = "Execute Payment Confirm Process", produces = "application/json", authorizations =
	{ @Authorization(value = CLIENT_CREDENTIAL_AUTHORIZATION_NAME), @Authorization(value = PASSWORD_AUTHORIZATION_NAME) })
	public PaymentCommerceMessage executepaymentconfirm(@ApiParam(value = "Payment Confirm Request", required = true)
	@RequestBody(required = true)
	final PaymentConfirmRequest request)
	{
		LOG.info("Execute Payment Confirm");
		final PaymentCommerceMessage message = new PaymentCommerceMessage();
		try
		{
			final OrderModel order = getOrder(request.getOrderId());
			LOG.info("ORDER CODE: " + order.getCode());
			final PaymentCommerceInfoData pci = paymentCommerceInfoConverter.convert(order);
			switch (pci.getPayment().getPaymentType())
			{
				case PAYMENT_TYPE_CARD:
					LOG.info("CARD PAYMENT");
					final CardConfirmRequest ccrequest = new CardConfirmRequest();
					ccrequest.setOrderId(request.getOrderIdPC());
					ccrequest.setCvv("111");
					ccrequest.setFingerprint(request.getFingerprint());
					final CardConfirmResponse ccresponse = paymentCommerceService.cardConfirm(ccrequest, request.getToken());
					if (ccresponse.getStatusCode() != HttpStatus.OK.value())
					{
						message.setStatus(false);
						message.setMessage("No se ejecuto el servicio correctamente");
					}
					else
					{
						updateOrderPaymentStatus(order, "SUCCESS", "GREEN");
						message.setStatus(true);
						message.setMessage("Se ejecuto el servicio correctamente");
					}
					message.setCardConfirmResponse(ccresponse);
					break;
				case PAYMENT_TYPE_SPEI:
					LOG.info("SPEI PAYMENT");
					final SpeiConfirmRequest screquest = new SpeiConfirmRequest();
					screquest.setOrderId(request.getOrderIdPC());
					final SpeiConfirmResponse scresponse = paymentCommerceService.speiConfirm(screquest, request.getToken());
					if (scresponse.getStatusCode() != HttpStatus.OK.value())
					{
						message.setStatus(false);
						message.setMessage("No se ejecuto el servicio correctamente");
					}
					else
					{
						message.setStatus(true);
						message.setMessage("Se ejecuto el servicio correctamente");
					}
					message.setSpeiConfirmResponse(scresponse);
					break;
				case PAYMENT_TYPE_REFERENCE:
					LOG.info("REFERENCE PAYMENT");
					final ReferenceConfirmRequest rcrequest = new ReferenceConfirmRequest();
					rcrequest.setOrderId(request.getOrderIdPC());
					final ReferenceConfirmResponse rcresponse = paymentCommerceService.referenceConfirm(rcrequest, request.getToken());
					if (rcresponse.getStatusCode() != HttpStatus.OK.value())
					{
						message.setStatus(false);
						message.setMessage("No se ejecuto el servicio correctamente");
					}
					else
					{
						message.setStatus(true);
						message.setMessage("Se ejecuto el servicio correctamente");
					}
					message.setReferenceConfirmResponse(rcresponse);
					break;
				default:
					message.setStatus(false);
					message.setMessage("Debe proporcionar un metodo de pago");
					break;
			}
		}
		catch (final Exception e)
		{
			LOG.info("Error Payment Commerce : " + e.getMessage());
			message.setStatus(false);
			message.setMessage("Ocurrio un error en los servicios de Payment Commerce");
		}
		return message;
	}

	/**
	 * Service cart promotions<br/>
	 * Example :</br>
	 * POST https://localhost:9002/telcelb2cwebservices/paymentcommerce/promotions</br>
	 *
	 * Method : POST</br>
	 *
	 * @param tokenCard
	 *           String</br>
	 * @param cartId
	 *           String</br>
	 * @param userId
	 *           String</br>
	 * @return - String object</br>
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 * @throws IOException
	 *
	 */

	@ResponseBody
	@PostMapping(value = "/promotions", produces = "application/json")
	@ApiOperation(value = "Card Promotions", produces = "application/json", authorizations =
	{ @Authorization(value = CLIENT_CREDENTIAL_AUTHORIZATION_NAME), @Authorization(value = PASSWORD_AUTHORIZATION_NAME) })
	public PromoResponse promotions(@ApiParam(value = "Token Card", required = true)
	@RequestParam(required = true)
	final String tokenCard, @ApiParam(value = "Cart Id", required = true)
	@RequestParam(required = true)
	final String cartId, @ApiParam(value = "User Id", required = true)
	@RequestParam(required = true)
	final String userId)
	{
		LOG.info("Get Card Promotions");
		try
		{
			final CartModel cart = getCartForUserByCartId(cartId, userId);
			if(cart.getUser() instanceof CustomerModel){
				CustomerModel customerModel = (CustomerModel) cart.getUser();
				final String emailUid = CustomerType.GUEST.equals(customerModel.getType()) ? StringUtils.substringAfter(
						customerModel.getUid(), "|") : telcelUtil.getEmailForToken(customerModel.getContactEmail(),cart.getAddressHolderLine().getEmail());
				final ValidateTokenResponse tokenResponse = paymentCommerceService.getTokenByUid(emailUid);
				final PromoRequest request = new PromoRequest();
				request.setTotalAmount(cart.getTotalPrice());
				request.setCardBin(tokenCard);
				if (Objects.isNull(cart.getEntries()))
				{
					final PromoResponse response = new PromoResponse();
					response.setStatusCode(HttpStatus.BAD_REQUEST.value());
					return response;
				}
				catalogVersionService.setSessionCatalogVersion(CATALOG_ID, VERSION_ONLINE);
				final List<Item> items = new ArrayList<>();
				for (final AbstractOrderEntryModel entry : cart.getEntries())
				{
					final ProductData data = productFacade.getProductForCodeAndOptions(entry.getProduct().getCode(), null);
//					if(entry.getProduct() instanceof TelcelPoVariantModel){
//						TelcelPoVariantModel telcelPoVariantModel = (TelcelPoVariantModel)entry.getProduct();
//						Optional<CategoryModel> smartphone = telcelPoVariantModel.getSupercategories()
//								.stream().filter(categoryModel -> categoryModel.getCode().equals(SMARTPHONE)).findFirst();
//						if (smartphone.isPresent()) {
							final Item item = new Item();
							item.setSku(entry.getProduct().getSku());
							item.setPrice(entry.getTotalPrice());
							final String desc = (data.getBaseProduct() == null ? "" : data.getBaseProduct().replaceAll("_", " ")) + " "
									+ (data.getColorData() == null ? "" : data.getColorData().getCode()) + " "
									+ (data.getCapacidad() == null ? "" : data.getCapacidad().getValue() + " " + data.getCapacidad().getUnit());
							item.setDescription(desc);
							items.add(item);
//						}
//					}
				}
				request.setItems(items);
				return paymentCommerceService.promotions(request, tokenResponse.getToken());
			}

		}
		catch (final Exception e)
		{
			LOG.info("ERROR PROMOTIONS : " + e.getMessage());
			final PromoResponse response = new PromoResponse();
			response.setStatusCode(HttpStatus.BAD_REQUEST.value());
			return response;
		}
		return null;
	}

	private String getIdUserCVTDAT()
	{
		String idUserCVTDAT = sessionService.getAttribute("idUserCVTDAT");
		if (Strings.isNullOrEmpty(idUserCVTDAT))
		{
			final RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
			idUserCVTDAT = Objects.nonNull(requestAttributes)
					? telcelUtil.getCookieByRequest(((ServletRequestAttributes) requestAttributes).getRequest(), "idUserCVTDAT")
					: StringUtils.EMPTY;
			idUserCVTDAT = idUserCVTDAT == null ? "" : idUserCVTDAT;
			LOG.info(String.format("Debug - cookie idUserCVTDAT: %s", idUserCVTDAT));
		}
		else
		{
			LOG.info("Se obtiene el valor de typeUserCVTDAT desde sessionService");
		}
		return idUserCVTDAT;
	}

	private CardValidateResponse executePaymentCard(final String cartId, final String userId, final String token) throws Exception
	{
		final CartModel cart = getCartForUserByCartId(cartId, userId);
		CardValidateResponse response = null;
		if (Objects.isNull(cart))
		{
			throw new Exception("No se encontro el carrito con el codigo : " + cartId);
		}
		LOG.info("CART CODE: " + cart.getCode());
		final PaymentCommerceInfoData pci = paymentCommerceInfoCartConverter.convert(cart);
		AddressModel deliveryAddress = cart.getDeliveryAddress();
		final CardValidateRequest request = new CardValidateRequest();
		final Order orderPC = new Order();
		final String postalCode = deliveryAddress.getPostalcode();
		final CodigosPostalesTelcelData codigosPostalesTelcelData = telcelCodigosPostalesService.getInfForZipcode(postalCode);
		if(deliveryAddress != null) {
			if (!Objects.isNull(codigosPostalesTelcelData) || !Objects.isNull(codigosPostalesTelcelData.getRegion())) {
				orderPC.setRegion(Integer.valueOf(codigosPostalesTelcelData.getRegion().getCode()));
			}
			orderPC.setCountry("México");
			orderPC.setCity(StringUtils.isNotEmpty(deliveryAddress.getTown()) ? deliveryAddress.getTown() : "NA");
			orderPC.setState(Objects.nonNull(deliveryAddress.getRegion()) ? deliveryAddress.getRegion().getName() : "NA");
			orderPC.setAddress(deliveryAddress.getStreetname() + " " + deliveryAddress.getExternalNumber());
			orderPC.setPostalCode(StringUtils.isNotEmpty(deliveryAddress.getPostalcode()) ? deliveryAddress.getPostalcode() : "NA");

		}
		orderPC.setEmail(pci.getOrder().getEmail());
		orderPC.setMdiKey("VENTA_EQUIPO");

		String salesForceId= StringUtils.EMPTY;
		String salesForceAlphanumeric= StringUtils.EMPTY;
		String typeUserCVTDAT= StringUtils.EMPTY;
		String idUserCVTDAT= StringUtils.EMPTY;
		if (!Objects.isNull(codigosPostalesTelcelData) || !Objects.isNull(codigosPostalesTelcelData.getRegion()))
		{
			String region = codigosPostalesTelcelData.getRegion().getCode();
			salesForceId = configurationService.getConfiguration().getString(TELCEL_SALES_FORCE_R + region);
			salesForceAlphanumeric = configurationService.getConfiguration().getString(TELCEL_SALES_FORCE_ALPHANUMERIC_R + region);



			typeUserCVTDAT = getTypeUserCVTDAT();
			idUserCVTDAT = getIdUserCVTDAT();


			//DAT ACTIVACIONES
			if (typeUserCVTDAT.equals("DAT"))
			{
				final UserInfoDATModel dat = userInfoDATDao.getUserInfoDATModel(idUserCVTDAT);

//                final UserInfoDATModel dat = cart.getUserinfodat();
				LOG.info("ACTIVACIONES DAT");
				LOG.info("ID FUERZA VENTA ACTIVA : " + dat.getIdFuerzaVentaActiva());
				LOG.info("FUERZA VENTA ACTIVA : " + dat.getFuerzaVentaActiva());
				salesForceId = dat.getIdFuerzaVentaActiva();
				salesForceAlphanumeric = dat.getFuerzaVentaActiva();
			}


			//CVT ACTIVACIONES
			if (typeUserCVTDAT.equals("CVT"))
			{
				final UserInfoCVTModel cvt = userInfoCVTDao.getUserInfoCVTModel(idUserCVTDAT);
				LOG.info("ACTIVACIONES CVT");
				LOG.info("REGION : " + region);
				if (region.trim().equals("9"))
				{
					LOG.info("fvPrepagoPadre : " + cvt.getFvPrepagoPadre());
					LOG.info("fvPospagoPadre. : " + cvt.getFvPospagoPadre());
					salesForceId = cvt.getFvPrepagoPadre();
					salesForceAlphanumeric = cvt.getFvPospagoPadre();
				}
				else
				{
					LOG.info("fvPrepagoPersonal : " + cvt.getFvPrepagoPersonal());
					LOG.info("fvPospagoPersonal. : " + cvt.getFvPospagoPersonal());
					salesForceId = cvt.getFvPrepagoPersonal();
					salesForceAlphanumeric = cvt.getFvPospagoPersonal();
				}
			}

		}

		if(StringUtils.isNotEmpty(typeUserCVTDAT)){
			orderPC.setDistributionCenter(typeUserCVTDAT);
			orderPC.setSalesForce(salesForceAlphanumeric);
		}else{
			orderPC.setDistributionCenter("PREPAGO");
		}


		orderPC.setPhone(StringUtils.isNotEmpty(pci.getOrder().getPhone()) ? pci.getOrder().getPhone() : deliveryAddress.getPhone1());
		orderPC.setFirstname(pci.getPayment().getFirstname());
		orderPC.setLastname(pci.getPayment().getLastname());
		orderPC.setTokenCard(pci.getPayment().getTokenCard());
		orderPC.setLastDigits(pci.getPayment().getLastDigits());

		final List<Payment> payments = new ArrayList<Payment>();
		final Payment payment = new Payment();
		payment.setAmount(pci.getPayment().getAmount());
		payment.setMsi(pci.getPayment().getMsi());
		final List<Item> items = new ArrayList<Item>();
		for (final ItemPCData itempc : pci.getPayment().getItems())
		{
			final Item item = new Item();
			item.setSku(itempc.getSku());
			item.setDescription(itempc.getDescription());
			item.setPrice(itempc.getPrice());
			item.setModel(itempc.getModel());
			item.setColor(StringUtils.isNotEmpty(itempc.getColor()) ? itempc.getColor() : "NA");
			items.add(item);
			payment.setItems(items);
		}
		payments.add(payment);
		request.setOrder(orderPC);
		request.setPayments(payments);
		final Gson gson = new Gson();
		LOG.info(gson.toJson(request));
		response = paymentCommerceService.cardValidate(request, token);
		return response;
	}

	private String getTypeUserCVTDAT()
	{
		String typeUserCVTDAT = sessionService.getAttribute("typeUserCVTDAT");
		if (Strings.isNullOrEmpty(typeUserCVTDAT))
		{
			final RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
			typeUserCVTDAT = Objects.nonNull(requestAttributes)
					? telcelUtil.getCookieByRequest(((ServletRequestAttributes) requestAttributes).getRequest(), "typeUserCVTDAT")
					: StringUtils.EMPTY;
			typeUserCVTDAT = typeUserCVTDAT == null ? "" : typeUserCVTDAT;
			LOG.info(String.format("Debug - cookie typeUserCVTDAT: %s", typeUserCVTDAT));
		}
		else
		{
			LOG.info("Se obtiene el valor de typeUserCVTDAT desde sessionService");
		}
		return typeUserCVTDAT;
	}

	private SpeiValidateResponse executePaymentSpei(final String cartId, final String userId, final String token) throws Exception
	{
		final CartModel cart = getCartForUserByCartId(cartId, userId);
		SpeiValidateResponse response = null;
		if (Objects.isNull(cart))
		{
			throw new Exception("No se encontro el carrito con el codigo : " + cartId);
		}
		LOG.info("CART CODE: " + cart.getCode());
		final PaymentCommerceInfoData pci = paymentCommerceInfoCartConverter.convert(cart);
		AddressModel deliveryAddress = cart.getDeliveryAddress();
		final SpeiValidateRequest request = new SpeiValidateRequest();
		final Order orderPC = new Order();
		if(deliveryAddress != null) {

			final String postalCode = deliveryAddress.getPostalcode();
			final CodigosPostalesTelcelData codigosPostalesTelcelData = telcelCodigosPostalesService.getInfForZipcode(postalCode);

			if (!Objects.isNull(codigosPostalesTelcelData) || !Objects.isNull(codigosPostalesTelcelData.getRegion())) {
				orderPC.setRegion(Integer.valueOf(codigosPostalesTelcelData.getRegion().getCode()));
			}
			orderPC.setCountry("México");
			orderPC.setCity(StringUtils.isNotEmpty(deliveryAddress.getTown()) ? deliveryAddress.getTown() : "NA");
			orderPC.setState(Objects.nonNull(deliveryAddress.getRegion()) ? deliveryAddress.getRegion().getName() : "NA");
			orderPC.setAddress(deliveryAddress.getStreetname() + " " + deliveryAddress.getExternalNumber());
			orderPC.setPostalCode(StringUtils.isNotEmpty(deliveryAddress.getPostalcode()) ? deliveryAddress.getPostalcode() : "NA");

		}
		orderPC.setEmail(pci.getOrder().getEmail());
		orderPC.setPhone(StringUtils.isNotEmpty(pci.getOrder().getPhone()) ? pci.getOrder().getPhone() : deliveryAddress.getPhone1());
		orderPC.setFirstname(pci.getPayment().getFirstname());
		orderPC.setLastname(pci.getPayment().getLastname());

		final List<Payment> payments = new ArrayList<Payment>();
		final Payment payment = new Payment();
		payment.setAmount(pci.getPayment().getAmount());
		payment.setType("SPEI");
		final List<Item> items = new ArrayList<Item>();
		for (final ItemPCData itempc : pci.getPayment().getItems())
		{
			final Item item = new Item();
			item.setSku(itempc.getSku());
			item.setDescription(itempc.getDescription());
			item.setPrice(itempc.getPrice());
			item.setModel(itempc.getModel());
			item.setColor(StringUtils.isNotEmpty(itempc.getColor()) ? itempc.getColor() : "NA");
			items.add(item);
			payment.setItems(items);
		}
		payments.add(payment);
		request.setOrder(orderPC);
		request.setPayments(payments);
		final Gson gson = new Gson();
		LOG.info(gson.toJson(request));
		response = paymentCommerceService.speiValidate(request, token);
		return response;
	}

	private ReferenceValidateResponse executePaymentReference(final String cartId, final String userId, final String token)
			throws Exception
	{
		final CartModel cart = getCartForUserByCartId(cartId, userId);
		ReferenceValidateResponse response = null;
		if (Objects.isNull(cart))
		{
			throw new Exception("No se encontro el carrito con el codigo : " + cartId);
		}
		LOG.info("CART CODE: " + cart.getCode());
		final PaymentCommerceInfoData pci = paymentCommerceInfoCartConverter.convert(cart);
		AddressModel deliveryAddress = cart.getDeliveryAddress();
		final ReferenceValidateRequest request = new ReferenceValidateRequest();
		final Order orderPC = new Order();
		if(deliveryAddress != null) {

			final String postalCode = deliveryAddress.getPostalcode();
			final CodigosPostalesTelcelData codigosPostalesTelcelData = telcelCodigosPostalesService.getInfForZipcode(postalCode);

			if (!Objects.isNull(codigosPostalesTelcelData) || !Objects.isNull(codigosPostalesTelcelData.getRegion())) {
				orderPC.setRegion(Integer.valueOf(codigosPostalesTelcelData.getRegion().getCode()));
			}
			orderPC.setCountry("México");
			orderPC.setCity(StringUtils.isNotEmpty(deliveryAddress.getTown()) ? deliveryAddress.getTown() : "NA");
			orderPC.setState(Objects.nonNull(deliveryAddress.getRegion()) ? deliveryAddress.getRegion().getName() : "NA");
			orderPC.setAddress(deliveryAddress.getStreetname() + " " + deliveryAddress.getExternalNumber());
			orderPC.setPostalCode(StringUtils.isNotEmpty(deliveryAddress.getPostalcode()) ? deliveryAddress.getPostalcode() : "NA");

		}
		orderPC.setEmail(pci.getOrder().getEmail());
		orderPC.setPhone(StringUtils.isNotEmpty(pci.getOrder().getPhone()) ? pci.getOrder().getPhone() : deliveryAddress.getPhone1());
		orderPC.setFirstname(pci.getPayment().getFirstname());
		orderPC.setLastname(pci.getPayment().getLastname());

		final List<Payment> payments = new ArrayList<Payment>();
		final Payment payment = new Payment();
		payment.setAmount(pci.getPayment().getAmount());
		payment.setType(pci.getPayment().getPaymentReferenceName());
		final List<Item> items = new ArrayList<Item>();
		for (final ItemPCData itempc : pci.getPayment().getItems())
		{
			final Item item = new Item();
			item.setSku(itempc.getSku());
			item.setDescription(itempc.getDescription());
			item.setPrice(itempc.getPrice());
			item.setModel(itempc.getModel());
			item.setColor(StringUtils.isNotEmpty(itempc.getColor()) ? itempc.getColor() : "NA");
			items.add(item);
			payment.setItems(items);
		}
		payments.add(payment);
		request.setOrder(orderPC);
		request.setPayments(payments);
		final Gson gson = new Gson();
		LOG.info(gson.toJson(request));
		response = paymentCommerceService.referenceValidate(request, token);
		return response;
	}



	private ValidateTokenResponse getToken() throws Exception
	{
		final AuthRequest request = new AuthRequest();
		request.setUsername(configurationService.getConfiguration().getString(AUTH_USER));
		request.setPassword(configurationService.getConfiguration().getString(AUTH_PASSWORD));
		request.setAccount(configurationService.getConfiguration().getString(AUTH_ACCOUNT));
		request.setChannel(configurationService.getConfiguration().getString(AUTH_CHANNEL));
		final AuthResponse auth = paymentCommerceService.auth(request);
		if (auth.getStatusCode().intValue() == HttpStatus.UNAUTHORIZED.value())
		{
			throw new Exception("No se pudo autenticar los datos para generar el token");
		}
		final ValidateTokenResponse validateToken = paymentCommerceService.validateToken(auth.getToken().getToken());
		if (!validateToken.isValid().booleanValue())
		{
			throw new Exception("El token no es valido");
		}
		return validateToken;
	}

	private CartModel getCartForUserByCartId(final String cartId, final String userId)
	{
		final UserModel user = userService.getUserForUID(userId);
		return commerceCartService.getCartForCodeAndUser(cartId, user);
	}

	private OrderModel getOrder(final String orderId) throws Exception
	{
		OrderModel order = null;
		BaseStoreModel baseStoreModel = baseStoreService.getCurrentBaseStore();
		if (Objects.isNull(baseStoreModel))
		{
			baseStoreModel = baseStoreService.getBaseStoreForUid(TELCEL);
		}
		if (Objects.nonNull(baseStoreModel))
		{
			order = customerAccountService.getOrderForCode(orderId, baseStoreModel);
			if (Objects.isNull(order))
			{
				LOG.info("Order Dont Exist");
				throw new Exception("La orden" + orderId + " no existe");
			}
		}
		else
		{
			LOG.info("Base store telcel dont exist");
			throw new Exception("El base store telcel no existe");
		}
		return order;
	}

	private void updateOrderPaymentStatus(final OrderModel order, final String status, final String risk)
	{
		LOG.info("Update Payment Status : (" + status + ", " + risk + ")");
		if (status.equals("SUCCESS") && risk.equals("GREEN"))
		{
			order.setPaymentStatus(PaymentStatus.PAID);
			modelService.save(order);
		}
	}

	/**
	 * Service payment commerce encryption card<br/>
	 * Example :</br>
	 * POST https://localhost:9002/telcelb2cwebservices/paymentcommerce/encryptioncard</br>
	 *
	 * Method : POST</br>
	 *
	 * @return - ReferenceConfirmResponse object</br>
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 * @throws IOException
	 *
	 */
	@ResponseBody
	@PostMapping(value = "/encryptioncard", produces = "application/json")
	public void encryptioncard(@ApiParam(value = "name", required = true)
	@RequestParam(required = true)
	final String name, @ApiParam(value = "numbercard", required = true)
	@RequestParam(required = true)
	final String numberCard, @ApiParam(value = "expiryyear", required = true)
	@RequestParam(required = true)
	final String expiryYear, @ApiParam(value = "expirymonth", required = true)
	@RequestParam(required = true)
	final String expiryMonth, @ApiParam(value = "saved", required = true)
	@RequestParam(required = true)
	final boolean saved, @ApiParam(value = "cvn", required = true)
	@RequestParam(required = true)
	final String cvn, @ApiParam(value = "zip", required = true)
	@RequestParam(required = true)
	final String zip) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException
	{

		LOG.info("Nombre: " + name + " C.P.: " + zip + " Guardada: " + saved);
		LOG.info("Tarjeta: " + numberCard + " Fecha: " + expiryMonth + expiryYear + " cvn: " + cvn);
	}

}
