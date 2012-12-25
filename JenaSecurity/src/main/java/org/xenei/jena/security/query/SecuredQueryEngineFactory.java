package org.xenei.jena.security.query;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.ARQInternalErrorException;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.sparql.engine.Plan;
import com.hp.hpl.jena.sparql.engine.QueryEngineFactory;
import com.hp.hpl.jena.sparql.engine.QueryEngineRegistry;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.util.Context;

import org.xenei.jena.security.SecurityEvaluator;
import org.xenei.jena.security.graph.SecuredGraph;

public class SecuredQueryEngineFactory implements QueryEngineFactory
{
	private boolean silentService = true;
	private SecuredQueryEngineConfig cfgResource;
	private SecurityEvaluator securityEvaluator;

	private static SecuredQueryEngineFactory factory = new SecuredQueryEngineFactory();

	static public SecuredQueryEngineFactory getFactory() {
		return factory;
	}

	static public void register() {
		QueryEngineRegistry.addFactory(factory);
	}

	static public void unregister() {
		QueryEngineRegistry.removeFactory(factory);
	}

	public SecurityEvaluator getSecurityEvaluator() {
		return securityEvaluator;
	}

	public void setSecurityEvaluator(SecurityEvaluator securityEvaluator) {
		this.securityEvaluator = securityEvaluator;
	}
	
	public boolean isSilentService() {
		return silentService;
	}

	public void setSilentService(boolean silentService) {
		this.silentService = silentService;
	}

	public void setSecuredQueryEngineConfig(SecuredQueryEngineConfig cfgResource) {
		this.cfgResource = cfgResource;

	}

	/**
	 * Only accept a secured dataset
	 */
	@Override
	public boolean accept(Query query, DatasetGraph dataset, Context context) {
		Graph g = dataset.getDefaultGraph();
		return g instanceof SecuredGraph;
	}

	@Override
	public Plan create(Query query, DatasetGraph dataset, Binding initial,
			Context context) {
		// set up the context
		if (cfgResource != null) {
			cfgResource.initializeContext( context );
		}

		// Create a query engine instance.
		SecuredQueryEngine engine = new SecuredQueryEngine(query, dataset,
				initial, context);
		return engine.getPlan();
	}

	@Override
	public boolean accept(Op op, DatasetGraph dataset, Context context) { // Refuse
																			// to
																			// accept
																			// algebra
																			// expressions
																			// directly.
		return false;
	}

	@Override
	public Plan create(Op op, DatasetGraph dataset, Binding inputBinding,
			Context context) { // Should not be called because acceept/Op is
								// false
		throw new ARQInternalErrorException(this.getClass().getSimpleName()
				+ ": factory called directly with an algebra expression");
	}

}
