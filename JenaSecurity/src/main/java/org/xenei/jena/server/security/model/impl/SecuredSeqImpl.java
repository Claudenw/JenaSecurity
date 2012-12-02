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
import com.hp.hpl.jena.rdf.model.Alt;
import com.hp.hpl.jena.rdf.model.Bag;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResourceF;
import com.hp.hpl.jena.rdf.model.Seq;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.Filter;
import com.hp.hpl.jena.vocabulary.RDF;

import org.xenei.jena.server.security.ItemHolder;
import org.xenei.jena.server.security.SecuredItem;
import org.xenei.jena.server.security.SecuredItemInvoker;
import org.xenei.jena.server.security.SecurityEvaluator;
import org.xenei.jena.server.security.model.SecuredAlt;
import org.xenei.jena.server.security.model.SecuredBag;
import org.xenei.jena.server.security.model.SecuredLiteral;
import org.xenei.jena.server.security.model.SecuredModel;
import org.xenei.jena.server.security.model.SecuredRDFNode;
import org.xenei.jena.server.security.model.SecuredResource;
import org.xenei.jena.server.security.model.SecuredSeq;

/**
 * Implementation of SecuredSeq to be used by a SecuredItemInvoker proxy.
 * 
 * Sequence may have breaks in the order.
 * http://www.w3.org/TR/2004/REC-rdf-mt-20040210/#Containers
 * 
 */
public class SecuredSeqImpl extends SecuredContainerImpl implements SecuredSeq
{
	private class RDFNodeFilter extends Filter<Statement>
	{
		private final RDFNode n;

		public RDFNodeFilter( final RDFNode n )
		{
			this.n = n;
		}

		@Override
		public boolean accept( final Statement o )
		{
			return (o.getPredicate().getOrdinal() != 0)
					&& n.equals(o.getObject());
		}

	}

	// the item holder that contains this SecuredSeq.
	private final ItemHolder<? extends Seq, ? extends SecuredSeq> holder;

	/**
	 * Constructor.
	 * 
	 * @param securityEvaluator
	 *            The security evaluator to use.
	 * @param graphIRI
	 *            The graph IRI to be verified against.
	 * @param holder
	 *            The item holder that will contain this SecuredSeq.
	 */
	protected SecuredSeqImpl( final SecuredModel securedModel,
			final ItemHolder<? extends Seq, ? extends SecuredSeq> holder)
	{
		super(securedModel, holder);
		this.holder = holder;
	}

	@Override
	public SecuredSeq add( final int index, final boolean o )
	{
		checkUpdate();
		final Literal l = holder.getBaseItem().getModel().createTypedLiteral(o);
		checkCreate(index, l);
		holder.getBaseItem().add(index, o);
		return holder.getSecuredItem();
	}

	@Override
	public SecuredSeq add( final int index, final char o )
	{
		checkUpdate();
		final Literal l = holder.getBaseItem().getModel().createTypedLiteral(o);
		checkCreate(index, l);
		holder.getBaseItem().add(index, o);
		return holder.getSecuredItem();
	}

	@Override
	public SecuredSeq add( final int index, final double o )
	{
		checkUpdate();
		final Literal l = holder.getBaseItem().getModel().createTypedLiteral(o);
		checkCreate(index, l);
		holder.getBaseItem().add(index, o);
		return holder.getSecuredItem();
	}

	@Override
	public SecuredSeq add( final int index, final float o )
	{
		checkUpdate();
		final Literal l = holder.getBaseItem().getModel().createTypedLiteral(o);
		checkCreate(index, l);
		holder.getBaseItem().add(index, o);
		return holder.getSecuredItem();
	}

	@Override
	public SecuredSeq add( final int index, final long o )
	{
		checkUpdate();
		final Literal l = holder.getBaseItem().getModel().createTypedLiteral(o);
		checkCreate(index, l);
		holder.getBaseItem().add(index, o);
		return holder.getSecuredItem();
	}

	@Override
	public SecuredSeq add( final int index, final Object o )
	{
		checkUpdate();
		final Literal l = holder.getBaseItem().getModel().createTypedLiteral(o);
		checkCreate(index, l);
		holder.getBaseItem().add(index, o);
		return holder.getSecuredItem();
	}

	@Override
	public SecuredSeq add( final int index, final RDFNode o )
	{
		checkUpdate();
		final Literal l = holder.getBaseItem().getModel().createTypedLiteral(o);
		checkCreate(index, l);
		holder.getBaseItem().add(index, o);
		return holder.getSecuredItem();
	}

