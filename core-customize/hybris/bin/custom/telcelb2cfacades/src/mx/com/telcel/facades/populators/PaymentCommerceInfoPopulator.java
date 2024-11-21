package mx.com.telcel.facades.populators;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.ReferencePaymentInfoModel;
import de.hybris.platform.core.model.order.payment.SpeiPaymentInfoModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import mx.com.telcel.core.services.TelcelCodigosPostalesService;
import mx.com.telcel.facades.paymentcommerce.data.ItemPCData;
import mx.com.telcel.facades.paymentcommerce.data.OrderPCData;
import mx.com.telcel.facades.paymentcommerce.data.PaymentCommerceInfoData;
import mx.com.telcel.facades.paymentcommerce.data.PaymentPCData;
import mx.com.telcel.facades.zipcodes.data.CodigosPostalesTelcelData;


public class PaymentCommerceInfoPopulator implements Populator<OrderModel, PaymentCommerceInfoData>
{

	@Resource(name = "baseStoreService")
	private BaseStoreService baseStoreService;

	@Resource(name = "customerAccountService")
	private CustomerAccountService customerAccountService;

	@Resource(name = "productVariantFacade")
	private ProductFacade productFacade;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource(name = "creditCardPaymentInfoConverter")
	private Converter<CreditCardPaymentInfoModel, CCPaymentInfoData> creditCardPaymentInfoConverter;

	@Resource(name = "referencePaymentInfoConverter")
	private Converter<ReferencePaymentInfoModel, CCPaymentInfoData> referencePaymentInfoConverter;

	@Resource(name = "speiPaymentInfoConverter")
	private Converter<SpeiPaymentInfoModel, CCPaymentInfoData> speiPaymentInfoConverter;

	@Resource(name = "telcelCodigosPostalesService")
	private TelcelCodigosPostalesService telcelCodigosPostalesService;

	private static final String CATALOG_ID = "telcelProductCatalog";
	private static final String VERSION_ONLINE = "Online";
	private static final Logger LOG = Logger.getLogger(PaymentCommerceInfoPopulator.class);

