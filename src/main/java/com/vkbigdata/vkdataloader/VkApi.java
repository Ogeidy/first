package main.java.com.vkbigdata.vkdataloader;
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
 * Realize some methods for connection with VK API.
 * @author Yakov Mamontov
 *
 */
public class VkApi {
	
	private boolean DBG = true;
	private String TAG = "   [VkApi]";
	private String API_VERSION = "5.37";
	private VkConfig conf;
	private VkPrint prnt;
	
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
	
	/**
	 * Creates a new <code>VkApi</code> instance 
	 * @param conf
	 */
	public VkApi(VkConfig conf,  VkPrint vkPrnt){
		
		this.conf = conf;
		this.prnt = vkPrnt;
		
	}
	
	/**
	 * Authentication on VK server and getting access token
	 * @throws IOException
	 */
	public void auth() throws IOException{
		String reqUrl = AUTH_URL
                .replace("{APP_ID}", this.conf.APP_ID)
                .replace("{PERMISSIONS}", "photos,messages,audio,status,groups,friends")
                .replace("{DISPLAY}", "page");
        try {
            Desktop.getDesktop().browse(new URL(reqUrl).toURI());
        } catch (URISyntaxException ex) {
        	if (DBG) prnt.log(TAG+" Can't open browser. Exception:"+ex);
        	ex.printStackTrace();
            throw new IOException(ex);
        }
	}
	
	/**
	 * Sending request via HTTP without access token.
	 * @param method
	 * @param parameters
	 * @return
	 */
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
			
			BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream(),"utf-8"));
			if ((data = br.readLine()) != null){
				String input;
				while ((input = br.readLine()) != null){
					data+=input;
				}
			}
			
			sock.close();
		}catch (MalformedURLException e) {
			if (DBG) prnt.log(TAG+" Malformed URL. Exception:"+e);
		    e.printStackTrace();
		} catch (IOException e) {
			if (DBG) prnt.log(TAG+" Can't sand request. Exception:"+e);
		    e.printStackTrace();
		}
		
		// Warning! Here recursion is possible
		int checkVal = check(data);
		if ( checkVal == 1){
			data = sendReq(method, parameters);
		}
		else if (checkVal == -1) {
			System.exit(1);
		}
		
		return data.substring(data.indexOf("{"));
	}
	
	/**
	 * Sending request via HTTPS using access token.
	 * @param method
	 * @param parameters
	 * @return
	 */
	public String sendReqS(String method, String parameters){
		
		String data = null;
		
		String reqUrl = API_REQUEST
				.replace("{METHOD_NAME}", method)
				.replace("{PARAMETERS}", parameters)
				.replace("{ACCESS_TOKEN}", this.conf.ACCESS_TOKEN);
		
		URL url;
		
		try{
			url = new URL(reqUrl);
			
			HttpsURLConnection con =(HttpsURLConnection)url.openConnection();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(),"utf-8"));
			if ((data = br.readLine()) != null){
				String input;
				while ((input = br.readLine()) != null){
					data+=input;
				}
			}
			
			con.disconnect();
			
		}catch (MalformedURLException e) {
			if (DBG) prnt.log(TAG+" Malformed URL. Exception:"+e);
		    e.printStackTrace();
		} catch (IOException e) {
			if (DBG) prnt.log(TAG+" Can't sand request. Exception:"+e);
		    e.printStackTrace();
		}
		
		// Warning! Here recursion is possible
		int checkVal = check(data);
		if ( checkVal == 1){
			data = sendReqS(method , parameters);
		}
		else if (checkVal == -1) {
			if (DBG) prnt.log(TAG+"Program closed!");
			System.exit(1);
		}
		
		return data;
	}
	
	/**
	 * Checks response data from Vk server
	 * @param data
	 * @return 0: all ok;  1: user authorization failed; -1: other fails
	 */
	private int check(String data){
		
		if (data == null)
			return 1;
		
		if (data.contains("{\"error\":")){
			int code = Integer.parseInt(data.substring(data.indexOf("{\"error_code\":")+14,data.indexOf(",\"error_msg\":")));
			if (DBG) prnt.log(TAG+" Error code: " + code);
			
			if (code == 5){
				if (DBG) prnt.log(TAG+" User authorization failed!");
				if (DBG) prnt.log(TAG+" Confirm your agreement to accessto some data, copy here access token form address line:");

				try {
					auth();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				Scanner in = new Scanner(System.in);
				String indata = in.next();
				conf.setAccessToken(indata);
				in.close();
				
				return 1;
			} 
			else {
				System.out.println();
				return -1;
			}
		}
		
		return 0;
	}
	
	/**
	 * Check the VK timeouts
	 * @param startTime
	 */
	public void checkTime(long startTime) {
		
		long finish = System.currentTimeMillis();
		int time = (int)(finish-startTime);
		if (DBG) prnt.log("Time:"+time+"ms");
		
		if (time < 340) {
			try {
				Thread.sleep(340 - time);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}

}
