package mx.com.telcel.utilities;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;


public class JsonConverter
{

	public <T> T convertJsonToObject(final String json, final Class<T> type)
	{
		return new Gson().fromJson(json, type);
	}

	public <T> T convertInputStreamToObject(final InputStream inputStream, final Class<T> type)
	{
		final Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
		return new Gson().fromJson(reader, type);
	}

	public String convertObjectToString(final Object object)
	{
		final Gson gson = new Gson();
		return gson.toJson(object);
	}

public String converterStringMonth(final int month) {
		String stringMonth;
		switch(month)
		{
			case 1:
				stringMonth = "enero";
				break;
			case 2:
				stringMonth = "febrero";
				break;
			case 3:
				stringMonth = "marzo";
				break;
			case 4:
				stringMonth = "abril";
				break;
			case 5:
				stringMonth = "mayo";
				break;
			case 6:
				stringMonth = "junio";
				break;
			case 7:
				stringMonth = "julio";
				break;
			case 8:
				stringMonth = "agosto";
				break;
			case 9:
				stringMonth = "septiembre";
				break;
			case 10:
				stringMonth = "octubre";
				break;
			case 11:
				stringMonth = "noviembre";
				break;
			case 12:
				stringMonth = "diciembre";
				break;
			default:
				stringMonth = "Error";
		}
		return stringMonth;
	}

}
