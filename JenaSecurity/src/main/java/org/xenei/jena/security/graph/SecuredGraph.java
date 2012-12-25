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

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.GraphStatisticsHandler;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.TripleMatch;
import com.hp.hpl.jena.shared.AddDeniedException;
import com.hp.hpl.jena.shared.DeleteDeniedException;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import org.xenei.jena.security.AccessDeniedException;
import org.xenei.jena.security.SecurityEvaluator;
import org.xenei.jena.security.SecurityEvaluator.SecNode;

/**
 * The interface for secured Graph instances.
 * 
 * Use the SecuredGraph.Factory to create instances
 */
public interface SecuredGraph extends Graph
{

	/**
	 * @graphSec Update
	 * @tripleSec Create
	 * @throws AccessDeniedException
	 * @throws AddDeniedException
	 */
	@Override
	public void add( final Triple t ) throws AddDeniedException,
			AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read
	 * @throws AccessDeniedException
	 */
	@Override
	public boolean contains( final Node s, final Node p, final Node o )
			throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read
	 * @throws AccessDeniedException
	 */
	@Override
	public boolean contains( final Triple t ) throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Delete
	 * @throws AccessDeniedException
	 * @throws DeleteDeniedException
	 */
	@Override
	public void delete( final Triple t ) throws DeleteDeniedException,
			AccessDeniedException;

	/**
	 * @graphSec Read
	 * @throws AccessDeniedException
	 */
	@Override
	public boolean dependsOn( final Graph other ) throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read, otherwise filtered from iterator.
	 * @throws AccessDeniedException
	 */
	@Override
	public ExtendedIterator<Triple> find( final Node s, final Node p,
			final Node o ) throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read, otherwise filtered from iterator.
	 * @throws AccessDeniedException
	 */
	@Override
	public ExtendedIterator<Triple> find( final TripleMatch m )
			throws AccessDeniedException;

	@Override
	public SecuredBulkUpdateHandler getBulkUpdateHandler();

	@Override
	public SecuredCapabilities getCapabilities();

	@Override
	public SecuredGraphEventManager getEventManager();

	public SecNode getModelNode();

	@Override
	public SecuredPrefixMapping getPrefixMapping();

	public SecurityEvaluator getSecurityEvaluator();

	/**
	 * @graphSec Read
	 * @throws AccessDeniedException
	 */
	@Override
	public GraphStatisticsHandler getStatisticsHandler()
			throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @throws AccessDeniedException
	 */
	@Override
	public boolean isEmpty() throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read
	 * @throws AccessDeniedException
	 */
	@Override
	public boolean isIsomorphicWith( final Graph g )
			throws AccessDeniedException;


	/**
	 * @graphSec Read
	 * @throws AccessDeniedException
	 */
	@Override
	public int size() throws AccessDeniedException;
	
	/**
	 * @graphSec Update
	 * @tripleSec Delete for every triple
	 * @throws AccessDeniedException
	 */
	@Override
	public void clear();

	/**
	 * @graphSec Update
	 * @tripleSec Delete (s, p, o )
	 * @throws AccessDeniedException
	 */
	@Override
	public void remove( Node s, Node p, Node o );

}
