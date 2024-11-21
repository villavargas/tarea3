package mx.com.telcel.facades.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import mx.com.telcel.core.model.EsquemaCobroModel;
import mx.com.telcel.facades.esquemacobro.data.TelcelEsquemaCobroDTO;
import org.apache.log4j.Logger;

public class TelcelEsquemaCobroPopulator implements Populator<EsquemaCobroModel, TelcelEsquemaCobroDTO> {

    private static final Logger LOG = Logger.getLogger(TelcelEsquemaCobroPopulator.class);

    @Override
    public void populate(EsquemaCobroModel source, TelcelEsquemaCobroDTO target) throws ConversionException {
        target.setCode(source.getCode());
        target.setName(source.getName());
        target.setDescription(source.getDescription());
    }
}
