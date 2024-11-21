/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.facades.populators;

import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import mx.com.telcel.core.model.AdditionalServiceEntryModel;
import mx.com.telcel.core.model.EsquemaCobroModel;
import mx.com.telcel.core.model.TelcelPoVariantModel;
import mx.com.telcel.facades.esquemacobro.data.TelcelEsquemaCobroDTO;
import mx.com.telcel.facades.order.data.AdditionalServiceEntryData;
import mx.com.telcel.facades.order.data.EntryQtyBreakdownData;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TelcelOrderEntryPopulator implements Populator<AbstractOrderEntryModel, OrderEntryData> {

    private Converter<AdditionalServiceEntryModel, AdditionalServiceEntryData> additionalServiceEntryConverter;
    private PriceDataFactory priceDataFactory;

    @Override
    public void populate(final AbstractOrderEntryModel source, final OrderEntryData target)
    {
        Assert.notNull(source, "Parameter source cannot be null.");
        Assert.notNull(target, "Parameter target cannot be null.");

        if (source.getEsquemaCobro()!=null) {
            EsquemaCobroModel model = source.getEsquemaCobro();

            TelcelEsquemaCobroDTO esquemaCobroDTO = new TelcelEsquemaCobroDTO();
            esquemaCobroDTO.setCode( model.getCode() );
            esquemaCobroDTO.setName( model.getName() );
            esquemaCobroDTO.setDescription( model.getDescription() );

            target.setEsquemaCobro( esquemaCobroDTO );
        }
        target.setAdditionalServiceEntries(getAdditionalServiceEntryConverter().convertAll(source.getAdditionalServiceEntries()));
        if(Objects.nonNull(source.getTotalPrice()))
        {
            double chipAsValue = getChipAsValues(source, 0D);
            target.setTotalWithoutAdditonalServices(createPrice(source, ((source.getTotalPrice() - source.getTotalAsPrice()) + chipAsValue)));
        }
        addQuantityBreakdowns(source, target);
    }

    private double getChipAsValues(AbstractOrderEntryModel entryModel, double chipAsValue) {
        if (entryModel.getProduct() instanceof TelcelPoVariantModel) {
            final TelcelPoVariantModel telcelPoVariantModel = (TelcelPoVariantModel) entryModel.getProduct();
            if (telcelPoVariantModel.getChipAdditionalServiceProduct() != null) {
                for (AdditionalServiceEntryModel additionalServiceEntryModel : entryModel.getAdditionalServiceEntries()) {
                    chipAsValue += additionalServiceEntryModel.getBasePrice();
                }
            }
        }

        return chipAsValue;
    }

    private void addQuantityBreakdowns(final AbstractOrderEntryModel source, final OrderEntryData target)
    {
        if(Objects.nonNull(source.getQuantity()))
        {
            List<EntryQtyBreakdownData> quantityBreakdown = new ArrayList<>();
            for(int i=0;i<source.getQuantity().intValue();i++)
            {
                EntryQtyBreakdownData entryQtyBreakdown = new EntryQtyBreakdownData();
                entryQtyBreakdown.setEntryQtyPos(i);
                entryQtyBreakdown.setQuantity((long) (i + 1));
                quantityBreakdown.add(entryQtyBreakdown);
            }
            target.setQuantityBreakdown(quantityBreakdown);
        }
    }

    protected PriceData createPrice(final AbstractOrderEntryModel source, final Double val)
    {
        return priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(val.doubleValue()), source.getOrder().getCurrency());
    }

    public void setPriceDataFactory(PriceDataFactory priceDataFactory) {
        this.priceDataFactory = priceDataFactory;
    }

    protected Converter<AdditionalServiceEntryModel, AdditionalServiceEntryData> getAdditionalServiceEntryConverter() {
        return additionalServiceEntryConverter;
    }

    public void setAdditionalServiceEntryConverter(Converter<AdditionalServiceEntryModel, AdditionalServiceEntryData> additionalServiceEntryConverter) {
        this.additionalServiceEntryConverter = additionalServiceEntryConverter;
    }
}
