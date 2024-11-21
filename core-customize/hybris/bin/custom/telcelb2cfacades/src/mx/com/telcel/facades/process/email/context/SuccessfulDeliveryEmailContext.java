package mx.com.telcel.facades.process.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.media.MediaService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Resource;

import mx.com.telcel.facades.order.data.OrderDetailProductData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.com.telcel.core.model.SuccessfulDeliveryEmailProcessModel;
import mx.com.telcel.facades.order.data.OrderDetailData;


public class SuccessfulDeliveryEmailContext extends AbstractEmailContext<SuccessfulDeliveryEmailProcessModel>
{

	private static final String ES_MX = "es_MX";

	@Resource(name = "commonI18NService")
	private CommonI18NService commonI18NService;
	@Resource(name = "mediaService")
	private MediaService mediaService;
	@Resource(name = "catalogVersionService")
	private CatalogVersionService catalogVersionService;
	@Resource(name = "orderDetailConsignmentConverter")
	private Converter<ConsignmentModel, OrderDetailData> orderDetailConsignmentConverter;

	@Resource(name = "productConverter")
	private Converter<ProductModel, ProductData> productConverter;

	public static final String EMAIL = "email";
	private static final Logger LOG = LoggerFactory.getLogger(SuccessfulDeliveryEmailContext.class);

	@Override
	public void init(final SuccessfulDeliveryEmailProcessModel successfulDeliveryEmailProcessModel,
			final EmailPageModel emailPageModel)
	{
		super.init(successfulDeliveryEmailProcessModel, emailPageModel);
		if (Objects.nonNull(successfulDeliveryEmailProcessModel.getConsignment()))
		{
			final OrderDetailData order = orderDetailConsignmentConverter
					.convert(successfulDeliveryEmailProcessModel.getConsignment());
			LOG.info("Send email successful delivery with order : " + order.getOrderNumber());

			order.getProducts().removeIf(data -> !data.getProductId()
					.equals(successfulDeliveryEmailProcessModel.getProduct().getSku()));
			put("order", order);
		}
		put("productType", successfulDeliveryEmailProcessModel.getProductType());
		put(EMAIL, successfulDeliveryEmailProcessModel.getEmail());
		put(FROM_EMAIL, emailPageModel.getFromEmail());
		put(DISPLAY_NAME, emailPageModel.getFromEmail());
	}

	@Override
	protected BaseSiteModel getSite(final SuccessfulDeliveryEmailProcessModel businessProcessModel)
	{
		return businessProcessModel.getSite();
	}

	@Override
	protected CustomerModel getCustomer(final SuccessfulDeliveryEmailProcessModel businessProcessModel)
	{
		return businessProcessModel.getCustomer();
	}

	@Override
	protected LanguageModel getEmailLanguage(final SuccessfulDeliveryEmailProcessModel businessProcessModel)
	{
		return businessProcessModel.getLanguage();
	}

}
