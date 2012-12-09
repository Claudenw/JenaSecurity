package org.xenei.jena.security.utils;

import com.hp.hpl.jena.rdf.model.RDFList;
import com.hp.hpl.jena.vocabulary.RDF;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RDFListIterator implements Iterator<RDFList>
{
	private RDFList current;
	private Boolean found;

	public RDFListIterator( final RDFList start )
	{
		this.current = start;
	}

	private boolean endOfList()
	{
		return current.equals(RDF.nil);
	}

	@Override
	public boolean hasNext()
	{
		if ((found == null) && !endOfList())
		{
			found = !endOfList();
		}
		return found == null ? false : found;
	}

	private void incrementCurrent()
	{
		if (!endOfList())
		{
			current = current.getRequiredProperty(RDF.rest).getResource()
					.as(RDFList.class);
		}
	}

	@Override
	public RDFList next()
	{
		if (hasNext())
		{
			found = null;
			final RDFList retval = current;
			incrementCurrent();
			return retval;
		}
		throw new NoSuchElementException();
	}

	@Override
	public void remove()
	{
		throw new UnsupportedOperationException();
	}

}
