package org.xenei.jena.server.security.model;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.shared.PrefixMapping;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.xenei.jena.server.security.AccessDeniedException;
import org.xenei.jena.server.security.EqualityTester;
import org.xenei.jena.server.security.Factory;
import org.xenei.jena.server.security.MockPrefixMapping;
import org.xenei.jena.server.security.MockSecurityEvaluator;
import org.xenei.jena.server.security.SecurityEvaluator;
import org.xenei.jena.server.security.SecurityEvaluatorParameters;
import org.xenei.jena.server.security.SecurityEvaluator.Action;
import org.xenei.jena.server.security.graph.SecuredGraph;

@RunWith( value = SecurityEvaluatorParameters.class )
public class SecuredModelTest
{
	protected final MockSecurityEvaluator securityEvaluator;
	protected SecuredModel model;
	protected Model m;
	protected Resource s;
	protected Property p;
	protected Resource o;

	public SecuredModelTest( final MockSecurityEvaluator securityEvaluator )
	{
		this.securityEvaluator = securityEvaluator;
	}

	protected Model createModel()
	{
		return ModelFactory.createDefaultModel();
	}

	@Test
	public void testGetAnyReifiedStmt()
	{
		// first with create.
		Set<Action> UCR = SecurityEvaluator.Util.asSet( new Action[] { Action.Update, Action.Create, Action.Read });
		try
		{
			model.getAnyReifiedStatement(m.listStatements().next());
			if (!securityEvaluator.evaluate(UCR))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(UCR))
			{
				e.printStackTrace();
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}

		Statement st = m.listStatements().next();
		m.createReifiedStatement(st);
		// now it is there so try with read
		try
		{
			model.getAnyReifiedStatement(st);
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Before
	public void setup()
	{
		m = createModel();
		m.removeAll();
		model = Factory.getInstance(securityEvaluator,
				"http://example.com/securedGraph", m);
		s = ResourceFactory.createResource("http://example.com/graph/s");
		p = ResourceFactory.createProperty("http://example.com/graph/p");
		o = ResourceFactory.createResource("http://example.com/graph/o");
		m.add(s, p, o);
	}

	@Test
	public void testAdd() throws Exception
	{
		final List<Statement> stmt = m.listStatements().toList();
		final Set<Action> createAndUpdate = SecurityEvaluator.Util
				.asSet(new Action[] { Action.Update, Action.Create });
		try
		{
			model.add(stmt);
			if (!securityEvaluator.evaluate(createAndUpdate))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(createAndUpdate))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		try
		{
			model.add(m);
			if (!securityEvaluator.evaluate(createAndUpdate))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(createAndUpdate))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		try
		{
			model.add(stmt.get(0));
			if (!securityEvaluator.evaluate(createAndUpdate))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(createAndUpdate))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		try
		{

			model.add(stmt.toArray(new Statement[stmt.size()]));
			if (!securityEvaluator.evaluate(createAndUpdate))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(createAndUpdate))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		try
		{
			model.add(m.listStatements());
			if (!securityEvaluator.evaluate(createAndUpdate))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(createAndUpdate))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		try
		{
			model.add(m, false);
			if (!securityEvaluator.evaluate(createAndUpdate))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(createAndUpdate))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		try
		{
			model.add(s, p, o);
			if (!securityEvaluator.evaluate(createAndUpdate))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(createAndUpdate))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		try
		{
			model.add(s, p, "foo");
			if (!securityEvaluator.evaluate(createAndUpdate))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(createAndUpdate))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		try
		{
			model.add(s, p, "foo", false);
			if (!securityEvaluator.evaluate(createAndUpdate))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(createAndUpdate))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		try
		{
			model.add(s, p, "foo", XSDDatatype.XSDstring);
			if (!securityEvaluator.evaluate(createAndUpdate))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(createAndUpdate))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		try
		{
			model.add(s, p, "foo", "en");
			if (!securityEvaluator.evaluate(createAndUpdate))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(createAndUpdate))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}

	}

	@Test
	public void testAsRDFNode() throws Exception
	{
		model.asRDFNode(Node.createURI("http://example.com/rdfNode"));
	}

	@Test
	public void testAsStatement()
	{
		final Triple t = new Triple(s.asNode(), p.asNode(), o.asNode());
		try
		{
			model.asStatement(t);
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testContains() throws Exception
	{
		final Statement stmt = m.listStatements().next();
		try
		{
			model.contains(stmt);
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		try
		{
			model.contains(s, p);
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		try
		{
			model.contains(s, p, o);
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		try
		{
			model.contains(s, p, "foo");
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		try
		{
			model.contains(s, p, "foo", "en");
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}

	}

	@Test
	public void testContainsAll() throws Exception
	{
		try
		{
			model.containsAll(m);
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		try
		{
			model.containsAll(m.listStatements());
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testCreateAlt() throws Exception
	{
		Set<Action> CU = SecurityEvaluator.Util.asSet( new Action[] { Action.Create, Action.Update} );
		try
		{
			model.createAlt();
			if (!securityEvaluator.evaluate(CU))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(CU))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		try
		{
			model.createAlt("foo");
			if (!securityEvaluator.evaluate(CU))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(CU))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testCreateBag() throws Exception
	{
		Set<Action> CU = SecurityEvaluator.Util.asSet( new Action[] { Action.Create, Action.Update} );
		try
		{
			model.createBag();
			if (!securityEvaluator.evaluate(CU))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(CU))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		try
		{
			model.createBag("foo");
			if (!securityEvaluator.evaluate(CU))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(CU))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testCreateList() throws Exception
	{
		Set<Action> CU = SecurityEvaluator.Util.asSet( new Action[] { Action.Create, Action.Update} );
		
		final List<RDFNode> nodeList = new ArrayList<RDFNode>();
		try
		{
			model.createList();
			if (!securityEvaluator.evaluate(CU))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(CU))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		m.removeAll();
		
		try
		{
			model.createList(nodeList.iterator());
			if (!securityEvaluator.evaluate(CU))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(CU))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		m.removeAll();

		try
		{
			final RDFNode[] list = new RDFNode[] {
					ResourceFactory.createResource(),
					ResourceFactory.createResource(),
					ResourceFactory.createResource(),
					ResourceFactory.createResource(), };

			model.createList(list);
			if (!securityEvaluator.evaluate(CU))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(CU))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		m.removeAll();

	}

	@Test
	public void testCreateLiteral() throws Exception
	{
		model.createLiteral("foo");
		model.createLiteral("foo", false);
		Set<Action> CU = SecurityEvaluator.Util.asSet( new Action[] { Action.Create, Action.Update} );
		
		try
		{
			model.createLiteralStatement(s, p, true);
			if (!securityEvaluator.evaluate(CU))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(CU))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		try
		{
			model.createLiteralStatement(s, p, 'a');
			if (!securityEvaluator.evaluate(CU))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(CU))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		try
		{
			model.createLiteralStatement(s, p, 1.0d);
			if (!securityEvaluator.evaluate(CU))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(CU))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		try
		{
			model.createLiteralStatement(s, p, 1.0f);
			if (!securityEvaluator.evaluate(CU))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(CU))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		try
		{
			model.createLiteralStatement(s, p, 1);
			if (!securityEvaluator.evaluate(CU))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(CU))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		try
		{
			model.createLiteralStatement(s, p, 1L);
			if (!securityEvaluator.evaluate(CU))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(CU))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		try
		{
			model.createLiteralStatement(s, p, new MockPrefixMapping());
			if (!securityEvaluator.evaluate(CU))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(CU))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testDifference() throws Exception
	{
		try
		{
			model.difference(m);
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testEquals() throws Exception
	{
		model.equals(m);
		m.equals(model);
	}

	@Test
	public void testExpandPrefix() throws Exception
	{
		try
		{
			model.expandPrefix("foo");
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testGetAlt() throws Exception
	{
		final Resource a = m.createAlt("http://example.com/model/alt");
		try
		{

			model.getAlt(a);
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}

		try
		{
			model.getAlt("http://example.com/model/alt");
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testGetBag()
	{
		final Resource b = m.createBag("http://example.com/model/bag");
		try
		{
			model.getBag(b);
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}

		try
		{
			model.getBag("http://example.com/model/bag");
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testGetGraph() throws Exception
	{
		final Graph g = model.getGraph();
		Assert.assertTrue(g instanceof SecuredGraph);
		EqualityTester.testInequality("getGraph test", g, m.getGraph());
	}

	@Test
	public void testGetLock()
	{
		model.getLock();
	}

	@Test
	public void testGetNsPrefixMap()
	{
		try
		{
			model.getNsPrefixMap();
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testGetNsPrefixURI()
	{
		try
		{
			model.getNsPrefixURI("foo");
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testGetNsURIPrefix() throws Exception
	{
		try
		{
			model.getNsURIPrefix("foo");
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testGetProperty()
	{

		try
		{
			model.getProperty("foo");
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		try
		{
			model.getProperty(s, p);
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		try
		{
			model.getProperty("fooNS", "foo");
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testGetQNameFor() throws Exception
	{
		try
		{
			model.qnameFor("foo");
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testgetRDFNode()
	{

		try
		{
			model.getRDFNode(Node.createURI("foo"));
			if (!securityEvaluator.evaluate(Action.Update))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Update))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testGetReader()
	{
		model.getReader();
		model.getReader("TURTLE");
	}

	@Test
	public void testGetReificationStyle()
	{
		model.getReificationStyle();
	}

	@Test
	public void testGetResource()
	{
		model.getResource("foo");
	}

	@Test
	public void testGetSeq()
	{
		final Resource s = m.createSeq("http://example.com/model/seq");
		try
		{
			model.getSeq(s);
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		try
		{
			model.getSeq("http://example.com/model/seq");
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testGetWriter()
	{
		model.getWriter();
		model.getWriter("TURTLE");
	}

	@Test
	public void testIndependent() throws Exception
	{
		Assert.assertFalse(model.independent());
	}

	@Test
	public void testIntersection() throws Exception
	{
		try
		{
			model.intersection(m);
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testIsClosed() throws Exception
	{
		model.isClosed();
	}

	@Test
	public void testIsEmpty() throws Exception
	{
		try
		{
			model.isEmpty();
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testIsIsomorphicWith()
	{
		try
		{
			model.isIsomorphicWith(m);
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}

		try
		{
			m.isIsomorphicWith(model);
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testIsReified()
	{
		try
		{
			model.isReified(m.listStatements().next());
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}

	}

	@Test
	public void testListLiteralStatements() throws Exception
	{
		try
		{
			model.listLiteralStatements(s, p, true);
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		try
		{
			model.listLiteralStatements(s, p, '0');
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		try
		{
			model.listLiteralStatements(s, p, 2.0d);
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		try
		{
			model.listLiteralStatements(s, p, 2.0f);
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		try
		{
			model.listLiteralStatements(s, p, 1);
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testLock() throws Exception
	{
		try
		{
			model.lock();
			if (!securityEvaluator.evaluate(Action.Update))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Update))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testQuery() throws Exception
	{
		final Selector s = new SimpleSelector();
		try
		{
			model.query(s);
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testQueryHandler()
	{
		try
		{
			model.queryHandler();
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testReadEmpty() throws Exception
	{
		final Set<Action> createAndUpdate = SecurityEvaluator.Util
				.asSet(new Action[] { Action.Update, Action.Create });

		final String XML_INPUT = "<rdf:RDF"
				+ "   xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#' "
				+ "   xmlns:rt='http://example.com/readTest#' "
				+ "   xmlns:j.0='http://example.com/readTest#3' > "
				+ "  <rdf:Description rdf:about='http://example.com/readTest#1'> "
				+ "    <rdf:type rdf:resource='http://example.com/readTest#3'/>"
				+ "  </rdf:Description>" + "</rdf:RDF>";
		final String TTL_INPUT = "@prefix rt: <http://example.com/readTest#> . rt:1 a rt:3 .";
		final String base = "http://example.com/test";
		final String lang = "TURTLE";
		try
		{
			final URL url = SecuredModelTest.class.getResource("./test.xml");
			model.read(url.toString());
			if (!securityEvaluator.evaluate(createAndUpdate))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(createAndUpdate))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		finally
		{
			m.removeAll();
		}

		try
		{
			final InputStream in = new ByteArrayInputStream(
					XML_INPUT.getBytes());
			model.read(in, base);
			if (!securityEvaluator.evaluate(createAndUpdate))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(createAndUpdate))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		finally
		{
			m.removeAll();
		}

		try
		{
			final Reader reader = new StringReader(XML_INPUT);
			model.read(reader, base);
			if (!securityEvaluator.evaluate(createAndUpdate))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(createAndUpdate))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		finally
		{
			m.removeAll();
		}

		try
		{
			final URL url = SecuredModelTest.class.getResource("./test.ttl");
			model.read(url.toString(), lang);
			if (!securityEvaluator.evaluate(createAndUpdate))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(createAndUpdate))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		finally
		{
			m.removeAll();
		}

		try
		{
			final InputStream in = new ByteArrayInputStream(
					TTL_INPUT.getBytes());
			model.read(in, base, lang);
			if (!securityEvaluator.evaluate(createAndUpdate))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(createAndUpdate))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		finally
		{
			m.removeAll();
		}

		try
		{
			final Reader reader = new StringReader(TTL_INPUT);
			model.read(reader, base, lang);
			if (!securityEvaluator.evaluate(createAndUpdate))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(createAndUpdate))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		finally
		{
			m.removeAll();
		}

		try
		{
			final URL url = SecuredModelTest.class.getResource("./test.ttl");
			model.read(url.toString(), base, lang);
			if (!securityEvaluator.evaluate(createAndUpdate))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(createAndUpdate))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		finally
		{
			m.removeAll();
		}

	}

	@Test
	public void testRemove() throws Exception
	{
		Set<Action> DU = SecurityEvaluator.Util.asSet( new Action[] { Action.Delete, Action.Update} );
		
		final List<Statement> stmt = m.listStatements().toList();
		try
		{
			model.remove(m.listStatements().toList());
			if (!securityEvaluator.evaluate(DU))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(DU))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}

		try
		{
			model.remove(m);
			if (!securityEvaluator.evaluate(DU))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(DU))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		try
		{
			model.remove(stmt.get(0));
			if (!securityEvaluator.evaluate(DU))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(DU))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}

		try
		{
			model.remove(stmt.toArray(new Statement[stmt.size()]));
			if (!securityEvaluator.evaluate(DU))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(DU))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}

		try
		{
			model.remove(m.listStatements());
			if (!securityEvaluator.evaluate(DU))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(DU))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}

		try
		{
			model.remove(m, true);
			if (!securityEvaluator.evaluate(DU))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(DU))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}

		try
		{
			model.remove(s, p, o);
			if (!securityEvaluator.evaluate(DU))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(DU))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}

	}

	@Test
	public void testRemoveAll() throws Exception
	{
		Set<Action> DU = SecurityEvaluator.Util.asSet( new Action[] { Action.Delete, Action.Update} );

		try
		{
			model.removeAll();
			if (!securityEvaluator.evaluate(DU))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(DU))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		try
		{
			model.removeAll(s, p, o);
			if (!securityEvaluator.evaluate(DU))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(DU))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testRemoveAllReifications()
	{
		Set<Action> DU = SecurityEvaluator.Util.asSet( new Action[] { Action.Delete, Action.Update} );

		final List<Statement> stmt = m.listStatements().toList();

		try
		{
			model.removeAllReifications(stmt.get(0));
			if (!securityEvaluator.evaluate(DU))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(DU))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testRequiredProperty()
	{

		try
		{
			model.getRequiredProperty(s, p);
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testSamePrefix() throws Exception
	{
		try
		{
			final PrefixMapping pm = new MockPrefixMapping();
			model.samePrefixMappingAs(pm);
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	public void testSetNs() throws Exception
	{
		try
		{
			model.setNsPrefix("foo", "http://example.com/foo");
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		try
		{
			final Map<String, String> nsMap = new HashMap<String, String>();
			nsMap.put("foo", "http://example.com/foo");
			model.setNsPrefixes(nsMap);
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		try
		{
			model.setNsPrefixes(new MockPrefixMapping());
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testShortForm() throws Exception
	{
		try
		{
			model.shortForm("foo");
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testSize() throws Exception
	{
		try
		{
			model.size();
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testUnion() throws Exception
	{
		try
		{
			model.union(m);
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}

		try
		{
			m.union(model);
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
	}

	@Test
	public void testWithDefaultMappings() throws Exception
	{
		try
		{
			model.withDefaultMappings(new MockPrefixMapping());
			if (!securityEvaluator.evaluate(Action.Update))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Update))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}

	}

	@Test
	public void testWrapAsResource() throws Exception
	{
		model.wrapAsResource(Node.createURI("http://example.com/rdfNode"));
	}

	@Test
	public void testWrite() throws Exception
	{
		final OutputStream out = new ByteArrayOutputStream();
		final Writer writer = new CharArrayWriter();
		final String lang = "TURTLE";
		try
		{
			model.write(out);
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		try
		{
			model.write(writer);
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		try
		{
			model.write(out, lang);
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		try
		{
			model.write(writer, lang);
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		try
		{
			model.write(out, lang, "http://example.com/securedGraph");
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}
		try
		{
			model.write(writer, lang, "http://example.com/securedGraph");
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String.format("Should not have thrown AccessDenied Exception: %s - %s", e, e.getTriple()));
			}
		}

	}
}