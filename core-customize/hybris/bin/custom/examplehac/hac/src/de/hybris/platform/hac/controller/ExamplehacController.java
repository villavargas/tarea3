/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.hac.controller;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.examplehac.data.SampleMonitorData;
import de.hybris.platform.examplehac.data.SampleMonitorResultData;
import org.apache.log4j.Logger;

import java.text.MessageFormat;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.ImmutableList;

import java.text.SimpleDateFormat;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;

import javax.annotation.Resource;


/**
 *
 */
@Controller
@RequestMapping("/examplehac/**")
public class ExamplehacController
{
	private static final Logger LOG = Logger.getLogger(ExamplehacController.class);

	public static final String CHECK_PENDING_ORDERS = "Pending Orders";
	public static final String CHECK_FAILING_ORDERS = "Failing Orders";
	
	private static final String HYBRIS_LOG_DIR_CONFIG = "HYBRIS_LOG_DIR";
	
	private static final SimpleDateFormat dateFormat1 = new SimpleDateFormat("YYYYMMdd");
	private static final SimpleDateFormat  dateFormat2 = new SimpleDateFormat("yyyyMMdd-HHmmss.SSS");
	
	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	@RequestMapping(value = "/extension", method = RequestMethod.GET)
	public @ResponseBody
	String sayHello()
	{
		return "Simple demonstration of extending hac by adding new controller and routing.";
	}
	
	@RequestMapping(value = "/renameTomcatLog", method = RequestMethod.GET)
	public String renameTomcatLogPage(final Model model)
	{
		LOG.info("Entro renameTomcatLogPage");
		return "renameTomcatLog";
	}

	@RequestMapping(value = "/renameTomcatLog/rename", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> renameTomcatLogExecute()
	{
		// Code to rename
		LOG.info("Entro renameTomcatLogExecute");
		final String logDir = configurationService.getConfiguration().getString(HYBRIS_LOG_DIR_CONFIG);
		LOG.info("logDir -> " + logDir);
		
		final String currentLog = logDir + "/tomcat/console-" + dateFormat1.format(new Date()) + ".log";
		final String moveLog = logDir + "/tomcat/SEVERE-" + dateFormat2.format(new Date()) + ".log";
		final File logFile = new File(currentLog);
		final File moveFile = new File(moveLog);
		
		LOG.info("logFile.exists() -> " + logFile.exists());
		LOG.info("moveFile.exists() -> " + moveFile.exists());
		
		final boolean successMove = logFile.renameTo(moveFile);
		LOG.info("successMove -> " + successMove);
		
		return Collections.singletonMap("success", successMove);
	}

	@RequestMapping(value = "/statistics", method = RequestMethod.GET)
	public String showCustomStatistics(final Model model)
	{
		final SampleMonitorData monitor = new SampleMonitorData();
		monitor.setResults(ImmutableList.of(createFakePendingOrders(), createFakeFailedOrders()));
		model.addAttribute("monitor", monitor);

		return "customStatistics";
	}

	public SampleMonitorResultData createFakePendingOrders()
	{
		final SampleMonitorResultData monitorResult = new SampleMonitorResultData();
		monitorResult.setName(CHECK_PENDING_ORDERS);
		monitorResult.setDuration(Long.valueOf(14L));
		monitorResult.setMessage("OK");

		return monitorResult;
	}

	public SampleMonitorResultData createFakeFailedOrders()
	{
		final MessageFormat format = new MessageFormat("There are {0} failing orders created more than {1} hour(s) ago");
		final Object[] array = new Object[]
				{ new Integer(5), new Integer(1) };

		final SampleMonitorResultData monitorResult = new SampleMonitorResultData();
		monitorResult.setName(CHECK_FAILING_ORDERS);
		monitorResult.setDuration(Long.valueOf(42L));
		monitorResult.setMessage(format.format(array));

		return monitorResult;
	}

}
