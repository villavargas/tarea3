/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.facades.product;

import de.hybris.platform.b2ctelcofacades.data.ProductOfferingSearchContextData;
import de.hybris.platform.b2ctelcofacades.product.TmaProductFacade;
import de.hybris.platform.commercefacades.product.data.ProductData;

import java.util.List;

import mx.com.telcel.core.model.AdditionalServiceEntryModel;
import mx.com.telcel.facades.product.data.AdditionalServiceDataList;


public interface TelcelProductFacade extends TmaProductFacade
{
	AdditionalServiceDataList getAdditionalServicesPOByType(String type, ProductOfferingSearchContextData contextData);

	void setProductDataPaqueteAmigo(ProductData productData, List<AdditionalServiceEntryModel> asel);

}
