package pl.mwojciec.test.interfaces;

import java.io.File;

public interface ITest {

	// Funkcje ladujace plik z trojkami, raporty czasu ladowania i zajetosci
	void loadRepository();
	String getLoadTimeReport();
	String getMemeoryLoadReport();
	
	//Funkcja wykonujaca zapytania
	void setQueriesFile(File queries);
	void executeQueries();
	void getQueryTimeReport(int queryNumber);
	void getqueryResult(int queryNumber);
	void getAllQueriesTimeReport();
	
}
