package org.xenei.jena.security.contract.model;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.test.AbstractTestPackage;
import com.hp.hpl.jena.rdf.model.test.helpers.TestingModelFactory;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.shared.ReificationStyle;

import java.lang.reflect.InvocationTargetException;

import junit.framework.TestSuite;

import org.xenei.jena.security.MockSecurityEvaluator;
import org.xenei.jena.security.SecurityEvaluator;

/**
 * Test package to test Model implementation.
 */
public class TestPackage extends AbstractTestPackage
{
	
	static public TestSuite suite() throws SecurityException, IllegalArgumentException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		return new TestPackage();
    }

	public TestPackage() throws SecurityException, IllegalArgumentException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException
	{
		super("SecuredModel", new PlainModelFactory() );
	}

	private static class PlainModelFactory implements TestingModelFactory
	{
		private final SecurityEvaluator eval;
		
		public PlainModelFactory()
		{
			eval = new MockSecurityEvaluator(true, true, true, true, true, true);
		}
		
		@Override
		public Model createModel()
		{
			// Graph graph = Factory.createDefaultGraph( style );
			final Model model = ModelFactory.createDefaultModel();
			return org.xenei.jena.security.Factory.getInstance(eval, "testModel",
					model);
		}

		@Override
		public Model createModel( final ReificationStyle style )
		{
			final Model model = ModelFactory.createDefaultModel(style);
			return org.xenei.jena.security.Factory.getInstance(eval, "testModel",
					model);
		}
		
		@Override
		public PrefixMapping getPrefixMapping()
		{
			return createModel().getGraph().getPrefixMapping();
		}
		
		@Override
		public Model createModel( Graph base )
		{
			return ModelFactory.createModelForGraph(base);
		}
	}
}
