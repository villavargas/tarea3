package mx.com.telcel.facades.populators;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class TelcelSearchProductPopulator implements Populator<SearchResultValueData, ProductData>
{

    @Override
    public void populate(final SearchResultValueData source, final ProductData target) throws ConversionException
    {
//        String modelo = getValue(source, "modelo");
//        String marca = getValue(source, "marca_string");
//
//        modelo = modelo != null ? modelo : "";
//        marca = marca != null ? marca : "";
//
//        target.setModelo(modelo);
//        target.setMarca(marca);
    }

    protected <T> T getValue(final SearchResultValueData source, final String propertyName)
    {
        if (source.getValues() == null)
        {
            return null;
        }
        // DO NOT REMOVE the cast (T) below, while it should be unnecessary it is required by the javac compiler
        return (T) source.getValues().get(propertyName);
    }
}
