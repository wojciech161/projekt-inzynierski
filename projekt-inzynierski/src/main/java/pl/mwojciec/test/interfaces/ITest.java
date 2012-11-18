package pl.mwojciec.test.interfaces;

import java.io.File;

import pl.mwojciec.generator.interfaces.ITriplesGenerator;

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
	
	//Settery wspolnych parametrow
	void setNamespaceName(String ns);
	void setGenerator(ITriplesGenerator g);
	
}
