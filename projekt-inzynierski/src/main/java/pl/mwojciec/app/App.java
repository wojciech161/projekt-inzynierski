package pl.mwojciec.app;

import pl.mwojciec.executor.TestExecutor;

public class App 
{
    public static void main( String[] args )
    {
    	if(args.length == 0) {
        	System.out.println("Test execution started.");
        	
            TestExecutor tester = new TestExecutor();
            tester.generateTriples();
            tester.executeTest();
            tester.generateReport();
            
            System.out.println("Test report:");
            System.out.print( tester.getReport() );        
            System.out.println("Test execution finished");
    	}
    	else {
    		if(args[0].equals("generator")) {
            	System.out.println("Test execution started.");
            	
                TestExecutor tester = new TestExecutor();
                tester.generateTriples();
    		} else if(args[0].equals("test")) {
            	System.out.println("Test execution started.");
            	
                TestExecutor tester = new TestExecutor();
                tester.executeTest();
                tester.generateReport();
                
                System.out.println("Test report:");
                System.out.print( tester.getReport() );        
                System.out.println("Test execution finished");
    		} else if(args[0].equals("help")) {
    			System.out.println("Triplestore Benchmark");
    			System.out.println("Application without arguments executes generation of trples and test");
    			System.out.println("Application with argument generator generates triples");
    			System.out.println("Application with argument test executes test with prevoiusly generated triples");
    			System.out.println("Application with argument help prints this text.");
    		} else {
            	System.out.println("Test execution started.");
            	
                TestExecutor tester = new TestExecutor();
                tester.generateTriples();
                tester.executeTest();
                tester.generateReport();
                
                System.out.println("Test report:");
                System.out.print( tester.getReport() );        
                System.out.println("Test execution finished");
    		}
    		
    	}
    }
}
