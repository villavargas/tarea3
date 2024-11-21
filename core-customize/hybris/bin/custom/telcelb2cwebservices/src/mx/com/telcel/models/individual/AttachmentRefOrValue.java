package mx.com.telcel.models.individual;

public class AttachmentRefOrValue
{

	private String id;
	private String href;
	private String attachmentType;
	private String content;
	private String description;
	private String mimeType;
	private String name;
	private String url;
	private Quantity size;
	private TimePeriod validFor;

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
	 * @return the attachmentType
	 */
	public String getAttachmentType()
	{
		return attachmentType;
	}

	/**
	 * @param attachmentType
	 *           the attachmentType to set
	 */
	public void setAttachmentType(final String attachmentType)
	{
		this.attachmentType = attachmentType;
	}

	/**
	 * @return the content
	 */
	public String getContent()
	{
		return content;
	}

	/**
	 * @param content
	 *           the content to set
	 */
	public void setContent(final String content)
	{
		this.content = content;
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
	 * @return the mimeType
	 */
	public String getMimeType()
	{
		return mimeType;
	}

	/**
	 * @param mimeType
	 *           the mimeType to set
	 */
	public void setMimeType(final String mimeType)
	{
		this.mimeType = mimeType;
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
	 * @return the url
	 */
	public String getUrl()
	{
		return url;
	}

	/**
	 * @param url
	 *           the url to set
	 */
	public void setUrl(final String url)
	{
		this.url = url;
	}

	/**
	 * @return the size
	 */
	public Quantity getSize()
	{
		return size;
	}

	/**
	 * @param size
	 *           the size to set
	 */
	public void setSize(final Quantity size)
	{
		this.size = size;
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
