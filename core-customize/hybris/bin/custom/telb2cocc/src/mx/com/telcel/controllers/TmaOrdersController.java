/*
 *	Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.controllers;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.b2ctelcofacades.order.TmaCartFacade;
import de.hybris.platform.b2ctelcoservices.compatibility.eligibility.TmaEligibilityContextService;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.ConsignmentData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.request.mapping.annotation.RequestMappingOverride;
import de.hybris.platform.commercewebservicescommons.dto.order.LinksOrderWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderWsDTO;
import de.hybris.platform.commercewebservicescommons.strategies.CartLoaderStrategy;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.enums.PaymentStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.ReferencePaymentInfoModel;
import de.hybris.platform.core.model.order.payment.SpeiPaymentInfoModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.europe1.enums.UserPriceGroup;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.amx.telcel.di.sds.esb.sap.cancelacion.LiberarRecursosResponse;
import com.google.gson.Gson;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import mx.com.amx.mexico.telcel.di.sds.gsa.dai.esb.consultastock.ConsultarAlmacenCPResponse;
import mx.com.amx.mexico.telcel.di.sds.gsa.dai.esb.consultastock.MaterialAlmacenRespType;
import mx.com.telcel.core.daos.cvtdat.UserInfoCVTDao;
import mx.com.telcel.core.daos.cvtdat.UserInfoDATDao;
import mx.com.telcel.core.model.AdditionalServiceEntryModel;
import mx.com.telcel.core.model.CybersourceKeysModel;
import mx.com.telcel.core.model.FingerprintModel;
import mx.com.telcel.core.model.HolderLineModel;
import mx.com.telcel.core.model.ItemPaymentModel;
import mx.com.telcel.core.model.OpenpayKeysModel;
import mx.com.telcel.core.model.PaymentReceiptModel;
import mx.com.telcel.core.model.PaymentsValidateModel;
import mx.com.telcel.core.model.SendErpModel;
import mx.com.telcel.core.model.SendErpRespModel;
import mx.com.telcel.core.model.TelcelFacturaModel;
import mx.com.telcel.core.model.TelcelPoVariantModel;
import mx.com.telcel.core.model.TelcelRollbackModel;
import mx.com.telcel.core.model.UserInfoCVTModel;
import mx.com.telcel.core.model.UserInfoDATModel;
import mx.com.telcel.core.models.liberarrecursos.InfoLiberarRecursos;
import mx.com.telcel.core.models.paymentcommerce.AddCardRequest;
import mx.com.telcel.core.models.paymentcommerce.CardConfirmRequest;
import mx.com.telcel.core.models.paymentcommerce.CardConfirmResponse;
import mx.com.telcel.core.models.paymentcommerce.CardConsultRequest;
import mx.com.telcel.core.models.paymentcommerce.CardResponse;
import mx.com.telcel.core.models.paymentcommerce.CardValidateRequest;
import mx.com.telcel.core.models.paymentcommerce.CardValidateResponse;
import mx.com.telcel.core.models.paymentcommerce.CybersourceKeys;
import mx.com.telcel.core.models.paymentcommerce.FingerPrint;
import mx.com.telcel.core.models.paymentcommerce.Item;
import mx.com.telcel.core.models.paymentcommerce.Order;
import mx.com.telcel.core.models.paymentcommerce.Payment;
import mx.com.telcel.core.models.paymentcommerce.ReferenceConfirmRequest;
import mx.com.telcel.core.models.paymentcommerce.ReferenceConfirmResponse;
import mx.com.telcel.core.models.paymentcommerce.RequestAddress;
import mx.com.telcel.core.models.paymentcommerce.SpeiConfirmRequest;
import mx.com.telcel.core.models.paymentcommerce.SpeiConfirmResponse;
import mx.com.telcel.core.models.paymentcommerce.ValidateTokenResponse;
import mx.com.telcel.core.service.AESService;
import mx.com.telcel.core.service.ReplicateStockService;
import mx.com.telcel.core.services.TelcelCodigosPostalesService;
import mx.com.telcel.core.services.TelcelOrderService;
import mx.com.telcel.core.services.TelcelSoapConverterService;
import mx.com.telcel.core.services.mq.liberacionrecursos.LiberaRecursosMQService;
import mx.com.telcel.core.services.paymentcommerce.PaymentCommerceService;
import mx.com.telcel.core.util.TelcelUtil;
import mx.com.telcel.facades.orders.CustomCheckoutFacade;
import mx.com.telcel.facades.orders.TelcelOrdersFacade;
import mx.com.telcel.facades.paymentcommerce.data.ItemPCData;
import mx.com.telcel.facades.paymentcommerce.data.PaymentCommerceInfoData;
import mx.com.telcel.facades.zipcodes.data.CodigosPostalesTelcelData;


/**
 * Web Service Controller for the ORDERS resource
 *
 * @since 2001
 */


@Controller
@RequestMapping(value = "/{baseSiteId}")
@Api(tags = "Orders")
public class TmaOrdersController extends BaseController
{
	private static final Logger LOG = LoggerFactory.getLogger(TmaOrdersController.class);
	private static final String TELCEL = "telcel";
	private static final String TELCEL_PAYMENT_COMMERCE_RETURN_3_DS = "telcel.payment.commerce.return3DS";
	public static final String MASTERCARD = "MASTERCARD";
	public static final String MASTER = "master";
	public static final String COMPLETE = "COMPLETE";
	public static final String REVIEW = "REVIEW";
	private static final String TELCEL_SALES_FORCE_R = "telcel.sales.force.r";
	private static final String TELCEL_SALES_FORCE_ALPHANUMERIC_R = "telcel.sales.force.alphanumeric.r";
	String TEST_PERFORMANCE = "telcel.performance.test.enabled";


	@Resource(name = "cartLoaderStrategy")
	private CartLoaderStrategy cartLoaderStrategy;

	@Resource(name = "tmaCartFacade")
	private TmaCartFacade tmaCartFacade;

	//    @Resource
	//    TmaCheckoutFacade tmaCheckoutFacade;

	@Resource(name = "customCheckoutFacade")
	private CustomCheckoutFacade tmaCheckoutFacade;

	@Resource(name = "customerFacade")
	private CustomerFacade customerFacade;

	@Resource(name = "orderFacade")
	private OrderFacade orderFacade;

	@Resource
	private ReplicateStockService replicateStockService;

	@Resource
	private TelcelUtil telcelUtil; // NOSONAR

	@Resource
	private BaseStoreService baseStoreService;

	@Resource
	private CartService cartService;

	@Resource(name = "paymentCommerceService")
	private PaymentCommerceService paymentCommerceService;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource
	private CartFacade cartFacade;

	@Resource(name = "customerAccountService")
	private CustomerAccountService customerAccountService;

	@Resource
	private EnumerationService enumerationService;

	@Resource(name = "userInfoCVTDao")
	private UserInfoCVTDao userInfoCVTDao;

	@Resource(name = "userInfoDATDao")
	private UserInfoDATDao userInfoDATDao;

	@Resource(name = "telcelOrdersFacade")
	private TelcelOrdersFacade telcelOrdersFacade;

	@Resource
	private TmaEligibilityContextService tmaEligibilityContextService;

	@Resource
	private CommerceCartService commerceCartService;

	@Resource
	private BaseSiteService baseSiteService;

	@Resource
	private TelcelOrderService telcelOrderService;

	@Resource(name = "telcelCodigosPostalesService")
	private TelcelCodigosPostalesService telcelCodigosPostalesService;

