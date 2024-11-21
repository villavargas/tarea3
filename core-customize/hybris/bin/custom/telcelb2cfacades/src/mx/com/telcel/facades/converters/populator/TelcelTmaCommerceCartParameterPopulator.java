package mx.com.telcel.facades.converters.populator;

import de.hybris.platform.b2ctelcofacades.data.CartActionInput;
import de.hybris.platform.b2ctelcofacades.order.converters.populator.TmaCommerceCartParameterPopulator;
import de.hybris.platform.b2ctelcoservices.enums.TmaPlaceRoleType;
import de.hybris.platform.b2ctelcoservices.model.TmaOneTimeProdOfferPriceChargeModel;
import de.hybris.platform.b2ctelcoservices.model.TmaProductOfferingModel;
import de.hybris.platform.b2ctelcoservices.order.TmaAbstractOrderEntryService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commerceservices.service.data.AsEntryParameter;

import de.hybris.platform.order.DeliveryModeService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import mx.com.telcel.core.model.EsquemaCobroModel;
import mx.com.telcel.core.model.TelcelAdditionalServiceProductOfferingModel;
import mx.com.telcel.core.services.TelcelEsquemasCobroService;
import mx.com.telcel.core.services.TelcelPoService;
import mx.com.telcel.facades.order.data.AdditionalServiceEntryData;
import mx.com.telcel.facades.product.data.AdditionalServiceData;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

public class TelcelTmaCommerceCartParameterPopulator extends TmaCommerceCartParameterPopulator{

    private static final Logger LOG = LoggerFactory.getLogger(TelcelTmaCommerceCartParameterPopulator.class);
    private TelcelEsquemasCobroService esquemasCobroService;
    private TelcelPoService telcelPoService;

    public TelcelTmaCommerceCartParameterPopulator(final Map<TmaPlaceRoleType, Converter> placeConverters,
                                             final DeliveryModeService deliveryModeService, final ModelService modelService,
                                             final Converter<CartActionInput, CommerceCartParameter> commerceCartParameterConverter,
                                             final TmaAbstractOrderEntryService abstractOrderEntryService)
    {
        super(placeConverters,deliveryModeService,modelService,commerceCartParameterConverter,abstractOrderEntryService);
    }

    @Override
    public void populate(final CartActionInput cartActionInput, final CommerceCartParameter commerceCartParameter) throws ConversionException
    {
        validateParameterNotNullStandardMessage("source", cartActionInput);
        validateParameterNotNullStandardMessage("target", commerceCartParameter);

        super.populate(cartActionInput, commerceCartParameter);
        commerceCartParameter.setEntryQtyPos(cartActionInput.getEntryQtyPos());
        if (cartActionInput.getEsquemaCobro()!=null) {
            String code = cartActionInput.getEsquemaCobro().getCode();
            if (StringUtils.isNotBlank(code)) {
                EsquemaCobroModel esquemaCobroModel = getEsquemasCobroService().getByCode( code );
                validateParameterNotNull(esquemaCobroModel,"No se encontró información del esquema seleccionado");

                commerceCartParameter.setEsquemaCobro( esquemaCobroModel );

            }
        }
        populateAdditionalServiceEntry(cartActionInput, commerceCartParameter);
    }

    private void populateAdditionalServiceEntry(final CartActionInput cartActionInput, final CommerceCartParameter commerceCartParameter)
    {
        //AdditionalService entry information for update/delete entry
        AdditionalServiceEntryData asEntryData = cartActionInput.getAdditionalServiceEntry();
        if(Objects.nonNull(asEntryData))
        {
            commerceCartParameter.setAsEntryNumber(asEntryData.getEntryNumber());
            commerceCartParameter.setAsRejected(asEntryData.getRejected());
            commerceCartParameter.setAsEntryProduct(getAsProductOffering(asEntryData.getAdditionalServiceProduct()));
        }

        //AdditionalService entries for creation of an entry with specific additional services. validation of EesquemaCobro must be apply on the strategy/service not in populators.
        if(CollectionUtils.isNotEmpty(cartActionInput.getAdditionalServiceEntries()))
        {
            List<AsEntryParameter> asEntriesParameter = new ArrayList<>();
            cartActionInput.getAdditionalServiceEntries().forEach(ase->{
                AsEntryParameter asEntryParameter = new AsEntryParameter();
                asEntryParameter.setAsEntryNumber(ase.getEntryNumber());
                asEntryParameter.setAsRejected(ase.getRejected());
                asEntryParameter.setAsEntryProduct(getAsProductOffering(ase.getAdditionalServiceProduct()));
                if(Objects.nonNull(asEntryParameter.getAsEntryProduct())
                        && CollectionUtils.isNotEmpty(asEntryParameter.getAsEntryProduct().getEurope1Prices()))
                {
                    asEntryParameter.setBasePrice(((TmaOneTimeProdOfferPriceChargeModel)
                            asEntryParameter.getAsEntryProduct().getEurope1Prices().iterator().next().getProductOfferingPrice()).getValue());
                }
                asEntriesParameter.add(asEntryParameter);
            });
            commerceCartParameter.setAdditionalServiceEntries(asEntriesParameter);
        }
    }

    private TelcelAdditionalServiceProductOfferingModel getAsProductOffering(final AdditionalServiceData asData)
    {
        if(Objects.nonNull(asData) && StringUtils.isNotBlank(asData.getCode()))
        {
            TmaProductOfferingModel po = getTelcelPoService().getPoForCode(asData.getCode());
            if(Objects.nonNull(po) && po instanceof TelcelAdditionalServiceProductOfferingModel)
            {
                return (TelcelAdditionalServiceProductOfferingModel) po;
            }
        }

        LOG.warn("There is no Additional Service Product Offering with "+ asData.getCode());
        return null;
    }

    protected TelcelEsquemasCobroService getEsquemasCobroService() {
        return esquemasCobroService;
    }

    public void setEsquemasCobroService(TelcelEsquemasCobroService esquemasCobroService) {
        this.esquemasCobroService = esquemasCobroService;
    }

    protected TelcelPoService getTelcelPoService() {
        return telcelPoService;
    }

    public void setTelcelPoService(TelcelPoService telcelPoService) {
        this.telcelPoService = telcelPoService;
    }
}
