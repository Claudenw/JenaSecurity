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
package org.xenei.jena.security;

import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.RDF;

import java.lang.reflect.Proxy;

import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.xenei.jena.security.SecurityEvaluator.Action;
import org.xenei.jena.security.SecurityEvaluator.Node;
import org.xenei.jena.security.SecurityEvaluator.Triple;
import org.xenei.jena.security.SecurityEvaluator.Node.Type;

public abstract class SecuredItemImpl implements SecuredItem
{
	private class CacheKey implements Comparable<CacheKey>
	{
		private final Action action;
		private final Node modelNode;
		private final Triple from;
		private final Triple to;
		private Integer hashCode;

		public CacheKey( final Action action, final Node modelNode )
		{
			this(action, modelNode, null, null);
		}

		public CacheKey( final Action action, final Node modelNode,
				final Triple to )
		{
			this(action, modelNode, to, null);
		}

		public CacheKey( final Action action, final Node modelNode,
				final Triple to, final Triple from )
		{
			this.action = action;
			this.modelNode = modelNode;
			this.to = to;
			this.from = from;
		}

		@Override
		public int compareTo( final CacheKey other )
		{
			int retval = this.action.compareTo(other.action);
			if (retval == 0)
			{
				retval = this.modelNode.compareTo(other.modelNode);
			}
			if (retval == 0)
			{
				if (this.to == null)
				{
					if (other.to == null)
					{
						return 0;
					}
					return -1;
				}
				retval = this.to.compareTo(other.to);
			}
			if (retval == 0)
			{
				if (this.from == null)
				{
					if (other.from == null)
					{
						return 0;
					}
					return -1;
				}
				retval = this.from.compareTo(other.from);
			}
			return retval;
		}

		@Override
		public boolean equals( final Object o )
		{
			if (o instanceof CacheKey)
			{
				return this.compareTo((CacheKey) o) == 0;
			}
			return false;
		}

		@Override
		public int hashCode()
		{
			if (hashCode == null)
			{
				hashCode = new HashCodeBuilder().append(action)
						.append(modelNode).append(from).append(to).toHashCode();
			}
			return hashCode;
		}
	}

	public static int MAX_CACHE = 100;
	public static final ThreadLocal<LRUMap> CACHE = new ThreadLocal<LRUMap>();

	public static final ThreadLocal<Integer> COUNT = new ThreadLocal<Integer>();

	public static Node convert( final com.hp.hpl.jena.graph.Node jenaNode )
	{
		if (com.hp.hpl.jena.graph.Node.ANY.equals(jenaNode))
		{
			return Node.ANY;
		}
		if (jenaNode.isLiteral())
		{
			return new Node(Type.Literal, jenaNode.getLiteral().toString());
		}
		if (jenaNode.isBlank())
		{
			return new Node(Type.Anonymous, jenaNode.getBlankNodeLabel());
		}
		return new Node(Type.URI, jenaNode.getURI());
	}

	public static Triple convert( final com.hp.hpl.jena.graph.Triple jenaTriple )
	{
		return new Triple(SecuredItemImpl.convert(jenaTriple.getSubject()),
				SecuredItemImpl.convert(jenaTriple.getPredicate()),
				SecuredItemImpl.convert(jenaTriple.getObject()));
	}

	public static void decrementUse()
	{
		final Integer i = SecuredItemImpl.COUNT.get();
		if (i == null)
		{
			throw new IllegalStateException("No count on exit");
		}
		if (i < 1)
		{
			throw new IllegalStateException("No count less than 1");
		}
		if (i == 1)
		{
			SecuredItemImpl.CACHE.remove();
			SecuredItemImpl.COUNT.remove();
		}
		else
		{
			SecuredItemImpl.COUNT.set(Integer.valueOf(i - 1));
		}
	}

	public static void incrementUse()
	{
		final Integer i = SecuredItemImpl.COUNT.get();
		if (i == null)
		{
			SecuredItemImpl.CACHE.set(new LRUMap(Math.max(
					SecuredItemImpl.MAX_CACHE, 100)));
			SecuredItemImpl.COUNT.set(Integer.valueOf(1));
		}
		else
		{
			SecuredItemImpl.COUNT.set(Integer.valueOf(i + 1));
		}
	}

	private final SecurityEvaluator securityEvaluator;

	private final SecurityEvaluator.Node modelNode;

