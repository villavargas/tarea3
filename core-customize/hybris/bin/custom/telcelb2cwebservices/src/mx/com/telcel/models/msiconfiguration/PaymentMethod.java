package mx.com.telcel.models.msiconfiguration;

import java.util.List;


public class PaymentMethod
{

	private String method;
	private List<PaymentOption> options;

	/**
	 * @return the method
	 */
	public String getMethod()
	{
		return method;
	}

	/**
	 * @param method
	 *           the method to set
	 */
	public void setMethod(final String method)
	{
		this.method = method;
	}

	/**
	 * @return the options
	 */
	public List<PaymentOption> getOptions()
	{
		return options;
	}

	/**
	 * @param options
	 *           the options to set
	 */
	public void setOptions(final List<PaymentOption> options)
	{
		this.options = options;
	}

}
