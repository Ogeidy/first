package main.java.com.vkbigdata.vkdataloader.tests;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestRunner {
	
	public static void main(String[] args) {
		
		System.out.println("## Starting tests:");
		Result result = JUnitCore.runClasses(TestVkApi.class,
				TestVkConfig.class,TestVkDataLoader.class,TestVkPrint.class);
		
		System.out.println("------------------");
		System.out.println("Result: " + result.wasSuccessful());
		System.out.println("Total number of tests " + result.getRunCount());
		System.out.println("Total number of tests failed " + result.getFailureCount());
		
		for(Failure failure : result.getFailures())
		{	
			System.out.println(" Fail: " + failure.getTestHeader());
			System.out.println(" Message: " + failure.getMessage());
		}	
		
	}

}