	private final ItemHolder<?, ?> itemHolder;

	protected SecuredItemImpl( final SecuredItem securedItem,
			final ItemHolder<?, ?> holder )
	{
		if (securedItem == null)
		{
			throw new IllegalArgumentException( "Secured item may not be null");
		}
		if (securedItem.getSecurityEvaluator() == null)
		{
			throw new IllegalArgumentException( "Security evaluator in secured item may not be null");
		}
		if (holder == null)
		{
			throw new IllegalArgumentException( "ItemHolder may not be null");
		}
		this.securityEvaluator = securedItem.getSecurityEvaluator();
		this.modelNode = new SecurityEvaluator.Node(
				SecurityEvaluator.Node.Type.URI, securedItem.getModelIRI());
		this.itemHolder = holder;
	}

	protected SecuredItemImpl( final SecurityEvaluator securityEvaluator,
			final String modelURI, final ItemHolder<?, ?> holder )
	{
		if (securityEvaluator == null)
		{
			throw new IllegalArgumentException( "Security evaluator may not be null");
		}
		if (StringUtils.isEmpty( modelURI ))
		{
			throw new IllegalArgumentException( "ModelURI may not be empty or null");
		}
		if (holder == null)
		{
			throw new IllegalArgumentException( "ItemHolder may not be null");
		}
		this.securityEvaluator = securityEvaluator;
		this.modelNode = new SecurityEvaluator.Node(
				SecurityEvaluator.Node.Type.URI, modelURI);
		this.itemHolder = holder;
	}

	private Boolean cacheGet( final CacheKey key )
	{
		final LRUMap cache = SecuredItemImpl.CACHE.get();
		return (cache == null)?null:(Boolean) cache.get(key);
	}

