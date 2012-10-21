package pl.mwojciec.app;

import pl.mwojciec.generator.classes.SimpleRDFGenerator;

public class App 
{
    public static void main( String[] args )
    {
    	
        System.out.println( "Projekt Inżynierski" );
        
        SimpleRDFGenerator generator = new SimpleRDFGenerator();
        generator.setNamespaceName("zespoly");
        generator.setNamespaceURI("http://www.mwojciec.pl");
        generator.setNumberOfTriples(1000);
        generator.setNumberOfSubjects(120);
        generator.setNumberOfPredicates(70);
        generator.setNumberOfValues(1000);
        generator.setMaxTriplesForSubject(30);
        generator.generate();
        System.out.println(generator.getReport());
        
    }
}
