package mx.com.telcel.controllers;


import de.hybris.platform.b2ctelcocommercewebservicescommons.dto.PlaceWsDTO;
import de.hybris.platform.b2ctelcocommercewebservicescommons.dto.ProcessTypeWsDTO;
import de.hybris.platform.b2ctelcocommercewebservicescommons.dto.ProductCharacteristicWsDTO;
import de.hybris.platform.b2ctelcocommercewebservicescommons.dto.RelatedPartyWsDTO;
import de.hybris.platform.b2ctelcofacades.data.CartActionInput;
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
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.order.SaveCartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.order.CommerceCartRestorationException;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commercewebservicescommons.dto.order.CartModificationWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.CartWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderEntryWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.product.ProductWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.CartException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.LowStockException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.ProductLowStockException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.StockSystemException;
import de.hybris.platform.commercewebservicescommons.strategies.CartLoaderStrategy;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.Config;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import mx.com.telcel.core.commerceservices.order.impl.TelcelCommerceCartModificationStatus;
import mx.com.telcel.core.util.TelcelUtil;
import mx.com.telcel.facades.checkout.TelcelDeliveryModeFacade;
import mx.com.telcel.facades.esquemacobro.data.TelcelEsquemaCobroDTO;
import mx.com.telcel.facades.orders.TelcelTmaCartFacade;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.QueryParam;
import java.util.*;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

@Controller
@RequestMapping(value = "/external/addCartEntry")
@CacheControl(directive = CacheControlDirective.NO_STORE)
public class TelcelExternalCartController {
    private static final Logger LOG = Logger.getLogger(TelcelExternalCartController.class);

    private static final String CURRENT = "current";
    private static final long DEFAULT_PRODUCT_QUANTITY = 1;
    private static final int DEFAULT_GROUP_NUMBER = -1;
    protected static final String DEFAULT_FIELD_SET = FieldSetLevelHelper.DEFAULT_LEVEL;
    private static final String TELCEL_PRODUCT_CATALOG = "telcelProductCatalog";
    private static final String ONLINE = "Online";
    private static final Collection<ProductOption> PRODUCT_OPTIONS = Arrays.asList(ProductOption.BASIC, ProductOption.DESCRIPTION);
    private static final char PIPE = '|';
    public static final String ES_MX = "es_MX";
    public static final String MXN = "MXN";
    public static final String ESQUEMA_COBRO = "ASL";
    public static final String TELCEL = "telcel";
    private static final String ANONYMOUS = "anonymous";
    private static final int MAX_COOKIE_TIME = 3600;
    private static final String TIENDA_EN_LINEA = "telcel.external.AddCart.tienda.url";
    private static final String TIENDA_EN_LINEA_DOMAIN = "telcel.ctvdat.tienda.domain";
    private static final String EXTERNAL_CART_USERGUESTEXTERNAL_COOKIEE_PATH = "external.cart.userGuestExternal.cookiee.path";
    private static final String EXTERNAL_CART_SPARTACUSTELCELCARTEXTERNAL_COOKIEE_PATH = "external.cart.spartacusTelcelCartExternal.cookiee.path";
    private static final String EXTERNAL_CART_SPARTACUSTELCELCARTCODEEXTERNAL_COOKIEE_PATH = "external.cart.spartacusTelcelCartCodeExternal.cookiee.path";



    @Resource
    private BaseSiteService baseSiteService;
    @Resource(name = "commerceCartService")
    private CommerceCartService commerceCartService;
    @Resource
    private UserService userService;
    @Resource
    private ModelService modelService;
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
    @Resource(name = "tmaProductOfferFacade")
    private TmaProductOfferFacade tmaProductOfferFacade;
    @Resource
    private CatalogVersionService catalogVersionService;
    @Resource(name = "dataMapper")
    private DataMapper dataMapper;
    @Resource
    private CommonI18NService commonI18NService;
    @Resource
    private CartService cartService;
    @Resource(name = "baseStoreService")
    private BaseStoreService baseStoreService;
    @Resource(name = "cartLoaderStrategy")
    private CartLoaderStrategy cartLoaderStrategy;
    @Resource
    private TelcelUtil telcelUtil;
    @Resource(name = "defaultConfigurationService")
    private ConfigurationService configurationService;



