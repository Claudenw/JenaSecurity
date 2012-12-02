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
import com.hp.hpl.jena.rdf.model.ReifiedStatement;

import org.xenei.jena.server.security.ItemHolder;
import org.xenei.jena.server.security.SecuredItem;
import org.xenei.jena.server.security.SecuredItemInvoker;
import org.xenei.jena.server.security.SecurityEvaluator;
import org.xenei.jena.server.security.model.SecuredModel;
import org.xenei.jena.server.security.model.SecuredReifiedStatement;
import org.xenei.jena.server.security.model.SecuredStatement;

/**
 * Implementation of SecuredReifiedStatement to be used by a SecuredItemInvoker
 * proxy.
 */
public class SecuredReifiedStatementImpl extends SecuredResourceImpl implements
		SecuredReifiedStatement
{
	// the item holder that contains this SecuredResource
	private final ItemHolder<? extends ReifiedStatement, ? extends SecuredReifiedStatement> holder;

	/**
	 * Constructor
	 * 
	 * @param securityEvaluator
	 *            The security evaluator to use
	 * @param graphIRI
	 *            the graph IRI to verify against.
	 * @param holder
	 *            the item holder that will contain this SecuredReifiedStatement
	 */
	protected SecuredReifiedStatementImpl(
			final SecuredModel securedModel,
			final ItemHolder<? extends ReifiedStatement, ? extends SecuredReifiedStatement> holder)
	{
		super(securedModel, holder);
		this.holder = holder;
	}

	@Override
	public SecuredStatement getStatement()
	{
		checkRead();
		return SecuredStatementImpl.getInstance(
				getModel(), holder.getBaseItem().getStatement());
	}

	/**
	 * Get an instance of SecuredReifiedStatement
	 * 
	 * @param securedItem
	 *            The securedItem that provides the security context
	 * @param stmt
	 *            The ReifiedStatement to secure.
	 * @return SecuredReifiedStatement
	 */
	static SecuredReifiedStatement getInstance( final SecuredModel securedModel,
			final ReifiedStatement stmt )
	{
		if (securedModel == null)
		{
			throw new IllegalArgumentException( "Secured model may not be null");
		}
		if (stmt == null)
		{
			throw new IllegalArgumentException( "Statement may not be null");
		}
		final ItemHolder<ReifiedStatement, SecuredReifiedStatement> holder = new ItemHolder<ReifiedStatement, SecuredReifiedStatement>(
				stmt);
		final SecuredReifiedStatementImpl checker = new SecuredReifiedStatementImpl(
				securedModel,
				holder);
		// if we are going to create a duplicate proxy, just return this
		// one.
		if (stmt instanceof SecuredReifiedStatement)
		{
			if (checker.isEquivalent((SecuredReifiedStatement) stmt))
			{
				return (SecuredReifiedStatement) stmt;
			}
		}
		return holder.setSecuredItem(new SecuredItemInvoker(stmt.getClass(),
				checker));
	}

}
