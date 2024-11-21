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

import de.hybris.platform.b2ctelcoservices.model.TmaPoVariantModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.ClassificationData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.util.Config;
import mx.com.telcel.core.model.*;
import mx.com.telcel.core.url.impl.TelcelProductModelUrlResolver;
import mx.com.telcel.core.util.TelcelUtil;
import mx.com.telcel.facades.product.data.ColorTelcelData;
import mx.com.telcel.facades.product.data.StorageTelcelData;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import java.util.*;

/**
 * The type Telcel product populator.
 */
public class TelcelProductPopulator implements Populator<ProductModel, ProductData> {

    private static final Logger LOG = Logger.getLogger(TelcelProductPopulator.class);
    private static final String ROOT_CATEGORY_CODE = "1";

    @Resource
    private TelcelUtil telcelUtil; // NOSONAR

    private TelcelProductModelUrlResolver productModelUrlResolver;

    public static final String SMARTPHONE = "smartphone";
    public static final Integer PRODUCT_Qty = 1 ;
    public static final String BASE_STORE = "telcel" ;

    @Override
    public void populate(ProductModel source, ProductData target) throws ConversionException
    {
        Assert.notNull(source, "Parameter source cannot be null.");
        Assert.notNull(target, "Parameter target cannot be null.");

        target.setSku(source.getSku());
        target.setDescripcionSeo(source.getDescripcionSeo());
        target.setTituloSeo(source.getTituloSeo());
        target.setActivable(source.getActivable());
        target.setTipoActivacion(source.getTipoActivacion());
        populateProductOffering(source, target);
        populateVariant(source, target);
        if (source.getItemtype().equals("TelcelPoVariant")) {
            TelcelPoVariantModel variante =  (TelcelPoVariantModel) source;
            if (variante!=null) {
                ColorModel colorModel = variante.getColor();
                if (colorModel !=null) {
                    target.setColor(colorModel.getName());
                    target.setColorName(colorModel.getName());
                } else {
                    target.setColor("");
                }
                String nombreComercial = ((TelcelPoVariantModel) source).getComercialName();
                nombreComercial = nombreComercial !=null ? nombreComercial : "";
                target.setNombreComercial(nombreComercial);

            }
			StorageModel storageModel = ((TelcelPoVariantModel) source).getStorage();
			if (storageModel!=null) {
				StorageUnitModel unitModel = storageModel.getStorageUnit();
				String unit = unitModel.getCode() != null ? unitModel.getCode() : "";
				String storageValue = storageModel.getStorageValue() != null ? storageModel.getStorageValue().toString() : "";
				StorageTelcelData storageTelcelData = new StorageTelcelData();
				storageTelcelData.setValue(storageValue);
				storageTelcelData.setUnit(unit);
				target.setCapacidad(storageTelcelData);
			} else {
				target.setCapacidad(null);
			}
            if (!source.getClassificationClasses().isEmpty()) {
                List<ClassificationData> classifications = new ArrayList<>();
                ClassificationClassModel classificationClassModel = source.getClassificationClasses().get(0);
                if (classificationClassModel != null) {
                    ClassificationData classificationData = new ClassificationData();
                    classificationData.setCode(classificationClassModel.getCode());
                    classificationData.setName(classificationClassModel.getName());

                    classifications.add(classificationData);
                }
                target.setClassifications(classifications);
            }
        }
        if(Objects.nonNull(source.getPicture()))
        {
            target.setPictureUrl(Config.getParameter(productModelUrlResolver.MEDIA_TELCEL_HTTPS)+source.getPicture().getURL());
        }
        //add Product To Cart Url
        //target.setAddProductToCartUrl(Config.getParameter(productModelUrlResolver.WEBSITE_TELCEL_HTTPS)+"/occ/v2/"+BASE_STORE+"/externalOCC/addCartEntry?productSku="+source.getSku()+"&entryNumber="+PRODUCT_Qty+"");
        target.setAddProductToCartUrl(Config.getParameter(productModelUrlResolver.WEBSITE_TELCEL_HTTPS)+"/telcelb2cwebservices/external/addCartEntry?productSku="+source.getSku()+"&entryNumber="+PRODUCT_Qty+"");



    }

