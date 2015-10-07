import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class VkDataLoader {
	
	private static String APP_ID = "5075749";
	
	private static String UNIVERSITY_ID = "56";
	
	private static String ACCESS_TOKEN = "c9a960b33ff40c89132692cc27eeeb8ee9788c170180b699ddf153cf0d01ce760a884e1d5db229d102bde";
			//"a08474e90bf43ac5b5a9b1687acbd7d79fad5ca00c82bae3f53889265d98541478cf8a59651fdc610989e";
			//"58adc3edf01eb80d8dbb86e0441cba1871c4405614192fd64fcd2364ae5cbea175da655e54ed46ebcad8b";
	
	public static void main(String[] args) throws IOException {
		
		File file = new File("output.txt");
		FileWriter wrt = new FileWriter(file);
		VkApi vk = new VkApi(APP_ID, ACCESS_TOKEN);
		String result = null;
		JSONParser parser = new JSONParser();
		JSONObject resJson = new JSONObject();
		JSONArray array = new JSONArray();
		
		// https request 
		
		result = vk.sendReqS("users.search", "university="+UNIVERSITY_ID+"&count=100");
		//result = vk.sendReqS("users.search", "university=UNIVERSITY_ID&fields=sex,bdate,country,city,home_town&offset=999");	
		
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
		wrt.append(result+"\n");
		System.out.println("Count: "+((JSONObject)resJson.get("response")).get("count"));
		wrt.append("Count: "+((JSONObject)resJson.get("response")).get("count")+"\n");
		System.out.println("Number of items:" + array.size());
		wrt.append("Number of items:" + array.size()+"\n");
		wrt.append("Items:"+"\n");
		for (int i=0; i<array.size();i++){
			wrt.append(i+": "+array.get(i).toString()+"\n");
		}
		
		wrt.flush();
		
		wrt.close();
		
	}
}
