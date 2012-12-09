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
import com.hp.hpl.jena.rdf.model.Container;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelChangedListener;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFList;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.Map1;
import com.hp.hpl.jena.util.iterator.WrappedIterator;
import com.hp.hpl.jena.vocabulary.RDF;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.xenei.jena.security.ItemHolder;
import org.xenei.jena.security.SecuredItemInvoker;
import org.xenei.jena.security.SecurityEvaluator;
import org.xenei.jena.security.SecurityEvaluator.Action;
import org.xenei.jena.security.model.SecuredContainer;
import org.xenei.jena.security.model.SecuredModel;
import org.xenei.jena.security.model.SecuredResource;
import org.xenei.jena.security.utils.ContainerFilter;
import org.xenei.jena.security.utils.PermStatementFilter;
import org.xenei.jena.security.utils.RDFListIterator;
import org.xenei.jena.security.utils.RDFListSecFilter;

/**
 * Implementation of SecuredContainer to be used by a SecuredItemInvoker proxy.
 */
public class SecuredContainerImpl extends SecuredResourceImpl
		implements SecuredContainer
{
	//private ModelChangedListener listener;
	//private List<Integer> indexes = new ArrayList<Integer>();
	
	/**
	 * Get a SecuredResource.
	 * 
	 * @param securedModel
	 *            the securedItem that provides the security context.
	 * @param resource
	 *            The resource to secure.
	 * @return The SecuredResource
	 */
	public static SecuredContainer getInstance( final SecuredModel securedModel,
			final Container container )
	{
		if (securedModel == null)
		{
			throw new IllegalArgumentException("Secured model may not be null");
		}
		if (container == null)
		{
			throw new IllegalArgumentException("Container may not be null");
		}
		
		// check that resource has a model.
		Container goodContainer = container;
		if (goodContainer.getModel() == null)
		{
			final Node n = container.asNode();
			goodContainer = securedModel.createBag();
		}

		final ItemHolder<Container, SecuredContainer> holder = new ItemHolder<Container, SecuredContainer>(
				goodContainer);

		final SecuredContainerImpl checker = new SecuredContainerImpl(
				securedModel, holder);
		// if we are going to create a duplicate proxy, just return this
		// one.
		if (goodContainer instanceof SecuredContainer)
		{
			if (checker.isEquivalent((SecuredContainer) goodContainer))
			{
				return (SecuredContainer) goodContainer;
			}
		}

		return holder.setSecuredItem(new SecuredItemInvoker(
				container.getClass(), checker));

	}

	
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
		//listener=new ChangeListener();
		//holder.getBaseItem().getModel().register(listener);
	}
	

	
	
	private int getAddIndex()
	{
		int pos=-1;
		ExtendedIterator<Statement> iter = holder.getBaseItem().listProperties();
		try {
			while (iter.hasNext())
			{
				pos = Math.max( pos, getIndex( iter.next().getPredicate() ) );
			}
		}
		finally {
			iter.close();
		}
		return pos+1;
	}

	@Override
	public SecuredContainer add( final boolean o )
	{
		checkUpdate();
		int pos = getAddIndex();
		checkAdd(pos, holder.getBaseItem().getModel().createTypedLiteral(o));
		holder.getBaseItem().add(o);
		return holder.getSecuredItem();
	}

	@Override
	public SecuredContainer add( final char o )
	{
		checkUpdate();
		int pos = getAddIndex();
		checkAdd(pos, ResourceFactory.createTypedLiteral(o));
		holder.getBaseItem().add(o);
		return holder.getSecuredItem();
	}

	@Override
	public SecuredContainer add( final double o )
	{
		checkUpdate();
		int pos = getAddIndex();
		checkAdd(pos, ResourceFactory.createTypedLiteral(o));
		holder.getBaseItem().add(o);
		return holder.getSecuredItem();
	}

	@Override
	public SecuredContainer add( final float o )
	{
		checkUpdate();
		int pos = getAddIndex();
		checkAdd(pos, ResourceFactory.createTypedLiteral(o));
		holder.getBaseItem().add(o);
		return holder.getSecuredItem();
	}

	@Override
	public SecuredContainer add( final long o )
	{
		checkUpdate();
		int pos = getAddIndex();
		checkAdd(pos, ResourceFactory.createTypedLiteral(o));
		holder.getBaseItem().add(o);
		return holder.getSecuredItem();
	}

	@Override
	public SecuredContainer add( final Object o )
	{
		checkUpdate();
		int pos = getAddIndex();
		checkAdd(pos, ResourceFactory.createTypedLiteral(o));
		holder.getBaseItem().add(o);
		return holder.getSecuredItem();
	}

	@Override
	public SecuredContainer add( final RDFNode o )
	{
		checkUpdate();
		int pos = getAddIndex();
		checkAdd(pos, o.asNode());
		holder.getBaseItem().add(o);
		return holder.getSecuredItem();
	}

	@Override
	public SecuredContainer add( final String o )
	{
		checkUpdate();
		int pos = getAddIndex();
		checkAdd(pos, ResourceFactory.createTypedLiteral(o));
		holder.getBaseItem().add(o);
		return holder.getSecuredItem();
	}

	@Override
	public SecuredContainer add( final String o, final String l )
	{
		checkUpdate();
		int pos = getAddIndex();
		checkAdd(pos, Node.createLiteral(o, l, false));
		holder.getBaseItem().add(o, l);
		return holder.getSecuredItem();
	}

	protected void checkAdd( int pos, final Literal literal )
	{
		checkAdd( pos, literal.asNode() );
	}

	protected void checkAdd( int pos, final Node node )
	{
		checkCreate(new Triple(holder.getBaseItem().asNode(), RDF.li(pos)
				.asNode(), node));
	}

	@Override
	public boolean contains( final boolean o )
	{
		// iterator check reads
		Literal l = holder.getBaseItem().getModel().createTypedLiteral(o);
		SecuredNodeIterator<RDFNode> iter = iterator();
		while (iter.hasNext())
		{
			if (iter.next().asNode().equals( l.asNode() ))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean contains( final char o )
	{
		// iterator check reads
		Literal l = holder.getBaseItem().getModel().createTypedLiteral(o);
		SecuredNodeIterator<RDFNode> iter = iterator();
		while (iter.hasNext())
		{
			if (iter.next().asNode().equals( l.asNode() ))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean contains( final double o )
	{
		// iterator check reads
		Literal l = holder.getBaseItem().getModel().createTypedLiteral(o);
		SecuredNodeIterator<RDFNode> iter = iterator();
		while (iter.hasNext())
		{
			if (iter.next().asNode().equals( l.asNode() ))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean contains( final float o )
	{
		// iterator check reads
		Literal l = holder.getBaseItem().getModel().createTypedLiteral(o);
		SecuredNodeIterator<RDFNode> iter = iterator();
		while (iter.hasNext())
		{
			if (iter.next().asNode().equals( l.asNode() ))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean contains( final long o )
	{
		// iterator check reads
		Literal l = holder.getBaseItem().getModel().createTypedLiteral(o);
		SecuredNodeIterator<RDFNode> iter = iterator();
		while (iter.hasNext())
		{
			if (iter.next().asNode().equals( l.asNode() ))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean contains( final Object o )
	{
		// iterator check reads
		Literal l = holder.getBaseItem().getModel().createTypedLiteral(o);
		SecuredNodeIterator<RDFNode> iter = iterator();
		while (iter.hasNext())
		{
			if (iter.next().asNode().equals( l.asNode() ))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean contains( final RDFNode o )
	{
		// iterator check reads
		SecuredNodeIterator<RDFNode> iter = iterator();
		while (iter.hasNext())
		{
			if (iter.next().asNode().equals( o.asNode() ))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean contains( final String o )
	{
		// iterator check reads
		Literal l = holder.getBaseItem().getModel().createTypedLiteral(o);
		SecuredNodeIterator<RDFNode> iter = iterator();
		while (iter.hasNext())
		{
			if (iter.next().asNode().equals( l.asNode() ))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean contains( final String o, final String l )
	{
		// iterator check reads
		Node lit = Node.createLiteral( o, l, false );
		SecuredNodeIterator<RDFNode> iter = iterator();
		while (iter.hasNext())
		{
			if (iter.next().asNode().equals( lit ))
			{
				return true;
			}
		}
		return false;
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

	protected ExtendedIterator<Statement> getStatementIterator(
			final Action perm )
	{
		return holder.getBaseItem().listProperties().filterKeep(
				new ContainerFilter() ).filterKeep( new PermStatementFilter(
						perm, this ) );
	}

	protected ExtendedIterator<Statement> getStatementIterator(
			final Set<Action> perm )
	{
		return holder.getBaseItem().listProperties().filterKeep(
				new ContainerFilter() ).filterKeep( new PermStatementFilter(
						perm, this ) );
	}	
	
	protected int getIndex( Property p )
	{
		if (p.getNameSpace().equals(RDF.getURI())
				&& p.getLocalName().startsWith("_"))
		{
			try
			{
				return Integer.parseInt(p.getLocalName().substring(1));
			}
			catch (final NumberFormatException e)
			{
				// acceptable;
			}
		}
		return -1;
	}
	
	@Override
	public SecuredNodeIterator<RDFNode> iterator()
	{
		checkRead();
		
		ExtendedIterator<RDFNode> ni = getStatementIterator( Action.Read ).mapWith( new Map1<Statement,RDFNode>(){

			@Override
			public RDFNode map1( Statement o )
			{
				return o.getObject();
			}});
		return new SecuredNodeIterator<RDFNode>( getModel(), ni );
		
	}

	@Override
	public SecuredNodeIterator<RDFNode> iterator( Set<Action> perms )
	{
		checkRead();
		Set<Action> permsCopy = new HashSet<Action>(perms);
		permsCopy.add( Action.Read );
		ExtendedIterator<RDFNode> ni = getStatementIterator( perms ).mapWith( new Map1<Statement,RDFNode>(){

			@Override
			public RDFNode map1( Statement o )
			{
				return o.getObject();
			}});
		return new SecuredNodeIterator<RDFNode>( getModel(), ni );
		
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
	/*
	private synchronized void resetIndexes()
	{
		indexes.clear();
	}
	*/
	/**
	 *  find the position of i in the array
	 * @param i
	 * @return the position or x<0 if not found.
	 */
	/*
	protected int mapValue( int i )
	{
		rebuildIndex();
		return Collections.binarySearch( indexes, i );
	}

	// return the value at position i
	protected int unmapValue( int i )
	{
		return indexes.get(i);
	}

	
	private synchronized void rebuildIndex()
	{
		if (indexes.isEmpty())
		{
			ExtendedIterator<Statement> iter = getStatementIterator( Action.Read );
			try {
				while (iter.hasNext())
				{
					indexes.add( getIndex( iter.next().getPredicate() ) );
				}
			}
			finally {
				iter.close();
			}
			Collections.sort(indexes);
		}	
	}
	
	private class ChangeListener implements ModelChangedListener
	{

		private void checkStatement( Statement s )
		{
			if (indexes != null && s.getSubject().equals( holder.getBaseItem()))
			{
				resetIndexes();
			}	
		}
		
		private void checkStatements( Iterator<Statement> iter )
		{
			while( indexes != null && iter.hasNext())
			{
				checkStatement( iter.next() );
			}
		}
		
		@Override
		public void addedStatement( Statement s )
		{
			checkStatement( s );			
		}

		@Override
		public void addedStatements( Statement[] statements )
		{
			checkStatements( Arrays.asList(statements).iterator() );
		}

		@Override
		public void addedStatements( List<Statement> statements )
		{
			checkStatements( statements.iterator() );
		}

		@Override
		public void addedStatements( StmtIterator statements )
		{
			try {
			checkStatements( statements );
			}
			finally {
				statements.close();
			}
		}

		@Override
		public void addedStatements( Model m )
		{
			addedStatements( m.listStatements() );
		}

		@Override
		public void removedStatement( Statement s )
		{
			checkStatement( s );
		}

		@Override
		public void removedStatements( Statement[] statements )
		{
			checkStatements( Arrays.asList(statements).iterator() );
		}

		@Override
		public void removedStatements( List<Statement> statements )
		{
			checkStatements( statements.iterator() );
		}

		@Override
		public void removedStatements( StmtIterator statements )
		{
			try {
			checkStatements( statements );
			}
			finally {
				statements.close();
			}
		}

		@Override
		public void removedStatements( Model m )
		{
			removedStatements( m.listStatements() );
		}

		@Override
		public void notifyEvent( Model m, Object event )
		{
			// do nothing
		}
		
	}
*/
}
