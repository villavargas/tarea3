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

import de.hybris.platform.b2ctelcoservices.order.exception.OrderProcessingException;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercewebservicescommons.dto.order.ActivateOrderWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.DesactivateOrderWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderWsDTO;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import mx.com.telcel.core.models.paymentcommerce.CardConfirmResponse;
import mx.com.telcel.core.service.TelcelPaymentService;
import mx.com.telcel.data.ReceiptStatusList;
import mx.com.telcel.facades.orders.CustomCheckoutFacade;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

@Controller
@RequestMapping(value = "/payment")
@CacheControl(directive = CacheControlDirective.NO_STORE)
@Api(tags = "Payment Controller")
public class PaymentController {

    private static final Logger LOG = Logger.getLogger(PaymentController.class);
    public static final String ONLINE = "Online";
    public static final String TELCEL_PRODUCT_CATALOG = "telcelProductCatalog";
    public static final String TELCEL = "telcel";
    public static final String OK = "200";
    public static final String NOK = "400";

    @Resource(name = "telcelPaymentService")
    private TelcelPaymentService telcelPaymentService;

    @Resource(name = "customCheckoutFacade")
    private CustomCheckoutFacade tmaCheckoutFacade;

    @Resource(name = "dataMapper")
    private DataMapper dataMapper;

    @Resource
    private CatalogVersionService catalogVersionService;

    @Resource
    private BaseSiteService baseSiteService;


    @RequestMapping(value = "/statusPayments", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getStatusPayments(@ApiParam(value = "cartId") @RequestParam(required = true) String cartId) throws NoSuchAlgorithmException, KeyStoreException, IOException, URISyntaxException, KeyManagementException {

        LOG.info("PaymentController.getStatusPayments");

        ReceiptStatusList receiptStatusList = telcelPaymentService.getReceiptStatus(cartId);

        return new ResponseEntity<>(receiptStatusList, HttpStatus.ACCEPTED);

    }


    @RequestMapping(value = "/verifyPayment", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getVerifyPayment(@ApiParam(value = "cartId") @RequestParam(required = true) String cartId,
                                                   @ApiParam(value = "paymentId") @RequestParam(required = true) String paymentId,
                                                   @ApiParam(value = "id3ds") @RequestParam(required = true) String id3ds) throws NoSuchAlgorithmException, KeyStoreException, IOException, URISyntaxException, KeyManagementException {

        LOG.info("PaymentController.getVerifyPayment");
        CardConfirmResponse cardConfirmResponse = telcelPaymentService.getVerifyPayment3Ds(cartId, paymentId, id3ds);
        return new ResponseEntity<>(cardConfirmResponse, HttpStatus.OK);

    }


    @RequestMapping(value = "/activateTimer", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> activateTimer(@ApiParam(value = "cartId") @RequestParam(required = true) String cartId) throws NoSuchAlgorithmException, KeyStoreException, IOException, URISyntaxException, KeyManagementException {

        ActivateOrderWsDTO activateOrderWsDTO = new ActivateOrderWsDTO();
        Date dateNow = new Date();
        String timer =  Long.toString(dateNow.getTime());
        LOG.info("PaymentController.getVerifyPayment cartId[ "+cartId+"]  timer["+timer+"]" );

        boolean activate = telcelPaymentService.activateTimer(cartId,timer);
        if(activate){
            activateOrderWsDTO.setStatus(OK);
            return new ResponseEntity<>(activateOrderWsDTO, HttpStatus.ACCEPTED);
        }
        activateOrderWsDTO.setStatus(NOK);
        return new ResponseEntity<>(activateOrderWsDTO, HttpStatus.CONFLICT);

    }


    @RequestMapping(value = "/desactivateTimer", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> desactivateTimer(@ApiParam(value = "cartId") @RequestParam(required = true) String cartId,
                                                   @ApiParam(value = "timerFromStore") @RequestParam(required = false, defaultValue = "false") final boolean timerFromStore) throws NoSuchAlgorithmException, KeyStoreException, IOException, URISyntaxException, KeyManagementException, OrderProcessingException {

        LOG.info("PaymentController.getVerifyPayment cartId[ "+cartId+"]" );
        LOG.info("PaymentController.getVerifyPayment timerFromStore[ "+timerFromStore+"]" );

        DesactivateOrderWsDTO desactivateOrderWsDTO = new DesactivateOrderWsDTO();
        boolean desactivateTimer = telcelPaymentService.desactivateTimer(cartId);

        if(!timerFromStore && desactivateTimer){
            LOG.info("PaymentController.getVerifyPayment timerFromStore && desactivateTimer" );

            if(baseSiteService.getCurrentBaseSite() == null) {
                baseSiteService.setCurrentBaseSite(TELCEL,true);
            }

            // placeOrder tmaordersControllers
            catalogVersionService.setSessionCatalogVersion(TELCEL_PRODUCT_CATALOG, ONLINE);

            final OrderData orderData = tmaCheckoutFacade.placeOrderFromCartCustomTimer( cartId, true);
            return new ResponseEntity<>(getDataMapper().map(orderData, OrderWsDTO.class), HttpStatus.OK);
        }

        if(desactivateTimer){
            desactivateOrderWsDTO.setStatus(OK);
            return new ResponseEntity<>(desactivateOrderWsDTO, HttpStatus.ACCEPTED);
        }
        desactivateOrderWsDTO.setStatus(NOK);
        return new ResponseEntity<>(desactivateOrderWsDTO, HttpStatus.CONFLICT);

    }

    public DataMapper getDataMapper() {
        return dataMapper;
    }

    public void setDataMapper(DataMapper dataMapper) {
        this.dataMapper = dataMapper;
    }
}
