package mx.com.telcel.facades.process.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.media.MediaService;

import java.text.DecimalFormat;
import java.util.Objects;

import javax.annotation.Resource;

import mx.com.telcel.core.model.PaymentsValidateModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.com.telcel.core.model.SuccessfulPurchaseEmailProcessModel;
import mx.com.telcel.facades.order.data.OrderDetailData;


public class SuccessfulPurchaseEmailContext extends AbstractEmailContext<SuccessfulPurchaseEmailProcessModel>
{

	@Resource(name = "commonI18NService")
	private CommonI18NService commonI18NService;
	@Resource(name = "mediaService")
	private MediaService mediaService;
	@Resource(name = "catalogVersionService")
	private CatalogVersionService catalogVersionService;
	@Resource(name = "orderDetailConverter")
	private Converter<OrderModel, OrderDetailData> orderDetailConverter;

	public static final String EMAIL = "email";
	private static final Logger LOG = LoggerFactory.getLogger(SuccessfulPurchaseEmailContext.class);

	@Override
	public void init(final SuccessfulPurchaseEmailProcessModel successfulPurchaseEmailProcessModel,
			final EmailPageModel emailPageModel)
	{
		super.init(successfulPurchaseEmailProcessModel, emailPageModel);
		if (Objects.nonNull(successfulPurchaseEmailProcessModel.getOrder()))
		{
			final OrderDetailData order = orderDetailConverter.convert(successfulPurchaseEmailProcessModel.getOrder());
			LOG.info("Send email successful purchase with order : " + order.getOrderNumber());
			put("order", order);
		}

		PaymentInfoModel paymentInfoModel = successfulPurchaseEmailProcessModel.getOrder().getPaymentInfo();
		String multicompraMsi = "false";

		if(paymentInfoModel instanceof CreditCardPaymentInfoModel){
			CreditCardPaymentInfoModel cardPaymentInfoModel = (CreditCardPaymentInfoModel)paymentInfoModel;
			if(!"0".equalsIgnoreCase(cardPaymentInfoModel.getMsi()) && cardPaymentInfoModel.getPaymentsValidate().size() > 1){
				multicompraMsi = "true";
				for(PaymentsValidateModel paymentsValidateModel : cardPaymentInfoModel.getPaymentsValidate()){
					if("0".equalsIgnoreCase(paymentsValidateModel.getMsi())){
						Double totalSinMSi = Double.valueOf(paymentsValidateModel.getAmount());
						put("totalSinMSi", totalSinMSi);

					}
					if(!"0".equalsIgnoreCase(paymentsValidateModel.getMsi())){
						Double totalConMSi = Double.valueOf(paymentsValidateModel.getAmount());
						put("totalConMSi", totalConMSi);

					}
				}
			}
		}
		put("multicompraMsi", multicompraMsi);

		put("paymentType", successfulPurchaseEmailProcessModel.getPaymentType());
		put("formatter", new DecimalFormat("###,##0.00"));
		put(EMAIL, successfulPurchaseEmailProcessModel.getEmail());
		put(FROM_EMAIL, emailPageModel.getFromEmail());
		put(DISPLAY_NAME, emailPageModel.getFromEmail());
	}

	@Override
	protected BaseSiteModel getSite(final SuccessfulPurchaseEmailProcessModel businessProcessModel)
	{
		return businessProcessModel.getSite();
	}

	@Override
	protected CustomerModel getCustomer(final SuccessfulPurchaseEmailProcessModel businessProcessModel)
	{
		return businessProcessModel.getCustomer();
	}

	@Override
	protected LanguageModel getEmailLanguage(final SuccessfulPurchaseEmailProcessModel businessProcessModel)
	{
		return businessProcessModel.getLanguage();
	}

}