    @PostMapping( produces = MediaType.APPLICATION_JSON_VALUE )
    @ResponseBody
    @ApiOperation(nickname = "addCartEntry", value = "Adds a product to the cart from external url.", notes = "...........")
    public ResponseEntity<CartModificationWsDTO> createCartEntry(
            @ApiParam(value = "Base site identifier", required = false)
            @ApiFieldsParam @RequestParam(defaultValue = "telcel")
            final String baseSiteId,
            @ApiParam(value = "product sku identifier", required = true)
            @QueryParam("productSku")
            final String productSku,
            @ApiParam(value = "The entry number. Each entry in a cart has an entry number. Cart entries are numbered in ascending order, starting with zero (0).", required = true)
            @QueryParam("entryNumber")
            final long entryNumber,
            @ApiParam(value = "region identifier", required = false)
            @ApiFieldsParam @RequestParam(defaultValue = "9")
            final String regionId,
            final HttpServletResponse httpServletResponse,
            final HttpServletRequest httpServletRequest
    ) throws CommerceCartModificationException,UnsupportedEncodingException
    {
        final OrderEntryWsDTO entry = new OrderEntryWsDTO();
        LOG.info("### External Add Cart Entry ###");
        //Add Session Data Telcel
        addvaluesInSessionTelcel();

        //Create Cart
        CartWsDTO  cartWsDTO = getRestoredCart("", ANONYMOUS, DEFAULT_FIELD_SET);
        LOG.info(cartWsDTO);
        if(Objects.nonNull(cartWsDTO)){
            LOG.info("cartWsDTO nonNull");
            // Add Qty
            entry.setQuantity(entryNumber);
            // Add Product Code
            ProductWsDTO product = new ProductWsDTO();
            product.setCode(productSku);
            entry.setProduct(product);
            //Add Esquema Cobro
            TelcelEsquemaCobroDTO esquemaCobro = new TelcelEsquemaCobroDTO();
            esquemaCobro.setCode(ESQUEMA_COBRO);
            esquemaCobro.setName("");
            esquemaCobro.setDescription("");
            entry.setEsquemaCobro(esquemaCobro);
            //Method 1
            setAnonymousCartToGuestCart(cartWsDTO.getGuid(),baseSiteId);
            //Method 2
            populateDefaultCartEntryValues(entry);
            //Get groupNumber
            Integer groupNumber = entry.getEntryGroupNumbers().stream().findFirst().get();
            //Get current Customer
            final CustomerModel currentCustomerModel = (CustomerModel) userService.getCurrentUser();
            LOG.info("User ExternalCart:: " + currentCustomerModel.getUid());

            return  addCartEntryInternal(baseSiteId,DEFAULT_FIELD_SET,entry,groupNumber,cartWsDTO.getGuid(),cartWsDTO.getCode(),currentCustomerModel.getUid());

        }else{
            LOG.info("NOK");
        }
        return  null;
    }



