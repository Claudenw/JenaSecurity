package org.xenei.jena.server.security.graph;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Reifier;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.TripleMatch;
import com.hp.hpl.jena.shared.ReificationStyle;
import com.hp.hpl.jena.sparql.graph.GraphFactory;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xenei.jena.server.security.AccessDeniedException;
import org.xenei.jena.server.security.Factory;
import org.xenei.jena.server.security.SecuredItem;
import org.xenei.jena.server.security.SecurityEvaluator;
import org.xenei.jena.server.security.SecurityEvaluatorParameters;
import org.xenei.jena.server.security.SecurityEvaluator.Action;

@RunWith( value = SecurityEvaluatorParameters.class )
public class SecuredReifierTest
{
	private SecurityEvaluator securityEvaluator;
	private SecuredReifier securedReifier;

	
	
	public SecuredReifierTest( final SecurityEvaluator securityEvaluator )
	{
		this.securityEvaluator = securityEvaluator;
	}
	
	@Before
	public void setup()
	{
		final Graph g = GraphFactory.createDefaultGraph();

		final SecuredGraph sg = Factory.getInstance(securityEvaluator,
				"http://example.com/testGraph", g);
		this.securedReifier = (SecuredReifier) sg.getReifier();
	}


	@Test
	public void testFind( )
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
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}


	@Test
	public void testGetTriple()
	{
		try
		{
			securedReifier.getTriple(Node.ANY);
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
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
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
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}


	@Test
	public void testFindEither()
	{
		try
		{
			securedReifier.findEither(Triple.ANY, true );
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
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
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
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testGetStyle()
	{
		securedReifier.getStyle();
	}


	@Test
	public void testGetParentGraph()
	{
		Graph g = securedReifier.getParentGraph();
		Assert.assertTrue( "Should have been an instance of SecuredGraph", g instanceof SecuredGraph );
	}

	@Test
	public void testReifyAs()
	{
		Set<Action> CRU = SecurityEvaluator.Util.asSet( new Action[] { Action.Create, Action.Read, Action.Update } ); 
		try
		{
			securedReifier.reifyAs(Node.ANY, Triple.ANY);
			if (!securityEvaluator.evaluate(CRU,
					securedReifier.getModelNode()))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(CRU,
					securedReifier.getModelNode()))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}


	@Test
	public void testHasTriple()
	{
		try
		{
			securedReifier.hasTriple( Node.ANY);
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
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		
		try
		{
			securedReifier.hasTriple( Triple.ANY);
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
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
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
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		
		try
		{
			securedReifier.allNodes(Triple.ANY );
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
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testRemove()
	{
		Set<Action> RUD = SecurityEvaluator.Util.asSet( new Action[] { Action.Read, Action.Update, Action.Delete } ); 
		try
		{
			securedReifier.remove(Node.ANY, Triple.ANY);
			if (!securityEvaluator.evaluate(RUD,
					securedReifier.getModelNode()))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(RUD,
					securedReifier.getModelNode()))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		
		try
		{
			securedReifier.remove(Triple.ANY);
			if (!securityEvaluator.evaluate(RUD,
					securedReifier.getModelNode()))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(RUD,
					securedReifier.getModelNode()))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testHandledAdd()
	{
		try
		{
			securedReifier.handledAdd( Triple.ANY );
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
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testHandledRemove()
	{
		try
		{
			securedReifier.handledRemove( Triple.ANY );
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
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}


	public void close()
	{
		securedReifier.close();
	}
	
	
}
