/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.process.strategies.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.processengine.model.BusinessProcessModel;


/**
 * Strategy to impersonate site and initialize session context from the QuoteProcessModel.
 */
public class QuoteProcessContextStrategy extends AbstractOrderProcessContextStrategy
{
	private QuoteService quoteService;

	@Override
	protected Optional<AbstractOrderModel> getOrderModel(final BusinessProcessModel businessProcessModel)
	{
		return Optional.of(businessProcessModel)
				.filter(businessProcess -> businessProcess instanceof QuoteProcessModel)
				.map(businessProcess -> (QuoteProcessModel) businessProcess)
				.map(QuoteProcessModel::getQuoteCode)
				.map(quoteCode -> getQuoteService().getCurrentQuoteForCode(quoteCode));
	}

	@Required
	public void setQuoteService(final QuoteService quoteService)
	{
		this.quoteService = quoteService;
	}

	protected QuoteService getQuoteService()
	{
		return quoteService;
	}
}
