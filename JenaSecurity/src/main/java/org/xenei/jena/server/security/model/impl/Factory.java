package org.xenei.jena.server.security.model.impl;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.rdf.model.Alt;
import com.hp.hpl.jena.rdf.model.Bag;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFList;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ReifiedStatement;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Seq;
import com.hp.hpl.jena.rdf.model.Statement;

import org.xenei.jena.server.security.ItemHolder;
import org.xenei.jena.server.security.SecuredItem;
import org.xenei.jena.server.security.SecuredItemInvoker;
import org.xenei.jena.server.security.SecurityEvaluator;
import org.xenei.jena.server.security.graph.SecuredGraph;
import org.xenei.jena.server.security.model.SecuredAlt;
import org.xenei.jena.server.security.model.SecuredBag;
import org.xenei.jena.server.security.model.SecuredLiteral;
import org.xenei.jena.server.security.model.SecuredModel;
import org.xenei.jena.server.security.model.SecuredProperty;
import org.xenei.jena.server.security.model.SecuredRDFList;
import org.xenei.jena.server.security.model.SecuredRDFNode;
import org.xenei.jena.server.security.model.SecuredReifiedStatement;
import org.xenei.jena.server.security.model.SecuredResource;
import org.xenei.jena.server.security.model.SecuredSeq;
import org.xenei.jena.server.security.model.SecuredStatement;

public class Factory
{

	/**
	 * Get an instance of SecuredAlt.
	 * 
	 * @param securedItem
	 *            the item providing the security context.
	 * @param alt
	 *            The Alt to be secured.
	 * @return The secured Alt instance.
	 */
	static SecuredAlt getInstance( final SecuredItem securedItem, final Alt alt )
	{
		final ItemHolder<Alt, SecuredAlt> holder = new ItemHolder<Alt, SecuredAlt>(
				alt);
		final SecuredAltImpl checker = new SecuredAltImpl(
				securedItem.getSecurityEvaluator(), securedItem.getModelIRI(),
				holder);
		// if we are going to create a duplicate proxy, just return this
		// one.
		if (alt instanceof SecuredAlt)
		{
			if (checker.isEquivalent((SecuredAlt) alt))
			{
				return (SecuredAlt) alt;
			}
		}
		return holder.setSecuredItem(new SecuredItemInvoker(alt.getClass(),
				checker));
	}

	/**
	 * Get an instance of SecuredBag
	 * 
	 * @param securedItem
	 *            The secureity context.
	 * @param bag
	 *            The bag to secure
	 * @return The SecuredBag
	 */
	static SecuredBag getInstance( final SecuredItem securedItem, final Bag bag )
	{
		final ItemHolder<Bag, SecuredBag> holder = new ItemHolder<Bag, SecuredBag>(
				bag);
		final SecuredBagImpl checker = new SecuredBagImpl(
				securedItem.getSecurityEvaluator(), securedItem.getModelIRI(),
				holder);
		// if we are going to create a duplicate proxy, just return this
		// one.
		if (bag instanceof SecuredBag)
		{
			if (checker.isEquivalent((SecuredBag) bag))
			{
				return (SecuredBag) bag;
			}
		}
		return holder.setSecuredItem(new SecuredItemInvoker(bag.getClass(),
				checker));
	}

	/**
	 * Create an instance of the SecuredGraph
	 * 
	 * @param securedItem
	 *            The SecuredItem that contains the securityEvaluator and
	 *            graphIRI.
	 * @param graph
	 *            The unsecured graph.
	 * @return SecuredGraph.
	 */
	static SecuredGraph getInstance( final SecuredItem securedItem,
			final Graph graph )
	{
		return org.xenei.jena.server.security.graph.impl.Factory.getInstance(
				securedItem.getSecurityEvaluator(), securedItem.getModelIRI(),
				graph);
	}