    private void setAnonymousCartToGuestCart(String cartId,String baseSiteId) {
        baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID(baseSiteId), false);
        LOG.info("BaseSite::" + baseSiteService.getCurrentBaseSite().getName());
        if (StringUtils.isNotEmpty(cartId)) {
            CartModel cartModel = commerceCartService.getCartForGuidAndSite(cartId, baseSiteService.getCurrentBaseSite());
            if (cartModel != null && cartModel.getUser() != null && StringUtils.contains(cartModel.getUser().getUid(), PIPE)) {
                cartModel.setUser(userService.getAnonymousUser());
                modelService.save(cartModel);
            }
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
                                                                       final OrderEntryWsDTO entry, final Integer cartGroupNo, final String cartId,final String cartCode, final String userId)
            throws CommerceCartModificationException, UnsupportedEncodingException {
        final String urlTienda = configurationService.getConfiguration().getString(TIENDA_EN_LINEA);
        final String domainTienda = configurationService.getConfiguration().getString(TIENDA_EN_LINEA_DOMAIN);

        ResponseEntity<CartModificationWsDTO> responseEntity= null;
        CartActionInput cartActionInput;
        cartActionInput = createCartItemAction(entry, StringUtils.EMPTY, entry.getProcessType(), cartGroupNo, cartId, userId,
                baseSiteId);

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
        }

        final CartModificationData cartModificationData = tmaCartFacade
                .processCartAction(Collections.singletonList(cartActionInput)).get(0);

        final CartModificationWsDTO cartModificationWsDTO = getDataMapper().map(cartModificationData, CartModificationWsDTO.class,
                fields);

        final ResponseCookie userGuestExternal = ResponseCookie.from("userGuestExternal", ANONYMOUS).httpOnly(false).secure(false)
                .domain(domainTienda).path(Config.getString(EXTERNAL_CART_USERGUESTEXTERNAL_COOKIEE_PATH,"/")).maxAge(MAX_COOKIE_TIME).build();
        final ResponseCookie spartacusTelcelCartExternal = ResponseCookie.from("spartacusTelcelCartExternal", cartId).httpOnly(false).secure(false)
                .domain(domainTienda).path(Config.getString(EXTERNAL_CART_SPARTACUSTELCELCARTEXTERNAL_COOKIEE_PATH,"/")).maxAge(MAX_COOKIE_TIME).build();
        final ResponseCookie spartacusTelcelCartCodeExternal = ResponseCookie.from("spartacusTelcelCartCodeExternal", cartCode).httpOnly(false).secure(false)
                .domain(domainTienda).path(Config.getString(EXTERNAL_CART_SPARTACUSTELCELCARTCODEEXTERNAL_COOKIEE_PATH,"/")).maxAge(MAX_COOKIE_TIME).build();
        final ResponseCookie cookieError = ResponseCookie.from("errorExternalCart", URLEncoder.encode("No fue posible crear un carrito desde el servicio externo", "UTF-8")).httpOnly(false)
                .secure(true).domain(domainTienda).path("/").maxAge(MAX_COOKIE_TIME).build();

