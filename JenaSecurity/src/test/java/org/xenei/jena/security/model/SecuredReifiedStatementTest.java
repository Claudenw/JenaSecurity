package org.xenei.jena.security.model;

import com.hp.hpl.jena.rdf.model.ReifiedStatement;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xenei.jena.security.AccessDeniedException;
import org.xenei.jena.security.MockSecurityEvaluator;
import org.xenei.jena.security.SecurityEvaluator.Action;
import org.xenei.jena.security.SecurityEvaluatorParameters;
import org.xenei.jena.security.model.impl.SecuredReifiedStatementImpl;

@RunWith( value = SecurityEvaluatorParameters.class )
public class SecuredReifiedStatementTest extends SecuredResourceTest
{

	public SecuredReifiedStatementTest(
			final MockSecurityEvaluator securityEvaluator )
	{
		super(securityEvaluator);
	}

	private SecuredReifiedStatement getSecuredReifiedStatement()
	{
		return (SecuredReifiedStatement) getSecuredRDFNode();
	}

	@Override
	@Before
	public void setup()
	{
		super.setup();
		final ReifiedStatement stmt = baseModel.listStatements().next()
				.createReifiedStatement();
		setSecuredRDFNode(
				SecuredReifiedStatementImpl.getInstance(securedModel, stmt),
				stmt);
	}

	/**
	 * @secGraph Read
	 * @throws AccessDeniedException
	 */
	@Test
	public void testGetStatement()
	{
		try
		{
			getSecuredReifiedStatement().getStatement();
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
