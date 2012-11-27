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

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Reifier;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.TripleMatch;
import com.hp.hpl.jena.graph.impl.GraphBase;
import com.hp.hpl.jena.graph.query.QueryHandler;
import com.hp.hpl.jena.rdf.model.Alt;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Bag;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelChangedListener;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.NsIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFList;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.RDFReader;
import com.hp.hpl.jena.rdf.model.RDFReaderF;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.rdf.model.RDFWriterF;
import com.hp.hpl.jena.rdf.model.RSIterator;
import com.hp.hpl.jena.rdf.model.ReifiedStatement;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceF;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.Seq;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.rdf.model.impl.RDFReaderFImpl;
import com.hp.hpl.jena.rdf.model.impl.RDFWriterFImpl;
import com.hp.hpl.jena.shared.Command;
import com.hp.hpl.jena.shared.JenaException;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.shared.PropertyNotFoundException;
import com.hp.hpl.jena.shared.ReificationStyle;
import com.hp.hpl.jena.shared.WrappedIOException;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.Filter;
import com.hp.hpl.jena.util.iterator.Map1;
import com.hp.hpl.jena.util.iterator.NiceIterator;
import com.hp.hpl.jena.util.iterator.WrappedIterator;
import com.hp.hpl.jena.vocabulary.RDF;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xenei.jena.server.security.AccessDeniedException;
import org.xenei.jena.server.security.ItemHolder;
import org.xenei.jena.server.security.SecuredItemImpl;
import org.xenei.jena.server.security.SecurityEvaluator;
import org.xenei.jena.server.security.graph.SecuredGraph;
import org.xenei.jena.server.security.model.SecuredModel;

/**
 * Implementation of SecuredModel to be used by a SecuredItemInvoker proxy.
 */
public class SecuredModelImpl extends SecuredItemImpl implements SecuredModel
{
	public class CollectionGraph extends GraphBase
	{

		private class MatchFilter extends Filter<Triple>
		{
			TripleMatch m;

			public MatchFilter( final TripleMatch m )
			{
				if (m == null)
				{
					throw new IllegalArgumentException("Match must not be null");
				}
				this.m = m;
			}

			@Override
			public boolean accept( final Triple t )
			{
				if (t == null)
				{
					throw new IllegalArgumentException(
							"Triple must not be null");
				}
				return matches(t.getMatchSubject(), m.getMatchSubject())
						&& matches(t.getMatchPredicate(), m.getMatchPredicate())
						&& matches(t.getMatchObject(), m.getMatchObject());
			}

			private boolean isWild( final Node n )
			{
				return (n == null) || n.matches(Node.ANY);
			}

			private boolean matches( final Node t, final Node m )
			{
				return isWild(m) || isWild(t) || m.equals(t);
			}

		}

		Collection<Statement> stmts;

		private CollectionGraph( final Collection<Statement> stmts )
		{
			super();
			this.stmts = stmts;
		}

		@Override
		protected ExtendedIterator<Triple> graphBaseFind( final TripleMatch m )
		{
			return WrappedIterator.create(stmts.iterator())
					.mapWith(new Map1<Statement, Triple>() {

						@Override
						public Triple map1( final Statement o )
						{
							return o.asTriple();
						}
					}).filterKeep(new MatchFilter(m));
		}

		@Override
		public void performAdd( final Triple t )
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public void performDelete( final Triple t )
		{
			stmts.remove(t);
		}
	}

	private class SecuredModelChangedListener implements ModelChangedListener
	{
		private final ModelChangedListener wrapped;

		private SecuredModelChangedListener( final ModelChangedListener wrapped )
		{
			this.wrapped = wrapped;
		}

		@Override
		public void addedStatement( final Statement s )
		{
			if (canRead(s.asTriple()))
			{
				wrapped.addedStatement(s);
			}
		}

		@Override
		public void addedStatements( final List<Statement> statements )
		{
			for (final Statement s : statements)
			{
				addedStatement(s);
			}
		}

		@Override
		public void addedStatements( final Model m )
		{
			addedStatements(m.listStatements(new SecuredSelector(
					SecuredModelImpl.this)));
		}

		@Override
		public void addedStatements( final Statement[] statements )
		{
			addedStatements(Arrays.asList(statements));
		}

		@Override
		public void addedStatements( final StmtIterator statements )
		{
			addedStatements(statements.toList());
		}

		@Override
		public void notifyEvent( final Model m, final Object event )
		{
			wrapped.notifyEvent(m, event);
		}

		@Override
		public void removedStatement( final Statement s )
		{
			if (canRead(s.asTriple()))
			{
				wrapped.removedStatement(s);
			}
		}

		@Override
		public void removedStatements( final List<Statement> statements )
		{
			for (final Statement s : statements)
			{
				removedStatement(s);
			}
		}

		@Override
		public void removedStatements( final Model m )
		{
			removedStatements(m.listStatements(new SecuredSelector(
					SecuredModelImpl.this)));
		}

		@Override
		public void removedStatements( final Statement[] statements )
		{
			removedStatements(Arrays.asList(statements));
		}

		@Override
		public void removedStatements( final StmtIterator statements )
		{
			removedStatements(statements.toList());
		}
	}

	private static final RDFReaderF readerFactory = new RDFReaderFImpl();
	private static final RDFWriterF writerFactory = new RDFWriterFImpl();
	// the item holder that contains this SecuredModel.
	private final ItemHolder<Model, SecuredModel> holder;

	// The secured graph that this model contains.
	private final SecuredGraph graph;

	//
	Map<ModelChangedListener, SecuredModelChangedListener> listeners = new HashMap<ModelChangedListener, SecuredModelChangedListener>();

	/**
	 * Constructor.
	 * 
	 * @param securityEvaluator
	 *            The security evaluator to use
	 * @param modelURI
	 *            The model IRI to verify against.
	 * @param holder
	 *            The item holder that will contina this SecuredModel.
	 */
	public SecuredModelImpl( final SecurityEvaluator securityEvaluator,
			final String modelURI, final ItemHolder<Model, SecuredModel> holder )
	{
		super(securityEvaluator, modelURI, holder);
		this.graph = org.xenei.jena.server.security.model.impl.Factory
				.getInstance(this, holder.getBaseItem().getGraph());
		this.holder = holder;
	}

	@Override
	public Model abort()
	{
		holder.getBaseItem().abort();
		return holder.getSecuredItem();
	}

	@Override
	public Model add( final List<Statement> statements )
	{
		checkUpdate();
		checkCreateStatement(WrappedIterator.create(statements.iterator()));
		holder.getBaseItem().add(statements);
		return holder.getSecuredItem();
	}

	@Override
	public Model add( final Model m )
	{
		return add(m, false);
	}

