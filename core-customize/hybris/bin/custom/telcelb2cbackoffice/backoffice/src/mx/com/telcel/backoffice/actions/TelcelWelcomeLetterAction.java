//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package mx.com.telcel.backoffice.actions;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.util.Config;

import java.util.Collection;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.hybris.backoffice.widgets.notificationarea.NotificationService;
import com.hybris.backoffice.widgets.notificationarea.event.NotificationEvent;
import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import com.hybris.cockpitng.actions.CockpitAction;

import mx.com.telcel.core.enums.WelcomeLetterType;
import mx.com.telcel.core.services.TelcelJasperReportsService;


/**
 * The type Telcel welcome letter action.
 */
public class TelcelWelcomeLetterAction implements CockpitAction<ConsignmentModel, Object>
{
	private static final String CONFIRMATION_MESSAGE = "hmc.action.telcelwelcomeletter.confirmation.message";
	private static final String CALL_TELCEL_WELCOME_LETTER_EVENT = "telcelb2cbackoffice.telcelwelcomeletter.event";
	private static final boolean NEEDS_CONFIRMATION = false;
	private static final String TELMEX = "telmex";
	private static final String AMIGO_CHIPCLASSIFICATION = "amigo_chipclassification";
	private static final String CHIP_CATEGORY = "chip.amigo.chipcategory";
	private static final Logger LOG = Logger.getLogger(TelcelWelcomeLetterAction.class);

	@Resource(name = "notificationService")
	private NotificationService notificationService;

	@Resource(name = "telcelJasperReportsService")
	private TelcelJasperReportsService telcelJasperReportsService;

	@Override
	public boolean canPerform(final ActionContext<ConsignmentModel> ctx)
	{
		if (ctx != null)
		{
			final Object data = ctx.getData();
			if (data instanceof ConsignmentModel)
			{
				final ConsignmentModel consignmentModel = (ConsignmentModel) data;
				if (consignmentModel.getOrder() != null && consignmentModel.getStatus() != null
						&& consignmentModel.getStatus().equals(ConsignmentStatus.READY_TO_PACK))
				{
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String getConfirmationMessage(final ActionContext<ConsignmentModel> ctx)
	{
		return ctx.getLabel(CONFIRMATION_MESSAGE);
	}

	@Override
	public boolean needsConfirmation(final ActionContext<ConsignmentModel> arg0)
	{
		return NEEDS_CONFIRMATION;
	}

	@Override
	public ActionResult<Object> perform(final ActionContext<ConsignmentModel> ctx)
	{
		final Object data = ctx.getData();

		if ((data != null) && (data instanceof ConsignmentModel))
		{
			final ConsignmentModel consignmentModel = (ConsignmentModel) data;
			final AbstractOrderModel orderModel = consignmentModel.getOrder();
			final ProductModel productModel = consignmentModel.getConsignmentEntries().iterator().next().getOrderEntry()
					.getProduct();
			try
			{
				final boolean isTelmexOrder = orderModel.getSite().getUid().equalsIgnoreCase(TELMEX);
				boolean isChip = false;
				if (containsClassification(productModel.getClassificationClasses(), AMIGO_CHIPCLASSIFICATION))
				{
					LOG.info("##### Classification Chip");
					isChip = true;
				}
				if (!isChip)
				{
					if (containsCategory(productModel.getSupercategories(), Config.getParameter(CHIP_CATEGORY)))
					{
						LOG.info("##### Category Chip");
						isChip = true;
					}
				}
				if (isChip)
				{
					telcelJasperReportsService.createWelcomeLetter(consignmentModel, WelcomeLetterType.CHIP, isTelmexOrder);
				}
				else if (consignmentModel.getAdditionalServiceEntry() != null)
				{
					telcelJasperReportsService.createWelcomeLetter(consignmentModel, WelcomeLetterType.WITH_PACKAGE, isTelmexOrder);
				}
				else
				{
					telcelJasperReportsService.createWelcomeLetter(consignmentModel, WelcomeLetterType.WITHOUT_PACKAGE, isTelmexOrder);
				}

			}
			catch (final Exception e)
			{
				e.printStackTrace();
				notificationService.notifyUser(notificationService.getWidgetNotificationSource(ctx), CALL_TELCEL_WELCOME_LETTER_EVENT,
						NotificationEvent.Level.FAILURE);
				return new ActionResult<Object>(ActionResult.ERROR, consignmentModel);
			}
			notificationService.notifyUser(notificationService.getWidgetNotificationSource(ctx), CALL_TELCEL_WELCOME_LETTER_EVENT,
					NotificationEvent.Level.SUCCESS);

			return new ActionResult<Object>(ActionResult.SUCCESS, consignmentModel);
		}
		else
		{
			return new ActionResult(ActionResult.ERROR);
		}

	}

	public boolean containsClassification(final Collection<ClassificationClassModel> list, final String code)
	{
		return list.stream().filter(o -> o.getCode().equals(code)).findFirst().isPresent();
	}

	public boolean containsCategory(final Collection<CategoryModel> list, final String code)
	{
		return list.stream().filter(o -> o.getCode().equals(code)).findFirst().isPresent();
	}

}
