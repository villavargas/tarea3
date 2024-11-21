package mx.com.telcel.controllers;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.google.gson.Gson;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import mx.com.telcel.core.daos.chipexpress.ChipExpressValidateDao;
import mx.com.telcel.core.model.HolderLineModel;
import mx.com.telcel.services.CustomValidationService;


@Controller
@RequestMapping(value = "/chipexpress")
@CacheControl(directive = CacheControlDirective.NO_CACHE)
@Api(tags = "Chip Express Validate Controller")
public class ChipExpressValidateController
{

	private static final Logger LOG = Logger.getLogger(ChipExpressValidateController.class);
	public static final String AMIGO_CHIP_CATEGORY = "amigo_chip";
	private static final String CATALOG_ID = "telcelProductCatalog";
	private static final String VERSION_ONLINE = "Online";
	private static final String MAX_PURCHASE_MESSAGE_CODE = "maxChipExceded";
	private static final String MAX_PURCHASE_WITH_PRODUCT_MESSAGE_CODE = "maxChipExcededMoreItems";
	private static final String USER_EMAIL_CURP_REQUIRED_MESSAGE_CODE = "curpRequired";
	private static final String USER_EMAIL_CURP_INVALID_MESSAGE_CODE = "curpInvalid";

	@Resource(name = "chipExpressValidateDao")
	private ChipExpressValidateDao chipExpressValidateDao;

	@Resource(name = "customValidationService")
	private CustomValidationService validationService;

