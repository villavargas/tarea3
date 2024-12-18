/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorocc.controllers;

import de.hybris.platform.acceleratorfacades.customerlocation.CustomerLocationFacade;
import de.hybris.platform.acceleratorservices.store.data.UserLocationData;
import de.hybris.platform.acceleratorocc.exceptions.NoLocationFoundException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commerceservices.store.data.GeoPoint;
import de.hybris.platform.storelocator.exception.GeoServiceWrapperException;
import de.hybris.platform.webservicescommons.util.YSanitizer;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping(value = "/{baseSiteId}/customers")
@ApiVersion("v1")
public class ExtendedCustomersController
{

	private final static Logger LOG = Logger.getLogger(ExtendedCustomersController.class);

	@Resource(name = "customerLocationFacade")
	private CustomerLocationFacade customerLocationFacade;

	@RequestMapping(value = "/current/locationLatLong", method = RequestMethod.PUT)
	@ResponseBody
	public UserLocationData setUserLocation(@RequestParam(required = true) final Double latitude,
			@RequestParam(required = true) final Double longitude) throws CommerceCartModificationException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("setUserLocation: latitude=" + latitude + " | longitude=" + longitude);
		}

		final UserLocationData userLocationData = new UserLocationData();
		userLocationData.setPoint(createGeoPoint(latitude, longitude));
		customerLocationFacade.setUserLocationData(userLocationData);
		return customerLocationFacade.getUserLocationData();
	}

	@RequestMapping(value = "/current/location", method = RequestMethod.PUT)
	@ResponseBody
	public UserLocationData setUserLocation(@RequestParam(required = true) final String location) throws NoLocationFoundException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("setUserLocation: term=" + YSanitizer.sanitize(location));
		}

		final UserLocationData userLocationData = new UserLocationData();
		userLocationData.setSearchTerm(location);

		try
		{
			customerLocationFacade.setUserLocationData(userLocationData);
			return customerLocationFacade.getUserLocationData();
		}
		catch (final GeoServiceWrapperException ex)
		{
			throw new NoLocationFoundException(location, ex);
		}
	}

	@RequestMapping(value = "/current/location", method = RequestMethod.GET)
	@ResponseBody
	public UserLocationData getUserLocation() throws CommerceCartModificationException
	{
		return customerLocationFacade.getUserLocationData();
	}

	private GeoPoint createGeoPoint(final Double latitude, final Double longitude)
	{
		final GeoPoint point = new GeoPoint();
		point.setLatitude(latitude.doubleValue());
		point.setLongitude(longitude.doubleValue());

		return point;
	}
}
