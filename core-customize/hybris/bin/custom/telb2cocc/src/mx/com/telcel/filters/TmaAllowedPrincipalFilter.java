/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.filters;

import mx.com.telcel.constants.Telb2coccConstants;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.session.SessionService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;


/**
 * Filter responsible for updating the allowed principal flag for the current user.
 *
 * @since 2105
 */
public class TmaAllowedPrincipalFilter extends OncePerRequestFilter
{
	private static final String REGEX = ",";
	private static final String APPLY_ALLOWED_PRINCIPAL_FLAG = "applyAllowedPrincipalFlag";
	private final String regexp;
	private final String regexpProductApi;
	private final ConfigurationService configurationService;
	private final SessionService sessionService;

	public TmaAllowedPrincipalFilter(final ConfigurationService configurationService, final String regexp,
			final String regexpProductApi, final SessionService sessionService)
	{
		super();
		this.regexp = regexp;
		this.regexpProductApi = regexpProductApi;
		this.configurationService = configurationService;
		this.sessionService = sessionService;
	}

	@Override
	protected void doFilterInternal(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse,
			final FilterChain filterChain) throws ServletException, IOException
	{
		if (matchesUrl(httpServletRequest, regexp) || matchesUrl(httpServletRequest, regexpProductApi))
		{
			getSessionService().setAttribute(APPLY_ALLOWED_PRINCIPAL_FLAG, !hasAdminRole());
		}

		filterChain.doFilter(httpServletRequest, httpServletResponse);
	}

	/**
	 * Checks if the URL matches the provided regular expression.
	 *
	 * @param request
	 * 		The HTTP request
	 * @param regexp
	 * 		The regular expression
	 * @return True if the regular expression matches the url, otherwise false
	 */
	protected boolean matchesUrl(final HttpServletRequest request, final String regexp)
	{
		final Matcher matcher = getMatcher(request, regexp);
		return (matcher.find());
	}

	/**
	 * Returns the string that matches the regular expression from the URL.
	 *
	 * @param request
	 * 		The HTTP request
	 * @param regexp
	 * 		The regular expression
	 * @return The matched string
	 */
	protected Matcher getMatcher(final HttpServletRequest request, final String regexp)
	{
		final Pattern pattern = Pattern.compile(regexp);
		final String path = request.getPathInfo() != null ? request.getPathInfo() : "";
		return pattern.matcher(path);
	}

	/**
	 * Checks if the authorized user has admin roles.
	 *
	 * @return True if the authorized user has admin role, otherwise false
	 */
	protected boolean hasAdminRole()
	{
		final List<String> eligibilityAdminRoles = Arrays
				.asList(getConfigurationService().getConfiguration().getString(Telb2coccConstants.ADMIN_ROLES).split(REGEX));
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		for (final GrantedAuthority ga : authentication.getAuthorities())
		{
			if (eligibilityAdminRoles.contains(ga.getAuthority()))
			{
				return true;
			}
		}
		return false;
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}
}
