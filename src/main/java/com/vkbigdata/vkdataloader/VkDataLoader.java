package main.java.com.vkbigdata.vkdataloader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class VkDataLoader {
	
	private static String CONFIG_FILE = "config_poly_2.txt";
	
	private static String OUT_FILE_PREFIX = "output-";
	
	private static String LOG_FILE = "VkDataLoader.log";
	
	private static VkConfig conf;
	private static VkApi vk;
	private static VkPrint prnt;
	private static VkCityDataBase cts;
	private static VkNoLimitThread noLimit;
	
	
	
	public static void main(String[] args) {
		
		conf = new VkConfig(CONFIG_FILE);
		conf.readConfig();  //Reading config file
		prnt = new VkPrint(OUT_FILE_PREFIX+".txt", LOG_FILE);
		vk = new VkApi(conf, prnt);
		cts = new VkCityDataBase("cities_base.txt", vk, prnt);
		noLimit = new VkNoLimitThread(vk, prnt);
		
		JSONParser parser = new JSONParser();
		JSONObject resJson = new JSONObject();
		String result = null;	
		long startTime;
		
		// Start the parallel thread
		noLimit.start();
		
		prnt.log("#### Start the program ####");
		
		for (int i = 0; i < conf.universityNum; i++) {

			int Uni = Integer.parseInt(conf.universities[i].get("id").toString());
			int numFcts = Integer.parseInt(((JSONObject)conf.faculties[i].get("response")).get("count").toString());
			prnt = new VkPrint(OUT_FILE_PREFIX+Uni+".txt", LOG_FILE);
			
			prnt.log("**** University ID: "+Uni+" ****");
			
			for (int j = 0; j < numFcts; j++){
				
				JSONArray arrFcts = (JSONArray)((JSONObject)conf.faculties[i].get("response")).get("items");
				int idFct = Integer.parseInt(((JSONObject)arrFcts.get(j)).get("id").toString());
				
				for (int k = conf.yearFrom; k <= conf.yearTo; k++) {      
					
					try {
						Thread.sleep(20000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					synchronized(vk) {
						prnt.log("----------------------------");
						prnt.log("<Uni:"+Uni+" Fct:"+idFct+" Year:"+k+">");
						
						startTime = System.currentTimeMillis();
						result = vk.sendReqS("users.search", "university="+Uni
								+"&university_faculty="+idFct
								+"&university_year="+k
								+"&fields=sex,bdate,city,country,home_town,universities,schools&count=1000");
						vk.checkTime(startTime);
					}
					
					try {
						resJson = (JSONObject)parser.parse(result);
					} catch (ParseException e) {
						prnt.log("Error: Can't parse the response!");
						e.printStackTrace();
						System.exit(1);
					}
					int count = Integer.parseInt(((JSONObject)resJson.get("response")).get("count").toString());
					
					if (count < 1000) {
						
						prnt.print("\nUniversity: "+conf.universities[i].toString()
								+" Year: "+k
								+" Faculty:"+((JSONObject)arrFcts.get(j)).toString()+"\n");
						prnt.printResult(result);
						cts.checkCity(resJson);
					} 
					else {
						synchronized(vk) {
							startTime = System.currentTimeMillis();
							result = vk.sendReqS("database.getChairs", "faculty_id="+idFct+"&count=1000");
							prnt.log("Getted chairs");
							vk.checkTime(startTime);
						}
						
						try {
							resJson = (JSONObject)parser.parse(result);
							
						} catch (ParseException e) {
							prnt.log("Error: Can't parse the response!");
							e.printStackTrace();
							System.exit(1);
						}
						int numChrs = Integer.parseInt(((JSONObject)resJson.get("response")).get("count").toString());
						
						JSONArray arrChrs = (JSONArray)((JSONObject)resJson.get("response")).get("items");
						
						for (int z = 0; z < numChrs; z++) {
							
							int idChr = Integer.parseInt(((JSONObject)arrChrs.get(z)).get("id").toString());
							
							try {
								Thread.sleep(20000);
							} catch (InterruptedException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							synchronized(vk) {
								prnt.log("^^^^");
								prnt.log("*ID Chair:"+idChr);
								
								startTime = System.currentTimeMillis();
								result = vk.sendReqS("users.search", "university="+Uni
										+"&university_faculty="+idFct
										+"&university_year="+k
										+"&university_chair="+idChr
										+"&fields=sex,bdate,city,country,home_town,universities,schools&count=1000");
								vk.checkTime(startTime);
							}
							
							prnt.print("\nUniversity: "+conf.universities[i].toString()
									+" Year: "+k
									+" Faculty:"+((JSONObject)arrFcts.get(j)).toString()
									+" Chair:"+((JSONObject)arrChrs.get(z)).toString()+"\n");
							prnt.printResult(result);
							cts.checkCity(resJson);
						}
						
					}
				}
			}
		}
		
		try {
			cts.cityDBThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// Stop the parallel thread
		noLimit.interrupt();
		
	}
	
	/**
	 * Parse String into JSONObject
	 * @param input
	 * @return
	 */
	public static JSONObject parseString(String input) {
		
		JSONParser parser = new JSONParser();
		JSONObject resJson = new JSONObject();
		
		try {
			resJson = (JSONObject)parser.parse(input);
		} catch (ParseException e) {
			System.out.println("Error: Can't parse the response!");
			e.printStackTrace();
			System.exit(1);
		}
		
		return resJson;
	}
}
