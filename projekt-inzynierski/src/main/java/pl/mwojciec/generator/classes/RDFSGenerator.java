package pl.mwojciec.generator.classes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import pl.mwojciec.generator.interfaces.IDictionaryGenerator;
import pl.mwojciec.generator.interfaces.ITriplesGenerator;
import pl.mwojciec.helpers.Pair;

public class RDFSGenerator implements ITriplesGenerator {

	//Parametry
	private int maxNumberOfTriples = 0;		//max ilosc trojek
	private int numberOfClasses = 0;		//liczba klas na najwyzszym poziomie
	private int maxNumberOfSubclasses = 0;  //max liczba podklas jednej klasy
	private int maxTriplesInOneSubject = 0; //max trojek dla 1 podmiotu
	private int maxInheritLevel = 0;		//max poziom dziedziczenia
	private String namespaceName = null;	//nazwa namespace
	private String namespaceURI = null;		//uri namespace
	
	//Ilosc elementow wygenerowanego slownika
	private int subjects = 0;
	private int predicates = 0;
	private int values = 0;
	private int subclasses = 0;
	private int comments = 0;
	private int labels = 0;
	
	//Kontenery przechowujace slownik w pamieci generatora
	private String subjectNames[] = null;
	private String predicateNames[] = null;
	private String valueNames[] = null;
	private String classNames[] = null;
	private String subclassNames[] = null;
	private String commentNames[] = null;
	private String labelNames[] = null;
	
	//Zmienna przechowujaca raport
	private String report = "";
	
	//Kontener przechowujacy nazwy zadeklarowanych klas
	private LinkedList<String> availableClasses = new LinkedList<String>();
	
	//Tablica przechowujaca nazwy klas na danym poziomie
	private ArrayList<ArrayList<String>> classesOnLevel = null;
	
	//Kontenery pomocnicze do zapytan
	List<String> usedSubjects;
	List<String> usedPredicates;
	List<String> usedClasses;
	List<List<String>> usedSubclasses;
	
	public RDFSGenerator(int triples, int classes, int sclasses, int maxTriplesSubject, int inheritLvl) {
		
		maxNumberOfTriples = triples;
		numberOfClasses = classes;
		maxNumberOfSubclasses = sclasses;
		maxTriplesInOneSubject = maxTriplesSubject;
		maxInheritLevel = inheritLvl;
		
		classesOnLevel = new ArrayList<ArrayList<String>>();
		for(int i = 0; i < maxInheritLevel; i++) {
			classesOnLevel.add(new ArrayList<String>());
		}
		
		usedSubjects = new ArrayList<String>();
		usedPredicates = new ArrayList<String>();
		usedClasses = new ArrayList<String>();
		usedSubclasses = new ArrayList<List<String>>();
		
		for(int i = 0; i < sclasses; i++) {
			usedSubclasses.add(new ArrayList<String>());
		}
		
	}
	
	private boolean checkData() {
		if (maxNumberOfTriples <= 0 
				|| numberOfClasses <= 0
				|| maxNumberOfSubclasses <= 0
				|| maxInheritLevel <=0 )
			return false;
		else
			return true;
	}
	
	private void generateDictionary() {
		
		subjects = maxNumberOfTriples;
		predicates = maxNumberOfTriples / 10;
		values = maxNumberOfTriples * maxTriplesInOneSubject;
		subclasses = numberOfClasses * maxNumberOfSubclasses * maxInheritLevel;
		comments = numberOfClasses * subclasses * maxInheritLevel;
		labels = numberOfClasses * subclasses * maxInheritLevel;
		
		IDictionaryGenerator generator = new DictionaryGenerator(subjects, predicates, values,
				numberOfClasses, subclasses, labels, comments);
		
		generator.generateSubjectNames();
		generator.generatePredicateNames();
		generator.generateObjectNames();
		generator.generateClassNames();
		generator.generateSubClassNames();
		generator.generateLabelNames();
		generator.generateCommentNames();
		
	}
	
