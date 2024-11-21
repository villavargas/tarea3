package mx.com.telcel.models.individual;

public class ErrorDetailRes
{

	private String code;
	private Integer severityLevel;
	private String description;
	private String actor;
	private String businessMeaning;

	/**
	 * @return the code
	 */
	public String getCode()
	{
		return code;
	}

	/**
	 * @param code
	 *           the code to set
	 */
	public void setCode(final String code)
	{
		this.code = code;
	}

	/**
	 * @return the severityLevel
	 */
	public Integer getSeverityLevel()
	{
		return severityLevel;
	}

	/**
	 * @param severityLevel
	 *           the severityLevel to set
	 */
	public void setSeverityLevel(final Integer severityLevel)
	{
		this.severityLevel = severityLevel;
	}

	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * @param description
	 *           the description to set
	 */
	public void setDescription(final String description)
	{
		this.description = description;
	}

	/**
	 * @return the actor
	 */
	public String getActor()
	{
		return actor;
	}

	/**
	 * @param actor
	 *           the actor to set
	 */
	public void setActor(final String actor)
	{
		this.actor = actor;
	}

	/**
	 * @return the businessMeaning
	 */
	public String getBusinessMeaning()
	{
		return businessMeaning;
	}

	/**
	 * @param businessMeaning
	 *           the businessMeaning to set
	 */
	public void setBusinessMeaning(final String businessMeaning)
	{
		this.businessMeaning = businessMeaning;
	}

}