	@Resource(name = "liberaRecursosMQService")
	private LiberaRecursosMQService liberaRecursosMQService;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "aesService")
	private AESService aesService;

	private String error3ds = StringUtils.EMPTY;
	private Boolean is3dsecureParam = false;
	private String id3dsParam = StringUtils.EMPTY;
	private Boolean timerFromStoreParam = false;
	

	@Resource
	private UserService userService;

	@Resource(name = "paymentCommerceInfoCartConverter")
	private Converter<CartModel, PaymentCommerceInfoData> paymentCommerceInfoCartConverter;

	@Resource(name = "telcelSoapConverterService")
	private TelcelSoapConverterService telcelSoapConverterService;

	@Resource(name = "defaultConfigurationService")
	private ConfigurationService configurationService;

	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_ANONYMOUS" })
	@RequestMapping(value = "/users/{userId}/orders", method = RequestMethod.POST)
	@RequestMappingOverride(priorityProperty = "telb2cocc.TmaOrdersController.placeOrder.priority")
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	@ApiOperation(nickname = "placeOrder", value = "Place a order.", notes = "Authorizes the cart and places the order. The response contains the new order data.")
	@ApiBaseSiteIdAndUserIdParam
	public ResponseEntity<Object> placeOrder(@ApiFieldsParam
	@RequestParam(defaultValue = DEFAULT_FIELD_SET)
	final String fields, @ApiParam(value = "Cart code for logged in user, cart GUID for guest checkout", required = true)
	@RequestParam
	final String cartId, @ApiParam(value = "sessionID", required = false)
	@RequestParam
	final String sessionID, @ApiParam(value = "typeUserCVTDAT", required = false)
	@RequestParam(required = false)
	final String typeUserCVTDAT, @ApiParam(value = "idUserCVTDAT", required = false)
	@RequestParam(required = false)
	final Boolean is3dsecure, @ApiParam(value = "is3dsecure", required = false)
	@RequestParam(required = false)
	final String id3ds, @ApiParam(value = "id3ds", required = false)
	@RequestParam(required = false)
	final Boolean timerFromStore, @ApiParam(value = "timerFromStore", required = false)
	@RequestParam(required = false)
	final String idUserCVTDAT, final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
			throws Exception
	{
		final String userId = customerFacade.getCurrentCustomerUid();

		CartModel cartModel = getCartForUserByGUID(cartId);

		is3dsecureParam = is3dsecure != null ? is3dsecure : false;
		id3dsParam = StringUtils.isNotBlank(id3ds) ? id3ds : StringUtils.EMPTY;
		timerFromStoreParam = timerFromStore != null ? timerFromStore : false;
		error3ds = StringUtils.EMPTY;

		if (timerFromStoreParam)
		{
			is3dsecureParam = false;
			id3dsParam = StringUtils.EMPTY;
			error3ds = StringUtils.EMPTY;
		}

		if (cartModel == null)
		{
			cartModel = commerceCartService.getCartForCodeAndUser(cartId, userService.getCurrentUser());
		}

		final String cookie = setCookieIfIsEmpty(httpServletRequest, cartModel, cartId);
		if (cookie != null)
		{
			LOG.error(
					String.format("Cookie is empty, setting cookie to user %s, cartId: %s and cookie: %s,", userId, cartId, cookie));
			httpServletResponse.setHeader("Set-Cookie", "tlsr=" + cookie + "; Path=/; Secure");
		}


		if (cartModel.getPaymentInfo() instanceof CreditCardPaymentInfoModel)
		{
			final CreditCardPaymentInfoModel creditCardPaymentInfoModel = (CreditCardPaymentInfoModel) cartModel.getPaymentInfo();
			if (StringUtils.isNotEmpty(creditCardPaymentInfoModel.getLinkUrl3ds())
					&& creditCardPaymentInfoModel.getReceipt().isEmpty() && StringUtils.isEmpty(id3ds))
			{
				final OrderWsDTO orderWsDTO = getDataMapper().map(new OrderData(), OrderWsDTO.class);
				orderWsDTO.setUrl3ds(creditCardPaymentInfoModel.getLinkUrl3ds());
				LOG.info("Entro StringUtils.isNotEmpty(creditCardPaymentInfoModel.getLinkUrl3ds())\n"
						+ "\t\t\t\t\t&& creditCardPaymentInfoModel.getReceipt().isEmpty() && StringUtils.isEmpty(id3ds)");
				return new ResponseEntity<>(orderWsDTO, HttpStatus.CONFLICT);

			}
		}

		if ("anonymous".equalsIgnoreCase(userId))
		{
			tmaEligibilityContextService.updateEligibilityContextsByCustomer(true, (CustomerModel) cartModel.getUser());
		}

		cartLoaderStrategy.loadCart(cartId);

		saveSessionId(sessionID);
		LOG.debug("validateCartForPlaceOrder inicio *********");
		validateCartForPlaceOrder();
		LOG.debug("validateCartForPlaceOrder fin *********");
		LOG.debug("error3ds [" + this.error3ds + "]");


		if (StringUtils.isNotEmpty(this.error3ds))
		{
			LOG.debug("Entro StringUtils.isNotEmpty(error3ds)");
			final List<String> linksUrls3ds = new ArrayList<>();
			final LinksOrderWsDTO linksOrderWsDTO = new LinksOrderWsDTO();
			if (cartModel.getPaymentInfo() != null && cartModel.getPaymentInfo().getReceiptStatus() != null)
			{
				LOG.debug("cartModel.getPaymentInfo() != null && cartModel.getPaymentInfo().getReceiptStatus() != null");
				final List<PaymentReceiptModel> paymentReceiptModelList = cartModel.getPaymentInfo().getReceiptStatus();
				for (final PaymentReceiptModel paymentReceiptModel : paymentReceiptModelList)
				{
					LOG.info("paymentReceiptModel.getStatus() [" + paymentReceiptModel.getStatus() + "]");
					if (paymentReceiptModel.getStatus().equalsIgnoreCase("REVIEW"))
					{
						linksUrls3ds.add(paymentReceiptModel.getUrl3DS());
					}
				}
			}

			if (linksUrls3ds.size() > 0)
			{
				linksOrderWsDTO.setUrl3ds(linksUrls3ds);
				return new ResponseEntity<>(linksOrderWsDTO, HttpStatus.CONFLICT);
			}
			error3ds = StringUtils.EMPTY;

		}


		associateCVTDATWithOrder(typeUserCVTDAT, idUserCVTDAT);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Place order cart: code=" + cartId + " | user=" + userId);
		}

		final OrderData orderData = tmaCheckoutFacade.placeOrderFromCartCustom(tmaCartFacade.getSessionCart().getCode(), cartId,
				userId, true);

		orderData.setOrderne(aesService.encryptGCM(orderData.getCode()));
		final OrderModel order = getOrder(orderData.getCode());
		if (Objects.nonNull(order))
		{
			final UserModel user = order.getUser();
			orderData.setUsne(aesService.encryptGCM(user.getUid(), user.getPk().getLongValueAsString()));
		}
		return new ResponseEntity<>(getDataMapper().map(orderData, OrderWsDTO.class), HttpStatus.OK);
	}

	private String setCookieIfIsEmpty(final HttpServletRequest httpServletRequest, final CartModel cartModel, final String cartId)
			throws Exception
	{
		final String region = telcelUtil.getRegionByRequest(httpServletRequest); // NOSONAR
		if (StringUtils.isEmpty(region))
		{


			final String postalCode = cartModel.getDeliveryAddress().getPostalcode();
			final CodigosPostalesTelcelData codigosPostalesTelcelData = telcelCodigosPostalesService.getInfForZipcode(postalCode);
			if (Objects.isNull(codigosPostalesTelcelData) || Objects.isNull(codigosPostalesTelcelData.getRegion()))
			{
				throw new Exception(String.format("No information for zip code: %s", postalCode));
			}
			final CustomerModel customerModel = (CustomerModel) cartModel.getUser();
			final UserPriceGroup userPriceGroup = enumerationService.getEnumerationValue(UserPriceGroup.class,
					codigosPostalesTelcelData.getRegion().getCode());
			if (Objects.nonNull(customerModel) && Objects.nonNull(userPriceGroup) && !userService.isAnonymousUser(customerModel))
			{
				customerModel.setEurope1PriceFactory_UPG(userPriceGroup);
				modelService.save(customerModel);
			}
			return String.format("%s-%s", userPriceGroup.getCode(), codigosPostalesTelcelData.getCodigo());
		}
		return null;
	}

	private CartModel getCartForUserByGUID(final String guid)
	{
		return commerceCartService.getCartForGuidAndSite(guid, baseSiteService.getBaseSiteForUID(TELCEL));
	}


	private void saveSessionId(final String sessionID)
	{
		final CartModel cartModel = getCartService().getSessionCart();
		cartModel.setSessionIdWs(sessionID);
		modelService.save(cartModel);
	}


	protected CartModel getCart()
	{
		return tmaCartFacade.hasSessionCart() ? getCartService().getSessionCart() : null;
	}

	private void executePayment(final CartData sessionCart, final Errors errors, final boolean isMsi)
			throws NoSuchAlgorithmException, KeyStoreException, IOException, KeyManagementException
	{
		LOG.info("2.- executePayment");
		final CCPaymentInfoData ccPaymentInfoData = sessionCart.getPaymentInfo();

		final CartModel cartModel = getCart();

		if (cartModel.getStatusValidePayment())
		{
			if (cartModel.getPaymentInfo() instanceof CreditCardPaymentInfoModel)
			{
				LOG.info("3.-executePayment card");
				if (haveErpResponsesList(cartModel))
				{
					executePaymentCard(cartModel, errors, isMsi);
				}
				else
				{
					LOG.info("!haveErpResponsesList(cartModel) hacer rollback");
					runRollback(cartModel);
					errors.reject("payment.execute.failed", new String[]
					{ sessionCart.getCode() }, "Ocurrio un error {0} ");
					return;
				}

			}
			else if (cartModel.getPaymentInfo() instanceof SpeiPaymentInfoModel)
			{
				LOG.info("3.-executePayment spei");

				if (haveErpResponsesList(cartModel))
				{
					executePaymentSpei(cartModel, errors);
				}
				else
				{
					LOG.info("!haveErpResponsesList(cartModel) hacer rollback");
					runRollback(cartModel);
					errors.reject("payment.execute.failed", new String[]
					{ sessionCart.getCode() }, "Ocurrio un error {0} ");
					return;
				}

			}
			else if (cartModel.getPaymentInfo() instanceof ReferencePaymentInfoModel)
			{
				LOG.info("3.-executePayment store");

				if (haveErpResponsesList(cartModel))
				{
					executePaymentReference(cartModel, errors);
				}
				else
				{
					LOG.info("!haveErpResponsesList(cartModel) hacer rollback");
					runRollback(cartModel);
					errors.reject("payment.execute.failed", new String[]
					{ sessionCart.getCode() }, "Ocurrio un error {0} ");
					return;
				}
			}
		}
		else
		{
			LOG.info("!cartModel.getStatusValidePayment() hacer rollback");
			runRollback(cartModel);
			errors.reject("payment.execute.failed", new String[]
			{ sessionCart.getCode() }, "No se encuentra validado el pago {0} ");
			return;

		}

	}

	private boolean haveErpResponsesList(final CartModel cartModel)
	{
		boolean resp = true;
		for (final AbstractOrderEntryModel entry : cartModel.getEntries())
		{
			if (entry.getSendErpRespList().isEmpty())
			{
				resp = false;
				break;
			}
		}
		return resp;
	}

	private void executePaymentReference(final CartModel cartModel, final Errors errors)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException
	{
		LOG.info("4.-executePaymentReference");
		final PaymentInfoModel paymentInfoModel = cartModel.getPaymentInfo();

		final ReferenceConfirmRequest rcrequest = new ReferenceConfirmRequest();
		if (StringUtils.isEmpty(paymentInfoModel.getOrderIdWs()))
		{
			LOG.info("No se ejecuto el servicio correctamente por falta de datos [OrderIdWs] executePaymentReference");
			errors.reject("payment.execute.failed", new String[]
			{ cartModel.getCode() }, "Ocurrio un error al ejecutar pago por falta de datos [OrderIdWs] {0} ");
			return;
		}
		rcrequest.setOrderId(paymentInfoModel.getOrderIdWs());
		final ReferenceConfirmResponse rcresponse = paymentCommerceService.referenceConfirm(rcrequest,
				paymentInfoModel.getTokenPayment());
		if (rcresponse.getStatusCode() != HttpStatus.OK.value())
		{
			LOG.info("No se ejecuto el servicio correctamente");
			errors.reject("payment.execute.failed", new String[]
			{ cartModel.getCode() }, "Ocurrio un error al ejecutar pago {0} ");
		}
		else
		{
			LOG.info("Se ejecuto el servicio correctamente");
			LOG.info("executePaymentReference status correcto al 200");
			updateOrderPaymentStatusReference(cartModel, rcresponse);
		}

		if (errors.hasErrors())
		{
			LOG.info("Hay Errores hacer rollback reference");
			runRollback(cartModel);
		}
	}

	private void executePaymentSpei(final CartModel cartModel, final Errors errors)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException
	{
		LOG.info("4.-executePaymentSpei");
		final PaymentInfoModel paymentInfoModel = cartModel.getPaymentInfo();

		final SpeiConfirmRequest screquest = new SpeiConfirmRequest();

		if (StringUtils.isEmpty(paymentInfoModel.getOrderIdWs()))
		{
			LOG.info("No se ejecuto el servicio correctamente por falta de datos [OrderIdWs] executePaymentSpei");
			errors.reject("payment.execute.failed", new String[]
			{ cartModel.getCode() }, "Ocurrio un error al ejecutar pago por falta de datos [OrderIdWs] {0} ");
			return;
		}

		screquest.setOrderId(paymentInfoModel.getOrderIdWs());
		final SpeiConfirmResponse scresponse = paymentCommerceService.speiConfirm(screquest, paymentInfoModel.getTokenPayment());
		if (scresponse.getStatusCode() != HttpStatus.OK.value())
		{
			LOG.info("No se ejecuto el servicio correctamente");
			errors.reject("payment.execute.failed", new String[]
			{ cartModel.getCode() }, "Ocurrio un error al ejecutar pago {0} ");
		}
		else
		{
			LOG.info("Se ejecuto el servicio correctamente");
			LOG.info("executePaymentReference status correcto al 200");
			updateOrderPaymentStatusSpei(cartModel, scresponse);
		}

		if (errors.hasErrors())
		{
			LOG.info("Hay Errores hacer rollback spei");
			runRollback(cartModel);
		}
	}

	private void executePaymentCard(final CartModel cartModel, final Errors errors, final boolean isMsi)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException
	{
		LOG.info("4.-executePaymentCard");
		final PaymentInfoModel paymentInfoModel = cartModel.getPaymentInfo();
		final int attempts = paymentInfoModel.getAttemptsPayment() != null ? paymentInfoModel.getAttemptsPayment() : 0;
		LOG.info("executePaymentCard attempts [" + attempts + "]");
		paymentInfoModel.setAttemptsPayment(attempts + 1);
		modelService.save(paymentInfoModel);


		//Bloque de codigo para pago con 3ds
		if (is3dsecureParam && StringUtils.isNotBlank(id3dsParam))
		{
			LOG.info("Entro por 3ds cart["+cartModel.getCode()+"] - is3dsecureParam ["+is3dsecureParam+"] - id3dsParam ["+id3dsParam+"]");
			if ((paymentInfoModel instanceof CreditCardPaymentInfoModel))
			{
				final CreditCardPaymentInfoModel creditCardPaymentInfoModel = (CreditCardPaymentInfoModel) paymentInfoModel;
				for(PaymentReceiptModel receiptModel : creditCardPaymentInfoModel.getReceiptStatus()){
					if("REVIEW".equalsIgnoreCase(receiptModel.getStatus())){
						receiptModel.setVerifiedPayment(true);
						modelService.save(receiptModel);
					}
				}

				LOG.info("5.-executePaymentCard with 3ds");
				//consultar servicio
//				final CardConsultRequest cardConsultRequest = createRequestCardConsult(cartModel, errors, paymentInfoModel,
//						creditCardPaymentInfoModel);
//				if (cardConsultRequest == null)
//				{
//					return;
//				}

				final CustomerModel currentCustomer = (CustomerModel) cartModel.getUser();
				final String emailUidTelcel = CustomerType.GUEST.equals(currentCustomer.getType())
						? org.apache.commons.lang.StringUtils.substringAfter(currentCustomer.getUid(), "|")
						: telcelUtil.getEmailRegisterForToken(currentCustomer.getUid(), currentCustomer.getContactEmail());
				final ValidateTokenResponse validateTokenResponse = getToken(emailUidTelcel);

				//Llamar servicio para ver estatus de pago
				final CardConfirmResponse ccresponse = new CardConfirmResponse();
				//				final CardConfirmResponse ccresponse = paymentCommerceService.cardConsult(cardConsultRequest,
				//						validateTokenResponse.getToken());
				LOG.info("executePaymentCard status correcto al 200");

				creditCardPaymentInfoModel.setUrl3ds("");
				this.error3ds = "";
				modelService.save(creditCardPaymentInfoModel);

				//Validamos que solo exista un payment
				//				if (ccresponse.getStatusCode() != 200)
				//				{
				//					LOG.info("No se ejecuto el servicio correctamente ccresponse nulo");
				//					LOG.info("ccresponse.getStatusCode() != 200 && paymentInfoModel.getPaymentsValidate().size() == 1");
				//					errors.reject("payment.execute.failed", new String[]
				//					{ cartModel.getCode() }, "Ocurrio un error al ejecutar pago  ccresponse nulo {0} ");
				//					return;
				//				}

				//Validamos que exista mas de un pago
				//				if(ccresponse.getStatusCode() != 200 && paymentInfoModel.getPaymentsValidate().size() > 1){
				//
				//					ValidateTokenResponse validatePaymentTwoTokenResponse = getToken(emailUidTelcel);
				//					cardConsultRequest.setPaymentId(paymentInfoModel.getPaymentsValidate().get(1).getPaymentId());
				//					final CardConfirmResponse ccresponsepaymenttwo = paymentCommerceService.cardConsult(cardConsultRequest, validatePaymentTwoTokenResponse.getToken());
				//
				//					if(ccresponsepaymenttwo.getStatusCode() != 200){
				//						LOG.info("No se ejecuto el servicio correctamente ccresponse nulo");
				//						LOG.info("ccresponse.getStatusCode() != 200 && paymentInfoModel.getPaymentsValidate().size() > 1");
				//						errors.reject("payment.execute.failed", new String[]
				//								{ cartModel.getCode() }, "Ocurrio un error al ejecutar pago  ccresponse nulo {0} ");
				//						return;
				//					}
				//				}
				LOG.info("6.-Running updateOrderPaymentStatus");
				updateOrderPaymentStatus3Ds(cartModel, ccresponse, errors, true);
			}
			saveCard(cartModel, paymentInfoModel);

		}
		else
		{

			//Bloque de codigo para pagos normales (Sin 3ds)
			LOG.info("5.-Bloque de codigo para pagos normales (Sin 3ds)");

			final CustomerModel customerModel = (CustomerModel) cartModel.getUser();
			final ValidateTokenResponse validateToken = paymentCommerceService.validateToken(paymentInfoModel.getTokenPayment());

			final String emailUid = CustomerType.GUEST.equals(customerModel.getType())
					? org.apache.commons.lang.StringUtils.substringAfter(customerModel.getUid(), "|")
					: telcelUtil.getEmailForToken(customerModel.getContactEmail(), cartModel.getAddressHolderLine().getEmail());

			//Validamos si el token que se tiene aun es valido
			if ((paymentInfoModel instanceof CreditCardPaymentInfoModel) && validateToken.getStatusCode() == 500
					|| (paymentInfoModel instanceof CreditCardPaymentInfoModel) && paymentInfoModel.getAttemptsPayment() > 1)
			{
				//El token ya no es valido
				final CreditCardPaymentInfoModel creditCardPaymentInfoModel = (CreditCardPaymentInfoModel) paymentInfoModel;

				//Generaramos un token nuevo
				final ValidateTokenResponse validateTokenResponse = getToken(emailUid);
				//Generar el valite de nuevo
				final boolean validateCard = callValidateCard(validateTokenResponse, cartModel,
						creditCardPaymentInfoModel.getSavedCard());
				if (!validateCard)
				{
					LOG.info("On !validateCard");
					errors.reject("payment.execute.failed", new String[]
					{ cartModel.getCode() }, "Ocurrio un error al validar pago {0} ");
					return;
				}
				LOG.info("6.-Genero correcto el validateCard");
			}


			final CardConfirmRequest ccrequest = createRequestCardConfirm(cartModel, paymentInfoModel);
			LOG.info("executePaymentCard paymentCommerceService.cardConfirm");
			final CardConfirmResponse ccresponse = paymentCommerceService.cardConfirm(ccrequest, paymentInfoModel.getTokenPayment());

			if (ccresponse != null && ccresponse.getStatusCode() != HttpStatus.OK.value())
			{
				LOG.info("On ccresponse != null && ccresponse.getStatusCode() != HttpStatus.OK.value()");
				errors.reject("payment.execute.failed", new String[]
				{ cartModel.getCode() }, "Ocurrio un error al ejecutar pago {0} ");
				runRollback(cartModel);
				return;

			}
			else
			{
				LOG.info("executePaymentCard status correcto al 200");
				LOG.info("7.-Running updateOrderPaymentStatus");
				updateOrderPaymentStatus(cartModel, ccresponse, errors, isMsi);
				saveCard(cartModel, paymentInfoModel);
			}

		}

		if (errors.hasErrors())
		{
			LOG.info("Hay Errores hacer rollback card");
			runRollback(cartModel);
		}
	}


	private void updateOrderPaymentStatus3Ds(final CartModel cartModel, final CardConfirmResponse ccresponse, final Errors errors,
			final boolean isMsi)
	{
		LOG.info("updateOrderPaymentStatus");
		final PaymentInfoModel paymentInfoModel = cartModel.getPaymentInfo();
		final List<PaymentReceiptModel> receiptModels = new ArrayList<>();
		final List<PaymentReceiptModel> receiptStatusModels = new ArrayList<>();
		paymentInfoModel.setReceipt(
				paymentInfoModel.getReceipt() != null && !paymentInfoModel.getReceipt().isEmpty() ? paymentInfoModel.getReceipt()
						: receiptModels);
		paymentInfoModel
				.setReceiptStatus(paymentInfoModel.getReceiptStatus() != null && !paymentInfoModel.getReceiptStatus().isEmpty()
						? paymentInfoModel.getReceiptStatus()
						: receiptStatusModels);
		modelService.save(paymentInfoModel);

		//		if (ccresponse != null && ccresponse.getPayments() != null)
		if (ccresponse != null)
		{
			LOG.info("ccresponse.getPayments() != null");

			final int items = 0;

			if (CollectionUtils.isNotEmpty(paymentInfoModel.getReceipt()))
			{
				LOG.info("Inicio paymentInfoModel.getReceipt() no es vacio size [" + paymentInfoModel.getReceipt().size() + "]");
				for (final PaymentReceiptModel paymentReceiptModel : paymentInfoModel.getReceipt())
				{
					LOG.info("Receipt Risk =" + paymentReceiptModel.getRisk());
					LOG.info("Receipt Status =" + paymentReceiptModel.getStatus());
					LOG.info("Receipt PaymentId =" + paymentReceiptModel.getPaymentId());
				}
			}

			for (final PaymentReceiptModel paymentReceiptModel : paymentInfoModel.getReceiptStatus())
			{
				paymentReceiptModel.setValidatedPayment3Ds(Boolean.FALSE);
				modelService.save(paymentReceiptModel);
			}


			//			setPayments(cartModel, ccresponse, receiptModels);
			cartModel.setPaymentStatus(PaymentStatus.NOTPAID);
			cartModel.setStatusConfirmatePayment(true);
			cartModel.setStatus(OrderStatus.PAYMENT_ON_VALIDATION);
			paymentInfoModel.setReceipt(receiptModels);
			//			if (isMsi && ccresponse.getPayments().size() > 1)
			//			{
			//				if (receiptModels.size() == 1)
			//				{
			//					//Se ejecuto pago parcial, hacer rollback y quitar "sendErpModel" con recargas
			//					runRollbackForPayments(cartModel, true);
			//				}
			//				else if (receiptModels.size() == 2)
			//				{
			//					//Se ejecuto pago completo, hacer rollback y quitar "sendErpModel" sin recargas
			//					runRollbackForPayments(cartModel, false);
			//				}
			//			}
		}

		cartModel.setPaymentInfo(paymentInfoModel);

		LOG.info("modelService.saveAll(paymentInfoModel, order)");

		modelService.saveAll(paymentInfoModel, cartModel);
		modelService.refresh(cartModel);

		if (CollectionUtils.isNotEmpty(paymentInfoModel.getReceipt()))
		{
			LOG.info("Fin paymentInfoModel.getReceipt() no es vacio size [" + paymentInfoModel.getReceipt().size() + "]");
			for (final PaymentReceiptModel paymentReceiptModel : paymentInfoModel.getReceipt())
			{
				LOG.info("Receipt Risk =" + paymentReceiptModel.getRisk());
				LOG.info("Receipt Status =" + paymentReceiptModel.getStatus());
				LOG.info("Receipt PaymentId =" + paymentReceiptModel.getPaymentId());
			}
		}

	}

	private CardConfirmRequest createRequestCardConfirm(final CartModel cartModel, final PaymentInfoModel paymentInfoModel)
	{
		final CardConfirmRequest ccrequest = new CardConfirmRequest();
		ccrequest.setOrderId(paymentInfoModel.getOrderIdWs());
		ccrequest.setSessionId(cartModel.getSessionIdWs());

		if (paymentInfoModel instanceof CreditCardPaymentInfoModel)
		{
			final CreditCardPaymentInfoModel creditCardPaymentInfoModel = (CreditCardPaymentInfoModel) paymentInfoModel;
			ccrequest.setReturn3DS(creditCardPaymentInfoModel.getUrl3ds());
		}

		if (paymentInfoModel instanceof CreditCardPaymentInfoModel)
		{
			final String cvv = ((CreditCardPaymentInfoModel) paymentInfoModel).getCvv();
			ccrequest.setCvv(cvv);
		}

		final FingerPrint fingerPrint = new FingerPrint();

		if (paymentInfoModel.getFingerprint() != null)
		{
			fingerPrint.setOrganizationId(paymentInfoModel.getFingerprint().getOrganizationId());
			fingerPrint.setSessionId(paymentInfoModel.getFingerprint().getSessionId());
			fingerPrint.setWebSession(paymentInfoModel.getFingerprint().getWebSession());
		}

		final CybersourceKeys cybersourceKeysModel = new CybersourceKeys();
		if (paymentInfoModel.getCybersourceKeys() != null)
		{
			cybersourceKeysModel.setSessionId(paymentInfoModel.getCybersourceKeys().getSessionId());
			cybersourceKeysModel.setMerchanId(paymentInfoModel.getCybersourceKeys().getMerchanId());
			cybersourceKeysModel.setOrgId(paymentInfoModel.getCybersourceKeys().getOrgId());
		}

		ccrequest.setFingerprint(fingerPrint);
		//ccrequest.setCybersourceKeys(cybersourceKeysModel);
		ccrequest.setCybersourceSession(paymentInfoModel.getCybersourceKeys().getSessionId());
		return ccrequest;
	}

	private void saveCard(final CartModel cartModel, final PaymentInfoModel paymentInfoModel)
	{
		if ((paymentInfoModel instanceof CreditCardPaymentInfoModel && paymentInfoModel.isSaved()))
		{
			final CreditCardPaymentInfoModel creditCardPaymentInfoModel = (CreditCardPaymentInfoModel) paymentInfoModel;
			final CardResponse cardResponse = createCard(creditCardPaymentInfoModel);


			creditCardPaymentInfoModel.setCvv("");
			creditCardPaymentInfoModel.setSaved(true);

			if (cardResponse.getStatusCode() != HttpStatus.OK.value())
			{
				LOG.info("No se creo la tarjeta [" + cardResponse.toString() + "]");
			}
			else
			{
				LOG.info("Se ejecuto el servicio correctamente");
				creditCardPaymentInfoModel.setTokenCardTelcel(cardResponse.getCardToken());
				cartModel.setPaymentInfo(creditCardPaymentInfoModel);

			}

			modelService.save(creditCardPaymentInfoModel);
			modelService.save(cartModel);

		}
	}

	private CardConsultRequest createRequestCardConsult(final CartModel cartModel, final Errors errors,
			final PaymentInfoModel paymentInfoModel, final CreditCardPaymentInfoModel creditCardPaymentInfoModel)
	{
		final CardConsultRequest cardConsultRequest = new CardConsultRequest();
		String paymentId = "";

		for (final PaymentReceiptModel paymentReceiptModel : creditCardPaymentInfoModel.getReceiptStatus())
		{
			if (id3dsParam.equalsIgnoreCase(paymentReceiptModel.getTransaction()))
			{
				paymentId = paymentReceiptModel.getPaymentId();
			}
		}
		if (StringUtils.isEmpty(paymentId) || StringUtils.isEmpty(id3dsParam)
				|| StringUtils.isEmpty(creditCardPaymentInfoModel.getOrderIdWs()))
		{

			LOG.info("No se ejecuto el servicio por falta de atributos cardConsultRequest");
			errors.reject("payment.execute.failed", new String[]
			{ cartModel.getCode() }, "Ocurrio un error al ejecutar pago  por falta de atributos cardConsultRequest {0} ");
			return null;

		}
		cardConsultRequest.setPaymentId(paymentId);
		cardConsultRequest.setAuthorization(id3dsParam);
		cardConsultRequest.setOrderId(creditCardPaymentInfoModel.getOrderIdWs());
		return cardConsultRequest;
	}

	private boolean callValidateCard(final ValidateTokenResponse validateTokenResponse, final CartModel cartModel,
			final boolean cardNew)
	{
		LOG.info("callValidateCard paymentCommerceService.cardValidate");

		CardValidateResponse cardValidate = null;
		try
		{
			cardValidate = validateCard(validateTokenResponse, cartModel, cardNew);
		}
		catch (final Exception e)
		{
			LOG.error("Error callValidateCard validateCard " + e.getMessage());
			LOG.error("Error callValidateCard validateCard getCause " + e.getCause().getMessage());
		}

		if (cardValidate.getStatusCode() != HttpStatus.OK.value())
		{
			LOG.info("validatePaymentCard callValidateCard cardValidate con estatus distinto al 200");
			try
			{
				cardValidate = validateCard(validateTokenResponse, cartModel, !cardNew);
			}
			catch (final Exception e)
			{
				LOG.error("Error callValidateCard validateCard " + e.getMessage());
				LOG.error("Error callValidateCard validateCard getCause " + e.getCause().getMessage());
			}

			if (cardValidate.getStatusCode() != HttpStatus.OK.value())
			{
				validateParameterNotNull(null, "Error al validar datos de tarjeta callValidateCard");
				return false;
			}

		}

		if (cardValidate.getStatusCode() == HttpStatus.OK.value())
		{
			LOG.info("validatePaymentCard callValidateCard cardValidate con estatus  200");

			cartModel.setStatusValidePayment(true);

			final PaymentInfoModel paymentInfoModel = cartModel.getPaymentInfo();
			if (paymentInfoModel != null)
			{
				paymentInfoModel.setTokenPayment(validateTokenResponse.getToken());
				paymentInfoModel.setOrderIdWs(cardValidate.getOrderId());
				paymentInfoModel.setSavedCard(true);

				final List<Payment> payments = cardValidate.getPayments();
				final List<PaymentsValidateModel> paymentsValidateModelList = new ArrayList<>();
				if (payments != null)
				{
					for (final Payment payment : payments)
					{
						final PaymentsValidateModel paymentsValidateModel = new PaymentsValidateModel();
						paymentsValidateModel.setPaymentId(payment.getPaymentId());
						paymentsValidateModel.setStatus(payment.getStatus());
						paymentsValidateModel.setAmount(payment.getAmount().toString());
						paymentsValidateModel.setMsi(payment.getMsi().toString());
						paymentsValidateModelList.add(paymentsValidateModel);
					}
					paymentInfoModel.setPaymentsValidate(paymentsValidateModelList);
					cartModel.setPaymentInfo(paymentInfoModel);
				}
				final FingerprintModel fingerprintModel = new FingerprintModel();
				if (cardValidate.getFingerprint() != null)
				{
					LOG.info("callValidateCard.getFingerprint() con datos");
					LOG.info("callValidateCard Fingerprint(getOrganizationId) =" + cardValidate.getFingerprint().getOrganizationId());
					LOG.info("callValidateCard Fingerprint(getSessionId) =" + cardValidate.getFingerprint().getSessionId());
					LOG.info("callValidateCard Fingerprint(getWebSession) =" + cardValidate.getFingerprint().getWebSession());
					fingerprintModel.setOrganizationId(cardValidate.getFingerprint().getOrganizationId());
					fingerprintModel.setSessionId(cardValidate.getFingerprint().getSessionId());
					fingerprintModel.setWebSession(cardValidate.getFingerprint().getWebSession());
					paymentInfoModel.setFingerprint(fingerprintModel);
					cartModel.setPaymentInfo(paymentInfoModel);

				}

				final CybersourceKeysModel cybersourceKeysModel = new CybersourceKeysModel();
				if (cardValidate.getCybersourceKeys() != null)
				{
					LOG.info("cardValidate.getCybersourceKeys() con datos");
					cybersourceKeysModel.setOrgId(cardValidate.getCybersourceKeys().getOrgId());
					cybersourceKeysModel.setMerchanId(cardValidate.getCybersourceKeys().getMerchanId());
					cybersourceKeysModel.setSessionId(cardValidate.getCybersourceKeys().getSessionId());
					paymentInfoModel.setCybersourceKeys(cybersourceKeysModel);
					cartModel.setPaymentInfo(paymentInfoModel);
				}

				final OpenpayKeysModel openpayKeysModel = new OpenpayKeysModel();
				if (cardValidate.getOpenpayKeys() != null)
				{
					openpayKeysModel.setApiKey(cardValidate.getOpenpayKeys().getApiKey());
					openpayKeysModel.setMerchantId(cardValidate.getOpenpayKeys().getMerchantId());
					paymentInfoModel.setOpenpayKeys(openpayKeysModel);
					cartModel.setPaymentInfo(paymentInfoModel);
				}
				modelService.saveAll(paymentInfoModel, cartModel);
				modelService.refresh(cartModel);
			}
		}

		return true;

	}

	private CardValidateResponse validateCard(final ValidateTokenResponse validateTokenResponse, final CartModel cart,
			final boolean cardNew) throws NoSuchAlgorithmException, KeyStoreException, IOException, KeyManagementException
	{
		LOG.info("TMAOrdersController validateCard");

		CardValidateResponse response = null;

		LOG.info("TMAOrdersController CART CODE: " + cart.getCode());
		final PaymentCommerceInfoData pci = paymentCommerceInfoCartConverter.convert(cart);
		final AddressModel deliveryAddress = cart.getDeliveryAddress();
		final CardValidateRequest request = new CardValidateRequest();

		final Order orderPC = new Order();

		final String postalCode = deliveryAddress.getPostalcode();
		final CodigosPostalesTelcelData codigosPostalesTelcelData = telcelCodigosPostalesService.getInfForZipcode(postalCode);

		if (deliveryAddress != null)
		{

			if (!Objects.isNull(codigosPostalesTelcelData) || !Objects.isNull(codigosPostalesTelcelData.getRegion()))
			{
				orderPC.setRegion(Integer.valueOf(codigosPostalesTelcelData.getRegion().getCode()));
			}
			orderPC.setCountry("México");
			orderPC.setCity(
					org.apache.commons.lang.StringUtils.isNotEmpty(deliveryAddress.getTown()) ? deliveryAddress.getTown() : "NA");
			orderPC.setState(Objects.nonNull(deliveryAddress.getRegion()) ? deliveryAddress.getRegion().getName() : "NA");
			orderPC.setAddress(deliveryAddress.getStreetname() + " " + deliveryAddress.getExternalNumber());
			orderPC.setPostalCode(
					org.apache.commons.lang.StringUtils.isNotEmpty(deliveryAddress.getPostalcode()) ? deliveryAddress.getPostalcode()
							: "NA");

		}
		orderPC.setEmail(pci.getOrder().getEmail());
		orderPC.setMdiKey("VENTA_EQUIPO");

		String salesForceId = org.apache.commons.lang.StringUtils.EMPTY;
		String salesForceAlphanumeric = org.apache.commons.lang.StringUtils.EMPTY;
		String typeUserCVTDAT = org.apache.commons.lang.StringUtils.EMPTY;
		String idUserCVTDAT = org.apache.commons.lang.StringUtils.EMPTY;
		if (!Objects.isNull(codigosPostalesTelcelData) || !Objects.isNull(codigosPostalesTelcelData.getRegion()))
		{
			final String region = codigosPostalesTelcelData.getRegion().getCode();
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

		if (org.apache.commons.lang.StringUtils.isNotEmpty(typeUserCVTDAT))
		{
			orderPC.setDistributionCenter(typeUserCVTDAT);
			orderPC.setSalesForce(salesForceAlphanumeric);
		}
		else
		{
			orderPC.setDistributionCenter("PREPAGO");
		}
		orderPC.setPhone(org.apache.commons.lang.StringUtils.isNotEmpty(pci.getOrder().getPhone()) ? pci.getOrder().getPhone()
				: deliveryAddress.getPhone1());
		orderPC.setFirstname(pci.getPayment().getFirstname());
		orderPC.setLastname(pci.getPayment().getLastname());
		orderPC.setTokenCard(pci.getPayment().getTokenCard());
		final Integer meses = pci.getPayment().getMsi() != null ? pci.getPayment().getMsi() : 0;
		orderPC.setLastDigits(pci.getPayment().getLastDigits());
		orderPC.setCommerceId(cart.getCode());
		orderPC.setSaveCard(false);
		orderPC.setNewCard(cardNew);
		orderPC.setCardType(pci.getPayment().getType());

		final RequestAddress requestAddress = new RequestAddress();

		if (deliveryAddress != null)
		{
			requestAddress.setAddress(
					deliveryAddress.getStreetname() + " " + deliveryAddress.getPostalcode() + " " + deliveryAddress.getTown());
			requestAddress.setCity(
					org.apache.commons.lang.StringUtils.isNotEmpty(deliveryAddress.getTown()) ? deliveryAddress.getTown() : "NA");
			requestAddress
					.setCountry(deliveryAddress.getCountry() != null
							? org.apache.commons.lang.StringUtils.isNotEmpty(deliveryAddress.getCountry().getName())
									? deliveryAddress.getCountry().getName()
									: "NA"
							: "NA");
			requestAddress.setPhone(
					org.apache.commons.lang.StringUtils.isNotEmpty(deliveryAddress.getPhone1()) ? deliveryAddress.getPhone1() : "NA");
			requestAddress.setFirstname(
					org.apache.commons.lang.StringUtils.isNotEmpty(deliveryAddress.getFirstname()) ? deliveryAddress.getFirstname()
							: "NA");
			requestAddress.setLastname(
					org.apache.commons.lang.StringUtils.isNotEmpty(deliveryAddress.getLastname()) ? deliveryAddress.getLastname()
							: "NA");
			requestAddress.setPostalCode(
					org.apache.commons.lang.StringUtils.isNotEmpty(deliveryAddress.getPostalcode()) ? deliveryAddress.getPostalcode()
							: "NA");
		}
		else
		{
			LOG.error("No cuenta con direccion de envio");
		}

		orderPC.setShippingAddress(requestAddress);


		final List<Payment> payments = new ArrayList<>();

		//mandar mas de un payment
		if (meses > 0)
		{
			paymentsWithPromotion(cart, pci, payments);
		}
		else
		{
			paymentsWithoutPromotion(cart, pci, payments);
		}

		request.setOrder(orderPC);
		request.setPayments(payments);
		final Gson gson = new Gson();
		LOG.info(gson.toJson(request));
		response = paymentCommerceService.cardValidate(request, validateTokenResponse.getToken());
		return response;
	}

	private String getIdUserCVTDAT()
	{
		String idUserCVTDAT = sessionService.getAttribute("idUserCVTDAT");
		if (Strings.isNullOrEmpty(idUserCVTDAT))
		{
			final RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
			idUserCVTDAT = Objects.nonNull(requestAttributes)
					? telcelUtil.getCookieByRequest(((ServletRequestAttributes) requestAttributes).getRequest(), "idUserCVTDAT")
					: org.apache.commons.lang.StringUtils.EMPTY;
			idUserCVTDAT = idUserCVTDAT == null ? "" : idUserCVTDAT;
			LOG.info(String.format("Debug - cookie idUserCVTDAT: %s", idUserCVTDAT));
		}
		else
		{
			LOG.info("Se obtiene el valor de typeUserCVTDAT desde sessionService");
		}
		return idUserCVTDAT;
	}

	private String getTypeUserCVTDAT()
	{
		String typeUserCVTDAT = sessionService.getAttribute("typeUserCVTDAT");
		if (Strings.isNullOrEmpty(typeUserCVTDAT))
		{
			final RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
			typeUserCVTDAT = Objects.nonNull(requestAttributes)
					? telcelUtil.getCookieByRequest(((ServletRequestAttributes) requestAttributes).getRequest(), "typeUserCVTDAT")
					: org.apache.commons.lang.StringUtils.EMPTY;
			typeUserCVTDAT = typeUserCVTDAT == null ? "" : typeUserCVTDAT;
			LOG.info(String.format("Debug - cookie typeUserCVTDAT: %s", typeUserCVTDAT));
		}
		else
		{
			LOG.info("Se obtiene el valor de typeUserCVTDAT desde sessionService");
		}
		return typeUserCVTDAT;
	}

	private void paymentsWithoutPromotion(final CartModel cart, final PaymentCommerceInfoData pci, final List<Payment> payments)
	{
		final Payment payment = new Payment();
		payment.setAmount(pci.getPayment().getAmount());
		payment.setMsi(pci.getPayment().getMsi() != null ? pci.getPayment().getMsi() : 0);
		payment.setPriority(1);
		final List<Item> items = new ArrayList<>();
		for (final ItemPCData itempc : pci.getPayment().getItems())
		{
			final Item item = new Item();
			item.setSku(itempc.getSku());
			item.setDescription(itempc.getDescription());
			item.setPrice(itempc.getPrice());
			item.setModel(itempc.getModel());
			item.setColor(org.apache.commons.lang.StringUtils.isNotEmpty(itempc.getColor()) ? itempc.getColor() : "NA");
			items.add(item);
		}

		for (final ItemPCData itempc : pci.getPayment().getItemsSa())
		{
			final Item item = new Item();
			item.setSku(itempc.getSku());
			item.setDescription(itempc.getDescription());
			item.setPrice(itempc.getPrice());
			item.setModel(itempc.getModel());
			item.setColor(org.apache.commons.lang.StringUtils.isNotEmpty(itempc.getColor()) ? itempc.getColor() : "NA");
			items.add(item);
		}

		payment.setItems(items);

		//Costo de envio
		if (cart.getDeliveryCost() != null && cart.getDeliveryCost() > 0)
		{
			final Item item = new Item();
			item.setSku("07");
			item.setDescription("Shipping cost");
			item.setPrice(cart.getDeliveryCost());
			item.setModel("NA");
			item.setColor("NA");
			payment.getItems().add(item);
		}
		payments.add(payment);
	}

	private void paymentsWithPromotion(final CartModel cart, final PaymentCommerceInfoData pci, final List<Payment> payments)
	{
		final Payment paymentProducts = new Payment();
		paymentProducts.setMsi(pci.getPayment().getMsi() != null ? pci.getPayment().getMsi() : 0);
		final List<Item> items = new ArrayList<>();
		double pricePaymentProduct = 0;
		for (final ItemPCData itempc : pci.getPayment().getItems())
		{
			final Item item = new Item();
			item.setSku(itempc.getSku());
			item.setDescription(itempc.getDescription());
			pricePaymentProduct = pricePaymentProduct + itempc.getPrice();
			item.setPrice(itempc.getPrice());
			item.setModel(itempc.getModel());
			item.setColor(org.apache.commons.lang.StringUtils.isNotEmpty(itempc.getColor()) ? itempc.getColor() : "NA");
			items.add(item);
			paymentProducts.setItems(items);
		}

		//Costo de envio
		if (cart.getDeliveryCost() != null && cart.getDeliveryCost() > 0)
		{
			final Item item = new Item();
			item.setSku("07");
			item.setDescription("Shipping cost");
			pricePaymentProduct = pricePaymentProduct + cart.getDeliveryCost();
			item.setPrice(cart.getDeliveryCost());
			//item.setPrice(200.0);
			item.setModel("NA");
			item.setColor("NA");
			paymentProducts.getItems().add(item);
		}
		paymentProducts.setAmount(pricePaymentProduct);
		paymentProducts.setPriority(1);
		payments.add(paymentProducts);

		final Payment paymentSinPromo = new Payment();

		if (pci.getPayment().getItemsSa() != null && pci.getPayment().getItemsSa().size() > 0)
		{
			paymentSinPromo.setMsi(0);
			paymentSinPromo.setPriority(2);
			final List<Item> itemsSinPromo = new ArrayList<>();
			paymentSinPromo.setItems(itemsSinPromo);
			double pricePaymentSinPromo = 0;

			for (final ItemPCData itempc : pci.getPayment().getItemsSa())
			{
				final Item item = new Item();
				item.setSku(itempc.getSku());
				item.setDescription(itempc.getDescription());
				pricePaymentSinPromo = pricePaymentSinPromo + itempc.getPrice();
				item.setPrice(itempc.getPrice());
				item.setModel(itempc.getModel());
				item.setColor(org.apache.commons.lang.StringUtils.isNotEmpty(itempc.getColor()) ? itempc.getColor() : "NA");
				itemsSinPromo.add(item);
				paymentSinPromo.setItems(itemsSinPromo);
			}
			paymentSinPromo.setAmount(pricePaymentSinPromo);
			payments.add(paymentSinPromo);
		}

	}

	private CardResponse createCard(final CreditCardPaymentInfoModel ccPaymentInfoModel)
	{
		LOG.info("createCard");
		final CustomerModel currentCustomer = (CustomerModel) ccPaymentInfoModel.getUser();
		final String emailUid = CustomerType.GUEST.equals(currentCustomer.getType())
				? org.apache.commons.lang.StringUtils.substringAfter(currentCustomer.getUid(), "|")
				: telcelUtil.getEmailRegisterForToken(currentCustomer.getUid(), currentCustomer.getContactEmail());

		final AddCardRequest addCardRequest = new AddCardRequest();


		final ValidateTokenResponse validateTokenResponse = getToken(emailUid);

		addCardRequest.setCardToken(ccPaymentInfoModel.getTokenCardTelcel());
		addCardRequest.setAccount(emailUid);
		addCardRequest.setFirstname(ccPaymentInfoModel.getHolderName());
		addCardRequest.setLastname(
				StringUtils.isNotEmpty(ccPaymentInfoModel.getHolderLastName()) ? ccPaymentInfoModel.getHolderLastName() : "NA");
		addCardRequest.setPostalCode(ccPaymentInfoModel.getZipCode());

		if (ccPaymentInfoModel.getType() != null && MASTER.equalsIgnoreCase(ccPaymentInfoModel.getType().getCode()))
		{
			addCardRequest.setCardType(MASTERCARD);
		}
		else
		{
			addCardRequest.setCardType(ccPaymentInfoModel.getType() != null ? ccPaymentInfoModel.getType().getCode().toUpperCase()
					: org.apache.commons.lang.StringUtils.EMPTY);
		}

		String lastDig = ccPaymentInfoModel.getNumber();
		lastDig = lastDig.substring(lastDig.length() - 4, lastDig.length());

		addCardRequest.setLastDigits(lastDig);

		CardResponse cardResponse = null;
		try
		{
			cardResponse = paymentCommerceService.addCard(addCardRequest, validateTokenResponse.getToken());
		}
		catch (final Exception e)
		{
			LOG.error(e.getMessage());
		}

		return cardResponse;

	}

	private ValidateTokenResponse getToken(final String uid)
	{
		LOG.info("getToken [" + uid + "]");
		ValidateTokenResponse validateTokenResponse = null;
		try
		{
			validateTokenResponse = paymentCommerceService.getTokenByUid(uid);
		}
		catch (final Exception e)
		{
			LOG.error("Error al obtener token =" + e.getLocalizedMessage());
		}

		LOG.info("getToken [" + uid + "] token [" + validateTokenResponse.getToken() + "]");

		return validateTokenResponse;
	}

	private void updateOrderPaymentStatus(final CartModel cartModel, final CardConfirmResponse ccresponse, final Errors errors,
			final boolean isMsi)
	{
		LOG.info("updateOrderPaymentStatus");
		final PaymentInfoModel paymentInfoModel = cartModel.getPaymentInfo();
		final List<PaymentReceiptModel> receiptModels = new ArrayList<>();
		final List<PaymentReceiptModel> receiptStatusModels = new ArrayList<>();
		paymentInfoModel.setReceipt(receiptModels);
		paymentInfoModel.setReceiptStatus(receiptStatusModels);
		modelService.save(paymentInfoModel);

		if (ccresponse != null && ccresponse.getPayments() != null)
		{
			LOG.info("ccresponse.getPayments() != null");

			int items = 0;
			final List<PaymentReceiptModel> receiptStatusNew = setReceiptStatus(cartModel, ccresponse, paymentInfoModel,
					receiptModels);
			if (CollectionUtils.isNotEmpty(paymentInfoModel.getReceipt()))
			{
				LOG.info("Inicio paymentInfoModel.getReceipt() no es vacio size [" + paymentInfoModel.getReceipt().size() + "]");
				for (final PaymentReceiptModel paymentReceiptModel : paymentInfoModel.getReceipt())
				{
					LOG.info("Receipt Risk =" + paymentReceiptModel.getRisk());
					LOG.info("Receipt Status =" + paymentReceiptModel.getStatus());
					LOG.info("Receipt PaymentId =" + paymentReceiptModel.getPaymentId());
				}
			}

			Integer numberOfPaymentComplete = 0;
			for (final Payment payment : ccresponse.getPayments())
			{

				if (payment.getReceipt() != null && "REVIEW".equalsIgnoreCase(payment.getReceipt().getStatus()))
				{
					LOG.info("payment.getReceipt() != null && REVIEW.equalsIgnoreCase(payment.getReceipt().getStatus())");
					LOG.info(" paymentId [" + payment.getPaymentId() + "]");
					this.error3ds = StringUtils.isNotBlank(payment.getReceipt().getUrl3DS()) ? payment.getReceipt().getUrl3DS() : null;

//					remover la recarga porque se detecto el estatus de review
					if(ccresponse.getPayments().size() > 1 && "DISCARDED".equalsIgnoreCase(ccresponse.getPayments().get(1).getReceipt().getStatus())){
						//Quitar SA
						final List<Item> itemList = ccresponse.getPayments().get(1).getItems();

						for (final Item item : itemList)
						{
							for (final AbstractOrderEntryModel entry : cartModel.getEntries())
							{
								final List<AdditionalServiceEntryModel> additionalServiceEntryModelList = entry
										.getAdditionalServiceEntries();
								for (final AdditionalServiceEntryModel additionalServiceEntry : additionalServiceEntryModelList)
								{
									if (additionalServiceEntry.getAdditionalServiceProduct().getSku().equalsIgnoreCase(item.getSku()))
									{
										LOG.info("Se quita SA ["+item.getSku()+"]");
										additionalServiceEntry.setRejected(true);
										additionalServiceEntry.setDeniedPayment(true);
										modelService.save(additionalServiceEntry);

										LOG.info("Se ejecuto pago parcial, hacer rollback y quitar \"sendErpModel\" con recargas");
										runRollbackForPayments(cartModel, true);
										//modelService.remove(additionalServiceEntry);
									}
								}
							}
						}
					}


					if (this.error3ds == null)
					{
						errors.reject("payment.execute.failed", new String[]
						{ cartModel.getCode() }, "Ocurrio un obtener el 3ds ");
					} // setPayments(cartModel, ccresponse, receiptModels);

					return;
				}

				if (payment.getReceipt() != null && "COMPLETE".equalsIgnoreCase(payment.getReceipt().getStatus()))
				{
					LOG.info(
							"payment.getReceipt() != null && \"COMPLETE\".equalsIgnoreCase(payment.getReceipt().getStatus()) numberOfPaymentComplete++ ");
					numberOfPaymentComplete++;
				}

				LOG.info("numberOfPaymentComplete [" + numberOfPaymentComplete + "]");

				if ((payment.getReceipt() != null && !"SUCCESS".equalsIgnoreCase(payment.getReceipt().getStatus())
						&& !"COMPLETE".equalsIgnoreCase(payment.getReceipt().getStatus()) && numberOfPaymentComplete == 0))
				{

					LOG.info(
							"payment.getReceipt() != null && \"ERROR\".equalsIgnoreCase(payment.getReceipt().getStatus()) && numberOfPaymentComplete==0\n"
									+ "\t\t\t\t\t\t|| payment.getReceipt() != null && \"REVIEW\".equalsIgnoreCase(payment.getReceipt().getStatus()) && numberOfPaymentComplete==0");

					boolean paymentRemove = false;
					paymentRemove = errorReview(cartModel, ccresponse, payment, paymentRemove);
					if (!paymentRemove)
					{
						LOG.info("payment.getReceipt() != null && ERROR.equalsIgnoreCase(payment.getReceipt().getStatus() ");
						LOG.info(" paymentId [" + payment.getPaymentId() + "]");
						errors.reject("payment.execute.failed", new String[]
						{ cartModel.getCode() }, "Ocurrio un error al ejecutar pago {0} ");
						return;
					}
				}
				if (payment.getReceipt() != null && "DENIED".equalsIgnoreCase(payment.getReceipt().getStatus()))
				{
					LOG.info("ppayment.getReceipt() != null && DENIED.equalsIgnoreCase(payment.getReceipt().getStatus())");
					LOG.info(" paymentId [" + payment.getPaymentId() + "]");
					items++;
				}

			}

			LOG.info("fuera de for numberOfPaymentComplete [" + numberOfPaymentComplete + "]");

			if (CollectionUtils.isNotEmpty(receiptStatusNew))
			{
				for (final PaymentReceiptModel paymentReceiptModel : receiptStatusNew)
				{
					if (!"COMPLETE".equalsIgnoreCase(paymentReceiptModel.getStatus()) && numberOfPaymentComplete == 0)
					{
						LOG.info("payment.getReceipt() != null && ERROR.equalsIgnoreCase(payment.getReceipt().getStatus() ");
						LOG.info(" paymentId [" + paymentReceiptModel.getPaymentId() + "]");
						errors.reject("payment.execute.failed", new String[]
						{ cartModel.getCode() }, "Ocurrio un error al ejecutar pago {0} ");
						return;
					}
				}
			}


			if (items == ccresponse.getPayments().size())
			{
				errors.reject("payment.execute.failed", new String[]
				{ cartModel.getCode() }, "No se permitio el pago, validar credito ");
				return;

			}

			setPayments(cartModel, ccresponse, receiptModels);
			cartModel.setPaymentStatus(PaymentStatus.PAID);
			cartModel.setStatusConfirmatePayment(true);
			paymentInfoModel.setReceipt(receiptModels);

			cartModel.setPaymentInfo(paymentInfoModel);


			if (isMsi && ccresponse.getPayments().size() > 1)
			{
				if (numberOfPaymentComplete == 1)
				{
					//Se ejecuto pago parcial, hacer rollback y quitar "sendErpModel" con recargas
					LOG.info("Se ejecuto pago parcial, hacer rollback y quitar \"sendErpModel\" con recargas");
					runRollbackForPayments(cartModel, true);
				}
				else if (numberOfPaymentComplete == 2)
				{
					//Se ejecuto pago completo, hacer rollback y quitar "sendErpModel" sin recargas
					LOG.info("Se ejecuto pago completo, hacer rollback y quitar \"sendErpModel\" sin recargas");
					runRollbackForPayments(cartModel, false);
				}
			}
		}

		//		cartModel.setPaymentInfo(paymentInfoModel);

		LOG.info("modelService.saveAll(paymentInfoModel, order)");

		modelService.saveAll(paymentInfoModel, cartModel);
		refreshEntries(cartModel);
		modelService.refresh(cartModel);

		if (CollectionUtils.isNotEmpty(paymentInfoModel.getReceipt()))
		{
			LOG.info("Fin paymentInfoModel.getReceipt() no es vacio size [" + paymentInfoModel.getReceipt().size() + "]");
			for (final PaymentReceiptModel paymentReceiptModel : paymentInfoModel.getReceipt())
			{
				LOG.info("Receipt Risk =" + paymentReceiptModel.getRisk());
				LOG.info("Receipt Status =" + paymentReceiptModel.getStatus());
				LOG.info("Receipt PaymentId =" + paymentReceiptModel.getPaymentId());
			}
		}

	}


	private List<PaymentReceiptModel> setReceiptStatus3Ds(final CartModel cartModel, final CardConfirmResponse ccresponse,
			final PaymentInfoModel paymentInfoModel, final List<PaymentReceiptModel> receiptModels)
	{
		final List<PaymentReceiptModel> receiptStatusNew = new ArrayList<>();
		setPaymentsResponseService(cartModel, ccresponse, receiptModels);
		if (paymentInfoModel.getReceiptStatus().size() > 0)
		{
			for (PaymentReceiptModel paymentReceiptModel : paymentInfoModel.getReceiptStatus())
			{
				for (final PaymentReceiptModel paymentR : receiptModels)
				{
					if (paymentReceiptModel.getPaymentId().equalsIgnoreCase(paymentR.getPaymentId()))
					{
						paymentReceiptModel = paymentR;
					}
				}
				receiptStatusNew.add(paymentReceiptModel);
			}
			paymentInfoModel.setReceiptStatus(receiptStatusNew);
			paymentInfoModel.setReceipt(receiptStatusNew);
		}
		else
		{
			paymentInfoModel.setReceiptStatus(receiptModels);
		}
		modelService.save(paymentInfoModel);
		return receiptStatusNew;

	}

	private boolean errorReview(final CartModel cartModel, final CardConfirmResponse ccresponse, final Payment payment,
			boolean paymentRemove)
	{
		LOG.info("método error review");
		if ("REVIEW".equalsIgnoreCase(payment.getReceipt().getStatus()) && ccresponse.getPayments().size() > 1)
		{
			for (final Payment paymentReview : ccresponse.getPayments())
			{
				if (paymentReview.getReceipt() != null && paymentReview.getReceipt().getStatus().equalsIgnoreCase("SUCCESS"))
				{
					LOG.info(" paymentId Success [" + paymentReview.getPaymentId() + "]");
					paymentRemove = true;
				}
			}
			if (paymentRemove)
			{
				for (final Payment paymentReview : ccresponse.getPayments())
				{
					if (paymentReview.getReceipt() != null && !paymentReview.getReceipt().getStatus().equalsIgnoreCase("SUCCESS"))
					{
						LOG.info(" paymentId Review [" + paymentReview.getPaymentId() + "]");
						final List<Item> itemList = payment.getItems();
						//Quitar items
						for (final Item item : itemList)
						{
							for (final AbstractOrderEntryModel entry : cartModel.getEntries())
							{
								if (entry.getProduct().getSku().equalsIgnoreCase(item.getSku()))
								{
									modelService.remove(entry);
								}
							}
						}
						//Quitar SA
						for (final Item item : itemList)
						{
							for (final AbstractOrderEntryModel entry : cartModel.getEntries())
							{
								final List<AdditionalServiceEntryModel> additionalServiceEntryModelList = entry
										.getAdditionalServiceEntries();
								for (final AdditionalServiceEntryModel additionalServiceEntry : additionalServiceEntryModelList)
								{
									if (additionalServiceEntry.getAdditionalServiceProduct().getSku().equalsIgnoreCase(item.getSku()))
									{
										additionalServiceEntry.setRejected(true);
										additionalServiceEntry.setDeniedPayment(true);
										modelService.save(additionalServiceEntry);
										//modelService.remove(additionalServiceEntry);
									}
								}
							}
						}
					}
				}
			}
		}
		return paymentRemove;
	}

	private List<PaymentReceiptModel> setReceiptStatus(final CartModel cartModel, final CardConfirmResponse ccresponse,
			final PaymentInfoModel paymentInfoModel, final List<PaymentReceiptModel> receiptModels)
	{
		final List<PaymentReceiptModel> receiptStatusNew = new ArrayList<>();
		setPaymentsResponseService(cartModel, ccresponse, receiptModels);
		LOG.info("paymentInfoModel.getReceiptStatus().size() : " + paymentInfoModel.getReceiptStatus().size());
		if (paymentInfoModel.getReceiptStatus().size() > 0)
		{
			for (PaymentReceiptModel paymentReceiptModel : paymentInfoModel.getReceiptStatus())
			{
				for (final PaymentReceiptModel paymentR : receiptModels)
				{
					if (paymentReceiptModel.getPaymentId().equalsIgnoreCase(paymentR.getPaymentId()))
					{
						paymentReceiptModel = paymentR;
					}
				}
				receiptStatusNew.add(paymentReceiptModel);
			}
			LOG.info("receiptStatusNew : " + receiptStatusNew.size());
			paymentInfoModel.setReceiptStatus(receiptStatusNew);
			paymentInfoModel.setReceipt(receiptStatusNew);
		}
		else
		{
			paymentInfoModel.setReceiptStatus(receiptModels);
		}
		modelService.save(paymentInfoModel);
		return receiptStatusNew;

	}

	private void setPayments(final CartModel cartModel, final CardConfirmResponse ccresponse,
			final List<PaymentReceiptModel> receiptModels)
	{
		int i = 0;
		for (final Payment payment : ccresponse.getPayments())
		{
			final PaymentReceiptModel paymentReceiptModel = new PaymentReceiptModel();
			i = setPayment(cartModel, receiptModels, i, payment, paymentReceiptModel);
		}
	}

	private void setPaymentsResponseService(final CartModel cartModel, final CardConfirmResponse ccresponse,
			final List<PaymentReceiptModel> receiptModels)
	{
		LOG.info("TmaOrdersController.setPaymentsResponseService");
		int i = 0;
		LOG.info("ccresponse.getPayments() : " + ccresponse.getPayments().size());
		for (final Payment payment : ccresponse.getPayments())
		{
			final PaymentReceiptModel paymentReceiptModel = new PaymentReceiptModel();
			i = setPaymentResponseService(cartModel, receiptModels, i, payment, paymentReceiptModel);
		}

	}


	private int setPaymentResponseService(final CartModel cartModel, final List<PaymentReceiptModel> receiptModels, int i,
			final Payment payment, final PaymentReceiptModel paymentReceiptModel)
	{
		if (payment.getReceipt() != null)
		{
			paymentReceiptModel.setIdPaymentReceipt(Integer.valueOf(cartModel.getCode()));
			paymentReceiptModel.setKeyReceipt(cartModel.getCode() + "-" + i);
			paymentReceiptModel.setPaymentId(payment.getPaymentId());
			paymentReceiptModel.setTransaction(payment.getReceipt().getTransaction());
			paymentReceiptModel.setAuthorization(payment.getReceipt().getAuthorization());
			paymentReceiptModel.setCode(payment.getReceipt().getCode());
			paymentReceiptModel.setRisk(payment.getReceipt().getRisk());
			paymentReceiptModel.setStatus(payment.getReceipt().getStatus());
			paymentReceiptModel.setUrl3DS(payment.getReceipt().getUrl3DS());
			paymentReceiptModel.setType(payment.getType());
			paymentReceiptModel.setProvider(payment.getReceipt().getProvider());
			paymentReceiptModel.setMembership(payment.getReceipt().getMembership());
			paymentReceiptModel.setBank(payment.getReceipt().getBank());
			paymentReceiptModel.setAttempts(payment.getReceipt().getAttempts());
			paymentReceiptModel.setLastDigits(payment.getLastDigits());
			final List<Item> itemList = payment.getItems();
			final List<ItemPaymentModel> itemPaymentModelList = new ArrayList<>();

			for (final Item item : itemList)
			{
				final ItemPaymentModel itemPaymentModel = new ItemPaymentModel();
				itemPaymentModel.setPaymentId(payment.getPaymentId());
				itemPaymentModel.setGetId(payment.getPaymentId());
				itemPaymentModel.setSku(item.getSku());
				itemPaymentModel.setDescription(item.getDescription());
				itemPaymentModel.setModel(item.getModel());
				itemPaymentModel.setColor(StringUtils.isNotEmpty(item.getColor()) ? item.getColor() : "NA");
				itemPaymentModel.setPrice(item.getPrice());
				itemPaymentModelList.add(itemPaymentModel);
			}

			paymentReceiptModel.setItemsPayment(itemPaymentModelList);

			receiptModels.add(paymentReceiptModel);
			i++;
		}
		else
		{
			LOG.error("setPaymentResponseService updateOrderPaymentStatus status receipt [" + payment.getReceipt() != null
					? payment.getReceipt().getStatus()
					: StringUtils.EMPTY + "]");
		}
		return i;
	}

	private int setPayment(final CartModel cartModel, final List<PaymentReceiptModel> receiptModels, int i, final Payment payment,
			final PaymentReceiptModel paymentReceiptModel)
	{
		if (payment.getReceipt() != null && "SUCCESS".equalsIgnoreCase(payment.getReceipt().getStatus())
				|| "COMPLETE".equalsIgnoreCase(payment.getReceipt().getStatus())
				|| "ERROR".equalsIgnoreCase(payment.getReceipt().getStatus())
				|| "DENIED".equalsIgnoreCase(payment.getReceipt().getStatus())
				|| "REVIEW".equalsIgnoreCase(payment.getReceipt().getStatus()))
		{


			paymentReceiptModel.setIdPaymentReceipt(Integer.valueOf(cartModel.getCode()));
			paymentReceiptModel.setKeyReceipt(cartModel.getCode() + "-" + i);
			paymentReceiptModel.setPaymentId(payment.getPaymentId());
			paymentReceiptModel.setTransaction(payment.getReceipt().getTransaction());
			paymentReceiptModel.setAuthorization(payment.getReceipt().getAuthorization());
			paymentReceiptModel.setCode(payment.getReceipt().getCode());
			paymentReceiptModel.setRisk(payment.getReceipt().getRisk());
			paymentReceiptModel.setStatus(payment.getReceipt().getStatus());
			paymentReceiptModel.setUrl3DS(payment.getReceipt().getUrl3DS());
			paymentReceiptModel.setType(payment.getType());
			paymentReceiptModel.setProvider(payment.getReceipt().getProvider());
			paymentReceiptModel.setMembership(payment.getReceipt().getMembership());
			paymentReceiptModel.setBank(payment.getReceipt().getBank());
			paymentReceiptModel.setAttempts(payment.getReceipt().getAttempts());
			paymentReceiptModel.setLastDigits(payment.getLastDigits());
			final List<Item> itemList = payment.getItems();
			final List<ItemPaymentModel> itemPaymentModelList = new ArrayList<>();

			for (final Item item : itemList)
			{
				final ItemPaymentModel itemPaymentModel = new ItemPaymentModel();
				itemPaymentModel.setPaymentId(payment.getPaymentId());
				itemPaymentModel.setGetId(payment.getPaymentId());
				itemPaymentModel.setSku(item.getSku());
				itemPaymentModel.setDescription(item.getDescription());
				itemPaymentModel.setModel(item.getModel());
				itemPaymentModel.setColor(StringUtils.isNotEmpty(item.getColor()) ? item.getColor() : "NA");
				itemPaymentModel.setPrice(item.getPrice());

				for (final AbstractOrderEntryModel entry : cartModel.getEntries())
				{
					if (entry.getProduct().getSku().equalsIgnoreCase(item.getSku()))
					{
						itemPaymentModel.setSkuFather(entry.getProduct().getSku());
					}
				}


				for (final AbstractOrderEntryModel entry : cartModel.getEntries())
				{
					final List<AdditionalServiceEntryModel> additionalServiceEntryModelList = entry.getAdditionalServiceEntries();
					for (final AdditionalServiceEntryModel additionalServiceEntry : additionalServiceEntryModelList)
					{
						if (additionalServiceEntry.getAdditionalServiceProduct().getSku().equalsIgnoreCase(item.getSku()))
						{
							itemPaymentModel.setSkuFather(entry.getProduct().getSku());
						}
					}
				}


				itemPaymentModelList.add(itemPaymentModel);
			}


			if ("ERROR".equalsIgnoreCase(payment.getReceipt().getStatus())
					|| "DENIED".equalsIgnoreCase(payment.getReceipt().getStatus())
					|| "DISCARDED".equalsIgnoreCase(payment.getReceipt().getStatus())
					|| "REVIEW".equalsIgnoreCase(payment.getReceipt().getStatus()))
			{
				//Quitar items
				for (final Item item : itemList)
				{
					for (final AbstractOrderEntryModel entry : cartModel.getEntries())
					{
						if (entry.getProduct().getSku().equalsIgnoreCase(item.getSku()))
						{
							modelService.remove(entry);
						}
					}
				}

				//Quitar SA

				for (final Item item : itemList)
				{
					for (final AbstractOrderEntryModel entry : cartModel.getEntries())
					{
						final List<AdditionalServiceEntryModel> additionalServiceEntryModelList = entry.getAdditionalServiceEntries();
						for (final AdditionalServiceEntryModel additionalServiceEntry : additionalServiceEntryModelList)
						{
							if (additionalServiceEntry.getAdditionalServiceProduct().getSku().equalsIgnoreCase(item.getSku()))
							{
								additionalServiceEntry.setRejected(true);
								additionalServiceEntry.setDeniedPayment(true);
								modelService.save(additionalServiceEntry);
							}
						}
					}
				}
			}


			paymentReceiptModel.setItemsPayment(itemPaymentModelList);

			receiptModels.add(paymentReceiptModel);
			i++;
		}
		else
		{
			LOG.error("updateOrderPaymentStatus status receipt [" + payment.getReceipt() != null ? payment.getReceipt().getStatus()
					: StringUtils.EMPTY + "]");
		}
		return i;
	}

	private void updateOrderPaymentStatusReference(final CartModel cartModel, final ReferenceConfirmResponse ccresponse)
	{
		LOG.info("updateOrderPaymentStatus");
		final PaymentInfoModel paymentInfoModel = cartModel.getPaymentInfo();

		final List<PaymentReceiptModel> receiptModels = new ArrayList<>();
		if (ccresponse != null && ccresponse.getPayments() != null)
		{
			LOG.info("ccresponse.getPayments() != null");
			int i = 0;
			for (final Payment payment : ccresponse.getPayments())
			{
				final PaymentReceiptModel paymentReceiptModel = new PaymentReceiptModel();
				if (payment.getReceipt() != null && "SUCCESS".equalsIgnoreCase(payment.getReceipt().getStatus())
						|| "COMPLETE".equalsIgnoreCase(payment.getReceipt().getStatus())
						|| "PENDING".equalsIgnoreCase(payment.getReceipt().getStatus()))
				{
					paymentReceiptModel.setIdPaymentReceipt(Integer.valueOf(cartModel.getCode()));
					paymentReceiptModel.setPaymentId(payment.getPaymentId());
					paymentReceiptModel.setKeyReceipt(cartModel.getCode() + "-" + i);
					paymentReceiptModel.setTransaction(payment.getReceipt().getTransaction());
					paymentReceiptModel.setAuthorization(payment.getReceipt().getAuthorization());
					paymentReceiptModel.setCode(payment.getReceipt().getCode());
					paymentReceiptModel.setRisk(payment.getReceipt().getRisk());
					paymentReceiptModel.setStatus(payment.getReceipt().getStatus());
					paymentReceiptModel.setUrl3DS(payment.getReceipt().getUrl3DS());
					paymentReceiptModel.setType(payment.getType());
					paymentReceiptModel.setProvider(payment.getReceipt().getProvider());
					paymentReceiptModel.setMembership(payment.getReceipt().getMembership());
					paymentReceiptModel.setBank(payment.getReceipt().getBank());
					paymentReceiptModel.setAttempts(payment.getReceipt().getAttempts());
					paymentReceiptModel.setClabe(payment.getReceipt().getClabe());
					paymentReceiptModel.setReference(payment.getReceipt().getReference());
					paymentReceiptModel.setExpiration(payment.getReceipt().getExpiration());
					paymentReceiptModel.setAgreement(payment.getReceipt().getAgreement());
					paymentReceiptModel.setBarcode(payment.getReceipt().getBarcode());
					receiptModels.add(paymentReceiptModel);
					i++;

				}
				else
				{
					LOG.error("updateOrderPaymentStatusReference status receipt [" + payment.getReceipt() != null
							? payment.getReceipt().getStatus()
							: StringUtils.EMPTY + "]");
				}
			}
			cartModel.setPaymentStatus(PaymentStatus.NOTPAID);
			cartModel.setStatusConfirmatePayment(true);
			paymentInfoModel.setReceipt(receiptModels);
		}

		cartModel.setPaymentInfo(paymentInfoModel);

		LOG.info("modelService.saveAll(paymentInfoModel, order)");

		modelService.saveAll(paymentInfoModel, cartModel);
		modelService.refresh(cartModel);

	}

	private void updateOrderPaymentStatusSpei(final CartModel cartModel, final SpeiConfirmResponse ccresponse)
	{
		LOG.info("updateOrderPaymentStatusSpei");
		final PaymentInfoModel paymentInfoModel = cartModel.getPaymentInfo();

		final List<PaymentReceiptModel> receiptModels = new ArrayList<>();
		if (ccresponse != null && ccresponse.getPayments() != null)
		{
			LOG.info("ccresponse.getPayments() != null");

			int i = 0;
			for (final Payment payment : ccresponse.getPayments())
			{
				final PaymentReceiptModel paymentReceiptModel = new PaymentReceiptModel();
				if (payment.getReceipt() != null && "SUCCESS".equalsIgnoreCase(payment.getReceipt().getStatus())
						|| "COMPLETE".equalsIgnoreCase(payment.getReceipt().getStatus())
						|| "PENDING".equalsIgnoreCase(payment.getReceipt().getStatus()))
				{
					paymentReceiptModel.setIdPaymentReceipt(Integer.valueOf(cartModel.getCode()));
					paymentReceiptModel.setPaymentId(payment.getPaymentId());
					paymentReceiptModel.setKeyReceipt(cartModel.getCode() + "-" + i);
					paymentReceiptModel.setTransaction(payment.getReceipt().getTransaction());
					paymentReceiptModel.setAuthorization(payment.getReceipt().getAuthorization());
					paymentReceiptModel.setCode(payment.getReceipt().getCode());
					paymentReceiptModel.setRisk(payment.getReceipt().getRisk());
					paymentReceiptModel.setStatus(payment.getReceipt().getStatus());
					paymentReceiptModel.setUrl3DS(payment.getReceipt().getUrl3DS());
					paymentReceiptModel.setType(payment.getType());
					paymentReceiptModel.setProvider(payment.getReceipt().getProvider());
					paymentReceiptModel.setMembership(payment.getReceipt().getMembership());
					paymentReceiptModel.setBank(payment.getReceipt().getBank());
					paymentReceiptModel.setAttempts(payment.getReceipt().getAttempts());
					paymentReceiptModel.setClabe(payment.getReceipt().getClabe());
					paymentReceiptModel.setReference(payment.getReceipt().getReference());
					paymentReceiptModel.setExpiration(payment.getReceipt().getExpiration());
					paymentReceiptModel.setAgreement(payment.getReceipt().getAgreement());
					paymentReceiptModel.setDescription(payment.getReceipt().getDescription());
					paymentReceiptModel.setBeneficiary(payment.getReceipt().getBeneficiary());
					receiptModels.add(paymentReceiptModel);
					i++;
				}
				else
				{
					LOG.error("updateOrderPaymentStatusSpei status receipt [" + payment.getReceipt() != null
							? payment.getReceipt().getStatus()
							: StringUtils.EMPTY + "]");
				}
			}
			cartModel.setPaymentStatus(PaymentStatus.NOTPAID);
			cartModel.setStatusConfirmatePayment(true);
			paymentInfoModel.setReceipt(receiptModels);
		}

		cartModel.setPaymentInfo(paymentInfoModel);

		LOG.info("modelService.saveAll(paymentInfoModel, order)");

		modelService.saveAll(paymentInfoModel, cartModel);
		modelService.refresh(cartModel);

	}

	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/users/{userId}/orders/{code}", method = RequestMethod.GET)
	@RequestMappingOverride(priorityProperty = "telb2cocc.TmaOrdersController.orderCache.priority")
	@CacheControl(directive = CacheControlDirective.PUBLIC, maxAge = 120)
	@Cacheable(value = "orderCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.CommerceCacheKeyGenerator).generateKey(true,true,'getOrderForUserByCode',#code,#fields)")
	@ResponseBody
	@ApiOperation(nickname = "getUserOrders", value = "Get a order.", notes = "Returns specific order details based on a specific order code. The response contains detailed order information.")
	@ApiBaseSiteIdAndUserIdParam
	public OrderWsDTO getUserOrders(@ApiParam(value = "Order GUID (Globally Unique Identifier) or order CODE", required = true)
	@PathVariable
	final String code, @ApiFieldsParam
	@RequestParam(defaultValue = DEFAULT_FIELD_SET)
	final String fields)
	{
		final OrderData orderData = orderFacade.getOrderDetailsForCode(code);
		updateOrderDataProductsOrderHistory(orderData);
		orderData.setOrderne(aesService.encryptGCM(orderData.getCode()));
		final OrderModel order = getOrder(orderData.getCode());
		if (Objects.nonNull(order))
		{
			final UserModel user = order.getUser();
			orderData.setUsne(aesService.encryptGCM(user.getUid(), user.getPk().getLongValueAsString()));
		}
		return getDataMapper().map(orderData, OrderWsDTO.class);
	}

	protected void validateCartForPlaceOrder() throws Exception //NOSONAR
	{
		removeBillingAddress();

		if (!timerFromStoreParam)
		{
			final String sap_expert_test = configurationService.getConfiguration().getString(TEST_PERFORMANCE);

			final UserModel user = userService.getCurrentUser();
			final String uid = user != null ? user.getUid() : "";
			final Pattern pat = Pattern.compile(".*@saplogtest.com.*");
			final Matcher mat = pat.matcher(uid);


			if (!getCartFacade().hasSessionCart())
			{
				throw new InvalidCartException("Cannot place order. There was no checkout cart created yet!");
			}

			final CartData cartData = tmaCartFacade.getSessionCart();

			final Errors errors = new BeanPropertyBindingResult(cartData, "sessionCart");
			validate(cartData, errors);
			final Errors errorsStock = new BeanPropertyBindingResult(cartData, "stockAdjustment | [GlobalMessage] Warning");
			Errors errorsERP = null;
			final CartModel cartModel = getCart();
			LOG.info("intentos: "+cartModel.getIntentos());
			if(cartModel.getIntentos() > 0){
				errorsERP = new BeanPropertyBindingResult(cartData, "realizaPedidoIntentos | [GlobalMessage] Warning");
			} else {
				errorsERP = new BeanPropertyBindingResult(cartData, "realizaPedido | [GlobalMessage] Warning");
			}

			boolean isMsi = false;
			if (sap_expert_test.equals("true") && mat.matches())
			{
				LOG.info("sap_expert_test.equals(\"true\") && mat.matches()");
			}
			else
			{
				if (!is3dsecureParam)
				{
					LOG.info("1.- validateStock is Running...");
					isMsi = validateStock(getCartForUserByGUID(cartData.getGuid()), errorsStock, errorsERP);
				}
			}

			if (sap_expert_test.equals("true") && mat.matches())
			{
				LOG.info("sap_expert_test.equals(\"true\") && mat.matches()");
			}
			else
			{
				if (errors.hasErrors())
				{
					throw new WebserviceValidationException(errors);
				}
				if (errorsStock.hasErrors())
				{
					throw new WebserviceValidationException(errorsStock);
				}
				if (errorsERP.hasErrors())
				{
					cartModel.setIntentos(1);
					modelService.save(cartModel);
					throw new WebserviceValidationException(errorsERP);
				}
			}




			if (sap_expert_test.equals("true") && mat.matches())
			{
				LOG.info("errorsPayment.hasErrors() sap_expert_test.equals(\"true\") && mat.matches()");
			}
			else
			{
				Errors errorsPayment = null;
				if(cartModel.getIntentos() > 0){
					errorsPayment = new BeanPropertyBindingResult(cartData, "failedExecutePaymentIntentos | [ModalMessage] Warning");
				} else {
					errorsPayment = new BeanPropertyBindingResult(cartData, "failedExecutePayment | [GlobalMessage] Warning");
				}

				executePayment(cartData, errorsPayment, isMsi);

				if (errorsPayment.hasErrors())
				{
					//final CartModel cartModel = getCart();
					cartModel.setIntentos(1);
					modelService.save(cartModel);
					LOG.info("errorsPayment.hasErrors() hacer rollback");
					runRollback(cartModel);
					throw new WebserviceValidationException(errorsPayment);
				}
			}

		}
		else
		{
			//Timer from store - validar si vamos a quitar algun producto que no se pago
			checkPayment3ds();
		}

	}

	private void removeBillingAddress() {
		final CartModel cartModel = getCart();
		AddressModel billingAddress = cartModel.getBillingAddress();
		if(billingAddress != null && StringUtils.isEmpty(billingAddress.getRfc())
			&& !billingAddress.getShippingAddress() ) {
			modelService.remove(cartModel.getBillingAddress());
			modelService.refresh(cartModel);
		}
	}

	private void checkPayment3ds()
	{
		final CartModel cartModel = getCart();
		final PaymentInfoModel paymentInfoModel = cartModel.getPaymentInfo();
		//Se agrega Estatus
		PaymentStatus paymentStatus = PaymentStatus.PAID;

		if (paymentInfoModel.getReceiptStatus() != null)
		{
			Boolean paymentComplete = false;
			for (final PaymentReceiptModel paymentReceiptModel : paymentInfoModel.getReceiptStatus())
			{
				if (!COMPLETE.equalsIgnoreCase(paymentReceiptModel.getStatus())
						&& !REVIEW.equalsIgnoreCase(paymentReceiptModel.getStatus()) && paymentReceiptModel.getItemsPayment() != null
						|| !paymentReceiptModel.getVerifiedPayment())
				{
					boolean removeEntry = true;
					//                        quitar items
					for (final ItemPaymentModel itemPaymentModel : paymentReceiptModel.getItemsPayment())
					{
						for (final AbstractOrderEntryModel entry : cartModel.getEntries())
						{
							final ProductModel productModel = entry.getProduct();
							if (productModel.getSku().equalsIgnoreCase(itemPaymentModel.getSku()))
							{
								LOG.info("runRollbackEntry product: " + productModel.getSku());
								runRollbackEntry(entry, null);
								paymentComplete = null;
								modelService.remove(entry);
								removeEntry = false;
							}
						}
					}

					//                        quitar SA
					if (removeEntry)
					{
						for (final ItemPaymentModel itemPaymentModel : paymentReceiptModel.getItemsPayment())
						{
							for (final AbstractOrderEntryModel entry : cartModel.getEntries())
							{
								final List<AdditionalServiceEntryModel> additionalServiceEntryModelList = entry
										.getAdditionalServiceEntries();
								for (final AdditionalServiceEntryModel additionalServiceEntry : additionalServiceEntryModelList)
								{
									if (additionalServiceEntry != null && additionalServiceEntry.getAdditionalServiceProduct() != null
											&& additionalServiceEntry.getAdditionalServiceProduct().getSku()
													.equalsIgnoreCase(itemPaymentModel.getSku()))
									{
										paymentComplete = true;
										additionalServiceEntry.setRejected(true);
										additionalServiceEntry.setDeniedPayment(true);
										modelService.save(additionalServiceEntry);
									}
								}
							}
						}
					}
				}

				if (REVIEW.equalsIgnoreCase(paymentReceiptModel.getStatus()) && paymentReceiptModel.getVerifiedPayment())
				{
					paymentReceiptModel.setValidatedPayment3Ds(Boolean.FALSE);
					modelService.save(paymentReceiptModel);
					cartModel.setStatus(OrderStatus.PAYMENT_ON_VALIDATION);
					paymentStatus = PaymentStatus.NOTPAID;
				}
				error3ds = "";
			}
			if (OrderStatus.PAYMENT_ON_VALIDATION.equals(cartModel.getStatus()))
			{
				if (paymentComplete)
				{
					rollback3Ds(cartModel, paymentComplete);
				}
			}
			else
			{
				rollback3Ds(cartModel, paymentComplete);
			}
		}
		cartModel.setPaymentStatus(paymentStatus);
		cartModel.setStatusConfirmatePayment(true);
		modelService.save(cartModel);
	}

	private void rollback3Ds(final CartModel cartModel, final Boolean paymentComplete)
	{
		for (final AbstractOrderEntryModel entry : cartModel.getEntries())
		{
			LOG.info("runRollbackEntry for cart entries");
			runRollbackEntry(entry, paymentComplete);
		}
	}

	private void runRollbackEntry(final AbstractOrderEntryModel entryModel, final Boolean rollbackRecarga)
	{
		LOG.info("runRollbackEntry");
		final List<SendErpRespModel> deleteSendErpRespModels = new ArrayList<>();
		final List<SendErpModel> deleteSendErpModels = new ArrayList<>();
		final boolean result = rollbackRecarga == null;
		if (CollectionUtils.isNotEmpty(entryModel.getSendErpRespList()))
		{
			for (final SendErpRespModel sendErpRespModel : entryModel.getSendErpRespList())
			{
				if (result)
				{
					deleteSendErpRespModels.add(sendErpRespModel);
					for (final SendErpModel sendErpModel : sendErpRespModel.getSendErpList())
					{
						deleteSendErpModels.add(sendErpModel);
						final InfoLiberarRecursos infoLiberarRecursos = new InfoLiberarRecursos();
						infoLiberarRecursos.setIdPedido(sendErpModel.getPoNoSalesDocument());
						infoLiberarRecursos.setIdEntrega(sendErpModel.getPoNoDelivery());
						infoLiberarRecursos.setRegion(entryModel.getOrder().getRegionCode());
						infoLiberarRecursos.setbConsigment(true);
						infoLiberarRecursos.setbImei(false);
						infoLiberarRecursos.setbIccid(false);
						try
						{
							final LiberarRecursosResponse liberarRecursosResponse = liberaRecursosMQService
									.rollbackService(infoLiberarRecursos, null);
							if (liberarRecursosResponse == null)
							{
								addToRollbackList(infoLiberarRecursos, sendErpModel.getPoNoKunnr(), entryModel.getOrder().getCode());
							}
						}
						catch (final Exception e)
						{
							addToRollbackList(infoLiberarRecursos, sendErpModel.getPoNoKunnr(), entryModel.getOrder().getCode());
							e.printStackTrace();
						}
					}
				}
				else if (CollectionUtils.isNotEmpty(sendErpRespModel.getSendErpList())
						&& sendErpRespModel.getSendErpList().size() > 1)
				{
					for (final SendErpModel sendErpModel : sendErpRespModel.getSendErpList())
					{
						boolean doRollback = false;
						if (sendErpModel.getInlcuyeRecarga().equals(rollbackRecarga))
						{
							deleteSendErpModels.add(sendErpModel);
							doRollback = true;
						}

						if (doRollback)
						{
							final InfoLiberarRecursos infoLiberarRecursos = new InfoLiberarRecursos();
							infoLiberarRecursos.setIdPedido(sendErpModel.getPoNoSalesDocument());
							infoLiberarRecursos.setIdEntrega(sendErpModel.getPoNoDelivery());
							infoLiberarRecursos.setRegion(entryModel.getOrder().getRegionCode());
							infoLiberarRecursos.setbConsigment(true);
							infoLiberarRecursos.setbImei(false);
							infoLiberarRecursos.setbIccid(false);
							try
							{
								final LiberarRecursosResponse liberarRecursosResponse = liberaRecursosMQService
										.rollbackService(infoLiberarRecursos, null);
								if (liberarRecursosResponse == null)
								{
									addToRollbackList(infoLiberarRecursos, sendErpModel.getPoNoKunnr(), entryModel.getOrder().getCode());
								}
							}
							catch (final Exception e)
							{
								addToRollbackList(infoLiberarRecursos, sendErpModel.getPoNoKunnr(), entryModel.getOrder().getCode());
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
		removeErpModels(deleteSendErpRespModels, deleteSendErpModels);
		modelService.refresh(entryModel);
	}

	private boolean validateStock(final CartModel cart, final Errors errors, Errors errorsERP) throws Exception
	{
		LOG.info("TmaOrdersController.validateStock");
		boolean isMSI = false;
		runRollback(cart);
		removeAllSendErpResponses(cart.getCode(), cart);
		setRegionToOrder(cart);
		final String postalCode = cart.getDeliveryAddress().getPostalcode();

		char prefixCode = 'a';
		if (StringUtils.isNotEmpty(postalCode))
		{
			for (final AbstractOrderEntryModel entry : cart.getEntries())
			{
				final TelcelPoVariantModel telcelPoVariant = (TelcelPoVariantModel) entry.getProduct();
				String requiereSim = "x";
				if (telcelPoVariant.getChipAdditionalServiceProduct() == null)
				{
					requiereSim = telcelPoVariant.getActivable() ? "1" : "x";
				}
				BigDecimal coberturaProduct = BigDecimal.valueOf(0);
				try
				{
					final ConsultarAlmacenCPResponse consultarAlmacenCPResponse = getReplicateStockService()
							.getAlmacenForCp(postalCode, requiereSim, entry.getProduct().getSku(), entry.getQuantity().toString(), "");
					if (consultarAlmacenCPResponse != null && consultarAlmacenCPResponse.getConsultarAlmacenCPResponse() != null
							&& consultarAlmacenCPResponse.getConsultarAlmacenCPResponse().getMaterialesResp() != null)
					{

						final List<MaterialAlmacenRespType> materialAlmacenRespTypeList = consultarAlmacenCPResponse
								.getConsultarAlmacenCPResponse().getMaterialesResp();
						for (final MaterialAlmacenRespType materialAlmacenRespType : materialAlmacenRespTypeList)
						{
							coberturaProduct = coberturaProduct.add(materialAlmacenRespType.getCobertura());
						}
					}

					if (entry.getQuantity() > coberturaProduct.longValue())
					{
						//						tmaCartFacade.updateCartEntry(entry.getEntryNumber(),coberturaProduct.longValue());

						//						errors.reject("cart.stockAdjustment",entry.getProduct().getSku());
						errors.reject("cart.stockAdjustment", new String[]
						{ entry.getProduct().getSku(), entry.getQuantity().toString(), coberturaProduct.toString() },
								"Stock insuficiente - SKU: {0} - Qty: {1} - Disponible: {2}");
						LOG.info("runRollback stock insuficiente");
						runRollback(entry.getOrder());
						break;
					}
					else
					{
						if (cart.getPaymentInfo() instanceof CreditCardPaymentInfoModel)
						{
							final CreditCardPaymentInfoModel creditCardPaymentInfoModel = (CreditCardPaymentInfoModel) cart
									.getPaymentInfo();
							if (StringUtils.isNotEmpty(creditCardPaymentInfoModel.getMsi())
									&& !creditCardPaymentInfoModel.getMsi().equals("0"))
							{
								if (CollectionUtils.isNotEmpty(entry.getAdditionalServiceEntries()))
								{
									for (final AdditionalServiceEntryModel additionalServiceEntryModel : entry
											.getAdditionalServiceEntries())
									{
										if (!additionalServiceEntryModel.getRejected())
										{
											isMSI = true;
											break;
										}
									}
								}
							}
						}
						createTelcelRealizaPedidoEntries(entry,
								consultarAlmacenCPResponse.getConsultarAlmacenCPResponse().getMaterialesResp(), isMSI, prefixCode);
						final boolean result = telcelOrderService.sendToERPByEntry(entry, entry.getSendErpRespList(), isMSI);
						if (result)
						{
							LOG.info("runRollback sendToERPByEntry");
							runRollback(entry.getOrder());
							String errorERP = "ERROR REALIZA PEDIDO";
							errorsERP.reject(errorERP, new String[]
											{ entry.getProduct().getSku(), entry.getQuantity().toString(),
													String.valueOf(coberturaProduct.subtract(BigDecimal.ONE)) },
									"Error realiza pedido - SKU: {0} - Qty: {1} - Disponible: {2}");
							break;
						}
						for (int x = 0; x < entry.getQuantity(); x++)
						{
							++prefixCode;
						}
					}
				}
				catch (final Exception e)
				{
					LOG.info("runRollback error : " + e.getMessage());
					runRollback(cart);
					e.printStackTrace();
					errors.reject("realiza.pedido.invalid");
					break;
				}
			}
		}
		else
		{
			throw new Exception(String.format("No postal code for cart: %s", cart.getCode()));
		}
		return isMSI;
	}

	private void removeAllSendErpResponses(final String code, final AbstractOrderModel order)
	{
		LOG.info("removeAllSendErpResponses");
		final List<SendErpRespModel> sendErpRespModels = telcelOrderService.getSendErpRespModels(code);
		if (CollectionUtils.isNotEmpty(sendErpRespModels))
		{
			final List<SendErpRespModel> deleteSendErpRespModels = new ArrayList<>();
			final List<SendErpModel> deleteSendErpModels = new ArrayList<>();
			for (final SendErpRespModel sendErpRespModel : sendErpRespModels)
			{
				deleteSendErpRespModels.add(sendErpRespModel);
				if (CollectionUtils.isNotEmpty(sendErpRespModel.getSendErpList()))
				{
					for (final SendErpModel sendErpModel : sendErpRespModel.getSendErpList())
					{
						deleteSendErpModels.add(sendErpModel);
						final InfoLiberarRecursos infoLiberarRecursos = new InfoLiberarRecursos();
						infoLiberarRecursos.setIdPedido(sendErpModel.getPoNoSalesDocument());
						infoLiberarRecursos.setIdEntrega(sendErpModel.getPoNoDelivery());
						infoLiberarRecursos.setRegion(code);
						infoLiberarRecursos.setbConsigment(true);
						infoLiberarRecursos.setbImei(false);
						infoLiberarRecursos.setbIccid(false);
						try
						{
							final LiberarRecursosResponse liberarRecursosResponse = liberaRecursosMQService
									.rollbackService(infoLiberarRecursos, null);
							if (liberarRecursosResponse == null)
							{
								addToRollbackList(infoLiberarRecursos, sendErpModel.getPoNoKunnr(), code);
							}
						}
						catch (final Exception e)
						{
							addToRollbackList(infoLiberarRecursos, sendErpModel.getPoNoKunnr(), code);
							e.printStackTrace();
						}
					}
				}
			}
			removeErpModels(deleteSendErpRespModels, deleteSendErpModels);
			refreshEntries(order);
			modelService.refresh(order);
		}
	}

	private void addToRollbackList(final InfoLiberarRecursos infoLiberarRecursos, final String poNoKunnr, final String code)
	{
		try
		{
			final TelcelRollbackModel telcelRollbackModel = modelService.create(TelcelRollbackModel.class);
			telcelRollbackModel.setPo_no_salesdocument(infoLiberarRecursos.getIdPedido());
			telcelRollbackModel.setPo_no_delivery(infoLiberarRecursos.getIdEntrega());
			telcelRollbackModel.setPo_no_kunnr(poNoKunnr);
			telcelRollbackModel.setOrderID(code);
			telcelRollbackModel.setRegionCode(
					StringUtils.isNotEmpty(infoLiberarRecursos.getRegion()) ? infoLiberarRecursos.getRegion() : StringUtils.EMPTY);
			modelService.save(telcelRollbackModel);
		}
		catch (final Exception e)
		{
			LOG.info(String.format("Error creating TelcelRollbackModel for order, order code:", code));
			LOG.error(e.getMessage());
		}
	}

	private void runRollback(final AbstractOrderModel order)
	{
		LOG.info("runRollback");
		final List<SendErpRespModel> deleteSendErpRespModels = new ArrayList<>();
		final List<SendErpModel> deleteSendErpModels = new ArrayList<>();
		for (final AbstractOrderEntryModel entryModel : order.getEntries())
		{
			if (CollectionUtils.isNotEmpty(entryModel.getSendErpRespList()))
			{
				for (final SendErpRespModel sendErpRespModel : entryModel.getSendErpRespList())
				{
					deleteSendErpRespModels.add(sendErpRespModel);
					if (CollectionUtils.isNotEmpty(sendErpRespModel.getSendErpList()))
					{
						for (final SendErpModel sendErpModel : sendErpRespModel.getSendErpList())
						{
							deleteSendErpModels.add(sendErpModel);
							final InfoLiberarRecursos infoLiberarRecursos = new InfoLiberarRecursos();
							infoLiberarRecursos.setIdPedido(sendErpModel.getPoNoSalesDocument());
							infoLiberarRecursos.setIdEntrega(sendErpModel.getPoNoDelivery());
							infoLiberarRecursos.setRegion(order.getRegionCode());
							infoLiberarRecursos.setbConsigment(true);
							infoLiberarRecursos.setbImei(false);
							infoLiberarRecursos.setbIccid(false);
							LOG.info("Rollback : " + "pedido = " + sendErpModel.getPoNoSalesDocument() + ", entrega = "
									+ sendErpModel.getPoNoDelivery());
							try
							{
								final LiberarRecursosResponse liberarRecursosResponse = liberaRecursosMQService
										.rollbackService(infoLiberarRecursos, null);
								if (liberarRecursosResponse == null)
								{
									addToRollbackList(infoLiberarRecursos, sendErpModel.getPoNoKunnr(), order.getCode());
								}
							}
							catch (final Exception e)
							{
								addToRollbackList(infoLiberarRecursos, sendErpModel.getPoNoKunnr(), order.getCode());
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
		removeErpModels(deleteSendErpRespModels, deleteSendErpModels);
		refreshEntries(order);
		modelService.refresh(order);
	}


	private void runRollbackForPayments(final AbstractOrderModel order, final Boolean rollbackRecarga)
	{
		LOG.info("runRollbackForPayments");
		final List<SendErpRespModel> deleteSendErpRespModels = new ArrayList<>();
		final List<SendErpModel> deleteSendErpModels = new ArrayList<>();
		final boolean result = rollbackRecarga == null;
		for (final AbstractOrderEntryModel entryModel : order.getEntries())
		{
			if (CollectionUtils.isNotEmpty(entryModel.getSendErpRespList()))
			{
				for (final SendErpRespModel sendErpRespModel : entryModel.getSendErpRespList())
				{
					if (result)
					{
						deleteSendErpRespModels.add(sendErpRespModel);
					}

					if (CollectionUtils.isNotEmpty(sendErpRespModel.getSendErpList()) && sendErpRespModel.getSendErpList().size() > 1)
					{
						for (final SendErpModel sendErpModel : sendErpRespModel.getSendErpList())
						{
							boolean doRollback = false;
							if (result)
							{
								deleteSendErpModels.add(sendErpModel);
								doRollback = true;
							}
							else
							{
								if (sendErpModel.getInlcuyeRecarga().equals(rollbackRecarga))
								{
									deleteSendErpModels.add(sendErpModel);
									doRollback = true;
								}
							}

							if (doRollback)
							{
								final InfoLiberarRecursos infoLiberarRecursos = new InfoLiberarRecursos();
								infoLiberarRecursos.setIdPedido(sendErpModel.getPoNoSalesDocument());
								infoLiberarRecursos.setIdEntrega(sendErpModel.getPoNoDelivery());
								infoLiberarRecursos.setRegion(order.getRegionCode());
								infoLiberarRecursos.setbConsigment(true);
								infoLiberarRecursos.setbImei(false);
								infoLiberarRecursos.setbIccid(false);
								try
								{
									final LiberarRecursosResponse liberarRecursosResponse = liberaRecursosMQService
											.rollbackService(infoLiberarRecursos, null);
									if (liberarRecursosResponse == null)
									{
										addToRollbackList(infoLiberarRecursos, sendErpModel.getPoNoKunnr(), order.getCode());
									}
								}
								catch (final Exception e)
								{
									addToRollbackList(infoLiberarRecursos, sendErpModel.getPoNoKunnr(), order.getCode());
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
		}

		removeErpModels(deleteSendErpRespModels, deleteSendErpModels);
		//		refreshEntries(order);
		//		modelService.refresh(order);
	}

	private void removeErpModels(final List<SendErpRespModel> deleteSendErpRespModels,
			final List<SendErpModel> deleteSendErpModels)
	{
		if (CollectionUtils.isNotEmpty(deleteSendErpModels))
		{
			for (final SendErpModel model : deleteSendErpModels)
			{
				modelService.remove(model);
			}
		}
		if (CollectionUtils.isNotEmpty(deleteSendErpRespModels))
		{
			for (final SendErpRespModel model : deleteSendErpRespModels)
			{
				modelService.remove(model);
			}
		}
	}

	private void refreshEntries(final AbstractOrderModel order)
	{
		try
		{
			for (final AbstractOrderEntryModel entry : order.getEntries())
			{
				modelService.refresh(entry);
			}
		}
		catch (final Exception e)
		{
			LOG.error("Error To refresh entry : " + e.getMessage());
		}
	}

	private void setRegionToOrder(final CartModel order)
	{
		final String postalCode = order.getDeliveryAddress().getPostalcode();
		final CodigosPostalesTelcelData codigosPostalesTelcelData = telcelCodigosPostalesService.getInfForZipcode(postalCode);
		if (!(Objects.isNull(codigosPostalesTelcelData) || Objects.isNull(codigosPostalesTelcelData.getRegion())))
		{
			order.setRegionCode(codigosPostalesTelcelData.getRegion().getCode());
			modelService.save(order);
		}
	}

	private void createTelcelRealizaPedidoEntries(final AbstractOrderEntryModel entry,
			final List<MaterialAlmacenRespType> materialesResp, final boolean isMSI, char prefixCode) throws Exception
	{
		final List<SendErpRespModel> telcelErpResponseModels = new ArrayList<>();
		final int value = entry.getQuantity().intValue();
		for (int x = 0; x < value; x++)
		{
			final SendErpRespModel telcelErpResponseModel = modelService.create(SendErpRespModel.class);
			telcelErpResponseModel.setCode(prefixCode + entry.getOrder().getCode());
			//Saber si tiene recarga
			if (CollectionUtils.isNotEmpty(entry.getAdditionalServiceEntries()))
			{
				try
				{
					final AdditionalServiceEntryModel additionalServiceEntryModel = entry.getAdditionalServiceEntries().get(x);
					if (!additionalServiceEntryModel.getRejected())
					{
						telcelErpResponseModel.setInlcuyeRecarga(true);
						telcelErpResponseModel.setAdditionalServiceEntry(additionalServiceEntryModel);
					}
					else
					{
						telcelErpResponseModel.setInlcuyeRecarga(false);
					}
				}
				catch (final Exception e)
				{
					//the values of the list are not the same as those of the recharges
				}
			}
			else
			{
				telcelErpResponseModel.setInlcuyeRecarga(false);
			}
			for (final MaterialAlmacenRespType materialAlmacenRespType : materialesResp)
			{
				if (!(BigDecimal.ZERO.compareTo(materialAlmacenRespType.getCobertura()) == 0))
				{
					telcelErpResponseModel.setAlmacen(materialAlmacenRespType.getAlmacen());
					telcelErpResponseModel.setCentro(materialAlmacenRespType.getCentro());
					if (entry.getProduct().getActivable())
					{
						telcelErpResponseModel.setSim(StringUtils.isNotEmpty(materialAlmacenRespType.getNumMaterialSim())
								? materialAlmacenRespType.getNumMaterialSim()
								: StringUtils.EMPTY);
					}
					materialAlmacenRespType.setCobertura(materialAlmacenRespType.getCobertura().subtract(BigDecimal.ONE));
					break;
				}
			}
			modelService.save(telcelErpResponseModel);
			telcelErpResponseModels.add(telcelErpResponseModel);
			++prefixCode;
		}
		entry.setSendErpRespList(telcelErpResponseModels);
		LOG.info("Entry ErpRespList size for order: " + entry.getOrder().getCode() + " = " + telcelErpResponseModels.size());
		modelService.saveAll();
	}

	public void validate(final Object target, final Errors errors)
	{
		final CartData cart = (CartData) target;

		if (!cart.isCalculated())
		{
			errors.reject("cart.notCalculated");
		}

		if (cart.getDeliveryMode() == null)
		{
			errors.reject("cart.deliveryModeNotSet");
		}

		//        if (cart.getPaymentInfo() == null) {
		//            errors.reject("cart.paymentInfoNotSet");
		//        }

	}

	public void updateOrderDataProductsOrderHistory(final OrderData orderData)
	{
		BaseStoreModel baseStoreModel = baseStoreService.getCurrentBaseStore();
		if (Objects.isNull(baseStoreModel))
		{
			baseStoreModel = baseStoreService.getBaseStoreForUid(TELCEL);
		}
		if (Objects.nonNull(baseStoreModel))
		{
			final OrderModel order = customerAccountService.getOrderForCode(orderData.getCode(), baseStoreModel);
			if (Objects.nonNull(order))
			{
				final HolderLineModel holderLine = order.getAddressHolderLine();
				if (Objects.nonNull(holderLine))
				{
					String name = (holderLine.getName() == null ? "" : holderLine.getName()) + " ";
					name += (holderLine.getLastName() == null ? "" : holderLine.getLastName());
					orderData.setHolderLineName(name);
					orderData.setHolderLineEmail(holderLine.getEmail());
				}
				orderData.setPaymentStatus(order.getPaymentStatus().getCode());
				orderData.setPaymentStatusDescription(
						enumerationService.getEnumerationName(order.getPaymentStatus(), new Locale("es", "MX")));
				/*
				 * for (final OrderEntryData oed : orderData.getEntries()) { final ProductData product = oed.getProduct();
				 * setAdditionalAttributes(order, product); }
				 */
				String consignmentCode = "";
				if (CollectionUtils.isNotEmpty(orderData.getConsignments()))
				{
					for (final ConsignmentData consignmentData : orderData.getConsignments())
					{
						consignmentCode = consignmentData.getCode();
						setAdditionalAttributes(order, consignmentData.getEntries().get(0).getOrderEntry().getProduct(),
								consignmentData);
					}
				}
			}
		}
	}

	private void setAdditionalAttributes(final OrderModel order, final ProductData product, final ConsignmentData consignmentData)
	{
		final String consignmentCode = consignmentData.getCode();

		try
		{
			if (OrderStatus.PAYMENT_ON_VALIDATION.equals(order.getStatus()))
			{
				product.setSendStatus(OrderStatus.PAYMENT_ON_VALIDATION.getCode());
			}
			else
			{
				final String[] sendTrack = telcelOrdersFacade.findTrackSendStatusProduct(order.getConsignments(), order,
						product.getCode(), consignmentCode);
				product.setTrackStatus(sendTrack[0]);
				product.setTrackStatusDescription(sendTrack[1]);
				product.setSendStatus(sendTrack[2]);
				product.setSendStatusDescription(sendTrack[3]);
			}
		}
		catch (final Exception e)
		{
			LOG.info(e.getMessage());
		}
		product.setEstimatedDeliveryDate(order.getEstimatedDateFedex() == null ? "" : order.getEstimatedDateFedex());
		//product.setInvoiceNumberList(findInvoicesProduct(order.getEntries(), product.getCode())); //NO SONAR
		product.setInvoiceNumberList(findTelcelInvoicesProduct(order.getConsignments(), order, product.getCode()));
		product.setPaymentStatus(order.getPaymentStatus().getCode());
		product
				.setPaymentStatusDescription(enumerationService.getEnumerationName(order.getPaymentStatus(), new Locale("es", "MX")));
		product.setRealDeliveryDate(findRealDeliveryDate(order.getConsignments(), order, product.getCode()));
		product.setGuiaFedex(telcelOrdersFacade.findFedexGuide(order.getConsignments(), order, product.getCode(), consignmentCode));
		final String[] paqueteInfo = telcelOrdersFacade.findPaqueteAmigoInfo(consignmentData);
		product.setPaqueteAmigoNombre(paqueteInfo[0]);
		product.setPaqueteAmigoPrecio(paqueteInfo[1]);
	}

	/*
	 * private String[] findTrackSendStatusProduct(final Set<ConsignmentModel> consignments, final OrderModel order,
	 * final String productCode) { if (Objects.isNull(consignments)) { return new String[] { "", "", "", "" }; } final
	 * Iterator<ConsignmentModel> consignmentIterator = consignments.iterator(); while (consignmentIterator.hasNext()) {
	 * final ConsignmentModel consignment = consignmentIterator.next(); final Set<ConsignmentEntryModel> entries =
	 * consignment.getConsignmentEntries(); final Iterator<ConsignmentEntryModel> entriesIterator = entries.iterator();
	 * while (entriesIterator.hasNext()) { final ConsignmentEntryModel entry = entriesIterator.next(); final ProductModel
	 * product = entry.getOrderEntry().getProduct(); if (product.getCode().equals(productCode)) { final String[] result =
	 * new String[] { "", "", "", "" }; final List<String> track = telcelOrdersFacade.getTrackStatus(consignment, order);
	 * final List<String> send = telcelOrdersFacade.getSendStatus(order, entry,consignment); if (!track.isEmpty()) {
	 * result[0] = track.get(0); result[1] = track.get(1); } if (!send.isEmpty()) { result[2] = send.get(0); result[3] =
	 * send.get(1); } return result; } } } return new String[] { "", "", "", "" }; }
	 */

	private String findRealDeliveryDate(final Set<ConsignmentModel> consignments, final OrderModel order, final String productCode)
	{
		if (Objects.isNull(consignments))
		{
			return null;
		}
		String realDeliveryString = "";
		final Iterator<ConsignmentModel> consignmentIterator = consignments.iterator();
		Date realDeliveryDate = null;
		while (consignmentIterator.hasNext())
		{
			final ConsignmentModel consignment = consignmentIterator.next();
			final Set<ConsignmentEntryModel> entries = consignment.getConsignmentEntries();
			final Iterator<ConsignmentEntryModel> entriesIterator = entries.iterator();
			while (entriesIterator.hasNext())
			{
				final ConsignmentEntryModel entry = entriesIterator.next();
				final ProductModel product = entry.getOrderEntry().getProduct();
				if (product.getCode().equals(productCode))
				{
					realDeliveryDate = consignment.getRealDeliveryDateFedex();
					if (Objects.nonNull(realDeliveryDate))
					{
						final String month = telcelSoapConverterService.converterStringMonth(realDeliveryDate.getMonth() + 1);
						return realDeliveryString = String.valueOf(realDeliveryDate.getDate()) + " de " + month + " de "
								+ String.valueOf(realDeliveryDate.getYear() + 1900);
					}
				}
			}
		}
		return "";
	}


	private List<String> findTelcelInvoicesProduct(final Set<ConsignmentModel> consignments, final OrderModel order,
			final String productCode)
	{
		if (Objects.isNull(consignments))
		{
			return null;
		}
		List<String> invoices = null;
		if (!consignments.isEmpty())
		{
			invoices = new ArrayList<>();
		}
		final Iterator<ConsignmentModel> consignmentIterator = consignments.iterator();
		while (consignmentIterator.hasNext())
		{
			final ConsignmentModel consignment = consignmentIterator.next();
			final Set<ConsignmentEntryModel> entries = consignment.getConsignmentEntries();
			final Iterator<ConsignmentEntryModel> entriesIterator = entries.iterator();
			while (entriesIterator.hasNext())
			{
				final ConsignmentEntryModel entry = entriesIterator.next();
				final ProductModel product = entry.getOrderEntry().getProduct();
				if (product.getCode().equals(productCode))
				{
					final TelcelFacturaModel telcelFactura = consignment.getTelcelFactura();
					if (Objects.nonNull(telcelFactura))
					{
						invoices.add(telcelFactura.getNumeroFactura());
						return invoices;
					}
				}
			}
		}
		return invoices;
	}

	//	private List<String> findInvoicesProduct(final List<AbstractOrderEntryModel> entries, final String productCode)
	//	{
	//		if (Objects.isNull(entries))
	//		{
	//			return null;
	//		}
	//		final Iterator<AbstractOrderEntryModel> entryIterator = entries.iterator();
	//		final List<String> invoices = new ArrayList<String>();
	//		while (entryIterator.hasNext())
	//		{
	//			final AbstractOrderEntryModel entry = entryIterator.next();
	//			final ProductModel product = entry.getProduct();
	//			if (product.getCode().equals(productCode))
	//			{
	//				final List<TelcelFacturaModel> telcelFacturas = entry.getFacturas();
	//				for (final TelcelFacturaModel factura : telcelFacturas)
	//				{
	//					invoices.add(factura.getNumeroFactura());
	//				}
	//			}
	//		}
	//		return invoices;
	//	}

	private void associateCVTDATWithOrder(final String typeUserCVTDAT, final String idUserCVTDAT)
	{
		final CartModel cartModel = getCartService().getSessionCart();
		if (Objects.nonNull(typeUserCVTDAT))
		{
			if (typeUserCVTDAT.equals("DAT"))
			{
				final UserInfoDATModel dat = userInfoDATDao.getUserInfoDATModel(idUserCVTDAT);
				cartModel.setUserinfodat(dat);
				modelService.save(cartModel);
			}
			else if (typeUserCVTDAT.equals("CVT"))
			{
				final UserInfoCVTModel cvt = userInfoCVTDao.getUserInfoCVTModel(idUserCVTDAT);
				cartModel.setUserinfocvt(cvt);
				modelService.save(cartModel);
			}
		}
	}

	private OrderModel getOrder(final String code)
	{
		BaseStoreModel baseStoreModel = baseStoreService.getCurrentBaseStore();
		if (Objects.isNull(baseStoreModel))
		{
			baseStoreModel = baseStoreService.getBaseStoreForUid(TELCEL);
		}
		if (Objects.nonNull(baseStoreModel))
		{
			final OrderModel order = customerAccountService.getOrderForCode(code, baseStoreModel);
			if (Objects.nonNull(order))
			{
				return order;
			}
		}
		return null;
	}

	public ReplicateStockService getReplicateStockService()
	{
		return replicateStockService;
	}

	public void setReplicateStockService(final ReplicateStockService replicateStockService)
	{
		this.replicateStockService = replicateStockService;
	}

	public BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	public CartService getCartService()
	{
		return cartService;
	}

	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	public CartFacade getCartFacade()
	{
		return cartFacade;
	}

	public void setCartFacade(final CartFacade cartFacade)
	{
		this.cartFacade = cartFacade;
	}

	public TelcelOrderService getTelcelOrderService()
	{
		return telcelOrderService;
	}

	public void setTelcelOrderService(final TelcelOrderService telcelOrderService)
	{
		this.telcelOrderService = telcelOrderService;
	}
	
}
