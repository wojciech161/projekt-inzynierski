package pl.mwojciec.helpers;

public class Triple {
	public String subject;
	public String predicate;
	public String object;
	
	public Triple() {
		
	}
	
	public Triple(String sub, String pred, String obj) {
		subject = sub;
		predicate = pred;
		object = obj;
	}
}
