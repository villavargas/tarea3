package mx.com.telcel.facades.ticket;

import mx.com.telcel.facades.ticket.data.AyudaTicket;
import mx.com.telcel.facades.ticket.data.TicketTelcel;

import java.util.List;

public interface TicketTelcelFacade {

    List<AyudaTicket> motivosTickets();

    void notificacionTicket(TicketTelcel ticketTelcel);
}
