package mx.com.telcel.utilities;

import java.util.Objects;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;


public class PrefixMapper extends NamespacePrefixMapper
{

	private final NameSpace[] namespaces;

	public PrefixMapper(final NameSpace[] namespaces)
	{
		this.namespaces = namespaces;
	}

	@Override
	public String getPreferredPrefix(final String namespaceUri, final String suggestion, final boolean requirePrefix)
	{
		if (Objects.isNull(this.namespaces))
		{
			return suggestion;
		}
		for (final NameSpace namespace : this.namespaces)
		{
			if (namespace.getUri().equals(namespaceUri))
			{
				return namespace.getPrefix();
			}
		}
		return suggestion;
	}

}
