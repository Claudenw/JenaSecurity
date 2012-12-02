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
package org.xenei.jena.server.security.model.impl;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Container;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;

import org.xenei.jena.server.security.ItemHolder;
import org.xenei.jena.server.security.SecurityEvaluator;
import org.xenei.jena.server.security.model.SecuredContainer;
import org.xenei.jena.server.security.model.SecuredModel;

/**
 * Implementation of SecuredContainer to be used by a SecuredItemInvoker proxy.
 */
public abstract class SecuredContainerImpl extends SecuredResourceImpl
		implements SecuredContainer
{
	// the item holder that contains this SecuredContainer.
	private final ItemHolder<? extends Container, ? extends SecuredContainer> holder;

	/**
	 * Constructor
	 * 
	 * @param securityEvaluator
	 *            The security evaluator to use
	 * @param graphIRI
	 *            the graph IRI to validate against.
	 * @param holder
	 *            The item holder that will contain this SecuredContainer
	 */
	protected SecuredContainerImpl(
			final SecuredModel securedModel,
			final ItemHolder<? extends Container, ? extends SecuredContainer> holder)
	{
		super(securedModel, holder);
		this.holder = holder;
	}

	@Override
	public SecuredContainer add( final boolean o )
	{
		checkUpdate();
		checkAdd(ResourceFactory.createTypedLiteral(o));
		holder.getBaseItem().add(o);
		return holder.getSecuredItem();
	}

	@Override
	public SecuredContainer add( final char o )
	{
		checkUpdate();
		checkAdd(ResourceFactory.createTypedLiteral(o));
		holder.getBaseItem().add(o);
		return holder.getSecuredItem();
	}

	@Override
	public SecuredContainer add( final double o )
	{
		checkUpdate();
		checkAdd(ResourceFactory.createTypedLiteral(o));
		holder.getBaseItem().add(o);
		return holder.getSecuredItem();
	}

	@Override
	public SecuredContainer add( final float o )
	{
		checkUpdate();
		checkAdd(ResourceFactory.createTypedLiteral(o));
		holder.getBaseItem().add(o);
		return holder.getSecuredItem();
	}

	@Override
	public SecuredContainer add( final long o )
	{
		checkUpdate();
		checkAdd(ResourceFactory.createTypedLiteral(o));
		holder.getBaseItem().add(o);
		return holder.getSecuredItem();
	}

	@Override
	public SecuredContainer add( final Object o )
	{
		checkUpdate();
		checkAdd(ResourceFactory.createTypedLiteral(o));
		holder.getBaseItem().add(o);
		return holder.getSecuredItem();
	}

	@Override
	public SecuredContainer add( final RDFNode o )
	{
		checkUpdate();
		checkAdd(o.asNode());
		holder.getBaseItem().add(o);
		return holder.getSecuredItem();
	}

	@Override
	public SecuredContainer add( final String o )
	{
		checkUpdate();
		checkAdd(ResourceFactory.createTypedLiteral(o));
		holder.getBaseItem().add(o);
		return holder.getSecuredItem();
	}

	@Override
	public SecuredContainer add( final String o, final String l )
	{
		checkUpdate();
		checkAdd(Node.createLiteral(o, l, false));
		holder.getBaseItem().add(o, l);
		return holder.getSecuredItem();
	}

	protected void checkAdd( final Literal l )
	{
		final int i = holder.getBaseItem().size();
		checkCreate(new Triple(holder.getBaseItem().asNode(), RDF.li(i + 1)
				.asNode(), l.asNode()));
	}

	protected void checkAdd( final Node n )
	{
		final int i = holder.getBaseItem().size();
		checkCreate(new Triple(holder.getBaseItem().asNode(), RDF.li(i + 1)
				.asNode(), n));
	}

	protected void checkRead( final Literal l )
	{
		checkCreate(new Triple(holder.getBaseItem().asNode(), Node.ANY,
				l.asNode()));
	}

	protected void checkRead( final Node n )
	{
		checkCreate(new Triple(holder.getBaseItem().asNode(), Node.ANY, n));
	}

	@Override
	public boolean contains( final boolean o )
	{
		checkRead();
		checkRead(ResourceFactory.createTypedLiteral(o));
		return holder.getBaseItem().contains(o);
	}

	@Override
	public boolean contains( final char o )
	{
		checkRead();
		checkRead(ResourceFactory.createTypedLiteral(o));
		return holder.getBaseItem().contains(o);
	}

	@Override
	public boolean contains( final double o )
	{
		checkRead();
		checkRead(ResourceFactory.createTypedLiteral(o));
		return holder.getBaseItem().contains(o);
	}

	@Override
	public boolean contains( final float o )
	{
		checkRead();
		checkRead(ResourceFactory.createTypedLiteral(o));
		return holder.getBaseItem().contains(o);
	}

	@Override
	public boolean contains( final long o )
	{
		checkRead();
		checkRead(ResourceFactory.createTypedLiteral(o));
		return holder.getBaseItem().contains(o);
	}

	@Override
	public boolean contains( final Object o )
	{
		checkRead();
		checkRead(ResourceFactory.createTypedLiteral(o));
		return holder.getBaseItem().contains(o);
	}

	@Override
	public boolean contains( final RDFNode o )
	{
		checkRead();
		checkRead(o.asNode());
		return holder.getBaseItem().contains(o);
	}

	@Override
	public boolean contains( final String o )
	{
		checkRead();
		checkRead(ResourceFactory.createTypedLiteral(o));
		return holder.getBaseItem().contains(o);
	}

	@Override
	public boolean contains( final String o, final String l )
	{
		checkRead();
		checkRead(Node.createLiteral(o, l, false));
		return holder.getBaseItem().contains(o, l);
	}

	@Override
	public boolean isAlt()
	{
		return holder.getBaseItem().isAlt();
	}

	@Override
	public boolean isBag()
	{
		return holder.getBaseItem().isBag();
	}

	@Override
	public boolean isSeq()
	{
		return holder.getBaseItem().isSeq();
	}

	@Override
	public SecuredNodeIterator iterator()
	{
		checkRead();
		checkRead(new Triple(holder.getBaseItem().asNode(), Node.ANY, Node.ANY));
		return new SecuredNodeIterator(getModel(), holder.getBaseItem().iterator());
	}

	@Override
	public SecuredContainer remove( final Statement s )
	{
		checkUpdate();
		checkDelete(s.asTriple());
		holder.getBaseItem().remove(s);
		return holder.getSecuredItem();
	}

	@Override
	public int size()
	{
		checkRead();
		return holder.getBaseItem().size();
	}

}
