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
package org.xenei.jena.security.model;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelChangedListener;
import com.hp.hpl.jena.rdf.model.NsIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFList;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ReifiedStatement;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceF;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.shared.PropertyNotFoundException;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.xenei.jena.security.AccessDeniedException;
import org.xenei.jena.security.SecuredItem;
import org.xenei.jena.security.graph.SecuredGraph;
import org.xenei.jena.security.graph.SecuredPrefixMapping;
import org.xenei.jena.security.model.impl.SecuredNodeIterator;
import org.xenei.jena.security.model.impl.SecuredRSIterator;
import org.xenei.jena.security.model.impl.SecuredResIterator;
import org.xenei.jena.security.model.impl.SecuredStatementIterator;

/**
 * The interface for secured Model instances.
 * 
 * Use the SecuredModel.Factory to create instances
 */
public interface SecuredModel extends Model, SecuredItem, SecuredPrefixMapping
{

	@Override
	public SecuredModel abort();

	/**
	 * @graphSec Update
	 * @tripleSec Create for each statement as a triple.
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredModel add( final List<Statement> statements )
			throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Create for each statement in the model as a triple.
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredModel add( final Model m ) throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Create for each statement in the model as a triple.
	 * @tripleSec Create for each reified statement if not supressReifications.
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredModel add( final Model m, final boolean suppressReifications )
			throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Create the triple Triple(s,p,o)
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredModel add( final Resource s, final Property p, final RDFNode o )
			throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Create the triple Triple(s,p,o)
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredModel add( final Resource s, final Property p, final String o )
			throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Create the triple Triple(s,p,o)
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredModel add( final Resource s, final Property p,
			final String o, final boolean wellFormed )
			throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Create the triple Triple(s,p,literal(lex,datatype))
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredModel add( final Resource s, final Property p,
			final String lex, final RDFDatatype datatype )
			throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Create the triple Triple(s,p,literal(o,l,false))
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredModel add( final Resource s, final Property p,
			final String o, final String l ) throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Create the statement as a triple
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredModel add( final Statement s ) throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Create all the statements as triples.
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredModel add( final Statement[] statements )
			throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Create all the statements as triples.
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredModel add( final StmtIterator iter )
			throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Create triple(s,p,o)
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredModel addLiteral( final Resource s, final Property p,
			final boolean o ) throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Create triple(s,p,o)
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredModel addLiteral( final Resource s, final Property p,
			final char o ) throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Create triple(s,p,o)
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredModel addLiteral( final Resource s, final Property p,
			final double o ) throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Create triple(s,p,o)
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredModel addLiteral( final Resource s, final Property p,
			final float o ) throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Create triple(s,p,o)
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredModel addLiteral( final Resource s, final Property p,
			final int o ) throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Create triple(s,p,o)
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredModel addLiteral( final Resource s, final Property p,
			final Literal o ) throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Create triple(s,p,o)
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredModel addLiteral( final Resource s, final Property p,
			final long o ) throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Create triple(s,p,o)
	 * @throws AccessDeniedException
	 */
	@Override
	@Deprecated
	public SecuredModel addLiteral( final Resource s, final Property p,
			final Object o ) throws AccessDeniedException;

	@Override
	public SecuredRDFNode asRDFNode( final Node n );

	@Override
	/**
	 * @graphSec Read if t does exist
	 * @graphSec Update it t does not exist
	 * @tripleSec Read if t does exist
	 * @tripleSec Create if t does exist
	 * @throws AccessDeniedException
	 */
	public SecuredStatement asStatement( final Triple t )
			throws AccessDeniedException;

	@Override
	public SecuredModel begin();

	@Override
	public SecuredModel commit();

