package mx.com.telcel.services;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import mx.com.telcel.models.fedex.request.FedexRequest;
import mx.com.telcel.models.fedex.request.HeaderRequest;


public interface FedexService
{

	Object retrieveRate(final FedexRequest request, final HeaderRequest headerRequest)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException;

	Object createShipment(final FedexRequest request, final HeaderRequest headerRequest)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException;

	Object retrieveTrack(Map<String, String> parameters, final HeaderRequest headerRequest)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException, URISyntaxException;

	Object createPickup(final FedexRequest request, final HeaderRequest headerRequest)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException;

	Object retrievePickup(final Map<String, String> parameters, final HeaderRequest headerRequest)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException, URISyntaxException;

	void removeGuideFromConsignment(ConsignmentModel consignmentModel);

}
