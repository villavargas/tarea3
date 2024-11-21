package mx.com.telcel.facades.process.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.media.MediaService;

import java.util.Objects;

import javax.annotation.Resource;

import org.assertj.core.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.com.telcel.core.model.PendingPaymentEmailProcessModel;
import mx.com.telcel.facades.order.data.OrderDetailData;


public class PendingPaymentEmailContext extends AbstractEmailContext<PendingPaymentEmailProcessModel>
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
	private static final Logger LOG = LoggerFactory.getLogger(PendingPaymentEmailContext.class);

	@Override
	public void init(final PendingPaymentEmailProcessModel pendingPaymentEmailProcessModel, final EmailPageModel emailPageModel)
	{
		super.init(pendingPaymentEmailProcessModel, emailPageModel);
		if (Objects.nonNull(pendingPaymentEmailProcessModel.getOrder()))
		{
			final OrderDetailData order = orderDetailConverter.convert(pendingPaymentEmailProcessModel.getOrder());
			LOG.info("Send email pending payment with order : " + order.getOrderNumber());
			put("order", order);
		}
		if (!Strings.isNullOrEmpty(pendingPaymentEmailProcessModel.getUrlDownloadReceipt()))
		{
			put("urlDownloadReceipt", pendingPaymentEmailProcessModel.getUrlDownloadReceipt());
		}
		else if (!Strings.isNullOrEmpty(pendingPaymentEmailProcessModel.getUrlDownloadReceiptLong()))
		{
			put("urlDownloadReceipt", pendingPaymentEmailProcessModel.getUrlDownloadReceiptLong());
		}
		put(EMAIL, pendingPaymentEmailProcessModel.getEmail());
		put(FROM_EMAIL, emailPageModel.getFromEmail());
		put(DISPLAY_NAME, emailPageModel.getFromEmail());
	}

	@Override
	protected BaseSiteModel getSite(final PendingPaymentEmailProcessModel businessProcessModel)
	{
		return businessProcessModel.getSite();
	}

	@Override
	protected CustomerModel getCustomer(final PendingPaymentEmailProcessModel businessProcessModel)
	{
		return businessProcessModel.getCustomer();
	}

	@Override
	protected LanguageModel getEmailLanguage(final PendingPaymentEmailProcessModel businessProcessModel)
	{
		return businessProcessModel.getLanguage();
	}

}