	/**
	 * Get an instance of SecuredLiteral
	 * 
	 * @param securedItem
	 *            the item providing the security context.
	 * @param literal
	 *            the literal to secure
	 * @return SecuredLiteral
	 */
	static SecuredLiteral getInstance( final SecuredItem securedItem,
			final Literal literal )
	{
		// return
		// org.xenei.jena.server.security.model.impl.Factory.getInstance(securedItem.getSecurityEvaluator(),
		// securedItem.getModelIRI(), literal);
		final ItemHolder<Literal, SecuredLiteral> holder = new ItemHolder<Literal, SecuredLiteral>(
				literal);
		final SecuredLiteralImpl checker = new SecuredLiteralImpl(
				securedItem.getSecurityEvaluator(), securedItem.getModelIRI(),
				holder);
		// if we are going to create a duplicate proxy, just return this
		// one.
		if (literal instanceof SecuredLiteral)
		{
			if (checker.isEquivalent((SecuredLiteral) literal))
			{
				return (SecuredLiteral) literal;
			}
		}
		return holder.setSecuredItem(new SecuredItemInvoker(literal.getClass(),
				checker));
	}

	/**
	 * Get an instance of SecuredModel
	 * 
	 * @param securedItem
	 *            the item providing the security context.
	 * @param model
	 *            the model to secure.
	 * @return The SecuredModel
	 */
	static SecuredModel getInstance( final SecuredItem securedItem,
			final Model model )
	{
		return org.xenei.jena.server.security.Factory.getInstance(
				securedItem.getSecurityEvaluator(), securedItem.getModelIRI(),
				model);
	}

	/**
	 * Get an instance of SecuredProperty
	 * 
	 * @param securedItem
	 *            the item that provides the security context.
	 * @param property
	 *            The property to secure
	 * @return The SecuredProperty
	 */
	static SecuredProperty getInstance( final SecuredItem securedItem,
			final Property property )
	{
		final ItemHolder<Property, SecuredProperty> holder = new ItemHolder<Property, SecuredProperty>(
				property);
		final SecuredPropertyImpl checker = new SecuredPropertyImpl(
				securedItem.getSecurityEvaluator(), securedItem.getModelIRI(),
				holder);
		// if we are going to create a duplicate proxy, just return this
		// one.
		if (property instanceof SecuredProperty)
		{
			if (checker.isEquivalent((SecuredProperty) property))
			{
				return (SecuredProperty) property;
			}
		}
		return holder.setSecuredItem(new SecuredItemInvoker(
				property.getClass(), checker));
	}

	/**
	 * Get an instance of SecuredProperty
	 * 
	 * @param securedItem
	 *            the item that provides the security context.
	 * @param rdfList
	 *            The rdfList to secure
	 * @return The SecuredProperty
	 */
	static SecuredRDFList getInstance( final SecuredItem securedItem,
			final RDFList rdfList )
	{
		final ItemHolder<RDFList, SecuredRDFList> holder = new ItemHolder<RDFList, SecuredRDFList>(
				rdfList);
		final SecuredRDFListImpl checker = new SecuredRDFListImpl(
				securedItem.getSecurityEvaluator(), securedItem.getModelIRI(),
				holder);
		// if we are going to create a duplicate proxy, just return this
		// one.
		if (rdfList instanceof SecuredRDFList)
		{
			if (checker.isEquivalent((SecuredRDFList) rdfList))
			{
				return (SecuredRDFList) rdfList;
			}
		}
		return holder.setSecuredItem(new SecuredItemInvoker(rdfList.getClass(),
				checker));
	}

	static SecuredRDFNode getInstance( final SecuredItem securedItem,
			final RDFNode rdfNode )
	{
		if (rdfNode instanceof Literal)
		{
			return Factory.getInstance(securedItem, (Literal) rdfNode);
		}
		else
		{
			return org.xenei.jena.server.security.model.impl.Factory
					.getInstance(securedItem, (Resource) rdfNode);
		}
	}