    private void populateProductOffering(ProductModel productModel, ProductData productData) {

        if(productModel instanceof TelcelSimpleProductOfferingModel){
            TelcelSimpleProductOfferingModel telcelSimpleProductOfferingModel = (TelcelSimpleProductOfferingModel)productModel;

            if(Objects.nonNull(telcelSimpleProductOfferingModel) && StringUtils.isNotBlank(telcelSimpleProductOfferingModel.getMarca()))
            {
                productData.setMarca(telcelSimpleProductOfferingModel.getMarca());
            }

            productData.setModelo(telcelSimpleProductOfferingModel.getModelo());

            TipoTerminalModel tipoTerminalModel = telcelSimpleProductOfferingModel.getTipoTerminal();

            if(Objects.nonNull(tipoTerminalModel) && SMARTPHONE.equalsIgnoreCase(tipoTerminalModel.getCode() != null ? tipoTerminalModel.getCode(): Strings.EMPTY)){
                productData.setIsFreeShipping(true);
            }

            productData.setSamePrice(telcelSimpleProductOfferingModel.getSamePrice() != null ? telcelSimpleProductOfferingModel.getSamePrice() :false);

            Collection<TmaPoVariantModel> tmaPoVariantModelCollection = telcelSimpleProductOfferingModel.getTmaPoVariants();
            if(CollectionUtils.isNotEmpty(tmaPoVariantModelCollection)){
                TmaPoVariantModel tmaPoVariantModel = tmaPoVariantModelCollection.iterator().next();
                populateVariantBase(tmaPoVariantModel,productData);
            }
        }else{
            if(productModel instanceof TelcelPoVariantModel){
                TelcelPoVariantModel telcelPoVariantModel = (TelcelPoVariantModel) productModel;
                if(telcelPoVariantModel.getTmaBasePo() != null &&
                        telcelPoVariantModel.getTmaBasePo() instanceof TelcelSimpleProductOfferingModel){
                    TelcelSimpleProductOfferingModel telcelSimpleProductOfferingModel = (TelcelSimpleProductOfferingModel)telcelPoVariantModel.getTmaBasePo();
                    if(Objects.nonNull(telcelSimpleProductOfferingModel) && StringUtils.isNotBlank(telcelSimpleProductOfferingModel.getMarca()))
                    {
                        productData.setMarca(telcelSimpleProductOfferingModel.getMarca());
                    }

                    productData.setModelo(telcelSimpleProductOfferingModel.getModelo()); // NOSONAR

                    TipoTerminalModel tipoTerminalModel = telcelSimpleProductOfferingModel.getTipoTerminal();

                    if(Objects.nonNull(tipoTerminalModel) && SMARTPHONE.equalsIgnoreCase(tipoTerminalModel.getCode() != null ? tipoTerminalModel.getCode(): Strings.EMPTY)){
                        productData.setIsFreeShipping(true);
                    }

                    productData.setSamePrice(telcelPoVariantModel.getSamePrice() != null ? telcelPoVariantModel.getSamePrice() :false);
                }
            }
        }


    }

    private void populateVariant(ProductModel productModel,ProductData productData) {
        if(productModel instanceof TelcelPoVariantModel){
            TelcelPoVariantModel tmaPoVariantModel = (TelcelPoVariantModel)productModel;
            populateAttributeVariant(productData, tmaPoVariantModel);
        }
    }

    private void populateVariantBase(TmaPoVariantModel productModel,ProductData productData) {
        if(productModel instanceof TelcelPoVariantModel){
            TelcelPoVariantModel tmaPoVariantModel = (TelcelPoVariantModel)productModel;
            populateAttributeVariantBase(productData, tmaPoVariantModel);
        }
    }

