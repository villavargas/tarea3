package mx.com.telcel.controllers;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;


@Controller
@RequestMapping(value = "/hybris")
public class UpTestController
{

	@ResponseBody
	@GetMapping(value = "/uptest", produces = "application/json")
	public String uptest()
	{
		final UpTestModel uptestmodel = new UpTestModel();
		uptestmodel.setUp("true");
		final Gson gson = new Gson();
		return gson.toJson(uptestmodel);
	}

	class UpTestModel
	{

		private String up;

		public String getUp()
		{
			return up;
		}

		public void setUp(final String up)
		{
			this.up = up;
		}

	}

	@ResponseBody
	@GetMapping(value = "/getip", produces = "application/json")
	public String handleError(final HttpServletRequest request, final HttpServletResponse response)
	{
		final String sIP = request.getHeader("X-FORWARDED-FOR");


		final Map<String, String> result = new HashMap<>();

		final Enumeration headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements())
		{
			final String key = (String) headerNames.nextElement();
			final String value = request.getHeader(key);
			System.out.println("KEY: " + key + "   value:" + value);
			result.put(key, value);
		}



		final UpTestModel uptestmodel = new UpTestModel();
		uptestmodel.setUp(sIP);

		final String ipclient = request.getHeader("x-client-ip");
		System.out.println("IP client: " + sIP);
		System.out.println("IP client 2: " + ipclient);
		final Gson gson = new Gson();
		return gson.toJson(uptestmodel);

	}

}
