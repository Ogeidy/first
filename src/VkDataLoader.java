import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class VkDataLoader {
	
	private static String APP_ID = "5075749";
	
	private static String ACCESS_TOKEN = "a08474e90bf43ac5b5a9b1687acbd7d79fad5ca00c82bae3f53889265d98541478cf8a59651fdc610989e";
			//"a08474e90bf43ac5b5a9b1687acbd7d79fad5ca00c82bae3f53889265d98541478cf8a59651fdc610989e";
			//"58adc3edf01eb80d8dbb86e0441cba1871c4405614192fd64fcd2364ae5cbea175da655e54ed46ebcad8b";
	
	public static void main(String[] args) throws IOException {
		
		File file = new File("output.txt");
		FileWriter wrt = new FileWriter(file);
		VkApi vk = new VkApi(APP_ID, ACCESS_TOKEN);
		String result = null;
		
		// http request
		result = vk.sendReq("users.get", "user_ids=18124036&fields=bdate,city");
		System.out.println(result);
		
		wrt.append(result+"\n");
		wrt.flush();
		
		//*** https request 
		result = vk.sendReqS("users.search", "q=Yakov%20Mamontov");	
		System.out.println(result);
		
		wrt.append(result);
		wrt.flush();
		
		wrt.close();
		
	}
}
