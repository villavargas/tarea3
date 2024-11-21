package mx.com.telcel.controllers;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
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

import com.google.gson.Gson;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.List;
import java.util.ArrayList;

import mx.com.telcel.constants.AuthorizationType;
import mx.com.telcel.models.Authorize;
import mx.com.telcel.response.models.AuthorizationResponse;
import mx.com.telcel.response.models.AuthorizeErrorResponse;
import mx.com.telcel.response.models.UserInfoAddressResponse;
import mx.com.telcel.response.models.ClientResponse;
import mx.com.telcel.response.models.ErrorResponse;
import mx.com.telcel.response.models.MessageResponse;
import mx.com.telcel.response.models.TokenResponse;
import mx.com.telcel.response.models.UserInfoResponse;
import mx.com.telcel.response.models.UserResponse;
import mx.com.telcel.services.TelcelSSOService;
import mx.com.telcel.response.models.ErrorResponse;
import mx.com.telcel.models.Authorize;
import mx.com.telcel.models.AddressUser;
import mx.com.telcel.models.Token;
import mx.com.telcel.models.Parameter;
import mx.com.telcel.models.User;
import mx.com.telcel.request.models.UserRequest;



@Controller
@RequestMapping(value = "/userInfo")
@Api(tags = "Details Notification")
public class InformationController {
    private static final Logger LOG = LoggerFactory.getLogger(InformationController.class);

    @Resource(name = "telcelSSOService")
    private TelcelSSOService telcelSSOService;

    @ResponseBody
	@GetMapping(value = "/generalDetails", produces = "application/json")
	public UserResponse lookupUser(@RequestParam(name = "userId") final String userId)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException, URISyntaxException, CertificateException
	{
		LOG.info("lookupUser user service...");
		final UserResponse response = telcelSSOService.lookupUser(userId);
		LOG.info(response.getUser().getAddressstreetone());
		return response;
	}

	@ResponseBody
	@GetMapping(value = "/modifyUser", produces = "application/json")
	public UserResponse modifyUser(@RequestParam(name = "username") final String username,
		@RequestParam(name = "userfamilyname") final String userfamilyname,
		@RequestParam(name = "birthdate") final String birthdate,
		@RequestParam(name = "phonenumberone") final String phonenumberone,
		@RequestParam(name = "emailone") final String emailone,
		@RequestParam(name = "usertitle") final String usertitle,
		@RequestParam(name = "loginname") final String loginname) throws KeyManagementException, 
					NoSuchAlgorithmException, KeyStoreException, IOException
	{
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
		UserRequest request = new UserRequest();

	
		try{
			Date date = sdf.parse(birthdate.toString());
			LOG.info("Add user service...");
			final Parameter[] parameters = new Parameter[2];
			final Parameter param = new Parameter();
			param.setKey("Version");
			param.setValue("Legacy");
			param.setType("java.lang.String");
			parameters[0] = param;

			//importante, asi se envian las modificaciones
			//pass parameters to modify
			final Parameter paramMod = new Parameter();
			final User userMod = new User();
			userMod.setUsername(username);
			userMod.setUserfamilyname(userfamilyname);
			userMod.setBirthdate(sdf.format(date));
			userMod.setPhonenumberone(phonenumberone);
			userMod.setEmailone(emailone);
			userMod.setUsertitle(usertitle);
			final String userMod_str = new Gson().toJson(userMod);
			paramMod.setKey("modifyUser");
			paramMod.setValue(userMod_str);
			paramMod.setType("com.hp.sso.provisioning.entities.User");
			parameters[1] = paramMod;

			//Para modificaciones, solo se envia el login y el id
			final User user = new User();
			user.setLoginname(new String[]
			{ loginname });
			user.setId(loginname);

			
			request.setSource("MiTelcel");
			request.setUser(user);
			request.setParameters(parameters);

		

		}catch(ParseException e){
	   		e.printStackTrace();
	 	} 

		final UserResponse response = telcelSSOService.modifyUser(request);
		return response;
	
	}