    private void populateAttributeVariantBase(ProductData productData, TelcelPoVariantModel tmaPoVariantModel) {
        setSupportedBands(productData, tmaPoVariantModel);
        setRegionFlags(productData, tmaPoVariantModel);
        productData.setPlaceholder(getPlaceholder(tmaPoVariantModel));

        /**
         * set Sector
         */
        if(Objects.nonNull(tmaPoVariantModel) && StringUtils.isNotBlank(tmaPoVariantModel.getSector())){
            productData.setSector(tmaPoVariantModel.getSector());
        }

        /**
         * set Region
         */
        if(Objects.nonNull(tmaPoVariantModel) && Objects.nonNull(tmaPoVariantModel.getRegion())){
            productData.setRegion(tmaPoVariantModel.getRegion().getName());
        }




        /**
         * set Sistema Operativo
         */
        if(Objects.nonNull(tmaPoVariantModel) && StringUtils.isNotBlank(tmaPoVariantModel.getSistemaOperativo()))
        {
            productData.setSistemaOperativo(tmaPoVariantModel.getSistemaOperativo());
        }

        /**
         * set Sistema Operativo Version
         */
        if(Objects.nonNull(tmaPoVariantModel) && StringUtils.isNotBlank(tmaPoVariantModel.getSistemaOperativoVersion()))
        {
            productData.setSistemaOperativoVersion(tmaPoVariantModel.getSistemaOperativoVersion());
        }


        /**
         * set Tipo sim
         */
        if(Objects.nonNull(tmaPoVariantModel) && StringUtils.isNotBlank(tmaPoVariantModel.getTipoSim())
        ){
            productData.setTipoSim(tmaPoVariantModel.getTipoSim());
        }

        productData.setDefaultTelcel(tmaPoVariantModel.getDefaultTelcel()!= null ? tmaPoVariantModel.getDefaultTelcel() : false);
        productData.setLanzamiento(tmaPoVariantModel.getLanzamiento()!= null ? tmaPoVariantModel.getLanzamiento() : false);
        productData.setPromocion(tmaPoVariantModel.getPromocion()!= null ? tmaPoVariantModel.getPromocion() : false);
        productData.setPreventa(tmaPoVariantModel.getPreventa()!= null ? tmaPoVariantModel.getPreventa() : false);
        productData.setHotsale(tmaPoVariantModel.getHotsale()!= null ? tmaPoVariantModel.getHotsale() : false);
        productData.setBuenFin(tmaPoVariantModel.getBuenFin()!= null ? tmaPoVariantModel.getBuenFin() : false);
        productData.setProximamente(tmaPoVariantModel.getProximamente()!= null ? tmaPoVariantModel.getProximamente() : false);
        productData.setNuevo(tmaPoVariantModel.getNuevo()!= null ? tmaPoVariantModel.getNuevo() : false);
        productData.setVolte(tmaPoVariantModel.getVolte());
        productData.setComercialName(tmaPoVariantModel.getComercialName());
        productData.setSitioOficial(tmaPoVariantModel.getSitioOficial());
        productData.setDimensiones(tmaPoVariantModel.getDimensiones());
        productData.setPesoGramos(tmaPoVariantModel.getPesoGramos());
        productData.setDuracionBateriaCC(tmaPoVariantModel.getDuracionBateriaCC());
        productData.setDuracionBateriaStandby(tmaPoVariantModel.getDuracionBateriaStandby());
        productData.setDuracionBateriaLC(tmaPoVariantModel.getDuracionBateriaLC());
        productData.setPantalla(tmaPoVariantModel.getPantalla());
        productData.setCamaraTrasera(tmaPoVariantModel.getCamaraTrasera());
        productData.setCamaraFrontal(tmaPoVariantModel.getCamaraFrontal());
        productData.setMemoria(tmaPoVariantModel.getMemoria());
        productData.setProcesador(tmaPoVariantModel.getProcesador());
        productData.setProcesadorMarca(tmaPoVariantModel.getProcesadorMarca());
        productData.setProcesadorModelo(tmaPoVariantModel.getProcesadorModelo());
        productData.setProcesadorVelocidad(tmaPoVariantModel.getProcesadorVelocidad());
        productData.setConexionWifi(tmaPoVariantModel.getConexionWifi()!=null ? tmaPoVariantModel.getConexionWifi() : false);
        productData.setConexionBluetooth(tmaPoVariantModel.getConexionBluetooth()!=null ? tmaPoVariantModel.getConexionBluetooth() : false);
        productData.setConexionNfc(tmaPoVariantModel.getConexionNfc()!=null ? tmaPoVariantModel.getConexionNfc() : false);
        productData.setConexionUsb(tmaPoVariantModel.getConexionUsb()!=null ? tmaPoVariantModel.getConexionUsb() : false);
        productData.setGigaRed(tmaPoVariantModel.getGigaRed());
        productData.setGigaRedSoportado(tmaPoVariantModel.getGigaRedSoportado());
        productData.setAccesorios(tmaPoVariantModel.getAccesorios());
        productData.setAltavoz(tmaPoVariantModel.getAltavoz());
        productData.setClienteCorreo(tmaPoVariantModel.getClienteCorreo());
        productData.setMicrofono(tmaPoVariantModel.getMicrofono());
        productData.setModem(tmaPoVariantModel.getModem());
        productData.setRanura(tmaPoVariantModel.getRanura());
        productData.setRanuraMicroSD(tmaPoVariantModel.getRanuraMicroSD());
        productData.setRequisitosSistema(tmaPoVariantModel.getRequisitosSistema());
        productData.setResolucion(tmaPoVariantModel.getResolucion());
        productData.setSensor(tmaPoVariantModel.getSensor());
        productData.setModemUsb(tmaPoVariantModel.getModemUsb());
        productData.setSlogan(tmaPoVariantModel.getSlogan());
        productData.setCeMotriz(tmaPoVariantModel.getCeMotriz() != null ? tmaPoVariantModel.getCeMotriz() : false);
        productData.setCeAuditiva(tmaPoVariantModel.getCeAuditiva() != null ? tmaPoVariantModel.getCeAuditiva() : false);
        productData.setCeVisual(tmaPoVariantModel.getCeVisual() != null ? tmaPoVariantModel.getCeVisual() : false);
        productData.setTieneLiga(tmaPoVariantModel.getTieneLiga() != null ? tmaPoVariantModel.getTieneLiga() : false);
        productData.setChipName(getChipName(tmaPoVariantModel));
    }

