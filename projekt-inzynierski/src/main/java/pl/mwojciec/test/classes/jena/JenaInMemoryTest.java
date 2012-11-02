package pl.mwojciec.test.classes.jena;

import java.io.File;
import java.io.InputStream;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

import pl.mwojciec.test.interfaces.ITest;

public class JenaInMemoryTest implements ITest {

	private Model model;					//Glowna klasa Jeny, w niej trzymana baza
	
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
		// TODO Auto-generated method stub
		
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

}
