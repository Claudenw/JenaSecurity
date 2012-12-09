package org.xenei.jena.security.utils;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.util.iterator.Filter;
import com.hp.hpl.jena.vocabulary.RDF;

public class ContainerFilter extends Filter<Statement>
{

	@Override
	public boolean accept( final Statement o )
	{
		final Property p = o.getPredicate();
		if (p.getNameSpace().equals(RDF.getURI())
				&& p.getLocalName().startsWith("_"))
		{
			try
			{
				Integer.parseInt(p.getLocalName().substring(1));
				return true;
			}
			catch (final NumberFormatException e)
			{
				// acceptable;
			}
		}
		return false;
	}

}
