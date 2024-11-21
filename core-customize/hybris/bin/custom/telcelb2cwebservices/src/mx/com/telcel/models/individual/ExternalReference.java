package mx.com.telcel.models.individual;

public class ExternalReference
{

	private String externalReferenceType;
	private String name;

	/**
	 * @return the externalReferenceType
	 */
	public String getExternalReferenceType()
	{
		return externalReferenceType;
	}

	/**
	 * @param externalReferenceType
	 *           the externalReferenceType to set
	 */
	public void setExternalReferenceType(final String externalReferenceType)
	{
		this.externalReferenceType = externalReferenceType;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name
	 *           the name to set
	 */
	public void setName(final String name)
	{
		this.name = name;
	}

}
