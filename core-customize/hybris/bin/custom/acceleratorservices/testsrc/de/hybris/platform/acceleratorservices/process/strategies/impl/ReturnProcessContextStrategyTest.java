/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.process.strategies.impl;

import static org.junit.Assert.assertSame;
import static org.mockito.BDDMockito.given;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;

/**
 * Test class for ReturnProcessContextStrategy
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ReturnProcessContextStrategyTest
{
	@Mock
	private OrderModel orderModel;

	@Mock
	private ReturnProcessModel businessProcessModel;

	@Mock
	private ReturnRequestModel returnRequestModel;

	@InjectMocks
	private ReturnProcessContextStrategy strategy = new ReturnProcessContextStrategy();

	@Test
	public void testGetOrderModel() throws Exception
	{
		given(businessProcessModel.getReturnRequest()).willReturn(returnRequestModel);
		given(returnRequestModel.getOrder()).willReturn(orderModel);

		final Optional<AbstractOrderModel> orderModelOptional = strategy.getOrderModel(businessProcessModel);

		assertSame(orderModel, orderModelOptional.get());
	}
}
