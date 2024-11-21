package mx.com.telcel.facades.populators;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.commercefacades.i18n.I18NFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.assertj.core.util.Strings;
import org.springframework.util.Assert;

import mx.com.telcel.core.model.AdditionalServiceEntryModel;
import mx.com.telcel.core.model.HolderLineModel;
import mx.com.telcel.core.model.ItemPaymentModel;
import mx.com.telcel.core.model.PaymentReceiptModel;
import mx.com.telcel.core.model.SeriesICCIDModel;
import mx.com.telcel.core.model.TelcelAdditionalServiceProductOfferingModel;
import mx.com.telcel.core.model.TelcelPoVariantModel;
import mx.com.telcel.core.util.TelcelUtil;
import mx.com.telcel.facades.order.data.OrderDetailAddressData;
import mx.com.telcel.facades.order.data.OrderDetailData;
import mx.com.telcel.facades.order.data.OrderDetailEntryDeniedData;
import mx.com.telcel.facades.order.data.OrderDetailImageData;
import mx.com.telcel.facades.order.data.OrderDetailPaymentData;
import mx.com.telcel.facades.order.data.OrderDetailProductData;


public class OrderDetailPopulator implements Populator<OrderModel, OrderDetailData>
{
	private static final Logger LOG = Logger.getLogger(OrderDetailPopulator.class);

	private static final String CATALOG_ID = "telcelProductCatalog";
	private static final String VERSION_ONLINE = "Online";
	private static final String YYYY_MM_DD = "yyyyMMdd";
	private static final String DD_DE_MMMM_DE_YYYY = "dd 'de' MMMM 'de' yyyy";
	private static final String ES = "es";
	private static final String MX = "MX";

	@Resource(name = "i18NFacade")
	private I18NFacade i18NFacade;

	@Resource(name = "productVariantFacade")
	private ProductFacade productFacade;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource(name = "defaultConfigurationService")
	private ConfigurationService configurationService;

	@Resource(name = "creditCardPaymentInfoConverter")
	private Converter<CreditCardPaymentInfoModel, CCPaymentInfoData> creditCardPaymentInfoConverter;

	@Resource(name = "productConverter")
	private Converter<ProductModel, ProductData> productConverter;

	@Resource
	private TelcelUtil telcelUtil; // NOSONAR

