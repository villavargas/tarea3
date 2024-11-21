/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.fulfilmentprocess.actions.consignment;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.orderprocessing.events.SendDeliveryMessageEvent;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.util.Config;

import java.util.Collection;
import java.util.Optional;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import mx.com.telcel.core.helper.TelcelEmailsHelper;
import mx.com.telcel.core.model.HolderLineModel;


public class SendDeliveryMessageAction extends AbstractProceduralAction<ConsignmentProcessModel>
{
	private static final Logger LOG = Logger.getLogger(SendDeliveryMessageAction.class);
	private static final String ACCESORIOS_CLASSIFICATION = "telcel.accesorios.accesoriosclassification";
	private static final String CHIP_CLASSIFICATION = "chip.amigo.chipclassification";
	private static final String SMARTPHONE_CHIPCLASSIFICATION = "telcel.smartphone.smartphoneclassification";
	private static final String TABLETS_CHIPCLASSIFICATION = "telcel.tablets.tabletsclassification";
	private static final String ACCESORIOS_CATEGORY = "telcel.accesorios.accesorioscategory";
	private static final String CHIP_CATEGORY = "chip.amigo.chipcategory";
	private static final String SMARTPHONE_CATEGORY = "telcel.smartphone.smartphonecategory";
	private static final String TABLETS_CATEGORY = "telcel.tablets.tabletscategory";
	private static final String TELCEL_PRODUCT_CATALOG = "telcelProductCatalog";
	private static final String ONLINE = "Online";

	private EventService eventService;

	@Resource(name = "telcelEmailsHelper")
	private TelcelEmailsHelper telcelEmailsHelper;

	@Resource(name = "catalogVersionService")
	private CatalogVersionService catalogVersionService;

	@Override
	public void executeAction(final ConsignmentProcessModel process)
	{
		final HolderLineModel addressHolderLine = process.getConsignment().getOrder().getAddressHolderLine();
		for (final ConsignmentEntryModel entryModel : process.getConsignment().getConsignmentEntries())
		{
			for (int qty = 1; qty <= entryModel.getQuantity(); qty++)
			{
				final ProductModel product = entryModel.getOrderEntry().getProduct();
				String productType = "";

				catalogVersionService.setSessionCatalogVersion(TELCEL_PRODUCT_CATALOG, ONLINE);
				final Optional<ClassificationClassModel> classificationClass = product.getClassificationClasses().stream()
						.findFirst();
				final Collection<CategoryModel> categories = product.getSupercategories();

				//Find by classification
				if (classificationClass.isPresent())
				{
					if (Config.getParameter(ACCESORIOS_CLASSIFICATION).equals(classificationClass.get().getCode()))
					{
						productType = "A";
					}
					else if (Config.getParameter(CHIP_CLASSIFICATION).equals(classificationClass.get().getCode()))
					{
						productType = "CE";
					}
					else if (Config.getParameter(SMARTPHONE_CHIPCLASSIFICATION).equals(classificationClass.get().getCode()))
					{
						productType = "RI";
					}
					else if (Config.getParameter(TABLETS_CHIPCLASSIFICATION).equals(classificationClass.get().getCode()))
					{
						productType = "RI";
					}
				}
				//Find By Category
				if (productType.isEmpty())
				{
					if (containsCategory(categories, Config.getParameter(ACCESORIOS_CATEGORY)))
					{
						productType = "A";
					}
					else if (containsCategory(categories, Config.getParameter(CHIP_CATEGORY)))
					{
						productType = "CE";
					}
					else if (containsCategory(categories, Config.getParameter(SMARTPHONE_CATEGORY)))
					{
						productType = "RI";
					}
					else if (containsCategory(categories, Config.getParameter(TABLETS_CATEGORY)))
					{
						productType = "RI";
					}
				}
				LOG.info("PRODUCT TYPE : " + productType);
				telcelEmailsHelper.sendSuccessfulDeliveryEmail(addressHolderLine.getEmail(), product, process.getConsignment(),
						productType);
			}
		}
		if (LOG.isInfoEnabled())
		{
			LOG.info("Process: " + process.getCode() + " in step " + getClass());
		}
	}

	public boolean containsCategory(final Collection<CategoryModel> list, final String code)
	{
		return list.stream().filter(o -> o.getCode().equals(code)).findFirst().isPresent();
	}

	protected EventService getEventService()
	{
		return eventService;
	}

	@Required
	public void setEventService(final EventService eventService)
	{
		this.eventService = eventService;
	}

	protected SendDeliveryMessageEvent getEvent(final ConsignmentProcessModel process)
	{
		return new SendDeliveryMessageEvent(process);
	}

}
