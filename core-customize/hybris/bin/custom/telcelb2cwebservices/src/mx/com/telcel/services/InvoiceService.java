package mx.com.telcel.services;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;

import mx.com.amx.mexico.telcel.di.sds.gsa.dai.esb.consultafacturas.ConsultaFacturaPDFXMLResponse;


public interface InvoiceService
{

	public ConsultaFacturaPDFXMLResponse getInvoice(String invoiceNumber)
			throws ParserConfigurationException, JAXBException, SOAPException, IOException, KeyManagementException,
			NoSuchAlgorithmException, KeyStoreException, DatatypeConfigurationException;

}
