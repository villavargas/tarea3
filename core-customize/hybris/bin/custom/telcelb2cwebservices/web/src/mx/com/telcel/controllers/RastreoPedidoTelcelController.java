package mx.com.telcel.controllers;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import mx.com.telcel.facades.exception.BadRequestException;
import mx.com.telcel.facades.order.data.RastreoOrderData;
import mx.com.telcel.facades.order.data.RastreoOrderRequiereCorreoData;
import mx.com.telcel.facades.orders.TelcelOrdersFacade;
import mx.com.telcel.services.CustomValidationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping(value = "/rastreo-pedido-telcel")
@CacheControl(directive = CacheControlDirective.NO_STORE)
@Api(tags = "Rastreo Pedido Telcel")
public class RastreoPedidoTelcelController {

    public static final String
            FIELDS = "orderNumber,products(fechaPedido,entregaEstimada," +
            "status.ultimoStatus,producto(marca,modelo,color,nombreComercial,capacidad,images(FULL),sku) )";

    private static final String ORDEN = "orden";
    private static final String CORREO = "correo";

    @Resource(name = "defaultTelcelOrdersFacade")
    private TelcelOrdersFacade telcelOrdersFacade;

    @Resource(name = "dataMapper")
    private DataMapper dataMapper;

    @Resource(name = "customValidationService")
    private CustomValidationService validationService;


    @PostMapping("")
    @ResponseBody
    @ApiOperation(nickname = "getRastreoPedido",
            value = "Obtener rastreo de pedido.",
            notes = "Obtener rastreo de pedido request.",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<RastreoOrderData> getRastreoPedido(@RequestBody final Map<String, Object> request)
    {
        if(Objects.isNull(request.get(ORDEN)))
        {
            throw new BadRequestException("el parametro orden es obligatorio");
        }
        String orden = request.get(ORDEN).toString(), correo = null;
        boolean isValidMail = false;
        if (Objects.nonNull(request.get(CORREO)))
        {
            correo = request.get(CORREO).toString();
            isValidMail = validationService.validateIsAnEmail(correo);
        }

        return telcelOrdersFacade.rastreoPedido(orden, correo, isValidMail);
    }

    @GetMapping(path = "/requiere-correo" , produces = MediaType.APPLICATION_JSON_VALUE )
    @ResponseBody
    @ApiOperation(nickname = "getRastreoPedido",
            value = "Obtener rastreo de pedido.",
            notes = "Obtener rastreo de pedido request.",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public RastreoOrderRequiereCorreoData validaSiRequiereCorreoParaRastreo(@RequestParam String orden) {
        return telcelOrdersFacade.requeireCorreoRastreo(orden);
    }


    protected DataMapper getDataMapper()
    {
        return dataMapper;
    }

    protected void setDataMapper(final DataMapper dataMapper)
    {
        this.dataMapper = dataMapper;
    }


}
