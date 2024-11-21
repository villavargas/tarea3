package mx.com.telcel.controllers;



import com.amx.mexico.telcel.esb.v1_1.ControlDataRequestType;
import com.amx.telcel.di.sds.esb.sap.cancelacion.*;
import io.swagger.annotations.Api;
import mx.com.telcel.core.models.paymentcommerce.CardRefundRequest;
import mx.com.telcel.core.models.paymentcommerce.CardRefundResponse;
import mx.com.telcel.core.models.paymentcommerce.Item;
import mx.com.telcel.core.services.TelcelSoapConverterService;
import mx.com.telcel.core.services.mq.liberacionrecursos.LiberaRecursosMQService;
import mx.com.telcel.core.services.paymentcommerce.PaymentCommerceService;
import mx.com.telcel.core.util.NameSpace;
import mx.com.telcel.core.util.SOAPConverter;
import mx.telcel.utilities.AbstractProxyControllerServlet;
import mx.telcel.utilities.HttpGenericResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


/**
 * <li>https://localhost:9002/telcelb2cwebservices/liberarRecursos *
 **/
@Controller
@RequestMapping(value = "/liberarRecursos")
@Api(tags = "Release Recourses")
public class ReleaseRecoursesController extends AbstractProxyControllerServlet
{
    private static final Logger LOG = LoggerFactory.getLogger(ReleaseRecoursesController.class);

    @Resource(name = "soapConverterDS")
    private SOAPConverter soapConverterWS;

    @Resource(name = "liberaRecursosMQService")
    private LiberaRecursosMQService liberaRecursosMQService;

    @Resource(name = "paymentCommerceService")
    private PaymentCommerceService paymentCommerceService;

    @Resource(name = "telcelSoapConverterService")
    private TelcelSoapConverterService telcelSoapConverterService;

    @ResponseBody
    @GetMapping(value = "/rollback", produces = "application/json")
    public ResponseEntity<LiberarRecursosResponse>  rollback()
            throws DatatypeConfigurationException, ParserConfigurationException, JAXBException, SOAPException, IOException,
            KeyManagementException, NoSuchAlgorithmException, KeyStoreException, ParseException
    {
        final ControlDataRequestType controlData = new ControlDataRequestType();
        controlData.setMessageUUID("lib00001");
        controlData.setRequestDate(DatatypeFactory.newInstance().newXMLGregorianCalendar("2022-05-13T00:00:00.000"));
        controlData.setSendBy("ECOMM");
        controlData.setVersion("1");

        RecursoSap recursoSap = new RecursoSap();
        recursoSap.setID_PEDIDO("1131299427");
        recursoSap.setID_ENTREGA("2053708589");

        RecursoServicioTelefonico recursoServicioTelefonico = new RecursoServicioTelefonico();
        recursoServicioTelefonico.setREGION("9");
        recursoServicioTelefonico.setMSISDN("8126209865");
        recursoServicioTelefonico.setICCID("895202032049281508");
        recursoServicioTelefonico.setIMEI("353000568611811");

        OtrosRecursos otrosRecursos = new OtrosRecursos();
        otrosRecursos.setNOMBRE("");
        otrosRecursos.setVALOR("");

        GrupoRecursos grupoRecursos = new GrupoRecursos();
        grupoRecursos.setRecursoSapType(recursoSap);
        grupoRecursos.setRecursoServicioTelefonicoType(recursoServicioTelefonico);
        grupoRecursos.setOtrosRecursosType(otrosRecursos);

        ListaGrupoRecursos listaGrupoRecursos = new ListaGrupoRecursos();
        listaGrupoRecursos.setGrupoRecursosType(grupoRecursos);
        listaGrupoRecursos.setID_ELEMENTO("");

        LiberarRecursosLv2Type liberarRecursosLv2Type = new LiberarRecursosLv2Type();
        liberarRecursosLv2Type.setListaGrupoRecursosType(listaGrupoRecursos);
        liberarRecursosLv2Type.setID_TRANSACCION("");
        liberarRecursosLv2Type.setID_CANAL("");
        liberarRecursosLv2Type.setNUMEROGRUPOSRECURSOS("");


        LiberarRecursosType liberarRecursosType = new LiberarRecursosType();
        liberarRecursosType.setLiberarRecursosLv2Type(liberarRecursosLv2Type);

        LiberarRecursosRequest request = new LiberarRecursosRequest();
        request.setLiberarRecursosActivacion(liberarRecursosType);
        request.setControlData(controlData);

        final mx.com.telcel.core.util.NameSpace[] nameSpaces = new mx.com.telcel.core.util.NameSpace[]
                { new mx.com.telcel.core.util.NameSpace("lib", "http://www.amx.com.mx/mexico/telcel/di/sds/gsa/dgpsti/esb/liberacionrecursos"),
                        new NameSpace("vl", "http://amx.com/mexico/telcel/esb/v1_1") };

        final String xmlRequest = soapConverterWS.jaxbObjectToXML(request, nameSpaces);
        LOG.info(xmlRequest);


        final HttpGenericResponse genericResponse = doPostSoap(
                "http://10.10.10.23/mexico/telcel/di/sds/gsa/esb/dai/liberacionrecursos/v1", xmlRequest, HTTP_UTF8_ENCODING,
                "Realiza Liberar Recursos", false, "", "");
        LOG.info("CODE : " + genericResponse.getCode());
        final String xmlResponse = genericResponse.getResponse();
        LOG.info(xmlResponse);
        LiberarRecursosResponse mensajeResponse = new LiberarRecursosResponse();
        if (genericResponse.getCode() == 200)
        {
            final mx.telcel.utilities.NameSpace[] nameSpacesResponse = new mx.telcel.utilities.NameSpace[]
                    { new mx.telcel.utilities.NameSpace("NS1", "http://schemas.xmlsoap.org/soap/envelope/") };
            mensajeResponse = telcelSoapConverterService.xmlToJaxbObject(xmlResponse,
                    LiberarRecursosResponse.class, nameSpacesResponse);


        }
        return new ResponseEntity<LiberarRecursosResponse>(mensajeResponse, HttpStatus.OK);
    }


