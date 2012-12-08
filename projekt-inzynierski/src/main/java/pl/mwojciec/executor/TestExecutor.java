package pl.mwojciec.executor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import pl.mwojciec.generator.interfaces.ITriplesGenerator;
import pl.mwojciec.test.interfaces.ITest;

public class TestExecutor {
	
	//Wstrzykiwanie zaleznosci
	private ApplicationContext factory;
	
	//Wlasciwe obiekty przechowujace testy oraz generator
	private ITriplesGenerator generator;
	private List<ITest> tests;
	
	// Zmienne przechowujace nazwy generatora oraz testow
	List<String> testNames;
	String generatorName;
	
	// Zmienna przechowujaca raport z testu
	private String report = "";
	
	public TestExecutor() {
		
		loadGeneratorsFile();
		loadTestsFile();
		
		factory = new FileSystemXmlApplicationContext("configuration/classes.xml");
		
		if(factory.containsBean(generatorName))
			generator = (ITriplesGenerator)factory.getBean(generatorName);
		
		tests = new ArrayList<ITest>();
		
		for(String testName : testNames) {
			if(factory.containsBean(testName)) {
				ITest newTest = (ITest)factory.getBean(testName);
				tests.add(newTest);
			}
		}
	}
	
	public void generateTriples() {
		if (generator != null) {
			generator.setNamespaceName("test");
			generator.setNamespaceURI("http://wogis.org/data/ont");
			generator.generate();
			generator.generateQueriesFile();
			report += "GENERATOR:\n" + generator.getReport() + "\n";
		}
	}
	
	public void executeTest() {
		
		for(ITest test : tests) {
			System.gc();
			test.loadRepository();
			test.setQueriesFile();
			test.executeQueries();
			report += test.getLoadTimeReport();
			report += test.getMemoryLoadReport();
			report += test.getAllQueriesTimeReport();
			report += test.getqueryResult();
		}
	}
	
	public String getReport() {
		return report;
	}
	
	public void generateReport() {
		
		File resultFile = new File("results.txt");
		try {
			resultFile.createNewFile();
		} catch (IOException e) {
			System.out.println("Error in creating results file");
			e.printStackTrace();
		}
		
		PrintWriter saveFile = null;
		
		try {
			saveFile = new PrintWriter(resultFile);
			
			saveFile.print(report);
		} catch (FileNotFoundException e) {
			System.out.println("result file not found!");
			e.printStackTrace();
		}
		
		saveFile.close();
	}
	

	private void loadGeneratorsFile() {
		File generators = new File("configuration/generator.txt");
		Scanner input = null;
		try {
			input = new Scanner(generators);
			
			generatorName = input.next();
			
		} catch (FileNotFoundException e) {
			System.out.println("File with generators doesn't exist.");
			e.printStackTrace();
		} finally {
			input.close();
		}
	}
	
	private void loadTestsFile() {
		File tests = new File("configuration/tests.txt");
		Scanner input = null;
		
		testNames = new ArrayList<String>();
		
		try {
			input = new Scanner(tests);
			
			while(input.hasNext()) {
				testNames.add(input.next());
			}
			
		} catch (FileNotFoundException e) {
			System.out.println("File with generators doesn't exist.");
			e.printStackTrace();
		} finally {
			input.close();
		}
	}
	
}
