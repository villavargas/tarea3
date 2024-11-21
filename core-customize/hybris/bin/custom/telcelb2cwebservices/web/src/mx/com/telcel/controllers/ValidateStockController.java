/*
 *
 * [y] hybris Platform
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package mx.com.telcel.controllers;


import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.stock.StockService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import mx.com.amx.mexico.telcel.di.sds.gsa.dai.esb.consultastock.ConsultarAlmacenCPResponse;
import mx.com.amx.mexico.telcel.di.sds.gsa.dai.esb.consultastock.MaterialAlmacenRespType;
import mx.com.telcel.core.service.ComparatorService;
import mx.com.telcel.core.service.ReplicateStockService;
import mx.com.telcel.data.BrandDataList;
import mx.com.telcel.data.DeviceDataList;
import mx.com.telcel.dto.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Comparator controller.
 */
@Controller
@RequestMapping(value = "/validate-stock")
@CacheControl(directive = CacheControlDirective.NO_STORE)
@Api(tags = "Validate Stock Controller")
public class ValidateStockController
{

    private static final Logger LOG = Logger.getLogger(ValidateStockController.class);
    public static final String TELCEL = "telcel";

    @Resource(name = "comparatorService")
    private ComparatorService comparatorService;

    @Resource(name = "dataMapper")
    private DataMapper dataMapper;

    @Resource
    private BaseSiteService baseSiteService;

    @Resource
    private CommerceCartService commerceCartService;

    @Resource
    private ReplicateStockService replicateStockService;

    @Resource
    private BaseStoreService baseStoreService;

    @Resource
    private StockService stockService;


    /**
     * The constant AUTHORIZATION_URL.
     */
    public static final String AUTHORIZATION_URL = "/authorizationserver/oauth/token";
    /**
     * The constant PASSWORD_AUTHORIZATION_NAME.
     */
    public static final String PASSWORD_AUTHORIZATION_NAME = "oauth2_password";
    /**
     * The constant CLIENT_CREDENTIAL_AUTHORIZATION_NAME.
     */
    public static final String CLIENT_CREDENTIAL_AUTHORIZATION_NAME = "oauth2_client_credentials";


