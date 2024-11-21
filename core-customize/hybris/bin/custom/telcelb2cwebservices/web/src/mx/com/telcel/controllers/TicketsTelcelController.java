package mx.com.telcel.controllers;

import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import mx.com.telcel.facades.ticket.TicketTelcelFacade;
import mx.com.telcel.facades.ticket.data.AyudaTicket;
import mx.com.telcel.facades.ticket.data.TicketTelcel;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping(value = "/ticket-telcel")
@CacheControl(directive = CacheControlDirective.NO_STORE)
@Api(tags = "Tickets Telcel")
public class TicketsTelcelController {

    @Resource(name = "defaultTicketTelcelFacade")
    private TicketTelcelFacade ticketTelcelFacade;


    @GetMapping(path = "/motivos" , produces = MediaType.APPLICATION_JSON_VALUE )
    @ResponseBody
    @ApiOperation(nickname = "getMotivosTicket",
            value = "Obtener motivos para creacion de ticket.",
            notes = "Obtener motivos para creacion de ticket request.",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AyudaTicket> getMotivosTicket() {
        return ticketTelcelFacade.motivosTickets();
    }

    @PostMapping (  produces = MediaType.APPLICATION_JSON_VALUE )
    @ResponseBody
    @ApiOperation(nickname = "creacionTicket",
            value = "Crear ticket usuario.",
            notes = "Crear ticket usuario request.",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public TicketTelcel creacionTicket(@RequestBody TicketTelcel ticketTelcel) {

        ticketTelcelFacade.notificacionTicket(ticketTelcel);

        return ticketTelcel;
    }


}
