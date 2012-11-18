package pl.mwojciec.test.classes.sesame;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;

import pl.mwojciec.generator.interfaces.ITriplesGenerator;
import pl.mwojciec.test.interfaces.ITest;

public class SesameInMemoryTest implements ITest {

	private Repository repository;
	private ITriplesGenerator generator;
	private String namespaceName;
	
	//Kontenery do raportow
	private List<String> loadTimeReport = new ArrayList<String>();
	private List<String> memoryUsageReport = new ArrayList<String>();
	private List<String> queryTimeReport = new ArrayList<String>();
	private List<String> queryResults = new ArrayList<String>();
	
	public void loadRepository() {
		
		repository = new SailRepository(new MemoryStore());
		
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

	public void executeQueries() {
		// Zapytanie 1 - zapytanie o podmiot
		executeQuery1(generator.getUsedSubjects().get(0));
		//Zapytanie 2 - zapytanie o podmiot i wydobycie z niego informacji
		executeQuery2(generator.getUsedSubjects().get(0));
		//Zapytanie 3 - wylistowanie wszystkich obiektow o podanym predykacie
		executeQuery3("mw:" + generator.getUsedPredicates().get(0));
		//Zapytanie 4 - Odnalezienie trojki podajac podmiot i predykat
		executeQuery4(generator.getSubjectAndPredicateFromLastLevel().get(0).first, "mw:" + generator.getSubjectAndPredicateFromLastLevel().get(0).second);
		//Zapytanie 5 - Odnalezienie trojki podajac predykat i wartosc
		//executeQuery5("mw:przyklad", "wartosc");
		
		//Zapytanie 6 - Dodanie do podanego podmiotu trojki
		//executeQuery6();
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

	@Override
	public void setNamespaceName(String ns) {
		namespaceName = ns;
	}

	@Override
	public void setGenerator(ITriplesGenerator g) {
		generator = g;
	}
	
	private void executeQuery1(String subjectName) {
		String resultStr = "Zapytanie 1: zapytanie o podmiot\n";
		
		try {
			RepositoryConnection con = repository.getConnection();
			try {
				String queryString = "SELECT x FROM {x} y {z} WHERE x LIKE \"*" + subjectName + "\" USING NAMESPACE mw = <" + namespaceName + "#>";
				TupleQuery query = con.prepareTupleQuery(QueryLanguage.SERQL, queryString);
				TupleQueryResult result = query.evaluate();
				try {
					while(result.hasNext()) {
						BindingSet bindingSet = result.next();
						Value valueOfX = bindingSet.getValue("x");
						resultStr += ("[" + valueOfX.toString() + "]\n");
					}
				}
				finally {
					result.close();
				}
			}
			finally {
				con.close();
			}
		}
		catch(OpenRDFException e) {
			e.printStackTrace();
		}
		
		queryResults.add(resultStr);
	}
	
	private void executeQuery2(String subjectName) {
		String resultStr = "Zapytanie 2: zapytanie o podmiot i wydobycie z niego informacji\n";
		
		try {
			RepositoryConnection con = repository.getConnection();
			try {
				String queryString = "SELECT x, y, z FROM {x} y {z} WHERE x LIKE \"*" + subjectName + "\" USING NAMESPACE mw = <" + namespaceName + "#>";
				TupleQuery query = con.prepareTupleQuery(QueryLanguage.SERQL, queryString);
				TupleQueryResult result = query.evaluate();
				try {
					while(result.hasNext()) {
						BindingSet bindingSet = result.next();
						Value valueOfX = bindingSet.getValue("x");
						Value valueOfY = bindingSet.getValue("y");
						Value valueOfZ = bindingSet.getValue("z");
						resultStr += ("[" + valueOfX.toString() + ", " + valueOfY.toString() + ", " + valueOfZ.toString() + "]\n");
					}
				}
				finally {
					result.close();
				}
			}
			finally {
				con.close();
			}
		}
		catch(OpenRDFException e) {
			e.printStackTrace();
		}
		
		queryResults.add(resultStr);
	}
	
	private void executeQuery3(String predicateName) {
		String resultStr = "Zapytanie 3: wylistowanie wszystkich obiektow o podanym predykacie\n";
		
		try {
			RepositoryConnection con = repository.getConnection();
			try {
				String queryString = "SELECT x, y FROM {x} " + predicateName + " {y} USING NAMESPACE mw = <" + namespaceName + "#>";
				TupleQuery query = con.prepareTupleQuery(QueryLanguage.SERQL, queryString);
				TupleQueryResult result = query.evaluate();
				try {
					while(result.hasNext()) {
						BindingSet bindingSet = result.next();
						Value valueOfX = bindingSet.getValue("x");
						Value valueOfY = bindingSet.getValue("y");
						resultStr += (valueOfX.toString() + " " + valueOfY.toString() + "\n");
					}
				}
				finally {
					result.close();
				}
			}
			finally {
				con.close();
			}
		}
		catch(OpenRDFException e) {
			e.printStackTrace();
		}
		
		queryResults.add(resultStr);
	}
	
	private void executeQuery4(String subjectName, String predicateName) {
		String resultStr = "Zapytanie 4: Odnalezienie trojki podajac podmiot i predykat\n";
		
		try {
			RepositoryConnection con = repository.getConnection();
			try {
				String queryString = "SELECT x, y FROM {x} " + predicateName + " {y} WHERE x LIKE \"*" + subjectName + "\" USING NAMESPACE mw = <" + namespaceName + "#>";
				TupleQuery query = con.prepareTupleQuery(QueryLanguage.SERQL, queryString);
				TupleQueryResult result = query.evaluate();
				try {
					while(result.hasNext()) {
						BindingSet bindingSet = result.next();
						Value valueOfX = bindingSet.getValue("x");
						Value valueOfY = bindingSet.getValue("y");
						resultStr += (valueOfX.toString() + " " + valueOfY.toString() + "\n");
					}
				}
				finally {
					result.close();
				}
			}
			finally {
				con.close();
			}
		}
		catch(OpenRDFException e) {
			e.printStackTrace();
		}
		
		queryResults.add(resultStr);
	}
	
	private void executeQuery5(String predicateName, String valueName) {
		String resultStr = "Zapytanie 5: Odnalezienie trojki podajac predykat i wartosc\n";
		
		try {
			RepositoryConnection con = repository.getConnection();
			try {
				String queryString = "SELECT x FROM {x} " + predicateName + " {y} WHERE y LIKE \"" + valueName + "\" USING NAMESPACE mw = <" + namespaceName + "#>";
				TupleQuery query = con.prepareTupleQuery(QueryLanguage.SERQL, queryString);
				TupleQueryResult result = query.evaluate();
				try {
					while(result.hasNext()) {
						BindingSet bindingSet = result.next();
						Value valueOfX = bindingSet.getValue("x");
						resultStr += (valueOfX.toString() + "\n");
					}
				}
				finally {
					result.close();
				}
			}
			finally {
				con.close();
			}
		}
		catch(OpenRDFException e) {
			e.printStackTrace();
		}
		
		queryResults.add(resultStr);
	}
	
	private void executeQuery6() {
		
	}

}
