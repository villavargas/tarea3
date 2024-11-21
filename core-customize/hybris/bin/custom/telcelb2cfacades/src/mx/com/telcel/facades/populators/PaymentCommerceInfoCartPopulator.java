package mx.com.telcel.facades.populators;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.ReferencePaymentInfoModel;
import de.hybris.platform.core.model.order.payment.SpeiPaymentInfoModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.payment.dto.FingerPrint;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.store.services.BaseStoreService;
import mx.com.telcel.core.model.AdditionalServiceEntryModel;
import mx.com.telcel.core.services.TelcelCodigosPostalesService;
import mx.com.telcel.core.util.TelcelUtil;
import mx.com.telcel.facades.paymentcommerce.data.ItemPCData;
import mx.com.telcel.facades.paymentcommerce.data.OrderPCData;
import mx.com.telcel.facades.paymentcommerce.data.PaymentCommerceInfoData;
import mx.com.telcel.facades.paymentcommerce.data.PaymentPCData;
import mx.com.telcel.facades.zipcodes.data.CodigosPostalesTelcelData;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class PaymentCommerceInfoCartPopulator implements Populator<CartModel, PaymentCommerceInfoData>
{

	private  static final String PIPE = "|";
	public static final String NA = "NA";

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

	@Resource(name = "addressConverter")
	private Converter<AddressModel, AddressData> addressConverter;

	@Resource
	private TelcelUtil telcelUtil; // NOSONAR

	private static final String CATALOG_ID = "telcelProductCatalog";
	private static final String VERSION_ONLINE = "Online";
	private static final Logger LOG = Logger.getLogger(PaymentCommerceInfoCartPopulator.class);

	@Override
	public void populate(final CartModel source, final PaymentCommerceInfoData target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");
		final OrderPCData orderData = new OrderPCData();
		final PaymentPCData paymentData = new PaymentPCData();
		final List<ItemPCData> items = new ArrayList<>();
		final List<ItemPCData> itemsSA = new ArrayList<>();
		final PaymentInfoModel paymentInfo = source.getPaymentInfo();
		String phoneCustomer = StringUtils.EMPTY;
		String emailGuest ="";
		if(source.getUser() instanceof CustomerModel){
			CustomerModel customerModel = (CustomerModel) source.getUser();
			phoneCustomer = customerModel.getNumberPhone();
			if (customerModel.getType() != null && customerModel.getType().equals(CustomerType.GUEST)) {
				emailGuest = StringUtils.substringAfter(customerModel.getUid(), PIPE);
			}
		}

		String externalNumber = "";
		if(source.getDeliveryAddress() != null ){
			externalNumber = source.getDeliveryAddress().getExternalNumber();
		}
		if (paymentInfo instanceof CreditCardPaymentInfoModel)
		{
			final CreditCardPaymentInfoModel ccpInfo = (CreditCardPaymentInfoModel) paymentInfo;
			final CCPaymentInfoData paymentInfoData = creditCardPaymentInfoConverter.convert(ccpInfo);
			paymentData.setLastDigits(paymentInfoData.getCardNumber().replace("*", ""));
			paymentData.setAmount(source.getTotalPrice());
			paymentData.setTokenCard(ccpInfo.getTokenCardTelcel());
			paymentData.setPromotions(ccpInfo.getPromotions());
			paymentData.setType(paymentInfoData.getCardType());

			FingerPrint fingerPrint = new FingerPrint();
			if(paymentInfo.getFingerprint() != null){
				fingerPrint.setSessionId(paymentInfo.getFingerprint().getSessionId());
				fingerPrint.setOrganizationId(paymentInfo.getFingerprint().getOrganizationId());
				fingerPrint.setWebSession(paymentInfo.getFingerprint().getWebSession());
				paymentInfoData.setFingerprint(fingerPrint);
			}


			if(ccpInfo.getOpenpayKeys() != null){
				paymentData.setMerchantId(ccpInfo.getOpenpayKeys().getMerchantId());
				paymentData.setApiKey(ccpInfo.getOpenpayKeys().getApiKey());
			}

			if(ccpInfo.getCybersourceKeys()!= null){
				paymentData.setSessionId(ccpInfo.getCybersourceKeys().getSessionId());
			}
			paymentData.setPaymentType(ccpInfo.getPaymentType());
			paymentData.setMsi(ccpInfo.getMsi() != null ? Integer.valueOf(ccpInfo.getMsi() ): 0);

			final AddressData addressBilling = paymentInfoData.getBillingAddress();
			if (Objects.nonNull(addressBilling))
			{
				paymentData.setFirstname(StringUtils.isNotEmpty(ccpInfo.getHolderName()) ? ccpInfo.getHolderName() : NA);
				paymentData.setLastname(StringUtils.isNotEmpty(ccpInfo.getHolderLastName()) ? ccpInfo.getHolderLastName() :NA);
				final CodigosPostalesTelcelData cpdata = telcelCodigosPostalesService
						.getInfForZipcode(addressBilling.getPostalCode());
				orderData.setRegion(Integer.parseInt(cpdata.getRegion().getCode()));
				orderData.setCity(StringUtils.isNotEmpty(addressBilling.getTown()) ? addressBilling.getTown() : NA);
				orderData.setState(Objects.isNull(cpdata.getEstado()) ? NA :cpdata.getEstado().getName());
				orderData.setAddress(StringUtils.isNotEmpty(addressBilling.getCalle()) ? addressBilling.getCalle() + " " + externalNumber: NA);
				orderData.setPostalCode(StringUtils.isNotEmpty(addressBilling.getPostalCode()) ? addressBilling.getPostalCode() : NA );

				if(source.getUser() instanceof CustomerModel){
					CustomerModel customerModel = (CustomerModel) source.getUser();
					final String emailUid = CustomerType.GUEST.equals(customerModel.getType()) ? StringUtils.substringAfter(
							customerModel.getUid(), "|") : telcelUtil.getEmailRegisterForToken(customerModel.getUid(),customerModel.getContactEmail() );
					orderData.setEmail(StringUtils.isNotEmpty(emailGuest) ?  emailGuest : emailUid);

				}

				orderData.setPhone(StringUtils.isNotEmpty(addressBilling.getPhone()) ? addressBilling.getPhone() : phoneCustomer);

			}
		}
		if (paymentInfo instanceof ReferencePaymentInfoModel)
		{
			final ReferencePaymentInfoModel ccpInfo = (ReferencePaymentInfoModel) paymentInfo;
			final CCPaymentInfoData paymentInfoData = getReferencePaymentInfoConverter().convert(ccpInfo);
//			paymentData.setLastDigits(paymentInfoData.getCardNumber().replace("*", ""));
			paymentData.setAmount(source.getTotalPrice());
//			paymentData.setTokenCard(ccpInfo.getTokenCardTelcel());
			if(ccpInfo.getOpenpayKeys() != null){
				paymentData.setMerchantId(ccpInfo.getOpenpayKeys().getMerchantId());
				paymentData.setApiKey(ccpInfo.getOpenpayKeys().getApiKey());
			}
			paymentData.setPaymentType(ccpInfo.getPaymentType());
			paymentData.setPaymentReferenceName(ccpInfo.getPaymentReferenceName());
			final AddressData addressBilling = paymentInfoData.getBillingAddress();
			if (Objects.nonNull(addressBilling))
			{
				paymentData.setFirstname(addressBilling.getFirstName());
				paymentData.setLastname(addressBilling.getLastName());
				final CodigosPostalesTelcelData cpdata = telcelCodigosPostalesService
						.getInfForZipcode(addressBilling.getPostalCode());
				orderData.setRegion(Integer.parseInt(cpdata.getRegion().getCode()));
				orderData.setCity(addressBilling.getTown());
				orderData.setState(Objects.isNull(cpdata.getEstado()) ? NA :cpdata.getEstado().getName());
				orderData.setAddress(addressBilling.getCalle() + " " + externalNumber);
				orderData.setPostalCode(addressBilling.getPostalCode());
				if(source.getUser() instanceof CustomerModel){
					CustomerModel customerModel = (CustomerModel) source.getUser();
					final String emailUid = CustomerType.GUEST.equals(customerModel.getType()) ? StringUtils.substringAfter(
							customerModel.getUid(), "|") : telcelUtil.getEmailRegisterForToken(customerModel.getUid(),customerModel.getContactEmail() );
					orderData.setEmail(StringUtils.isNotEmpty(emailGuest) ?  emailGuest : emailUid);

				}
				orderData.setPhone(StringUtils.isNotEmpty(addressBilling.getPhone()) ? addressBilling.getPhone() : phoneCustomer);
			}
		}
		if (paymentInfo instanceof SpeiPaymentInfoModel)
		{
			final SpeiPaymentInfoModel ccpInfo = (SpeiPaymentInfoModel) paymentInfo;
			final CCPaymentInfoData paymentInfoData = getSpeiPaymentInfoConverter().convert(ccpInfo);
//			paymentData.setLastDigits(paymentInfoData.getCardNumber().replace("*", ""));
			paymentData.setAmount(source.getTotalPrice());
//			paymentData.setTokenCard(ccpInfo.getTokenCardTelcel());
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
				orderData.setState(Objects.isNull(cpdata.getEstado()) ? NA :cpdata.getEstado().getName());
				orderData.setAddress(addressBilling.getCalle() + " " + externalNumber);
				orderData.setPostalCode(addressBilling.getPostalCode());
				if(source.getUser() instanceof CustomerModel){
					CustomerModel customerModel = (CustomerModel) source.getUser();
					final String emailUid = CustomerType.GUEST.equals(customerModel.getType()) ? StringUtils.substringAfter(
							customerModel.getUid(), "|") : telcelUtil.getEmailRegisterForToken(customerModel.getUid(),customerModel.getContactEmail() );
					orderData.setEmail(StringUtils.isNotEmpty(emailGuest) ?  emailGuest : emailUid);

				}
				orderData.setPhone(StringUtils.isNotEmpty(addressBilling.getPhone()) ? addressBilling.getPhone() : phoneCustomer);
			}
		}
		if (Objects.nonNull(source.getEntries()))
		{
			catalogVersionService.setSessionCatalogVersion(CATALOG_ID, VERSION_ONLINE);
			for (final AbstractOrderEntryModel orderDetail : source.getEntries())
			{
				int itemQty = 0;
				for(int i = 1 ; i <= orderDetail.getQuantity().intValue() ; i++ ){
					final ProductModel productModel = orderDetail.getProduct();
					final ProductData data = productFacade.getProductForCodeAndOptions(productModel.getCode(), null);
					final ItemPCData item = new ItemPCData();
					item.setSku(data.getSku());
					item.setDescription(data.getBaseProduct() == null ? NA : data.getBaseProduct().replaceAll("_", " "));
					item.setModel(data.getModelo());
					item.setColor(data.getColorData() != null ? data.getColorData().getCode() : NA);
					item.setType("product");


					Double servicioAdicionales = 0D;
					if(!orderDetail.getAdditionalServiceEntries().isEmpty() && orderDetail.getAdditionalServiceEntries().size() >= (itemQty+1)){
						final AdditionalServiceEntryModel additionalServiceEntryModel = orderDetail.getAdditionalServiceEntries().get(itemQty);
						if(!additionalServiceEntryModel.getRejected()){
							final ProductModel productAdditionServiceModel = additionalServiceEntryModel.getAdditionalServiceProduct();
							final ProductData dataAdditionService = productFacade.getProductForCodeAndOptions(productAdditionServiceModel.getCode(), null);
							final ItemPCData itemAdditionService = new ItemPCData();
							itemAdditionService.setSku(dataAdditionService.getSku());
							itemAdditionService.setDescription(dataAdditionService.getCode().replaceAll("_", " "));
							servicioAdicionales = additionalServiceEntryModel.getBasePrice();
							itemAdditionService.setPrice(additionalServiceEntryModel.getBasePrice());
							itemAdditionService.setModel(dataAdditionService.getModelo() != null ? dataAdditionService.getModelo() : NA);
							itemAdditionService.setColor(dataAdditionService.getColorData() != null ? dataAdditionService.getColorData().getCode() : NA);
							itemAdditionService.setType("service");
							itemsSA.add(itemAdditionService);
						}
					}
					item.setPrice(orderDetail.getBasePrice());
					items.add(item);
					itemQty++;
				}
//				items.addAll(itemsSA);

			}
			paymentData.setItems(items);
			paymentData.setItemsSa(itemsSA);
		}


		target.setOrder(orderData);
		target.setPayment(paymentData);
		target.setValidatePayment(source.getStatusValidePayment());
		target.setExecutePayment(source.getStatusConfirmatePayment());
	}


	public Converter<AddressModel, AddressData> getAddressConverter() {
		return addressConverter;
	}

	public void setAddressConverter(Converter<AddressModel, AddressData> addressConverter) {
		this.addressConverter = addressConverter;
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
