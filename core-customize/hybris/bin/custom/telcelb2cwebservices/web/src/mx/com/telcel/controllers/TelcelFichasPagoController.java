package mx.com.telcel.controllers;

import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.ReferencePaymentInfoModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.BusinessException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.Config;
import de.hybris.platform.util.MediaUtil;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.BadRequestException;

import mx.com.telcel.core.services.TelcelSoapConverterService;
import mx.com.telcel.core.url.impl.TelcelProductModelUrlResolver;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.log4j.Logger;
import org.assertj.core.util.Strings;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import mx.com.telcel.core.model.AdditionalServiceEntryModel;
import mx.com.telcel.core.model.HolderLineModel;
import mx.com.telcel.core.model.PaymentReceiptModel;
import mx.com.telcel.core.model.TelcelAdditionalServiceProductOfferingModel;
import mx.com.telcel.core.model.TelcelPoVariantModel;
import mx.com.telcel.core.model.TelcelSimpleProductOfferingModel;
import mx.com.telcel.core.service.AESService;
import mx.com.telcel.facades.order.data.OrderDetailProductData;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;


@Controller
@RequestMapping(value = "/ficha-pago")
@CacheControl(directive = CacheControlDirective.NO_STORE)
@Api(tags = "Fichas de Pago Telcel")
public class TelcelFichasPagoController
{

	private static final String TELCEL = "telcel";
	private static final String TELCEL_CONTENT_CATALOG = "telcelContentCatalog";
	private static final String TELCEL_PRODUCT_CATALOG = "telcelProductCatalog";
	private static final String ONLINE = "Online";
	//private static final String IMAGES_THEME_LOGO_TELCEL_PNG = "/images/theme/logo-telcel.png";
	private static final String IMAGES_THEME_LOGO_TELCEL_PNG = "/images/theme/logo-telcel-nuevo.png";
	private static final String IMAGES_THEME_LOGO_OXXO_PNG = "/images/theme/logo-oxxo.png";
	private static final String IMAGES_THEME_NOT_AVAILABLE_PNG = "/images/theme/imagenNoDisponible.png";
	private static final String APPLICATION_PDF = "application/pdf";
	private static final String APPLICATION_X_PDF = "application/x-pdf";
	private static final String REFERENCE = "REFERENCE";
	private static final String SPEI = "SPEI";
	private static final String CARD = "CARD";
	private static final String ES = "es";
	private static final String MX = "MX";
	//private static final String DD_DE_MMMM_DE_YYYY = "dd 'de' MMMM 'de' yyyy";
	private static final String DD_DE_MMMM_DE_YYYY = "dd 'de' MMMM";
	private static final String DD_MM_YYYY_HH_MM = "dd/MM/yyyy hh:mm aa";
	//private static final String HH_MM = "HH:mm";
	private static final String HH_MM_A = "hh:mm a";
	private static final String OXXO = "OXXO";
	private static final String FICHA_DE_PAGO_OXXO = "ficha-de-pago-oxxo";
	private static final String FICHA_DE_PAGO_CONVENENCIA = "ficha-de-pago-convenencia";
	private static final String FICHA_DE_PAGO_BANCO = "ficha-de-pago-banco";
	private static final String JASPER = ".jasper";
	private static final String FICHASDEPAGO = "/fichasdepago/";
	private static final String FICHA_DE_PAGO = "ficha-de-pago";
	private static final String PDF = ".pdf";
	private static final String YYYY_MM_DD_HH_MM_SS = "yyyyMMdd hhmmss";
	private static final String ES_MX = "es_MX";
	private static final String CHIP_CATEGORY = "chip.amigo.chipcategory";

	@Resource
	private MediaService mediaService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource(name = "siteBaseUrlResolutionService")
	private SiteBaseUrlResolutionService siteBaseUrlResolutionService;

	@Resource
	private BaseSiteService baseSiteService;

	@Resource(name = "baseStoreService")
	private BaseStoreService baseStoreService;

	@Resource(name = "customerAccountService")
	private CustomerAccountService customerAccountService;

	@Resource
	private CommonI18NService commonI18NService;

	@Resource(name = "defaultConfigurationService")
	private ConfigurationService configurationService;

	@Resource(name = "aesService")
	private AESService aesService;

	@Resource
	private UserService userService;
	@Resource
	private TelcelProductModelUrlResolver productModelUrlResolver;