	/**
	 * @graphSec Read
	 * @tripleSec Read Triple( s, p, Node.ANY )
	 * @throws AccessDeniedException
	 */
	@Override
	public boolean contains( final Resource s, final Property p )
			throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read Triple( s, p, o )
	 * @throws AccessDeniedException
	 */
	@Override
	public boolean contains( final Resource s, final Property p, final RDFNode o )
			throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read Triple( s, p, o )
	 * @throws AccessDeniedException
	 */
	@Override
	public boolean contains( final Resource s, final Property p, final String o )
			throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read Triple( s, p, literal(o,l,null) )
	 * @throws AccessDeniedException
	 */
	@Override
	public boolean contains( final Resource s, final Property p,
			final String o, final String l ) throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read s as a triple with null replaced by Node.ANY
	 * @throws AccessDeniedException
	 */
	@Override
	public boolean contains( final Statement s ) throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read every statement in model.
	 * @throws AccessDeniedException
	 */
	@Override
	public boolean containsAll( final Model model )
			throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read every statement
	 * @throws AccessDeniedException
	 */
	@Override
	public boolean containsAll( final StmtIterator iter )
			throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read any statement in model to be included in check, if no
	 *            statement in model can be read will return false;
	 * @throws AccessDeniedException
	 */
	@Override
	public boolean containsAny( final Model model )
			throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read any statement in iter to be included in check, if no
	 *            statement in iter can be read will return false;
	 * @throws AccessDeniedException
	 */
	@Override
	public boolean containsAny( final StmtIterator iter )
			throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read Triple( s, p, literal(o) )
	 * @throws AccessDeniedException
	 */
	@Override
	public boolean containsLiteral( final Resource s, final Property p,
			final boolean o ) throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read Triple( s, p, literal(o) )
	 * @throws AccessDeniedException
	 */
	@Override
	public boolean containsLiteral( final Resource s, final Property p,
			final char o ) throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read Triple( s, p, literal(o) )
	 * @throws AccessDeniedException
	 */
	@Override
	public boolean containsLiteral( final Resource s, final Property p,
			final double o ) throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read Triple( s, p, literal(o) )
	 * @throws AccessDeniedException
	 */
	@Override
	public boolean containsLiteral( final Resource s, final Property p,
			final float o ) throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read Triple( s, p, literal(o) )
	 * @throws AccessDeniedException
	 */
	@Override
	public boolean containsLiteral( final Resource s, final Property p,
			final int o ) throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read Triple( s, p, literal(o) )
	 * @throws AccessDeniedException
	 */
	@Override
	public boolean containsLiteral( final Resource s, final Property p,
			final long o ) throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read Triple( s, p, typedLiteral(o) )
	 * @throws AccessDeniedException
	 */
	@Override
	public boolean containsLiteral( final Resource s, final Property p,
			final Object o ) throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read Triple( s, p, r) where Triple(s,p,r) is in the model.
	 * @throws AccessDeniedException
	 */
	@Override
	public boolean containsResource( final RDFNode r )
			throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Create Triple( Node.ANY, RDF.type, Rdf.Alt)
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredAlt createAlt() throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Create Triple( uri, RDF.type, Rdf.Alt)
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredAlt createAlt( final String uri )
			throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Create Triple( Node.ANY, RDF.type, Rdf.Bag)
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredBag createBag() throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Create Triple( uri, RDF.type, Rdf.Bag)
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredBag createBag( final String uri )
			throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Create Triple( RDF.nil, Node.IGNORE, Node.IGNORE)
	 * @throws AccessDeniedException
	 */
	@Override
	public RDFList createList() throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Create Triple( RDF.nil, Node.IGNORE, Node.IGNORE)
	 * @tripleSec Create for each member Triple(Node.ANY, RDF.first.asNode(),
	 *            member.asNode())
	 * @tripleSec Create Triple(Node.ANY, RDF.rest.asNode(), Node.ANY)
	 * @throws AccessDeniedException
	 */
	@Override
	public RDFList createList( final Iterator<? extends RDFNode> members )
			throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Create Triple( RDF.nil, Node.IGNORE, Node.IGNORE)
	 * @tripleSec Create for each member Triple(Node.ANY, RDF.first.asNode(),
	 *            member.asNode())
	 * @tripleSec Create Triple(Node.ANY, RDF.rest.asNode(), Node.ANY)
	 * @throws AccessDeniedException
	 */
	@Override
	public RDFList createList( final RDFNode[] members )
			throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Create Triple( s,p,o )
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredStatement createLiteralStatement( final Resource s,
			final Property p, final boolean o ) throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Create Triple( s,p,o )
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredStatement createLiteralStatement( final Resource s,
			final Property p, final char o ) throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Create Triple( s,p,o )
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredStatement createLiteralStatement( final Resource s,
			final Property p, final double o ) throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Create Triple( s,p,o )
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredStatement createLiteralStatement( final Resource s,
			final Property p, final float o ) throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Create Triple( s,p,o )
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredStatement createLiteralStatement( final Resource s,
			final Property p, final int o ) throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Create Triple( s,p,o )
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredStatement createLiteralStatement( final Resource s,
			final Property p, final long o ) throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Create Triple( s,p,o )
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredStatement createLiteralStatement( final Resource s,
			final Property p, final Object o ) throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Create Triple( s,p,o )
	 * @throws AccessDeniedException
	 */
	@Override
	public Property createProperty( final String uri )
			throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Create Triple( s,p,o )
	 * @throws AccessDeniedException
	 */
	@Override
	public Property createProperty( final String nameSpace,
			final String localName ) throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Read s as a triple
	 * @tripleSec create Triple( Node.Future, RDF.subject, t.getSubject() )
	 * @tripleSec create Triple( Node.Future, RDF.subject, t.getPredicate() )
	 * @tripleSec create Triple( Node.Future, RDF.subject, t.getObject() )
	 * @throws AccessDeniedException
	 */
	@Override
	public ReifiedStatement createReifiedStatement( final Statement s )
			throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Read s as a triple
	 * @tripleSec create Triple( uri, RDF.subject, t.getSubject() )
	 * @tripleSec create Triple( uri, RDF.subject, t.getPredicate() )
	 * @tripleSec create Triple( uri, RDF.subject, t.getObject() )
	 * @throws AccessDeniedException
	 */
	@Override
	public ReifiedStatement createReifiedStatement( final String uri,
			final Statement s ) throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Read s as a triple
	 * @tripleSec create Triple( Node.FUTURE, Node.IGNORE, Node.IGNORE )
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredResource createResource() throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Read s as a triple
	 * @tripleSec create Triple( Anonymous(id), Node.IGNORE, Node.IGNORE )
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredResource createResource( final AnonId id )
			throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Create Triple( Node.FUTURE, RDF.type, type )
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredResource createResource( final Resource type )
			throws AccessDeniedException;

