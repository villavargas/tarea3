package mx.com.telcel.facades.populators;

import de.hybris.platform.acceleratorcms.model.components.SimpleResponsiveBannerComponentModel;
import de.hybris.platform.b2ctelcocommercewebservicescommons.dto.MoneyWsDTO;
import de.hybris.platform.b2ctelcocommercewebservicescommons.dto.ProductOfferingPriceWsDTO;
import de.hybris.platform.b2ctelcofacades.product.TmaProductOfferFacade;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cmsfacades.data.AbstractCMSComponentData;
import de.hybris.platform.cmsfacades.rendering.populators.CMSComponentModelToDataRenderingPopulator;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.ProductSearchFacade;
import de.hybris.platform.commercefacades.search.data.SearchFilterQueryData;
import de.hybris.platform.commercefacades.search.data.SearchQueryData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.enums.SearchQueryContext;
import de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryTermData;
import de.hybris.platform.commercewebservicescommons.dto.product.PriceWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.product.ProductWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.assertj.core.util.Strings;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import mx.com.telcel.core.daos.msiconfiguration.MSIConfigurationDao;
import mx.com.telcel.core.model.MSIConfigurationModel;
import mx.com.telcel.core.util.TelcelUtil;


public class TelcelCMSComponentModelToDataRenderingPopulator extends CMSComponentModelToDataRenderingPopulator
{

	public static final String NO_APLICA = "No aplica";
	public static final String TELCEL_RECOMMENDATIONS_PRODUCT_CAROUSEL_COMPONENT = "TelcelRecommendationsProductCarouselComponent";
	public static final String ACCESORIO_COMPONENT = "Accesorio";
	public static final String PRODUCT_CODES = "productCodes";
	protected static final int NEXT_TERM = 2;
	public static final String TELCEL_RECOMMENDATIONS_PRODUCT_INTERNET_CAROUSEL_COMPONENT = "TelcelRecommendationsProductInternetCarouselComponent";
	public static final String TELCEL_RECOMMENDATIONS_PRODUCT_TABLETS_CAROUSEL_COMPONENT = "TelcelRecommendationsProductTabletsCarouselComponent";
	public static final String PRODUCT_DETAIL_LIST = "productDetailList";
	private static final Collection<ProductOption> PRODUCT_OPTIONS = Arrays.asList(ProductOption.BASIC, ProductOption.DESCRIPTION,
			ProductOption.SOLD_INDIVIDUALLY, ProductOption.PRODUCT_SPECIFICATION, ProductOption.CATEGORIES,
			ProductOption.PRODUCT_MEDIA, ProductOption.PRODUCT_BPO_CHILDREN, ProductOption.PRODUCT_OFFERING_PRICES,
			ProductOption.PARENT_BPOS, ProductOption.PRODUCT_OFFERING_GROUPS, ProductOption.PRODUCT_SPEC_CHAR_VALUE_USE,
			ProductOption.CLASSIFICATION, ProductOption.VARIANT_MATRIX, ProductOption.VARIANT_MATRIX_ALL_OPTIONS,
			ProductOption.MEDIA_ATTACHMENT, ProductOption.REVIEW);
	private final DecimalFormat decimalFormat = new DecimalFormat("###,##0.00");
	private static final String TELCEL = "telcel";
	private static final Double MONTO_SUPERIOR_DEFAULT = 2500D;
	private static final Double MONTO_INFERIOR_DEFAULT = 900D;
	private static final Integer MONTO_MAYOR_MESES_DEFAULT = 13;
	private static final Integer MONTO_INTERVALO_MESES_DEFAULT = 6;
	private static final String CHIP_SECTION = "chip.amigo.component.section";
	private static final String CHIP_BANNER = "chip.amigo.banner";
	private static final String USER_DAT = "DAT";

	@Resource(name = "productSearchFacade")
	private ProductSearchFacade<ProductData> productSearchFacade;

	@Resource(name = "dataMapper")
	private DataMapper dataMapper;

