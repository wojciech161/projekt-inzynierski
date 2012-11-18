package pl.mwojciec.test.classes.jena;

import java.io.File;
import java.io.InputStream;

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
		// TODO Auto-generated method stub
		return null;
	}

	public String getMemeoryLoadReport() {
		// TODO Auto-generated method stub
		return null;
	}

	public void printRDFFile() {
		
		model.write(System.out);
		
	}
	
	public void executeQueries() {
		// Zapytanie 1 - zapytanie o podmiot
		if(generator != null)
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
	}

	public void getQueryTimeReport(int queryNumber) {
		// TODO Auto-generated method stub
		
	}

	public void getqueryResult(int queryNumber) {
		// TODO Auto-generated method stub
		
	}

	public void getAllQueriesTimeReport() {
		// TODO Auto-generated method stub
		
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
		System.out.println("Zapytanie o podmiot " + resourceName);
		Resource r1 = model.getResource(resourceName);
		System.out.println(r1.toString());
	}
	
	private void executeQuery2(String resourceName) {
		System.out.println("Wyswietlenie wszystkich obiektow podmiotu rekurencyjnie po calym drzewie");
		Resource r2 = model.getResource(resourceName);
		System.out.println(r2.toString());
		getNodeIdObjects(r2);
	}
	
	private void executeQuery3(String predicateName) {
		System.out.println("Wylistowanie trojek posiadajacych predykat " + predicateName);
		Property propertyForQuery3 = model.createProperty(predicateName);
		ResIterator resIter = model.listResourcesWithProperty(propertyForQuery3);
		if(resIter.hasNext()) {
			while(resIter.hasNext()) {
				Resource foundResource = resIter.nextResource();
				System.out.println( foundResource.getRequiredProperty(propertyForQuery3).toString() );
			}
		}
		else {
			System.out.println("Brak obiektow o podanej nazwie");
		}
	}
	
	private void executeQuery4(String resourceName, String predicateName) {
		System.out.println("Odnalezienie trojki o podmiocie "
				+ resourceName + " oraz predykacie " + predicateName);
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
				System.out.println("[" + res.toString() + ", " + prop.toString() + ", " + node.toString() + "]");
			}
		}
		else {
			System.out.println("Nie znaleziono trojek o podanych parametrach");
		}
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
		System.out.println("Dodanie do podmiotu " + resourceName + " kolejnej trojki");
		
		Resource r = model.getResource(resourceName);

		r.addProperty(model.createProperty("NewProperty"), "NewValue");
		
		System.out.println("Dodawanie zakonczone pomyslnie");
	}
	
	private void getNodeIdObjects(Resource r) {
		StmtIterator iter = r.listProperties();
		while(iter.hasNext()) {
			Statement currentStatement = iter.nextStatement();
			Resource currentSubject = currentStatement.getSubject();
			Property currentPredicate = currentStatement.getPredicate();
			RDFNode currentObject = currentStatement.getObject();
			if( currentObject.isResource() ) {
				System.out.println("NodeId [" +
						currentSubject.toString() + ", " +
						currentPredicate.toString() + ", " +
						currentObject.toString() + "]");
				Resource rNext = (Resource)currentObject;
				getNodeIdObjects(rNext);
			}
			else {
				System.out.println( "Obiekt: [" +
						currentSubject.toString() + ", " +
						currentPredicate.toString() + ", " +
						currentObject.toString() + "]" );
			}
		}
	}
	
}