    /**
     * Gets device for brand.
     *
     * @param cartId the brand
     * @return the device for brand
     */
    @RequestMapping(value = "/cart" , method = RequestMethod.GET)
    @CacheControl(directive = CacheControlDirective.PUBLIC, maxAge = 1800)
    @ResponseBody
    @ApiOperation(value = "Validate", notes = "Method to validate stock", produces = "application/json,application/xml",
            authorizations = {@Authorization(value = CLIENT_CREDENTIAL_AUTHORIZATION_NAME), @Authorization(value = PASSWORD_AUTHORIZATION_NAME)})
    public ResponseEntity<ErrorsValidateStockListWsDTO> getValidateStock(@ApiParam(value = "Cart ID") @RequestParam(required = true) String cartId,
                                                                         @ApiParam(value = "Postal code") @RequestParam(required = true) String postalCode)
    {
        LOG.info("ValidateStockController.getValidateStock = "+cartId);

        final BaseStoreModel baseStoreModel = getBaseStoreService().getBaseStoreForUid("telcel");
        boolean validateStock = baseStoreModel.getValidateStock()!=null ? baseStoreModel.getValidateStock() : false;

        final CartModel cartModel = getCommerceCartService().getCartForGuidAndSite(cartId, getBaseSiteService().getBaseSiteForUID("telcel"));

        String cp = postalCode;

        ErrorsValidateStockListWsDTO errorsValidateStockWsDTO = new ErrorsValidateStockListWsDTO();
        List<ErrorsValidateStockWsDTO> errorsValidateStock = new ArrayList<>();

        //Cuando no se valida stock
        if(!validateStock){
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        }


        //Cuando validamos stock con Codigo Postal
        if(StringUtils.isNotEmpty(cp)){
            LOG.info("ValidateStockController.getValidateStock con CP= "+cartId);

            for(AbstractOrderEntryModel entry :cartModel.getEntries()){
                entry.getProduct().getCode();
                String requiereSim = entry.getProduct().getActivable() ? "1" : "x";


                BigDecimal coberturaProduct = BigDecimal.valueOf(0);

                try {
                    ConsultarAlmacenCPResponse consultarAlmacenCPResponse = getReplicateStockService().getAlmacenForCp(cp,requiereSim,entry.getProduct().getSku(),entry.getQuantity().toString(),"");
                    if(consultarAlmacenCPResponse != null && consultarAlmacenCPResponse.getConsultarAlmacenCPResponse() != null &&
                            consultarAlmacenCPResponse.getConsultarAlmacenCPResponse().getMaterialesResp() != null){

                        List<MaterialAlmacenRespType> materialAlmacenRespTypeList = consultarAlmacenCPResponse.getConsultarAlmacenCPResponse().getMaterialesResp();
                        for(MaterialAlmacenRespType materialAlmacenRespType : materialAlmacenRespTypeList){
                            coberturaProduct = coberturaProduct.add(materialAlmacenRespType.getCobertura());
                        }
                    }

                    if(entry.getQuantity() > coberturaProduct.longValue()){
                        ErrorsValidateStockWsDTO error = new ErrorsValidateStockWsDTO();
                        error.setSku(entry.getProduct().getSku());
                        error.setRequiere(entry.getQuantity().toString());
                        error.setDisponible(coberturaProduct.toString());
                        errorsValidateStock.add(error);

                    }


                } catch (Exception e ) {
                    e.printStackTrace();
                }
            }


            if(!errorsValidateStock.isEmpty()){
                errorsValidateStockWsDTO.setErrorsValidateStock(errorsValidateStock);
                return new ResponseEntity<>(errorsValidateStockWsDTO, HttpStatus.ACCEPTED);

            }
        }else {
            LOG.info("ValidateStockController.getValidateStock con Stock de SAP Commerce= "+cartId);

            for(AbstractOrderEntryModel entry :cartModel.getEntries()){
                int stock = getStockService().getTotalStockLevelAmount(entry.getProduct());
                LOG.info("ValidateStockController.getValidateStock ["+entry.getProduct().getCode()+"] stock ["+stock+"]");
                if(entry.getQuantity() > stock){
                    ErrorsValidateStockWsDTO error = new ErrorsValidateStockWsDTO();
                    error.setSku(entry.getProduct().getSku());
                    error.setRequiere(entry.getQuantity().toString());
                    error.setDisponible(String.valueOf(stock));
                    errorsValidateStock.add(error);

                }
            }

            if(!errorsValidateStock.isEmpty()){
                errorsValidateStockWsDTO.setErrorsValidateStock(errorsValidateStock);
                return new ResponseEntity<>(errorsValidateStockWsDTO, HttpStatus.ACCEPTED);

            }
        }

        return new ResponseEntity<>(HttpStatus.ACCEPTED);


    }

    public CommerceCartService getCommerceCartService() {
        return commerceCartService;
    }

    public void setCommerceCartService(CommerceCartService commerceCartService) {
        this.commerceCartService = commerceCartService;
    }

    public BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    public void setBaseSiteService(BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }

    public ReplicateStockService getReplicateStockService() {
        return replicateStockService;
    }

    public void setReplicateStockService(ReplicateStockService replicateStockService) {
        this.replicateStockService = replicateStockService;
    }

    public BaseStoreService getBaseStoreService() {
        return baseStoreService;
    }

    public void setBaseStoreService(BaseStoreService baseStoreService) {
        this.baseStoreService = baseStoreService;
    }

    public StockService getStockService() {
        return stockService;
    }

    public void setStockService(StockService stockService) {
        this.stockService = stockService;
    }
}
