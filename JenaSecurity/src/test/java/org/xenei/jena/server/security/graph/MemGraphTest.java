package org.xenei.jena.server.security.graph;

import com.hp.hpl.jena.graph.BulkUpdateHandler;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Reifier;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.TripleMatch;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.sparql.graph.GraphFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.xenei.jena.server.security.AccessDeniedException;
import org.xenei.jena.server.security.EqualityTester;
import org.xenei.jena.server.security.MockPrefixMapping;
import org.xenei.jena.server.security.MockSecurityEvaluator;
import org.xenei.jena.server.security.SecurityEvaluator;
import org.xenei.jena.server.security.SecurityEvaluatorParameters;
import org.xenei.jena.server.security.SecurityEvaluator.Action;

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
	public void testBulkHandlerAdd() throws Exception
	{
		final Graph g = GraphFactory.createDefaultGraph();
		g.add(new Triple(Node.createURI("http://example.com/graph/s2"), Node
				.createURI("http://example.com/graph/p2"), Node
				.createURI("http://example.com/graph/o2")));
		final Set<Action> updateAndCreate = SecurityEvaluator.Util
				.asSet(new Action[] { Action.Create, Action.Update });
		try
		{
			graph.getBulkUpdateHandler().add(g);
			if (!securityEvaluator.evaluate(updateAndCreate))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(updateAndCreate))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testBulkHandlerDelete() throws Exception
	{
		final Graph g = GraphFactory.createDefaultGraph();
		g.add(new Triple(Node.createURI("http://example.com/graph/s2"), Node
				.createURI("http://example.com/graph/p2"), Node
				.createURI("http://example.com/graph/o2")));
		try
		{
			graph.getBulkUpdateHandler().delete(g);
			if (!securityEvaluator.evaluate(Action.Delete))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Delete))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testBulkHandlerEquality() throws Exception
	{
		final BulkUpdateHandler bh = graph.getBulkUpdateHandler();
		final BulkUpdateHandler bh2 = g.getBulkUpdateHandler();
		EqualityTester.testInequality("Bulk handler test", bh, bh2);
	}

	@Test
	public void testBulkHandlerRemove() throws Exception
	{
		try
		{
			graph.getBulkUpdateHandler().remove(s, p, o);
			if (!securityEvaluator.evaluate(Action.Delete))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Delete))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testBulkHandlerRemoveAll() throws Exception
	{
		try
		{
			graph.getBulkUpdateHandler().removeAll();

			if (!securityEvaluator.evaluate(Action.Delete))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Delete))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
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
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
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
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}

	}

	@Test
	public void testDelete() throws Exception
	{
		Set<Action> UD = SecurityEvaluator.Util.asSet( new Action[] { Action.Update, Action.Delete} );
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
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
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
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
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
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testExpandPrefix()
	{
		try
		{
			graph.getPrefixMapping().expandPrefix("foo");
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
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
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
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
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testGetNsPrefixMap()
	{
		try
		{
			graph.getPrefixMapping().getNsPrefixMap();
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
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
			graph.getPrefixMapping().getNsPrefixURI("foo");
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
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
			graph.getPrefixMapping().getNsURIPrefix("foo");
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
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
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
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
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testLock()
	{
		try
		{
			graph.getPrefixMapping().lock();
			if (!securityEvaluator.evaluate(Action.Update))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Update))
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
			graph.getPrefixMapping().qnameFor("uri");
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
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
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testReifierAllNodes()
	{
		try
		{
			graph.getReifier().allNodes();
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testReifierAllNodesTriple()
	{
		try
		{
			graph.getReifier().allNodes(t);
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testReifierClose()
	{
		graph.getReifier().close();

	}

	@Test
	public void testReifierEquality()
	{
		final Reifier r1 = graph.getReifier();
		final Reifier r2 = graph.getReifier();
		EqualityTester.testEquality("Reifier test", r1, r2);
	}

	@Test
	public void testReifierFindEither()
	{
		try
		{
			graph.getReifier().findEither(t, true);
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testReifierFindExposed()
	{
		try
		{
			graph.getReifier().findExposed(t);
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testReifierFindTripleMatch()
	{
		try
		{
			graph.getReifier().hasTriple(t);
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testReifierGetParentGraph()
	{
		final Graph g2 = graph.getReifier().getParentGraph();
		EqualityTester.testEquality("getParentGraph", graph, g2);
	}

	@Test
	public void testReifierGetStyle()
	{
		graph.getReifier().getStyle();

	}

	@Test
	public void testReifierGetTriple()
	{
		try
		{
			graph.getReifier().hasTriple(Node.createURI("foo"));
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testReifierHandledAdd()
	{
		try
		{
			graph.getReifier().handledAdd(t);
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testReifierHandledRemove()
	{
		try
		{
			graph.getReifier().handledRemove(t);
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testReifierHasTriple()
	{
		try
		{
			graph.getReifier().hasTriple(t);
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testReifierHasTripleNode()
	{
		try
		{
			graph.getReifier().hasTriple(Node.createURI("foo"));
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testReifierReifyAs()
	{
		Set<Action> CRU = SecurityEvaluator.Util.asSet( new Action[] { Action.Create,
					Action.Read, Action.Update });
		try
		{
			graph.getReifier().reifyAs(Node.createURI("foo"), t);
			if (!securityEvaluator.evaluate(CRU))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(CRU))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testReifierRemoveNodeTriple()
	{
		Set<Action> DRU = SecurityEvaluator.Util.asSet( new Action[] { Action.Delete,
				Action.Read, Action.Update });
		try
		{
			graph.getReifier().remove(Node.createURI("foo"), t);
			if (!securityEvaluator.evaluate(DRU))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(DRU))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testReifierRemoveTriple()
	{
		Set<Action> DRU = SecurityEvaluator.Util.asSet( new Action[] { Action.Delete,
				Action.Read, Action.Update });
		try
		{
			graph.getReifier().remove(t);
			if (!securityEvaluator.evaluate(DRU))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(DRU))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testReifierSize()
	{
		try
		{
			graph.getReifier().size();
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
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
			graph.getPrefixMapping().removeNsPrefix("foo");
			if (!securityEvaluator.evaluate(Action.Update))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Update))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}

	}

	@Test
	public void testSamePrefixMappingAs()
	{

		final PrefixMapping pm = new MockPrefixMapping();
		try
		{
			graph.getPrefixMapping().samePrefixMappingAs(pm);
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
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
			graph.getPrefixMapping().setNsPrefix("foo",
					"http://example.com/prefixTest/");
			if (!securityEvaluator.evaluate(Action.Update))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Update))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}

	}

	@Test
	public void testSetNsPrefixes()
	{
		try
		{
			graph.getPrefixMapping().setNsPrefixes(new MockPrefixMapping());
			if (!securityEvaluator.evaluate(Action.Update))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Update))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}

		try
		{
			graph.getPrefixMapping().setNsPrefixes(
					new HashMap<String, String>());
			if (!securityEvaluator.evaluate(Action.Update))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Update))
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
			graph.getPrefixMapping().shortForm("uri");
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
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
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
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
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testWithDefaultMappings()
	{
		try
		{
			graph.getPrefixMapping().setNsPrefixes(
					new HashMap<String, String>());
			if (!securityEvaluator.evaluate(Action.Update))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Update))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

}
