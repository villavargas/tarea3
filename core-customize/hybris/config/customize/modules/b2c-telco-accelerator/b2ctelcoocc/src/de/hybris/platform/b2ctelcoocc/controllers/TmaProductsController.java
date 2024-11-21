/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.b2ctelcoocc.controllers;

import de.hybris.platform.b2ctelcocommercewebservicescommons.validator.TmaUserAccessValidator;
import de.hybris.platform.b2ctelcofacades.data.TmaPriceContextData;
import de.hybris.platform.b2ctelcofacades.product.TmaProductOfferFacade;
import de.hybris.platform.b2ctelcoocc.constants.B2ctelcooccConstants;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commerceservices.request.mapping.annotation.RequestMappingOverride;
import de.hybris.platform.commercewebservicescommons.dto.product.ProductWsDTO;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;



/**
 * Web Services Controller to expose the functionality of the
 * {@link de.hybris.platform.b2ctelcofacades.product.TmaProductOfferFacade} and SearchFacade.
 *
 * @since1907
 */

@Controller
@RequestMapping(value = "/{baseSiteId}/products")
@ApiVersion("v2")
@Api(tags = "Products")
public class TmaProductsController extends BaseController
{

	private static final Collection<ProductOption> PRODUCT_OPTIONS = Arrays.asList(ProductOption.BASIC, ProductOption.DESCRIPTION,
			ProductOption.SOLD_INDIVIDUALLY, ProductOption.PRODUCT_SPECIFICATION, ProductOption.CATEGORIES,
			ProductOption.PRODUCT_MEDIA, ProductOption.PRODUCT_BPO_CHILDREN, ProductOption.PRODUCT_OFFERING_PRICES,
			ProductOption.PARENT_BPOS, ProductOption.PRODUCT_OFFERING_GROUPS, ProductOption.PRODUCT_SPEC_CHAR_VALUE_USE,
			ProductOption.CLASSIFICATION, ProductOption.VARIANT_MATRIX, ProductOption.VARIANT_MATRIX_ALL_OPTIONS,
			ProductOption.MEDIA_ATTACHMENT, ProductOption.REVIEW);

	private static final String ANONYMOUS = "anonymous";
	
	private static final String REGEX = ",";
	
	@Resource(name = "tmaProductOfferFacade")
	private TmaProductOfferFacade tmaProductOfferFacade;
	@Resource(name = "userAccessValidator")
	private TmaUserAccessValidator userAccessValidator;
	@Resource(name = "customerFacade")
	private CustomerFacade customerFacade;
	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	@RequestMapping(value = "/{productCode}", method = RequestMethod.GET)
	@RequestMappingOverride(priorityProperty = "b2ctelcoocc.TmaProductsController.getProductByCode.priority")
	@CacheControl(directive = CacheControlDirective.PRIVATE, maxAge = 120)
	@Cacheable(value = "productCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.CommerceCacheKeyGenerator).generateKey(true,true,true,true,true,#productCode,#processType,#userId,#fields,#region)")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@ApiBaseSiteIdParam
	@ApiOperation(value = "Get product details", notes = "Returns details of a single product according to a product code.")
	public ProductWsDTO getProductByCode(
			@ApiParam(value = "Product identifier", required = true) @PathVariable final String productCode,
			@ApiParam(value = "Process Type, for filtering product prices based on Process Type") @RequestParam(required = false) final String processType,
			@ApiParam(value = "User ID, for filtering product prices based on Customer") @RequestParam(required = false) final String userId,
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields,
			@ApiParam(value = "Region identifier for filtering product prices based on region") @Valid @RequestParam(value = "region", required = false) final String region)
	{

		validate(userId, "String", userAccessValidator);

		final String applicableUID = getApplicableUID(userId);

		final TmaPriceContextData tmaPriceContextData = new TmaPriceContextData();
		tmaPriceContextData.setProductCode(productCode);

		if (StringUtils.isNotBlank(region))
		{
			tmaPriceContextData.setRegionIsoCodes(Stream.of(region).collect(Collectors.toSet()));
		}
		if (StringUtils.isNotBlank(processType))
		{
			tmaPriceContextData
					.setProcessTypeCodes(new HashSet<String>(Arrays.asList(StringUtils.deleteWhitespace(processType).split(","))));
		}
		if (StringUtils.isNotBlank(applicableUID))
		{
			tmaPriceContextData.setUserId(applicableUID);
		}
		final ProductData product = tmaProductOfferFacade.getPoForCode(productCode, PRODUCT_OPTIONS, tmaPriceContextData);
		return getDataMapper().map(product, ProductWsDTO.class);
	}

	protected String getApplicableUID(final String userId)
	{
		final List<String> eligibilityAdminRoles = Arrays.asList(configurationService.getConfiguration().getString(B2ctelcooccConstants.ELIGIBILTY_ROLES).split(REGEX));

		if (StringUtils.isEmpty(userId))
		{
			final String currentUID = customerFacade.getCurrentCustomerUid();
			if (!hasRole(eligibilityAdminRoles))
			{
				return currentUID; // SSU1 or Anonymous
			}
			if (!currentUID.equals(ANONYMOUS))
			{
				return currentUID; //SSU1
			}
		}
		return userId; //SSU1 or ""
	}

	protected boolean hasRole(final List<String> eligibilityAdminRoles)
	{
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
}
