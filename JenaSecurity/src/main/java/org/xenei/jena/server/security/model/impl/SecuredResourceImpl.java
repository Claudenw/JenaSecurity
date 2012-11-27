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
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.LiteralRequiredException;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.RDFVisitor;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.shared.PropertyNotFoundException;

import org.xenei.jena.server.security.AccessDeniedException;
import org.xenei.jena.server.security.ItemHolder;
import org.xenei.jena.server.security.SecurityEvaluator;
import org.xenei.jena.server.security.SecurityEvaluator.Action;
import org.xenei.jena.server.security.model.SecuredResource;

/**
 * Implementation of SecuredResource to be used by a SecuredItemInvoker proxy.
 */
public class SecuredResourceImpl extends SecuredRDFNodeImpl implements
		SecuredResource
{
	// the item holder that contains this SecuredResource
	private final ItemHolder<? extends Resource, ? extends SecuredResource> holder;

	/**
	 * Constructor.
	 * 
	 * @param securityEvaluator
	 *            the security evaluator to use.
	 * @param graphIRI
	 *            the graph IRI to verify against.
	 * @param holder
	 *            the item holder that will contain this SecuredResource.
	 */
	public SecuredResourceImpl(
			final SecurityEvaluator securityEvaluator,
			final String graphIRI,
			final ItemHolder<? extends Resource, ? extends SecuredResource> holder )
	{
		super(securityEvaluator, graphIRI, holder);
		this.holder = holder;
	}

	/**
	 * Abort the transaction in the associated model.
	 * 
	 * @return This resource to permit cascading.
	 */
	@Override
	public Resource abort()
	{
		holder.getBaseItem().abort();
		return holder.getSecuredItem();
	}

	/**
	 * Add the property <code>p</code> with the typed-literal value
	 * <code>o</code> to this resource, <i>ie</i> add (this, p, typed(o)) to
	 * this's model. Answer
	 * this resource. The typed literal is equal to one constructed by using
	 * <code>this.getModel().createTypedLiteral(o)</code>.
	 */
	@Override
	public Resource addLiteral( final Property p, final boolean o )
	{
		checkUpdate();
		final Literal l = ResourceFactory.createTypedLiteral(o);
		checkCreate(new Triple(this.asNode(), p.asNode(), l.asNode()));
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().addLiteral(p, l));
	}

	/**
	 * Add the property <code>p</code> with the typed-literal value
	 * <code>o</code> to this resource, <i>ie</i> add (this, p, typed(o)) to
	 * this's model. Answer
	 * this resource. The typed literal is equal to one constructed by using
	 * <code>this.getModel().createTypedLiteral(o)</code>.
	 */
	@Override
	public Resource addLiteral( final Property p, final char o )
	{
		checkUpdate();
		final Literal l = ResourceFactory.createTypedLiteral(o);
		checkCreate(new Triple(this.asNode(), p.asNode(), l.asNode()));
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().addLiteral(p, o));
	}

	/**
	 * Add the property <code>p</code> with the typed-literal value
	 * <code>o</code> to this resource, <i>ie</i> add (this, p, typed(o)) to
	 * this's model. Answer
	 * this resource. The typed literal is equal to one constructed by using
	 * <code>this.getModel().createTypedLiteral(o)</code>.
	 */
	@Override
	public Resource addLiteral( final Property value, final double d )
	{
		checkUpdate();
		final Literal l = ResourceFactory.createTypedLiteral(d);
		checkCreate(new Triple(this.asNode(), value.asNode(), l.asNode()));
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().addLiteral(value, l));
	}

	/**
	 * Add the property <code>p</code> with the typed-literal value
	 * <code>o</code> to this resource, <i>ie</i> add (this, p, typed(o)) to
	 * this's model. Answer
	 * this resource. The typed literal is equal to one constructed by using
	 * <code>this.getModel().createTypedLiteral(o)</code>.
	 */
	@Override
	public Resource addLiteral( final Property value, final float d )
	{
		checkUpdate();
		final Literal l = ResourceFactory.createTypedLiteral(d);
		checkCreate(new Triple(this.asNode(), value.asNode(), l.asNode()));
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().addLiteral(value, l));
	}

	/**
	 * Add the property <code>p</code> with the pre-constructed Literal value
	 * <code>o</code> to this resource, <i>ie</i> add (this, p, o) to this's
	 * model. Answer this resource. <b>NOTE</b> thjat this is distinct from the
	 * other addLiteral methods in that the Literal is not turned into a
	 * Literal.
	 */
	@Override
	public Resource addLiteral( final Property p, final Literal o )
	{
		checkUpdate();
		final Literal l = ResourceFactory.createTypedLiteral(o);
		checkCreate(new Triple(this.asNode(), p.asNode(), l.asNode()));
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().addLiteral(p, l));
	}

	/**
	 * Add the property <code>p</code> with the typed-literal value
	 * <code>o</code> to this resource, <i>ie</i> add (this, p, typed(o)) to
	 * this's model. Answer
	 * this resource. The typed literal is equal to one constructed by using
	 * <code>this.getModel().createTypedLiteral(o)</code>.
	 */
	@Override
	public Resource addLiteral( final Property p, final long o )
	{
		checkUpdate();
		final Literal l = ResourceFactory.createTypedLiteral(o);
		checkCreate(new Triple(this.asNode(), p.asNode(), l.asNode()));
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().addLiteral(p, l));
	}

	/**
	 * Add the property <code>p</code> with the typed-literal value
	 * <code>o</code> to this resource, <i>ie</i> add (this, p, typed(o)) to
	 * this's model. Answer
	 * this resource. The typed literal is equal to one constructed by using
	 * <code>this.getModel().createTypedLiteral(o)</code>.
	 */
	@Override
	public Resource addLiteral( final Property p, final Object o )
	{
		checkUpdate();
		final Literal l = ResourceFactory.createTypedLiteral(o);
		checkCreate(new Triple(this.asNode(), p.asNode(), l.asNode()));
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().addLiteral(p, l));
	}

	/**
	 * Add a property to this resource.
	 * 
	 * <p>
	 * A statement with this resource as the subject, p as the predicate and o
	 * as the object is added to the model associated with this resource.
	 * </p>
	 * 
	 * @param p
	 *            The property to be added.
	 * @param o
	 *            The value of the property to be added.
	 * @return This resource to allow cascading calls.
	 */
	@Override
	public Resource addProperty( final Property p, final RDFNode o )
	{
		checkUpdate();
		checkCreate(new Triple(this.asNode(), p.asNode(), o.asNode()));
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().addProperty(p, o));
	}

	/**
	 * Add a property to this resource.
	 * 
	 * <p>
	 * A statement with this resource as the subject, p as the predicate and o
	 * as the object is added to the model associated with this resource.
	 * </p>
	 * 
	 * @param p
	 *            The property to be added.
	 * @param o
	 *            The value of the property to be added.
	 * @return This resource to allow cascading calls.
	 */
	@Override
	public Resource addProperty( final Property p, final String o )
	{
		checkUpdate();
		final Literal l = ResourceFactory.createTypedLiteral(o);
		checkCreate(new Triple(this.asNode(), p.asNode(), l.asNode()));
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().addProperty(p, l));
	}

	/**
	 * Add a property to this resource.
	 * 
	 * <p>
	 * A statement with this resource as the subject, p as the predicate and o
	 * as the object is added to the model associated with this resource.
	 * </p>
	 * 
	 * @param p
	 *            The property to be added.
	 * @param lexicalForm
	 *            The lexical form of the literal
	 * @param datatype
	 *            The datatype
	 * @return This resource to allow cascading calls.
	 */
	@Override
	public Resource addProperty( final Property p, final String lexicalForm,
			final RDFDatatype datatype )
	{
		checkUpdate();
		final Literal l = ResourceFactory.createTypedLiteral(lexicalForm,
				datatype);
		checkCreate(new Triple(this.asNode(), p.asNode(), l.asNode()));
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().addProperty(p, l));
	}

	/**
	 * Add a property to this resource.
	 * 
	 * <p>
	 * A statement with this resource as the subject, p as the predicate and o
	 * as the object is added to the model associated with this resource.
	 * </p>
	 * 
	 * @param p
	 *            The property to be added.
	 * @param o
	 *            The value of the property to be added.
	 * @param l
	 *            the language of the property
	 * @return This resource to allow cascading calls.
	 */
	@Override
	public Resource addProperty( final Property p, final String o,
			final String l )
	{
		checkUpdate();
		checkCreate(new Triple(this.asNode(), p.asNode(), Node.createLiteral(o,
				l, false)));
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().addProperty(p, o, l));
	}

	@Override
	public Literal asLiteral()
	{
		throw new LiteralRequiredException(asNode());
	}

	@Override
	public Resource asResource()
	{
		return this;
	}

	/**
	 * Begin a transaction in the associated model.
	 * 
	 * @return This resource to permit cascading.
	 */
	@Override
	public Resource begin()
	{
		holder.getBaseItem().begin();
		return holder.getSecuredItem();
	}

	public boolean canReadProperty( final Node p )
	{
		return canRead(new Triple(holder.getBaseItem().asNode(), p, Node.ANY));
	}

	protected void checkReadProperty( final Node p )
	{
		if (!canReadProperty(p))
		{
			throw new AccessDeniedException(getModelNode(), Action.Read);
		}
	}

	/**
	 * Commit the transaction in the associated model.
	 * 
	 * @return This resource to permit cascading.
	 */
	@Override
	public Resource commit()
	{
		holder.getBaseItem().commit();
		return holder.getSecuredItem();
	}

	/**
	 * Determine whether two objects represent the same resource.
	 * 
	 * <p>
	 * A resource can only be equal to another resource. If both resources are
	 * not anonymous, then they are equal if the URI's are equal. If both
	 * resources are anonymous, they are equal only if there Id's are the same.
	 * If one resource is anonymous and the other is not, then they are not
	 * equal.
	 * </p>
	 * 
	 * @param o
	 *            The object to be compared.
	 * @return true if and only if both objects are equal
	 */
	@Override
	public boolean equals( final Object o )
	{
		checkRead();
		return holder.getBaseItem().equals(o);
	}

	/**
	 * Returns an a unique identifier for anonymous resources.
	 * 
	 * <p>
	 * The id is unique within the scope of a particular implementation. All
	 * models within an implementation will use the same id for the same
	 * anonymous resource.
	 * </p>
	 * 
	 * <p>
	 * This method is undefined if called on resources which are not anonymous
	 * and may raise an exception.
	 * </p>
	 * 
	 * @return A unique id for an anonymous resource.
	 */
	@Override
	public AnonId getId()
	{
		checkRead();
		return holder.getBaseItem().getId();

	}

	/**
	 * Returns the name of this resource within its namespace.
	 * 
	 * @return The name of this property within its namespace.
	 */
	@Override
	public String getLocalName()
	{
		checkRead();
		return holder.getBaseItem().getLocalName();
	}

	/**
	 * Returns the namespace associated with this resource.
	 * 
	 * @return The namespace for this property.
	 */
	@Override
	public String getNameSpace()
	{
		checkRead();
		return holder.getBaseItem().getNameSpace();
	}

	/**
	 * Answer some statement (this, p, O) in the associated model. If there are
	 * several
	 * such statements, any one of them may be returned. If no such statements
	 * exist,
	 * null is returned - in this is differs from getRequiredProperty.
	 * 
	 * @param p
	 *            the property sought
	 * @return a statement (this, p, O), or null if no such statements exist
	 *         here
	 */
	@Override
	public Statement getProperty( final Property p )
	{
		checkRead();
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().getProperty(p));
	}

	/**
	 * Answer some resource R for which this.hasProperty( p, R ),
	 * or null if no such R exists.
	 */
	@Override
	public Resource getPropertyResourceValue( final Property p )
	{
		checkRead();
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().getPropertyResourceValue(p));
	}

	/**
	 * Get a property value of this resource.
	 * 
	 * <p>
	 * The model associated with the resource instance is searched for
	 * statements whose subject is this resource and whose predicate is p. If
	 * such a statement is found, it is returned. If several such statements are
	 * found, any one may be returned. If no such statements are found, an
	 * exception is thrown.
	 * </p>
	 * 
	 * @param p
	 *            The property sought.
	 * @return some (this, p, ?O) statement if one exists
	 * @throws PropertyNotFoundException
	 *             if no such statement found
	 */
	@Override
	public Statement getRequiredProperty( final Property p )
	{
		checkRead();
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().getRequiredProperty(p));
	}

	/**
	 * Return the URI of the resource, or null if it's a bnode.
	 * 
	 * @return The URI of the resource, or null if it's a bnode.
	 */
	@Override
	public String getURI()
	{
		checkRead();
		return holder.getBaseItem().getURI();
	}

	/**
	 * Answer true iff this resource has the value <code>o</code> for
	 * property <code>p</code>. <code>o</code> is interpreted as
	 * a typed literal with the appropriate RDF type.
	 */
	@Override
	public boolean hasLiteral( final Property p, final boolean o )
	{
		checkRead();
		return holder.getBaseItem().hasLiteral(p, o);
	}

	/**
	 * Answer true iff this resource has the value <code>o</code> for
	 * property <code>p</code>. <code>o</code> is interpreted as
	 * a typed literal with the appropriate RDF type.
	 */
	@Override
	public boolean hasLiteral( final Property p, final char o )
	{
		checkRead();
		return holder.getBaseItem().hasLiteral(p, o);
	}

	/**
	 * Answer true iff this resource has the value <code>o</code> for
	 * property <code>p</code>. <code>o</code> is interpreted as
	 * a typed literal with the appropriate RDF type.
	 */
	@Override
	public boolean hasLiteral( final Property p, final double o )
	{
		checkRead();
		return holder.getBaseItem().hasLiteral(p, o);
	}

	/**
	 * Answer true iff this resource has the value <code>o</code> for
	 * property <code>p</code>. <code>o</code> is interpreted as
	 * a typed literal with the appropriate RDF type.
	 */
	@Override
	public boolean hasLiteral( final Property p, final float o )
	{
		checkRead();
		return holder.getBaseItem().hasLiteral(p, o);
	}

	/**
	 * Answer true iff this resource has the value <code>o</code> for
	 * property <code>p</code>. <code>o</code> is interpreted as
	 * a typed literal with the appropriate RDF type.
	 */
	@Override
	public boolean hasLiteral( final Property p, final long o )
	{
		checkRead();
		return holder.getBaseItem().hasLiteral(p, o);
	}

	/**
	 * Answer true iff this resource has the value <code>o</code> for
	 * property <code>p</code>. <code>o</code> is interpreted as
	 * a typed literal with the appropriate RDF type.
	 */
	@Override
	public boolean hasLiteral( final Property p, final Object o )
	{
		checkRead();
		return holder.getBaseItem().hasLiteral(p, o);
	}

	/**
	 * Determine whether this resource has any values for a given property.
	 * 
	 * @param p
	 *            The property sought.
	 * @return true if and only if this resource has at least one
	 *         value for the property.
	 */
	@Override
	public boolean hasProperty( final Property p )
	{
		checkRead();
		return holder.getBaseItem().hasProperty(p);
	}

	/**
	 * Test if this resource has a given property with a given value.
	 * 
	 * @param p
	 *            The property sought.
	 * @param o
	 *            The value of the property sought.
	 * @return true if and only if this resource has property p with
	 *         value o.
	 */
	@Override
	public boolean hasProperty( final Property p, final RDFNode o )
	{
		checkRead();
		return holder.getBaseItem().hasProperty(p, o);
	}

	/**
	 * Test if this resource has a given property with a given value.
	 * 
	 * @param p
	 *            The property sought.
	 * @param o
	 *            The value of the property sought.
	 * @return true if and only if this resource has property p with
	 *         value o.
	 */
	@Override
	public boolean hasProperty( final Property p, final String o )
	{
		checkRead();
		return holder.getBaseItem().hasProperty(p, o);
	}

	/**
	 * Test if this resource has a given property with a given value.
	 * 
	 * @param p
	 *            The property sought.
	 * @param o
	 *            The value of the property sought.
	 * @param l
	 *            The language of the property sought.
	 * @return true if and only if this resource has property p with
	 *         value o.
	 */
	@Override
	public boolean hasProperty( final Property p, final String o, final String l )
	{
		checkRead();
		return holder.getBaseItem().hasProperty(p, o, l);
	}

	/**
	 * Answer true iff this Resource is a URI resource with the given URI.
	 * Using this is preferred to using getURI() and .equals().
	 */
	@Override
	public boolean hasURI( final String uri )
	{
		checkRead();
		return holder.getBaseItem().hasURI(uri);
	}

	@Override
	public Resource inModel( final Model m )
	{
		return (Resource) super.inModel(m);
	}

	/**
	 * Return an iterator over all the properties of this resource.
	 * 
	 * <p>
	 * The model associated with this resource is search and an iterator is
	 * returned which iterates over all the statements which have this resource
	 * as a subject.
	 * </p>
	 * 
	 * @return An iterator over all the statements about this object.
	 */
	@Override
	public SecuredStatementIterator listProperties()
	{
		checkRead();
		return new SecuredStatementIterator(this, holder.getBaseItem()
				.listProperties());
	}

	/**
	 * List all the values of the property p.
	 * 
	 * <p>
	 * Returns an iterator over all the statements in the associated model whose
	 * subject is this resource and whose predicate is p.
	 * </p>
	 * 
	 * @param p
	 *            The predicate sought.
	 * @return An iterator over the statements.
	 */
	@Override
	public SecuredStatementIterator listProperties( final Property p )
	{
		checkRead();
		return new SecuredStatementIterator(this, holder.getBaseItem()
				.listProperties(p));

	}

	/**
	 * Delete all the statements with predicate <code>p</code> for this resource
	 * from its associated model.
	 * 
	 * @param p
	 *            the property to remove
	 * @return this resource, to permit cascading
	 */
	@Override
	public Resource removeAll( final Property p )
	{
		checkDelete();
		if (!canDelete(new Triple(holder.getBaseItem().asNode(), p.asNode(),
				Node.ANY)))
		{
			final StmtIterator iter = holder.getBaseItem().listProperties(p);
			try
			{
				checkDelete(iter.next().asTriple());
			}
			finally
			{
				iter.close();
			}
		}
		holder.getBaseItem().removeAll(p);
		return holder.getSecuredItem();
	}

	/**
	 * Delete all the properties for this resource from the associated model.
	 * 
	 * @return This resource to permit cascading.
	 */
	@Override
	public Resource removeProperties()
	{
		checkDelete();
		if (!canDelete(new Triple(holder.getBaseItem().asNode(), Node.ANY,
				Node.ANY)))
		{
			final StmtIterator iter = holder.getBaseItem().listProperties();
			try
			{
				checkDelete(iter.next().asTriple());
			}
			finally
			{
				iter.close();
			}
		}
		holder.getBaseItem().removeProperties();
		return holder.getSecuredItem();
	}

	/**
	 * Return a string representation of the resource.
	 * 
	 * Returns the URI of the resource unless the resource is anonymous
	 * in which case it returns the id of the resource enclosed in square
	 * brackets.
	 * 
	 * @return Return a string representation of the resource.
	 *         if it is anonymous.
	 */
	@Override
	public String toString()
	{
		return holder.getBaseItem().toString();
	}

	@Override
	public Object visitWith( final RDFVisitor rv )
	{
		return isAnon() ? rv.visitBlank(this, getId()) : rv.visitURI(this,
				getURI());
	}

}
