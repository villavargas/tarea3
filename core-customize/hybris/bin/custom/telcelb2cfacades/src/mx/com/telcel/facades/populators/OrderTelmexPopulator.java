package mx.com.telcel.facades.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.Assert;

import mx.com.telcel.core.model.OrderTelmexModel;
import mx.com.telcel.core.model.PosicionOrderTelmexModel;
import mx.com.telcel.facades.ordertelmex.data.MaterialOrderTelmexData;
import mx.com.telcel.facades.ordertelmex.data.OrderTelmexData;
import mx.com.telcel.facades.ordertelmex.data.PosicionOrderTelmexData;


public class OrderTelmexPopulator implements Populator<OrderTelmexModel, OrderTelmexData>
{

	@Override
	public void populate(final OrderTelmexModel source, final OrderTelmexData target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");
		target.setPedido(source.getPedido());
		target.setEntrega(source.getEntrega());
		target.setFactura(source.getFactura());
		target.setRegion(source.getRegion());
		target.setAlmacen(source.getAlmacen());
		final List<PosicionOrderTelmexModel> posiciones = source.getPosiciones();
		final List<PosicionOrderTelmexData> posicionesData = new ArrayList<>();
		for (final PosicionOrderTelmexModel pm : posiciones)
		{
			final PosicionOrderTelmexData pd = new PosicionOrderTelmexData();
			final MaterialOrderTelmexData material = new MaterialOrderTelmexData();
			material.setCveMaterial(pm.getMaterial().getCveMaterial());
			material.setClaveSAT(pm.getMaterial().getClaveSAT());
			material.setDescripcionSAT(pm.getMaterial().getDescripcionSAT());
			material.setPzas(pm.getMaterial().getPzas());
			if (!pm.getSeriesIMEI().isEmpty())
			{
				pd.setImei(pm.getSeriesIMEI().get(0).getImei());
			}
			if (!pm.getSeriesICCID().isEmpty())
			{
				pd.setIccid(pm.getSeriesICCID().get(0).getIccid());
			}
			if (!pm.getPrecioTelmex().isEmpty())
			{
				pd.setPrecio(pm.getPrecioTelmex().get(0).getPrecio());
			}
			pd.setMaterial(material);
			posicionesData.add(pd);
		}
		target.setPosiciones(posicionesData);
	}

}
