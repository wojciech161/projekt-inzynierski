package pl.mwojciec.generator.interfaces;

public interface ITriplesGenerator {
	
	//Funkcja generujaca trojki
	void generate();
	
	//Funkcja zwracajaca raport
	String getReport();
	
	//Settery do namespace
	void setNamespaceName(String ns);
	void setNamespaceURI(String uri);
	
	//Funkcja generujaca plik z zapytaniami
	void generateQueriesFile();
	
}