	void cachePut( final CacheKey key, final boolean value )
	{
		final LRUMap cache = SecuredItemImpl.CACHE.get();
		if (cache != null)
		{
			cache.put(key, value);
			SecuredItemImpl.CACHE.set( cache );
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xenei.jena.security.SecuredItem#canCreate()
	 */
	@Override
	public boolean canCreate()
	{
		final CacheKey key = new CacheKey(Action.Create, modelNode);
		Boolean retval = cacheGet(key);
		if (retval == null)
		{
			retval = securityEvaluator.evaluate(Action.Create, modelNode);
			cachePut(key, retval);
		}
		return retval;
	}

	public boolean canCreate( final com.hp.hpl.jena.graph.Triple t )
	{
		return canCreate(SecuredItemImpl.convert(t));
	}

	public boolean canCreate( final Statement s )
	{
		return canCreate(s.asTriple());
	}

	@Override
	public boolean canCreate( final Triple t )
	{
		final CacheKey key = new CacheKey(Action.Create, modelNode, t);
		Boolean retval = cacheGet(key);
		if (retval == null)
		{
			retval = securityEvaluator.evaluate(Action.Create, modelNode, t);
			cachePut(key, retval);
		}
		return retval;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xenei.jena.security.SecuredItem#canDelete()
	 */
	@Override
	public boolean canDelete()
	{
		final CacheKey key = new CacheKey(Action.Delete, modelNode);
		Boolean retval = cacheGet(key);
		if (retval == null)
		{
			retval = securityEvaluator.evaluate(Action.Delete, modelNode);
			cachePut(key, retval);
		}
		return retval;
	}

	public boolean canDelete( final com.hp.hpl.jena.graph.Triple t )
	{
		return canDelete(SecuredItemImpl.convert(t));
	}

	public boolean canDelete( final Statement s )
	{
		return canDelete(s.asTriple());
	}

	@Override
	public boolean canDelete( final Triple t )
	{
		final CacheKey key = new CacheKey(Action.Delete, modelNode, t);
		Boolean retval = cacheGet(key);
		if (retval == null)
		{
			retval = securityEvaluator.evaluate(Action.Delete, modelNode, t);
			cachePut(key, retval);
		}
		return retval;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xenei.jena.security.SecuredItem#canRead()
	 */
	@Override
	public boolean canRead()
	{
		final CacheKey key = new CacheKey(Action.Read, modelNode);
		Boolean retval = cacheGet(key);
		if (retval == null)
		{
			retval = securityEvaluator.evaluate(Action.Read, modelNode);
			cachePut(key, retval);
		}
		return retval;
	}

	public boolean canRead( final com.hp.hpl.jena.graph.Triple t )
	{
		return canRead(SecuredItemImpl.convert(t));
	}

	public boolean canRead( final Statement s )
	{
		return canRead(s.asTriple());
	}

	@Override
	public boolean canRead( final Triple t )
	{
		final CacheKey key = new CacheKey(Action.Read, modelNode, t);
		Boolean retval = cacheGet(key);
		if (retval == null)
		{
			retval = securityEvaluator.evaluate(Action.Read, modelNode, t);
			cachePut(key, retval);
		}
		return retval;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xenei.jena.security.SecuredItem#canUpdate()
	 */
	@Override
	public boolean canUpdate()
	{
		final CacheKey key = new CacheKey(Action.Update, modelNode);
		Boolean retval = cacheGet(key);
		if (retval == null)
		{
			retval = securityEvaluator.evaluate(Action.Update, modelNode);
			cachePut(key, retval);
		}
		return retval;
	}

	public boolean canUpdate( final com.hp.hpl.jena.graph.Triple from,
			final com.hp.hpl.jena.graph.Triple to )
	{
		return canUpdate(SecuredItemImpl.convert(from),
				SecuredItemImpl.convert(to));
	}

	public boolean canUpdate( final Statement from, final Statement to )
	{
		return canUpdate(from.asTriple(), to.asTriple());
	}

	@Override
	public boolean canUpdate( final Triple from, final Triple to )
	{
		final CacheKey key = new CacheKey(Action.Update, modelNode, from, to);
		Boolean retval = cacheGet(key);
		if (retval == null)
		{
			retval = securityEvaluator.evaluateUpdate(modelNode, from, to);
			cachePut(key, retval);
		}
		return retval;
	}

	/**
	 * check that create on the model is allowed,
	 * 
	 * @throws AccessDeniedException
	 *             on failure
	 */
	protected void checkCreate()
	{
		if (!canCreate())
		{
			throw new AccessDeniedException(modelNode, Action.Create);
		}
	}

	protected void checkCreate( final com.hp.hpl.jena.graph.Triple t )
	{
		checkCreate(SecuredItemImpl.convert(t));
	}

	protected void checkCreate( final Statement s )
	{
		checkCreate(s.asTriple());
	}

	/**
	 * check that the triple can be created in the model.,
	 * 
	 * @throws AccessDeniedException
	 *             on failure
	 */
	protected void checkCreate( final Triple t )
	{
		if (!canCreate(t))
		{
			throw new AccessDeniedException(modelNode, t.toString(),
					Action.Create);
		}
	}

	protected void checkCreateReified( final String uri, final Triple t )
	{
		checkUpdate();
		final Node n = uri == null ? Node.FUTURE : new Node(
				Type.URI, uri);
		checkCreate(new Triple(n, SecuredItemImpl.convert(RDF.subject.asNode()),
				t.getSubject()));
		checkCreate(new Triple(n,
				SecuredItemImpl.convert(RDF.predicate.asNode()),
				t.getPredicate()));
		checkCreate(new Triple(n, SecuredItemImpl.convert(RDF.object.asNode()),
				t.getObject()));
	}

	protected void checkCreateStatement( final ExtendedIterator<Statement> stmts )
	{
		if (!canCreate(Triple.ANY))
		{
			try
			{
				while (stmts.hasNext())
				{
					checkCreate(stmts.next());
				}
			}
			finally
			{
				stmts.close();
			}
		}
	}

	protected void checkCreateTriples(
			final ExtendedIterator<com.hp.hpl.jena.graph.Triple> triples )
	{
		if (!canCreate(Triple.ANY))
		{
			try
			{
				while (triples.hasNext())
				{
					checkCreate(triples.next());
				}
			}
			finally
			{
				triples.close();
			}
		}
	}

	/**
	 * check that delete on the model is allowed,
	 * 
	 * @throws AccessDeniedException
	 *             on failure
	 */
	protected void checkDelete()
	{
		if (!canDelete())
		{
			throw new AccessDeniedException(modelNode, Action.Delete);
		}
	}

	protected void checkDelete( final com.hp.hpl.jena.graph.Triple t )
	{
		checkDelete(SecuredItemImpl.convert(t));
	}

	protected void checkDelete( final Statement s )
	{
		checkDelete(s.asTriple());
	}

	/**
	 * check that the triple can be deleted in the model.,
	 * 
	 * @throws AccessDeniedException
	 *             on failure
	 */
	protected void checkDelete( final Triple t )
	{
		if (!canDelete(t))
		{
			throw new AccessDeniedException(modelNode, t.toString(),
					Action.Delete);
		}
	}

	protected void checkDeleteStatements(
			final ExtendedIterator<Statement> stmts )
	{
		if (!canDelete(Triple.ANY))
		{
			try
			{
				while (stmts.hasNext())
				{
					checkDelete(stmts.next());
				}
			}
			finally
			{
				stmts.close();
			}
		}
	}

	protected void checkDeleteTriples(
			final ExtendedIterator<com.hp.hpl.jena.graph.Triple> triples )
	{
		if (!canDelete(Triple.ANY))
		{
			try
			{
				while (triples.hasNext())
				{
					checkDelete(triples.next());
				}
			}
			finally
			{
				triples.close();
			}
		}
	}

	/**
	 * check that read on the model is allowed,
	 * 
	 * @throws AccessDeniedException
	 *             on failure
	 */
	protected void checkRead()
	{
		if (!canRead())
		{
			throw new AccessDeniedException(modelNode, Action.Read);
		}
	}

	protected void checkRead( final com.hp.hpl.jena.graph.Triple t )
	{
		checkRead(SecuredItemImpl.convert(t));
	}

	protected void checkRead( final Statement s )
	{
		checkRead(s.asTriple());
	}

	/**
	 * check that the triple can be read in the model.,
	 * 
	 * @throws AccessDeniedException
	 *             on failure
	 */
	protected void checkRead( final Triple t )
	{
		if (!canRead(t))
		{
			throw new AccessDeniedException(modelNode, t.toString(),
					Action.Read);
		}
	}

	protected void checkReadStatement( final ExtendedIterator<Statement> stmts )
	{
		try
		{
			while (stmts.hasNext())
			{
				checkRead(stmts.next());
			}
		}
		finally
		{
			stmts.close();
		}
	}

	protected void checkReadTriples(
			final ExtendedIterator<com.hp.hpl.jena.graph.Triple> triples )
	{
		try
		{
			while (triples.hasNext())
			{
				checkRead(triples.next());
			}
		}
		finally
		{
			triples.close();
		}
	}

	/**
	 * check that update on the model is allowed,
	 * 
	 * @throws AccessDeniedException
	 *             on failure
	 */
	protected void checkUpdate()
	{
		if (!canUpdate())
		{
			throw new AccessDeniedException(modelNode, Action.Update);
		}
	}

	protected void checkUpdate( final com.hp.hpl.jena.graph.Triple from,
			final com.hp.hpl.jena.graph.Triple to )
	{
		checkUpdate(SecuredItemImpl.convert(from), SecuredItemImpl.convert(to));
	}

	/**
	 * check that the triple can be updated in the model.,
	 * 
	 * @param the
	 *            starting triple
	 * @param the
	 *            final triple.
	 * @throws AccessDeniedException
	 *             on failure
	 */
	protected void checkUpdate( final Triple from, final Triple to )
	{
		if (!canUpdate(from, to))
		{
			throw new AccessDeniedException(modelNode, String.format(
					"%s to %s", from, to), Action.Update);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xenei.jena.security.SecuredItem#equals(java.lang.Object)
	 */
	@Override
	public boolean equals( final Object o )
	{
		if (Proxy.isProxyClass(o.getClass()))
		{
			return o.equals(itemHolder.getSecuredItem());
		}
		else
		{
			return super.equals(o);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xenei.jena.security.SecuredItem#getBaseItem()
	 */
	@Override
	public Object getBaseItem()
	{
		return itemHolder.getBaseItem();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xenei.jena.security.SecuredItem#getModelIRI()
	 */
	@Override
	public String getModelIRI()
	{
		return modelNode.getValue();
	}

	@Override
	public Node getModelNode()
	{
		return modelNode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xenei.jena.security.SecuredItem#getSecurityEvaluator()
	 */
	@Override
	public SecurityEvaluator getSecurityEvaluator()
	{
		return securityEvaluator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xenei.jena.security.isEquivalent()
	 */
	@Override
	public boolean isEquivalent( final SecuredItem securedItem )
	{
		return SecuredItem.Util.isEquivalent(this, securedItem);
	}
}