/*
 *
 * [y] hybris Platform
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package mx.com.telcel.controllers;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressWsDTO;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.util.localization.Localization;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import mx.com.amx.mexico.telcel.di.sds.gsa.dai.esb.consultafacturas.ConsultaFacturaPDFXMLResponse;
import mx.com.telcel.core.service.BillingService;
import mx.com.telcel.data.ConceptBillingList;
import mx.com.telcel.data.RegimenCapitalDataList;
import mx.com.telcel.data.RegimenFiscalDataList;
import mx.com.telcel.dto.ConceptsBillingListWsDTO;
import mx.com.telcel.dto.RegimenCapitalListWsDTO;
import mx.com.telcel.dto.RegimenFiscalesListWsDTO;
import mx.com.telcel.models.individual.IndividualResponse;
import mx.com.telcel.services.IndividualService;
import mx.com.telcel.services.InvoiceService;


@Controller
@RequestMapping(value = "/billing")
@CacheControl(directive = CacheControlDirective.NO_STORE)
@Api(tags = "Billing Controller")
public class BillingController
{

	private static final Logger LOG = Logger.getLogger(BillingController.class);
	protected static final String DEFAULT_FIELD_SET = FieldSetLevelHelper.DEFAULT_LEVEL;
	private static final String ADDRESS_MAPPING = "firstName,lastName,titleCode,phone,line1,line2,town,postalCode,country(isocode),region(isocode),defaultAddress,district,externalNumber,interiorNumber,references,razonSocial,rfc,conceptoDescription,regimenDescription,regimenCapitalDescription,concepto,regimen,regimenCapital";
	public static final String ZIP = ".zip";
	public static final String PDF = ".pdf";
	public static final String XML = ".xml";
	public static final String BASE_SITE_ID = "telcel";

	@Resource(name = "billingService")
	private BillingService billingService;

	@Resource(name = "dataMapper")
	private DataMapper dataMapper;

	@Resource
	private ModelService modelService;

	@Resource
	private Populator<AddressData, AddressModel> addressReversePopulator;

	@Resource
	private Populator<AddressData, AddressModel> telcelAddressReversePopulator;

	@Resource(name = "tmaCustomerAccountService")
	private CustomerAccountService customerAccountService;


	@Resource(name = "userFacade")
	private UserFacade userFacade;

	@Resource(name = "userService")
	private UserService userService;

	@Resource
	private CommerceCartService commerceCartService;

	@Resource
	private BaseSiteService baseSiteService;


	@Resource(name = "individualService")
	private IndividualService individualService;

	@Resource
	private final Converter<AddressModel, AddressData> addressConverter;

	public BillingController(final Converter<AddressModel, AddressData> addressConverter)
	{
		this.addressConverter = addressConverter;
	}


	@Resource(name = "invoiceService")
	private InvoiceService invoiceService;

	@RequestMapping(value = "/getpdftest", method = RequestMethod.GET)
	public ResponseEntity<byte[]> getPDF()
	{


		final File file = new File("./test.pdf");

		try (FileOutputStream fos = new FileOutputStream(file);)
		{
			// To be short I use a corrupted PDF string, so make sure to use a valid one if you want to preview the PDF file
			final String b64 = "";
			final byte[] decoder = Base64.getDecoder().decode(b64);

			fos.write(decoder);
			System.out.println("PDF File Saved");
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}


		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		final String filename = "output.pdf";
		headers.setContentDispositionFormData(filename, filename);
		headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
		ResponseEntity<byte[]> response = null;
		try
		{
			response = new org.springframework.http.ResponseEntity<>(Files.readAllBytes(file.toPath()), headers,
					org.springframework.http.HttpStatus.OK);
		}
		catch (final java.io.IOException e)
		{
			e.printStackTrace();
		}
		return response;
	}

	@ResponseBody
	@GetMapping(value = "/getInvoice", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<byte[]> getInvoicePdf(@RequestParam(name = "invoiceNumber")
	final String invoiceNumber) throws ParserConfigurationException, JAXBException, SOAPException, IOException,
			KeyManagementException, NoSuchAlgorithmException, KeyStoreException, DatatypeConfigurationException
	{
		final ConsultaFacturaPDFXMLResponse responseWs = invoiceService.getInvoice(invoiceNumber);


		if (Objects.nonNull(responseWs) && Objects.nonNull(responseWs.getConsultaFacturaPDFXMLResponse())
				&& Objects.nonNull(responseWs.getConsultaFacturaPDFXMLResponse().getFacturaPDFXML())
				&& Objects.nonNull(responseWs.getConsultaFacturaPDFXMLResponse().getFacturaPDFXML().getPdf()))
		{

			return generateInvoice(invoiceNumber, responseWs.getConsultaFacturaPDFXMLResponse().getFacturaPDFXML().getPdf(),
					responseWs.getConsultaFacturaPDFXMLResponse().getFacturaPDFXML().getXml());
		}
		return new ResponseEntity(null, HttpStatus.BAD_REQUEST);


	}


	private ResponseEntity<byte[]> generateInvoice(@RequestParam(name = "invoiceNumber")
	final String invoiceNumber, final String pdf, final String xml) throws IOException
	{
		final String cadena = pdf;
		final String b64 = cadena.substring(1, cadena.length() - 1).replace("\n", "");
		final byte[] decoder = Base64.getDecoder().decode(b64);

		final File downloadFile = new File(invoiceNumber + PDF);
		final File downloadFileZip = new File(invoiceNumber + ZIP);


		final String cadenaXml = xml;
		final String b64Xml = cadenaXml.substring(1, cadenaXml.length() - 1).replace("\n", "");
		final byte[] decoderXml = Base64.getDecoder().decode(b64Xml);

		final File downloadFileXml = new File(invoiceNumber + XML);

		writeFiles(decoder, downloadFile, decoderXml, downloadFileXml);
		createZip(downloadFile, downloadFileZip, downloadFileXml);

		final FileInputStream fizip = new FileInputStream(downloadFileZip);
		final byte[] buffer2 = IOUtils.toByteArray(fizip);

		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		final String filename = invoiceNumber + ZIP;
		headers.setContentDispositionFormData(filename, filename);
		headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

		final ResponseEntity<byte[]> response = null;
		try
		{
			return new ResponseEntity<>(buffer2, headers, HttpStatus.OK);
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
		return new ResponseEntity(null, HttpStatus.BAD_REQUEST);

	}

	private void writeFiles(final byte[] decoder, final File downloadFile, final byte[] decoderXml, final File downloadFileXml)
			throws IOException
	{
		try (FileOutputStream out = new FileOutputStream(downloadFile))
		{
			out.write(decoder);
			out.flush();
		}

		try (FileOutputStream outXml = new FileOutputStream(downloadFileXml))
		{
			outXml.write(decoderXml);
			outXml.flush();
		}
	}

	private void createZip(final File downloadFile, final File downloadFileZip, final File downloadFileXml) throws IOException
	{
		final FileOutputStream fos = new FileOutputStream(downloadFileZip);
		final ZipOutputStream zos = new ZipOutputStream(fos);

		final FileInputStream fis = new FileInputStream(downloadFile);
		final FileInputStream fisXml = new FileInputStream(downloadFileXml);

		final ZipEntry ze = new ZipEntry(downloadFile.getName());
		zos.putNextEntry(ze);

		final ZipEntry zeXml = new ZipEntry(downloadFileXml.getName());

		final byte[] bytes = new byte[1024];
		int longitud;

		while ((longitud = fis.read(bytes)) >= 0)
		{
			zos.write(bytes, 0, longitud);
		}
		zos.closeEntry();

		zos.putNextEntry(zeXml);

		while ((longitud = fisXml.read(bytes)) >= 0)
		{
			zos.write(bytes, 0, longitud);
		}
		zos.closeEntry();


		zos.close();
		fis.close();
		fisXml.close();
		fos.close();
	}

	@RequestMapping(value = "/regimen-capital", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Object> getRegimenCapital(@ApiParam(value = "RFC")
	@RequestParam(required = true)
	final String rfc) throws NoSuchAlgorithmException, KeyStoreException, IOException, URISyntaxException, KeyManagementException
	{

		boolean isMoral = false;

		final ResponseEntity<Object> errormsj = validateRfc(rfc);
		if (errormsj != null)
		{
			return errormsj;
		}

		if (rfc.length() == 12)
		{
			isMoral = true;
			final RegimenCapitalDataList regimenCapitalDataList = billingService.regimenCapital();
			return new ResponseEntity<>(dataMapper.map(regimenCapitalDataList, RegimenCapitalListWsDTO.class), HttpStatus.OK);
		}

		LOG.error("RFC fisico / N/A regimen capital");
		final String errormsjFisica = Localization.getLocalizedString("billing.rfc.fisica.regimencapital");
		return new ResponseEntity<>(errormsjFisica, HttpStatus.ACCEPTED);

	}

	@RequestMapping(method = RequestMethod.GET, value = "/validateBillingAddress/{guid}")
	@ResponseBody
	public AddressWsDTO validateAddressBilling(@ApiParam(value = "Guid ID", required = true)
	@PathVariable
	final String guid)
	{
		if (StringUtils.isNotEmpty(guid))
		{
			final CartModel cartModel = getCartForUserByGUID(guid, BASE_SITE_ID);
			if (cartModel != null)
			{
				final AddressModel addressModel = cartModel.getBillingAddress();
				if (addressModel != null)
				{
					return getDataMapper().map(addressConverter.convert(addressModel), AddressWsDTO.class);
				}
			}
		}
		return null;
	}

	private CartModel getCartForUserByGUID(final String guid, final String baseSiteId)
	{
		return commerceCartService.getCartForGuidAndSite(guid, baseSiteService.getBaseSiteForUID(baseSiteId));
	}

	@RequestMapping(value = "/regimen-fiscal", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Object> getRegimenFiscal(@ApiParam(value = "RFC")
	@RequestParam(required = true)
	final String rfc) throws NoSuchAlgorithmException, KeyStoreException, IOException, URISyntaxException, KeyManagementException
	{

		boolean isMoral = false;

		final ResponseEntity<Object> errormsj = validateRfc(rfc);
		if (errormsj != null)
		{
			return errormsj;
		}

		if (rfc.length() == 12)
		{
			isMoral = true;
		}
		final RegimenFiscalDataList regimenFiscalDataList = billingService.regimenFiscalBilling(isMoral);

		return new ResponseEntity<>(dataMapper.map(regimenFiscalDataList, RegimenFiscalesListWsDTO.class), HttpStatus.OK);

	}

	private ResponseEntity<Object> validateRfc(final String rfc)
	{
		try
		{
			final IndividualResponse response = individualService.validateRFC(rfc);
			if (200 != response.getCode())
			{
				final String errormsj = Localization.getLocalizedString("billing.rfc.invalid.error");
				return new ResponseEntity<>(errormsj, HttpStatus.BAD_REQUEST);
			}

		}
		catch (final Exception e)
		{
			LOG.error("Ocurrio un error en servicio de validar RFC ");
			final String errormsj = Localization.getLocalizedString("billing.rfc.invalid.error");
			return new ResponseEntity<>(errormsj, HttpStatus.BAD_REQUEST);
		}


		if (rfc.length() < 12 || rfc.length() > 13)
		{


			final String errormsj = Localization.getLocalizedString("billing.rfc.invalid.error");
			return new ResponseEntity<>(errormsj, HttpStatus.BAD_REQUEST);
		}
		return null;
	}

	@RequestMapping(value = "/concept", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Object> getConcepts(@ApiParam(value = "RFC")
	@RequestParam(required = true)
	final String rfc, @ApiParam(value = "Regimen")
	@RequestParam(required = true)
	final String regimen)
	{

		boolean isMoral = false;

		final ResponseEntity<Object> errormsj = validateRfc(rfc);
		if (errormsj != null)
		{
			return errormsj;
		}

		if (rfc.length() == 12)
		{
			isMoral = true;
		}
		final ConceptBillingList conceptsBilling = billingService.conceptBilling(isMoral, regimen);

		return new ResponseEntity<>(dataMapper.map(conceptsBilling, ConceptsBillingListWsDTO.class), HttpStatus.OK);

	}


	@PostMapping(value = "/{cartId}/addresses/billing", consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseBody
	@ApiOperation(nickname = "createCartBillingAddress", value = "Creates a Billing address for the cart.", notes = "Creates an address and assigns it to the cart as the Billing address.")
	public ResponseEntity<AddressWsDTO> createCartBillingAddress(
			@ApiParam(value = "Request body parameter that contains details such as the customer's first name (firstName), the customer's last name (lastName), the customer's title (titleCode), the customer's phone (phone), "
					+ "the country (country.isocode), the first part of the address (line1), the second part of the address (line2), the town (town), the postal code (postalCode), and the region (region.isocode).\n\nThe DTO is in XML or .json format.", required = true)
			@RequestBody
			final AddressWsDTO address, @ApiFieldsParam
			@RequestParam(defaultValue = DEFAULT_FIELD_SET)
			final String fields, @ApiParam(value = "Cart Id", required = true)
			@PathVariable
			final String cartId)
	{

		LOG.info("createCartBillingAddress.Inicio");

		final CartModel cartModel = getCommerceCartService().getCartForGuidAndSite(cartId,
				getBaseSiteService().getBaseSiteForUID("telcel"));


		if (StringUtils.isNotEmpty(address.getId()))
		{
			final AddressModel addressModel = getCustomerAccountService().getAddressForCode(((CustomerModel) cartModel.getUser()),
					address.getId());
			if (addressModel != null)
			{
				final AddressData addressData = useAddressForBilling(address, cartModel, addressModel);
				return new ResponseEntity<AddressWsDTO>(getDataMapper().map(addressData, AddressWsDTO.class, fields),
						HttpStatus.ACCEPTED);

			}
			else
			{
				final AddressWsDTO addressData = new AddressWsDTO();
				addressData.setId("No encontrada con el Id" + address.getId());
				return new ResponseEntity<AddressWsDTO>(addressData, HttpStatus.NOT_FOUND);

			}


		}

		if (StringUtils.isEmpty(address.getTitleCode()))
		{
			address.setTitleCode("mr");
		}

		LOG.info("createCartBillingAddress");
		AddressData addressData = getDataMapper().map(address, AddressData.class, ADDRESS_MAPPING);
		addressData = createAddressBillingInternal(addressData, cartModel);

		return new ResponseEntity<AddressWsDTO>(getDataMapper().map(addressData, AddressWsDTO.class, fields), HttpStatus.ACCEPTED);
	}

	private AddressData useAddressForBilling(final AddressWsDTO address, final CartModel cartModel,
			final AddressModel addressModel)
	{
		AddressData addressDataNew = getDataMapper().map(address, AddressData.class, ADDRESS_MAPPING);
		addressDataNew.setBillingAddress(true);
		addressDataNew.setShippingAddress(true);
		//addressDataNew.setVisibleInAddressBook(true);

		validateParameterNotNullStandardMessage("addressData", addressDataNew);

		final CustomerModel currentCustomer = (CustomerModel) cartModel.getUser();
		//final boolean makeThisAddressTheDefault = addressDataNew.isDefaultAddress()
		//		|| (currentCustomer.getDefaultShipmentAddress() == null && addressDataNew.isVisibleInAddressBook());
		final AddressModel newAddress = getModelService().create(AddressModel.class);
		getAddressReversePopulator().populate(addressDataNew, newAddress);
		getTelcelAddressReversePopulator().populate(addressDataNew, newAddress);
		// Store the address against the user
		getCustomerAccountService().saveAddressEntry(currentCustomer, newAddress);

		if (address.getSaveAddress())
		{
			newAddress.setFirstname(addressModel.getFirstname());
			newAddress.setLastname(addressModel.getLastname());
			newAddress.setStreetname(addressModel.getStreetname());
			newAddress.setExternalNumber(addressModel.getExternalNumber());
			newAddress.setInteriorNumber(addressModel.getInteriorNumber());
			newAddress.setPostalcode(addressModel.getPostalcode());
			newAddress.setTown(addressModel.getTown());
			newAddress.setPhone1(addressModel.getPhone1());
			newAddress.setCountry(addressModel.getCountry());
			newAddress.setRegion(addressModel.getRegion());

			newAddress.setBillingAddress(true);

			newAddress.setConcepto(address.getConcepto());
			newAddress.setRfc(address.getRfc());
			newAddress.setRazonSocial(address.getRazonSocial());
			newAddress.setRegimen(address.getRegimen());
			newAddress.setRegimenCapital(address.getRegimenCapital());

			newAddress.setConceptoDescription(address.getConceptoDescription());
			newAddress.setRegimenDescription(address.getRegimenDescription());
			newAddress.setRegimenCapitalDescription(address.getRegimenCapitalDescription());
		}
		else
		{
			newAddress.setFirstname(StringUtils.EMPTY);
			newAddress.setLastname(StringUtils.EMPTY);
			newAddress.setStreetname(StringUtils.EMPTY);
			newAddress.setExternalNumber(StringUtils.EMPTY);
			newAddress.setInteriorNumber(StringUtils.EMPTY);
			newAddress.setPostalcode(StringUtils.EMPTY);
			newAddress.setTown(StringUtils.EMPTY);
			newAddress.setPhone1(StringUtils.EMPTY);
			newAddress.setCountry(null);
			newAddress.setRegion(null);

			newAddress.setBillingAddress(false);
			newAddress.setConcepto(StringUtils.EMPTY);
			newAddress.setRfc(StringUtils.EMPTY);
			newAddress.setRazonSocial(StringUtils.EMPTY);
			newAddress.setRegimen(StringUtils.EMPTY);
			newAddress.setRegimenCapital(StringUtils.EMPTY);

			newAddress.setConceptoDescription(StringUtils.EMPTY);
			newAddress.setRegimenDescription(StringUtils.EMPTY);
			newAddress.setRegimenCapitalDescription(StringUtils.EMPTY);
		}


		getModelService().save(newAddress);
		cartModel.setBillingAddress(newAddress);
		getModelService().save(cartModel);
		getModelService().refresh(cartModel);

		final AddressData addressData = getAddressConverter().convert(newAddress);
		return addressData;
	}

	protected AddressData createAddressBillingInternal(final AddressData addressData, final CartModel cartModel)
	{
		addressData.setBillingAddress(true);
		addressData.setShippingAddress(false);
		addressData.setVisibleInAddressBook(true);

		validateParameterNotNullStandardMessage("addressData", addressData);


		final CustomerModel currentCustomer = (CustomerModel) cartModel.getUser();

		final boolean makeThisAddressTheDefault = addressData.isDefaultAddress()
				|| (currentCustomer.getDefaultShipmentAddress() == null && addressData.isVisibleInAddressBook());

		// Create the new address model
		final AddressModel newAddress = getModelService().create(AddressModel.class);
		LOG.info("Numero interior: " + addressData.getInteriorNumber());
		LOG.info("Numero exterior: " + addressData.getExternalNumber());
		LOG.info("Refrencia: " + addressData.getReferences());
		getAddressReversePopulator().populate(addressData, newAddress);
		getTelcelAddressReversePopulator().populate(addressData, newAddress);

		// Store the address against the user
		getCustomerAccountService().saveAddressEntry(currentCustomer, newAddress);

		// Update the address ID in the newly created address
		addressData.setId(newAddress.getId());

		newAddress.setShippingAddress(false);
		newAddress.setBillingAddress(true);
		getModelService().save(newAddress);

		cartModel.setBillingAddress(newAddress);

		getModelService().save(cartModel);
		getModelService().refresh(cartModel);


		if (makeThisAddressTheDefault)
		{
			getCustomerAccountService().setDefaultAddressEntry(currentCustomer, newAddress);
		}


		if (addressData.isDefaultAddress())
		{
			userFacade.setDefaultAddress(addressData);
		}
		return addressData;
	}


	public DataMapper getDataMapper()
	{
		return dataMapper;
	}


	public ModelService getModelService()
	{
		return modelService;
	}


	public Populator<AddressData, AddressModel> getAddressReversePopulator()
	{
		return addressReversePopulator;
	}


	public Populator<AddressData, AddressModel> getTelcelAddressReversePopulator()
	{
		return telcelAddressReversePopulator;
	}


	public CustomerAccountService getCustomerAccountService()
	{
		return customerAccountService;
	}


	public CommerceCartService getCommerceCartService()
	{
		return commerceCartService;
	}


	public BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	public Converter<AddressModel, AddressData> getAddressConverter()
	{
		return addressConverter;
	}
}