	@Override
	public Model add( final Model m, final boolean suppressReifications )
	{
		checkUpdate();
		if (!canCreate(Triple.ANY))
		{
			checkCreateStatement(m.listStatements());
			if (!suppressReifications)
			{
				final Reifier r = m.getGraph().getReifier();
				checkCreateTriples(r.find(Triple.ANY));
			}
		}
		holder.getBaseItem().add(m, suppressReifications);
		return holder.getSecuredItem();
	}

	@Override
	public Model add( final Resource s, final Property p, final RDFNode o )
	{
		checkUpdate();
		checkCreate(new Triple(s.asNode(), p.asNode(), o.asNode()));
		holder.getBaseItem().add(s, p, o);
		return holder.getSecuredItem();
	}

	@Override
	public Model add( final Resource s, final Property p, final String o )
	{
		checkUpdate();
		checkCreate(new Triple(s.asNode(), p.asNode(), ResourceFactory
				.createTypedLiteral(o).asNode()));
		holder.getBaseItem().add(s, p, o);
		return holder.getSecuredItem();
	}

	@Override
	public Model add( final Resource s, final Property p, final String o,
			final boolean wellFormed )
	{
		checkUpdate();
		checkCreate(new Triple(s.asNode(), p.asNode(), Node.createLiteral(o,
				"", wellFormed)));
		holder.getBaseItem().add(s, p, o, wellFormed);
		return holder.getSecuredItem();
	}

	@Override
	public Model add( final Resource s, final Property p, final String lex,
			final RDFDatatype datatype )
	{
		checkUpdate();
		checkCreate(new Triple(s.asNode(), p.asNode(), Node.createLiteral(lex,
				datatype)));
		holder.getBaseItem().add(s, p, lex, datatype);
		return holder.getSecuredItem();
	}

	@Override
	public Model add( final Resource s, final Property p, final String o,
			final String l )
	{
		checkUpdate();
		checkCreate(new Triple(s.asNode(), p.asNode(), Node.createLiteral(o, l,
				false)));
		holder.getBaseItem().add(s, p, o, l);
		return holder.getSecuredItem();
	}

	@Override
	public Model add( final Statement s )
	{
		checkUpdate();
		checkCreate(s.asTriple());
		holder.getBaseItem().add(s);
		return holder.getSecuredItem();
	}

	@Override
	public Model add( final Statement[] statements )
	{
		return add(Arrays.asList(statements));
	}

	@Override
	public Model add( final StmtIterator iter )
	{
		return add(iter.toList());
	}

	@Override
	public Model addLiteral( final Resource s, final Property p, final boolean o )
	{
		final Literal l = ResourceFactory.createTypedLiteral(o);
		return add(s, p, l);
	}

	@Override
	public Model addLiteral( final Resource s, final Property p, final char o )
	{
		final Literal l = ResourceFactory.createTypedLiteral(o);
		return add(s, p, l);
	}

	@Override
	public Model addLiteral( final Resource s, final Property p, final double o )
	{
		final Literal l = ResourceFactory.createTypedLiteral(o);
		return add(s, p, l);
	}

	@Override
	public Model addLiteral( final Resource s, final Property p, final float o )
	{
		final Literal l = ResourceFactory.createTypedLiteral(o);
		return add(s, p, l);
	}

	@Override
	public Model addLiteral( final Resource s, final Property p, final int o )
	{
		final Literal l = ResourceFactory.createTypedLiteral(o);
		return add(s, p, l);
	}

	@Override
	public Model addLiteral( final Resource s, final Property p, final Literal o )
	{
		return add(s, p, o);
	}

	@Override
	public Model addLiteral( final Resource s, final Property p, final long o )
	{
		final Literal l = ResourceFactory.createTypedLiteral(o);
		return add(s, p, l);
	}

	@Override
	@Deprecated
	public Model addLiteral( final Resource s, final Property p, final Object o )
	{
		final Literal l = ResourceFactory.createTypedLiteral(o);
		return add(s, p, l);
	}

