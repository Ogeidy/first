
public class vk_first {
	
	private static String APP_ID = "5075749";
	
	private static String ACCESS_TOKEN = "58adc3edf01eb80d8dbb86e0441cba1871c4405614192fd64fcd2364ae5cbea175da655e54ed46ebcad8b";
	
	public static void main(String[] args) {

		VkApi vk = new VkApi(APP_ID);
		// http request
		vk.sendReq("users.get.xml", "user_ids=18124036&fields=bdate,city");
		
		//*** https request 
		vk.setAccessToken(ACCESS_TOKEN);
		vk.sendReqS("users.search.xml", "q=Yakov%20Mamontov");	
		
	}
}
