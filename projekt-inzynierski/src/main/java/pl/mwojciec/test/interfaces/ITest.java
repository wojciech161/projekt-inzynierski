package pl.mwojciec.test.interfaces;

import java.io.File;

import pl.mwojciec.generator.interfaces.ITriplesGenerator;

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
	
	//Settery wspolnych parametrow
	void setNamespaceName(String ns);
	void setGenerator(ITriplesGenerator g);
	
}
