import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class VkDataLoader {
	
	public static void main(String[] args) throws IOException {
		
		File file = new File("output.txt");
		FileWriter wrt = new FileWriter(file);
		VkConfig conf = new VkConfig("config.txt");
		VkApi vk = new VkApi(conf.APP_ID, conf.ACCESS_TOKEN);
		String result = null;
		JSONParser parser = new JSONParser();
		JSONObject resJson = new JSONObject();
		JSONArray array = new JSONArray();
		
		
		// https request 
		
//		result = vk.sendReqS("users.search", "university="+UNIVERSITY_ID+"&count=100");
		result = vk.sendReqS("users.search", "q=Leonid%20Borisevich&university="+conf.UNIVERSITY_ID+"&fields=sex,bdate,country,city,home_town&count=100");	
		
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
