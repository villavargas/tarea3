/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.facades.mappers;

import org.springframework.beans.factory.annotation.Required;

import ma.glasnost.orika.MappingContext;


/**
 * This attribute mapper class that populates the value of a {@link TARGET} attribute by processing a {@link SOURCE}
 * object
 *
 * @since 1911
 */

public abstract class TelcelAttributeMapper<SOURCE, TARGET>
{
    /**
     * Name of the source attribute
     */
    private String sourceAttributeName;

    /**
     * Name of the target attribute
     */
    private String targetAttributeName;

    /**
     * Populates the target attribute value by obtained by processing the source object.
     *
     * @param source
     *           source object
     * @param target
     *           target object
     */
    public abstract void populateTargetAttributeFromSource(SOURCE source, TARGET target, final MappingContext context);

    /**
     * Populates the source attribute value by obtained by processing the target object.
     *
     * @param source
     *           source object
     * @param target
     *           target object
     */
    public void populateSourceAttributeFromTarget(final TARGET target, final SOURCE source, final MappingContext context)
    {
    }

    @SuppressWarnings("WeakerAccess")
    public String getSourceAttributeName()
    {
        return sourceAttributeName;
    }

    @Required
    public void setSourceAttributeName(final String sourceAttributeName)
    {
        this.sourceAttributeName = sourceAttributeName;
    }

    @SuppressWarnings("WeakerAccess")
    public String getTargetAttributeName()
    {
        return targetAttributeName;
    }

    @Required
    public void setTargetAttributeName(final String targetAttributeName)
    {
        this.targetAttributeName = targetAttributeName;
    }
}

