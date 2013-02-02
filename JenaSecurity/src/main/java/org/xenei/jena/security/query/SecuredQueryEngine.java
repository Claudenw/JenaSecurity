package org.xenei.jena.security.query;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.engine.main.QueryEngineMain;
import com.hp.hpl.jena.sparql.util.Context;
import com.hp.hpl.jena.tdb.TDB;

import java.security.Principal;
import java.util.Iterator;
import java.util.Set;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xenei.jena.security.MockSecurityEvaluator;
import org.xenei.jena.security.SecuredItemImpl;
import org.xenei.jena.security.SecurityEvaluator;
import org.xenei.jena.security.SecurityEvaluator.SecNode;
import org.xenei.jena.security.SecurityEvaluator.SecNode.Type;
import org.xenei.jena.security.graph.SecuredGraph;
import org.xenei.jena.security.query.rewriter.OpRewriter;

public class SecuredQueryEngine extends QueryEngineMain
{
	private static Logger LOG = LoggerFactory
			.getLogger(SecuredQueryEngine.class);
	
	private SecurityEvaluator securityEvaluator;
	private SecNode graphIRI;
	
	/*
	public SecuredQueryEngine( Op op, DatasetGraph dataset, Binding input,
			Context context )
	{
		super(op, dataset, input, context);
		setGraphIRI( dataset );
	}
*/
	public SecuredQueryEngine( Query query, DatasetGraph dataset,
			Binding input, Context context)
	{
		super(query, dataset, input, context);
		setGraphIRI( dataset );
	}

	private void setGraphIRI(DatasetGraph dataset)
	{
		Graph g = dataset.getDefaultGraph();
		if (g instanceof SecuredGraph)
		{
			SecuredGraph sg = (SecuredGraph) g;
			graphIRI = sg.getModelNode();
			this.securityEvaluator = sg.getSecurityEvaluator();
		}
		else
		{
			graphIRI = new SecNode( Type.URI, "urn:x-arq:DefaultGraph" );
			this.securityEvaluator = new SecurityEvaluator() {

				@Override
				public boolean evaluate( Action action, SecNode graphIRI )
				{
					return true;
				}

				@Override
				public boolean evaluate( Action action, SecNode graphIRI,
						SecTriple triple )
				{
					return true;
				}

				@Override
				public boolean evaluate( Set<Action> action, SecNode graphIRI )
				{
					return true;
				}

				@Override
				public boolean evaluate( Set<Action> action, SecNode graphIRI,
						SecTriple triple )
				{
					return true;
				}

				@Override
				public boolean evaluateAny( Set<Action> action, SecNode graphIRI )
				{
					return true;
				}

				@Override
				public boolean evaluateAny( Set<Action> action,
						SecNode graphIRI, SecTriple triple )
				{
					return true;
				}

				@Override
				public boolean evaluateUpdate( SecNode graphIRI,
						SecTriple from, SecTriple to )
				{
					return true;
				}

				@Override
				public Principal getPrincipal()
				{
					return null;
				}};
			
		}
	}
	
	public SecurityEvaluator getSecurityEvaluator()
	{
		return securityEvaluator;
	}

	@Override
	protected Op modifyOp(Op op) {
		OpRewriter rewriter = new OpRewriter( securityEvaluator, graphIRI );
		LOG.debug("Before: {}", op);
		op.visit(rewriter);
		Op result = rewriter.getResult();
		result = result == null ? op : result;
		LOG.debug("After: {}", result);
		result = super.modifyOp(result);
		LOG.debug("After Optimize: {}", result);
		System.out.println( result );
		return result;
	}
}
