package org.xenei.jena.server.security.model;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.xenei.jena.server.security.Factory;
import org.xenei.jena.server.security.MockSecurityEvaluator;

@RunWith( value = Parameterized.class )
public class SecuredResourceTest
{
	@Parameters
	public static Collection<Object[]> data()
	{
		final List<Object[]> retval = new ArrayList<Object[]>();
		/*
		 * for (final boolean create : bSet)
		 * {
		 * for (final boolean read : bSet)
		 * {
		 * for (final boolean update : bSet)
		 * {
		 * for (final boolean delete : bSet)
		 * {
		 * retval.add(new Object[] { new MockSecurityEvaluator(
		 * true, create, read, update, delete) });
		 * }
		 * }
		 * }
		 * }
		 */
		retval.add(new Object[] { new MockSecurityEvaluator(true, false, true,
				false, false) });
		return retval;

	}

	private final MockSecurityEvaluator securityEvaluator;
	private Model model;
	private Model m;
	private Resource s;
	private Property p;
	private Resource o;

	public SecuredResourceTest( final MockSecurityEvaluator securityEvaluator )
	{
		this.securityEvaluator = securityEvaluator;
	}

	protected Model createModel()
	{
		return ModelFactory.createDefaultModel();
	}

	@Before
	public void setup()
	{
		m = createModel();
		model = Factory.getInstance(securityEvaluator,
				"http://example.com/securedGraph", m);
		s = ResourceFactory.createResource("http://example.com/graph/s");
		p = ResourceFactory.createProperty("http://example.com/graph/p");
		o = ResourceFactory.createResource("http://example.com/graph/o");
		m.add(s, p, o);
	}

	@Test
	public void testResource()
	{
		final Statement stmt = model.listStatements().toList().get(0);
		final Resource r = stmt.getResource();
		stmt.changeLiteralObject(1000);

		r.addLiteral(p, true);

		m.write(System.out, "TURTLE");
	}

}