    @ResponseBody
	@GetMapping(value = "/addressDetails", produces = "application/json")
	public UserInfoAddressResponse lookupAddress(@RequestParam(name = "userId") final String userId)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException, 
					URISyntaxException, CertificateException
	{
		LOG.info("lookup address service...");
		UserInfoAddressResponse response = new UserInfoAddressResponse();
		UserResponse userResponse = telcelSSOService.lookupUser(userId);
		List<AddressUser> addressObj = new ArrayList<>();
		AddressUser addressUser = new AddressUser();
		LOG.info(userResponse.getUser().getAddresspostalcodeone());
		String compare = "0";
		//!userResponse.getUser().getAddresspostalcodeone().equals(compare)
		if(!userResponse.getUser().getAddresspostalcodeone().equals(compare)){
			addressUser.setStreet_address(userResponse.getUser().getAddressstreetone());
			addressUser.setLocaly(userResponse.getUser().getAddresslocalityone());
			addressUser.setRegion(userResponse.getUser().getAddressregionone());
			addressUser.setPostal_code(userResponse.getUser().getAddresspostalcodeone());
			addressUser.setCountry(userResponse.getUser().getAddresscountryone());
			addressUser.setNumberaddress(1);
			addressUser.setPhonenumber(userResponse.getUser().getPhonenumberone());
			addressUser.setAddressprimary(userResponse.getUser().getAddressprimaryone());
			addressUser.setUserreceiving(userResponse.getUser().getAddressformattedone());
			addressObj.add(addressUser);
		}

		AddressUser addressUserTwo = new AddressUser();

		if(!userResponse.getUser().getAddresspostalcodetwo().equals(compare)){
			addressUserTwo.setStreet_address(userResponse.getUser().getAddressstreettwo());
			addressUserTwo.setLocaly(userResponse.getUser().getAddresslocalitytwo());
			addressUserTwo.setRegion(userResponse.getUser().getAddressregiontwo());
			addressUserTwo.setPostal_code(userResponse.getUser().getAddresspostalcodetwo());
			addressUserTwo.setCountry(userResponse.getUser().getAddresscountrytwo());
			addressUserTwo.setNumberaddress(2);
			addressUserTwo.setPhonenumber(userResponse.getUser().getPhonenumbertwo());
			addressUserTwo.setAddressprimary(userResponse.getUser().getAddressprimarytwo());
			addressUserTwo.setUserreceiving(userResponse.getUser().getAddressformattedtwo());
			addressObj.add(addressUserTwo);
		}

		response.setError(userResponse.getError());
		response.setAddressUser(addressObj);
		return response;
	}

@ResponseBody
	@GetMapping(value = "/modifyAddress", produces = "application/json")
	public UserResponse modifyAddress(@RequestParam(name = "addressstreet") final String addressstreet,
		@RequestParam(name = "numberouter") final String numberouter,
		@RequestParam(name = "numberintern") final String numberintern,
		@RequestParam(name = "addresslocality") final String addresslocality,
		@RequestParam(name = "addressregion") final String addressregion,
		@RequestParam(name = "addressstate") final String addressstate,
		@RequestParam(name = "phonenumber") final String phonenumber,
		@RequestParam(name = "addresspostalcode") final String addresspostalcode,
		@RequestParam(name = "addresscountry") final String addresscountry,
		@RequestParam(name = "addressprimary") final Boolean addressprimary,//variable para saber direccion default
		@RequestParam(name = "loginname") final String loginname,
		@RequestParam(name = "userreceiving") final String userreceiving,
		@RequestParam(name = "numerAddress") final Integer numerAddress) throws KeyManagementException, 
					NoSuchAlgorithmException, KeyStoreException, IOException
	{
		LOG.info("Add user service...");
		String addressfull = addressstreet+ " |" +numberouter+ " |" +numberintern;
		String addresscomplement = addresslocality+ " |" +addressregion+ " |" +addressstate;

		final Parameter[] parameters = new Parameter[2];
		final Parameter param = new Parameter();
		param.setKey("Version");
		param.setValue("Legacy");
		param.setType("java.lang.String");
		parameters[0] = param;

		//importante, asi se envian las modificaciones
		//pass parameters to modify
		final Parameter paramMod = new Parameter();
		final User userMod = new User();
		if (numerAddress == 1){
			userMod.setAddressstreetone(addressfull);
			userMod.setAddresslocalityone(addresscomplement);
			userMod.setAddressregionone(addressregion);
			userMod.setAddresspostalcodeone(addresspostalcode);
			userMod.setAddresscountryone(addresscountry);
			userMod.setAddressprimaryone(addressprimary);
			userMod.setPhonenumberone(phonenumber);
			userMod.setAddressformattedone(userreceiving);	
		} else {
			userMod.setAddressstreettwo(addressfull);
			userMod.setAddresslocalitytwo(addresscomplement);
			userMod.setAddressregiontwo(addressregion);
			userMod.setAddresspostalcodetwo(addresspostalcode);
			userMod.setAddresscountrytwo(addresscountry);
			userMod.setAddressprimarytwo(addressprimary);
			userMod.setPhonenumbertwo(phonenumber);
			userMod.setAddressformattedtwo(userreceiving);
		}

		final String userMod_str = new Gson().toJson(userMod);
		paramMod.setKey("modifyUser");
		paramMod.setValue(userMod_str);
		paramMod.setType("com.hp.sso.provisioning.entities.User");
		parameters[1] = paramMod;

		//Para modificaciones, solo se envia el login y el id
		final User user = new User();
		user.setLoginname(new String[]
		{ loginname });
		user.setId(loginname);

		UserRequest request = new UserRequest();
		request.setSource("MiTelcel");
		request.setUser(user);
		request.setParameters(parameters);

		final UserResponse response = telcelSSOService.modifyUser(request);
		return response;
	}
	