	@Override
	public SecuredSeq add( final int index, final String o )
	{
		checkUpdate();
		final Literal l = holder.getBaseItem().getModel().createTypedLiteral(o);
		checkCreate(index, l);
		holder.getBaseItem().add(index, o);
		return holder.getSecuredItem();
	}

	@Override
	public SecuredSeq add( final int index, final String o, final String l )
	{
		checkUpdate();
		final Literal l2 = holder.getBaseItem().getModel().createLiteral(o, l);
		checkCreate(index, l2);
		holder.getBaseItem().add(index, o, l);
		return holder.getSecuredItem();
	}

	private void checkCreate( final int index, final Literal l )
	{
		checkCreate(new Triple(holder.getBaseItem().asNode(), RDF.li(index)
				.asNode(), l.asNode()));
	}

	private Statement containerIndexOf( final RDFNode n )
	{
		final ExtendedIterator<Statement> iter = listProperties().filterKeep(
				new RDFNodeFilter(n));
		try
		{
			if (iter.hasNext())
			{
				return iter.next();
			}
			else
			{
				return null;
			}
		}
		finally
		{
			iter.close();
		}
	}

	@Override
	public SecuredAlt getAlt( final int index )
	{
		checkRead();
		final Alt a = holder.getBaseItem().getAlt(index);
		checkRead(new Triple(holder.getBaseItem().asNode(), RDF.li(index)
				.asNode(), a.asNode()));
		return SecuredAltImpl.getInstance(
				getModel(), a);
	}

	@Override
	public SecuredBag getBag( final int index )
	{
		checkRead();
		final Bag b = holder.getBaseItem().getBag(index);
		checkRead(new Triple(holder.getBaseItem().asNode(), RDF.li(index)
				.asNode(), b.asNode()));
		return SecuredBagImpl.getInstance(
				getModel(), b);
	}

	@Override
	public boolean getBoolean( final int index )
	{
		checkRead();
		checkRead(new Triple(holder.getBaseItem().asNode(), RDF.li(index)
				.asNode(), Node.ANY));
		return holder.getBaseItem().getBoolean(index);
	}

	@Override
	public byte getByte( final int index )
	{
		checkRead();
		checkRead(new Triple(holder.getBaseItem().asNode(), RDF.li(index)
				.asNode(), Node.ANY));
		return holder.getBaseItem().getByte(index);
	}

	@Override
	public char getChar( final int index )
	{
		checkRead();
		checkRead(new Triple(holder.getBaseItem().asNode(), RDF.li(index)
				.asNode(), Node.ANY));
		return holder.getBaseItem().getChar(index);

	}

	@Override
	public double getDouble( final int index )
	{
		checkRead();
		checkRead(new Triple(holder.getBaseItem().asNode(), RDF.li(index)
				.asNode(), Node.ANY));
		return holder.getBaseItem().getDouble(index);
	}

	@Override
	public float getFloat( final int index )
	{
		checkRead();
		checkRead(new Triple(holder.getBaseItem().asNode(), RDF.li(index)
				.asNode(), Node.ANY));
		return holder.getBaseItem().getFloat(index);
	}

	@Override
	public int getInt( final int index )
	{
		checkRead();
		checkRead(new Triple(holder.getBaseItem().asNode(), RDF.li(index)
				.asNode(), Node.ANY));
		return holder.getBaseItem().getInt(index);

	}

	@Override
	public String getLanguage( final int index )
	{
		checkRead();
		checkRead(new Triple(holder.getBaseItem().asNode(), RDF.li(index)
				.asNode(), Node.ANY));
		return holder.getBaseItem().getLanguage(index);
	}

	@Override
	public SecuredLiteral getLiteral( final int index )
	{
		checkRead();
		checkRead(new Triple(holder.getBaseItem().asNode(), RDF.li(index)
				.asNode(), Node.ANY));
		return SecuredLiteralImpl.getInstance(
				getModel(), holder.getBaseItem().getLiteral(index));
	}

	@Override
	public long getLong( final int index )
	{
		checkRead();
		checkRead(new Triple(holder.getBaseItem().asNode(), RDF.li(index)
				.asNode(), Node.ANY));
		return holder.getBaseItem().getLong(index);
	}

	@Override
	public SecuredRDFNode getObject( final int index )
	{
		checkRead();
		checkRead(new Triple(holder.getBaseItem().asNode(), RDF.li(index)
				.asNode(), Node.ANY));
		return SecuredRDFNodeImpl.getInstance(
				getModel(), holder.getBaseItem().getObject(index));
	}

