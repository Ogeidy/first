import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class vk_first {
	
	public static void main(String[] args) {
		
		//request strings
		//String req = "GET http://api.vk.com/method/users.get.xml?user_ids=1&fields=bdate HTTP/1.0 \n\n";
		String req = "GET http://api.vk.com/method/users.get.xml?user_ids=18124036&fields=bdate,city HTTP/1.0\r\n\r\n";
		String req2 = "GET https://api.vk.com/method/users.search.xml?q=яков ћамонтов&access_token=d4b119db0001a9a1f54837a7b8a1a2688c646030ac92cdddd6a3a05116cf9e7899d7a451f0bc967585698 HTTP/1.0\r\n\r\n";
		String req3 = "https://oauth.vk.com/authorize?client_id=5075749&display=page&redirect_uri=https://oauth.vk.com/blank.html&scope=friends&response_type=token&v=5.37";
		String https_url = "https://api.vk.com/method/users.search.xml?q=Yakov%20Mamontov&access_token=58adc3edf01eb80d8dbb86e0441cba1871c4405614192fd64fcd2364ae5cbea175da655e54ed46ebcad8b";

		VkApi vk = new VkApi();
		// http request
		vk.sendReq("users.get.xml", "user_ids=18124036&fields=bdate,city");
		
		//*** https request 
		vk.setAccessToken("58adc3edf01eb80d8dbb86e0441cba1871c4405614192fd64fcd2364ae5cbea175da655e54ed46ebcad8b");
		vk.sendReqS("users.search.xml", "q=Yakov%20Mamontov");	
		
	}
}
