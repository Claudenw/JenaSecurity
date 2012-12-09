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
package org.xenei.jena.security.graph;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Reifier;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.TripleMatch;
import com.hp.hpl.jena.shared.AddDeniedException;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import org.xenei.jena.security.AccessDeniedException;
import org.xenei.jena.security.SecuredItem;

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
	 * @tripleSec Read SecTriple(n, RDF.subject.asNode(), t.getSubject())
	 * @tripleSec Read SecTriple(n, RDF.predicate.asNode(), t.getPredicate())
	 * @tripleSec Read SecTriple(n, RDF.object.asNode(), t.getObject())
	 * @throws AccessDeniedException
	 */
	@Override
	public Triple getTriple( final Node n ) throws AccessDeniedException;

	/**
	 * Permissions match the graph.add(t) method to avoid unauthorized insertion.
	 * @graphSec Update
	 * @tripleSec Create
	 * @throws AccessDeniedException
	 * @throws AddDeniedException
	 */
	@Override
	public boolean handledAdd( final Triple t ) throws AccessDeniedException;

	/**
	 * permission match the graph.delete(t) method to avoid unauthorized deletion.
	 * @graphSec Update
	 * @tripleSec Delete
	 * @throws AccessDeniedException
	 * @throws AddDeniedException
	 */
	@Override
	public boolean handledRemove( final Triple t ) throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read on one of SecTriple(n, RDF.subject.asNode(), SecNode.ANY)
	 * @tripleSec Read SecTriple(n, RDF.predicate.asNode(), SecNode.ANY)
	 * @tripleSec Read SecTriple(n, RDF.object.asNode(), SecNode.ANY)
	 * @throws AccessDeniedException
	 */
	@Override
	public boolean hasTriple( final Node n ) throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read on one of Read SecTriple(SecNode.ANY, RDF.subject.asNode(), t.getSubject())
	 * @tripleSec Read SecTriple(SecNode.ANY, RDF.predicate.asNode(),
	 *            t.getPredicate())
	 * @tripleSec Read SecTriple(SecNode.ANY, RDF.object.asNode(), t.getObject())
	 * @throws AccessDeniedException
	 */
	@Override
	public boolean hasTriple( final Triple t ) throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Read t
	 * @tripleSec Create SecTriple(n, RDF.subject.asNode(), t.getSubject())
	 * @tripleSec Create SecTriple(n, RDF.predicate.asNode(), t.getPredicate())
	 * @tripleSec Create SecTriple(n, RDF.object.asNode(), t.getObject())
	 * @throws AccessDeniedException
	 */
	@Override
	public Node reifyAs( final Node n, final Triple t )
			throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Read t,
	 * @tripleSec Delete SecTriple(n, RDF.subject.asNode(), t.getSubject())
	 * @tripleSec Delete SecTriple(n, RDF.predicate.asNode(), t.getPredicate())
	 * @tripleSec Delete SecTriple(n, RDF.object.asNode(), t.getObject())
	 * @throws AccessDeniedException
	 */
	@Override
	public void remove( final Node n, final Triple t )
			throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Read t,
	 * @tripleSec Delete SecTriple(SecNode.ANY, RDF.subject.asNode(), t.getSubject())
	 * @tripleSec Delete SecTriple(SecNode.ANY, RDF.predicate.asNode(),
	 *            t.getPredicate())
	 * @tripleSec Delete SecTriple(SecNode.ANY, RDF.object.asNode(), t.getObject())
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
