/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.constants;

/**
 * Global class for all AcceleratorServices constants. You can add global constants for your extension into this class.
 */
@SuppressWarnings("deprecation")
public final class AcceleratorServicesConstants extends GeneratedAcceleratorServicesConstants
{
	public static final String EXTENSIONNAME = "acceleratorservices";

	// implement here constants used by this extension
	public static final String URL_ENCODING_ATTRIBUTES = "encodingAttributes";

	public static final String LANGUAGE_ENCODING = "language";

	public static final String CURRENCY_ENCODING = "currency";

	public static final String SAVECART_RESTORATION_SAVETIMEHOOK_ENABLED = "acceleratorservices.commercesavecart.restoration.savetime.hook.enabled";
	public static final String SAVECART_SESSIONCARTHOOK_ENABLED = "acceleratorservices.commercesavecart.sessioncart.hook.enabled";

	private AcceleratorServicesConstants()
	{
		//empty to avoid instantiating this constant class
	}
}
