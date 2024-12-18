/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.search.comparators;

import de.hybris.platform.commerceservices.util.AbstractComparator;
import de.hybris.platform.solrfacetsearch.search.FacetValue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Required;


/**
 * Compares to {@link FacetValue} as numbers If {@link #pattern} is provided then regular expression is used against
 * facet strings to find number to compare.
 */
public class NumericFacetComparator extends AbstractComparator<FacetValue>
{
	private String pattern;
	private Pattern regexPattern;

	@Override
	protected int compareInstances(final FacetValue facet1, final FacetValue facet2)
	{
		return compareValues(getNumber(facet1.getDisplayName()), getNumber(facet2.getDisplayName()));
	}

	protected double getNumber(final String text)
	{
		if (pattern == null)
		{
			parseDouble(text);
		}
		else
		{
			//try to find some number in string
			final Matcher matcher = getRegexPattern().matcher(text);
			if (matcher.find())
			{
				return parseDouble(matcher.group());
			}
		}
		return 0d;
	}

	protected double parseDouble(final String text)
	{
		try
		{
			return Double.parseDouble(text.trim());
		}
		catch (final NumberFormatException e)
		{
			return 0d;
		}
	}

	protected String getPattern()
	{
		return pattern;
	}

	@Required
	public void setPattern(final String numericPattern)
	{
		this.pattern = numericPattern;
		this.regexPattern = Pattern.compile(numericPattern);
	}

	protected Pattern getRegexPattern()
	{
		return regexPattern;
	}
}
