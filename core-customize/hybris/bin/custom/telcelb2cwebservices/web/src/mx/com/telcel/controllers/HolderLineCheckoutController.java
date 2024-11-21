/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.controllers;

import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commercewebservicescommons.dto.order.CartWsDTO;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.util.localization.Localization;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import mx.com.telcel.core.model.HolderLineModel;
import mx.com.telcel.dto.CustomerCheckoutInfWsDTO;
import mx.com.telcel.dto.GenericResponseWsDTO;
import mx.com.telcel.facades.holderline.data.HolderLineData;
import mx.com.telcel.response.models.UserResponse;
import mx.com.telcel.services.TelcelSSOService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;


/**
 * The type Holder line checkout controller.
 */
@Controller
@RequestMapping(value = "/{baseSiteId}/holder-line-checkout")
@CacheControl(directive = CacheControlDirective.NO_STORE)
@Api(tags = "Telcel Holder Line Checkout")
public class HolderLineCheckoutController {

    private static final Logger LOG = LoggerFactory.getLogger(HolderLineCheckoutController.class);

    private static final int NOT_FOUND_CUSTOMER_CODE = 114;
    private static final String DEMO_EXAMPLE_COM = "demo@example.com";


    @Resource(name = "dataMapper")
    private DataMapper dataMapper;

    @Resource(name = "customerCheckoutInfWsDTOValidator")
    private Validator customerCheckoutInfWsDTOValidator;

    @Resource(name = "telcelSSOService")
    private TelcelSSOService telcelSSOService;

    @Resource(name = "commerceCartService")
    private CommerceCartService commerceCartService;

    @Resource(name = "holderLineReverseConverter")
    private Converter<CustomerCheckoutInfWsDTO, HolderLineModel> holderLineReverseConverter;

    @Resource(name = "holderLineConverter")
    private Converter<HolderLineModel,HolderLineData > holderLineConverter;

    @Resource(name = "modelService")
    private ModelService modelService;

    @Resource
    private BaseSiteService baseSiteService;

    @Resource(name = "userService")
    private UserService userService;

