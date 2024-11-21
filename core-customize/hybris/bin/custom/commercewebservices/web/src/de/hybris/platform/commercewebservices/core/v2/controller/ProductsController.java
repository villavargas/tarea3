/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercewebservices.core.v2.controller;

import de.hybris.platform.b2ctelcocommercewebservicescommons.dto.MoneyWsDTO;
import de.hybris.platform.b2ctelcocommercewebservicescommons.dto.ProductOfferingPriceWsDTO;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.commercefacades.catalog.CatalogFacade;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.ProductReferenceData;
import de.hybris.platform.commercefacades.product.data.ProductReferencesData;
import de.hybris.platform.commercefacades.product.data.ReviewData;
import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.commercefacades.product.data.SuggestionData;
import de.hybris.platform.commercefacades.search.ProductSearchFacade;
import de.hybris.platform.commercefacades.search.data.AutocompleteSuggestionData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commercefacades.storefinder.StoreFinderStockFacade;
import de.hybris.platform.commercefacades.storefinder.data.StoreFinderStockSearchPageData;
import de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.store.data.GeoPoint;
import de.hybris.platform.commercewebservices.core.formatters.WsDateFormatter;
import de.hybris.platform.commercewebservices.core.product.data.ReviewDataList;
import de.hybris.platform.commercewebservices.core.product.data.SuggestionDataList;
import de.hybris.platform.commercewebservices.core.queues.data.ProductExpressUpdateElementData;
import de.hybris.platform.commercewebservices.core.queues.data.ProductExpressUpdateElementDataList;
import de.hybris.platform.commercewebservices.core.queues.impl.ProductExpressUpdateQueue;
import de.hybris.platform.commercewebservices.core.stock.CommerceStockFacade;
import de.hybris.platform.commercewebservices.core.v2.helper.ProductsHelper;
import de.hybris.platform.commercewebservices.core.validator.PointOfServiceValidator;
import de.hybris.platform.commercewebservicescommons.dto.product.CategoryWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.product.ProductReferenceListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.product.ProductReferenceWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.product.ProductWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.product.ReviewListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.product.ReviewWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.product.StockWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.product.SuggestionListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.product.SuggestionWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.queues.ProductExpressUpdateElementListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.search.facetdata.FacetValueWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.search.facetdata.FacetWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.search.facetdata.ProductSearchPageWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.store.StoreFinderStockSearchPageWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.StockSystemException;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.util.Config;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.assertj.core.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import mx.com.telcel.core.daos.msiconfiguration.MSIConfigurationDao;
import mx.com.telcel.core.model.MSIConfigurationModel;
import mx.com.telcel.core.util.TelcelUtil;


/**
 * Web Services Controller to expose the functionality of the
 * {@link de.hybris.platform.commercefacades.product.ProductFacade} and SearchFacade.
 */

@Controller
@Api(tags = "Products")
@RequestMapping(value = "/{baseSiteId}/products")
public class ProductsController extends BaseController
{
	private static final EnumSet<ProductOption> PRODUCT_OPTIONS_SET = EnumSet.allOf(ProductOption.class);
	private static final String MAX_INTEGER = "2147483647";
	private static final int CATALOG_ID_POS = 0;
	private static final int CATALOG_VERSION_POS = 1;
	private static final String COMMA_SEPARATOR = ",";
	private final DecimalFormat decimalFormat = new DecimalFormat("###,##0.00");
	private static final String TELCEL = "telcel";
	private static final Double MONTO_SUPERIOR_DEFAULT = 2500D;
	private static final Double MONTO_INFERIOR_DEFAULT = 900D;
	private static final Integer MONTO_MAYOR_MESES_DEFAULT = 13;
	private static final Integer MONTO_INTERVALO_MESES_DEFAULT = 6;
	private static final String CHIP_CATEGORY = "chip.amigo.chipcategory";
	private static final String CHIP_QUERY = "chip.amigo.query";
	private static final String TELCEL_PRODUCT_CATALOG = "telcelProductCatalog";
	private static final String ONLINE = "Online";
	private static final String USER_DAT = "DAT";
	private static final Logger LOG = LoggerFactory.getLogger(ProductsController.class);

