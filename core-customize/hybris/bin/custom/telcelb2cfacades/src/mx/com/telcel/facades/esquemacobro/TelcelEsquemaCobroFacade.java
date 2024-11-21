package mx.com.telcel.facades.esquemacobro;

import mx.com.telcel.facades.esquemacobro.data.TelcelEsquemaCobroDTO;

import java.util.List;

public interface TelcelEsquemaCobroFacade {
    List<TelcelEsquemaCobroDTO> getAll();

    TelcelEsquemaCobroDTO getByCode(String code);
}
