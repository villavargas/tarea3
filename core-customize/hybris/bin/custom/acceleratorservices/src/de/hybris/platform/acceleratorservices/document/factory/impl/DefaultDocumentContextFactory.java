/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.document.factory.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.acceleratorservices.document.context.AbstractDocumentContext;
import de.hybris.platform.acceleratorservices.document.context.AbstractHybrisVelocityContext;
import de.hybris.platform.acceleratorservices.document.factory.DocumentContextFactory;
import de.hybris.platform.acceleratorservices.model.cms2.pages.DocumentPageModel;
import de.hybris.platform.acceleratorservices.process.strategies.EmailTemplateTranslationStrategy;
import de.hybris.platform.commons.model.renderer.RendererTemplateModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default factory used to create the velocity context for rendering document
 */
public class DefaultDocumentContextFactory extends AbstractHybrisVelocityContextFactory
		implements DocumentContextFactory<BusinessProcessModel>
{
	private EmailTemplateTranslationStrategy emailTemplateTranslationStrategy;
	private Map<String, String> documentContextVariables;

	private static final Logger LOG = LoggerFactory.getLogger(DefaultDocumentContextFactory.class);

	@Override
	public AbstractDocumentContext<BusinessProcessModel> create(final BusinessProcessModel businessProcessModel,
			final DocumentPageModel documentPageModel, final RendererTemplateModel renderTemplate)
	{
		validateParameterNotNull(businessProcessModel, "BusinessProcessModel cannot be null");
		validateParameterNotNull(documentPageModel, "DocumentPageModel cannot be null");
		validateParameterNotNull(renderTemplate, "Render template cannot be null");

		final AbstractDocumentContext<BusinessProcessModel> documentContext = resolveDocumentContext(renderTemplate);
		documentContext.init(businessProcessModel, documentPageModel);

		renderCMSSlotsIntoContext(documentContext, documentPageModel, businessProcessModel);

		// parse and populate the variable at the end
		parseVariablesIntoDocumentContext(documentContext);

		//Render translated messages from the document message resource bundles into the document context.
		final String languageIso = documentContext.getDocumentLanguage() == null ? null
				: documentContext.getDocumentLanguage().getIsocode();

		documentContext
				.setMessages(getEmailTemplateTranslationStrategy().translateMessagesForTemplate(renderTemplate, languageIso));

		return documentContext;
	}

	protected void parseVariablesIntoDocumentContext(final AbstractHybrisVelocityContext<BusinessProcessModel> context)
	{
		final Map<String, String> variables = getDocumentContextVariables();
		if (variables != null)
		{
			for (final Map.Entry<String, String> entry : variables.entrySet())
			{
				final StringBuilder buffer = new StringBuilder();

				appendTokensToBuffer(context, entry, buffer);

				context.put(entry.getKey(), buffer.toString());
			}
		}
	}

	/**
	 * Provides DocumentContext based on the context class for the given {@link RendererTemplateModel}
	 *
	 * @param renderTemplate
	 *           The template for rendering the document
	 * @param <T>
	 * @return the document context
	 */
	protected <T extends AbstractHybrisVelocityContext<BusinessProcessModel>> T resolveDocumentContext(
			final RendererTemplateModel renderTemplate)
	{
		validateParameterNotNull(renderTemplate, "Render template cannot be null");
		Class<T> contextClass = null;
		try
		{
			contextClass = (Class<T>) Class.forName(renderTemplate.getContextClass());
			return getApplicationContext().getBean(contextClass);
		}
		catch (final ClassNotFoundException e)
		{
			LOG.error("failed to create document context", e);
			throw new IllegalStateException("Cannot find document context class", e);
		}
		catch (final BeansException e)
		{
			throw new IllegalStateException("Cannot find bean in application context for context class [" + contextClass + "]");
		}
	}

	protected EmailTemplateTranslationStrategy getEmailTemplateTranslationStrategy()
	{
		return emailTemplateTranslationStrategy;
	}

	@Required
	public void setEmailTemplateTranslationStrategy(final EmailTemplateTranslationStrategy emailTemplateTranslationStrategy)
	{
		this.emailTemplateTranslationStrategy = emailTemplateTranslationStrategy;
	}

	public void setDocumentContextVariables(final Map<String, String> documentContextVariables)
	{
		this.documentContextVariables = documentContextVariables;
	}

	public Map<String, String> getDocumentContextVariables()
	{
		return this.documentContextVariables;
	}
}
