/*
 * Copyright (c)  2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package mx.com.telcel.facades.populators;

import de.hybris.platform.commercefacades.order.data.ConsignmentData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import mx.com.telcel.core.model.AdditionalServiceEntryModel;
import mx.com.telcel.facades.order.data.AdditionalServiceEntryData;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Consignment populator with TUA specific details.
 *
 * @since 2102
 */
public class TelcelConsignmentPopulator implements Populator<ConsignmentModel, ConsignmentData>
{

    private Converter<AdditionalServiceEntryModel, AdditionalServiceEntryData> additionalServiceEntryConverter;

    @Override
    public void populate(final ConsignmentModel source, final ConsignmentData target)
    {
        if(Objects.nonNull(source.getAdditionalServiceEntry()))
        {
            target.setAdditionalServiceEntry(additionalServiceEntryConverter.convert(source.getAdditionalServiceEntry()));
        }
        if(hasValidLine(source))
        {
            target.setLine(source.getSeriesICCID().getLinea());
        }
    }

    private boolean hasValidLine(final ConsignmentModel consignment)
    {
        return Objects.nonNull(consignment.getSeriesICCID())
                && StringUtils.isNotBlank(consignment.getSeriesICCID().getSku())
                && StringUtils.isNotBlank(consignment.getSeriesICCID().getLinea());
    }

    public void setAdditionalServiceEntryConverter(Converter<AdditionalServiceEntryModel, AdditionalServiceEntryData> additionalServiceEntryConverter) {
        this.additionalServiceEntryConverter = additionalServiceEntryConverter;
    }
}