	@Resource(name = "telcelSoapConverterService")
	private TelcelSoapConverterService telcelSoapConverterService;

	private static final Logger LOG = Logger.getLogger(TelcelFichasPagoController.class);

	@RequestMapping(value = "/download", method = RequestMethod.GET)
	@ResponseBody
	public void downloadReport(final HttpServletResponse response, @RequestParam
	final String prm, @RequestParam(required = false)
	final String prm2) throws JRException, IOException
	{
		String order = prm;
		LOG.info("ORDER ENCRYPT : " + order);
		order = aesService.decryptGCM(order);
		if (Objects.isNull(order))
		{
			throw new BadRequestException("El codigo de la orden es corrupto");
		}
		LOG.info("ORDER DECRYPT : " + order);
		final LanguageModel languageToSet = commonI18NService.getLanguage(ES_MX);
		if (languageToSet != null && !languageToSet.equals(commonI18NService.getCurrentLanguage()))
		{
			commonI18NService.setCurrentLanguage(languageToSet);
		}
		final OrderModel orderModel = getOrder(order);
		if (!Strings.isNullOrEmpty(prm2))
		{
			LOG.info("PARAM USER UID : " + prm2);
			final UserModel user = orderModel.getUser();
			final String userUID = aesService.decryptGCM(prm2, user.getPk().getLongValueAsString());
			LOG.info("PARAM USER UID DECRYPT : " + userUID);
			if (!userUID.equalsIgnoreCase(user.getUid()))
			{
				throw new BadRequestException("No hay coincidencias uid");
			}
		}
		else
		{
			throw new BadRequestException("No se encontro el parametro uid");
		}
		if (orderModel.getPaymentInfo() == null)
		{
			throw new BadRequestException("El metodo de pago de la orden seleccionada no es valido para "
					+ "generar ficha de pago, por favor verifícalo e intenta de nuevo.");
		}

		final String paymentType = orderModel.getPaymentInfo().getPaymentType();

		if (paymentType == null || paymentType.equals(CARD))
		{
			throw new BadRequestException("El metodo de pago de la orden seleccionada no es valido para "
					+ "generar ficha de pago, por favor verifícalo e intenta de nuevo.");
		}

		final Map<String, Object> params = parametrosFichaPago(orderModel);

		final String reporte = tipoReporte(orderModel, paymentType);

		final InputStream jasperStream = this.getClass().getResourceAsStream(reporte);

		final JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
		final JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, new JREmptyDataSource());

		response.setContentType(APPLICATION_X_PDF);
		response.setHeader("Content-disposition", "attachment; filename=ficha-de-pago" + order + PDF);

