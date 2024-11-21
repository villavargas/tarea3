package mx.com.telcel.controllers;

import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.assertj.core.util.Strings;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.annotations.Api;
import mx.com.telcel.core.service.AESService;
import mx.com.telcel.facades.cvtdat.data.UserInfoCVTData;
import mx.com.telcel.facades.cvtdat.data.UserInfoDATData;
import mx.com.telcel.models.cvtdat.ErrorList;
import mx.com.telcel.models.cvtdat.GenericFault;
import mx.com.telcel.models.cvtdat.UserInfoCVTDATResponse;
import mx.com.telcel.services.CVTDATService;


@Controller
@RequestMapping(value = "/cvtdat")
@Api(tags = "CVTs y DATs")
public class CVTDATController
{

	@Resource(name = "cvtdatService")
	private CVTDATService cvtdatService;

	@Resource(name = "defaultConfigurationService")
	private ConfigurationService configurationService;

	@Resource(name = "aesService")
	private AESService aesService;

	private static final Logger LOG = Logger.getLogger(CVTDATController.class);
	private static final String TIENDA_EN_LINEA = "website.telcel.external.https";
	private static final String TIENDA_EN_LINEA_DOMAIN = "telcel.ctvdat.tienda.domain";
	private static final String CVT_PASSWORD = "telcel.cvtdat.cvtpassword";

	private static final int MAX_COOKIE_TIME = 3600;

