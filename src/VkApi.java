import java.awt.Desktop;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * This class realize some methods for connection with VK API.
 * @author Yakov Mamontov
 *
 */
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
	private String appId;
	
	public VkApi(String appId){
		this.appId = appId;
	}
	
	public VkApi(String appId, String accessToken){
		this.appId = appId;
		this.accessToken = accessToken;
	}
	
	public void setAccessToken(String accessToken){
		this.accessToken = accessToken;
	}
	
	/** Authentication on VK server and getting access token. */
	public void auth() throws IOException{
		String reqUrl = AUTH_URL
                .replace("{APP_ID}", this.appId)
                .replace("{PERMISSIONS}", "photos,messages")
                .replace("{DISPLAY}", "page");
        try {
            Desktop.getDesktop().browse(new URL(reqUrl).toURI());
        } catch (URISyntaxException ex) {
            throw new IOException(ex);
        }
	}
	
	/** Sending request via HTTP without access token. */
	public String sendReq(String method, String parameters){
		//*** http request ***
		
		String data = null;
		//Create socket, send request and receive reply
//		JSONParser parser = new JSONParser();
//		JSONObject resJson = new JSONObject();
		
		try {
			Socket sock = new Socket("api.vk.com", 80);
			
			String reqUrl = "GET "
					+ API_REQUEST
					.replace("https", "http")
					.replace("{METHOD_NAME}", method)
					.replace("{PARAMETERS}", parameters)
					.replace("&access_token={ACCESS_TOKEN}", "")
	                + " HTTP/1.0\r\n\r\n";
			
			sock.getOutputStream().write(reqUrl.getBytes());
			
			byte buf[] = new byte[64*1024];
			int r = sock.getInputStream().read(buf);
			data = new String(buf, 0, r);
			
			//System.out.println(data);
			
//			try {
//				resJson = (JSONObject)parser.parse(data.substring(data.indexOf("{")));
//			} catch (ParseException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
//			System.out.println(resJson.toString().replace(",", ",\n").replace("{", "{\n").replace("}", "\n}"));
			
			sock.close();
		}catch (MalformedURLException e) {
		     e.printStackTrace();
		} catch (IOException e) {
		     e.printStackTrace();
		}
		
		return data.substring(data.indexOf("{"));
	}
	
	/** Sending request via HTTPS using access token. */
	public void sendReqS(String meth, String param){
		//*** https request ***
		JSONParser parser = new JSONParser();
		JSONObject resJson = new JSONObject();
		
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
			
			//System.out.println(data);
			
			try {
				resJson = (JSONObject)parser.parse(data.substring(data.indexOf("{")));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println(resJson.toString().replace(",", ",\n").replace("{", "{\n").replace("}", "\n}"));
			
			con.disconnect();
			
		}catch (MalformedURLException e) {
		     e.printStackTrace();
		} catch (IOException e) {
		     e.printStackTrace();
		}
	}

}
