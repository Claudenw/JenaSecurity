package org.xenei.jena.security.model;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.shared.PropertyNotFoundException;

import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xenei.jena.security.AccessDeniedException;
import org.xenei.jena.security.MockSecurityEvaluator;
import org.xenei.jena.security.SecurityEvaluator;
import org.xenei.jena.security.SecurityEvaluatorParameters;
import org.xenei.jena.security.SecurityEvaluator.Action;
import org.xenei.jena.security.model.SecuredResource;
import org.xenei.jena.security.model.impl.SecuredResourceImpl;

@RunWith( value = SecurityEvaluatorParameters.class )
public class SecuredResourceTest extends SecuredRDFNodeTest
{

	public SecuredResourceTest( final MockSecurityEvaluator securityEvaluator )
	{
		super(securityEvaluator);
	}

	private SecuredResource getSecuredResource()
	{
		return (SecuredResource) getSecuredRDFNode();
	}

	@Override
	@Before
	public void setup()
	{
		super.setup();
		setSecuredRDFNode(SecuredResourceImpl.getInstance(securedModel,
				SecuredRDFNodeTest.s), SecuredRDFNodeTest.s);
	}

	/**
	 * @graphSec Update
	 * @tripleSec Create (this, p, o )
	 * @throws AccessDeniedException
	 */
	@Test
	public void testAddLiteral()
	{
		final Set<Action> perms = SecurityEvaluator.Util.asSet(new Action[] {
				Action.Update, Action.Create });
		try
		{
			getSecuredResource().addLiteral(SecuredRDFNodeTest.p, true);
			if (!securityEvaluator.evaluate(perms))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(perms))
			{
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
			}
		}

