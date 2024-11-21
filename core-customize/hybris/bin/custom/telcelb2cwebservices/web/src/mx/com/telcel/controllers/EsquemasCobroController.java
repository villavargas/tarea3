package mx.com.telcel.controllers;

import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import mx.com.telcel.facades.esquemacobro.TelcelEsquemaCobroFacade;
import mx.com.telcel.facades.esquemacobro.data.TelcelEsquemaCobroDTO;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping(value = "/esquemas-cobro")
@CacheControl(directive = CacheControlDirective.NO_STORE)
@Api(tags = "Esquemas De Cobro Telcel")
public class EsquemasCobroController {

    protected static final String DEFAULT_FIELD_SET = FieldSetLevelHelper.DEFAULT_LEVEL;

    @Resource(name = "defaultTelcelEsquemaCobroFacade")
    private TelcelEsquemaCobroFacade telcelEsquemaCobroFacade;

    @Resource(name = "dataMapper")
    private DataMapper dataMapper;

    @GetMapping( produces = MediaType.APPLICATION_JSON_VALUE )
    @ResponseBody
    @ApiOperation(nickname = "getAllEsquemasDeCobro", value = "Obtener los Esquemas de Cobro.", notes = "Esquemas De Cobro Telcel request.",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TelcelEsquemaCobroDTO> getAllEsquemasDeCobro() {
        List<TelcelEsquemaCobroDTO> esquemaCobroDTOList = telcelEsquemaCobroFacade.getAll();

        return dataMapper.mapAsList(esquemaCobroDTOList, TelcelEsquemaCobroDTO.class, DEFAULT_FIELD_SET);
    }


}