    private String getChipName(TelcelPoVariantModel tmaPoVariantModel) {
        String result = "";
        if (tmaPoVariantModel.getChipAdditionalServiceProduct() != null) {
            result = tmaPoVariantModel.getChipAdditionalServiceProduct().getName();
        }
        return result;
    }

    private void populateAttributeVariant(ProductData productData, TelcelPoVariantModel tmaPoVariantModel) {
        setSupportedBands(productData, tmaPoVariantModel);
        setRegionFlags(productData, tmaPoVariantModel);
        productData.setPlaceholder(getPlaceholder(tmaPoVariantModel));

        /**
         * set Sector
         */
        if(Objects.nonNull(tmaPoVariantModel) && StringUtils.isNotBlank(tmaPoVariantModel.getSector())){
            productData.setSector(tmaPoVariantModel.getSector());
        }

        /**
         * set Region
         */
        if(Objects.nonNull(tmaPoVariantModel) && Objects.nonNull(tmaPoVariantModel.getRegion())){
            productData.setRegion(tmaPoVariantModel.getRegion().getName());
        }

        /**
         * set Storage
         */
        if(Objects.nonNull(tmaPoVariantModel) && Objects.nonNull(tmaPoVariantModel.getStorage())){
            productData.setStorage(tmaPoVariantModel.getStorage().getStorageValue().toString());
        }


        /**
         * set Sistema Operativo
         */
        if(Objects.nonNull(tmaPoVariantModel) && StringUtils.isNotBlank(tmaPoVariantModel.getSistemaOperativo()))
        {
            productData.setSistemaOperativo(tmaPoVariantModel.getSistemaOperativo());
        }

        /**
         * set Sistema Operativo Version
         */
        if(Objects.nonNull(tmaPoVariantModel) && StringUtils.isNotBlank(tmaPoVariantModel.getSistemaOperativoVersion()))
        {
            productData.setSistemaOperativoVersion(tmaPoVariantModel.getSistemaOperativoVersion());
        }


        /**
         * set Tipo sim
         */
        if(Objects.nonNull(tmaPoVariantModel) && StringUtils.isNotBlank(tmaPoVariantModel.getTipoSim())
        ){
            productData.setTipoSim(tmaPoVariantModel.getTipoSim());
        }


        /**
         * set Color
         */
        if(Objects.nonNull(tmaPoVariantModel) && Objects.nonNull(tmaPoVariantModel.getColor())
        ){
            ColorTelcelData colorTelcelData = new ColorTelcelData();
            colorTelcelData.setCode(tmaPoVariantModel.getColor().getCode());
            colorTelcelData.setName(tmaPoVariantModel.getColor().getName());
            colorTelcelData.setHexadecimal(tmaPoVariantModel.getColor().getHexCode());
            productData.setColorData(colorTelcelData);
        }

        productData.setDefaultTelcel(tmaPoVariantModel.getDefaultTelcel()!= null ? tmaPoVariantModel.getDefaultTelcel() : false);
        productData.setLanzamiento(tmaPoVariantModel.getLanzamiento()!= null ? tmaPoVariantModel.getLanzamiento() : false);
        productData.setPromocion(tmaPoVariantModel.getPromocion()!= null ? tmaPoVariantModel.getPromocion() : false);
        productData.setPreventa(tmaPoVariantModel.getPreventa()!= null ? tmaPoVariantModel.getPreventa() : false);
        productData.setHotsale(tmaPoVariantModel.getHotsale()!= null ? tmaPoVariantModel.getHotsale() : false);
        productData.setBuenFin(tmaPoVariantModel.getBuenFin()!= null ? tmaPoVariantModel.getBuenFin() : false);
        productData.setProximamente(tmaPoVariantModel.getProximamente()!= null ? tmaPoVariantModel.getProximamente() : false);
        productData.setNuevo(tmaPoVariantModel.getNuevo()!= null ? tmaPoVariantModel.getNuevo() : false);
        productData.setVolte(tmaPoVariantModel.getVolte());
        productData.setComercialName(tmaPoVariantModel.getComercialName());
        productData.setSitioOficial(tmaPoVariantModel.getSitioOficial());
        productData.setDimensiones(tmaPoVariantModel.getDimensiones());
        productData.setPesoGramos(tmaPoVariantModel.getPesoGramos());
        productData.setDuracionBateriaCC(tmaPoVariantModel.getDuracionBateriaCC());
        productData.setDuracionBateriaStandby(tmaPoVariantModel.getDuracionBateriaStandby());
        productData.setDuracionBateriaLC(tmaPoVariantModel.getDuracionBateriaLC());
        productData.setPantalla(tmaPoVariantModel.getPantalla());
        productData.setCamaraTrasera(tmaPoVariantModel.getCamaraTrasera());
        productData.setCamaraFrontal(tmaPoVariantModel.getCamaraFrontal());
        productData.setMemoria(tmaPoVariantModel.getMemoria());
        productData.setProcesador(tmaPoVariantModel.getProcesador());
        productData.setProcesadorMarca(tmaPoVariantModel.getProcesadorMarca());
        productData.setProcesadorModelo(tmaPoVariantModel.getProcesadorModelo());
        productData.setProcesadorVelocidad(tmaPoVariantModel.getProcesadorVelocidad());
        productData.setConexionWifi(tmaPoVariantModel.getConexionWifi()!=null ? tmaPoVariantModel.getConexionWifi() : false);
        productData.setConexionBluetooth(tmaPoVariantModel.getConexionBluetooth()!=null ? tmaPoVariantModel.getConexionBluetooth() : false);
        productData.setConexionNfc(tmaPoVariantModel.getConexionNfc()!=null ? tmaPoVariantModel.getConexionNfc() : false);
        productData.setConexionUsb(tmaPoVariantModel.getConexionUsb()!=null ? tmaPoVariantModel.getConexionUsb() : false);
        productData.setConexionWifiOffload(tmaPoVariantModel.getConexionWifiOffload()!=null ? tmaPoVariantModel.getConexionWifiOffload() : false);
        productData.setConexionHdmi(tmaPoVariantModel.getConexionHdmi()!=null ? tmaPoVariantModel.getConexionHdmi() : false);
        productData.setConexionTvout(tmaPoVariantModel.getConexionTvout()!=null ? tmaPoVariantModel.getConexionTvout() : false);
        productData.setGigaRed(tmaPoVariantModel.getGigaRed());
        productData.setGigaRedSoportado(tmaPoVariantModel.getGigaRedSoportado() != null ? tmaPoVariantModel.getGigaRedSoportado() : false);
        productData.setAccesorios(tmaPoVariantModel.getAccesorios());
        productData.setAltavoz(tmaPoVariantModel.getAltavoz());
        productData.setClienteCorreo(tmaPoVariantModel.getClienteCorreo());
        productData.setMicrofono(tmaPoVariantModel.getMicrofono());
        productData.setModem(tmaPoVariantModel.getModem());
        productData.setRanura(tmaPoVariantModel.getRanura());
        productData.setRanuraMicroSD(tmaPoVariantModel.getRanuraMicroSD());
        productData.setRequisitosSistema(tmaPoVariantModel.getRequisitosSistema());
        productData.setResolucion(tmaPoVariantModel.getResolucion());
        productData.setSensor(tmaPoVariantModel.getSensor());
        productData.setModemUsb(tmaPoVariantModel.getModemUsb());
        productData.setSlogan(tmaPoVariantModel.getSlogan());
        productData.setCeMotriz(tmaPoVariantModel.getCeMotriz() != null ? tmaPoVariantModel.getCeMotriz() : false);
        productData.setCeAuditiva(tmaPoVariantModel.getCeAuditiva() != null ? tmaPoVariantModel.getCeAuditiva() : false);
        productData.setCeVisual(tmaPoVariantModel.getCeVisual() != null ? tmaPoVariantModel.getCeVisual() : false);
        productData.setTieneLiga(tmaPoVariantModel.getTieneLiga() != null ? tmaPoVariantModel.getTieneLiga() : false);
        productData.setIncludeGift(tmaPoVariantModel.getIncludeGift() != null ? tmaPoVariantModel.getIncludeGift() : false);
        productData.setDescripcionIncludeGift(tmaPoVariantModel.getDescripcionIncludeGift());
        if (CollectionUtils.isNotEmpty(tmaPoVariantModel.getSupercategories())) {
            for (CategoryModel categoryModel : tmaPoVariantModel.getSupercategories())
            {
                if (categoryModel.getSupercategories().stream().filter(c -> c.getCode().equals(ROOT_CATEGORY_CODE)).findFirst().isPresent())
                {
                    productData.setCategoryName(categoryModel.getName());
                    break;
                }
            }
        }

        setAvailableInRegion(productData,tmaPoVariantModel);
        productData.setChipName(getChipName(tmaPoVariantModel));
    }

