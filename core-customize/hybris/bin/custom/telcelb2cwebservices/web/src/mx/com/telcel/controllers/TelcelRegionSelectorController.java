/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.controllers;

import de.hybris.platform.b2ctelcoservices.model.TmaOneTimeProdOfferPriceChargeModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.europe1.enums.UserPriceGroup;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import mx.com.telcel.core.services.TelcelCodigosPostalesService;
import mx.com.telcel.facades.region.selector.data.TelcelRegionDataWsDTO;
import mx.com.telcel.facades.zipcodes.data.CodigosPostalesTelcelData;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Objects;

/**
 * The type Telcel region selector controller.
 */
@Controller
@RequestMapping(value = "/{baseSiteId}/users/{userId}/region")
@CacheControl(directive = CacheControlDirective.NO_CACHE)
@Api(tags = "Telcel Region Selector")
public class TelcelRegionSelectorController {
    private static final Logger LOG = LoggerFactory.getLogger(TelcelRegionSelectorController.class);
    public static final String DASH = "-";
    private static final String CATALOG_ID = "telcelProductCatalog";
    private static final String VERSION_ONLINE = "Online";

    @Resource(name = "telcelCodigosPostalesService")
    private TelcelCodigosPostalesService telcelCodigosPostalesService;

    @Resource(name = "modelService")
    private ModelService modelService;

    @Resource(name = "dataMapper")
    private DataMapper dataMapper;

    @Resource
    private UserService userService;

    @Resource
    private EnumerationService enumerationService;

    @Resource
    ProductService productService;

    @Resource
    private CatalogVersionService catalogVersionService;

    /**
     * Sets region on sesion.
     *
     * @param postalCode          the postal code
     * @param userId              the user id
     * @param httpServletResponse the http servlet response
     * @return the region on sesion
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{postalCode}/setRegion", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiBaseSiteIdAndUserIdParam
    @ResponseBody
    @ApiOperation(nickname = "setTelcelRegionRequest", value = "Telcel Region request.", notes = "Set Telcel Region request.",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public TelcelRegionDataWsDTO setRegionOnSesion(@ApiParam(value = "Postal Code", required = true) @PathVariable final String postalCode,
                                                   @ApiParam(value = "User ID", required = true) @PathVariable final String userId,
                                                   @ApiParam(value = "Product Code") @RequestParam(required = false) String productCode,
                                                   final HttpServletResponse httpServletResponse) {

        TelcelRegionDataWsDTO telcelRegionDataWsDTO = new TelcelRegionDataWsDTO();
        final CodigosPostalesTelcelData codigosPostalesTelcelData = telcelCodigosPostalesService.getInfForZipcode(postalCode);
        if (Objects.isNull(codigosPostalesTelcelData) || Objects.isNull(codigosPostalesTelcelData.getRegion())) {
            telcelRegionDataWsDTO.setCode("-1");
            telcelRegionDataWsDTO.setDescription("Codigo postal no encontrado");
            return telcelRegionDataWsDTO;
        }

        final CustomerModel customerModel = getUserById(userId);
        final UserPriceGroup userPriceGroup = enumerationService.getEnumerationValue(UserPriceGroup.class, codigosPostalesTelcelData.getRegion().getCode());

        //Usuarios Registrados
        if (Objects.nonNull(customerModel) && Objects.nonNull(userPriceGroup) && !userService.isAnonymousUser(customerModel)) {
            customerModel.setEurope1PriceFactory_UPG(userPriceGroup);
            modelService.save(customerModel);
        }else{
            telcelRegionDataWsDTO.setDescription("El usuario no se encontro o es anonimo");
        }
        //Usuario Anonimo
        httpServletResponse.setHeader("Set-Cookie", "tlsr=" + userPriceGroup.getCode() //NOSONAR
                + DASH + codigosPostalesTelcelData.getCodigo() +
                "; Path=/; Secure");

        if (StringUtils.isNotEmpty(productCode)) {
            telcelRegionDataWsDTO.setIsPriceZero(isZeroPrice(userPriceGroup.getCode(), productCode));
        } else {
            telcelRegionDataWsDTO.setIsPriceZero(false);
        }
        telcelRegionDataWsDTO.setCode("0");
        telcelRegionDataWsDTO.setRegion(dataMapper.map(codigosPostalesTelcelData.getEstado(), RegionData.class));
        telcelRegionDataWsDTO.setRegionCode(codigosPostalesTelcelData.getRegion().getCode());
        return telcelRegionDataWsDTO;
    }

    private boolean isZeroPrice(String code, String productCode) {
        catalogVersionService.setSessionCatalogVersion(CATALOG_ID, VERSION_ONLINE);
        final ProductModel productModel = productService.getProductForCode(productCode);

        if (Objects.nonNull(productModel)) {
            final Collection<PriceRowModel> priceRowModels = productModel.getEurope1Prices();
            if (CollectionUtils.isNotEmpty(priceRowModels)) {
                for (PriceRowModel priceRowModel : priceRowModels) {
                    if (Objects.nonNull(priceRowModel.getUg()) && priceRowModel.getUg().getCode().contains(code)) {
                        if (Objects.nonNull(priceRowModel.getProductOfferingPrice()) && priceRowModel.getProductOfferingPrice() instanceof TmaOneTimeProdOfferPriceChargeModel) {
                            final TmaOneTimeProdOfferPriceChargeModel tmaOneTimeProdOfferPriceChargeModel = (TmaOneTimeProdOfferPriceChargeModel) priceRowModel.getProductOfferingPrice();
                            return tmaOneTimeProdOfferPriceChargeModel.getValue().compareTo(0D) == 0 ? true : false;
                        }
                    }
                }
            } else {
                LOG.info(String.format("prices not found for product: %s", code));
            }
        }

        return false;
    }


    /**
     * Gets current user region.
     *
     * @param userId the user id
     * @return the current user region
     */
    @RequestMapping(method = RequestMethod.GET, value = "/getRegion", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiBaseSiteIdAndUserIdParam
    @ResponseBody
    @ApiOperation(nickname = "getCurrentUserRegion", value = "Get Telcel Region request.", notes = "Get Telcel Region request.",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getCurrentUserRegion(@ApiParam(value = "User ID", required = true) @PathVariable final String userId) {

        final CustomerModel customerModel = getUserById(userId);
        //Usuarios Registrados
        if (Objects.nonNull(customerModel)) {
            UserPriceGroup userDiscountGroup = customerModel.getEurope1PriceFactory_UPG();
            if (Objects.nonNull(userDiscountGroup)) {
                return new ResponseEntity<>(userDiscountGroup.getCode(), HttpStatus.OK);
            }
        }
        //Usuario Anonimo
        return new ResponseEntity<>(StringUtils.EMPTY, HttpStatus.NOT_FOUND);
    }


    private CustomerModel getUserById(String userId) {
        UserModel userModel = null;
        try{
            userModel = userService.getUserForUID(userId);
        }catch (UnknownIdentifierException e){
            LOG.info(String.format("User with id: %s not found", userId));
        }

        if (Objects.nonNull(userModel)) {
            return (CustomerModel) userModel;
        }
        return null;
    }
}
