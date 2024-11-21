/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.web.payment.controllers;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;


@UnitTest
public class HostedOrderPageMockControllerTest
{
	private HostedOrderPageMockController hostedOrderPageMockController;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		hostedOrderPageMockController = new HostedOrderPageMockController();

	}


	@Test
	public void shoulDeserializeParameterMap()
	{
		// test odd number of characters
		String paramString = "A" + HostedOrderPageMockController.SEPARATOR_STR + "B" + HostedOrderPageMockController.SEPARATOR_STR
				+ "C";

		Map<String, String> paramMap = hostedOrderPageMockController.deserializeParameterMap(paramString);
		Assert.assertEquals(2, paramMap.size());
		Assert.assertEquals("B", paramMap.get("A"));
		Assert.assertEquals("", paramMap.get("C"));

		// test even number of characters
		paramString = paramString + HostedOrderPageMockController.SEPARATOR_STR + "D";
		paramMap = hostedOrderPageMockController.deserializeParameterMap(paramString);
		Assert.assertEquals("D", paramMap.get("C"));

		// test empty paramString
		paramString = "";
		paramMap = hostedOrderPageMockController.deserializeParameterMap(paramString);
		Assert.assertEquals(0, paramMap.size());
	}

}