	private void getDictionary() {
		
		subjectNames = new String[subjects];
		predicateNames = new String[predicates];
		valueNames = new String[values];
		classNames = new String[numberOfClasses];
		subclassNames = new String[subclasses];
		commentNames = new String[comments];
		labelNames = new String[labels];
		
		//Pobieranie podmiotow
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
		File valuesFile = new File("Objects.txt");
		try {
			Scanner input = new Scanner(valuesFile);
			
			for(int i = 0; i < values; i++) {
				valueNames[i] = input.nextLine();
			}
			
			input.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("Plik z wartosciami nie został znaleziony");
			e.printStackTrace();
		}
		
		//Pobieranie klas
		File classFile = new File("Classes.txt");
		try {
			Scanner input = new Scanner(classFile);
			
			for(int i = 0; i < numberOfClasses; i++) {
				classNames[i] = input.nextLine();
			}
			
			input.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("Plik z klasami nie został znaleziony");
			e.printStackTrace();
		}
		
		//Pobieranie podklas
		File subclassFile = new File("Subclasses.txt");
		try {
			Scanner input = new Scanner(subclassFile);
			
			for(int i = 0; i < subclasses; i++) {
				subclassNames[i] = input.nextLine();
			}
			
			input.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("Plik z podklasami nie został znaleziony");
			e.printStackTrace();
		}
		
		//Pobieranie labeli
		File labelsFile = new File("Labels.txt");
		try {
			Scanner input = new Scanner(labelsFile);
			
			for(int i = 0; i < labels; i++) {
				labelNames[i] = input.nextLine();
			}
			
			input.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("Plik z labelami nie został znaleziony");
			e.printStackTrace();
		}
		
		//Pobieranie komentarzy
		File commentsFile = new File("Comments.txt");
		try {
			Scanner input = new Scanner(commentsFile);
			
			for(int i = 0; i < comments; i++) {
				commentNames[i] = input.nextLine();
			}
			
			input.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("Plik z komentarzami nie został znaleziony");
			e.printStackTrace();
		}
	}
	
	private String generateClass(int name, int commentLabelIdx) {
		String result = "";
		
		result += RDFSyntax.rdfsClass + classNames[name] + "\"" + ">\n\t";
		
		result += RDFSyntax.rdfsLabel + "\n\t\t";
		result += labelNames[commentLabelIdx] + "\n\t";
		result += RDFSyntax.rdfsLabelEnding + "\n\t";
		
		result += RDFSyntax.rdfsComment + "\n\t\t";
		result += commentNames[commentLabelIdx] + "\n\t";
		result += RDFSyntax.rdfsCommentEnding + "\n";
		
		result += RDFSyntax.rdfsClassEnding;
		
		classesOnLevel.get(0).add( classNames[name] );
		usedClasses.add(classNames[name]);
		
		return result;
	}
	
	private String generateSubclass(int name, int commentLabelIdx, String inheritedClass, int currentLvl) {
		String result = "";
		
		result += RDFSyntax.rdfsClass + subclassNames[name] + "\"" + ">\n\t";
		
		result += RDFSyntax.rdfsSubClass + inheritedClass + "\"/>\n\t";
		
		result += RDFSyntax.rdfsLabel + "\n\t\t";
		result += labelNames[commentLabelIdx] + "\n\t";
		result += RDFSyntax.rdfsLabelEnding + "\n\t";
		
		result += RDFSyntax.rdfsComment + "\n\t\t";
		result += commentNames[commentLabelIdx] + "\n\t";
		result += RDFSyntax.rdfsCommentEnding + "\n";
		
		result += RDFSyntax.rdfsClassEnding;
		
		classesOnLevel.get(currentLvl).add(subclassNames[name]);
		usedSubclasses.get(currentLvl).add(subclassNames[name]);
		
		return result;
	}
	
