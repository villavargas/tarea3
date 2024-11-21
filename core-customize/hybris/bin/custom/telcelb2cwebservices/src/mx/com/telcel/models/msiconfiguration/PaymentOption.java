package mx.com.telcel.models.msiconfiguration;

import java.util.List;


public class PaymentOption
{

	private String option;
	private String logo;
	private List<PaymentDetail> detail;

	/**
	 * @return the option
	 */
	public String getOption()
	{
		return option;
	}

	/**
	 * @param option
	 *           the option to set
	 */
	public void setOption(final String option)
	{
		this.option = option;
	}

	/**
	 * @return the logo
	 */
	public String getLogo()
	{
		return logo;
	}

	/**
	 * @param logo
	 *           the logo to set
	 */
	public void setLogo(final String logo)
	{
		this.logo = logo;
	}

	/**
	 * @return the detail
	 */
	public List<PaymentDetail> getDetail()
	{
		return detail;
	}

	/**
	 * @param detail
	 *           the detail to set
	 */
	public void setDetail(final List<PaymentDetail> detail)
	{
		this.detail = detail;
	}

}
