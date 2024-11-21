/*
 *	Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.controllers;

import de.hybris.platform.b2ctelcofacades.data.ProductDataList;
import de.hybris.platform.b2ctelcofacades.data.ProductOfferingSearchContextData;
import de.hybris.platform.b2ctelcofacades.product.TmaProductSearchFacade;
import mx.com.telcel.constants.Telb2coccConstants;
import de.hybris.platform.b2ctelcocommercewebservicescommons.formatters.TmaWsDateFormatter;
import de.hybris.platform.commercefacades.product.ProductExportFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductResultData;
import de.hybris.platform.commerceservices.request.mapping.annotation.RequestMappingOverride;
import de.hybris.platform.commercewebservicescommons.dto.product.ProductListWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;


/**
 * Web Services Controller to expose the functionality of the
 * {@link de.hybris.platform.commercefacades.product.ProductFacade} and SearchFacade.
 *
 * @deprecated since 1911 - use ExportController instead
 */
@Deprecated(since = "1911", forRemoval= true)
@Controller
@RequestMapping(value = "/{baseSiteId}/export")
@Api(tags = "Export")
public class TmaExportController extends BaseController
{
	private static final Set<ProductOption> OPTIONS;
	private static final String DEFAULT_PAGE_VALUE = "0";
	private static final String MAX_INTEGER = "20";
	@Resource(name = "cwsProductExportFacade")
	private ProductExportFacade productExportFacade;
	@Resource(name = "tmaWsDateFormatter")
	private TmaWsDateFormatter wsDateFormatter;
	@Resource(name = "productSearchFacade")
	private TmaProductSearchFacade tmaProductSearchFacade;

	static
	{
		StringBuilder productOptions = new StringBuilder("");

		for (final ProductOption option : ProductOption.values())
		{
			productOptions.append(option.toString()+" ");
		}

		OPTIONS = extractOptions(productOptions.toString().trim().replace(" ", Telb2coccConstants.OPTIONS_SEPARATOR));
	}

	protected static Set<ProductOption> extractOptions(final String options)
	{
		final List<String> optionsStrings = Arrays.asList(options.split(Telb2coccConstants.OPTIONS_SEPARATOR));
		final Set<ProductOption> opts = new HashSet<ProductOption>();
		optionsStrings.forEach(option ->
		{
			opts.add(ProductOption.valueOf(option));
		});
		return opts;
	}

	@Secured("ROLE_TRUSTED_CLIENT")
	@RequestMapping(value = "/products", method = RequestMethod.GET)
	@RequestMappingOverride(priorityProperty = "telb2cocc.TmaExportController.exportProducts.priority")
	@ResponseBody
	@ApiOperation(value = "Get a list of product exports.", notes = "Used for product export. Depending on the timestamp parameter, it can return all products or only products modified after the given time.", authorizations =
	{ @Authorization(value = "oauth2_client_credentials") })
	@ApiBaseSiteIdParam
	public ProductListWsDTO exportProducts(
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields,
			@ApiParam(value = "The current result page requested.") @RequestParam(required = false, defaultValue = DEFAULT_PAGE_VALUE) final int currentPage,
			@ApiParam(value = "The number of results returned per page.") @RequestParam(required = false, defaultValue = MAX_INTEGER) final int pageSize,
			@ApiParam(value = "The catalog to retrieve products from. The catalog must be provided along with the version.") @RequestParam(required = false) final String catalog,
			@ApiParam(value = "The catalog version. The catalog version must be provided along with the catalog.") @RequestParam(required = false) final String version,
			@ApiParam(value = "When this parameter is set, only products modified after the given time will be returned. This parameter should be in ISO-8601 format (for example, 2018-01-09T16:28:45+0000).") @RequestParam(required = false) final String timestamp)
	{
		if ((StringUtils.isEmpty(catalog) && !StringUtils.isEmpty(version))
				|| (StringUtils.isEmpty(version) && !StringUtils.isEmpty(catalog)))
		{
			throw new RequestParameterException("Both 'catalog' and 'version' parameters have to be provided or ignored.",
					RequestParameterException.MISSING, catalog == null ? "catalog" : "version");
		}

		final ProductOfferingSearchContextData productOfferingSearchContext = new ProductOfferingSearchContextData();
		if (timestamp != null)
		{
			final Date timestampDate;
			try
			{
				timestampDate = wsDateFormatter.toDate(timestamp);
				productOfferingSearchContext.setTimestamp(timestampDate);
			}
			catch (final IllegalArgumentException e)
			{
				throw new RequestParameterException("Wrong time format. The only accepted format is ISO-8601.",
						RequestParameterException.INVALID, "timestamp", e);
			}
		}
		productOfferingSearchContext.setCatalog(catalog);
		productOfferingSearchContext.setCurrentPage(currentPage);
		productOfferingSearchContext.setVersion(version);
		productOfferingSearchContext.setPageSize(pageSize);

		final ProductResultData products = tmaProductSearchFacade.getProductOfferingsForOptions(productOfferingSearchContext,
				OPTIONS);

		return getDataMapper().map(convertResultset(currentPage, pageSize, catalog, version, products), ProductListWsDTO.class,
				fields);
	}

	public ProductDataList convertResultset(final int page, final int pageSize, final String catalog, final String version,
			final ProductResultData modifiedProducts)
	{
		final ProductDataList result = new ProductDataList();
		result.setProducts(modifiedProducts.getProducts());
		if (pageSize > 0)
		{
			result.setTotalPageCount((modifiedProducts.getTotalCount() % pageSize == 0) ? ((modifiedProducts.getTotalCount()) / pageSize)
					: ((modifiedProducts.getTotalCount()) / pageSize + 1));
		}
		result.setCurrentPage(page);
		result.setTotalProductCount(modifiedProducts.getTotalCount());
		result.setCatalog(catalog);
		result.setVersion(version);
		return result;
	}
}