        if (TelcelCommerceCartModificationStatus.STOCK_ASJUSTMENT.equalsIgnoreCase(cartModificationWsDTO.getStatusCode())
                || CommerceCartModificationStatus.MAX_ORDER_QUANTITY_EXCEEDED.equalsIgnoreCase(cartModificationWsDTO.getStatusCode())
                || TelcelCommerceCartModificationStatus.MAX_ORDER_CHIP_EXPRESS_QUANTITY_EXCEEDED
                .equalsIgnoreCase(cartModificationWsDTO.getStatusCode())
                || TelcelCommerceCartModificationStatus.MAX_ORDER_TOTAL_PRICE_EXCEEDED
                .equalsIgnoreCase(cartModificationWsDTO.getStatusCode()))
        {
            //return new ResponseEntity<>(cartModificationWsDTO, HttpStatus.CONFLICT);
            //return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("https://localhost:4200/cart")).build();
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.SET_COOKIE, userGuestExternal.toString())
                    .header(HttpHeaders.SET_COOKIE, spartacusTelcelCartExternal.toString())
                    .header(HttpHeaders.SET_COOKIE, spartacusTelcelCartCodeExternal.toString())
                    .header(HttpHeaders.SET_COOKIE, cookieError.toString())
                    .location(URI.create(urlTienda+"cart")).build();
        }

        //return new ResponseEntity<>(cartModificationWsDTO, HttpStatus.OK);
        LOG.info("Request-Add-External-Cart" + cartModificationWsDTO);
        //return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("https://localhost:4200/cart")).build();
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.SET_COOKIE, userGuestExternal.toString())
                .header(HttpHeaders.SET_COOKIE, spartacusTelcelCartExternal.toString())
                .header(HttpHeaders.SET_COOKIE, spartacusTelcelCartCodeExternal.toString())
                .location(URI.create(urlTienda+"cart")).build();
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

    private String getProductCode(final OrderEntryWsDTO entry)
    {
        return entry.getProduct() != null ? entry.getProduct().getCode() : null;
    }

    private String getProcessType(final ProcessTypeWsDTO processType)
    {
        return processType != null ? processType.getId() : null;
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
                //throw new LowStockException("Product [" + sanitize(productCode) + "] is currently out of stock", //NOSONAR
                //      LowStockException.NO_STOCK, String.valueOf(entryNumber));
            }
            else
            {
                //throw new ProductLowStockException("Product [" + sanitize(productCode) + "] is currently out of stock",
                //      LowStockException.NO_STOCK, productCode);
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
    DataMapper getDataMapper()
    {
        return dataMapper;
    }

    private CustomerModel getUserById(String userId) {
        UserModel userModel = null;
        try{
            userModel = userService.getUserForUID(userId);
        }catch (UnknownIdentifierException e){
            LOG.info(String.format("User with id: %s not found", userId));
        }

        if (Objects.nonNull(userModel)) {
            return (CustomerModel) userModel;
        }
        return null;
    }

    public void addvaluesInSessionTelcel(){
        //Set Site
        baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID(TELCEL), false);

        //Set CatalogVersion
        catalogVersionService.setSessionCatalogVersion(TELCEL_PRODUCT_CATALOG, ONLINE);
        CatalogVersionModel catalogVersionModel = catalogVersionService.getCatalogVersion(TELCEL_PRODUCT_CATALOG, ONLINE);

        //Set CurrentCurrency
        commonI18NService.setCurrentCurrency(commonI18NService.getCurrency(MXN));
        CurrencyModel currencyToSet = commonI18NService.getCurrency(MXN);
        if (currencyToSet != null && !currencyToSet.equals(commonI18NService.getCurrentCurrency()))
        {
            commonI18NService.setCurrentCurrency(currencyToSet);
            LOG.info("Currency::::: " + commonI18NService.getCurrentCurrency());
        }

        //Set CurrentLanguage
        commonI18NService.setCurrentLanguage(commonI18NService.getLanguage(ES_MX));
        final LanguageModel languageToSet = commonI18NService.getLanguage(ES_MX);
        if (languageToSet != null && !languageToSet.equals(commonI18NService.getCurrentLanguage()))
        {
            commonI18NService.setCurrentLanguage(languageToSet);
            LOG.info("Currency::::: " + commonI18NService.getCurrentLanguage());
        }

    }
    public  CartModel createCartByService(){
        CartModel sessionCart = new CartModel();
        baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID(TELCEL), false);
        BaseSiteModel currentBaseSite = baseSiteService.getCurrentBaseSite();
        BaseStoreModel currentBaseStore = baseStoreService.getBaseStoreForUid(TELCEL);
        try {
            sessionCart= cartService.getSessionCart();
            sessionCart.setSite(currentBaseSite);
            sessionCart.setStore(currentBaseStore);
            LOG.info("## External Service: Create Cart");
            LOG.info("CartCode:: " +sessionCart.getCode());
            LOG.info("CartUid:: " +sessionCart.getGuid());
            modelService.save(sessionCart);
            modelService.refresh(sessionCart);
            cartLoaderStrategy.loadCart(sessionCart.getCode());
        }catch (Exception ex){
            LOG.error("Error, External Service: Create Cart:: "+ex);
        }
        return sessionCart;
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

}
