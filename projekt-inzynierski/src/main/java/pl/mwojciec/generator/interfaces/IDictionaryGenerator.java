package pl.mwojciec.generator.interfaces;

public interface IDictionaryGenerator {
	
	//Funkcje generujace nazwy do wymaganych wartosci
	void generateSubjectNames();
	void generatePredicateNames();
	void generateObjectNames();
	void generateClassNames();
	void generateSubClassNames();
	void generateNodeNames();
	void generateLabelNames();
	void generateCommentNames();
	
}
