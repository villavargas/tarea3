package mx.com.telcel.controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.annotations.Api;
import mx.com.telcel.models.individual.IndividualResponse;
import mx.com.telcel.services.IndividualService;


@Controller
@RequestMapping(value = "/individual")
@Api(tags = "Individual")
public class IndividualController
{

	@Resource(name = "individualService")
	private IndividualService individualService;

	private static final Logger LOG = Logger.getLogger(IndividualController.class);

	/**
	 * Service for validate RFC<br/>
	 * Example :</br>
	 * GET https://localhost:9002/telcelb2cwebservices/individual/validateRFC</br>
	 * Method : GET</br>
	 *
	 * @param rfc
	 *           - RFC</br>
	 * @return - ResponseEntity object</br>
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 *
	 */
	@GetMapping(value = "/validateRFC")
	public ResponseEntity<IndividualResponse> validateRFC(@RequestParam(name = "rfc")
	final String rfc) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException, URISyntaxException
	{
		final IndividualResponse response = individualService.validateRFC(rfc);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
