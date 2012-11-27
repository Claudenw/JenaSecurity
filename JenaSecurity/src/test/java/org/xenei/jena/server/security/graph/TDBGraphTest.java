package org.xenei.jena.server.security.graph;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.tdb.TDB;
import com.hp.hpl.jena.tdb.TDBFactory;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.xenei.jena.server.security.MockSecurityEvaluator;

public class TDBGraphTest extends MemGraphTest
{

	private DatasetGraph dsGraph;

	private File f;

	public TDBGraphTest( final MockSecurityEvaluator securityEvaluator )
	{
		super(securityEvaluator);
	}

	@Override
	protected Graph createGraph() throws IOException
	{
		TDB.init();
		f = new File(System.getProperty("java.io.tmpdir") + "/TDBTest");
		dsGraph = TDBFactory.createDatasetGraph(f.getCanonicalPath());
		return dsGraph.getDefaultGraph();
	}

	@After
	public void tearDown()
	{
		TDB.sync(dsGraph);
		dsGraph.close();
		f.delete();
		TDB.closedown();
	}

}