		final OutputStream outStream = response.getOutputStream();
		JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
	}

	@RequestMapping(value = "/preview", method = RequestMethod.GET)
	public ResponseEntity<byte[]> previewReport(@RequestParam
	final String order) throws JRException, IOException, BusinessException
	{

		final LanguageModel languageToSet = commonI18NService.getLanguage(ES_MX);
		if (languageToSet != null && !languageToSet.equals(commonI18NService.getCurrentLanguage()))
		{
			commonI18NService.setCurrentLanguage(languageToSet);
		}
		final OrderModel orderModel = getOrder(order);

		if (orderModel.getPaymentInfo() == null)
		{
			throw new BadRequestException("El metodo de pago de la orden seleccionada no es valido para "
					+ "generar ficha de pago, por favor verifícalo e intenta de nuevo.");
		}

		final String paymentType = orderModel.getPaymentInfo().getPaymentType();

		if (paymentType == null || paymentType.equals(CARD))
		{
			throw new BadRequestException("El metodo de pago de la orden seleccionada no es valido para "
					+ "generar ficha de pago, por favor verifícalo e intenta de nuevo.");
		}

		final Map<String, Object> params = parametrosFichaPago(orderModel);

		final String reporte = tipoReporte(orderModel, paymentType);

		final InputStream jasperStream = this.getClass().getResourceAsStream(reporte);
		if (jasperStream == null)
		{
			throw new BusinessException("Ocurrio un error al generar la ficha de pago, "
					+ "intenta de nuevo, si el problema persiste comunicalo al administrador del sistema.");
		}

		final JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
		final JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, new JREmptyDataSource());

		final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);

		final HttpHeaders headers = new HttpHeaders();

		headers.setContentType(MediaType.parseMediaType(APPLICATION_PDF));
		final String filename = FICHA_DE_PAGO + order + PDF;

		headers.add("content-disposition", "inline;filename=" + filename);

		return new ResponseEntity<byte[]>(outStream.toByteArray(), headers, HttpStatus.OK);
	}

	private String tipoReporte(final OrderModel orderModel, final String paymentType)
	{
		String tipoReporte = "";

		if (paymentType.equals(REFERENCE))
		{
			final ReferencePaymentInfoModel referencePaymentInfoModel = (ReferencePaymentInfoModel) orderModel.getPaymentInfo();
			if (referencePaymentInfoModel.getPaymentReferenceName().equals(OXXO))
			{
				tipoReporte = FICHA_DE_PAGO_OXXO;
			}
			else
			{
				tipoReporte = FICHA_DE_PAGO_CONVENENCIA;
			}
		}
		else if (paymentType.equals(SPEI))
		{
			tipoReporte = FICHA_DE_PAGO_BANCO;
		}
		LOG.info("REPORTE JASPER : " + (FICHASDEPAGO + tipoReporte + JASPER));
		return FICHASDEPAGO + tipoReporte + JASPER;
	}

	private Map<String, Object> parametrosFichaPago(final OrderModel orderModel)
	{
		final String urlLogoTelcel = urlLogoTelcel();

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DD_MM_YYYY_HH_MM);
		//final String orderDate = simpleDateFormat.format(orderModel.getDate());
		Date orderDateFormat = null;
		String orderDateStringFormat = "";

		orderDateFormat = orderModel.getDate();
		if (Objects.nonNull(orderModel.getDate()))
		{
			final String month = telcelSoapConverterService.converterStringMonth(orderDateFormat.getMonth() + 1);
			orderDateStringFormat = String.valueOf(orderDateFormat.getDate()) + " de " + month + " de "
					+ String.valueOf(orderDateFormat.getYear() + 1900);
		}
		final HolderLineModel addressHolderLine = orderModel.getAddressHolderLine();

		final PaymentInfoModel paymentInfo = orderModel.getPaymentInfo();
		if (Objects.isNull(paymentInfo.getReceipt()))
		{
			throw new BadRequestException("El recibo de pago es nulo");
		}
		final PaymentReceiptModel receipt = paymentInfo.getReceipt().get(0);

		Date expiration = null;
		try
		{
			simpleDateFormat = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
			expiration = simpleDateFormat.parse(receipt.getExpiration());
		}
		catch (final ParseException e)
		{
		}
		String expirationTime = "";
		try
		{
			final DateFormatSymbols formatSymbols = DateFormatSymbols.getInstance(new Locale(ES, MX));
			final SimpleDateFormat simpleTimeFormat = new SimpleDateFormat(HH_MM_A, formatSymbols);
			expirationTime = expiration != null ? simpleTimeFormat.format(expiration) : "";
			expirationTime = expirationTime.replace("AM", "a.m.").replace("PM","p.m.");
		}
		catch (final Exception e)
		{
			LOG.error("ERROR PARSE DATE : " + expiration.toString());
		}

		final DateFormatSymbols formatSymbols = DateFormatSymbols.getInstance(new Locale(ES, MX));
		simpleDateFormat = new SimpleDateFormat(DD_DE_MMMM_DE_YYYY, formatSymbols);
		final String expirationDate = expiration != null ? simpleDateFormat.format(expiration) : "";
		final DecimalFormat decimalFormat = new DecimalFormat("$###,##0.00");
		final DecimalFormat decimalFormatTotal = new DecimalFormat("$###,##0.00");
		final String total = decimalFormatTotal.format(orderModel.getTotalPrice());

		final String barcode = receipt.getBarcode() != null ? receipt.getBarcode() : "";

		final List<OrderDetailProductData> productsData = new ArrayList<>();
		for (final AbstractOrderEntryModel entryModel : orderModel.getEntries())
		{
			final TelcelPoVariantModel productModel = (TelcelPoVariantModel) entryModel.getProduct();
			final TelcelSimpleProductOfferingModel baseProduct = (TelcelSimpleProductOfferingModel) (productModel).getTmaBasePo();

			final String name = productModel.getName();
			final String color = productModel.getColor() != null ? productModel.getColor().getName() : "";
			final String marca = baseProduct.getMarca();
			String storage = productModel.getStorage() != null && productModel.getStorage().getStorageValue() != null
					? productModel.getStorage().getStorageValue().toString()
					: "";
			storage = !storage.equals("") && productModel.getStorage().getStorageUnit() != null
					? storage + " " + productModel.getStorage().getStorageUnit().getCode()
					: "";

			String description = "";
			if (validateField(name))
			{
				description = description + name + " | ";
			}
			if (validateField(marca))
			{
				description = description + marca + " | ";
			}
			if (validateField(storage))
			{
				description = description + storage + " | ";
			}
			if (validateField(color))
			{
				description = description + color + " | ";
			}
			if (!description.isEmpty())
			{
				description = description.substring(0, description.length() - 2);
			}
			/*
			 * if (entryModel.getEsquemaCobro() != null) { description = String.join(", ", description,
			 * entryModel.getEsquemaCobro().getName()); }
			 */
			if (productModel.getIncludeGift())
			{
				String descripcionIncludeGift="";
				if(Strings.isNullOrEmpty(productModel.getDescripcionIncludeGift())) {
					descripcionIncludeGift =  productModel.getDescripcionIncludeGift(new Locale("es", "MX"));
				}else{
					descripcionIncludeGift = productModel.getDescripcionIncludeGift();
				}
				description = description + " | Incluye regalo: " + descripcionIncludeGift;
			}
			catalogVersionService.setSessionCatalogVersion(TELCEL_PRODUCT_CATALOG, ONLINE);
			if (!containsCategory(entryModel.getProduct().getSupercategories(), Config.getParameter(CHIP_CATEGORY)))
			{
				/*final OrderDetailProductData productData = new OrderDetailProductData();
				productData.setName(description);
				productData.setPrice(decimalFormat.format(entryModel.getBasePrice()));*/
				final List<OrderDetailProductData> recargasProducto = amigoSinLimite(entryModel, decimalFormat);
				int quantity = 1;
				if (Objects.nonNull(entryModel.getQuantity()))
				{
					quantity = entryModel.getQuantity().intValue();
				}
				for (int i = 0; i < quantity; i++)
				{
					final OrderDetailProductData productData = new OrderDetailProductData();
					productData.setName(description);
					productData.setPrice(decimalFormat.format(entryModel.getBasePrice()));

					//Asigna imagen
					if(Objects.nonNull(productModel.getPicture()))
					{
						String imagePath="";
						imagePath = MediaUtil.getTenantMediaReadDir() + File.separator + productModel.getPicture().getLocation();
						LOG.info("Product_imagePath:: " +imagePath);
						productData.setPictureUrlJasper(imagePath);
					}else{
						final String urlLogoImageNotAvailable = urlLogoImageNotAvailable();
						productData.setPictureUrlJasper(urlLogoImageNotAvailable);
					}

					OrderDetailProductData recargaObject = null;
					if (!recargasProducto.isEmpty()) {
						recargaObject = recargasProducto.get(i);
						if(BooleanUtils.isFalse(recargasProducto.get(i).isRecargaRejected())){
							productData.setRecargaName(recargaObject.getRecargaName());
							productData.setRecargaPrice(decimalFormat.format(Double.valueOf(recargaObject.getRecargaPrice())));
							productData.setRecargaRejected(recargaObject.isRecargaRejected());
						}else{
							productData.setRecargaName("");
							productData.setRecargaPrice("");
							productData.setRecargaRejected(recargaObject.isRecargaRejected());
						}
					}else{
						productData.setRecargaName("");
						productData.setRecargaPrice("");
					}
					productsData.add(productData);
				}
				/*//Agregar recargas
				try
				{
					final List<OrderDetailProductData> recargasProducto = amigoSinLimite(entryModel, decimalFormat);
					if (!recargasProducto.isEmpty())
					{
						for (final OrderDetailProductData recarga : recargasProducto)
						{
							recarga.setPrice(decimalFormat.format(Double.valueOf(recarga.getPrice())));
							productsData.add(recarga);
						}
					}
				}
				catch (final Exception e)
				{
					LOG.error("ERROR RECARGA AMIGO : " + e.getMessage());
				}*/
			}
			else//Chip Express
			{
				/*List<OrderDetailProductData> recargasProducto = null;
				OrderDetailProductData recarga = null;
				try
				{
					recargasProducto = amigoSinLimite(entryModel, decimalFormat);
					if (Objects.nonNull(recargasProducto))
					{
						recarga = recargasProducto.iterator().next();
					}
				}
				catch (final Exception e)
				{
					LOG.error("ERROR RECARGA AMIGO : " + e.getMessage());
				}
				final OrderDetailProductData productData = new OrderDetailProductData();
				productData.setName(description);
				productData.setPrice(decimalFormat.format(entryModel.getBasePrice()));
				if (Objects.nonNull(recarga))
				{
					productData.setPrice(decimalFormat.format(entryModel.getBasePrice() + Double.valueOf(recarga.getRecargaPrice())));
				}
				int quantity = 1;
				if (Objects.nonNull(entryModel.getQuantity()))
				{
					quantity = entryModel.getQuantity().intValue();
				}
				for (int i = 0; i < quantity; i++)
				{
					productsData.add(productData);
					if (Objects.nonNull(recarga))
					{
						recarga.setPrice("");
						productsData.add(recarga);
					}
				}*/
				List<OrderDetailProductData> recargasProducto = null;
				OrderDetailProductData recarga = null;
				try
				{
					recargasProducto = amigoSinLimite(entryModel, decimalFormat);
				}
				catch (final Exception e) {
					LOG.error("ERROR RECARGA AMIGO : " + e.getMessage());
				}

				int quantity = 1;
				if (Objects.nonNull(entryModel.getQuantity()))
				{
					quantity = entryModel.getQuantity().intValue();
				}
				for (int i = 0; i < quantity; i++)
				{
					final OrderDetailProductData productData = new OrderDetailProductData();
					productData.setName(description);
					productData.setPrice(decimalFormat.format(entryModel.getBasePrice()));

					//Asigna imagen
					if(Objects.nonNull(productModel.getPicture()))
					{
						String imagePath="";
						imagePath = MediaUtil.getTenantMediaReadDir() + File.separator + productModel.getPicture().getLocation();
						LOG.info("Product_imagePath: " +imagePath);
						productData.setPictureUrlJasper(imagePath);
					}else{
						final String urlLogoImageNotAvailable = urlLogoImageNotAvailable();
						productData.setPictureUrlJasper(urlLogoImageNotAvailable);
					}

					OrderDetailProductData recargaObject = null;
					if (!recargasProducto.isEmpty()) {
						recargaObject = recargasProducto.get(i);
							productData.setPrice(decimalFormat.format(entryModel.getBasePrice() + Double.valueOf(recargaObject.getRecargaPrice())));
							productData.setRecargaName(recargaObject.getRecargaName());
							productData.setRecargaPrice("");

					}

					productsData.add(productData);
				}

			}
		}

		final String paymentType = orderModel.getPaymentInfo().getPaymentType();

		final Map<String, Object> params = new HashMap<>();
		params.put("order-code", orderModel.getCode());
		params.put("date", orderDateStringFormat);
		params.put("email", addressHolderLine.getEmail());
		params.put(TELCEL, urlLogoTelcel);
		params.put("barcode", barcode);
		params.put("limite-pago", expirationDate+" hasta las " + expirationTime);
		params.put("limite-pago-hrs", "hasta las " + expirationTime + " hrs");
		params.put("total", total);
		params.put("products", productsData);

		final String linkTiendasOxxo = configurationService.getConfiguration().getString("link-tiendas-oxxo");
		final String linkTiendasConvenencias = configurationService.getConfiguration().getString("link-tiendas-conveniencia");
		final String montoMaximo = configurationService.getConfiguration().getString("link-monto-maximo");

		if (paymentType.equals(REFERENCE))
		{
			final ReferencePaymentInfoModel referencePaymentInfoModel = (ReferencePaymentInfoModel) orderModel.getPaymentInfo();
			if (referencePaymentInfoModel.getPaymentReferenceName().equals(OXXO))
			{
				final String urlLogoOxxo = urlLogoOxxo();
				params.put("link-tiendas-oxxo", linkTiendasOxxo);
				params.put("logo-oxxo", urlLogoOxxo);
				params.put("reference", receipt.getReference());
			}
			else
			{
				params.put("link-monto-maximo", montoMaximo);
				params.put("reference", receipt.getReference());
				params.put("link-tiendas-conveniencia", linkTiendasConvenencias);
			}
		}
		else if (paymentType.equals(SPEI))
		{
			params.put("concepto-bbva", receipt.getDescription());
			params.put("referencia-bbva", receipt.getReference());
			params.put("clabe-otros-bancos", receipt.getClabe());
			//params.put("concepto-otros-bancos", receipt.getDescription());
			params.put("concepto-otros-bancos", receipt.getReference());
			params.put("referencia-otros-bancos", receipt.getAgreement());
			params.put("cie-bbva", receipt.getAgreement());
			params.put("beneficiario-otros-bancos", receipt.getBeneficiary());
			params.put("banco-destino-otros-bancos", receipt.getBank());
			params.put("beneficiario-bbva", receipt.getBeneficiary());
		}

		return params;
	}

	private OrderModel getOrder(final String order)
	{
		final BaseStoreModel baseStore = baseStoreService.getBaseStoreForUid(TELCEL);

		try
		{
			final OrderModel orderModel = customerAccountService.getOrderForCode(order, baseStore);
			if (orderModel == null)
			{
				throw new BadRequestException("No existe la orden solicitada, por favor verifícalo e intenta de nuevo.");
			}
			return orderModel;
		}
		catch (final ModelNotFoundException exception)
		{
			throw new BadRequestException("No existe la orden solicitada, por favor verifícalo e intenta de nuevo.");
		}
	}

	private String urlLogoTelcel()
	{

		CatalogVersionModel cat = new CatalogVersionModel();
		cat = getCatalogVersionService().getCatalogVersion(TELCEL_CONTENT_CATALOG, ONLINE);
		final MediaModel logoTelcel = getMediaService().getMedia(cat, IMAGES_THEME_LOGO_TELCEL_PNG);
		final String imagePath = MediaUtil.getTenantMediaReadDir() + File.separator + logoTelcel.getLocation();
		LOG.info("MEDIA LOCATION : " + imagePath);
		return imagePath;
	}

	private String urlLogoOxxo()
	{
		CatalogVersionModel cat = new CatalogVersionModel();
		cat = getCatalogVersionService().getCatalogVersion(TELCEL_CONTENT_CATALOG, ONLINE);
		final MediaModel logoTelcel = getMediaService().getMedia(cat, IMAGES_THEME_LOGO_OXXO_PNG);
		final String imagePath = MediaUtil.getTenantMediaReadDir() + File.separator + logoTelcel.getLocation();
		LOG.info("MEDIA LOCATION : " + imagePath);
		return imagePath;
	}

	private String urlLogoImageNotAvailable()
	{
		CatalogVersionModel cat = new CatalogVersionModel();
		cat = getCatalogVersionService().getCatalogVersion(TELCEL_CONTENT_CATALOG, ONLINE);
		final MediaModel logoTelcel = getMediaService().getMedia(cat, IMAGES_THEME_NOT_AVAILABLE_PNG);
		final String imagePath = MediaUtil.getTenantMediaReadDir() + File.separator + logoTelcel.getLocation();
		LOG.info("MEDIA LOCATION : " + imagePath);
		return imagePath;
	}

	private List<OrderDetailProductData> amigoSinLimite(final AbstractOrderEntryModel orderDetail,
			final DecimalFormat decimalFormat)
	{
		final List<OrderDetailProductData> productData = new ArrayList<>();
		final List<AdditionalServiceEntryModel> asel = orderDetail.getAdditionalServiceEntries();
		if (Objects.nonNull(asel))
		{
			for (final AdditionalServiceEntryModel ase : asel)
			{
				final TelcelAdditionalServiceProductOfferingModel asep = ase.getAdditionalServiceProduct();
				//Todo: No debería usarse harcoded. los productos as tienen un type y ese type es para ver si es recarga_inicial
				if (asep.getCode().contains("recarga"))
				{
					final OrderDetailProductData productDataOD = new OrderDetailProductData();
					try
					{
						final String name = asep.getName(new Locale("es", "MX"));
						productDataOD.setRecargaName(name == null ? "" :"Incluye Paquete "+name);
						productDataOD.setRecargaPrice("" + ase.getBasePrice());
						productDataOD.setRecargaRejected(ase.getRejected());
						productData.add(productDataOD);
					}
					catch (final Exception e)
					{
						LOG.error("ERROR RECARGA AMIGO: " + e.getMessage());
					}
				}
			}
		}
		return productData;
	}

	private boolean validateField(final String field)
	{
		if (Objects.isNull(field))
		{
			return false;
		}
		if (field.trim().isEmpty())
		{
			return false;
		}
		return true;
	}

	private boolean containsCategory(final Collection<CategoryModel> list, final String code)
	{
		return list.stream().filter(o -> o.getCode().equals(code)).findFirst().isPresent();
	}

	public MediaService getMediaService()
	{
		return mediaService;
	}

	public CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}

}
