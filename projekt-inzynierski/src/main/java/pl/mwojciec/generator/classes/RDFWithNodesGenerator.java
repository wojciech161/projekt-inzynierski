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

public class RDFWithNodesGenerator implements ITriplesGenerator {

	// Parametry
	private int maxNumberOfTriples = 0;					// Maksymalna liczba wygenerowanych trojek, nie liczac trojek w nodach
	private int maxNumberOfLevels = 0;					// Maksymalna liczba poziomow w grafie
	private int maxNumberOfTriplesInOneSubject = 0;		// Max liczba trojek w 1 podmiocie
	
	private String namespaceName = null;
	private String namespaceURI = null;
	
	//Zmienna zbierajaca raport
	private String report = "";
	
	//Tablice przechowujace nazwy
	private String[] subjectNames = null;
	private String[] predicateNames = null;
	private String[] valueNames = null;
	private String[] nodeNames = null;
	
	//Ilosci generowanych nazw
	private int subjects = 0;
	private int predicates = 0;
	private int objects = 0;
	private int nodes = 0;
	
	//Tablica list przechowujaca poziomy node-ow
	private String usedNodes[][] = null;
	
	//Zmienna do statystyk
	private int numberOfTriplesInsideNodes = 0;
	private int generatedMainTriples = 0;
	
	//Zmienne do generacji zapytan
	List<Triple> usedTriples = new ArrayList<Triple>();
	
	public RDFWithNodesGenerator(int triples, int levels, int maxTriplesInSubject) {
		
		maxNumberOfTriples = triples;
		maxNumberOfLevels = levels;
		maxNumberOfTriplesInOneSubject = maxTriplesInSubject;
	}
	
	//Funkcja sprawdzajaca poprawnosc parametrow
	private boolean checkData() {
		if (maxNumberOfTriples == 0 || maxNumberOfLevels == 0) 
			return false;
		else if (maxNumberOfTriplesInOneSubject > maxNumberOfTriples)
			return false;
		else
			return true;
	}
	
	//Funkcja generujaca slownik
	private void generateDictionary() {
		
		subjects = maxNumberOfTriples / maxNumberOfTriplesInOneSubject * 10;
		predicates = maxNumberOfTriples / 10;
		objects = maxNumberOfTriples * maxNumberOfLevels * maxNumberOfTriplesInOneSubject * 2;
		nodes = subjects * maxNumberOfLevels;
		
		IDictionaryGenerator generator = new DictionaryGenerator(subjects, predicates, objects, nodes);
		
		generator.generateSubjectNames();
		generator.generatePredicateNames();
		generator.generateObjectNames();
		generator.generateNodeNames();
	}
	
	//Funkcja pobierajaca slownik z pliku i zapisujacy go w kontenerze
	private void getDictionary() {
		//Pobieranie podmiotow
		subjectNames = new String[subjects];
		File subjectsFile = new File("Subjects.txt");
		try {
			Scanner subjectsInput = new Scanner(subjectsFile);
			
			for(int i = 0; i < subjects; i++) {
				subjectNames[i] = subjectsInput.nextLine();
			}
			
			subjectsInput.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("Plik z podmiotami nie został znaleziony");
			e.printStackTrace();
		}
		
		//Pobieranie predykatow
		predicateNames = new String[predicates];
		File predicatesFile = new File("Predicates.txt");
		try {
			Scanner input = new Scanner(predicatesFile);
			
			for(int i = 0; i < predicates; i++) {
				predicateNames[i] = input.nextLine();
			}
			
			input.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("Plik z predykatami nie został znaleziony");
			e.printStackTrace();
		}
		
		//Pobieranie wartosci
		valueNames = new String[objects];
		File valuesFile = new File("Objects.txt");
		try {
			Scanner input = new Scanner(valuesFile);
			
			System.out.println(objects);
			
			for(int i = 0; i < objects; i++) {
				valueNames[i] = input.nextLine();
			}
			
			input.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("Plik z wartosciami nie został znaleziony");
			e.printStackTrace();
		}
		
		// Pobieranie nazw node-ow
		nodeNames = new String[nodes];
		File nodesFile = new File("Nodes.txt");
		try {
			
			Scanner input = new Scanner(nodesFile);
			
			for (int i = 0; i < nodes; i++) {
				nodeNames[i] = input.nextLine();
			}
			
			input.close();
			
		} catch(FileNotFoundException e) {
			
		}
				
	}
	
