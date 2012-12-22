package org.xenei.jena.security.model;

import com.hp.hpl.jena.rdf.model.Container;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;

import java.util.Set;

import org.xenei.jena.security.AccessDeniedException;
import org.xenei.jena.security.SecurityEvaluator.Action;
import org.xenei.jena.security.model.impl.SecuredNodeIterator;

/**
 * The interface for secured Container instances.
 * 
 * Use one of the SecuredContainer derived class Factory methods to create
 * instances
 */
public interface SecuredContainer extends Container, SecuredResource
{

	/**
	 * @graphSec Update
	 * @tripleSec Create SecTriple( this, RDF.li, o );
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredContainer add( final boolean o ) throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Create SecTriple( this, RDF.li, o );
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredContainer add( final char o ) throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Create SecTriple( this, RDF.li, o );
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredContainer add( final double o ) throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Create SecTriple( this, RDF.li, o );
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredContainer add( final float o ) throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Create SecTriple( this, RDF.li, o );
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredContainer add( final long o ) throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Create SecTriple( this, RDF.li, o );
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredContainer add( final Object o ) throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Create SecTriple( this, RDF.li, o );
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredContainer add( final RDFNode o ) throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Create SecTriple( this, RDF.li, o );
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredContainer add( final String o ) throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Create SecTriple( this, RDF.li, o );
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredContainer add( final String o, final String l )
			throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read SecTriple( this, RDF.li, o );
	 * @throws AccessDeniedException
	 */
	@Override
	public boolean contains( final boolean o ) throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read SecTriple( this, RDF.li, o );
	 * @throws AccessDeniedException
	 */
	@Override
	public boolean contains( final char o ) throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read SecTriple( this, RDF.li, o );
	 * @throws AccessDeniedException
	 */
	@Override
	public boolean contains( final double o ) throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read SecTriple( this, RDF.li, o );
	 * @throws AccessDeniedException
	 */
	@Override
	public boolean contains( final float o ) throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read SecTriple( this, RDF.li, o );
	 * @throws AccessDeniedException
	 */
	@Override
	public boolean contains( final long o ) throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read SecTriple( this, RDF.li, o );
	 * @throws AccessDeniedException
	 */
	@Override
	public boolean contains( final Object o ) throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read SecTriple( this, RDF.li, o );
	 * @throws AccessDeniedException
	 */
	@Override
	public boolean contains( final RDFNode o ) throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read SecTriple( this, RDF.li, o );
	 * @throws AccessDeniedException
	 */
	@Override
	public boolean contains( final String o ) throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read SecTriple( this, RDF.li, o );
	 * @throws AccessDeniedException
	 */
	@Override
	public boolean contains( final String o, final String l )
			throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read on each triple ( this, rdf:li_? node ) returned by
	 *            iterator;
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredNodeIterator<RDFNode> iterator() throws AccessDeniedException;

	/**
	 * @perms the Permissions required on each node returned
	 * @graphSec Read
	 * @tripleSec Read + perms on each triple ( this, rdf:li_? node ) returned
	 *            by iterator;
	 * @throws AccessDeniedException
	 */
	public SecuredNodeIterator<RDFNode> iterator( Set<Action> perms )
			throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Delete s as triple;
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredContainer remove( final Statement s )
			throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @throws AccessDeniedException
	 */
	@Override
	public int size() throws AccessDeniedException;
}
