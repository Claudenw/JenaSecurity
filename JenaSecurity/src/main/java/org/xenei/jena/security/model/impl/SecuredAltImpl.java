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
package org.xenei.jena.security.model.impl;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Alt;
import com.hp.hpl.jena.rdf.model.AltHasNoDefaultException;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFList;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResourceF;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.Map1;
import com.hp.hpl.jena.vocabulary.RDF;

import org.xenei.jena.security.ItemHolder;
import org.xenei.jena.security.SecuredItem;
import org.xenei.jena.security.SecuredItemInvoker;
import org.xenei.jena.security.SecurityEvaluator;
import org.xenei.jena.security.SecurityEvaluator.Action;
import org.xenei.jena.security.model.SecuredAlt;
import org.xenei.jena.security.model.SecuredBag;
import org.xenei.jena.security.model.SecuredLiteral;
import org.xenei.jena.security.model.SecuredModel;
import org.xenei.jena.security.model.SecuredRDFNode;
import org.xenei.jena.security.model.SecuredResource;
import org.xenei.jena.security.model.SecuredSeq;

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
	
	private Statement getDefaultStatement()
	{
		checkRead();
		ExtendedIterator<Statement> iter = getStatementIterator( Action.Read );
		try {
			if (iter.hasNext())
			{
				return iter.next();
			}
			throw new AltHasNoDefaultException( this );
		}
		finally {
			iter.close();
		}	
	}

	@Override
	public SecuredRDFNode getDefault()
	{
		// getDefaultStatement() calls checkRead
		return SecuredRDFNodeImpl.getInstance(getModel(), getDefaultStatement().getObject() );
	}

	@Override
	public SecuredAlt getDefaultAlt()
	{
		// getDefaultStatement() calls checkRead
		return SecuredAltImpl.getInstance(getModel(), getDefaultStatement().getAlt() );
	}

	@Override
	public SecuredBag getDefaultBag()
	{
		// getDefaultStatement() calls checkRead
		return SecuredBagImpl.getInstance(getModel(), getDefaultStatement().getBag());
	}

	@Override
	public boolean getDefaultBoolean()
	{
		// getDefaultStatement() calls checkRead
		return getDefaultStatement().getBoolean();
	}

	@Override
	public byte getDefaultByte()
	{
		// getDefaultStatement() calls checkRead
		return getDefaultStatement().getByte();
	}

	@Override
	public char getDefaultChar()
	{
		// getDefaultStatement() calls checkRead
		return getDefaultStatement().getChar();
	}

	@Override
	public double getDefaultDouble()
	{
		// getDefaultStatement() calls checkRead
		return getDefaultStatement().getDouble();
	}

	@Override
	public float getDefaultFloat()
	{
		// getDefaultStatement() calls checkRead
		return getDefaultStatement().getFloat();
	}

	@Override
	public int getDefaultInt()
	{
		// getDefaultStatement() calls checkRead
		return getDefaultStatement().getInt();
	}

	@Override
	public String getDefaultLanguage()
	{
		// getDefaultStatement() calls checkRead
		return getDefaultStatement().getLanguage();
	}

	@Override
	public SecuredLiteral getDefaultLiteral()
	{
		// getDefaultStatement() calls checkRead
		return SecuredLiteralImpl.getInstance( getModel(), getDefaultStatement().getLiteral());
	}

	@Override
	public long getDefaultLong()
	{
		// getDefaultStatement() calls checkRead
		return getDefaultStatement().getLong();
	}

	@Override
	public SecuredResource getDefaultResource()
	{
		// getDefaultStatement() calls checkRead
		return SecuredResourceImpl.getInstance( getModel(), getDefaultStatement().getResource());
	}

	@Override
	@Deprecated
	public SecuredResource getDefaultResource( final ResourceF f )
	{
		// getDefaultStatement() calls checkRead
		return SecuredResourceImpl.getInstance( getModel(), getDefaultStatement().getResource( f ));
	}

	@Override
	public SecuredSeq getDefaultSeq()
	{
		// getDefaultStatement() calls checkRead
		return SecuredSeqImpl.getInstance( getModel(), getDefaultStatement().getSeq());
	}

	@Override
	public short getDefaultShort()
	{
		// getDefaultStatement() calls checkRead
		return getDefaultStatement().getShort();

	}

	@Override
	public String getDefaultString()
	{
		// getDefaultStatement() calls checkRead
		return getDefaultStatement().getString();

	}

	/*private Triple getDefaultTriple()
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
*/
	@Override
	public SecuredAlt setDefault( final boolean o )
	{
		checkUpdate();
		Statement stmt = getDefaultStatement();
		Triple t = stmt.asTriple();		
		Triple t2 = new Triple( t.getSubject(), t.getPredicate(), holder.getBaseItem().getModel().createTypedLiteral(o).asNode());
		checkUpdate( t, t2 );
		stmt.changeLiteralObject(o);
		return holder.getSecuredItem();
	}

	@Override
	public SecuredAlt setDefault( final char o )
	{
		checkUpdate();
		Statement stmt = getDefaultStatement();
		Triple t = stmt.asTriple();		
		Triple t2 = new Triple( t.getSubject(), t.getPredicate(), holder.getBaseItem().getModel().createTypedLiteral(o).asNode());
		checkUpdate( t, t2 );
		stmt.changeLiteralObject(o);
		return holder.getSecuredItem();
	}

	@Override
	public SecuredAlt setDefault( final double o )
	{
		checkUpdate();
		Statement stmt = getDefaultStatement();
		Triple t = stmt.asTriple();		
		Triple t2 = new Triple( t.getSubject(), t.getPredicate(), holder.getBaseItem().getModel().createTypedLiteral(o).asNode());
		checkUpdate( t, t2 );
		stmt.changeLiteralObject(o);
		return holder.getSecuredItem();
	}

	@Override
	public SecuredAlt setDefault( final float o )
	{
		checkUpdate();
		Statement stmt = getDefaultStatement();
		Triple t = stmt.asTriple();		
		Triple t2 = new Triple( t.getSubject(), t.getPredicate(), holder.getBaseItem().getModel().createTypedLiteral(o).asNode());
		checkUpdate( t, t2 );
		stmt.changeLiteralObject(o);
		return holder.getSecuredItem();
	}

	@Override
	public SecuredAlt setDefault( final long o )
	{
		checkUpdate();
		Statement stmt = getDefaultStatement();
		Triple t = stmt.asTriple();		
		Triple t2 = new Triple( t.getSubject(), t.getPredicate(), holder.getBaseItem().getModel().createTypedLiteral(o).asNode());
		checkUpdate( t, t2 );
		stmt.changeLiteralObject(o);
		return holder.getSecuredItem();
	}

	@Override
	public SecuredAlt setDefault( final Object o )
	{
		checkUpdate();
		Statement stmt = getDefaultStatement();
		Triple t = stmt.asTriple();		
		Triple t2 = new Triple( t.getSubject(), t.getPredicate(), holder.getBaseItem().getModel().createTypedLiteral(o).asNode());
		checkUpdate( t, t2 );
		stmt.changeObject(stmt.getModel().createTypedLiteral(o));
		return holder.getSecuredItem();
	}

	@Override
	public SecuredAlt setDefault( final RDFNode o )
	{
		checkUpdate();
		Statement stmt = getDefaultStatement();
		Triple t = stmt.asTriple();		
		Triple t2 = new Triple( t.getSubject(), t.getPredicate(), o.asNode());
		checkUpdate( t, t2 );
		stmt.changeObject(o);
		return holder.getSecuredItem();
	}

	@Override
	public SecuredAlt setDefault( final String o )
	{
		checkUpdate();
		Statement stmt = getDefaultStatement();
		Triple t = stmt.asTriple();		
		Triple t2 = new Triple( t.getSubject(), t.getPredicate(), holder.getBaseItem().getModel().createTypedLiteral(o).asNode());
		checkUpdate( t, t2 );
		stmt.changeObject(o);
		return holder.getSecuredItem();
	}

	@Override
	public SecuredAlt setDefault( final String o, final String l )
	{
		checkUpdate();
		Statement stmt = getDefaultStatement();
		Triple t = stmt.asTriple();		
		Triple t2 = new Triple( t.getSubject(), t.getPredicate(), holder.getBaseItem().getModel().createTypedLiteral(o).asNode());
		checkUpdate( t, t2 );
		stmt.changeObject(o);
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
	public static SecuredAlt getInstance( final SecuredModel securedModel, final Alt alt )
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
