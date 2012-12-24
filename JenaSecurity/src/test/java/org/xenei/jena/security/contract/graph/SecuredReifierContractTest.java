package org.xenei.jena.security.contract.graph;

import com.hp.hpl.jena.graph.Factory;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.test.AbstractTestReifier;
import com.hp.hpl.jena.shared.ReificationStyle;
import com.hp.hpl.jena.sparql.graph.GraphFactory;

import junit.framework.Assert;

import org.xenei.jena.security.MockSecurityEvaluator;
import org.xenei.jena.security.SecurityEvaluator;

public class SecuredReifierContractTest extends AbstractTestReifier
{

	private final SecurityEvaluator eval;

	public SecuredReifierContractTest( final String name )
	{
		super(name);
		eval = new MockSecurityEvaluator(true, true, true, true, true, true);
	}

	@Override
	public Graph getGraph()
	{

		final Graph graph = GraphFactory.createDefaultGraph();
		return org.xenei.jena.security.Factory.getInstance(eval, getName(),
				graph);
	}

	@Override
	public Graph getGraph( final ReificationStyle style )
	{
		final Graph graph = Factory.createDefaultGraph(style);
		return org.xenei.jena.security.Factory.getInstance(eval, getName(),
				graph);
	}

	@Override
	public void testParent()
	{
		final Graph G = getGraph(), H = getGraph();
		Assert.assertEquals("correct reifier (G)", G, G.getReifier()
				.getParentGraph());
		Assert.assertEquals("correct reifier (H)", H, H.getReifier()
				.getParentGraph());
	}
}