	@Override
	public SecuredResource getResource( final int index )
	{
		checkRead();
		checkRead(new Triple(holder.getBaseItem().asNode(), RDF.li(index)
				.asNode(), Node.ANY));
		return SecuredResourceImpl.getInstance(
				getModel(), holder.getBaseItem().getResource(index));
	}

	@Override
	@Deprecated
	public SecuredResource getResource( final int index, final ResourceF f )
	{
		checkRead();
		checkRead(new Triple(holder.getBaseItem().asNode(), RDF.li(index)
				.asNode(), Node.ANY));
		return SecuredResourceImpl.getInstance(
				getModel(), holder.getBaseItem().getResource(index, f));
	}

	@Override
	public SecuredSeq getSeq( final int index )
	{
		checkRead();
		checkRead(new Triple(holder.getBaseItem().asNode(), RDF.li(index)
				.asNode(), Node.ANY));
		return getInstance(
				getModel(), holder.getBaseItem().getSeq(index));
	}

	@Override
	public short getShort( final int index )
	{
		checkRead();
		checkRead(new Triple(holder.getBaseItem().asNode(), RDF.li(index)
				.asNode(), Node.ANY));
		return holder.getBaseItem().getShort(index);
	}

	@Override
	public String getString( final int index )
	{
		checkRead();
		checkRead(new Triple(holder.getBaseItem().asNode(), RDF.li(index)
				.asNode(), Node.ANY));
		return holder.getBaseItem().getString(index);
	}

	@Override
	public int indexOf( final boolean o )
	{
		checkRead();
		final Statement stmt = containerIndexOf(holder.getBaseItem().getModel()
				.createTypedLiteral(o));
		if (stmt == null)
		{
			checkRead(new Triple(holder.getBaseItem().asNode(), RDF.li(1)
					.asNode(), Node.ANY));
			return 0;
		}
		return stmt.getPredicate().getOrdinal();
	}

	@Override
	public int indexOf( final char o )
	{
		checkRead();
		final Statement stmt = containerIndexOf(holder.getBaseItem().getModel()
				.createTypedLiteral(o));
		if (stmt == null)
		{
			checkRead(new Triple(holder.getBaseItem().asNode(), RDF.li(1)
					.asNode(), Node.ANY));
			return 0;
		}
		return stmt.getPredicate().getOrdinal();
	}

	@Override
	public int indexOf( final double o )
	{
		checkRead();
		final Statement stmt = containerIndexOf(holder.getBaseItem().getModel()
				.createTypedLiteral(o));
		if (stmt == null)
		{
			checkRead(new Triple(holder.getBaseItem().asNode(), RDF.li(1)
					.asNode(), Node.ANY));
			return 0;
		}
		return stmt.getPredicate().getOrdinal();
	}

	@Override
	public int indexOf( final float o )
	{
		checkRead();
		final Statement stmt = containerIndexOf(holder.getBaseItem().getModel()
				.createTypedLiteral(o));
		if (stmt == null)
		{
			checkRead(new Triple(holder.getBaseItem().asNode(), RDF.li(1)
					.asNode(), Node.ANY));
			return 0;
		}
		return stmt.getPredicate().getOrdinal();
	}

	@Override
	public int indexOf( final long o )
	{
		checkRead();
		final Statement stmt = containerIndexOf(holder.getBaseItem().getModel()
				.createTypedLiteral(o));
		if (stmt == null)
		{
			checkRead(new Triple(holder.getBaseItem().asNode(), RDF.li(1)
					.asNode(), Node.ANY));
			return 0;
		}
		return stmt.getPredicate().getOrdinal();
	}

	@Override
	public int indexOf( final Object o )
	{
		checkRead();
		final Statement stmt = containerIndexOf(holder.getBaseItem().getModel()
				.createTypedLiteral(o));
		if (stmt == null)
		{
			checkRead(new Triple(holder.getBaseItem().asNode(), RDF.li(1)
					.asNode(), Node.ANY));
			return 0;
		}
		return stmt.getPredicate().getOrdinal();
	}

	@Override
	public int indexOf( final RDFNode o )
	{
		checkRead();
		final Statement stmt = containerIndexOf(o);
		if (stmt == null)
		{
			checkRead(new Triple(holder.getBaseItem().asNode(), RDF.li(1)
					.asNode(), Node.ANY));
			return 0;
		}
		return stmt.getPredicate().getOrdinal();
	}

