package main.java.com.vkbigdata.vkdataloader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;
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
	private String TAG = "   [VkCityDataBase]";
	private HashMap<String, String> cities;
	private LinkedList<BufCity> bufCities;
	private JSONArray jsArray;
	
	private File ctFile;
	private BufferedReader ctRd;
	private BufferedWriter ctWrt;
	private VkApi vk;
	private VkPrint prnt;
	
	public CityDBThread cityDBThread;
	
	private static class BufCity {
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
			if (cityDBThread == null || !cityDBThread.isAlive()) {
				cityDBThread = new CityDBThread();
				cityDBThread.start();
			}
			else {
				if (DBG) prnt.log(TAG+" cityDBThread already running!!");
			}
		}
	
		return 0;
	}
	
	protected class CityDBThread extends Thread{
		
		public void run() {
			
			prnt.log(TAG+" cityDBThread started");
			
			try {
				
				JSONObject resJson = new JSONObject();
				JSONArray resArr;
				String result = null;	
				long startTime;
				
				while (!bufCities.isEmpty()) {
					
					ctWrt =  new BufferedWriter(new OutputStreamWriter(
					        new FileOutputStream(ctFile), "UTF-8"));//new FileWriter(ctFile, true);
					
					String cityTitle = null;
					String countryTitle = null;
					String regionTitle = null;
					
					BufCity bCt = bufCities.remove();
					
					if (!cities.containsKey(bCt.id) && bCt.countryId != null && !bCt.countryId.equals("0") ) {
						
						try {
							Thread.sleep(300);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						
						//---Get City Title---
						if (bCt.title == null) {
							//if (DBG) prnt.log(TAG+"Get City Title");
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
								if (DBG) prnt.log(TAG+" Error: Can't get City title!");
								continue;
							}
						}
						
						try {
							Thread.sleep(300);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						//---Get Country Title---
						if (bCt.countryTitle == null) {
							//if (DBG) prnt.log(TAG+"Get Country Title");
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
								if (DBG) prnt.log(TAG+" Error: Can't get Country title!");
								continue;
							}
						}
						
						try {
							Thread.sleep(300);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						//---Get region---
						//if (DBG) prnt.log(TAG+"Get region");
						if (cityTitle != null) {
							synchronized(vk) {
								startTime = System.currentTimeMillis();
								if (bCt.title == null) {
									result = vk.sendReq("database.getCities", "country_id="+bCt.countryId
											+"&q="+URLEncoder.encode(cityTitle,"UTF-8")+"&lang=ru");
								}
								else {
									result = vk.sendReq("database.getCities", "country_id="+bCt.countryId
											+"&q="+URLEncoder.encode(bCt.title,"UTF-8")+"&lang=ru");
								}
								vk.checkTime(startTime);
							}
							
							resJson = VkDataLoader.parseString(result);
							
							resArr = (JSONArray)((JSONObject)resJson.get("response")).get("items");
							
							for (int i = 0; i < resArr.size(); i++) {
								
								JSONObject tmpJs = (JSONObject)resArr.get(i);
								if ( bCt.id.equals(tmpJs.get("id").toString()) ) {
									if (tmpJs.get("region") != null) 
										regionTitle = tmpJs.get("region").toString();
									//continue; ??
								}
							}
							if (DBG) prnt.log(TAG+"regionTitle:"+regionTitle);
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
			ctRd = new BufferedReader(new InputStreamReader(new FileInputStream(ctFile),"UTF-8"));
			
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

}
