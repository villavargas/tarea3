package mx.com.telcel.models.individual;

import java.util.List;


public class GeographicAddressSpec
{

	private String id;
	private String href;
	private String type;
	private List<Characteristic> characteristic;

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
	 * @return the href
	 */
	public String getHref()
	{
		return href;
	}

	/**
	 * @param href
	 *           the href to set
	 */
	public void setHref(final String href)
	{
		this.href = href;
	}

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
	 * @return the characteristic
	 */
	public List<Characteristic> getCharacteristic()
	{
		return characteristic;
	}

	/**
	 * @param characteristic
	 *           the characteristic to set
	 */
	public void setCharacteristic(final List<Characteristic> characteristic)
	{
		this.characteristic = characteristic;
	}

}
