package org.xenei.jena.security.model;

import com.hp.hpl.jena.rdf.model.Bag;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.xenei.jena.security.MockSecurityEvaluator;
import org.xenei.jena.security.SecurityEvaluatorParameters;
import org.xenei.jena.security.model.impl.SecuredBagImpl;

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
		final Bag bag = baseModel.getBag("http://example.com/testContainer");
		bag.add("SomeDummyItem");
		setSecuredRDFNode(SecuredBagImpl.getInstance(securedModel, bag), bag);
	}

}
