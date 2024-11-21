/**
 *
 */
package mx.com.telcel.controllers;

import static java.lang.String.valueOf;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.ApiParam;
import mx.com.telcel.core.daos.zipcode.TelcelCodigosPostalesDao;
import mx.com.telcel.core.model.CodigosPostalesTelcelModel;
import mx.com.telcel.core.services.TelcelFedexService;
import mx.com.telcel.core.services.TelcelSoapConverterService;
import mx.com.telcel.models.fedex.Account;
import mx.com.telcel.models.fedex.Address;
import mx.com.telcel.models.fedex.Characteristic;
import mx.com.telcel.models.fedex.ContactMedium;
import mx.com.telcel.models.fedex.Individual;
import mx.com.telcel.models.fedex.MediumCharacteristic;
import mx.com.telcel.models.fedex.Order;
import mx.com.telcel.models.fedex.OrderItem;
import mx.com.telcel.models.fedex.Organization;
import mx.com.telcel.models.fedex.Party;
import mx.com.telcel.models.fedex.Quantity;
import mx.com.telcel.models.fedex.Role;
import mx.com.telcel.models.fedex.Service;
import mx.com.telcel.models.fedex.TimePeriod;
import mx.com.telcel.models.fedex.request.FedexRequest;
import mx.com.telcel.models.fedex.request.HeaderRequest;
import mx.com.telcel.models.fedex.response.FedexRateResponse;
import mx.com.telcel.services.CustomValidationService;
import mx.com.telcel.services.FedexService;
import mx.com.telcel.utilities.AbstractProxyControllerServlet;



/**
 * @author luis.patino
 * @author marco.osnaya
 *
 */

@Controller
@RequestMapping(value = "/fedex")
public class FedexServicesController extends AbstractProxyControllerServlet
{

    private static final Logger LOG = Logger.getLogger(FedexServicesController.class);

    @Resource(name = "fedexService")
    private FedexService fedexService;

    @Resource(name = "telcelFedexService")
    private TelcelFedexService telcelFedexService;

    @Resource
    private SessionService sessionService;
    private static final String TELCEL_REGION = "telcelRegion";

    @Resource(name = "customValidationService")
    private CustomValidationService validationService;

    @Resource(name = "telcelCodigosPostalesDao")
    private TelcelCodigosPostalesDao telcelCodigosPostalesDao;

	 @Resource(name = "defaultConfigurationService")
	 private ConfigurationService configurationService;

	 @Resource(name = "telcelSoapConverterService")
	 private TelcelSoapConverterService telcelSoapConverterService;

    @Resource
    private UserService userService;

