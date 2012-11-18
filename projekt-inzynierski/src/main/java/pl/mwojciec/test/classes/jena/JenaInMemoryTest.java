package pl.mwojciec.test.classes.jena;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;

import pl.mwojciec.generator.interfaces.ITriplesGenerator;
import pl.mwojciec.test.interfaces.ITest;

public class JenaInMemoryTest implements ITest {

	private Model model;					//Glowna klasa Jeny, w niej trzymana baza
	private ITriplesGenerator generator;
	private String namespaceName;
	
	//Kontenery do raportow
	private List<String> loadTimeReport = new ArrayList<String>();
	private List<String> memoryUsageReport = new ArrayList<String>();
	private List<String> queryTimeReport = new ArrayList<String>();
	private List<String> queryResults = new ArrayList<String>();
	
	public JenaInMemoryTest() {
		model = ModelFactory.createDefaultModel();
	}
	
	public void loadRepository() {
		
		InputStream in = FileManager.get().open("Triples.rdf");
		
		if ( in == null ) {
			System.err.println("Plik RDF z trojkami nie zostal znaleziony!");
		}
		
		model.read(in, null);
		
	}

	public String getLoadTimeReport() {
		String result = new String();
		
		Iterator<String> iter = loadTimeReport.iterator();
		
		while( iter.hasNext() ) {
			result += iter.next() + "\n";
		}
		
		return result;
	}

	public String getMemoryLoadReport() {
		String result = new String();
		
		Iterator<String> iter = memoryUsageReport.iterator();
		
		while( iter.hasNext() ) {
			result += iter.next() + "\n";
		}
		
		return result;
	}

	public void printRDFFile() {
		
		model.write(System.out);
		
	}
	
	public void executeQueries() {
		// Zapytanie 1 - zapytanie o podmiot
		executeQuery1(namespaceName + "/" + generator.getUsedSubjects().get(0));
		
		//Zapytanie 2 - zapytanie o podmiot i wydobycie z niego informacji
		executeQuery2(namespaceName + "/" + generator.getUsedSubjects().get(0));
		
		//Zapytanie 3 - wylistowanie wszystkich obiektow o podanym predykacie
		executeQuery3(namespaceName + "#" + generator.getUsedPredicates().get(0));
		
		//Zapytanie 4 - Odnalezienie trojki podajac podmiot i predykat
		executeQuery4(namespaceName + "/" + generator.getSubjectAndPredicateFromLastLevel().get(0).first, namespaceName + "#" + 
				generator.getSubjectAndPredicateFromLastLevel().get(0).second);
		
		//Zapytanie 5 - Odnalezienie trojki podajac predykat i wartosc
		//executeQuery5(namespaceName + "#dj2", "byqn3");
		
		//Zapytanie 6 - Dodanie do podanego podmiotu trojki
		executeQuery6(namespaceName + "/" + generator.getUsedSubjects().get(0));
		
		//Zapytanie 7 - Rekursywne wypisanie wszystkich atrybutow z node'ow podajac predykat
		executeQuery7(namespaceName + "/" + generator.getUsedSubjects().get(0));
	}

	public String getQueryTimeReport(int queryNumber) {
		return queryTimeReport.get(queryNumber);
	}

	public String getqueryResult(int queryNumber) {
		return queryResults.get(queryNumber);
	}

	public String getAllQueriesTimeReport() {
		String result = new String();
		int queryNumber = 1;
		
		Iterator<String> iter = queryTimeReport.iterator();
		
		while( iter.hasNext() ) {
			String resNum = "Zapytanie " + queryNumber + "\n";
			result += resNum + iter.next() + "\n";
			++queryNumber;
		}
		
		return result;
	}

	public void setQueriesFile(File queries) {
		// TODO Auto-generated method stub
		
	}
	
	public void setGenerator(ITriplesGenerator g) {
		generator = g;
	}
	
	public void setNamespaceName(String ns) {
		namespaceName = ns;
	}

	//Prywatne funkcje pomocnicze do zapytan
	
	private void executeQuery1(String resourceName) {
		String result = new String();
		
		result += ("Zapytanie o podmiot " + resourceName + "\n");
		Resource r1 = model.getResource(resourceName);
		result += (r1.toString() + "\n");
		
		queryResults.add(result);
	}
	
