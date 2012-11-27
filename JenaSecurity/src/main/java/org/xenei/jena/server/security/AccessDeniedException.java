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

package org.xenei.jena.server.security;

import org.xenei.jena.server.security.SecurityEvaluator.Action;
import org.xenei.jena.server.security.SecurityEvaluator.Node;

/**
 * Exception thrown by the security system when an action is not allowed.
 * 
 * Contains the graphIRI and the action that was not allowed.
 * 
 * Note: The presence of the graphIRI may leak data to the end user. In cases
 * where
 * this is not an issue set the static variable NO_IRI.
 */
public class AccessDeniedException extends RuntimeException
{
	private static final long serialVersionUID = 2789332975364811725L;
	public static boolean NO_IRI = false;

	public AccessDeniedException()
	{
		super();
	}

	public AccessDeniedException( final Node uri, final Action action )
	{
		super(AccessDeniedException.NO_IRI ? action.toString() : String.format(
				"%s: %s", uri, action));
	}
}
