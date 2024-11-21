package mx.com.telcel.fulfilmentprocess.activation;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import mx.com.telcel.facades.order.data.ProductOrderingResponse;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public interface TelcelActivationService {

    ProductOrderingResponse activationPost(String region,
                                           String postalCode,
                                           String imei,
                                           String iccid,
                                           String salesForce,
                                           String salesForceAlphanumeric,
                                           AbstractOrderEntryModel entryModel,
                                           String esquemaCobro,
                                           ConsignmentModel consignmentModel,
                                           String store)
            throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException;

    @Async
    void ejecutaCronMensajesMq();

}
