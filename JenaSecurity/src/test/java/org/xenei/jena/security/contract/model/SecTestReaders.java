package org.xenei.jena.security.contract.model;

import com.hp.hpl.jena.rdf.model.test.TestPackage;

public class SecTestReaders extends com.hp.hpl.jena.rdf.model.test.TestReaders {

	public SecTestReaders() {
		super( new TestPackage.PlainModelFactory(), "SecTestReaders" );
	}

}
