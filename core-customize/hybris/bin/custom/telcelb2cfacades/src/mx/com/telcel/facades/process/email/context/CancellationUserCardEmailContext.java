/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.facades.process.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import mx.com.telcel.core.model.AdditionalServiceEntryModel;
import mx.com.telcel.core.model.CancellationUserCardProcessModel;
import mx.com.telcel.core.model.PaymentsValidateModel;
import mx.com.telcel.core.util.TelcelUtil;
import mx.com.telcel.facades.order.data.OrderDetailData;
import mx.com.telcel.facades.product.TelcelProductFacade;


/**
 * The type Cancellation user card email contex.
 */
public class CancellationUserCardEmailContext extends AbstractEmailContext<CancellationUserCardProcessModel>
{

	private static final String CUSTOMER_ID = "customerID";
	private static final String CUSTOMER_NAME = "customerName";
	private static final String ORDER_ID = "orderID";
	private static final String COMMA = ", ";
	private static final String C_P = "C.P.";
	private static final String DOT = ".";
	private static final String SPACE = " ";
	private static final String ADDRESS = "address";
	private static final String ENTRY = "entry";
	private static final String PAYMENT_INFO = "paymentInfo";
	private static final String MSI = "msi";
	private static final String SUBTOTAL = "subtotal";
	private static final String DELIVERY_COST = "deliveryCost";
	private static final String TOTAL = "total";
	private static final String DAY = "day";
	private static final String MONTH = "month";
	private static final String YEAR = "year";
	private static final String ADITIONAL_SERVICE_NAME = "aditionalServiceName";
	private static final String SUBTOTAL_CONSIGNMENT = "subtotalConsignment";
	private static final String TOTAL_CONSIGNMENT = "totalConsignment";
	private static final String COSTO_ENVIO_CONSIGNMENT = "costoEnvioConsignment";

	private Converter<OrderEntryModel, OrderEntryData> orderEntryConverter;
	private EnumerationService enumerationService;

	@Resource(name = "orderDetailConverter")
	private Converter<OrderModel, OrderDetailData> orderDetailConverter;

	@Resource(name = "productFacade")
	private TelcelProductFacade productFacade;

	@Resource
	private TelcelUtil telcelUtil;

	private static final Logger LOG = Logger.getLogger(CancellationUserCardEmailContext.class);

	@Override
	public void init(final CancellationUserCardProcessModel cancellationUserCardProcessModel, final EmailPageModel emailPageModel)
	{
		super.init(cancellationUserCardProcessModel, emailPageModel);
		final ConsignmentModel consignmentModel = cancellationUserCardProcessModel.getConsignment();
		final ReturnRequestModel returnRequestModel = cancellationUserCardProcessModel.getReturnRequest();
		final Date creationtime = returnRequestModel.getCreationtime();
		final AbstractOrderModel orderModel = consignmentModel.getOrder();
		final CustomerModel customerModel = (CustomerModel) orderModel.getUser();

		final LocalDate localDate = creationtime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		put(DAY, localDate.getDayOfMonth());
		put(MONTH, localDate.getMonthValue());
		put(YEAR, localDate.getYear());

		put(CUSTOMER_ID, customerModel.getCustomerID());
		put(CUSTOMER_NAME, telcelUtil.nameTwoFirstWords(customerModel.getName()));
		put(ORDER_ID, orderModel.getCode());
		final AddressModel addressModel = orderModel.getDeliveryAddress();
		if (Objects.nonNull(addressModel))
		{
			put(ADDRESS, getAddress(addressModel));
		}

		if (Objects.nonNull(orderModel))
		{
			final OrderDetailData order = orderDetailConverter.convert((OrderModel) orderModel);
			put("order", order);
		}

		final AbstractOrderEntryModel entryModel = consignmentModel.getConsignmentEntries().iterator().next().getOrderEntry();
		LOG.info("CONSIGNMENT : " + consignmentModel.getCode());
		LOG.info("ENTRY : " + entryModel.getEntryNumber());
		final PaymentInfoModel paymentInfoModel = orderModel.getPaymentInfo();
		final boolean returnAditionalService = returnRequestModel.getRefundAdditionalService()
				&& returnRequestModel.getAdditionalServiceValue() > 0D && consignmentModel.getAdditionalServiceEntry() != null;
		String multicompraMsi = "false";
		put("totalSinMSi", 0);
		put("totalConMSi", 0);
		if (paymentInfoModel instanceof CreditCardPaymentInfoModel)
		{
			final CreditCardPaymentInfoModel cardPaymentInfoModel = (CreditCardPaymentInfoModel) paymentInfoModel;
			if (!"0".equalsIgnoreCase(cardPaymentInfoModel.getMsi()) && cardPaymentInfoModel.getPaymentsValidate().size() > 1)
			{
				multicompraMsi = "true";
				for (final PaymentsValidateModel paymentsValidateModel : cardPaymentInfoModel.getPaymentsValidate())
				{
					if ("0".equalsIgnoreCase(paymentsValidateModel.getMsi()))
					{
						//final Double totalSinMSi = Double.valueOf(paymentsValidateModel.getAmount());
						Double totalSinMSi = 0D;
						if (returnAditionalService)
						{
							totalSinMSi = consignmentModel.getAdditionalServiceEntry().getBasePrice();
						}
						LOG.info("TOTAL SIN MSI : " + totalSinMSi);
						put("totalSinMSi", totalSinMSi);
					}
					if (!"0".equalsIgnoreCase(paymentsValidateModel.getMsi()))
					{
						//final Double totalConMSi = Double.valueOf(paymentsValidateModel.getAmount());
						final Double totalConMSi = entryModel.getBasePrice();
						LOG.info("TOTAL CON MSI : " + totalConMSi);
						put("totalConMSi", totalConMSi);

					}
				}
			}
		}
		put("paymentType", orderModel.getPaymentInfo().getPaymentType());
		put("formatter", new DecimalFormat("###,##0.00"));
		put("multicompraMsi", multicompraMsi);
		final OrderEntryData orderEntryData = orderEntryConverter.convert((OrderEntryModel) entryModel);
		productFacade.setProductDataPaqueteAmigo(orderEntryData.getProduct(), entryModel.getAdditionalServiceEntries());
		put(ENTRY, orderEntryData);
		put("consignmentCode", consignmentModel.getCode());
		final Double subtotal = getTotalWithAS(entryModel.getBasePrice(), returnAditionalService,
				consignmentModel.getAdditionalServiceEntry());
		final Double costoEnvio = orderModel.getDeliveryCost();
		final Double total = getTotalWithAS(Double.sum(entryModel.getBasePrice(), orderModel.getDeliveryCost()),
				returnAditionalService, consignmentModel.getAdditionalServiceEntry());
		LOG.info("SUBTOTAL : " + subtotal);
		LOG.info("COSTO ENVIO : " + costoEnvio);
		LOG.info("TOTAL : " + total);
		put(SUBTOTAL_CONSIGNMENT, subtotal);
		put(COSTO_ENVIO_CONSIGNMENT, costoEnvio);
		put(TOTAL_CONSIGNMENT, total);

		if (returnAditionalService)
		{
			put(ADITIONAL_SERVICE_NAME, consignmentModel.getAdditionalServiceEntry().getAdditionalServiceProduct().getName());
		}
		put("StringUtils", StringUtils.class);
		setCardInfo(orderModel);
		put(EMAIL, orderModel.getAddressHolderLine().getEmail());
		put(FROM_EMAIL, emailPageModel.getFromEmail());
		put(DISPLAY_NAME, emailPageModel.getFromEmail());
	}

