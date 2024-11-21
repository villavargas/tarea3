package de.hybris.platform.commercewebservices.core.v2.controller.Telcel;

import com.google.gson.Gson;
import de.hybris.platform.commercewebservices.core.constants.YcommercewebservicesConstants;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.util.Config;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import mx.com.telcel.dto.sso.SSOParameterDTO;
import mx.com.telcel.dto.sso.SSOUserDTO;
import mx.com.telcel.dto.sso.request.SSOUserRequestDTO;
import mx.com.telcel.dto.sso.response.SSOMessageResponseDTO;
import mx.com.telcel.dto.sso.response.SSOUserResponseDTO;
import mx.com.telcel.core.services.TelcelSSOCoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static de.hybris.platform.oauth2.constants.OAuth2Constants.PASSWORD_AUTHORIZATION_NAME;

@Controller
@RequestMapping(value = "{baseSiteId}/activate")
@Api(tags = "Activate Email SSO")
public class ActivateEmailController {
    private static final int SUCCESS = 0;
    public static final int NOT_FOUND_CUSTOMER_CODE = 114;

    private static final Logger LOG = LoggerFactory.getLogger(ActivateEmailController.class);
    private static final String WEBSITE_TELCEL = "website.telcel.external.https";
    private static final String LOGIN = "/login";
    private static final String EMAIL = "email";

    private static final String SOURCE_PROPERTY = "telcelb2cwebservices.registration.user.source";
    private static final String KEY_PROPERTY = "telcelb2cwebservices.registration.parameter.key";
    private static final String VALUE_LEGACY_PROPERTY = "telcelb2cwebservices.registration.parameter.legacy.value";
    private static final String TYPE_PROPERTY = "telcelb2cwebservices.registration.parameter.type";

    @Resource(name = "telcelSSOCoreService")
    private TelcelSSOCoreService telcelSSOCoreService;

    @Resource(name = "defaultConfigurationService")
    private ConfigurationService configurationService;
    /**
     * URL : https://localhost:9002/occ/v2/telcel/activate/resend/key</br>
     * Method : POST</br>
     * Header : Content-Type=application/json</br>
     *
     * @return the response entity
     * @throws KeyManagementException
     *            the key management exception
     * @throws NoSuchAlgorithmException
     *            the no such algorithm exception
     * @throws KeyStoreException
     *            the key store exception
     * @throws IOException
     *            the io exception
     *            **/
    @PostMapping("/resend/key")
    @ResponseBody
    @ApiOperation(value = "Update User Registration Send Mail Validation", notes = "Method to send email validation SSO", produces = "application/json,application/xml", authorizations =
            { @Authorization(value = YcommercewebservicesConstants.CLIENT_CREDENTIAL_AUTHORIZATION_NAME), @Authorization(value = PASSWORD_AUTHORIZATION_NAME) })
    public ResponseEntity<SSOMessageResponseDTO> resendEmail(@RequestBody final Map<String, Object> request)throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException
    {
        String email;
        try{
            email = request.get(EMAIL).toString();
        }catch (NullPointerException | ClassCastException ex)
        {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        LOG.info("RESEND KEY");
        LOG.info("email : " + email);
        final SSOMessageResponseDTO messageResponse = telcelSSOCoreService.resendKeyEmail(email);
        return new ResponseEntity<>(messageResponse,HttpStatus.OK);
    }
    /**
     * URL : https://localhost:9002/occ/v2/telcel/activate/resend/key</br>
     * Method : GET</br>
     * Header : Content-Type=application/json</br>
     *
     * @return the response entity
     * @throws KeyManagementException
     *            the key management exception
     * @throws NoSuchAlgorithmException
     *            the no such algorithm exception
     * @throws KeyStoreException
     *            the key store exception
     * @throws IOException
     *            the io exception
     * @throws  CertificateException
     *            **/
    @GetMapping(value = "email")
    public ResponseEntity<Void> email(@RequestParam(name = "USER") final String user,
                                      @RequestParam(name = "ACTIVATION_KEY") final String activationKey) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException, URISyntaxException, CertificateException
    {
        LOG.info("ACTIVATE EMAIL");
        LOG.info("KEY : " + activationKey);
        LOG.info("USER : " + user);
        final String urlTiendaLogin = configurationService.getConfiguration().getString(WEBSITE_TELCEL)+LOGIN;
        final SSOMessageResponseDTO response = telcelSSOCoreService.activateEmail(activationKey);
        if (response.getResponseCode().equals(String.valueOf(HttpStatus.OK.value())))
        {
            final SSOUserResponseDTO lookupUser = telcelSSOCoreService.lookupUser(user);
            if (lookupUser.getError().getCode() == NOT_FOUND_CUSTOMER_CODE)
            {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            if (lookupUser.getError().getCode() == SUCCESS)
            {
                if (!lookupUser.getUser().getActive() && lookupUser.getUser().getPasswordfailedattemptscounter() != 0)
                {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
            }
            final String id = lookupUser.getUser().getId();
            final List<SSOParameterDTO> parameters = new ArrayList<>();
            final SSOParameterDTO param = new SSOParameterDTO();
            param.setKey(Config.getParameter(KEY_PROPERTY));
            param.setValue(Config.getParameter(VALUE_LEGACY_PROPERTY));
            param.setType(Config.getParameter(TYPE_PROPERTY));
            parameters.add(param);
            final SSOParameterDTO paramMod = new SSOParameterDTO();
            final SSOUserDTO userMod = new SSOUserDTO();
            userMod.setRegistrationcompleted(true);
            final String userMod_json = new Gson().toJson(userMod);
            paramMod.setKey("modifyUser");
            paramMod.setValue(userMod_json);
            paramMod.setType("com.hp.sso.provisioning.entities.User");
            parameters.add(paramMod);

            final SSOUserDTO userModel = new SSOUserDTO();
            userModel.setLoginname(new String[]
                    { user });
            userModel.setId(id);

            final SSOUserRequestDTO userRequest = new SSOUserRequestDTO();
            userRequest.setSource(Config.getParameter(SOURCE_PROPERTY));
            userRequest.setUser(userModel);
            userRequest.setParameters(parameters);

            final SSOUserResponseDTO userResponse = telcelSSOCoreService.modifyUserSecondLogin(userRequest);
            if (userResponse.getError().getCode() == SUCCESS){
                return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(urlTiendaLogin)).build();
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

        }
        else
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
