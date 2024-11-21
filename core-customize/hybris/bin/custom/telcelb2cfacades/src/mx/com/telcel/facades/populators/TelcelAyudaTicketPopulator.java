package mx.com.telcel.facades.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import mx.com.telcel.core.model.AyudaTicketModel;
import mx.com.telcel.core.model.MotivoAyudaTicketModel;
import mx.com.telcel.facades.ticket.data.AyudaTicket;
import mx.com.telcel.facades.ticket.data.MotivoAyudaTicket;

import java.util.ArrayList;
import java.util.List;

public class TelcelAyudaTicketPopulator implements Populator<AyudaTicketModel, AyudaTicket> {
    @Override
    public void populate(AyudaTicketModel source, AyudaTicket target) throws ConversionException {
        target.setCode( source.getCode() );
        target.setDescripcion( source.getDescripcion() );

        if (!source.getMotivos().isEmpty()) {
            List<MotivoAyudaTicket> motivos = new ArrayList<>();
            for (MotivoAyudaTicketModel motivoModel : source.getMotivos()) {
                MotivoAyudaTicket motivo = new MotivoAyudaTicket();
                motivo.setCode(motivoModel.getCode());
                motivo.setDescripcion( motivoModel.getDescripcion() );
                motivo.setRequiereSituacion( motivoModel.getRequiereSituacion() );
                motivo.setNota( motivoModel.getNota() );
                motivos.add( motivo );
            }
            target.setMotivos(motivos);
        }
    }
}