	@Override
	@Deprecated
	public SecuredResource createResource( final ResourceF f );

	@Override
	public SecuredResource createResource( final String uri );

	/**
	 * @graphSec Update if uri exists
	 * @graphSec Create if uri does not exist
	 * @tripleSec Read if Triple( uri, RDF.type, type ) exists
	 * @tripleSec Create if Triple( uri, RDF.type, type ) does not exist
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredResource createResource( final String uri, final Resource type )
			throws AccessDeniedException;

	@Override
	@Deprecated
	public SecuredResource createResource( final String uri, final ResourceF f );

	/**
	 * @graphSec Update
	 * @tripleSec Create Triple( Node.FUTURE, RDF.type, RDF.Alt )
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredSeq createSeq() throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Create Triple( uri, RDF.type, RDF.Alt )
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredSeq createSeq( final String uri )
			throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Create Triple( s, p, o )
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredStatement createStatement( final Resource s,
			final Property p, final RDFNode o ) throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Create Triple( s, p, o )
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredStatement createStatement( final Resource s,
			final Property p, final String o ) throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Create Triple( s, p, o )
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredStatement createStatement( final Resource s,
			final Property p, final String o, final boolean wellFormed )
			throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Create Triple( s, p, literal(o,l,false ))
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredStatement createStatement( final Resource s,
			final Property p, final String o, final String l )
			throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Create Triple( s, p, literal(o,l,wellFormed )
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredStatement createStatement( final Resource s,
			final Property p, final String o, final String l,
			final boolean wellFormed ) throws AccessDeniedException;

	@Override
	public SecuredLiteral createTypedLiteral( final boolean v );

	@Override
	public Literal createTypedLiteral( final Calendar d );

	@Override
	public SecuredLiteral createTypedLiteral( final char v );

	@Override
	public SecuredLiteral createTypedLiteral( final double v );

	@Override
	public SecuredLiteral createTypedLiteral( final float v );

	@Override
	public SecuredLiteral createTypedLiteral( final int v );

	@Override
	public SecuredLiteral createTypedLiteral( final long v );

	@Override
	public SecuredLiteral createTypedLiteral( final Object value );

	@Override
	public SecuredLiteral createTypedLiteral( final Object value,
			final RDFDatatype dtype );

	@Override
	public SecuredLiteral createTypedLiteral( final Object value,
			final String typeURI );

	@Override
	public SecuredLiteral createTypedLiteral( final String v );

	@Override
	public SecuredLiteral createTypedLiteral( final String lex,
			final RDFDatatype dtype );

	@Override
	public SecuredLiteral createTypedLiteral( final String lex,
			final String typeURI );

	/**
	 * @graphSec Read
	 * @tripleSec Read for every triple contributed to the difference.
	 * @throws AccessDeniedException
	 */
	@Override
	public Model difference( final Model model ) throws AccessDeniedException;

