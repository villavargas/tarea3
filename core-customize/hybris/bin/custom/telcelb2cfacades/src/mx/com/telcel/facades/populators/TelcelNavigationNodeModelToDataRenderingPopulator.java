package mx.com.telcel.facades.populators;

import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cmsfacades.data.MediaData;
import de.hybris.platform.cmsfacades.data.NavigationEntryData;
import de.hybris.platform.cmsfacades.data.NavigationNodeData;
import de.hybris.platform.cmsfacades.rendering.populators.NavigationNodeModelToDataRenderingPopulator;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.util.Config;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.assertj.core.util.Strings;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import mx.com.telcel.core.util.TelcelUtil;


public class TelcelNavigationNodeModelToDataRenderingPopulator extends NavigationNodeModelToDataRenderingPopulator
{

	private static final Logger LOG = Logger.getLogger(TelcelNavigationNodeModelToDataRenderingPopulator.class);
	private static final String CHIP_NAVIGATION_NODE = "chip.amigo.navigation.node";
	private static final String USER_DAT = "DAT";

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource
	private TelcelUtil telcelUtil;

	private Converter<MediaModel, MediaData> mediaToDataContentConverter;

	@Override
	public void populate(final CMSNavigationNodeModel source, final NavigationNodeData target)
	{
		String typeUserCVTDAT = sessionService.getAttribute("typeUserCVTDAT");
		if (Strings.isNullOrEmpty(typeUserCVTDAT))
		{
			final RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
			typeUserCVTDAT = Objects.nonNull(requestAttributes)
					? telcelUtil.getCookieByRequest(((ServletRequestAttributes) requestAttributes).getRequest(), "typeUserCVTDAT")
					: StringUtils.EMPTY;
			typeUserCVTDAT = typeUserCVTDAT == null ? "" : typeUserCVTDAT;
			//LOG.debug(String.format("Debug - cookie typeUserCVTDAT: %s", typeUserCVTDAT));
		}
		//Component Navigation Node Chip
		if (typeUserCVTDAT.equals(USER_DAT) && source.getUid().equals(Config.getParameter(CHIP_NAVIGATION_NODE)))
		{
			return;
		}
		target.setUid(source.getUid());
		target.setUuid(getUniqueIdentifierAttributeToDataContentConverter().convert(source));
		target.setName(source.getName());
		target.setLocalizedTitle(source.getTitle());
		final List<NavigationEntryData> navigationEntries = source.getEntries().stream() //
				.filter(entry -> getRenderingVisibilityService().isVisible(entry.getItem())) //
				.map(getNavigationEntryModelToDataConverter()::convert) //
				.collect(Collectors.toList());
		target.setEntries(navigationEntries);
		if(Objects.nonNull(source.getIcon())){
			target.setIcon(mediaToDataContentConverter.convert(source.getIcon()));
		}
	}

	public void setMediaToDataContentConverter(Converter<MediaModel, MediaData> mediaToDataContentConverter) {
		this.mediaToDataContentConverter = mediaToDataContentConverter;
	}
}
