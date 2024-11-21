package mx.com.telcel.models.individual;

public class IndividualResponse
{

	private Integer code;
	private Individual individual;
	private GenericFault error;

	/**
	 * @return the code
	 */
	public Integer getCode()
	{
		return code;
	}

	/**
	 * @param code
	 *           the code to set
	 */
	public void setCode(final Integer code)
	{
		this.code = code;
	}

	/**
	 * @return the individual
	 */
	public Individual getIndividual()
	{
		return individual;
	}

	/**
	 * @param individual
	 *           the individual to set
	 */
	public void setIndividual(final Individual individual)
	{
		this.individual = individual;
	}

	/**
	 * @return the error
	 */
	public GenericFault getError()
	{
		return error;
	}

	/**
	 * @param error
	 *           the error to set
	 */
	public void setError(final GenericFault error)
	{
		this.error = error;
	}

}