    @ResponseBody
    @GetMapping(value = "/rollbackMQ", produces = "application/json")
    public ResponseEntity<LiberarRecursosResponse> rollbackMQ() throws DatatypeConfigurationException, JAXBException, SOAPException, NoSuchAlgorithmException, KeyStoreException, ParserConfigurationException, IOException, KeyManagementException {
        LOG.info("entro servicio");
        //LiberarRecursosResponse liberarRecursosResponse = new LiberarRecursosResponse();
        //liberaRecursosMQService.rollbackService();
        LiberarRecursosResponse liberarRecursosResponse = liberaRecursosMQService.updateLiberarRecursosResponse();
        return new ResponseEntity<>(liberarRecursosResponse, HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping(value = "/devoluciones", produces = "application/json")
    public ResponseEntity<CardRefundResponse> devoluciones() throws DatatypeConfigurationException, JAXBException, SOAPException, NoSuchAlgorithmException, KeyStoreException, ParserConfigurationException, IOException, KeyManagementException {
        LOG.info("entro servicio");
        CardRefundResponse cardRefundResponse = new CardRefundResponse();
        CardRefundRequest cardRefundRequest = new CardRefundRequest();

        cardRefundRequest.setOrderId("TEST-s768");
        cardRefundRequest.setCommerceId("ASF1521150");
        cardRefundRequest.setPaymentId("ASD5168g00");
        cardRefundRequest.setEmail("gestorpagos@gluo.mx");
        cardRefundRequest.setLastDigits("1111");
        cardRefundRequest.setFirstname("Svend");
        cardRefundRequest.setLastname("Haraldsen");
        cardRefundRequest.setRefundAmount(899.0);
        cardRefundRequest.setRefundType("COMPLETE");
        List<Item> listItems = new ArrayList<>();
        Item item = new Item();
        item.setSku("GY150");
        item.setDescription("Alcatel 4034t");
        item.setPrice(899.0);
        item.setModel("4034t");
        item.setColor("Black");
        listItems.add(item);
        cardRefundRequest.setItems(listItems);
        cardRefundResponse = paymentCommerceService.cardRefund(cardRefundRequest, "32s7682");
        return new ResponseEntity<CardRefundResponse>(cardRefundResponse, HttpStatus.OK);
    }

}

