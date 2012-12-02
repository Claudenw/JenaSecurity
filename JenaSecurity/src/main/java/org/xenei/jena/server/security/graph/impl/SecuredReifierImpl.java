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
package org.xenei.jena.server.security.graph.impl;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Reifier;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.TripleMatch;
import com.hp.hpl.jena.shared.ReificationStyle;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.Filter;
import com.hp.hpl.jena.vocabulary.RDF;

import org.xenei.jena.server.security.ItemHolder;
import org.xenei.jena.server.security.SecuredItemImpl;
import org.xenei.jena.server.security.SecurityEvaluator.Action;
import org.xenei.jena.server.security.graph.SecuredGraph;
import org.xenei.jena.server.security.graph.SecuredReifier;
import org.xenei.jena.server.security.utils.PermTripleFilter;

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
			return canRead(new Triple(o, RDF.subject.asNode(), Node.ANY))
					|| canRead(new Triple(o, RDF.predicate.asNode(), Node.ANY))
					|| canRead(new Triple(o, RDF.object.asNode(), Node.ANY));
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
	SecuredReifierImpl( final SecuredGraph graph,
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
		checkRead(t);
		return holder.getBaseItem().allNodes(t)
				.filterKeep(new ReadNodeFilter());
	}

	// check that all entries for n associated with reified t can be read.
	private void checkRead( final Node n, final Triple t )
	{
		checkRead(t);
		checkRead(new Triple(n, RDF.subject.asNode(), t.getSubject()));
		checkRead(new Triple(n, RDF.predicate.asNode(), t.getPredicate()));
		checkRead(new Triple(n, RDF.object.asNode(), t.getObject()));
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

	@Override
	public boolean handledAdd( final Triple t )
	{
		checkRead();
		checkRead(Node.ANY, t);
		return holder.getBaseItem().handledAdd(t);
	}

	@Override
	public boolean handledRemove( final Triple t )
	{
		checkRead();
		checkRead(Node.ANY, t);
		return holder.getBaseItem().handledRemove(t);
	}

	@Override
	public boolean hasTriple( final Node n )
	{
		checkRead();
		checkRead(n, Triple.ANY);
		return holder.getBaseItem().hasTriple(n);
	}

	@Override
	public boolean hasTriple( final Triple t )
	{
		checkRead();
		checkRead(Node.ANY, t);
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
		checkRead(t);
		checkDelete(new Triple(n, RDF.subject.asNode(), t.getSubject()));
		checkDelete(new Triple(n, RDF.predicate.asNode(), t.getPredicate()));
		checkDelete(new Triple(n, RDF.object.asNode(), t.getObject()));
		holder.getBaseItem().remove(n, t);
	}

	@Override
	public void remove( final Triple t )
	{
		checkUpdate();
		checkRead(t);
		checkDelete(new Triple(Node.ANY, RDF.subject.asNode(), t.getSubject()));
		checkDelete(new Triple(Node.ANY, RDF.predicate.asNode(),
				t.getPredicate()));
		checkDelete(new Triple(Node.ANY, RDF.object.asNode(), t.getObject()));
		holder.getBaseItem().remove(t);
	}

	@Override
	public int size()
	{
		checkRead();
		return holder.getBaseItem().size();
	}
}
