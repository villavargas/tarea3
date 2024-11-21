package mx.com.telcel.facades.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Objects;

import org.springframework.util.Assert;

import mx.com.telcel.facades.order.data.OrderHeaderData;


public class OrderHeaderPopulator implements Populator<OrderModel, OrderHeaderData>
{

	@Override
	public void populate(final OrderModel source, final OrderHeaderData target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");
		target.setOrderNumber(source.getCode());
		final DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		target.setRequestDate(df.format(source.getDate()));
		if (Objects.nonNull(source.getStatus()))
		{
			target.setStatus(source.getStatus().getCode());
		}
		target.setTotalPrice(String.valueOf(source.getTotalPrice()));
	}

}
