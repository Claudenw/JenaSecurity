package org.xenei.jena.security.contract.graph;

import com.hp.hpl.jena.graph.Factory;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.test.MetaTestGraph;

import org.xenei.jena.security.MockSecurityEvaluator;
import org.xenei.jena.security.SecurityEvaluator;

public class SecuredGraphContractTests extends MetaTestGraph
{

	private final SecurityEvaluator eval;

	public SecuredGraphContractTests( final Class<? extends Graph> graphClass,
			final String name )
	{
		super(graphClass, name);
		eval = new MockSecurityEvaluator(true, true, true, true, true, true);
	}

	public SecuredGraphContractTests( final String name )
	{
		super(name);
		eval = new MockSecurityEvaluator(true, true, true, true, true, true);
	}

	@Override
	public Graph getGraph()
	{
		return org.xenei.jena.security.Factory.getInstance(eval, getName(),
				Factory.createDefaultGraph());
	}

}
