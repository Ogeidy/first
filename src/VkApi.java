import java.awt.Desktop;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


public class VkApi {
	
	private String API_VERSION = "5.37";
	
	private String API_REQUEST = "https://api.vk.com/method/{METHOD_NAME}"
            + "?{PARAMETERS}"
            + "&access_token={ACCESS_TOKEN}"
            + "&v=" + API_VERSION;
	
	private String AUTH_URL = "https://oauth.vk.com/authorize"
            + "?client_id={APP_ID}"
            + "&redirect_uri=https://oauth.vk.com/blank.html"
            + "&display={DISPLAY}"
            + "&scope={PERMISSIONS}"
            + "&response_type=token"
            + "&v=" + API_VERSION;
            
	private String accessToken;
	
	public VkApi(){
	}
	
	public VkApi(String accessToken){
		this.accessToken = accessToken;
	}
	
	public void setAccessToken(String accessToken){
		this.accessToken = accessToken;
	}
	
	public void auth(String appId) throws IOException{
		String reqUrl = AUTH_URL
                .replace("{APP_ID}", appId)
                .replace("{PERMISSIONS}", "photos,messages")
                .replace("{DISPLAY}", "page");
        try {
            Desktop.getDesktop().browse(new URL(reqUrl).toURI());
        } catch (URISyntaxException ex) {
            throw new IOException(ex);
        }
	}
	
	public void sendReq(String meth, String param){
		//*** http request ***
		//Create socket, send request and receive reply
		try {
			Socket sock = new Socket("api.vk.com", 80);
			
			String reqUrl = "GET "
					+ API_REQUEST
					.replace("https", "http")
					.replace("{METHOD_NAME}", meth)
					.replace("{PARAMETERS}", param)
					.replace("&access_token={ACCESS_TOKEN}", "")
	                + " HTTP/1.0\r\n\r\n";
			
			sock.getOutputStream().write(reqUrl.getBytes());
			
			byte buf[] = new byte[64*1024];
			int r = sock.getInputStream().read(buf);
			String data = new String(buf, 0, r);
			
			System.out.println(data);
			
			sock.close();
		}
		catch(Exception e) {
			System.out.println("Error: "+e);
		}
	}
	
	public void sendReqS(String meth, String param){
		//*** https request ***
		String reqUrl = API_REQUEST
				.replace("{METHOD_NAME}", meth)
				.replace("{PARAMETERS}", param)
				.replace("{ACCESS_TOKEN}", accessToken);
		
		URL url;
		
		try{
			url = new URL(reqUrl);
			
			HttpsURLConnection con =(HttpsURLConnection)url.openConnection();
			
			byte buf[] = new byte[64*1024];
			int r = con.getInputStream().read(buf);
			String data = new String(buf, 0, r);
			
			System.out.println(data);
			
			con.disconnect();
			
		}catch (MalformedURLException e) {
		     e.printStackTrace();
		} catch (IOException e) {
		     e.printStackTrace();
		}
	}

}
