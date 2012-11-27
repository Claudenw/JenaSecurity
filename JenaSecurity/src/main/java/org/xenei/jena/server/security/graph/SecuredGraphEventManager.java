package org.xenei.jena.server.security.graph;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.GraphEventManager;
import com.hp.hpl.jena.graph.GraphListener;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.util.iterator.WrappedIterator;

import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xenei.jena.server.security.CachedSecurityEvaluator;
import org.xenei.jena.server.security.SecuredItemImpl;
import org.xenei.jena.server.security.SecurityEvaluator;
import org.xenei.jena.server.security.SecurityEvaluator.Action;

public class SecuredGraphEventManager implements GraphEventManager
{
	private class SecuredGraphListener implements GraphListener
	{
		private final GraphListener wrapped;
		private final Principal runAs;

		SecuredGraphListener( final GraphListener wrapped )
		{
			this.wrapped = wrapped;
			this.runAs = securedGraph.getSecurityEvaluator().getPrincipal();
		}

		@Override
		public void notifyAddArray( final Graph g, final Triple[] triples )
		{
			notifyAddList(g, Arrays.asList(triples));
		}

		@Override
		public void notifyAddGraph( final Graph g, final Graph added )
		{
			if (g instanceof SecuredGraph)
			{
				final SecuredGraph sg = (SecuredGraph) g;
				final SecurityEvaluator evaluator = CachedSecurityEvaluator
						.getInstance(sg.getSecurityEvaluator(), runAs);
				if (evaluator.evaluateAny(SecuredGraphEventManager.ADD,
						sg.getModelNode()))
				{
					Graph g2 = added;
					if (!evaluator.evaluateAny(SecuredGraphEventManager.ADD,
							sg.getModelNode(),
							SecuredItemImpl.convert(Triple.ANY)))
					{
						g2 = new CollectionGraph(added
								.find(Triple.ANY)
								.filterKeep(
										new PermTripleFilter(
												SecuredGraphEventManager.ADD,
												sg, evaluator)).toList());

					}
					wrapped.notifyAddGraph(g, g2);
				}
				else
				{
					// do nothing.
				}
			}
			else
			{
				wrapped.notifyAddGraph(g, added);
			}
		}

		@SuppressWarnings( "unchecked" )
		@Override
		public void notifyAddIterator( final Graph g, final Iterator<Triple> it )
		{
			Iterator<Triple> iter = it;
			if (g instanceof SecuredGraph)
			{
				final SecuredGraph sg = (SecuredGraph) g;
				final SecurityEvaluator evaluator = CachedSecurityEvaluator
						.getInstance(sg.getSecurityEvaluator(), runAs);
				if (evaluator.evaluateAny(SecuredGraphEventManager.ADD,
						sg.getModelNode()))
				{

					if (!evaluator.evaluateAny(SecuredGraphEventManager.ADD,
							sg.getModelNode(),
							SecuredItemImpl.convert(Triple.ANY)))
					{
						iter = WrappedIterator.create(it).filterKeep(
								new PermTripleFilter(
										SecuredGraphEventManager.ADD, sg,
										evaluator));
					}
					// else use the default list as all can bee seen
					wrapped.notifyAddIterator(g, iter);
				}
				else
				{
					// do nothing.
				}
			}
			else
			{
				wrapped.notifyAddIterator(g, iter);
			}

		}

		@Override
		public void notifyAddList( final Graph g, final List<Triple> triples )
		{
			List<Triple> list = triples;
			if (g instanceof SecuredGraph)
			{
				final SecuredGraph sg = (SecuredGraph) g;
				final SecurityEvaluator evaluator = CachedSecurityEvaluator
						.getInstance(sg.getSecurityEvaluator(), runAs);
				if (evaluator.evaluateAny(SecuredGraphEventManager.ADD,
						sg.getModelNode()))
				{
					if (!evaluator.evaluateAny(SecuredGraphEventManager.ADD,
							sg.getModelNode(),
							SecuredItemImpl.convert(Triple.ANY)))
					{
						list = WrappedIterator
								.create(triples.iterator())
								.filterKeep(
										new PermTripleFilter(
												SecuredGraphEventManager.ADD,
												sg, evaluator)).toList();
					}
					// else use the default list as all can bee seen
				}
				else
				{
					list = Collections.emptyList();
				}
			}

			if (list.size() > 0)
			{
				wrapped.notifyAddList(g, list);
			}
		}

