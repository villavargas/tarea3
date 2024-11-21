/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercewebservices.core.v2.controller;

import de.hybris.platform.acceleratorservices.payment.data.PaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.commercewebservices.core.form.PaymenDetails;
import de.hybris.platform.commercewebservices.core.form.PaymentDetails;
import de.hybris.platform.commercewebservicescommons.dto.order.PaymentDetailsWsDTO;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdUserIdAndCartIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import de.hybris.platform.commercewebservices.core.exceptions.InvalidPaymentInfoException;
import de.hybris.platform.commercewebservices.core.exceptions.NoCheckoutCartException;
import de.hybris.platform.commercewebservices.core.exceptions.UnsupportedRequestException;
import de.hybris.platform.commercewebservices.core.request.support.impl.PaymentProviderRequestSupportedStrategy;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import mx.com.telcel.facades.orders.TelcelCheckoutFacade;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


@Controller
@RequestMapping(value = "/{baseSiteId}/users/{userId}/carts")
@CacheControl(directive = CacheControlDirective.NO_CACHE)
@Api(tags = "Cart Payments")
public class CartPaymentsController extends BaseCommerceController
{

	private static final Logger LOG = LoggerFactory.getLogger(CartPaymentsController.class);

	@Resource
	private CheckoutCustomerStrategy checkoutCustomerStrategy;

	@Resource
	private CustomerAccountService customerAccountService;

	@Resource
	private ModelService modelService;



	private static final String PAYMENT_MAPPING = "accountHolderName,cardNumber,cardType,cardTypeData(code),expiryMonth,expiryYear,issueNumber,startMonth,startYear,subscriptionId,defaultPaymentInfo,saved,billingAddress(titleCode,firstName,lastName,line1,line2,town,postalCode,country(isocode),region(isocode),defaultAddress)";

	@Resource(name = "paymentProviderRequestSupportedStrategy")
	private PaymentProviderRequestSupportedStrategy paymentProviderRequestSupportedStrategy;

	@Resource(name = "telcelCheckoutFacade")
	private TelcelCheckoutFacade telcelCheckoutFacade;

	@Secured({ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@PostMapping(value = "/{cartId}/paymentdetails", consumes = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	@ApiOperation(nickname = "createCartPaymentDetails", value = "Defines and assigns details of a new credit card payment to the cart.", notes = "Defines the details of a new credit card, and assigns this payment option to the cart.")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public PaymentDetailsWsDTO createCartPaymentDetails(@ApiParam(value =
			"Request body parameter that contains details such as the name on the card (accountHolderName), the card number (cardNumber), the card type (cardType.code), "
					+ "the month of the expiry date (expiryMonth), the year of the expiry date (expiryYear), whether the payment details should be saved (saved), whether the payment details "
					+ "should be set as default (defaultPaymentInfo), and the billing address (billingAddress.firstName, billingAddress.lastName, billingAddress.titleCode, billingAddress.country.isocode, "
					+ "billingAddress.line1, billingAddress.line2, billingAddress.town, billingAddress.postalCode, billingAddress.region.isocode)\n\nThe DTO is in XML or .json format.", required = true) @RequestBody final PaymentDetailsWsDTO paymentDetails,
			@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
			throws InvalidPaymentInfoException, NoCheckoutCartException, UnsupportedRequestException
	{
		paymentProviderRequestSupportedStrategy.checkIfRequestSupported("addPaymentDetails");
		validatePayment(paymentDetails);
		CCPaymentInfoData paymentInfoData = getDataMapper().map(paymentDetails, CCPaymentInfoData.class, PAYMENT_MAPPING);
		paymentInfoData = addPaymentDetailsInternal(paymentInfoData).getPaymentInfo();
		return getDataMapper().map(paymentInfoData, PaymentDetailsWsDTO.class, fields);
	}

	@PutMapping(value = "/{cartId}/paymentdetails" , consumes = {MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE})
	@ResponseBody
	@ApiOperation(nickname = "replaceCartPaymentDetails", value = "Sets credit card payment details for the cart.", notes = "Sets credit card payment details for the cart.")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public void replaceCartPaymentDetails(@ApiParam(value = "paymen Details Object", required = true) @RequestBody final PaymentDetails paymenDetails, final BindingResult bindingResult,
										  final HttpServletRequest request, final Model model,
										  @ApiParam(value = "Payment details identifier.", required = true) @RequestParam final String paymentDetailsId)
			throws InvalidPaymentInfoException
	{
		setPaymentDetailsInternalTelcel(paymentDetailsId,paymenDetails);
	}


	protected CartData setPaymentDetailsInternalTelcel(final String paymentDetailsId, PaymentDetails paymentDetails) throws InvalidPaymentInfoException
	{
		LOG.debug("setPaymentDetailsInternal: {}", logParam("paymentDetailsId", paymentDetailsId));

		if (StringUtils.isNotBlank(paymentDetailsId))
		{
			final CustomerModel currentUserForCheckout = getCurrentUserForCheckout();
			final CreditCardPaymentInfoModel ccPaymentInfoModel = getCustomerAccountService()
					.getCreditCardPaymentInfoForCode(currentUserForCheckout, paymentDetailsId);
			PaymenDetails paymentInfoData = paymentDetails.getPaymenDetails();
			ccPaymentInfoModel.setCvv(paymentInfoData.getCvn());
			ccPaymentInfoModel.setMsi(paymentInfoData.getMsi());
			ccPaymentInfoModel.setUrl3ds(paymentInfoData.getUrl3ds());
			ccPaymentInfoModel.setHolderLastName(paymentInfoData.getHolderLastName());

//			ccPaymentInfoModel.setSavedCard(paymentDetails.getPaymenDetails().isSaved());
			ccPaymentInfoModel.setSavedCard(false);

			getModelService().save(ccPaymentInfoModel);

		}else{
			throw new InvalidPaymentInfoException(paymentDetailsId);

		}


		if (telcelCheckoutFacade.setPaymentDetails(paymentDetailsId))
		{
			return getSessionCart();
		}
		throw new InvalidPaymentInfoException(paymentDetailsId);
	}

	protected CustomerModel getCurrentUserForCheckout()
	{
		return getCheckoutCustomerStrategy().getCurrentUserForCheckout();
	}

	protected void validatePayment(final PaymentDetailsWsDTO paymentDetails) throws NoCheckoutCartException
	{
		if (!getCheckoutFacade().hasCheckoutCart())
		{
			throw new NoCheckoutCartException("Cannot add PaymentInfo. There was no checkout cart created yet!");
		}
		validate(paymentDetails, "paymentDetails", getPaymentDetailsDTOValidator());
	}

	public CheckoutCustomerStrategy getCheckoutCustomerStrategy() {
		return checkoutCustomerStrategy;
	}

	public void setCheckoutCustomerStrategy(CheckoutCustomerStrategy checkoutCustomerStrategy) {
		this.checkoutCustomerStrategy = checkoutCustomerStrategy;
	}

	public CustomerAccountService getCustomerAccountService() {
		return customerAccountService;
	}

	public void setCustomerAccountService(CustomerAccountService customerAccountService) {
		this.customerAccountService = customerAccountService;
	}

	public ModelService getModelService() {
		return modelService;
	}

	public void setModelService(ModelService modelService) {
		this.modelService = modelService;
	}
}