	@Resource(name = "solrSearchStateConverter")
	private Converter<SolrSearchQueryData, SearchStateData> solrSearchStateConverter;

	@Resource(name = "tmaProductOfferFacade")
	private TmaProductOfferFacade tmaProductOfferFacade;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "msiConfigurationDao")
	private MSIConfigurationDao msiConfigurationDao;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource
	private TelcelUtil telcelUtil;

	private static final Logger LOG = Logger.getLogger(TelcelCMSComponentModelToDataRenderingPopulator.class);

	@Override
	public void populate(final AbstractCMSComponentModel componentModel, final AbstractCMSComponentData componentData)
	{
		final String queryCustom = ":relevance:allCategories:telefonos-y-smartphones";
		final String queryTabletasCustom = ":relevance:allCategories:tablets";
		final String queryVidaConectadaCustom = ":relevance:allCategories:vida_conectada";
		final String queryAccesoriosCustom = ":relevance:allCategories:accesorios";
		final String searchQueryContext = null;

		String typeUserCVTDAT = sessionService.getAttribute("typeUserCVTDAT");
		if (Strings.isNullOrEmpty(typeUserCVTDAT))
		{
			final RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
			typeUserCVTDAT = Objects.nonNull(requestAttributes)
					? telcelUtil.getCookieByRequest(((ServletRequestAttributes) requestAttributes).getRequest(), "typeUserCVTDAT")
					: StringUtils.EMPTY;
			typeUserCVTDAT = typeUserCVTDAT == null ? "" : typeUserCVTDAT;
			//LOG.debug(String.format("Debug - cookie typeUserCVTDAT: %s", typeUserCVTDAT));
		}
		//Component Banner Chip
		if (typeUserCVTDAT.equals(USER_DAT) && componentModel.getUid().equals(Config.getParameter(CHIP_SECTION)))
		{
			return;
		}
		if (typeUserCVTDAT.equals(USER_DAT) && componentModel.getUid().equals(Config.getParameter(CHIP_BANNER)))
		{
			return;
		}

		componentData.setUid(componentModel.getUid());
		componentData.setTypeCode(componentModel.getItemtype());
		componentData.setModifiedtime(componentModel.getModifiedtime());
		componentData.setName(componentModel.getName());

		componentData.setCatalogVersion( //
				getUniqueIdentifierAttributeToDataContentConverter().convert(componentModel.getCatalogVersion()));
		componentData.setUuid( //
				getUniqueIdentifierAttributeToDataContentConverter().convert(componentModel));

		componentData.setOtherProperties(getCmsItemConverter().convert(componentModel));

		final UserModel userModel = userService.getCurrentUser();
		String applicableUID = "";
		if (Objects.nonNull(userModel))
		{
			applicableUID = userModel.getUid();
		}

		if (TELCEL_RECOMMENDATIONS_PRODUCT_INTERNET_CAROUSEL_COMPONENT.equalsIgnoreCase(componentModel.getUid()))
		{

			final SearchQueryContext context = decodeContext(searchQueryContext);

			final ProductSearchPageData<SearchStateData, ProductData> sourceResult = searchProductsCustom(queryVidaConectadaCustom,
					0, 10, null, context);
			final List<ProductData> lista = sourceResult.getResults();

			final ArrayList<String> array = (ArrayList<String>) componentData.getOtherProperties().get(PRODUCT_CODES);
			array.clear();
			if (CollectionUtils.isNotEmpty(lista))
			{
				for (final ProductData productWsDTO : lista)
				{
					array.add(productWsDTO.getCode());
				}
			}
			componentData.getOtherProperties().put(PRODUCT_DETAIL_LIST, getProductsDetail(lista, applicableUID));
		}

		if (TELCEL_RECOMMENDATIONS_PRODUCT_TABLETS_CAROUSEL_COMPONENT.equalsIgnoreCase(componentModel.getUid()))
		{

			final SearchQueryContext context = decodeContext(searchQueryContext);

			final ProductSearchPageData<SearchStateData, ProductData> sourceResult = searchProductsCustom(queryTabletasCustom, 0, 10,
					null, context);
			final List<ProductData> lista = sourceResult.getResults();

			final ArrayList<String> array = (ArrayList<String>) componentData.getOtherProperties().get(PRODUCT_CODES);
			array.clear();
			if (CollectionUtils.isNotEmpty(lista))
			{
				for (final ProductData productWsDTO : lista)
				{
					array.add(productWsDTO.getCode());
				}
			}
			componentData.getOtherProperties().put(PRODUCT_DETAIL_LIST, getProductsDetail(lista, applicableUID));
		}

		if (TELCEL_RECOMMENDATIONS_PRODUCT_CAROUSEL_COMPONENT.equalsIgnoreCase(componentModel.getUid()))
		{

			final SearchQueryContext context = decodeContext(searchQueryContext);

			final ProductSearchPageData<SearchStateData, ProductData> sourceResult = searchProductsCustom(queryCustom, 0, 10, null,
					context);
			final List<ProductData> lista = sourceResult.getResults();

			final ArrayList<String> array = (ArrayList<String>) componentData.getOtherProperties().get(PRODUCT_CODES);
			array.clear();
			if (CollectionUtils.isNotEmpty(lista))
			{
				for (final ProductData productWsDTO : lista)
				{
					array.add(productWsDTO.getCode());
				}
			}
			componentData.getOtherProperties().put(PRODUCT_DETAIL_LIST, getProductsDetail(lista, applicableUID));
		}
		if (ACCESORIO_COMPONENT.equalsIgnoreCase(componentModel.getUid()))
		{
			final SearchQueryContext context = decodeContext(searchQueryContext);

			final ProductSearchPageData<SearchStateData, ProductData> sourceResult = searchProductsCustom(queryAccesoriosCustom, 0,
					10, null, context);
			final List<ProductData> lista = sourceResult.getResults();

			final ArrayList<String> array = (ArrayList<String>) componentData.getOtherProperties().get(PRODUCT_CODES);
			array.clear();
			if (CollectionUtils.isNotEmpty(lista))
			{
				for (final ProductData productWsDTO : lista)
				{
					array.add(productWsDTO.getCode());
				}
			}
			componentData.getOtherProperties().put(PRODUCT_DETAIL_LIST, getProductsDetail(lista, applicableUID));
		}


		if (componentData.getTypeCode().equals("BannerComponent")
				|| componentData.getTypeCode().equals("SimpleResponsiveBannerComponent"))
		{
			final Map<String, Object> otherProperties = new HashMap<>();

			final String tipoBanner = componentModel.getTipoBanner() == null || componentModel.getTipoBanner().equals("") ? NO_APLICA
					: componentModel.getTipoBanner();

			if (componentModel instanceof SimpleResponsiveBannerComponentModel)
			{
				final SimpleResponsiveBannerComponentModel simpleResponsiveBannerComponentModel = (SimpleResponsiveBannerComponentModel) componentModel;
				final String title = simpleResponsiveBannerComponentModel.getTitle() == null
						|| simpleResponsiveBannerComponentModel.getTitle().equals("") ? NO_APLICA
								: simpleResponsiveBannerComponentModel.getTitle();
				final String caption = simpleResponsiveBannerComponentModel.getCaption() == null
						|| simpleResponsiveBannerComponentModel.getCaption().equals("") ? NO_APLICA
								: simpleResponsiveBannerComponentModel.getCaption();

				otherProperties.put("title", title);
				otherProperties.put("caption", caption);
			}

			final String posicion = componentModel.getPosicion() == null || componentModel.getPosicion().equals("") ? NO_APLICA
					: componentModel.getPosicion();
			final String categoria = componentModel.getCategoria() == null || componentModel.getCategoria().equals("") ? NO_APLICA
					: componentModel.getCategoria();
			final String subcategoria = componentModel.getSubcategoria() == null || componentModel.getSubcategoria().equals("")
					? NO_APLICA
					: componentModel.getSubcategoria();
			final String nombre = componentModel.getNombre() == null || componentModel.getNombre().equals("") ? NO_APLICA
					: componentModel.getNombre();
			final String ubicacion = componentModel.getUbicacion() == null || componentModel.getUbicacion().equals("") ? NO_APLICA
					: componentModel.getUbicacion();

			otherProperties.put("tipoBanner", tipoBanner);
			otherProperties.put("posicion", posicion);
			otherProperties.put("categoria", categoria);
			otherProperties.put("subcategoria", subcategoria);
			otherProperties.put("nombre", nombre);
			otherProperties.put("ubicacion", ubicacion);

			if (!componentData.getOtherProperties().isEmpty())
			{
				componentData.getOtherProperties().putAll(otherProperties);
			}
			else
			{
				componentData.setOtherProperties(otherProperties);
			}
		}
	}

	protected ProductSearchPageData<SearchStateData, ProductData> searchProductsCustom(final String query, final int currentPage,
			final int pageSize, final String sort, final SearchQueryContext searchQueryContext)
	{
		final SolrSearchQueryData searchQueryData = decodeQuery(query);
		searchQueryData.setSearchQueryContext(searchQueryContext);

		final de.hybris.platform.commerceservices.search.pagedata.PageableData pageable = this.createPageableData(currentPage,
				pageSize, sort);

		final SearchStateData searchStateData = new SearchStateData();
		final SearchQueryData queryData = new SearchQueryData();
		final List<SearchFilterQueryData> filterQueries = new ArrayList<SearchFilterQueryData>();
		queryData.setFilterQueries(filterQueries);
		queryData.setValue(query);
		searchStateData.setQuery(queryData);

		searchStateData.setQuery(queryData);
		searchStateData.setUrl("/search?q=%3Arelevance%3AallCategories%3Atelefonos-y-smartphones");

		return productSearchFacade.textSearch(solrSearchStateConverter.convert(searchQueryData), pageable);
	}

	protected SearchQueryContext decodeContext(final String searchQueryContext)
	{
		if (StringUtils.isBlank(searchQueryContext))
		{
			return null;
		}

		try
		{
			return SearchQueryContext.valueOf(searchQueryContext);
		}
		catch (final IllegalArgumentException e)
		{
			throw new RequestParameterException(searchQueryContext + " context does not exist", RequestParameterException.INVALID,
					e);
		}
	}

	public SolrSearchQueryData decodeQuery(final String queryString)
	{
		final SolrSearchQueryData searchQuery = new SolrSearchQueryData();
		final List<SolrSearchQueryTermData> filters = new ArrayList<SolrSearchQueryTermData>();

		if (queryString == null)
		{
			return searchQuery;
		}

		final String[] parts = queryString.split(":");

		if (parts.length > 0)
		{
			searchQuery.setFreeTextSearch(parts[0]);
			if (parts.length > 1)
			{
				searchQuery.setSort(parts[1]);
			}
		}

		for (int i = NEXT_TERM; i < parts.length; i = i + NEXT_TERM)
		{
			final SolrSearchQueryTermData term = new SolrSearchQueryTermData();
			term.setKey(parts[i]);
			term.setValue(parts[i + 1]);
			filters.add(term);
		}
		searchQuery.setFilterTerms(filters);

		return searchQuery;
	}

	private List<String> getProductsDetail(final List<ProductData> products, final String applicableUID)
	{
		final MSIConfigurationModel msiConfiguration = msiConfigurationDao.getCurrentAndActivateConfig();
		final List<String> productsDetail = new ArrayList<>();
		for (final ProductData productData : products)
		{
			final ProductWsDTO productDTO = getDataMapper().map(productData, ProductWsDTO.class);
			productDTO.setNameHtml(productDTO.getName());
			productDTO.setLeyendaMSI("");
			if (Objects.isNull(productDTO.getPrice()))
			{
				try
				{
					final PriceWsDTO price = new PriceWsDTO();
					final ProductOfferingPriceWsDTO offeringPrice = productDTO.getProductOfferingPrice().iterator().next();
					final ProductOfferingPriceWsDTO bof = offeringPrice.getBundledPop().iterator().next();
					final MoneyWsDTO mp = bof.getPrice();
					price.setValue(new BigDecimal(mp.getValue()));
					price.setCurrencyIso(mp.getCurrencyIso());
					productDTO.setPrice(price);
					final String leyenda = buildLeyendaMSI(msiConfiguration, Double.valueOf(mp.getValue()));
					productDTO.setLeyendaMSI(leyenda);
				}
				catch (final Exception e)
				{
					LOG.info(e.getMessage());
				}
			}
			final ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(Include.NON_NULL);
			String jsonStringProduct = "";
			try
			{
				jsonStringProduct = mapper.writeValueAsString(productDTO);
			}
			catch (final JsonProcessingException e)
			{
				e.printStackTrace();
			}
			productsDetail.add(jsonStringProduct);
		}
		return productsDetail;
	}

	/*
	 * private List<String> getProductsDetail(final List<String> codes, final String applicableUID) { final
	 * MSIConfigurationModel msiConfiguration = msiConfigurationDao.getCurrentAndActivateConfig(); final List<String>
	 * productsDetail = new ArrayList<>(); for (final String productCode : codes) { final TmaPriceContextData
	 * tmaPriceContextData = new TmaPriceContextData(); tmaPriceContextData.setProductCode(productCode); if
	 * (StringUtils.isNotBlank(applicableUID)) { tmaPriceContextData.setUserId(applicableUID); } final ProductData
	 * product = tmaProductOfferFacade.getPoForCode(productCode, PRODUCT_OPTIONS, tmaPriceContextData); final
	 * ProductWsDTO productDTO = getDataMapper().map(product, ProductWsDTO.class);
	 * productDTO.setNameHtml(productDTO.getName()); productDTO.setLeyendaMSI(""); if
	 * (Objects.isNull(productDTO.getPrice())) { try { final PriceWsDTO price = new PriceWsDTO(); final
	 * ProductOfferingPriceWsDTO offeringPrice = productDTO.getProductOfferingPrice().iterator().next(); final
	 * ProductOfferingPriceWsDTO bof = offeringPrice.getBundledPop().iterator().next(); final MoneyWsDTO mp =
	 * bof.getPrice(); price.setValue(new BigDecimal(mp.getValue())); price.setCurrencyIso(mp.getCurrencyIso());
	 * productDTO.setPrice(price); final String leyenda = buildLeyendaMSI(msiConfiguration,
	 * Double.valueOf(mp.getValue())); productDTO.setLeyendaMSI(leyenda); } catch (final Exception e) {
	 * LOG.info(e.getMessage()); } } final ObjectMapper mapper = new ObjectMapper();
	 * mapper.setSerializationInclusion(Include.NON_NULL); String jsonStringProduct = ""; try { jsonStringProduct =
	 * mapper.writeValueAsString(productDTO); } catch (final JsonProcessingException e) { e.printStackTrace(); }
	 * productsDetail.add(jsonStringProduct); } return productsDetail; }
	 */

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

	protected de.hybris.platform.commerceservices.search.pagedata.PageableData createPageableData(final int currentPage,
			final int pageSize, final String sort)
	{
		final de.hybris.platform.commerceservices.search.pagedata.PageableData pageable = new de.hybris.platform.commerceservices.search.pagedata.PageableData();
		pageable.setCurrentPage(currentPage);
		pageable.setPageSize(pageSize);
		pageable.setSort(sort);
		return pageable;
	}

	public DataMapper getDataMapper()
	{
		return dataMapper;
	}

	public void setDataMapper(final DataMapper dataMapper)
	{
		this.dataMapper = dataMapper;
	}
}