	@Override
	public int indexOf( final String o )
	{
		checkRead();
		final Statement stmt = containerIndexOf(holder.getBaseItem().getModel()
				.createTypedLiteral(o));
		if (stmt == null)
		{
			checkRead(new Triple(holder.getBaseItem().asNode(), RDF.li(1)
					.asNode(), Node.ANY));
			return 0;
		}
		return stmt.getPredicate().getOrdinal();
	}

	@Override
	public int indexOf( final String o, final String l )
	{
		checkRead();
		final Statement stmt = containerIndexOf(holder.getBaseItem().getModel()
				.createLiteral(o, l));
		if (stmt == null)
		{
			checkRead(new Triple(holder.getBaseItem().asNode(), RDF.li(1)
					.asNode(), Node.ANY));
			return 0;
		}
		return stmt.getPredicate().getOrdinal();
	}

	@Override
	public SecuredSeq remove( final int index )
	{
		checkUpdate();
		final RDFNode rdfNode = holder.getBaseItem().getObject(index);
		if (rdfNode != null)
		{
			checkDelete(new Triple(holder.getBaseItem().asNode(), RDF.li(index)
					.asNode(), rdfNode.asNode()));
			holder.getBaseItem().remove(index);
		}
		return holder.getSecuredItem();
	}

	@Override
	public SecuredSeq set( final int index, final boolean o )
	{
		checkUpdate();
		final Literal l = holder.getBaseItem().getModel().createTypedLiteral(o);
		final Triple t2 = new Triple(holder.getBaseItem().asNode(), RDF.li(
				index).asNode(), l.asNode());
		final RDFNode rdfNode = holder.getBaseItem().getObject(index);
		if (rdfNode != null)
		{
			final Triple t1 = new Triple(holder.getBaseItem().asNode(), RDF.li(
					index).asNode(), rdfNode.asNode());
			checkUpdate(t1, t2);
		}
		else
		{
			checkCreate(t2);
		}
		holder.getBaseItem().set(index, o);
		return holder.getSecuredItem();
	}

	@Override
	public SecuredSeq set( final int index, final char o )
	{
		checkUpdate();
		final Literal l = holder.getBaseItem().getModel().createTypedLiteral(o);
		final Triple t2 = new Triple(holder.getBaseItem().asNode(), RDF.li(
				index).asNode(), l.asNode());
		final RDFNode rdfNode = holder.getBaseItem().getObject(index);
		if (rdfNode != null)
		{
			final Triple t1 = new Triple(holder.getBaseItem().asNode(), RDF.li(
					index).asNode(), rdfNode.asNode());
			checkUpdate(t1, t2);
		}
		else
		{
			checkCreate(t2);
		}
		holder.getBaseItem().set(index, o);
		return holder.getSecuredItem();
	}

	@Override
	public SecuredSeq set( final int index, final double o )
	{
		checkUpdate();
		final Literal l = holder.getBaseItem().getModel().createTypedLiteral(o);
		final Triple t2 = new Triple(holder.getBaseItem().asNode(), RDF.li(
				index).asNode(), l.asNode());
		final RDFNode rdfNode = holder.getBaseItem().getObject(index);
		if (rdfNode != null)
		{
			final Triple t1 = new Triple(holder.getBaseItem().asNode(), RDF.li(
					index).asNode(), rdfNode.asNode());
			checkUpdate(t1, t2);
		}
		else
		{
			checkCreate(t2);
		}
		holder.getBaseItem().set(index, o);
		return holder.getSecuredItem();
	}

	@Override
	public SecuredSeq set( final int index, final float o )
	{
		checkUpdate();
		final Literal l = holder.getBaseItem().getModel().createTypedLiteral(o);
		final Triple t2 = new Triple(holder.getBaseItem().asNode(), RDF.li(
				index).asNode(), l.asNode());
		final RDFNode rdfNode = holder.getBaseItem().getObject(index);
		if (rdfNode != null)
		{
			final Triple t1 = new Triple(holder.getBaseItem().asNode(), RDF.li(
					index).asNode(), rdfNode.asNode());
			checkUpdate(t1, t2);
		}
		else
		{
			checkCreate(t2);
		}
		holder.getBaseItem().set(index, o);
		return holder.getSecuredItem();
	}

