package mx.com.telcel.services.impl;

import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;

import mx.com.telcel.core.service.AESService;
import org.apache.log4j.Logger;

import com.amx.mexico.telcel.esb.v1_1.ControlDataRequestType;

import mx.com.amx.mexico.telcel.di.sds.gsa.dai.esb.consultafacturas.ConsultaFacturaPDFXML;
import mx.com.amx.mexico.telcel.di.sds.gsa.dai.esb.consultafacturas.ConsultaFacturaPDFXMLPetType;
import mx.com.amx.mexico.telcel.di.sds.gsa.dai.esb.consultafacturas.ConsultaFacturaPDFXMLResponse;
import mx.com.telcel.services.InvoiceService;
import mx.com.telcel.utilities.AbstractProxyControllerServlet;
import mx.com.telcel.utilities.HttpGenericResponse;
import mx.com.telcel.utilities.NameSpace;
import mx.com.telcel.utilities.SOAPConverter;


public class InvoiceServiceImpl extends AbstractProxyControllerServlet implements InvoiceService
{

	private static final String INVOICE_PDF_URL_SOAP = "telcel.invoice.pdf.service";
	private static final String INVOICE_PDF_USER = "telcel.invoice.pdf.user";
	private static final String INVOICE_PDF_PASSWORD = "telcel.invoice.pdf.password";

	private static final Logger LOG = Logger.getLogger(InvoiceServiceImpl.class);

	@Resource(name = "defaultConfigurationService")
	private ConfigurationService configurationService;

	@Resource(name = "soapConverter")
	private SOAPConverter soapConverter;

	@Resource(name = "aesService")
	private AESService aesService;

	@Override
	public ConsultaFacturaPDFXMLResponse getInvoice(final String invoiceNumber)
			throws ParserConfigurationException, JAXBException, SOAPException, IOException, KeyManagementException,
			NoSuchAlgorithmException, KeyStoreException, DatatypeConfigurationException
	{
		final String url = configurationService.getConfiguration().getString(INVOICE_PDF_URL_SOAP);
		final String user = configurationService.getConfiguration().getString(INVOICE_PDF_USER);
		final String password = configurationService.getConfiguration().getString(INVOICE_PDF_PASSWORD);

		LOG.info("URL INVOICE SERVICE: " + url);

		final ControlDataRequestType controlData = new ControlDataRequestType();
		controlData.setMessageUUID("" + System.currentTimeMillis() + "inv");
		final GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		controlData.setRequestDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar));
		controlData.setSendBy("tienda");
		controlData.setVersion("1");

		final ConsultaFacturaPDFXMLPetType consultaFacturaPDFXML = new ConsultaFacturaPDFXMLPetType();
		consultaFacturaPDFXML.setUsuario(user);
		consultaFacturaPDFXML.setPassword(aesService.decrypt(password));
		consultaFacturaPDFXML.setFactura(invoiceNumber);
		consultaFacturaPDFXML.setOpcion(3);

		final ConsultaFacturaPDFXML request = new ConsultaFacturaPDFXML();
		request.setControlData(controlData);
		request.setConsultaFacturaPDFXML(consultaFacturaPDFXML);
		final NameSpace[] nameSpaces = new NameSpace[]
		{ new NameSpace("con", "http://www.amx.com.mx/mexico/telcel/di/sds/gsa/dai/esb/consultafacturas"),
				new NameSpace("vl", "http://amx.com/mexico/telcel/esb/v1_1") };

		final String xmlRequest = soapConverter.jaxbObjectToXML(request, nameSpaces);

		LOG.info("XML REQUEST INVOICE SERVICE: " + xmlRequest);

		final HttpGenericResponse genericResponse = doPostSoap(url, xmlRequest, HTTP_UTF8_ENCODING, "Consulta Factura PDF", false,
				"", "");
		final String xmlResponse = genericResponse.getResponse();
		LOG.info("CODE RESPONSE INVOICE SERVICE: " + genericResponse.getCode());
		LOG.info("XML RESPONSE INVOICE SERVICE: " + xmlResponse);
		if (genericResponse.getCode() == 200)
		{
			final NameSpace[] nameSpacesResponse = new NameSpace[]
			{ new NameSpace("NS1", "http://schemas.xmlsoap.org/soap/envelope/"),
					new NameSpace("apl", "http://www.amx.com.mx/mexico/telcel/di/sds/gsa/dai/esb/consultafacturas"),
					new NameSpace("esb", "http://amx.com/mexico/telcel/esb/v1_1") };
			return soapConverter.xmlToJaxbObject(xmlResponse, ConsultaFacturaPDFXMLResponse.class, nameSpacesResponse);
		}
		return null;
	}

}