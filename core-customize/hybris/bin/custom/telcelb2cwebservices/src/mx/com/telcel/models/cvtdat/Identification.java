package mx.com.telcel.models.cvtdat;

public class Identification
{

	private String type;
	private String identificationId;
	private String checkDigit;

	/**
	 * @return the type
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * @param type
	 *           the type to set
	 */
	public void setType(final String type)
	{
		this.type = type;
	}

	/**
	 * @return the identificationId
	 */
	public String getIdentificationId()
	{
		return identificationId;
	}

	/**
	 * @param identificationId
	 *           the identificationId to set
	 */
	public void setIdentificationId(final String identificationId)
	{
		this.identificationId = identificationId;
	}

	/**
	 * @return the checkDigit
	 */
	public String getCheckDigit()
	{
		return checkDigit;
	}

	/**
	 * @param checkDigit
	 *           the checkDigit to set
	 */
	public void setCheckDigit(final String checkDigit)
	{
		this.checkDigit = checkDigit;
	}

}
