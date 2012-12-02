package org.xenei.jena.server.security.graph;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.GraphEventManager;
import com.hp.hpl.jena.graph.GraphListener;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.graph.GraphFactory;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xenei.jena.server.security.Factory;
import org.xenei.jena.server.security.MockSecurityEvaluator;
import org.xenei.jena.server.security.SecurityEvaluator;
import org.xenei.jena.server.security.SecurityEvaluator.Action;
import org.xenei.jena.server.security.SecurityEvaluatorParameters;
import org.xenei.jena.server.security.utils.CollectionGraph;

@RunWith( value = SecurityEvaluatorParameters.class )
public class GraphEventManagerTest
{
	private class RecordingGraphListener implements GraphListener
	{

		private boolean add;
		private boolean delete;
		private boolean event;

		public boolean isAdd()
		{
			return add;
		}

		public boolean isDelete()
		{
			return delete;
		}

		public boolean isEvent()
		{
			return event;
		}

		@Override
		public void notifyAddArray( final Graph g, final Triple[] triples )
		{
			add = true;
		}

		@Override
		public void notifyAddGraph( final Graph g, final Graph added )
		{
			add = true;
		}

		@Override
		public void notifyAddIterator( final Graph g, final Iterator<Triple> it )
		{
			add = true;
		}

		@Override
		public void notifyAddList( final Graph g, final List<Triple> triples )
		{
			add = true;
		}

		@Override
		public void notifyAddTriple( final Graph g, final Triple t )
		{
			add = true;
		}

		@Override
		public void notifyDeleteArray( final Graph g, final Triple[] triples )
		{
			delete = true;
		}

		@Override
		public void notifyDeleteGraph( final Graph g, final Graph removed )
		{
			delete = true;
		}

		@Override
		public void notifyDeleteIterator( final Graph g,
				final Iterator<Triple> it )
		{
			delete = true;
		}

		@Override
		public void notifyDeleteList( final Graph g, final List<Triple> L )
		{
			delete = true;
		}

		@Override
		public void notifyDeleteTriple( final Graph g, final Triple t )
		{
			delete = true;
		}

		@Override
		public void notifyEvent( final Graph source, final Object value )
		{
			event = true;
		}

		public void reset()
		{
			add = false;
			delete = false;
			event = false;
		}

	}

	private final GraphEventManager manager;
	private final Graph g;
	private final SecuredGraph sg;
	private final SecurityEvaluator securityEvaluator;
	private Triple[] tripleArray;

	private final RecordingGraphListener listener;

	public GraphEventManagerTest( final MockSecurityEvaluator securityEvaluator )
	{
		this.securityEvaluator = securityEvaluator;
		g = GraphFactory.createDefaultGraph();

		sg = Factory.getInstance(securityEvaluator,
				"http://example.com/testGraph", g);
		manager = sg.getEventManager();
		listener = new RecordingGraphListener();
		manager.register(listener);

	}

	@Test
	public void notifyAddTest()
	{
		final Set<Action> ADD = SecurityEvaluator.Util.asSet(new Action[] {
				Action.Create, Action.Read });
		g.add(tripleArray[0]);
		if (securityEvaluator.evaluateAny(ADD, sg.getModelNode()))
		{
			Assert.assertTrue("Should recorded add", listener.isAdd());
		}
		else
		{
			Assert.assertFalse("Should not have recorded add", listener.isAdd());
		}
		g.delete(Triple.ANY);
		listener.reset();

		g.getBulkUpdateHandler().add(tripleArray);
		if (securityEvaluator.evaluateAny(ADD, sg.getModelNode()))
		{
			Assert.assertTrue("Should recorded add", listener.isAdd());
		}
		else
		{
			Assert.assertFalse("Should not have recorded add", listener.isAdd());
		}
		g.delete(Triple.ANY);
		listener.reset();

		g.getBulkUpdateHandler().add(Arrays.asList(tripleArray));
		if (securityEvaluator.evaluateAny(ADD, sg.getModelNode()))
		{
			Assert.assertTrue("Should recorded add", listener.isAdd());
		}
		else
		{
			Assert.assertFalse("Should not have recorded add", listener.isAdd());
		}
		g.delete(Triple.ANY);
		listener.reset();

		g.getBulkUpdateHandler().add(Arrays.asList(tripleArray).iterator());
		if (securityEvaluator.evaluateAny(ADD, sg.getModelNode()))
		{
			Assert.assertTrue("Should recorded add", listener.isAdd());
		}
		else
		{
			Assert.assertFalse("Should not have recorded add", listener.isAdd());
		}
		g.delete(Triple.ANY);
		listener.reset();

		g.getBulkUpdateHandler().add(
				new CollectionGraph(Arrays.asList(tripleArray)));
		if (securityEvaluator.evaluateAny(ADD, sg.getModelNode()))
		{
			Assert.assertTrue("Should recorded add", listener.isAdd());
		}
		else
		{
			Assert.assertFalse("Should not have recorded add", listener.isAdd());
		}
		g.delete(Triple.ANY);
		listener.reset();
	}

	@Test
	public void notifyDeleteTest()
	{
		final Set<Action> DELETE = SecurityEvaluator.Util.asSet(new Action[] {
				Action.Delete, Action.Read });
		g.delete(tripleArray[0]);
		if (securityEvaluator.evaluateAny(DELETE, sg.getModelNode()))
		{
			Assert.assertTrue("Should recorded delete", listener.isDelete());
		}
		else
		{
			Assert.assertFalse("Should not have recorded delete",
					listener.isDelete());
		}

		listener.reset();

		g.getBulkUpdateHandler().delete(tripleArray);
		if (securityEvaluator.evaluateAny(DELETE, sg.getModelNode()))
		{
			Assert.assertTrue("Should recorded delete", listener.isDelete());
		}
		else
		{
			Assert.assertFalse("Should not have recorded delete",
					listener.isDelete());
		}
		listener.reset();

		g.getBulkUpdateHandler().delete(Arrays.asList(tripleArray));
		if (securityEvaluator.evaluateAny(DELETE, sg.getModelNode()))
		{
			Assert.assertTrue("Should recorded delete", listener.isDelete());
		}
		else
		{
			Assert.assertFalse("Should not have recorded delete",
					listener.isDelete());
		}
		listener.reset();

		g.getBulkUpdateHandler().delete(Arrays.asList(tripleArray).iterator());
		if (securityEvaluator.evaluateAny(DELETE, sg.getModelNode()))
		{
			Assert.assertTrue("Should recorded delete", listener.isDelete());
		}
		else
		{
			Assert.assertFalse("Should not have recorded delete",
					listener.isDelete());
		}
		listener.reset();

		g.getBulkUpdateHandler().delete(
				new CollectionGraph(Arrays.asList(tripleArray)));
		if (securityEvaluator.evaluateAny(DELETE, sg.getModelNode()))
		{
			Assert.assertTrue("Should recorded delete", listener.isDelete());
		}
		else
		{
			Assert.assertFalse("Should not have recorded delete",
					listener.isDelete());
		}
		listener.reset();
	}

	@Test
	public void notifyEventTest()
	{
		g.getEventManager().notifyEvent(g, "Foo");
		Assert.assertTrue("Should recorded delete", listener.isEvent());
		listener.reset();
		final RecordingGraphListener listener2 = new RecordingGraphListener();
		g.getEventManager().register(listener2);
		sg.getEventManager().notifyEvent(sg, "Foo");
		Assert.assertTrue("Should recorded delete", listener.isEvent());
		Assert.assertTrue("Should recorded delete", listener2.isEvent());
		listener.reset();

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
}
