package mx.com.telcel.facades.pages.impl;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.cmsfacades.data.AbstractPageData;
import de.hybris.platform.cmsfacades.rendering.impl.DefaultPageRenderingService;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;


public class DefaultTelcelPageRenderingService extends DefaultPageRenderingService
{

	public static final String ANALYTICS_CATEGORY = "category";
	private static final Logger LOG = Logger.getLogger(DefaultTelcelPageRenderingService.class);

	@Override
	public AbstractPageData getPageRenderingData(final String pageTypeCode, final String pageLabelOrId, final String code)
			throws CMSItemNotFoundException
	{
		LOG.info("getPageRenderingData pageTypeCode: " + pageTypeCode);
		LOG.info("getPageRenderingData pageLabelOrId: " + pageLabelOrId);
		LOG.info("getPageRenderingData code: " + code);
		validateParameters(pageTypeCode, pageLabelOrId, code);

		final String pageQualifier = getPageQualifier(pageLabelOrId, code);
		final RestrictionData restrictionData = getRestrictionData(pageTypeCode, code);
		final AbstractPageModel pageModel = getPageModel(pageTypeCode, pageQualifier);
		final AbstractPageData pageData = getPageData(pageModel, restrictionData);
		if (pageData.getUid() != null && pageData.getUid().equals("productList"))
		{
			addOtherPropertiesCategory(code, pageData);
		}
		return pageData;
	}

	protected void addOtherPropertiesCategory(final String code, final AbstractPageData pageData)
	{
		final Map<String, Object> otherProperties = new HashMap<>();
		final String analyticsCategory = code.isEmpty() ? "EMPTY" : code;
		otherProperties.put(ANALYTICS_CATEGORY, analyticsCategory);

		if (!pageData.getOtherProperties().isEmpty())
		{
			pageData.getOtherProperties().putAll(otherProperties);
		}
		else
		{
			pageData.setOtherProperties(otherProperties);
		}
	}
}
