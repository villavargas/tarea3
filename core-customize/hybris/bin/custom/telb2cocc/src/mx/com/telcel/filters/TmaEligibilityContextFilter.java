/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.filters;

import de.hybris.platform.b2ctelcofacades.user.TmaCustomerFacade;
import mx.com.telcel.constants.Telb2coccConstants;
import de.hybris.platform.b2ctelcoservices.compatibility.eligibility.TmaEligibilityContextService;
import de.hybris.platform.b2ctelcoservices.eligibility.data.TmaEligibilityContext;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;


/**
 * Filter responsible for updating the {@link TmaEligibilityContext} for the current user.
 *
 * @since 1907
 */
public class TmaEligibilityContextFilter extends OncePerRequestFilter
{
	private static final String REGEX = ",";
	private final TmaEligibilityContextService eligibilityContextService;
	private final ConfigurationService configurationService;
	private final String regexp;
	private final String regexpProductApi;
	private final TmaCustomerFacade tmaCustomerFacade;


	/**
	 * @param eligibilityContextService
	 * @param regexp
	 * @param regexpProductApi
	 */
	public TmaEligibilityContextFilter(final TmaEligibilityContextService eligibilityContextService, final String regexp,
			final String regexpProductApi, final ConfigurationService configurationService,
			final TmaCustomerFacade tmaCustomerFacade)
	{
		super();
		this.eligibilityContextService = eligibilityContextService;
		this.regexp = regexp;
		this.regexpProductApi = regexpProductApi;
		this.configurationService = configurationService;
		this.tmaCustomerFacade = tmaCustomerFacade;
	}

	@Override
	protected void doFilterInternal(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse,
			final FilterChain filterChain) throws ServletException, IOException
	{
		if (matchesUrl(httpServletRequest, regexp) || matchesUrl(httpServletRequest, regexpProductApi))
		{
			final String filterUserId = httpServletRequest.getParameter("userId");

			if (StringUtils.isNotEmpty(filterUserId))
			{
				tmaCustomerFacade.updateEligibilityContextsByCustomer(filterUserId);
				getEligibilityContextService().updateApplyEligibilityFlag(true);
			}
			else
			{
				getEligibilityContextService().updateEligibilityContexts(true);
				getEligibilityContextService().updateApplyEligibilityFlag(!hasAdminRole());
			}
		}

		filterChain.doFilter(httpServletRequest, httpServletResponse);
	}

	protected boolean matchesUrl(final HttpServletRequest request, final String regexp)
	{
		final Matcher matcher = getMatcher(request, regexp);
		return (matcher.find());
	}

	protected Matcher getMatcher(final HttpServletRequest request, final String regexp)
	{
		final Pattern pattern = Pattern.compile(regexp);
		final String path = request.getPathInfo() != null ? request.getPathInfo() : "";
		return pattern.matcher(path);
	}

	protected boolean hasAdminRole()
	{
		final List<String> eligibilityAdminRoles = Arrays
				.asList(getConfigurationService().getConfiguration().getString(Telb2coccConstants.ELIGIBILTY_ROLES).split(REGEX));
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

	protected TmaEligibilityContextService getEligibilityContextService()
	{
		return eligibilityContextService;
	}


	protected String getRegexp()
	{
		return regexp;
	}

	protected String getRegexpProductApi()
	{
		return regexpProductApi;
	}

	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}


}
