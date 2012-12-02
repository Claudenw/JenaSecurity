package org.xenei.jena.contract.model;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xenei.jena.server.security.AccessDeniedException;
import org.xenei.jena.server.security.Factory;
import org.xenei.jena.server.security.MockSecurityEvaluator;
import org.xenei.jena.server.security.SecurityEvaluatorParameters;
import org.xenei.jena.server.security.SecurityEvaluator.Action;
import org.xenei.jena.server.security.model.impl.SecuredLiteralImpl;
import org.xenei.jena.server.security.model.impl.SecuredRDFNodeImpl;

@RunWith( value = SecurityEvaluatorParameters.class )
public class RDFNodeTest
{

	protected RDFNode rdfNode;

	public RDFNodeTest()
	{

	}

	protected RDFNode createRDFNode()
	{
		return ModelFactory.createDefaultModel();
	}

	@Before
	public void setup()
	{
		rdfNode = createRDFNode();
	}

	@Test
	public void testAsNode()
	{

		Assert.assertNotNull("Should not have returned null", rdfNode.asNode());
	}

	@Test
	public void testCanAs()
	{
		// just verify that canAs does not throw an exception
		rdfNode.canAs(RDFNode.class);
	}

	@Test
	public void testGetModel()
	{
		// just verify that getModel() does not throw an execption
		rdfNode.getModel();
	}

	@Test
	public void testInModel()
	{
		Model m2 = ModelFactory.createDefaultModel();

		RDFNode n2 = rdfNode.inModel(m2);
		Assert.assertEquals("Wrong model returned", n2.getModel(), m2);
	}

}
