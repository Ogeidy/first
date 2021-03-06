package main.java.com.vkbigdata.vkdataloader;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Realize some methods for connection with VK API.
 * @author Yakov Mamontov
 *
 */
public class VkApi {
	
	private boolean DBG = true;
	private String TAG = "   [VkApi]";
	private String API_VERSION = "5.40";
	private boolean isCapcha = false; 
	private String capchaId;
	private String capchaCode;
	
	private VkConfig conf;
	private VkPrint prnt;
	
	private static final Logger log = LogManager.getLogger(VkApi.class.getSimpleName());
	
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
		
		if (conf == null || vkPrnt == null) {
			throw new IllegalArgumentException();
		}
		
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
        	/* Running the default browser with 'reqUrl' for getting access token */
            Desktop.getDesktop().browse(new URL(reqUrl).toURI());
        } catch (URISyntaxException ex) {
        	//if (DBG) prnt.log(TAG+" Can't open browser. Exception:"+ex);
        	//ex.printStackTrace();
        	log.warn("Can't open browser. Exception:"+ex);
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
					.replace("&access_token={ACCESS_TOKEN}", "");
			if (isCapcha) {
				reqUrl = reqUrl.concat("&captcha_sid="+capchaId+"&captcha_key="+capchaCode);
				isCapcha = false;
			}
			reqUrl = reqUrl.concat(" HTTP/1.0\r\n\r\n");
			
			sock.getOutputStream().write(reqUrl.getBytes("UTF-8"));
			
			BufferedReader br = new BufferedReader(
					new InputStreamReader(sock.getInputStream(),"UTF-8"));
			if ((data = br.readLine()) != null){
				String input;
				while ((input = br.readLine()) != null){
					data+=input;
				}
			}
			br.close();
			sock.close();
		}catch (MalformedURLException e) {
			//if (DBG) prnt.log(TAG+" Malformed URL. Exception:"+e);
			log.warn("Malformed URL. Exception:"+e);
		    //e.printStackTrace();
		} catch (IOException e) {
			//if (DBG) prnt.log(TAG+" Can't sand request. Exception:"+e);
			log.warn("Can't sand request. Exception:"+e);
		    //e.printStackTrace();
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
				.replace("{ACCESS_TOKEN}", this.conf.ACCESS_TOKEN);
		
		if (isCapcha) {
			reqUrl = reqUrl.replace("{PARAMETERS}", 
					parameters+"&captcha_sid="+capchaId+"&captcha_key="+capchaCode);
			isCapcha = false;
		}
		else {
			reqUrl = reqUrl.replace("{PARAMETERS}", parameters);
		}
		
		URL url;
		
		try{
			url = new URL(reqUrl);
			
			HttpsURLConnection con =(HttpsURLConnection)url.openConnection();
			
			BufferedReader br = new BufferedReader(
					new InputStreamReader(con.getInputStream(),"utf-8"));
			if ((data = br.readLine()) != null){
				String input;
				while ((input = br.readLine()) != null){
					data+=input;
				}
			}
			br.close();
			con.disconnect();
			
		}catch (MalformedURLException e) {
//			if (DBG) prnt.log(TAG+" Malformed URL. Exception:"+e);
//		    e.printStackTrace();
		    log.warn("Malformed URL. Exception:"+e);
		} catch (IOException e) {
//			if (DBG) prnt.log(TAG+" Can't sand request. Exception:"+e);
//		    e.printStackTrace();
		    log.warn("Can't sand request. Exception:"+e);
		}
		
		// Warning! Here recursion is possible
		int checkVal = check(data);
		if ( checkVal == 1){
			data = sendReqS(method , parameters);
		}
		else if (checkVal == -1) {
			//if (DBG) prnt.log(TAG+"Program closed!");
			log.fatal("Program closed!");
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
			int code = Integer.parseInt(data.substring(data.indexOf("{\"error_code\":")
					+ 14,data.indexOf(",\"error_msg\":")));
			//if (DBG) prnt.log(TAG+" Error code: " + code);
			log.warn(" Error code: " + code);
			
			if (code == 5){
//				prnt.log(TAG+" User authorization failed!");
//				prnt.log(TAG+" Confirm your agreement to accessto some data, "
//						+ "copy here access token form address line:");
				log.warn("User authorization failed!");
				log.warn("Confirm your agreement to accessto some data, "
						+ "copy here access token form address line:");

				try {
					auth();
				} catch (IOException e) {
//					if (DBG) prnt.log(TAG+" Can't authenticate. Exception:"+e);
					log.warn("Can't authenticate. Exception:"+e);
					e.printStackTrace();
				}
				
				Scanner in = new Scanner(System.in);
				String indata = in.next();
				conf.setAccessToken(indata);
				//in.close();
				
				return 1;
			} 
			else if (code == 14) {
//				prnt.log(TAG+" Capcha needed!!");
//				prnt.log(TAG+data);
//				prnt.log(TAG+" Write below CapchaId and code from picture");
				log.warn("Capcha needed!!");
				log.warn(data);
				log.warn("Write below CapchaId and code from picture");
				
				Scanner inp = new Scanner(System.in);
				if (inp.hasNext()) {
					capchaId = inp.next();
					try {
						capchaCode = URLEncoder.encode(inp.next(),"UTF-8");
					} catch (UnsupportedEncodingException e) {
						//if (DBG) prnt.log(TAG+" Can't read capcha. Exception:"+e);
						log.warn("Can't read capcha. Exception:" + e);
						//e.printStackTrace();
					}
				}
				//in.close();
				isCapcha = true;
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
		//if (DBG) prnt.log("Time:"+time+"ms");
		log.info("Time:"+time+"ms");
		
		if (time < 340) {
			try {
				Thread.sleep(340 - time);
			} catch (InterruptedException e) {
//				if (DBG) prnt.log(TAG+" Can't check time. Exception:"+e);
//				e.printStackTrace();
				log.warn("Can't check time. Exception:" + e);
			}
		}
		
	}

}
