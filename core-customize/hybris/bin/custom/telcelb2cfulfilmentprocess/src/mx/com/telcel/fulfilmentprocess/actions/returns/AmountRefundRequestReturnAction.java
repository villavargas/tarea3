/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.fulfilmentprocess.actions.returns;

import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.TelcelReplacementOrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.processengine.action.AbstractAction;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.task.RetryLaterException;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import javax.annotation.Resource;

import mx.com.telcel.core.services.TelcelCodigosPostalesService;
import mx.com.telcel.facades.zipcodes.data.CodigosPostalesTelcelData;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import mx.com.telcel.core.model.AdditionalServiceEntryModel;
import mx.com.telcel.core.model.PaymentsValidateModel;
import mx.com.telcel.core.model.TelcelAdditionalServiceProductOfferingModel;
import mx.com.telcel.core.model.TelcelPoVariantModel;
import mx.com.telcel.core.model.TelcelSimpleProductOfferingModel;
import mx.com.telcel.core.models.paymentcommerce.CardRefundRequest;
import mx.com.telcel.core.models.paymentcommerce.CardRefundResponse;
import mx.com.telcel.core.models.paymentcommerce.Item;
import mx.com.telcel.core.models.paymentcommerce.ValidateTokenResponse;
import mx.com.telcel.core.services.paymentcommerce.PaymentCommerceService;
import mx.com.telcel.core.util.TelcelUtil;


/**
 * The type Amount refund request return action.
 */
public class AmountRefundRequestReturnAction extends AbstractAction<ReturnProcessModel>
{
	private static final Logger LOG = Logger.getLogger(AmountRefundRequestReturnAction.class);
	private static final String PARTIAL = "PARTIAL";
	private static final String SUCCESS = "SUCCESS";
	private static final int CODE_SUCCESS = 200;
	private static final String NA = "NA";
	private static final String DELIVERY_SKU = "07";
	private static final String SHIPPING_COST = "Shipping cost";
	private static final String ZERO = "0";

	@Resource(name = "paymentCommerceService")
	private PaymentCommerceService paymentCommerceService;

	@Resource(name = "enumerationService")
	private EnumerationService enumerationService;

	@Resource
	private TelcelUtil telcelUtil; // NOSONAR

	@Resource(name = "telcelCodigosPostalesService")
	private TelcelCodigosPostalesService telcelCodigosPostalesService;

