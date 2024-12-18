/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorocc.payment.dao.impl;


import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.acceleratorfacades.payment.data.PaymentSubscriptionResultData;
import de.hybris.platform.acceleratorocc.payment.dao.PaymentSubscriptionResultDao;
import de.hybris.platform.commercewebservicescommons.model.payment.PaymentSubscriptionResultModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultPaymentSubscriptionResultDaoTest extends ServicelayerTransactionalTest
{
	private static String CART_CODE_1 = "1";
	private static String CART_CODE_2 = "2";
	private static String CART_GUID_1 = "1111-11111-111-111";
	private static String CART_GUID_2 = "2222-22222-222-222";
	private static String SUCCESSFUL_RESULT_CODE = "succesfulResultCode";
	private static String FAILURE_RESULT_CODE = "failureResultCode";
	private static String APPROVED = "approved";
	private static String REJECT = "reject";
	private final PaymentSubscriptionResultData successfulPaymentSubscriptionResultData = new PaymentSubscriptionResultData();
	private final PaymentSubscriptionResultData failurePaymentSubscriptionResultData = new PaymentSubscriptionResultData();

	@Resource(name = "defaultPaymentSubscriptionResultDao")
	private PaymentSubscriptionResultDao paymentSubscriptionResultDao;

	@Resource(name = "modelService")
	private ModelService modelService;


	@Before
	public void setUp()
	{
		successfulPaymentSubscriptionResultData.setSuccess(true);
		successfulPaymentSubscriptionResultData.setResultCode(SUCCESSFUL_RESULT_CODE);
		successfulPaymentSubscriptionResultData.setDecision(APPROVED);

		failurePaymentSubscriptionResultData.setSuccess(false);
		failurePaymentSubscriptionResultData.setResultCode(FAILURE_RESULT_CODE);
		failurePaymentSubscriptionResultData.setDecision(REJECT);

		createPaymentSubscriptionResult(CART_CODE_1, successfulPaymentSubscriptionResultData);
		createPaymentSubscriptionResult(CART_GUID_2, failurePaymentSubscriptionResultData);
	}

	private void createPaymentSubscriptionResult(final String cartId,
			final PaymentSubscriptionResultData paymentSubscriptionResultData)
	{
		final PaymentSubscriptionResultModel paymentSubscriptionResult = modelService.create(PaymentSubscriptionResultModel.class);
		paymentSubscriptionResult.setCartId(cartId);
		paymentSubscriptionResult.setResult(paymentSubscriptionResultData);
		modelService.save(paymentSubscriptionResult);
	}


	@Test
	public void testFindPaymentSubscriptionResultByCartCode()
	{
		final PaymentSubscriptionResultModel paymentSubscriptionResult = paymentSubscriptionResultDao
				.findPaymentSubscriptionResultByCart(CART_CODE_1);
		Assert.assertEquals(CART_CODE_1, paymentSubscriptionResult.getCartId());
		final PaymentSubscriptionResultData paymentSubscriptionResultData = (PaymentSubscriptionResultData) paymentSubscriptionResult
				.getResult();
		Assert.assertEquals(Boolean.valueOf(successfulPaymentSubscriptionResultData.isSuccess()),
				Boolean.valueOf(paymentSubscriptionResultData.isSuccess()));
		Assert.assertEquals(successfulPaymentSubscriptionResultData.getResultCode(), paymentSubscriptionResultData.getResultCode());
		Assert.assertEquals(successfulPaymentSubscriptionResultData.getDecision(), paymentSubscriptionResultData.getDecision());
	}

	@Test
	public void testFindPaymentSubscriptionResultByCartGuid()
	{
		final PaymentSubscriptionResultModel paymentSubscriptionResult = paymentSubscriptionResultDao
				.findPaymentSubscriptionResultByCart(CART_GUID_2);
		Assert.assertEquals(CART_GUID_2, paymentSubscriptionResult.getCartId());
		final PaymentSubscriptionResultData paymentSubscriptionResultData = (PaymentSubscriptionResultData) paymentSubscriptionResult
				.getResult();
		Assert.assertEquals(Boolean.valueOf(failurePaymentSubscriptionResultData.isSuccess()),
				Boolean.valueOf(paymentSubscriptionResultData.isSuccess()));
		Assert.assertEquals(failurePaymentSubscriptionResultData.getResultCode(), paymentSubscriptionResultData.getResultCode());
		Assert.assertEquals(failurePaymentSubscriptionResultData.getDecision(), paymentSubscriptionResultData.getDecision());
	}

	@Test(expected = ModelNotFoundException.class)
	public void testFindNotExistingPaymentSubscriptionResult()
	{
		paymentSubscriptionResultDao.findPaymentSubscriptionResultByCart(CART_CODE_2);
	}

	public void testFindPaymentSubscriptionResultByCartCodeAndGuid()
	{
		List<PaymentSubscriptionResultModel> paymentSubscriptionResultList = paymentSubscriptionResultDao
				.findPaymentSubscriptionResultByCart(CART_CODE_1, CART_GUID_1);

		Assert.assertEquals(1, paymentSubscriptionResultList.size());
		final PaymentSubscriptionResultData paymentSubscriptionResultData = (PaymentSubscriptionResultData) paymentSubscriptionResultList
				.get(0).getResult();
		Assert.assertEquals(Boolean.valueOf(successfulPaymentSubscriptionResultData.isSuccess()),
				Boolean.valueOf(paymentSubscriptionResultData.isSuccess()));
		Assert.assertEquals(successfulPaymentSubscriptionResultData.getResultCode(), paymentSubscriptionResultData.getResultCode());
		Assert.assertEquals(successfulPaymentSubscriptionResultData.getDecision(), paymentSubscriptionResultData.getDecision());

		paymentSubscriptionResultList = paymentSubscriptionResultDao.findPaymentSubscriptionResultByCart(CART_CODE_1, CART_GUID_2);

		Assert.assertEquals(2, paymentSubscriptionResultList.size());
	}

	@Test
	public void testFindNotExistingPaymentSubscriptionResultByCartCodeAndGuid()
	{
		final List<PaymentSubscriptionResultModel> paymentSubscriptionResultList = paymentSubscriptionResultDao
				.findPaymentSubscriptionResultByCart(CART_CODE_2, CART_GUID_1);
		Assert.assertTrue(paymentSubscriptionResultList.isEmpty());
	}

	@Test
	public void testFindOldPaymentSubscriptionResult()
	{
		final List<PaymentSubscriptionResultModel> paymentSubscriptionResultList = paymentSubscriptionResultDao
				.findOldPaymentSubscriptionResult(new Date());
		Assert.assertEquals(2, paymentSubscriptionResultList.size());
	}
}
