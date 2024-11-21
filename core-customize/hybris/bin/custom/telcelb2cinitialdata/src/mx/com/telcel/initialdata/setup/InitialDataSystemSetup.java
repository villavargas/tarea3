/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.initialdata.setup;

import mx.com.telcel.initialdata.setup.dataimport.impl.CoreDataImportService;
import mx.com.telcel.initialdata.setup.dataimport.impl.SampleDataImportService;
import de.hybris.platform.commerceservices.setup.AbstractSystemSetup;
import de.hybris.platform.commerceservices.setup.data.ImportData;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetup.Process;
import de.hybris.platform.core.initialization.SystemSetup.Type;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import de.hybris.platform.core.initialization.SystemSetupParameterMethod;
import mx.com.telcel.initialdata.constants.Telcelb2cInitialDataConstants;
import de.hybris.platform.commerceservices.setup.SetupImpexService;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import javax.annotation.Resource;

import de.hybris.platform.commerceservices.setup.events.CoreDataImportedEvent;
import de.hybris.platform.commerceservices.setup.events.SampleDataImportedEvent;

/**
 * This class provides hooks into the system's initialization and update processes.
 */
@SystemSetup(extension = Telcelb2cInitialDataConstants.EXTENSIONNAME)
public class InitialDataSystemSetup extends AbstractSystemSetup
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(InitialDataSystemSetup.class);

	private static final String IMPORT_CORE_DATA = "importCoreData";
	private static final String IMPORT_SAMPLE_DATA = "importSampleData";
	private static final String ACTIVATE_SOLR_CRON_JOBS = "activateSolrCronJobs";

	public static final String TELCEL_INITIAL_DATA = "telcelb2cinitialdata";

	public static final String TELCEL_STORE = "telcel";
	public static final String TELCEL_CONTENT_CATALOG = "telcelContentCatalog";
	public static final String TELCEL_PRODUCT_CATALOG = "telcelProductCatalog";

	private CoreDataImportService coreDataImportService;
	private SampleDataImportService sampleDataImportService;

	@Resource
	private SetupImpexService setupImpexService;

	/**
	 * Generates the Dropdown and Multi-select boxes for the project data import
	 */
	@Override
	@SystemSetupParameterMethod
	public List<SystemSetupParameter> getInitializationOptions()
	{
		final List<SystemSetupParameter> params = new ArrayList<SystemSetupParameter>();

		params.add(createBooleanSystemSetupParameter(IMPORT_CORE_DATA, "Import Core Data", true));
		params.add(createBooleanSystemSetupParameter(IMPORT_SAMPLE_DATA, "Import Sample Data", true));
		params.add(createBooleanSystemSetupParameter(ACTIVATE_SOLR_CRON_JOBS, "Activate Solr Cron Jobs", true));
		// Add more Parameters here as you require

		return params;
	}

	/**
	 * Implement this method to create initial objects. This method will be called by system creator during
	 * initialization and system update. Be sure that this method can be called repeatedly.
	 * 
	 * @param context
	 *           the context provides the selected parameters and values
	 */
	@SystemSetup(type = Type.ESSENTIAL, process = Process.ALL)
	public void createEssentialData(final SystemSetupContext context)
	{
		// Add Essential Data here as you require
	}

	/**
	 * Implement this method to create data that is used in your project. This method will be called during the system
	 * initialization. <br>
	 * Add import data for each site you have configured
	 *
	 * <pre>
	 * final List<ImportData> importData = new ArrayList<ImportData>();
	 *
	 * final ImportData sampleImportData = new ImportData();
	 * sampleImportData.setProductCatalogName(SAMPLE_PRODUCT_CATALOG_NAME);
	 * sampleImportData.setContentCatalogNames(Arrays.asList(SAMPLE_CONTENT_CATALOG_NAME));
	 * sampleImportData.setStoreNames(Arrays.asList(SAMPLE_STORE_NAME));
	 * importData.add(sampleImportData);
	 *
	 * getCoreDataImportService().execute(this, context, importData);
	 * getEventService().publishEvent(new CoreDataImportedEvent(context, importData));
	 *
	 * getSampleDataImportService().execute(this, context, importData);
	 * getEventService().publishEvent(new SampleDataImportedEvent(context, importData));
	 * </pre>
	 *
	 * @param context
	 *           the context provides the selected parameters and values
	 */
	@SystemSetup(type = Type.PROJECT, process = Process.ALL)
	public void createProjectData(final SystemSetupContext context)
	{
		final List<ImportData> importData = new ArrayList<ImportData>();
		importData.add(returnImportData(TELCEL_STORE));

		getCoreDataImportService().execute(this, context, importData);
		getEventService().publishEvent(new CoreDataImportedEvent(context, importData));

		getSampleDataImportService().execute(this, context, importData);
		getEventService().publishEvent(new SampleDataImportedEvent(context, importData));

		executeImpexAddons(TELCEL_CONTENT_CATALOG, TELCEL_PRODUCT_CATALOG);

		synchronizedCatalogs(context, TELCEL_CONTENT_CATALOG);
		synchronizedCatalogs(context, TELCEL_PRODUCT_CATALOG);

	}

	private void synchronizedCatalogs(SystemSetupContext context, String contentCatalog) {
		final PerformResult syncCronJobResult = executeCatalogSyncJob(context, contentCatalog);
		if (isSyncRerunNeeded(syncCronJobResult)) {
			logInfo(context, "Catalog catalog [" + contentCatalog + "] sync has issues.");
			LOG.info("Catalog catalog [" + contentCatalog + "] sync has issues.");
		}
	}

	private void executeImpexAddons(String contentCatalog, String productCatalog) {
//		final String pathContentCatalog = "/" + TELCEL_INITIAL_DATA + "/import/sampledata/contentCatalogs/" + contentCatalog + "/";
//		setupImpexService.importImpexFile(pathContentCatalog + "cms-responsive-content.impex", false, true);

	}

	private ImportData returnImportData(final String storeName) {
		final ImportData importData = new ImportData();
		importData.setProductCatalogName(storeName);
		importData.setContentCatalogNames(Arrays.asList(storeName));
		importData.setStoreNames(Arrays.asList(storeName));
		return importData;
	}

	public CoreDataImportService getCoreDataImportService()
	{
		return coreDataImportService;
	}

	@Required
	public void setCoreDataImportService(final CoreDataImportService coreDataImportService)
	{
		this.coreDataImportService = coreDataImportService;
	}

	public SampleDataImportService getSampleDataImportService()
	{
		return sampleDataImportService;
	}

	@Required
	public void setSampleDataImportService(final SampleDataImportService sampleDataImportService)
	{
		this.sampleDataImportService = sampleDataImportService;
	}
}
