/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.facades.mappers;

import de.hybris.platform.b2ctelcofacades.mappers.TmaAttributeMapper;
import de.hybris.platform.webservicescommons.mapping.mappers.AbstractCustomMapper;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.metadata.TypeFactory;


/**
 * This Mapper class maps data in between resource{@link SOURCE} and {@link TARGET}
 *
 * @since 1911
 */
public class TelcelEntityMapper<SOURCE, TARGET> extends AbstractCustomMapper<SOURCE, TARGET>
{
    /**
     * Source class
     */
    private Class<SOURCE> sourceClass;
    /**
     * Target class
     */
    private Class<TARGET> targetClass;

    /**
     * List of atribute mappers
     */
    private List<TmaAttributeMapper<SOURCE, TARGET>> attributeMappers;

    /**
     * List of atribute mappers
     */
    private List<TelcelAttributeMapper<SOURCE, TARGET>> telcelAttributeMappers;

    /**
     * {@inheritDoc} Populates the attributes on the target object with values of the attributes from source object, by
     * calling the attribute mappers.
     *
     * @param source
     *           the source object
     * @param target
     *           the target object
     * @param context
     *           the context
     */
    @Override
    public void mapAtoB(final SOURCE source, final TARGET target, final MappingContext context)
    {
        getAttributeMappers().forEach(attributeMapper ->
        {
            context.beginMappingField(attributeMapper.getSourceAttributeName(), getAType(), source,
                    attributeMapper.getTargetAttributeName(), getBType(), target);
            try
            {
                if (shouldMap(source, target, context))
                {
                    attributeMapper.populateTargetAttributeFromSource(source, target, context);
                }
            }
            finally
            {
                context.endMappingField();
            }
        });
        getTelcelAttributeMappers().forEach(attributeMapper -> {
            context.beginMappingField(attributeMapper.getSourceAttributeName(), getAType(), source,
                    attributeMapper.getTargetAttributeName(), getBType(), target);
            try {
                attributeMapper.populateTargetAttributeFromSource(source, target, context);
            } finally {
                context.endMappingField();
            }
        });
    }

    /**
     * {@inheritDoc} Populates the attributes on the source object with values of the attributes from target object, by
     * calling the attribute mappers.
     *
     * @param source
     *           the source object
     * @param target
     *           the target object
     * @param context
     *           the context
     */
    @Override
    public void mapBtoA(final TARGET target, final SOURCE source, final MappingContext context)
    {
        getAttributeMappers().forEach(attributeMapper ->
        {
            context.beginMappingField(attributeMapper.getTargetAttributeName(), getBType(), target,
                    attributeMapper.getSourceAttributeName(), getAType(), source);
            try
            {
                if (shouldMap(target, source, context))
                {
                    attributeMapper.populateSourceAttributeFromTarget(target, source, context);
                }
            }
            finally
            {
                context.endMappingField();
            }
        });
    }

    @Override
    public Type<SOURCE> getAType()
    {
        return TypeFactory.valueOf(getSourceClass());
    }

    @Override
    public Type<TARGET> getBType()
    {
        return TypeFactory.valueOf(getTargetClass());
    }

    @SuppressWarnings("WeakerAccess")
    public Class<SOURCE> getSourceClass()
    {
        return sourceClass;
    }

    @Required
    public void setSourceClass(final Class<SOURCE> sourceClass)
    {
        this.sourceClass = sourceClass;
    }

    @SuppressWarnings("WeakerAccess")
    public Class<TARGET> getTargetClass()
    {
        return targetClass;
    }

    @Required
    public void setTargetClass(final Class<TARGET> targetClass)
    {
        this.targetClass = targetClass;
    }

    @SuppressWarnings("WeakerAccess")
    public List<TmaAttributeMapper<SOURCE, TARGET>> getAttributeMappers()
    {
        return attributeMappers;
    }

    @Required
    public void setAttributeMappers(final List<TmaAttributeMapper<SOURCE, TARGET>> attributeMappers)
    {
        this.attributeMappers = attributeMappers;
    }

    @SuppressWarnings("WeakerAccess")
    public List<TelcelAttributeMapper<SOURCE, TARGET>> getTelcelAttributeMappers()
    {
        return telcelAttributeMappers;
    }

    @Required
    public void setTelcelAttributeMappers(final List<TelcelAttributeMapper<SOURCE, TARGET>> telcelAttributeMappers)
    {
        this.telcelAttributeMappers = telcelAttributeMappers;
    }

}
