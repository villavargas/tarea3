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
package mx.com.telcel.facades.mappers;

import de.hybris.platform.b2ctelcocommercewebservicescommons.mappers.productoffering.TmaProductSpecificationAttributeMapper;
import de.hybris.platform.b2ctelcofacades.data.TmaComponentProdOfferPriceData;
import de.hybris.platform.b2ctelcofacades.price.TmaPriceValueFormatter;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.commercewebservicescommons.dto.product.ProductWsDTO;
import de.hybris.platform.util.Config;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Objects;
import org.apache.log4j.Logger;

/**
 * The type Telcel tma product specification attribute mapper.
 */
public class TelcelTmaProductSpecificationAttributeMapper extends TmaProductSpecificationAttributeMapper {
    private static final Logger LOG = Logger.getLogger(TelcelTmaProductSpecificationAttributeMapper.class);

    private MapperFacade mapperFacade;
    private static final String PRICE_PER_MONTH_PROPERTY = "telcel.price.per.month.months.number";
    private static final Double PRICE_PER_MONTH_DEFAULT_VALUE = 13D;
    private final DecimalFormat decimalFormat = new DecimalFormat("0.00");

    private CommerceCommonI18NService commerceCommonI18NService;
    private TmaPriceValueFormatter priceValueFormatter;

    @Override
    public void populateTargetAttributeFromSource(final ProductData source, final ProductWsDTO target,
                                                  final MappingContext context)
    {
        Assert.notNull(source, "Parameter source cannot be null.");
        Assert.notNull(target, "Parameter target cannot be null.");

        super.populateTargetAttributeFromSource(source,target,context);
        target.setPlaceholder(source.getPlaceholder());
        target.setIncludeGift(source.isIncludeGift());
        target.setDescripcionIncludeGift(source.getDescripcionIncludeGift());
        target.setAvailableInRegion(source.isAvailableInRegion());
        target.setIsFreeShipping(source.isIsFreeShipping());
        target.setVariants(source.getVariants());
        target.setSku(source.getSku());
        target.setPdpUrl(source.getPdpUrl());

        populateVariant(source,target);
    }

    private void populateVariant(ProductData source, ProductWsDTO target) {
        target.setDefaultTelcel(source.isDefaultTelcel());
        target.setLanzamiento(source.isLanzamiento());
        target.setPromocion(source.isPromocion());
        target.setPreventa(source.isPreventa());
        target.setHotsale(source.isHotsale());
        target.setBuenFin(source.isBuenFin());
        target.setProximamente(source.isProximamente());
        target.setNuevo(source.isNuevo());
        target.setVolte(source.getVolte());
        target.setComercialName(source.getComercialName());
        target.setSitioOficial(source.getSitioOficial());
        target.setDimensiones(source.getDimensiones());
        target.setPesoGramos(source.getPesoGramos());
        target.setDuracionBateriaCC(source.getDuracionBateriaCC());
        target.setDuracionBateriaStandby(source.getDuracionBateriaStandby());
        target.setDuracionBateriaLC(source.getDuracionBateriaLC());
        target.setPantalla(source.getPantalla());
        target.setCamaraTrasera(source.getCamaraTrasera());
        target.setCamaraFrontal(source.getCamaraFrontal());
        target.setMemoria(source.getMemoria());
        target.setProcesador(source.getProcesador());
        target.setProcesadorMarca(source.getProcesadorMarca());
        target.setProcesadorModelo(source.getProcesadorModelo());
        target.setProcesadorVelocidad(source.getProcesadorVelocidad());
        target.setConexionWifi(source.isConexionWifi());
        target.setConexionBluetooth(source.isConexionBluetooth());
        target.setConexionNfc(source.isConexionNfc());
        target.setConexionUsb(source.isConexionUsb());
        target.setGigaRed(source.getGigaRed());
        target.setGigaRedSoportado(source.isGigaRedSoportado());
        target.setAccesorios(source.getAccesorios());
        target.setAltavoz(source.getAltavoz());
        target.setClienteCorreo(source.getClienteCorreo());
        target.setMicrofono(source.getMicrofono());
        target.setModem(source.getModem());
        target.setRanura(source.getRanura());
        target.setRanuraMicroSD(source.getRanuraMicroSD());
        target.setRequisitosSistema(source.getRequisitosSistema());
        target.setResolucion(source.getResolucion());
        target.setSensor(source.getSensor());
        target.setModemUsb(source.getModemUsb());
        target.setDescripcionSeo(source.getDescripcionSeo());
        target.setTituloSeo(source.getTituloSeo());
        target.setSlogan(source.getSlogan());
        target.setCeMotriz(source.isCeMotriz());
        target.setCeAuditiva(source.isCeAuditiva());
        target.setCeVisual(source.isCeVisual());
        target.setTieneLiga(source.isTieneLiga());
        target.setTipoSim(source.getTipoSim());
        target.setSistemaOperativo(source.getSistemaOperativo());
        target.setSistemaOperativoVersion(source.getSistemaOperativoVersion());
        target.setRegion(source.getRegion());
        target.setSector(source.getSector());
        target.setMarca(source.getMarca());
        target.setColorData(source.getColorData());
        target.setActivable(source.getActivable());
        target.setModelo(source.getModelo());
        target.setTipoActivacion(source.isTipoActivacion());
        target.setColor(source.getColor());
        target.setCategoryName(source.getCategoryName());
        target.setNombreComercial(source.getNombreComercial());
        target.setColorName(source.getColorName());
        target.setSamePrice(source.isSamePrice());
        populatePricePerMonth(source, target);
        target.setChipName(source.getChipName());
    }

