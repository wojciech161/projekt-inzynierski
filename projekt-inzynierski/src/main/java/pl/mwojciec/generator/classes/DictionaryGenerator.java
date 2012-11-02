package pl.mwojciec.generator.classes;

import java.io.File;
import java.io.IOException;

import pl.mwojciec.generator.interfaces.IDictionaryGenerator;

class DictionaryGenerator implements IDictionaryGenerator{

	private int numberOfSubjects;		// Ile wygenerowac podmiotow
	private int numberOfPredicates;		// Ile wygenerowac predykatow
	private int numberOfObjects;		// Ile wygenerowac obiektow
	private int numberOfClasses;		// Ile wygenerowac klas
	private int numberOfSubclasses;		// Ile wygenerowac podklas
	private int numberOfNodes;			// Ile wygenerowac node'ow
	private int numberOfLabels;			// Ile wygenerowac node'ow
	private int numberOfComments;			// Ile wygenerowac node'ow
	
	public DictionaryGenerator(int subjects, int predicates, int objects) {
		
		numberOfSubjects = subjects;
		numberOfPredicates = predicates;
		numberOfObjects = objects;
		numberOfClasses = 0;
		numberOfSubclasses = 0;
		numberOfNodes = 0;
		numberOfLabels = 0;
		numberOfComments = 0;
		
	}
	
	public DictionaryGenerator(int subjects, int predicates, int objects, int nodes) {
		numberOfSubjects = subjects;
		numberOfPredicates = predicates;
		numberOfObjects = objects;
		numberOfClasses = 0;
		numberOfSubclasses = 0;
		numberOfNodes = nodes;
		numberOfLabels = 0;
		numberOfComments = 0;
	}
	
	public DictionaryGenerator(int subjects, int predicates, int objects, int classes, int subclasses, int values, int comments) {
		
		numberOfSubjects = subjects;
		numberOfPredicates = predicates;
		numberOfObjects = objects;
		numberOfClasses = classes;
		numberOfSubclasses = subclasses;
		numberOfLabels = values;
		numberOfComments = comments;
		numberOfNodes = 0;
		
	}
	
	//Funkcja liczaca minimalna dlugosc 
	//slowa aby wygenerowac wymagana liczbe wartosci
	private int countWordLength(int numberOfValues) {
		
		int result = 1;
		int availableVariations = 26;
		
		for(; availableVariations < numberOfValues; result++) {
			availableVariations *= 26;
		}
		
		return result;
	}

	public void generateSubjectNames() {
		
		int minWordLength = countWordLength(numberOfSubjects);
		
		File generatedFile = new File("Subjects.txt");
		try {
			
			generatedFile.createNewFile();
			
		} catch (IOException e) {
			System.out.println("Nie moge utworzyc pliku z podmiotami!");
			e.printStackTrace();
		}
		
		Variation words = new Variation(minWordLength, numberOfSubjects, generatedFile, '1');
		words.variate();
		
	}

	public void generatePredicateNames() {
		
		int minWordLength = countWordLength(numberOfPredicates);
		
		File generatedFile = new File("Predicates.txt");
		try {
			
			generatedFile.createNewFile();
			
		} catch (IOException e) {
			System.out.println("Nie moge utworzyc pliku z predykatami!");
			e.printStackTrace();
		}
		
		Variation words = new Variation(minWordLength, numberOfPredicates, generatedFile, '2');
		words.variate();
		
	}

	public void generateObjectNames() {
		
		int minWordLength = countWordLength(numberOfObjects);
		
		File generatedFile = new File("Objects.txt");
		try {
			
			generatedFile.createNewFile();
			
		} catch (IOException e) {
			System.out.println("Nie moge utworzyc pliku z obiektami!");
			e.printStackTrace();
		}
		
		Variation words = new Variation(minWordLength, numberOfObjects, generatedFile, '3');
		words.variate();
		
	}

	public void generateClassNames() {
		
		int minWordLength = countWordLength(numberOfClasses);
		
		File generatedFile = new File("Classes.txt");
		try {
			
			generatedFile.createNewFile();
			
		} catch (IOException e) {
			System.out.println("Nie moge utworzyc pliku z klasami!");
			e.printStackTrace();
		}
		
		Variation words = new Variation(minWordLength, numberOfClasses, generatedFile, '4');
		words.variate();
		
	}

	public void generateSubClassNames() {
		
		int minWordLength = countWordLength(numberOfSubclasses);
		
		File generatedFile = new File("Subclasses.txt");
		try {
			
			generatedFile.createNewFile();
			
		} catch (IOException e) {
			System.out.println("Nie moge utworzyc pliku z podklasami!");
			e.printStackTrace();
		}
		
		Variation words = new Variation(minWordLength, numberOfSubclasses, generatedFile, '5');
		words.variate();
		
	}

	public void generateNodeNames() {
		
		int minWordLength = countWordLength(numberOfNodes);
		
		File generatedFile = new File("Nodes.txt");
		try {
			
			generatedFile.createNewFile();
			
		} catch (IOException e) {
			System.out.println("Nie moge utworzyc pliku z nodami!");
			e.printStackTrace();
		}
		
		Variation words = new Variation(minWordLength, numberOfNodes, generatedFile, '6');
		words.variate();
		
	}

	public void generateLabelNames() {
		int minWordLength = countWordLength(numberOfLabels);
		
		File generatedFile = new File("Labels.txt");
		try {
			
			generatedFile.createNewFile();
			
		} catch (IOException e) {
			System.out.println("Nie moge utworzyc pliku z labelami!");
			e.printStackTrace();
		}
		
		Variation words = new Variation(minWordLength, numberOfLabels, generatedFile, '7');
		words.variate();
		
	}

	public void generateCommentNames() {
		int minWordLength = countWordLength(numberOfComments);
		
		File generatedFile = new File("Comments.txt");
		try {
			
			generatedFile.createNewFile();
			
		} catch (IOException e) {
			System.out.println("Nie moge utworzyc pliku z komentarzami!");
			e.printStackTrace();
		}
		
		Variation words = new Variation(minWordLength, numberOfComments, generatedFile, '8');
		words.variate();
		
	}

}
