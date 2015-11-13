package main.java.com.vkbigdata.vkdataloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * 
 * @author Yakov Mamontov
 *
 */
public class VkCityDataBase {
	
	private boolean DBG = true;
	private String TAG = "[VkCityDataBase]";
	private HashMap<String, String> cities;
	private LinkedList<BufCity> bufCities;
	private JSONArray jsArray;
	
	private File ctFile;
	private BufferedReader ctRd;
	private FileWriter ctWrt;
	private VkApi vk;
	private VkPrint prnt;
	
	private class BufCity {
		public String id;
		public String title;
		public String countryId;
		public String countryTitle;
		
		public BufCity (String id, String title, String countryId, String countryTitle) {
			this.id = id;
			this.title = title;
			this.countryId = countryId;
			this.countryTitle = countryTitle;
		}
	}
	
	/**
	 * 
	 * @param fileName
	 * @param vkApi
	 * @param vkPrnt
	 */
	public VkCityDataBase(String fileName, VkApi vkApi, VkPrint vkPrnt) {
		
		ctFile = new File(fileName);
		vk = vkApi;
		prnt = vkPrnt;
		cities = new HashMap<String, String>();
		jsArray = new JSONArray();
		bufCities = new LinkedList<>();
		readBase();
	}
	
	/**
	 * 
	 * @param checkingJson
	 * @return
	 */
	public int checkCity(JSONObject checkingJson) {
		
		boolean isNewCity = false;
		
		jsArray = (JSONArray)((JSONObject)checkingJson.get("response")).get("items");
		for (int i=0; i < jsArray.size(); i++) {
			
			JSONObject tmpJson = (JSONObject)jsArray.get(i);
			JSONObject cityJson = (JSONObject)tmpJson.get("city");
			if (cityJson != null) {
				
				String key = cityJson.get("id").toString();
				if (!cities.containsKey(key) && !key.equals("0")) {
					
					String title = cityJson.get("title").toString();
					JSONObject countryJson = (JSONObject)tmpJson.get("country");
					if (countryJson != null) {
						String cntrId = countryJson.get("id").toString();
						String cntrTitle = countryJson.get("title").toString();
						bufCities.add(new BufCity(key, title, cntrId, cntrTitle));
					}
					else {
						bufCities.add(new BufCity(key, title, null, null));
					}
					isNewCity = true;
				}
			}
			
			JSONArray schoolsArray = (JSONArray)tmpJson.get("schools");
			if (schoolsArray != null) {
				for (int j = 0; j < schoolsArray.size(); j++) {
					String schoolCity = ((JSONObject)schoolsArray.get(j)).get("city").toString();
					if (schoolCity != null && !cities.containsKey(schoolCity) && !schoolCity.equals("0")) {
						String shoolCountryId = ((JSONObject)schoolsArray.get(j)).get("country").toString();
						bufCities.add(new BufCity(schoolCity, null, shoolCountryId, null));
						isNewCity = true;
					}
				}
			}
			
			JSONArray uniArray = (JSONArray)tmpJson.get("universities");
			if (uniArray != null) {
				for (int j = 0; j < uniArray.size(); j++) {
					String uniCity = ((JSONObject)uniArray.get(j)).get("city").toString();
					if (uniCity != null && !cities.containsKey(uniCity) && !uniCity.equals("0")) {
						String uniCountryId = ((JSONObject)uniArray.get(j)).get("country").toString();
						bufCities.add(new BufCity(uniCity, null, uniCountryId, null));
						isNewCity = true;
					}
				}
			}
			
		}
		
		if (isNewCity) {
			if (!cityDBThread.isAlive()) {
				cityDBThread.start();
			}
			else {
				if (DBG) prnt.log(TAG+" cityDBThread already running!!");
			}
		}
	
		return 0;
	}
	