    private void setAvailableInRegion(ProductData productData, TelcelPoVariantModel tmaPoVariantModel) {
        final RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        final String region = Objects.nonNull(requestAttributes) ? telcelUtil.getRegionByRequest(((ServletRequestAttributes) requestAttributes).getRequest())
                : StringUtils.EMPTY;
        if (StringUtils.isNotBlank(region)) {
            switch (region) {
                case "1":
                    productData.setAvailableInRegion(tmaPoVariantModel.getR1() != null ? tmaPoVariantModel.getR1() : false);
                    break;
                case "2":
                    productData.setAvailableInRegion(tmaPoVariantModel.getR2() != null ? tmaPoVariantModel.getR2() : false);
                    break;
                case "3":
                    productData.setAvailableInRegion(tmaPoVariantModel.getR3() != null ? tmaPoVariantModel.getR3() : false);
                    break;
                case "4":
                    productData.setAvailableInRegion(tmaPoVariantModel.getR4() != null ? tmaPoVariantModel.getR4() : false);
                    break;
                case "5":
                    productData.setAvailableInRegion(tmaPoVariantModel.getR5() != null ? tmaPoVariantModel.getR5() : false);
                    break;
                case "6":
                    productData.setAvailableInRegion(tmaPoVariantModel.getR6() != null ? tmaPoVariantModel.getR6() : false);
                    break;
                case "7":
                    productData.setAvailableInRegion(tmaPoVariantModel.getR7() != null ? tmaPoVariantModel.getR7() : false);
                    break;
                case "8":
                    productData.setAvailableInRegion(tmaPoVariantModel.getR8() != null ? tmaPoVariantModel.getR8() : false);
                    break;
                case "9":
                    productData.setAvailableInRegion(tmaPoVariantModel.getR9() != null ? tmaPoVariantModel.getR9() : false);
                    break;
                default:
                    productData.setAvailableInRegion(false);
            }
        } else {
            productData.setAvailableInRegion(false);
        }
    }

