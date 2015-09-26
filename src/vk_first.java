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
		
		//*** http request ***
		//Create socket, send request and receive reply
		try {
			Socket sock = new Socket("api.vk.com", 80);
			
			sock.getOutputStream().write(req.getBytes());
			
			byte buf[] = new byte[64*1024];
			int r = sock.getInputStream().read(buf);
			String data = new String(buf, 0, r);
			
			System.out.println(data);
			
			sock.close();
		}
		catch(Exception e) {
			System.out.println("Error: "+e);
		}
		
		//*** https request ***
		
		//https://oauth.vk.com/blank.html#access_token=d4b119db0001a9a1f54837a7b8a1a2688c646030ac92cdddd6a3a05116cf9e7899d7a451f0bc967585698&expires_in=86400&user_id=18124036
		String https_url = "https://api.vk.com/method/users.search.xml?q=иван иванов&access_token=4ca1d44dd783ef9e2a5938ebc018a51f83548f6c93b8a18a53c70b918aaef1b9f9d4f555baf553bacb3d1";
						//"https://vk.com";
				//"https://yandex.ru";
		URL url;
		
		try{
			url = new URL(https_url);
			
			HttpsURLConnection con =(HttpsURLConnection)url.openConnection();
			
			con.setDoOutput(true);
			con.setDoInput(true);
			
			//con.getOutputStream().write(req2.getBytes());
			
			byte buf[] = new byte[64*1024];
			int r = con.getInputStream().read(buf);
			String data = new String(buf, 0, r);
			
			System.out.println(data);
			
			con.disconnect();
			
		} catch (MalformedURLException e) {
		     e.printStackTrace();
		 } catch (IOException e) {
		     e.printStackTrace();
		 }
		
		
	}
}