	/**
	 * @graphSec Read if read lock is requested
	 * @graphSec Update if write lock is requested
	 * @throws AccessDeniedException
	 */
	@Override
	public void enterCriticalSection( final boolean readLockRequested )
			throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @throws AccessDeniedException
	 */
	@Override
	public String expandPrefix( final String prefixed )
			throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read Triple( r, RDF.type, RDF.alt )
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredAlt getAlt( final Resource r ) throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read Triple( uri, RDF.type, RDF.alt )
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredAlt getAlt( final String uri ) throws AccessDeniedException;

	/**
	 * @graphSec Read if statement exists
	 * @graphSec Update if statement does not exist
	 * @tripleSec Read s as a triple
	 * @tripleSec Read Triple( result, RDF.subject, s.getSubject() ) if
	 *            reification existed
	 * @tripleSec Read Triple( result, RDF.predicate, s.getPredicate() ) if
	 *            reification existed
	 * @tripleSec Read Triple( result, RDF.object, s.getObject() ) if
	 *            reification existed
	 * @tripleSec Create Triple( result, RDF.subject, s.getSubject() ) if
	 *            reification did not exist.
	 * @tripleSec Create Triple( result, RDF.redicate, s.getPredicate() ) if
	 *            reification did not exist
	 * @tripleSec Create Triple( result, RDF.object, s.getObject() ) if
	 *            reification did not exist
	 * @throws AccessDeniedException.
	 */
	@Override
	public SecuredResource getAnyReifiedStatement( final Statement s )
			throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read Triple( r, RDF.type, RDF.Bag )
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredBag getBag( final Resource r ) throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read Triple( uri, RDF.type, RDF.Bag )
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredBag getBag( final String uri ) throws AccessDeniedException;

	@Override
	public SecuredGraph getGraph();

	/**
	 * @graphSec Read
	 * @tripleSec Read on the returned statement.
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredStatement getProperty( final Resource s, final Property p )
			throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @throws AccessDeniedException
	 */
	@Override
	public Property getProperty( final String uri )
			throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @throws AccessDeniedException
	 */
	@Override
	public Property getProperty( final String nameSpace, final String localName )
			throws AccessDeniedException;

	/**
	 * @graphSec Read if the node exists
	 * @graphSec Update if the node does not exist
	 * @throws AccessDeniedException
	 */
	@Override
	public RDFNode getRDFNode( final Node n ) throws AccessDeniedException;

	/**
	 * .
	 * If the PropertyNotFoundException was thrown by the enclosed model and the
	 * user can not read Triple(s, p, Node.ANY) AccessDeniedExcepiton is thrown,
	 * otherwise the PropertyNotFoundException will be thrown.
	 * 
	 * @graphSec Read
	 * @tripleSec Read on the returned statement
	 * @tripleSec Read on Triple(s, p, Node.ANY) if PropertyNotFoundException
	 *            was thrown
	 * @throws AccessDeniedException
	 * @throws PropertyNotFoundException
	 */
	@Override
	public SecuredStatement getRequiredProperty( final Resource s,
			final Property p ) throws PropertyNotFoundException,
			AccessDeniedException;

	@Override
	public SecuredResource getResource( final String uri );

	@Override
	@Deprecated
	public SecuredResource getResource( final String uri, final ResourceF f );

	/**
	 * 
	 * @graphSec Read
	 * @tripleSec Read on Triple(r, RDF.type, RDF.Seq)
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredSeq getSeq( final Resource r ) throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Read
	 * @tripleSec Read on Triple(uri, RDF.type, RDF.Seq)
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredSeq getSeq( final String uri ) throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Read
	 * @tripleSec Read on all triples contributed to the new model.
	 * @throws AccessDeniedException
	 */
	@Override
	public Model intersection( final Model model ) throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Read
	 * @throws AccessDeniedException
	 */
	@Override
	public boolean isEmpty() throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Read
	 * @tripleSec Read all compared triples. Triples that can not be read will
	 *            not be compared.
	 * @throws AccessDeniedException
	 */
	@Override
	public boolean isIsomorphicWith( final Model g )
			throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Read
	 * @tripleSec Read on s as triple
	 * @tripleSec Read on at least one set reified statements.
	 * @throws AccessDeniedException
	 */
	@Override
	public boolean isReified( final Statement s ) throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Read
	 * @tripleSec Read on all triples returned.
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredStatementIterator listLiteralStatements(
			final Resource subject, final Property predicate,
			final boolean object ) throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Read
	 * @tripleSec Read on all triples returned.
	 * @throws AccessDeniedException
	 */

