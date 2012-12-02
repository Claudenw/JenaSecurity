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
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResourceF;
import com.hp.hpl.jena.rdf.model.StmtIterator;
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
 * Implementation of SecuredAlt to be used by a SecuredItemInvoker proxy.
 */
public class SecuredAltImpl extends SecuredContainerImpl implements SecuredAlt
{
	// The item holder holding this SecuredAlt
	private final ItemHolder<? extends Alt, ? extends SecuredAlt> holder;

	/**
	 * Constructor.
	 * 
	 * @param securityEvaluator
	 *            the evaluator to use.
	 * @param graphIRI
	 *            the graph to verify against.
	 * @param holder
	 *            The item holder that will hold this SecuredAlt.
	 */
	protected SecuredAltImpl( final SecuredModel securedModel,
			final ItemHolder<? extends Alt, ? extends SecuredAlt> holder)
	{
		super(securedModel, holder);
		this.holder = holder;
	}

	@Override
	public SecuredRDFNode getDefault()
	{
		checkRead();
		checkRead(getDefaultTriple());
		final RDFNode node = holder.getBaseItem().getDefault();
		return SecuredRDFNodeImpl
					.getInstance(getModel(), node.asLiteral());
	}

	@Override
	public SecuredAlt getDefaultAlt()
	{
		checkRead();
		checkRead(getDefaultTriple());
		return getInstance(
				getModel(), holder.getBaseItem().getDefaultAlt());
	}

	@Override
	public SecuredBag getDefaultBag()
	{
		checkRead();
		checkRead(getDefaultTriple());
		return SecuredBagImpl.getInstance(
				getModel(), holder.getBaseItem().getDefaultBag());
	}

	@Override
	public boolean getDefaultBoolean()
	{
		checkRead();
		checkRead(getDefaultTriple());
		return holder.getBaseItem().getDefaultBoolean();
	}

	@Override
	public byte getDefaultByte()
	{
		checkRead();
		checkRead(getDefaultTriple());
		return holder.getBaseItem().getDefaultByte();
	}

	@Override
	public char getDefaultChar()
	{
		checkRead();
		checkRead(getDefaultTriple());
		return holder.getBaseItem().getDefaultChar();
	}

	@Override
	public double getDefaultDouble()
	{
		checkRead();
		checkRead(getDefaultTriple());
		return holder.getBaseItem().getDefaultDouble();
	}

	@Override
	public float getDefaultFloat()
	{
		checkRead();
		checkRead(getDefaultTriple());
		return holder.getBaseItem().getDefaultFloat();
	}

	@Override
	public int getDefaultInt()
	{
		checkRead();
		checkRead(getDefaultTriple());
		return holder.getBaseItem().getDefaultInt();
	}

	@Override
	public String getDefaultLanguage()
	{
		checkRead();
		checkRead(getDefaultTriple());
		return holder.getBaseItem().getDefaultLanguage();
	}

	@Override
	public SecuredLiteral getDefaultLiteral()
	{
		checkRead();
		checkRead(getDefaultTriple());
		return SecuredLiteralImpl.getInstance(
				getModel(), holder.getBaseItem().getDefaultLiteral());
	}

	@Override
	public long getDefaultLong()
	{
		checkRead();
		checkRead(getDefaultTriple());
		return holder.getBaseItem().getDefaultLong();
	}

	@Override
	public SecuredResource getDefaultResource()
	{
		checkRead();
		checkRead(getDefaultTriple());
		return SecuredResourceImpl.getInstance(
				getModel(), holder.getBaseItem().getDefaultResource());
	}

	@Override
	@Deprecated
	public SecuredResource getDefaultResource( final ResourceF f )
	{
		checkRead();
		checkRead(getDefaultTriple());
		return SecuredResourceImpl.getInstance(
				getModel(), holder.getBaseItem().getDefaultResource(f));
	}

	@Override
	public SecuredSeq getDefaultSeq()
	{
		checkRead();
		checkRead(getDefaultTriple());
		return SecuredSeqImpl.getInstance(
				getModel(), holder.getBaseItem().getDefaultSeq());

	}

	@Override
	public short getDefaultShort()
	{
		checkRead();
		checkRead(getDefaultTriple());
		return holder.getBaseItem().getDefaultShort();
	}

	@Override
	public String getDefaultString()
	{
		checkRead();
		checkRead(getDefaultTriple());
		return holder.getBaseItem().getDefaultString();
	}

	private Triple getDefaultTriple()
	{
		final StmtIterator iter = holder.getBaseItem().getModel()
				.listStatements(this, RDF.li(1), (RDFNode) null);
		try
		{
			return iter.hasNext() ? iter.nextStatement().asTriple() : null;
		}
		finally
		{
			iter.close();
		}

	}

