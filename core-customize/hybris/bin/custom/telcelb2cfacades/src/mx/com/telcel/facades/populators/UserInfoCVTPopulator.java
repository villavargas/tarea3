package mx.com.telcel.facades.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.springframework.util.Assert;

import mx.com.telcel.core.model.UserInfoCVTModel;
import mx.com.telcel.facades.cvtdat.data.UserInfoCVTData;


public class UserInfoCVTPopulator implements Populator<UserInfoCVTModel, UserInfoCVTData>
{

	@Override
	public void populate(final UserInfoCVTModel source, final UserInfoCVTData target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setNumEmpleado(source.getNumEmpleado());
		target.setUsrUniversal(source.getUsrUniversal());
		target.setNombre(source.getNombre());
		target.setApPaterno(source.getApPaterno());
		target.setApMaterno(source.getApMaterno());
		target.setRegion(source.getRegion());
		target.setIdPerfil(source.getIdPerfil());
		target.setNombrePerfil(source.getNombrePerfil());
		target.setFvPrepagoPadre(source.getFvPrepagoPadre());
		target.setFvPospagoPadre(source.getFvPospagoPadre());
		target.setFvPrepagoPersonal(source.getFvPrepagoPersonal());
		target.setFvPospagoPersonal(source.getFvPospagoPersonal());
		target.setFvPrepagoReporte(source.getFvPrepagoReporte());
		target.setFvPospagoReporte(source.getFvPospagoReporte());
		target.setEscenario(source.getEscenario());
		target.setDireccion(source.getDireccion());
		target.setSubDireccion(source.getSubDireccion());
		target.setGerencia(source.getGerencia());
		target.setDepartamento(source.getDepartamento());
		target.setDescDepartamento(source.getDescDepartamento());
		target.setPuesto(source.getPuesto());
		target.setCorreo(source.getCorreo());
	}

}
