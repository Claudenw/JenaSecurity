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

import org.xenei.jena.security.SecurityEvaluator.Action;
import org.xenei.jena.security.SecurityEvaluator.SecNode;

/**
 * Exception thrown by the security system when an action is not allowed.
 * 
 * Contains the graphIRI and the action that was not allowed.
 */
public class AccessDeniedException extends RuntimeException
{
	private static final long serialVersionUID = 2789332975364811725L;

	String triple;

	public AccessDeniedException( final SecNode uri, final Action action )
	{
		super(String.format("securedModel sec. %s: %s", uri, action));
	}

	public AccessDeniedException( final SecNode uri, final String triple,
			final Action action )
	{
		super(String.format("triple sec. %s: %s", uri, action));
		this.triple = triple;
	}

	public String getTriple()
	{
		return triple;
	}

}
