/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.process.email.context.impl;

import de.hybris.platform.acceleratorservices.document.factory.impl.AbstractHybrisVelocityContextFactory;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.acceleratorservices.process.email.context.EmailContextFactory;
import de.hybris.platform.acceleratorservices.process.strategies.EmailTemplateTranslationStrategy;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.commons.model.renderer.RendererTemplateModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;

import java.io.StringWriter;
import java.util.*;

import de.hybris.platform.util.Utilities;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;


/**
 * Default factory used to create the velocity context for rendering emails
 */
public class DefaultEmailContextFactory extends AbstractHybrisVelocityContextFactory implements EmailContextFactory<BusinessProcessModel>
{
	private static final Logger LOG = Logger.getLogger(DefaultEmailContextFactory.class);

	private Map<String, String> emailContextVariables;
	private EmailTemplateTranslationStrategy emailTemplateTranslationStrategy;

	@Override
	public AbstractEmailContext<BusinessProcessModel> create(final BusinessProcessModel businessProcessModel,
			final EmailPageModel emailPageModel, final RendererTemplateModel renderTemplate)
	{
		final AbstractEmailContext<BusinessProcessModel> emailContext = resolveEmailContext(renderTemplate);
		emailContext.init(businessProcessModel, emailPageModel);


		final String languageIso = emailContext.getEmailLanguage() == null ? null : emailContext.getEmailLanguage().getIsocode();
		//Render translated messages from the email message resource bundles into the email context.
		emailContext.setMessages(getEmailTemplateTranslationStrategy().translateMessagesForTemplate(renderTemplate, languageIso));

		renderCMSSlotsIntoEmailContext(emailContext, emailPageModel, businessProcessModel);

		// parse and populate the variable at the end
		parseVariablesIntoEmailContext(emailContext);
		
		// Satisfy platform mechanism - requires context class defined for template, see VelocityTemplateRenderer class
		if (renderTemplate.getContextClass() == null)
		{
			renderTemplate.setContextClass(emailContext.getClass().getName());
		}

		return emailContext;
	}


	protected <T extends AbstractEmailContext<BusinessProcessModel>> T resolveEmailContext(
			final RendererTemplateModel renderTemplate)
	{
		try
		{
			final Class<T> contextClass = (Class<T>) Class.forName(renderTemplate.getContextClass());
			final Map<String, T> emailContexts = getApplicationContext().getBeansOfType(contextClass);
			if (MapUtils.isNotEmpty(emailContexts))
			{
				return emailContexts.entrySet().iterator().next().getValue();
			}
			else
			{
				throw new IllegalStateException("Cannot find bean in application context for context class [" + contextClass + "]");
			}
		}
		catch (final ClassNotFoundException e)
		{
			LOG.error("failed to create email context", e);
			throw new IllegalStateException("Cannot find email context class", e);
		}
	}

	protected ApplicationContext getApplicationContext()
	{
		return super.getApplicationContext();
	}

	protected void renderCMSSlotsIntoEmailContext(final AbstractEmailContext<BusinessProcessModel> _emailContext,
			final EmailPageModel emailPageModel, final BusinessProcessModel businessProcessModel)
	{
		AbstractEmailContext<BusinessProcessModel> emailContext = (AbstractEmailContext<BusinessProcessModel>) _emailContext;

		final Locale locale = getLocaleFromLanguageIsoCode(emailContext.getEmailLanguage().getIsocode());

		super.renderCMSSlotsIntoContext(locale, emailContext,emailPageModel,businessProcessModel);
	}

	protected String renderComponents(final Locale locale, final ContentSlotModel contentSlotModel,
									  final AbstractEmailContext<BusinessProcessModel> emailContext, final BusinessProcessModel businessProcessModel)
	{
		return super.renderComponents(locale, contentSlotModel,emailContext,businessProcessModel);
	}

	protected void renderTemplate(final Locale locale, final AbstractEmailContext<BusinessProcessModel> emailContext, final StringWriter text,
								  final AbstractCMSComponentModel component, final String renderTemplateCode, final RendererTemplateModel renderTemplate,
								  final BaseSiteModel site)
	{
		super.renderTemplate(locale, emailContext,text,component,renderTemplateCode,renderTemplate,site);
	}

	protected void processProperties(final Locale locale, final AbstractCMSComponentModel component, final Map<String, Object> componentContext)
	{
		super.processProperties(locale, component,componentContext);
	}

	protected void processProperties(final AbstractCMSComponentModel component, final Map<String, Object> componentContext)
	{
		super.processProperties(null, component,componentContext);
	}


	protected Locale getLocaleFromLanguageIsoCode(String languageIso) {
		final String[] loc = Utilities.parseLocaleCodes(languageIso);
		return new Locale(loc[0], loc[1], loc[2]);
	}

	protected String resolveRendererTemplateForComponent(final AbstractCMSComponentModel component,
			final BusinessProcessModel businessProcessModel)
	{
		return super.resolveRendererTemplateForComponent(component,businessProcessModel);
	}

	protected void parseVariablesIntoEmailContext(final AbstractEmailContext<BusinessProcessModel> emailContext)
	{
		final Map<String, String> variables = getEmailContextVariables();
		if (variables != null)
		{
			for (final Map.Entry<String, String> entry : variables.entrySet())
			{
				final StringBuilder buffer = new StringBuilder();

				appendTokensToBuffer(emailContext, entry, buffer);

				emailContext.put(entry.getKey(), buffer.toString());
			}
		}
	}

	protected Map<String, String> getEmailContextVariables()
	{
		return emailContextVariables;
	}

	public void setEmailContextVariables(final Map<String, String> emailContextVariables)
	{
		this.emailContextVariables = emailContextVariables;
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

}
