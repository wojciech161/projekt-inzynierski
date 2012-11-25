package pl.mwojciec.test.classes.allegrograph;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.openrdf.query.BindingSet;
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
		
		System.out.println("Initialization done");
	}
	
	@Override
	public void loadRepository() {
		System.out.println("Loading repository");
		
		try {
			model.read(new FileInputStream("Triples.rdf"), "http://example.org");
		} catch (FileNotFoundException e) {
			System.out.println("Nie znaleziono pliku z trojkami");
			e.printStackTrace();
		}
		
		System.out.println("Loading finished.");

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
	public void setQueriesFile(File queries) {
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
	public String getQueryTimeReport(int queryNumber) {
		return queryTimeReport.get(queryNumber);
	}

	@Override
	public String getqueryResult(int queryNumber) {
		return queryResults.get(queryNumber);
	}

	@Override
	public String getAllQueriesTimeReport() {
		String result = new String();
		int queryNumber = 1;
		
		Iterator<String> iter = queryTimeReport.iterator();
		
		while( iter.hasNext() ) {
			String resNum = "Query: " + queryNumber + "\n";
			result += resNum + iter.next() + "\n";
			++queryNumber;
		}
		
		return result;
	}
	
	private void executeQuery(String queryString, int queryNumber) {
		
		System.out.println("Query " + queryNumber + " - " + queryString);
		
		String resultStr = "Query " + queryNumber + " - " + queryString + "\n";
		
		TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
		try {
			TupleQueryResult result = tupleQuery.evaluate();
			while(result.hasNext()) {
				BindingSet bindingSet = result.next();
				resultStr += bindingSet.toString() + "\n";
			}
			result.close();
		} catch (QueryEvaluationException e) {
			System.out.println("Error when executing a query");
			e.printStackTrace();
		}
		
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
