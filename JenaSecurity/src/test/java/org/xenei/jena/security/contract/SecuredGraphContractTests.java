package org.xenei.jena.security.contract;

import com.hp.hpl.jena.graph.Factory;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.test.MetaTestGraph;
import com.hp.hpl.jena.shared.ReificationStyle;
import com.hp.hpl.jena.sparql.graph.GraphFactory;

import org.xenei.jena.security.MockSecurityEvaluator;
import org.xenei.jena.security.SecurityEvaluator;

public class SecuredGraphContractTests extends MetaTestGraph
{

	SecurityEvaluator eval;
	public SecuredGraphContractTests(
			String name)
	{
		super( name);
		eval = new MockSecurityEvaluator(true, true, true, true, true, true);
	}
	
	public SecuredGraphContractTests( Class<? extends Graph> graphClass,
			String name, ReificationStyle style )
	{
		super(graphClass, name, style);
		eval = new MockSecurityEvaluator(true, true, true, true, true, true);
	}

	@Override
	public Graph getGraph()
	{
		
		//Graph graph = Factory.createDefaultGraph( style );
		Graph graph = GraphFactory.createDefaultGraph();
		return org.xenei.jena.security.Factory.getInstance(eval, getName(), graph);
	}
	
	
	
}
