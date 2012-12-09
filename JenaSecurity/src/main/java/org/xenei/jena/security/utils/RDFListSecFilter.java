package org.xenei.jena.security.utils;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.RDFList;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.util.iterator.Filter;
import com.hp.hpl.jena.vocabulary.RDF;

import java.util.Set;

import org.xenei.jena.security.SecuredItem;
import org.xenei.jena.security.SecuredItemImpl;
import org.xenei.jena.security.SecurityEvaluator;
import org.xenei.jena.security.SecurityEvaluator.Action;

public class RDFListSecFilter<T extends RDFList> extends Filter<T>
{

	private SecuredItem securedItem;
	private Set<Action> perms;
	
	public RDFListSecFilter(SecuredItem securedItem, Action perm )
	{
		this( securedItem, SecurityEvaluator.Util.asSet( new Action[]{ perm }));
	}
	
	public RDFListSecFilter(SecuredItem securedItem, Set<Action> perms )
	{
		this.securedItem = securedItem;
		this.perms = perms;
	}

	@Override
	public boolean accept( RDFList o )
	{
		Statement s = o.getRequiredProperty(RDF.first);
		return securedItem.getSecurityEvaluator().evaluate(
				perms,
				securedItem.getModelNode(),
				SecuredItemImpl.convert( s.asTriple() ));
	}
}