    private void setSupportedBands(ProductData productData, TelcelPoVariantModel tmaPoVariantModel)
    {
        productData.setBandaSoportada3G(tmaPoVariantModel.getBandaSoportada3G());
        productData.setBandaSoportada4G(tmaPoVariantModel.getBandaSoportada4G());
        productData.setBandaSoportadaGSM(tmaPoVariantModel.getBandaSoportadaGSM());
        productData.setBandaSoportadaVOLTE(tmaPoVariantModel.getBandaSoportadaVOLTE());
        productData.setFrecuenciaCATM1(tmaPoVariantModel.getFrecuenciaCATM1());
        productData.setFrecuenciaNGIOT(tmaPoVariantModel.getFrecuenciaNGIOT());
        if(Objects.nonNull(tmaPoVariantModel.getTecnologia()))
        {
            productData.setTecnologia(tmaPoVariantModel.getTecnologia().getName());
        }
    }

    private void setRegionFlags(ProductData productData, TelcelPoVariantModel tmaPoVariantModel)
    {
        productData.setR1(tmaPoVariantModel.getR1());
        productData.setR2(tmaPoVariantModel.getR2());
        productData.setR3(tmaPoVariantModel.getR3());
        productData.setR4(tmaPoVariantModel.getR4());
        productData.setR5(tmaPoVariantModel.getR5());
        productData.setR6(tmaPoVariantModel.getR6());
        productData.setR7(tmaPoVariantModel.getR7());
        productData.setR8(tmaPoVariantModel.getR8());
        productData.setR9(tmaPoVariantModel.getR9());
    }

