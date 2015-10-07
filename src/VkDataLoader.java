import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class VkDataLoader {
	
	private static String APP_ID;// = "5075749";
	
	private static String ACCESS_TOKEN;// = "164a5f0c4feee1ea77a594b26e3cc815fbcf46cf508f45a4961bdfe2362a2022a2ac98ff1986651687f22";
			//"a08474e90bf43ac5b5a9b1687acbd7d79fad5ca00c82bae3f53889265d98541478cf8a59651fdc610989e";
			//"58adc3edf01eb80d8dbb86e0441cba1871c4405614192fd64fcd2364ae5cbea175da655e54ed46ebcad8b";
	
	private static String UNIVERSITY_ID;// = "56";
	
	public static void main(String[] args) throws IOException {
		
		File file = new File("output.txt");
		File config = new File("config.txt");
		FileWriter wrt = new FileWriter(file);
		BufferedReader cfgRd = new BufferedReader(new FileReader(config.getAbsoluteFile()));	
		//-------------------------------------------------------
		// Read config file
		String s;
		int it = 0;
		while ((s = cfgRd.readLine()) != null) {
			System.out.println(s.substring(s.indexOf("=")+2));
			if (it == 0) {
				APP_ID = s.substring(s.indexOf("=")+1);
				it++;
			} else if(it == 1) {
				ACCESS_TOKEN = s.substring(s.indexOf("=")+2);
				it++;
			} else if ( it == 2) {
				UNIVERSITY_ID = s.substring(s.indexOf("=")+2);
				it++;
			}
		}
		cfgRd.close();
		//-------------------------------------------------------
		VkApi vk = new VkApi(APP_ID, ACCESS_TOKEN);
		String result = null;
		JSONParser parser = new JSONParser();
		JSONObject resJson = new JSONObject();
		JSONArray array = new JSONArray();
		
		
		// https request 
		
//		result = vk.sendReqS("users.search", "university="+UNIVERSITY_ID+"&count=100");
		result = vk.sendReqS("users.search", "university="+UNIVERSITY_ID+"&fields=sex,bdate,country,city,home_town&count=100");	
		
		try {
			resJson = (JSONObject)parser.parse(result);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			array = (JSONArray)((JSONObject)resJson.get("response")).get("items");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//printing
		System.out.println(result);
//		wrt.append(result+"\n");
		System.out.println("Count: "+((JSONObject)resJson.get("response")).get("count"));
		wrt.append("Count: "+((JSONObject)resJson.get("response")).get("count")+"\n");
		System.out.println("Number of items:" + array.size());
		wrt.append("Number of items:" + array.size()+"\n");
		wrt.append("Items:"+"\n");
		for (int i=0; i<array.size();i++){
			wrt.append(array.get(i).toString()+"\n");
		}
		
		wrt.flush();
		
		wrt.close();
		
	}
}