	@Override
	public String execute(final ReturnProcessModel process) throws RetryLaterException, Exception
	{
		LOG.info("Process: " + process.getCode() + " in step " + getClass().getSimpleName());
		final ReturnRequestModel returnRequestModel = process.getReturnRequest();
		final OrderModel orderModel = returnRequestModel.getOrder();
		final PaymentInfoModel infoModel = orderModel.getPaymentInfo();
		boolean isCardRefund = false;
		if (infoModel instanceof CreditCardPaymentInfoModel)
		{
			isCardRefund = true;
		}

		if (returnRequestModel.getProcessBySipab() != null && returnRequestModel.getProcessBySipab() && !isCardRefund)
		{
			LOG.info(String.format("Flag ProcessBySipab is true...RMA %s is being forced to make a refund by sipab...",
					returnRequestModel.getRMA()));
			return continueByOtherPayments(process, returnRequestModel);
		}

		final RefundEntryModel entryModel = (RefundEntryModel) returnRequestModel.getReturnEntries().get(0);
		final CustomerModel customerModel = (CustomerModel) orderModel.getUser();
		final ConsignmentModel consignmentModel = entryModel.getConsignment();
		final AbstractOrderEntryModel abstractOrderEntryModel = consignmentModel.getConsignmentEntries().iterator().next()
				.getOrderEntry();

		if (infoModel instanceof CreditCardPaymentInfoModel)
		{
			final CreditCardPaymentInfoModel creditCardPaymentInfoModel = (CreditCardPaymentInfoModel) infoModel;
			final CardRefundRequest cardRefundRequest = new CardRefundRequest();
			cardRefundRequest.setOrderId(creditCardPaymentInfoModel.getOrderIdWs());
			//cardRefundRequest.setCommerceId(orderModel.getCode());
			cardRefundRequest.setEmail(customerModel.getContactEmail());
			cardRefundRequest.setLastDigits(creditCardPaymentInfoModel.getReceiptStatus().get(0).getLastDigits());
			cardRefundRequest.setFirstname(creditCardPaymentInfoModel.getHolderName());
			cardRefundRequest.setLastname(StringUtils.isNotEmpty(creditCardPaymentInfoModel.getHolderLastName())
					? creditCardPaymentInfoModel.getHolderLastName()
					: NA);

			cardRefundRequest.setRefundType(PARTIAL);
			cardRefundRequest.setComments(enumerationService.getEnumerationName(entryModel.getReason()));

			AddressModel deliveryAddress = orderModel.getDeliveryAddress();

			if(deliveryAddress != null) {

				final String postalCode = deliveryAddress.getPostalcode();
				final CodigosPostalesTelcelData codigosPostalesTelcelData = telcelCodigosPostalesService.getInfForZipcode(postalCode);

				if (!Objects.isNull(codigosPostalesTelcelData) || !Objects.isNull(codigosPostalesTelcelData.getRegion())) {
					cardRefundRequest.setRegion(Integer.valueOf(codigosPostalesTelcelData.getRegion().getCode()));
				}
			}

			if (!ZERO.equals(creditCardPaymentInfoModel.getMsi()) && creditCardPaymentInfoModel.getPaymentsValidate().size() > 1)
			{
				final boolean refundMSIProductDC = returnRequestModel.getRefundMSIProductDC();
				final boolean refundMSIAdditionalService = returnRequestModel.getRefundMSIAdditionalService();
				final PaymentsValidateModel paymentsValidateModelProduct = creditCardPaymentInfoModel.getPaymentsValidate().stream()
						.filter(paymentsValidateModel -> !ZERO.equals(paymentsValidateModel.getMsi())).findFirst().get();
				final PaymentsValidateModel paymentsValidateModelAditionalServicesDC = creditCardPaymentInfoModel
						.getPaymentsValidate().stream().filter(paymentsValidateModel -> ZERO.equals(paymentsValidateModel.getMsi()))
						.findFirst().get();

				if (!refundMSIProductDC)
				{
					generateRollBackMSIProductDCForValidatePayment(creditCardPaymentInfoModel, cardRefundRequest,
							paymentsValidateModelProduct, returnRequestModel, abstractOrderEntryModel, orderModel);
				}

				if (!refundMSIAdditionalService && returnRequestModel.getRefundAdditionalService())
				{
					generateRollBackMSIAditionalServiceForValidatePayment(creditCardPaymentInfoModel, cardRefundRequest,
							paymentsValidateModelAditionalServicesDC, returnRequestModel, orderModel, consignmentModel);
				}

				if ((returnRequestModel.getRefundMSIProductDC() && returnRequestModel.getRefundMSIAdditionalService())
						|| (returnRequestModel.getRefundMSIProductDC() && !returnRequestModel.getRefundAdditionalService()))
				{
					return Transition.CARD_SUCCESS.toString();
				}
				else
				{
					return Transition.FAILED_REFUND.toString();
				}
			}
			else
			{
				cardRefundRequest
						.setPaymentId(String.valueOf(creditCardPaymentInfoModel.getPaymentsValidate().get(0).getPaymentId()));
				BigDecimal totalAmount = returnRequestModel.getSubtotal();
				boolean refoundDeliveryCost = false;
				if (returnRequestModel.getRefundDeliveryCost() && orderModel.getDeliveryCost() > 0D)
				{
					totalAmount = totalAmount.add(new BigDecimal(orderModel.getDeliveryCost()));
					refoundDeliveryCost = true;
				}
				boolean refoundAdditionalService = false;
				if (returnRequestModel.getRefundAdditionalService() && consignmentModel.getAdditionalServiceEntry() != null)
				{
					totalAmount = totalAmount.add(new BigDecimal(consignmentModel.getAdditionalServiceEntry().getBasePrice()));
					refoundAdditionalService = true;
				}
				//cardRefundRequest.setRefundAmount(totalAmount.doubleValue());
				final List<Item> listItems = new ArrayList<>();
				final Item item = createItem(orderModel, abstractOrderEntryModel);
				listItems.add(item);
				if (refoundDeliveryCost)
				{
					final Item deliveryCostItem = new Item();
					deliveryCostItem.setSku(DELIVERY_SKU);
					deliveryCostItem.setDescription(SHIPPING_COST);
					deliveryCostItem.setPrice(orderModel.getDeliveryCost());
					deliveryCostItem.setModel(NA);
					deliveryCostItem.setColor(NA);
					listItems.add(deliveryCostItem);
				}
				if (refoundAdditionalService)
				{
					final BigDecimal basePriceAdditionalService = new BigDecimal(0);
					final Item additionalServiceItem = new Item();
					final AdditionalServiceEntryModel additionalServiceEntry = consignmentModel.getAdditionalServiceEntry();
					final TelcelAdditionalServiceProductOfferingModel additionalServiceProduct = additionalServiceEntry
							.getAdditionalServiceProduct();
					additionalServiceItem.setSku(consignmentModel.getAdditionalServicesSku());
					additionalServiceItem.setDescription(additionalServiceProduct.getCode().replaceAll("_", " "));
					additionalServiceItem.setPrice(additionalServiceEntry.getBasePrice());
					additionalServiceItem.setModel(NA);
					additionalServiceItem.setColor(NA);
					listItems.add(additionalServiceItem);

					//Se suma el precio del producto del servicio adicional
					basePriceAdditionalService.add(new BigDecimal(additionalServiceEntry.getBasePrice()));
					totalAmount = totalAmount.add(basePriceAdditionalService);

				}
				//Reembolso:RefundAmount
				cardRefundRequest.setRefundAmount(totalAmount.doubleValue());

				cardRefundRequest.setItems(listItems);
				final String emailUid = CustomerType.GUEST.equals(customerModel.getType())
						? StringUtils.substringAfter(customerModel.getUid(), "|")
						: telcelUtil.getEmailForToken(customerModel.getContactEmail(), orderModel.getAddressHolderLine().getEmail());

				final ValidateTokenResponse validateTokenResponse = getToken(emailUid);
				LOG.info("execute token for cardRefund [" + validateTokenResponse.getToken() + "]");
				final CardRefundResponse cardRefundResponse = paymentCommerceService.cardRefund(cardRefundRequest,
						validateTokenResponse.getToken());
				if (cardRefundResponse.getStatusCode().equals(CODE_SUCCESS)
						&& cardRefundResponse.getStatus().equalsIgnoreCase(SUCCESS))
				{
					return Transition.CARD_SUCCESS.toString();
				}
				else
				{
					return Transition.FAILED_REFUND.toString();
				}
			}
		}
		else
		{
			return continueByOtherPayments(process, returnRequestModel);
		}
	}