	private Triple getNewTriple( final Triple t, final Object o )
	{
		return new Triple(t.getSubject(), t.getPredicate(), Node.createLiteral(
				String.valueOf(o), "", false));
	}

	@Override
	public SecuredAlt setDefault( final boolean o )
	{
		checkUpdate();
		final Triple defaultTriple = getDefaultTriple();
		final Triple newTriple = getNewTriple(defaultTriple, o);
		checkUpdate(defaultTriple, newTriple);
		holder.getBaseItem().setDefault(o);
		return holder.getSecuredItem();
	}

	@Override
	public SecuredAlt setDefault( final char o )
	{
		checkUpdate();
		final Triple defaultTriple = getDefaultTriple();
		final Triple newTriple = getNewTriple(defaultTriple, o);
		checkUpdate(defaultTriple, newTriple);
		holder.getBaseItem().setDefault(o);
		return holder.getSecuredItem();
	}

	@Override
	public SecuredAlt setDefault( final double o )
	{
		checkUpdate();
		final Triple defaultTriple = getDefaultTriple();
		final Triple newTriple = getNewTriple(defaultTriple, o);
		checkUpdate(defaultTriple, newTriple);
		holder.getBaseItem().setDefault(o);
		return holder.getSecuredItem();
	}

	@Override
	public SecuredAlt setDefault( final float o )
	{
		checkUpdate();
		final Triple defaultTriple = getDefaultTriple();
		final Triple newTriple = getNewTriple(defaultTriple, o);
		checkUpdate(defaultTriple, newTriple);
		holder.getBaseItem().setDefault(o);
		return holder.getSecuredItem();
	}

	@Override
	public SecuredAlt setDefault( final long o )
	{
		checkUpdate();
		final Triple defaultTriple = getDefaultTriple();
		final Triple newTriple = getNewTriple(defaultTriple, o);
		checkUpdate(defaultTriple, newTriple);
		holder.getBaseItem().setDefault(o);
		return holder.getSecuredItem();
	}

	@Override
	public SecuredAlt setDefault( final Object o )
	{
		checkUpdate();
		final Triple defaultTriple = getDefaultTriple();
		final Triple newTriple = getNewTriple(defaultTriple, o);
		checkUpdate(defaultTriple, newTriple);
		holder.getBaseItem().setDefault(o);
		return holder.getSecuredItem();
	}

	@Override
	public SecuredAlt setDefault( final RDFNode o )
	{
		checkUpdate();
		final Triple defaultTriple = getDefaultTriple();
		final Triple newTriple = new Triple(defaultTriple.getSubject(),
				defaultTriple.getPredicate(), o.asNode());
		checkUpdate(defaultTriple, newTriple);
		holder.getBaseItem().setDefault(o);
		return holder.getSecuredItem();
	}

	@Override
	public SecuredAlt setDefault( final String o )
	{
		checkUpdate();
		final Triple defaultTriple = getDefaultTriple();
		final Triple newTriple = getNewTriple(defaultTriple, o);
		checkUpdate(defaultTriple, newTriple);
		holder.getBaseItem().setDefault(o);
		return holder.getSecuredItem();
	}

	@Override
	public SecuredAlt setDefault( final String o, final String l )
	{
		checkUpdate();
		final Triple defaultTriple = getDefaultTriple();
		final Triple newTriple = new Triple(defaultTriple.getSubject(),
				defaultTriple.getPredicate(), Node.createLiteral(o, l, false));
		checkUpdate(defaultTriple, newTriple);
		holder.getBaseItem().setDefault(o, l);
		return holder.getSecuredItem();
	}

	/**
	 * Get an instance of SecuredAlt.
	 * 
	 * @param securedItem
	 *            the item providing the security context.
	 * @param alt
	 *            The Alt to be secured.
	 * @return The secured Alt instance.
	 */
	static SecuredAlt getInstance( final SecuredModel securedModel, final Alt alt )
	{
		if (securedModel == null)
		{
			throw new IllegalArgumentException( "Secured model may not be null");
		}
		if (alt == null)
		{
			throw new IllegalArgumentException( "Alt may not be null");
		}
		final ItemHolder<Alt, SecuredAlt> holder = new ItemHolder<Alt, SecuredAlt>(
				alt);
		final SecuredAltImpl checker = new SecuredAltImpl(
				securedModel,
				holder);
		// if we are going to create a duplicate proxy, just return this
		// one.
		if (alt instanceof SecuredAlt)
		{
			if (checker.isEquivalent((SecuredAlt) alt))
			{
				return (SecuredAlt) alt;
			}
		}
		return holder.setSecuredItem(new SecuredItemInvoker(alt.getClass(),
				checker));
	}

}
