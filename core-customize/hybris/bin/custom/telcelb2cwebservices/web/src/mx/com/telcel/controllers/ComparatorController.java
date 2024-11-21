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
package mx.com.telcel.controllers;

import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import mx.com.telcel.core.service.ComparatorService;
import mx.com.telcel.data.BrandDataList;
import mx.com.telcel.data.DeviceDataList;
import mx.com.telcel.dto.*;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * The type Comparator controller.
 */
@Controller
@RequestMapping(value = "/comparator")
@CacheControl(directive = CacheControlDirective.NO_STORE)
@Api(tags = "Comparator Controller")
public class ComparatorController
{

    private static final Logger LOG = Logger.getLogger(ComparatorController.class);

    @Resource(name = "comparatorService")
    private ComparatorService comparatorService;

    @Resource(name = "dataMapper")
    private DataMapper dataMapper;

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
     * Gets brands.
     *
     * @return the brands
     */
    @RequestMapping(value = "/brands",method = RequestMethod.GET)
    @ResponseBody
    public BrandsListWsDTO getBrands()
    {

        BrandDataList brandDataList = comparatorService.getBrands();
        return dataMapper.map(brandDataList, BrandsListWsDTO.class);

    }


    /**
     * Gets device for brand.
     *
     * @param brand the brand
     * @return the device for brand
     */
    @RequestMapping(value = "/devices" , method = RequestMethod.GET)
    @CacheControl(directive = CacheControlDirective.PUBLIC, maxAge = 1800)
    @ResponseBody
    @ApiOperation(value = "Get DEvices", notes = "Method to get devices", produces = "application/json,application/xml",
            authorizations = {@Authorization(value = CLIENT_CREDENTIAL_AUTHORIZATION_NAME), @Authorization(value = PASSWORD_AUTHORIZATION_NAME)})
    public DevicesListWsDTO getDeviceForBrand(@ApiParam(value = "Brand ID") @RequestParam(required = true) String brand)
    {
        LOG.info("ComparatorController.getDeviceForBrand = "+brand);
        DeviceDataList deviceDataList = comparatorService.getDevices(brand);
        return dataMapper.map(deviceDataList, DevicesListWsDTO.class);


    }





}