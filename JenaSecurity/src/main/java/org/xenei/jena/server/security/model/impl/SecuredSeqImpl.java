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

import com.hp.hpl.jena.rdf.model.Alt;
import com.hp.hpl.jena.rdf.model.Bag;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceF;
import com.hp.hpl.jena.rdf.model.Seq;

import org.xenei.jena.server.security.ItemHolder;
import org.xenei.jena.server.security.SecurityEvaluator;
import org.xenei.jena.server.security.model.SecuredSeq;

/**
 * Implementation of SecuredSeq to be used by a SecuredItemInvoker proxy.
 */
public class SecuredSeqImpl extends SecuredContainerImpl implements SecuredSeq
{
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
	public SecuredSeqImpl( final SecurityEvaluator securityEvaluator,
			final String graphIRI,
			final ItemHolder<? extends Seq, ? extends SecuredSeq> holder )
	{
		super(securityEvaluator, graphIRI, holder);
		this.holder = holder;
	}

	@Override
	public Seq add( final int index, final boolean o )
	{
		checkUpdate();
		holder.getBaseItem().add(index, o);
		return holder.getSecuredItem();
	}

	@Override
	public Seq add( final int index, final char o )
	{
		checkUpdate();
		holder.getBaseItem().add(index, o);
		return holder.getSecuredItem();
	}

	@Override
	public Seq add( final int index, final double o )
	{
		checkUpdate();
		holder.getBaseItem().add(index, o);
		return holder.getSecuredItem();
	}

	@Override
	public Seq add( final int index, final float o )
	{
		checkUpdate();
		holder.getBaseItem().add(index, o);
		return holder.getSecuredItem();
	}

	@Override
	public Seq add( final int index, final long o )
	{
		checkUpdate();
		holder.getBaseItem().add(index, o);
		return holder.getSecuredItem();
	}

	@Override
	public Seq add( final int index, final Object o )
	{
		checkUpdate();
		holder.getBaseItem().add(index, o);
		return holder.getSecuredItem();
	}

	@Override
	public Seq add( final int index, final RDFNode o )
	{
		checkUpdate();
		holder.getBaseItem().add(index, o);
		return holder.getSecuredItem();
	}

	@Override
	public Seq add( final int index, final String o )
	{
		checkUpdate();
		holder.getBaseItem().add(index, o);
		return holder.getSecuredItem();
	}

	@Override
	public Seq add( final int index, final String o, final String l )
	{
		checkUpdate();
		holder.getBaseItem().add(index, o, l);
		return holder.getSecuredItem();
	}

