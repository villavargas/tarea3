package mx.com.telcel.facades.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.springframework.util.Assert;

import mx.com.telcel.core.model.UserInfoDATModel;
import mx.com.telcel.facades.cvtdat.data.UserInfoDATData;


public class UserInfoDATPopulator implements Populator<UserInfoDATModel, UserInfoDATData>
{

	@Override
	public void populate(final UserInfoDATModel source, final UserInfoDATData target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setUsuario(source.getUsuario());
		target.setToken(source.getToken());
		target.setRegion(source.getRegion());
		target.setFuerzaVentaActiva(source.getFuerzaVentaActiva());
		target.setFuerzaVentaReporte(source.getFuerzaVentaReporte());
		target.setFuerzaVentaPadre(source.getFuerzaVentaPadres());
		target.setIdFuerzaVentaActiva(source.getIdFuerzaVentaActiva());
	}

}
