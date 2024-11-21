package mx.com.telcel.facades.product.impl;

import de.hybris.platform.b2ctelcofacades.data.ProductOfferingSearchContextData;
import de.hybris.platform.b2ctelcofacades.data.TmaOneTimeProdOfferPriceChargeData;
import de.hybris.platform.b2ctelcofacades.product.impl.DefaultTmaProductFacade;
import de.hybris.platform.b2ctelcoservices.services.TmaPoService;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.Comparator;
import java.util.List;

import javax.annotation.Resource;

import mx.com.telcel.core.model.AdditionalServiceEntryModel;
import mx.com.telcel.core.model.TelcelAdditionalServiceProductOfferingModel;
import mx.com.telcel.core.services.TelcelPoService;
import mx.com.telcel.facades.product.TelcelProductFacade;
import mx.com.telcel.facades.product.data.AdditionalServiceDataList;


public class DefaultTelcelProductFacade extends DefaultTmaProductFacade implements TelcelProductFacade
{

	TelcelPoService telcelPoService;

	@Resource(name = "defaultConfigurationService")
	private ConfigurationService configurationService;

	public DefaultTelcelProductFacade(final TmaPoService tmaPoService)
	{
		super(tmaPoService);
	}


	@Override
	public AdditionalServiceDataList getAdditionalServicesPOByType(final String type,
			final ProductOfferingSearchContextData contextData)
	{
		final AdditionalServiceDataList dataList = getTelcelPoService().getAdditionalServicesPOByType(type, contextData);
		dataList.getAdditionalServices().sort(
				Comparator.comparing(r -> r.getPrices().get(0).getProductOfferingPrice() instanceof TmaOneTimeProdOfferPriceChargeData
						? ((TmaOneTimeProdOfferPriceChargeData) r.getPrices().get(0).getProductOfferingPrice()).getValue().intValue()
						: 0));

		return dataList;
	}

	@Override
	public void setProductDataPaqueteAmigo(final ProductData productData, final List<AdditionalServiceEntryModel> asel)
	{
		productData.setPaqueteAmigo("");
		productData.setVigenciaPaqueteAmigo("");
		for (final AdditionalServiceEntryModel ase : asel)
		{
			if (ase.getRejected().equals(Boolean.FALSE))
			{
				final TelcelAdditionalServiceProductOfferingModel asep = ase.getAdditionalServiceProduct();
				if (asep.getCode().contains("recarga"))
				{
					try
					{
						final String name = asep.getName();
						productData.setPaqueteAmigo(name == null ? "" : name);
					}
					catch (final Exception e)
					{
						final String name = asep.getName();
						productData.setPaqueteAmigo(name == null ? "" : name);
					}
				}
				if (asep.getCode().contains("100"))
				{
					final String vigencia100 = configurationService.getConfiguration().getString("telcel.paquete.amigo100");
					productData.setVigenciaPaqueteAmigo(vigencia100);
				}
				if (asep.getCode().contains("50"))
				{
					final String vigencia50 = configurationService.getConfiguration().getString("telcel.paquete.amigo50");
					productData.setVigenciaPaqueteAmigo(vigencia50);
				}
			}
		}
	}

	public TelcelPoService getTelcelPoService()
	{
		return telcelPoService;
	}

	public void setTelcelPoService(final TelcelPoService telcelPoService)
	{
		this.telcelPoService = telcelPoService;
	}
}