		@Override
		public void notifyAddTriple( final Graph g, final Triple t )
		{
			boolean notify = false;
			if (g instanceof SecuredGraph)
			{
				final SecuredGraph sg = (SecuredGraph) g;
				final SecurityEvaluator evaluator = CachedSecurityEvaluator
						.getInstance(sg.getSecurityEvaluator(), runAs);
				notify = evaluator.evaluateAny(SecuredGraphEventManager.ADD,
						sg.getModelNode());
				if (notify)
				{
					notify = evaluator.evaluateAny(
							SecuredGraphEventManager.ADD, sg.getModelNode(),
							SecuredItemImpl.convert(t));
				}
			}
			else
			{
				notify = true;
			}
			if (notify)
			{
				wrapped.notifyAddTriple(g, t);
			}
		}

		@Override
		public void notifyDeleteArray( final Graph g, final Triple[] triples )
		{
			notifyDeleteList(g, Arrays.asList(triples));
		}

		@Override
		public void notifyDeleteGraph( final Graph g, final Graph removed )
		{
			if (g instanceof SecuredGraph)
			{
				final SecuredGraph sg = (SecuredGraph) g;
				final SecurityEvaluator evaluator = CachedSecurityEvaluator
						.getInstance(sg.getSecurityEvaluator(), runAs);
				if (evaluator.evaluateAny(SecuredGraphEventManager.DELETE,
						sg.getModelNode()))
				{
					Graph g2 = removed;
					if (!evaluator.evaluateAny(SecuredGraphEventManager.DELETE,
							sg.getModelNode(),
							SecuredItemImpl.convert(Triple.ANY)))
					{
						g2 = new CollectionGraph(
								removed.find(Triple.ANY)
										.filterKeep(
												new PermTripleFilter(
														SecuredGraphEventManager.DELETE,
														sg, evaluator))
										.toList());

					}
					wrapped.notifyDeleteGraph(g, g2);
				}
				else
				{
					// do nothing.
				}
			}
			else
			{
				wrapped.notifyDeleteGraph(g, removed);
			}
		}

		@Override
		public void notifyDeleteIterator( final Graph g,
				final Iterator<Triple> it )
		{
			Iterator<Triple> iter = it;
			if (g instanceof SecuredGraph)
			{
				final SecuredGraph sg = (SecuredGraph) g;
				final SecurityEvaluator evaluator = CachedSecurityEvaluator
						.getInstance(sg.getSecurityEvaluator(), runAs);
				if (evaluator.evaluateAny(SecuredGraphEventManager.DELETE,
						sg.getModelNode()))
				{

					if (!evaluator.evaluateAny(SecuredGraphEventManager.DELETE,
							sg.getModelNode(),
							SecuredItemImpl.convert(Triple.ANY)))
					{
						iter = WrappedIterator.create(it).filterKeep(
								new PermTripleFilter(
										SecuredGraphEventManager.DELETE, sg,
										evaluator));
					}
					// else use the default list as all can bee seen
					wrapped.notifyDeleteIterator(g, iter);
				}
				else
				{
					// do nothing.
				}
			}
			else
			{
				wrapped.notifyDeleteIterator(g, iter);
			}

		}

		@Override
		public void notifyDeleteList( final Graph g, final List<Triple> triples )
		{
			List<Triple> list = triples;
			if (g instanceof SecuredGraph)
			{
				final SecuredGraph sg = (SecuredGraph) g;
				final SecurityEvaluator evaluator = CachedSecurityEvaluator
						.getInstance(sg.getSecurityEvaluator(), runAs);
				if (evaluator.evaluateAny(SecuredGraphEventManager.DELETE,
						sg.getModelNode()))
				{
					if (!evaluator.evaluateAny(SecuredGraphEventManager.DELETE,
							sg.getModelNode(),
							SecuredItemImpl.convert(Triple.ANY)))
					{
						list = WrappedIterator
								.create(triples.iterator())
								.filterKeep(
										new PermTripleFilter(
												SecuredGraphEventManager.DELETE,
												sg, evaluator)).toList();
					}
					// else use the default list as all can bee seen
				}
				else
				{
					list = Collections.emptyList();
				}
			}

			if (list.size() > 0)
			{
				wrapped.notifyDeleteList(g, list);
			}
		}

