package mx.com.telcel.models.individual;

import org.joda.time.DateTime;


public class PartyIdentification
{

	private String identificationId;
	private String identificationType;
	private String issuingAuthority;
	private DateTime issuingDate;
	private String checkDigit;
	private TimePeriod validFor;

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
	 * @return the identificationType
	 */
	public String getIdentificationType()
	{
		return identificationType;
	}

	/**
	 * @param identificationType
	 *           the identificationType to set
	 */
	public void setIdentificationType(final String identificationType)
	{
		this.identificationType = identificationType;
	}

	/**
	 * @return the issuingAuthority
	 */
	public String getIssuingAuthority()
	{
		return issuingAuthority;
	}

	/**
	 * @param issuingAuthority
	 *           the issuingAuthority to set
	 */
	public void setIssuingAuthority(final String issuingAuthority)
	{
		this.issuingAuthority = issuingAuthority;
	}

	/**
	 * @return the issuingDate
	 */
	public DateTime getIssuingDate()
	{
		return issuingDate;
	}

	/**
	 * @param issuingDate
	 *           the issuingDate to set
	 */
	public void setIssuingDate(final DateTime issuingDate)
	{
		this.issuingDate = issuingDate;
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

	/**
	 * @return the validFor
	 */
	public TimePeriod getValidFor()
	{
		return validFor;
	}

	/**
	 * @param validFor
	 *           the validFor to set
	 */
	public void setValidFor(final TimePeriod validFor)
	{
		this.validFor = validFor;
	}

}
