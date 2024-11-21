package mx.com.telcel.models.individual;

public class Characteristic
{

	private String id;
	private String name;
	private String valueType;
	private String value;

	/**
	 * @return the id
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * @param id
	 *           the id to set
	 */
	public void setId(final String id)
	{
		this.id = id;
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

	/**
	 * @return the valueType
	 */
	public String getValueType()
	{
		return valueType;
	}

	/**
	 * @param valueType
	 *           the valueType to set
	 */
	public void setValueType(final String valueType)
	{
		this.valueType = valueType;
	}

	/**
	 * @return the value
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 * @param value
	 *           the value to set
	 */
	public void setValue(final String value)
	{
		this.value = value;
	}

}