	@Resource(name = "categoryService")
	private CategoryService categoryService;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "commerceCartService")
	private CommerceCartService commerceCartService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/validatemaxpurchasedate/{customerId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(nickname = "validatemaxpurchasedate", value = "Validate Max Purchase Interval Date", notes = "Validate Max Purchase In An Interval Date")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public String validateMaxPurchaseDate(@ApiParam(value = "Customer Id", required = true)
	@PathVariable
	String customerId, @ApiParam(value = "Cart Id", required = true)
	@RequestParam(name = "cartId", required = true)
	final String cartId, @ApiParam(value = "Curp", required = false)
	@RequestParam(name = "curp", required = false, defaultValue = "")
	final String curp)
	{
		final ResponseStatusModel response = new ResponseStatusModel();
		try
		{
			catalogVersionService.setSessionCatalogVersion(CATALOG_ID, VERSION_ONLINE);
			final UserModel user = userService.getUserForUID(customerId);
			if (Objects.isNull(user))
			{
				LOG.info("El usuario " + customerId + " no existe");
			}
			boolean isGuestUser = false;
			if (user instanceof CustomerModel)
			{
				final CustomerModel customer = (CustomerModel) user;
				if (CustomerType.GUEST.equals(customer.getType()))
				{
					LOG.info("IS GUEST USER");
					isGuestUser = true;
				}
			}

			final CartModel cart = commerceCartService.getCartForCodeAndUser(cartId, user);
			if (Objects.isNull(cart))
			{
				LOG.info("El carrito " + cartId + " no existe");
			}
			LOG.info("CART CODE : " + cart.getCode());
			response.setStatusCode("");
			if (haveAChipExpressInCart(cart))
			{
				List<OrderModel> orders = chipExpressValidateDao.getOrderWithDateInterval(customerId);
				if (isGuestUser)
				{
					customerId = StringUtils.substringAfter(customerId, "|");
					orders = chipExpressValidateDao.getOrderGuestUserWithDateInterval(customerId);
				}
				LOG.info("HAVE ORDERS : " + !orders.isEmpty());
				if (validationService.is10DigitPhoneNumber(customerId))
				{
					LOG.info("Customer login is a phone");
					if (!orders.isEmpty())
					{
						final int chipExpressCount = countChipExpressForUserPhone(orders);
						int chipExpressCountCart = 0;
						if (Objects.nonNull(cart))
						{
							chipExpressCountCart = chipExpressValidateDao.countChipExpressEntries(cart.getEntries());
						}
						LOG.info("COUNT CHIPS : " + chipExpressCount);
						LOG.info("COUNT CHIPS CART : " + chipExpressCountCart);
						if ((chipExpressCount + chipExpressCountCart) > chipExpressValidateDao.getMaxPurchaseQuantity())
						{
							final boolean onlyChipExpressCart = onlyChipExpressInCart(cart);
							if (onlyChipExpressCart)
							{
								response.setStatusCode(MAX_PURCHASE_MESSAGE_CODE);
							}
							else
							{
								response.setStatusCode(MAX_PURCHASE_WITH_PRODUCT_MESSAGE_CODE);
							}
						}
					}
				}
				else if (validationService.validateIsAnEmail(customerId) && !curp.isEmpty() && validarCURP(curp))
				{
					LOG.info("Customer login is an email");
					if (!orders.isEmpty())
					{
						final int chipExpressCount = countChipExpressForUserEmail(orders, curp);
						int chipExpressCountCart = 0;
						if (Objects.nonNull(cart))
						{
							chipExpressCountCart = chipExpressValidateDao.countChipExpressEntries(cart.getEntries());
						}
						LOG.info("COUNT CHIPS : " + chipExpressCount);
						LOG.info("COUNT CHIPS CART : " + chipExpressCountCart);
						if ((chipExpressCount + chipExpressCountCart) > chipExpressValidateDao.getMaxPurchaseQuantity())
						{
							final boolean onlyChipExpressCart = onlyChipExpressInCart(cart);
							if (onlyChipExpressCart)
							{
								response.setStatusCode(MAX_PURCHASE_MESSAGE_CODE);
							}
							else
							{
								response.setStatusCode(MAX_PURCHASE_WITH_PRODUCT_MESSAGE_CODE);
							}
						}
					}
				}
				else if (validationService.validateIsAnEmail(customerId) && curp.trim().isEmpty())
				{
					response.setStatusCode(USER_EMAIL_CURP_REQUIRED_MESSAGE_CODE);
				}
				else if (!validarCURP(curp))
				{
					response.setStatusCode(USER_EMAIL_CURP_INVALID_MESSAGE_CODE);
				}
			}
		}
		catch (final Exception error)
		{
			LOG.error("CHIP EXPRESS VALIDATE ERROR : " + error.getMessage());
			response.setStatusCode("");
		}
		finally
		{
			final Gson gson = new Gson();
			return gson.toJson(response);
		}
	}

	private int countChipExpressForUserPhone(final List<OrderModel> orders)
	{
		int chipExpressCount = 0;
		for (final OrderModel order : orders)
		{
			chipExpressCount += chipExpressValidateDao.countChipExpressEntries(order.getEntries());
		}
		return chipExpressCount;
	}

	private int countChipExpressForUserEmail(final List<OrderModel> orders, final String curp)
	{
		int chipExpressCount = 0;
		for (final OrderModel order : orders)
		{
			final HolderLineModel holderLine = order.getAddressHolderLine();
			String curpOrder = "";
			if (Objects.nonNull(holderLine))
			{
				curpOrder = holderLine.getCurp();
			}
			for (final AbstractOrderEntryModel detail : order.getEntries())
			{
				final ProductModel product = detail.getProduct();
				final List<CategoryModel> categories = categoryService.getCategoryPathForProduct(product);
				if (Objects.nonNull(categories))
				{
					final boolean isAmigoChip = categories.stream()
							.anyMatch(category -> category.getCode().equalsIgnoreCase(AMIGO_CHIP_CATEGORY));
					if (isAmigoChip && curpOrder.equals(curp))
					{
						chipExpressCount += detail.getQuantity();
					}
				}
			}
		}
		return chipExpressCount;
	}

	private boolean onlyChipExpressInCart(final CartModel cart)
	{
		boolean onlyChipExpress = true;
		if (Objects.nonNull(cart))
		{
			for (final AbstractOrderEntryModel detail : cart.getEntries())
			{
				final ProductModel product = detail.getProduct();
				final List<CategoryModel> categories = categoryService.getCategoryPathForProduct(product);
				if (Objects.nonNull(categories))
				{
					final boolean isAmigoChip = categories.stream()
							.anyMatch(category -> category.getCode().equalsIgnoreCase(AMIGO_CHIP_CATEGORY));
					if (!isAmigoChip)
					{
						return false;
					}
				}
			}
		}
		else
		{
			onlyChipExpress = false;
		}
		return onlyChipExpress;
	}

	private boolean haveAChipExpressInCart(final CartModel cart)
	{
		boolean haveChipExpress = false;
		if (Objects.nonNull(cart))
		{
			for (final AbstractOrderEntryModel detail : cart.getEntries())
			{
				final ProductModel product = detail.getProduct();
				final List<CategoryModel> categories = categoryService.getCategoryPathForProduct(product);
				if (Objects.nonNull(categories))
				{
					final boolean isAmigoChip = categories.stream()
							.anyMatch(category -> category.getCode().equalsIgnoreCase(AMIGO_CHIP_CATEGORY));
					if (isAmigoChip)
					{
						return true;
					}
				}
			}
		}
		else
		{
			haveChipExpress = false;
		}
		LOG.info("CARRITO TIENE CHIP EXPRESS : " + haveChipExpress);
		return haveChipExpress;
	}

	private boolean validarCURP(final String curp)
	{
		final String regex = "[A-Z]{1}[AEIOU]{1}[A-Z]{2}[0-9]{2}" + "(0[1-9]|1[0-2])(0[1-9]|1[0-9]|2[0-9]|3[0-1])" + "[HM]{1}"
				+ "(AS|BC|BS|CC|CS|CH|CL|CM|DF|DG|GT|GR|HG|JC|MC|MN|MS|NT|NL|OC|PL|QT|QR|SP|SL|SR|TC|TS|TL|VZ|YN|ZS|NE)"
				+ "[B-DF-HJ-NP-TV-Z]{3}" + "[0-9A-Z]{1}[0-9]{1}$";

		final Pattern patron = Pattern.compile(regex);
		if (!patron.matcher(curp).matches())
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	class ResponseStatusModel
	{

		private String statusCode;
		private Integer quantity;

		public String getStatusCode()
		{
			return statusCode;
		}

		public void setStatusCode(final String statusCode)
		{
			this.statusCode = statusCode;
		}

		public Integer getQuantity()
		{
			return quantity;
		}

		public void setQuantity(final Integer quantity)
		{
			this.quantity = quantity;
		}

	}

}
