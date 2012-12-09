package org.xenei.jena.security.model;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xenei.jena.security.AccessDeniedException;
import org.xenei.jena.security.Factory;
import org.xenei.jena.security.MockSecurityEvaluator;
import org.xenei.jena.security.SecurityEvaluatorParameters;
import org.xenei.jena.security.SecurityEvaluator.Action;
import org.xenei.jena.security.model.SecuredModel;
import org.xenei.jena.security.model.SecuredRDFNode;
import org.xenei.jena.security.model.impl.SecuredRDFNodeImpl;

@RunWith( value = SecurityEvaluatorParameters.class )
public class SecuredRDFNodeTest
{
	protected final MockSecurityEvaluator securityEvaluator;
	protected Model baseModel;
	protected SecuredModel securedModel;
	private SecuredRDFNode securedRDFNode;
	private RDFNode baseRDFNode;

	public static Resource s = ResourceFactory
			.createResource("http://example.com/graph/s");
	public static Property p = ResourceFactory
			.createProperty("http://example.com/graph/p");
	public static Property p2 = ResourceFactory
			.createProperty("http://example.com/graph/p2");
	public static Resource o = ResourceFactory
			.createResource("http://example.com/graph/o");

	public SecuredRDFNodeTest( final MockSecurityEvaluator securityEvaluator )
	{
		this.securityEvaluator = securityEvaluator;
	}

	protected Model createModel()
	{
		return ModelFactory.createDefaultModel();
	}

	protected RDFNode getBaseRDFNode()
	{
		return baseRDFNode;
	}

	protected SecuredRDFNode getSecuredRDFNode()
	{
		return securedRDFNode;
	}

	protected void setSecuredRDFNode( final SecuredRDFNode securedRDFNode,
			final RDFNode baseRDFNode )
	{
		this.securedRDFNode = securedRDFNode;
		this.baseRDFNode = baseRDFNode;
	}

	@Before
	public void setup()
	{
		baseModel = createModel();
		baseModel.removeAll();
		baseModel.add(SecuredRDFNodeTest.s, SecuredRDFNodeTest.p,
				SecuredRDFNodeTest.o);
		baseModel.add(SecuredRDFNodeTest.s, SecuredRDFNodeTest.p2, "yeehaw");
		securedModel = Factory.getInstance(securityEvaluator,
				"http://example.com/securedGraph", baseModel);
		securedRDFNode = SecuredRDFNodeImpl.getInstance(
				securedModel,
				baseModel.listObjectsOfProperty(SecuredRDFNodeTest.s,
						SecuredRDFNodeTest.p).next());
	}

	@After
	public void teardown()
	{
		securedModel.close();
		securedModel = null;
	}

	@Test
	public void testAsNode()
	{
		try
		{
			securedRDFNode.asNode();
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
			}
		}
	}

	@Test
	public void testCanAs()
	{
		try
		{
			securedRDFNode.canAs(Resource.class);
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
			}
		}
	}

	@Test
	public void testGetModel()
	{
		final Model m2 = securedRDFNode.getModel();
		Assert.assertTrue("Model should have been secured",
				m2 instanceof SecuredModel);
	}

	@Test
	public void testInModel()
	{
		final Model m2 = ModelFactory.createDefaultModel();
		try
		{
			final RDFNode n2 = securedRDFNode.inModel(m2);
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
			Assert.assertFalse("RDFNode should not have been secured",
					n2 instanceof SecuredRDFNode);
			Assert.assertEquals("Wrong model returned", n2.getModel(), m2);
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
			}
		}

		m2.removeAll();
		final SecuredModel m3 = Factory.getInstance(securityEvaluator,
				"http://example.com/securedGraph2", m2);

		try
		{
			final RDFNode n2 = securedRDFNode.inModel(m3);
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
			Assert.assertTrue("RDFNode should have been secured",
					n2 instanceof SecuredRDFNode);
			Assert.assertEquals("Wrong model returned", n2.getModel(), m3);
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
			}
		}

	}

}