	private Item createItem(final OrderModel orderModel, final AbstractOrderEntryModel abstractOrderEntryModel) throws Exception
	{
		final Item item = new Item();
		final TelcelPoVariantModel telcelPoVariantModel = orderModel instanceof TelcelReplacementOrderModel
				? (TelcelPoVariantModel) abstractOrderEntryModel.getReplacedProduct()
				: (TelcelPoVariantModel) abstractOrderEntryModel.getProduct();
		//TODO: Remove temporal tracking log
		LOG.info("is replacementOrderModel: " + (orderModel instanceof TelcelReplacementOrderModel));
		final TelcelSimpleProductOfferingModel telcelSimpleProductOffering = (TelcelSimpleProductOfferingModel) telcelPoVariantModel
				.getTmaBasePo();
		item.setSku(telcelPoVariantModel.getSku());
		item.setDescription(telcelPoVariantModel.getTmaBasePo() != null ? telcelPoVariantModel.getTmaBasePo().getCode().replaceAll("_", " ") : NA);
		item.setPrice(abstractOrderEntryModel.getBasePrice());
		item.setModel(telcelSimpleProductOffering.getModelo() != null ? telcelSimpleProductOffering.getModelo() : NA);
		item.setColor(telcelPoVariantModel.getColor() != null ? telcelPoVariantModel.getColor().getCode() : NA);

		return item;
	}