	@Override
	public void populate(final OrderModel source, final OrderDetailData target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");
		target.setOrderNumber(source.getCode());
		final DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		target.setPurchaseDate(df.format(source.getDate()));
		if (Objects.nonNull(source.getPaymentStatus()))
		{
			target.setPaymentStatus(source.getPaymentStatus().getCode());
		}

		final List<OrderDetailEntryDeniedData> entrysDenied = new ArrayList<>();
		if (Objects.nonNull(source.getPaymentInfo().getReceipt()))
		{
			final List<PaymentReceiptModel> paymentReceiptModelList = source.getPaymentInfo().getReceipt();
			for (final PaymentReceiptModel paymentReceiptModel : paymentReceiptModelList)
			{
				if ("DENIED".equalsIgnoreCase(paymentReceiptModel.getRisk()))
				{
					final List<ItemPaymentModel> itemPaymentModelList = paymentReceiptModel.getItemsPayment();
					for (final ItemPaymentModel itemPaymentModel : itemPaymentModelList)
					{
						final OrderDetailEntryDeniedData orderDetailEntryDeniedData = new OrderDetailEntryDeniedData();
						orderDetailEntryDeniedData.setSku(itemPaymentModel.getSku());
						orderDetailEntryDeniedData.setSkuFather(itemPaymentModel.getSkuFather());
						orderDetailEntryDeniedData.setEntryNumber(itemPaymentModel.getEntrynumber());
						entrysDenied.add(orderDetailEntryDeniedData);
					}
				}
			}
			target.setEntrysDenied(entrysDenied);

		}
		if (Objects.nonNull(source.getPaymentInfo().getReceipt()))
		{
			if (!source.getPaymentInfo().getReceipt().isEmpty())
			{
				final PaymentReceiptModel receipt = source.getPaymentInfo().getReceipt().get(0);
				if (StringUtils.isNotEmpty(receipt.getExpiration()))
				{
					//Date yyyy-MM-dd
					Date expiration = null;
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat(YYYY_MM_DD);
					try
					{
						expiration = simpleDateFormat.parse(receipt.getExpiration());
					}
					catch (final ParseException e)
					{
						LOG.error(String.format("OrderDetailPopulator - error converting expiration date %s", e.getMessage()));
					}

					final DateFormatSymbols formatSymbols = DateFormatSymbols.getInstance(new Locale(ES, MX));
					simpleDateFormat = new SimpleDateFormat(DD_DE_MMMM_DE_YYYY, formatSymbols);
					final String expirationDate = expiration != null ? simpleDateFormat.format(expiration) : "";

					target.setExpirationDate(expirationDate);
				}
			}
		}
		target.setEstimatedDeliveryDate(source.getEstimatedDateFedex() == null ? "" : source.getEstimatedDateFedex());
		target.setSubtotal(source.getSubtotal());
		target.setShippingCost(source.getDeliveryCost());
		target.setTotal(source.getTotalPrice());
		final HolderLineModel addressHolderLine = source.getAddressHolderLine();
		target.setCustomerName(telcelUtil.nameTwoFirstWords(addressHolderLine.getName(), addressHolderLine.getLastName()));
		if (Objects.nonNull(source.getDeliveryAddress()))
		{
			final AddressModel deliveryAddress = source.getDeliveryAddress();
			final OrderDetailAddressData address = new OrderDetailAddressData();
			address.setName(deliveryAddress.getFirstname() + " " + deliveryAddress.getLastname());
			address.setStreet(deliveryAddress.getStreetname());
			address.setNumber(deliveryAddress.getStreetnumber() == null ? "" : deliveryAddress.getStreetnumber());
			address.setDistrict(deliveryAddress.getDistrict());
			address.setPostalCode(deliveryAddress.getPostalcode());
			address.setTown(deliveryAddress.getTown());
			if (Objects.nonNull(deliveryAddress.getRegion()))
			{
				final RegionModel region = deliveryAddress.getRegion();
				final CountryModel country = region.getCountry();
				address.setState(i18NFacade.getRegion(country.getIsocode(), region.getIsocode()).getName());
			}
			target.setFormattedAddress(getFormattedAddress(deliveryAddress));
			target.setAddress(address);
		}
		final PaymentInfoModel paymentInfo = source.getPaymentInfo();
		if (paymentInfo instanceof CreditCardPaymentInfoModel)
		{
			final CreditCardPaymentInfoModel ccpInfo = (CreditCardPaymentInfoModel) paymentInfo;
			final CCPaymentInfoData paymentInfoData = creditCardPaymentInfoConverter.convert(ccpInfo);
			final OrderDetailPaymentData paymentData = new OrderDetailPaymentData();
			paymentData.setLastDigits(paymentInfoData.getCardNumber().replace("*", ""));
			paymentData.setMsi(ccpInfo.getMsi() != null ? Integer.valueOf(ccpInfo.getMsi()) : 0);
			paymentData.setCardType(paymentInfoData.getCardType());
			target.setPayment(paymentData);
		}
		final List<OrderDetailProductData> products = new ArrayList<OrderDetailProductData>();
		final Set<ConsignmentModel> consignments = source.getConsignments();
		if (Objects.nonNull(source.getEntries()))
		{
			catalogVersionService.setSessionCatalogVersion(CATALOG_ID, VERSION_ONLINE);
			for (final ConsignmentModel consignmentDetail : consignments)
			{
				final AbstractOrderEntryModel orderDetail = consignmentDetail.getConsignmentEntries().iterator().next()
						.getOrderEntry();
				final ProductModel productModel = orderDetail.getProduct();
				ProductData data = null;
				try
				{
					data = productFacade.getProductForCodeAndOptions(productModel.getCode(), null);
				}
				catch (final Exception e)
				{
					data = null;
				}
				if (Objects.isNull(data))
				{
					LOG.info("The product is not available");
					data = productConverter.convert(productModel);
				}
				final OrderDetailProductData productData = new OrderDetailProductData();
				final List<OrderDetailImageData> orderDetailImageDataList = new ArrayList<OrderDetailImageData>();
				final String hostMedia = configurationService.getConfiguration().getString("media.telcel.https");
				final Collection<ImageData> images = data.getImages();
				if (images != null)
				{
					final Iterator<ImageData> iterator = images.iterator();
					while (iterator.hasNext())
					{
						final ImageData image = iterator.next();
						final OrderDetailImageData orderDetailImageData = new OrderDetailImageData();
						orderDetailImageData.setFormat(image.getFormat());
						orderDetailImageData.setImageType(image.getImageType().name());
						orderDetailImageData.setUrl(hostMedia + image.getUrl());
						orderDetailImageDataList.add(orderDetailImageData);
					}
				}
				productData.setImages(orderDetailImageDataList);
				productData.setName(productName(productModel, data));
				productData.setPrice(String.valueOf(orderDetail.getTotalPrice()));
				productData.setBrand(data.getMarca());
				if (Objects.nonNull(data.getCapacidad()))
				{
					productData.setCapacity(data.getCapacidad().getValue() + " " + data.getCapacidad().getUnit());
				}
				if (Objects.nonNull(data.getColorData()))
				{
					productData.setColor(data.getColorData().getCode());
				}
				productData.setChargingScheme("");
				productData.setLineToRenew("");
				productData.setProductId(data.getSku());
				productData.setTrackStatus(findTrackStatusProduct(consignments, productModel.getCode()));
				productData.setFormattedDescription(getFormattedProductDescription(data));
				if (Objects.nonNull(source.getPaymentStatus()))
				{
					productData.setPaymentStatus(source.getPaymentStatus().getCode());
				}
				productData.setEstimatedDeliveryDate(source.getEstimatedDateFedex() == null ? "" : source.getEstimatedDateFedex());
				if (Objects.nonNull(consignmentDetail.getTelcelFactura()))
				{
					final List<String> invoices = new ArrayList<String>();
					/*
					 * for (final TelcelFacturaModel factura : orderDetail.getFacturas()) {
					 * invoices.add(factura.getNumeroFactura()); }
					 */
					invoices.add(consignmentDetail.getTelcelFactura().getNumeroFactura());
					productData.setInvoiceNumberList(invoices);
				}
				productData.setIncludeGift(data.isIncludeGift());
				productData.setDescripcionIncludeGift(data.getDescripcionIncludeGift());
				productData.setPaqueteAmigo("");
				productData.setVigenciaPaqueteAmigo("");
				//final List<AdditionalServiceEntryModel> asel = orderDetail.getAdditionalServiceEntries();
				final AdditionalServiceEntryModel ase = consignmentDetail.getAdditionalServiceEntry();
				if (Objects.nonNull(ase))
				{
					if (ase.getRejected().equals(Boolean.FALSE))
					{
						final TelcelAdditionalServiceProductOfferingModel asep = ase.getAdditionalServiceProduct();
						//Todo: No debería usarse harcoded. los productos as tienen un type y ese type es para ver si es recarga_inicial
						if (asep.getCode().contains("recarga"))
						{
							try
							{
								final String name = asep.getName(new Locale("es", "MX"));
								productData.setPaqueteAmigo(name == null ? "" : name);
							}
							catch (final Exception e)
							{
								final String name = asep.getName();
								productData.setPaqueteAmigo(name == null ? "" : name);
							}
						}
						if (asep.getCode().contains("100"))
						{
							final String vigencia100 = configurationService.getConfiguration().getString("telcel.paquete.amigo100");
							productData.setVigenciaPaqueteAmigo(vigencia100);
						}
						if (asep.getCode().contains("50"))
						{
							final String vigencia50 = configurationService.getConfiguration().getString("telcel.paquete.amigo50");
							productData.setVigenciaPaqueteAmigo(vigencia50);
						}
					}
				}
				try
				{
					//final ConsignmentEntryModel consignmentEntryModel = orderDetail.getConsignmentEntries().iterator().next();
					productData.setConsignmentCode(consignmentDetail.getCode());
					productData.setLinea("");
					final SeriesICCIDModel seriesiccid = consignmentDetail.getSeriesICCID();
					if (Objects.nonNull(seriesiccid))
					{
						if (!Strings.isNullOrEmpty(seriesiccid.getLinea()))
						{
							productData.setLinea(seriesiccid.getLinea());
						}
					}
				}
				catch (final Exception e)
				{
					LOG.info("ERROR CONSIGNMENT : " + e.getMessage());
				}
				products.add(productData);
			}
			LOG.info("PRODUCTOS : " + products.size());
			target.setProducts(products);
		}
	}