	/**
	 * Service that receive token and consume user info CVT Service<br/>
	 * Example :</br>
	 * POST https://localhost:9002/telcelb2cwebservices/cvtdat/userInfoCVT</br>
	 * Method : POST</br>
	 *
	 * @param accesoSeguro
	 *           - accesoSeguro</br>
	 * @param password
	 *           - password</br>
	 * @return - ResponseEntity object</br>
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 *
	 */
	@PostMapping(value = "/userInfoCVT")
	public ResponseEntity<Void> userInfoCVT(@RequestParam(name = "accesoSeguro")
	final String accesoSeguro)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException, URISyntaxException
	{
		LOG.info("User Info for CVT");
		final String urlTienda = configurationService.getConfiguration().getString(TIENDA_EN_LINEA) + "/";
		final String domainTienda = configurationService.getConfiguration().getString(TIENDA_EN_LINEA_DOMAIN);
		try
		{
			final String password = configurationService.getConfiguration().getString(CVT_PASSWORD);
			final UserInfoCVTDATResponse response = cvtdatService.userInfoCVT(accesoSeguro, aesService.decrypt(password));
			if (response.getCode() == HttpStatus.OK.value())
			{
				//Validate if user CVT is saved in database
				final UserInfoCVTData userInfo = cvtdatService.decodeUserCVT(response.getUserInfo());
				String emailUser = userInfo.getCorreo();
				final String employeeId = userInfo.getNumEmpleado();
				if (Strings.isNullOrEmpty(emailUser))
				{
					emailUser = userInfo.getNumEmpleado() + "@tiendatelcel.com";
				}
				if (emailUser.equalsIgnoreCase("null"))
				{
					emailUser =userInfo.getNumEmpleado() + "@tiendatelcel.com";
				}
				final UserInfoCVTData userCVT = cvtdatService.existUserCVT(employeeId);
				if (Objects.nonNull(userCVT))
				{
					LOG.info("Usuario cvt existente : " + employeeId);
					final ResponseCookie cookieEmail = ResponseCookie.from("emailCVTDAT", emailUser).httpOnly(false).secure(true)
							.domain(domainTienda).path("/").maxAge(MAX_COOKIE_TIME).build();
					final ResponseCookie cookieTypeUser = ResponseCookie.from("typeUserCVTDAT", "CVT").httpOnly(false).secure(true)
							.domain(domainTienda).path("/").maxAge(MAX_COOKIE_TIME).build();
					final ResponseCookie cookieIdUser = ResponseCookie.from("idUserCVTDAT", userInfo.getNumEmpleado()).httpOnly(false)
							.secure(true).domain(domainTienda).path("/").maxAge(MAX_COOKIE_TIME).build();
					final ResponseCookie cookieError = ResponseCookie.from("errorCVTDAT", null).httpOnly(false).secure(true)
							.domain(domainTienda).path("/").maxAge(0).build();
					return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.SET_COOKIE, cookieEmail.toString())
							.header(HttpHeaders.SET_COOKIE, cookieTypeUser.toString())
							.header(HttpHeaders.SET_COOKIE, cookieIdUser.toString())
							.header(HttpHeaders.SET_COOKIE, cookieError.toString()).location(URI.create(urlTienda)).build();
				}
				//Save user CVT
				final UserInfoCVTData data = cvtdatService.saveUserInfoCVT(response.getUserInfo());
				if (!Objects.isNull(data))
				{
					String email = data.getCorreo();
					if (Strings.isNullOrEmpty(data.getCorreo()))
					{
						email = data.getNumEmpleado() + "@tiendatelcel.com";
					}
					if (data.getCorreo().equalsIgnoreCase("null"))
					{
						email = data.getNumEmpleado() + "@tiendatelcel.com";
					}
					LOG.info("Usuario cvt nuevo : guardar");
					final ResponseCookie cookieEmail = ResponseCookie.from("emailCVTDAT", email).httpOnly(false).secure(true)
							.domain(domainTienda).path("/").maxAge(MAX_COOKIE_TIME).build();
					final ResponseCookie cookieTypeUser = ResponseCookie.from("typeUserCVTDAT", "CVT").httpOnly(false).secure(true)
							.domain(domainTienda).path("/").maxAge(MAX_COOKIE_TIME).build();
					final ResponseCookie cookieIdUser = ResponseCookie.from("idUserCVTDAT", data.getNumEmpleado()).httpOnly(false)
							.secure(true).domain(domainTienda).path("/").maxAge(MAX_COOKIE_TIME).build();
					final ResponseCookie cookieError = ResponseCookie.from("errorCVTDAT", null).httpOnly(false).secure(true)
							.domain(domainTienda).path("/").maxAge(0).build();
					return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.SET_COOKIE, cookieEmail.toString())
							.header(HttpHeaders.SET_COOKIE, cookieTypeUser.toString())
							.header(HttpHeaders.SET_COOKIE, cookieIdUser.toString())
							.header(HttpHeaders.SET_COOKIE, cookieError.toString(), null).location(URI.create(urlTienda)).build();
				}
				else
				{
					LOG.error("No se pudo guardar el usuario info CVT");
					final ResponseCookie cookieEmail = ResponseCookie.from("emailCVTDAT", null).httpOnly(false).secure(true)
							.domain(domainTienda).path("/").maxAge(0).build();
					final ResponseCookie cookieTypeUser = ResponseCookie.from("typeUserCVTDAT", null).httpOnly(false).secure(true)
							.domain(domainTienda).path("/").maxAge(0).build();
					final ResponseCookie cookieIdUser = ResponseCookie.from("idUserCVTDAT", null).httpOnly(false).secure(true)
							.domain(domainTienda).path("/").maxAge(0).build();
					final ResponseCookie cookieError = ResponseCookie
							.from("errorCVTDAT", URLEncoder.encode("No se pudo guardar el usuario info CVT", "UTF-8")).httpOnly(false)
							.secure(true).domain(domainTienda).path("/").maxAge(MAX_COOKIE_TIME).build();
					return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.SET_COOKIE, cookieError.toString())
							.header(HttpHeaders.SET_COOKIE, cookieEmail.toString())
							.header(HttpHeaders.SET_COOKIE, cookieTypeUser.toString())
							.header(HttpHeaders.SET_COOKIE, cookieIdUser.toString()).location(URI.create(urlTienda)).build();
				}
			}
			else
			{
				final GenericFault error = response.getError();
				LOG.error("Error al obtener datos de usuario cvt : ");
				String errorStr = "";
				for (final ErrorList err : error.getErrorList())
				{
					LOG.error(err.getError().getCode() + " : " + err.getError().getDescription());
					errorStr += err.getError().getDescription() + ",";
				}
				if (errorStr.length() > 1)
				{
					errorStr = errorStr.substring(0, errorStr.length() - 1);
				}
				final ResponseCookie cookieEmail = ResponseCookie.from("emailCVTDAT", null).httpOnly(false).secure(true)
						.domain(domainTienda).path("/").maxAge(0).build();
				final ResponseCookie cookieTypeUser = ResponseCookie.from("typeUserCVTDAT", null).httpOnly(false).secure(true)
						.domain(domainTienda).path("/").maxAge(0).build();
				final ResponseCookie cookieIdUser = ResponseCookie.from("idUserCVTDAT", null).httpOnly(false).secure(true)
						.domain(domainTienda).path("/").maxAge(0).build();
				final ResponseCookie cookieError = ResponseCookie.from("errorCVTDAT", URLEncoder.encode(errorStr, "UTF-8"))
						.httpOnly(false).secure(true).domain(domainTienda).path("/").maxAge(MAX_COOKIE_TIME).build();
				return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.SET_COOKIE, cookieError.toString())
						.header(HttpHeaders.SET_COOKIE, cookieEmail.toString())
						.header(HttpHeaders.SET_COOKIE, cookieTypeUser.toString())
						.header(HttpHeaders.SET_COOKIE, cookieIdUser.toString()).location(URI.create(urlTienda)).build();
			}
		}
		catch (final Exception e)
		{
			LOG.error("ERROR CVT : " + e.getMessage());
			final ResponseCookie cookieEmail = ResponseCookie.from("emailCVTDAT", null).httpOnly(false).secure(true)
					.domain(domainTienda).path("/").maxAge(0).build();
			final ResponseCookie cookieTypeUser = ResponseCookie.from("typeUserCVTDAT", null).httpOnly(false).secure(true)
					.domain(domainTienda).path("/").maxAge(0).build();
			final ResponseCookie cookieIdUser = ResponseCookie.from("idUserCVTDAT", null).httpOnly(false).secure(true)
					.domain(domainTienda).path("/").maxAge(0).build();
			final ResponseCookie cookieError = ResponseCookie
					.from("errorCVTDAT", URLEncoder.encode("Ocurrio un error : " + e.getMessage(), "UTF-8")).httpOnly(false)
					.secure(true).domain(domainTienda).path("/").maxAge(MAX_COOKIE_TIME).build();
			return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.SET_COOKIE, cookieError.toString())
					.header(HttpHeaders.SET_COOKIE, cookieEmail.toString()).header(HttpHeaders.SET_COOKIE, cookieTypeUser.toString())
					.header(HttpHeaders.SET_COOKIE, cookieIdUser.toString()).location(URI.create(urlTienda)).build();
		}
	}

	/**
	 * Service that receive datb64 and consume user info DAT Service<br/>
	 * Example :</br>
	 * POST https://localhost:9002/telcelb2cwebservices/cvtdat/userInfoDAT</br>
	 * Method : POST</br>
	 *
	 * @param datb64
	 *           - User Info DAT base 64</br>
	 * @return - ResponseEntity object</br>
	 * @throws UnsupportedEncodingException
	 *
	 */
	@PostMapping(value = "/userInfoDAT")
	public ResponseEntity<Void> userInfoDAT(@RequestParam(name = "datb64")
	final String datb64) throws UnsupportedEncodingException
	{
		LOG.info("User Info for DAT");
		final String urlTienda = configurationService.getConfiguration().getString(TIENDA_EN_LINEA);
		final String domainTienda = configurationService.getConfiguration().getString(TIENDA_EN_LINEA_DOMAIN);
		try
		{
			//Validate Decode user DAT
			final UserInfoDATData userDATDecode = cvtdatService.decodeUserDAT(datb64);
			if (Objects.isNull(userDATDecode))
			{
				LOG.info("No se pudo decodificar el usuario info DAT");
				final ResponseCookie cookieEmail = ResponseCookie.from("emailCVTDAT", null).httpOnly(false).secure(true)
						.domain(domainTienda).path("/").maxAge(0).build();
				final ResponseCookie cookieTypeUser = ResponseCookie.from("typeUserCVTDAT", null).httpOnly(false).secure(true)
						.domain(domainTienda).path("/").maxAge(0).build();
				final ResponseCookie cookieIdUser = ResponseCookie.from("idUserCVTDAT", null).httpOnly(false).secure(true)
						.domain(domainTienda).path("/").maxAge(0).build();
				final ResponseCookie cookieError = ResponseCookie
						.from("errorCVTDAT", URLEncoder.encode("No se pudo decodificar el usuario info DAT", "UTF-8")).httpOnly(false)
						.secure(true).domain(domainTienda).path("/").maxAge(MAX_COOKIE_TIME).build();
				return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.SET_COOKIE, cookieError.toString())
						.header(HttpHeaders.SET_COOKIE, cookieEmail.toString())
						.header(HttpHeaders.SET_COOKIE, cookieTypeUser.toString())
						.header(HttpHeaders.SET_COOKIE, cookieIdUser.toString()).location(URI.create(urlTienda)).build();
			}
			//Validate if user DAT is saved in database
			final UserInfoDATData userDAT = cvtdatService.existUserDAT(userDATDecode.getUsuario());
			if (Objects.nonNull(userDAT))
			{
				LOG.info("Usuario dat existente : " + userDAT.getUsuario());
				final ResponseCookie cookieEmail = ResponseCookie.from("emailCVTDAT", userDAT.getUsuario() + "@tiendatelcel.com")
						.httpOnly(false).secure(true).domain(domainTienda).path("/").maxAge(MAX_COOKIE_TIME).build();
				final ResponseCookie cookieTypeUser = ResponseCookie.from("typeUserCVTDAT", "DAT").httpOnly(false).secure(true)
						.domain(domainTienda).path("/").maxAge(MAX_COOKIE_TIME).build();
				final ResponseCookie cookieIdUser = ResponseCookie.from("idUserCVTDAT", userDAT.getUsuario()).httpOnly(false)
						.secure(true).domain(domainTienda).path("/").maxAge(MAX_COOKIE_TIME).build();
				final ResponseCookie cookieError = ResponseCookie.from("errorCVTDAT", null).httpOnly(false).secure(true)
						.domain(domainTienda).path("/").maxAge(0).build();
				return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.SET_COOKIE, cookieEmail.toString())
						.header(HttpHeaders.SET_COOKIE, cookieTypeUser.toString())
						.header(HttpHeaders.SET_COOKIE, cookieIdUser.toString()).header(HttpHeaders.SET_COOKIE, cookieError.toString())
						.location(URI.create(urlTienda)).build();
			}
			//Save user DAT
			final UserInfoDATData data = cvtdatService.saveUserInfoDAT(datb64);
			if (!Objects.isNull(data))
			{
				LOG.info("Usuario dat nuevo : guardar");
				final ResponseCookie cookieEmail = ResponseCookie.from("emailCVTDAT", data.getUsuario() + "@tiendatelcel.com")
						.httpOnly(false).secure(true).domain(domainTienda).path("/").maxAge(MAX_COOKIE_TIME).build();
				final ResponseCookie cookieTypeUser = ResponseCookie.from("typeUserCVTDAT", "DAT").httpOnly(false).secure(true)
						.domain(domainTienda).path("/").maxAge(MAX_COOKIE_TIME).build();
				final ResponseCookie cookieIdUser = ResponseCookie.from("idUserCVTDAT", data.getUsuario()).httpOnly(false)
						.secure(true).domain(domainTienda).path("/").maxAge(MAX_COOKIE_TIME).build();
				final ResponseCookie cookieError = ResponseCookie.from("errorCVTDAT", null).httpOnly(false).secure(true)
						.domain(domainTienda).path("/").maxAge(0).build();
				return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.SET_COOKIE, cookieEmail.toString())
						.header(HttpHeaders.SET_COOKIE, cookieTypeUser.toString())
						.header(HttpHeaders.SET_COOKIE, cookieIdUser.toString()).header(HttpHeaders.SET_COOKIE, cookieError.toString())
						.location(URI.create(urlTienda)).build();
			}
			else
			{
				LOG.error("No se pudo guardar el usuario info DAT");
				final ResponseCookie cookieEmail = ResponseCookie.from("emailCVTDAT", null).httpOnly(false).secure(true)
						.domain(domainTienda).path("/").maxAge(0).build();
				final ResponseCookie cookieTypeUser = ResponseCookie.from("typeUserCVTDAT", null).httpOnly(false).secure(true)
						.domain(domainTienda).path("/").maxAge(0).build();
				final ResponseCookie cookieIdUser = ResponseCookie.from("idUserCVTDAT", null).httpOnly(false).secure(true)
						.domain(domainTienda).path("/").maxAge(0).build();
				final ResponseCookie cookieError = ResponseCookie
						.from("errorCVTDAT", URLEncoder.encode("No se pudo guardar el usuario info DAT", "UTF-8")).httpOnly(false)
						.secure(true).domain(domainTienda).path("/").maxAge(MAX_COOKIE_TIME).build();
				return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.SET_COOKIE, cookieError.toString())
						.header(HttpHeaders.SET_COOKIE, cookieEmail.toString())
						.header(HttpHeaders.SET_COOKIE, cookieTypeUser.toString())
						.header(HttpHeaders.SET_COOKIE, cookieIdUser.toString()).location(URI.create(urlTienda)).build();
			}
		}
		catch (final Exception e)
		{
			LOG.error("ERROR DAT : " + e.getMessage());
			final ResponseCookie cookieEmail = ResponseCookie.from("emailCVTDAT", null).httpOnly(false).secure(true)
					.domain(domainTienda).path("/").maxAge(0).build();
			final ResponseCookie cookieTypeUser = ResponseCookie.from("typeUserCVTDAT", null).httpOnly(false).secure(true)
					.domain(domainTienda).path("/").maxAge(0).build();
			final ResponseCookie cookieIdUser = ResponseCookie.from("idUserCVTDAT", null).httpOnly(false).secure(true)
					.domain(domainTienda).path("/").maxAge(0).build();
			final ResponseCookie cookieError = ResponseCookie
					.from("errorCVTDAT", URLEncoder.encode("Ocurrio un error : " + e.getMessage(), "UTF-8")).httpOnly(false)
					.secure(true).domain(domainTienda).path("/").maxAge(MAX_COOKIE_TIME).build();
			return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.SET_COOKIE, cookieError.toString())
					.header(HttpHeaders.SET_COOKIE, cookieEmail.toString()).header(HttpHeaders.SET_COOKIE, cookieTypeUser.toString())
					.header(HttpHeaders.SET_COOKIE, cookieIdUser.toString()).location(URI.create(urlTienda)).build();
		}
	}

	@GetMapping(value = "/userInfoCVTDAT")
	public ResponseEntity<Void> userInfoCVTDAT(@RequestParam(name = "email")
	final String email)
	{
		LOG.info("User Info for CVTDAT");
		LOG.info("EMAIL : " + email);
		final String urlTienda = configurationService.getConfiguration().getString(TIENDA_EN_LINEA);
		final String domainTienda = configurationService.getConfiguration().getString(TIENDA_EN_LINEA_DOMAIN);
		final ResponseCookie springCookie = ResponseCookie.from("emailCVTDAT", email).httpOnly(false).secure(true)
				.domain(domainTienda).path("/").maxAge(MAX_COOKIE_TIME).build();
		return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.SET_COOKIE, springCookie.toString())
				.location(URI.create(urlTienda)).build();
	}

}
