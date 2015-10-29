package main.java.com.vkbigdata.vkdataloader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Implement read, write and store configuration information.
 * @author Yakov Mamontov
 *
 */
public class VkConfig {

	public String APP_ID;
	public String ACCESS_TOKEN;
	public int universityNum;
	public JSONObject[] universities;
	public int yearFrom;
	public int yearTo;
	public JSONObject[] faculties;
	private File configFile;
	private BufferedReader cfgRd;
	private FileWriter cfgWrt;
	
	/**
	 * Creates a new <code>VkConfig</code> instance 
	 * @param fileName
	 */
	public VkConfig(String fileName) {
		
		configFile = new File(fileName);
		
	}
	
	/**
	 *  Reads the configure file and fills all necessary fields.
	 * @return
	 */
	public int readConfig() {

		String s;
		int it = 0;
		
		try {
			cfgRd = new BufferedReader(new InputStreamReader(new FileInputStream(configFile)));
			/*
			 * If happened troubles with encoding
			 * new InputStreamReader(new FileInputStream(configFile),"utf-8")
			 */
			while ((s = cfgRd.readLine()) != null) {
//				System.out.println(s);
				if (it == 0) {
					APP_ID = s.substring(s.indexOf("=")+2);
					it++;
				} else if(it == 1) {
					ACCESS_TOKEN = s.substring(s.indexOf("=")+2);
					it++;
				}else if ( it == 2) {
					yearFrom = Integer.parseInt(s.substring(s.indexOf("=")+2));
					it++;
				} else if ( it == 3) {
					yearTo = Integer.parseInt(s.substring(s.indexOf("=")+2));
					it++;
				} else if ( it == 4) {
					universityNum = Integer.parseInt(s.substring(s.indexOf("=")+2));
					universities = new JSONObject[universityNum];
					try {
						for (int i = 0; i < universityNum; i++) {
							universities[i] = (JSONObject)(new JSONParser()).parse(cfgRd.readLine());
	//						System.out.println(universities[i]);
						}
					} catch (ParseException e) {
						System.out.println("Error: Can't parse universities Json");
						e.printStackTrace();
						System.exit(1);
					}
					it++;
				}  else if ( it == 5) {
					faculties = new JSONObject[universityNum];
					try {
						for (int i = 0; i < universityNum; i++) {
							faculties[i] = (JSONObject)(new JSONParser()).parse(cfgRd.readLine());
//							System.out.println(faculties[i].toString());
						}
					} catch (ParseException e) {
						System.out.println("Error: Can't parse faculties Json");
						e.printStackTrace();
						System.exit(1);
					}
					it++;
				}
			}
			cfgRd.close();
		} catch (FileNotFoundException e) {
			System.out.println("Error: Configure file not found");
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			System.out.println("Error: Can't read to config file");
			e.printStackTrace();
			System.exit(1);
		}
		
		return 0;
	}
	
	/**
	 * Sets <code>ACCESS_TOKEN</code> and writes it to configure file.
	 * @param accessToken
	 * @return
	 */
	public int setAccessToken(String accessToken) {
		
		this.ACCESS_TOKEN = accessToken;
		
		try {
			cfgWrt = new FileWriter(configFile);
			
			cfgWrt.append("APP_ID = "+APP_ID+"\n");
			cfgWrt.append("ACCESS_TOKEN = "+ACCESS_TOKEN+"\n");
			cfgWrt.append("YearFrom = "+yearFrom+"\n");
			cfgWrt.append("YearTo = "+yearTo+"\n");
			cfgWrt.append("UniversityNum = "+universityNum+"\n");
			
			for (int i = 0; i < universityNum; i++) {
				cfgWrt.append(universities[i]+"\n");
			}
			
			cfgWrt.append("Faculties:\n");
			for (int i = 0; i < universityNum; i++) {
				cfgWrt.append(faculties[i].toString()+"\n");
			}
			
			
			cfgWrt.flush();
			cfgWrt.close();
		} catch (IOException e) {
			System.out.println("Error: Can't write to config file.\n Confilure file was not changed.");
		}
	
		return 0;
	}
	
}
