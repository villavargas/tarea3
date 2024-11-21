/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercewebservices.core.v2.filter;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercewebservices.core.exceptions.InvalidResourceException;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.webservicescommons.util.YSanitizer;

import java.io.IOException;
import java.util.Objects;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.util.WebUtils;


/**
 * Filter that resolves base site id from the requested url and activates it.
 */
public class BaseSiteMatchingFilter extends AbstractUrlMatchingFilter
{
	private static final Logger LOG = Logger.getLogger(BaseSiteMatchingFilter.class);

	private String regexp;
	private BaseSiteService baseSiteService;
	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain filterChain) throws ServletException, IOException
	{
		final String baseSiteID = getBaseSiteValue(request, regexp);

		if (baseSiteID != null)
		{
			final BaseSiteModel requestedBaseSite = getBaseSiteService().getBaseSiteForUID(baseSiteID);
			if (requestedBaseSite != null)
			{
				final BaseSiteModel currentBaseSite = getBaseSiteService().getCurrentBaseSite();

				if (!requestedBaseSite.equals(currentBaseSite))
				{
					getBaseSiteService().setCurrentBaseSite(requestedBaseSite, true);
				}
			}
			else
			{
				final InvalidResourceException ex = new InvalidResourceException(YSanitizer.sanitize(baseSiteID));
				LOG.debug(ex.getMessage());
				throw ex;
			}
		}

		final String typeUserCVTDAT = getCookieValue(request, "typeUserCVTDAT");
		if (Objects.nonNull(typeUserCVTDAT))
		{
			sessionService.setAttribute("typeUserCVTDAT", typeUserCVTDAT);
		}

		filterChain.doFilter(request, response);
	}

	private String getCookieValue(final HttpServletRequest request, final String cookieName)
	{
		final Cookie[] cookies = request.getCookies();
		if (Objects.isNull(cookies))
		{
			return null;
		}
		final Cookie cookie = WebUtils.getCookie(request, cookieName);
		if (cookie != null)
		{
			return cookie.getValue();
		}
		else
		{
			return null;
		}
	}

	protected String getRegexp()
	{
		return regexp;
	}

	@Required
	public void setRegexp(final String regexp)
	{
		this.regexp = regexp;
	}

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}
}