	private String findTrackStatusProduct(final Set<ConsignmentModel> consignments, final String productCode)
	{
		if (Objects.isNull(consignments))
		{
			return "";
		}
		final Iterator<ConsignmentModel> consignmentIterator = consignments.iterator();
		String statusTrack = "";
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
					statusTrack = consignment.getStatusRetrieveTrack();
					return statusTrack;
				}
			}
		}
		return statusTrack;
	}

	private String getFormattedAddress(final AddressModel address)
	{
		String formattedAddress = "";
		formattedAddress += address.getStreetname() + ", ";
		if (!Strings.isNullOrEmpty(address.getInteriorNumber()))
		{
			formattedAddress += "int." + address.getInteriorNumber() + ", ";
		}
		if (!Strings.isNullOrEmpty(address.getExternalNumber()))
		{
			formattedAddress += "ext." + address.getExternalNumber() + ", ";
		}
		formattedAddress += address.getDistrict() + ", ";
		formattedAddress += address.getTown() + ", ";
		formattedAddress += "C.P." + address.getPostalcode() + ", ";
		if (Objects.nonNull(address.getRegion()))
		{
			final RegionModel region = address.getRegion();
			final CountryModel country = region.getCountry();
			formattedAddress += i18NFacade.getRegion(country.getIsocode(), region.getIsocode()).getName();
		}
		if (!Strings.isNullOrEmpty(address.getReferences()))
		{
			formattedAddress += ", Ref." + address.getReferences();
		}
		return formattedAddress;
	}

	private String getFormattedProductDescription(final ProductData data)
	{
		String formattedDescription = "";
		if (!Strings.isNullOrEmpty(data.getMarca()))
		{
			formattedDescription += data.getMarca() + " | ";
		}
		if (Objects.nonNull(data.getCapacidad()))
		{
			formattedDescription += (data.getCapacidad().getValue() + " " + data.getCapacidad().getUnit()) + " | ";
		}
		if (Objects.nonNull(data.getColorData()))
		{
			formattedDescription += data.getColorData().getCode() + " | ";
		}
		formattedDescription = formattedDescription.substring(0, formattedDescription.length() - 3);
		return formattedDescription;
	}

	private String getNameMonth(final String code)
	{
		switch (code)
		{
			case "01":
				return "enero";
			case "02":
				return "febrero";
			case "03":
				return "marzo";
			case "04":
				return "abril";
			case "05":
				return "mayo";
			case "06":
				return "junio";
			case "07":
				return "julio";
			case "08":
				return "agosto";
			case "09":
				return "septiembre";
			case "10":
				return "octubre";
			case "11":
				return "noviembre";
			case "12":
				return "diciembre";
			default:
				return "";
		}
	}

	private String productName(final ProductModel product, final ProductData data)
	{
		try
		{
			final TelcelPoVariantModel productVariant = (TelcelPoVariantModel) product;
			return productVariant.getName();
		}
		catch (final Exception e)
		{
			return data.getBaseProduct() == null ? "" : data.getBaseProduct().replaceAll("_", " ");
		}
	}

}
