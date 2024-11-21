package mx.com.telcel.models.fedex;

import java.util.List;


public class Service
{

	private String id;
	private String type;
	private String description;
	private String name;
	private List<Characteristic> characteristics;
	private Order order;

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
	 * @return the characteristics
	 */
	public List<Characteristic> getCharacteristics()
	{
		return characteristics;
	}

	/**
	 * @param characteristics
	 *           the characteristics to set
	 */
	public void setCharacteristics(final List<Characteristic> characteristics)
	{
		this.characteristics = characteristics;
	}

	/**
	 * @return the order
	 */
	public Order getOrder()
	{
		return order;
	}

	/**
	 * @param order
	 *           the order to set
	 */
	public void setOrder(final Order order)
	{
		this.order = order;
	}

}
