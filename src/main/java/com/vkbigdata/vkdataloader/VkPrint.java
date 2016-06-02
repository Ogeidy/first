package main.java.com.vkbigdata.vkdataloader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Implements printing functions for response from VK server
 * @author Yakov Mamontov
 *
 */
public class VkPrint {
	
	private File outFile;
	
	private File logFile;
	
	private static final Logger log = LogManager.getLogger(VkPrint.class.getSimpleName());
	
	/**
	 * Creates a new <code>VkPrint</code> instance.
	 * @param fileName
	 */
	public VkPrint(String fileName) {
		
		this.outFile = new File(fileName);
		this.logFile = null;
		
	}
	
	/**
	 * Creates a new <code>VkPrint</code> instance.
	 * @param fileName
	 * @param logName
	 */
	public VkPrint(String fileName, String logName) {
		
		this.outFile = new File(fileName);
		this.logFile = new File(logName);
		
	}
	
	/**
	 * Prints <code>String</code> to log file.
	 * @param str
	 * @return
	 */
	public int log(String str) {
		
		DateFormat df = DateFormat.getDateTimeInstance (DateFormat.DEFAULT,DateFormat.DEFAULT);
		Date currentDate = new Date();
		
		System.out.println(df.format(currentDate)+" | "+str);
		
		if (logFile != null) {
		
			BufferedWriter wrtFile = null;
			
			
			try {
				wrtFile = new BufferedWriter(new OutputStreamWriter(
				        new FileOutputStream(logFile), "UTF-8")); //new FileWriter(logFile, true);
			} catch (IOException e) {
				System.out.println("Error: Can't open log file!");
				e.printStackTrace();
				System.exit(1);
			}
			
			try {
				wrtFile.append(df.format(currentDate)+" | "+str+"\n");
				wrtFile.flush();
				wrtFile.close();
			} catch (IOException e) {
				System.out.println("Error: Can't write to file!");
				e.printStackTrace();
				System.exit(1);
			}
			
			return 0;
		}
		
		return 1;
	}
	
	/**
	 * Prints <code>String</code> to file.
	 * @param str
	 * @return
	 */
	public int print(String str) {
		
		BufferedWriter wrtFile = null;
		
		try {
			wrtFile = new BufferedWriter(new OutputStreamWriter(
			        new FileOutputStream(outFile), "UTF-8")); //new FileWriter(outFile, true);
		} catch (IOException e) {
			System.out.println("Error: Can't open file!");
			e.printStackTrace();
			System.exit(1);
		}
		
		try {
			wrtFile.append(str);
			wrtFile.flush();
			wrtFile.close();
		} catch (IOException e) {
			System.out.println("Error: Can't write to file!");
			e.printStackTrace();
			System.exit(1);
		}
		
		return 0;
	}
	
	/**
	 * Prints <code>result</code> to file.
	 * @param result
	 * @return
	 */
	public int printResult(String result) {
		
		BufferedWriter wrtFile = null;
		JSONParser parser = new JSONParser();
		JSONObject resJson = null;
		JSONArray array = null;
		
		try {
			wrtFile = new BufferedWriter(new OutputStreamWriter(
			        new FileOutputStream(outFile), "UTF-8")); //new FileWriter(outFile, true);
		} catch (IOException e) {
//			System.out.println("Error: Can't open file!");
//			e.printStackTrace();
			log.fatal("Can't open file!");
			System.exit(1);
		}
		
		try {
			resJson = (JSONObject)parser.parse(result);
			array = (JSONArray)((JSONObject)resJson.get("response")).get("items");
			
		} catch (ParseException e) {
			//System.out.println("Error: Can't parse the response!");
			//e.printStackTrace();
			log.warn("Can't parse the response!");
			return 1;
			//System.exit(1);
		}
		
		//System.out.println(result);
//		log("Count: "+((JSONObject)resJson.get("response")).get("count"));
//		log("Number of items:" + array.size());
		log.info("Print results, Count: "+((JSONObject)resJson.get("response")).get("count")
				+ ", Number of items:" + array.size());
		
		try {
			wrtFile.append("Count: "+((JSONObject)resJson.get("response")).get("count")+"\n");
			wrtFile.append("Number of items:" + array.size()+"\n");
			wrtFile.append("Items:"+"\n");
			for (int i=0; i<array.size();i++){
				wrtFile.append(array.get(i).toString()+"\n");
			}
			wrtFile.flush();
			wrtFile.close();
		} catch (IOException e) {
			System.out.println("Error: Can't write to file!");
			e.printStackTrace();
			System.exit(1);
		}
		
		return 0;
	}
}
