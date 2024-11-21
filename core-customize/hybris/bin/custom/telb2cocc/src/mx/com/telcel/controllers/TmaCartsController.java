/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.controllers;

import com.amx.telcel.di.sds.esb.sap.cancelacion.LiberarRecursosResponse;
import de.hybris.platform.b2ctelcocommercewebservicescommons.dto.PlaceWsDTO;
import de.hybris.platform.b2ctelcocommercewebservicescommons.dto.ProcessTypeWsDTO;
import de.hybris.platform.b2ctelcocommercewebservicescommons.dto.ProductCharacteristicWsDTO;
import de.hybris.platform.b2ctelcocommercewebservicescommons.dto.RelatedPartyWsDTO;
import de.hybris.platform.b2ctelcocommercewebservicescommons.validator.TmaAnonymousCartValidator;
import de.hybris.platform.b2ctelcocommercewebservicescommons.validator.TmaUserCartValidator;
import de.hybris.platform.b2ctelcofacades.data.CartActionInput;
import de.hybris.platform.b2ctelcofacades.data.CartDataList;
import de.hybris.platform.b2ctelcofacades.data.TmaPlaceData;
import de.hybris.platform.b2ctelcofacades.data.TmaPriceContextData;
import de.hybris.platform.b2ctelcofacades.order.TmaOrderEntryFacade;
import de.hybris.platform.b2ctelcofacades.product.TmaProductOfferFacade;
import de.hybris.platform.b2ctelcofacades.stock.TmaStockFacade;
import de.hybris.platform.b2ctelcoservices.data.TmaProductSpecCharacteristicConfigItem;
import de.hybris.platform.b2ctelcoservices.enums.TmaPlaceRoleType;
import de.hybris.platform.b2ctelcoservices.enums.TmaProcessType;
import de.hybris.platform.b2ctelcoservices.enums.TmaRelatedPartyRole;
import de.hybris.platform.b2ctelcoservices.enums.TmaSubscribedProductAction;
import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.order.SaveCartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.order.CommerceCartRestorationException;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.request.mapping.annotation.RequestMappingOverride;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commercewebservicescommons.dto.order.CartListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.CartModificationWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.CartWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderEntryWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.CartEntryException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.CartException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.LowStockException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.ProductLowStockException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.StockSystemException;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.util.Config;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdUserIdAndCartIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;

import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import mx.com.telcel.core.model.SendErpModel;
import mx.com.telcel.core.model.SendErpRespModel;
import mx.com.telcel.core.model.TelcelRollbackModel;
import mx.com.telcel.core.models.liberarrecursos.InfoLiberarRecursos;
import mx.com.telcel.core.services.mq.liberacionrecursos.LiberaRecursosMQService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import mx.com.telcel.core.commerceservices.order.impl.TelcelCommerceCartModificationStatus;
import mx.com.telcel.core.order.exception.MaxQtyCartMeringException;
import mx.com.telcel.core.order.exception.TotalExceededCartMergingException;
import mx.com.telcel.dto.order.AdditionalServiceEntryWsDTO;
import mx.com.telcel.facades.checkout.TelcelDeliveryModeFacade;
import mx.com.telcel.facades.esquemacobro.data.TelcelEsquemaCobroDTO;
import mx.com.telcel.facades.order.data.AdditionalServiceEntryData;
import mx.com.telcel.facades.orders.TelcelTmaCartFacade;
import mx.com.telcel.facades.product.data.AdditionalServiceData;



/**
 * Controller for cart related requests such as retrieving/delete/update/... a cart
 *
 * @since 1911
 */
@Controller
@RequestMapping(value = "/{baseSiteId}/users/{userId}/carts")
@CacheControl(directive = CacheControlDirective.NO_CACHE)
@Api(tags = "Carts")
public class TmaCartsController extends BaseController
{
	private static final Logger LOG = LoggerFactory.getLogger(TmaCartsController.class);
	private static final String DEFAULT_PAGE_SIZE = "20";
	private static final String DEFAULT_CURRENT_PAGE = "0";
	private static final String ANONYMOUS = "anonymous";
	private static final String CURRENT = "current";
	private static final long DEFAULT_PRODUCT_QUANTITY = 1;
	private static final int DEFAULT_GROUP_NUMBER = -1;
	private static final String ENTRY = "entry";
	private static final String ENTRY_NOT_FOUND = "Entry not found";

	private static final Collection<ProductOption> PRODUCT_OPTIONS = Arrays.asList(ProductOption.BASIC, ProductOption.DESCRIPTION);
	private static final char PIPE = '|';
	private static final String DOUBLE_DASH = "--";
	private static final String AMIGO_CHIPCLASSIFICATION = "amigo_chipclassification";
	private static final String ASL = "ASL";

	private static final String SMARTPHONE_CLASSIFICATION = "telcel.smartphone.smartphoneclassification";

	@Resource(name = "userFacade")
	private UserFacade userFacade;

	@Resource(name = "tmaCartFacade")
	private TelcelTmaCartFacade tmaCartFacade;

	@Resource(name = "customerFacade")
	private CustomerFacade customerFacade;

	@Resource(name = "saveCartFacade")
	private SaveCartFacade saveCartFacade;

	@Resource(name = "tmaStockFacade")
	private TmaStockFacade tmaStockFacade;

	@Resource(name = "tmaOrderEntryFacade")
	private TmaOrderEntryFacade tmaOrderEntryFacade;

	@Resource(name = "telcelDeliveryModeFacade")
	private TelcelDeliveryModeFacade telcelDeliveryModeFacade;

	//@Resource(name = "tmaOrderEntryCreateValidatorCustom")
	//private Validator tmaOrderEntryCreateValidatorCustom;

	/*
	 * @Resource(name = "tmaOrderEntryUpdateValidator") private Validator tmaOrderEntryUpdateValidator;
	 *
	 * @Resource(name = "tmaOrderEntryReplaceValidator") private Validator tmaOrderEntryReplaceValidator;
	 */

	@Resource(name = "anonymousCartValidator")
	private TmaAnonymousCartValidator anonymousCartValidator;

	@Resource(name = "userCartValidator")
	private TmaUserCartValidator userCartValidator;

	@Resource(name = "tmaProductOfferFacade")
	private TmaProductOfferFacade tmaProductOfferFacade;

	//TODO: START - Change flows so as to remove services.
	// Services must not being called over controllers, it  does not follow design pattern and creates perform issues.
	@Resource(name = "commerceCartService")
	private CommerceCartService commerceCartService;

	@Resource
	private BaseSiteService baseSiteService;

	@Resource
	private UserService userService;

	@Resource
	private ModelService modelService;

	@Resource(name = "liberaRecursosMQService")
	private LiberaRecursosMQService liberaRecursosMQService;
	//TODO: END - Change flows so as to remove services.

	private final String[] DISALLOWED_FIELDS = new String[] {};

	// Work around, handling requests once per cart.
	// This was impelmented because of the lack of support for multithreading in SAP Commerce 2105 Model Service and all services using it.
	private final Map<String, Object> createEntryAsyncLocks = new WeakHashMap<>();
	private final Map<String, Object> manageEntryAsyncLocks = new WeakHashMap<>();
	private final Map<String, Object> createCartAsyncLocks = new WeakHashMap<>();