    private static final String SEND_BY = "order.fedex.SendBy";
    private static final String PACKAGES = "order.fedex.packages";
    private static final String PACKAGING_TYPE = "order.fedex.rate.service.order.characteristics1.name";
    private static final String PACKAGING_TYPE_VALUE = "order.fedex.rate.service.order.characteristics1.value";
    private static final String PAYMENT_TYPE = "order.fedex.rate.service.order.characteristics2.name";
    private static final String PAYMENT_TYPE_VALUE = "order.fedex.rate.service.order.characteristics2.value";
    private static final String REGULAR_PICKUP = "order.fedex.rate.service.order.regularPickup";
    private static final String ONE = "order.fedex.rate.value.one";
    private static final String KG = "order.fedex.rate.kg";
    private static final String SHIPPER = "order.fedex.rate.party.role1.name";
    private static final String RECIPIENT = "order.fedex.rate.party.role2.name";
    private static final String PAYOR = "order.fedex.rate.party.role3.name";
    private static final String COMPANY_NAME = "order.fedex.rate.party.role.charactertictics1.name";
    private static final String LEGAL_ENTITY ="order.fedex.rate.party.type";
    private static final String INDIVIDUAL ="order.fedex.rate.party2.type";
    private static final String MESSAGEUIID="order.fedex.messageUiid";
    private static final String ACCOUNTNUMBER="fedex.services.account.number";
    private static final String RFC ="order.fedex.rate.rfc";
	 String SECURITY_CODE_TEST = "telcel.performance.test.enabled";
    /**
     * Service FEDEX delivery date<br/>
     * Example :</br>
     * POST https://localhost:9002/telcelb2cwebservices/fedex/retrieveRate?postalCode=00000</br>
     *
     * Method : GET</br>
     *
     * @param postalCode
     *           - postal code[5 digits]</br>
     * @return - DeliveryDateResponse object</br>
     * @throws IOException
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     * @throws URISyntaxException
     * @throws CertificateException
     *
     */
    @ResponseBody
    @GetMapping(value = "/retrieveRate", produces = "application/json")
    public Object retrieveRate(@ApiParam(value = "Postal Code", required = true)
                               @RequestParam(name = "postalCode", required = true)
                                       String postalCode,
                               @RequestParam(name = "cartId", required = true)
                                       String cartId)
            throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException
    {
        LOG.info("Servicio RATE FEDEX, usando C.P. "+postalCode);
		  final String security_code_test = configurationService.getConfiguration().getString(SECURITY_CODE_TEST);
		  LocalDateTime localDate = null;
		  LocalDateTime localDateEntrega = null;
		  final int days = 0;

		  LOG.info("Fedex security_code_test " + security_code_test);

            UserModel user = userService.getCurrentUser();
            String uid =	user != null ? user.getUid() : "";
            Pattern pat = Pattern.compile(".*@saplogtest.com.*");
            Matcher mat = pat.matcher(uid);

		  if (security_code_test.equals("true") && mat.matches())
		  {
			  final FedexRateResponse fedexRate = new FedexRateResponse();

			  localDate = LocalDateTime.now();
			  localDateEntrega = localDate.plusDays(telcelFedexService.SumarCuatroDias(localDate, days));
			  final String month = telcelSoapConverterService.converterStringMonth(localDateEntrega.getMonthValue());
			  fedexRate.setDescription(String.valueOf(localDateEntrega.getDayOfMonth()) + " de " + month + " de "
					  + String.valueOf(localDateEntrega.getYear()));
			  fedexRate.setCode(0);

			  LOG.info("fedexRate.getCode(): " + fedexRate.getCode());
			  LOG.info("fedexRate.getDeliveryDate(): " + fedexRate.getDeliveryDate());

			  return fedexRate;
		  }

        final CodigosPostalesTelcelModel model = telcelCodigosPostalesDao.getInfForZipcode(postalCode);
        if(model != null){

            LOG.info("retrieve rate service 111 ...");
            sessionService.setAttribute(TELCEL_REGION, "R1");
            LOG.info("retrieve rate service 222 ...");
            sessionService.getAttribute(TELCEL_REGION);
            final TimePeriod validFor = new TimePeriod();
            validFor.setStartDateTime(valueOf(Instant.now()));
            final Quantity quantity = new Quantity();
            quantity.setUnit(Config.getParameter(PACKAGES));
            quantity.setAmount(Config.getParameter(ONE));
            final Characteristic c1 = new Characteristic();
            c1.setName(Config.getParameter(PACKAGING_TYPE));
            c1.setValue(Config.getParameter(PACKAGING_TYPE_VALUE));
            final Characteristic c2 = new Characteristic();
            c2.setName(Config.getParameter(PAYMENT_TYPE));
            c2.setValue(Config.getParameter(PAYMENT_TYPE_VALUE));
            final List<Characteristic> characteristics = new ArrayList<Characteristic>();
            characteristics.add(c1);
            characteristics.add(c2);
            final OrderItem orderItem = new OrderItem();
            final Quantity weight = new Quantity();
            weight.setUnit(Config.getParameter(KG));
            weight.setAmount(Config.getParameter(ONE));
            orderItem.setItemSequence(Config.getParameter(ONE));
            orderItem.setItemGroup(Config.getParameter(ONE));
            orderItem.setWeight(weight);
            final List<OrderItem> orderItems = new ArrayList<OrderItem>();
            orderItems.add(orderItem);

            final Order order = new Order();
            order.setType(Config.getParameter(REGULAR_PICKUP));
            order.setName(Config.getParameter(REGULAR_PICKUP));
            order.setValidFor(validFor);
            order.setQuantity(quantity);
            order.setCharacteristics(characteristics);
            order.setOrderItem(orderItems);

            final Service service = new Service();
            service.setOrder(order);

            //PARTY
            //***Party 1
            final Organization organization1 = new Organization();
            organization1.setOrganizationName("");
            organization1.setTradingName("VL01 R9");
            final Role role1 = new Role();
            role1.setName(Config.getParameter(SHIPPER));
            final Address address1 = new Address();
            address1.setLocality(model.getRegion().getCode());
            address1.setIsResidential("true");
            final MediumCharacteristic mediumCharacteristic1 = new MediumCharacteristic();
            mediumCharacteristic1.setPhoneNumber("0");
            final ContactMedium contactMedium1 = new ContactMedium();
            contactMedium1.setContactMediumCharacteristic(mediumCharacteristic1);
            final List<ContactMedium> contactsMediums1 = new ArrayList<ContactMedium>();
            contactsMediums1.add(contactMedium1);
            final Account account1 = new Account();
            account1.setAccountNumber(Config.getParameter(ACCOUNTNUMBER));
            final List<Account> accounts1 = new ArrayList<Account>();
            accounts1.add(account1);
            final Party party1 = new Party();
            party1.setType(Config.getParameter(LEGAL_ENTITY));
            party1.setId("VL01");
            party1.setIsLegalEntity("true");
            party1.setOrganization(organization1);
            party1.setRole(role1);
            party1.setAddress(address1);
            party1.setContactMedium(contactsMediums1);
            party1.setAccount(accounts1);
            //***Party 2
            final Individual individual2 = new Individual();
            individual2.setName("Prueba");
            individual2.setLastName("Tienda");
            individual2.setSecondLastName("Linea");
            final Role role2 = new Role();
            role2.setName(Config.getParameter(RECIPIENT));
            final Characteristic cp21 = new Characteristic();
            cp21.setName(Config.getParameter(COMPANY_NAME));
            cp21.setValue("NVATIENDAL");
            final Characteristic cp22 = new Characteristic();
            cp22.setName("RFC");
            cp22.setValue(Config.getParameter(RFC));
            final List<Characteristic> characteristicsp2 = new ArrayList<Characteristic>();
            characteristicsp2.add(cp21);
            final Address address2 = new Address();
            address2.setZipCode(postalCode);
            address2.setCity(model.getCiudad());
            address2.setStateOrProvince(model.getEstado().getIsocodeShort().substring(0,1));
            address2.setCountry(model.getEstado().getCountry().getIsocode());
            address2.setIsResidential("true");
            final MediumCharacteristic mediumCharacteristic2 = new MediumCharacteristic();
            mediumCharacteristic2.setPhoneNumber("0");
            final ContactMedium contactMedium2 = new ContactMedium();
            contactMedium2.setPreferred("true");
            contactMedium2.setContactMediumCharacteristic(mediumCharacteristic2);
            final List<ContactMedium> contactsMediums2 = new ArrayList<ContactMedium>();
            contactsMediums2.add(contactMedium2);
            final Account account2 = new Account();
            account2.setAccountNumber(Config.getParameter(ACCOUNTNUMBER));
            final List<Account> accounts2 = new ArrayList<Account>();
            accounts2.add(account1);
            final Party party2 = new Party();
            party2.setType(Config.getParameter(INDIVIDUAL));
            party2.setIsLegalEntity("false");
            party2.setIndividual(individual2);
            party2.setRole(role2);
            party2.setCharacteristics(characteristicsp2);
            party2.setAddress(address2);
            party2.setContactMedium(contactsMediums2);
            party2.setAccount(accounts2);
            //***Party 3
            final Organization organization3 = new Organization();
            organization3.setOrganizationName("");
            organization3.setTradingName("VL01 R9");
            final Role role3 = new Role();
            role3.setName(Config.getParameter(PAYOR));
            final Address address3 = new Address();
            address3.setLocality(model.getRegion().getCode());
            address3.setIsResidential("true");
            final MediumCharacteristic mediumCharacteristic3 = new MediumCharacteristic();
            mediumCharacteristic3.setPhoneNumber("0");
            final ContactMedium contactMedium3 = new ContactMedium();
            contactMedium3.setContactMediumCharacteristic(mediumCharacteristic3);
            final List<ContactMedium> contactsMediums3 = new ArrayList<ContactMedium>();
            contactsMediums3.add(contactMedium3);
            final Account account3 = new Account();
            account3.setAccountNumber(Config.getParameter(ACCOUNTNUMBER));
            final List<Account> accounts3 = new ArrayList<Account>();
            accounts3.add(account3);
            final Party party3 = new Party();
            party3.setType(Config.getParameter(LEGAL_ENTITY));
            party3.setId("VL01");//dinamico
            party3.setIsLegalEntity("true");
            party3.setOrganization(organization3);
            party3.setRole(role3);
            party3.setAddress(address3);
            party3.setContactMedium(contactsMediums3);
            party3.setAccount(accounts3);

            final List<Party> parties = new ArrayList<Party>();
            parties.add(party1);
            parties.add(party2);
            parties.add(party3);
            //Request
            final FedexRequest request = new FedexRequest();
            request.setService(service);
            request.setParty(parties);

            final HeaderRequest headers = new HeaderRequest();
            headers.setMessageUUID(Config.getParameter(MESSAGEUIID));//Obligatorio
            headers.setRequestDate(valueOf(Instant.now()));//Obligatorio
            headers.setSendBy(Config.getParameter(SEND_BY));//Obligatorio
            headers.setVersion(Config.getParameter(ONE));//Obligatorio
            headers.setUser(Config.getParameter(SEND_BY));

            return telcelFedexService.retrieveRate(request, headers, cartId);

        } else {
            final FedexRateResponse fedexRate = new FedexRateResponse();
            fedexRate.setDescription("El código postal es incorrecto");
            fedexRate.setCode(-1);
            return fedexRate;
        }
    }

}
