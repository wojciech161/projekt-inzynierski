package pl.mwojciec.test.classes.virtuoso;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import pl.mwojciec.test.interfaces.ITest;
import virtuoso.sesame2.driver.VirtuosoRepository;

public class VirtuosoNativeWithSesame implements ITest {

//	//Parametry
//	private final String sesameServer = "http://localhost:8080/openrdf-sesame";
//	private final String repositoryID = "VirtuosoTest";
	
	// Zmienne do sesame
	Repository repository;
	RepositoryConnection connection;
	
	//Kontenery do raportow
	private List<String> loadTimeReport = new ArrayList<String>();
	private List<String> memoryUsageReport = new ArrayList<String>();
	private List<String> queryTimeReport = new ArrayList<String>();
	private List<String> queryResults = new ArrayList<String>();
	
	//Zapytania
	private List<String> queryList = new ArrayList<String>();
	
	public VirtuosoNativeWithSesame() {
		System.out.println("VirtuosoNative with Sesame Provider test");
		System.out.println("Initializing framework...");
		
		repository = new VirtuosoRepository("jdbc:virtuoso://localhost:1111","dba","dba");
		
		try {
			repository.initialize();
		} catch (RepositoryException e) {
			System.out.println("Error in initializing repository");
			e.printStackTrace();
		}
		
		try {
			connection = repository.getConnection();
		} catch (RepositoryException e) {
			System.out.println("Error in getting connection");
			e.printStackTrace();
		}
		
		System.out.println("Initialization finished");
	}
	
	@Override
	public void loadRepository() {
		
		System.out.println("Virtuoso doesn't provide method to load repository from file.");
		System.out.println("To do it, You have to copy Triples.rdf file to virtuoso root");
		System.out.println("In Ubuntu it is /var/lib/virtuoso-opensource-6.1/vsp");
		System.out.println("Next, you have to execute query:");
		System.out.println("LOAD <file:/Triples.rdf> INTO GRAPH <(Namespace URI here)>");
		System.out.println("After that You can execute queries normally.");
		
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
			String resNum = "Zapytanie " + queryNumber + "\n";
			result += resNum + iter.next() + "\n";
			++queryNumber;
		}
		
		return result;
	}
	
	private void executeQuery(String queryString, int queryNumber) {
		String resultStr = "Query " + queryNumber + " - " + queryString + "\n";
		System.out.println("Query " + queryNumber + " - " + queryString);
		
		TupleQuery tupleQuery = null;
		
		try {
			tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
		} catch (RepositoryException e) {
			System.out.println("Error in preparing query");
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			System.out.println("Query is wrong(Query syntax error).");
			e.printStackTrace();
		}
		
		TupleQueryResult result = null;
		try {
			result = tupleQuery.evaluate();
			while(result.hasNext()) {
				
				BindingSet bs = result.next();
				resultStr += bs.toString() + "\n";
			}
		} catch (QueryEvaluationException e) {
			System.out.println("Error in evaluating query.");
			e.printStackTrace();
		} finally {
			try {
				result.close();
			} catch (QueryEvaluationException e) {
				System.out.println("Error in closing result");
				e.printStackTrace();
			}
		}
		
		queryResults.add(resultStr);
		
		System.out.println(resultStr);
	}
	
	protected void finalize() {
		try {
			connection.close();
		} catch (RepositoryException e) {
			System.out.println("Error in closing connection");
			e.printStackTrace();
		}
	}

}
