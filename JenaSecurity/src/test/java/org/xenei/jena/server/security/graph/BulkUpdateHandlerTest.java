package org.xenei.jena.server.security.graph;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.graph.GraphFactory;

import java.util.Arrays;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xenei.jena.server.security.AccessDeniedException;
import org.xenei.jena.server.security.Factory;
import org.xenei.jena.server.security.SecurityEvaluator;
import org.xenei.jena.server.security.SecurityEvaluator.Action;

@RunWith( value = SecurityEvaluatorParameters.class )
public class BulkUpdateHandlerTest
{
	private final SecuredBulkUpdateHandler handler;
	private final SecurityEvaluator securityEvaluator;
	private Triple[] tripleArray;

	public BulkUpdateHandlerTest( final SecurityEvaluator securityEvaluator )
	{
		this.securityEvaluator = securityEvaluator;
		final Graph g = GraphFactory.createDefaultGraph();

		final SecuredGraph sg = Factory.getInstance(securityEvaluator,
				"http://example.com/testGraph", g);
		handler = (SecuredBulkUpdateHandler) sg.getBulkUpdateHandler();
	}

	@Before
	public void setup()
	{
		tripleArray = new Triple[] {
				new Triple(Node.createURI("http://example.com/1"),
						Node.createURI("http://example.com/v"),
						Node.createAnon()),
				new Triple(Node.createURI("http://example.com/2"),
						Node.createURI("http://example.com/v"),
						Node.createAnon()),
				new Triple(Node.createURI("http://example.com/3"),
						Node.createURI("http://example.com/v"),
						Node.createAnon()) };

	}

	@Test
	public void testAdd()
	{
		final Set<Action> createAndUpdate = SecurityEvaluator.Util
				.asSet(new Action[] { Action.Update, Action.Create });
		try
		{
			handler.add(tripleArray);
			if (!securityEvaluator.evaluate(createAndUpdate,
					handler.getModelNode()))
			{
				
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(createAndUpdate,
					handler.getModelNode()))
			{
				throw e;
			}
		}

		try
		{
			handler.add(Arrays.asList(tripleArray));
			if (!securityEvaluator.evaluate(createAndUpdate,
					handler.getModelNode()))
			{
				
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(createAndUpdate,
					handler.getModelNode()))
			{
				throw e;
			}
		}

		try
		{
			handler.add(Arrays.asList(tripleArray).iterator());
			if (!securityEvaluator.evaluate(createAndUpdate,
					handler.getModelNode()))
			{
				
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(createAndUpdate,
					handler.getModelNode()))
			{
				throw e;
			}
		}

		try
		{
			handler.add(new CollectionGraph(Arrays.asList(tripleArray)));
			if (!securityEvaluator.evaluate(createAndUpdate,
					handler.getModelNode()))
			{
				
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(createAndUpdate,
					handler.getModelNode()))
			{
				throw e;
			}
		}

		try
		{
			handler.add(new CollectionGraph(Arrays.asList(tripleArray)));
			if (!securityEvaluator.evaluate(createAndUpdate,
					handler.getModelNode()))
			{
				
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(createAndUpdate,
					handler.getModelNode()))
			{
				throw e;
			}
		}

		try
		{
			handler.add(new CollectionGraph(Arrays.asList(tripleArray)), true);
			if (!securityEvaluator.evaluate(createAndUpdate,
					handler.getModelNode()))
			{
				
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(createAndUpdate,
					handler.getModelNode()))
			{
				throw e;
			}
		}
	}

	@Test
	public void testDelete()
	{

		try
		{
			handler.delete(tripleArray);
			if (!securityEvaluator.evaluate(Action.Delete,
					handler.getModelNode()))
			{
				
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Delete,
					handler.getModelNode()))
			{
				throw e;
			}
		}

		try
		{
			handler.delete(Arrays.asList(tripleArray));
			if (!securityEvaluator.evaluate(Action.Delete,
					handler.getModelNode()))
			{
				
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Delete,
					handler.getModelNode()))
			{
				throw e;
			}
		}

		try
		{
			handler.delete(Arrays.asList(tripleArray).iterator());
			if (!securityEvaluator.evaluate(Action.Delete,
					handler.getModelNode()))
			{
				
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Delete,
					handler.getModelNode()))
			{
				throw e;
			}
		}

		try
		{
			handler.delete(new CollectionGraph(Arrays.asList(tripleArray)));
			if (!securityEvaluator.evaluate(Action.Delete,
					handler.getModelNode()))
			{
				
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Delete,
					handler.getModelNode()))
			{
				throw e;
			}
		}

		try
		{
			handler.delete(new CollectionGraph(Arrays.asList(tripleArray)),
					true);
			if (!securityEvaluator.evaluate(Action.Delete,
					handler.getModelNode()))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Delete,
					handler.getModelNode()))
			{
				throw e;
			}
		}

	}

	public void testRemove()
	{
		try
		{
			handler.remove(Node.createURI("http://example.com/1"),
					Node.createURI("http://example.com/v"), Node.createAnon());
			if (!securityEvaluator.evaluate(Action.Delete,
					handler.getModelNode()))
			{
				
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Delete,
					handler.getModelNode()))
			{
				throw e;
			}
		}
	}

	public void testRemoveAll()
	{
		try
		{
			handler.removeAll();
			if (!securityEvaluator.evaluate(Action.Delete,
					handler.getModelNode()))
			{
				
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Delete,
					handler.getModelNode()))
			{
				throw e;
			}
		}
	}

}
