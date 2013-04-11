package org.xenei.jena.security.example;

import java.security.Principal;
import java.util.Set;

import org.apache.http.auth.BasicUserPrincipal;
import org.xenei.jena.security.SecurityEvaluator;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * An example evaluator that only provides access ot messages in the graph that 
 * are from or to the principal.
 *
 */
public class ExampleEvaluator implements SecurityEvaluator {
	
	private Principal principal;
	private Model model;
	private RDFNode msgType = ResourceFactory.createResource( "http://example.com/msg" );
	private Property pTo = ResourceFactory.createProperty( "http://example.com/to" );
	private Property pFrom = ResourceFactory.createProperty( "http://example.com/from" );
	
	/**
	 * 
	 * @param model The graph we are going to evaluate against.
	 */
	public ExampleEvaluator( Model model )
	{
		this.model = model;
	}
	
	@Override
	public boolean evaluate(Action action, SecNode graphIRI) {
		// we allow any action on a graph.
		return true;
	}

	private boolean evaluate( Resource r )
	{
		// a message is only available to sender or recipient
		if (r.hasProperty( RDF.type, msgType ))
		{
			return r.hasProperty( pTo, principal.getName() ) ||
					r.hasProperty( pFrom, principal.getName());
		}
		return true;	
	}
	
	private boolean evaluate( SecNode node )
	{
		if (node.equals( SecNode.ANY )) {
			return false;  // all wild cards are false
		}
		
		if (node.getType().equals( SecNode.Type.URI)) {
			Resource r = model.createResource( node.getValue() );
			return evaluate( r );
		}
		else if (node.getType().equals( SecNode.Type.Anonymous)) {
			Resource r = model.getRDFNode( NodeFactory.createAnon( new AnonId( node.getValue()) ) ).asResource();
			return evaluate( r );
		}
		else
		{
			return true;
		}

	}
	
	private boolean evaluate( SecTriple triple ) {
		return evaluate( triple.getSubject()) &&
				evaluate( triple.getObject()) &&
				evaluate( triple.getPredicate());
	}
	
	@Override
	public boolean evaluate(Action action, SecNode graphIRI, SecTriple triple) {
		return evaluate( triple );
	}

	@Override
	public boolean evaluate(Set<Action> actions, SecNode graphIRI) {
		return true;
	}

	@Override
	public boolean evaluate(Set<Action> actions, SecNode graphIRI,
			SecTriple triple) {
		return evaluate( triple );
	}

	@Override
	public boolean evaluateAny(Set<Action> actions, SecNode graphIRI) {
		return true;
	}

	@Override
	public boolean evaluateAny(Set<Action> actions, SecNode graphIRI,
			SecTriple triple) {
		return evaluate( triple );
	}

	@Override
	public boolean evaluateUpdate(SecNode graphIRI, SecTriple from, SecTriple to) {
		return evaluate( from ) && evaluate( to );
	}

	public void setPrincipal( String userName )
	{
		if (userName == null)
		{
			principal = null;
		}
		principal = new BasicUserPrincipal( userName );
	}
	@Override
	public Principal getPrincipal() {
		return principal;
	}

}
