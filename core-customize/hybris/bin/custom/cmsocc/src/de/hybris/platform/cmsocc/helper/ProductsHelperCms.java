/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsocc.helper;

import de.hybris.platform.b2ctelcofacades.product.impl.DefaultTmaProductSearchFacade;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.impl.DefaultCategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.ProductSearchFacade;
import de.hybris.platform.commercefacades.search.data.SearchFilterQueryData;
import de.hybris.platform.commercefacades.search.data.SearchQueryData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.enums.SearchQueryContext;
import de.hybris.platform.commerceservices.search.facetdata.ProductCategorySearchPageData;
import de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commercewebservicescommons.dto.search.facetdata.ProductCategorySearchPageWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.search.facetdata.ProductSearchPageCategory;
import de.hybris.platform.commercewebservicescommons.dto.search.facetdata.ProductSearchPageWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;


@Component
public class ProductsHelperCms extends AbstractHelper
{
	@Resource(name = "productSearchFacade")
	private ProductSearchFacade<ProductData> productSearchFacade;

	private de.hybris.platform.cmsocc.helper.DefaultSearchQueryCodec searchQueryCodecCms = new de.hybris.platform.cmsocc.helper.DefaultSearchQueryCodec();

	//private de.hybris.platform.converters.impl.AbstractPopulatingConverter<SolrSearchQueryData, SearchStateData> solrSearchStateConverterCms = new de.hybris.platform.converters.impl.AbstractPopulatingConverter<SolrSearchQueryData, SearchStateData>();

	@Resource(name = "solrSearchStateConverter")
	private Converter<SolrSearchQueryData, SearchStateData> solrSearchStateConverter;
	@Resource
	private CategoryService categoryService;

	/**
	 * @deprecated since 6.6. Please use {@link #searchProducts(String, int, int, String, String, String)} instead.
	 */
	@Deprecated(since = "6.6", forRemoval = true)
	public ProductSearchPageWsDTO searchProducts(final String query, final int currentPage, final int pageSize, final String sort,
												 final String fields)
	{
		final ProductSearchPageData<SearchStateData, ProductData> sourceResult = searchProducts(query, currentPage, pageSize, sort);
		if (sourceResult instanceof ProductCategorySearchPageData)
		{
			String categoria = getCategoria(query);
			final CategoryModel category = categoryService.getCategoryForCode(categoria);
			ProductCategorySearchPageWsDTO productCategorySearchPageWsDTO = getDataMapper().map(sourceResult, ProductCategorySearchPageWsDTO.class, fields);
			ProductSearchPageCategory productSearchPageCategory = new ProductSearchPageCategory();
			productSearchPageCategory.setTitleSeo(category.getTitleSeo());
			productSearchPageCategory.setDescriptionSeo(category.getDescriptionSeo());
			productCategorySearchPageWsDTO.setCategory(productSearchPageCategory);
			return productCategorySearchPageWsDTO;
		}

		return getDataMapper().map(sourceResult, ProductSearchPageWsDTO.class, fields);
	}

	public ProductSearchPageData<SearchStateData, ProductData> searchProducts(final String query, final int currentPage,
																			  final int pageSize, final String sort)
	{
		final SolrSearchQueryData searchQueryData = searchQueryCodecCms.decodeQuery(query);
		final de.hybris.platform.commerceservices.search.pagedata.PageableData pageable = this.createPageableData(currentPage,
				pageSize, sort);

		return productSearchFacade.textSearch(solrSearchStateConverter.convert(searchQueryData), pageable);
	}

	public ProductSearchPageWsDTO searchProducts(final String query, final int currentPage, final int pageSize, final String sort,
												 final String fields, final String searchQueryContext)
	{
		final SearchQueryContext context = decodeContext(searchQueryContext);

		final ProductSearchPageData<SearchStateData, ProductData> sourceResult = searchProducts(query, currentPage, pageSize, sort,
				context);
		if (sourceResult instanceof ProductCategorySearchPageData)
		{
			final String categoria = getCategoria(query);
			if(StringUtils.isNotBlank(categoria))
			{
				final CategoryModel category = categoryService.getCategoryForCode(categoria);
				final ProductCategorySearchPageWsDTO productCategorySearchPageWsDTO = getDataMapper().map(sourceResult, ProductCategorySearchPageWsDTO.class, fields);
				final ProductSearchPageCategory productSearchPageCategory = new ProductSearchPageCategory();
				productSearchPageCategory.setTitleSeo(category.getTitleSeo());
				productSearchPageCategory.setDescriptionSeo(category.getDescriptionSeo());
				productCategorySearchPageWsDTO.setCategory(productSearchPageCategory);

				return productCategorySearchPageWsDTO;
			}
		}

		return getDataMapper().map(sourceResult, ProductSearchPageWsDTO.class, fields);
	}

	protected ProductSearchPageData<SearchStateData, ProductData> searchProducts(final String query, final int currentPage,
																				 final int pageSize, final String sort, final SearchQueryContext searchQueryContext)
	{
		final SolrSearchQueryData searchQueryData = searchQueryCodecCms.decodeQuery(query);
		searchQueryData.setSearchQueryContext(searchQueryContext);

		final de.hybris.platform.commerceservices.search.pagedata.PageableData pageable = this.createPageableData(currentPage,
				pageSize, sort);
 
		SearchStateData searchStateData = new SearchStateData();
		final SearchQueryData queryData = new SearchQueryData();
		final List<SearchFilterQueryData> filterQueries = new ArrayList<SearchFilterQueryData>();
		queryData.setFilterQueries(filterQueries);
		queryData.setValue(query);
		searchStateData.setQuery(queryData);
		
		searchStateData.setQuery(queryData);
		searchStateData.setUrl("/search?q=%3Arelevance%3AallCategories%3Atelefonos-y-smartphones");
		
		return productSearchFacade.textSearch(solrSearchStateConverter.convert(searchQueryData), pageable);
	}

	protected SearchQueryContext decodeContext(final String searchQueryContext)
	{
		if (StringUtils.isBlank(searchQueryContext))
		{
			return null;
		}

		try
		{
			return SearchQueryContext.valueOf(searchQueryContext);
		}
		catch (final IllegalArgumentException e)
		{
			throw new RequestParameterException(searchQueryContext + " context does not exist", RequestParameterException.INVALID,
					e);
		}
	}


	private static String getCategoria(final String query) {
		String categoria = "";
		if(StringUtils.isNotBlank(query) )
		{
			final String [] querySplit = query.split(":");
			boolean allCategories = false;
			for(final String cadena : querySplit){
				if(allCategories){
					categoria = cadena;
					allCategories = false;
				}
				if("allCategories".equalsIgnoreCase(cadena)){
					allCategories = true;
				}
			}
		}

		return categoria;
	}


}
