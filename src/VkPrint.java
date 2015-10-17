import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
	
	/**
	 * Creates a new <code>VkPrint</code> instance.
	 * @param fileName
	 */
	public VkPrint(String fileName) {
		
		this.outFile = new File(fileName);
		
	}
	
	/**
	 * Prints <code>String</code> to file.
	 * @param str
	 * @return
	 */
	public int print(String str) {
		
		FileWriter wrtFile = null;
		
		try {
			wrtFile = new FileWriter(outFile, true);
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
		
		FileWriter wrtFile = null;
		JSONParser parser = new JSONParser();
		JSONObject resJson = new JSONObject();
		JSONArray array = new JSONArray();
		
		try {
			wrtFile = new FileWriter(outFile, true);
		} catch (IOException e) {
			System.out.println("Error: Can't open file!");
			e.printStackTrace();
			System.exit(1);
		}
		
		try {
			resJson = (JSONObject)parser.parse(result);
			array = (JSONArray)((JSONObject)resJson.get("response")).get("items");
			
		} catch (ParseException e) {
			System.out.println("Error: Can't parse the response!");
			e.printStackTrace();
			System.exit(1);
		}
		
		System.out.println(result);
		System.out.println("Count: "+((JSONObject)resJson.get("response")).get("count"));
		System.out.println("Number of items:" + array.size());
		
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
