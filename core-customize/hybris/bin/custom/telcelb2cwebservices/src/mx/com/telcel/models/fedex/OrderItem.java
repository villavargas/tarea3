package mx.com.telcel.models.fedex;

import java.util.List;


public class OrderItem
{

	private String itemSequence;
	private String itemGroup;
	private Quantity weight;
	private List<Characteristic> characteristics;
	private Label label;

	/**
	 * @return the itemSequence
	 */
	public String getItemSequence()
	{
		return itemSequence;
	}

	/**
	 * @param itemSequence
	 *           the itemSequence to set
	 */
	public void setItemSequence(final String itemSequence)
	{
		this.itemSequence = itemSequence;
	}

	/**
	 * @return the itemGroup
	 */
	public String getItemGroup()
	{
		return itemGroup;
	}

	/**
	 * @param itemGroup
	 *           the itemGroup to set
	 */
	public void setItemGroup(final String itemGroup)
	{
		this.itemGroup = itemGroup;
	}

	/**
	 * @return the weight
	 */
	public Quantity getWeight()
	{
		return weight;
	}

	/**
	 * @param weight
	 *           the weight to set
	 */
	public void setWeight(final Quantity weight)
	{
		this.weight = weight;
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
	 * @return the label
	 */
	public Label getLabel()
	{
		return label;
	}

	/**
	 * @param label
	 *           the label to set
	 */
	public void setLabel(final Label label)
	{
		this.label = label;
	}

}