	@Override
	public Alt getAlt( final int index )
	{
		checkRead();
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().getAlt(index));
	}

	@Override
	public Bag getBag( final int index )
	{
		checkRead();
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().getBag(index));
	}

	@Override
	public boolean getBoolean( final int index )
	{
		checkRead();
		return holder.getBaseItem().getBoolean(index);
	}

	@Override
	public byte getByte( final int index )
	{
		checkRead();
		return holder.getBaseItem().getByte(index);
	}

	@Override
	public char getChar( final int index )
	{
		checkRead();
		return holder.getBaseItem().getChar(index);

	}

	@Override
	public double getDouble( final int index )
	{
		checkRead();
		return holder.getBaseItem().getDouble(index);
	}

	@Override
	public float getFloat( final int index )
	{
		checkRead();
		return holder.getBaseItem().getFloat(index);
	}

	@Override
	public int getInt( final int index )
	{
		checkRead();
		return holder.getBaseItem().getInt(index);

	}

	@Override
	public String getLanguage( final int index )
	{
		checkRead();
		return holder.getBaseItem().getLanguage(index);
	}

	@Override
	public Literal getLiteral( final int index )
	{
		checkRead();
		return holder.getBaseItem().getLiteral(index);
	}

	@Override
	public long getLong( final int index )
	{
		checkRead();
		return holder.getBaseItem().getLong(index);
	}

	@Override
	public RDFNode getObject( final int index )
	{
		checkRead();
		final RDFNode node = holder.getBaseItem().getObject(index);
		if (node.isLiteral())
		{
			return org.xenei.jena.server.security.model.impl.Factory
					.getInstance(this, node.asLiteral());
		}

		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, node.asResource());
	}

	@Override
	public Resource getResource( final int index )
	{
		checkRead();
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().getResource(index));
	}

	@Override
	@Deprecated
	public Resource getResource( final int index, final ResourceF f )
	{
		checkRead();
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().getResource(index, f));
	}

	@Override
	public Seq getSeq( final int index )
	{
		checkRead();
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().getSeq(index));
	}

	@Override
	public short getShort( final int index )
	{
		checkRead();
		return holder.getBaseItem().getShort(index);
	}

	@Override
	public String getString( final int index )
	{
		checkRead();
		return holder.getBaseItem().getString(index);
	}

	@Override
	public int indexOf( final boolean o )
	{
		checkRead();
		return holder.getBaseItem().indexOf(o);
	}

	@Override
	public int indexOf( final char o )
	{
		checkRead();
		return holder.getBaseItem().indexOf(o);
	}

	@Override
	public int indexOf( final double o )
	{
		checkRead();
		return holder.getBaseItem().indexOf(o);
	}

	@Override
	public int indexOf( final float o )
	{
		checkRead();
		return holder.getBaseItem().indexOf(o);
	}

	@Override
	public int indexOf( final long o )
	{
		checkRead();
		return holder.getBaseItem().indexOf(o);
	}

	@Override
	public int indexOf( final Object o )
	{
		checkRead();
		return holder.getBaseItem().indexOf(o);
	}

	@Override
	public int indexOf( final RDFNode o )
	{
		checkRead();
		return holder.getBaseItem().indexOf(o);
	}

	@Override
	public int indexOf( final String o )
	{
		checkRead();
		return holder.getBaseItem().indexOf(o);
	}

	@Override
	public int indexOf( final String o, final String l )
	{
		checkRead();
		return holder.getBaseItem().indexOf(o, l);
	}

	@Override
	public Seq remove( final int index )
	{
		checkUpdate();
		holder.getBaseItem().remove(index);
		return holder.getSecuredItem();
	}

	@Override
	public Seq set( final int index, final boolean o )
	{
		checkUpdate();
		holder.getBaseItem().set(index, o);
		return holder.getSecuredItem();
	}

	@Override
	public Seq set( final int index, final char o )
	{
		checkUpdate();
		holder.getBaseItem().set(index, o);
		return holder.getSecuredItem();
	}

	@Override
	public Seq set( final int index, final double o )
	{
		checkUpdate();
		holder.getBaseItem().set(index, o);
		return holder.getSecuredItem();
	}

	@Override
	public Seq set( final int index, final float o )
	{
		checkUpdate();
		holder.getBaseItem().set(index, o);
		return holder.getSecuredItem();
	}

	@Override
	public Seq set( final int index, final long o )
	{
		checkUpdate();
		holder.getBaseItem().set(index, o);
		return holder.getSecuredItem();
	}

	@Override
	public Seq set( final int index, final Object o )
	{
		checkUpdate();
		holder.getBaseItem().set(index, o);
		return holder.getSecuredItem();
	}

	@Override
	public Seq set( final int index, final RDFNode o )
	{
		checkUpdate();
		holder.getBaseItem().set(index, o);
		return holder.getSecuredItem();
	}

	@Override
	public Seq set( final int index, final String o )
	{
		checkUpdate();
		holder.getBaseItem().set(index, o);
		return holder.getSecuredItem();
	}

	@Override
	public Seq set( final int index, final String o, final String l )
	{
		checkUpdate();
		holder.getBaseItem().set(index, o, l);
		return holder.getSecuredItem();
	}

}
