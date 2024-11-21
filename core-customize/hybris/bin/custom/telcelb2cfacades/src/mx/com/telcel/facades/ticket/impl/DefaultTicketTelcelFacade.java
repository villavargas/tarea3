package mx.com.telcel.facades.ticket.impl;

import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.keygenerator.impl.PersistentKeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import mx.com.telcel.core.daos.ticket.AyudaTicketDao;
import mx.com.telcel.core.daos.ticket.CorreosContactoRegionDao;
import mx.com.telcel.core.helper.TelcelEmailsHelper;
import mx.com.telcel.core.model.AyudaTicketModel;
import mx.com.telcel.core.model.CorreosContactoRegionModel;
import mx.com.telcel.core.model.HolderLineModel;
import mx.com.telcel.core.model.TicketTelcelModel;
import mx.com.telcel.facades.exception.BadRequestException;
import mx.com.telcel.facades.ticket.TicketTelcelFacade;
import mx.com.telcel.facades.ticket.data.AyudaTicket;
import mx.com.telcel.facades.ticket.data.TicketTelcel;


public class DefaultTicketTelcelFacade implements TicketTelcelFacade
{

	private static final String R = "R";
	private static final String FROM_EMAIL = "mail.from";
	private Converter<AyudaTicketModel, AyudaTicket> ayudaTicketConverter;
	private PersistentKeyGenerator ticketTelcelCodeGenerator;
	private static final String TELCEL = "telcel";
	private static final String SPACE = " ";

	@Resource(name = "ayudaTicketDao")
	private AyudaTicketDao ayudaTicketDao;

	@Resource(name = "defaultCorreoContactoDao")
	private CorreosContactoRegionDao correosContactoRegionDao;

	@Resource(name = "customerAccountService")
	private CustomerAccountService customerAccountService;

	@Resource(name = "baseStoreService")
	private BaseStoreService baseStoreService;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "telcelEmailsHelper")
	private TelcelEmailsHelper telcelEmailsHelper;

	@Resource(name = "defaultConfigurationService")
	private ConfigurationService configurationService;

	private CustomerNameStrategy customerNameStrategy;
	private static final Logger LOG = Logger.getLogger(DefaultTicketTelcelFacade.class);

	@Override
	public List<AyudaTicket> motivosTickets()
	{
		final List<AyudaTicketModel> motivosModel = ayudaTicketDao.motivosTicket();
		if (!motivosModel.isEmpty())
		{
			return ayudaTicketConverter.convertAll(motivosModel);
		}
		return null;
	}

	@Override
	public void notificacionTicket(final TicketTelcel ticketTelcel)
	{
		if (ticketTelcel.getNumeroOrden() == null || ticketTelcel.getNumeroOrden().equals(""))
		{
			throw new BadRequestException("El numero de orden es requerido");
		}

		final BaseStoreModel baseStore = baseStoreService.getBaseStoreForUid(TELCEL);

		try
		{
			final String fromEmail = configurationService.getConfiguration().getString(FROM_EMAIL);
			final OrderModel orderModel = customerAccountService.getOrderForCode(ticketTelcel.getNumeroOrden(), baseStore);
			if (orderModel == null)
			{
				throw new BadRequestException("El número que ingresaste es incorrecto, por favor verifícalo e intenta de nuevo.");
			}

			final List<CorreosContactoRegionModel> correRegion = correosContactoRegionDao
					.getByCodeRegion(R + orderModel.getRegionCode());
			if (!correRegion.isEmpty())
			{
				LOG.info("CORREO REGION : " + correRegion.get(0).getCorreo());
				final HolderLineModel addressHolderLine = orderModel.getAddressHolderLine();

				final String name = addressHolderLine.getName();
				final String lastName = addressHolderLine.getLastName() != null ? addressHolderLine.getLastName() : "";

				telcelEmailsHelper.sendTicketNotificationCustomerEmail(String.join(" ", name, lastName), addressHolderLine.getEmail(),
						fromEmail);

				ticketTelcel.setNombreCliente(name);
				String apellidoPaterno = "";
				String apellidoMaterno = "";
				if (lastName.contains(SPACE))
				{
					final String[] apellidos = lastName.split(SPACE);
					apellidoPaterno = apellidos[0];
					apellidoMaterno = apellidos[1];
				}
				ticketTelcel.setApellidoPaternoCliente(apellidoPaterno);
				ticketTelcel.setApellidoMaternoCliente(apellidoMaterno);
				if (ticketTelcel.getSituacion() == null)
				{
					ticketTelcel.setSituacion(" ");
				}

				if (orderModel.getDeliveryAddress() != null && orderModel.getDeliveryAddress().getRegion() != null)
				{
					final String estado = orderModel.getDeliveryAddress().getRegion().getName();
					ticketTelcel.setEstado(estado);
				}

				telcelEmailsHelper.sendTicketNotificationAgentEmail(ticketTelcel, correRegion.get(0).getCorreo(), fromEmail,
						addressHolderLine.getEmail());
			}
			else
			{
				throw new BadRequestException(
						"Lo sentimos, por el momento el servicio no está disponible. Por favor inténtalo mas tarde.");
			}

			final String code = getTicketTelcelCodeGenerator().generate().toString();

			final TicketTelcelModel ticketTelcelModel = new TicketTelcelModel();
			ticketTelcelModel.setNumeroOrden(ticketTelcel.getNumeroOrden());
			ticketTelcelModel.setCode(code);
			ticketTelcelModel.setMotivo(ticketTelcel.getMotivo());
			ticketTelcelModel.setContacto(ticketTelcel.getContacto());
			ticketTelcelModel.setSituacion(ticketTelcel.getSituacion());

			modelService.save(ticketTelcelModel);
		}
		catch (final ModelNotFoundException exception)
		{
			throw new BadRequestException("El número que ingresaste es incorrecto, por favor verifícalo e intenta de nuevo.");
		}
	}

	private void testServerCorreos()
	{
		LOG.info("Facade--Prueba Correo tiendaenlineaR1");
		telcelEmailsHelper.sendTicketNotificationCustomerEmail("Jorge Toral (QA-R1)", "jorge.toral@neoris.com",
				"tiendaenlineaR1@telcel.com");//correRegion.get(0).getCorreo() );

		LOG.info("Facade--Prueba Correo tiendaenlinea");
		telcelEmailsHelper.sendTicketNotificationCustomerEmail("Jorge Toral (QA)", "jorge.toral@neoris.com",
				"tiendaenlinea@telcel.com");//correRegion.get(0).getCorreo() );
	}

	public Converter<AyudaTicketModel, AyudaTicket> getAyudaTicketConverter()
	{
		return ayudaTicketConverter;
	}

	@Required
	public void setAyudaTicketConverter(final Converter<AyudaTicketModel, AyudaTicket> ayudaTicketConverter)
	{
		this.ayudaTicketConverter = ayudaTicketConverter;
	}

	protected CustomerNameStrategy getCustomerNameStrategy()
	{
		return customerNameStrategy;
	}

	@Required
	public void setCustomerNameStrategy(final CustomerNameStrategy customerNameStrategy)
	{
		this.customerNameStrategy = customerNameStrategy;
	}

	public PersistentKeyGenerator getTicketTelcelCodeGenerator()
	{
		return ticketTelcelCodeGenerator;
	}

	@Required
	public void setTicketTelcelCodeGenerator(final PersistentKeyGenerator ticketTelcelCodeGenerator)
	{
		this.ticketTelcelCodeGenerator = ticketTelcelCodeGenerator;
	}


}
