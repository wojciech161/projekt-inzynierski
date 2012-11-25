package pl.mwojciec.test.classes.owlim;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
		
		System.out.println("Initialization finished.");
	}
	
	@Override
	public void loadRepository() {
		
		System.out.println("Loading repository");
		
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
		
		System.out.println("Repository successfully loaded.");
		
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
			tupleQuery = connection.prepareTupleQuery(QueryLanguage.SERQL, queryString);
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
	}

}
