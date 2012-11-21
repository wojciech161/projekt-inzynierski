package pl.mwojciec.test.interfaces;

import java.io.File;

public interface ITest {

	// Funkcje ladujace plik z trojkami, raporty czasu ladowania i zajetosci
	void loadRepository();
	String getLoadTimeReport();
	String getMemoryLoadReport();
	
	//Funkcja wykonujaca zapytania
	void setQueriesFile(File queries);
	void executeQueries();
	String getQueryTimeReport(int queryNumber);
	String getqueryResult(int queryNumber);
	String getAllQueriesTimeReport();
	
}
