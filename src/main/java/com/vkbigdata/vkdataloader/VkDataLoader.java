package main.java.com.vkbigdata.vkdataloader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class VkDataLoader {
	
	private static String CONFIG_FILE = "config_spbsu.txt";
	private static String OUT_FILE_PREFIX = "output-";
	private static String LOG_FILE = "VkDataLoader.log";
	
	private static VkConfig conf;
	private static VkApi vk;
	private static VkPrint prnt;
	private static VkNoLimitThread noLimit;
	
	
	
	public static void main(String[] args) {
		
		conf = new VkConfig(CONFIG_FILE);
		conf.readConfig();  //Reading config file
		vk = new VkApi(conf);
		noLimit = new VkNoLimitThread(vk, prnt);
		
		JSONParser parser = new JSONParser();
		JSONObject resJson = new JSONObject();
		String result = null;	
		long startTime;
		
		new Thread(noLimit).start();
		
		for (int i = 0; i < conf.universityNum; i++) {

			int Uni = Integer.parseInt(conf.universities[i].get("id").toString());
			int numFcts = Integer.parseInt(((JSONObject)conf.faculties[i].get("response")).get("count").toString());
			prnt = new VkPrint(OUT_FILE_PREFIX+Uni+".txt", LOG_FILE);
			
			for (int j = 0; j < numFcts; j++){
				
				JSONArray arrFcts = (JSONArray)((JSONObject)conf.faculties[i].get("response")).get("items");
				int idFct = Integer.parseInt(((JSONObject)arrFcts.get(j)).get("id").toString());
				
				for (int k = conf.yearFrom; k <= conf.yearTo; k++) {      
					
					synchronized(noLimit) {
						prnt.log("----------------------------");
						prnt.log("**i:"+i+" j:"+j+" k:"+k+" ID Fct:"+idFct);
						
						startTime = System.currentTimeMillis();
						result = vk.sendReqS("users.search", "university="+Uni
								+"&university_faculty="+idFct
								+"&university_year="+k
								+"&fields=home_town,universities,schools,sex&count=1000");
						checkTime(startTime);
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
					} 
					else {
						synchronized(noLimit) {
							startTime = System.currentTimeMillis();
							result = vk.sendReqS("database.getChairs", "faculty_id="+idFct+"&count=1000");
							checkTime(startTime);
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
							
							synchronized(noLimit) {
								prnt.log("^^^^");
								prnt.log("*ID Chair:"+idChr);
								
								startTime = System.currentTimeMillis();
								result = vk.sendReqS("users.search", "university="+Uni
										+"&university_faculty="+idFct
										+"&university_year="+k
										+"&university_chair="+idChr
										+"&fields=home_town,universities,schools,sex&count=1000");
								checkTime(startTime);
							}
							
							prnt.print("\nUniversity: "+conf.universities[i].toString()
									+" Year: "+k
									+" Faculty:"+((JSONObject)arrFcts.get(j)).toString()
									+" Chair:"+((JSONObject)arrChrs.get(z)).toString()+"\n");
							prnt.printResult(result);
						}
						
					}
				}
			}
		}
		
	}
	
	/**
	 * Check the VK timeouts
	 * @param startTime
	 */
	private static void checkTime(long startTime) {
		
		long finish = System.currentTimeMillis();
		int time = (int)(finish-startTime);
		prnt.log("Time:"+time+"ms");
		
		if (time < 340) {
			try {
				Thread.sleep(340 - time);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
}
