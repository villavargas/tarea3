/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */

package mx.com.telcel.controllers;

import de.hybris.platform.b2ctelcocommercewebservicescommons.dto.ProductOfferingPriceWsDTO;
import de.hybris.platform.b2ctelcocommercewebservicescommons.validator.TmaUserAccessValidator;
import de.hybris.platform.b2ctelcofacades.data.ProductOfferingSearchContextData;
import de.hybris.platform.b2ctelcofacades.data.TmaPriceContextData;
import de.hybris.platform.b2ctelcofacades.product.TmaProductOfferFacade;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ImageDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commerceservices.request.mapping.annotation.RequestMappingOverride;
import de.hybris.platform.commercewebservicescommons.dto.product.ImageWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.product.ProductWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.stock.impl.StockLevelDao;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.util.Config;
import de.hybris.platform.warehousing.stock.services.WarehouseStockService;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang.BooleanUtils;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.assertj.core.util.Strings;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.amx.telcel.di.sds.esb.sap.inventario.ConsultarExistenciasResponse;
import com.amx.telcel.di.sds.esb.sap.inventario.ExistenciaType;
import com.amx.telcel.di.sds.esb.sap.inventario.MaterialAlmacenType;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import mx.com.telcel.constants.Telb2coccConstants;
import mx.com.telcel.core.daos.msiconfiguration.MSIConfigurationDao;
import mx.com.telcel.core.daos.product.TelcelProductDao;
import mx.com.telcel.core.model.MSIConfigurationModel;
import mx.com.telcel.core.model.TelcelPoVariantModel;
import mx.com.telcel.core.service.ReplicateStockService;
import mx.com.telcel.core.service.TelcelWarehouseService;
import mx.com.telcel.core.util.TelcelUtil;
import mx.com.telcel.dto.AdditionalServicesListWsDTO;
import mx.com.telcel.facades.product.TelcelProductFacade;
import mx.com.telcel.facades.product.data.AdditionalServiceData;
import mx.com.telcel.facades.product.data.AdditionalServiceDataList;


/**
 * Web Services Controller to expose the functionality of the
 * {@link de.hybris.platform.b2ctelcofacades.product.TmaProductOfferFacade} and SearchFacade.
 *
 * @since1907
 */

@Controller
@RequestMapping(value = "/{baseSiteId}/products")
@ApiVersion("v2")
@Api(tags = "Products")
public class TmaProductsController extends BaseController
{
	private static final Logger LOG = Logger.getLogger(TmaProductsController.class);

	private static final Collection<ProductOption> PRODUCT_OPTIONS = Arrays.asList(ProductOption.BASIC, ProductOption.DESCRIPTION,
			ProductOption.SOLD_INDIVIDUALLY, ProductOption.PRODUCT_SPECIFICATION, ProductOption.CATEGORIES,
			ProductOption.PRODUCT_MEDIA, ProductOption.PRODUCT_BPO_CHILDREN, ProductOption.PRODUCT_OFFERING_PRICES,
			ProductOption.PARENT_BPOS, ProductOption.PRODUCT_OFFERING_GROUPS, ProductOption.PRODUCT_SPEC_CHAR_VALUE_USE,
			ProductOption.CLASSIFICATION, ProductOption.VARIANT_MATRIX, ProductOption.VARIANT_MATRIX_ALL_OPTIONS,
			ProductOption.MEDIA_ATTACHMENT, ProductOption.REVIEW);
	private final DecimalFormat decimalFormat = new DecimalFormat("###,##0.00");
	private static final String ANONYMOUS = "anonymous";
	private static final String REGEX = ",";
	private static final String ONLINE = "Online";
	private static final String TELB_2_COCC_CART_RECARGA_INICIAL_DISPONIBLES_BY_CODE = "telb2cocc.cart.recarga.inicial.disponibles.by.code";