		try
		{
			getSecuredResource().addLiteral(SecuredRDFNodeTest.p, 'c');
			if (!securityEvaluator.evaluate(perms))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(perms))
			{
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
			}
		}

		try
		{
			getSecuredResource().addLiteral(SecuredRDFNodeTest.p, 3.14D);
			if (!securityEvaluator.evaluate(perms))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(perms))
			{
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
			}
		}

		try
		{
			getSecuredResource().addLiteral(SecuredRDFNodeTest.p, 3.14F);
			if (!securityEvaluator.evaluate(perms))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(perms))
			{
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
			}
		}

		try
		{
			getSecuredResource().addLiteral(SecuredRDFNodeTest.p,
					ResourceFactory.createTypedLiteral("Yee haw"));
			if (!securityEvaluator.evaluate(perms))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(perms))
			{
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
			}
		}

		try
		{
			getSecuredResource().addLiteral(SecuredRDFNodeTest.p, 1L);
			if (!securityEvaluator.evaluate(perms))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(perms))
			{
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
			}
		}

		final Object o = Integer.valueOf("1234");
		try
		{
			getSecuredResource().addLiteral(SecuredRDFNodeTest.p, o);
			if (!securityEvaluator.evaluate(perms))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(perms))
			{
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
			}
		}

	}

	@Test
	public void testAddProperty()
	{
		final Set<Action> perms = SecurityEvaluator.Util.asSet(new Action[] {
				Action.Update, Action.Create });

		final RDFNode rdfNode = ResourceFactory
				.createResource("http://example.com/newResource");
		try
		{
			getSecuredResource().addLiteral(SecuredRDFNodeTest.p, rdfNode);
			if (!securityEvaluator.evaluate(perms))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(perms))
			{
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
			}
		}

		try
		{
			getSecuredResource().addLiteral(SecuredRDFNodeTest.p, "string");
			if (!securityEvaluator.evaluate(perms))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(perms))
			{
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
			}
		}

		final Literal l = ResourceFactory.createTypedLiteral(Float
				.valueOf(3.14F));
		try
		{
			getSecuredResource().addProperty(SecuredRDFNodeTest.p,
					l.getLexicalForm(), l.getDatatype());
			if (!securityEvaluator.evaluate(perms))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(perms))
			{
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
			}
		}

		try
		{
			getSecuredResource().addProperty(SecuredRDFNodeTest.p, "dos", "sp");
			if (!securityEvaluator.evaluate(perms))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(perms))
			{
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
			}
		}
	}

	@Test
	public void testAnonFuncs()
	{

		final SecuredResource anonResource = securedModel.createResource();
		setSecuredRDFNode(anonResource, null);

		try
		{
			getSecuredResource().getId();
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
	public void testAsResource()
	{
		getSecuredResource().asResource();
	}

	@Test
	public void testEquals()
	{
		try
		{
			getSecuredResource().equals(SecuredRDFNodeTest.s);
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

	public void testGetLocalName()
	{
		try
		{
			getSecuredResource().getLocalName();
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

	public void testGetNameSpace()
	{
		try
		{
			getSecuredResource().getNameSpace();
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
	public void testGetProperty()
	{
		try
		{
			getSecuredResource().getProperty(SecuredRDFNodeTest.p);
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

		try
		{
			getSecuredResource().getPropertyResourceValue(SecuredRDFNodeTest.p);
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

		try
		{
			getSecuredResource().getRequiredProperty(SecuredRDFNodeTest.p);
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
		catch (final PropertyNotFoundException e)
		{
			// expected if (this, "p", ANY) is not in the base model.
			final StmtIterator iter = baseModel.listStatements(
					getSecuredResource(), SecuredRDFNodeTest.p, (RDFNode) null);
			try
			{
				if (iter.hasNext())
				{
					throw e;
				}
			}
			finally
			{
				iter.close();
			}
		}
	}

	@Test
	public void testGetURI()
	{
		try
		{
			getSecuredResource().getURI();
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
	public void testHasLiteral()
	{
		try
		{
			getSecuredResource().hasLiteral(SecuredRDFNodeTest.p, true);
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

		try
		{
			getSecuredResource().hasLiteral(SecuredRDFNodeTest.p, 'c');
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

		try
		{
			getSecuredResource().hasLiteral(SecuredRDFNodeTest.p, 3.14d);
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

		try
		{
			getSecuredResource().hasLiteral(SecuredRDFNodeTest.p, 3.14f);
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

		try
		{
			getSecuredResource().hasLiteral(SecuredRDFNodeTest.p, 6l);
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

		final Object o = Integer.valueOf(6);
		try
		{
			getSecuredResource().hasLiteral(SecuredRDFNodeTest.p, o);
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
	public void testHasProperty()
	{

		try
		{
			getSecuredResource().hasProperty(SecuredRDFNodeTest.p);
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

		try
		{
			getSecuredResource().hasProperty(SecuredRDFNodeTest.p,
					SecuredRDFNodeTest.o);
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

		try
		{
			getSecuredResource().hasProperty(SecuredRDFNodeTest.p, "yeee haw");
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

		try
		{
			getSecuredResource().hasProperty(SecuredRDFNodeTest.p, "dos", "sp");
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
	public void testHasURI()
	{
		try
		{
			getSecuredResource().hasURI("http://example.com/yeeHaw");
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
	public void testListProperties()
	{
		try
		{
			getSecuredResource().listProperties();
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

		try
		{
			getSecuredResource().listProperties(SecuredRDFNodeTest.p);
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
	public void testRemoveAll()
	{
		final Set<Action> perms = SecurityEvaluator.Util.asSet(new Action[] {
				Action.Update, Action.Delete });
		final int count = baseModel
				.listStatements(getBaseRDFNode().asResource(),
						SecuredRDFNodeTest.p, (RDFNode) null).toSet().size();

		try
		{
			getSecuredResource().removeAll(SecuredRDFNodeTest.p);
			// only throw on delete if count > 0
			if (!securityEvaluator.evaluate(Action.Update)
					|| ((count > 0) && !securityEvaluator
							.evaluate(Action.Delete)))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(perms))
			{
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
			}
		}
	}

	@Test
	public void testRemoveProperties()
	{
		final Set<Action> perms = SecurityEvaluator.Util.asSet(new Action[] {
				Action.Update, Action.Delete });
		final int count = baseModel
				.listStatements(getBaseRDFNode().asResource(),
						SecuredRDFNodeTest.p, (RDFNode) null).toSet().size();

		try
		{
			getSecuredResource().removeProperties();
			// only throw on delete if count > 0
			if (!securityEvaluator.evaluate(Action.Update)
					|| ((count > 0) && !securityEvaluator
							.evaluate(Action.Delete)))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(perms))
			{
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
			}
		}
	}
}
