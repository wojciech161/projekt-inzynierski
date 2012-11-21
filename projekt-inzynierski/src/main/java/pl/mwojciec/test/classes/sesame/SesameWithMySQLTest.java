package pl.mwojciec.test.classes.sesame;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.openrdf.OpenRDFException;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.nativerdf.NativeStore;

import pl.mwojciec.test.interfaces.ITest;

public class SesameWithMySQLTest implements ITest {

	private Repository repository;
	
	//Kontenery do raportow
	private List<String> loadTimeReport = new ArrayList<String>();
	private List<String> memoryUsageReport = new ArrayList<String>();
	private List<String> queryTimeReport = new ArrayList<String>();
	private List<String> queryResults = new ArrayList<String>();

	//Zapytania
	private List<String> queryList = new ArrayList<String>();
	
	public SesameWithMySQLTest() {
		// TODO Auto-generated constructor stub
	}
	
	public void loadRepository() {
		File dataDir = new File("SesameNativeDB/");
		repository = new SailRepository(new NativeStore(dataDir));
		
		try {
			repository.initialize();
		} catch (RepositoryException e) {
			System.err.println("Blad przy inicjalizacji Sesame Native");
			e.printStackTrace();
		}
		
		File rdfFile = new File("Triples.rdf");
		String baseURI = "http://www.mwojciec.pl#";
		
		try {
			RepositoryConnection con = repository.getConnection();
			
			try {
				con.add(rdfFile, baseURI, RDFFormat.RDFXML);
				
				System.out.println("Repozytorium pomyslnie zaladowane!");
			} catch (RDFParseException e) {
				System.out.println("Blad przy parsowaniu pliku RDF");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("Nie moge uzyskac dostepu do pliku!");
				e.printStackTrace();
			}
			finally {
				con.close();
			}
			
		} catch (RepositoryException e) {
			System.out.println("Nie udalo sie polaczyc z repozytorium sesame");
			e.printStackTrace();
		}
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

	public String getQueryTimeReport(int queryNumber) {
		return queryTimeReport.get(queryNumber);
	}

	public String getqueryResult(int queryNumber) {
		return queryResults.get(queryNumber);
	}

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

	public void executeQueries() {
		Iterator<String> iter = queryList.iterator();
		int queryNumber = 1;
		
		while(iter.hasNext()) {
			executeQuery(iter.next(), queryNumber++);
		}
	}
	
	
	private void executeQuery(String query, int queryNumber) {
		String resultStr = "Query " + queryNumber + " - " + query + "\n";
		
		try{
			RepositoryConnection con = repository.getConnection();
			try {
				TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SERQL, query);
				TupleQueryResult result = tupleQuery.evaluate();
				try {
					while(result.hasNext()) {
						BindingSet bs = result.next();
						resultStr += bs.toString() + "\n";
					}
				} finally {
					result.close();
				}
			}
			finally {
				con.close();
			}
		} catch (OpenRDFException e) {
			System.err.println("Error in executing query.");
			e.printStackTrace();
		}
		
		queryResults.add(resultStr);
	}

}
