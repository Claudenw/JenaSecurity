package org.xenei.jena.security.model.impl;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;

import org.xenei.jena.security.SecurityEvaluator.SecNode;
import org.xenei.jena.security.SecurityEvaluator.SecTriple;
import org.xenei.jena.security.impl.SecuredItem;
import org.xenei.jena.security.impl.SecuredItemImpl;

public class SecuredSelector implements Selector
{
	private final SecuredItem securedItem;
	private final Selector selector;

	public SecuredSelector( final SecuredItem securedItem )
	{
		this(securedItem, new SimpleSelector());
	}

	public SecuredSelector( final SecuredItem securedItem,
			final Selector selector )
	{
		this.securedItem = securedItem;
		this.selector = selector;
	}

	private SecNode getNode( final RDFNode node )
	{
		return node == null ? SecNode.ANY : SecuredItemImpl.convert(node
				.asNode());
	}

	@Override
	public RDFNode getObject()
	{
		return selector.getObject();
	}

	@Override
	public Property getPredicate()
	{
		return selector.getPredicate();
	}

	@Override
	public Resource getSubject()
	{
		return selector.getSubject();
	}

	@Override
	public boolean isSimple()
	{
		return securedItem.canRead(SecTriple.ANY);
	}

	/**
	 * This method is designed to be over ridden by subclasses to define
	 * application
	 * specific constraints on the statements selected.
	 * 
	 * @param s
	 *            the statement to be tested
	 * @return true if the statement satisfies the constraint
	 */
	@Override
	public boolean test( final Statement s )
	{
		if (selector.test(s))
		{
			final SecTriple t = new SecTriple(getNode(s.getSubject()),
					getNode(s.getPredicate()), getNode(s.getObject()));
			return securedItem.canRead(t);
		}
		return false;
	}

}
