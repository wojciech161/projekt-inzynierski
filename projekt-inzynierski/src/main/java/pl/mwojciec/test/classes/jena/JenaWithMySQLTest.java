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
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.db.*;

import pl.mwojciec.test.interfaces.ITest;

public class JenaWithMySQLTest implements ITest {

	private Model model;					//Glowna klasa Jeny, w niej trzymana baza
	private IDBConnection conn;
	
	//Kontenery do raportow
	private List<String> loadTimeReport = new ArrayList<String>();
	private List<String> memoryUsageReport = new ArrayList<String>();
	private List<String> queryTimeReport = new ArrayList<String>();
	private List<String> queryResults = new ArrayList<String>();
	
	//Zapytania
	private List<String> queryList = new ArrayList<String>();
	
	public JenaWithMySQLTest() {
		String className = "com.mysql.jdbc.Driver";
		try {
			Class.forName(className);
		} catch (ClassNotFoundException e) {
			System.out.println("Cannot find mysql jdbc driver");
			e.printStackTrace();
		}
		
		String DB_URL = "jdbc:mysql://localhost/ONTOLOGY_DB";	// URL bazy
		String DB_USER = "ontology";							// Nazwa uzytkownika
		String DB_PASSWD = "password";							// Haslo uzytkownika
		String DB = "MySQL";									// typ bazy danych
		
		
		
		//ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
	}
	
	public void loadRepository() {
		
		InputStream in = FileManager.get().open("Triples.rdf");
		
		if ( in == null ) {
			System.err.println("Plik RDF z trojkami nie zostal znaleziony!");
		}
		
		model.read(in, null);

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

	private void executeQuery(String queryString, int queryNumber) {
		
		String resultString = "Query " + queryNumber + " - " + queryString + "\n";
		
		Query query = QueryFactory.create(queryString);
		
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		ResultSet results = qe.execSelect();
		
		resultString += ResultSetFormatter.asText(results, query);
		System.out.println(resultString);
		
		queryResults.add(resultString);
		
		qe.close();
		
	}
	
}
