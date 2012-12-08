package pl.mwojciec.test.interfaces;

public interface ITest {

	// Funkcje ladujace plik z trojkami, raporty czasu ladowania i zajetosci
	void loadRepository();
	String getLoadTimeReport();
	String getMemoryLoadReport();
	
	//Funkcja wykonujaca zapytania
	void setQueriesFile();
	void executeQueries();
	String getqueryResult();
	String getAllQueriesTimeReport();
	
}
