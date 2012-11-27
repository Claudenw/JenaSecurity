package org.xenei.jena.server.security.model.impl;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.EmptyListException;
import com.hp.hpl.jena.rdf.model.EmptyListUpdateException;
import com.hp.hpl.jena.rdf.model.InvalidListException;
import com.hp.hpl.jena.rdf.model.ListIndexException;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFList;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.Map1;
import com.hp.hpl.jena.vocabulary.RDF;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.xenei.jena.server.security.ItemHolder;
import org.xenei.jena.server.security.SecuredItemImpl;
import org.xenei.jena.server.security.SecurityEvaluator;
import org.xenei.jena.server.security.model.SecuredRDFList;
import org.xenei.jena.server.security.model.SecuredStatement;

public class SecuredRDFListImpl extends SecuredResourceImpl implements
		SecuredRDFList
{
	/** Error message if validity check fails */
	protected String m_errorMsg = null;

	/** Pointer to the node that is the tail of the list */
	protected RDFList m_tail = null;

	/** The URI for the 'first' property in this list */
	protected Property m_listFirst = RDF.first;

	/** The URI for the 'rest' property in this list */
	protected Property m_listRest = RDF.rest;

	/** The URI for the 'nil' Resource in this list */
	protected Resource m_listNil = RDF.nil;

	/** The URI for the rdf:type of this list */
	protected Resource m_listType = RDF.List;

	private final ItemHolder<RDFList, SecuredRDFList> holder;

	public SecuredRDFListImpl( final SecurityEvaluator securityEvaluator,
			final String graphIRI,
			final ItemHolder<RDFList, SecuredRDFList> holder )
	{
		super(securityEvaluator, graphIRI, holder);
		this.holder = holder;
	}

	@Override
	public void add( final RDFNode value )
	{
		checkCreate();
		checkCreateNewList(value, listNil());
		checkChangeTail(holder.getBaseItem(), Node.createAnon(), listRest());
		holder.getBaseItem().add(value);
	}

	@Override
	public RDFList append( final Iterator<? extends RDFNode> nodes )
	{
		checkRead();
		final SecuredRDFList copy = copy();
		while (nodes.hasNext())
		{
			copy.add(nodes.next());
		}
		return copy;
	}

	@Override
	public RDFList append( final RDFList list )
	{
		checkRead();
		if (holder.getBaseItem().isEmpty())
		{
			return list.copy();
		}
		else
		{
			if (canRead(Triple.ANY))
			{
				return org.xenei.jena.server.security.model.impl.Factory
						.getInstance(this, holder.getBaseItem().append(list));
			}
			else
			{
				final SecuredRDFList copy = copy();
				copy.concatenate(list);
				return copy;
			}
		}
	}

	@Override
	public void apply( final ApplyFn fn )
	{
		for (final Iterator<RDFNode> i = iterator(); i.hasNext();)
		{
			fn.apply(i.next());
		}
	}

	@Override
	public List<RDFNode> asJavaList()
	{
		final List<RDFNode> l = new ArrayList<RDFNode>();

		for (final Iterator<RDFNode> i = iterator(); i.hasNext();)
		{
			l.add(i.next());
		}

		return l;
	}

	@Override
	public boolean canRead()
	{
		if (canRead())
		{
			return canRead(new Triple(holder.getBaseItem().asNode(), Node.ANY,
					Node.ANY));
		}
		return false;
	}

	private Statement checkChangeTail( final Resource root, final Node tail,
			final Property pTail )
	{
		final Statement current = root.getRequiredProperty(pTail);
		checkUpdate(current.asTriple(),
				new Triple(root.asNode(), pTail.asNode(), tail));
		return current;
	}

	private void checkCreateNewList( final RDFNode value, final Resource tail )
	{
		checkCreate(new Triple(Node.ANY, listFirst().asNode(), value.asNode()));
		checkCreate(new Triple(Node.ANY, listRest().asNode(), tail.asNode()));
	}

	/**
	 * <p>
	 * Check that the current list cell is not the nil list, and throw an empty
	 * list exception if it is.
	 * </p>
	 * 
	 * @param msg
	 *            The context message for the empty list exception
	 * @exception EmptyListException
	 *                if the list is the nil list
	 */
	protected void checkNotNil( final String msg )
	{
		if (isEmpty())
		{
			throw new EmptyListException(msg);
		}
	}

	/**
	 * <p>
	 * Answer a set of all of the RDF statements whose subject is one of the
	 * cells of this list.
	 * </p>
	 * 
	 * @return A list of the statements that form the encoding of this list.
	 */
	private Set<SecuredStatement> collectStatements()
	{
		final Set<SecuredStatement> stmts = new HashSet<SecuredStatement>();
		SecuredRDFList l = this;

		do
		{
			// collect all statements of this list cell
			for (final Iterator<Statement> i = l.listProperties(); i.hasNext();)
			{
				stmts.add(org.xenei.jena.server.security.model.impl.Factory
						.getInstance(this, i.next()));
			}

			// move on to next cell
			l = (SecuredRDFList) l.getTail();
		} while (!l.isEmpty());

		return stmts;
	}

	@Override
	public void concatenate( final Iterator<? extends RDFNode> nodes )
	{
		checkUpdate();
		if (isEmpty())
		{
			// concatenating list onto the empty list is an error
			throw new EmptyListUpdateException(
					"Tried to concatenate onto the empty list");
		}
		else
		{
			final org.xenei.jena.server.security.SecurityEvaluator.Node s = new org.xenei.jena.server.security.SecurityEvaluator.Node(
					org.xenei.jena.server.security.SecurityEvaluator.Node.Type.Anonymous,
					null);

			final org.xenei.jena.server.security.SecurityEvaluator.Node p = SecuredItemImpl
					.convert(listFirst().asNode());
			org.xenei.jena.server.security.SecurityEvaluator.Triple t = new org.xenei.jena.server.security.SecurityEvaluator.Triple(
					s, p,
					org.xenei.jena.server.security.SecurityEvaluator.Node.ANY);
			if (!canCreate(t))
			{

				final List<RDFNode> list = new ArrayList<RDFNode>();
				while (nodes.hasNext())
				{
					final RDFNode n = nodes.next();
					t = new org.xenei.jena.server.security.SecurityEvaluator.Triple(
							s, p, SecuredItemImpl.convert(n.asNode()));
					checkCreate(t);
					list.add(n);
				}
				holder.getBaseItem().concatenate(list.iterator());

			}
			holder.getBaseItem().concatenate(nodes);
		}
		final Model m = ModelFactory.createDefaultModel();
		final RDFList l = m.createList();
		// FIXME does this change when node element checking is added
		l.concatenate(nodes);
		concatenate(l);
	}

	@Override
	public void concatenate( final RDFList list )
	{
		checkUpdate();
		if (isEmpty())
		{
			// concatenating list onto the empty list is an error
			throw new EmptyListUpdateException(
					"Tried to concatenate onto the empty list");
		}
		else
		{
			final org.xenei.jena.server.security.SecurityEvaluator.Node s = new org.xenei.jena.server.security.SecurityEvaluator.Node(
					org.xenei.jena.server.security.SecurityEvaluator.Node.Type.Anonymous,
					null);

			final org.xenei.jena.server.security.SecurityEvaluator.Node p = SecuredItemImpl
					.convert(listFirst().asNode());
			org.xenei.jena.server.security.SecurityEvaluator.Triple t = new org.xenei.jena.server.security.SecurityEvaluator.Triple(
					s, p,
					org.xenei.jena.server.security.SecurityEvaluator.Node.ANY);
			if (!canCreate(t))
			{

				final ExtendedIterator<RDFNode> iter = list.iterator();
				try
				{
					while (iter.hasNext())
					{
						t = new org.xenei.jena.server.security.SecurityEvaluator.Triple(
								s, p, SecuredItemImpl.convert(iter.next()
										.asNode()));
						checkCreate(t);
					}
				}
				finally
				{
					iter.close();
				}
			}
			holder.getBaseItem().concatenate(list);
		}
	}

	@Override
	public RDFList cons( final RDFNode value )
	{
		checkCreate();
		checkCreateNewList(value, this);
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().cons(value));
	}

	@Override
	public boolean contains( final RDFNode value )
	{
		return indexOf(value) >= 0;
	}

	@Override
	public SecuredRDFList copy()
	{
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().copy());
	}

	private RDFList findElement( final boolean last, final int index )
	{
		final Property tail = listRest();
		final Resource nil = listNil();

		Resource l = this;
		int i = index;
		boolean found = (last && l.hasProperty(tail, nil))
				|| (!last && (i == 0));

		// search for the element whose tail is nil, or whose index is now zero
		while (!found && !l.equals(nil))
		{
			l = l.getRequiredProperty(tail).getResource();
			found = (last && l.hasProperty(tail, nil)) || (!last && (--i == 0));
		}

		if (!found)
		{
			// premature end of list
			if (!last)
			{
				throw new ListIndexException("Tried to access element " + index
						+ " that is beyond the length of the list");
			}
			else
			{
				throw new InvalidListException(
						"Could not find last element of list (suggests list is not valid)");
			}
		}
		else
		{
			return l.as(listAbstractionClass());
		}
	}

	private RDFList findNode( final RDFNode val )
	{
		RDFList cell = holder.getBaseItem();
		final boolean searching = true;

		while (searching && !cell.isEmpty())
		{
			if (cell.getHead().equals(val))
			{
				// found the value to be removed
				return cell;
			}
			else
			{
				cell = cell.getTail();
			}
		}
		return null;
	}

	@Override
	public RDFNode get( final int i )
	{
		checkRead();
		final RDFNode retval = holder.getBaseItem().get(i);
		checkRead(retval.asResource().getProperty(listRest()).asTriple());
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, retval);
	}

	@Override
	public RDFNode getHead()
	{
		checkRead();
		final RDFNode n = holder.getBaseItem().getHead();
		checkRead(new Triple(holder.getBaseItem().asNode(), listFirst()
				.asNode(), n.asNode()));
		return n;
	}

	@Override
	public boolean getStrict()
	{
		return holder.getBaseItem().getStrict();
	}

	@Override
	public SecuredRDFList getTail()
	{
		checkRead();
		final RDFList tail = holder.getBaseItem().getTail();
		checkRead(new Triple(this.asNode(), listRest().asNode(), tail.asNode()));
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, tail);
	}

	@Override
	public String getValidityErrorMessage()
	{
		checkRead();
		return holder.getBaseItem().getValidityErrorMessage();
	}

	@Override
	public int indexOf( final RDFNode value )
	{
		return indexOf(value, 0);
	}

	@Override
	public int indexOf( final RDFNode value, final int start )
	{
		checkRead();
		final Property head = listFirst();
		if (!canRead(new Triple(Node.ANY, head.asNode(), value.asNode())))
		{
			// first get to where we start
			Resource l = findElement(false, start);
			int index = start;

			final Property tail = listRest();
			final Resource nil = listNil();

			boolean found = l.hasProperty(head, value);

			// search for the element whose value is, er, value
			while (!found && !l.equals(nil))
			{
				l = l.getRequiredProperty(tail).getResource();
				index++;
				found = l.hasProperty(head, value);
			}
			if (found)
			{
				checkRead(new Triple(l.asNode(), head.asNode(), value.asNode()));
			}
			return found ? index : -1;
		}
		else
		{
			return holder.getBaseItem().indexOf(value, start);
		}
	}

	@Override
	public boolean isEmpty()
	{
		checkRead();
		return holder.getBaseItem().isEmpty();
	}

	@Override
	public boolean isValid()
	{
		checkRead();
		return holder.getBaseItem().isValid();
	}

	@Override
	public ExtendedIterator<RDFNode> iterator()
	{
		return new SecuredNodeIterator(this, holder.getBaseItem().iterator());
	}

	public Class<? extends RDFList> listAbstractionClass()
	{
		return RDFList.class;
	}

	public Property listFirst()
	{
		return m_listFirst;
	}

	public Resource listNil()
	{
		return m_listNil;
	}

	public Property listRest()
	{
		return m_listRest;
	}

	public Resource listType()
	{
		return m_listType;
	}

	@Override
	public <T> ExtendedIterator<T> mapWith( final Map1<RDFNode, T> fn )
	{
		return iterator().mapWith(fn);
	}

	@Override
	public Object reduce( final ReduceFn fn, final Object initial )
	{
		Object acc = initial;

		for (final Iterator<RDFNode> i = iterator(); i.hasNext();)
		{
			acc = fn.reduce(i.next(), acc);
		}

		return acc;
	}

	@Override
	public RDFList remove( final RDFNode val )
	{
		checkUpdate();
		final RDFList cell = findNode(val);
		checkDelete(new Triple(cell.asNode(), Node.ANY, Node.ANY));
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().remove(val));
	}

	@Override
	@Deprecated
	public void removeAll()
	{
		removeList();
	}

	@Override
	public RDFList removeHead()
	{
		checkNotNil("Attempted to delete the head of a nil list");

		final RDFList tail = getTail();
		removeProperties();

		return tail;
	}

	@Override
	public void removeList()
	{
		for (final SecuredStatement securedStatement : collectStatements())
		{
			securedStatement.remove();
		}
	}

	@Override
	public RDFNode replace( final int i, final RDFNode value )
	{
		checkUpdate();
		final RDFNode retval = holder.getBaseItem().get(i);
		final Statement s = retval.asResource().getProperty(listRest());
		final Triple t = new Triple(s.getSubject().asNode(), listRest()
				.asNode(), value.asNode());
		checkUpdate(s.asTriple(), t);
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, holder.getBaseItem().replace(i, value));
	}

	@Override
	public boolean sameListAs( final RDFList list )
	{
		ExtendedIterator<RDFNode> thisIter = null;
		ExtendedIterator<RDFNode> thatIter = null;
		try
		{
			thisIter = iterator();
			thatIter = list.iterator();
			while (thisIter.hasNext() && thatIter.hasNext())
			{
				final RDFNode thisN = thisIter.next();
				final RDFNode thatN = thatIter.next();
				if ((thisN == null) || !thisN.equals(thatN))
				{
					// not equal at this position
					return false;
				}
			}
			return !(thisIter.hasNext() || thatIter.hasNext());
		}
		finally
		{
			if (thisIter != null)
			{
				thisIter.close();
			}
			if (thatIter != null)
			{
				thatIter.close();
			}
		}
	}

	@Override
	public RDFNode setHead( final RDFNode value )
	{
		checkUpdate();
		// first remove the existing head
		final Statement current = holder.getBaseItem().getRequiredProperty(
				listFirst());
		final Triple t = new Triple(this.asNode(), listFirst().asNode(),
				value.asNode());
		checkUpdate(current.asTriple(), t);
		final RDFNode retval = holder.getBaseItem().setHead(value);
		if (canRead(current.asTriple()))
		{
			if (retval.isLiteral())
			{
				return org.xenei.jena.server.security.model.impl.Factory
						.getInstance(this, retval.asLiteral());
			}
			else
			{
				return org.xenei.jena.server.security.model.impl.Factory
						.getInstance(this, retval.asResource());
			}
		}
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this,
				retval.getModel().createResource(
						Node.createAnon().getBlankNodeId()));
	}

	@Override
	public void setStrict( final boolean strict )
	{
		checkUpdate();
		holder.getBaseItem().setStrict(strict);
	}

	@Override
	public RDFList setTail( final RDFList tail )
	{
		checkUpdate();
		final Statement current = checkChangeTail(holder.getBaseItem(),
				tail.asNode(), listRest());
		final RDFList retval = holder.getBaseItem().setTail(tail);
		if (canRead(current.asTriple()))
		{
			return org.xenei.jena.server.security.model.impl.Factory
					.getInstance(this, retval);
		}
		return org.xenei.jena.server.security.model.impl.Factory.getInstance(
				this, retval.getModel().createList());
	}

	@Override
	public int size()
	{
		checkRead();
		return holder.getBaseItem().size();
	}

	@Override
	public RDFList with( final RDFNode value )
	{
		// if this is the empty list, we create a new node containing value -
		// i.e. cons
		if (holder.getBaseItem().isEmpty())
		{
			return cons(value);
		}
		else
		{
			checkCreate();
			checkCreateNewList(value, listNil());
			checkChangeTail(holder.getBaseItem(), Node.createAnon(), listRest());
			return org.xenei.jena.server.security.model.impl.Factory
					.getInstance(this, holder.getBaseItem().with(value));
		}
	}

}