    private String getPlaceholder(TelcelPoVariantModel tmaPoVariantModel){
        if(Objects.nonNull(tmaPoVariantModel) && StringUtils.isNotBlank(tmaPoVariantModel.getSector())){

            return tmaPoVariantModel.getSector();
        }

        return Strings.EMPTY;
    }



    private boolean isFreeShipping(ProductModel productModel) {
        if(productModel instanceof TelcelSimpleProductOfferingModel){
            TelcelSimpleProductOfferingModel tmaSimpleProductOfferingModel = (TelcelSimpleProductOfferingModel) productModel;
            TipoTerminalModel tipoTerminalModel = tmaSimpleProductOfferingModel.getTipoTerminal();

            if(Objects.nonNull(tipoTerminalModel) && SMARTPHONE.equalsIgnoreCase(tipoTerminalModel.getCode() != null ? tipoTerminalModel.getCode(): Strings.EMPTY)){
                return true;
            }
        }else{
            if(productModel instanceof TelcelPoVariantModel){
                TelcelPoVariantModel telcelPoVariantModel = (TelcelPoVariantModel) productModel;
                if(telcelPoVariantModel.getTmaBasePo() != null &&
                        telcelPoVariantModel.getTmaBasePo() instanceof TelcelSimpleProductOfferingModel){
                    TelcelSimpleProductOfferingModel telcelSimpleProductOfferingModel = (TelcelSimpleProductOfferingModel)telcelPoVariantModel.getTmaBasePo();
                    TipoTerminalModel tipoTerminalModel = telcelSimpleProductOfferingModel.getTipoTerminal();

                    if(Objects.nonNull(tipoTerminalModel) && SMARTPHONE.equalsIgnoreCase(tipoTerminalModel.getCode() != null ? tipoTerminalModel.getCode(): Strings.EMPTY)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void setProductModelUrlResolver(final TelcelProductModelUrlResolver productModelUrlResolver)
    {
        this.productModelUrlResolver = productModelUrlResolver;
    }
}
