/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorocc.cronjob;

import de.hybris.platform.acceleratorocc.payment.dao.PaymentSubscriptionResultDao;
import de.hybris.platform.commercewebservicescommons.model.payment.OldPaymentSubscriptionResultRemovalCronJobModel;
import de.hybris.platform.commercewebservicescommons.model.payment.PaymentSubscriptionResultModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Required;


/**
 * A Cron Job to clean up old payment subscription result.
 */
public class OldPaymentSubscriptionResultRemovalJob
		extends AbstractJobPerformable<OldPaymentSubscriptionResultRemovalCronJobModel>
{
	private static final Logger LOG = Logger.getLogger(OldPaymentSubscriptionResultRemovalJob.class);

	private PaymentSubscriptionResultDao paymentSubscriptionResultDao;
	private TimeService timeService;

	private static final int DEFAULT_MAX_AGE = 86400;

	@Override
	public PerformResult perform(final OldPaymentSubscriptionResultRemovalCronJobModel job)
	{
		try
		{
			final int age = job.getAge() != null ? job.getAge().intValue() : DEFAULT_MAX_AGE;

			for (final PaymentSubscriptionResultModel oldObject : paymentSubscriptionResultDao
					.findOldPaymentSubscriptionResult(new DateTime(getTimeService().getCurrentTime()).minusSeconds(age).toDate()))
			{
				getModelService().remove(oldObject);
			}

			return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
		}
		catch (final Exception e)
		{
			LOG.error("Exception occurred during payment subscription result cleanup", e);
			return new PerformResult(CronJobResult.ERROR, CronJobStatus.ABORTED);
		}
	}

	public PaymentSubscriptionResultDao getPaymentSubscriptionResultDao()
	{
		return paymentSubscriptionResultDao;
	}

	@Required
	public void setPaymentSubscriptionResultDao(final PaymentSubscriptionResultDao paymentSubscriptionResultDao)
	{
		this.paymentSubscriptionResultDao = paymentSubscriptionResultDao;
	}

	protected TimeService getTimeService()
	{
		return timeService;
	}

	@Required
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}
}
