package main.java.com.vkbigdata.vkdataloader;

import java.io.File;
import java.util.HashMap;

import org.json.simple.JSONObject;

public class VkCityDataBase {
	
	HashMap<String, String> cities;
	
	private File cityFile;
	
	public VkCityDataBase(String fileName) {
		
		this.cityFile = new File(fileName);
		cities = new HashMap<String, String>();
		
	}
	
	public int checkCity(JSONObject checkingJson) {
		
		return 0;
	}

}
