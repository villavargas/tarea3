/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsocc.helper;

public interface SearchQueryCodecCms<QUERY>
{
	QUERY decodeQuery(String query);

	String encodeQuery(QUERY query);
}
