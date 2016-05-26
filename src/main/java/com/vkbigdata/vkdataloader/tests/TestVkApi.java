package main.java.com.vkbigdata.vkdataloader.tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URLEncoder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Ignore;
import org.junit.Test;
import main.java.com.vkbigdata.vkdataloader.VkApi;
import main.java.com.vkbigdata.vkdataloader.VkConfig;
import main.java.com.vkbigdata.vkdataloader.VkPrint;

public class TestVkApi {
	
	private static String CONFIG_FILE = "TestConfig.txt";
	private static String OUT_FILE = "TestOutFile.txt";
	private static String LOG_FILE = "TestLogFile.log";
	private VkApi vk;
	private VkConfig conf;
	private VkPrint prnt;
	
	public TestVkApi() {
		
		conf = new VkConfig(CONFIG_FILE);
		conf.readConfig();  //Reading config file
		prnt = new VkPrint(OUT_FILE, LOG_FILE);
		
		vk = new VkApi(conf, prnt);
	}

	@Ignore
	@Test
	public void testAuth() {
		
		try {
			vk.auth();
		} catch (IOException e) {
			fail("testAuth fail");
			e.printStackTrace();
		}
	}
	
	@Test
	public void testCheckTimeOk() {
		
		long startTime = System.currentTimeMillis();

		vk.checkTime(startTime);
		
		long finish = System.currentTimeMillis();
		int time = (int)(finish-startTime);
		//System.out.println("OkTime: " + time);
		assertTrue(time >= 340);
	}
	
	@Test
	public void testCheckTimeFail() {

		long startTime = System.currentTimeMillis();
		try {
			Thread.sleep(340);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		vk.checkTime(startTime);
		int time = (int)(System.currentTimeMillis()-startTime);
		//System.out.println("FailTime: " + time);
		assertTrue(time >= 340);
	}

	@Test
	public void testSendReq() {
		
		String result = vk.sendReq("users.get", "user_ids=1");
		//System.out.println("result: " + result);
		assertEquals("{\"response\":[{\"id\":1,\"first_name\":\"Павел\",\"last_name\":\"Дуров\"}]}", result);
	}
	
	@Ignore
	@Test
	public void testSendReqS() {
		
		int Uni = Integer.parseInt(conf.universities[conf.universityNum - 1].get("id").toString());
		JSONArray arrFcts = (JSONArray)((JSONObject)conf.faculties[0].get("response")).get("items");
		int idFct = Integer.parseInt(((JSONObject)arrFcts.get(0)).get("id").toString());
		
		String result = vk.sendReqS("users.search", "university="+Uni
				+"&university_faculty="+idFct
				+"&university_year="+conf.yearFrom
				+"&fields=sex,bdate&count=1");
		//System.out.println("result: " + result);
		assertEquals("{\"response\":{\"count\":514,\"items\":[{\"id\":6492,\"first_name\":\"Андрей\","
				+ "\"last_name\":\"Рогозов\",\"sex\":2,\"bdate\":\"11.11\"}]}}"
				, result);
	}
}
