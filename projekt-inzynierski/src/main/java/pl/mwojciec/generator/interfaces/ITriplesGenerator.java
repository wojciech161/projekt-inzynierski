package pl.mwojciec.generator.interfaces;

import java.util.List;
import pl.mwojciec.helpers.Pair;

public interface ITriplesGenerator {
	
	//Funkcja generujaca trojki
	void generate();
	
	//Funkcja zwracajaca raport
	String getReport();
	
	//Settery do namespace
	void setNamespaceName(String ns);
	void setNamespaceURI(String uri);
	
	//Potrzebne do zapytan informacje
	List<String> getUsedSubjects();
	List<String> getUsedPredicates();
	List<Pair<String, String>> getSubjectAndPredicateFromLastLevel();
	List<String> getUsedClasses();
	List<List<String>> getUsedSubclasses();
	
}
