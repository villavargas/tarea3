/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.facades;

import de.hybris.platform.core.servicelayer.data.SearchPageData;

import java.util.List;

import mx.com.telcel.data.UserData;
import mx.com.telcel.dto.SampleWsDTO;
import mx.com.telcel.dto.TestMapWsDTO;


public interface SampleFacades
{
	SampleWsDTO getSampleWsDTO(final String value);

	UserData getUser(String id);

	List<UserData> getUsers();

	SearchPageData<UserData> getUsers(SearchPageData<?> params);

	TestMapWsDTO getMap();
}
