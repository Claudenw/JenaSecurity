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

import java.security.Principal;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * SecurityEvaluator.
 * 
 * The security evaluator is the link between the graph security system and an
 * external
 * security system. This interface specifies the methods that are required by
 * the graph
 * security system. It is assumed that the implementation will handle tracking
 * the current
 * user and will query some underlying data source to determine what actions the
 * user can
 * and can not take.
 * 
 * All quesitons of whitelisting or blacklisting will be handled in the concrete
 * implementation.
 * 
 * Implementations of this class should probably cache any evaluate calculations
 * as the evaluate methods are called frequently.
 * 
 * Note on triple checks:
 * 
 * If any s,p or o is Node.ANY then the methods must return false if there are
 * any restrictions where the remaining nodes and held constant and the ANY node
 * is allowed to vary.
 * examples:
 * (S, P, O) check if the action can be performed on S,P,O .
 * (S, P, ANY) check if the action can be performed on all S,P,x triples.
 * (S, ANY, P) check if the action can be performed on all S,x,P triples.
 * (ANY, ANY, ANY) check if action can be performed on all triples in the graph.
 * 
 * The (ANY, ANY, ANY) pattern is used to determine if the system has any
 * restrictions that should
 * be checked when dealing with collections of triples (e.g. delete(Triple[])).
 * If (ANY,ANY,ANY)
 * returns true then we don't have to check each of the triples in the
 * collection.
 * 
 */
public interface SecurityEvaluator
{
	static enum Action
	{
		Create, Read, Update, Delete
	}

	/**
	 * A node in the evaluation.
	 * 
	 * A node with no value represents a node of that type but unknown
	 * exactitude. (e.g.
	 * Node(URI,"") is a URI but of unknown value. Useful for systems that
	 * restrict
	 * type creation.
	 * 
	 * Node(Anymous,"") represents an anymous node that will be created.
	 * Otherwise anonymous
	 * node values are the values within the secured graph.
	 * 
	 * An "Any" node type matches any node.
	 * 
	 * 
	 */
	public static class Node implements Comparable<Node>
	{

		public static enum Type
		{
			URI, Literal, Anonymous, Any
		}

		/**
		 * Matches any node in the secrity system.
		 * Asking (Node, Node.ANY, Node.ANY) is asking if there 
		 * are any explicit using the Node as a Subject.
		 */
		public static final Node ANY = new Node(Type.Any, "any");
		/**
		 * Ignore the nodes in this position in the triple.
		 * Asking (Node, Node.IGNORE, Node.IGNORE) is asking if there 
		 * are any implicit restrictions on using the Node as a Subject.
		 */
		public static final Node IGNORE = new Node(Type.Any, "ignore");
		
		/**
		 * This is an anonymous node that will be created in the future.
		 * FUTURE is used to check that an anonymous node may be created in
		 * as specific position in a triple.
		 */
		public static final Node FUTURE = new Node(Type.Anonymous, "");

		private final Type type;
		private final String value;
		private Integer hashCode;

		public Node( final Type type, final String value )
		{
			this.type = type;
			this.value = value == null ? "" : value;
		}

		@Override
		public int compareTo( final Node node )
		{
			final int retval = type.compareTo(node.type);
			return retval == 0 ? value.compareTo(node.value) : retval;
		}

		@Override
		public boolean equals( final Object o )
		{
			if (o instanceof Node)
			{
				return this.compareTo((Node) o) == 0;
			}
			return false;
		}

		public Type getType()
		{
			return type;
		}

		public String getValue()
		{
			return value;
		}

		@Override
		public int hashCode()
		{
			if (hashCode == null)
			{
				hashCode = new HashCodeBuilder().append(type).append(value)
						.toHashCode();
			}
			return hashCode;
		}
		
		@Override
		public String toString()
		{
			return String.format( "[%s:%s]", getType(), getValue() );
		}
	}

	/**
	 * A triple of nodes.
	 * 
	 */
	public static class Triple implements Comparable<Triple>
	{
		private final Node subject;
		private final Node predicate;
		private final Node object;
		private transient Integer hashCode;

		public static final Triple ANY = new Triple(Node.ANY, Node.ANY,
				Node.ANY);

		public Triple( final Node subject, final Node predicate,
				final Node object )
		{
			if (subject == null)
			{
				throw new IllegalArgumentException("Subject may not be null");
			}
			if (predicate == null)
			{
				throw new IllegalArgumentException("Predicate may not be null");
			}
			if (object == null)
			{
				throw new IllegalArgumentException("Object may not be null");
			}
			this.subject = subject;
			this.predicate = predicate;
			this.object = object;
		}

		@Override
		public int compareTo( final Triple o )
		{
			if (o == null)
			{
				return 1;
			}
			int retval = subject.compareTo(o.subject);
			if (retval == 0)
			{
				retval = predicate.compareTo(o.predicate);
			}
			return retval == 0 ? object.compareTo(o.object) : retval;
		}

