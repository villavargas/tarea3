package mx.com.telcel.controllers;

import de.hybris.platform.util.localization.Localization;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import mx.com.telcel.response.models.TokenNipResponse;
import mx.com.telcel.response.models.UserResponse;
import mx.com.telcel.services.CustomValidationService;
import mx.com.telcel.services.TelcelSSOService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Map;
import java.util.Objects;

import static mx.com.telcel.constants.Telcelb2cwebservicesConstants.CLIENT_CREDENTIAL_AUTHORIZATION_NAME;
import static mx.com.telcel.constants.Telcelb2cwebservicesConstants.PASSWORD_AUTHORIZATION_NAME;

/**
 * The type Registration controller.
 */
@Controller
@RequestMapping(value = "/codeSecurity")
@CacheControl(directive = CacheControlDirective.NO_STORE)
@Api(tags = "Code Security")

public class CodeSecurityController {

    private static final Logger LOG = LoggerFactory.getLogger(CodeSecurityController.class);
    private static final String UID = "uid";

    /**
     * The constant NOT_FOUND_CUSTOMER_CODE.
     */
    public static final int NOT_FOUND_CUSTOMER_CODE = 114;

    @Resource(name = "telcelSSOService")
    private TelcelSSOService telcelSSOService;

    @Resource(name = "customValidationService")
    private CustomValidationService validationService;

