package mx.com.telcel.controllers;

import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.Iterator;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import mx.com.telcel.core.helper.TelcelEmailsHelper;


@Controller
@RequestMapping(value = "/temporalTestEmail")
public class TemporalTestSenderEmail
{

	@Resource(name = "baseStoreService")
	private BaseStoreService baseStoreService;

	@Resource(name = "customerAccountService")
	private CustomerAccountService customerAccountService;

	@Resource(name = "telcelEmailsHelper")
	private TelcelEmailsHelper telcelEmailsHelper;

	@ResponseBody
	@GetMapping(value = "/sendPendingPayment", produces = "application/json")
	public String sendPendingPayment(@RequestParam(name = "orderNumber")
	final String orderNumber, @RequestParam(name = "email")
	final String email)
	{
		final BaseStoreModel baseStoreModel = baseStoreService.getBaseStoreForUid("telcel");
		final OrderModel order = customerAccountService.getOrderForCode(orderNumber, baseStoreModel);
		telcelEmailsHelper.sendPendingPaymentEmail(email, order, "");
		return "SEND PENDING PAYMENT";
	}

	@ResponseBody
	@GetMapping(value = "/sendSuccessfulPurchase", produces = "application/json")
	public String sendSuccessfulPurchase(@RequestParam(name = "orderNumber")
	final String orderNumber, @RequestParam(name = "email")
	final String email)
	{
		final BaseStoreModel baseStoreModel = baseStoreService.getBaseStoreForUid("telcel");
		final OrderModel order = customerAccountService.getOrderForCode(orderNumber, baseStoreModel);
		telcelEmailsHelper.sendSuccessfulPurchaseEmail(email, order, "SPEI");
		return "SEND SUCCESSFUL PURCHASE";
	}

	@ResponseBody
	@GetMapping(value = "/sendSuccessfulDelivery", produces = "application/json")
	public String sendSuccessfulDelivery(@RequestParam(name = "orderNumber")
	final String orderNumber, @RequestParam(name = "productType")
	final String productType, @RequestParam(name = "email")
	final String email, @RequestParam(name = "consignment")
	final String consignmentCode)
	{
		final BaseStoreModel baseStoreModel = baseStoreService.getBaseStoreForUid("telcel");
		final OrderModel order = customerAccountService.getOrderForCode(orderNumber, baseStoreModel);
		//final ConsignmentModel consignment = order.getConsignments().iterator().next();
		ConsignmentModel consignment = null;
		final Iterator<ConsignmentModel> iterator = order.getConsignments().iterator();
		while (iterator.hasNext())
		{
			final ConsignmentModel con = iterator.next();
			if (con.getCode().equals(consignmentCode))
			{
				consignment = con;
				break;
			}
		}
		final AbstractOrderEntryModel entry = consignment.getConsignmentEntries().iterator().next().getOrderEntry();
		//productType = RI, CE, A
		telcelEmailsHelper.sendSuccessfulDeliveryEmail(email, entry.getProduct(), consignment, productType);
		return "SEND SUCCESSFUL DELIVERY";
	}

	@ResponseBody
	@GetMapping(value = "/sendCancelacionEntregaT", produces = "application/json")
	public String sendCancelacionEntregaT(@RequestParam(name = "orderNumber")
	final String orderNumber)
	{
		final BaseStoreModel baseStoreModel = baseStoreService.getBaseStoreForUid("telcel");
		final OrderModel order = customerAccountService.getOrderForCode(orderNumber, baseStoreModel);
		final ConsignmentModel consignment = order.getConsignments().iterator().next();
		final ReturnRequestModel returnrequest = order.getReturnRequests().iterator().next();
		telcelEmailsHelper.sendCancellationCardItemProcessEmail(consignment, returnrequest);
		return "SEND CANCELACION ENTREGA T";
	}

	@ResponseBody
	@GetMapping(value = "/sendCancelacionEntregaES", produces = "application/json")
	public String sendCancelacionEntregaES(@RequestParam(name = "orderNumber")
	final String orderNumber)
	{
		final BaseStoreModel baseStoreModel = baseStoreService.getBaseStoreForUid("telcel");
		final OrderModel order = customerAccountService.getOrderForCode(orderNumber, baseStoreModel);
		final ConsignmentModel consignment = order.getConsignments().iterator().next();
		final ReturnRequestModel returnrequest = order.getReturnRequests().iterator().next();
		telcelEmailsHelper.sendCancellationOthersItemProcessEmail(consignment, returnrequest);
		return "SEND CANCELACION ENTREGA ES";
	}

	@ResponseBody
	@GetMapping(value = "/sendCancelacionTelcel", produces = "application/json")
	public String sendCancelacionTelcel(@RequestParam(name = "orderNumber")
	final String orderNumber)
	{
		final BaseStoreModel baseStoreModel = baseStoreService.getBaseStoreForUid("telcel");
		final OrderModel order = customerAccountService.getOrderForCode(orderNumber, baseStoreModel);
		final ConsignmentModel consignment = order.getConsignments().iterator().next();
		final ReturnRequestModel returnrequest = order.getReturnRequests().iterator().next();
		telcelEmailsHelper.sendCancellationTelcelEmailProcessEmail(consignment, returnrequest);
		return "SEND CANCELACION TELCEL";
	}

	@ResponseBody
	@GetMapping(value = "/sendCancelacionUsuarioT", produces = "application/json")
	public String sendCancelacionUsuarioT(@RequestParam(name = "orderNumber")
	final String orderNumber)
	{
		final BaseStoreModel baseStoreModel = baseStoreService.getBaseStoreForUid("telcel");
		final OrderModel order = customerAccountService.getOrderForCode(orderNumber, baseStoreModel);
		final ConsignmentModel consignment = order.getConsignments().iterator().next();
		final ReturnRequestModel returnrequest = order.getReturnRequests().iterator().next();
		telcelEmailsHelper.sendCancellationUserCardProcessEmail(consignment, returnrequest);
		return "SEND CANCELACION USUARIO T";
	}

	@ResponseBody
	@GetMapping(value = "/sendCancelacionUsuarioES", produces = "application/json")
	public String sendCancelacionUsuarioES(@RequestParam(name = "orderNumber")
	final String orderNumber)
	{
		final BaseStoreModel baseStoreModel = baseStoreService.getBaseStoreForUid("telcel");
		final OrderModel order = customerAccountService.getOrderForCode(orderNumber, baseStoreModel);
		final ConsignmentModel consignment = order.getConsignments().iterator().next();
		final ReturnRequestModel returnrequest = order.getReturnRequests().iterator().next();
		telcelEmailsHelper.sendCancellationUserOthersProcessEmail(consignment, returnrequest);
		return "SEND CANCELACION USUARIO ES";
	}

	@ResponseBody
	@GetMapping(value = "/sendCancelacionPagoInconcluso", produces = "application/json")
	public String sendCancelacionPagoInconcluso(@RequestParam(name = "orderNumber")
	final String orderNumber)
	{
		final BaseStoreModel baseStoreModel = baseStoreService.getBaseStoreForUid("telcel");
		final OrderModel order = customerAccountService.getOrderForCode(orderNumber, baseStoreModel);
		//final ConsignmentModel consignment = order.getConsignments().iterator().next();
		//final ReturnRequestModel returnrequest = order.getReturnRequests().iterator().next();
		telcelEmailsHelper.sendCancellationUnderpaymentEmail(order);
		return "SEND CANCELACION PAGO INCONCLUSO";
	}

}
