package mx.com.telcel.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.server.endpoint.AbstractDomPayloadEndpoint;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import mx.com.telcel.notificacionfacturatelmex.NotificacionFacturaRequest;
import mx.com.telcel.notificacionfacturatelmex.NotificacionFacturaResponse;
import mx.com.telcel.utilities.SOAPConverter;


public class NotificacionFacturaEndPoint extends AbstractDomPayloadEndpoint
{

	private SOAPConverter soapConverter;

	private NotificacionFacturaRequestProcessor procesor;

	private static final Logger LOG = LoggerFactory.getLogger(NotificacionFacturaEndPoint.class);

	public void setProcesor(final NotificacionFacturaRequestProcessor procesor)
	{
		this.procesor = procesor;
	}

	public void setSoapConverter(final SOAPConverter soapConverter)
	{
		this.soapConverter = soapConverter;
	}


	@Override
	protected Element invokeInternal(final Element domRequest, final Document document) throws Exception
	{
		LOG.info("Notificacion Factura XML");
		final NotificacionFacturaRequest request = this.xmlToInfoRequest(domRequest);
		final NotificacionFacturaResponse response = this.procesor.process(request);
		return this.responseToXml(response);

	}

	private NotificacionFacturaRequest xmlToInfoRequest(final Element request)
	{
		NotificacionFacturaRequest getCountryRequest = null;
		try
		{
			final TransformerFactory transformerFactory = TransformerFactory.newInstance(); // NOSONAR
			final Transformer transformer = transformerFactory.newTransformer();
			final DOMSource source = new DOMSource(request);
			final StreamResult result = new StreamResult(new StringWriter());
			transformer.transform(source, result);
			final String xml = result.getWriter().toString();
			final JAXBContext jaxbContext = JAXBContext.newInstance(NotificacionFacturaRequest.class);
			final Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			getCountryRequest = (NotificacionFacturaRequest) jaxbUnmarshaller.unmarshal(new StringReader(xml));
		}
		catch (final Exception e)
		{
			LOG.error("Transform Request Error : " + e.getMessage());
		}
		return getCountryRequest;
	}

	private Element responseToXml(final NotificacionFacturaResponse response)
			throws ParserConfigurationException, JAXBException, SOAPException, IOException, SAXException
	{
		Element elementResponse = null;
		try
		{
			final String xmlResponse = this.soapConverter.jaxbObjectToXMLSingle(response, null);
			LOG.info("XML RESPONSE : " + xmlResponse);
			elementResponse = DocumentBuilderFactory.newInstance().newDocumentBuilder() // NOSONAR
					.parse(new ByteArrayInputStream(xmlResponse.getBytes())).getDocumentElement();
		}
		catch (final Exception e)
		{
			LOG.error("Transform Response Error : " + e.getMessage());
		}
		return elementResponse;
	}



}