	@Override
	public SecuredStatementIterator listLiteralStatements(
			final Resource subject, final Property predicate, final char object )
			throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Read
	 * @tripleSec Read on all triples returned.
	 * @throws AccessDeniedException
	 */

	@Override
	public SecuredStatementIterator listLiteralStatements(
			final Resource subject, final Property predicate,
			final double object ) throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Read
	 * @tripleSec Read on all triples returned.
	 * @throws AccessDeniedException
	 */

	@Override
	public SecuredStatementIterator listLiteralStatements(
			final Resource subject, final Property predicate, final float object )
			throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Read
	 * @tripleSec Read on all triples returned.
	 * @throws AccessDeniedException
	 */

	@Override
	public SecuredStatementIterator listLiteralStatements(
			final Resource subject, final Property predicate, final long object )
			throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Read
	 * @throws AccessDeniedException
	 */
	@Override
	public NsIterator listNameSpaces() throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Read
	 * @tripleSec Read on each RDFNode returned
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredNodeIterator listObjects() throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Read
	 * @tripleSec Read on each RDFNode returned
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredNodeIterator listObjectsOfProperty( final Property p )
			throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Read
	 * @tripleSec Read on each RDFNode returned
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredNodeIterator listObjectsOfProperty( final Resource s,
			final Property p ) throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Read
	 * @tripleSec Read on each Reified statement returned
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredRSIterator listReifiedStatements()
			throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Read
	 * @tripleSec Read on each Reified statement returned
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredRSIterator listReifiedStatements( final Statement st )
			throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Read
	 * @tripleSec Read at least one Triple( resource, p, o ) for each resource
	 *            returned;
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredResIterator listResourcesWithProperty( final Property p )
			throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Read
	 * @tripleSec Read at least one Triple( resource, p, o ) for each resource
	 *            returned;
	 * @throws AccessDeniedException
	 */

	@Override
	public SecuredResIterator listResourcesWithProperty( final Property p,
			final boolean o ) throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Read
	 * @tripleSec Read at least one Triple( resource, p, o ) for each resource
	 *            returned;
	 * @throws AccessDeniedException
	 */

	@Override
	public SecuredResIterator listResourcesWithProperty( final Property p,
			final char o ) throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Read
	 * @tripleSec Read at least one Triple( resource, p, o ) for each resource
	 *            returned;
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredResIterator listResourcesWithProperty( final Property p,
			final double o ) throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Read
	 * @tripleSec Read at least one Triple( resource, p, o ) for each resource
	 *            returned;
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredResIterator listResourcesWithProperty( final Property p,
			final float o ) throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Read
	 * @tripleSec Read at least one Triple( resource, p, o ) for each resource
	 *            returned;
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredResIterator listResourcesWithProperty( final Property p,
			final long o ) throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Read
	 * @tripleSec Read at least one Triple( resource, p, o ) for each resource
	 *            returned;
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredResIterator listResourcesWithProperty( final Property p,
			final Object o ) throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Read
	 * @tripleSec Read at least one Triple( resource, p, o ) for each resource
	 *            returned
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredResIterator listResourcesWithProperty( final Property p,
			final RDFNode o ) throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Read
	 * @tripleSec Read on all triples returned
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredStatementIterator listStatements()
			throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Read
	 * @tripleSec Read on all triples returned
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredStatementIterator listStatements( final Resource s,
			final Property p, final RDFNode o ) throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Read
	 * @tripleSec Read on all triples returned
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredStatementIterator listStatements( final Resource subject,
			final Property predicate, final String object )
			throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Read
	 * @tripleSec Read on all triples returned
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredStatementIterator listStatements( final Resource subject,
			final Property predicate, final String object, final String lang )
			throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Read
	 * @tripleSec Read on all triples returned
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredStatementIterator listStatements( final Selector s )
			throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Read
	 * @tripleSec Read at least one Triple( resource, p, o ) for each resource
	 *            returned
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredResIterator listSubjects() throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Read
	 * @tripleSec Read at least one Triple( resource, p, o ) for each resource
	 *            returned
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredResIterator listSubjectsWithProperty( final Property p )
			throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Read
	 * @tripleSec Read at least one Triple( resource, p, o ) for each resource
	 *            returned
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredResIterator listSubjectsWithProperty( final Property p,
			final RDFNode o ) throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Read
	 * @tripleSec Read at least one Triple( resource, p, o ) for each resource
	 *            returned
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredResIterator listSubjectsWithProperty( final Property p,
			final String o ) throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Read
	 * @tripleSec Read at least one Triple( resource, p, o ) for each resource
	 *            returned
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredResIterator listSubjectsWithProperty( final Property p,
			final String o, final String l ) throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Update
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredPrefixMapping lock() throws AccessDeniedException;

