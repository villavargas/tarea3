/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.fraud.symptom.impl;

import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.fraud.constants.FrauddetectionConstants;
import de.hybris.platform.fraud.impl.FraudServiceResponse;
import de.hybris.platform.fraud.impl.FraudSymptom;
import de.hybris.platform.fraud.symptom.impl.BlackListSymptom;

import org.apache.commons.lang.StringUtils;


public class AcceleratorBlackListSymptom extends BlackListSymptom
{
	@Override
	public FraudServiceResponse recognizeSymptom(final FraudServiceResponse fraudResponse, final AbstractOrderModel order)
	{
		if (getBannedEmails().contains(getCustomerUid(order.getUser().getUid(), order)))
		{
			fraudResponse.addSymptom(new FraudSymptom(getSymptomName() + ":" + FrauddetectionConstants.USERID, getIncrement()));
		}
		else
		{
			fraudResponse.addSymptom(new FraudSymptom(getSymptomName(), 0));
		}

		return fraudResponse;
	}

	protected String getCustomerUid(final String uid, final AbstractOrderModel order)
	{
		return (order.getUser() instanceof CustomerModel && CustomerType.GUEST.equals(((CustomerModel) order.getUser()).getType())) ? StringUtils
				.substringAfter(uid, "|") : uid;
	}
}
