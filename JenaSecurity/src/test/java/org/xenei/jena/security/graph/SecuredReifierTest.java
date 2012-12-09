package org.xenei.jena.security.graph;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Reifier;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.graph.GraphFactory;

import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xenei.jena.security.AccessDeniedException;
import org.xenei.jena.security.Factory;
import org.xenei.jena.security.SecurityEvaluator;
import org.xenei.jena.security.SecurityEvaluatorParameters;
import org.xenei.jena.security.SecurityEvaluator.Action;
import org.xenei.jena.security.graph.SecuredGraph;
import org.xenei.jena.security.graph.SecuredReifier;

@RunWith( value = SecurityEvaluatorParameters.class )
public class SecuredReifierTest
{
	private final SecurityEvaluator securityEvaluator;
	protected SecuredReifier securedReifier;
	protected Reifier baseReifier;

	public SecuredReifierTest( final SecurityEvaluator securityEvaluator )
	{
		this.securityEvaluator = securityEvaluator;
	}
	
	@Before
	public void setup() {
		final Graph g = GraphFactory.createDefaultGraph();
		baseReifier = g.getReifier();
		final SecuredGraph sg = Factory.getInstance(securityEvaluator,
				"http://example.com/testGraph", g);
		this.securedReifier = sg.getReifier();
	}

	@Test
	public void testAllNodes()
	{
		try
		{
			securedReifier.allNodes();
			if (!securityEvaluator.evaluate(Action.Read,
					securedReifier.getModelNode()))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read,
					securedReifier.getModelNode()))
			{
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
			}
		}

		try
		{
			securedReifier.allNodes(Triple.ANY);
			if (!securityEvaluator.evaluate(Action.Read,
					securedReifier.getModelNode()))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read,
					securedReifier.getModelNode()))
			{
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
			}
		}
	}

	@Test
	public void testFind()
	{
		try
		{
			securedReifier.find(Triple.ANY);
			if (!securityEvaluator.evaluate(Action.Read,
					securedReifier.getModelNode()))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read,
					securedReifier.getModelNode()))
			{
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
			}
		}
	}

	@Test
	public void testFindEither()
	{
		try
		{
			securedReifier.findEither(Triple.ANY, true);
			if (!securityEvaluator.evaluate(Action.Read,
					securedReifier.getModelNode()))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read,
					securedReifier.getModelNode()))
			{
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
			}
		}
	}

	@Test
	public void testFindExposed()
	{
		try
		{
			securedReifier.findExposed(Triple.ANY);
			if (!securityEvaluator.evaluate(Action.Read,
					securedReifier.getModelNode()))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read,
					securedReifier.getModelNode()))
			{
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
			}
		}
	}

	@Test
	public void testGetParentGraph()
	{
		final Graph g = securedReifier.getParentGraph();
		Assert.assertTrue("Should have been an instance of SecuredGraph",
				g instanceof SecuredGraph);
	}

	@Test
	public void testGetStyle()
	{
		securedReifier.getStyle();
	}

	@Test
	public void testGetTriple()
	{
		try
		{
			securedReifier.getTriple( Node.createAnon());
			if (!securityEvaluator.evaluate(Action.Read,
					securedReifier.getModelNode()))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read,
					securedReifier.getModelNode()))
			{
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
			}
		}
	}

	@Test
	public void testHandledAdd()
	{
		Set<Action> perms = SecurityEvaluator.Util.asSet( new Action[] {Action.Update, Action.Create });
		Triple t = new Triple( Node.createAnon(), Node.createURI( "http://example.com/examplePred"), Node.createLiteral( "yee haw"));
		try
		{
			securedReifier.handledAdd( t );
			if (!securityEvaluator.evaluate(perms,
					securedReifier.getModelNode()))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(perms,
					securedReifier.getModelNode()))
			{
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
			}
		}
	}

	@Test
	public void testHandledRemove()
	{
		Set<Action> perms = SecurityEvaluator.Util.asSet( new Action[] {Action.Update, Action.Delete });
		Triple t = new Triple( Node.createAnon(), Node.createURI( "http://example.com/examplePred"), Node.createLiteral( "yee haw"));

		try
		{
			securedReifier.handledRemove(t);
			if (!securityEvaluator.evaluate(perms,
					securedReifier.getModelNode()))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(perms,
					securedReifier.getModelNode()))
			{
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
			}
		}
	}

	@Test
	public void testHasTriple()
	{
		try
		{
			securedReifier.hasTriple(Node.ANY);
			if (!securityEvaluator.evaluate(Action.Read,
					securedReifier.getModelNode()))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read,
					securedReifier.getModelNode()))
			{
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
			}
		}

		try
		{
			securedReifier.hasTriple(Triple.ANY);
			if (!securityEvaluator.evaluate(Action.Read,
					securedReifier.getModelNode()))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read,
					securedReifier.getModelNode()))
			{
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
			}
		}
	}

	@Test
	public void testReifyAs()
	{
		final Set<Action> CRU = SecurityEvaluator.Util.asSet(new Action[] {
				Action.Create, Action.Read, Action.Update });
		try
		{
			Triple t  = new Triple( Node.createURI("http://example.com/S" ),
					Node.createURI("http://example.com/P" ),
					Node.createURI("http://example.com/O" ));
			
			securedReifier.reifyAs(Node.createURI("http://example.com/Reified" ), t);
			if (!securityEvaluator.evaluate(CRU, securedReifier.getModelNode()))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(CRU, securedReifier.getModelNode()))
			{
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
			}
		}
	}

	@Test
	public void testRemove()
	{
		final Set<Action> UD = SecurityEvaluator.Util.asSet(new Action[] {
				Action.Update, Action.Delete });
		Triple t = new Triple( Node.createAnon(), Node.createURI( "http://example.com/examplePred"), Node.createLiteral( "yee haw"));
		Node n =Node.createAnon();
		baseReifier.reifyAs( n,  t);
		try
		{
			securedReifier.remove(n, t);
			if (!securityEvaluator.evaluate(UD, securedReifier.getModelNode()))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(UD, securedReifier.getModelNode()))
			{
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
			}
		}

		try
		{
			securedReifier.remove(t);
			if (!securityEvaluator.evaluate(UD, securedReifier.getModelNode()))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(UD, securedReifier.getModelNode()))
			{
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
			}
		}
	}

	@Test
	public void testSize()
	{
		try
		{
			securedReifier.size();
			if (!securityEvaluator.evaluate(Action.Read,
					securedReifier.getModelNode()))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read,
					securedReifier.getModelNode()))
			{
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
			}
		}
	}

}
