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

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.GraphStatisticsHandler;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.TripleMatch;
import com.hp.hpl.jena.graph.query.QueryHandler;
import com.hp.hpl.jena.shared.AddDeniedException;
import com.hp.hpl.jena.shared.DeleteDeniedException;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import org.xenei.jena.server.security.AccessDeniedException;
import org.xenei.jena.server.security.SecuredItem;

/**
 * The interface for secured Graph instances.
 * 
 * Use the SecuredGraph.Factory to create instances
 */
public interface SecuredGraph extends Graph, SecuredItem
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

	@Override
	public SecuredPrefixMapping getPrefixMapping();

	@Override
	public SecuredReifier getReifier();

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
	public QueryHandler queryHandler() throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @throws AccessDeniedException
	 */
	@Override
	public int size() throws AccessDeniedException;
}