		@Override
		public boolean equals( final Object o )
		{
			if (o instanceof Triple)
			{
				return this.compareTo((Triple) o) == 0;
			}
			return false;
		}

		public Node getObject()
		{
			return object;
		}

		public Node getPredicate()
		{
			return predicate;
		}

		public Node getSubject()
		{
			return subject;
		}

		@Override
		public int hashCode()
		{
			if (hashCode == null)
			{
				hashCode = new HashCodeBuilder().append(object)
						.append(predicate).append(subject).toHashCode();
			}
			return hashCode;
		}

	}

	public static class Util
	{
		public static Set<Action> asSet( final Action[] actions )
		{
			return Util.asSet(Arrays.asList(actions));
		}

		public static Set<Action> asSet( final Collection<Action> actions )
		{
			if (actions instanceof Set)
			{
				return (Set<Action>) actions;
			}
			else
			{
				return new LinkedHashSet<Action>(actions);
			}
		}
	}

	/**
	 * Determine if the action is allowed on the graph.
	 * 
	 * @param action
	 *            The action to perform
	 * @param graphIRI
	 *            The IRI of the graph to check
	 * @return true if the action is allowed, false otherwise.
	 */
	public boolean evaluate( Action action, Node graphIRI );

	/**
	 * Determine if the action is allowed on the triple within the graph.
	 * If any s,p or o is Node.ANY then this method must return false if there
	 * are
	 * any restrictions where the remaining nodes and held constant and the ANY
	 * node
	 * is allowed to vary.
	 * 
	 * (S, P, O) check if action can be performed S,P,O .
	 * (S, P, ANY) check if action can be performed for all S,P,x triples.
	 * (S, ANY, P) check if action can be performed for all S,x,P triples.
	 * (ANY, ANY, ANY) check if action can be performed all triples.
	 * 
	 * @param action
	 *            The action to perform
	 * @param graphIRI
	 *            The IRI of the graph to check.
	 * @param triple
	 *            The triple to check
	 * @return true if the action is allowed, false otherwise.
	 */
	public boolean evaluate( Action action, Node graphIRI, Triple triple );

	/**
	 * Determine if the action is allowed on the triple.
	 * 
	 * @param action
	 *            The action to perform
	 * @param graphIRI
	 *            The IRI of the graph to check.
	 * @return true if all the actions are allowed, false otherwise.
	 */
	public boolean evaluate( Set<Action> action, Node graphIRI );

	/**
	 * Determine if the action is allowed on the triple within the graph.
	 * 
	 * If any s,p or o is Node.ANY then this method must return false if there
	 * are
	 * any restrictions where the remaining nodes and held constant and the ANY
	 * node
	 * is allowed to vary.
	 * 
	 * See evaluate( Action, Triple for examples)
	 * 
	 * @param action
	 *            The action to perform.
	 * @param graphIRI
	 *            The IRI of the graph to check.
	 * @param triple
	 *            The triple to check
	 * @return true if all the actions are allowed, false otherwise.
	 */
	public boolean evaluate( Set<Action> action, Node graphIRI, Triple triple );

	/**
	 * Determine if the action is allowed on the triple.
	 * 
	 * @param action
	 *            The action to perform
	 * @param graphIRI
	 *            The IRI of the graph to check.
	 * @return true true if any the actions are allowed, false otherwise.
	 */
	public boolean evaluateAny( Set<Action> action, Node graphIRI );

	/**
	 * Determine if the action is allowed on the triple.
	 * If any s,p or o is Node.ANY then this method must return false if there
	 * are
	 * any restrictions where the remaining nodes and held constant and the ANY
	 * node
	 * is allowed to vary.
	 * 
	 * See evaluate( Action, Triple for examples)
	 * 
	 * @param action
	 *            The action to perform
	 * @param graphIRI
	 *            The IRI of the graph to check.
	 * @param triple
	 *            The triple to check
	 * @return true if any the actions are allowed, false otherwise.
	 */
	public boolean evaluateAny( Set<Action> action, Node graphIRI, Triple triple );

	/**
	 * Determine if the user is allowed to update the "from" triple to the "to" triple.
	 * @param graphIRI The IRI for the graph
	 * @param from The triple to be changed
	 * @param to The value to change it to.
	 * @return true if the user may make the change, false otherwise.
	 */
	public boolean evaluateUpdate( Node graphIRI, Triple from, Triple to );

	/**
	 * returns the current principal or null if there is no current principal.
	 * 
	 * All security evaluation methods should use this method to determine who
	 * the call is being executed as.
	 * 
	 * This allows subsystems (like the listener system) to capture the current
	 * user
	 * and evaluate later calls in terms of that user.
	 * 
	 * @return
	 */
	public Principal getPrincipal();
}
