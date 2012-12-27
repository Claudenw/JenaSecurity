package org.xenei.jena.security;

import com.hp.hpl.jena.assembler.Assembler;
import com.hp.hpl.jena.assembler.assemblers.AssemblerGroup;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.tdb.assembler.DatasetAssemblerTDB;
import com.hp.hpl.jena.tdb.assembler.NodeTableAssembler;
import com.hp.hpl.jena.tdb.assembler.TDBGraphAssembler;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;


import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.xenei.jena.security.model.SecuredModel;

public class SecuredAssemblerTest
{
	private Assembler assembler;
	private Model model;
	
	public SecuredAssemblerTest() 
	{
		assembler = Assembler.general;
	}
	
	@Before
	public void setUp() throws Exception {
		model = ModelFactory.createDefaultModel();
		URL url = SecuredAssemblerTest.class.getClassLoader().getResource( SecuredAssemblerTest.class.getName().replace(".", "/")+".ttl");
		model.read( url.toURI().toString(), "TURTLE" );
		model.write( System.out, "TURTLE" );
	}
	
	@Test
	public void testCreation() throws Exception {
		
		Resource r = model.createResource( "http://xenei.org/jena/security/test#secModel");
		Object o = assembler.open( r );
		Assert.assertTrue( o instanceof Model);
		Assert.assertTrue( o instanceof SecuredModel );
	}

}
