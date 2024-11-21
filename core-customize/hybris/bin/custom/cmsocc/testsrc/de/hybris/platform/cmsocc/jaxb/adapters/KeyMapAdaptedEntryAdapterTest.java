/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsocc.jaxb.adapters;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsocc.jaxb.adapters.KeyMapAdaptedEntryAdapter.KeyMapAdaptedEntry;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;


/**
 * JUnit Tests for the KeyMapAdaptedEntryAdapter
 */
@UnitTest
public class KeyMapAdaptedEntryAdapterTest
{
	private static final String TEST_KEY = "TestKey";
	private static final String TEST_STRING = "TestString";
	private static final String TEST_KEY_MAP = "TestKeyMapAdaptedEntry";


	private final KeyMapAdaptedEntryAdapter keyMapAdaptedEntryAdapter = new KeyMapAdaptedEntryAdapter();
	private KeyMapAdaptedEntry keyMapAdaptedEntry;

	@Before
	public void setup()
	{
		keyMapAdaptedEntry = new KeyMapAdaptedEntry();
	}

	@Test
	public void shouldMarshalStringValue()
	{
		keyMapAdaptedEntry.setKey(TEST_KEY);
		keyMapAdaptedEntry.setStrValue(TEST_STRING);

		final Element result = keyMapAdaptedEntryAdapter.marshal(keyMapAdaptedEntry);

		assertThat(result.getTextContent(), equalTo(TEST_STRING));
	}

	@Test
	public void shouldMarshalMapValue()
	{
		final List<KeyMapAdaptedEntry> testMap = new ArrayList<KeyMapAdaptedEntry>();
		final KeyMapAdaptedEntry testkeyMapAdaptedEntry = new KeyMapAdaptedEntry();
		testkeyMapAdaptedEntry.setKey(TEST_KEY_MAP);
		testkeyMapAdaptedEntry.setStrValue(TEST_STRING);

		testMap.add(testkeyMapAdaptedEntry);

		keyMapAdaptedEntry.setKey(TEST_KEY);
		keyMapAdaptedEntry.setMapValue(testMap);

		final Element result = keyMapAdaptedEntryAdapter.marshal(keyMapAdaptedEntry);
		assertThat(result.getTextContent(), equalTo(TEST_STRING));
	}

	@Test
	public void shouldMarshalMapValue_MapIsEmpty()
	{
		final List<KeyMapAdaptedEntry> testMap = new ArrayList<KeyMapAdaptedEntry>();

		keyMapAdaptedEntry.setKey(TEST_KEY);
		keyMapAdaptedEntry.setMapValue(testMap);

		final Element result = keyMapAdaptedEntryAdapter.marshal(keyMapAdaptedEntry);
		assertThat(result.getTextContent(), equalTo(""));
	}

	@Test
	public void shouldMarshalCollectionValue()
	{
		final List<String> testCollection = new ArrayList<String>();
		final String testString = TEST_STRING;

		testCollection.add(testString);

		keyMapAdaptedEntry.setKey(TEST_KEY);
		keyMapAdaptedEntry.setArrayValue(testCollection);

		final Element result = keyMapAdaptedEntryAdapter.marshal(keyMapAdaptedEntry);

		assertThat(result.getTextContent(), equalTo(TEST_STRING));
	}

	@Test
	public void shouldMarshalCollectionValue_CollectionIsEmpty()
	{
		final List<String> testCollection = new ArrayList<String>();

		keyMapAdaptedEntry.setKey(TEST_KEY);
		keyMapAdaptedEntry.setArrayValue(testCollection);

		final Element result = keyMapAdaptedEntryAdapter.marshal(keyMapAdaptedEntry);

		assertThat(result, equalTo(null));
	}

	@Test
	public void shouldNotMarshalNullValue()
	{
		keyMapAdaptedEntry.setKey(TEST_KEY);

		final Object result = keyMapAdaptedEntryAdapter.marshal(keyMapAdaptedEntry);

		assertThat(result, equalTo(null));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void shouldThrowUnsupportedOperationException() throws Exception
	{
		keyMapAdaptedEntryAdapter.unmarshal(null);
	}
}