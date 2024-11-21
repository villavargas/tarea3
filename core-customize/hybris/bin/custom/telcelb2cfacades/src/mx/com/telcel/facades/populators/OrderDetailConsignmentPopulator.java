package mx.com.telcel.facades.populators;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.commercefacades.i18n.I18NFacade;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.assertj.core.util.Strings;
import org.springframework.util.Assert;

import mx.com.telcel.core.model.AdditionalServiceEntryModel;
import mx.com.telcel.core.model.HolderLineModel;
import mx.com.telcel.core.model.SeriesICCIDModel;
import mx.com.telcel.core.model.TelcelAdditionalServiceProductOfferingModel;
import mx.com.telcel.core.model.TelcelPoVariantModel;
import mx.com.telcel.core.util.TelcelUtil;
import mx.com.telcel.facades.order.data.OrderDetailData;
import mx.com.telcel.facades.order.data.OrderDetailImageData;
import mx.com.telcel.facades.order.data.OrderDetailProductData;


public class OrderDetailConsignmentPopulator implements Populator<ConsignmentModel, OrderDetailData>
{

	private static final Logger LOG = Logger.getLogger(OrderDetailConsignmentPopulator.class);
	private static final String CATALOG_ID = "telcelProductCatalog";
	private static final String VERSION_ONLINE = "Online";
	private final Collection<ProductOption> PRODUCT_OPTIONS = Arrays.asList(ProductOption.BASIC, ProductOption.DESCRIPTION,
			ProductOption.SOLD_INDIVIDUALLY, ProductOption.PRODUCT_SPECIFICATION, ProductOption.CATEGORIES,
			ProductOption.PRODUCT_MEDIA, ProductOption.PRODUCT_BPO_CHILDREN, ProductOption.PRODUCT_OFFERING_PRICES,
			ProductOption.PARENT_BPOS, ProductOption.PRODUCT_OFFERING_GROUPS, ProductOption.PRODUCT_SPEC_CHAR_VALUE_USE,
			ProductOption.CLASSIFICATION, ProductOption.VARIANT_MATRIX, ProductOption.VARIANT_MATRIX_ALL_OPTIONS,
			ProductOption.MEDIA_ATTACHMENT, ProductOption.REVIEW);

	@Resource(name = "i18NFacade")
	private I18NFacade i18NFacade;

	@Resource(name = "productVariantFacade")
	private ProductFacade productFacade;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource(name = "defaultConfigurationService")
	private ConfigurationService configurationService;

	@Resource(name = "productConverter")
	private Converter<ProductModel, ProductData> productConverter;

	@Resource
	private TelcelUtil telcelUtil; // NOSONAR

	@Override
	public void populate(final ConsignmentModel source, final OrderDetailData target) throws ConversionException
	{

		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");
		final AbstractOrderModel order = source.getOrder();
		final HolderLineModel addressHolderLine = order.getAddressHolderLine();

		target.setOrderNumber(order.getCode());
		target.setCustomerName(telcelUtil.nameTwoFirstWords(addressHolderLine.getName(), addressHolderLine.getLastName()));
		if (Objects.nonNull(order.getUser()))
		{
			if (order.getUser() instanceof CustomerModel)
			{
				final CustomerModel customer = (CustomerModel) order.getUser();
				target.setNumberPhone(customer.getNumberPhone() == null ? "" : customer.getNumberPhone());
			}
		}
		if (Objects.nonNull(source.getRealDeliveryDateFedex()))
		{
			final Calendar calendar = Calendar.getInstance();
			calendar.setTime(source.getRealDeliveryDateFedex());
			final DecimalFormat formatter = new DecimalFormat("00");
			final String[] dateData = new String[3];
			dateData[0] = "" + calendar.get(Calendar.YEAR);
			dateData[1] = getNameMonthInt(calendar.get(Calendar.MONTH));
			dateData[2] = formatter.format(calendar.get(Calendar.DAY_OF_MONTH));
			target.setDateOfDelivery(dateData);
		}
		if (Objects.nonNull(source.getOrder().getDeliveryAddress()))
		{
			target.setFormattedAddress(getFormattedAddress(source.getOrder().getDeliveryAddress()));
		}
		final List<OrderDetailProductData> products = new ArrayList<OrderDetailProductData>();
		if (Objects.nonNull(source.getConsignmentEntries()))
		{
			catalogVersionService.setSessionCatalogVersion(CATALOG_ID, VERSION_ONLINE);
			for (final ConsignmentEntryModel orderDetail : source.getConsignmentEntries())
			{
				final ProductModel productModel = orderDetail.getOrderEntry().getProduct();
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
				productData.setFormattedDescription(getFormattedProductDescription(data));
				productData.setIncludeGift(data.isIncludeGift());
				productData.setDescripcionIncludeGift(data.getDescripcionIncludeGift());
				productData.setPaqueteAmigo("");
				productData.setVigenciaPaqueteAmigo("");
				final List<AdditionalServiceEntryModel> asel = orderDetail.getOrderEntry().getAdditionalServiceEntries();
				for (final AdditionalServiceEntryModel ase : asel)
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
				productData.setConsignmentCode(source.getCode());
				productData.setLinea("");
				final SeriesICCIDModel seriesiccid = orderDetail.getConsignment().getSeriesICCID();
				if (Objects.nonNull(seriesiccid))
				{
					if (!Strings.isNullOrEmpty(seriesiccid.getLinea()))
					{
						productData.setLinea(seriesiccid.getLinea());
					}
				}
				products.add(productData);
			}
			target.setProducts(products);
		}
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

	private String getNameMonthInt(final Integer code)
	{
		switch (code)
		{
			case 0:
				return "enero";
			case 1:
				return "febrero";
			case 2:
				return "marzo";
			case 3:
				return "abril";
			case 4:
				return "mayo";
			case 5:
				return "junio";
			case 6:
				return "julio";
			case 7:
				return "agosto";
			case 8:
				return "septiembre";
			case 9:
				return "octubre";
			case 10:
				return "noviembre";
			case 11:
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
