package org.xenei.jena.security.model;

import com.hp.hpl.jena.rdf.model.Bag;
import com.hp.hpl.jena.rdf.model.Container;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;

import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xenei.jena.security.AccessDeniedException;
import org.xenei.jena.security.MockSecurityEvaluator;
import org.xenei.jena.security.SecurityEvaluator;
import org.xenei.jena.security.SecurityEvaluatorParameters;
import org.xenei.jena.security.SecurityEvaluator.Action;
import org.xenei.jena.security.model.impl.SecuredBagImpl;
import org.xenei.jena.security.model.impl.SecuredContainerImpl;

@RunWith( value = SecurityEvaluatorParameters.class )
public class SecuredBagTest extends SecuredContainerTest 
{

	public SecuredBagTest( final MockSecurityEvaluator securityEvaluator )
	{
		super(securityEvaluator);
	}


	@Override
	@Before
	public void setup()
	{
		super.setup();
		final Bag bag = baseModel
				.getBag("http://example.com/testContainer");
		bag.add( "SomeDummyItem");
		setSecuredRDFNode(
				SecuredBagImpl.getInstance(securedModel, bag),
				bag);
	}

}
