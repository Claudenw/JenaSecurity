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
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.RSIterator;
import com.hp.hpl.jena.rdf.model.ReifiedStatement;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceF;
import com.hp.hpl.jena.rdf.model.Seq;
import com.hp.hpl.jena.rdf.model.Statement;

import org.xenei.jena.server.security.ItemHolder;
import org.xenei.jena.server.security.SecuredItem;
import org.xenei.jena.server.security.SecuredItemImpl;
import org.xenei.jena.server.security.SecuredItemInvoker;
import org.xenei.jena.server.security.SecurityEvaluator;
import org.xenei.jena.server.security.model.SecuredLiteral;
import org.xenei.jena.server.security.model.SecuredModel;
import org.xenei.jena.server.security.model.SecuredRDFNode;
import org.xenei.jena.server.security.model.SecuredStatement;

/**
 * Implementation of SecuredStatement to be used by a SecuredItemInvoker proxy.
 */
public class SecuredStatementImpl extends SecuredItemImpl implements
		SecuredStatement
{
	// the item holder that contains this SecuredStatement.
	private final ItemHolder<Statement, SecuredStatement> holder;
	private final SecuredModel securedModel;

	/**
	 * Constructor.
	 * 
	 * @param securityEvaluator
	 *            The security evaluator to use.
	 * @param graphIRI
	 *            the graph IRI to verify against.
	 * @param holder
	 *            The item holder that will contain this SecuredStatement.
	 */
	private SecuredStatementImpl( final SecuredModel securedModel,
			final ItemHolder<Statement, SecuredStatement> holder)
	{
		super(securedModel, holder);
		this.holder = holder;
		this.securedModel = securedModel;
	}

	@Override
	public Triple asTriple()
	{
		checkRead();
		final Triple retval = holder.getBaseItem().asTriple();
		checkRead(retval);
		return retval;
	}

	@Override
	public boolean canCreate()
	{
		return super.canCreate() ? canCreate(holder.getBaseItem()) : false;
	}

	@Override
	public boolean canDelete()
	{
		return super.canDelete() ? canDelete(holder.getBaseItem()) : false;
	}

	@Override
	public boolean canRead()
	{
		return super.canRead() ? canRead(holder.getBaseItem()) : false;
	}

	@Override
	public Statement changeLiteralObject( final boolean o )
	{
		checkUpdate();
		final Triple base = holder.getBaseItem().asTriple();
		final Triple newBase = getNewTriple(base, o);
		checkUpdate(base, newBase);
		return SecuredStatementImpl.getInstance(
				getModel(),holder.getBaseItem().changeLiteralObject(o));
	}

	@Override
	public Statement changeLiteralObject( final char o )
	{
		checkUpdate();
		final Triple base = holder.getBaseItem().asTriple();
		final Triple newBase = getNewTriple(base, o);
		checkUpdate(base, newBase);
		return SecuredStatementImpl.getInstance(
				getModel(),holder.getBaseItem().changeLiteralObject(o));
	}

	@Override
	public Statement changeLiteralObject( final double o )
	{
		checkUpdate();
		final Triple base = holder.getBaseItem().asTriple();
		final Triple newBase = getNewTriple(base, o);
		checkUpdate(base, newBase);
		return SecuredStatementImpl.getInstance(
				getModel(),holder.getBaseItem().changeLiteralObject(o));
	}

	@Override
	public Statement changeLiteralObject( final float o )
	{
		checkUpdate();
		final Triple base = holder.getBaseItem().asTriple();
		final Triple newBase = getNewTriple(base, o);
		checkUpdate(base, newBase);
		return SecuredStatementImpl.getInstance(
				getModel(),holder.getBaseItem().changeLiteralObject(o));
	}

	@Override
	public Statement changeLiteralObject( final int o )
	{
		checkUpdate();
		final Triple base = holder.getBaseItem().asTriple();
		final Triple newBase = getNewTriple(base, o);
		checkUpdate(base, newBase);
		return SecuredStatementImpl.getInstance(
				getModel(),holder.getBaseItem().changeLiteralObject(o));
	}

	@Override
	public Statement changeLiteralObject( final long o )
	{
		checkUpdate();
		final Triple base = holder.getBaseItem().asTriple();
		final Triple newBase = getNewTriple(base, o);
		checkUpdate(base, newBase);
		return SecuredStatementImpl.getInstance(
				getModel(),holder.getBaseItem().changeLiteralObject(o));
	}

	@Override
	public Statement changeObject( final RDFNode o )
	{
		checkUpdate();
		final Triple base = holder.getBaseItem().asTriple();
		final Triple newBase = new Triple(base.getSubject(),
				base.getPredicate(), o.asNode());
		checkUpdate(base, newBase);
		return SecuredStatementImpl.getInstance(
				getModel(),holder.getBaseItem().changeObject(o));
	}

	@Override
	public Statement changeObject( final String o )
	{
		checkUpdate();
		final Triple base = holder.getBaseItem().asTriple();
		final Triple newBase = getNewTriple(base, o);
		checkUpdate(base, newBase);
		return SecuredStatementImpl.getInstance(
				getModel(),holder.getBaseItem().changeObject(o));
	}

	@Override
	public Statement changeObject( final String o, final boolean wellFormed )
	{
		checkUpdate();
		final Triple base = holder.getBaseItem().asTriple();
		final Triple newBase = new Triple(base.getSubject(),
				base.getPredicate(), Node.createLiteral(o, "", wellFormed));
		checkUpdate(base, newBase);
		return SecuredStatementImpl.getInstance(
				getModel(),holder.getBaseItem().changeObject(o));
	}

	@Override
	public Statement changeObject( final String o, final String l )
	{
		checkUpdate();
		final Triple base = holder.getBaseItem().asTriple();
		final Triple newBase = new Triple(base.getSubject(),
				base.getPredicate(), Node.createLiteral(o, l, false));
		checkUpdate(base, newBase);
		return SecuredStatementImpl.getInstance(
				getModel(),holder.getBaseItem().changeObject(o, l));
	}

	@Override
	public Statement changeObject( final String o, final String l,
			final boolean wellFormed )
	{
		checkUpdate();
		final Triple base = holder.getBaseItem().asTriple();
		final Triple newBase = new Triple(base.getSubject(),
				base.getPredicate(), Node.createLiteral(o, l, wellFormed));
		checkUpdate(base, newBase);
		return SecuredStatementImpl.getInstance(
				getModel(),holder.getBaseItem().changeObject(o, l, wellFormed));
	}

	@Override
	public ReifiedStatement createReifiedStatement()
	{
		checkUpdate();
		checkCreateReified(null,
				SecuredItemImpl.convert(holder.getBaseItem().asTriple()));
		return SecuredReifiedStatementImpl.getInstance(
				getModel(),holder.getBaseItem().createReifiedStatement());
	}

	@Override
	public ReifiedStatement createReifiedStatement( final String uri )
	{
		checkUpdate();
		checkCreateReified(uri,
				SecuredItemImpl.convert(holder.getBaseItem().asTriple()));
		return SecuredReifiedStatementImpl.getInstance(
				getModel(),holder.getBaseItem().createReifiedStatement(uri));
	}

	@Override
	public Alt getAlt()
	{
		checkRead();
		checkRead(holder.getBaseItem().asTriple());
		return SecuredAltImpl.getInstance(
				getModel(),holder.getBaseItem().getAlt());
	}

	@Override
	public Bag getBag()
	{
		checkRead();
		checkRead(holder.getBaseItem().asTriple());
		return SecuredBagImpl.getInstance(
				getModel(),holder.getBaseItem().getBag());
	}

	@Override
	public boolean getBoolean()
	{
		checkRead();
		checkRead(holder.getBaseItem().asTriple());
		return holder.getBaseItem().getBoolean();
	}

	@Override
	public byte getByte()
	{
		checkRead();
		checkRead(holder.getBaseItem().asTriple());
		return holder.getBaseItem().getByte();
	}

	@Override
	public char getChar()
	{
		checkRead();
		checkRead(holder.getBaseItem().asTriple());
		return holder.getBaseItem().getChar();

	}

	@Override
	public double getDouble()
	{
		checkRead();
		checkRead(holder.getBaseItem().asTriple());
		return holder.getBaseItem().getDouble();
	}

	@Override
	public float getFloat()
	{
		checkRead();
		checkRead(holder.getBaseItem().asTriple());
		return holder.getBaseItem().getFloat();
	}

	@Override
	public int getInt()
	{
		checkRead();
		checkRead(holder.getBaseItem().asTriple());
		return holder.getBaseItem().getInt();
	}

	@Override
	public String getLanguage()
	{
		checkRead();
		checkRead(holder.getBaseItem().asTriple());
		return holder.getBaseItem().getLiteral().getLanguage();
	}

	@Override
	public SecuredLiteral getLiteral()
	{
		checkRead();
		checkRead(holder.getBaseItem().asTriple());
		return SecuredLiteralImpl.getInstance(
				getModel(), holder.getBaseItem().getLiteral() );
	}

	@Override
	public long getLong()
	{
		checkRead();
		checkRead(holder.getBaseItem().asTriple());
		return holder.getBaseItem().getLong();
	}

	@Override
	public SecuredModel getModel()
	{
		return securedModel;
	}

	private Triple getNewTriple( final Triple t, final Object o )
	{
		return new Triple(t.getSubject(), t.getPredicate(), Node.createLiteral(
				String.valueOf(o), "", false));
	}

	@Override
	public SecuredRDFNode getObject()
	{
		checkRead();
		checkRead(holder.getBaseItem().asTriple());
		final RDFNode rdfNode = holder.getBaseItem().getObject();
		return SecuredRDFNodeImpl.getInstance(getModel(),rdfNode);
		
	}

	@Override
	public Property getPredicate()
	{
		checkRead();
		checkRead(holder.getBaseItem().asTriple());
		return SecuredPropertyImpl.getInstance(
				getModel(),holder.getBaseItem().getPredicate());
	}

	@Override
	public Statement getProperty( final Property p )
	{
		checkRead();
		checkRead(holder.getBaseItem().asTriple());
		return SecuredStatementImpl.getInstance(
				getModel(),holder.getBaseItem().getProperty(p));
	}

	@Override
	public Resource getResource()
	{
		checkRead();
		checkRead(holder.getBaseItem().asTriple());
		return SecuredResourceImpl.getInstance(
				getModel(),holder.getBaseItem().getResource() );
	}

	@Override
	@Deprecated
	public Resource getResource( final ResourceF f )
	{
		checkRead();
		checkRead(holder.getBaseItem().asTriple());
		return SecuredResourceImpl.getInstance(
				getModel(),holder.getBaseItem().getResource(f));
	}

	@Override
	public Seq getSeq()
	{
		checkRead();
		checkRead(holder.getBaseItem().asTriple());
		return SecuredSeqImpl.getInstance(
				getModel(),holder.getBaseItem().getSeq());
	}

	@Override
	public short getShort()
	{
		checkRead();
		checkRead(holder.getBaseItem().asTriple());
		return holder.getBaseItem().getShort();
	}

	@Override
	public Statement getStatementProperty( final Property p )
	{
		checkRead();
		checkRead(holder.getBaseItem().asTriple());
		return SecuredStatementImpl.getInstance(
				getModel(),holder.getBaseItem().getStatementProperty(p));
	}

	@Override
	public String getString()
	{
		checkRead();
		checkRead(holder.getBaseItem().asTriple());
		return holder.getBaseItem().getString();
	}

	@Override
	public Resource getSubject()
	{
		checkRead();
		checkRead(holder.getBaseItem().asTriple());
		return SecuredResourceImpl.getInstance(
				getModel(),holder.getBaseItem().getSubject());
	}

	@Override
	public boolean hasWellFormedXML()
	{
		checkRead();
		checkRead(holder.getBaseItem().asTriple());
		return holder.getBaseItem().getLiteral().isWellFormedXML();
	}

	@Override
	public boolean isReified()
	{
		checkRead();
		checkRead(holder.getBaseItem().asTriple());
		return holder.getBaseItem().isReified();
	}

	@Override
	public RSIterator listReifiedStatements()
	{
		checkRead();
		checkRead(holder.getBaseItem().asTriple());
		return new SecuredRSIterator(getModel(),holder.getBaseItem()
				.listReifiedStatements());
	}

	@Override
	public Statement remove()
	{
		checkDelete();
		checkDelete(holder.getBaseItem());
		return holder.getSecuredItem();
	}

	@Override
	public void removeReification()
	{
		checkUpdate();
		holder.getBaseItem().removeReification();
	}

	/**
	 * get a SecuredStatement
	 * 
	 * @param securedItem
	 *            the secured item that provides the security context.
	 * @param stmt
	 *            The statement to secure.
	 * @return the SecuredStatement
	 */
	static SecuredStatement getInstance( final SecuredModel securedModel,
			final Statement stmt )
	{
		if (securedModel == null)
		{
			throw new IllegalArgumentException( "Secured model may not be null");
		}
		if (stmt == null)
		{
			throw new IllegalArgumentException( "Statement may not be null");
		}
		
		final ItemHolder<Statement, SecuredStatement> holder = new ItemHolder<Statement, SecuredStatement>(
				stmt);
	
		final SecuredStatementImpl checker = new SecuredStatementImpl(securedModel,
				holder);
		// if we are going to create a duplicate proxy, just return this
		// one.
		if (stmt instanceof SecuredStatement)
		{
			if (checker.isEquivalent((SecuredStatement) stmt))
			{
				return (SecuredStatement) stmt;
			}
		}
		return holder.setSecuredItem(new SecuredItemInvoker(holder
				.getBaseItem().getClass(), checker));
	}

}
