package pl.mwojciec.test.classes.virtuoso;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import pl.mwojciec.test.interfaces.ITest;

public class VirtuosoNativeWithSesame implements ITest {

	//Kontenery do raportow
	private List<String> loadTimeReport = new ArrayList<String>();
	private List<String> memoryUsageReport = new ArrayList<String>();
	private List<String> queryTimeReport = new ArrayList<String>();
	private List<String> queryResults = new ArrayList<String>();
	
	//Zapytania
	private List<String> queryList = new ArrayList<String>();
	
	public VirtuosoNativeWithSesame() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void loadRepository() {
		// TODO Auto-generated method stub

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
		
	}

}
