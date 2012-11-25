package pl.mwojciec.test.classes.bigdata;

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
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;

import com.bigdata.rdf.sail.BigdataSail;
import com.bigdata.rdf.sail.BigdataSailRepository;
import com.bigdata.rdf.sail.BigdataSailRepositoryConnection;

import pl.mwojciec.test.interfaces.ITest;

public class BigDataNativeWithSesameTest implements ITest {
	
	BigdataSail sail = null;
	BigdataSailRepository repository = null;
	BigdataSailRepositoryConnection connection = null;
	
	//Kontenery do raportow
	private List<String> loadTimeReport = new ArrayList<String>();
	private List<String> memoryUsageReport = new ArrayList<String>();
	private List<String> queryTimeReport = new ArrayList<String>();
	private List<String> queryResults = new ArrayList<String>();
	
	//Zapytania
	private List<String> queryList = new ArrayList<String>();
	
	public BigDataNativeWithSesameTest() {
		
		sail = new BigdataSail();
		repository = new BigdataSailRepository(sail);
		
		try {
			repository.initialize();
			connection = repository.getConnection();
			connection.setAutoCommit(false);
		} catch (RepositoryException e) {
			System.out.println("Cannot initialize BigDataSailRepository");
			e.printStackTrace();
		}
	}
	
	@Override
	public void loadRepository() {
		
		File rdfFile = new File("Triples.rdf");
		String baseURI = "http://www.mwojciec.pl#";
		
		try {
			connection.add(rdfFile, baseURI, RDFFormat.RDFXML);
		} catch (RDFParseException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		
			try {
				TupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
				tupleQuery.setIncludeInferred(true /* includeInferred */);
	            TupleQueryResult result = tupleQuery.evaluate();
	            
	            while( result.hasNext() ) {
	            	BindingSet bindingSet = result.next();
	            	resultStr += bindingSet.toString() + "\n";
	            }
	            
			} catch (MalformedQueryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RepositoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (QueryEvaluationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		queryResults.add(resultStr);
		System.out.println(resultStr);
	}
	
	protected void finalize() {
		try {
			connection.close();
		} catch (RepositoryException e) {
			System.out.println("Error when closing connection");
			e.printStackTrace();
		}
	}
}
