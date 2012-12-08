package pl.mwojciec.test.classes.allegrograph;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.openrdf.query.BindingSet;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;

import com.franz.agraph.http.exception.AGHttpException;
import com.franz.agraph.jena.AGGraphMaker;
import com.franz.agraph.jena.AGModel;
import com.franz.agraph.repository.AGCatalog;
import com.franz.agraph.repository.AGRepository;
import com.franz.agraph.repository.AGRepositoryConnection;
import com.franz.agraph.repository.AGServer;

import pl.mwojciec.test.interfaces.ITest;

public class AllegroGraphNativeTest implements ITest {

	//Potrzebne stale
	public static String SERVER_URL = "http://localhost:10035";
	public static String CATALOG_ID = "java-catalog";
	public static String REPOSITORY_ID = "testrepository";
	public static String USERNAME = "marcin";
	public static String PASSWORD = "password";
	
	//Zmienne potrzebne dla AllegroGraph
	AGServer server = null;
	AGCatalog catalog = null;
	AGRepository repository = null;
	AGRepositoryConnection conn = null;
	AGGraphMaker maker = null;
	AGModel model = null;
	
	//Kontenery do raportow
	private List<String> loadTimeReport = new ArrayList<String>();
	private List<String> memoryUsageReport = new ArrayList<String>();
	private List<String> queryTimeReport = new ArrayList<String>();
	private List<String> queryResults = new ArrayList<String>();
	
	//Zapytania
	private List<String> queryList = new ArrayList<String>();
	
	public AllegroGraphNativeTest() {
		
		System.out.println("AllegroGraph Native test");
		System.out.println("Initializing framework");
		
		server = new AGServer(SERVER_URL, USERNAME, PASSWORD);
		try {
			catalog = server.getCatalog(CATALOG_ID);
		} catch (AGHttpException e) {
			System.out.println("Blad przy wybieraniu katalogu");
			e.printStackTrace();
		}
		
		try {
			catalog.deleteRepository(REPOSITORY_ID);
		} catch (RepositoryException e) {
			System.out.println("Blad przy usuwaniu repozytorium");
			e.printStackTrace();
		}
		
		try {
			repository = catalog.createRepository(REPOSITORY_ID);
			repository.initialize();
			conn = repository.getConnection();
		} catch (RepositoryException e) {
			System.out.println("Blad przy tworzeniu repozytorium");
			e.printStackTrace();
		}
		
		maker = new AGGraphMaker(conn);
		
		model = new AGModel(maker.getGraph());
		
		loadTimeReport.add("AllegroGraph Native test - Load time report");
		memoryUsageReport.add("AllegroGraph Native test - Memory usage report");
		queryTimeReport.add("AllegroGraph Native test - Query time report");
		queryResults.add("AllegroGraph Native test - Query results");
		
		System.out.println("Initialization done");
	}
	
	@Override
	public void loadRepository() {
		System.out.println("Loading repository");
		
		long memoryStart = Runtime.getRuntime().totalMemory(); 	// w bajtach
		long timeStart = System.nanoTime();						// w nanosekundach
		
		try {
			model.read(new FileInputStream("Triples.rdf"), "http://example.org");
		} catch (FileNotFoundException e) {
			System.out.println("File with triples not found");
			e.printStackTrace();
		}
		
		System.out.println("Loading finished.");

		long elapsedTimeInNs = System.nanoTime() - timeStart;
		
		double elapsedTimeInSeconds = (double)elapsedTimeInNs/1000000000;
		long usedMemoryInBytes = Runtime.getRuntime().totalMemory() - memoryStart;
		double usedMemoryInMegabytes = (double)usedMemoryInBytes / 1024 / 1024;
		
		loadTimeReport.add("Loading time: " + elapsedTimeInSeconds + " seconds");
		memoryUsageReport.add("Used memory: " + usedMemoryInMegabytes + "MB");
		
		System.out.println("After loading, model contains " + model.size() 
				+ " triples in graph '" + model.getGraph().getName() + "'.");
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
		String results = "";
		
		Iterator<String> iter = queryResults.iterator();
		
		while( iter.hasNext() ) {
			results += iter.next() + "\n";
		}
		
		return results;
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
				
				TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
				tupleQuery.setIncludeInferred(true /* includeInferred */);
				TupleQueryResult result = tupleQuery.evaluate();
				queryTime = System.nanoTime() - queryStartTime;
				
	            while( result.hasNext() ) {
	            	BindingSet bindingSet = result.next();
	            	resultStr += bindingSet.toString() + "\n";
	            }
			} else if (queryString.contains("CONSTRUCT")) {
				GraphQuery graphQuery = conn.prepareGraphQuery(QueryLanguage.SPARQL, queryString);
				graphQuery.setIncludeInferred(true);
				graphQuery.evaluate();
				queryTime = System.nanoTime() - queryStartTime;
				
			} else if(queryString.contains("INSERT") || queryString.contains("DELETE")) {
				conn.prepareUpdate(QueryLanguage.SPARQL, queryString);
				conn.commit();
			}
            
		} catch (QueryEvaluationException e) {
			System.out.println("Error in evaluating query");
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		
		double queryTimeInSeconds = (double)queryTime/1000000000;
		queryTimeReport.add("Query " + queryNumber + " - " + queryString + " - " + queryTimeInSeconds + "s");
		
		queryResults.add(resultStr);
		
		System.out.println("Query execution finished");
		
	}
	
	protected void finalize() {
		
		model.close();
		maker.close();
		
		try {
			conn.close();
			repository.close();
		} catch (RepositoryException e) {
			System.out.println("Error when closing connections");
			e.printStackTrace();
		}
		
		server.close();
	}

}