	private String continueByOtherPayments(final ReturnProcessModel process, final ReturnRequestModel returnRequestModel)
	{
		process.setWaitingForRefund(true);
		getModelService().save(process);
		returnRequestModel.setProcessBySipab(true);
		getModelService().save(returnRequestModel);
		LOG.info(String.format("Process waiting for SIPAB file for RMA: %s", returnRequestModel.getRMA()));
		return Transition.OTHER_PAYMENTS.toString();
	}

	private ValidateTokenResponse getToken(final String uid)
	{
		LOG.info("getToken [" + uid + "]");
		ValidateTokenResponse validateTokenResponse = null;
		try
		{
			validateTokenResponse = paymentCommerceService.getTokenByUid(uid);
		}
		catch (final Exception e)
		{
			LOG.error("Error al obtener token =" + e.getLocalizedMessage());
		}

		LOG.info("getToken [" + uid != null ? uid : "anonimo" + "] token [" + validateTokenResponse.getToken() + "]");

		return validateTokenResponse;
	}

	private void generateRollBackMSIProductDCForValidatePayment(final CreditCardPaymentInfoModel creditCardPaymentInfoModel,
			final CardRefundRequest cardRefundRequest, final PaymentsValidateModel paymentsValidateModel,
			final ReturnRequestModel returnRequestModel, final AbstractOrderEntryModel abstractOrderEntryModel,
			final OrderModel orderModel) throws Exception
	{
		cardRefundRequest.setPaymentId(String.valueOf(paymentsValidateModel.getPaymentId()));
		BigDecimal totalAmount = new BigDecimal(returnRequestModel.getSubtotal().doubleValue());

		final List<Item> listItems = new ArrayList<>();
		final Item item = createItem(orderModel, abstractOrderEntryModel);
		listItems.add(item);

		if (returnRequestModel.getRefundDeliveryCost() && orderModel.getDeliveryCost() > 0D)
		{
			totalAmount = totalAmount.add(new BigDecimal(orderModel.getDeliveryCost()));

			final Item deliveryCostItem = new Item();
			deliveryCostItem.setSku(DELIVERY_SKU);
			deliveryCostItem.setDescription(SHIPPING_COST);
			deliveryCostItem.setPrice(orderModel.getDeliveryCost());
			deliveryCostItem.setModel(NA);
			deliveryCostItem.setColor(NA);
			listItems.add(deliveryCostItem);
		}
		cardRefundRequest.setRefundAmount(totalAmount.doubleValue());

		cardRefundRequest.setItems(listItems);

		final CustomerModel customerModel = (CustomerModel) orderModel.getUser();
		final String emailUid = CustomerType.GUEST.equals(customerModel.getType())
				? StringUtils.substringAfter(customerModel.getUid(), "|")
				: telcelUtil.getEmailForToken(customerModel.getContactEmail(), orderModel.getAddressHolderLine().getEmail());

		final ValidateTokenResponse validateTokenResponse = getToken(emailUid);
		LOG.info("generateRollBackMSIProductDCForValidatePayment token for cardRefund [" + validateTokenResponse.getToken() + "]");

		final CardRefundResponse cardRefundResponse = paymentCommerceService.cardRefund(cardRefundRequest,
				validateTokenResponse.getToken());
		if (cardRefundResponse.getStatusCode().equals(CODE_SUCCESS) && cardRefundResponse.getStatus().equalsIgnoreCase(SUCCESS))
		{
			returnRequestModel.setRefundMSIProductDC(Boolean.TRUE);
		}
		else
		{
			returnRequestModel.setRefundMSIProductDC(Boolean.FALSE);
		}
		modelService.save(returnRequestModel);
	}

