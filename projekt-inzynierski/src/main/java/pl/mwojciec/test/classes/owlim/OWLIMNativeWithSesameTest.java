package pl.mwojciec.test.classes.owlim;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.openrdf.query.BindingSet;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.manager.LocalRepositoryManager;
import org.openrdf.repository.manager.RepositoryManager;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;

import pl.mwojciec.test.interfaces.ITest;

public class OWLIMNativeWithSesameTest implements ITest {

	private Repository repository = null;
	private RepositoryConnection connection = null;
	private RepositoryManager manager = null;
	
	//Kontenery do raportow
	private List<String> loadTimeReport = new ArrayList<String>();
	private List<String> memoryUsageReport = new ArrayList<String>();
	private List<String> queryTimeReport = new ArrayList<String>();
	private List<String> queryResults = new ArrayList<String>();
	
	//Zapytania
	private List<String> queryList = new ArrayList<String>();
	
	public OWLIMNativeWithSesameTest() {
		System.out.println("OWLIM Native test");
		System.out.println("Initialization");
		
		manager = new LocalRepositoryManager(new File("."));
		try {
			manager.initialize();
		} catch (RepositoryException e) {
			System.out.println("Error in initializing OWLIM LocalRepositoryManager");
			e.printStackTrace();
		}
		
		repository = manager.getSystemRepository();
		
		try {
			connection = repository.getConnection();
		} catch (RepositoryException e) {
			System.out.println("Error in getting connection from repository");
			e.printStackTrace();
		}
		
		loadTimeReport.add("OWLIM Native test - Load time report");
		memoryUsageReport.add("OWLIM Native test - Memory usage report");
		queryTimeReport.add("OWLIM Native test - Query time report");
		queryResults.add("OWLIM Native test - Query results");
		
		System.out.println("Initialization finished.");
	}
	
	@Override
	public void loadRepository() {
		
		System.out.println("Loading repository");
		
		long memoryStart = Runtime.getRuntime().totalMemory(); 	// w bajtach
		long timeStart = System.nanoTime();		
		
		File rdfFile = new File("Triples.rdf");
		String baseURI = "http://www.mwojciec.pl#";
		
		try {
			connection.add(rdfFile, baseURI, RDFFormat.RDFXML);
		} catch (RDFParseException e) {
			System.out.println("Error in parsing RDF file");
			e.printStackTrace();
		} catch (RepositoryException e) {
			System.out.println("Error in loading file into repository");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("RDF File not found.");
			e.printStackTrace();
		}
		
		System.out.println("Loading finished.");

		long elapsedTimeInNs = System.nanoTime() - timeStart;
		
		double elapsedTimeInSeconds = (double)elapsedTimeInNs/1000000000;
		long usedMemoryInBytes = Runtime.getRuntime().totalMemory() - memoryStart;
		double usedMemoryInMegabytes = (double)usedMemoryInBytes / 1024 / 1024;
		
		loadTimeReport.add("Loading time: " + elapsedTimeInSeconds + " seconds");
		memoryUsageReport.add("Used memory: " + usedMemoryInMegabytes + "MB");
		
	}

	@Override
	public String getLoadTimeReport() {
		String result = new String();
		
		Iterator<String> iter = loadTimeReport.iterator();
		
		while( iter.hasNext() ) {
			result += iter.next() + "\n";
		}
		
		return result;
	}

	@Override
	public String getMemoryLoadReport() {
		String result = new String();
		
		Iterator<String> iter = memoryUsageReport.iterator();
		
		while( iter.hasNext() ) {
			result += iter.next() + "\n";
		}
		
		return result;
	}

	@Override
	public void setQueriesFile() {
	
		File queries = new File("SparqlQueries.txt");
		
		try {
			Scanner input = new Scanner(queries);
			
			while(input.hasNext()) {
				queryList.add(input.nextLine());
			}
			
			input.close();
		} catch(FileNotFoundException e) {
			System.out.println("File with queries not found.");
			e.printStackTrace();
		}

	}

	@Override
	public void executeQueries() {
		Iterator<String> iter = queryList.iterator();
		int queryNumber = 1;
		
		while(iter.hasNext()) {
			executeQuery(iter.next(), queryNumber++);
		}
	}

	@Override
	public String getqueryResult() {
		
		String result = new String();
		
		Iterator<String> iter = queryResults.iterator();
		
		while( iter.hasNext() ) {
			result += iter.next() + "\n";
		}
		
		return result;
	}

	@Override
	public String getAllQueriesTimeReport() {
		String result = new String();
		
		Iterator<String> iter = queryTimeReport.iterator();
		
		while( iter.hasNext() ) {
			result += iter.next() + "\n";
		}
		
		return result;
	}
	
	private void executeQuery(String queryString, int queryNumber) {
		String resultStr = "Query " + queryNumber + " - " + queryString + "\n";
		System.out.print(resultStr);
		
		long queryTime = 0;
		long queryStartTime = System.nanoTime();
		
		try {
			
			if(queryString.contains("SELECT")) {
				
				TupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
				tupleQuery.setIncludeInferred(true /* includeInferred */);
				TupleQueryResult result = tupleQuery.evaluate();
				queryTime = System.nanoTime() - queryStartTime;
				
	            while( result.hasNext() ) {
	            	BindingSet bindingSet = result.next();
	            	resultStr += bindingSet.toString() + "\n";
	            }
			} else if (queryString.contains("CONSTRUCT")) {
				GraphQuery graphQuery = connection.prepareGraphQuery(QueryLanguage.SPARQL, queryString);
				graphQuery.setIncludeInferred(true);
				graphQuery.evaluate();
				queryTime = System.nanoTime() - queryStartTime;
				
			} else if(queryString.contains("INSERT") || queryString.contains("DELETE")) {
				connection.prepareUpdate(QueryLanguage.SPARQL, queryString);
				connection.commit();
			}
            
		} catch (MalformedQueryException e) {
			System.out.println("Malformed query - syntax error");
			e.printStackTrace();
		} catch (RepositoryException e) {
			System.out.println("Error with repository");
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			System.out.println("Error in evaluating query");
			e.printStackTrace();
		}
		
		double queryTimeInSeconds = (double)queryTime/1000000000;
		queryTimeReport.add("Query " + queryNumber + " - " + queryString + " - " + queryTimeInSeconds + "s");
		
		queryResults.add(resultStr);
		
		System.out.println("Query execution finished");
	}

}