	@Override
	public SecuredSeq set( final int index, final long o )
	{
		checkUpdate();
		final Literal l = holder.getBaseItem().getModel().createTypedLiteral(o);
		final Triple t2 = new Triple(holder.getBaseItem().asNode(), RDF.li(
				index).asNode(), l.asNode());
		final RDFNode rdfNode = holder.getBaseItem().getObject(index);
		if (rdfNode != null)
		{
			final Triple t1 = new Triple(holder.getBaseItem().asNode(), RDF.li(
					index).asNode(), rdfNode.asNode());
			checkUpdate(t1, t2);
		}
		else
		{
			checkCreate(t2);
		}
		holder.getBaseItem().set(index, o);
		return holder.getSecuredItem();
	}

	@Override
	public SecuredSeq set( final int index, final Object o )
	{
		checkUpdate();
		final Literal l = holder.getBaseItem().getModel().createTypedLiteral(o);
		final Triple t2 = new Triple(holder.getBaseItem().asNode(), RDF.li(
				index).asNode(), l.asNode());
		final RDFNode rdfNode = holder.getBaseItem().getObject(index);
		if (rdfNode != null)
		{
			final Triple t1 = new Triple(holder.getBaseItem().asNode(), RDF.li(
					index).asNode(), rdfNode.asNode());
			checkUpdate(t1, t2);
		}
		else
		{
			checkCreate(t2);
		}
		holder.getBaseItem().set(index, o);
		return holder.getSecuredItem();
	}

	@Override
	public SecuredSeq set( final int index, final RDFNode o )
	{
		checkUpdate();
		final Triple t2 = new Triple(holder.getBaseItem().asNode(), RDF.li(
				index).asNode(), o.asNode());
		final RDFNode rdfNode = holder.getBaseItem().getObject(index);
		if (rdfNode != null)
		{
			final Triple t1 = new Triple(holder.getBaseItem().asNode(), RDF.li(
					index).asNode(), rdfNode.asNode());
			checkUpdate(t1, t2);
		}
		else
		{
			checkCreate(t2);
		}
		holder.getBaseItem().set(index, o);
		return holder.getSecuredItem();
	}

	@Override
	public SecuredSeq set( final int index, final String o )
	{
		checkUpdate();
		final Literal l = holder.getBaseItem().getModel().createTypedLiteral(o);
		final Triple t2 = new Triple(holder.getBaseItem().asNode(), RDF.li(
				index).asNode(), l.asNode());
		final RDFNode rdfNode = holder.getBaseItem().getObject(index);
		if (rdfNode != null)
		{
			final Triple t1 = new Triple(holder.getBaseItem().asNode(), RDF.li(
					index).asNode(), rdfNode.asNode());
			checkUpdate(t1, t2);
		}
		else
		{
			checkCreate(t2);
		}
		holder.getBaseItem().set(index, o);
		return holder.getSecuredItem();
	}

	@Override
	public SecuredSeq set( final int index, final String o, final String l )
	{
		checkUpdate();
		final Literal l2 = holder.getBaseItem().getModel().createLiteral(o, l);
		final Triple t2 = new Triple(holder.getBaseItem().asNode(), RDF.li(
				index).asNode(), l2.asNode());
		final RDFNode rdfNode = holder.getBaseItem().getObject(index);
		if (rdfNode != null)
		{
			final Triple t1 = new Triple(holder.getBaseItem().asNode(), RDF.li(
					index).asNode(), rdfNode.asNode());
			checkUpdate(t1, t2);
		}
		else
		{
			checkCreate(t2);
		}
		holder.getBaseItem().set(index, o);
		return holder.getSecuredItem();
	}

	/**
	 * get a SecuredSeq.
	 * 
	 * @param securedItem
	 *            The secured item that provides the security context
	 * @param seq
	 *            The Seq to secure.
	 * @return the SecuredSeq
	 */
	static SecuredSeq getInstance( final SecuredModel securedModel, final Seq seq )
	{
		if (securedModel == null)
		{
			throw new IllegalArgumentException( "Secured model may not be null");
		}
		if (seq == null)
		{
			throw new IllegalArgumentException( "Seq may not be null");
		}
		final ItemHolder<Seq, SecuredSeq> holder = new ItemHolder<Seq, SecuredSeq>(
				seq);
		final SecuredSeqImpl checker = new SecuredSeqImpl(securedModel,
				holder);
		// if we are going to create a duplicate proxy, just return this
		// one.
		if (seq instanceof SecuredSeq)
		{
			if (checker.isEquivalent((SecuredSeq) seq))
			{
				return (SecuredSeq) seq;
			}
		}
		return holder.setSecuredItem(new SecuredItemInvoker(seq.getClass(),
				checker));
	}
}
