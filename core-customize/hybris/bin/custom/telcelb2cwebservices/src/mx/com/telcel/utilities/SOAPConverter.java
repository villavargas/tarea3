package mx.com.telcel.utilities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Objects;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.w3c.dom.Document;


public class SOAPConverter
{

	public String jaxbObjectToXML(final Object T, final NameSpace[] namespaces)
			throws ParserConfigurationException, JAXBException, SOAPException, IOException
	{
		String output = "";
		final Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		final JAXBContext context = JAXBContext.newInstance(T.getClass());
		final Marshaller m = context.createMarshaller();
		final MessageFactory mf = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
		final SOAPMessage soapMessage = mf.createMessage();
		soapMessage.getSOAPHeader().setPrefix("soapenv");
		soapMessage.getSOAPBody().setPrefix("soapenv");
		// for pretty-print XML in JAXB
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		if (Objects.nonNull(namespaces))
		{
			m.setProperty("com.sun.xml.bind.namespacePrefixMapper", new PrefixMapper(namespaces));
		}
		m.marshal(T, document);
		final SOAPPart part = soapMessage.getSOAPPart();
		final SOAPEnvelope envelope = part.getEnvelope();
		envelope.setPrefix("soapenv");
		if (Objects.nonNull(namespaces))
		{
			for (final NameSpace namespace : namespaces)
			{
				envelope.addNamespaceDeclaration(namespace.getPrefix(), namespace.getUri());
			}
		}
		envelope.removeNamespaceDeclaration("SOAP-ENV");
		soapMessage.getSOAPBody().addDocument(document);
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		soapMessage.writeTo(outputStream);
		output = new String(outputStream.toByteArray());
		return output;
	}
	
	public String jaxbObjectToXMLSingle(final Object object, final NameSpace[] namespaces) throws JAXBException
	{
		final JAXBContext jaxbContext = JAXBContext.newInstance(object.getClass());
		final Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
		if (Objects.nonNull(namespaces))
		{
			jaxbMarshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new PrefixMapper(namespaces));
		}
		final StringWriter sw = new StringWriter();
		jaxbMarshaller.marshal(object, sw);
		return sw.toString();
	}

	public <T> T xmlToJaxbObject(final String xmlString, final Class<T> type, final NameSpace[] namespaces)
			throws JAXBException, SOAPException, IOException
	{
		final SOAPMessage soapMessage = xmlToSoapMessage(xmlString, namespaces);
		final Unmarshaller unmarshaller = JAXBContext.newInstance(type).createUnmarshaller();
		final T response = (T) unmarshaller.unmarshal(soapMessage.getSOAPBody().extractContentAsDocument());
		return response;
	}

	private SOAPMessage xmlToSoapMessage(final String xmlString, final NameSpace[] namespaces) throws SOAPException, IOException
	{
		SOAPMessage soapMessage = null;
		final InputStream is = new ByteArrayInputStream(xmlString.getBytes());
		soapMessage = MessageFactory.newInstance().createMessage(null, is);
		final SOAPEnvelope soapEnvelope = soapMessage.getSOAPPart().getEnvelope();
		if (Objects.nonNull(namespaces))
		{
			for (final NameSpace namespace : namespaces)
			{
				soapEnvelope.addNamespaceDeclaration(namespace.getPrefix(), namespace.getUri());
			}
		}
		soapMessage.saveChanges();
		return soapMessage;
	}

}
