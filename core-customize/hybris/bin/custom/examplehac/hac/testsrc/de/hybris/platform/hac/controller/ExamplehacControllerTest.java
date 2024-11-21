/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.hac.controller;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.IntegrationTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * Test for {@link ExamplehacController}.
 */
@IntegrationTest
public class ExamplehacControllerTest
{

	/**
	 * Code under test.
	 */
	protected ExamplehacController cut;

	/**
	 * Set up the code under test.
	 */
	@Before
	public void setup()
	{
		cut = new ExamplehacController();
	}

	/**
	 * Clean up the code under test.
	 */
	@After
	public void teardown()
	{
		cut = null;
	}

	@Test
	public void testSayHello()
	{
		final String helloText = cut.sayHello();

		assertNotNull(helloText);
		assertNotEquals(0, helloText.length());
	}
}
