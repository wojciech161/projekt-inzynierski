package pl.mwojciec.test.classes.executor;

import java.io.File;

import pl.mwojciec.generator.classes.RDFWithNodesGenerator;
import pl.mwojciec.generator.interfaces.ITriplesGenerator;
import pl.mwojciec.test.classes.allegrograph.AllegroGraphNativeTest;
import pl.mwojciec.test.classes.bigdata.BigDataNativeWithSesameTest;
import pl.mwojciec.test.classes.jena.JenaInMemoryTest;
import pl.mwojciec.test.classes.owlim.OWLIMNativeWithSesameTest;
import pl.mwojciec.test.classes.sesame.SesameWithMySQLTest;
import pl.mwojciec.test.classes.virtuoso.VirtuosoNativeWithSesame;
import pl.mwojciec.test.interfaces.ITest;

public class TestExecutor {
	
	private ITriplesGenerator generator;
	private ITest test;
	
	public TestExecutor() {
		
	}
	
	public void generateTriples() {
		generator = new RDFWithNodesGenerator(1000, 5, 10);
		generator.setNamespaceName("mw");
		generator.setNamespaceURI("http://www.mwojciec.pl");
		generator.generate();
		generator.generateQueriesFile();
	}
	
	public void executeTest() {
		
		//Jena InMemory Test
		
		System.out.println("Test Jena InMemory");
		test = new JenaInMemoryTest();
		test.loadRepository();
		test.setQueriesFile(new File("SparqlQueries.txt"));
		test.executeQueries();
		
		//AlegroGraph test
		test = new AllegroGraphNativeTest();
		test.loadRepository();
		test.setQueriesFile(new File("SparqlQueries.txt"));
		test.executeQueries();
		
		//Virtuoso with Jena
		test = new VirtuosoNativeWithSesame();
		test.loadRepository();
		test.setQueriesFile(new File("SparqlQueries.txt"));
		test.executeQueries();
		
		//BigData
		test = new BigDataNativeWithSesameTest();
		test.loadRepository();
		test.setQueriesFile(new File("SparqlQueries.txt"));
		test.executeQueries();
		
		//owlim
		test = new OWLIMNativeWithSesameTest();
		test.loadRepository();
		test.setQueriesFile(new File("SerqlQueries.txt"));
		test.executeQueries();
		
		//Virtuoso sesame
		test = new VirtuosoNativeWithSesame();
		test.loadRepository();
		test.setQueriesFile(new File("SparqlQueries.txt"));
		test.executeQueries();
		
		// Sesame Mysql
		test = new SesameWithMySQLTest();
		test.loadRepository();
		test.setQueriesFile(new File("SerqlQueries.txt"));
		test.executeQueries();
	}
	
	public String getReport() {
		return "Koniec";
	}
	
	public void generateReport() {
		
	}
	
	
}
