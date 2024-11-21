package mx.com.telcel.facades.process.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import mx.com.telcel.core.model.CancellationUnderpaymentProcessModel;
import mx.com.telcel.core.util.TelcelUtil;
import mx.com.telcel.facades.product.TelcelProductFacade;


/**
 * The type Cancellation underpayment email context.
 */
public class CancellationUnderpaymentEmailContext extends AbstractEmailContext<CancellationUnderpaymentProcessModel>
{

	private static final String EMAIL = "email";
	private static final String CUSTOMER_ID = "customerID";
	private static final String CUSTOMER_NAME = "customerName";
	private static final String ORDER_ID = "orderID";
	private static final String ADDRESS = "address";
	private static final String COMMA = ", ";
	private static final String C_P = "C.P.";
	private static final String DOT = ".";
	private static final String SPACE = " ";
	private static final String PAYMENT_TYPE = "paymentType";
	private static final String SUBTOTAL = "subtotal";
	private static final String DELIVERY_COST = "deliveryCost";
	private static final String TOTAL = "total";
	private final DecimalFormat decimalFormat = new DecimalFormat("###,##0.00");
	private Converter<OrderModel, OrderData> orderConverter;
	private static final String ENTRIES = "entries";
	private static final String CATALOG_ID = "telcelProductCatalog";
	private static final String TELCEL_PRODUCT_CATALOG = "telcelProductCatalog";
	private static final String VERSION_ONLINE = "Online";

	@Resource(name = "productFacade")
	private TelcelProductFacade productFacade;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private TelcelUtil telcelUtil;

	@Override
	public void init(final CancellationUnderpaymentProcessModel cancellationUnderpaymentEmailProcessModel,
			final EmailPageModel emailPageModel)
	{
		super.init(cancellationUnderpaymentEmailProcessModel, emailPageModel);
		catalogVersionService.setSessionCatalogVersion(CATALOG_ID, VERSION_ONLINE);
		catalogVersionService.setSessionCatalogVersion(TELCEL_PRODUCT_CATALOG, VERSION_ONLINE);
		final AbstractOrderModel orderModel = cancellationUnderpaymentEmailProcessModel.getOrder();
		final CustomerModel customerModel = (CustomerModel) orderModel.getUser();
		put(CUSTOMER_ID, customerModel.getCustomerID());
		put(CUSTOMER_NAME, telcelUtil.nameTwoFirstWords(customerModel.getName()));
		put(ORDER_ID, orderModel.getCode());

		final AddressModel addressModel = orderModel.getDeliveryAddress();
		if (Objects.nonNull(addressModel))
		{
			put(ADDRESS, getAddress(addressModel));
		}
		put(PAYMENT_TYPE, getPaymentType(orderModel.getPaymentInfo()));
		put(SUBTOTAL, decimalFormat.format(orderModel.getSubtotal()));
		put(DELIVERY_COST, decimalFormat.format(orderModel.getDeliveryCost()));
		put(TOTAL, decimalFormat.format(orderModel.getTotalPrice()));

		final OrderData orderData = getOrderConverter().convert((OrderModel) orderModel);
		final List<AbstractOrderEntryModel> entries = orderModel.getEntries();
		int index = 0;
		for (final OrderEntryData oed : orderData.getEntries())
		{
			productFacade.setProductDataPaqueteAmigo(oed.getProduct(), entries.get(index).getAdditionalServiceEntries());
			index++;
		}
		put(ENTRIES, orderData.getEntries());
		put("StringUtils", StringUtils.class);

		put(EMAIL, orderModel.getAddressHolderLine().getEmail());
		put(FROM_EMAIL, emailPageModel.getFromEmail());
		put(DISPLAY_NAME, emailPageModel.getFromEmail());
	}

	private String getAddress(final AddressModel addressModel)
	{
		final StringBuilder builder = new StringBuilder();
		builder.append(addressModel.getStreetname());
		builder.append(SPACE);
		builder.append(addressModel.getExternalNumber());
		builder.append(COMMA);
		builder.append(StringUtils.isNotEmpty(addressModel.getInteriorNumber()) ? "Int. " + addressModel.getInteriorNumber() + COMMA
				: StringUtils.EMPTY);
		builder.append(addressModel.getDistrict());
		builder.append(COMMA);
		builder.append(addressModel.getTown());
		builder.append(COMMA);
		builder.append(C_P);
		builder.append(addressModel.getPostalcode());
		builder.append(COMMA);
		builder.append(addressModel.getRegion().getName());
		builder.append(DOT);
		return builder.toString();
	}

	private String getPaymentType(final PaymentInfoModel paymentInfo)
	{
		String result = "";

		String type = paymentInfo.getPaymentType();
		if (StringUtils.isNotEmpty(type))
		{
			switch (type)
			{
				case "SPEI":
					result = "SPEI";
					break;
				case "REFERENCE":
					result = "Referencia";
					break;
			}
		}

		if (StringUtils.isEmpty(result))
		{
			if (CollectionUtils.isNotEmpty(paymentInfo.getReceipt()))
			{
				type = paymentInfo.getReceipt().get(0).getType();
				if (StringUtils.isNotEmpty(type))
				{
					switch (type)
					{
						case "DEBIT":
							result = "Debito";
							break;
						case "CREDIT":
							result = "Credito";
							break;
					}
				}
			}

		}

		return result;
	}


	@Override
	protected BaseSiteModel getSite(final CancellationUnderpaymentProcessModel businessProcessModel)
	{
		return businessProcessModel.getSite();
	}

	@Override
	protected CustomerModel getCustomer(final CancellationUnderpaymentProcessModel businessProcessModel)
	{
		return businessProcessModel.getCustomer();
	}

	@Override
	protected LanguageModel getEmailLanguage(final CancellationUnderpaymentProcessModel businessProcessModel)
	{
		return businessProcessModel.getLanguage();
	}

	/**
	 * Gets order converter.
	 *
	 * @return the order converter
	 */
	public Converter<OrderModel, OrderData> getOrderConverter()
	{
		return orderConverter;
	}

	/**
	 * Sets order converter.
	 *
	 * @param orderConverter
	 *           the order converter
	 */
	public void setOrderConverter(final Converter<OrderModel, OrderData> orderConverter)
	{
		this.orderConverter = orderConverter;
	}
}
