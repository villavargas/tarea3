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
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.text.DecimalFormat;
import java.util.Objects;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import mx.com.telcel.core.model.AdditionalServiceEntryModel;
import mx.com.telcel.core.model.CancellationOthersItemProcessModel;
import mx.com.telcel.core.util.TelcelUtil;
import mx.com.telcel.facades.product.TelcelProductFacade;


/**
 * The type Cancellation others item email contex.
 */
public class CancellationOthersItemEmailContext extends AbstractEmailContext<CancellationOthersItemProcessModel>
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
	private static final String PAYMENT_TYPE = "paymentType";
	private static final String SUBTOTAL = "subtotal";
	private static final String DELIVERY_COST = "deliveryCost";
	private static final String TOTAL = "total";
	private static final String ADITIONAL_SERVICE_NAME = "aditionalServiceName";
	private final DecimalFormat decimalFormat = new DecimalFormat("###,##0.00");

	private Converter<OrderEntryModel, OrderEntryData> orderEntryConverter;
	private EnumerationService enumerationService;

	@Resource(name = "productFacade")
	private TelcelProductFacade productFacade;

	@Resource
	private TelcelUtil telcelUtil;

	@Override
	public void init(final CancellationOthersItemProcessModel cancellationOthersItemProcessModel,
			final EmailPageModel emailPageModel)
	{
		super.init(cancellationOthersItemProcessModel, emailPageModel);
		final ConsignmentModel consignmentModel = cancellationOthersItemProcessModel.getConsignment();
		final ReturnRequestModel returnRequestModel = cancellationOthersItemProcessModel.getReturnRequest();
		final AbstractOrderModel orderModel = consignmentModel.getOrder();
		final CustomerModel customerModel = (CustomerModel) orderModel.getUser();

		put(CUSTOMER_ID, customerModel.getCustomerID());
		put(CUSTOMER_NAME, telcelUtil.nameTwoFirstWords(customerModel.getName()));
		put(ORDER_ID, orderModel.getCode());
		final AddressModel addressModel = orderModel.getDeliveryAddress();
		if (Objects.nonNull(addressModel))
		{
			put(ADDRESS, getAddress(addressModel));
		}
		final AbstractOrderEntryModel entryModel = consignmentModel.getConsignmentEntries().iterator().next().getOrderEntry();
		final OrderEntryData orderEntryData = orderEntryConverter.convert((OrderEntryModel) entryModel);
		productFacade.setProductDataPaqueteAmigo(orderEntryData.getProduct(), entryModel.getAdditionalServiceEntries());
		put(ENTRY, orderEntryData);
		put("consignmentCode", consignmentModel.getCode());

		final boolean returnAditionalService = returnRequestModel.getRefundAdditionalService()
				&& returnRequestModel.getAdditionalServiceValue() > 0D && consignmentModel.getAdditionalServiceEntry() != null;
		put(DELIVERY_COST, decimalFormat.format(0));
		if (returnRequestModel.getRefundDeliveryCost() && orderModel.getDeliveryCost() > 0D)
		{
			put(SUBTOTAL, decimalFormat.format(
					getTotalWithAS(entryModel.getBasePrice(), returnAditionalService, consignmentModel.getAdditionalServiceEntry())));
			put(DELIVERY_COST, decimalFormat.format(orderModel.getDeliveryCost()));
			put(TOTAL, decimalFormat.format(getTotalWithAS(Double.sum(entryModel.getBasePrice(), orderModel.getDeliveryCost()),
					returnAditionalService, consignmentModel.getAdditionalServiceEntry())));
		}
		else
		{
			put(TOTAL, decimalFormat.format(
					getTotalWithAS(entryModel.getBasePrice(), returnAditionalService, consignmentModel.getAdditionalServiceEntry())));
		}
		if (returnAditionalService)
		{
			put(ADITIONAL_SERVICE_NAME, consignmentModel.getAdditionalServiceEntry().getAdditionalServiceProduct().getName());
		}
		put(PAYMENT_TYPE, getPaymentType(orderModel.getPaymentInfo()));
		put("StringUtils", StringUtils.class);

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

	private String getPaymentType(final PaymentInfoModel paymentInfo)
	{
		String result = "";

		String type = paymentInfo.getPaymentType();
		if (StringUtils.isNotEmpty(type))
		{
			switch (type)
			{
				case "SPEI":
					result = "SPEI";
					break;
				case "REFERENCE":
					result = "Referencia";
					break;
			}
		}

		if (StringUtils.isEmpty(result))
		{
			if (CollectionUtils.isNotEmpty(paymentInfo.getReceipt()))
			{
				type = paymentInfo.getReceipt().get(0).getType();
				if (StringUtils.isNotEmpty(type))
				{
					switch (type)
					{
						case "DEBIT":
							result = "Debito";
							break;
						case "CREDIT":
							result = "Credito";
							break;
					}
				}
			}

		}

		return result;
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
	protected BaseSiteModel getSite(final CancellationOthersItemProcessModel businessProcessModel)
	{
		return businessProcessModel.getSite();
	}

	@Override
	protected CustomerModel getCustomer(final CancellationOthersItemProcessModel businessProcessModel)
	{
		return businessProcessModel.getCustomer();
	}

	@Override
	protected LanguageModel getEmailLanguage(final CancellationOthersItemProcessModel businessProcessModel)
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