	Thread cityDBThread = new Thread(){
		
		public void run() {
			
			prnt.log(TAG+" cityDBThread started");
			
			try {
				//ctWrt = new FileWriter(ctFile, true);
				
				JSONObject resJson = new JSONObject();
				JSONArray resArr;
				String result = null;	
				long startTime;
				
				while (!bufCities.isEmpty()) {
					
					ctWrt = new FileWriter(ctFile, true);
					
					String cityTitle = null;
					String cityTitleEn = null;
					String countryTitle = null;
					String regionTitle = null;
					
					BufCity bCt = bufCities.remove();
					
					if (!cities.containsKey(bCt.id)) {
						
						//---Get City Title---
						if (bCt.title == null) {
							synchronized(vk) {
								startTime = System.currentTimeMillis();
								result = vk.sendReq("database.getCitiesById", "city_ids="+bCt.id);
								vk.checkTime(startTime);
							}
							
							resJson = VkDataLoader.parseString(result);
							
							if (resJson != null) {
								if ( !((JSONArray)resJson.get("response")).isEmpty() )
									cityTitle = ((JSONObject)((JSONArray)resJson.get("response")).get(0)).get("title").toString();
								if (DBG) prnt.log(TAG+" cityTitle:"+cityTitle);
							}
							else {
								System.out.println("Error: Can't get City title!");
								continue;
							}
						}
						
						//---Get Country Title---
						if (bCt.countryTitle == null) {
							synchronized(vk) {
								startTime = System.currentTimeMillis();
								result = vk.sendReq("database.getCountriesById", "country_ids="+bCt.countryId);
								vk.checkTime(startTime);
							}
							
							resJson = VkDataLoader.parseString(result);
							
							if (resJson != null) {
								if ( !((JSONArray)resJson.get("response")).isEmpty() )
									countryTitle = ((JSONObject)((JSONArray)resJson.get("response")).get(0)).get("title").toString();
								if (DBG) prnt.log(TAG+"countryTitle:"+countryTitle);
							}
							else {
								System.out.println("Error: Can't get Country title!");
								continue;
							}
						}
						
						//---Get City Title in English---
						synchronized(vk) {
							startTime = System.currentTimeMillis();
							result = vk.sendReq("database.getCitiesById", "city_ids="+bCt.id+"&lang=en");
							vk.checkTime(startTime);
						}
						
						resJson = VkDataLoader.parseString(result);
						
						if (resJson != null) {
							if ( !((JSONArray)resJson.get("response")).isEmpty() )
								cityTitleEn = ((JSONObject)((JSONArray)resJson.get("response")).get(0)).get("title").toString();
							if (DBG) prnt.log(TAG+"cityTitleEn:"+cityTitleEn);
						}
						else {
							System.out.println("Error: Can't get City title!");
							continue;
						}
						
						cityTitleEn = removeSpace(cityTitleEn);
						
						//---Get region---
						if (cityTitleEn != null) {
							synchronized(vk) {
								startTime = System.currentTimeMillis();
								result = vk.sendReq("database.getCities", "country_id="+bCt.countryId
										+"&q="+cityTitleEn+"&lang=ru");
								vk.checkTime(startTime);
							}
							//System.out.println(result);
							resJson = VkDataLoader.parseString(result);
							
							resArr = (JSONArray)((JSONObject)resJson.get("response")).get("items");
							
							for (int i = 0; i < resArr.size(); i++) {
								
								JSONObject tmpJs = (JSONObject)resArr.get(i);
								if ( bCt.id.equals(tmpJs.get("id").toString()) ) {
									if (tmpJs.get("region") != null) 
										regionTitle = tmpJs.get("region").toString();
									if (DBG) prnt.log(TAG+"regionTitle:"+regionTitle);
									//continue; ??
								}
							}
						}
						
						//---Fill Cities----
						JSONObject resultCity = new JSONObject();
						resultCity.put("id", bCt.id);
						resultCity.put("title", cityTitle == null ? bCt.title : cityTitle);
						resultCity.put("region", regionTitle);
						resultCity.put("country", countryTitle == null ? bCt.countryTitle : countryTitle);
						
						cities.put(bCt.id, resultCity.toString());
						ctWrt.append(resultCity.toString()+"\n");
						ctWrt.flush();
						ctWrt.close();
					}
				}
				
				//ctWrt.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			prnt.log(TAG+" cityDBThread stopped");
		}
		
	};
	
	/**
	 * 
	 * @return
	 */
	private int readBase() {
		
		String s;
		JSONObject rdJson = null;
		
		try {
			ctRd = new BufferedReader(new InputStreamReader(new FileInputStream(ctFile)));
			
			while ((s = ctRd.readLine()) != null) {
				
				try {
					rdJson = (JSONObject)(new JSONParser()).parse(s);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				String key = rdJson.get("id").toString();
				if (!cities.containsKey(key)) {
					cities.put(key, s);
				}	
			}
			
			ctRd.close();
		} catch (FileNotFoundException e) {
			System.out.println("Error: City base file not found");
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			System.out.println("Error: Can't read City base file");
			e.printStackTrace();
			System.exit(1);
		}
		
		return 0;
	}
	
	public static String removeSpace(String input) {
		
		if (input != null) {
			StringBuffer stb = new StringBuffer(input);
			for (int i = 0; i < stb.length(); i++) {
				if (stb.charAt(i) == ' '){
					stb.replace(i, i+1, "%20");
				}
			}
			return stb.toString();
		}
		else {
			return null;
		}
	}

}
