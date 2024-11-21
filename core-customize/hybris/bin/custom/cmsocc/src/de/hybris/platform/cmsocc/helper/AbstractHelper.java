/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsocc.helper;

import de.hybris.platform.webservicescommons.mapping.DataMapper;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;


@Component
public abstract class AbstractHelper
{
	@Resource(name = "dataMapper")
	private DataMapper dataMapper;

	protected de.hybris.platform.commerceservices.search.pagedata.PageableData createPageableData(final int currentPage,
			final int pageSize, final String sort)
	{
		final de.hybris.platform.commerceservices.search.pagedata.PageableData pageable = new de.hybris.platform.commerceservices.search.pagedata.PageableData();
		pageable.setCurrentPage(currentPage);
		pageable.setPageSize(pageSize);
		pageable.setSort(sort);
		return pageable;
	}

	protected DataMapper getDataMapper()
	{
		return dataMapper;
	}

	protected void setDataMapper(final DataMapper dataMapper)
	{
		this.dataMapper = dataMapper;
	}
}
