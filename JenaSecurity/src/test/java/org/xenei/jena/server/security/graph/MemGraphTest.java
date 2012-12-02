package org.xenei.jena.server.security.graph;

import com.hp.hpl.jena.graph.BulkUpdateHandler;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Reifier;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.TripleMatch;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.sparql.graph.GraphFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Set;

import org.junit.Assert;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runners.ParentRunner;
import org.xenei.jena.server.security.AccessDeniedException;
import org.xenei.jena.server.security.EqualityTester;
import org.xenei.jena.server.security.MockPrefixMapping;
import org.xenei.jena.server.security.MockSecurityEvaluator;
import org.xenei.jena.server.security.SecurityEvaluator;
import org.xenei.jena.server.security.SecurityEvaluator.Action;
import org.xenei.jena.server.security.SecurityEvaluatorParameters;

@RunWith( value = SecurityEvaluatorParameters.class )
public class MemGraphTest
{
	private SecuredGraph graph;
	private final MockSecurityEvaluator securityEvaluator;
	private Node s;
	private Node p;
	private Node o;
	private Triple t;

	private Graph g;

	public MemGraphTest( final MockSecurityEvaluator securityEvaluator )
	{
		this.securityEvaluator = securityEvaluator;
	}

	protected Graph createGraph() throws Exception
	{
		return GraphFactory.createDefaultGraph();
	}

	@Before
	public void setUp() throws Exception
	{
		g = createGraph();
		g.getBulkUpdateHandler().removeAll();
		graph = org.xenei.jena.server.security.Factory.getInstance(
				securityEvaluator, "http://example.com/securedGraph", g);
		s = Node.createURI("http://example.com/graph/s");
		p = Node.createURI("http://example.com/graph/p");
		o = Node.createURI("http://example.com/graph/o");
		t = new Triple(s, p, o);
		g.add(t);
	}

	
	@Test
	public void testBulkUpdateHandler() throws Exception
	{
		final BulkUpdateHandler buh = graph.getBulkUpdateHandler();
		Assert.assertNotNull( "BulkUpdateHandler may not be null", buh );
		Assert.assertTrue( "BulkUpdateHandler should be secured", buh instanceof SecuredBulkUpdateHandler );
		BulkUpdateHandlerTest buhTest = new BulkUpdateHandlerTest( securityEvaluator ) {
			public void setup()
			{
				this.handler = (SecuredBulkUpdateHandler) buh;
			}
		};
		for (Method m : buhTest.getClass().getMethods())
		{
			if (m.isAnnotationPresent(Test.class))
			{
				buhTest.setup();
				m.invoke( buhTest ); 
			}
		}
	}
	
	@Test
	public void testContainsNodes() throws Exception
	{
		try
		{
			Assert.assertTrue(graph.contains(s, p, o));
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
	public void testContainsTriple() throws Exception
	{
		try
		{
			Assert.assertTrue(graph.contains(t));
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
	public void testDelete() throws Exception
	{
		final Set<Action> UD = SecurityEvaluator.Util.asSet(new Action[] {
				Action.Update, Action.Delete });
		try
		{
			graph.delete(t);

			if (!securityEvaluator.evaluate(UD))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
			Assert.assertEquals(0, g.size());

		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(UD))
			{
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
			}
		}
	}

	@Test
	public void testDependsOn() throws Exception
	{
		try
		{
			Assert.assertFalse(graph.dependsOn(GraphFactory
					.createDefaultGraph()));
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
			Assert.assertTrue(graph.dependsOn(g));
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
	public void testFindNodes() throws Exception
	{
		try
		{

			Assert.assertFalse(graph.find(Node.ANY, Node.ANY, Node.ANY)
					.toList().isEmpty());
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
	public void testFindTriple() throws Exception
	{
		try
		{
			Assert.assertFalse(graph.find(t).toList().isEmpty());
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
	public void testGetPrefixMapping() throws Exception
	{
		SecuredPrefixMappingTest.runTests( securityEvaluator, graph.getPrefixMapping() );
	}

	@Test
	public void testInequality()
	{
		EqualityTester.testInequality("proxy and base", graph, g);
		final Graph g2 = org.xenei.jena.server.security.graph.impl.Factory
				.getInstance(securityEvaluator,
						"http://example.com/securedGraph", g);
		EqualityTester.testInequality("proxy and proxy2", graph, g2);
		EqualityTester.testInequality("base and proxy2", g, g2);
	}

	@Test
	public void testIsIsomorphicWith() throws Exception
	{
		try
		{
			Assert.assertFalse(graph.isIsomorphicWith(GraphFactory
					.createDefaultGraph()));
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
			Assert.assertTrue(graph.isIsomorphicWith(g));
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
	public void testQueryHandler() throws Exception
	{
		try
		{
			graph.queryHandler();
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
	public void testGetReifier() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		final Reifier reifier = graph.getReifier();
		Assert.assertNotNull( "Reifier may not be null", reifier );
		Assert.assertTrue( "Reifier should be secured", reifier instanceof SecuredReifier );
		SecuredReifierTest reifierTest = new SecuredReifierTest( securityEvaluator ){
			public void setup() {
				this.securedReifier = (SecuredReifier) reifier; 
			}
		};
		
		for (Method m : reifierTest.getClass().getMethods())
		{
			if (m.isAnnotationPresent(Test.class))
			{
				reifierTest.setup();
				m.invoke( reifierTest ); 
			}
		}
		
	}

	@Test
	public void testReifierEquality()
	{
		final Reifier r1 = graph.getReifier();
		final Reifier r2 = graph.getReifier();
		EqualityTester.testEquality("Reifier test", r1, r2);
	}

	@Test
	public void testReifierGetParentGraph()
	{
		final Graph g2 = graph.getReifier().getParentGraph();
		EqualityTester.testEquality("getParentGraph", graph, g2);
	}

	@Test
	public void testSize() throws Exception
	{
		try
		{
			Assert.assertEquals(1, graph.size());
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
	public void testTripleMatch() throws Exception
	{
		try
		{
			Assert.assertFalse(graph.find(new TripleMatch() {

				@Override
				public Triple asTriple()
				{
					return Triple.ANY;
				}

				@Override
				public Node getMatchObject()
				{
					return Node.ANY;
				}

				@Override
				public Node getMatchPredicate()
				{
					return Node.ANY;
				}

				@Override
				public Node getMatchSubject()
				{
					return Node.ANY;
				}
			}).toList().isEmpty());
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