	@Override
	public RDFNode asRDFNode( final Node n )
	{
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().asRDFNode(n));
	}

	@Override
	public Statement asStatement( final Triple t )
	{
		final ExtendedIterator<Triple> iter = holder.getBaseItem().getGraph()
				.find(t);
		final boolean exists = iter.hasNext();
		iter.close();
		if (exists)
		{
			checkRead();
			checkRead(t);
		}
		else
		{
			checkUpdate();
			checkCreate(t);
		}
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().asStatement(t));
	}

	@Override
	public Model begin()
	{
		holder.getBaseItem().begin();
		return holder.getSecuredItem();
	}

	private void checkCreate( final Node n, final Triple t )
	{
		checkRead(t);
		checkRead(new Triple(n, RDF.subject.asNode(), t.getSubject()));
		checkRead(new Triple(n, RDF.predicate.asNode(), t.getPredicate()));
		checkRead(new Triple(n, RDF.object.asNode(), t.getObject()));
	}

	private void checkCreateAnonymousResource( final String value )
	{
		checkCreate();
		final SecurityEvaluator.Triple t = new SecurityEvaluator.Triple(
				new SecurityEvaluator.Node(
						SecurityEvaluator.Node.Type.Anonymous, value),
				SecurityEvaluator.Node.IGNORE, SecurityEvaluator.Node.IGNORE);
		checkCreate(t);
	}

	@Override
	public void close()
	{
		holder.getBaseItem().close();
	}

	@Override
	public Model commit()
	{
		holder.getBaseItem().commit();
		return holder.getSecuredItem();
	}

	@Override
	public boolean contains( final Resource s, final Property p )
	{
		checkRead();
		checkRead(new Triple(s.asNode(), p.asNode(), Node.ANY));
		return holder.getBaseItem().contains(s, p);
	}

	@Override
	public boolean contains( final Resource s, final Property p, final RDFNode o )
	{
		checkRead();
		checkRead(new Triple(s.asNode(), p.asNode(), o.asNode()));
		return holder.getBaseItem().contains(s, p, o);
	}

	@Override
	public boolean contains( final Resource s, final Property p, final String o )
	{
		checkRead();
		checkRead(new Triple(s.asNode(), p.asNode(), Node.createLiteral(o)));
		return holder.getBaseItem().containsLiteral(s, p, o);
	}

	@Override
	public boolean contains( final Resource s, final Property p,
			final String o, final String l )
	{
		checkRead();
		checkRead(new Triple(s.asNode(), p.asNode(), Node.createLiteral(o, l,
				null)));
		return holder.getBaseItem().containsLiteral(s, p, o);
	}

	@Override
	public boolean contains( final Statement s )
	{
		checkRead();
		checkRead(wildCardTriple(s));
		return holder.getBaseItem().contains(s);
	}

	@Override
	public boolean containsAll( final Model model )
	{
		checkRead();
		checkReadStatement(model.listStatements());
		return holder.getBaseItem().containsAll(model);
	}

	@Override
	public boolean containsAll( final StmtIterator iter )
	{
		checkRead();
		if (!canRead(Triple.ANY))
		{
			final Set<Statement> s = NiceIterator.asSet(iter);
			checkReadStatement(WrappedIterator.create(s.iterator()));
			final Model m = ModelFactory
					.createModelForGraph(new CollectionGraph(s));
			return holder.getBaseItem().containsAll(m);
		}
		return holder.getBaseItem().containsAll(iter);
	}

	@Override
	public boolean containsAny( final Model model )
	{
		checkRead();
		if (!canRead(Triple.ANY))
		{
			for (final Statement s : model.listStatements().toSet())
			{
				if (canRead(s.asTriple()))
				{
					if (holder.getBaseItem().contains(s))
					{
						return true;
					}
				}
			}
			return false;
		}
		else
		{
			return holder.getBaseItem().containsAny(model);
		}
	}

	@Override
	public boolean containsAny( final StmtIterator iter )
	{
		checkRead();
		if (!canRead(Triple.ANY))
		{
			while (iter.hasNext())
			{
				final Statement s = iter.next();
				if (canRead(s.asTriple()))
				{
					if (holder.getBaseItem().contains(s))
					{
						return true;
					}
				}
			}
			return false;
		}
		else
		{
			return holder.getBaseItem().containsAny(iter);
		}
	}

	@Override
	public boolean containsLiteral( final Resource s, final Property p,
			final boolean o )
	{
		return contains(s, p, ResourceFactory.createTypedLiteral(o));
	}

	@Override
	public boolean containsLiteral( final Resource s, final Property p,
			final char o )
	{
		return contains(s, p, ResourceFactory.createTypedLiteral(o));
	}

	@Override
	public boolean containsLiteral( final Resource s, final Property p,
			final double o )
	{
		return contains(s, p, ResourceFactory.createTypedLiteral(o));
	}

	@Override
	public boolean containsLiteral( final Resource s, final Property p,
			final float o )
	{
		return contains(s, p, ResourceFactory.createTypedLiteral(o));
	}

	@Override
	public boolean containsLiteral( final Resource s, final Property p,
			final int o )
	{
		return contains(s, p, ResourceFactory.createTypedLiteral(o));
	}

	@Override
	public boolean containsLiteral( final Resource s, final Property p,
			final long o )
	{
		return contains(s, p, ResourceFactory.createTypedLiteral(o));
	}

	@Override
	public boolean containsLiteral( final Resource s, final Property p,
			final Object o )
	{
		return contains(s, p, ResourceFactory.createTypedLiteral(o));
	}

	@Override
	public boolean containsResource( final RDFNode r )
	{
		checkRead();
		if (canRead(new Triple(Node.ANY, Node.ANY, r.asNode())))
		{
			return holder.getBaseItem().containsResource(r);
		}
		else
		{
			for (final Statement stmt : holder.getBaseItem()
					.listStatements(null, null, r).toSet())
			{
				if (canRead(stmt.asTriple()))
				{
					return true;
				}
			}
			return false;
		}
	}

	@Override
	public Alt createAlt()
	{
		checkCreate();
		checkCreate(new Triple(Node.ANY, RDF.type.asNode(), RDF.Alt.asNode()));
		return holder.getBaseItem().createAlt();
	}

	@Override
	public Alt createAlt( final String uri )
	{
		checkCreate();
		checkCreate(new Triple(Node.createURI(uri), RDF.type.asNode(),
				RDF.Alt.asNode()));
		return holder.getBaseItem().createAlt(uri);
	}

	@Override
	public Bag createBag()
	{
		checkCreate();
		checkCreate(new Triple(Node.ANY, RDF.type.asNode(), RDF.Bag.asNode()));
		return holder.getBaseItem().createBag();
	}

	@Override
	public Bag createBag( final String uri )
	{
		checkCreate();
		checkCreate(new Triple(Node.createURI(uri), RDF.type.asNode(),
				RDF.Bag.asNode()));
		return holder.getBaseItem().createBag(uri);
	}

	@Override
	public RDFList createList()
	{
		checkCreate();
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().createList());
	}

	@Override
	public RDFList createList( final Iterator<? extends RDFNode> members )
	{
		checkCreate();
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().createList(members));
	}

	@Override
	public RDFList createList( final RDFNode[] members )
	{
		checkCreate();
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().createList(members));
	}

	@Override
	public Literal createLiteral( final String v )
	{
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().createLiteral(v));
	}

	@Override
	public Literal createLiteral( final String v, final boolean wellFormed )
	{
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().createLiteral(v, wellFormed));

	}

	@Override
	public Literal createLiteral( final String v, final String language )
	{
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().createLiteral(v, language));
	}

	@Override
	public Statement createLiteralStatement( final Resource s,
			final Property p, final boolean o )
	{
		return createLiteralStatement(s, p,
				ResourceFactory.createTypedLiteral(o));
	}

	@Override
	public Statement createLiteralStatement( final Resource s,
			final Property p, final char o )
	{
		return createLiteralStatement(s, p,
				ResourceFactory.createTypedLiteral(o));
	}

	@Override
	public Statement createLiteralStatement( final Resource s,
			final Property p, final double o )
	{
		return createLiteralStatement(s, p,
				ResourceFactory.createTypedLiteral(o));
	}

	@Override
	public Statement createLiteralStatement( final Resource s,
			final Property p, final float o )
	{
		return createLiteralStatement(s, p,
				ResourceFactory.createTypedLiteral(o));
	}

	@Override
	public Statement createLiteralStatement( final Resource s,
			final Property p, final int o )
	{
		return createLiteralStatement(s, p,
				ResourceFactory.createTypedLiteral(o));
	}

	private Statement createLiteralStatement( final Resource s,
			final Property p, final Literal l )
	{
		checkCreate();
		checkCreate(new Triple(s.asNode(), p.asNode(), l.asNode()));
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().createStatement(s, p, l));
	}

	@Override
	public Statement createLiteralStatement( final Resource s,
			final Property p, final long o )
	{
		return createLiteralStatement(s, p,
				ResourceFactory.createTypedLiteral(o));
	}

	@Override
	public Statement createLiteralStatement( final Resource s,
			final Property p, final Object o )
	{
		return createLiteralStatement(s, p,
				ResourceFactory.createTypedLiteral(o));
	}

	@Override
	public Property createProperty( final String uri )
	{
		checkUpdate();
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().createProperty(uri));
	}

	@Override
	public Property createProperty( final String nameSpace,
			final String localName )
	{
		checkUpdate();
		return org.xenei.jena.server.security.model.impl.Factory
				.getInstance(
						this,
						holder.getBaseItem().createProperty(nameSpace,
								localName));
	}

	@Override
	public ReifiedStatement createReifiedStatement( final Statement s )
	{
		checkUpdate();
		checkCreate(Node.ANY, s.asTriple());
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().createReifiedStatement(s));
	}

	@Override
	public ReifiedStatement createReifiedStatement( final String uri,
			final Statement s )
	{
		checkUpdate();
		checkCreate(Node.createURI(uri), s.asTriple());
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().createReifiedStatement(uri, s));
	}

	@Override
	public Resource createResource()
	{
		checkCreateAnonymousResource("");
		return holder.getBaseItem().createResource();
	}

	@Override
	public Resource createResource( final AnonId id )
	{
		checkCreateAnonymousResource(id.getLabelString());
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().createResource(id));
	}

	@Override
	public Resource createResource( final Resource type )
	{
		final SecurityEvaluator.Triple t = new SecurityEvaluator.Triple(
				SecurityEvaluator.Node.FUTURE, SecuredItemImpl.convert(RDF.type
						.asNode()), SecuredItemImpl.convert(type.asNode()));

		checkCreate(t);

		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().createResource(type));
	}

	@Override
	@Deprecated
	public Resource createResource( final ResourceF f )
	{
		return createResource(null, f);
	}

	/*
	 * private void checkCreateResource( Resource r )
	 * {
	 * if (holder.getBaseItem().contains(r, (Property)null, (RDFNode)null))
	 * {
	 * checkRead();
	 * }
	 * else
	 * {
	 * checkCreate();
	 * SecurityEvaluator.Triple t = new SecurityEvaluator.Triple(
	 * convert( r.asNode() ),
	 * SecurityEvaluator.Node.IGNORE,
	 * SecurityEvaluator.Node.IGNORE );
	 * checkCreate( t );
	 * }
	 * }
	 */
	@Override
	public Resource createResource( final String uri )
	{
		// checkCreateResource( ResourceFactory.createResource( uri ) );
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().createResource(uri));

	}

	@Override
	public Resource createResource( final String uri, final Resource type )
	{
		final Resource r = ResourceFactory.createResource(uri);
		// checkCreateResource( r );
		final SecurityEvaluator.Triple t = new SecurityEvaluator.Triple(
				SecuredItemImpl.convert(r.asNode()),
				SecuredItemImpl.convert(RDF.type.asNode()),
				SecuredItemImpl.convert(type.asNode()));
		if (holder.getBaseItem().contains(r, RDF.type, type))
		{
			checkRead(t);
		}
		else
		{
			checkCreate(t);
		}
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().createResource(uri, type));

	}

	@Override
	@Deprecated
	public Resource createResource( final String uri, final ResourceF f )
	{
		// Resource r = f.createResource( ResourceFactory.createResource( uri )
		// );
		// checkCreateResource( r );
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().createResource(uri, f));
	}

	@Override
	public Seq createSeq()
	{
		checkUpdate();
		checkCreate(new Triple(Node.ANY, RDF.type.asNode(), RDF.Alt.asNode()));
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().createSeq());
	}

	@Override
	public Seq createSeq( final String uri )
	{
		checkUpdate();
		checkCreate(new Triple(Node.createURI(uri), RDF.type.asNode(),
				RDF.Alt.asNode()));
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().createSeq(uri));
	}

	@Override
	public Statement createStatement( final Resource s, final Property p,
			final RDFNode o )
	{
		checkUpdate();
		checkCreate(new Triple(s.asNode(), p.asNode(), o.asNode()));
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().createStatement(s, p, o));
	}

	@Override
	public Statement createStatement( final Resource s, final Property p,
			final String o )
	{
		checkUpdate();
		checkCreate(new Triple(s.asNode(), p.asNode(), Node.createURI(o)));
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().createStatement(s, p, o));
	}

	@Override
	public Statement createStatement( final Resource s, final Property p,
			final String o, final boolean wellFormed )
	{
		return createStatement(s, p, o, "", wellFormed);
	}

	@Override
	public Statement createStatement( final Resource s, final Property p,
			final String o, final String l )
	{
		return createStatement(s, p, o, l, false);
	}

	@Override
	public Statement createStatement( final Resource s, final Property p,
			final String o, final String l, final boolean wellFormed )
	{
		checkUpdate();
		checkCreate(new Triple(s.asNode(), p.asNode(), Node.createLiteral(o, l,
				wellFormed)));
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this,
				holder.getBaseItem().createStatement(s, p, o, l, wellFormed));
	}

	@Override
	public Literal createTypedLiteral( final boolean v )
	{
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().createTypedLiteral(v));
	}

	@Override
	public Literal createTypedLiteral( final Calendar d )
	{
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().createTypedLiteral(d));
	}

	@Override
	public Literal createTypedLiteral( final char v )
	{
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().createTypedLiteral(v));
	}

	@Override
	public Literal createTypedLiteral( final double v )
	{
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().createTypedLiteral(v));
	}

	@Override
	public Literal createTypedLiteral( final float v )
	{
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().createTypedLiteral(v));
	}

	@Override
	public Literal createTypedLiteral( final int v )
	{
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().createTypedLiteral(v));
	}

	@Override
	public Literal createTypedLiteral( final long v )
	{
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().createTypedLiteral(v));
	}

	@Override
	public Literal createTypedLiteral( final Object value )
	{
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().createTypedLiteral(value));
	}

	@Override
	public Literal createTypedLiteral( final Object value,
			final RDFDatatype dtype )
	{
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().createTypedLiteral(value, dtype));
	}

	@Override
	public Literal createTypedLiteral( final Object value, final String typeURI )
	{
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().createTypedLiteral(value, typeURI));
	}

	@Override
	public Literal createTypedLiteral( final String v )
	{
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().createTypedLiteral(v));
	}

	@Override
	public Literal createTypedLiteral( final String lex, final RDFDatatype dtype )
	{
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().createTypedLiteral(lex, dtype));
	}

	@Override
	public Literal createTypedLiteral( final String lex, final String typeURI )
	{
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().createTypedLiteral(lex, typeURI));
	}

	@Override
	public Model difference( final Model model )
	{
		checkRead();
		final Model workingModel = holder.getBaseItem().difference(model);
		if (!canRead(Triple.ANY))
		{
			final List<Statement> dropthem = workingModel.listStatements()
					.filterDrop(new Filter<Statement>() {

						@Override
						public boolean accept( final Statement o )
						{
							return canRead(o.asTriple());
						}
					}).toList();

			workingModel.remove(dropthem);
		}
		return workingModel;
	}

	@Override
	public void enterCriticalSection( final boolean readLockRequested )
	{
		if (readLockRequested)
		{
			checkRead();
		}
		else
		{
			checkUpdate();
		}
		holder.getBaseItem().enterCriticalSection(readLockRequested);
	}

	@Override
	public Object executeInTransaction( final Command cmd )
	{
		return holder.getBaseItem().executeInTransaction(cmd);
	}

	@Override
	public String expandPrefix( final String prefixed )
	{
		checkRead();
		return holder.getBaseItem().expandPrefix(prefixed);
	}

	@Override
	public Alt getAlt( final Resource r )
	{
		checkRead();
		checkRead(new Triple(r.asNode(), RDF.type.asNode(), RDF.Alt.asNode()));
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().getAlt(r));
	}

	@Override
	public Alt getAlt( final String uri )
	{
		checkRead();
		checkRead(new Triple(Node.createURI(uri), RDF.type.asNode(),
				RDF.Alt.asNode()));
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().getAlt(uri));
	}

	@Override
	public Resource getAnyReifiedStatement( final Statement s )
	{
		checkRead();
		final Triple t = s.asTriple();
		checkRead(t);
		checkRead(new Triple(Node.ANY, RDF.subject.asNode(), t.getSubject()));
		checkRead(new Triple(Node.ANY, RDF.predicate.asNode(), t.getPredicate()));
		checkRead(new Triple(Node.ANY, RDF.object.asNode(), t.getObject()));

		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().getAnyReifiedStatement(s));
	}

	@Override
	public Bag getBag( final Resource r )
	{
		checkRead();
		checkRead(new Triple(r.asNode(), RDF.type.asNode(), RDF.Bag.asNode()));
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().getBag(r));
	}

	@Override
	public Bag getBag( final String uri )
	{
		checkRead();
		checkRead(new Triple(Node.createURI(uri), RDF.type.asNode(),
				RDF.Bag.asNode()));
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().getBag(uri));
	}

	@Override
	public Graph getGraph()
	{
		return graph;
	}

	@Override
	public Lock getLock()
	{
		return holder.getBaseItem().getLock();
	}

	@Override
	public Map<String, String> getNsPrefixMap()
	{
		checkRead();
		return holder.getBaseItem().getNsPrefixMap();
	}

	@Override
	public String getNsPrefixURI( final String prefix )
	{
		checkRead();
		return holder.getBaseItem().getNsPrefixURI(prefix);
	}

	@Override
	public String getNsURIPrefix( final String uri )
	{
		checkRead();
		return holder.getBaseItem().getNsURIPrefix(uri);
	}

	@Override
	public Statement getProperty( final Resource s, final Property p )
	{
		final StmtIterator stmt = listStatements(s, p, (RDFNode) null);
		try
		{
			if (stmt.hasNext())
			{
				return stmt.next();
			}
			return null;
		}
		finally
		{
			if (stmt != null)
			{
				stmt.close();
			}
		}
	}

	@Override
	public Property getProperty( final String uri )
	{
		checkRead();
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().getProperty(uri));
	}

	@Override
	public Property getProperty( final String nameSpace, final String localName )
	{
		checkRead();
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().getProperty(nameSpace, localName));
	}

	@Override
	public RDFNode getRDFNode( final Node n )
	{
		RDFNode rdfNode = null;
		if (n.isLiteral())
		{
			rdfNode = ResourceFactory.createTypedLiteral(
					n.getLiteralLexicalForm(), n.getLiteralDatatype());
		}
		else if (n.isURI())
		{
			rdfNode = ResourceFactory.createProperty(n.getURI());
		}
		else
		{
			throw new IllegalArgumentException("Illegal Node type: " + n);
		}

		if (holder.getBaseItem().containsResource(rdfNode))
		{
			checkRead();
		}
		else
		{
			checkUpdate();
		}
		if (n.isLiteral())
		{
			return org.xenei.jena.server.security.model.impl.Factory
					.getInstance(this, holder.getBaseItem().getRDFNode(n)
							.asLiteral());
		}
		else
		{
			return org.xenei.jena.server.security.model.impl.Factory
					.getInstance(this, holder.getBaseItem().getRDFNode(n)
							.asResource());
		}
	}

	@Override
	public RDFReader getReader()
	{
		return holder.getBaseItem().getReader();
	}

	@Override
	public RDFReader getReader( final String lang )
	{
		return holder.getBaseItem().getReader(lang);
	}

	@Override
	public ReificationStyle getReificationStyle()
	{
		return holder.getBaseItem().getReificationStyle();
	}

	@Override
	public Statement getRequiredProperty( final Resource s, final Property p )
	{
		checkRead();
		try
		{
			final Statement stmt = holder.getBaseItem().getRequiredProperty(s,
					p);
			checkRead(stmt.asTriple());
			return stmt;
		}
		catch (final PropertyNotFoundException e)
		{
			checkRead(new Triple(s.asNode(), p.asNode(), Node.ANY));
			throw e;
		}
	}

	@Override
	public Resource getResource( final String uri )
	{
		return createResource(uri);
	}

	@Override
	@Deprecated
	public Resource getResource( final String uri, final ResourceF f )
	{
		return createResource(uri, f);
	}

	@Override
	public Seq getSeq( final Resource r )
	{
		checkRead();
		checkRead(new Triple(r.asNode(), RDF.type.asNode(), RDF.Seq.asNode()));
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().getSeq(r));
	}

	@Override
	public Seq getSeq( final String uri )
	{
		checkRead();
		checkRead(new Triple(Node.createURI(uri), RDF.type.asNode(),
				RDF.Seq.asNode()));
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().getSeq(uri));
	}

	@Override
	public RDFWriter getWriter()
	{
		return holder.getBaseItem().getWriter();
	}

	@Override
	public RDFWriter getWriter( final String lang )
	{
		return holder.getBaseItem().getWriter(lang);
	}

	@Override
	public boolean independent()
	{
		return false;
	}

	@Override
	public Model intersection( final Model model )
	{
		checkRead();
		final Model m = holder.getBaseItem().intersection(model);
		if (!canRead(Triple.ANY))
		{
			final Filter<Statement> filter = new Filter<Statement>() {

				@Override
				public boolean accept( final Statement o )
				{
					return canRead(o.asTriple());
				}
			};
			// remove all the items that can not be read.
			m.remove(m.listStatements().filterDrop(filter).toList());
		}
		return m;
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
		return holder.getBaseItem().isEmpty();
	}

	@Override
	public boolean isIsomorphicWith( final Model g )
	{
		checkRead();
		final boolean retval = holder.getBaseItem().isIsomorphicWith(g);
		if (retval && !canRead(Triple.ANY))
		{
			// in this case we have to check all the items in the graph to see
			// if the user can read
			// them all.
			final ExtendedIterator<Statement> stmtIter = holder.getBaseItem()
					.listStatements();
			try
			{
				while (stmtIter.hasNext())
				{
					if (!canRead(stmtIter.next().asTriple()))
					{
						return false;
					}
				}
			}
			finally
			{
				if (stmtIter != null)
				{
					stmtIter.close();
				}
			}
		}
		return retval;
	}

	@Override
	public boolean isReified( final Statement s )
	{
		checkRead();
		final Triple t = s.asTriple();
		checkRead(new Triple(Node.ANY, RDF.subject.asNode(), t.getSubject()));
		checkRead(new Triple(Node.ANY, RDF.predicate.asNode(), t.getPredicate()));
		checkRead(new Triple(Node.ANY, RDF.object.asNode(), t.getObject()));
		return holder.getBaseItem().isReified(s);
	}

	@Override
	public void leaveCriticalSection()
	{
		holder.getBaseItem().leaveCriticalSection();
	}

	@Override
	public StmtIterator listLiteralStatements( final Resource subject,
			final Property predicate, final boolean object )
	{
		checkRead();
		return new SecuredStatementIterator(this, holder.getBaseItem()
				.listLiteralStatements(subject, predicate, object));
	}

	@Override
	public StmtIterator listLiteralStatements( final Resource subject,
			final Property predicate, final char object )
	{
		checkRead();
		return new SecuredStatementIterator(this, holder.getBaseItem()
				.listLiteralStatements(subject, predicate, object));
	}

	@Override
	public StmtIterator listLiteralStatements( final Resource subject,
			final Property predicate, final double object )
	{
		checkRead();
		return new SecuredStatementIterator(this, holder.getBaseItem()
				.listLiteralStatements(subject, predicate, object));
	}

	@Override
	public StmtIterator listLiteralStatements( final Resource subject,
			final Property predicate, final float object )
	{
		checkRead();
		return new SecuredStatementIterator(this, holder.getBaseItem()
				.listLiteralStatements(subject, predicate, object));
	}

	@Override
	public StmtIterator listLiteralStatements( final Resource subject,
			final Property predicate, final long object )
	{
		checkRead();
		return new SecuredStatementIterator(this, holder.getBaseItem()
				.listLiteralStatements(subject, predicate, object));
	}

	@Override
	public NsIterator listNameSpaces()
	{
		checkRead();
		return holder.getBaseItem().listNameSpaces();
	}

	@Override
	public NodeIterator listObjects()
	{
		checkRead();
		return new SecuredNodeIterator(this, holder.getBaseItem().listObjects());
	}

	@Override
	public NodeIterator listObjectsOfProperty( final Property p )
	{
		checkRead();
		return new SecuredNodeIterator(this, holder.getBaseItem()
				.listObjectsOfProperty(p));
	}

	@Override
	public NodeIterator listObjectsOfProperty( final Resource s,
			final Property p )
	{
		checkRead();
		return new SecuredNodeIterator(this, holder.getBaseItem()
				.listObjectsOfProperty(s, p));
	}

	@Override
	public RSIterator listReifiedStatements()
	{
		checkRead();
		return new SecuredRSIterator(this, holder.getBaseItem()
				.listReifiedStatements());
	}

	@Override
	public RSIterator listReifiedStatements( final Statement st )
	{
		checkRead();
		return new SecuredRSIterator(this, holder.getBaseItem()
				.listReifiedStatements(st));
	}

	@Override
	public ResIterator listResourcesWithProperty( final Property p )
	{
		checkRead();
		return new SecuredResIterator(this, holder.getBaseItem()
				.listResourcesWithProperty(p));
	}

	@Override
	public ResIterator listResourcesWithProperty( final Property p,
			final boolean o )
	{
		checkRead();
		return new SecuredResIterator(this, holder.getBaseItem()
				.listResourcesWithProperty(p, o));
	}

	@Override
	public ResIterator listResourcesWithProperty( final Property p, final char o )
	{
		checkRead();
		return new SecuredResIterator(this, holder.getBaseItem()
				.listResourcesWithProperty(p, o));
	}

	@Override
	public ResIterator listResourcesWithProperty( final Property p,
			final double o )
	{
		checkRead();
		return new SecuredResIterator(this, holder.getBaseItem()
				.listResourcesWithProperty(p, o));
	}

	@Override
	public ResIterator listResourcesWithProperty( final Property p,
			final float o )
	{
		checkRead();
		return new SecuredResIterator(this, holder.getBaseItem()
				.listResourcesWithProperty(p, o));
	}

	@Override
	public ResIterator listResourcesWithProperty( final Property p, final long o )
	{
		checkRead();
		return new SecuredResIterator(this, holder.getBaseItem()
				.listResourcesWithProperty(p, o));
	}

	@Override
	public ResIterator listResourcesWithProperty( final Property p,
			final Object o )
	{
		checkRead();
		return new SecuredResIterator(this, holder.getBaseItem()
				.listResourcesWithProperty(p, o));
	}

	@Override
	public ResIterator listResourcesWithProperty( final Property p,
			final RDFNode o )
	{
		checkRead();
		return new SecuredResIterator(this, holder.getBaseItem()
				.listResourcesWithProperty(p, o));
	}

	@Override
	public StmtIterator listStatements()
	{
		checkRead();
		return new SecuredStatementIterator(this, holder.getBaseItem()
				.listStatements());
	}

	@Override
	public StmtIterator listStatements( final Resource s, final Property p,
			final RDFNode o )
	{
		checkRead();
		return new SecuredStatementIterator(this, holder.getBaseItem()
				.listStatements(s, p, o));
	}

	@Override
	public StmtIterator listStatements( final Resource subject,
			final Property predicate, final String object )
	{
		checkRead();
		return new SecuredStatementIterator(this, holder.getBaseItem()
				.listStatements(subject, predicate, object));
	}

	@Override
	public StmtIterator listStatements( final Resource subject,
			final Property predicate, final String object, final String lang )
	{
		checkRead();
		return new SecuredStatementIterator(this, holder.getBaseItem()
				.listStatements(subject, predicate, object, lang));
	}

	@Override
	public StmtIterator listStatements( final Selector s )
	{
		checkRead();
		return new SecuredStatementIterator(this, holder.getBaseItem()
				.listStatements(s));
	}

	@Override
	public ResIterator listSubjects()
	{
		checkRead();
		return new SecuredResIterator(this, holder.getBaseItem().listSubjects());
	}

	@Override
	public ResIterator listSubjectsWithProperty( final Property p )
	{
		checkRead();
		return new SecuredResIterator(this, holder.getBaseItem()
				.listSubjectsWithProperty(p));
	}

	@Override
	public ResIterator listSubjectsWithProperty( final Property p,
			final RDFNode o )
	{
		checkRead();
		return new SecuredResIterator(this, holder.getBaseItem()
				.listSubjectsWithProperty(p, o));
	}

	@Override
	public ResIterator listSubjectsWithProperty( final Property p,
			final String o )
	{
		checkRead();
		return new SecuredResIterator(this, holder.getBaseItem()
				.listSubjectsWithProperty(p, o));
	}

	@Override
	public ResIterator listSubjectsWithProperty( final Property p,
			final String o, final String l )
	{
		checkRead();
		return new SecuredResIterator(this, holder.getBaseItem()
				.listSubjectsWithProperty(p, o));
	}

	@Override
	public PrefixMapping lock()
	{
		checkUpdate();
		holder.getBaseItem().lock();
		return holder.getSecuredItem();
	}

	@Override
	public Model notifyEvent( final Object e )
	{
		holder.getBaseItem().notifyEvent(e);
		return holder.getSecuredItem();
	}

	@Override
	public String qnameFor( final String uri )
	{
		checkRead();
		return holder.getBaseItem().qnameFor(uri);
	}

	@Override
	public Model query( final Selector s )
	{
		checkRead();
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().query(new SecuredSelector(this, s)));
	}

	@Override
	public QueryHandler queryHandler()
	{
		return graph.queryHandler();
	}

	@Override
	public Model read( final InputStream in, final String base )
	{
		checkUpdate();
		try
		{
			SecuredModelImpl.readerFactory.getReader().read(this, in, base);
			return holder.getSecuredItem();
		}
		catch (final JenaException e)
		{
			if ((e.getCause() != null)
					&& (e.getCause() instanceof AccessDeniedException))
			{
				throw (AccessDeniedException) e.getCause();
			}
			throw e;
		}
	}

	@Override
	public Model read( final InputStream in, final String base,
			final String lang )
	{
		checkUpdate();
		try
		{
			SecuredModelImpl.readerFactory.getReader(lang).read(this, in, base);
			return holder.getSecuredItem();
		}
		catch (final JenaException e)
		{
			if ((e.getCause() != null)
					&& (e.getCause() instanceof AccessDeniedException))
			{
				throw (AccessDeniedException) e.getCause();
			}
			throw e;
		}
	}

	@Override
	public Model read( final Reader reader, final String base )
	{
		checkUpdate();
		try
		{
			SecuredModelImpl.readerFactory.getReader().read(this, reader, base);
			return holder.getSecuredItem();
		}
		catch (final JenaException e)
		{
			if ((e.getCause() != null)
					&& (e.getCause() instanceof AccessDeniedException))
			{
				throw (AccessDeniedException) e.getCause();
			}
			throw e;
		}
	}

	@Override
	public Model read( final Reader reader, final String base, final String lang )
	{
		checkUpdate();
		try
		{
			SecuredModelImpl.readerFactory.getReader(lang).read(this, reader,
					base);
			return holder.getSecuredItem();
		}
		catch (final JenaException e)
		{
			if ((e.getCause() != null)
					&& (e.getCause() instanceof AccessDeniedException))
			{
				throw (AccessDeniedException) e.getCause();
			}
			throw e;
		}
	}

	@Override
	public Model read( final String url )
	{
		checkUpdate();
		try
		{
			SecuredModelImpl.readerFactory.getReader().read(this, url);
			return holder.getSecuredItem();
		}
		catch (final JenaException e)
		{
			if ((e.getCause() != null)
					&& (e.getCause() instanceof AccessDeniedException))
			{
				throw (AccessDeniedException) e.getCause();
			}
			throw e;
		}
	}

	@Override
	public Model read( final String url, final String lang )
	{
		checkUpdate();
		try
		{
			SecuredModelImpl.readerFactory.getReader(lang).read(this, url);
			return holder.getSecuredItem();
		}
		catch (final JenaException e)
		{
			if ((e.getCause() != null)
					&& (e.getCause() instanceof AccessDeniedException))
			{
				throw (AccessDeniedException) e.getCause();
			}
			throw e;
		}
	}

	@Override
	public Model read( final String url, final String base, final String lang )
	{
		try
		{
			final InputStream is = new URL(url).openStream();
			try
			{
				read(is, base, lang);
			}
			finally
			{
				if (null != is)
				{
					is.close();
				}
			}
		}
		catch (final IOException e)
		{
			throw new WrappedIOException(e);
		}
		return holder.getSecuredItem();
	}

	@Override
	public Model register( final ModelChangedListener listener )
	{
		checkRead();
		if (!listeners.containsKey(listener))
		{
			final SecuredModelChangedListener secL = new SecuredModelChangedListener(
					listener);
			listeners.put(listener, secL);
			holder.getBaseItem().register(secL);
		}
		return holder.getSecuredItem();
	}

	@Override
	public Model remove( final List<Statement> statements )
	{
		checkDelete();
		if (canDelete(Triple.ANY))
		{
			holder.getBaseItem().remove(statements);
		}
		else
		{
			for (final Statement s : statements)
			{
				remove(s);
			}
		}
		return holder.getSecuredItem();
	}

	@Override
	public Model remove( final Model m )
	{
		return remove(m, false);
	}

	@Override
	public Model remove( final Model m, final boolean suppressReifications )
	{
		checkDelete();
		if (canDelete(Triple.ANY))
		{
			holder.getBaseItem().remove(m);
			if (!suppressReifications)
			{
				final RSIterator iter = m.listReifiedStatements();
				try
				{
					while (iter.hasNext())
					{
						holder.getBaseItem().remove(
								iter.next().listProperties());
					}
				}
				finally
				{
					iter.close();
				}
			}
		}
		else
		{
			final StmtIterator iter = m.listStatements();
			try
			{
				while (iter.hasNext())
				{
					remove(iter.next());
				}
				if (!suppressReifications)
				{
					final RSIterator rIter = m.listReifiedStatements();
					while (rIter.hasNext())
					{
						remove(rIter.next().listProperties());
					}
				}
			}
			finally
			{
				iter.close();
			}
		}
		return holder.getSecuredItem();

	}

	@Override
	public Model remove( final Resource s, final Property p, final RDFNode o )
	{
		checkDelete();
		checkDelete(new Triple(s.asNode(), p.asNode(), o.asNode()));
		holder.getBaseItem().remove(s, p, o);
		return holder.getSecuredItem();
	}

	@Override
	public Model remove( final Statement s )
	{
		checkDelete();
		checkDelete(wildCardTriple(s));
		holder.getBaseItem().remove(s);
		return holder.getSecuredItem();
	}

	@Override
	public Model remove( final Statement[] statements )
	{
		return remove(Arrays.asList(statements));
	}

	@Override
	public Model remove( final StmtIterator iter )
	{
		while (iter.hasNext())
		{
			remove(iter.next());
		}
		return holder.getSecuredItem();
	}

	@Override
	public Model removeAll()
	{
		checkDelete();
		checkDelete(Triple.ANY);
		return holder.getSecuredItem();
	}

	@Override
	public Model removeAll( final Resource s, final Property p, final RDFNode r )
	{
		checkDelete();
		checkDelete(new Triple(wildCardNode(s), wildCardNode(p),
				wildCardNode(r)));
		holder.getBaseItem().removeAll(s, p, r);
		return holder.getSecuredItem();
	}

	@Override
	public void removeAllReifications( final Statement s )
	{
		checkDelete();
		checkDelete(new Triple(Node.ANY, RDF.subject.asNode(),
				wildCardNode(s.getSubject())));
		checkDelete(new Triple(Node.ANY, RDF.predicate.asNode(),
				wildCardNode(s.getPredicate())));
		checkDelete(new Triple(Node.ANY, RDF.object.asNode(),
				wildCardNode(s.getObject())));

		holder.getBaseItem().removeAllReifications(s);
	}

	@Override
	public PrefixMapping removeNsPrefix( final String prefix )
	{
		checkUpdate();
		holder.getBaseItem().removeNsPrefix(prefix);
		return holder.getSecuredItem();
	}

	@Override
	public void removeReification( final ReifiedStatement rs )
	{
		checkDelete();
		if (!canDelete(Triple.ANY))
		{
			final StmtIterator stmtIter = rs.listProperties();
			try
			{
				while (stmtIter.hasNext())
				{
					checkDelete(stmtIter.next().asTriple());
				}
			}
			finally
			{
				stmtIter.close();
			}
		}
		holder.getBaseItem().removeReification(rs);
	}

	@Override
	public boolean samePrefixMappingAs( final PrefixMapping other )
	{
		checkRead();
		return holder.getBaseItem().samePrefixMappingAs(other);
	}

	@Override
	public PrefixMapping setNsPrefix( final String prefix, final String uri )
	{
		checkUpdate();
		holder.getBaseItem().setNsPrefix(prefix, uri);
		return holder.getSecuredItem();
	}

	@Override
	public PrefixMapping setNsPrefixes( final Map<String, String> map )
	{
		checkUpdate();
		holder.getBaseItem().setNsPrefixes(map);
		return holder.getSecuredItem();
	}

	@Override
	public PrefixMapping setNsPrefixes( final PrefixMapping other )
	{
		checkUpdate();
		holder.getBaseItem().setNsPrefixes(other);
		return holder.getSecuredItem();
	}

	@Override
	public String setReaderClassName( final String lang, final String className )
	{
		checkUpdate();
		return holder.getBaseItem().setReaderClassName(lang, className);
	}

	@Override
	public String setWriterClassName( final String lang, final String className )
	{
		checkUpdate();
		return holder.getBaseItem().setWriterClassName(lang, className);
	}

	@Override
	public String shortForm( final String uri )
	{
		checkRead();
		return holder.getBaseItem().shortForm(uri);
	}

	@Override
	public long size()
	{
		checkRead();
		return holder.getBaseItem().size();
	}

	@Override
	public boolean supportsSetOperations()
	{
		return holder.getBaseItem().supportsTransactions();
	}

	@Override
	public boolean supportsTransactions()
	{
		return holder.getBaseItem().supportsTransactions();
	}

	@Override
	public Model union( final Model model )
	{
		checkRead();
		final Model m = holder.getBaseItem().union(model);
		if (!canRead(Triple.ANY))
		{
			final StmtIterator iter = (StmtIterator) holder.getBaseItem()
					.listStatements().filterDrop(new Filter<Statement>() {

						@Override
						public boolean accept( final Statement o )
						{
							return canRead(o.asTriple());
						}
					});
			m.remove(iter);
		}
		return m;
	}

	@Override
	public Model unregister( final ModelChangedListener listener )
	{
		if (listeners.containsKey(listener))
		{
			final SecuredModelChangedListener secL = listeners.get(listener);
			holder.getBaseItem().unregister(secL);
			listeners.remove(listener);
		}
		return holder.getSecuredItem();
	}

	private Node wildCardNode( final RDFNode node )
	{
		return node == null ? Node.ANY : node.asNode();
	}

	private Triple wildCardTriple( final Statement s )
	{
		return new Triple(wildCardNode(s.getSubject()),
				wildCardNode(s.getPredicate()), wildCardNode(s.getObject()));
	}

	@Override
	public PrefixMapping withDefaultMappings( final PrefixMapping map )
	{
		checkUpdate();
		holder.getBaseItem().withDefaultMappings(map);
		return holder.getSecuredItem();
	}

	@Override
	public Resource wrapAsResource( final Node n )
	{
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().wrapAsResource(n));
	}

	@Override
	public Model write( final OutputStream out )
	{
		checkRead();
		if (canRead(Triple.ANY))
		{
			holder.getBaseItem().write(out);
		}
		else
		{
			getWriter().write(this, out, "");
		}
		return holder.getSecuredItem();

	}

	@Override
	public Model write( final OutputStream out, final String lang )
	{
		checkRead();
		if (canRead(Triple.ANY))
		{
			holder.getBaseItem().write(out, lang);
		}
		else
		{
			getWriter(lang).write(this, out, "");
		}
		return holder.getSecuredItem();
	}

	@Override
	public Model write( final OutputStream out, final String lang,
			final String base )
	{
		checkRead();
		if (canRead(Triple.ANY))
		{
			holder.getBaseItem().write(out, lang, base);
		}
		else
		{
			getWriter(lang).write(this, out, base);
		}
		return holder.getSecuredItem();

	}

	@Override
	public Model write( final Writer writer )
	{
		checkRead();
		if (canRead(Triple.ANY))
		{
			holder.getBaseItem().write(writer);
		}
		else
		{
			getWriter().write(this, writer, "");
		}
		return holder.getSecuredItem();
	}

	@Override
	public Model write( final Writer writer, final String lang )
	{
		checkRead();
		if (canRead(Triple.ANY))
		{
			holder.getBaseItem().write(writer, lang);
		}
		else
		{
			getWriter(lang).write(this, writer, "");
		}
		return holder.getSecuredItem();
	}

	@Override
	public Model write( final Writer writer, final String lang,
			final String base )
	{
		checkRead();
		if (canRead(Triple.ANY))
		{
			holder.getBaseItem().write(writer, lang, base);
		}
		else
		{
			getWriter(lang).write(this, writer, base);
		}
		return holder.getSecuredItem();

	}
}
