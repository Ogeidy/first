
public class VkDataLoader {
	
	public static void main(String[] args) {
		
		VkConfig conf = new VkConfig("config.txt");
		conf.readConfig();  //Reading config file
		
		VkApi vk = new VkApi(conf);
		VkPrint prnt = new VkPrint("output.txt");
		String result = null;
			
		// https request 
		
//		result = vk.sendReq("users.get","user_ids=142218715&fields=home_town");
		result = vk.sendReqS("users.search", "university="+conf.universities[0]+"&fields=education&count=100");
//		result = vk.sendReqS("users.search", "university="+conf.universities[0]+"&fields=sex,bdate,country,city,home_town&count=100");	
		
		//printing
		prnt.printResult(result);	
		
	}
}
