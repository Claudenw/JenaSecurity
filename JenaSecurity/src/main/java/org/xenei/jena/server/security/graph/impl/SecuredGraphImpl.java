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

import com.hp.hpl.jena.graph.BulkUpdateHandler;
import com.hp.hpl.jena.graph.Capabilities;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.GraphEventManager;
import com.hp.hpl.jena.graph.GraphStatisticsHandler;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Reifier;
import com.hp.hpl.jena.graph.TransactionHandler;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.TripleMatch;
import com.hp.hpl.jena.graph.query.QueryHandler;
import com.hp.hpl.jena.shared.AddDeniedException;
import com.hp.hpl.jena.shared.DeleteDeniedException;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import org.xenei.jena.server.security.ItemHolder;
import org.xenei.jena.server.security.SecuredItem;
import org.xenei.jena.server.security.SecuredItemImpl;
import org.xenei.jena.server.security.SecurityEvaluator;
import org.xenei.jena.server.security.graph.SecuredCapabilities;
import org.xenei.jena.server.security.graph.SecuredGraph;
import org.xenei.jena.server.security.graph.SecuredGraphEventManager;
import org.xenei.jena.server.security.graph.SecuredPrefixMapping;
import org.xenei.jena.server.security.graph.SecuredReifier;

/**
 * Implementation of SecuredGraph to be used by a SecuredItemInvoker proxy.
 */
public class SecuredGraphImpl extends SecuredItemImpl implements SecuredGraph
{
	// the reifier for this secured graph.
	private SecuredReifier reifier;
	// the prefixMapping for this graph.
	private SecuredPrefixMapping prefixMapping;
	// the item holder that contains this SecuredGraph
	private final ItemHolder<Graph, SecuredGraph> holder;

	private final SecuredGraphEventManager eventManager;

	/**
	 * Constructor
	 * 
	 * @param securityEvaluator
	 *            The security evaluator to use
	 * @param graphIRI
	 *            The IRI for the graph
	 * @param holder
	 *            The item holder that will contain this SecuredGraph.
	 */
	SecuredGraphImpl( final SecuredItem securedItem,
			final ItemHolder<Graph, SecuredGraph> holder )
	{
		super(securedItem, holder);
		this.holder = holder;
		this.eventManager = new SecuredGraphEventManager(this, holder
				.getBaseItem().getEventManager());
	}

	SecuredGraphImpl( final SecurityEvaluator securityEvaluator,
			final String modelURI, final ItemHolder<Graph, SecuredGraph> holder )
	{
		super(securityEvaluator, modelURI, holder);
		this.holder = holder;
		this.eventManager = new SecuredGraphEventManager(this, holder
				.getBaseItem().getEventManager());
	}

	@Override
	public void add( final Triple t ) throws AddDeniedException
	{
		checkUpdate();
		checkCreate(t);
		holder.getBaseItem().add(t);
	}

	@Override
	public void close()
	{
		holder.getBaseItem().close();
	}

	@Override
	public boolean contains( final Node s, final Node p, final Node o )
	{
		checkRead();
		checkRead(new Triple(s, p, o));
		return holder.getBaseItem().contains(s, p, o);
	}

	@Override
	public boolean contains( final Triple t )
	{
		checkRead();
		checkRead(t);
		return holder.getBaseItem().contains(t);
	}

	private synchronized void createPrefixMapping()
	{
		if (prefixMapping == null)
		{
			prefixMapping = org.xenei.jena.server.security.graph.impl.Factory
					.getInstance(this, holder.getBaseItem().getPrefixMapping());
		}
	}

	private synchronized void createReifier()
	{
		if (reifier == null)
		{
			reifier = org.xenei.jena.server.security.graph.impl.Factory
					.getInstance(this, holder.getBaseItem().getReifier());
		}
	}

	@Override
	public void delete( final Triple t ) throws DeleteDeniedException
	{
		checkDelete();
		checkDelete(t);
		holder.getBaseItem().delete(t);
	}

	@Override
	public boolean dependsOn( final Graph other )
	{
		checkRead();
		if (other.equals(holder.getBaseItem()))
		{
			return true;
		}
		return holder.getBaseItem().dependsOn(other);
	}

	@Override
	public ExtendedIterator<Triple> find( final Node s, final Node p,
			final Node o )
	{
		checkRead();
		checkRead(new Triple(s, p, o));
		return holder.getBaseItem().find(s, p, o);
	}

	@Override
	public ExtendedIterator<Triple> find( final TripleMatch m )
	{
		checkRead();
		checkRead(m.asTriple());
		return holder.getBaseItem().find(m);
	}

	@Override
	public BulkUpdateHandler getBulkUpdateHandler()
	{
		return org.xenei.jena.server.security.graph.impl.Factory.getInstance(
				this, holder.getBaseItem().getBulkUpdateHandler());
	}

	@Override
	public Capabilities getCapabilities()
	{
		return new SecuredCapabilities(getSecurityEvaluator(), getModelIRI(),
				holder.getBaseItem().getCapabilities());
	}

	@Override
	public GraphEventManager getEventManager()
	{
		return eventManager;
	}

	@Override
	public PrefixMapping getPrefixMapping()
	{
		if (prefixMapping == null)
		{
			createPrefixMapping();
		}
		return prefixMapping;
	}

	@Override
	public Reifier getReifier()
	{
		if (reifier == null)
		{
			createReifier();
		}
		return reifier;
	}

	@Override
	public GraphStatisticsHandler getStatisticsHandler()
	{
		checkRead();
		return holder.getBaseItem().getStatisticsHandler();
	}

	@Override
	public TransactionHandler getTransactionHandler()
	{
		return holder.getBaseItem().getTransactionHandler();
	}

	@Override
	public boolean isClosed()
	{
		return holder.getBaseItem().isClosed();
	}

	@Override
	public boolean isEmpty()
	{
		checkRead();
		return holder.getBaseItem().isClosed();
	}

	@Override
	public boolean isIsomorphicWith( final Graph g )
	{
		checkRead();
		if (g.size() != holder.getBaseItem().size())
		{
			return false;
		}
		final Triple t = new Triple(Node.ANY, Node.ANY, Node.ANY);
		if (!canRead(t))
		{
			final ExtendedIterator<Triple> iter = g.find(t);
			while (iter.hasNext())
			{
				checkRead(iter.next());
			}
		}
		return holder.getBaseItem().isIsomorphicWith(g);
	}

	@Override
	public QueryHandler queryHandler()
	{
		checkRead();
		return holder.getBaseItem().queryHandler();
	}

	@Override
	public int size()
	{
		checkRead();
		return holder.getBaseItem().size();
	}

}