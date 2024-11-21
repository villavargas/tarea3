package mx.com.telcel.facades.process.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.media.MediaService;

import javax.annotation.Resource;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import mx.com.telcel.core.model.TicketNotificationAgentEmailProcessModel;


public class TicketNotificationAgentEmailContext extends AbstractEmailContext<TicketNotificationAgentEmailProcessModel>
{

	@Resource
	private MediaService mediaService;

	@Resource(name = "defaultConfigurationService")
	private ConfigurationService configurationService;

	private static final Logger LOG = Logger.getLogger(TicketNotificationAgentEmailContext.class);

	private static final String SECURE_WEBSITE = "website.telcel.https";

	@Override
	public void init(final TicketNotificationAgentEmailProcessModel businessProcessModel, final EmailPageModel emailPageModel)
	{
		super.init(businessProcessModel, emailPageModel);

		CatalogVersionModel cat = new CatalogVersionModel();
		cat = getCatalogVersionService().getCatalogVersion("telcelContentCatalog", "Online");
		final MediaModel bannerModel = getMediaService().getMedia(cat, "/images/theme/img-banner_01.png");
		final MediaModel logoModel = getMediaService().getMedia(cat, "/images/theme/logo-telcel.png");
		final String mediaSecureBaseWebsiteUrl = configurationService.getConfiguration().getString(SECURE_WEBSITE);
		LOG.info("WEBSITE URL : " + mediaSecureBaseWebsiteUrl);
		LOG.info("LOGO URL : " + logoModel.getURL2());
		put("StringUtils", StringUtils.class);
		put("ObjectUtils", ObjectUtils.class);
		put("bannerModel", bannerModel.getURL2());
		put("logoModel", logoModel.getURL2());
		put("orderNumber", businessProcessModel.getOrderNumber());
		put("nombreCliente", businessProcessModel.getNombreCliente());
		put("apellidoPaterno", businessProcessModel.getApellidoPaterno());
		put("apellidoMaterno", businessProcessModel.getApellidoMaterno());
		put("estado", businessProcessModel.getEstado());
		put("telefonoContacto", businessProcessModel.getTelefonoContacto());
		put("comentarios", businessProcessModel.getComentarios());
		put("consulta", businessProcessModel.getConsulta());
		put("customerEmail", businessProcessModel.getCustomerEmail());
		put("mediaSecureBaseWebsiteUrl", mediaSecureBaseWebsiteUrl);
		put(EMAIL, businessProcessModel.getToEmail());
		put(FROM_EMAIL, businessProcessModel.getFromEmail());
		put(DISPLAY_NAME, businessProcessModel.getNombreCliente());
	}

	@Override
	protected BaseSiteModel getSite(final TicketNotificationAgentEmailProcessModel businessProcessModel)
	{
		return businessProcessModel.getSite();
	}

	@Override
	protected CustomerModel getCustomer(final TicketNotificationAgentEmailProcessModel businessProcessModel)
	{
		return businessProcessModel.getCustomer();
	}

	@Override
	protected LanguageModel getEmailLanguage(final TicketNotificationAgentEmailProcessModel businessProcessModel)
	{
		return businessProcessModel.getLanguage();
	}


	public MediaService getMediaService()
	{
		return mediaService;
	}

	public void setMediaService(final MediaService mediaService)
	{
		this.mediaService = mediaService;
	}

	@Resource
	private CatalogVersionService catalogVersionService;

	public CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}

	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}

}
