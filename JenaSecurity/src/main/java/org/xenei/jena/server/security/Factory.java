package org.xenei.jena.server.security;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.rdf.model.Model;

import org.xenei.jena.server.security.graph.SecuredGraph;
import org.xenei.jena.server.security.model.SecuredModel;

public class Factory
{

	/**
	 * Create an instance of the SecuredGraph
	 * 
	 * @param securityEvaluator
	 *            The security evaluator to use
	 * @param graphIRI
	 *            The IRI for the graph.
	 * @param graph
	 *            The graph that we are wrapping.
	 * @return
	 */
	public static SecuredGraph getInstance(
			final SecurityEvaluator securityEvaluator, final String graphIRI,
			final Graph graph )
	{

		return org.xenei.jena.server.security.graph.impl.Factory.getInstance(
				securityEvaluator, graphIRI, graph);
	}

	/**
	 * Get an instance of SecuredModel
	 * 
	 * @param securityEvaluator
	 *            The security evaluator to use
	 * @param modelIRI
	 *            The model IRI (graph IRI) to evaluate against.
	 * @param model
	 *            The model to secure.
	 * @return the SecuredModel
	 */
	public static SecuredModel getInstance(
			final SecurityEvaluator securityEvaluator, final String modelURI,
			final Model model )
	{
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				securityEvaluator, modelURI, model);
	}
}