	private void generateRollBackMSIAditionalServiceForValidatePayment(final CreditCardPaymentInfoModel creditCardPaymentInfoModel,
			final CardRefundRequest cardRefundRequest, final PaymentsValidateModel paymentsValidateModel,
			final ReturnRequestModel returnRequestModel, final OrderModel orderModel, final ConsignmentModel consignmentModel)
			throws NoSuchAlgorithmException, KeyStoreException, IOException, KeyManagementException
	{
		cardRefundRequest.setPaymentId(String.valueOf(paymentsValidateModel.getPaymentId()));
		final List<Item> listItems = new ArrayList<>();
		BigDecimal totalAmount = new BigDecimal(0);
		if (returnRequestModel.getRefundAdditionalService() && consignmentModel.getAdditionalServiceEntry() != null)
		{
			totalAmount = totalAmount.add(new BigDecimal(consignmentModel.getAdditionalServiceEntry().getBasePrice()));

			final Item additionalServiceItem = new Item();
			final AdditionalServiceEntryModel additionalServiceEntry = consignmentModel.getAdditionalServiceEntry();
			final TelcelAdditionalServiceProductOfferingModel additionalServiceProduct = additionalServiceEntry
					.getAdditionalServiceProduct();
			additionalServiceItem.setSku(consignmentModel.getAdditionalServicesSku());
			additionalServiceItem.setDescription(additionalServiceProduct.getCode().replaceAll("_", " "));
			additionalServiceItem.setPrice(additionalServiceEntry.getBasePrice());
			additionalServiceItem.setModel(NA);
			additionalServiceItem.setColor(NA);
			listItems.add(additionalServiceItem);
		}
		cardRefundRequest.setRefundAmount(totalAmount.doubleValue());

		cardRefundRequest.setItems(listItems);

		final CustomerModel customerModel = (CustomerModel) orderModel.getUser();
		final String emailUid = CustomerType.GUEST.equals(customerModel.getType())
				? StringUtils.substringAfter(customerModel.getUid(), "|")
				: telcelUtil.getEmailForToken(customerModel.getContactEmail(), orderModel.getAddressHolderLine().getEmail());

		final ValidateTokenResponse validateTokenResponse = getToken(emailUid);

		LOG.info("generateRollBackMSIAditionalServiceForValidatePayment token for cardRefund [" + validateTokenResponse.getToken()
				+ "]");
		final CardRefundResponse cardRefundResponse = paymentCommerceService.cardRefund(cardRefundRequest,
				validateTokenResponse.getToken());
		if (cardRefundResponse.getStatusCode().equals(CODE_SUCCESS) && cardRefundResponse.getStatus().equalsIgnoreCase(SUCCESS))
		{
			returnRequestModel.setRefundMSIAdditionalService(Boolean.TRUE);
		}
		else
		{
			returnRequestModel.setRefundMSIAdditionalService(Boolean.FALSE);
		}
		modelService.save(returnRequestModel);
	}

	@Override
	public Set<String> getTransitions()
	{
		return Transition.getStringValues();
	}

	/**
	 * The enum Transition.
	 */
	protected enum Transition
	{
		CARD_SUCCESS, OTHER_PAYMENTS, FAILED_REFUND;

		/**
		 * Gets string values.
		 *
		 * @return the string values
		 */
		public static Set<String> getStringValues()
		{
			final Set<String> res = new HashSet<String>();

			for (final Transition transition : Transition.values())
			{
				res.add(transition.toString());
			}
			return res;
		}
	}
}
