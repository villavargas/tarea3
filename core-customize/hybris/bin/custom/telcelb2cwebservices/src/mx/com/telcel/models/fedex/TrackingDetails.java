package mx.com.telcel.models.fedex;

public class TrackingDetails
{

	private String trackId;
	private String formId;
	private String trackingType;

	/**
	 * @return the trackId
	 */
	public String getTrackId()
	{
		return trackId;
	}

	/**
	 * @param trackId
	 *           the trackId to set
	 */
	public void setTrackId(final String trackId)
	{
		this.trackId = trackId;
	}

	/**
	 * @return the formId
	 */
	public String getFormId()
	{
		return formId;
	}

	/**
	 * @param formId
	 *           the formId to set
	 */
	public void setFormId(final String formId)
	{
		this.formId = formId;
	}

	/**
	 * @return the trackingType
	 */
	public String getTrackingType()
	{
		return trackingType;
	}

	/**
	 * @param trackingType
	 *           the trackingType to set
	 */
	public void setTrackingType(final String trackingType)
	{
		this.trackingType = trackingType;
	}

}
