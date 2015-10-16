import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class VkDataLoader {
	
	public static void main(String[] args) {
		
		VkConfig conf = new VkConfig("config_spbsu.txt");
		conf.readConfig();  //Reading config file
		
		VkApi vk = new VkApi(conf);
		VkPrint prnt = new VkPrint("output.txt");
		String result = null;
			
		// https request 
		
//		result = vk.sendReq("users.get","user_ids=142218715&fields=home_town");
//		result = vk.sendReqS("users.search", "university="+conf.universities[0]+"&fields=education&count=100");
//		result = vk.sendReqS("users.search", "university="+conf.universities[0]+"&fields=sex,bdate,country,city,home_town&count=100");	
		
		//printing
//		prnt.printResult(result);	
		
		for (int i = 0; i < conf.universityNum; i++) {
			
			int Uni = Integer.parseInt(conf.universities[i].get("id").toString());
			int numFcts = Integer.parseInt(((JSONObject)conf.faculties[i].get("response")).get("count").toString());
			
			for (int j = 0; j < numFcts; j++){
				
				JSONArray arrFcts = (JSONArray)((JSONObject)conf.faculties[i].get("response")).get("items");
				int idFct = Integer.parseInt(((JSONObject)arrFcts.get(j)).get("id").toString());
				
				for (int k = conf.yearFrom; k <= conf.yearTo; k++) {      
					
					System.out.println("i:"+i+" j:"+j+" k:"+k+" ID Fct:"+idFct);
					
					long start = System.currentTimeMillis();
					
					result = vk.sendReqS("users.search", "university="+Uni
							+"&university_faculty="+idFct
							+"&university_year="+k
							+"&fields=home_town,universities,schools&count=1000");
					
					long finish = System.currentTimeMillis();
					int tm = (int)(finish-start);
					System.out.println("Time:"+tm+"ms");
					
					// Check the VK timeouts
					if (tm < 340) {
						try {
							Thread.sleep(340 - tm);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
					JSONParser parser = new JSONParser();
					JSONObject resJson = new JSONObject();
					
					try {
						resJson = (JSONObject)parser.parse(result);
					} catch (ParseException e) {
						System.out.println("Error: Can't parse the response!");
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
						
						start = System.currentTimeMillis();
						
						result = vk.sendReqS("database.getChairs", "faculty_id="+idFct+"&count=1000");
						//prnt.printResult(result);
						
						finish = System.currentTimeMillis();
						tm = (int)(finish-start);
						System.out.println("Time:"+tm+"ms");
						
						// Check the VK timeouts
						if (tm < 340) {
							try {
								Thread.sleep(340 - tm);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						
						try {
							resJson = (JSONObject)parser.parse(result);
							
						} catch (ParseException e) {
							System.out.println("Error: Can't parse the response!");
							e.printStackTrace();
							System.exit(1);
						}
						int numChrs = Integer.parseInt(((JSONObject)resJson.get("response")).get("count").toString());
						
						JSONArray arrChrs = (JSONArray)((JSONObject)resJson.get("response")).get("items");
						
						for (int z = 0; z < numChrs; z++) {
							
							int idChr = Integer.parseInt(((JSONObject)arrChrs.get(z)).get("id").toString());
							
							System.out.println("ID Chair:"+idChr);
							
							start = System.currentTimeMillis();
							
							result = vk.sendReqS("users.search", "university="+Uni
									+"&university_faculty="+idFct
									+"&university_year="+k
//									+"&sex="+2
									+"&university_chair="+idChr
									+"&fields=home_town,universities,schools&count=1000");
							
							finish = System.currentTimeMillis();
							tm = (int)(finish-start);
							System.out.println("Time:"+tm+"ms");
							
							// Check the VK timeouts
							if (tm < 340) {
								try {
									Thread.sleep(340 - tm);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
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
}