	@Resource(name = "storeFinderStockFacade")
	private StoreFinderStockFacade storeFinderStockFacade;
	@Resource(name = "cwsProductFacade")
	private ProductFacade productFacade;
	@Resource(name = "wsDateFormatter")
	private WsDateFormatter wsDateFormatter;
	@Resource(name = "productSearchFacade")
	private ProductSearchFacade<ProductData> productSearchFacade;
	@Resource(name = "httpRequestReviewDataPopulator")
	private Populator<HttpServletRequest, ReviewData> httpRequestReviewDataPopulator;
	@Resource(name = "reviewValidator")
	private Validator reviewValidator;
	@Resource(name = "reviewDTOValidator")
	private Validator reviewDTOValidator;
	@Resource(name = "commerceStockFacade")
	private CommerceStockFacade commerceStockFacade;
	@Resource(name = "pointOfServiceValidator")
	private PointOfServiceValidator pointOfServiceValidator;
	@Resource(name = "productExpressUpdateQueue")
	private ProductExpressUpdateQueue productExpressUpdateQueue;
	@Resource(name = "catalogFacade")
	private CatalogFacade catalogFacade;
	@Resource(name = "productsHelper")
	private ProductsHelper productsHelper;
	@Resource
	private TelcelUtil telcelUtil;
	@Resource(name = "msiConfigurationDao")
	private MSIConfigurationDao msiConfigurationDao;
	@Resource(name = "sessionService")
	private SessionService sessionService;
	@Resource(name = "catalogVersionService")
	private CatalogVersionService catalogVersionService;

