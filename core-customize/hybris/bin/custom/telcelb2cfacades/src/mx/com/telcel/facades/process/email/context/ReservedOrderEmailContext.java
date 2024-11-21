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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.com.telcel.core.model.ReservedOrderEmailProcessModel;
import mx.com.telcel.facades.order.data.OrderDetailData;


public class ReservedOrderEmailContext extends AbstractEmailContext<ReservedOrderEmailProcessModel>
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
	private static final Logger LOG = LoggerFactory.getLogger(ReservedOrderEmailContext.class);

	@Override
	public void init(final ReservedOrderEmailProcessModel reservedOrderEmailProcessModel, final EmailPageModel emailPageModel)
	{
		super.init(reservedOrderEmailProcessModel, emailPageModel);
		if (Objects.nonNull(reservedOrderEmailProcessModel.getOrder()))
		{
			final OrderDetailData order = orderDetailConverter.convert(reservedOrderEmailProcessModel.getOrder());
			LOG.info("Send email reserved order with order : " + order.getOrderNumber());
			put("order", order);
		}
		put(EMAIL, reservedOrderEmailProcessModel.getEmail());
		put(FROM_EMAIL, emailPageModel.getFromEmail());
		put(DISPLAY_NAME, emailPageModel.getFromEmail());
	}

	@Override
	protected BaseSiteModel getSite(final ReservedOrderEmailProcessModel businessProcessModel)
	{
		return businessProcessModel.getSite();
	}

	@Override
	protected CustomerModel getCustomer(final ReservedOrderEmailProcessModel businessProcessModel)
	{
		return businessProcessModel.getCustomer();
	}

	@Override
	protected LanguageModel getEmailLanguage(final ReservedOrderEmailProcessModel businessProcessModel)
	{
		return businessProcessModel.getLanguage();
	}

}
