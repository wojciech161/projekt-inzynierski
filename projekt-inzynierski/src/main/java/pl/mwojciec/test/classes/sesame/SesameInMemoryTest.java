package pl.mwojciec.test.classes.sesame;

import java.io.File;
import java.io.IOException;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;

import pl.mwojciec.test.interfaces.ITest;

public class SesameInMemoryTest implements ITest {

	Repository repository;
	
	public void loadRepository() {
		
		repository = new SailRepository(new MemoryStore());
		
		//2 funkcjonalnosci inmemory - mozna new MemoryStore(new File(sciezka do katalogu)) i wtedy kopia repo z pamieci trzymana w katalogu
		
		try {
			repository.initialize();
		} catch (RepositoryException e) {
			System.out.println("Nie udalo sie zainicjalizowac repozytorium sesame");
			e.printStackTrace();
		}
		
		File rdfFile = new File("Triples.rdf");
		String baseURI = "http://www.mwojciec.pl#";
		
		try {
			RepositoryConnection con = repository.getConnection();
			
			try {
				con.add(rdfFile, baseURI, RDFFormat.RDFXML);
				
				System.out.println("Repozytorium pomyslnie zaladowane!");
			} catch (RDFParseException e) {
				System.out.println("Blad przy parsowaniu pliku RDF");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("Nie moge uzyskac dostepu do pliku!");
				e.printStackTrace();
			}
			finally {
				con.close();
			}
			
		} catch (RepositoryException e) {
			System.out.println("Nie udalo sie polaczyc z repozytorium sesame");
			e.printStackTrace();
		}
		
	}

	public String getLoadTimeReport() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getMemeoryLoadReport() {
		// TODO Auto-generated method stub
		return null;
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
