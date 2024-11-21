/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsocc.redirect.suppliers.impl;

import de.hybris.platform.cms2.model.pages.ProductPageModel;
import de.hybris.platform.cms2.model.preview.PreviewDataModel;
import de.hybris.platform.cmsocc.constants.CmsoccConstants;
import de.hybris.platform.cmsocc.data.RequestParamData;
import de.hybris.platform.cmsocc.redirect.suppliers.PageRedirectSupplier;

import java.util.Objects;

import org.springframework.util.MultiValueMap;


/**
 * Implementation of {@link PageRedirectSupplier} to handle {@link de.hybris.platform.cms2.model.pages.ProductPageModel}
 */
public class ProductPageRedirectSupplier extends AbstractPageRedirectSupplier
{
	@Override
	protected void populateParams(final PreviewDataModel previewData, final RequestParamData paramData)
	{
		final MultiValueMap<String, String> queryParams = (MultiValueMap<String, String>) paramData.getQueryParameters();
		if (Objects.nonNull(queryParams))
		{
			final String productCode = getPreviewCode(previewData);

			queryParams.remove(CmsoccConstants.PAGE_LABEL_ID);
			queryParams.set(CmsoccConstants.PAGE_TYPE, ProductPageModel._TYPECODE);

			if (Objects.isNull(productCode) && Objects.nonNull(previewData.getPage()))
			{
				paramData.getPathParameters().put(CmsoccConstants.PAGE_ID, previewData.getPage().getUid());
				queryParams.remove(CmsoccConstants.CODE);
			}
			else if (Objects.nonNull(productCode))
			{
				queryParams.set(CmsoccConstants.CODE, productCode);
			}
		}
	}

	@Override
	protected String getPreviewCode(final PreviewDataModel previewData)
	{
		if (Objects.nonNull(previewData) && Objects.nonNull(previewData.getPreviewProduct()))
		{
			return previewData.getPreviewProduct().getCode();
		}
		return null;
	}

}
