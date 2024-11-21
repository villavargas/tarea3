package mx.com.telcel.facades.populators;

import de.hybris.platform.commerceservices.search.facetdata.FacetData;
import de.hybris.platform.commerceservices.search.facetdata.FacetSearchPageData;
import de.hybris.platform.commerceservices.search.facetdata.FacetValueData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryTermData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchResponse;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.search.Facet;
import de.hybris.platform.solrfacetsearch.search.FacetValue;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.SearchResult;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.util.CollectionUtils;

import java.util.*;

public class TelcelSearchResponseFacetsPopulator<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, INDEXED_PROPERTY_TYPE, INDEXED_TYPE_SORT_TYPE, ITEM>
        implements
        Populator<SolrSearchResponse<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, INDEXED_PROPERTY_TYPE, SearchQuery, INDEXED_TYPE_SORT_TYPE, SearchResult>, FacetSearchPageData<SolrSearchQueryData, ITEM>>,
        BeanFactoryAware
{

    public static final String ACCESIBILIDAD = "Accesibilidad";
    public static final boolean VISIBLE = true;
    public static final String MAS_VENDIDO = "masVendidos";
    public static final String DESTACADOS = "Destacados";
    public static final String MAS_VENDIDOS = "masVendidosValue";
    public static final String IS_PROMOTION = "isPromotion";
    public static final String IS_NEW = "isNew";
    public static final String IS_VISUAL = "isVisual";
    public static final String IS_AUDITVA = "isAuditva";
    public static final String IS_MOTRIZ = "isMotriz";
    public static final int PRIORITY = 7000;
    public static final int INDEX = 3;
    private BeanFactory beanFactory;

    @Override
    public void populate(
            final SolrSearchResponse<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, INDEXED_PROPERTY_TYPE, SearchQuery, INDEXED_TYPE_SORT_TYPE, SearchResult> source,
            final FacetSearchPageData<SolrSearchQueryData, ITEM> target)
    {
        final SearchResult solrSearchResult = source.getSearchResult();
        final SolrSearchQueryData solrSearchQueryData = target.getCurrentQuery();
        final IndexedType indexedType = source.getRequest().getSearchQuery().getIndexedType();

        if (solrSearchResult != null)
        {
            target.setFacets(buildFacets(solrSearchResult, solrSearchQueryData, indexedType));
            crearFacetAccesibilidad(target.getFacets());
            crearFacetDestacados(target.getFacets());
        }
    }

    protected List<FacetData<SolrSearchQueryData>> buildFacets(final SearchResult solrSearchResult,
                                                               final SolrSearchQueryData searchQueryData, final IndexedType indexedType)
    {
        final List<Facet> solrSearchResultFacets = solrSearchResult.getFacets();
        List<FacetData<SolrSearchQueryData>> result = new ArrayList<>(solrSearchResultFacets.size());

        for (final Facet facet : solrSearchResultFacets)
        {
            final IndexedProperty indexedProperty = indexedType.getIndexedProperties().get(facet.getName());

            // Ignore any facets with a priority less than or equal to 0 as they are for internal use only
            final FacetData<SolrSearchQueryData> facetData = createFacetData();
            facetData.setCode(facet.getName());
            facetData.setCategory(indexedProperty.isCategoryField());
            final String displayName = indexedProperty.getDisplayName();
            facetData.setName(displayName == null ? facet.getName() : displayName);
            facetData.setMultiSelect(facet.isMultiselect());
            facetData.setPriority(facet.getPriority());
            facetData.setVisible(indexedProperty.isVisible());

            buildFacetValues(facetData, facet, indexedProperty, solrSearchResult, searchQueryData);

            // Only add the facet if there are values
            if (facetData.getValues() != null && !facetData.getValues().isEmpty())
            {
                result.add(facetData);
            }
        }

        return result;
    }

    protected void buildFacetValues(final FacetData<SolrSearchQueryData> facetData, final Facet facet,
                                    final IndexedProperty indexedProperty, final SearchResult solrSearchResult, final SolrSearchQueryData searchQueryData)
    {
        final List<FacetValue> facetValues = facet.getFacetValues();
        if (facetValues != null && !facetValues.isEmpty())
        {
            final List<FacetValueData<SolrSearchQueryData>> allFacetValues = new ArrayList<>(facetValues.size());

            for (final FacetValue facetValue : facetValues)
            {
                final FacetValueData<SolrSearchQueryData> facetValueData = buildFacetValue(facetData, facet, facetValue,
                        solrSearchResult, searchQueryData);
                if (facetValueData != null)
                {
                    allFacetValues.add(facetValueData);
                }
            }

            facetData.setValues(allFacetValues);

            if (!CollectionUtils.isEmpty(facet.getTopFacetValues()))
            {
                final List<FacetValueData<SolrSearchQueryData>> topFacetValuesData = new ArrayList<>();
                for (final FacetValue facetValue : facet.getTopFacetValues())
                {
                    final FacetValueData<SolrSearchQueryData> topFacetValueData = buildFacetValue(facetData, facet, facetValue,
                            solrSearchResult, searchQueryData);
                    if (topFacetValueData != null)
                    {
                        topFacetValuesData.add(topFacetValueData);
                    }
                }
                facetData.setTopValues(topFacetValuesData);
            }
        }
    }

    @SuppressWarnings("unused")
    protected FacetValueData<SolrSearchQueryData> buildFacetValue(final FacetData<SolrSearchQueryData> facetData,
                                                                  final Facet facet, final FacetValue facetValue, final SearchResult searchResult,
                                                                  final SolrSearchQueryData searchQueryData)
    {
        final FacetValueData<SolrSearchQueryData> facetValueData = createFacetValueData();
        facetValueData.setCode(facetValue.getName());
        facetValueData.setName(facetValue.getDisplayName());
        facetValueData.setCount(facetValue.getCount());

        // Check if the facet is selected
        facetValueData.setSelected(isFacetSelected(searchQueryData, facet.getName(), facetValue.getName()));

        if (facetValueData.isSelected())
        {
            // Query to remove, rather than add facet
            facetValueData.setQuery(refineQueryRemoveFacet(searchQueryData, facet.getName(), facetValue.getName()));
        }
        else
        {
            // Query to add the facet
            facetValueData.setQuery(refineQueryAddFacet(searchQueryData, facet.getName(), facetValue.getName()));
        }

        return facetValueData;
    }

    protected boolean isFacetSelected(final SolrSearchQueryData searchQueryData, final String facetName, final String facetValue)
    {
        for (final SolrSearchQueryTermData termData : searchQueryData.getFilterTerms())
        {
            if (termData.getKey().equals(facetName) && termData.getValue().equals(facetValue))
            {
                return true;
            }
        }
        return false;
    }

    protected SolrSearchQueryData refineQueryAddFacet(final SolrSearchQueryData searchQueryData, final String facet,
                                                      final String facetValue)
    {
        final SolrSearchQueryTermData newTerm = createSearchQueryTermData();
        newTerm.setKey(facet);
        newTerm.setValue(facetValue);

        final List<SolrSearchQueryTermData> newTerms = new ArrayList<>(searchQueryData.getFilterTerms());
        newTerms.add(newTerm);

        // Build the new query data
        final SolrSearchQueryData result = cloneSearchQueryData(searchQueryData);
        result.setFilterTerms(newTerms);
        return result;
    }

    protected SolrSearchQueryData refineQueryRemoveFacet(final SolrSearchQueryData searchQueryData, final String facet,
                                                         final String facetValue)
    {
        final List<SolrSearchQueryTermData> newTerms = new ArrayList<>(searchQueryData.getFilterTerms());

        // Remove the term for the specified facet
        final Iterator<SolrSearchQueryTermData> iterator = newTerms.iterator();
        while (iterator.hasNext())
        {
            final SolrSearchQueryTermData term = iterator.next();
            if (facet.equals(term.getKey()) && facetValue.equals(term.getValue()))
            {
                iterator.remove();
            }
        }

        // Build the new query data
        final SolrSearchQueryData result = cloneSearchQueryData(searchQueryData);
        result.setFilterTerms(newTerms);
        return result;
    }

    /**
     * Shallow clone of the source SearchQueryData
     *
     * @param source
     *           the instance to clone
     * @return the shallow clone
     */
    protected SolrSearchQueryData cloneSearchQueryData(final SolrSearchQueryData source)
    {
        final SolrSearchQueryData target = createSearchQueryData();
        target.setFreeTextSearch(source.getFreeTextSearch());
        target.setCategoryCode(source.getCategoryCode());
        target.setSort(source.getSort());
        target.setFilterTerms(source.getFilterTerms());
        return target;
    }

    protected FacetData<SolrSearchQueryData> createFacetData()
    {
        return new FacetData<>();
    }

    protected FacetValueData<SolrSearchQueryData> createFacetValueData()
    {
        return new FacetValueData<>();
    }

    protected SolrSearchQueryTermData createSearchQueryTermData()
    {
        return new SolrSearchQueryTermData();
    }

    protected SolrSearchQueryData createSearchQueryData()
    {
        return new SolrSearchQueryData();
    }

    protected boolean isRanged(final IndexedProperty property)
    {
        return !CollectionUtils.isEmpty(property.getValueRangeSets());
    }

    @Override
    public void setBeanFactory(final BeanFactory beanFactory) throws BeansException
    {
        this.beanFactory = beanFactory;
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    private void crearFacetAccesibilidad(List<FacetData<SolrSearchQueryData>> facetData) {
        List<FacetValueData<SolrSearchQueryData>> values = new ArrayList<>();
        if (!facetData.isEmpty()){
            FacetData<SolrSearchQueryData> facetAccesibilidad = createFacetData();
            facetAccesibilidad.setName(ACCESIBILIDAD);
            facetAccesibilidad.setCode(ACCESIBILIDAD);
            facetAccesibilidad.setVisible(VISIBLE);
            facetAccesibilidad.setMultiSelect(true);


            Optional<FacetData<SolrSearchQueryData>> isMotriz =
                    facetData.stream().filter(data -> data.getCode().equals(IS_MOTRIZ)).findFirst();
            if (isMotriz.isPresent() && !isMotriz.get().getValues().isEmpty() ) {
                values.addAll(isMotriz.get().getValues());
            }

            Optional<FacetData<SolrSearchQueryData>>isAuditva =
                    facetData.stream().filter(data -> data.getCode().equals(IS_AUDITVA)).findFirst();
            if (isAuditva.isPresent() && !isAuditva.get().getValues().isEmpty()) {
                values.addAll(isAuditva.get().getValues());
            }

            Optional<FacetData<SolrSearchQueryData>> isVisual =
                    facetData.stream().filter(data -> data.getCode().equals(IS_VISUAL)).findFirst();
            if (isVisual.isPresent() && !isVisual.get().getValues().isEmpty()) {
                values.addAll(isVisual.get().getValues());
            }


            if (!values.isEmpty()) {
                facetAccesibilidad.setValues(values);
                facetData.add(facetAccesibilidad);
            }
        }
    }

    private void crearFacetDestacados( List<FacetData<SolrSearchQueryData>> facetData) {
        List<FacetValueData<SolrSearchQueryData>> values = new ArrayList<>();
        if (!facetData.isEmpty()){
            FacetData<SolrSearchQueryData> facetAccesibilidad = createFacetData();
            facetAccesibilidad.setName(DESTACADOS);
            facetAccesibilidad.setCode(DESTACADOS);
            facetAccesibilidad.setVisible(VISIBLE);
            facetAccesibilidad.setMultiSelect(true);
            facetAccesibilidad.setPriority(PRIORITY);

            Optional<FacetData<SolrSearchQueryData>> isNew =
                    facetData.stream().filter(data -> data.getCode().equals(IS_NEW)).findFirst();
            if (isNew.isPresent() && !isNew.get().getValues().isEmpty() ) {
                values.addAll(isNew.get().getValues());
            }

            Optional<FacetData<SolrSearchQueryData>> isPromotion =
                    facetData.stream().filter(data -> data.getCode().equals(IS_PROMOTION)).findFirst();
            if (isPromotion.isPresent() && !isPromotion.get().getValues().isEmpty()) {
                values.addAll(isPromotion.get().getValues());
            }

            Optional<FacetData<SolrSearchQueryData>> masVendidos =
                    facetData.stream().filter(data -> data.getCode().equals(MAS_VENDIDOS)).findFirst();
            if (masVendidos.isPresent() && !masVendidos.get().getValues().isEmpty()) {
                String name = masVendidos.get().getName();
                List<FacetValueData<SolrSearchQueryData>> valueMasVendidos = masVendidos.get().getValues();
                for ( FacetValueData<SolrSearchQueryData> facetValueData : valueMasVendidos) {
                    facetValueData.setName(name);
                }
                values.addAll(valueMasVendidos);
            }

            if (!values.isEmpty()) {
                facetAccesibilidad.setValues(values);
                facetData.add(INDEX,facetAccesibilidad);
            }
        }
    }
}
