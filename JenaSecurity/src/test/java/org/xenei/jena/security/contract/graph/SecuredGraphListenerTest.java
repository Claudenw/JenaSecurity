package org.xenei.jena.security.contract.graph;

import com.hp.hpl.jena.graph.Factory;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.test.TestGraphListener;
import com.hp.hpl.jena.shared.ReificationStyle;

import org.xenei.jena.security.MockSecurityEvaluator;
import org.xenei.jena.security.SecurityEvaluator;

public class SecuredGraphListenerTest extends TestGraphListener
{
	private final SecurityEvaluator eval;

	public SecuredGraphListenerTest( final Class<? extends Graph> graphClass,
			final String name )
	{
		super(graphClass, name);
		eval = new MockSecurityEvaluator(true, true, true, true, true, true);
	}

	public SecuredGraphListenerTest( final String name )
	{
		super(name);
		eval = new MockSecurityEvaluator(true, true, true, true, true, true);
	}

	@Override
	public Graph getGraph()
	{
		final Graph graph = org.xenei.jena.security.Factory.getInstance(eval,
				getName(), Factory.createDefaultGraph());
		graph.getEventManager().register(new CheckChanges("simple tracking", graph));
		return graph;
	}
}
