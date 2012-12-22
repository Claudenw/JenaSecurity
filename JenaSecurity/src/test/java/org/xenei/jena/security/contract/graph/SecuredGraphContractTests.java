package org.xenei.jena.security.contract.graph;

import com.hp.hpl.jena.graph.Factory;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.test.MetaTestGraph;
import com.hp.hpl.jena.shared.ReificationStyle;

import org.xenei.jena.security.MockSecurityEvaluator;
import org.xenei.jena.security.SecurityEvaluator;

public class SecuredGraphContractTests extends MetaTestGraph
{

	private final SecurityEvaluator eval;
	private ReificationStyle style;

	public SecuredGraphContractTests( final Class<? extends Graph> graphClass,
			final String name, final ReificationStyle style )
	{
		super(graphClass, name, style);
		this.style = style;
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
		final Graph graph = (style != null) ? Factory.createDefaultGraph(style)
				: Factory.createDefaultGraph();

		return org.xenei.jena.security.Factory.getInstance(eval, getName(),
				graph);
	}

}
