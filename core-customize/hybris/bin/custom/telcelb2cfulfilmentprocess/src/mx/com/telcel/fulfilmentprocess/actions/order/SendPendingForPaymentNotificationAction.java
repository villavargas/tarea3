package mx.com.telcel.fulfilmentprocess.actions.order;

import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import mx.com.telcel.core.helper.TelcelEmailsHelper;
import mx.com.telcel.core.model.HolderLineModel;
import mx.com.telcel.core.service.AESService;


public class SendPendingForPaymentNotificationAction extends AbstractProceduralAction<OrderProcessModel>
{

	private static final Logger LOG = Logger.getLogger(SendPendingForPaymentNotificationAction.class);
	private final String FICHA_PAGO_DOWNLOAD_ORDER = "/telcelb2cwebservices/ficha-pago/download";
	private static final String REFERENCE = "REFERENCE";
	private static final String SPEI = "SPEI";

	@Resource(name = "telcelEmailsHelper")
	private TelcelEmailsHelper telcelEmailsHelper;

	@Resource(name = "siteBaseUrlResolutionService")
	private SiteBaseUrlResolutionService siteBaseUrlResolutionService;

	@Resource(name = "aesService")
	private AESService aesService;

	@Override
	public void executeAction(final OrderProcessModel process) throws Exception
	{
		final OrderModel orderModel = process.getOrder();
		final String paymentType = orderModel.getPaymentInfo().getPaymentType();
		if (OrderStatus.PAYMENT_ON_VALIDATION.equals(orderModel.getStatus()))
		{
			LOG.info("### PROCESS TO SEND EMAIL PaymentOnValidationEmailTemplate ####");
			final HolderLineModel addressHolderLine = process.getOrder().getAddressHolderLine();
			telcelEmailsHelper.sendReservedOrderEmail(addressHolderLine.getEmail(), orderModel);
			LOG.info("Process: " + process.getCode() + " in step " + getClass());
		}
		else if (paymentType.equals(REFERENCE) || paymentType.equals(SPEI))
		{
			LOG.info("### PROCESS TO SEND EMAIL PendingPaymentEmailTemplate ####");
			LOG.info("ORDER: " + process.getOrder().getCode() + " paymentType: " + paymentType);
			LOG.info("##########################################################");
			final HolderLineModel addressHolderLine = process.getOrder().getAddressHolderLine();
			final String mediaUrlForSite = siteBaseUrlResolutionService.getWebsiteUrlForSite(process.getOrder().getSite(), true,
					FICHA_PAGO_DOWNLOAD_ORDER);
			LOG.info("URL : " + mediaUrlForSite);
			final String orderEncrypt = aesService.encryptGCM(process.getOrder().getCode());
			LOG.info("ORDER ENCRYPT : " + orderEncrypt);
			final UserModel user = process.getOrder().getUser();
			final String fichaPago = mediaUrlForSite + "?prm=" + encodeURIComponent(orderEncrypt) + "&prm2="
					+ encodeURIComponent(aesService.encryptGCM(user.getUid(), user.getPk().getLongValueAsString()));
			LOG.info("EMAIL : " + addressHolderLine.getEmail());
			LOG.info("FICHA : " + fichaPago);
			telcelEmailsHelper.sendPendingPaymentEmail(addressHolderLine.getEmail(), process.getOrder(), fichaPago);
			LOG.info("Process: " + process.getCode() + " in step " + getClass());
		}
		else
		{
			LOG.info("### PROCESS TO NOT SEND EMAIL PendingPaymentEmailTemplate ####");
			LOG.info("ORDER: " + process.getOrder().getCode() + " paymentType: " + paymentType);
			LOG.info("##########################################################");
		}
	}

	private String encodeURIComponent(final String s)
	{
		String result = null;
		try
		{
			result = URLEncoder.encode(s, StandardCharsets.UTF_8).replaceAll("\\+", "%20").replaceAll("\\%21", "!")
					.replaceAll("\\%27", "'").replaceAll("\\%28", "(").replaceAll("\\%29", ")").replaceAll("\\%7E", "~");
		}
		catch (final Exception e)
		{
			LOG.info("Errot to encode uri component : " + e.getMessage());
			result = s;
		}
		return result;
	}

}
