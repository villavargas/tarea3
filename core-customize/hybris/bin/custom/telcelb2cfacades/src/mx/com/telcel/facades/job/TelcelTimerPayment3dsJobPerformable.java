/*
 *
 * [y] hybris Platform
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package mx.com.telcel.facades.job;

import de.hybris.platform.b2ctelcoservices.order.exception.OrderProcessingException;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import mx.com.telcel.core.daos.payment.TelcelPaymentDao;
import mx.com.telcel.core.model.TimerPaymentModel;
import mx.com.telcel.core.service.TelcelPaymentService;
import mx.com.telcel.facades.orders.CustomCheckoutFacade;
import org.apache.log4j.Logger;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * The type Telcel timer payment 3 ds job performable.
 */
public class TelcelTimerPayment3dsJobPerformable extends AbstractJobPerformable<CronJobModel> {

    private static final Logger LOG = Logger.getLogger(TelcelTimerPayment3dsJobPerformable.class);
    /**
     * The constant ONLINE.
     */
    public static final String ONLINE = "Online";
    /**
     * The constant TELCEL_PRODUCT_CATALOG.
     */
    public static final String TELCEL_PRODUCT_CATALOG = "telcelProductCatalog";
    /**
     * The constant TELCEL.
     */
    public static final String TELCEL = "telcel";
    /**
     * The constant TIME_TIMER.
     */
    public static final String TIME_TIMER = "telcel.job.timer3ds";

    @Resource
    private TelcelPaymentDao telcelPaymentDao;

    @Resource(name = "telcelPaymentService")
    private TelcelPaymentService telcelPaymentService;

    @Resource(name = "customCheckoutFacade")
    private CustomCheckoutFacade tmaCheckoutFacade;

    @Resource(name = "dataMapper")
    private DataMapper dataMapper;

    @Resource
    private CatalogVersionService catalogVersionService;

    @Resource
    private BaseSiteService baseSiteService;

    @Resource(name = "defaultConfigurationService")
    private ConfigurationService configurationService;


    @Override
    public PerformResult perform(final CronJobModel cronJob) {
        LOG.info("TimerPayment3dsJobPerformable.perform");
        List<TimerPaymentModel> timerPaymentModelList = telcelPaymentDao.getAllTimerPayment();
        if (timerPaymentModelList != null) {
            LOG.info("TimerPayment3dsJobPerformable.perform timerPaymentModelList!=null timerPaymentModelList.size [" + timerPaymentModelList.size() + "]");
            verifyTimeInFoundTimers(timerPaymentModelList);
        } else {
            LOG.info("Sin datos en TimerPaymentModel");
            return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
        }
        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }

    private void verifyTimeInFoundTimers(List<TimerPaymentModel> timerPaymentModelList) {
        LOG.info("TimerPayment3dsJobPerformable.verifyTimeInFoundTimers");
        Date nowDate = new Date();
        for (TimerPaymentModel timerPaymentModel : timerPaymentModelList) {
            long diferencia = nowDate.getTime() - timerPaymentModel.getStartTimer().getTime();
            long minutos = TimeUnit.MILLISECONDS.toMinutes(diferencia);
            final int timer = configurationService.getConfiguration().getInt(TIME_TIMER);

            if (minutos > timer) {
                LOG.info("minutos [" + minutos + "] > timer [" + timer + "]");
                processCreateOrder(timerPaymentModel);
                telcelPaymentService.desactivateTimer(timerPaymentModel.getCartId());

            }
        }
    }

    private void processCreateOrder(TimerPaymentModel timerPaymentModel) {
        //Proceso para ejecutar la orden internamente
        LOG.info("PaymentController.getVerifyPayment timerFromStore && desactivateTimer");
        if (baseSiteService.getCurrentBaseSite() == null) {
            baseSiteService.setCurrentBaseSite(TELCEL, true);
        }

        // placeOrder tmaordersControllers
        catalogVersionService.setSessionCatalogVersion(TELCEL_PRODUCT_CATALOG, ONLINE);

        try {
            //
            final OrderData orderData = tmaCheckoutFacade.placeOrderFromCartCustomTimer(timerPaymentModel.getCartId(), true);
            LOG.info("Creacion de orden correcta code [" + orderData.getCode() + "]");
        } catch (OrderProcessingException e) {
            LOG.info("Ocurrio un error en la creacion de orden - cartId guid [" + timerPaymentModel.getCartId() + "]");
            LOG.error(e.getMessage());
        }
    }

    /**
     * Gets data mapper.
     *
     * @return the data mapper
     */
    public DataMapper getDataMapper() {
        return dataMapper;
    }

    /**
     * Sets data mapper.
     *
     * @param dataMapper the data mapper
     */
    public void setDataMapper(DataMapper dataMapper) {
        this.dataMapper = dataMapper;
    }
}