	private String generateNode(int level, String name) {
		String result = "";
		Random r = new Random();
		
		if( level == 0 )
		{
			int numberOfTriples = r.nextInt(maxNumberOfTriplesInOneSubject) + 1;
			
			result = RDFSyntax.rdfDescriptionNodeId + "\""
					+ name + "\">\n";
			
			for(int i = 0; i < numberOfTriples; i++) {
				int predicateNumber = r.nextInt(predicates);
				int valueNumber = r.nextInt(objects);
				result += "\t<" + namespaceName + ":"
						+ predicateNames[predicateNumber] + ">"
						+ valueNames[valueNumber] + "</"
						+ namespaceName + ":"
						+ predicateNames[predicateNumber] + ">\n";
				numberOfTriplesInsideNodes++;
				//usedPredicateObjects.add(new Pair<String, String>(name, predicateNames[valueNumber]));
			}
			
			result += RDFSyntax.rdfDescriptionEnding;
		}
		else {
			result = RDFSyntax.rdfDescriptionNodeId + "\""
					+ name + "\">\n";
			
			int numberOfTriples = r.nextInt(maxNumberOfTriplesInOneSubject) + 1;
			
			for(int i = 0; i < numberOfTriples; i++) {
				int predicateNumber = r.nextInt(predicates);
				int nodeNumber = r.nextInt(usedNodes[level - 1].length);
				
				String nodeName = usedNodes[level - 1][nodeNumber];
				
				result += "\t<" + namespaceName + ":"
						+ predicateNames[predicateNumber] + " "
						+ RDFSyntax.rdfNodeId + "\""
						+ nodeName + "\"/>\n";
				numberOfTriplesInsideNodes++;
			}
			
			result += RDFSyntax.rdfDescriptionEnding;
		}
		
		return result;
	}
	
	private String generateTriple(int subject) {
		String result = "";
		Random r = new Random();
		
		result += RDFSyntax.rdfDescription
				+ "\"" + namespaceURI + "/" + subjectNames[subject] + "\">\n";
		
		int numberOfTriples = r.nextInt(maxNumberOfTriplesInOneSubject) + 1;
		
		for (int i = 0; i < numberOfTriples; i++) {
			int levelNumber = r.nextInt(maxNumberOfLevels);
			int nodeNumber = r.nextInt(usedNodes[levelNumber].length);
			int predicateNumber = r.nextInt(predicates);
			
			result += "\t<" + namespaceName + ":"
						+ predicateNames[predicateNumber] + " "
						+ RDFSyntax.rdfNodeId
						+ "\"" + usedNodes[levelNumber][nodeNumber]
						+ "\"/>\n";
			
			usedTriples.add(new Triple( subjectNames[subject], predicateNames[predicateNumber], usedNodes[levelNumber][nodeNumber] ));
			
			generatedMainTriples++;
		}
		
		result += RDFSyntax.rdfDescriptionEnding;
		
		return result;
	}
	
	private void generateRDF() {
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
		
		//Naglowek
		output.println(RDFSyntax.xmlHeader);
		output.println(RDFSyntax.rdfHeader);
		output.println(RDFSyntax.rdfNamespace);
		output.println(RDFSyntax.xmlNamespace + namespaceName + "=\"" + namespaceURI + "#\">\n");
		
		//Na kazdy poziom iloscNodow/maxPoziomow node'ow
		int nodeLimiter = nodes/maxNumberOfLevels;
		
		int usedNodeNames = 0;
		
		usedNodes = new String[maxNumberOfLevels][nodeLimiter];
		
		//Generowanie node-ow.
		for(int lvl = 0; lvl < maxNumberOfLevels; lvl++) {
			for( int i = 0; i < nodeLimiter; i++ ) {
				usedNodes[lvl][i] = nodeNames[usedNodeNames];
				output.println( generateNode(lvl, nodeNames[usedNodeNames]) );
				usedNodeNames++;
			}
		}
		
		//Generowanie trojek
		for(int i = 0; i < subjects; i++) {
			if ( generatedMainTriples >= maxNumberOfTriples )
				break;
			output.println( generateTriple(i) );
		}
		
		//Zamkniecie rdf-a
		output.println(RDFSyntax.rdfEnding);
		
		//Zamkniecie pliku
		output.close();
	}
	
	private void prepareReport() {
		report = "Wygenerowano " + generatedMainTriples + " trojek, dodatkowo "
				+ numberOfTriplesInsideNodes + " trojek jako node-y";
	}
	
	public void generate() {
		if( checkData() ) {
			
			generateDictionary();
			
			getDictionary();
			
			generateRDF();
			
			prepareReport();
		}
	}

	public String getReport() {
		return report;
	}

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
				//System.out.println(it.next());
				output.println(it.next());
			}
			
			output.close();
			
		} catch(IOException e) {
			System.err.println("Error when creating file");
			e.printStackTrace();
		}
		
	}

}
