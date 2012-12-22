package org.xenei.jena.security.model.impl;

import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.Filter;
import com.hp.hpl.jena.util.iterator.Map1;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.xenei.jena.security.model.SecuredModel;
import org.xenei.jena.security.model.SecuredResource;

public class SecuredResIterator implements ResIterator
{

	private class PermResourceMap implements Map1<Resource, Resource>
	{
		private final SecuredModel securedModel;

		public PermResourceMap( final SecuredModel securedModel )
		{
			this.securedModel = securedModel;
		}

		@Override
		public SecuredResource map1( final Resource o )
		{
			return SecuredResourceImpl.getInstance(securedModel, o);
		}
	}

	private final ExtendedIterator<Resource> iter;

	public SecuredResIterator( final SecuredModel securedModel,
			final ExtendedIterator<Resource> wrapped )
	{

		final PermResourceMap map1 = new PermResourceMap(securedModel);
		iter = wrapped.mapWith(map1);
	}

	@Override
	public <X extends Resource> ExtendedIterator<Resource> andThen(
			final Iterator<X> other )
	{
		return iter.andThen(other);
	}

	@Override
	public void close()
	{
		iter.close();
	}

	@Override
	public ExtendedIterator<Resource> filterDrop( final Filter<Resource> f )
	{
		return iter.filterDrop(f);
	}

	@Override
	public ExtendedIterator<Resource> filterKeep( final Filter<Resource> f )
	{
		return iter.filterKeep(f);
	}

	@Override
	public boolean hasNext()
	{
		return iter.hasNext();
	}

	@Override
	public <U> ExtendedIterator<U> mapWith( final Map1<Resource, U> map1 )
	{
		return iter.mapWith(map1);
	}

	@Override
	public Resource next()
	{
		return iter.next();
	}

	@Override
	public Resource nextResource()
	{
		return next();
	}

	@Override
	public void remove()
	{
		iter.remove();
	}

	@Override
	public Resource removeNext()
	{
		return iter.removeNext();
	}

	@Override
	public List<Resource> toList()
	{
		return iter.toList();
	}

	@Override
	public Set<Resource> toSet()
	{
		return iter.toSet();
	}
}