	@ResponseBody
	@GetMapping(value = "/deleteAddress", produces = "application/json")
	public UserResponse deleteAddress(@RequestParam(name = "loginname") final String loginname,
		@RequestParam(name = "numerAddress") final Integer numerAddress) throws KeyManagementException, 
					NoSuchAlgorithmException, KeyStoreException, IOException, URISyntaxException, CertificateException
	{
		LOG.info("Delete address service...");

		final UserResponse userResponse = telcelSSOService.lookupUser(loginname);

		final Parameter[] parameters = new Parameter[2];
		final Parameter param = new Parameter();
		param.setKey("Version");
		param.setValue("Legacy");
		param.setType("java.lang.String");
		parameters[0] = param;
		String valor = "empty";
		Boolean bValor = null;
		//importante, asi se envian las modificaciones
		//pass parameters to modify
		final Parameter paramMod = new Parameter();
		final User userMod = new User();
		if (numerAddress == 1){
			if(userResponse.getUser().getAddressprimaryone()) {
				userMod.setAddresspostalcodetwo(userResponse.getUser().getAddresspostalcodetwo());
				userMod.setAddressprimarytwo(true);
			}
			userMod.setAddressstreetone(valor);
			userMod.setAddresslocalityone(valor);
			userMod.setAddressregionone(valor);
			userMod.setAddresspostalcodeone("0");
			userMod.setAddresscountryone(valor);
			userMod.setPhonenumberone(valor);
			userMod.setAddressformattedone(valor);
			userMod.setAddressprimaryone(bValor);
		} else {
			if(userResponse.getUser().getAddressprimaryone()) {
				userMod.setAddresspostalcodeone(userResponse.getUser().getAddresspostalcodeone());
				userMod.setAddressprimaryone(true);
			}
			userMod.setAddressstreettwo(valor);
			userMod.setAddresslocalitytwo(valor);
			userMod.setAddressregiontwo(valor);
			userMod.setAddresspostalcodetwo("0");
			userMod.setAddresscountrytwo(valor);
			userMod.setPhonenumbertwo(valor);
			userMod.setAddressformattedtwo(valor);
			userMod.setAddressprimarytwo(bValor);
		}

		final String userMod_str = new Gson().toJson(userMod);
		paramMod.setKey("modifyUser");
		paramMod.setValue(userMod_str);
		paramMod.setType("com.hp.sso.provisioning.entities.User");
		parameters[1] = paramMod;

		//Para modificaciones, solo se envia el login y el id
		final User user = new User();
		user.setLoginname(new String[]
		{ loginname });
		user.setId(loginname);

		UserRequest request = new UserRequest();
		request.setSource("MiTelcel");
		request.setUser(user);
		request.setParameters(parameters);

		final UserResponse response = telcelSSOService.modifyUser(request);
		return response;
	}

	@ResponseBody
	@GetMapping(value = "/defaultAddress", produces = "application/json")
	public UserResponse defaultAddress(@RequestParam(name = "addressprimary") final Boolean addressprimary,//variable para saber direccion default
		@RequestParam(name = "loginname") final String loginname,
		@RequestParam(name = "numerAddress") final Integer numerAddress) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, 
					IOException, URISyntaxException, CertificateException
	{
		LOG.info("Default address service...");
		final UserResponse userResponse = telcelSSOService.lookupUser(loginname);

		final Parameter[] parameters = new Parameter[2];
		final Parameter param = new Parameter();
		param.setKey("Version");
		param.setValue("Legacy");
		param.setType("java.lang.String");
		parameters[0] = param;

		//importante, asi se envian las modificaciones
		//pass parameters to modify
		final Parameter paramMod = new Parameter();
		final User userMod = new User();
		if (numerAddress == 1){
			userMod.setAddresspostalcodeone(userResponse.getUser().getAddresspostalcodeone());
			userMod.setAddressprimaryone(addressprimary);
			userMod.setAddresspostalcodetwo(userResponse.getUser().getAddresspostalcodetwo());
			userMod.setAddressprimarytwo(false);
		} else {
			userMod.setAddresspostalcodetwo(userResponse.getUser().getAddresspostalcodetwo());
			userMod.setAddressprimarytwo(addressprimary);			
			userMod.setAddresspostalcodeone(userResponse.getUser().getAddresspostalcodeone());
			userMod.setAddressprimaryone(false);
		}

		final String userMod_str = new Gson().toJson(userMod);
		paramMod.setKey("modifyUser");
		paramMod.setValue(userMod_str);
		paramMod.setType("com.hp.sso.provisioning.entities.User");
		parameters[1] = paramMod;

		//Para modificaciones, solo se envia el login y el id
		final User user = new User();
		user.setLoginname(new String[]
		{ loginname });
		user.setId(loginname);

		UserRequest request = new UserRequest();
		request.setSource("MiTelcel");
		request.setUser(user);
		request.setParameters(parameters);

		final UserResponse response = telcelSSOService.modifyUser(request);
		return response;
	}
}