package pl.mwojciec.test.classes.executor;

import pl.mwojciec.generator.classes.RDFWithNodesGenerator;
import pl.mwojciec.generator.interfaces.ITriplesGenerator;
import pl.mwojciec.test.classes.jena.JenaInMemoryTest;
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
	}
	
	public void executeTest() {
		test = new JenaInMemoryTest();
		test.setNamespaceName("http://www.mwojciec.pl");
		test.setGenerator(generator);
		test.loadRepository();
		((JenaInMemoryTest)test).printRDFFile();
		test.executeQueries();
	}
	
	public String getReport() {
		return null;
	}
	
	public void generateReport() {
		
	}
	
	
}