	@Override
	public void populate(final OrderModel source, final PaymentCommerceInfoData target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");
		final OrderPCData orderData = new OrderPCData();
		final PaymentPCData paymentData = new PaymentPCData();
		final List<ItemPCData> items = new ArrayList<>();
		final PaymentInfoModel paymentInfo = source.getPaymentInfo();
		if (paymentInfo instanceof CreditCardPaymentInfoModel)
		{
			final CreditCardPaymentInfoModel ccpInfo = (CreditCardPaymentInfoModel) paymentInfo;
			final CCPaymentInfoData paymentInfoData = creditCardPaymentInfoConverter.convert(ccpInfo);
			paymentData.setLastDigits(paymentInfoData.getCardNumber().replace("*", ""));
			paymentData.setAmount(source.getTotalPrice());
			paymentData.setTokenCard(ccpInfo.getTokenCardTelcel());
			paymentData.setPromotions(ccpInfo.getPromotions());

			if(ccpInfo.getOpenpayKeys() != null){
				paymentData.setMerchantId(ccpInfo.getOpenpayKeys().getMerchantId());
				paymentData.setApiKey(ccpInfo.getOpenpayKeys().getApiKey());
			}

			if(ccpInfo.getCybersourceKeys() != null){
				paymentData.setSessionId(ccpInfo.getCybersourceKeys().getSessionId());
			}
			paymentData.setPaymentType(ccpInfo.getPaymentType());
			final AddressData addressBilling = paymentInfoData.getBillingAddress();
			if (Objects.nonNull(addressBilling))
			{
				paymentData.setFirstname(addressBilling.getFirstName());
				paymentData.setLastname(addressBilling.getLastName());
				final CodigosPostalesTelcelData cpdata = telcelCodigosPostalesService
						.getInfForZipcode(addressBilling.getPostalCode());
				orderData.setRegion(Integer.parseInt(cpdata.getRegion().getCode()));
				orderData.setCity(addressBilling.getTown());
				orderData.setAddress(addressBilling.getFormattedAddress());
				orderData.setPostalCode(addressBilling.getPostalCode());
				orderData.setEmail(addressBilling.getEmail());
				orderData.setPhone(addressBilling.getPhone());
			}
		}

		if (paymentInfo instanceof ReferencePaymentInfoModel)
		{
			final ReferencePaymentInfoModel ccpInfo = (ReferencePaymentInfoModel) paymentInfo;
			final CCPaymentInfoData paymentInfoData = getReferencePaymentInfoConverter().convert(ccpInfo);
			paymentData.setLastDigits(paymentInfoData.getCardNumber().replace("*", ""));
			paymentData.setAmount(source.getTotalPrice());

			if(ccpInfo.getOpenpayKeys() != null){
				paymentData.setMerchantId(ccpInfo.getOpenpayKeys().getMerchantId());
				paymentData.setApiKey(ccpInfo.getOpenpayKeys().getApiKey());
			}
			paymentData.setPaymentType(ccpInfo.getPaymentType());
			final AddressData addressBilling = paymentInfoData.getBillingAddress();
			if (Objects.nonNull(addressBilling))
			{
				paymentData.setFirstname(addressBilling.getFirstName());
				paymentData.setLastname(addressBilling.getLastName());
				final CodigosPostalesTelcelData cpdata = telcelCodigosPostalesService
						.getInfForZipcode(addressBilling.getPostalCode());
				orderData.setRegion(Integer.parseInt(cpdata.getRegion().getCode()));
				orderData.setCity(addressBilling.getTown());
				orderData.setAddress(addressBilling.getFormattedAddress());
				orderData.setPostalCode(addressBilling.getPostalCode());
				orderData.setEmail(addressBilling.getEmail());
				orderData.setPhone(addressBilling.getPhone());
			}
		}
		if (paymentInfo instanceof SpeiPaymentInfoModel)
		{
			final SpeiPaymentInfoModel ccpInfo = (SpeiPaymentInfoModel) paymentInfo;
			final CCPaymentInfoData paymentInfoData = getSpeiPaymentInfoConverter().convert(ccpInfo);
			paymentData.setLastDigits(paymentInfoData.getCardNumber().replace("*", ""));
			paymentData.setAmount(source.getTotalPrice());

			if(ccpInfo.getOpenpayKeys() != null){
				paymentData.setMerchantId(ccpInfo.getOpenpayKeys().getMerchantId());
				paymentData.setApiKey(ccpInfo.getOpenpayKeys().getApiKey());
			}
			paymentData.setPaymentType(ccpInfo.getPaymentType());
			final AddressData addressBilling = paymentInfoData.getBillingAddress();
			if (Objects.nonNull(addressBilling))
			{
				paymentData.setFirstname(addressBilling.getFirstName());
				paymentData.setLastname(addressBilling.getLastName());
				final CodigosPostalesTelcelData cpdata = telcelCodigosPostalesService
						.getInfForZipcode(addressBilling.getPostalCode());
				orderData.setRegion(Integer.parseInt(cpdata.getRegion().getCode()));
				orderData.setCity(addressBilling.getTown());
				orderData.setAddress(addressBilling.getFormattedAddress());
				orderData.setPostalCode(addressBilling.getPostalCode());
				orderData.setEmail(addressBilling.getEmail());
				orderData.setPhone(addressBilling.getPhone());
			}
		}
		if (Objects.nonNull(source.getEntries()))
		{
			catalogVersionService.setSessionCatalogVersion(CATALOG_ID, VERSION_ONLINE);
			for (final AbstractOrderEntryModel orderDetail : source.getEntries())
			{
				final ProductModel productModel = orderDetail.getProduct();
				final ProductData data = productFacade.getProductForCodeAndOptions(productModel.getCode(), null);
				final ItemPCData item = new ItemPCData();
				item.setSku(data.getSku());
				item.setDescription(data.getBaseProduct() == null ? "" : data.getBaseProduct().replaceAll("_", " "));
				item.setPrice(orderDetail.getTotalPrice());
				item.setModel(data.getModelo());
				item.setColor(StringUtils.isNotEmpty(data.getColorData().getCode()) ? data.getColorData().getCode() : "NA");
				items.add(item);
			}
			paymentData.setItems(items);
		}
		target.setOrder(orderData);
		target.setPayment(paymentData);
	}

	public Converter<ReferencePaymentInfoModel, CCPaymentInfoData> getReferencePaymentInfoConverter() {
		return referencePaymentInfoConverter;
	}

	public void setReferencePaymentInfoConverter(Converter<ReferencePaymentInfoModel, CCPaymentInfoData> referencePaymentInfoConverter) {
		this.referencePaymentInfoConverter = referencePaymentInfoConverter;
	}

	public Converter<SpeiPaymentInfoModel, CCPaymentInfoData> getSpeiPaymentInfoConverter() {
		return speiPaymentInfoConverter;
	}

	public void setSpeiPaymentInfoConverter(Converter<SpeiPaymentInfoModel, CCPaymentInfoData> speiPaymentInfoConverter) {
		this.speiPaymentInfoConverter = speiPaymentInfoConverter;
	}
}