    /**
     * Validate holder address address generic response ws dto.
     *
     * @param customerCheckoutInfWsDTO the customer checkout inf ws dto
     * @param userID                   the user id
     * @param baseSiteId               the base site id
     * @param guid                     the guid
     * @return the response entity
     * @throws KeyManagementException   the key management exception
     * @throws NoSuchAlgorithmException the no such algorithm exception
     * @throws KeyStoreException        the key store exception
     * @throws IOException              the io exception
     * @throws URISyntaxException       the uri syntax exception
     * @throws CertificateException     the certificate exception
     */
    @RequestMapping(method = RequestMethod.POST, value = "/{userID}/validate-holder-address", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiOperation(nickname = "validateHolderLineAddressRequest", value = "Calidate Holder Line Address request.", notes = "Validate Holder Line Address request.",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public GenericResponseWsDTO validateHolderAddressAddress(
            @ApiParam(value = "Customer Checkout object.", required = true) @RequestBody final CustomerCheckoutInfWsDTO customerCheckoutInfWsDTO,
            @ApiParam(value = "User ID", required = true) @PathVariable String userID,
            @ApiParam(value = "Base site identifier", required = true) @PathVariable final String baseSiteId,
            @ApiParam(value = "Cart GUID") @RequestParam(required = true) final String guid)
            throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException,
            IOException, URISyntaxException, CertificateException {
        LOG.debug("validateHolderAddressAddress");
        //Validar Campos enviados por front
        validate(customerCheckoutInfWsDTO, "customerCheckoutInfWsDTO", customerCheckoutInfWsDTOValidator);
        final String email = customerCheckoutInfWsDTO.getEmail();
        //Continuar con el flujo
        CartModel cartModel = null;
        if (guid.contains("-")) {
            cartModel = getCartForUserByGUID(guid, baseSiteId);
        } else {
            cartModel = getCartForUserByCartID(guid, userID);
        }

        //Validar si el usuario existe en SSO
        if (cartModel.getUser().getDisplayName().equalsIgnoreCase("guest")) {
            final UserResponse userResponse = telcelSSOService.lookupUser(email);
            if (userResponse.getError().getCode() != NOT_FOUND_CUSTOMER_CODE) {
                //Existe en SSO
                //setAnonymousCartToGuestCart(cartModel);
                //Crear modelo de address holder
                HolderLineModel holderLineModel = modelService.create(HolderLineModel.class);
                holderLineReverseConverter.convert(customerCheckoutInfWsDTO, holderLineModel);
                modelService.save(holderLineModel);
                //Setearlo en el carrito
                cartModel.setAddressHolderLine(holderLineModel);
                modelService.save(cartModel);
                return getGenericResponseWsDTO(HttpStatus.FOUND, Boolean.TRUE, StringUtils.EMPTY);
            }
        }
        //Crear modelo de address holder
        HolderLineModel holderLineModel = modelService.create(HolderLineModel.class);
        holderLineReverseConverter.convert(customerCheckoutInfWsDTO, holderLineModel);
        modelService.save(holderLineModel);
        //Setearlo en el carrito
        cartModel.setAddressHolderLine(holderLineModel);
        modelService.save(cartModel);

        CustomerModel customerModel = (CustomerModel) cartModel.getUser();
        String emailContact =  customerModel.getContactEmail();
        if(DEMO_EXAMPLE_COM.equalsIgnoreCase(emailContact)){
            customerModel.setDetailEmail(holderLineModel.getEmail());
            modelService.save(customerModel);

        }

        return getGenericResponseWsDTO(HttpStatus.OK, Boolean.TRUE, StringUtils.EMPTY);
    }

    /**
     * return Car anonymous.
     *
     * @param userID                   the user id
     * @param baseSiteId               the base site id
     * @param guid                     the guid
     * @return the response entity
     * @throws KeyManagementException   the key management exception
     * @throws NoSuchAlgorithmException the no such algorithm exception
     * @throws KeyStoreException        the key store exception
     * @throws IOException              the io exception
     * @throws URISyntaxException       the uri syntax exception
     * @throws CertificateException     the certificate exception
     */
    @RequestMapping(method = RequestMethod.POST, value = "/{userID}/return-cart-anonymous", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiOperation(nickname = "returnCartAnonymous", value = "Return Cart Anonymous.", notes = "Return Cart Anonymous.",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public GenericResponseWsDTO returnCartAnonymous(
            @ApiParam(value = "User ID", required = true) @PathVariable String userID,
            @ApiParam(value = "Base site identifier", required = true) @PathVariable final String baseSiteId,
            @ApiParam(value = "Cart GUID") @RequestParam(required = true) final String guid)
            throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException,
            IOException, URISyntaxException, CertificateException {
        CartModel cartModel = null;
      if (guid.contains("-")) {
            cartModel = getCartForUserByGUID(guid, baseSiteId);
        } else {
            cartModel = getCartForUserByCartID(guid, userID);
        }
        HolderLineModel holderLineModel = new HolderLineModel();
        modelService.save(holderLineModel);
        cartModel.setAddressHolderLine(holderLineModel);
        setAnonymousCartToGuestCart(cartModel);
        return getGenericResponseWsDTO(HttpStatus.OK, Boolean.TRUE, StringUtils.EMPTY);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/holderline/{guid}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiOperation(nickname = "validateHolderLineAddressRequest", value = "Calidate Holder Line Address request.", notes = "Validate Holder Line Address request.",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public HolderLineData validateHolderAddressAddress(CustomerCheckoutInfWsDTO customerCheckoutInfWsDTO, @ApiParam(value = "Guid ID", required = true) @PathVariable String guid,
                                                       @ApiParam(value = "Base site identifier", required = true) @PathVariable final String baseSiteId) {
        if (StringUtils.isNotEmpty(guid)) {
            CartModel cartModel = getCartForUserByGUID(guid, baseSiteId);
            if (cartModel != null) {
                final HolderLineModel holderLineModel = cartModel.getAddressHolderLine();
                if (holderLineModel != null) {
                    return dataMapper.map(holderLineConverter.convert(holderLineModel), HolderLineData.class);
                }
            }
        }
        return null;
    }

    private CartModel getCartForUserByCartID(String cartID, String userID) {
        final UserModel user = userService.getUserForUID(userID);
        return commerceCartService.getCartForCodeAndUser(cartID, user);
    }

    private CartModel getCartForUserByGUID(String guid, String baseSiteId) {
        return commerceCartService.getCartForGuidAndSite(guid, baseSiteService.getBaseSiteForUID(baseSiteId));
    }

    private GenericResponseWsDTO getGenericResponseWsDTO(HttpStatus status, boolean wasSuccessful, String errorMesage) {
        final GenericResponseWsDTO genericResponseWsDTO = new GenericResponseWsDTO();
        genericResponseWsDTO.setErrorCode(status.value());
        genericResponseWsDTO.setErrorMessage(errorMesage);
        genericResponseWsDTO.setWasSuccessful(wasSuccessful);
        return dataMapper.map(genericResponseWsDTO, GenericResponseWsDTO.class);
    }

    /**
     * Validate.
     *
     * @param object     the object
     * @param objectName the object name
     * @param validator  the validator
     */
    protected void validate(final Object object, final String objectName, final Validator validator) {
        final Errors errors = new BeanPropertyBindingResult(object, objectName);
        validator.validate(object, errors);
        if (errors.hasErrors()) {
            throw new WebserviceValidationException(errors);
        }
    }

    private void setAnonymousCartToGuestCart(CartModel cartModel) {
        if (cartModel != null && cartModel.getUser() != null && org.apache.commons.lang.StringUtils.contains(cartModel.getUser().getUid(), "|")) {
            cartModel.setUser(userService.getAnonymousUser());
            modelService.save(cartModel);
        }
    }
}