		@Override
		public void notifyDeleteTriple( final Graph g, final Triple t )
		{
			boolean notify = false;
			if (g instanceof SecuredGraph)
			{
				final SecuredGraph sg = (SecuredGraph) g;
				final SecurityEvaluator evaluator = CachedSecurityEvaluator
						.getInstance(sg.getSecurityEvaluator(), runAs);
				notify = evaluator.evaluateAny(SecuredGraphEventManager.DELETE,
						sg.getModelNode());
				if (notify)
				{
					notify = evaluator.evaluateAny(
							SecuredGraphEventManager.DELETE, sg.getModelNode(),
							SecuredItemImpl.convert(t));
				}
			}
			else
			{
				notify = true;
			}
			if (notify)
			{
				wrapped.notifyDeleteTriple(g, t);
			}
		}

		@Override
		public void notifyEvent( final Graph source, final Object value )
		{
			wrapped.notifyEvent(source, value);
		}

	}

	private final GraphEventManager manager;
	// the security evaluator in use
	private final SecuredGraph securedGraph;
	private final Map<GraphListener, SecuredGraphListener> listenerMap = new HashMap<GraphListener, SecuredGraphListener>();
	private static Set<Action> DELETE;

	private static Set<Action> ADD;

	static
	{
		SecuredGraphEventManager.ADD = new HashSet<Action>(
				Arrays.asList(new Action[] { Action.Create, Action.Read }));
		SecuredGraphEventManager.DELETE = new HashSet<Action>(
				Arrays.asList(new Action[] { Action.Delete, Action.Read }));
	}

	public SecuredGraphEventManager( final SecuredGraph securedGraph,
			final GraphEventManager manager )
	{
		this.securedGraph = securedGraph;
		this.manager = manager;
	}

	@Override
	public boolean listening()
	{
		return manager.listening();
	}

	@Override
	public void notifyAddArray( final Graph g, final Triple[] triples )
	{
		manager.notifyAddArray(g, triples);
	}

	@Override
	public void notifyAddGraph( final Graph g, final Graph added )
	{
		manager.notifyAddGraph(g, added);
	}

	@Override
	public void notifyAddIterator( final Graph g, final Iterator<Triple> it )
	{
		manager.notifyAddIterator(g, it);
	}

	@Override
	public void notifyAddIterator( final Graph g, final List<Triple> triples )
	{
		manager.notifyAddIterator(g, triples);
	}

	@Override
	public void notifyAddList( final Graph g, final List<Triple> triples )
	{
		manager.notifyAddList(g, triples);
	}

	@Override
	public void notifyAddTriple( final Graph g, final Triple t )
	{
		manager.notifyAddTriple(g, t);
	}

	@Override
	public void notifyDeleteArray( final Graph g, final Triple[] triples )
	{
		manager.notifyDeleteArray(g, triples);
	}

	@Override
	public void notifyDeleteGraph( final Graph g, final Graph removed )
	{
		manager.notifyDeleteGraph(g, removed);
	}

	@Override
	public void notifyDeleteIterator( final Graph g, final Iterator<Triple> it )
	{
		manager.notifyDeleteIterator(g, it);
	}

	@Override
	public void notifyDeleteIterator( final Graph g, final List<Triple> triples )
	{
		manager.notifyDeleteIterator(g, triples);
	}

	@Override
	public void notifyDeleteList( final Graph g, final List<Triple> L )
	{
		manager.notifyDeleteList(g, L);
	}

	@Override
	public void notifyDeleteTriple( final Graph g, final Triple t )
	{
		manager.notifyDeleteTriple(g, t);
	}

	@Override
	public void notifyEvent( final Graph source, final Object value )
	{
		manager.notifyEvent(source, value);
	}

	@Override
	public GraphEventManager register( final GraphListener listener )
	{
		SecuredGraphListener sgl = listenerMap.get(listener);
		if (sgl == null)
		{
			sgl = new SecuredGraphListener(listener);
		}
		return manager.register(sgl);
	}

	@Override
	public GraphEventManager unregister( final GraphListener listener )
	{
		SecuredGraphListener sgl = listenerMap.get(listener);
		if (sgl == null)
		{
			sgl = new SecuredGraphListener(listener);
		}
		return manager.unregister(sgl);
	}

}
