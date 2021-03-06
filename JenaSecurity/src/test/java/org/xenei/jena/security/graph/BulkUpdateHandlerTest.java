package org.xenei.jena.security.graph;

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
import org.xenei.jena.security.AccessDeniedException;
import org.xenei.jena.security.Factory;
import org.xenei.jena.security.SecurityEvaluator;
import org.xenei.jena.security.SecurityEvaluator.Action;
import org.xenei.jena.security.SecurityEvaluatorParameters;
import org.xenei.jena.security.utils.CollectionGraph;

@RunWith( value = SecurityEvaluatorParameters.class )
public class BulkUpdateHandlerTest
{
	protected SecuredBulkUpdateHandler handler;
	private final SecurityEvaluator securityEvaluator;
	private final Triple[] tripleArray;
	private final Set<Action> deleteAndUpdate;
	private final Set<Action> createAndUpdate;

	public BulkUpdateHandlerTest( final SecurityEvaluator securityEvaluator )
	{
		this.securityEvaluator = securityEvaluator;

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
		createAndUpdate = SecurityEvaluator.Util.asSet(new Action[] {
				Action.Create, Action.Update });
		deleteAndUpdate = SecurityEvaluator.Util.asSet(new Action[] {
				Action.Delete, Action.Update });
	}

	@Before
	public void setup()
	{
		final Graph g = GraphFactory.createDefaultGraph();

		final SecuredGraph sg = Factory.getInstance(securityEvaluator,
				"http://example.com/testGraph", g);
		handler = sg.getBulkUpdateHandler();
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
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
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
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
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
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
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
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
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
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
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
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
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
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
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
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
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
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
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
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
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
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
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
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
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
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
			}
		}
	}

}
