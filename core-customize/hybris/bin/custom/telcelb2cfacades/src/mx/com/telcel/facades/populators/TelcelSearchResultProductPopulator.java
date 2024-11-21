/*
 * [y] hybris Platform
 *
 * Copyright (c) 2021 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package mx.com.telcel.facades.populators;

import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;


/**
 * The type Telcel search result product populator.
 */
public class TelcelSearchResultProductPopulator implements Populator<SearchResultValueData, ProductData>
{

	private static final Logger LOG = Logger.getLogger(TelcelSearchResultProductPopulator.class);

	@Override
	public void populate(final SearchResultValueData source, final ProductData target) throws ConversionException
	{
		final ArrayList includeGiftList = this.getValue(source, "includeGift");
		final ArrayList tipoTerminalList = this.getValue(source, "tipoTerminal");
		final ArrayList sectorList = this.getValue(source, "sector");
		final ArrayList variantMatrixList = this.getValue(source, "variantMatrix");
		final String sku = this.getValue(source, "sku");
		final ArrayList modeloList = this.getValue(source, "modelo");
		final ArrayList volteList = this.getValue(source, "volte");
		final ArrayList buenFinList = this.getValue(source, "buenfin");
		final ArrayList proximamenteList = this.getValue(source, "proximamente");

		final ArrayList lanzamientoList = this.getValue(source, "lanzamiento");
		final ArrayList promocionList = this.getValue(source, "promocion");
		final ArrayList preventaList = this.getValue(source, "preventa");
		final ArrayList hotsaleList = this.getValue(source, "hotsale");
		final ArrayList tipoActivacionList = this.getValue(source, "tipoActivacion");

		final ArrayList colorList = this.getValue(source, "color");

		final ArrayList categoryNameList = this.getValue(source, "categoryName");
		final ArrayList categoryCodeList = this.getValue(source, "category");
		final ArrayList colorNameList = this.getValue(source, "colorName");

		final ArrayList nuevoList = this.getValue(source, "nuevo");
		final String pdpUrl = this.getValue(source, "pdpUrl");
		final String marca = this.getValue(source, "marca");


		final Boolean includeGift = (CollectionUtils.isNotEmpty(includeGiftList) && !includeGiftList.isEmpty())
				? (Boolean) includeGiftList.get(0)
				: false;

		final String volte = (CollectionUtils.isNotEmpty(volteList) && !volteList.isEmpty()) ? (String) volteList.get(0) : null;

		final Boolean buenfin = (CollectionUtils.isNotEmpty(buenFinList) && !buenFinList.isEmpty()) ? (Boolean) buenFinList.get(0)
				: false;

		final Boolean proximamente = (CollectionUtils.isNotEmpty(proximamenteList) && !proximamenteList.isEmpty())
				? (Boolean) proximamenteList.get(0)
				: false;

		final Boolean nuevo = (CollectionUtils.isNotEmpty(nuevoList) && !nuevoList.isEmpty()) ? (Boolean) nuevoList.get(0) : false;

		final Boolean lanzamiento = (CollectionUtils.isNotEmpty(lanzamientoList) && !lanzamientoList.isEmpty())
				? (Boolean) lanzamientoList.get(0)
				: false;

		final Boolean promocion = (CollectionUtils.isNotEmpty(promocionList) && !promocionList.isEmpty())
				? (Boolean) promocionList.get(0)
				: false;

		final Boolean preventa = (CollectionUtils.isNotEmpty(preventaList) && !preventaList.isEmpty())
				? (Boolean) preventaList.get(0)
				: false;

		final Boolean hotsale = (CollectionUtils.isNotEmpty(hotsaleList) && !hotsaleList.isEmpty()) ? (Boolean) hotsaleList.get(0)
				: false;

		final Boolean tipoActivacion = (CollectionUtils.isNotEmpty(tipoActivacionList) && !tipoActivacionList.isEmpty())
				? (Boolean) tipoActivacionList.get(0)
				: false;

		final String tipoTerminal = (CollectionUtils.isNotEmpty(tipoTerminalList) && !tipoTerminalList.isEmpty())
				? (String) tipoTerminalList.get(0)
				: null;

		final String colorName = (CollectionUtils.isNotEmpty(colorNameList) && !colorNameList.isEmpty())
				? (String) colorNameList.get(0)
				: null;

		final String sector = (CollectionUtils.isNotEmpty(sectorList) && !sectorList.isEmpty()) ? (String) sectorList.get(0) : null;

		final String variantMatrix = (CollectionUtils.isNotEmpty(variantMatrixList) && !variantMatrixList.isEmpty())
				? (String) variantMatrixList.get(0)
				: null;

		final String modelo = (CollectionUtils.isNotEmpty(modeloList) && !modeloList.isEmpty()) ? (String) modeloList.get(0) : null;

		final String color = (CollectionUtils.isNotEmpty(colorList) && !colorList.isEmpty()) ? (String) colorList.get(0) : null;

		final CategoryData lastCategory = new CategoryData();
		lastCategory.setCode((String) categoryCodeList.get(0));
		target.setCategories(Arrays.asList(lastCategory));

		target.setCategoryName((String) categoryNameList.get(0));
		target.setIncludeGift(includeGift);
		target.setIsFreeShipping("SMARTPHONE".equalsIgnoreCase(tipoTerminal));
		target.setPlaceholder(sector);
		target.setPdpUrl(pdpUrl);
		target.setVariants(variantMatrix.replace("\"", "\'")); // NOSONAR
		target.setSku(sku);
		target.setModelo(modelo);
		target.setMarca(marca);
		target.setBuenFin(buenfin);
		target.setVolte(volte);
		target.setProximamente(proximamente);
		target.setNuevo(nuevo);
		target.setHotsale(hotsale);
		target.setLanzamiento(lanzamiento);
		target.setPreventa(preventa);
		target.setPromocion(promocion);
		target.setTipoActivacion(tipoActivacion);
		target.setColor(color);

		target.setNombreComercial(getFirstOrString(source, "nombreComercial"));

		target.setColorName(colorName);

		final ArrayList samePriceList = this.getValue(source, "samePrice");

		final Boolean samePrice = (CollectionUtils.isNotEmpty(samePriceList) && !samePriceList.isEmpty())
				? (Boolean) samePriceList.get(0)
				: false;
		target.setSamePrice(samePrice);

		final ArrayList chipNameList = this.getValue(source, "chipName");
		final String chipName = (CollectionUtils.isNotEmpty(chipNameList) && !chipNameList.isEmpty()) ? (String) chipNameList.get(0) : null;
		target.setChipName(chipName);
	}

	private String getFirstOrString(final SearchResultValueData source, final String propertyName)
	{
		try
		{
			final ArrayList nombreComercialList = this.getValue(source, propertyName);
			return (CollectionUtils.isNotEmpty(nombreComercialList)) ? (String) nombreComercialList.get(0) : null;
		}
		catch (final ClassCastException ex)
		{
			return this.getValue(source, propertyName);
		}
	}


	/**
	 * Gets value.
	 *
	 * @param <T>
	 *           the type parameter
	 * @param source
	 *           the source
	 * @param propertyName
	 *           the property name
	 * @return the value
	 */
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
