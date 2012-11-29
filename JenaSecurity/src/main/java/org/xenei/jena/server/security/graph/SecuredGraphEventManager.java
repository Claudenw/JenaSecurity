package org.xenei.jena.server.security.graph;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.GraphEventManager;
import com.hp.hpl.jena.graph.GraphListener;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.WrappedIterator;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.xenei.jena.server.security.CachedSecurityEvaluator;
import org.xenei.jena.server.security.SecuredItemImpl;
import org.xenei.jena.server.security.SecurityEvaluator;
import org.xenei.jena.server.security.SecurityEvaluator.Action;
import org.xenei.jena.server.security.utils.CollectionGraph;
import org.xenei.jena.server.security.utils.PermTripleFilter;

public class SecuredGraphEventManager implements GraphEventManager
{
	private class SecuredGraphListener implements GraphListener
	{
		private final GraphListener wrapped;
		private final Principal runAs;

		SecuredGraphListener( final GraphListener wrapped )
		{
			if (wrapped == null)
			{
				throw new IllegalArgumentException( "Wrapped listener may not be null");
			}
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

	
	// the security evaluator in use
	private final SecuredGraph securedGraph;
	private final Map<GraphListener, Stack<SecuredGraphListener>> listenerMap = new HashMap<GraphListener, Stack<SecuredGraphListener>>();
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
		manager.register(this);
	}

	@Override
	public boolean listening()
	{
		return ! listenerMap.isEmpty();
	}

	private synchronized Collection<SecuredGraphListener>  getListenerCollection()
	{
		ExtendedIterator<SecuredGraphListener> retval = WrappedIterator.emptyIterator();
		for (Collection<SecuredGraphListener> coll : listenerMap.values())
		{
			retval = retval.andThen( coll.iterator() );
		}
		return retval.toList();
	}
	
	
	@Override
	public void notifyAddArray( final Graph g, final Triple[] triples )
	{
		boolean wrap = securedGraph.getBaseItem().equals(g);
		
		for (SecuredGraphListener sgl : getListenerCollection() )
		{
			if (wrap)
			{
				sgl.notifyAddArray(securedGraph, triples);
			}
			else
			{
				sgl.notifyAddArray(g, triples);
			}
		}
	}

	@Override
	public void notifyAddGraph( final Graph g, final Graph added )
	{
		boolean wrap = securedGraph.getBaseItem().equals(g);
		
		for (SecuredGraphListener sgl : getListenerCollection() )
		{
			if (wrap)
			{
				sgl.notifyAddGraph(securedGraph, added);
			}
			else
			{
				sgl.notifyAddGraph(g, added);
			}
		}
	}

	@Override
	public void notifyAddIterator( final Graph g, final Iterator<Triple> it )
	{
		notifyAddList( g, WrappedIterator.create(it).toList() );
		boolean wrap = securedGraph.getBaseItem().equals(g);
	}

	@Override
	public void notifyAddIterator( final Graph g, final List<Triple> triples )
	{
		boolean wrap = securedGraph.getBaseItem().equals(g);
		
		for (SecuredGraphListener sgl : getListenerCollection() )
		{
			if (wrap)
			{
				sgl.notifyAddList(securedGraph, triples);
			}
			else
			{
				sgl.notifyAddList(g, triples);
			}
		}
	}

	@Override
	public void notifyAddList( final Graph g, final List<Triple> triples )
	{
boolean wrap = securedGraph.getBaseItem().equals(g);
		
		for (SecuredGraphListener sgl : getListenerCollection() )
		{
			if (wrap)
			{
				sgl.notifyAddList(securedGraph, triples);
			}
			else
			{
				sgl.notifyAddList(g, triples);
			}
		}
	}

	@Override
	public void notifyAddTriple( final Graph g, final Triple t )
	{
boolean wrap = securedGraph.getBaseItem().equals(g);
		
		for (SecuredGraphListener sgl : getListenerCollection() )
		{
			if (wrap)
			{
				sgl.notifyAddTriple(securedGraph, t);
			}
			else
			{
				sgl.notifyAddTriple(g, t);
			}
		}
	}

	@Override
	public void notifyDeleteArray( final Graph g, final Triple[] triples )
	{
boolean wrap = securedGraph.getBaseItem().equals(g);
		
		for (SecuredGraphListener sgl : getListenerCollection() )
		{
			if (wrap)
			{
				sgl.notifyDeleteArray(securedGraph, triples);
			}
			else
			{
				sgl.notifyDeleteArray(g, triples);
			}
		}
	}

	@Override
	public void notifyDeleteGraph( final Graph g, final Graph removed )
	{
boolean wrap = securedGraph.getBaseItem().equals(g);
		
		for (SecuredGraphListener sgl : getListenerCollection() )
		{
			if (wrap)
			{
				sgl.notifyDeleteGraph(securedGraph, removed);
			}
			else
			{
				sgl.notifyDeleteGraph(g, removed);
			}
		}
	}

	@Override
	public void notifyDeleteIterator( final Graph g, final Iterator<Triple> it )
	{
		notifyDeleteList( g, WrappedIterator.create(it).toList() );
	}

	@Override
	public void notifyDeleteIterator( final Graph g, final List<Triple> triples )
	{
boolean wrap = securedGraph.getBaseItem().equals(g);
		
		for (SecuredGraphListener sgl : getListenerCollection() )
		{
			if (wrap)
			{
				sgl.notifyDeleteList(securedGraph, triples);
			}
			else
			{
				sgl.notifyDeleteList(g, triples);
			}
		}
	}

	@Override
	public void notifyDeleteList( final Graph g, final List<Triple> L )
	{
boolean wrap = securedGraph.getBaseItem().equals(g);
		
		for (SecuredGraphListener sgl : getListenerCollection() )
		{
			if (wrap)
			{
				sgl.notifyDeleteList(securedGraph, L);
			}
			else
			{
				sgl.notifyDeleteList(g, L);
			}
		}
	}

	@Override
	public void notifyDeleteTriple( final Graph g, final Triple t )
	{
boolean wrap = securedGraph.getBaseItem().equals(g);
		
		for (SecuredGraphListener sgl : getListenerCollection() )
		{
			if (wrap)
			{
				sgl.notifyDeleteTriple(securedGraph, t);
			}
			else
			{
				sgl.notifyDeleteTriple(g, t);
			}
		}
	}

	@Override
	public void notifyEvent( final Graph source, final Object value )
	{
		if (source.equals( securedGraph ))
		{
			Graph g = (Graph)securedGraph.getBaseItem();
			g.getEventManager().notifyEvent( g, value );		
		}
		for (SecuredGraphListener sgl : getListenerCollection() )
		{
			sgl.notifyEvent(source, value);
		}
	}

	@Override
	public synchronized GraphEventManager register( final GraphListener listener )
	{
		Stack<SecuredGraphListener> sgl = listenerMap.get(listener);
		if (sgl == null)
		{
			sgl = new Stack<SecuredGraphListener>();;
		}
		sgl.push( new SecuredGraphListener( listener ));
		listenerMap.put( listener, sgl );
		return this;
	}

	@Override
	public synchronized GraphEventManager unregister( final GraphListener listener )
	{
		Stack<SecuredGraphListener> sgl = listenerMap.get(listener);
		if (sgl != null)
		{
			if (sgl.size() == 1)
			{
				listenerMap.remove( listener );
			}
			else
			{
				sgl.pop();
				listenerMap.put(listener, sgl);
			}
		}
		return this;
	}

}