    private void populatePricePerMonth(ProductData source, ProductWsDTO target) {
        final PriceData priceData = source.getPrice() != null ? source.getPrice() :
                CollectionUtils.isNotEmpty(source.getPrices()) ? source.getPrices().get(0) : null;
        if (Objects.nonNull(priceData) && Objects.nonNull(priceData.getProductOfferingPrice())) {
            if (priceData.getProductOfferingPrice() instanceof TmaComponentProdOfferPriceData) {
                final TmaComponentProdOfferPriceData prodOfferPriceData = (TmaComponentProdOfferPriceData) priceData.getProductOfferingPrice();
                final Double value = prodOfferPriceData.getValue();
               // Before change
                /*if (value != null && Double.compare(value, 0D) > 0) {
                    target.setPricePerMonth(prodOfferPriceData.getCurrency().getSymbol() + decimalFormat.format(value / Config.getDouble(PRICE_PER_MONTH_PROPERTY, PRICE_PER_MONTH_DEFAULT_VALUE)));
                }*/

                // After Change, new Format to Price Per Month
                if (value != null && Double.compare(value, 0D) > 0) {
                    BigDecimal prodOfferPrice = new BigDecimal(value / Config.getDouble(PRICE_PER_MONTH_PROPERTY, PRICE_PER_MONTH_DEFAULT_VALUE));
                    target.setPricePerMonth(getPriceValueFormatter().formatPriceValue(prodOfferPrice,getCommerceCommonI18NService().getCurrentCurrency()));
                }
            }
        }
    }

    @Override
    public MapperFacade getMapperFacade() {
        return mapperFacade;
    }

    @Override
    public void setMapperFacade(MapperFacade mapperFacade) {
        this.mapperFacade = mapperFacade;
    }

    public CommerceCommonI18NService getCommerceCommonI18NService() {
        return commerceCommonI18NService;
    }
    @Required
    public void setCommerceCommonI18NService(CommerceCommonI18NService commerceCommonI18NService) {
        this.commerceCommonI18NService = commerceCommonI18NService;
    }

    public TmaPriceValueFormatter getPriceValueFormatter() {
        return priceValueFormatter;
    }
    @Required
    public void setPriceValueFormatter(TmaPriceValueFormatter priceValueFormatter) {
        this.priceValueFormatter = priceValueFormatter;
    }
}
