package mx.com.telcel.facades.populators;

import de.hybris.platform.b2ctelcoservices.model.TmaComponentProdOfferPriceModel;
import de.hybris.platform.b2ctelcoservices.model.TmaPricingLogicAlgorithmModel;
import de.hybris.platform.b2ctelcoservices.model.TmaProductSpecCharacteristicValueModel;
import de.hybris.platform.b2ctelcoservices.pricing.TmaSolrDocumentComponentPop;
import de.hybris.platform.b2ctelcoservices.pricing.TmaSolrDocumentCurrency;
import de.hybris.platform.b2ctelcoservices.pricing.TmaSolrDocumentPricingLogicAlgorithm;
import de.hybris.platform.b2ctelcoservices.pricing.TmaSolrDocumentPscvUse;
import de.hybris.platform.b2ctelcoservices.search.solrfacetsearch.populator.pricing.TmaSolrDocumentProductOfferingPricePopulator;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class TelcelTmaSolrDocumentComponentPopPopulator <SOURCE extends TmaComponentProdOfferPriceModel, TARGET extends TmaSolrDocumentComponentPop>
        extends TmaSolrDocumentProductOfferingPricePopulator<SOURCE, TARGET>
{
    private Converter<TmaPricingLogicAlgorithmModel, TmaSolrDocumentPricingLogicAlgorithm> pricingLogicAlgorithmConverter;

    public TelcelTmaSolrDocumentComponentPopPopulator(
            final Converter<TmaProductSpecCharacteristicValueModel, TmaSolrDocumentPscvUse> pscvUseConverter,
            final Converter<TmaPricingLogicAlgorithmModel, TmaSolrDocumentPricingLogicAlgorithm> pricingLogicAlgorithmConverter)
    {
        super(pscvUseConverter);
        this.pricingLogicAlgorithmConverter = pricingLogicAlgorithmConverter;
    }

    @Override
    public void populate(final SOURCE source, final TARGET target)
    {
        super.populate(source, target);

        target.setValue(source.getValue());
        target.setCurrency(getCurrencyFromSource(source.getCurrency()));
        if (source.getBasePricevalue() != null) {
            target.setBasePricevalue(source.getBasePricevalue());
        }
        if (source.getMaxPrice() != null) {
            target.setMaxPrice(source.getMaxPrice());
        }

        if (source.getPricingLogicAlgorithm() != null)
        {
            target.setPla(getPricingLogicAlgorithmConverter().convert(source.getPricingLogicAlgorithm()));
        }
    }

    protected TmaSolrDocumentCurrency getCurrencyFromSource(final CurrencyModel sourceCurrency)
    {
        final TmaSolrDocumentCurrency currency = new TmaSolrDocumentCurrency();

        currency.setName(sourceCurrency.getName());
        currency.setActive(sourceCurrency.getActive());
        currency.setIsocode(sourceCurrency.getIsocode());
        currency.setSymbol(sourceCurrency.getSymbol());
        return currency;
    }

    protected Converter<TmaPricingLogicAlgorithmModel, TmaSolrDocumentPricingLogicAlgorithm> getPricingLogicAlgorithmConverter()
    {
        return pricingLogicAlgorithmConverter;
    }
}