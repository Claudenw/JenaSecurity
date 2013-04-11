package org.xenei.jena.security.contract.model;

import com.hp.hpl.jena.rdf.model.test.TestLiterals;
import com.hp.hpl.jena.rdf.model.test.TestPackage;
import com.hp.hpl.jena.rdf.model.test.helpers.TestingModelFactory;

public class SecTestLiterals extends TestLiterals {

	public SecTestLiterals() {
		super(new TestPackage.PlainModelFactory(), "SecTestLiterals");
	}

}
