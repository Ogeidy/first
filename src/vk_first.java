import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class vk_first {
	
	private static String APP_ID = "5075749";
	
	private static String ACCESS_TOKEN = "58adc3edf01eb80d8dbb86e0441cba1871c4405614192fd64fcd2364ae5cbea175da655e54ed46ebcad8b";
	
	public static void main(String[] args) throws IOException {
		
		File file = new File("output.txt");
		FileWriter wrt = new FileWriter(file);
		VkApi vk = new VkApi(APP_ID);
		
		// http request
		String result = vk.sendReq("users.get", "user_ids=18124036&fields=bdate,city");
		System.out.println(result);
		
		wrt.append(result);
		wrt.flush();
		
		//*** https request 
//		vk.setAccessToken(ACCESS_TOKEN);
//		vk.sendReqS("users.search", "q=Yakov%20Mamontov");	
		
	}
}
