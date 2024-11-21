/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.web.payment.validation;

import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.site.BaseSiteService;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractMockUrlValidator
{
	protected static final String ALLOWED_PAYMENT_HOSTS = "acceleratorservices.payment.response.url.allowlist";
	protected static final String URL_STRICT_CHECK_ENABLED = "acceleratorservices.payment.url.strict.enabled";
	protected static final String SEPARATOR = ",";
	protected static final String SAMPLE_URL = "sampleUrl";

	private static final Logger LOG = LoggerFactory.getLogger(AbstractMockUrlValidator.class);

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	@Resource(name = "baseSiteService")
	private BaseSiteService baseSiteService;

	@Resource(name = "siteBaseUrlResolutionService")
	private SiteBaseUrlResolutionService siteBaseUrlResolutionService;

	/**
	 * Returns a list of allowed hosts from configuration based on the key. Falls back to a list of default allowed hosts
	 * in case configuration is empty.
	 *
	 * @param key
	 *           config key to look for.
	 * @return list of allowed hosts
	 */
	protected List<String> getAllowedHosts(final String key)
	{
		final List<String> allowedHosts = new ArrayList<>();

		final List<String> allowedHostsFromConfig = Arrays
				.asList(StringUtils.split(getConfigurationService().getConfiguration().getString(key, StringUtils.EMPTY), SEPARATOR));


		if (CollectionUtils.isEmpty(allowedHostsFromConfig))
		{
			// fallback to default allowed hosts if configuration was empty.
			if (LOG.isInfoEnabled())
			{
				LOG.info("Configuration not defined for key :{}, falling back to default values", key);
			}
			final List<String> defaultAllowedHosts = getDefaultAllowedSchemeHostAndPortUrls();
			defaultAllowedHosts.forEach(host -> {
				if (StringUtils.isNotBlank(host))
				{
					allowedHosts.add(host.trim()); //remove terminal whitespaces if any
				}
			});
		}
		else
		{
			// add hosts from config
			allowedHostsFromConfig.forEach(host -> {
				if (StringUtils.isNotBlank(host))
				{
					allowedHosts.add(host.trim()); //remove terminal whitespaces if any
				}
			});
		}

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Allowed hosts for key: {}", allowedHosts);
		}
		return allowedHosts;
	}

	/**
	 * Return a list of strings containing allowed protocol, host and port for all base sites. example:
	 * "http://electronics.local:9001", "https://electronics.local:9002"
	 *
	 * @return list of default allowed urls, containing scheme, host and protocols
	 */
	protected List<String> getDefaultAllowedSchemeHostAndPortUrls()
	{
		final Collection<BaseSiteModel> allBaseSites = getBaseSiteService().getAllBaseSites();
		final List<String> defaultAllowedHosts = new ArrayList<>();

		// compose a list of all allowed protocol, host and port combinations
		for (final BaseSiteModel site : allBaseSites)
		{
			final String fullResponseUrlHttp = getSiteBaseUrlResolutionService().getWebsiteUrlForSite(site, false, "");
			if (StringUtils.isNotBlank(fullResponseUrlHttp))
			{
				defaultAllowedHosts.add(extractSchemeHostAndPortFromUrl(fullResponseUrlHttp));
			}

			final String fullResponseUrlHttps = getSiteBaseUrlResolutionService().getWebsiteUrlForSite(site, true, "");
			if (StringUtils.isNotBlank(fullResponseUrlHttps))
			{
				defaultAllowedHosts.add(extractSchemeHostAndPortFromUrl(fullResponseUrlHttps));
			}

		}
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Default allowed hosts: {}", defaultAllowedHosts);
		}
		return defaultAllowedHosts;
	}

	/**
	 * Checks if strict check is enabled or not. based on acceleratorservices.payment.url.strict.enabled property in
	 * configuration.
	 *
	 * @return true if enabled, otherwise false, also true if property not defined.
	 */
	protected boolean isStrictCheckEnabled()
	{
		return getConfigurationService().getConfiguration().getBoolean(URL_STRICT_CHECK_ENABLED, true);
	}

	/**
	 * Perform null checks and scheme checks as defined in {@link UrlSchemeValidator}.
	 *
	 * @param url
	 *           url to check
	 * @return true if url is valid, false otherwise.
	 */
	protected boolean isValidUrlSyntax(final String url)
	{
		if (StringUtils.isBlank(url))
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Blank or null url, returning false");
			}
			return false;
		}

		if (!UrlSchemeValidator.validate(url))
		{
			if (LOG.isDebugEnabled())
			{
				LOG.error("URL not allowed(scheme not allowed), malicious attempt? Url checked: {}",
						URLEncoder.encode(url, StandardCharsets.UTF_8));
			}
			return false;
		}
		return true;
	}


	/**
	 * Extracts the protocol, host and port from passed url.
	 *
	 * @param url
	 *           url to be parsed.
	 * @return url containing only scheme, host and port.
	 */
	protected String extractSchemeHostAndPortFromUrl(final String url)
	{

		try
		{
			final URI uri = new URI(url);
			final String protocol = uri.getScheme();
			final String host = uri.getHost();
			final int port = uri.getPort();

			// if the port is not explicitly specified in the input, it will be -1.
			if (port == -1)
			{
				return String.format("%s://%s", protocol, host);
			}
			else
			{
				return String.format("%s://%s:%d", protocol, host, port);
			}

		}
		catch (final URISyntaxException e)
		{
			if (LOG.isErrorEnabled())
			{
				LOG.error("Unable to parse url", e);
			}
			return "";
		}

	}

	/**
	 * Extracts the path from the passed url.
	 *
	 * @param url
	 *           url to be parsed.
	 * @return path component of the url.
	 */
	protected String extractPathFromUrl(final String url)
	{

		try
		{
			final URI uri = new URI(url);
			return uri.getPath();

		}
		catch (final URISyntaxException e)
		{
			if (LOG.isErrorEnabled())
			{
				LOG.error("Unable to parse url", e);
			}
			return "";
		}

	}

	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	public BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	public SiteBaseUrlResolutionService getSiteBaseUrlResolutionService()
	{
		return siteBaseUrlResolutionService;
	}

	public void setSiteBaseUrlResolutionService(final SiteBaseUrlResolutionService siteBaseUrlResolutionService)
	{
		this.siteBaseUrlResolutionService = siteBaseUrlResolutionService;
	}

	/**
	 * Validate the passed payment response url, if it is allowed or not. For a Url to be allowed it must be only
	 * containing allowed scheme, host and port. Allowed hosts are picked up from config property
	 * acceleratorservices.payment.response.url.allowlist Also the url must contain the relative path (e.g.
	 * /sop/response).
	 * <p>
	 * If the property acceleratorservices.payment.url.strict.enabled is set to false this method only does scheme
	 * validation {@link UrlSchemeValidator}, and skips other validations.
	 *
	 * @param url
	 *           url to be validated.
	 * @return true if url is allowed, otherwise false.
	 */
	public boolean validatePaymentResponseUrl(final String url)
	{

		// do some basic validations like null and scheme check
		if (!isValidUrlSyntax(url))
		{
			return false;
		}

		// if strict check is disabled then simply return true
		if (!isStrictCheckEnabled())
		{
			return true;
		}

		// strict check is enabled, do further validation

		// check for at least making sure the mandatory relative path is present in the url
		if (!extractPathFromUrl(url).endsWith(getPaymentResponseEndRelativeUrl()))
		{
			if (LOG.isErrorEnabled() && !url.equals(SAMPLE_URL))
			{
				LOG.error("Expected relative part: {}", getPaymentResponseEndRelativeUrl());
				LOG.error("Payment response URL not allowed(relative part is not as expected), malicious attempt? Url checked: {}",
						URLEncoder.encode(url, StandardCharsets.UTF_8));
			}
			return false;
		}

		// check for scheme, host and port
		final List<String> allowedPaymentHosts = getAllowedHosts(ALLOWED_PAYMENT_HOSTS);
		if (!allowedPaymentHosts.contains(extractSchemeHostAndPortFromUrl(url)))
		{
			if (LOG.isErrorEnabled())
			{
				LOG.error("Allowed payment scheme,host and port urls: {}", allowedPaymentHosts);
				LOG.error(
						"Payment response URL not allowed(scheme,host and port not as expected), malicious attempt? Url checked: {}",
						URLEncoder.encode(url, StandardCharsets.UTF_8));
			}
			return false;
		}

		return true;
	}

	protected abstract String getPaymentResponseEndRelativeUrl();
}
