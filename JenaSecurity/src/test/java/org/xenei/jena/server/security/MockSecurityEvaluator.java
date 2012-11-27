package org.xenei.jena.server.security;

import com.hp.hpl.jena.rdf.model.Resource;

import java.security.Principal;
import java.util.Collections;
import java.util.Set;

public class MockSecurityEvaluator implements SecurityEvaluator
{

	boolean loggedIn;
	boolean create;
	boolean read;
	boolean update;
	boolean delete;

	public MockSecurityEvaluator( final boolean loggedIn, final boolean create,
			final boolean read, final boolean update, final boolean delete )
	{
		this.loggedIn = loggedIn;
		this.create = create;
		this.read = read;
		this.update = update;
		this.delete = delete;
	}

	public boolean evaluate( final Action action )
	{
		switch (action)
		{
			case Read:
				return read;
			case Create:
				return create;
			case Update:
				return update;
			case Delete:
				return delete;
			default:
				throw new IllegalArgumentException();
		}
	}

	@Override
	public boolean evaluate( final Action action, final Node uri )
	{
		return evaluate(action);
	}

	@Override
	public boolean evaluate( final Action action, final Node graphIRI,
			final Triple triple )
	{
		return evaluate(action);
	}

	/**
	 * Answers the question. can the logged in user perform action on the
	 * object.
	 * 
	 * if there is no logged in user then anonymous access is assumed.
	 * 
	 * @param action
	 * @param object
	 * @return
	 */
	public boolean evaluate( final Action action, final Resource object )
	{

		return evaluate(action);
	}

	public boolean evaluate( final Action[] actions )
	{
		for (final Action a : actions)
		{
			if (!evaluate(a))
			{
				return false;
			}
		}
		return true;
	}

	public boolean evaluate( final Set<Action> action )
	{
		boolean result = true;
		for (final Action a : action)
		{
			result &= evaluate(a);
		}
		return result;
	}

	@Override
	public boolean evaluate( final Set<Action> action, final Node uri )
	{
		return evaluate(action);
	}

	@Override
	public boolean evaluate( final Set<Action> action, final Node graphIRI,
			final Triple triple )
	{
		for (final Action a : action)
		{
			if (!evaluate(a))
			{
				return false;
			}
		}
		return true;
	}

	public boolean evaluate( final Set<Action> action, final Resource object )
	{
		boolean result = true;
		for (final Action a : action)
		{
			result &= evaluate(a);
		}
		return result;
	}

	@Override
	public boolean evaluateAny( final Set<Action> action, final Node graphIRI )
	{
		for (final Action a : action)
		{
			if (evaluate(a))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean evaluateAny( final Set<Action> action, final Node graphIRI,
			final Triple triple )
	{
		return evaluateAny(action, graphIRI);
	}

	@Override
	public boolean evaluateUpdate( final Node graphIRI, final Triple from,
			final Triple to )
	{
		return evaluate(Action.Update);
	}

	public Set<Action> getPermissions( final Node uri )
	{
		return Collections.emptySet();
	}

	public Set<Action> getPermissions( final Resource resourceID )
	{
		return Collections.emptySet();
	}

	@Override
	public Principal getPrincipal()
	{
		if (loggedIn)
		{
			return new Principal() {

				@Override
				public String getName()
				{
					return "TestingPrincipal";
				}
			};
		}
		return null;
	}

	public boolean isLoggedIn()
	{
		return loggedIn;
	}

}
