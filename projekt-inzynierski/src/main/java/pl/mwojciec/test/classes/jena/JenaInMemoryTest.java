package pl.mwojciec.test.classes.jena;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.update.UpdateAction;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateRequest;
import com.hp.hpl.jena.util.FileManager;

import pl.mwojciec.test.interfaces.ITest;

public class JenaInMemoryTest implements ITest {

	private Model model;					//Glowna klasa Jeny, w niej trzymana baza
	
	//Kontenery do raportow
	private List<String> loadTimeReport = new ArrayList<String>();
	private List<String> memoryUsageReport = new ArrayList<String>();
	private List<String> queryTimeReport = new ArrayList<String>();
	private List<String> queryResults = new ArrayList<String>();
	
	//Zapytania
	private List<String> queryList = new ArrayList<String>();
	
	public JenaInMemoryTest() {
		System.out.println("Jena InMemory Test");
		System.out.println("Initializing");
		
		model = ModelFactory.createDefaultModel();
		loadTimeReport.add("Jena InMemory Test - Load time report");
		memoryUsageReport.add("Jena InMemory Test - Memory usage report");
		queryTimeReport.add("Jena InMemory Test - Query time report");
		queryResults.add("Jena InMemory Test - Query results");
		
		System.out.println("initialization finished");
	}
	
	public void loadRepository() {
		
		System.out.println("Loading repository");
		
		long memoryStart = Runtime.getRuntime().totalMemory(); 	// w bajtach
		long timeStart = System.nanoTime();						// w nanosekundach
		
		InputStream in = FileManager.get().open("Triples.rdf");
		
		if ( in == null ) {
			System.err.println("RDF file not found");
		}
		
		model.read(in, null);
		
		long elapsedTimeInNs = System.nanoTime() - timeStart;
		
		System.out.println("Loading finished");
		
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

	public void printRDFFile() {
		
		model.write(System.out);
		
	}
	
	public void executeQueries() {
		Iterator<String> iter = queryList.iterator();
		int queryNumber = 1;
		
		while(iter.hasNext()) {
			executeQuery(iter.next(), queryNumber++);
		}
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
	
	private void executeQuery(String queryString, int queryNumber) {
		String resultString = "Query " + queryNumber + " - " + queryString + "\n";
		System.out.print(resultString);
		
		long queryTime = 0;
		long queryStartTime = System.nanoTime();
		
		if(queryString.contains("SELECT")) {
			Query query = QueryFactory.create(queryString);
			QueryExecution qe = QueryExecutionFactory.create(query, model);
			ResultSet results = qe.execSelect();
			queryTime = System.nanoTime() - queryStartTime;
			resultString += ResultSetFormatter.asText(results, query);
			qe.close();
			
		}
		else if(queryString.contains("CONSTRUCT")) {
			Query query = QueryFactory.create(queryString);
			QueryExecution qe = QueryExecutionFactory.create(query, model);
			qe.execConstruct();
			qe.close();
		}
		else if (queryString.contains("INSERT") || queryString.contains("DELETE")) {
			UpdateRequest ur = UpdateFactory.create(queryString);
			UpdateAction.execute(ur.getOperations().get(0), model);
		}
		
		double queryTimeInSeconds = (double)queryTime/1000000000;
		queryTimeReport.add("Query " + queryNumber + " - " + queryString + " - " + queryTimeInSeconds + "s");
		
		queryResults.add(resultString);
	}
	
}
