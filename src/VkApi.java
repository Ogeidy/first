import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;


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
	
	private void setAccessToken(String accessToken){
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
		
		String data = null;
		
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
			
			BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			if ((data = br.readLine()) != null){
				String input;
				while ((input = br.readLine()) != null){
					data+=input;
				}
			}
			
			sock.close();
		}catch (MalformedURLException e) {
		     e.printStackTrace();
		} catch (IOException e) {
		     e.printStackTrace();
		}
		
		return data.substring(data.indexOf("{"));
	}
	
	/** Sending request via HTTPS using access token. */
	public String sendReqS(String meth, String param){
		
		String data = null;
		
		String reqUrl = API_REQUEST
				.replace("{METHOD_NAME}", meth)
				.replace("{PARAMETERS}", param)
				.replace("{ACCESS_TOKEN}", accessToken);
		
		URL url;
		
		try{
			url = new URL(reqUrl);
			
			HttpsURLConnection con =(HttpsURLConnection)url.openConnection();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
			if ((data = br.readLine()) != null){
				String input;
				while ((input = br.readLine()) != null){
					data+=input;
				}
			}
			
			con.disconnect();
			
		}catch (MalformedURLException e) {
		     e.printStackTrace();
		} catch (IOException e) {
		     e.printStackTrace();
		}
		
		// Warning! Here recursion is possible
		if (check(data) == 1){
			data = sendReqS(meth, param);
		}
		
		return data;
	}
	
	private int check(String data){
		
		if (data == null)
			return 1;
		
		if (data.contains("{\"error\":")){
			int code = Integer.parseInt(data.substring(data.indexOf("{\"error_code\":")+14,data.indexOf(",\"error_msg\":")));
			System.out.println("Error code: " + code);
			
			if (code == 5){
				
				System.out.println("Confirm your agreement to accessto some data, copy here access token form address line:");

				try {
					auth();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				Scanner in = new Scanner(System.in);
				String indata = in.next();
				setAccessToken(indata);
				in.close();
				
				return 1;
			}
		}
		
		return 0;
	}

}