	@RequestMapping(value = "/search", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(nickname = "getProducts", value = "Get a list of products and additional data", notes = "Returns a list of products and additional data, such as available facets, "
			+ "available sorting, and pagination options. It can also include spelling suggestions. To make spelling suggestions work, you need to make sure "
			+ "that \"enableSpellCheck\" on the SearchQuery is set to \"true\" (by default, it should already be set to \"true\"). You also need to have indexed "
			+ "properties configured to be used for spellchecking.")
	@ApiBaseSiteIdParam
	public ProductSearchPageWsDTO getProducts(
			@ApiParam(value = "Serialized query, free text search, facets. The format of a serialized query: freeTextSearch:sort:facetKey1:facetValue1:facetKey2:facetValue2")
			@RequestParam(required = false)
			final String query, @ApiParam(value = "The current result page requested.")
			@RequestParam(defaultValue = DEFAULT_CURRENT_PAGE)
			final int currentPage, @ApiParam(value = "The number of results returned per page.")
			@RequestParam(defaultValue = DEFAULT_PAGE_SIZE)
			final int pageSize, @ApiParam(value = "Sorting method applied to the return results.")
			@RequestParam(required = false)
			final String sort, @ApiParam(value = "The context to be used in the search query.")
			@RequestParam(required = false)
			final String searchQueryContext, @ApiFieldsParam
			@RequestParam(defaultValue = DEFAULT_FIELD_SET)
			final String fields, final HttpServletResponse response)
	{
		final ProductSearchPageWsDTO result = productsHelper.searchProducts(query, currentPage, pageSize, sort,
				addPaginationField(fields), searchQueryContext);
		final MSIConfigurationModel msiConfiguration = msiConfigurationDao.getCurrentAndActivateConfig();
		buildLeyendasProductosMSI(result, msiConfiguration);
		setTotalCountHeader(response, result.getPagination());

		try
		{
			validateChipDATSearch(result, query);
		}
		catch (final Exception e)
		{
			LOG.error("ERROR TO VALIDATE CHIP SEARCH : " + e.getMessage());
		}
		return result;
	}


	@RequestMapping(value = "/search", method = RequestMethod.HEAD)
	@ApiOperation(nickname = "countProducts", value = "Get a header with total number of products.", notes = "In the response header, the \"x-total-count\" indicates the total number of products satisfying a query.")
	@ApiBaseSiteIdParam
	public void countProducts(
			@ApiParam(value = "Serialized query, free text search, facets. The format of a serialized query: freeTextSearch:sort:facetKey1:facetValue1:facetKey2:facetValue2")
			@RequestParam(required = false)
			final String query, final HttpServletResponse response)
	{
		final ProductSearchPageData<SearchStateData, ProductData> result = productsHelper.searchProducts(query, 0, 1, null);
		setTotalCountHeader(response, result.getPagination());
	}


	@RequestMapping(value = "/{productCode}", method = RequestMethod.GET)
	@CacheControl(directive = CacheControlDirective.PRIVATE, maxAge = 120)
	@Cacheable(value = "productCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.CommerceCacheKeyGenerator).generateKey(true,true,#productCode,#fields)")
	@ResponseBody
	@ApiOperation(nickname = "getProduct", value = "Get product details.", notes = "Returns details of a single product according to a product code.")
	@ApiBaseSiteIdParam
	public ProductWsDTO getProduct(@ApiParam(value = "Product identifier", required = true)
	@PathVariable
	final String productCode, @ApiFieldsParam
	@RequestParam(defaultValue = DEFAULT_FIELD_SET)
	final String fields)
	{
		LOG.debug("getProduct: code={} | options={}", sanitize(productCode), PRODUCT_OPTIONS_SET);
		final ProductData product = productFacade.getProductForCodeAndOptions(productCode, PRODUCT_OPTIONS_SET);
		return getDataMapper().map(product, ProductWsDTO.class, fields);
	}


	@RequestMapping(value = "/{productCode}/stock/{storeName}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(nickname = "getStoreProductStock", value = "Get a product's stock level for a store", notes = "Returns a product's stock level for a particular store (in other words, for a particular point of sale).")
	public StockWsDTO getStoreProductStock(@ApiParam(value = "Base site identifier", required = true)
	@PathVariable
	final String baseSiteId, @ApiParam(value = "Product identifier", required = true)
	@PathVariable
	final String productCode, @ApiParam(value = "Store identifier", required = true)
	@PathVariable
	final String storeName, @ApiFieldsParam
	@RequestParam(defaultValue = DEFAULT_FIELD_SET)
	final String fields)
	{
		validate(storeName, "storeName", pointOfServiceValidator);
		if (!commerceStockFacade.isStockSystemEnabled(baseSiteId))
		{
			throw new StockSystemException("Stock system is not enabled on this site", StockSystemException.NOT_ENABLED, baseSiteId);
		}
		final StockData stockData = commerceStockFacade.getStockDataForProductAndPointOfService(productCode, storeName);
		return getDataMapper().map(stockData, StockWsDTO.class, fields);
	}


	@RequestMapping(value = "/{productCode}/stock", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(nickname = "getLocationProductStock", value = "Get a product's stock level.", notes = "Returns a product's stock levels sorted by distance from the specified location, which is provided "
			+ "using the free-text \"location\" parameter, or by using the longitude and latitude parameters. The following two sets of parameters are available: location "
			+ "(required), currentPage (optional), pageSize (optional); or longitude (required), latitude (required), currentPage (optional), pageSize(optional).")
	@ApiBaseSiteIdParam
	public StoreFinderStockSearchPageWsDTO getLocationProductStock(@ApiParam(value = "Product identifier", required = true)
	@PathVariable
	final String productCode, @ApiParam(value = "Free-text location")
	@RequestParam(required = false)
	final String location, @ApiParam(value = "Latitude location parameter.")
	@RequestParam(required = false)
	final Double latitude, @ApiParam(value = "Longitude location parameter.")
	@RequestParam(required = false)
	final Double longitude, @ApiParam(value = "The current result page requested.")
	@RequestParam(required = false, defaultValue = DEFAULT_CURRENT_PAGE)
	final int currentPage, @ApiParam(value = "The number of results returned per page.")
	@RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE)
	final int pageSize, @ApiFieldsParam
	@RequestParam(defaultValue = DEFAULT_FIELD_SET)
	final String fields, final HttpServletResponse response)
	{
		LOG.debug("getLocationProductStock: code={}  | location={} | latitude={} | longitude={}", sanitize(productCode),
				sanitize(location), latitude, longitude);

		final StoreFinderStockSearchPageData result = doSearchProductStockByLocation(productCode, location, latitude, longitude,
				currentPage, pageSize);
		setTotalCountHeader(response, result.getPagination());
		return getDataMapper().map(result, StoreFinderStockSearchPageWsDTO.class, addPaginationField(fields));
	}


	@RequestMapping(value = "/{productCode}/stock", method = RequestMethod.HEAD)
	@ApiOperation(nickname = "countProductStockByLocation", value = "Get header with a total number of product's stock levels.", notes = "In the response header, the \"x-total-count\" indicates the total number of a "
			+ "product's stock levels. The following two sets of parameters are available: location (required); or longitude (required), and latitude (required).")
	@ApiBaseSiteIdParam
	public void countProductStockByLocation(@ApiParam(value = "Product identifier", required = true)
	@PathVariable
	final String productCode, @ApiParam(value = "Free-text location")
	@RequestParam(required = false)
	final String location, @ApiParam(value = "Latitude location parameter.")
	@RequestParam(required = false)
	final Double latitude, @ApiParam(value = "Longitude location parameter.")
	@RequestParam(required = false)
	final Double longitude, final HttpServletResponse response)
	{
		final StoreFinderStockSearchPageData result = doSearchProductStockByLocation(productCode, location, latitude, longitude, 0,
				1);
		setTotalCountHeader(response, result.getPagination());
	}

	protected StoreFinderStockSearchPageData doSearchProductStockByLocation(final String productCode, final String location,
			final Double latitude, final Double longitude, final int currentPage, final int pageSize)
	{
		final Set<ProductOption> opts = EnumSet.of(ProductOption.BASIC);
		final StoreFinderStockSearchPageData result;
		if (latitude != null && longitude != null)
		{
			result = storeFinderStockFacade.productSearch(createGeoPoint(latitude, longitude),
					productFacade.getProductForCodeAndOptions(productCode, opts), createPageableData(currentPage, pageSize, null));
		}
		else if (location != null)
		{
			result = storeFinderStockFacade.productSearch(location, productFacade.getProductForCodeAndOptions(productCode, opts),
					createPageableData(currentPage, pageSize, null));
		}
		else
		{
			throw new RequestParameterException("You need to provide location or longitute and latitute parameters",
					RequestParameterException.MISSING, "location or longitute and latitute");
		}
		return result;
	}


	@RequestMapping(value = "/{productCode}/reviews", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(nickname = "getProductReviews", value = "Get reviews for a product", notes = "Returns the reviews for a product with a given product code.")
	@ApiBaseSiteIdParam
	public ReviewListWsDTO getProductReviews(@ApiParam(value = "Product identifier", required = true)
	@PathVariable
	final String productCode, @ApiParam(value = "Maximum count of reviews")
	@RequestParam(required = false)
	final Integer maxCount, @ApiFieldsParam
	@RequestParam(defaultValue = DEFAULT_FIELD_SET)
	final String fields)
	{
		final ReviewDataList reviewDataList = new ReviewDataList();
		reviewDataList.setReviews(productFacade.getReviews(productCode, maxCount));
		return getDataMapper().map(reviewDataList, ReviewListWsDTO.class, fields);
	}

	/**
	 * @deprecated since 2005. Please use {@link ProductsController#createProductReview(String, ReviewWsDTO, String)}
	 *             instead.
	 */
	@Deprecated(since = "2005", forRemoval = true)
	@RequestMapping(value = "/{productCode}/reviews", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	@ApiOperation(hidden = true, value = "Creates a new customer review as an anonymous user", notes = "Creates a new customer review as an anonymous user.")
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "headline", value = "Headline of customer review", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "comment", value = "Comment of customer review", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "rating", value = "Rating of customer review", required = true, dataType = "Double", paramType = "query"),
			@ApiImplicitParam(name = "alias", value = "Alias of customer review", dataType = "String", paramType = "query") })
	@ApiBaseSiteIdParam
	public ReviewWsDTO createProductReview(@ApiParam(value = "Product identifier", required = true)
	@PathVariable
	final String productCode, @ApiFieldsParam
	@RequestParam(defaultValue = DEFAULT_FIELD_SET)
	final String fields, final HttpServletRequest request)
	{
		final ReviewData reviewData = new ReviewData();
		httpRequestReviewDataPopulator.populate(request, reviewData);
		validate(reviewData, "reviewData", reviewValidator);
		final ReviewData reviewDataRet = productFacade.postReview(productCode, reviewData);
		return getDataMapper().map(reviewDataRet, ReviewWsDTO.class, fields);
	}


	@RequestMapping(value = "/{productCode}/reviews", method = RequestMethod.POST, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	@ApiOperation(nickname = "createProductReview", value = "Creates a new customer review as an anonymous user.", notes = "Creates a new customer review as an anonymous user.")
	@ApiBaseSiteIdParam
	public ReviewWsDTO createProductReview(@ApiParam(value = "Product identifier", required = true)
	@PathVariable
	final String productCode,
			@ApiParam(value = "Object contains review details like : rating, alias, headline, comment", required = true)
			@RequestBody
			final ReviewWsDTO review, @ApiFieldsParam
			@RequestParam(defaultValue = DEFAULT_FIELD_SET)
			final String fields)
	{
		validate(review, "review", reviewDTOValidator);
		final ReviewData reviewData = getDataMapper().map(review, ReviewData.class, "alias,rating,headline,comment");
		final ReviewData reviewDataRet = productFacade.postReview(productCode, reviewData);
		return getDataMapper().map(reviewDataRet, ReviewWsDTO.class, fields);
	}


	@RequestMapping(value = "/{productCode}/references", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(nickname = "getProductReferences", value = "Get a product reference", notes = "Returns references for a product with a given product code. Reference type specifies which references to return.")
	@ApiBaseSiteIdParam
	public ProductReferenceListWsDTO getProductReferences(@ApiParam(value = "Product identifier", required = true)
	@PathVariable
	final String productCode, @ApiParam(value = "Maximum size of returned results.")
	@RequestParam(required = false, defaultValue = MAX_INTEGER)
	final int pageSize,
			@ApiParam(value = "Comma-separated list of reference types according to enum ProductReferenceTypeEnum. If not specified, all types of product references will be used.")
			@RequestParam(required = false)
			final String referenceType, @ApiFieldsParam
			@RequestParam(defaultValue = DEFAULT_FIELD_SET)
			final String fields)
	{
		final List<ProductOption> opts = Lists.newArrayList(PRODUCT_OPTIONS_SET);

		final List<ProductReferenceTypeEnum> productReferenceTypeList = StringUtils.isNotEmpty(referenceType)
				? getProductReferenceTypeEnums(referenceType)
				: List.of(ProductReferenceTypeEnum.values());

		final List<ProductReferenceData> productReferences = productFacade.getProductReferencesForCode(productCode,
				productReferenceTypeList, opts, Integer.valueOf(pageSize));
		final ProductReferencesData productReferencesData = new ProductReferencesData();
		formatsFilter(productReferences);

		productReferencesData.setReferences(productReferences);
		//return  getDataMapper().map(productReferencesData, ProductReferenceListWsDTO.class, fields);
		final ProductReferenceListWsDTO productReferenceListWsDTO = getDataMapper().map(productReferencesData,
				ProductReferenceListWsDTO.class, fields);

		return filteringPricesByRegion(productReferenceListWsDTO);
	}

	protected List<ProductReferenceTypeEnum> getProductReferenceTypeEnums(final String referenceType)
	{
		final String[] referenceTypes = referenceType.split(COMMA_SEPARATOR);
		return Arrays.stream(referenceTypes).map(ProductReferenceTypeEnum::valueOf).collect(Collectors.toList());
	}

	protected PageableData createPageableData(final int currentPage, final int pageSize, final String sort)
	{
		final PageableData pageable = new PageableData();

		pageable.setCurrentPage(currentPage);
		pageable.setPageSize(pageSize);
		pageable.setSort(sort);
		return pageable;
	}

	protected GeoPoint createGeoPoint(final Double latitude, final Double longitude)
	{
		final GeoPoint point = new GeoPoint();
		point.setLatitude(latitude.doubleValue());
		point.setLongitude(longitude.doubleValue());

		return point;
	}



	@RequestMapping(value = "/suggestions", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(nickname = "getSuggestions", value = "Get a list of available suggestions", notes = "Returns a list of all available suggestions related to a given term and limits the results to a specific value of the max parameter.")
	@ApiBaseSiteIdParam
	public SuggestionListWsDTO getSuggestions(@ApiParam(value = "Specified term", required = true)
	@RequestParam
	final String term, @ApiParam(value = "Specifies the limit of results.")
	@RequestParam(defaultValue = "10")
	final int max, @ApiFieldsParam
	@RequestParam(defaultValue = DEFAULT_FIELD_SET)
	final String fields)
	{
		final List<SuggestionData> suggestions = new ArrayList<>();
		final SuggestionDataList suggestionDataList = new SuggestionDataList();

		List<AutocompleteSuggestionData> autoSuggestions = productSearchFacade.getAutocompleteSuggestions(term);
		if (max < autoSuggestions.size())
		{
			autoSuggestions = autoSuggestions.subList(0, max);
		}

		for (final AutocompleteSuggestionData autoSuggestion : autoSuggestions)
		{
			final SuggestionData suggestionData = new SuggestionData();
			suggestionData.setValue(autoSuggestion.getTerm());
			suggestions.add(suggestionData);
		}
		suggestionDataList.setSuggestions(suggestions);
		final SuggestionListWsDTO suggestionsList = getDataMapper().map(suggestionDataList, SuggestionListWsDTO.class, fields);
		validateChipDATSuggestion(suggestionsList);
		return suggestionsList;
	}


	@Secured("ROLE_TRUSTED_CLIENT")
	@RequestMapping(value = "/expressupdate", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(nickname = "getExpressUpdateProducts", value = "Get products added to the express update feed.", notes = "Returns products added to the express update feed. Returns only elements "
			+ "updated after the provided timestamp. The queue is cleared using a defined cronjob.", authorizations =
	{ @Authorization(value = "oauth2_client_credentials") })
	@ApiBaseSiteIdParam
	public ProductExpressUpdateElementListWsDTO getExpressUpdateProducts(
			@ApiParam(value = "Only items newer than the given parameter are retrieved from the queue. This parameter should be in ISO-8601 format.", required = true)
			@RequestParam
			final String timestamp,
			@ApiParam(value = "Only products from this catalog are returned. Format: catalogId:catalogVersion")
			@RequestParam(required = false)
			final String catalog, @ApiFieldsParam
			@RequestParam(defaultValue = DEFAULT_FIELD_SET)
			final String fields)
	{
		final Date timestampDate;
		try
		{
			timestampDate = wsDateFormatter.toDate(timestamp);
		}
		catch (final IllegalArgumentException ex)
		{
			throw new RequestParameterException("Wrong time format. The only accepted format is ISO-8601.",
					RequestParameterException.INVALID, "timestamp", ex);
		}
		final ProductExpressUpdateElementDataList productExpressUpdateElementDataList = new ProductExpressUpdateElementDataList();
		final List<ProductExpressUpdateElementData> products = productExpressUpdateQueue.getItems(timestampDate);
		filterExpressUpdateQueue(products, validateAndSplitCatalog(catalog));
		productExpressUpdateElementDataList.setProductExpressUpdateElements(products);
		return getDataMapper().map(productExpressUpdateElementDataList, ProductExpressUpdateElementListWsDTO.class, fields);
	}

	protected void filterExpressUpdateQueue(final List<ProductExpressUpdateElementData> products, final List<String> catalogInfo)
	{
		if (catalogInfo.size() == 2 && StringUtils.isNotEmpty(catalogInfo.get(CATALOG_ID_POS))
				&& StringUtils.isNotEmpty(catalogInfo.get(CATALOG_VERSION_POS)) && CollectionUtils.isNotEmpty(products))
		{
			final Iterator<ProductExpressUpdateElementData> dataIterator = products.iterator();
			while (dataIterator.hasNext())
			{
				final ProductExpressUpdateElementData productExpressUpdateElementData = dataIterator.next();
				if (!catalogInfo.get(CATALOG_ID_POS).equals(productExpressUpdateElementData.getCatalogId())
						|| !catalogInfo.get(CATALOG_VERSION_POS).equals(productExpressUpdateElementData.getCatalogVersion()))
				{
					dataIterator.remove();
				}
			}
		}
	}

	protected List<String> validateAndSplitCatalog(final String catalog)
	{
		final List<String> catalogInfo = new ArrayList<>();
		if (StringUtils.isNotEmpty(catalog))
		{
			catalogInfo.addAll(Lists.newArrayList(Splitter.on(':').trimResults().omitEmptyStrings().split(catalog)));
			if (catalogInfo.size() == 2)
			{
				catalogFacade.getProductCatalogVersionForTheCurrentSite(catalogInfo.get(CATALOG_ID_POS),
						catalogInfo.get(CATALOG_VERSION_POS), Collections.emptySet());
			}
			else if (!catalogInfo.isEmpty())
			{
				throw new RequestParameterException("Invalid format. You have to provide catalog as 'catalogId:catalogVersion'",
						RequestParameterException.INVALID, "catalog");
			}
		}
		return catalogInfo;
	}

	protected List<ProductReferenceData> formatsFilter(final List<ProductReferenceData> productReferencesList)
	{
		final Collection<ImageData> imageDataListTemp = new HashSet<>();
		productReferencesList.stream().forEach(productRef -> {
			imageDataListTemp.clear();
			productRef.getTarget().getImages().forEach(imageData -> {
				if (!imageData.getFormat().equals("product") && !imageData.getImageType().name().equals("PRIMARY"))
				{
					imageDataListTemp.add(imageData);
				}
				else if (!imageData.getFormat().equals("thumbnail") && !imageData.getImageType().name().equals("PRIMARY"))
				{
					imageDataListTemp.add(imageData);
				}
				else if (imageData.getFormat().equals("zoom") && imageData.getImageType().name().equals("PRIMARY"))
				{
					imageDataListTemp.add(imageData);
				}
				else if (imageData.getFormat().equals("cartIcon") && imageData.getImageType().name().equals("PRIMARY"))
				{
					imageDataListTemp.add(imageData);
				}
			});
			productRef.getTarget().getImages().removeAll(imageDataListTemp);

		});
		return productReferencesList;
	}

	protected ProductReferenceListWsDTO filteringPricesByRegion(final ProductReferenceListWsDTO productReferenceListWsDTO)
	{
		final List<ProductOfferingPriceWsDTO> productOfferingPriceWsDTOTemp = new ArrayList<>();
		final RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		final String region = Objects.nonNull(requestAttributes)
				? telcelUtil.getRegionByRequest(((ServletRequestAttributes) requestAttributes).getRequest())
				: StringUtils.EMPTY;
		LOG.info("Filtering_Prices_By_Region:: " + region);

		for (final ProductReferenceWsDTO productReferenceWsDTO : productReferenceListWsDTO.getReferences())
		{
			productOfferingPriceWsDTOTemp.clear();
			productReferenceWsDTO.getTarget().getProductOfferingPrice().stream().forEach(productOffePriceData -> {
				if (productOffePriceData.getUserPriceGroupID() != null && !productOffePriceData.getUserPriceGroupID().equals(region))
				{
					productOfferingPriceWsDTOTemp.add(productOffePriceData);
				}
			});
			productReferenceWsDTO.getTarget().getProductOfferingPrice().removeAll(productOfferingPriceWsDTOTemp);

			LOG.info("Product:: " + productReferenceWsDTO.getTarget().getCode() + " Price List:: "
					+ productReferenceWsDTO.getTarget().getProductOfferingPrice().size());
		}

		return productReferenceListWsDTO;
	}

	private void buildLeyendasProductosMSI(final ProductSearchPageWsDTO result, final MSIConfigurationModel msiConfiguration)
	{
		final List<ProductWsDTO> products = result.getProducts();
		for (final ProductWsDTO productDTO : products)
		{
			final ProductOfferingPriceWsDTO offeringPrice = productDTO.getProductOfferingPrice().iterator().next();
			final ProductOfferingPriceWsDTO bof = offeringPrice.getBundledPop().iterator().next();
			final MoneyWsDTO mp = bof.getPrice();
			final String leyenda = buildLeyendaMSI(msiConfiguration, Double.valueOf(mp.getValue()));
			productDTO.setLeyendaMSI(leyenda);
		}
	}

	private String buildLeyendaMSI(final MSIConfigurationModel msiConfiguration, final Double precioProducto)
	{
		String leyenda = "";
		Double montoSuperior = MONTO_SUPERIOR_DEFAULT;
		Double montoInferior = MONTO_INFERIOR_DEFAULT;
		Integer montoMayorMeses = MONTO_MAYOR_MESES_DEFAULT;
		Integer montoIntervaloMeses = MONTO_INTERVALO_MESES_DEFAULT;
		if (Objects.nonNull(msiConfiguration))
		{
			montoSuperior = Objects.nonNull(msiConfiguration.getMsiMontoSuperior()) ? msiConfiguration.getMsiMontoSuperior()
					: MONTO_SUPERIOR_DEFAULT;
			montoInferior = Objects.nonNull(msiConfiguration.getMsiMontoInferior()) ? msiConfiguration.getMsiMontoInferior()
					: MONTO_INFERIOR_DEFAULT;
			montoMayorMeses = Objects.nonNull(msiConfiguration.getMsiMontoMayorMeses()) ? msiConfiguration.getMsiMontoMayorMeses()
					: MONTO_MAYOR_MESES_DEFAULT;
			montoIntervaloMeses = Objects.nonNull(msiConfiguration.getMsiMontoIntervaloMeses())
					? msiConfiguration.getMsiMontoIntervaloMeses()
					: MONTO_INTERVALO_MESES_DEFAULT;
		}
		if (precioProducto >= montoSuperior)
		{
			final BigDecimal prodOfferPrice = BigDecimal.valueOf(precioProducto / montoMayorMeses);
			leyenda = "o en hasta " + montoMayorMeses + " pagos de " + "<b>$" + decimalFormat.format(prodOfferPrice.doubleValue())
					+ "</b>";
		}
		else if (precioProducto >= montoInferior && precioProducto < montoSuperior)
		{
			final BigDecimal prodOfferPrice = BigDecimal.valueOf(precioProducto / montoIntervaloMeses);
			leyenda = "o en hasta " + montoIntervaloMeses + " pagos de " + "<b>$"
					+ decimalFormat.format(prodOfferPrice.doubleValue()) + "</b>";
		}
		else if (precioProducto < montoInferior)
		{
			leyenda = "";
		}
		return leyenda;
	}

	private void validateChipDATSearch(final ProductSearchPageWsDTO result, final String query)
	{
		final String typeUserCVTDAT = getTypeUserCVTDAT();

		//Validate query allCategory chip
		if (typeUserCVTDAT.equals(USER_DAT) && query.equals(Config.getParameter(CHIP_QUERY)))
		{
			result.getPagination().setTotalResults(0L);
			result.getPagination().setTotalPages(0);
			result.setProducts(new ArrayList<>());
			result.setFacets(new ArrayList<>());
		}
		//Validate query product
		if (isNumber(query))
		{
			ProductData product = null;
			try
			{
				product = productFacade.getProductForCodeAndOptions(query, PRODUCT_OPTIONS_SET);
			}
			catch (final Exception e)
			{
				product = null;
			}
			if (Objects.nonNull(product))
			{
				if (validateChipProductDAT(product))
				{
					result.getPagination().setTotalResults(0L);
					result.getPagination().setTotalPages(0);
					result.setProducts(new ArrayList<>());
					result.setFacets(new ArrayList<>());
				}
			}
		}
		//Validate Facet query
		if (Objects.nonNull(result.getFacets()) && typeUserCVTDAT.equals(USER_DAT))
		{
			for (final FacetWsDTO facet : result.getFacets())
			{
				if (Objects.nonNull(facet.getValues()) && !facet.getValues().isEmpty())
				{
					final FacetValueWsDTO v = facet.getValues().get(0);
					if (v.getQuery().getQuery().getValue().contains(Config.getParameter(CHIP_CATEGORY)))
					{
						result.getPagination().setTotalResults(0L);
						result.getPagination().setTotalPages(0);
						result.setProducts(new ArrayList<>());
						result.setFacets(new ArrayList<>());
					}
				}
			}
		}

		catalogVersionService.setSessionCatalogVersion(TELCEL_PRODUCT_CATALOG, ONLINE);
		if (typeUserCVTDAT.equals(USER_DAT))
		{
			final List<ProductWsDTO> products = result.getProducts();
			for (int i = products.size() - 1; i >= 0; i--)
			{
				final ProductWsDTO productDTO = products.get(i);
				if (containsCategoryWsDTO(productDTO.getCategories(), Config.getParameter(CHIP_CATEGORY)))
				{
					products.remove(i);
				}
			}
		}
	}

	private void validateChipDATSuggestion(final SuggestionListWsDTO suggestionsList)
	{
		catalogVersionService.setSessionCatalogVersion(TELCEL_PRODUCT_CATALOG, ONLINE);
		final String typeUserCVTDAT = getTypeUserCVTDAT();
		if (typeUserCVTDAT.equals(USER_DAT))
		{
			final List<SuggestionWsDTO> suggestions = suggestionsList.getSuggestions();
			for (int i = suggestions.size() - 1; i >= 0; i--)
			{
				final SuggestionWsDTO s = suggestions.get(i);
				ProductData product = null;
				try
				{
					product = productFacade.getProductForCodeAndOptions(s.getValue(), PRODUCT_OPTIONS_SET);
				}
				catch (final Exception e)
				{
					product = null;
				}
				if (Objects.nonNull(product))
				{
					if (Objects.nonNull(product.getCategories()))
					{
						if (containsCategoryData(product.getCategories(), Config.getParameter(CHIP_CATEGORY)))
						{
							suggestions.remove(i);
						}
					}
				}
			}
		}
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

	private boolean containsCategoryData(final Collection<CategoryData> list, final String code)
	{
		return list.stream().filter(o -> o.getCode().equals(code)).findFirst().isPresent();
	}

	private boolean containsCategoryWsDTO(final Collection<CategoryWsDTO> list, final String code)
	{
		return list.stream().filter(o -> o.getCode().equals(code)).findFirst().isPresent();
	}

	private boolean validateChipProductDAT(final ProductData product)
	{
		catalogVersionService.setSessionCatalogVersion(TELCEL_PRODUCT_CATALOG, ONLINE);
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
		if (typeUserCVTDAT.equals(USER_DAT))
		{
			if (Objects.nonNull(product))
			{
				if (Objects.nonNull(product.getCategories()))
				{
					if (containsCategoryData(product.getCategories(), Config.getParameter(CHIP_CATEGORY)))
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean isNumber(final String number)
	{
		final Pattern pattern = Pattern.compile("^\\d+$");
		final Matcher matcher = pattern.matcher(number);
		return matcher.matches();
	}

}
