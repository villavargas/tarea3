package mx.com.telcel.utilities;

public class NameSpace
{

	private String prefix;
	private String uri;

	public NameSpace()
	{
	}

	/**
	 * @param prefix
	 * @param uri
	 */
	public NameSpace(final String prefix, final String uri)
	{
		super();
		this.prefix = prefix;
		this.uri = uri;
	}

	/**
	 * @return the prefix
	 */
	public String getPrefix()
	{
		return prefix;
	}

	/**
	 * @param prefix
	 *           the prefix to set
	 */
	public void setPrefix(final String prefix)
	{
		this.prefix = prefix;
	}

	/**
	 * @return the uri
	 */
	public String getUri()
	{
		return uri;
	}

	/**
	 * @param uri
	 *           the uri to set
	 */
	public void setUri(final String uri)
	{
		this.uri = uri;
	}

}
