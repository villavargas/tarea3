package mx.com.telcel.facades.populators;

import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cmsfacades.data.AbstractPageData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.HashMap;
import java.util.Map;

public class TelcelPageModelToDataRenderingPopulator implements Populator<AbstractPageModel, AbstractPageData> {

    public static final String ANALYTIC_JSON = "analyticJson";
    public static final String PAGINA = "pagina";

    @Override
    public void populate(AbstractPageModel source, AbstractPageData target) throws ConversionException {
        String json = source.getAnalyticJson() != null ? source.getAnalyticJson() : "";
        String pagina = source.getPagina() != null ? source.getPagina() : "";
        target.setAnalyticJson( json );
        target.setPagina( pagina );

        populateOtherProperties(target);
    }

    protected void populateOtherProperties(final AbstractPageData targetData)
    {
        final Map<String, Object> otherProperties = new HashMap<>();
        String value = targetData.getAnalyticJson().isEmpty() ? "EMPTY" : targetData.getAnalyticJson();
        String pagina = targetData.getPagina().isEmpty() ? "EMPTY" : targetData.getPagina();
        otherProperties.put(ANALYTIC_JSON, value);
        otherProperties.put(PAGINA, pagina);

        if (!targetData.getOtherProperties().isEmpty()) {
            targetData.getOtherProperties().putAll(otherProperties);
        } else {
            targetData.setOtherProperties(otherProperties);
        }
    }
}
