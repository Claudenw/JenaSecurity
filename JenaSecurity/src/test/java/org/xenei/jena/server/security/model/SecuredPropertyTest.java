package org.xenei.jena.server.security.model;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xenei.jena.server.security.AccessDeniedException;
import org.xenei.jena.server.security.MockSecurityEvaluator;
import org.xenei.jena.server.security.SecurityEvaluator.Action;
import org.xenei.jena.server.security.SecurityEvaluatorParameters;
import org.xenei.jena.server.security.model.impl.SecuredPropertyImpl;

@RunWith( value = SecurityEvaluatorParameters.class )
public class SecuredPropertyTest extends SecuredResourceTest
{

	public SecuredPropertyTest( final MockSecurityEvaluator securityEvaluator )
	{
		super(securityEvaluator);
	}

	private SecuredProperty getSecuredProperty()
	{
		return (SecuredProperty) getSecuredRDFNode();
	}

	@Override
	@Before
	public void setup()
	{
		super.setup();
		final Property p = ResourceFactory
				.createProperty("http://example.com/testProperty");
		setSecuredRDFNode(SecuredPropertyImpl.getInstance(securedModel, p), p);
	}

	@Test
	public void testGetOrdinal()
	{
		try
		{
			getSecuredProperty().getOrdinal();
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

}
