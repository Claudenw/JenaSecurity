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
import org.xenei.jena.server.security.utils.CollectionGraph;

@RunWith( value = SecurityEvaluatorParameters.class )
public class BulkUpdateHandlerTest
{
	private final SecuredBulkUpdateHandler handler;
	private final SecurityEvaluator securityEvaluator;
	private Triple[] tripleArray;
	private Set<Action> deleteAndUpdate;
	private Set<Action> createAndUpdate;

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
		createAndUpdate = SecurityEvaluator.Util.asSet( new Action[] {Action.Create,Action.Update});
		deleteAndUpdate = SecurityEvaluator.Util.asSet( new Action[] {Action.Delete,Action.Update});
	}

	@Test
	public void testAdd()
	{
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
				Assert.fail("Should not have thrown AccessDenied Exception");
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
				Assert.fail("Should not have thrown AccessDenied Exception");
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
				Assert.fail("Should not have thrown AccessDenied Exception");
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
				Assert.fail("Should not have thrown AccessDenied Exception");
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
				Assert.fail("Should not have thrown AccessDenied Exception");
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
				Assert.fail("Should not have thrown AccessDenied Exception");
			}
		}
	}

	@Test
	public void testDelete()
	{

		try
		{
			handler.delete(tripleArray);
			if (!securityEvaluator.evaluate(deleteAndUpdate,
					handler.getModelNode()))
			{
				
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(deleteAndUpdate,
					handler.getModelNode()))
			{
				Assert.fail("Should not have thrown AccessDenied Exception");
			}
		}

		try
		{
			handler.delete(Arrays.asList(tripleArray));
			if (!securityEvaluator.evaluate(deleteAndUpdate,
					handler.getModelNode()))
			{
				
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(deleteAndUpdate,
					handler.getModelNode()))
			{
				Assert.fail("Should not have thrown AccessDenied Exception");
			}
		}

		try
		{
			handler.delete(Arrays.asList(tripleArray).iterator());
			if (!securityEvaluator.evaluate(deleteAndUpdate,
					handler.getModelNode()))
			{
				
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(deleteAndUpdate,
					handler.getModelNode()))
			{
				Assert.fail("Should not have thrown AccessDenied Exception");
			}
		}

		try
		{
			handler.delete(new CollectionGraph(Arrays.asList(tripleArray)));
			if (!securityEvaluator.evaluate(deleteAndUpdate,
					handler.getModelNode()))
			{
				
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(deleteAndUpdate,
					handler.getModelNode()))
			{
				Assert.fail("Should not have thrown AccessDenied Exception");
			}
		}

		try
		{
			handler.delete(new CollectionGraph(Arrays.asList(tripleArray)),
					true);
			if (!securityEvaluator.evaluate(deleteAndUpdate,
					handler.getModelNode()))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(deleteAndUpdate,
					handler.getModelNode()))
			{
				Assert.fail("Should not have thrown AccessDenied Exception");
			}
		}

	}

	public void testRemove()
	{
		try
		{
			handler.remove(Node.createURI("http://example.com/1"),
					Node.createURI("http://example.com/v"), Node.createAnon());
			if (!securityEvaluator.evaluate(deleteAndUpdate,
					handler.getModelNode()))
			{
				
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(deleteAndUpdate,
					handler.getModelNode()))
			{
				Assert.fail("Should not have thrown AccessDenied Exception");
			}
		}
	}

	public void testRemoveAll()
	{
		try
		{
			handler.removeAll();
			if (!securityEvaluator.evaluate(deleteAndUpdate,
					handler.getModelNode()))
			{
				
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(deleteAndUpdate,
					handler.getModelNode()))
			{
				Assert.fail("Should not have thrown AccessDenied Exception");
			}
		}
	}

}
