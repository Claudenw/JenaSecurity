/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.xenei.jena.server.security.iterators;

import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.Filter;
import com.hp.hpl.jena.util.iterator.Map1;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.xenei.jena.server.security.SecuredItem;

/**
 * Abstract secured iterator class.
 * 
 * @param <Base>
 *            The base class
 * @param <Secured>
 *            The secured class
 */
public abstract class SecuredIterator<Base, Secured extends SecuredItem>
		implements ExtendedIterator<Secured>
{
	private final ExtendedIterator<Secured> iter;

	/**
	 * Constructor
	 * 
	 * @param securedItem
	 *            The item that defines the security context.
	 * @param wrapped
	 *            The iterator that is being wrapped.
	 */
	protected SecuredIterator( final SecuredItem securedItem,
			final ExtendedIterator<Base> wrapped, final Filter<Base> filter,
			final Map1<Base, Secured> map1 )
	{
		iter = wrapped.filterKeep(filter).mapWith(map1);
	}

	@Override
	public <X extends Secured> ExtendedIterator<Secured> andThen(
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
	public ExtendedIterator<Secured> filterDrop( final Filter<Secured> f )
	{
		return iter.filterDrop(f);
	}

	@Override
	public ExtendedIterator<Secured> filterKeep( final Filter<Secured> f )
	{
		return iter.filterKeep(f);
	}

	@Override
	public boolean hasNext()
	{
		return iter.hasNext();
	}

	@Override
	public <U> ExtendedIterator<U> mapWith( final Map1<Secured, U> map1 )
	{
		return iter.mapWith(map1);
	}

	@Override
	public Secured next()
	{
		return iter.next();
	}

	@Override
	public void remove()
	{
		iter.remove();
	}

	@Override
	public Secured removeNext()
	{
		return iter.removeNext();
	}

	@Override
	public List<Secured> toList()
	{
		return iter.toList();
	}

	@Override
	public Set<Secured> toSet()
	{
		return iter.toSet();
	}

}
