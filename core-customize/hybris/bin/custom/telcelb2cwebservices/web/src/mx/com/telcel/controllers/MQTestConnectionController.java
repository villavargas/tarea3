package mx.com.telcel.controllers;

import de.hybris.platform.servicelayer.config.ConfigurationService;

import javax.annotation.Resource;
import javax.jms.JMSException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.ibm.msg.client.wmq.common.CommonConstants;


@Controller
@RequestMapping(value = "/mqtestconnection")
public class MQTestConnectionController
{

	@Resource(name = "defaultConfigurationService")
	private ConfigurationService configurationService;

	@ResponseBody
	@GetMapping(value = "/telmex", produces = "application/json")
	public String telmex()
	{
		try
		{
			getMQConnectionFactory("telcel.mq.telmex.host", "telcel.mq.telmex.queue.channel", "telcel.mq.telmex.port",
					"telcel.mq.telmex.queue.manager").createQueueConnection();
			final MQConnectionTestModel mqctestmodel = new MQConnectionTestModel();
			mqctestmodel.setStatus("CONNECTED");
			final Gson gson = new Gson();
			return gson.toJson(mqctestmodel);
		}
		catch (final Exception error)
		{
			final MQConnectionTestModel mqctestmodelErr = new MQConnectionTestModel();
			mqctestmodelErr.setStatus("NOT CONNECTED");
			mqctestmodelErr.setError("" + error.getMessage());
			final Gson gson = new Gson();
			return gson.toJson(mqctestmodelErr);
		}
	}

	@ResponseBody
	@GetMapping(value = "/facturacion", produces = "application/json")
	public String facturacion()
	{
		try
		{
			getMQConnectionFactory("telcel.mq.facturacion.host", "telcel.mq.facturacion.queue.channel", "telcel.mq.facturacion.port",
					"telcel.mq.facturacion.queue.manager").createQueueConnection();
			final MQConnectionTestModel mqctestmodel = new MQConnectionTestModel();
			mqctestmodel.setStatus("CONNECTED");
			final Gson gson = new Gson();
			return gson.toJson(mqctestmodel);
		}
		catch (final Exception error)
		{
			final MQConnectionTestModel mqctestmodelErr = new MQConnectionTestModel();
			mqctestmodelErr.setStatus("NOT CONNECTED");
			mqctestmodelErr.setError("" + error.getMessage());
			final Gson gson = new Gson();
			return gson.toJson(mqctestmodelErr);
		}
	}

	@ResponseBody
	@GetMapping(value = "/activacion", produces = "application/json")
	public String activacion()
	{
		try
		{
			getMQConnectionFactory("telcel.mq.activacionprepago.host", "telcel.mq.activacionprepago.queue.channel",
					"telcel.mq.activacionprepago.port", "telcel.mq.activacionprepago.queue.manager").createQueueConnection();
			final MQConnectionTestModel mqctestmodel = new MQConnectionTestModel();
			mqctestmodel.setStatus("CONNECTED");
			final Gson gson = new Gson();
			return gson.toJson(mqctestmodel);
		}
		catch (final Exception error)
		{
			final MQConnectionTestModel mqctestmodelErr = new MQConnectionTestModel();
			mqctestmodelErr.setStatus("NOT CONNECTED");
			mqctestmodelErr.setError("" + error.getMessage());
			final Gson gson = new Gson();
			return gson.toJson(mqctestmodelErr);
		}
	}
	
	@ResponseBody
	@GetMapping(value = "/liberarecursos", produces = "application/json")
	public String liberarecursos()
	{
		try
		{
			getMQConnectionFactory("telcel.mq.liberarecursos.host", "telcel.mq.liberarecursos.queue.channel",
					"telcel.mq.liberarecursos.port", "telcel.mq.liberarecursos.queue.manager").createQueueConnection();
			final MQConnectionTestModel mqctestmodel = new MQConnectionTestModel();
			mqctestmodel.setStatus("CONNECTED");
			final Gson gson = new Gson();
			return gson.toJson(mqctestmodel);
		}
		catch (final Exception error)
		{
			final MQConnectionTestModel mqctestmodelErr = new MQConnectionTestModel();
			mqctestmodelErr.setStatus("NOT CONNECTED");
			mqctestmodelErr.setError("" + error.getMessage());
			final Gson gson = new Gson();
			return gson.toJson(mqctestmodelErr);
		}
	}

	private MQQueueConnectionFactory getMQConnectionFactory(final String MQ_HOST, final String MQ_QUEUE_CHANNEL,
			final String MQ_PORT, final String MQ_QUEUE_MANAGER) throws JMSException
	{
		final MQQueueConnectionFactory mqQueueConnectionFactory = new MQQueueConnectionFactory();
		mqQueueConnectionFactory.setHostName(configurationService.getConfiguration().getString(MQ_HOST));
		mqQueueConnectionFactory.setChannel(configurationService.getConfiguration().getString(MQ_QUEUE_CHANNEL));
		mqQueueConnectionFactory.setPort(configurationService.getConfiguration().getInt(MQ_PORT));
		mqQueueConnectionFactory.setQueueManager(configurationService.getConfiguration().getString(MQ_QUEUE_MANAGER));
		mqQueueConnectionFactory.setIntProperty(CommonConstants.WMQ_CONNECTION_MODE, CommonConstants.WMQ_CM_CLIENT);
		return mqQueueConnectionFactory;
	}

	class MQConnectionTestModel
	{

		private String status;
		private String error;

		public String getStatus()
		{
			return status;
		}

		public void setStatus(final String status)
		{
			this.status = status;
		}

		public String getError()
		{
			return error;
		}

		public void setError(final String error)
		{
			this.error = error;
		}

	}

}
