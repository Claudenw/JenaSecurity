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

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;

import org.xenei.jena.server.security.ItemHolder;
import org.xenei.jena.server.security.SecurityEvaluator;
import org.xenei.jena.server.security.model.SecuredProperty;

/**
 * Implementation of SecuredProperty to be used by a SecuredItemInvoker proxy.
 */
public class SecuredPropertyImpl extends SecuredResourceImpl implements
		SecuredProperty
{
	// the item holder that contains this SecuredProperty
	private final ItemHolder<? extends Property, ? extends SecuredProperty> holder;

	/**
	 * Constructor
	 * 
	 * @param securityEvaluator
	 *            The security evaluator to use.
	 * @param graphIRI
	 *            the graph IRI to validate against.
	 * @param holder
	 *            The item holder that will contain this SecuredProperty.
	 */
	public SecuredPropertyImpl(
			final SecurityEvaluator securityEvaluator,
			final String graphIRI,
			final ItemHolder<? extends Property, ? extends SecuredProperty> holder )
	{
		super(securityEvaluator, graphIRI, holder);
		this.holder = holder;
	}

	@Override
	public int getOrdinal()
	{
		canRead();
		return holder.getBaseItem().getOrdinal();
	}

	@Override
	public Property inModel( final Model m )
	{
		return (Property) super.inModel(m);
	}

	@Override
	public boolean isProperty()
	{
		return true;
	}
}
