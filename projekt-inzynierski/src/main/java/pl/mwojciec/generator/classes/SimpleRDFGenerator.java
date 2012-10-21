package pl.mwojciec.generator.classes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Scanner;

import pl.mwojciec.generator.interfaces.IDictionaryGenerator;
import pl.mwojciec.generator.interfaces.ITriplesGenerator;

public class SimpleRDFGenerator implements ITriplesGenerator {
	
	//Parametry
	private int numberOfTriples = 0;		// Maksymalna liczbba wygenerowanych trojek
	private int numberOfSubjects = 0;		// liczba generowanych podmiotow
	private int numberOfPredicates = 0;		// liczba generowanych predykatow
	private int numberOfValues = 0;			// liczba generowanych wartosci
	private int maxTriplesForSubject = 10;  // liczba trojek dla jednego podmiotu(max)
	
	private String namespaceName = null;
	private String namespaceURI = null;
	
	//Zmienna zbierajaca raport
	private String report = "";
	
	//Tablice przechowujace nazwy
	String[] subjectNames = null;
	String[] predicateNames = null;
	String[] valueNames = null;
	
	public SimpleRDFGenerator() {
		
	}
	
	//Funkcja sprawdzajaca czy mozna generowac rdf-a(czy parametry sie zgadzaja)
	private boolean checkData() {
		if (numberOfTriples == 0 || 
				numberOfSubjects == 0 ||
				numberOfPredicates == 0 ||
				numberOfValues == 0) {
			return false;
		}
		else if( numberOfPredicates < maxTriplesForSubject || numberOfValues < maxTriplesForSubject )
			return false;
		else
			return true;
	}
	
	//Generowanie slownika
	private void generateDictionary() {
		IDictionaryGenerator generator = new DictionaryGenerator(numberOfSubjects, numberOfPredicates, numberOfValues);
		generator.generateObjectNames();
		generator.generatePredicateNames();
		generator.generateSubjectNames();
	}
	
	//Funkcja zapisujaca nazwy podmiotow,predykatow,wartosci z plikow do tablic
	private void getNames() {
		
		//Pobieranie podmiotow
		subjectNames = new String[numberOfSubjects];
		File subjectsFile = new File("Subjects.txt");
		try {
			Scanner subjectsInput = new Scanner(subjectsFile);
			
			for(int i = 0; i < numberOfSubjects; i++) {
				subjectNames[i] = subjectsInput.nextLine();
			}
			
			subjectsInput.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("Plik z podmiotami nie został znaleziony");
			e.printStackTrace();
		}
		
		//Pobieranie predykatow
		predicateNames = new String[numberOfPredicates];
		File predicatesFile = new File("Predicates.txt");
		try {
			Scanner input = new Scanner(predicatesFile);
			
			for(int i = 0; i < numberOfPredicates; i++) {
				predicateNames[i] = input.nextLine();
			}
			
			input.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("Plik z predykatami nie został znaleziony");
			e.printStackTrace();
		}
		
		//Pobieranie wartosci
		valueNames = new String[numberOfValues];
		File valuesFile = new File("Objects.txt");
		try {
			Scanner input = new Scanner(valuesFile);
			
			for(int i = 0; i < numberOfValues; i++) {
				valueNames[i] = input.nextLine();
			}
			
			input.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("Plik z wartosciami nie został znaleziony");
			e.printStackTrace();
		}
	}
	
	//Pomocnicza funkcja generujaca jedna trojke
	private String generateOneTriple(int predicate, int value) {
		String result = "<" + namespaceName + ":"
				+ predicateNames[predicate] + ">"
				+ valueNames[value] + "</"
				+ namespaceName + ":"
				+ predicateNames[predicate] + ">";
		
		return result;
	}
	
	//Generowanie rdf-a
	private void generateRDFFile() {
		
		File file = new File("Triples.rdf");
		
		try {
			file.createNewFile();
		} catch (IOException e) {
			System.out.println("Nie moge utworzyc pliku z trojkami!");
			e.printStackTrace();
		}
		
		PrintWriter output = null;
		
		try {
			output = new PrintWriter(file);
		} catch (FileNotFoundException e) {
			System.out.println("nie moge otworzyc pliku z trojkami do zapisu!");
			e.printStackTrace();
		}
		
		int addedTriples = 0;
		int addedSubjects = 0;
		
		//Naglowek
		output.println(RDFSyntax.xmlHeader);
		output.println(RDFSyntax.rdfHeader);
		output.println(RDFSyntax.rdfNamespace);
		output.println(RDFSyntax.xmlNamespace + namespaceName + "=\"" + namespaceURI + "#\">");
		
		//Dodawanie trojek
		for(addedSubjects = 0; addedSubjects < numberOfSubjects; addedSubjects++) {
			
			//Definicja podmiotu
			output.println(RDFSyntax.rdfDescription + "\"" + namespaceURI + "/" + subjectNames[addedSubjects] + "\">");
			
			Random r = new Random();
			int triplesInDescription = r.nextInt(maxTriplesForSubject);
			
			for(int i = 0; i < triplesInDescription; i++) {
				int predicateName = r.nextInt(numberOfPredicates);
				int valueName = r.nextInt(numberOfValues);
				
				output.println( generateOneTriple(predicateName, valueName) );
				
				addedTriples++;
			}
			
			addedSubjects++;
			
			output.println(RDFSyntax.rdfDescriptionEnding);
			
			if(addedTriples > numberOfTriples)
				break;
			
		}
		
		//Zamkniecie rdf-a
		output.println(RDFSyntax.rdfEnding);
		
		//Zamkniecie pliku
		output.close();
		
		report = "Wygenerowano: " + addedTriples + " trojek, podmiotow: " + addedSubjects;
	}
	
	//Publiczna funkcja generujaca plik rdf z trojkami
	public void generate() {
		
		if( checkData() ) {
			
			System.out.println("Generowanie slownika");
			generateDictionary();
			System.out.println("Ukonczono generowanie slownika");
			
			getNames();
			
			System.out.println("Generowanie pliku RDF");
			generateRDFFile();
			System.out.println("Ukonczono generowanie pliku RDF");
			
		}
		
	}

	//Pobranie raportu
	public String getReport() {
		return report;
		
	}

	//Settery parametrow
	public void setNamespaceName(String ns) {
		
		namespaceName = ns;
		
	}

	public void setNamespaceURI(String uri) {
		
		namespaceURI = uri;
		
	}
	
	public void setNumberOfTriples( int triples ) {
		numberOfTriples = triples;
	}
	
	public void setNumberOfSubjects( int subjects ) {
		numberOfSubjects = subjects;
	}
	
	public void setNumberOfPredicates( int predicates ) {
		numberOfPredicates = predicates;
	}
	
	public void setNumberOfValues(int values) {
		numberOfValues = values;
	}
	
	public void setMaxTriplesForSubject ( int max ) {
		maxTriplesForSubject = max;
	}
	
}
