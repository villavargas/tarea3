package mx.com.telcel.controllers;

import static mx.com.telcel.constants.Telcelb2cwebservicesConstants.CLIENT_CREDENTIAL_AUTHORIZATION_NAME;
import static mx.com.telcel.constants.Telcelb2cwebservicesConstants.PASSWORD_AUTHORIZATION_NAME;

import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import mx.com.telcel.facades.order.data.OrderDetailData;
import mx.com.telcel.facades.order.data.OrderHeaderData;


@Controller
@RequestMapping(value = "/customerOrder")
@Api(tags = "UserOrder")
public class UserOrderController
{

	private static final Logger LOG = LoggerFactory.getLogger(UserOrderController.class);

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "baseStoreService")
	private BaseStoreService baseStoreService;

	@Resource(name = "customerAccountService")
	private CustomerAccountService customerAccountService;

	@Resource(name = "orderHeaderConverter")
	private Converter<OrderModel, OrderHeaderData> orderHeaderConverter;

	@Resource(name = "orderDetailConverter")
	private Converter<OrderModel, OrderDetailData> orderDetailConverter;

	@ResponseBody
	@GetMapping(value = "/header", produces = "application/json")
	@ApiOperation(value = "Header Order", notes = "This Method Return Header Order", produces = "application/json", authorizations =
	{ @Authorization(value = CLIENT_CREDENTIAL_AUTHORIZATION_NAME), @Authorization(value = PASSWORD_AUTHORIZATION_NAME) })
	public List<OrderHeaderData> header(@ApiParam(value = "User Name", required = true)
	@RequestParam(name = "username")
	final String username)
	{
		final CustomerModel customer = (CustomerModel) userService.getUserForUID(username);
		LOG.info("USER : " + customer.getCustomerID());
		final Collection<OrderModel> orders = customer.getOrders();
		if (Objects.isNull(orders))
		{
			return new ArrayList<>();
		}
		return orderHeaderConverter.convertAll(orders);
	}

	@ResponseBody
	@GetMapping(value = "/detail", produces = "application/json")
	@ApiOperation(value = "Detail Order", notes = "This Method Return Detail Order", produces = "application/json", authorizations =
	{ @Authorization(value = CLIENT_CREDENTIAL_AUTHORIZATION_NAME), @Authorization(value = PASSWORD_AUTHORIZATION_NAME) })
	public OrderDetailData tienda(@ApiParam(value = "Order Number", required = true)
	@RequestParam(name = "orderNumber")
	final String orderNumber)
	{
		BaseStoreModel baseStoreModel = baseStoreService.getCurrentBaseStore();
		if (Objects.isNull(baseStoreModel))
		{
			baseStoreModel = baseStoreService.getBaseStoreForUid("telcel");
		}
		if (Objects.nonNull(baseStoreModel))
		{
			final OrderModel order = customerAccountService.getOrderForCode(orderNumber, baseStoreModel);
			if (Objects.isNull(order))
			{
				LOG.info("Order Dont Exist");
				return null;
			}
			return orderDetailConverter.convert(order);
		}
		else
		{
			LOG.info("Base store telcel dont exist");
			return null;
		}
	}

}
