/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.constants;

import de.hybris.platform.util.Config;


/**
 * Global class for all telb2cocc constants. You can add global constants for your extension into this class.
 */
@SuppressWarnings(
{ "deprecation", "squid:CallToDeprecatedMethod" })
public final class Telb2coccConstants extends GeneratedTelb2coccConstants
{
	public static final String EXTENSIONNAME = "telb2cocc"; //NOSONAR

	public static final String OPTIONS_SEPARATOR = ",";

	public static final String API_BASE_URL = EXTENSIONNAME + ".api.base.url";

	public static final String TMA_API_WEBROOT = EXTENSIONNAME + ".api.webmodule.webroot";

	public static final String TMA_PRODUCT_OFFERING_API_VERSION = EXTENSIONNAME + ".tmaProductOfferingApi.version";

	public static final String PRODUCT_SPECIFICATION_API_HREF = EXTENSIONNAME + ".productSpecificationApi.href";

	public static final String PRODUCT_OFFERING_API_HREF = EXTENSIONNAME + ".productOfferingApi.href";

	public static final String PRODUCT_SPECIFICATION_API_URL = Config.getParameter(API_BASE_URL)
			+ Config.getParameter(TMA_API_WEBROOT) + Config.getParameter(TMA_PRODUCT_OFFERING_API_VERSION)
			+ Config.getParameter(PRODUCT_SPECIFICATION_API_HREF);

	public static final String PRODUCT_OFFERING_API_URL = Config.getParameter(API_BASE_URL) + Config.getParameter(TMA_API_WEBROOT)
			+ Config.getParameter(TMA_PRODUCT_OFFERING_API_VERSION) + Config.getParameter(PRODUCT_OFFERING_API_HREF);

	public static final String ELIGIBILTY_ROLES = "telb2cocc.eligibility.admin.roles";

	public static final String ADMIN_ROLES = "telb2cocc.admin.roles";

	private Telb2coccConstants()
	{
		//empty to avoid instantiating this constant class
	}

}
