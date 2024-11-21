package mx.com.telcel.services;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import mx.com.telcel.models.individual.IndividualResponse;


public interface IndividualService
{

	public IndividualResponse validateRFC(String rfc)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException, URISyntaxException;

}
