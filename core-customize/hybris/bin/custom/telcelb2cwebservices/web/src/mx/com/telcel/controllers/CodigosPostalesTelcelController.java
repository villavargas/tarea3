/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.controllers;

import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import mx.com.telcel.core.services.TelcelCodigosPostalesService;
import mx.com.telcel.facades.zipcodes.data.CodigosPostalesTelcelDTO;
import mx.com.telcel.facades.zipcodes.data.CodigosPostalesTelcelData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Objects;


/**
 * The type Holder line checkout controller.
 */
@Controller
@RequestMapping(value = "/codigos-postales-telcel")
@CacheControl(directive = CacheControlDirective.NO_STORE)
@Api(tags = "Codigos Postales Telcel")
public class CodigosPostalesTelcelController {

    private static final Logger LOG = LoggerFactory.getLogger(CodigosPostalesTelcelController.class);
    private static final String CODIGOS_POSTALES_FIELDS = "estado,municipio";

    @Resource(name = "dataMapper")
    private DataMapper dataMapper;

    @Resource(name = "telcelCodigosPostalesService")
    private TelcelCodigosPostalesService telcelCodigosPostalesService;

    /**
     * Gets inf by postal code.
     *
     * @param zipcode the zipcode
     * @return the inf by postal code
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{zipcode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiOperation(nickname = "getInformacionPorCodigoPostal", value = "Codigos Postales Telcel request.", notes = "Codigos Postales Telcel request.",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public CodigosPostalesTelcelDTO getInfByPostalCode(
            @ApiParam(value = "Codigo Postal", required = true) @PathVariable final String zipcode) {
        LOG.debug("getInformacionPorCodigoPostal");
        final CodigosPostalesTelcelData codigosPostalesTelcelData = telcelCodigosPostalesService.getInfForZipcode(zipcode);
        if (Objects.isNull(codigosPostalesTelcelData)) {
            return null;
        }
        return dataMapper.map(codigosPostalesTelcelData, CodigosPostalesTelcelDTO.class, CODIGOS_POSTALES_FIELDS);
    }
}
