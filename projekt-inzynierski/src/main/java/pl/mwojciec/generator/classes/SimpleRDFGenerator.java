package pl.mwojciec.generator.classes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import pl.mwojciec.generator.interfaces.IDictionaryGenerator;
import pl.mwojciec.generator.interfaces.ITriplesGenerator;
import pl.mwojciec.helpers.Triple;

public class SimpleRDFGenerator implements ITriplesGenerator {
	
	//Parametry
	private int numberOfTriples = 0;		// Maksymalna liczba wygenerowanych trojek
	private int numberOfSubjects = 0;		// liczba generowanych podmiotow
	private int numberOfPredicates = 0;		// liczba generowanych predykatow
	private int numberOfValues = 0;			// liczba generowanych wartosci
	private int maxTriplesForSubject = 10;  // liczba trojek dla jednego podmiotu(max)
	
	private String namespaceName = null;
	private String namespaceURI = null;
	
	//Zmienna zbierajaca raport
	private String report = "";
	
	//Tablice przechowujace nazwy
	private String[] subjectNames = null;
	private String[] predicateNames = null;
	private String[] valueNames = null;
	
	//Kontenery do funkcji pomocniczych przy zapytaniach
	List<Triple> usedTriples = new ArrayList<Triple>();
	
	public SimpleRDFGenerator(int triples, int subjects, int predicates, int values, int maxTriplesSubject) {
		
		numberOfTriples = triples;
		numberOfSubjects = subjects;
		numberOfPredicates = predicates;
		numberOfValues = values;
		maxTriplesForSubject = maxTriplesSubject;
		
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
			System.out.println("File with subjects not found");
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
			System.out.println("File with predicates not found");
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
			System.out.println("File with values not found");
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
			System.out.println("Error in creating RDF file");
			e.printStackTrace();
		}
		
		PrintWriter output = null;
		
		try {
			output = new PrintWriter(file);
		} catch (FileNotFoundException e) {
			System.out.println("Error in saving RDF file");
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
				
				Triple t = new Triple(
						subjectNames[addedSubjects], 
						predicateNames[predicateName],
						valueNames[valueName]);
				
				usedTriples.add(t);
				
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
		
		report = "Generated: " + addedTriples + " triples, subjects: " + addedSubjects;
	}
	
	//Publiczna funkcja generujaca plik rdf z trojkami
	public void generate() {
		
		if( checkData() ) {
			
			System.out.println("Generating dictionary...");
			
			generateDictionary();
			
			System.out.println("Generating dictionary finished.");
			
			getNames();
			
			System.out.println("Generating RDF file...");
			
			generateRDFFile();
			
			System.out.println("Generating RDF file finished");
			
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

	@Override
	public void generateQueriesFile() {
		List<String> sparqlQueries = new ArrayList<String>();
		List<String> serqlQueries = new ArrayList<String>();
		
		String queryString;
		Random r = new Random();
		
		//Zapytanie 1 - zapytanie o podmiot i wydobycie z niego informacji
		int tripleNumber = r.nextInt(usedTriples.size());
		queryString = "SELECT x, y, z FROM {x} y {z} WHERE x LIKE \"*" + usedTriples.get(tripleNumber).subject + "\" USING NAMESPACE "
				+ namespaceName + " = <" + namespaceURI + "#>";
		serqlQueries.add(queryString);
		
		queryString = "SELECT ?predicate ?value WHERE {<" + namespaceURI + "/" + usedTriples.get(tripleNumber).subject + "> ?predicate ?value}";
		sparqlQueries.add(queryString);
		
		//Zapytanie 2 - wylistowanie wszystkich obiektow o podanym predykacie
		tripleNumber = r.nextInt(usedTriples.size());
		queryString = "SELECT x, y FROM {x} "
		+ namespaceName + ":" + usedTriples.get(tripleNumber).predicate
		+ " {y} USING NAMESPACE "
				+ namespaceName + " = <" + namespaceURI + "#>";
		serqlQueries.add(queryString);
		
		queryString = "SELECT ?subject ?value WHERE {?subject " + "<" + namespaceURI + "#" + usedTriples.get(tripleNumber).predicate + "> ?value}";
		sparqlQueries.add(queryString);
		
		//Zapytanie 3 - Odnalezienie trojki podajac podmiot i predykat
		tripleNumber = r.nextInt(usedTriples.size());
		queryString = "SELECT x, y FROM {x} "
				+ namespaceName + ":" + usedTriples.get(tripleNumber).predicate
				+ " {y} WHERE x LIKE \"*" + usedTriples.get(tripleNumber).subject
				+ "\" USING NAMESPACE "
				+ namespaceName + " = <" + namespaceURI + "#>";
		serqlQueries.add(queryString);
		
		queryString = "SELECT ?value WHERE {<" + namespaceURI + "/" + usedTriples.get(tripleNumber).subject + 
				"> " + "<" + namespaceURI + "#" + usedTriples.get(tripleNumber).predicate + "> ?value}";
		sparqlQueries.add(queryString);
		
		//Zapytanie 4 - Odnalezienie trojki podajac predykat i wartosc
		tripleNumber = r.nextInt(usedTriples.size());
		queryString = "SELECT x FROM {x} "
		+ namespaceName + ":" + usedTriples.get(tripleNumber).predicate
		+ " {y} WHERE y LIKE \""
		+ usedTriples.get(tripleNumber).object + "\" USING NAMESPACE "
		+ namespaceName + " = <" + namespaceURI + "#>";
		serqlQueries.add(queryString);
		
		queryString = "SELECT ?subject WHERE {?subject <" + namespaceURI + "#" + 
		usedTriples.get(tripleNumber).predicate + "> \"" + usedTriples.get(tripleNumber).object + "\"}";
		sparqlQueries.add(queryString);
		
		//Zapytanie 5 - Dodanie do podanego podmiotu trojki
		
		//Generowanie pliku Serql
		File serqlQueriesFile = new File("SerqlQueries.txt");
		Iterator<String> iter = serqlQueries.iterator();
		try {
			serqlQueriesFile.createNewFile();
			PrintWriter output = new PrintWriter(serqlQueriesFile);
			
			while(iter.hasNext()) {
				output.println(iter.next());
			}
			
			output.close();
			
		} catch (IOException e) {
			System.err.println("error when creating a new file.");
			e.printStackTrace();
		}
		
		//generowanie pliku sparql
		File sparqlQueriesFile = new File("SparqlQueries.txt");
		Iterator<String> it = sparqlQueries.iterator();
		try {
			sparqlQueriesFile.createNewFile();
			PrintWriter output = new PrintWriter(sparqlQueriesFile);
			
			while(it.hasNext()) {
				output.println(it.next());
			}
			
			output.close();
			
		} catch(IOException e) {
			System.err.println("Error when creating file");
			e.printStackTrace();
		}
		
	}
	
}