	private double getTotalWithAS(final Double basePrice, final boolean returnAditionalService,
			final AdditionalServiceEntryModel additionalServiceEntry)
	{
		if (returnAditionalService)
		{
			return Double.sum(basePrice, additionalServiceEntry.getBasePrice());
		}
		else
		{
			return basePrice;
		}
	}

	private void setCardInfo(final AbstractOrderModel orderModel)
	{
		final PaymentInfoModel infoModel = orderModel.getPaymentInfo();
		if (infoModel instanceof CreditCardPaymentInfoModel)
		{
			final CreditCardPaymentInfoModel creditCardPaymentInfoModel = (CreditCardPaymentInfoModel) infoModel;
			put(PAYMENT_INFO, getEnumerationService().getEnumerationName(creditCardPaymentInfoModel.getType()) + SPACE
					+ creditCardPaymentInfoModel.getNumber());
			if (creditCardPaymentInfoModel.getMsi() != null && Integer.valueOf(creditCardPaymentInfoModel.getMsi()) > 0)
			{
				put(MSI, creditCardPaymentInfoModel.getMsi());
			}
		}
	}

	private String getAddress(final AddressModel addressModel)
	{
		final StringBuilder builder = new StringBuilder();
		builder.append(addressModel.getStreetname());
		builder.append(SPACE);
		builder.append(addressModel.getExternalNumber());
		builder.append(COMMA);
		builder.append(StringUtils.isNotEmpty(addressModel.getInteriorNumber()) ? "Int. " + addressModel.getInteriorNumber() + COMMA
				: StringUtils.EMPTY);
		builder.append(addressModel.getDistrict());
		builder.append(COMMA);
		builder.append(addressModel.getTown());
		builder.append(COMMA);
		builder.append(C_P);
		builder.append(addressModel.getPostalcode());
		builder.append(COMMA);
		builder.append(addressModel.getRegion().getName());
		builder.append(DOT);
		return builder.toString();
	}

	@Override
	protected BaseSiteModel getSite(final CancellationUserCardProcessModel businessProcessModel)
	{
		return businessProcessModel.getSite();
	}

	@Override
	protected CustomerModel getCustomer(final CancellationUserCardProcessModel businessProcessModel)
	{
		return businessProcessModel.getCustomer();
	}

	@Override
	protected LanguageModel getEmailLanguage(final CancellationUserCardProcessModel businessProcessModel)
	{
		return businessProcessModel.getLanguage();
	}

	/**
	 * Gets order entry converter.
	 *
	 * @return the order entry converter
	 */
	public Converter<OrderEntryModel, OrderEntryData> getOrderEntryConverter()
	{
		return orderEntryConverter;
	}

	/**
	 * Sets order entry converter.
	 *
	 * @param orderEntryConverter
	 *           the order entry converter
	 */
	public void setOrderEntryConverter(final Converter<OrderEntryModel, OrderEntryData> orderEntryConverter)
	{
		this.orderEntryConverter = orderEntryConverter;
	}

	/**
	 * Gets enumeration service.
	 *
	 * @return the enumeration service
	 */
	public EnumerationService getEnumerationService()
	{
		return enumerationService;
	}

	/**
	 * Sets enumeration service.
	 *
	 * @param enumerationService
	 *           the enumeration service
	 */
	public void setEnumerationService(final EnumerationService enumerationService)
	{
		this.enumerationService = enumerationService;
	}
}
