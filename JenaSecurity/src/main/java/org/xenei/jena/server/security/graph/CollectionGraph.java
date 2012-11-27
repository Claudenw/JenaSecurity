package org.xenei.jena.server.security.graph;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.TripleMatch;
import com.hp.hpl.jena.graph.impl.GraphBase;
import com.hp.hpl.jena.shared.ReificationStyle;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.Filter;
import com.hp.hpl.jena.util.iterator.WrappedIterator;

import java.util.Collection;
import java.util.HashSet;

public class CollectionGraph extends GraphBase
{

	private class MatchFilter extends Filter<Triple>
	{
		TripleMatch m;

		public MatchFilter( final TripleMatch m )
		{
			if (m == null)
			{
				throw new IllegalArgumentException("Match must not be null");
			}
			this.m = m;
		}

		@Override
		public boolean accept( final Triple t )
		{
			if (t == null)
			{
				throw new IllegalArgumentException("Triple must not be null");
			}
			return matches(t.getMatchSubject(), m.getMatchSubject())
					&& matches(t.getMatchPredicate(), m.getMatchPredicate())
					&& matches(t.getMatchObject(), m.getMatchObject());
		}

		private boolean isWild( final Node n )
		{
			return (n == null) || Node.ANY.equals(n);
		}

		private boolean matches( final Node t, final Node m )
		{
			return isWild(m) || isWild(t) || m.equals(t);
		}

	}

	Collection<Triple> triples;

	public CollectionGraph()
	{
		this(new HashSet<Triple>());
	}

	public CollectionGraph( final Collection<Triple> triples )
	{
		super();
		this.triples = triples;
	}

	public CollectionGraph( final ReificationStyle style,
			final Collection<Triple> triples )
	{
		super(style);
		this.triples = triples;
	}

	@Override
	protected ExtendedIterator<Triple> graphBaseFind( final TripleMatch m )
	{
		return WrappedIterator.create(triples.iterator()).filterKeep(
				new MatchFilter(m));
	}

	@Override
	public void performAdd( final Triple t )
	{
		triples.add(t);
	}

	@Override
	public void performDelete( final Triple t )
	{
		triples.remove(t);
	}
}
