package pl.mwojciec.test.classes.sesame;

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
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.SailException;
import org.openrdf.sail.rdbms.mysql.MySqlStore;

import pl.mwojciec.test.interfaces.ITest;

public class SesameWithMySQLTest implements ITest {

	Repository repository;
	MySqlStore mySQLStore;
	
	//Kontenery do raportow
	private List<String> loadTimeReport = new ArrayList<String>();
	private List<String> memoryUsageReport = new ArrayList<String>();
	private List<String> queryTimeReport = new ArrayList<String>();
	private List<String> queryResults = new ArrayList<String>();

	//Zapytania
	private List<String> queryList = new ArrayList<String>();
	
	public SesameWithMySQLTest() {
		
		System.out.println("Sesame with MySQL test");
		
		System.out.println("Initializing repository");
		
		mySQLStore = new MySqlStore("ONTOLOGY_DB");
		mySQLStore.setUser("ontology");
		mySQLStore.setPassword("password");
		
		repository = new SailRepository(mySQLStore);
		
		try {
			repository.initialize();
		} catch (RepositoryException e) {
			System.out.println("Error in initializing repository");
			e.printStackTrace();
		}
		
		loadTimeReport.add("Sesame with MySQL test - Load time report");
		memoryUsageReport.add("Sesame with MySQL test - Memory usage report");
		queryTimeReport.add("Sesame with MySQL test - Query time report");
		queryResults.add("Sesame with MySQL test - Query results");
		
		System.out.println("Initializing finished");
	}
	
	public void loadRepository() {
		
		System.out.println("Loading repository");
		
		long memoryStart = Runtime.getRuntime().totalMemory(); 	// w bajtach
		long timeStart = System.nanoTime();	
		
		File rdfFile = new File("Triples.rdf");
		String baseURI = "http://www.mwojciec.pl#";
		
		try {
			RepositoryConnection con = repository.getConnection();
			
			try {
				con.add(rdfFile, baseURI, RDFFormat.RDFXML);
			} catch (RDFParseException e) {
				System.out.println("Parse RDF error");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("RDF file not found!");
				e.printStackTrace();
			}
			finally {
				con.close();
			}
			
		} catch (RepositoryException e) {
			System.out.println("Nie udalo sie polaczyc z repozytorium sesame");
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

	public String getLoadTimeReport() {
		String result = new String();
		
		Iterator<String> iter = loadTimeReport.iterator();
		
		while( iter.hasNext() ) {
			result += iter.next() + "\n";
		}
		
		return result;
	}

	public String getMemoryLoadReport() {
		String result = new String();
		
		Iterator<String> iter = memoryUsageReport.iterator();
		
		while( iter.hasNext() ) {
			result += iter.next() + "\n";
		}
		
		return result;
	}

	public String getqueryResult() {
		String result = new String();
		
		Iterator<String> iter = queryResults.iterator();
		
		while( iter.hasNext() ) {
			result += iter.next() + "\n";
		}
		
		return result;
	}

	public String getAllQueriesTimeReport() {
		String result = new String();
		
		Iterator<String> iter = queryTimeReport.iterator();
		
		while( iter.hasNext() ) {
			result += iter.next() + "\n";
		}
		
		return result;
	}

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

	public void executeQueries() {
		Iterator<String> iter = queryList.iterator();
		int queryNumber = 1;
		
		while(iter.hasNext()) {
			executeQuery(iter.next(), queryNumber++);
		}
	}
	
	
	private void executeQuery(String queryString, int queryNumber) {
		String resultStr = "Query " + queryNumber + " - " + queryString + "\n";
		System.out.print(resultStr);
		
		RepositoryConnection connection = null;
		try {
			connection = repository.getConnection();
		} catch (RepositoryException e1) {
			e1.printStackTrace();
		}
		
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
	
	protected void finalize() {
		try {
			repository.shutDown();
		} catch (RepositoryException e) {
			System.out.println("Error in shutdown repository");
			e.printStackTrace();
		}
		try {
			mySQLStore.shutDown();
		} catch (SailException e) {
			System.out.println("Error in shutdown MySQLStore Sail");
			e.printStackTrace();
		}
	}

}
