package org.xenei.jena.server.security.graph;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.sparql.graph.GraphFactory;

import java.util.HashMap;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xenei.jena.server.security.AccessDeniedException;
import org.xenei.jena.server.security.Factory;
import org.xenei.jena.server.security.SecurityEvaluator;
import org.xenei.jena.server.security.SecurityEvaluatorParameters;
import org.xenei.jena.server.security.SecurityEvaluator.Action;

@RunWith( value = SecurityEvaluatorParameters.class )
public class SecuredPrefixMappingTest
{
	private final SecurityEvaluator securityEvaluator;
	private final SecuredPrefixMapping securedMapping;

	public SecuredPrefixMappingTest( final SecurityEvaluator securityEvaluator )
	{
		this.securityEvaluator = securityEvaluator;
		final Graph g = GraphFactory.createDefaultGraph();

		final SecuredGraph sg = Factory.getInstance(securityEvaluator,
				"http://example.com/testGraph", g);
		this.securedMapping = (SecuredPrefixMapping) sg.getPrefixMapping();
	}

	@Test
	public void testExpandPrefix()
	{
		try
		{
			securedMapping.expandPrefix("foo");
			if (!securityEvaluator.evaluate(Action.Read,
					securedMapping.getModelNode()))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read,
					securedMapping.getModelNode()))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testGetNsPrefixMap()
	{
		try
		{
			securedMapping.getNsPrefixMap();
			if (!securityEvaluator.evaluate(Action.Read,
					securedMapping.getModelNode()))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read,
					securedMapping.getModelNode()))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testGetNsPrefixURI()
	{

		try
		{
			securedMapping.getNsPrefixURI("foo");
			if (!securityEvaluator.evaluate(Action.Read,
					securedMapping.getModelNode()))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read,
					securedMapping.getModelNode()))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}

	}

	@Test
	public void testGetNsURIPrefix()
	{

		try
		{
			securedMapping.getNsURIPrefix("http://example.com/foo");
			if (!securityEvaluator.evaluate(Action.Read,
					securedMapping.getModelNode()))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read,
					securedMapping.getModelNode()))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testLock()
	{
		try
		{
			securedMapping.lock();
			if (!securityEvaluator.evaluate(Action.Update,
					securedMapping.getModelNode()))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Update,
					securedMapping.getModelNode()))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}

	}

	@Test
	public void testQnameFor()
	{
		try
		{
			securedMapping.qnameFor("http://example.com/foo/bar");
			if (!securityEvaluator.evaluate(Action.Read,
					securedMapping.getModelNode()))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read,
					securedMapping.getModelNode()))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testRemoveNsPrefix()
	{
		try
		{
			securedMapping.removeNsPrefix("foo");
			if (!securityEvaluator.evaluate(Action.Update,
					securedMapping.getModelNode()))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Update,
					securedMapping.getModelNode()))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}

	}

	@Test
	public void testSamePrefixMappingAs()
	{
		try
		{
			securedMapping.samePrefixMappingAs(GraphFactory
					.createDefaultGraph().getPrefixMapping());
			if (!securityEvaluator.evaluate(Action.Read,
					securedMapping.getModelNode()))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read,
					securedMapping.getModelNode()))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testSetNsPrefix()
	{
		try
		{
			securedMapping.setNsPrefix("foo", "http://example.com/foo");
			if (!securityEvaluator.evaluate(Action.Update,
					securedMapping.getModelNode()))
			{

				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Update,
					securedMapping.getModelNode()))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}

		try
		{
			securedMapping.setNsPrefixes(GraphFactory.createDefaultGraph()
					.getPrefixMapping());
			if (!securityEvaluator.evaluate(Action.Update,
					securedMapping.getModelNode()))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Update,
					securedMapping.getModelNode()))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}

		try
		{
			securedMapping.setNsPrefixes(new HashMap<String, String>());
			if (!securityEvaluator.evaluate(Action.Update,
					securedMapping.getModelNode()))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Update,
					securedMapping.getModelNode()))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testShortForm()
	{
		try
		{
			securedMapping.shortForm("http://example.com/foo/bar");
			if (!securityEvaluator.evaluate(Action.Read,
					securedMapping.getModelNode()))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read,
					securedMapping.getModelNode()))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testWithDefaultMappings()
	{
		try
		{
			securedMapping.withDefaultMappings(GraphFactory
					.createDefaultGraph().getPrefixMapping());
			if (!securityEvaluator.evaluate(Action.Update,
					securedMapping.getModelNode()))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Update,
					securedMapping.getModelNode()))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

}