	private void executeQuery2(String resourceName) {
		String result = new String();
		
		Resource r = model.getResource(resourceName);
		
		StmtIterator iter = r.listProperties();
		while(iter.hasNext()) {
			Statement currentStatement = iter.nextStatement();
			Resource currentSubject = currentStatement.getSubject();
			Property currentPredicate = currentStatement.getPredicate();
			RDFNode currentObject = currentStatement.getObject();
			result += ( "Obiekt: [" +
						currentSubject.toString() + ", " +
						currentPredicate.toString() + ", " +
						currentObject.toString() + "]\n" );
		}
		
		queryResults.add(result);
	}
	
	private void executeQuery3(String predicateName) {
		String result = new String();
		
		result += ("Wylistowanie trojek posiadajacych predykat " + predicateName + "\n");
		Property propertyForQuery3 = model.createProperty(predicateName);
		ResIterator resIter = model.listResourcesWithProperty(propertyForQuery3);
		if(resIter.hasNext()) {
			while(resIter.hasNext()) {
				Resource foundResource = resIter.nextResource();
				result += ( foundResource.getRequiredProperty(propertyForQuery3).toString() + "\n" );
			}
		}
		else {
			result += ("Brak obiektow o podanej nazwie\n");
		}
		
		queryResults.add(result);
	}
	
	private void executeQuery4(String resourceName, String predicateName) {
		String result = new String();
		
		result += ("Odnalezienie trojki o podmiocie "
				+ resourceName + " oraz predykacie " + predicateName + "\n");
		Resource r4 = model.createResource(resourceName);
		Property propertyForQuery4 = model.createProperty(predicateName);
		Selector selectorQuery4 = new SimpleSelector(r4, propertyForQuery4, (RDFNode)null);
		StmtIterator iter = model.listStatements(selectorQuery4);
		if (iter.hasNext()) {
			while(iter.hasNext()) {
				Statement currentStatement = iter.nextStatement();
				Resource res = currentStatement.getResource();
				Property prop = currentStatement.getPredicate();
				RDFNode node = currentStatement.getObject();
				result += ("[" + res.toString() + ", " + prop.toString() + ", " + node.toString() + "]\n");
			}
		}
		else {
			result += ("Nie znaleziono trojek o podanych parametrach\n");
		}
		
		queryResults.add(result);
	}
	
	/*private void executeQuery5(String predicateName, String valueName) {
		System.out.println("Odnalezienie trojki o predykacie " + predicateName + 
				" oraz wartosci " + valueName);
		Property propertyForQuery5 = model.createProperty(predicateName);
		Selector selectorQuery5 = new SimpleSelector((Resource)null, propertyForQuery5, valueName);
		StmtIterator iter = model.listStatements(selectorQuery5);
		if (iter.hasNext()) {
			while(iter.hasNext()) {
				Statement currentStatement = iter.nextStatement();
				Resource res = currentStatement.getResource();
				Property prop = currentStatement.getPredicate();
				RDFNode node = currentStatement.getObject();
				System.out.println("[" + res.toString() + ", " + prop.toString() + ", " + node.toString() + "]");
			}
		}
		else {
			System.out.println("Nie znaleziono trojek o podanych parametrach");
		}
	}*/
	
	private void executeQuery6(String resourceName) {
		String result = new String();
		
		result += ("Dodanie do podmiotu " + resourceName + " kolejnej trojki\n");
		
		Resource r = model.getResource(resourceName);

		r.addProperty(model.createProperty("NewProperty"), "NewValue");
		
		result += ("Dodawanie zakonczone pomyslnie\n");
		
		queryResults.add(result);
	}
	
	private void getNodeIdObjects(Resource r, String result) {
		StmtIterator iter = r.listProperties();
		while(iter.hasNext()) {
			Statement currentStatement = iter.nextStatement();
			Resource currentSubject = currentStatement.getSubject();
			Property currentPredicate = currentStatement.getPredicate();
			RDFNode currentObject = currentStatement.getObject();
			if( currentObject.isResource() ) {
				result += ("NodeId [" +
						currentSubject.toString() + ", " +
						currentPredicate.toString() + ", " +
						currentObject.toString() + "]\n");
				Resource rNext = (Resource)currentObject;
				getNodeIdObjects(rNext, result);
			}
			else {
				result += ( "Obiekt: [" +
						currentSubject.toString() + ", " +
						currentPredicate.toString() + ", " +
						currentObject.toString() + "]\n" );
			}
		}
	}
	
	private void executeQuery7(String resourceName) {
		String result = new String();
		
		result += ("Wyswietlenie wszystkich trojek zwiazanych z podmiotem\n");
		Resource r2 = model.getResource(resourceName);
		result += (r2.toString() + "\n");
		getNodeIdObjects(r2, result);
		
		queryResults.add(result);
	}
	
}
