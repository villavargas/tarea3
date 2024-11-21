/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.backoffice.omsbackoffice.handlers;

import com.hybris.backoffice.navigation.NavigationNode;
import com.hybris.backoffice.widgets.advancedsearch.AdvancedSearchInitializer;
import com.hybris.backoffice.widgets.advancedsearch.impl.AdvancedSearchData;
import com.hybris.backoffice.widgets.advancedsearch.impl.SearchConditionData;
import com.hybris.backoffice.widgets.advancedsearch.impl.SearchConditionDataList;
import com.hybris.cockpitng.core.config.impl.jaxb.hybris.advancedsearch.FieldType;
import com.hybris.cockpitng.search.data.ValueComparisonOperator;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.nonNull;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;


public class SourceRuleAdvancedSearchInitializer implements AdvancedSearchInitializer {

    private static final String STATUS = "status";
    @Override
    public void addSearchDataConditions(AdvancedSearchData advancedSearchData, Optional<NavigationNode> optional) {
        if (nonNull(advancedSearchData))
        {
            removeExistingStatusCondition(advancedSearchData);
            advancedSearchData.addConditionList(ValueComparisonOperator.OR, createStatusSearchConditions());
        }
    }

    protected void removeExistingStatusCondition(final AdvancedSearchData searchData)
    {
        final List<SearchConditionData> conditions = searchData.getConditions(STATUS);
        if (isNotEmpty(conditions))
        {
            conditions.clear();
        }
    }

    protected List<SearchConditionData> createStatusSearchConditions()
    {
        return Arrays.asList(createStatusConditionsList());
    }

    protected SearchConditionDataList createStatusConditionsList()
    {
        final FieldType status = createStausField();
        final List statusConditionsList = new ArrayList();
        statusConditionsList.add(createCondition(status, ConsignmentStatus.DELIVERY_COMPLETED));
        statusConditionsList.add(createCondition(status, ConsignmentStatus.ORDER_DELIVERED));
        return SearchConditionDataList.or(statusConditionsList);
    }

    protected FieldType createStausField()
    {
        final FieldType status = new FieldType();
        status.setDisabled(Boolean.FALSE);
        status.setSelected(Boolean.TRUE);
        status.setName(STATUS);
        return status;
    }

    protected SearchConditionData createCondition(final FieldType status, final ConsignmentStatus predicate)
    {
        return new SearchConditionData(status, predicate, ValueComparisonOperator.EQUALS);
    }
}
