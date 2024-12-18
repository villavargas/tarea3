/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.facades.converters.impl;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.converters.PopulatorList;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import javax.annotation.PostConstruct;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.jalo.JaloObjectNoLongerValidException;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Populating converter that uses a list of configured populators to populate the target during conversion. Class used
 * to be but is no longer abstract. It allows to declare it as an abstract bean in Spring, otherwise we'd get
 * BeanInstantiationException.
 */
public class AbstractPopulatingAsyncConverter<SOURCE, TARGET> extends AbstractAsyncConverter<SOURCE, TARGET> implements
        PopulatorList<SOURCE, TARGET>
{

    private final static Logger LOG = Logger.getLogger(AbstractPopulatingAsyncConverter.class);
    private List<Populator<SOURCE, TARGET>> populators;
    private ModelService modelService;

    @Override
    public List<Populator<SOURCE, TARGET>> getPopulators()
    {
        return populators;
    }

    @Override
    public void setPopulators(final List<Populator<SOURCE, TARGET>> populators)
    {
        this.populators = populators;
    }

    /**
     * Populate the target instance from the source instance. Calls a list of injected populators to populate the
     * instance.
     *
     * @param source the source item
     * @param target the target item to populate
     */
    @Override
    public void populate(final SOURCE source, final TARGET target)
    {
        try {
            final List<Populator<SOURCE, TARGET>> list = getPopulators();

            if (list == null) {
                return;
            }

            for (final Populator<SOURCE, TARGET> populator : list) {
                if (populator != null) {
                    populator.populate(source, target);
                }
            }
        }catch (JaloObjectNoLongerValidException ex)
        {
            if(source instanceof ItemModel)
            {
                modelService.refresh(source);
                LOG.warn("Refresh model");
            }
            throw ex;
        }
    }

    // execute when BEAN name is known
    @PostConstruct
    public void removePopulatorsDuplicates()
    {
        // CHECK for populators duplicates
        if (CollectionUtils.isNotEmpty(populators))
        {
            final LinkedHashSet<Populator<SOURCE, TARGET>> distinctPopulators = new LinkedHashSet<>();

            for (final Populator<SOURCE, TARGET> populator : populators)
            {
                if (!distinctPopulators.add(populator))
                {
                    LOG.warn("Duplicate populator entry [" + populator.getClass().getName() + "] found for converter "
                            + getMyBeanName() + "! The duplication has been removed.");
                }
            }
            this.populators = new ArrayList<>(distinctPopulators);
        }
        else
        {
            LOG.warn("Empty populators list found for converter " + getMyBeanName() + "!");
        }
    }

    public void setModelService(final ModelService modelService)
    {
        this.modelService = modelService;
    }
}