	public static final String REGIONS = "telcel.replication.stock.regions";
	private static final String TELCEL_PRODUCT_CATALOG = "telcelProductCatalog";
	private static final String TELCEL = "telcel";
	private static final Double MONTO_SUPERIOR_DEFAULT = 2500D;
	private static final Double MONTO_INFERIOR_DEFAULT = 900D;
	private static final Integer MONTO_MAYOR_MESES_DEFAULT = 13;
	private static final Integer MONTO_INTERVALO_MESES_DEFAULT = 6;
	private static final String CHIP_CATEGORY = "chip.amigo.chipcategory";
	private static final String USER_DAT = "DAT";
	String CONSULT_INVENTORY_SERVICE_WITHOUT_ZIP_CODE = "telcel.section.consult.service.inventory.pdp";

	@Resource(name = "tmaProductOfferFacade")
	private TmaProductOfferFacade tmaProductOfferFacade;

	@Resource(name = "userAccessValidator")
	private TmaUserAccessValidator userAccessValidator;

	@Resource(name = "customerFacade")
	private CustomerFacade customerFacade;

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	@Resource(name = "baseSiteService")
	private BaseSiteService baseSiteService;

	@Resource(name = "productFacade")
	private TelcelProductFacade telcelProductFacade;

	@Resource(name = "defaultTelcelProductDao")
	private TelcelProductDao telcelProductDao;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private TelcelWarehouseService telcelWarehouseService;

	@Resource
	private ReplicateStockService replicateStockService;

	@Resource
	private TelcelUtil telcelUtil;

	@Resource
	private WarehouseStockService warehouseStockService;

