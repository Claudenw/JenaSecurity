package org.xenei.jena.server.security.graph;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.util.iterator.Filter;

import java.util.Collection;
import java.util.Set;

import org.xenei.jena.server.security.SecuredItem;
import org.xenei.jena.server.security.SecuredItemImpl;
import org.xenei.jena.server.security.SecurityEvaluator;
import org.xenei.jena.server.security.SecurityEvaluator.Action;
import org.xenei.jena.server.security.SecurityEvaluator.Node;

public class PermTripleFilter extends Filter<Triple>
{
	SecurityEvaluator evaluator;
	Node modelNode;
	Set<Action> actions;

	public PermTripleFilter( final Action[] actions, final SecuredItem sg,
			final SecurityEvaluator evaluator )
	{
		this.modelNode = sg.getModelNode();
		this.actions = SecurityEvaluator.Util.asSet(actions);
		this.evaluator = evaluator;
	}

	public PermTripleFilter( final Collection<Action> actions,
			final SecuredItem sg, final SecurityEvaluator evaluator )
	{
		this.modelNode = sg.getModelNode();
		this.actions = SecurityEvaluator.Util.asSet(actions);
		this.evaluator = evaluator;
	}

	@Override
	public boolean accept( final Triple t )
	{
		return evaluator.evaluateAny(actions, modelNode,
				SecuredItemImpl.convert(t));
	}

}