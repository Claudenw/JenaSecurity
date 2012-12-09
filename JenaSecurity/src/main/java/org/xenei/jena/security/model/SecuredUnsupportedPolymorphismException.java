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

import com.hp.hpl.jena.enhanced.EnhGraph;
import com.hp.hpl.jena.enhanced.UnsupportedPolymorphismException;

import org.xenei.jena.security.graph.impl.SecuredGraphImpl;
import org.xenei.jena.security.model.impl.SecuredModelImpl;
import org.xenei.jena.security.model.impl.SecuredRDFNodeImpl;

/**
 * An extension to the UnsupportedPolymorphismException to handle
 * secured polymorphic changes.
 */
public class SecuredUnsupportedPolymorphismException extends
		UnsupportedPolymorphismException
{
	private final SecuredRDFNodeImpl node;

	public SecuredUnsupportedPolymorphismException(
			final SecuredRDFNodeImpl node, final Class<?> type )
	{
		super(null, type);
		this.node = node;
	}

	@Override
	public EnhGraph getBadGraph()
	{
		final SecuredModelImpl model = (SecuredModelImpl) node.getModel();
		final SecuredGraphImpl graph = (SecuredGraphImpl) model.getGraph();
		final Object o = graph.getBaseItem();
		if (o instanceof EnhGraph)
		{
			return (EnhGraph) o;
		}
		return null;
	}

	@Override
	public Object getBadNode()
	{
		return node;
	}

}