	@Override
	public SecuredModel notifyEvent( final Object e );

	/**
	 * 
	 * @graphSec Read
	 * @throws AccessDeniedException
	 */
	@Override
	public String qnameFor( final String uri ) throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Read
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredModel query( final Selector s ) throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Update
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredModel read( final InputStream in, final String base )
			throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Update
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredModel read( final InputStream in, final String base,
			final String lang ) throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Update
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredModel read( final Reader reader, final String base )
			throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Update
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredModel read( final Reader reader, final String base,
			final String lang ) throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Update
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredModel read( final String url ) throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Update
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredModel read( final String url, final String lang )
			throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Update
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredModel read( final String url, final String base,
			final String lang ) throws AccessDeniedException;

	/**
	 * 
	 * Listener will be filtered to only report events that the user can see.
	 * 
	 * @graphSec Read
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredModel register( final ModelChangedListener listener )
			throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Update
	 * @tripleSec Delete on every statement in statments.
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredModel remove( final List<Statement> statements )
			throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Update
	 * @tripleSec Delete on every statement in baseModel.
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredModel remove( final Model m ) throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Update
	 * @tripleSec Delete on every statement in baseModel.
	 * @tripleSec Delete on every statement in reified statements if
	 *            suppressReifications is false.
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredModel remove( final Model m,
			final boolean suppressReifications ) throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Update
	 * @tripleSec Delete on Triple( s, p, o )
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredModel remove( final Resource s, final Property p,
			final RDFNode o ) throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Update
	 * @tripleSec Delete on statment.
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredModel remove( final Statement s )
			throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Update
	 * @tripleSec Delete on every statement in statments.
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredModel remove( final Statement[] statements )
			throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Update
	 * @tripleSec Delete on every statement in iter.
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredModel remove( final StmtIterator iter )
			throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Update
	 * @tripleSec Delete on every statement in the model
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredModel removeAll() throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Update
	 * @tripleSec Delete on every statement identified by Triple( s,p,o)
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredModel removeAll( final Resource s, final Property p,
			final RDFNode r ) throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Update
	 * @tripleSec Delete on every reification statement for each statement in
	 *            statments.
	 * @throws AccessDeniedException
	 */
	@Override
	public void removeAllReifications( final Statement s )
			throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Update
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredPrefixMapping removeNsPrefix( final String prefix )
			throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Update
	 * @tripleSec Delete on every reification statement fore each statement in
	 *            rs.
	 * @throws AccessDeniedException
	 */
	@Override
	public void removeReification( final ReifiedStatement rs )
			throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Update
	 * @throws AccessDeniedException
	 */
	@Override
	public String setReaderClassName( final String lang, final String className )
			throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Update
	 * @throws AccessDeniedException
	 */
	@Override
	public String setWriterClassName( final String lang, final String className )
			throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Read
	 * @throws AccessDeniedException
	 */
	@Override
	public String shortForm( final String uri ) throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Read
	 * @throws AccessDeniedException
	 */
	@Override
	public long size() throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Read
	 * @tripleSec Read on all statements contributed to the union.
	 * @throws AccessDeniedException
	 */
	@Override
	public Model union( final Model model ) throws AccessDeniedException;

	@Override
	public SecuredModel unregister( final ModelChangedListener listener );

	@Override
	public SecuredResource wrapAsResource( final Node n );

	/**
	 * 
	 * @graphSec Read
	 * @tripleSec Read on all statements that are written.
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredModel write( final OutputStream out )
			throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Read
	 * @tripleSec Read on all statements that are written.
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredModel write( final OutputStream out, final String lang )
			throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Read
	 * @tripleSec Read on all statements that are written.
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredModel write( final OutputStream out, final String lang,
			final String base ) throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Read
	 * @tripleSec Read on all statements that are written.
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredModel write( final Writer writer )
			throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Read
	 * @tripleSec Read on all statements that are written.
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredModel write( final Writer writer, final String lang )
			throws AccessDeniedException;

	/**
	 * 
	 * @graphSec Read
	 * @tripleSec Read on all statements that are written.
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredModel write( final Writer writer, final String lang,
			final String base ) throws AccessDeniedException;

}
