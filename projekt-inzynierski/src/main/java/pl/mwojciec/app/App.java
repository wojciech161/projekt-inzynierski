package pl.mwojciec.app;

import pl.mwojciec.test.classes.executor.TestExecutor;

public class App 
{
    public static void main( String[] args )
    {
        TestExecutor tester = new TestExecutor();
        tester.generateTriples();
        tester.executeTest();
        tester.generateReport();
        
        System.out.println( tester.getReport() );
    }
}
