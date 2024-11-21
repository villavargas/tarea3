
package com.amx.telcel.di.sds.esb.logistica.fedex.rate;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import com.amx.mexico.telcel.esb.v1_0_2.DetailType;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.amx.telcel.di.sds.esb.logistica.fedex.rate package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _GetRates_QNAME = new QName("http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate", "getRates");
    private final static QName _GetRatesResponse_QNAME = new QName("http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate", "getRatesResponse");
    private final static QName _GetRatesExcep_QNAME = new QName("http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate", "getRatesExcep");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.amx.telcel.di.sds.esb.logistica.fedex.rate
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetRatesOpPetType }
     * 
     */
    public GetRatesOpPetType createGetRatesOpPetType() {
        return new GetRatesOpPetType();
    }

    /**
     * Create an instance of {@link GetRatesOpRespType }
     * 
     */
    public GetRatesOpRespType createGetRatesOpRespType() {
        return new GetRatesOpRespType();
    }

    /**
     * Create an instance of {@link GetRatesPetType }
     * 
     */
    public GetRatesPetType createGetRatesPetType() {
        return new GetRatesPetType();
    }

    /**
     * Create an instance of {@link GetRatesRespType }
     * 
     */
    public GetRatesRespType createGetRatesRespType() {
        return new GetRatesRespType();
    }

    /**
     * Create an instance of {@link WebAuthenticationCredential }
     * 
     */
    public WebAuthenticationCredential createWebAuthenticationCredential() {
        return new WebAuthenticationCredential();
    }

    /**
     * Create an instance of {@link WebAuthenticationDetail }
     * 
     */
    public WebAuthenticationDetail createWebAuthenticationDetail() {
        return new WebAuthenticationDetail();
    }

    /**
     * Create an instance of {@link ClientDetail }
     * 
     */
    public ClientDetail createClientDetail() {
        return new ClientDetail();
    }

    /**
     * Create an instance of {@link VersionId }
     * 
     */
    public VersionId createVersionId() {
        return new VersionId();
    }

    /**
     * Create an instance of {@link Contact }
     * 
     */
    public Contact createContact() {
        return new Contact();
    }

    /**
     * Create an instance of {@link Address }
     * 
     */
    public Address createAddress() {
        return new Address();
    }

    /**
     * Create an instance of {@link Party }
     * 
     */
    public Party createParty() {
        return new Party();
    }

    /**
     * Create an instance of {@link Payor }
     * 
     */
    public Payor createPayor() {
        return new Payor();
    }

    /**
     * Create an instance of {@link Payment }
     * 
     */
    public Payment createPayment() {
        return new Payment();
    }

    /**
     * Create an instance of {@link Weight }
     * 
     */
    public Weight createWeight() {
        return new Weight();
    }

    /**
     * Create an instance of {@link RequestedPackageLineItem }
     * 
     */
    public RequestedPackageLineItem createRequestedPackageLineItem() {
        return new RequestedPackageLineItem();
    }

    /**
     * Create an instance of {@link RequestedShipment }
     * 
     */
    public RequestedShipment createRequestedShipment() {
        return new RequestedShipment();
    }

    /**
     * Create an instance of {@link Notification }
     * 
     */
    public Notification createNotification() {
        return new Notification();
    }

    /**
     * Create an instance of {@link TransactionDetail }
     * 
     */
    public TransactionDetail createTransactionDetail() {
        return new TransactionDetail();
    }

    /**
     * Create an instance of {@link CommitDetail }
     * 
     */
    public CommitDetail createCommitDetail() {
        return new CommitDetail();
    }

    /**
     * Create an instance of {@link CurrencyExchangeRate }
     * 
     */
    public CurrencyExchangeRate createCurrencyExchangeRate() {
        return new CurrencyExchangeRate();
    }

    /**
     * Create an instance of {@link Money }
     * 
     */
    public Money createMoney() {
        return new Money();
    }

    /**
     * Create an instance of {@link ShipmentRateDetail }
     * 
     */
    public ShipmentRateDetail createShipmentRateDetail() {
        return new ShipmentRateDetail();
    }

    /**
     * Create an instance of {@link RatedShipmentDetail }
     * 
     */
    public RatedShipmentDetail createRatedShipmentDetail() {
        return new RatedShipmentDetail();
    }

    /**
     * Create an instance of {@link RateReplyDetail }
     * 
     */
    public RateReplyDetail createRateReplyDetail() {
        return new RateReplyDetail();
    }

    /**
     * Create an instance of {@link Surcharge }
     * 
     */
    public Surcharge createSurcharge() {
        return new Surcharge();
    }

    /**
     * Create an instance of {@link Tax }
     * 
     */
    public Tax createTax() {
        return new Tax();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRatesOpPetType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link GetRatesOpPetType }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate", name = "getRates")
    public JAXBElement<GetRatesOpPetType> createGetRates(GetRatesOpPetType value) {
        return new JAXBElement<GetRatesOpPetType>(_GetRates_QNAME, GetRatesOpPetType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRatesOpRespType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link GetRatesOpRespType }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate", name = "getRatesResponse")
    public JAXBElement<GetRatesOpRespType> createGetRatesResponse(GetRatesOpRespType value) {
        return new JAXBElement<GetRatesOpRespType>(_GetRatesResponse_QNAME, GetRatesOpRespType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DetailType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link DetailType }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate", name = "getRatesExcep")
    public JAXBElement<DetailType> createGetRatesExcep(DetailType value) {
        return new JAXBElement<DetailType>(_GetRatesExcep_QNAME, DetailType.class, null, value);
    }

}
