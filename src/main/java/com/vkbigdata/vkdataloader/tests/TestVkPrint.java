package main.java.com.vkbigdata.vkdataloader.tests;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.Date;

import org.junit.Test;
import main.java.com.vkbigdata.vkdataloader.VkPrint;

public class TestVkPrint {

	private static String OUT_FILE = "TestOutFile.txt";
	private static String LOG_FILE = "TestLogFile.log";
	private VkPrint prnt;
	
	public TestVkPrint() {
		
		prnt = new VkPrint(OUT_FILE, LOG_FILE);
	}
	
	@Test
	public void testLog() {
		
		String str = "Testing logging";
		DateFormat df = DateFormat.getDateTimeInstance (DateFormat.DEFAULT,DateFormat.DEFAULT);
		String testString = df.format(new Date()) + " | " + str;
		prnt.log(str);
		
		File logFile = new File(LOG_FILE);
		try {
			BufferedReader logRd = new BufferedReader(new InputStreamReader(new FileInputStream(logFile), "UTF-8"));
			
			String result = logRd.readLine();
			
//			System.out.println("result:" + result);
//			System.out.println("testString:" + testString);
			
			logRd.close();
			
			assertEquals(testString, result);
			
		} catch (FileNotFoundException e) {
			fail("Error: Logging file not found");
			e.printStackTrace();
		} catch (IOException e) {
			fail("Error: Can't read logging file");
			e.printStackTrace();
		}
	}
	
	@Test
	public void testPrint() {
		
		String testString = "Testing printing";
		prnt.print(testString);
		
		File outFile = new File(OUT_FILE);
		try {
			BufferedReader outRd = new BufferedReader(new InputStreamReader(new FileInputStream(outFile), "UTF-8"));
			
			String result = outRd.readLine();
			
//			System.out.println("result:" + result);
//			System.out.println("testString:" + testString);
			
			outRd.close();
			
			assertEquals(testString, result);
			
		} catch (FileNotFoundException e) {
			fail("Error: Logging file not found");
			e.printStackTrace();
		} catch (IOException e) {
			fail("Error: Can't read logging file");
			e.printStackTrace();
		}
	}
	
	@Test
	public void testPrintResultOk() {

		String testString = "{\"response\":{\"count\":514,\"items\":[{\"id\":6492,\"first_name\":\"Андрей\","
				+ "\"last_name\":\"Рогозов\",\"sex\":2,\"bdate\":\"11.11\"}]}}";
		
		prnt.printResult(testString);
		
		File outFile = new File(OUT_FILE);
		try {
			BufferedReader outRd = new BufferedReader(new InputStreamReader(new FileInputStream(outFile), "UTF-8"));
			
			String result;
			
			if ((result = outRd.readLine()) == null) {
				fail("Fail read log file");
			} else {
				if (!result.equals("Count: 514")) {
					fail("Fail in log file, expected:Count: 514, actual:" + result);
				}
			}
			
			if ((result = outRd.readLine()) == null) {
				fail("Fail read log file");
			} else {
				if (!result.equals("Number of items:1")) {
					fail("Fail in log file, expected:Number of items:1, actual:" + result);
				}
			}
			
			if ((result = outRd.readLine()) == null) {
				fail("Fail read log file");
			} else {
				if (!result.equals("Items:")) {
					fail("Fail in log file, expected:Items:, actual:" + result);
				}
			}
			
			testString = "{\"bdate\":\"11.11\",\"sex\":2,"
					+ "\"last_name\":\"Рогозов\",\"id\":6492,\"first_name\":\"Андрей\"}";
			if ((result = outRd.readLine()) == null) {
				fail("Fail read log file");
			} else {
				if (!result.equals(testString)) {
					fail("Fail in log file, expected:" + testString + ", actual:" + result);
				}
			}
			
			System.out.println("result:" + result);
			System.out.println("testString:" + testString);
			
			outRd.close();
			
			assertEquals(testString, result);
			
		} catch (FileNotFoundException e) {
			fail("Error: Logging file not found");
			e.printStackTrace();
		} catch (IOException e) {
			fail("Error: Can't read logging file");
			e.printStackTrace();
		}
	}
	
	@Test
	public void testPrintResultFail() {

		String testString = "{\"response\":{\"count\":514,\"items\":[{\"id\":6492,\"first_name\":\"Андрей\","
				+ "\"last_name\":\"Рогозов\",\"sex\":2,\"bdate\":\"11.11}]}}";
		
		assertEquals(1, prnt.printResult(testString));
		
	}
}
