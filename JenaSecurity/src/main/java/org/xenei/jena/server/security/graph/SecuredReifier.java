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
package org.xenei.jena.server.security.graph;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Reifier;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.TripleMatch;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import org.xenei.jena.server.security.AccessDeniedException;
import org.xenei.jena.server.security.SecuredItem;

/**
 * The interface for secured Reifier instances.
 * 
 * Use the SecuredReifier.Factory to create instances
 */
public interface SecuredReifier extends Reifier, SecuredItem
{

	/**
	 * @graphSec Read
	 * @tripleSec Read on triple containing node, otherwise not in iterator.
	 * @throws AccessDeniedException
	 */
	@Override
	public ExtendedIterator<Node> allNodes() throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read on t otherwise throw AccessDenied
	 * @tripleSec Read returned triple containing node, otherwise not in
	 *            iterator.
	 * @throws AccessDeniedException
	 */
	@Override
	public ExtendedIterator<Node> allNodes( final Triple t )
			throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read returned triple otherwise not in iterator.
	 * @throws AccessDeniedException
	 */
	@Override
	public ExtendedIterator<Triple> find( final TripleMatch m )
			throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read returned triple otherwise not in iterator.
	 * @throws AccessDeniedException
	 */
	@Override
	public ExtendedIterator<Triple> findEither( final TripleMatch m,
			final boolean showHidden ) throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read returned triple otherwise not in iterator.
	 * @throws AccessDeniedException
	 */
	@Override
	public ExtendedIterator<Triple> findExposed( final TripleMatch m )
			throws AccessDeniedException;

	@Override
	public SecuredGraph getParentGraph();

	/**
	 * @graphSec Read
	 * @tripleSec Read returned triple,
	 * @tripleSec Read Triple(n, RDF.subject.asNode(), t.getSubject())
	 * @tripleSec Read Triple(n, RDF.predicate.asNode(), t.getPredicate())
	 * @tripleSec Read Triple(n, RDF.object.asNode(), t.getObject())
	 * @throws AccessDeniedException
	 */
	@Override
	public Triple getTriple( final Node n ) throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read t
	 * @tripleSec Read Triple(Node.ANY, RDF.subject.asNode(), t.getSubject())
	 * @tripleSec Read Triple(Node.ANY, RDF.predicate.asNode(),
	 *            t.getPredicate())
	 * @tripleSec Read Triple(Node.ANY, RDF.object.asNode(), t.getObject())
	 * @throws AccessDeniedException
	 */
	@Override
	public boolean handledAdd( final Triple t ) throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read t
	 * @tripleSec Read Triple(Node.ANY, RDF.subject.asNode(), t.getSubject())
	 * @tripleSec Read Triple(Node.ANY, RDF.predicate.asNode(),
	 *            t.getPredicate())
	 * @tripleSec Read Triple(Node.ANY, RDF.object.asNode(), t.getObject())
	 * @throws AccessDeniedException
	 */
	@Override
	public boolean handledRemove( final Triple t ) throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read Triple(n, RDF.subject.asNode(), Node.ANY)
	 * @tripleSec Read Triple(n, RDF.predicate.asNode(), Node.ANY)
	 * @tripleSec Read Triple(n, RDF.object.asNode(), Node.ANY)
	 * @throws AccessDeniedException
	 */
	@Override
	public boolean hasTriple( final Node n ) throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read t
	 * @tripleSec Read Triple(Node.ANY, RDF.subject.asNode(), t.getSubject())
	 * @tripleSec Read Triple(Node.ANY, RDF.predicate.asNode(),
	 *            t.getPredicate())
	 * @tripleSec Read Triple(Node.ANY, RDF.object.asNode(), t.getObject())
	 * @throws AccessDeniedException
	 */
	@Override
	public boolean hasTriple( final Triple t ) throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Read t
	 * @tripleSec Create Triple(n, RDF.subject.asNode(), t.getSubject())
	 * @tripleSec Create Triple(n, RDF.predicate.asNode(), t.getPredicate())
	 * @tripleSec Create Triple(n, RDF.object.asNode(), t.getObject())
	 * @throws AccessDeniedException
	 */
	@Override
	public Node reifyAs( final Node n, final Triple t )
			throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Read t,
	 * @tripleSec Delete Triple(n, RDF.subject.asNode(), t.getSubject())
	 * @tripleSec Delete Triple(n, RDF.predicate.asNode(), t.getPredicate())
	 * @tripleSec Delete Triple(n, RDF.object.asNode(), t.getObject())
	 * @throws AccessDeniedException
	 */
	@Override
	public void remove( final Node n, final Triple t )
			throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Read t,
	 * @tripleSec Delete Triple(Node.ANY, RDF.subject.asNode(), t.getSubject())
	 * @tripleSec Delete Triple(Node.ANY, RDF.predicate.asNode(),
	 *            t.getPredicate())
	 * @tripleSec Delete Triple(Node.ANY, RDF.object.asNode(), t.getObject())
	 * @throws AccessDeniedException
	 */
	@Override
	public void remove( final Triple t ) throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @throws AccessDeniedException
	 */
	@Override
	public int size() throws AccessDeniedException;
}