	/**
	 * Get an instance of SecuredReifiedStatement
	 * 
	 * @param securedItem
	 *            The securedItem that provides the security context
	 * @param stmt
	 *            The ReifiedStatement to secure.
	 * @return SecuredReifiedStatement
	 */
	static SecuredReifiedStatement getInstance( final SecuredItem securedItem,
			final ReifiedStatement stmt )
	{
		final ItemHolder<ReifiedStatement, SecuredReifiedStatement> holder = new ItemHolder<ReifiedStatement, SecuredReifiedStatement>(
				stmt);
		final SecuredReifiedStatementImpl checker = new SecuredReifiedStatementImpl(
				securedItem.getSecurityEvaluator(), securedItem.getModelIRI(),
				holder);
		// if we are going to create a duplicate proxy, just return this
		// one.
		if (stmt instanceof SecuredReifiedStatement)
		{
			if (checker.isEquivalent((SecuredReifiedStatement) stmt))
			{
				return (SecuredReifiedStatement) stmt;
			}
		}
		return holder.setSecuredItem(new SecuredItemInvoker(stmt.getClass(),
				checker));
	}

	/**
	 * Get a SecuredResource.
	 * 
	 * @param securedItem
	 *            the securedItem that provides the security context.
	 * @param resource
	 *            The resource to secure.
	 * @return The SecuredResource
	 */
	static SecuredResource getInstance( final SecuredItem securedItem,
			final Resource resource )
	{
		final ItemHolder<Resource, SecuredResource> holder = new ItemHolder<Resource, SecuredResource>(
				resource);
		final SecuredResourceImpl checker = new SecuredResourceImpl(
				securedItem.getSecurityEvaluator(), securedItem.getModelIRI(),
				holder);
		// if we are going to create a duplicate proxy, just return this
		// one.
		if (resource instanceof SecuredResource)
		{
			if (checker.isEquivalent((SecuredResource) resource))
			{
				return (SecuredResource) resource;
			}
		}

		return holder.setSecuredItem(new SecuredItemInvoker(
				resource.getClass(), checker));

	}

	/**
	 * get a SecuredSeq.
	 * 
	 * @param securedItem
	 *            The secured item that provides the security context
	 * @param seq
	 *            The Seq to secure.
	 * @return the SecuredSeq
	 */
	static SecuredSeq getInstance( final SecuredItem securedItem, final Seq seq )
	{
		final ItemHolder<Seq, SecuredSeq> holder = new ItemHolder<Seq, SecuredSeq>(
				seq);
		final SecuredSeqImpl checker = new SecuredSeqImpl(
				securedItem.getSecurityEvaluator(), securedItem.getModelIRI(),
				holder);
		// if we are going to create a duplicate proxy, just return this
		// one.
		if (seq instanceof SecuredSeq)
		{
			if (checker.isEquivalent((SecuredSeq) seq))
			{
				return (SecuredSeq) seq;
			}
		}
		return holder.setSecuredItem(new SecuredItemInvoker(seq.getClass(),
				checker));
	}

	/**
	 * get a SecuredStatement
	 * 
	 * @param securedItem
	 *            the secured item that provides the security context.
	 * @param stmt
	 *            The statement to secure.
	 * @return the SecuredStatement
	 */
	static SecuredStatement getInstance( final SecuredItem securedItem,
			final Statement stmt )
	{
		final ItemHolder<Statement, SecuredStatement> holder = new ItemHolder<Statement, SecuredStatement>(
				stmt);

		final SecuredStatementImpl checker = new SecuredStatementImpl(
				securedItem.getSecurityEvaluator(), securedItem.getModelIRI(),
				holder);
		// if we are going to create a duplicate proxy, just return this
		// one.
		if (stmt instanceof SecuredStatement)
		{
			if (checker.isEquivalent((SecuredStatement) stmt))
			{
				return (SecuredStatement) stmt;
			}
		}
		return holder.setSecuredItem(new SecuredItemInvoker(holder
				.getBaseItem().getClass(), checker));
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
		final ItemHolder<Model, SecuredModel> holder = new ItemHolder<Model, SecuredModel>(
				model);

		final SecuredModelImpl checker = new SecuredModelImpl(
				securityEvaluator, modelURI, holder);
		// if we are going to create a duplicate proxy, just return this
		// one.
		if (model instanceof SecuredModel)
		{
			if (checker.isEquivalent((SecuredModel) model))
			{
				return (SecuredModel) model;
			}
		}
		return holder.setSecuredItem(new SecuredItemInvoker(model.getClass(),
				checker));
	}

}
