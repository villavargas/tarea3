/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.facades.process.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.coupon.data.CouponData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.util.Config;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import mx.com.telcel.core.model.HolderLineModel;


/**
 * Velocity context for a order notification email.
 */
public class OrderNotificationEmailContext extends AbstractEmailContext<OrderProcessModel>
{
	private static final String CUSTOMER_PHONE = "customerPhone";
	private static final String CUSTOMER_NAME = "customerName";
	private static final String ORDER_CODE = "orderCode";
	private static final String HOLDER_LINE_NAME = "holderLineName";
	private static final String SPACE = " ";
	private static final String SPACE_COMMA = ", ";
	private static final String DELIVERY_COST = "deliveryCost";
	private static final String DELIVERY_ADDRESS = "deliveryAddress";
	private static final String $ = "$";
	private static final String TOTAL = "total";
	private static final String CP = "C.P.";
	public static final String DAYS = "estimated.delivery.date.days";
	public static final String ESTIMATED_DELIVERY_DATE = "estimatedDeliveryDate";
	public static final String SLASH = "/";
	public static final String ENTRIES = "entries";
	private Converter<OrderModel, OrderData> orderConverter;
	private OrderData orderData;
	private List<CouponData> giftCoupons;


	@Override
	public void init(final OrderProcessModel orderProcessModel, final EmailPageModel emailPageModel)
	{
		super.init(orderProcessModel, emailPageModel);
		final OrderModel orderModel = orderProcessModel.getOrder();
		put(ORDER_CODE, orderModel.getCode());

		final UserModel userModel = orderModel.getUser();
		if (userModel instanceof CustomerModel)
		{
			put(CUSTOMER_PHONE, ((CustomerModel) userModel).getNumberPhone());
			put(CUSTOMER_NAME, userModel.getName());
		}

		put(DELIVERY_COST, $ + orderModel.getDeliveryCost());
		put(TOTAL, $ + orderModel.getTotalPrice());
		final HolderLineModel holderLineModel = orderModel.getAddressHolderLine();
		if (Objects.nonNull(holderLineModel))
		{
			put(HOLDER_LINE_NAME, holderLineModel.getName() + SPACE + holderLineModel.getLastName());
		}
		final AddressModel addressModel = orderModel.getDeliveryAddress();
		if (Objects.nonNull(addressModel))
		{
			final String interior = StringUtils.isNotEmpty(addressModel.getInteriorNumber())
					? addressModel.getInteriorNumber() + SPACE_COMMA
					: "";
			put(DELIVERY_ADDRESS,
					addressModel.getStreetname() + SPACE_COMMA + addressModel.getExternalNumber() + SPACE_COMMA + interior
							+ addressModel.getDistrict() + SPACE_COMMA + addressModel.getTown() + SPACE_COMMA + CP
							+ addressModel.getPostalcode() + SPACE_COMMA + addressModel.getRegion().getName());
		}

		put(ESTIMATED_DELIVERY_DATE, getEstimatedDeliveryDate(orderModel.getCreationtime()));

		put("StringUtils", StringUtils.class);
		put("ObjectUtils", ObjectUtils.class);
		orderData = getOrderConverter().convert(orderProcessModel.getOrder());
		put(ENTRIES, orderData.getEntries());
		giftCoupons = orderData.getAppliedOrderPromotions().stream()
				.filter(x -> CollectionUtils.isNotEmpty(x.getGiveAwayCouponCodes())).flatMap(p -> p.getGiveAwayCouponCodes().stream())
				.collect(Collectors.toList());
	}

	private String getEstimatedDeliveryDate(final Date creationtime)
	{
		int day = 0;
		final int days = Config.getInt(DAYS, 3);
		final Calendar dateTime = Calendar.getInstance();
		dateTime.setTime(creationtime);
		while (day < days)
		{
			if (dateTime.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && dateTime.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)
			{
				day++;
			}
			dateTime.add(Calendar.DATE, 1);
		}
		return dateTime.get(Calendar.DAY_OF_MONTH) + SLASH + Config.getParameter("month.name." + dateTime.get(Calendar.MONTH))
				+ SLASH + dateTime.get(Calendar.YEAR);
	}


	@Override
	protected BaseSiteModel getSite(final OrderProcessModel orderProcessModel)
	{
		return orderProcessModel.getOrder().getSite();
	}

	@Override
	protected CustomerModel getCustomer(final OrderProcessModel orderProcessModel)
	{
		return (CustomerModel) orderProcessModel.getOrder().getUser();
	}

	/**
	 * Gets order converter.
	 *
	 * @return the order converter
	 */
	protected Converter<OrderModel, OrderData> getOrderConverter()
	{
		return orderConverter;
	}

	/**
	 * Sets order converter.
	 *
	 * @param orderConverter
	 *           the order converter
	 */
	@Required
	public void setOrderConverter(final Converter<OrderModel, OrderData> orderConverter)
	{
		this.orderConverter = orderConverter;
	}

	/**
	 * Gets order.
	 *
	 * @return the order
	 */
	public OrderData getOrder()
	{
		return orderData;
	}

	@Override
	protected LanguageModel getEmailLanguage(final OrderProcessModel orderProcessModel)
	{
		return orderProcessModel.getOrder().getLanguage();
	}

	/**
	 * Gets coupons.
	 *
	 * @return the coupons
	 */
	public List<CouponData> getCoupons()
	{
		return giftCoupons;
	}
}
