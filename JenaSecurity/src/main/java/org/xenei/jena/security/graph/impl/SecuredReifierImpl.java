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
package org.xenei.jena.security.graph.impl;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Reifier;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.TripleMatch;
import com.hp.hpl.jena.shared.ReificationStyle;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.Filter;
import com.hp.hpl.jena.vocabulary.RDF;

import org.xenei.jena.security.AccessDeniedException;
import org.xenei.jena.security.ItemHolder;
import org.xenei.jena.security.SecuredItemImpl;
import org.xenei.jena.security.SecurityEvaluator.Action;
import org.xenei.jena.security.graph.SecuredGraph;
import org.xenei.jena.security.graph.SecuredReifier;
import org.xenei.jena.security.utils.PermTripleFilter;

/**
 * Implementation of SecuredReifier to be used by a SecuredItemInvoker proxy.
 */
public class SecuredReifierImpl extends SecuredItemImpl implements
		SecuredReifier
{
	private class ReadNodeFilter extends Filter<Node>
	{
		@Override
		public boolean accept( final Node o )
		{
			final Triple t = holder.getBaseItem().getTriple(o);
			return canRead(new Triple(o, RDF.subject.asNode(), t.getSubject()))
					|| canRead(new Triple(o, RDF.predicate.asNode(),
							t.getPredicate()))
					|| canRead(new Triple(o, RDF.object.asNode(), t.getObject()));
		}
	}

	// The holder that contains this SecuredReifier
	private final ItemHolder<Reifier, SecuredReifier> holder;

	// the secured graph this reifier is for.
	private final SecuredGraph graph;

	/**
	 * constructor.
	 * 
	 * @param graph
	 *            The secured graph this reifier is for.
	 * @param holder
	 *            The item holder that will contain this SecuredReifier.
	 */
	SecuredReifierImpl( final SecuredGraphImpl graph,
			final ItemHolder<Reifier, SecuredReifier> holder )
	{
		super(graph, holder);
		this.graph = graph;
		this.holder = holder;
	}

	@Override
	public ExtendedIterator<Node> allNodes()
	{
		checkRead();
		return holder.getBaseItem().allNodes().filterKeep(new ReadNodeFilter());
	}

	@Override
	public ExtendedIterator<Node> allNodes( final Triple t )
	{
		checkRead();
		return holder.getBaseItem().allNodes(t)
				.filterKeep(new ReadNodeFilter());
	}

	// check that all entries for n associated with reified t can be read.
	private boolean canRead( final Node n, final Triple t )
	{
		return canRead(t)
				&& canRead(new Triple(n, RDF.subject.asNode(), t.getSubject()))
				&& canRead(new Triple(n, RDF.predicate.asNode(),
						t.getPredicate()))
				&& canRead(new Triple(n, RDF.object.asNode(), t.getObject()));
	}

	private void checkDelete( final Node n, final Triple t )
	{
		final ExtendedIterator<Triple> iter = getTriples(n, t);
		try
		{
			while (iter.hasNext())
			{
				checkDelete(iter.next());
			}
		}
		finally
		{
			iter.close();
		}
	}

	// check that all entries for n associated with reified t can be read.
	private void checkRead( final Node n, final Triple t )
	{
		checkRead(t);
		checkRead(new Triple(n, RDF.subject.asNode(), t.getSubject()));
		checkRead(new Triple(n, RDF.predicate.asNode(), t.getPredicate()));
		checkRead(new Triple(n, RDF.object.asNode(), t.getObject()));
	}

	// verify that we can read at least one of the triples
	// that we would expect to reifiy the triple.
	private void checkReadTriple( final Node n, final Triple t )
	{
		final ExtendedIterator<Triple> iter = getTriples(n, t);
		boolean found = false;
		try
		{
			if (iter.hasNext())
			{ // only an error if there are elements to check.
				while (iter.hasNext() && !found)
				{
					found = canRead(iter.next());
				}
				if (!found)
				{
					throw new AccessDeniedException(getModelNode(), null,
							Action.Read);
				}
			}
		}
		finally
		{
			iter.close();
		}
	}

	@Override
	public void close()
	{
		holder.getBaseItem().close();
	}

	@Override
	public ExtendedIterator<Triple> find( final TripleMatch m )
	{
		checkRead();
		return holder.getBaseItem().find(m)
				.filterKeep(new PermTripleFilter(Action.Read, this));
	}

	@Override
	public ExtendedIterator<Triple> findEither( final TripleMatch m,
			final boolean showHidden )
	{
		checkRead();
		return holder.getBaseItem().findEither(m, showHidden)
				.filterKeep(new PermTripleFilter(Action.Read, this));
	}

	@Override
	public ExtendedIterator<Triple> findExposed( final TripleMatch m )
	{
		checkRead();
		return holder.getBaseItem().findExposed(m)
				.filterKeep(new PermTripleFilter(Action.Read, this));
	}

	@Override
	public SecuredGraph getParentGraph()
	{
		return graph;
	}

	@Override
	public ReificationStyle getStyle()
	{
		return holder.getBaseItem().getStyle();
	}

	@Override
	public Triple getTriple( final Node n )
	{
		checkRead();
		final Triple t = holder.getBaseItem().getTriple(n);
		if (t != null)
		{
			checkRead(n, t);
		}
		return t;
	}

	private ExtendedIterator<Triple> getTriples( final Node n, final Triple t )
	{
		return holder
				.getBaseItem()
				.find(new Triple(n, RDF.subject.asNode(), t.getSubject()))
				.andThen(
						holder.getBaseItem().find(
								new Triple(n, RDF.predicate.asNode(), t
										.getPredicate())))
				.andThen(
						holder.getBaseItem().find(
								new Triple(n, RDF.object.asNode(), t
										.getObject())));

	}

	@Override
	public boolean handledAdd( final Triple t )
	{
		checkUpdate();
		checkCreate(t);
		return holder.getBaseItem().handledAdd(t);
	}

	@Override
	public boolean handledRemove( final Triple t )
	{
		checkUpdate();
		checkDelete(t);
		return holder.getBaseItem().handledRemove(t);
	}

	@Override
	public boolean hasTriple( final Node n )
	{
		checkRead();
		if (!canRead(n, Triple.ANY))
		{
			checkReadTriple(n, Triple.ANY);
		}
		return holder.getBaseItem().hasTriple(n);
	}

	@Override
	public boolean hasTriple( final Triple t )
	{
		checkRead();
		if (!canRead(Node.ANY, t))
		{
			/*
			 * ExtendedIterator<Triple>iter = find( t );
			 * try {
			 * return iter.hasNext();
			 * }
			 * finally {
			 * iter.close();
			 * }
			 */
			checkReadTriple(Node.ANY, t);
		}
		return holder.getBaseItem().hasTriple(t);
	}

	@Override
	public Node reifyAs( final Node n, final Triple t )
	{
		checkUpdate();
		checkRead(t);
		checkCreate(new Triple(n, RDF.subject.asNode(), t.getSubject()));
		checkCreate(new Triple(n, RDF.predicate.asNode(), t.getPredicate()));
		checkCreate(new Triple(n, RDF.object.asNode(), t.getObject()));
		return holder.getBaseItem().reifyAs(n, t);
	}

	@Override
	public void remove( final Node n, final Triple t )
	{
		checkUpdate();
		checkDelete(n, t);
		holder.getBaseItem().remove(n, t);
	}

	@Override
	public void remove( final Triple t )
	{
		checkUpdate();
		checkDelete(Node.ANY, t);
		holder.getBaseItem().remove(t);
	}

	@Override
	public int size()
	{
		checkRead();
		return holder.getBaseItem().size();
	}
}