	//Pomocnicza funkcja generujaca jedna trojke
	private String generateOneTriple(int predicate, int value) {
		String result = "<" + namespaceName + ":"
				+ predicateNames[predicate] + ">"
				+ valueNames[value] + "</"
				+ namespaceName + ":"
				+ predicateNames[predicate] + ">";
		
		usedPredicates.add(predicateNames[predicate]);
		
		return result;
	}
	
	private String generateTriple(String className, String name) {
		String result = "";
		Random r = new Random();
		
		int numberOfValues = r.nextInt(maxTriplesInOneSubject) + 1;
		
		result += "<" + className + " " + RDFSyntax.rdfID + name + "\">\n";
		
		for(int i = 0; i < numberOfValues; i++) {
			result += "\t" + generateOneTriple( r.nextInt(predicates), r.nextInt(values)) + "\n";
		}
		
		result += "</" + className + ">";
		
		usedSubjects.add(name);
		
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
		
		int addedTriples = 0;
		int addedSubjects = 0;
		int addedClasses = 0;
		
		//Naglowek
		output.println(RDFSyntax.xmlHeader);
		output.println(RDFSyntax.rdfHeader);
		output.println(RDFSyntax.rdfNamespace);
		output.println(RDFSyntax.rdfsNamespace);
		output.println(RDFSyntax.xmlNamespace + namespaceName + "=\"" + namespaceURI + "#\">");
		
		//Generowanie klas
		
		int usedValueAndCommentNames = 0;
		
		for(int i = 0; i < numberOfClasses; i++) {
			output.println(generateClass(i, usedValueAndCommentNames));
			usedValueAndCommentNames++;
			addedClasses++;
			availableClasses.add(classNames[i]);
		}
		
		//Generowanie podklas
		int usedSubclassNames = 0;
		for (int lvl = 0; lvl < maxInheritLevel - 1; lvl++) {
			for (int i = 0; i < classesOnLevel.get(lvl).size(); i++) {

				Random r = new Random();
				int subclassesNum = r.nextInt(maxNumberOfSubclasses) + 1;
				for (int j = 0; j < subclassesNum; j++) {
					output.println(generateSubclass(usedSubclassNames,
							usedValueAndCommentNames, classesOnLevel.get(lvl).get(i), lvl+1));
					usedValueAndCommentNames++;
					addedClasses++;
					usedSubclassNames++;
					availableClasses.add(subclassNames[usedSubclassNames]);
				}

			}
		}
		//Generowanie trojek
		Iterator<String> itr = availableClasses.iterator();
		int maxNumberOfInstancesOfOneClass = maxNumberOfTriples / availableClasses.size();
		if(maxNumberOfInstancesOfOneClass < 1)
			maxNumberOfInstancesOfOneClass = 1;
		
		while(itr.hasNext()) {
			String className = itr.next();
			Random r = new Random();
			int instances = r.nextInt(maxNumberOfInstancesOfOneClass) + 1;
			for(int i = 0; i < instances; i++) {
				output.println(generateTriple(className, subjectNames[addedTriples]));
				addedTriples++;
				
				if(addedTriples >= maxNumberOfTriples)
					break;
			}
			
			if(addedTriples >= maxNumberOfTriples)
				break;
		}
		
		//Zamkniecie rdf-a
		output.println(RDFSyntax.rdfEnding);
		
		//Zamkniecie pliku
		output.close();
		
		report = "Wygenerowano " + addedTriples + " trojek, " + addedSubjects + " podmiotow, " + addedClasses + " klas.";
	}
	
	public void generate() {
		if(checkData()) {
			
			generateDictionary();
			
			getDictionary();
			
			generateRDF();
			
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
	public List<String> getUsedSubjects() {
		return usedSubjects;
	}

	@Override
	public List<String> getUsedPredicates() {
		return usedPredicates;
	}

	@Override
	public List<Pair<String, String>> getSubjectAndPredicateFromLastLevel() {
		return null;
	}

	@Override
	public List<String> getUsedClasses() {
		return usedClasses;
	}

	@Override
	public List<List<String>> getUsedSubclasses() {
		return usedSubclasses;
	}
 
}
