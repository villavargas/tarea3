package mx.com.telcel.models.fedex;

import java.util.List;


public class Order
{

	private String id;
	private String description;
	private String name;
	private String status;
	private String type;
	private TimePeriod validFor;
	private TrackingDetails trackingDetails;
	private Quantity quantity;
	private List<Characteristic> characteristics;
	private List<OrderItem> orderItem;
	private List<ServiceRates> serviceRates;
	private List<Characteristic> operationalInstructions;
	private List<Characteristic> barCodes;

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
	 * @return the status
	 */
	public String getStatus()
	{
		return status;
	}

	/**
	 * @param status
	 *           the status to set
	 */
	public void setStatus(final String status)
	{
		this.status = status;
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

	/**
	 * @return the quantity
	 */
	public Quantity getQuantity()
	{
		return quantity;
	}

	/**
	 * @param quantity
	 *           the quantity to set
	 */
	public void setQuantity(final Quantity quantity)
	{
		this.quantity = quantity;
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
	 * @return the orderItem
	 */
	public List<OrderItem> getOrderItem()
	{
		return orderItem;
	}

	/**
	 * @param orderItem
	 *           the orderItem to set
	 */
	public void setOrderItem(final List<OrderItem> orderItem)
	{
		this.orderItem = orderItem;
	}

	/**
	 * @return the serviceRates
	 */
	public List<ServiceRates> getServiceRates()
	{
		return serviceRates;
	}

	/**
	 * @param serviceRates
	 *           the serviceRates to set
	 */
	public void setServiceRates(final List<ServiceRates> serviceRates)
	{
		this.serviceRates = serviceRates;
	}

	/**
	 * @return the trackingDetails
	 */
	public TrackingDetails getTrackingDetails()
	{
		return trackingDetails;
	}

	/**
	 * @param trackingDetails
	 *           the trackingDetails to set
	 */
	public void setTrackingDetails(final TrackingDetails trackingDetails)
	{
		this.trackingDetails = trackingDetails;
	}

	/**
	 * @return the operationalInstructions
	 */
	public List<Characteristic> getOperationalInstructions()
	{
		return operationalInstructions;
	}

	/**
	 * @param operationalInstructions
	 *           the operationalInstructions to set
	 */
	public void setOperationalInstructions(final List<Characteristic> operationalInstructions)
	{
		this.operationalInstructions = operationalInstructions;
	}

	/**
	 * @return the barCodes
	 */
	public List<Characteristic> getBarCodes()
	{
		return barCodes;
	}

	/**
	 * @param barCodes
	 *           the barCodes to set
	 */
	public void setBarCodes(final List<Characteristic> barCodes)
	{
		this.barCodes = barCodes;
	}

}