	@Resource
	private StockLevelDao stockLevelDao;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "msiConfigurationDao")
	private MSIConfigurationDao msiConfigurationDao;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@RequestMapping(value = "/{productCode}", method = RequestMethod.GET)
	@RequestMappingOverride(priorityProperty = "telb2cocc.TmaProductsController.getProductByCode.priority")
	@CacheControl(directive = CacheControlDirective.PRIVATE, maxAge = 120)
	@Cacheable(value = "productCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.CommerceCacheKeyGenerator).generateKey(true,true,true,true,true,#productCode,#processType,#userId,#fields,#region)")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@ApiBaseSiteIdParam
	@ApiOperation(value = "Get product details", notes = "Returns details of a single product according to a product code.")
	public ProductWsDTO getProductByCode(@ApiParam(value = "Product identifier", required = true)
	@PathVariable
	final String productCode, @ApiParam(value = "Process Type, for filtering product prices based on Process Type")
	@RequestParam(required = false)
	final String processType, @ApiParam(value = "User ID, for filtering product prices based on Customer")
	@RequestParam(required = false)
	final String userId,
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL")
			@RequestParam(defaultValue = DEFAULT_FIELD_SET)
			final String fields, @ApiParam(value = "Region identifier for filtering product prices based on region")
			@Valid
			@RequestParam(value = "region", required = false)
			final String region) throws DatatypeConfigurationException, JAXBException, SOAPException, ParserConfigurationException,
			IOException, NoSuchAlgorithmException, KeyStoreException, ParseException, KeyManagementException
	{
		try
		{
			//Validate Level Stock, From Telcel Services
			boolean consultInventoryServWithoutCP = configurationService.getConfiguration().getBoolean(CONSULT_INVENTORY_SERVICE_WITHOUT_ZIP_CODE);
			if(BooleanUtils.isTrue(consultInventoryServWithoutCP)) {
				LOG.info("Consulta Inventario...");
				validateStockProduct(productCode);
			}else{
				LOG.info("NO Consulta Inventario,por Flag.");
			}
		}
		catch (final Exception e)
		{
			LOG.error(e);
		}

		validate(userId, "String", userAccessValidator);

		final String applicableUID = getApplicableUID(userId);

		final TmaPriceContextData tmaPriceContextData = new TmaPriceContextData();
		tmaPriceContextData.setProductCode(productCode);

		if (StringUtils.isNotBlank(region))
		{
			tmaPriceContextData.setRegionIsoCodes(Stream.of(region).collect(Collectors.toSet()));
		}
		if (StringUtils.isNotBlank(processType))
		{
			tmaPriceContextData
					.setProcessTypeCodes(new HashSet<String>(Arrays.asList(StringUtils.deleteWhitespace(processType).split(","))));
		}
		if (StringUtils.isNotBlank(applicableUID))
		{
			tmaPriceContextData.setUserId(applicableUID);
		}
		final ProductData product = tmaProductOfferFacade.getPoForCode(productCode, PRODUCT_OPTIONS, tmaPriceContextData);
		if (validateChipProductDAT(product))
		{
			return null;
		}
		final ProductWsDTO productDTO = getDataMapper().map(product, ProductWsDTO.class);
		final MSIConfigurationModel msiConfiguration = msiConfigurationDao.getCurrentAndActivateConfig();
		Double mp = Double.valueOf(0d);
		if (CollectionUtils.isNotEmpty(productDTO.getProductOfferingPrice()))
		{
			LOG.info("Product " + productCode + " has no valid prices");
			final ProductOfferingPriceWsDTO offeringPrice = productDTO.getProductOfferingPrice().iterator().next();
			if (CollectionUtils.isNotEmpty(offeringPrice.getBundledPop()))
			{
				final ProductOfferingPriceWsDTO bof = offeringPrice.getBundledPop().iterator().next();
				mp = Double.valueOf(bof.getPrice().getValue());
			}
		}
		final String leyenda = buildLeyendaMSI(msiConfiguration, mp);
		productDTO.setLeyendaMSI(leyenda);
		buildGalleryIndex(productDTO);

		return productDTO;
	}

	@RequestMapping(value = "/as", method = RequestMethod.GET)
	@CacheControl(directive = CacheControlDirective.PRIVATE, maxAge = 120)
	@Cacheable(value = "productCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.CommerceCacheKeyGenerator).generateKey(true,true,true,true,true,#fields,#type,#esquemaCobro)")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@ApiBaseSiteIdParam
	@ApiOperation(value = "Get product details", notes = "Returns details of a single product according to a product code.")
	public AdditionalServicesListWsDTO getProductByCode(@ApiParam(value = "Base site", required = true)
	@PathVariable
	final String baseSiteId,
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL")
			@RequestParam(defaultValue = DEFAULT_FIELD_SET)
			final String fields, @ApiParam(value = "Type of additional service products to be returned")
			@Valid
			@RequestParam(value = "type", required = false)
			final String type, @ApiParam(value = "Esquema de Cobro related to additional service product")
			@Valid
			@RequestParam(value = "esquemaCobro", required = false)
			final String esquemaCobro)
	{
		if (StringUtils.isBlank(baseSiteId))
		{
			throw new RequestParameterException("Invalid Request.", RequestParameterException.MISSING, "baseSiteId");
		}

		final String catalog = ((CMSSiteModel) baseSiteService.getBaseSiteForUID(baseSiteId)).getDefaultCatalog().getId();
		final ProductOfferingSearchContextData contextData = new ProductOfferingSearchContextData();
		contextData.setCatalog(catalog);
		contextData.setVersion(ONLINE);
		contextData.setEsquemaCobro(esquemaCobro);
		final AdditionalServiceDataList additionalServiceDataList = telcelProductFacade.getAdditionalServicesPOByType(type,
				contextData);

		//Start - Quitar condicion cuando se habilite definitivamente la recarga de 100 que aplican para chip
		final String recargasVisibles = Config.getParameter(TELB_2_COCC_CART_RECARGA_INICIAL_DISPONIBLES_BY_CODE);
		if (StringUtils.isNotEmpty(recargasVisibles) && additionalServiceDataList != null
				&& CollectionUtils.isNotEmpty(additionalServiceDataList.getAdditionalServices()))
		{
			final AdditionalServiceDataList serviceDataList = new AdditionalServiceDataList();
			final List<AdditionalServiceData> serviceData = new ArrayList<>();
			final List<String> recargasVisiblesList = Arrays.asList(recargasVisibles.split(","));
			for (final AdditionalServiceData additionalServiceData : additionalServiceDataList.getAdditionalServices())
			{
				if (recargasVisiblesList.contains(additionalServiceData.getCode()))
				{
					serviceData.add(additionalServiceData);
				}
			}
			serviceDataList.setAdditionalServices(serviceData);
			return getDataMapper().map(serviceDataList, AdditionalServicesListWsDTO.class);
		}
		//End
		return getDataMapper().map(additionalServiceDataList, AdditionalServicesListWsDTO.class);
	}

	protected String getApplicableUID(final String userId)
	{
		final List<String> eligibilityAdminRoles = Arrays
				.asList(configurationService.getConfiguration().getString(Telb2coccConstants.ELIGIBILTY_ROLES).split(REGEX));

		if (StringUtils.isEmpty(userId))
		{
			final String currentUID = customerFacade.getCurrentCustomerUid();
			if (!hasRole(eligibilityAdminRoles))
			{
				return currentUID; // SSU1 or Anonymous
			}
			if (!currentUID.equals(ANONYMOUS))
			{
				return currentUID; //SSU1
			}
		}
		return userId; //SSU1 or ""
	}

	protected boolean hasRole(final List<String> eligibilityAdminRoles)
	{
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		for (final GrantedAuthority ga : authentication.getAuthorities())
		{
			if (eligibilityAdminRoles.contains(ga.getAuthority()))
			{
				return true;
			}
		}
		return false;
	}

	public void validateStockProduct(final String productSKU)
			throws DatatypeConfigurationException, JAXBException, SOAPException, ParserConfigurationException, IOException,
			NoSuchAlgorithmException, KeyStoreException, ParseException, KeyManagementException
	{
		int stocknuevos = 0;
		int stockactualizados = 0;

		//GET PRODUCTS, JUST ONLINE VERSION (TYPE: TelcelPoVariantModel)
		final List<TelcelPoVariantModel> telcelPoVariantModelList = telcelProductDao.getProductTelcelPoVariantByCode(productSKU,
				catalogVersionService.getCatalogVersion(TELCEL_PRODUCT_CATALOG, ONLINE));
		//GET WAREHOUSES
		final List<WarehouseModel> warehouseModelList = telcelWarehouseService.getWarehouses();
		//GET REGIONS
		final String regions = configurationService.getConfiguration().getString(REGIONS);
		final String[] arrRegions = regions.split(",");

		for (final TelcelPoVariantModel telcelPoVariantModel : telcelPoVariantModelList)
		{
			final List<MaterialAlmacenType> listaproducto = new ArrayList<>();
			for (final WarehouseModel warehouse : warehouseModelList)
			{
				if (warehouse.getIsocode().length() <= 4)
				{
					for (final String region : arrRegions)
					{
						LOG.debug("ReplicateStockJobPerformable replicar stock [ " + telcelPoVariantModel.getCode() + "]");
						setListResquestStock(telcelPoVariantModel, listaproducto, warehouse, region);
					}
				}
			}
			//LOGIC TO SAVE  AVAILABLE AMOUNT
			if (listaproducto.size() > 0)
			{
				final ConsultarExistenciasResponse response = replicateStockService.getStockForProductList(listaproducto);
				LOG.debug("Registros a procesar [" + listaproducto.size() + "]");
				if (response.getRespuesta() != null)
				{
					final List<ExistenciaType> existencias = response.getRespuesta().getExistencia();
					for (final ExistenciaType existenciaType : existencias)
					{
						if (StringUtils.isNotEmpty(existenciaType.getMATNR()))
						{

							final List<WarehouseModel> warehouseFilter = new ArrayList<>();
							for (final WarehouseModel warehouseModel : warehouseModelList)
							{
								if (existenciaType.getLGORT().equalsIgnoreCase(warehouseModel.getIsocode()))
								{
									warehouseFilter.add(warehouseModel);
								}
							}

							boolean isprocesado = false;
							for (final WarehouseModel warehouseModel : warehouseFilter)
							{

								final Collection<PointOfServiceModel> pointOfServiceModels = warehouseModel.getPointsOfService();
								if (existenciaType.getWERKS().equalsIgnoreCase(
										pointOfServiceModels.stream().iterator().next().getAddress().getRegionTelcel().getCode()))
								{

									LOG.debug("******************INICIO PROCESADO *******************");
									LOG.debug("Producto [" + telcelPoVariantModel.getCode() + "]");
									LOG.debug("Disponible [" + existenciaType.getLABST() + "]");
									LOG.debug("Region [" + existenciaType.getWERKS() + "]");
									LOG.debug("Almacen WS [" + existenciaType.getLGORT() + "]");
									LOG.debug("Almacen Commerce [" + warehouseModel.getCode() + "]");
									LOG.debug("Material [" + existenciaType.getMATNR() + "]");
									LOG.debug("****************** FIN PROCESADO *******************");

									final StockLevelModel stockLevelModel = getStockLevelDao()
											.findStockLevel(telcelPoVariantModel.getCode(), warehouseModel);

									if (Objects.isNull(stockLevelModel))
									{
										getWarehouseStockService().createStockLevel(telcelPoVariantModel.getCode(), warehouseModel,
												existenciaType.getLABST().intValue(), null, new Date(),
												telcelPoVariantModel.getCode() + warehouseModel.getCode());
										stocknuevos = stocknuevos + 1;
									}
									else
									{
										stockLevelModel.setAvailable(existenciaType.getLABST().intValue());
										stockactualizados = stockactualizados + 1;
										modelService.save(stockLevelModel);
									}
									isprocesado = true;
								}

								if (!isprocesado)
								{
									//                                    LOG.debug("************** INICIO NO PROCESADO **************************");
									//                                    LOG.debug("Producto [" + telcelPoVariantModel.getCode() + "]");
									//                                    LOG.debug("Disponible [" + existenciaType.getLABST() + "]");
									//                                    LOG.debug("Region [" + existenciaType.getWERKS() + "]");
									//                                    LOG.debug("Almacen Respuesta [" + existenciaType.getLGORT() + "]");
									//                                    LOG.debug("Almacen Commerce [" + warehouseModel.getCode() + "]");
									//                                    LOG.debug("Material [" + existenciaType.getMATNR() + "]");
									//                                    LOG.debug("************** FIN NO PROCESADO **************************");
								}
							}
						}
					}
					LOG.debug("Registros procesados para producto [" + telcelPoVariantModel.getCode() + "] total ["
							+ existencias.size() + "]");
					LOG.debug(
							"Registros procesados para stockactualizados [" + stockactualizados + "] stocknuevos [" + stocknuevos + "]");
				}
			}

		}
	}

	private void setListResquestStock(final TelcelPoVariantModel telcelPoVariantModel,
			final List<MaterialAlmacenType> listaproducto, final WarehouseModel warehouse, final String region)
	{
		try
		{
			final MaterialAlmacenType materialAlmacenType = new MaterialAlmacenType();
			materialAlmacenType.setLGORT(warehouse.getIsocode());
			materialAlmacenType.setMATNR(telcelPoVariantModel.getSku());
			materialAlmacenType.setWERKS(region);
			listaproducto.add(materialAlmacenType);
		}
		catch (final Exception e)
		{
			e.printStackTrace();
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

	private void buildGalleryIndex(final ProductWsDTO productDTO)
	{
		int galleryIndex = 0;
		if (Objects.nonNull(productDTO.getImages()))
		{
			for (final ImageWsDTO image : productDTO.getImages())
			{
				if (ImageDataType.GALLERY.name().equals(image.getImageType().name()))
				{
					image.setGalleryIndex(galleryIndex++);
				}
			}
		}
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

	private boolean containsCategoryData(final Collection<CategoryData> list, final String code)
	{
		return list.stream().filter(o -> o.getCode().equals(code)).findFirst().isPresent();
	}

	public WarehouseStockService getWarehouseStockService()
	{
		return warehouseStockService;
	}

	public void setWarehouseStockService(final WarehouseStockService warehouseStockService)
	{
		this.warehouseStockService = warehouseStockService;
	}

	public StockLevelDao getStockLevelDao()
	{
		return stockLevelDao;
	}

	public void setStockLevelDao(final StockLevelDao stockLevelDao)
	{
		this.stockLevelDao = stockLevelDao;
	}
}
