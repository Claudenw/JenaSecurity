package org.xenei.jena.security.contract.model;

import com.hp.hpl.jena.rdf.model.test.TestPackage;

public class SecTestReaderEvents extends com.hp.hpl.jena.rdf.model.test.TestReaderEvents {

	public SecTestReaderEvents() {
		super( new TestPackage.PlainModelFactory(), "SecTestReaderEvents" );
	}

}
