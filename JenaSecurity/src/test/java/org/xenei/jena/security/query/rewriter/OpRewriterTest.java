package org.xenei.jena.security.query.rewriter;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.algebra.op.OpBGP;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.vocabulary.RDF;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.xenei.jena.security.MockSecurityEvaluator;
import org.xenei.jena.security.SecurityEvaluator;
import org.xenei.jena.security.SecurityEvaluator.SecNode;

public class OpRewriterTest
{
	private OpRewriter rewriter;
	private SecurityEvaluator securityEvaluator = new MockSecurityEvaluator( true, true, true, true, true, true );
	
	public OpRewriterTest()
	{
	}
	
	@Before
	public void setUp()
	{
		rewriter = new OpRewriter( securityEvaluator, "http://example.com/dummy");
	}
	
	@Test
	public void testBGP()
	{
		Triple[] triples = {
				new Triple( Node.createVariable("foo"), RDF.type.asNode(), Node.createURI( "http://example.com/class")),
				new Triple( Node.createVariable("foo"), Node.createAnon(), Node.createVariable("bar")),
				new Triple( Node.createVariable("bar"), Node.createAnon(), Node.createVariable("baz")),
		};
		rewriter.visit( new OpBGP( BasicPattern.wrap(Arrays.asList(triples))));
		System.out.println( rewriter.getResult());
	}

}
