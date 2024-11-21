package mx.com.telcel.facades.populators;

import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;



import static de.hybris.platform.util.localization.Localization.getLocalizedString;


public class TelcelOrderHistoryPopulator implements Populator<OrderModel, OrderHistoryData> {
    private static final Logger LOG = LoggerFactory.getLogger(TelcelOrderHistoryPopulator.class);
    private static final String STATUS_PROCESSING_ORDER_HISTORY = "processes.list.item.processing";
    private EnumerationService enumerationService;
    private ConfigurationService configurationService;

    @Override
    public void populate(OrderModel source, OrderHistoryData target) throws ConversionException {

        if(source.getStatusDisplay().contains("processing")){
            target.setStatusDisplay(getLocalizedString(STATUS_PROCESSING_ORDER_HISTORY));
        }else{
            LOG.info("OrderCode: " + target.getCode() + " Status OrderHistory:: " + target.getStatusDisplay());
        }
    }

    public EnumerationService getEnumerationService() {
        return enumerationService;
    }

    @Required
    public void setEnumerationService(EnumerationService enumerationService) {
        this.enumerationService = enumerationService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }
    @Required
    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
