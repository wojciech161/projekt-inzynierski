package pl.mwojciec.test.classes.executor;

import java.io.File;

import pl.mwojciec.generator.classes.RDFWithNodesGenerator;
import pl.mwojciec.generator.interfaces.ITriplesGenerator;
import pl.mwojciec.test.classes.jena.JenaInMemoryTest;
import pl.mwojciec.test.classes.jena.JenaWithMySQLTest;
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
		
		//Jena with MySQL test
		System.out.println("Test Jena MySQL");
		test = new JenaWithMySQLTest();
		test.loadRepository();
		test.setQueriesFile(new File("SparqlQueries.txt"));
		test.executeQueries();
		
	}
	
	public String getReport() {
		return "Koniec";
	}
	
	public void generateReport() {
		
	}
	
	
}