    /**
     * Generate code response entity.
     *  * Example :</br>
     * 	 * POST https://localhost:9002/telcelb2cwebservices/codeSecurity/send?uid=521234567890</br>
     * @return the response entity
     */
    @PostMapping("/send")
    @CacheControl(directive = CacheControlDirective.PUBLIC, maxAge = 1800)
    @ResponseBody
    @ApiOperation(value = "Generate code security", notes = "Generate the security code sent by telcel", produces = "application/json,application/xml")
    public ResponseEntity<TokenNipResponse> generateCode(@RequestBody final Map<String, Object> request) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, KeyManagementException, URISyntaxException {
        if(Objects.isNull(request.get(UID)))
        {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        String uid = request.get(UID).toString();
        LOG.debug("TokenController.generateCode = " + uid);

        //Validate
        uid = uid.toLowerCase();
        TokenNipResponse tokenNipResponse = new TokenNipResponse();
        try
        {
            LOG.info("Send token...");
            boolean isnumber = false;
            if (validationService.validateIsANumber(uid))
            {
                isnumber = true;
                if (!validationService.is10DigitPhoneNumber(uid))
                {
                    tokenNipResponse.setCode(-5);
                    tokenNipResponse.setErrorDescription(Localization.getLocalizedString("login.validation.phonenumber.label"));
                    return new ResponseEntity<>(tokenNipResponse, HttpStatus.UNAUTHORIZED);
                }
                else if (validationService.is10DigitPhoneNumber(uid))
                {
                    uid = "52" + uid;
                }
            }
            if (!validationService.validateIsAnEmail(uid) && !isnumber)
            {
                tokenNipResponse.setCode(-6);
                tokenNipResponse.setErrorDescription(Localization.getLocalizedString("login.validation.email.label"));
                return new ResponseEntity<>(tokenNipResponse, HttpStatus.UNAUTHORIZED);
            }
            final Boolean exists = userExistsInSSO(uid);
            if (exists != null && exists){
                String stringComplete = "";
                tokenNipResponse.setCode(10);
                tokenNipResponse = telcelSSOService.sendTokenNipRC(uid);
                if(tokenNipResponse.getRequestId() != null){
                    if (isnumber){
                        stringComplete = Localization.getLocalizedString("restore.password.token.info.phone.label")+" "+removePrefix(uid,"52");
                        tokenNipResponse.setUid(removePrefix(uid,"52"));
                    } else {
                        stringComplete = Localization.getLocalizedString("restore.password.token.info.email.label")+" "+uid;
                        tokenNipResponse.setUid(uid);
                    }
                } else {
                    if(tokenNipResponse.getErrorDescription().equals("user not authenticated.")){
                        tokenNipResponse.setCode(-11);
                        tokenNipResponse.setErrorDescription("");
                        return new ResponseEntity<>(tokenNipResponse, HttpStatus.UNAUTHORIZED);
                    }
                }
                tokenNipResponse.setDescription(stringComplete);
                return new ResponseEntity<>(tokenNipResponse, HttpStatus.OK);
            } else {
                tokenNipResponse.setCode(-10);
                tokenNipResponse.setErrorDescription(Localization.getLocalizedString("restore.password.not.found.label"));
                return new ResponseEntity<>(tokenNipResponse, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error("Ocurrio un error "+e);
            LOG.error(e.getMessage());
            tokenNipResponse.setCode(-4);
            tokenNipResponse.setErrorDescription(Localization.getLocalizedString("login.validation.error.label"));
            return new ResponseEntity<>(tokenNipResponse, HttpStatus.CONFLICT);
        }
    }

    /**
     * Validate code response entity.
     *
     *  * Example :</br>
     *  * GET https://localhost:9002/telcelb2cwebservices/codeSecurity/validateCode?code=123456&
     *      userName=5512345678&requestId=E4A59DE3BD9F6A075ADB94C4D5A12636</br>
     *
     * @param code the code
     * @return the response entity
     */
    @RequestMapping(value = "/validateCode", method = RequestMethod.GET)
    @CacheControl(directive = CacheControlDirective.PUBLIC, maxAge = 1800)
    @ResponseBody
    @ApiOperation(value = "Validate code security", notes = "validate the security code sent by telcel", produces = "application/json,application/xml",
            authorizations = {@Authorization(value = CLIENT_CREDENTIAL_AUTHORIZATION_NAME), @Authorization(value = PASSWORD_AUTHORIZATION_NAME)})
    public ResponseEntity<TokenNipResponse> validateCode(@ApiParam(value = "Code") @RequestParam(required = true) String code,
                                                         @ApiParam(value = "UserName") @RequestParam(required = true) String username,
                                                         @ApiParam(value = "RequestId") @RequestParam(required = true) String requestId) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, KeyManagementException {
        LOG.info("validateCode = " + code);

        username = username.toLowerCase();
        //Validate
        if (validationService.validateIsANumber(username)) {
            if (validationService.is10DigitPhoneNumber(username)) {
                username = "52" + username;
            }
            TokenNipResponse tokenNipResponse = telcelSSOService.validateTokenNip(username, requestId, code);
            LOG.info("tokenNipResponse = " + tokenNipResponse);
            if(tokenNipResponse.getAccessToken() == null){
                if (tokenNipResponse.getError().equals("OTP_TIMEOUT")//Verfiy Code Timeout Failed
                        || tokenNipResponse.getErrorDescription().equals("request invalid or expired.")) {
                    LOG.info("El código ha caducado " + tokenNipResponse.getErrorDescription());
                    tokenNipResponse.setError("-2");
                    tokenNipResponse.setErrorDescription("El código ha caducado");
                } else if (tokenNipResponse.getErrorDescription().equals("user not authenticated.")) {
                    LOG.info("El código es incorrecto " + tokenNipResponse.getErrorDescription());
                    tokenNipResponse.setError("-3");
                    tokenNipResponse.setErrorDescription("El código es incorrecto");
                } else {
                    tokenNipResponse.setError("-1");
                    tokenNipResponse.setErrorDescription("Ocurrio un error al validar el codigo");
                }
                return new ResponseEntity<>(tokenNipResponse, HttpStatus.BAD_REQUEST);
            } else {
                tokenNipResponse.setUid(removePrefix(username,"52"));
                return new ResponseEntity<>(tokenNipResponse, HttpStatus.OK);
            }
        } else if (validationService.validateIsAnEmail(username)) {
            //Llamar a servicio que valida codigho de seguridad que se envio por email
            TokenNipResponse tokenNipResponse = telcelSSOService.validateTokenNip(username, requestId, code);
            if(tokenNipResponse.getAccessToken() == null){
                if (tokenNipResponse.getError().equals("OTP_TIMEOUT")//Verfiy Code Timeout Failed
                        || tokenNipResponse.getErrorDescription().equals("request invalid or expired.")) {
                    LOG.info("El código ha caducado " + tokenNipResponse.getErrorDescription());
                    tokenNipResponse.setError("-2");
                    tokenNipResponse.setErrorDescription("El código ha caducado");
                } else if (tokenNipResponse.getErrorDescription().equals("user not authenticated.")) {
                    LOG.info("El código es incorrecto " + tokenNipResponse.getErrorDescription());
                    tokenNipResponse.setError("-3");
                    tokenNipResponse.setErrorDescription("El código es incorrecto");
                } else {
                    tokenNipResponse.setError("-1");
                    tokenNipResponse.setErrorDescription("Ocurrio un error al validar el codigo");
                }
                return new ResponseEntity<>(tokenNipResponse, HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(tokenNipResponse, HttpStatus.OK);
        }
        return null;
    }

    private Boolean userExistsInSSO(String userID) throws KeyManagementException, NoSuchAlgorithmException,
            KeyStoreException, IOException, URISyntaxException, CertificateException {
        UserResponse userResponse = telcelSSOService.lookupUser(userID);
        if (userResponse != null) {
            if (userResponse.getError().getCode() == NOT_FOUND_CUSTOMER_CODE) {
                return false;
            } else {
                return true;
            }
        }
        return null;
    }

    public static String removePrefix(String s, String prefix)
    {
        if (s != null && s.startsWith(prefix)) {
            return s.split(prefix, 2)[1];
        }
        return s;
    }
}