	@RequestMapping(method = RequestMethod.GET)
	@RequestMappingOverride(priorityProperty = "telb2cocc.TmaCartsController.getCarts.priority")
	@ResponseBody
	@ApiOperation(value = "Get all customer carts.", notes = "Lists all customer carts.")
	@ApiBaseSiteIdAndUserIdParam
	public CartListWsDTO getCarts(
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL")
			@RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET)
			final String fields,
			@ApiParam(value = "Optional parameter. If the parameter is provided and its value is true, only saved carts are returned.")
			@RequestParam(defaultValue = "false")
			final boolean savedCartsOnly,
			@ApiParam(value = "Optional pagination parameter in case of savedCartsOnly == true. Default value 0.")
			@RequestParam(defaultValue = DEFAULT_CURRENT_PAGE)
			final int currentPage,
			@ApiParam(value = "Optional {@link PaginationData} parameter in case of savedCartsOnly == true. Default value 20.")
			@RequestParam(defaultValue = DEFAULT_PAGE_SIZE)
			final int pageSize, @ApiParam(value = "Optional sort criterion in case of savedCartsOnly == true. No default value.")
			@RequestParam(required = false)
			final String sort)
	{
		if (userFacade.isAnonymousUser())
		{
			throw new AccessDeniedException("Access is denied");
		}
		final CartDataList cartDataList = new CartDataList();
		final PageableData pageableData = new PageableData();
		pageableData.setCurrentPage(currentPage);
		pageableData.setPageSize(pageSize);
		pageableData.setSort(sort);
		final List<CartData> allCarts = new ArrayList<>(
				saveCartFacade.getSavedCartsForCurrentUser(pageableData, null).getResults());

		if (!savedCartsOnly)
		{
			allCarts.addAll(tmaCartFacade.getCartsForCurrentUser());
		}

		cartDataList.setCarts(allCarts);
		return getDataMapper().map(cartDataList, CartListWsDTO.class, fields);
	}

	@RequestMapping(value = "/{cartId}", method = RequestMethod.GET)
	@RequestMappingOverride(priorityProperty = "telb2cocc.TmaCartsController.getCart.priority")
	@ResponseBody
	@ApiOperation(nickname = "getCart", value = "Get a cart with a given identifier.", notes = "Returns the cart with a given identifier.")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public CartWsDTO getCart(@ApiParam(value = "Identifier of the Shopping Cart", required = true)
	@PathVariable("cartId")
	final String cartId, @ApiParam(value = "Identifier of the Customer", required = true)
	@PathVariable("userId")
	final String userId,
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL")
			@RequestParam(defaultValue = DEFAULT_FIELD_SET)
			final String fields)
	{
		Optional<CartData> cartData;

		final String customerId = userId.equalsIgnoreCase(CURRENT) ? customerFacade.getCurrentCustomerUid() : userId;

		if (userId.equalsIgnoreCase(ANONYMOUS))
		{
			cartData = telcelDeliveryModeFacade.getCartAndSetDeliveryModeByCartID(cartId);
			//cartData = tmaCartFacade.getCartForGuid(cartId);
		}
		else
		{
			cartData = telcelDeliveryModeFacade.getCartAndSetDeliveryModeByCartIdAndCustomer(cartId, customerId);
			//cartData = tmaCartFacade.getCartForCodeAndCustomer(cartId, customerId);
		}
		if (!cartData.isPresent()) {
			LOG.error("Cart not found for cartID "+cartId, CartException.NOT_FOUND);
			return new CartWsDTO();
		}

		return getDataMapper().map(cartData.get(), CartWsDTO.class);
	}

	@RequestMapping(value = "/{cartId}/entries", method = RequestMethod.POST, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@RequestMappingOverride(priorityProperty = "telb2cocc.TmaCartsController.createCartEntry.priority")
	@ResponseBody
	@ApiOperation(nickname = "createCartEntry", value = "Adds a product to the cart.", notes = "Applies prices to the Cart entries based on the location of the customer when the attribute region is provided and stores other details like formerSupplier of the customer.")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public ResponseEntity<CartModificationWsDTO> createCartEntry(
			@ApiParam(value = "Identifier of the Shopping Cart", required = true)
			@PathVariable("cartId")
			final String cartId, @ApiParam(value = "Identifier of the Customer", required = true)
			@PathVariable("userId")
			final String userId, @ApiParam(value = "Base site identifier", required = true)
			@PathVariable
			final String baseSiteId,
			@ApiParam(value = "Request body parameter that contains details such as the product code (product.code), BPO Code (rootBpoCode), ProcessType (processType.id), GroupNumber (entryGroupNumbers), the quantity of product (quantity), and the pickup store name (deliveryPointOfService.name).\n\nThe DTO is in XML or .json format.", required = true)
			@RequestBody
			final OrderEntryWsDTO entry,
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL")
			@RequestParam(defaultValue = DEFAULT_FIELD_SET)
			final String fields) throws CommerceCartModificationException
	{
		ResponseEntity<CartModificationWsDTO> modificationDTO;
		Object lock;
		synchronized (createEntryAsyncLocks){
			lock = createEntryAsyncLocks.computeIfAbsent(cartId,o -> new Object());
		}
		synchronized (lock) {
			LOG.info("ENDPOINT_START_createCartEntry "+Thread.currentThread().getName() +" "+cartId);
			// Esto se hace para evitar que el carrito Guest se pierda al no completar una compra
			// Fix rapido: Ningun carrito guest se puede modificar las entradas una vez creado solo es valido durante
			// la sesion del checkout.
			setAnonymousCartToGuestCartAndRollback(cartId);
			populateDefaultCartEntryValues(entry);
			//	validate(entry, ENTRY, tmaOrderEntryCreateValidatorCustom);
			final Integer groupNumber = entry.getEntryGroupNumbers().stream().findFirst().get();
			final String customerId = userId.equalsIgnoreCase(CURRENT) ? customerFacade.getCurrentCustomerUid() : userId;
			modificationDTO = addCartEntryInternal(baseSiteId, fields, entry, groupNumber, cartId, customerId);
			LOG.info("ENDPOINT_END_createCartEntry "+Thread.currentThread().getName() +" "+cartId);
		}

		return modificationDTO;
	}

	private void setAnonymousCartToGuestCartAndRollback(String cartId) {
		if (StringUtils.isNotEmpty(cartId)) {
			CartModel cartModel = commerceCartService.getCartForGuidAndSite(cartId, baseSiteService.getCurrentBaseSite());
			if (cartModel != null) {
				runRollback(cartModel);
				if (cartModel.getUser() != null && StringUtils.contains(cartModel.getUser().getUid(), PIPE)) {
					cartModel.setUser(userService.getAnonymousUser());
					modelService.save(cartModel);
				}
			} else {
				final UserModel user = userService.getCurrentUser();
				if (user != null) {
					cartModel = commerceCartService.getCartForCodeAndUser(cartId, user);
					runRollback(cartModel);
				}

			}
		}
	}

	private void runRollback(AbstractOrderModel cartModel) {
		try {
			if (cartModel != null) {
				List<SendErpRespModel> deleteSendErpRespModels = new ArrayList<>();
				List<SendErpModel> deleteSendErpModels = new ArrayList<>();
				for (AbstractOrderEntryModel entryModel : cartModel.getEntries()) {
					if (CollectionUtils.isNotEmpty(entryModel.getSendErpRespList())) {
						for (SendErpRespModel sendErpRespModel : entryModel.getSendErpRespList()) {
							deleteSendErpRespModels.add(sendErpRespModel);
							if (CollectionUtils.isNotEmpty(sendErpRespModel.getSendErpList())) {
								for (SendErpModel sendErpModel : sendErpRespModel.getSendErpList()) {
									deleteSendErpModels.add(sendErpModel);
									final InfoLiberarRecursos infoLiberarRecursos = new InfoLiberarRecursos();
									infoLiberarRecursos.setIdPedido(sendErpModel.getPoNoSalesDocument());
									infoLiberarRecursos.setIdEntrega(sendErpModel.getPoNoDelivery());
									infoLiberarRecursos.setRegion(cartModel.getRegionCode());
									infoLiberarRecursos.setbConsigment(true);
									infoLiberarRecursos.setbImei(false);
									infoLiberarRecursos.setbIccid(false);

									try {
										final LiberarRecursosResponse liberarRecursosResponse = liberaRecursosMQService.rollbackService(infoLiberarRecursos, null);
										if (liberarRecursosResponse == null) {
											addToRollbackList(infoLiberarRecursos, sendErpModel.getPoNoKunnr(), cartModel.getCode());
										}
									} catch (Exception e) {
										addToRollbackList(infoLiberarRecursos, sendErpModel.getPoNoKunnr(), cartModel.getCode());
										e.printStackTrace();
									}
								}
							}
						}
					}
				}
				if (CollectionUtils.isNotEmpty(deleteSendErpModels)) {
					for (SendErpModel model : deleteSendErpModels) {
						modelService.remove(model);
					}
				}
				if (CollectionUtils.isNotEmpty(deleteSendErpRespModels)) {
					for (SendErpRespModel model : deleteSendErpRespModels) {
						modelService.remove(model);
					}
				}
			}
		} catch (Exception e) {
			LOG.info("Error rollback for cart");
		}
	}

	private void addToRollbackList(InfoLiberarRecursos infoLiberarRecursos, String poNoKunnr, String code) {
		try {
			TelcelRollbackModel telcelRollbackModel = modelService.create(TelcelRollbackModel.class);
			telcelRollbackModel.setPo_no_salesdocument(infoLiberarRecursos.getIdPedido());
			telcelRollbackModel.setPo_no_delivery(infoLiberarRecursos.getIdEntrega());
			telcelRollbackModel.setPo_no_kunnr(poNoKunnr);
			telcelRollbackModel.setOrderID(code);
			modelService.save(telcelRollbackModel);
		} catch (Exception e) {
			LOG.info(String.format("Error creating TelcelRollbackModel for cart, cart code:", code));
			LOG.error(e.getMessage());
		}
	}

	@RequestMapping(method = RequestMethod.POST)
	@RequestMappingOverride(priorityProperty = "telb2cocc.TmaCartsController.createCart.priority")
	@ResponseBody
	@ApiOperation(nickname = "createCart", value = "Creates or restore a cart for a user.", notes = "Creates a new cart or restores an anonymous cart as a user’s cart (if an old Cart Id is given in the request), and also considers region as a distinguishing factor for merging cart entries or adding a new cart entries.")
	@ApiBaseSiteIdAndUserIdParam
	public CartWsDTO createCart(@ApiParam(value = "Anonymous cart GUID.")
	@RequestParam(required = false)
	final String oldCartId, @ApiParam(value = "The GUID of the user's cart that will be merged with the anonymous cart.")
	@RequestParam(required = false)
	final String toMergeCartGuid, @ApiParam(value = "Identifier of the Customer", required = false)
	@PathVariable("userId")
	final String userId,
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL")
			@RequestParam(defaultValue = DEFAULT_FIELD_SET)
			final String fields, final HttpServletResponse response)

	{
		CartWsDTO cartWsDTO;
		Object lock;
		synchronized (createCartAsyncLocks){
			lock = createCartAsyncLocks.computeIfAbsent(userId,o -> new Object());
		}
		synchronized (lock){
			LOG.info("ENDPOINT_START_createCart "+Thread.currentThread().getName());
			if (StringUtils.isEmpty(oldCartId))
			{
				cartWsDTO = getRestoredCart(toMergeCartGuid, userId, fields);
			}
			else if (StringUtils.isEmpty(toMergeCartGuid))
			{
				cartWsDTO = userFacade.isAnonymousUser() ? new CartWsDTO() : changeCartUserToCurrent(oldCartId, fields);
			}
			else if (oldCartId.equalsIgnoreCase(toMergeCartGuid))
			{
				Optional<CartData> cart = tmaCartFacade.getCartForGuid(oldCartId);
				cartWsDTO = getDataMapper().map(cart.isPresent() ? cart.get() : tmaCartFacade.getSessionCart(), CartWsDTO.class, fields);
			}
			else
			{
				LOG.info("Merging cart "+oldCartId+" to "+toMergeCartGuid);
				return mergeCarts(oldCartId, toMergeCartGuid, userId, fields, response);
			}
			response.setStatus(HttpStatus.CREATED.value());
			LOG.info("ENDPOINT_END_createCart "+Thread.currentThread().getName());
		}

		return cartWsDTO;
	}

	@RequestMapping(value = "/{cartId}/entries/{entryNumber}", method = RequestMethod.DELETE)
	@RequestMappingOverride(priorityProperty = "telb2cocc.TmaCartsController.deleteCartEntry.priority")
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(nickname = "removeCartEntry", value = "Deletes cart entry.", notes = "Deletes cart entry.")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public void removeCartEntry(
			@ApiParam(value = "The entry number. Each entry in a cart has an entry number. Cart entries are numbered in ascending order, starting with zero (0).", required = true)
			@PathVariable
			final long entryNumber, @ApiParam(value = "The ID of the user's cart that will be cloned.", required = true)
			@PathVariable("cartId")
			final String cartId, @ApiParam(value = "Identifier of the Customer", required = true)
			@PathVariable("userId")
			final String userId) throws CommerceCartModificationException
	{
		LOG.info("ENDPOINT_START_removeCartEntry "+Thread.currentThread().getName());
		final CartActionInput cartActionInput = new CartActionInput();
		if (tmaCartFacade.isAnonymousUserCart(cartId))
		{
			cartActionInput.setToCartGUID(cartId);
		}
		else
		{
			cartActionInput.setCartId(cartId);
		}

		final String customerId = userId.equalsIgnoreCase(CURRENT) ? customerFacade.getCurrentCustomerUid() : userId;
		cartActionInput.setUserGuid(customerId);
		cartActionInput.setQuantity(Long.valueOf(0));
		cartActionInput.setEntryNumber((int) entryNumber);
		tmaCartFacade.processCartAction(Collections.singletonList(cartActionInput)).get(0);
		LOG.info("ENDPOINT_END_removeCartEntry "+Thread.currentThread().getName());
	}

	@RequestMapping(value = "/{cartId}/entries/{entryNumber}", method = RequestMethod.PATCH, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@RequestMappingOverride(priorityProperty = "telb2cocc.TmaCartsController.updateCartEntry.priority")
	@ResponseBody
	@ApiOperation(nickname = "updateCartEntry", value = "Update quantity and store details of a cart entry.", notes = "Updates the quantity of a single cart entry and the details of the store where the cart entry will be picked up, and also updates the other details like region,process type, formerSupplier of the customer.")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public ResponseEntity<CartModificationWsDTO> updateCartEntry(@ApiParam(value = "Base site identifier.", required = true)
	@PathVariable
	final String baseSiteId,
			@ApiParam(value = "The entry number. Each entry in a cart has an entry number. Cart entries are numbered in ascending order, starting with zero (0).", required = true)
			@PathVariable
			final long entryNumber,
			@ApiParam(value = "Request body parameter that contains details such as the quantity of product (quantity), and the pickup store name (deliveryPointOfService.name)\n\nThe DTO is in XML or .json format.", required = true)
			@RequestBody
			final OrderEntryWsDTO entry,
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL")
			@RequestParam(defaultValue = DEFAULT_FIELD_SET)
			final String fields, @ApiParam(value = "The ID of the user's cart that will be updated", required = true)
			@PathVariable("cartId")
			final String cartId, @ApiParam(value = "Identifier of the Customer", required = true)
			@PathVariable("userId")
			final String userId) throws CommerceCartModificationException
	{
		CartModificationData cartModificationData;
		Object lock;
		synchronized (manageEntryAsyncLocks){
			lock = manageEntryAsyncLocks.computeIfAbsent(cartId,o -> new Object());
		}
		synchronized (lock) {
			LOG.info("ENDPOINT_START_updateCartEntry "+Thread.currentThread().getName());
			// Esto se hace para evitar que el carrito Guest se pierda al no completar una compra
			// Fix rapido: Ningun carrito guest se puede modificar las entradas una vez creado solo es valido durante
			// la sesion del checkout.
			setAnonymousCartToGuestCartAndRollback(cartId);
			entry.setEntryNumber((int) entryNumber);
			//	validate(entry, ENTRY, tmaOrderEntryUpdateValidator);
			final CartActionInput cartActionInput = new CartActionInput();
			if (tmaCartFacade.isAnonymousUserCart(cartId))
			{
				cartActionInput.setToCartGUID(cartId);
			}
			else
			{
				cartActionInput.setCartId(cartId);
			}

			final String customerId = userId.equalsIgnoreCase(CURRENT) ? customerFacade.getCurrentCustomerUid() : userId;
			cartActionInput.setUserGuid(customerId);
			cartActionInput.setQuantity(entry.getQuantity());
			cartActionInput.setEntryNumber((int) entryNumber);
			cartActionInput.setEntryQtyPos(entry.getEntryQtyPos());
			cartActionInput.setEntryId(String.valueOf(entryNumber));
			if (!ObjectUtils.isEmpty(entry.getProcessType()))
			{
				cartActionInput.setProcessType(entry.getProcessType().getId());
			}
			setCpiInformation(entry, cartActionInput);
			cartActionInput.setAppointmentId(entry.getAppointment() != null ? entry.getAppointment().getId() : null);
			cartActionInput.setPlaces(getPlaces(entry));
			cartActionInput.setServiceProvider(
					entry.getSubscribedProduct() != null ? getServiceProvider(entry.getSubscribedProduct().getRelatedParty()) : null);
			cartActionInput.setContractStartDate(entry.getContractStartDate());
			setProductCharacteristics(entry, cartActionInput);

			if (entry.getDeliveryPointOfService() != null)
			{
				cartActionInput.setStoreId(entry.getDeliveryPointOfService().getName());
			}
			cartModificationData = tmaCartFacade.processCartAction(Collections.singletonList(cartActionInput)).get(0);
			LOG.info("ENDPOINT_END_updateCartEntry "+Thread.currentThread().getName());
		}

		final CartModificationWsDTO cartModificationWsDTO = getDataMapper().map(cartModificationData, CartModificationWsDTO.class,
				fields);
		if (TelcelCommerceCartModificationStatus.STOCK_ASJUSTMENT.equalsIgnoreCase(cartModificationWsDTO.getStatusCode())
				|| CommerceCartModificationStatus.MAX_ORDER_QUANTITY_EXCEEDED.equalsIgnoreCase(cartModificationWsDTO.getStatusCode())
				|| TelcelCommerceCartModificationStatus.MAX_ORDER_CHIP_EXPRESS_QUANTITY_EXCEEDED
				.equalsIgnoreCase(cartModificationWsDTO.getStatusCode())
				|| TelcelCommerceCartModificationStatus.MAX_ORDER_TOTAL_PRICE_EXCEEDED
				.equalsIgnoreCase(cartModificationWsDTO.getStatusCode()))
		{

			return new ResponseEntity<>(cartModificationWsDTO, HttpStatus.CONFLICT);
		}

		return new ResponseEntity<>(cartModificationWsDTO, HttpStatus.OK);
	}

	@RequestMapping(value = "/{cartId}/entries/{entryNumber}", method = RequestMethod.GET)
	@RequestMappingOverride(priorityProperty = "telb2cocc.TmaCartsController.getCartEntry.priority")
	@ResponseBody
	@ApiOperation(nickname = "getCartEntry", value = "Get the details of the cart entries.", notes = "Returns the details of the cart entries.")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public OrderEntryWsDTO getCartEntry(
			@ApiParam(value = "The entry number. Each entry in a cart has an entry number. Cart entries are numbered in ascending order, starting with zero (0).", required = true)
			@PathVariable
			final long entryNumber, @ApiFieldsParam
			@RequestParam(defaultValue = DEFAULT_FIELD_SET)
			final String fields)
	{
		final CartData sessionCart = tmaCartFacade.getSessionCart();
		if (sessionCart == null)
		{
			throw new CartException("No cart found.", CartException.NOT_FOUND);
		}

		final OrderEntryData orderEntry = tmaOrderEntryFacade.getEntry(sessionCart.getEntries(), entryNumber);
		if (orderEntry == null)
		{
			throw new CartEntryException(ENTRY_NOT_FOUND, CartEntryException.NOT_FOUND, String.valueOf(entryNumber));
		}

		return getDataMapper().map(orderEntry, OrderEntryWsDTO.class, fields);
	}

	@RequestMapping(value = "/{cartId}/entries/{entryNumber}", method = RequestMethod.PUT, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@RequestMappingOverride(priorityProperty = "telb2cocc.TmaCartsController.replaceCartEntry.priority")
	@ResponseBody
	@ApiOperation(nickname = "replaceCartEntry", value = "Set quantity and store details of a cart entry.", notes = "Updates the quantity of a single cart entry and the details of the store where the cart entry will be picked up. "
			+ "Attributes not provided in request will be defined again (set to null or default)")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public CartModificationWsDTO replaceCartEntry(@ApiParam(value = "Base site identifier.", required = true)
	@PathVariable
	final String baseSiteId,
			@ApiParam(value = "The entry number. Each entry in a cart has an entry number. Cart entries are numbered in ascending order, starting with zero (0).", required = true)
			@PathVariable
			final long entryNumber,
			@ApiParam(value = "Request body parameter that contains details such as the quantity of product (quantity), and the pickup store name (deliveryPointOfService.name)\n\nThe DTO is in XML or .json format.", required = true)
			@RequestBody
			final OrderEntryWsDTO entry,
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL")
			@RequestParam(defaultValue = DEFAULT_FIELD_SET)
			final String fields) throws CommerceCartModificationException
	{
		final CartData cart = tmaCartFacade.getSessionCart();
		CartModificationWsDTO cartModificationWsDTO;
		Object lock;
		synchronized (manageEntryAsyncLocks){
			lock = manageEntryAsyncLocks.computeIfAbsent(cart.getCode(),o -> new Object());
		}
		synchronized (lock) {
			LOG.info("ENDPOINT_START_replaceCartEntry "+Thread.currentThread().getName());
			if (CollectionUtils.isEmpty(cart.getEntries()))
			{
				throw new CartEntryException(ENTRY_NOT_FOUND, CartEntryException.NOT_FOUND, String.valueOf(entryNumber));
			}

			final OrderEntryData orderEntry = tmaOrderEntryFacade.getEntry(cart.getEntries(), entryNumber);

			if (orderEntry == null)
			{
				throw new CartEntryException(ENTRY_NOT_FOUND, CartEntryException.NOT_FOUND, String.valueOf(entryNumber));
			}

			final String pickupStore = entry.getDeliveryPointOfService() == null ? null : entry.getDeliveryPointOfService().getName();

			validateCartEntryForReplace(orderEntry, entry);

			cartModificationWsDTO = updateCartEntryInternal(baseSiteId, cart, orderEntry, entry.getQuantity(), pickupStore, fields, true);
			LOG.info("ENDPOINT_END_replaceCartEntry "+Thread.currentThread().getName());
		}

		return cartModificationWsDTO;
	}

	@RequestMapping(value = "/{cartId}/entries/{entryNumber}/as/entries/{asEntryNumber}", method = RequestMethod.POST, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseBody
	@ApiOperation(nickname = "updateCartAsEntry", value = "Update details of additional service entry of a cart entry.")
	public ResponseEntity<CartModificationWsDTO> updateCartAsEntry(@ApiParam(value = "Base site identifier.", required = true)
	@PathVariable
	final String baseSiteId, @ApiParam(value = "The ID of the user's cart that will be updated", required = true)
	@PathVariable("cartId")
	final String cartId,
			@ApiParam(value = "The entry number. Each entry in a cart has an entry number. Cart entries are numbered in ascending order, starting with zero (0).", required = true)
			@PathVariable
			final long entryNumber,
			@ApiParam(value = "The additional service entry number. Each additional service entry of entries has an entry number.", required = true)
			@PathVariable
			final int asEntryNumber,
			@ApiParam(value = "Request body parameter that contains details such as additional service product. The DTO is in XML or .json format.", required = true)
			@RequestBody
			final AdditionalServiceEntryWsDTO asEntry, @ApiParam(value = "Identifier of the Customer", required = true)
			@PathVariable("userId")
			final String userId,
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "DEFAULT")
			@RequestParam(defaultValue = DEFAULT_FIELD_SET)
			final String fields) throws CommerceCartModificationException
	{
		CartModificationData cartModificationData;
		Object lock;
		synchronized (manageEntryAsyncLocks){
			lock = manageEntryAsyncLocks.computeIfAbsent(cartId,o -> new Object());
		}
		synchronized (lock) {
			LOG.info("ENDPOINT_START_updateCartAsEntry "+Thread.currentThread().getName());
			// Esto se hace para evitar que el carrito Guest se pierda al no completar una compra
			// Fix rapido: Ningun carrito guest se puede modificar las entradas una vez creado solo es valido durante
			// la sesion del checkout.
			setAnonymousCartToGuestCartAndRollback(cartId);
			if (Objects.nonNull(asEntry.getAdditionalServiceProduct())
					&& StringUtils.isBlank(asEntry.getAdditionalServiceProduct().getCode()))
			{
				throw new RequestParameterException("Additional Service Entry must have Additional Service Product with valid code",
						RequestParameterException.MISSING, "asEntry");
			}
			final CartActionInput cartActionInput = new CartActionInput();
			if (tmaCartFacade.isAnonymousUserCart(cartId))
			{
				cartActionInput.setToCartGUID(cartId);
			}
			else
			{
				cartActionInput.setCartId(cartId);
			}
			final String customerId = userId.equalsIgnoreCase(CURRENT) ? customerFacade.getCurrentCustomerUid() : userId;
			cartActionInput.setUserGuid(customerId);
			cartActionInput.setEntryNumber((int) entryNumber);
			cartActionInput.setEntryId(String.valueOf(entryNumber));
			asEntry.setEntryNumber(asEntryNumber);
			cartActionInput.setAdditionalServiceEntry(getAdditionalServiceEntryData(asEntry));

			cartModificationData = tmaCartFacade.processCartAction(Collections.singletonList(cartActionInput)).get(0);
			LOG.info("ENDPOINT_END_updateCartAsEntry "+Thread.currentThread().getName());
		}

		final CartModificationWsDTO cartModificationWsDTO = getDataMapper().map(cartModificationData, CartModificationWsDTO.class,
				fields);
		if (TelcelCommerceCartModificationStatus.STOCK_ASJUSTMENT.equalsIgnoreCase(cartModificationWsDTO.getStatusCode())
				|| CommerceCartModificationStatus.MAX_ORDER_QUANTITY_EXCEEDED.equalsIgnoreCase(cartModificationWsDTO.getStatusCode())
				|| TelcelCommerceCartModificationStatus.MAX_ORDER_CHIP_EXPRESS_QUANTITY_EXCEEDED
						.equalsIgnoreCase(cartModificationWsDTO.getStatusCode())
				|| TelcelCommerceCartModificationStatus.MAX_ORDER_TOTAL_PRICE_EXCEEDED
						.equalsIgnoreCase(cartModificationWsDTO.getStatusCode()))
		{
			return new ResponseEntity<>(cartModificationWsDTO, HttpStatus.CONFLICT);
		}

		return new ResponseEntity<>(cartModificationWsDTO, HttpStatus.OK);
	}

	private AdditionalServiceEntryData getAdditionalServiceEntryData(final AdditionalServiceEntryWsDTO asEntryWsDTO)
	{
		final AdditionalServiceEntryData asEntryData = new AdditionalServiceEntryData();
		if (Objects.nonNull(asEntryWsDTO.getAdditionalServiceProduct()))
		{
			final AdditionalServiceData asData = new AdditionalServiceData();
			asData.setCode(asEntryWsDTO.getAdditionalServiceProduct().getCode());
			asEntryData.setAdditionalServiceProduct(asData);
		}
		asEntryData.setEntryNumber(asEntryWsDTO.getEntryNumber());
		asEntryData.setRejected(asEntryWsDTO.getRejected());
		asEntryData.setDeniedPayment(asEntryWsDTO.getDeniedPayment() != null ? asEntryWsDTO.getDeniedPayment() : false);
		return asEntryData;
	}

	private CartWsDTO changeCartUserToCurrent(final String cartGuid, final String fields) {
		CartData cartData;
		try {
			cartData = tmaCartFacade.changeCurrentUserCart(cartGuid);

			return getDataMapper().map(cartData, CartWsDTO.class, fields);
		} catch (final CommerceCartModificationException ex) {
			LOG.error("Couldn't restore cart " + cartGuid + " to user session cart", CartException.CANNOT_MERGE, ex);
			return new CartWsDTO();
		}
	}

	/**
	 * Merges anonymous cart with given user cart and returns user cart.
	 *
	 * @param oldCartId
	 *           the old cart id
	 * @param evaluatedToMergeCartGuid
	 *           the evaluated to merge cart guid
	 * @param userId
	 *           the user id
	 * @param fields
	 *           the fields
	 * @return the cart ws DTO
	 * @throws CartException
	 *            the cart exception
	 */
	private CartWsDTO  mergeCarts(final String oldCartId, final String evaluatedToMergeCartGuid, final String userId,
			final String fields, final HttpServletResponse response) throws CartException
	{
		Optional<CartData> etmOptional = tmaCartFacade.getCartForGuid(evaluatedToMergeCartGuid);
		if(etmOptional.isPresent())
		{
			try
			{
				CartData etmCartData = etmOptional.get();
				if(!tmaCartFacade.getCartForGuid(oldCartId).isPresent())
				{
					LOG.warn("Cannot perform merge. oldCart["+oldCartId+"] does not exist anymore");
					return mergeCartsCreated(etmCartData, fields, response);
				}
//				validate(evaluatedToMergeCartGuid, "String", userCartValidator);
				final CartActionInput cartActionInput = new CartActionInput();
				cartActionInput.setFromCartGUID(oldCartId);
				cartActionInput.setToCartGUID(evaluatedToMergeCartGuid);
				final String customerId = userId.equalsIgnoreCase(CURRENT) ? customerFacade.getCurrentCustomerUid() : userId;
				cartActionInput.setUserGuid(customerId);
				List<CartModificationData> cartModificationData = new ArrayList<>();

				try
				{
					cartModificationData = tmaCartFacade.processCartAction(Collections.singletonList(cartActionInput));
				}
				catch (final CommerceCartModificationException ex)
				{
					if (ex.getCause() instanceof TotalExceededCartMergingException)
					{
						return mergeCartsConflicted(evaluatedToMergeCartGuid, TelcelCommerceCartModificationStatus.MERGED_CARTS_TOTAL_PRICE_EXCEEDED, fields, response);
					}
					if (ex.getCause() instanceof MaxQtyCartMeringException)
					{
						return mergeCartsConflicted(evaluatedToMergeCartGuid, TelcelCommerceCartModificationStatus.MERGED_CARTS_QUANTITY_EXCEEDED, fields, response);
					}

					throw ex;
				}

				if(CollectionUtils.isNotEmpty(cartModificationData))
				{
					final Optional<CartData> cartData = tmaCartFacade.getCartForCodeAndCustomer(cartModificationData.get(0).getCartCode(),
							customerId);
					if (!cartData.isEmpty())
					{

						return mergeCartsCreated(cartData.get(), fields, response);
					}
				}
			}
			catch (final Exception ex)
			{
				LOG.error("Error merging cart: ", ex);
			}
		}

		return mergeCartsCreated(tmaCartFacade.getSessionCart(), fields, response);
	}

	private CartWsDTO mergeCartsCreated(final CartData cartData, final String fields, final HttpServletResponse response)
	{
		final CartWsDTO cartWsDTO = getDataMapper().map(cartData, CartWsDTO.class, fields);
		response.setStatus(HttpStatus.CREATED.value());
		return cartWsDTO;
	}

	private CartWsDTO mergeCartsConflicted(final String evaluatedToMergeCartGuid, final String conflictStatusCode, final String fields, final HttpServletResponse response)
	{
		final CartWsDTO cartWsDTO = getDataMapper().map(tmaCartFacade.getCartForGuid(evaluatedToMergeCartGuid).get(), CartWsDTO.class, fields);
		cartWsDTO.setConflictStatusCode(conflictStatusCode);
		response.setStatus(HttpStatus.CONFLICT.value());
		return cartWsDTO;
	}

	/**
	 * Gets restored cart or session cart when oldCartId is not provided.
	 *
	 * @param toMergeCartGuid
	 *           the evaluated to merge cart guid
	 * @param userId
	 *           the user id
	 * @param fields
	 *           the fields
	 * @return the restored cart
	 * @throws CartException
	 *            the cart exception
	 */
	private CartWsDTO getRestoredCart(final String toMergeCartGuid, final String userId, final String fields) throws CartException
	{
		if (StringUtils.isEmpty(toMergeCartGuid))
		{
			final CartData sessionCart = tmaCartFacade.getSessionCart();
			return getDataMapper().map(sessionCart, CartWsDTO.class, fields);
		}
		if (!tmaCartFacade.isCurrentUserCart(toMergeCartGuid))
		{
			throw new CartException("Cart is not current user's cart", CartException.CANNOT_RESTORE, toMergeCartGuid);
		}
		try
		{
			tmaCartFacade.restoreSavedCart(toMergeCartGuid);
			final CartData sessionCart = tmaCartFacade.getSessionCart();
			return getDataMapper().map(sessionCart, CartWsDTO.class, fields);
		}
		catch (final CommerceCartRestorationException e)
		{
			throw new CartException("Couldn't restore cart", CartException.CANNOT_RESTORE, toMergeCartGuid, e);
		}
	}

	/**
	 * Adds to the cart an entry.
	 *
	 * @param baseSiteId
	 *           the identifier of the base site
	 * @param fields
	 *           the fields
	 * @param entry
	 *           the cart entry
	 * @param cartGroupNo
	 *           the group number
	 * @param cartId
	 *           the identifier of the shopping cart
	 * @param userId
	 *           the identifier of the user
	 * @return instance of {@link CartModificationWsDTO}
	 * @throws CommerceCartModificationException
	 */
	private ResponseEntity<CartModificationWsDTO> addCartEntryInternal(final String baseSiteId, final String fields,
			final OrderEntryWsDTO entry, final Integer cartGroupNo, final String cartId, final String userId)
			throws CommerceCartModificationException
	{
		//TODO: tmp tracking, remove logs
		LOG.info("NEORIS_START_addCartEntryInternal "+Thread.currentThread().getName()+" "+cartId);
		CartActionInput cartActionInput;
		cartActionInput = createCartItemAction(entry, StringUtils.EMPTY, entry.getProcessType(), cartGroupNo, cartId, userId, baseSiteId);

		final TmaPriceContextData tmaPriceContextData = new TmaPriceContextData();
		tmaPriceContextData.setProductCode(entry.getProduct().getCode());

		final ProductData product = tmaProductOfferFacade.getPoForCode(entry.getProduct().getCode(), PRODUCT_OPTIONS,
				tmaPriceContextData);
		if (product.isTipoActivacion() && entry.getEsquemaCobro() != null && entry.getEsquemaCobro().getCode() != null
				&& !entry.getEsquemaCobro().getCode().equals(""))
		{
			final TelcelEsquemaCobroDTO esquemaCobroDTO = new TelcelEsquemaCobroDTO();
			esquemaCobroDTO.setCode(entry.getEsquemaCobro().getCode());
			cartActionInput.setEsquemaCobro(esquemaCobroDTO);
			setAdditionalServicesEntries(entry, cartActionInput);
		}

		if (!product.getClassifications().isEmpty() && product.getClassifications().iterator().next().getCode().equals(AMIGO_CHIPCLASSIFICATION)) {
			LOG.info("Amigo_chip");
			final TelcelEsquemaCobroDTO esquemaCobroDTO = new TelcelEsquemaCobroDTO();
			esquemaCobroDTO.setCode(ASL);
			cartActionInput.setEsquemaCobro(esquemaCobroDTO);
		}

		//Payment scheme
		//Assign ASL (Amigo Sin Limite), to other categories
		if (!product.getClassifications().isEmpty() && !product.getClassifications().iterator().next().getCode().equals(Config.getParameter(SMARTPHONE_CLASSIFICATION))
		     && BooleanUtils.isTrue(product.getActivable())){
			 if (product.getClassifications().iterator().next().getCode().equals(AMIGO_CHIPCLASSIFICATION)){
				 LOG.info("Payment scheme:: Classification:: " + product.getClassifications().iterator().next().getCode() + "");
			 }else{
				 LOG.info("######### ASSING ASL (AMIGO SIN LIMITE) ##################");
				 LOG.info("ProductCode:: " + product.getCode() + " SKU:: " +product.getSku());
				 LOG.info("Classification:: " + product.getClassifications().iterator().next().getCode());
				 LOG.info("##########################################################");
				 final TelcelEsquemaCobroDTO esquemaCobroDTO = new TelcelEsquemaCobroDTO();
				 esquemaCobroDTO.setCode(ASL);
				 cartActionInput.setEsquemaCobro(esquemaCobroDTO);
			 }
		}

		final CartModificationData cartModificationData = tmaCartFacade.processCartAction(Collections.singletonList(cartActionInput)).get(0);

		//TODO: tmp tracking, remove logs
		LOG.info("NEORIS_END_addCartEntryInternal "+Thread.currentThread().getName()+" "+cartId);

		final CartModificationWsDTO cartModificationWsDTO = getDataMapper().map(cartModificationData, CartModificationWsDTO.class,
				fields);
		if (TelcelCommerceCartModificationStatus.STOCK_ASJUSTMENT.equalsIgnoreCase(cartModificationWsDTO.getStatusCode())
				|| CommerceCartModificationStatus.MAX_ORDER_QUANTITY_EXCEEDED.equalsIgnoreCase(cartModificationWsDTO.getStatusCode())
				|| TelcelCommerceCartModificationStatus.MAX_ORDER_CHIP_EXPRESS_QUANTITY_EXCEEDED
						.equalsIgnoreCase(cartModificationWsDTO.getStatusCode())
				|| TelcelCommerceCartModificationStatus.MAX_ORDER_TOTAL_PRICE_EXCEEDED
						.equalsIgnoreCase(cartModificationWsDTO.getStatusCode()))
		{
			return new ResponseEntity<>(cartModificationWsDTO, HttpStatus.CONFLICT);
		}

		return new ResponseEntity<>(cartModificationWsDTO, HttpStatus.OK);
	}

	/**
	 * Creates a {@link CartActionInput} for a {@link OrderEntryWsDTO}.
	 *
	 * @param entry
	 *           the cart entry
	 * @param rootBpoCode
	 *           the bpo Code
	 * @param processType
	 *           the process type
	 * @param cartGroupNo
	 *           the group number
	 * @param cartId
	 *           the identifier of the shopping cart
	 * @param userId
	 *           the identifier of the user
	 * @param baseSiteId
	 *           the identifier of the base site
	 * @return instance of {@link CartActionInput}
	 */
	private CartActionInput createCartItemAction(final OrderEntryWsDTO entry, final String rootBpoCode,
			final ProcessTypeWsDTO processType, final Integer cartGroupNo, final String cartId, final String userId,
			final String baseSiteId)
	{
		final CartActionInput cartActionInput = new CartActionInput();

		if (tmaCartFacade.isAnonymousUserCart(cartId))
		{
			cartActionInput.setToCartGUID(cartId);
		}
		else
		{
			cartActionInput.setCartId(cartId);
		}

		final String customerId = userId.equalsIgnoreCase(CURRENT) ? customerFacade.getCurrentCustomerUid() : userId;
		cartActionInput.setUserGuid(customerId);

		cartActionInput.setProductCode(getProductCode(entry));
		cartActionInput.setQuantity(entry.getQuantity());
		cartActionInput.setProcessType(getProcessType(processType));
		cartActionInput.setRootBpoCode(rootBpoCode);
		cartActionInput.setParentEntryNumber(entry.getParentEntryNumber());

		cartActionInput.setCartGroupNo(cartGroupNo);

		cartActionInput.setStoreId(getStoreId(entry, rootBpoCode, baseSiteId));

		if (entry.getSubscriptionTerm() != null)
		{
			cartActionInput.setSubscriptionTermId(entry.getSubscriptionTerm().getId());
		}

		setCpiInformation(entry, cartActionInput);
		cartActionInput.setAppointmentId(entry.getAppointment() != null ? entry.getAppointment().getId() : null);
		cartActionInput.setPlaces(getPlaces(entry));
		setProductCharacteristics(entry, cartActionInput);
		cartActionInput.setServiceProvider(
				entry.getSubscribedProduct() != null ? getServiceProvider(entry.getSubscribedProduct().getRelatedParty()) : null);
		cartActionInput.setContractStartDate(entry.getContractStartDate());

		if (CollectionUtils.isNotEmpty(entry.getEntries()))
		{
			cartActionInput.setChildren(entry
					.getEntries().stream().map((final OrderEntryWsDTO child) -> createCartItemAction(child,
							entry.getProduct().getCode(), processType, cartGroupNo, cartId, userId, baseSiteId))
					.collect(Collectors.toList()));
		}

		return cartActionInput;
	}

	private void setAdditionalServicesEntries (final OrderEntryWsDTO entry, CartActionInput cartActionInput) throws CommerceCartModificationException
	{
		if(CollectionUtils.isNotEmpty(entry.getAdditionalServiceEntries()))
		{
			List<AdditionalServiceEntryData> asEntriesData = new ArrayList<>();
			for(AdditionalServiceEntryWsDTO ase:entry.getAdditionalServiceEntries())
			{
				if(!isValidAdditionalServiceEntry(ase))
				{
					throw new CommerceCartModificationException("Additional Services must contain valid Additional Service Product and EntryNumber");
				}
				AdditionalServiceEntryData asEntryData = new AdditionalServiceEntryData();
				AdditionalServiceData asData = new AdditionalServiceData();
				asData.setCode(ase.getAdditionalServiceProduct().getCode());
				asEntryData.setAdditionalServiceProduct(asData);
				asEntryData.setEntryNumber(ase.getEntryNumber());
				asEntryData.setRejected(ase.getRejected());
				asEntryData.setDeniedPayment(ase.getDeniedPayment() != null ? ase.getDeniedPayment() : false);
				asEntriesData.add(asEntryData);
			}
			cartActionInput.setAdditionalServiceEntries(asEntriesData);
		}
	}

	private boolean isValidAdditionalServiceEntry(AdditionalServiceEntryWsDTO ase)
	{
		return Objects.nonNull(ase.getAdditionalServiceProduct())
				&& StringUtils.isNotBlank(ase.getAdditionalServiceProduct().getCode())
				&& Objects.nonNull(ase.getEntryNumber());
	}

	/**
	 * Sets CPI related information
	 *
	 * @param entry
	 *           the order entry
	 * @param cartActionInput
	 *           the cart action input
	 */
	private void setCpiInformation(final OrderEntryWsDTO entry, final CartActionInput cartActionInput)
	{
		if (entry.getAction() != null)
		{
			cartActionInput.setAction(TmaSubscribedProductAction.valueOf(entry.getAction().name()));
		}
		cartActionInput
				.setSubscribedProductCode(entry.getSubscribedProduct() != null ? entry.getSubscribedProduct().getId() : null);
	}

	/**
	 * Sets PSCV related data from the entry on the cartActionInput
	 *
	 * @param entry
	 *           the order entry
	 * @param cartActionInput
	 *           the cart action input
	 */
	private void setProductCharacteristics(final OrderEntryWsDTO entry, final CartActionInput cartActionInput)
	{
		final List<ProductCharacteristicWsDTO> productCharacteristics = getCharacteristics(entry);
		if (CollectionUtils.isNotEmpty(productCharacteristics))
		{
			final Set<TmaProductSpecCharacteristicConfigItem> characteristicConfigItems = productCharacteristics.stream()
					.map(this::getProductSpecCharacteristicConfigItem).collect(Collectors.toSet());
			cartActionInput.setConfigurableProductSpecCharacteristics(characteristicConfigItems);
		}
	}

	private void populateDefaultCartEntryValues(final OrderEntryWsDTO entry) throws CommerceCartModificationException
	{
		if (entry.getQuantity() == null)
		{
			entry.setQuantity(Long.valueOf(DEFAULT_PRODUCT_QUANTITY));
		}
		if (entry.getProcessType() == null && entry.getParentEntryNumber() == null)
		{
			LOG.warn("Process type empty, setting default ACQUISITION");
			final ProcessTypeWsDTO pdt = new ProcessTypeWsDTO();
			pdt.setId(TmaProcessType.ACQUISITION.getCode());
			entry.setProcessType(pdt);
		}
		if (CollectionUtils.isEmpty(entry.getEntryGroupNumbers()))
		{
			final Collection<Integer> defaultGroupNumber = new ArrayList<>();
			defaultGroupNumber.add(DEFAULT_GROUP_NUMBER);
			entry.setEntryGroupNumbers(defaultGroupNumber);
		}
	}

	/**
	 * @param baseSiteId
	 *           the store identifier
	 * @param productCode
	 *           the product identifier
	 * @param storeName
	 *           the store name
	 * @param entryNumber
	 *           the long value for entry
	 */
	private void validateIfProductIsInStockInPOS(final String baseSiteId, final String productCode, final String storeName,
			final Long entryNumber)
	{
		if (!tmaStockFacade.isStockSystemEnabled(baseSiteId))
		{
			throw new StockSystemException("Stock system is not enabled on this site", StockSystemException.NOT_ENABLED, baseSiteId);
		}
		final StockData stock = tmaStockFacade.getStockDataForProductAndPointOfService(productCode, storeName);
		if (stock != null && stock.getStockLevelStatus().equals(StockLevelStatus.OUTOFSTOCK))
		{
			if (entryNumber != null)
			{
				throw new LowStockException("Product [" + sanitize(productCode) + "] is currently out of stock", //NOSONAR
						LowStockException.NO_STOCK, String.valueOf(entryNumber));
			}
			else
			{
				throw new ProductLowStockException("Product [" + sanitize(productCode) + "] is currently out of stock",
						LowStockException.NO_STOCK, productCode);
			}
		}
		else if (stock != null && stock.getStockLevelStatus().equals(StockLevelStatus.LOWSTOCK))
		{
			if (entryNumber != null)
			{
				throw new LowStockException("Not enough product in stock", LowStockException.LOW_STOCK, String.valueOf(entryNumber));
			}
			else
			{
				throw new ProductLowStockException("Not enough product in stock", LowStockException.LOW_STOCK, productCode);
			}
		}
	}

	/**
	 * Returns the places from the product in the order entry.
	 *
	 * @param orderEntry
	 *           the order entry
	 * @return The {@link TmaPlaceData}s found.
	 */
	private List<TmaPlaceData> getPlaces(final OrderEntryWsDTO orderEntry)
	{
		final List<TmaPlaceData> tmaPlacesData = new ArrayList<>();
		if (orderEntry.getSubscribedProduct() == null || orderEntry.getSubscribedProduct().getPlace() == null)
		{
			return tmaPlacesData;
		}
		final List<PlaceWsDTO> inputPlaces = orderEntry.getSubscribedProduct().getPlace();
		inputPlaces.forEach(inputPlace -> {
			final TmaPlaceData placeData = new TmaPlaceData();
			placeData.setId(inputPlace.getId());
			placeData.setRole(TmaPlaceRoleType.valueOf(inputPlace.getRole()));
			tmaPlacesData.add(placeData);
		});
		return tmaPlacesData;
	}

	/**
	 * Returns the configured characteristics of the product offering if it is present, else it returns empty list.
	 *
	 * @param cartItem
	 *           the cart item
	 * @return the configured characteristics of the product offering
	 */
	private List<ProductCharacteristicWsDTO> getCharacteristics(final OrderEntryWsDTO cartItem)
	{
		return cartItem.getSubscribedProduct() != null
				&& CollectionUtils.isNotEmpty(cartItem.getSubscribedProduct().getCharacteristic())
						? cartItem.getSubscribedProduct().getCharacteristic()
						: new ArrayList<>();
	}

	private TmaProductSpecCharacteristicConfigItem getProductSpecCharacteristicConfigItem(
			final ProductCharacteristicWsDTO productCharacteristic)
	{
		final TmaProductSpecCharacteristicConfigItem characteristicConfigItem = new TmaProductSpecCharacteristicConfigItem();
		characteristicConfigItem.setName(productCharacteristic.getName());
		characteristicConfigItem.setValue(productCharacteristic.getValue());
		return characteristicConfigItem;
	}

	private String getProductCode(final OrderEntryWsDTO entry)
	{
		return entry.getProduct() != null ? entry.getProduct().getCode() : null;
	}

	private String getProcessType(final ProcessTypeWsDTO processType)
	{
		return processType != null ? processType.getId() : null;
	}

	private String getServiceProvider(final List<RelatedPartyWsDTO> relatedParties)
	{
		if (CollectionUtils.isNotEmpty(relatedParties))
		{
			for (final RelatedPartyWsDTO relatedParty : relatedParties)
			{
				if (StringUtils.equalsIgnoreCase(relatedParty.getRole(), TmaRelatedPartyRole.SERVICE_PROVIDER.name()))
				{
					return relatedParty.getId();
				}

			}
		}
		return null;
	}

	private String getStoreId(final OrderEntryWsDTO entry, final String rootBpoCode, final String baseSiteId)
	{
		final String pickupStore = entry.getDeliveryPointOfService() == null ? null : entry.getDeliveryPointOfService().getName();
		if (pickupStore != null && StringUtils.isEmpty(rootBpoCode) && entry.getParentEntryNumber() == null)
		{
			validateIfProductIsInStockInPOS(baseSiteId, entry.getProduct().getCode(), entry.getDeliveryPointOfService().getName(),
					null);
			return pickupStore;
		}

		return StringUtils.EMPTY;
	}


	private void validateCartEntryForReplace(final OrderEntryData oryginalEntry, final OrderEntryWsDTO entry)
	{
		final String productCode = oryginalEntry.getProduct().getCode();
		final Errors errors = new BeanPropertyBindingResult(entry, ENTRY);
		if (entry.getProduct() != null && entry.getProduct().getCode() != null && !entry.getProduct().getCode().equals(productCode))
		{
			errors.reject("cartEntry.productCodeNotMatch");
			throw new WebserviceValidationException(errors);
		}

		//	validate(entry, ENTRY, tmaOrderEntryReplaceValidator);
	}

	private CartModificationWsDTO updateCartEntryInternal(final String baseSiteId, final CartData cart,
			final OrderEntryData orderEntry, final Long qty, final String pickupStore, final String fields, final boolean putMode)
			throws CommerceCartModificationException
	{
		final long entryNumber = orderEntry.getEntryNumber().longValue();
		final String productCode = orderEntry.getProduct().getCode();
		final PointOfServiceData currentPointOfService = orderEntry.getDeliveryPointOfService();

		CartModificationData cartModificationData1 = null;
		CartModificationData cartModificationData2 = null;

		if (!StringUtils.isEmpty(pickupStore))
		{
			if (currentPointOfService == null || !currentPointOfService.getName().equals(pickupStore))
			{
				//was 'shipping mode' or store is changed
				validateForAmbiguousPositions(cart, orderEntry, pickupStore);
				validateIfProductIsInStockInPOS(baseSiteId, productCode, pickupStore, entryNumber);
				cartModificationData1 = tmaCartFacade.updateCartEntry(entryNumber, pickupStore);
			}
		}
		else if (putMode && currentPointOfService != null)
		{
			//was 'pickup in store', now switch to 'shipping mode'
			validateForAmbiguousPositions(cart, orderEntry, pickupStore);
			validateIfProductIsInStockOnline(baseSiteId, productCode, Long.valueOf(entryNumber));
			cartModificationData1 = tmaCartFacade.updateCartEntry(entryNumber, pickupStore);
		}

		if (qty != null)
		{
			cartModificationData2 = tmaCartFacade.updateCartEntry(entryNumber, qty);
		}

		return getDataMapper().map(mergeCartModificationData(cartModificationData1, cartModificationData2),
				CartModificationWsDTO.class, fields);
	}

	private void validateForAmbiguousPositions(final CartData currentCart, final OrderEntryData currentEntry,
			final String newPickupStore)
	{
		final OrderEntryData entryToBeModified = tmaOrderEntryFacade.getEntry(currentCart.getEntries(),
				currentEntry.getProduct().getCode(), newPickupStore);
		if (entryToBeModified != null && !entryToBeModified.getEntryNumber().equals(currentEntry.getEntryNumber()))
		{
			throw new CartEntryException(
					"Ambiguous cart entries! Entry number " + currentEntry.getEntryNumber()
							+ " after change would be the same as entry " + entryToBeModified.getEntryNumber(),
					CartEntryException.AMBIGIOUS_ENTRY, entryToBeModified.getEntryNumber().toString());
		}
	}

	private void validateIfProductIsInStockOnline(final String baseSiteId, final String productCode, final Long entryNumber)
	{
		if (!tmaStockFacade.isStockSystemEnabled(baseSiteId))
		{
			throw new StockSystemException("Stock system is not enabled on this site", StockSystemException.NOT_ENABLED, baseSiteId);
		}
		final StockData stock = tmaStockFacade.getStockDataFor(productCode, baseSiteId);
		if (stock != null && stock.getStockLevelStatus().equals(StockLevelStatus.OUTOFSTOCK))
		{
			if (entryNumber != null)
			{
				throw new LowStockException("Product [" + sanitize(productCode) + "] cannot be shipped - out of stock online",
						LowStockException.NO_STOCK, String.valueOf(entryNumber));
			}
			else
			{
				throw new ProductLowStockException("Product [" + sanitize(productCode) + "] cannot be shipped - out of stock online",
						LowStockException.NO_STOCK, productCode);
			}
		}
	}

	private static CartModificationData mergeCartModificationData(final CartModificationData cmd1, final CartModificationData cmd2)
	{
		if ((cmd1 == null) && (cmd2 == null))
		{
			return new CartModificationData();
		}
		if (cmd1 == null)
		{
			return cmd2;
		}
		if (cmd2 == null)
		{
			return cmd1;
		}
		final CartModificationData cmd = new CartModificationData();
		cmd.setDeliveryModeChanged(
				Boolean.TRUE.equals(cmd1.getDeliveryModeChanged()) || Boolean.TRUE.equals(cmd2.getDeliveryModeChanged()));
		cmd.setEntry(cmd2.getEntry());
		cmd.setQuantity(cmd2.getQuantity());
		cmd.setQuantityAdded(cmd1.getQuantityAdded() + cmd2.getQuantityAdded());
		cmd.setStatusCode(cmd2.getStatusCode());
		return cmd;
	}

	@InitBinder
	public void initBinder(final WebDataBinder binder)
	{
		binder.setDisallowedFields(DISALLOWED_FIELDS);
	}
}
